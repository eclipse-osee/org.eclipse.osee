/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.server.internal;

import java.io.ByteArrayInputStream;
import java.lang.Thread.State;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.ChecksumUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationServerManager implements IApplicationServerManager {
   private final Map<String, OseeServerThreadFactory> threadFactories;
   private final Map<String, InternalOseeHttpServlet> oseeHttpServlets;

   private Log logger;
   private JdbcService jdbcService;

   private ApplicationServerDataStore dataStore;
   private OseeServerInfo serverInfo;
   private Timer timer;

   public ApplicationServerManager() {
      this.oseeHttpServlets = new ConcurrentHashMap<>();
      this.threadFactories = new ConcurrentHashMap<>();
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public Log getLogger() {
      return logger;
   }

   public void setJdbcService(JdbcService dbService) {
      this.jdbcService = dbService;
   }

   public void start() throws Exception {
      dataStore = new ApplicationServerDataStore(jdbcService.getClient());
      serverInfo = createOseeServerInfo(getLogger(), dataStore, OseeCodeVersion.getVersion());
      System.setProperty("OseeApplicationServer", serverInfo.getUri().toString());

      timer = new Timer();
      timer.schedule(new TimerTask() {
         @Override
         public void run() {
            if (isDbInitialized()) {
               try {
                  executeLookupRegistration();
               } catch (Exception ex) {
                  getLogger().error(ex, "Error during lookup registration");
               } finally {
                  timer.cancel();
               }
            }
         }

         private boolean isDbInitialized() {
            boolean result = false;
            try {
               String id = dataStore.getDatabaseGuid();
               if (Strings.isValid(id)) {
                  result = true;
               }
            } catch (Exception ex) {
               // Do nothing - no need to log exception
            }
            return result;
         }
      }, 5 * 1000);
   }

   public void stop() {
      shutdown();
   }

   private static OseeServerInfo createOseeServerInfo(Log logger, ApplicationServerDataStore dataStore, String... defaultVersions) {
      String serverAddress = "127.0.0.1";
      try {
         serverAddress = InetAddress.getLocalHost().getCanonicalHostName();
      } catch (UnknownHostException ex) {
         //
      }
      int port = OseeServerProperties.getOseeApplicationServerPort();
      String scheme = OseeServerProperties.getOseeApplicationServerScheme();
      URI uri = null;
      try {
         uri = new URI(scheme, null, serverAddress, port, null, null, null);
      } catch (URISyntaxException ex) {
         logger.error(ex, "Error generating application server uri");
      }

      String checkSum = "-1";
      try {
         String address = String.format("%s:%s", serverAddress, port);
         ByteArrayInputStream inputStream = new ByteArrayInputStream(address.getBytes("UTF-8"));
         checkSum = ChecksumUtil.createChecksumAsString(inputStream, ChecksumUtil.MD5);
      } catch (Exception ex) {
         logger.error(ex, "Error generating application server id");
      }
      return new OseeServerInfo(checkSum, uri == null ? null : uri.toString(), port, defaultVersions,
         GlobalTime.GreenwichMeanTimestamp(), false);
   }

   @Override
   public boolean executeLookupRegistration() {
      OseeServerInfo info = getApplicationServerInfo();
      deregisterWithDb(info);
      boolean isRegistered = registerWithDb(info);
      if (isRegistered) {
         getLogger().info("Application Server: [%s] registered.", info.getServerId());
      }
      return isRegistered;
   }

   @Override
   public void register(String context, OseeHttpServlet servlet) {
      InternalOseeHttpServlet internalServlet = servlet;
      this.oseeHttpServlets.put(context, internalServlet);
   }

   @Override
   public void unregister(String key) {
      this.oseeHttpServlets.remove(key);
      this.threadFactories.remove(key);
   }

   @Override
   public Collection<String> getRegisteredServlets() {
      return oseeHttpServlets.keySet();
   }

   private OseeServerInfo getApplicationServerInfo() {
      return serverInfo;
   }

   @Override
   public ThreadFactory createNewThreadFactory(String name, int priority) {
      OseeServerThreadFactory factory = new OseeServerThreadFactory(name, priority);
      this.threadFactories.put(name, factory);
      return factory;
   }

   private List<OseeServerThread> getThreadsFromFactory(String key) {
      OseeServerThreadFactory factory = threadFactories.get(key);
      return factory.getThreads();
   }

   /**
    * This method expects that one OSEE server job is running, namely the job calling this method, so it will return
    * true if 1 or less jobs are running.
    */
   @Override
   public boolean isSystemIdle() {
      return !Operations.areOperationsScheduled();
   }

   @Override
   public void shutdown() {
      if (timer != null) {
         timer.cancel();
      }
      OseeServerInfo info = getApplicationServerInfo();
      deregisterWithDb(info);
   }

   @Override
   public List<String> getCurrentProcesses() {
      List<String> processList = new ArrayList<>();
      for (String key : threadFactories.keySet()) {
         for (OseeServerThread thread : getThreadsFromFactory(key)) {
            State state = thread.getState();
            if (!state.equals(State.TERMINATED)) {
               processList.add(thread.getName());
            }
         }
      }
      for (String contexts : oseeHttpServlets.keySet()) {
         InternalOseeHttpServlet servlets = oseeHttpServlets.get(contexts);
         if (servlets.getState().equals(ProcessingStateEnum.BUSY)) {
            processList.add(servlets.getCurrentRequest());
         }
      }
      return processList;
   }

   @Override
   public int getNumberOfActiveThreads() {
      int totalProcesses = 0;
      for (String contexts : oseeHttpServlets.keySet()) {
         InternalOseeHttpServlet servlet = oseeHttpServlets.get(contexts);
         if (servlet.getState().isBusy()) {
            totalProcesses++;
         }
      }

      for (String key : threadFactories.keySet()) {
         for (OseeServerThread thread : getThreadsFromFactory(key)) {
            State state = thread.getState();
            if (State.TERMINATED != state) {
               totalProcesses++;
            }
         }
      }
      return totalProcesses;
   }

   @Override
   public String getId() {
      return getApplicationServerInfo().getServerId();
   }

   @Override
   public String getServerUri() {
      return getApplicationServerInfo().getUri();
   }

   @Override
   public int getPort() {
      return getApplicationServerInfo().getPort();
   }

   @Override
   public Date getDateStarted() {
      return getApplicationServerInfo().getDateStarted();
   }

   @Override
   public String[] getVersions() {
      return getApplicationServerInfo().getVersion();
   }

   private boolean deregisterWithDb(OseeServerInfo info) {
      boolean status = false;
      try {
         dataStore.delete(info);
         status = true;
      } catch (OseeCoreException ex) {
         getLogger().info("Server lookup table is not initialized");
      }
      return status;
   }

   private boolean registerWithDb(OseeServerInfo info) {
      boolean status = false;
      try {
         dataStore.create(info);
         status = true;
      } catch (OseeCoreException ex) {
         getLogger().info("Server lookup table is not initialized");
      }
      return status;
   }

}
