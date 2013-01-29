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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.util.ChecksumUtil;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationServerManager implements IApplicationServerManager {
   private final Map<String, OseeServerThreadFactory> threadFactories;
   private final Map<String, InternalOseeHttpServlet> oseeHttpServlets;

   private Log logger;
   private IOseeDatabaseService dbService;

   private ApplicationServerDataStore serverDataStore;
   private InternalOseeServerInfo applicationServerInfo;
   private Timer timer;

   public ApplicationServerManager() {
      this.oseeHttpServlets = new ConcurrentHashMap<String, InternalOseeHttpServlet>();
      this.threadFactories = new ConcurrentHashMap<String, OseeServerThreadFactory>();
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public Log getLogger() {
      return logger;
   }

   public void setDatabaseService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   private IOseeDatabaseService getDatabaseService() {
      return dbService;
   }

   public void start() throws Exception {
      serverDataStore = new ApplicationServerDataStore(getLogger(), getDatabaseService());
      applicationServerInfo = createOseeServerInfo(getLogger(), serverDataStore);

      timer = new Timer();
      timer.schedule(new TimerTask() {
         @Override
         public void run() {
            try {
               executeLookupRegistration();
            } catch (Exception ex) {
               getLogger().error(ex, "Error during lookup registration");
            } finally {
               timer.cancel();
            }
         }
      }, 5 * 1000);

      applicationServerInfo.setAcceptingRequests(true);
   }

   public void stop() throws OseeCoreException {
      shutdown();
   }

   private static InternalOseeServerInfo createOseeServerInfo(Log logger, ApplicationServerDataStore dataStore) {
      String serverAddress = "127.0.0.1";
      try {
         serverAddress = InetAddress.getLocalHost().getCanonicalHostName();
      } catch (UnknownHostException ex) {
         //
      }
      int port = OseeServerProperties.getOseeApplicationServerPort();

      String checkSum = "-1";
      try {
         String address = String.format("%s:%s", serverAddress, port);
         ByteArrayInputStream inputStream = new ByteArrayInputStream(address.getBytes("UTF-8"));
         checkSum = ChecksumUtil.createChecksumAsString(inputStream, ChecksumUtil.MD5);
      } catch (Exception ex) {
         logger.error(ex, "Error generating application server id");
      }

      return new InternalOseeServerInfo(logger, dataStore, checkSum, serverAddress, port,
         GlobalTime.GreenwichMeanTimestamp(), false);
   }

   @Override
   public boolean executeLookupRegistration() {
      boolean isRegistered = getApplicationServerInfo().updateRegistration();
      if (isRegistered) {
         getLogger().info("Application Server: [%s] registered.", getApplicationServerInfo().getServerId());
      }
      return isRegistered;
   }

   @Override
   public void register(String context, OseeHttpServlet servlet) {
      InternalOseeHttpServlet internalServlet = servlet;
      internalServlet.setRequestsAllowed(getApplicationServerInfo().isAcceptingRequests());
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

   private InternalOseeServerInfo getApplicationServerInfo() {
      return applicationServerInfo;
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

   private void updateServletRequestsAllowed(final boolean value) {
      for (String contexts : oseeHttpServlets.keySet()) {
         InternalOseeHttpServlet servlets = oseeHttpServlets.get(contexts);
         servlets.setRequestsAllowed(value);
      }
   }

   @Override
   public synchronized void setServletRequestsAllowed(final boolean value) throws OseeCoreException {
      if (getApplicationServerInfo().isAcceptingRequests() != value) {
         boolean wasSuccessful = serverDataStore.updateServerState(getApplicationServerInfo(), value);
         if (wasSuccessful) {
            getApplicationServerInfo().setAcceptingRequests(value);
            updateServletRequestsAllowed(value);
         }
      }
   }

   @Override
   public void shutdown() throws OseeCoreException {
      if (timer != null) {
         timer.cancel();
      }
      setServletRequestsAllowed(false);
   }

   @Override
   public List<String> getCurrentProcesses() {
      List<String> processList = new ArrayList<String>();
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
   public String getServerAddress() {
      return getApplicationServerInfo().getServerAddress();
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
   public boolean isAcceptingRequests() {
      return getApplicationServerInfo().isAcceptingRequests();
   }

   @Override
   public String[] getSupportedVersions() {
      return getApplicationServerInfo().getVersion();
   }

   @Override
   public void addSupportedVersion(String version) throws OseeCoreException {
      getApplicationServerInfo().addVersion(version);
   }

   @Override
   public void removeSupportedVersion(String version) throws OseeCoreException {
      getApplicationServerInfo().removeVersion(version);
   }

}
