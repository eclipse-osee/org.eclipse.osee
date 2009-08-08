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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.server.CoreServerActivator;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.jdk.core.util.ChecksumUtil;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationServerManager implements IApplicationServerManager {
   private final Map<String, OseeServerThreadFactory> threadFactories;
   private final Map<String, InternalOseeHttpServlet> oseeHttpServlets;

   private final InternalOseeServerInfo applicationServerInfo;
   private final Timer timer;

   public ApplicationServerManager() {
      this.oseeHttpServlets = Collections.synchronizedMap(new HashMap<String, InternalOseeHttpServlet>());
      this.threadFactories = Collections.synchronizedMap(new HashMap<String, OseeServerThreadFactory>());
      this.applicationServerInfo = createOseeServerInfo();
      applicationServerInfo.setAcceptingRequests(true);

      timer = new Timer();
      timer.schedule(new TimerTask() {
         @Override
         public void run() {
            try {
               executeLookupRegistration();
            } catch (Exception ex) {
               OseeLog.log(CoreServerActivator.class, Level.SEVERE, ex);
            } finally {
               timer.cancel();
            }
         }
      }, 3 * 1000);
   }

   private InternalOseeServerInfo createOseeServerInfo() {
      String serverAddress = "127.0.0.1";
      try {
         serverAddress = InetAddress.getLocalHost().getCanonicalHostName();
      } catch (UnknownHostException ex) {
      }
      int port = OseeServerProperties.getOseeApplicationServerPort();

      String checkSum = "-1";
      try {
         String address = String.format("%s:%s", serverAddress, port);
         ByteArrayInputStream inputStream = new ByteArrayInputStream(address.getBytes("UTF-8"));
         checkSum = ChecksumUtil.createChecksumAsString(inputStream, ChecksumUtil.MD5);
      } catch (Exception ex) {
         OseeLog.log(CoreServerActivator.class, Level.SEVERE, "Error generating application server id", ex);
      }

      return new InternalOseeServerInfo(checkSum, serverAddress, port, GlobalTime.GreenwichMeanTimestamp(), false);
   }

   public boolean executeLookupRegistration() {
      boolean isRegistered = getApplicationServerInfo().updateRegistration();
      if (isRegistered) {
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

   private InternalOseeServerInfo getApplicationServerInfo() {
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
      timer.cancel();
      setServletRequestsAllowed(false);
      ApplicationServerDataStore.deregisterWithDb(getApplicationServerInfo());
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

   @Override
   public String getId() {
      return getApplicationServerInfo().getServerId();
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
