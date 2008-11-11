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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.server.CoreServerActivator;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.DatabaseInfoManager;
import org.eclipse.osee.framework.db.connection.IDatabaseInfo;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.ChecksumUtil;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationServerManager implements IApplicationServerManager {
   private static final String OSGI_PORT_PROPERTY = "org.osgi.service.http.port";
   private Map<String, OseeServerThreadFactory> threadFactories;
   private final Map<String, InternalOseeHttpServlet> oseeHttpServlets;

   private final OseeServerInfo applicationServerInfo;
   private boolean isRegistered;
   private final static long TEN_MINUTES = 1000 * 60 * 10;
   private final static long TWENTY_MINUTES = 1000 * 60 * 20;

   public ApplicationServerManager() {
      this.oseeHttpServlets = Collections.synchronizedMap(new HashMap<String, InternalOseeHttpServlet>());
      this.threadFactories = Collections.synchronizedMap(new HashMap<String, OseeServerThreadFactory>());
      this.applicationServerInfo = createOseeServerInfo();
      this.isRegistered = false;
      applicationServerInfo.setAcceptingRequests(true);

      new Thread(new Runnable() {
         @Override
         public void run() {
            try {
               executeLookupRegistration();
            } catch (Exception ex) {
               ex.printStackTrace();
            }
         }
      }).start();

      Timer timer = new Timer("Clean up Join Tables");
      timer.scheduleAtFixedRate(new TimerTask() {
         public void run() {
            cleanUpJoinTables();
         }
      }, TEN_MINUTES, TEN_MINUTES);
   }

   private OseeServerInfo createOseeServerInfo() {
      String serverAddress = "127.0.0.1";
      try {
         serverAddress = InetAddress.getLocalHost().getCanonicalHostName();
      } catch (UnknownHostException ex) {
      }
      int port = Integer.valueOf(System.getProperty(OSGI_PORT_PROPERTY, "-1"));

      String checkSum = "-1";
      try {
         String address = String.format("%s:%s", serverAddress, port);
         ByteArrayInputStream inputStream = new ByteArrayInputStream(address.getBytes("UTF-8"));
         checkSum = ChecksumUtil.createChecksumAsString(inputStream, ChecksumUtil.MD5);
      } catch (Exception ex) {
         OseeLog.log(CoreServerActivator.class, Level.SEVERE, "Error generating application server id", ex);
      }

      return new OseeServerInfo(checkSum, serverAddress, port, OseeCodeVersion.getVersion(),
            GlobalTime.GreenwichMeanTimestamp(), false);
   }

   public boolean executeLookupRegistration() {
      this.isRegistered = false;
         ApplicationServerDataStore.deregisterWithDb(getApplicationServerInfo());
         boolean status = ApplicationServerDataStore.registerWithDb(getApplicationServerInfo());
         this.isRegistered = status;
      if (this.isRegistered) {
         OseeLog.log(CoreServerActivator.class, Level.INFO, String.format("Application Server: [%s] registered.",
               getApplicationServerInfo().getServerId()));
      }
      return isRegistered;
   }

   void register(String context, InternalOseeHttpServlet servlets) {
      servlets.setRequestsAllowed(getApplicationServerInfo().isAcceptingRequests());
      this.oseeHttpServlets.put(context, servlets);
   }

   void unregister(String key) {
      this.oseeHttpServlets.remove(key);
      this.threadFactories.remove(key);
   }

   public OseeServerInfo getApplicationServerInfo() {
      return applicationServerInfo;
   }

   public ThreadFactory createNewThreadFactory(String name, int priority) {
      OseeServerThreadFactory factory = new OseeServerThreadFactory(name, priority);
      this.threadFactories.put(name, factory);
      return factory;
   }

   private List<OseeServerThread> getThreadsFromFactory(String key) {
      OseeServerThreadFactory factory = threadFactories.get(key);
      return factory.getThreads();
   }

   public boolean isSystemIdle() {
      boolean result = true;
      for (String contexts : oseeHttpServlets.keySet()) {
         InternalOseeHttpServlet servlets = oseeHttpServlets.get(contexts);
         result &= !servlets.getState().equals(ProcessingStateEnum.BUSY);
      }

      for (String key : threadFactories.keySet()) {
         for (OseeServerThread thread : getThreadsFromFactory(key)) {
            State state = thread.getState();
            result &= !state.equals(State.TERMINATED);
         }
      }
      return result;
   }

   private void updateServletRequestsAllowed(final boolean value) {
      for (String contexts : oseeHttpServlets.keySet()) {
         InternalOseeHttpServlet servlets = oseeHttpServlets.get(contexts);
         servlets.setRequestsAllowed(value);
      }
   }

   public synchronized void setServletRequestsAllowed(final boolean value) throws OseeDataStoreException {
      if (getApplicationServerInfo().isAcceptingRequests() != value) {
         boolean wasSuccessful = ApplicationServerDataStore.updateServerState(getApplicationServerInfo(), value);
         if (wasSuccessful) {
            getApplicationServerInfo().setAcceptingRequests(value);
            updateServletRequestsAllowed(value);
         }
      }
   }

   public void shutdown() throws OseeCoreException {
      setServletRequestsAllowed(false);
      ApplicationServerDataStore.deregisterWithDb(getApplicationServerInfo());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.resource.common.IApplicationServerManager#getCurrentProcesses()
    */
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.resource.common.IApplicationServerManager#getNumberOfActiveThreads()
    */
   @Override
   public int getNumberOfActiveThreads() {
      int totalProcesses = 0;
      for (String contexts : oseeHttpServlets.keySet()) {
         InternalOseeHttpServlet servlets = oseeHttpServlets.get(contexts);
         if (servlets.getState().equals(ProcessingStateEnum.BUSY)) {
            totalProcesses++;
         }
      }

      for (String key : threadFactories.keySet()) {
         for (OseeServerThread thread : getThreadsFromFactory(key)) {
            State state = thread.getState();
            if (!state.equals(State.TERMINATED)) {
               totalProcesses++;
            }
         }
      }
      return totalProcesses;
   }

   private final static String DELETE_JOIN_TIME = "DELETE FROM %s WHERE insert_time < ?";
   private final static boolean DEBUG =
         Boolean.valueOf(Platform.getDebugOption("org.eclipse.osee.framework.core.server/debug/JoinCleanup"));

   private void cleanUpJoinTables() {
      try {
         if (DEBUG) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy 'at' hh:mm:ss");
            OseeLog.log(CoreServerActivator.class, Level.INFO, String.format("Join Table cleanup ran on %s",
                  sdf.format(cal.getTime())));
         }
         Timestamp time = new Timestamp(System.currentTimeMillis() - TWENTY_MINUTES);
         ConnectionHandler.runPreparedUpdate(String.format(DELETE_JOIN_TIME, "osee_join_artifact"), time);
         ConnectionHandler.runPreparedUpdate(String.format(DELETE_JOIN_TIME, "osee_join_attribute"), time);
         ConnectionHandler.runPreparedUpdate(String.format(DELETE_JOIN_TIME, "osee_join_transaction"), time);

      } catch (OseeDataStoreException ex) {
         // if (!ex.getMessage().contains("PSQLException")) {
         //Postrgesql Does not like sysdate.  Will ignore until we need to run on postgresql
         OseeLog.log(CoreServerActivator.class, Level.WARNING, ex);
         //}
      }
   }
}
