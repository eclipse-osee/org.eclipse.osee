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
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.OseeInfo;
import org.eclipse.osee.framework.jdk.core.util.ChecksumUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
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

   private ApplicationServerDataStore dataStore;
   private OseeServerInfoMutable serverInfo;
   private Timer timer;
   private final Set<String> defaultVersions = new HashSet<String>();

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
      defaultVersions.clear();
      String[] userSpecifiedVersion = OseeServerProperties.getOseeVersion();
      if (Conditions.hasValues(userSpecifiedVersion)) {
         for (String version : userSpecifiedVersion) {
            defaultVersions.add(version);
         }
      } else {
         defaultVersions.add(OseeCodeVersion.getVersion());
      }

      dataStore = new ApplicationServerDataStore(getLogger(), getDatabaseService());
      serverInfo = createOseeServerInfo(getLogger(), dataStore, defaultVersions);

      timer = new Timer();
      timer.schedule(new TimerTask() {
         @Override
         public void run() {
            if (isDbInitialized()) {
               try {
                  boolean wasRegistered = executeLookupRegistration();
                  setServletRequestsAllowed(wasRegistered);
               } catch (Exception ex) {
                  getLogger().error(ex, "Error during lookup registration");
               } finally {
                  timer.cancel();
               }
            } else {
               setServletRequestsAllowedNoDbUpdate(true);
            }
         }

         private boolean isDbInitialized() {
            boolean result = false;
            try {
               String id = OseeInfo.getDatabaseGuid();
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
      defaultVersions.clear();
   }

   private static OseeServerInfoMutable createOseeServerInfo(Log logger, ApplicationServerDataStore dataStore, Set<String> defaultVersions) {
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
      OseeServerInfoMutable toReturn =
         new OseeServerInfoMutable(checkSum, uri.toString(), new String[0], GlobalTime.GreenwichMeanTimestamp(), false);
      toReturn.setVersions(defaultVersions);
      return toReturn;
   }

   private void refreshData(OseeServerInfoMutable info) {
      dataStore.refresh(info);

      Set<String> supportedVersions = info.getVersionSet();
      if (!supportedVersions.containsAll(defaultVersions)) {
         supportedVersions.addAll(defaultVersions);
         info.setVersions(supportedVersions);
      }
   }

   @Override
   public boolean executeLookupRegistration() {
      OseeServerInfoMutable info = getApplicationServerInfo();
      refreshData(info);
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

   private OseeServerInfoMutable getApplicationServerInfo() {
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

   private static enum PersistType {
      ALLOW_DB_PERSIST,
      DONT_DB_PERSIST;
   }

   private synchronized void setServletRequestsAllowed(final boolean value, PersistType persistType) throws OseeCoreException {
      OseeServerInfoMutable info = getApplicationServerInfo();
      info.setAcceptingRequests(value);
      if (PersistType.ALLOW_DB_PERSIST == persistType) {
         dataStore.update(Collections.singleton(info));
      }
      for (String contexts : oseeHttpServlets.keySet()) {
         InternalOseeHttpServlet servlets = oseeHttpServlets.get(contexts);
         servlets.setRequestsAllowed(value);
      }
   }

   private void setServletRequestsAllowedNoDbUpdate(final boolean value) {
      try {
         setServletRequestsAllowed(value, PersistType.DONT_DB_PERSIST);
      } catch (OseeCoreException ex) {
         logger.warn(ex, "Error updating servlet requests allowed to [%s]- current setting is [%s]", value,
            getApplicationServerInfo().isAcceptingRequests());
      }
   }

   @Override
   public synchronized void setServletRequestsAllowed(final boolean value) throws OseeCoreException {
      setServletRequestsAllowed(value, PersistType.ALLOW_DB_PERSIST);
   }

   @Override
   public void shutdown() {
      if (timer != null) {
         timer.cancel();
      }
      OseeServerInfoMutable info = getApplicationServerInfo();
      deregisterWithDb(info);
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
   public URI getServerUri() {
      return getApplicationServerInfo().getUri();
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
      OseeServerInfoMutable info = getApplicationServerInfo();
      refreshData(info);
      return getApplicationServerInfo().getVersion();
   }

   @Override
   public void addSupportedVersion(String version) throws OseeCoreException {
      Conditions.checkNotNull(version, "Osee Version");
      OseeServerInfoMutable info = getApplicationServerInfo();
      refreshData(info);
      info.addVersion(version);
      dataStore.update(Collections.singleton(info));
   }

   @Override
   public void removeSupportedVersion(String version) throws OseeCoreException {
      Conditions.checkNotNull(version, "Osee Version");
      Conditions.checkExpressionFailOnTrue(defaultVersions.contains(version),
         "Unable to remove default Osee version [%s]", version);

      OseeServerInfoMutable info = getApplicationServerInfo();
      refreshData(info);
      Set<String> versions = info.getVersionSet();
      boolean wasRemoved = versions.remove(version);
      if (wasRemoved) {
         info.setVersions(versions);
         dataStore.update(Collections.singleton(info));
      } else {
         throw new OseeStateException("Not part of the supported version [%s]", version);
      }
   }

   private boolean deregisterWithDb(OseeServerInfo info) {
      boolean status = false;
      try {
         dataStore.delete(Collections.singleton(info));
         status = true;
      } catch (OseeCoreException ex) {
         getLogger().info("Server lookup table is not initialized");
      }
      return status;
   }

   private boolean registerWithDb(OseeServerInfo info) {
      boolean status = false;
      try {
         dataStore.create(Collections.singleton(info));
         status = true;
      } catch (OseeCoreException ex) {
         getLogger().info("Server lookup table is not initialized");
      }
      return status;
   }

}
