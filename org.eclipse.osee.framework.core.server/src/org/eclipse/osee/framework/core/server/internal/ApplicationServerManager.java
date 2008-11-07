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

import java.lang.Thread.State;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationServerManager implements IApplicationServerManager {
   private static final String OSGI_PORT_PROPERTY = "org.osgi.service.http.port";
   private Map<String, OseeServerThreadFactory> threadFactories;
   private final Map<String, InternalOseeHttpServlet> oseeHttpServlets;

   private final OseeServerInfo applicationServerInfo;
   private boolean isRegistered;

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
   }

   private OseeServerInfo createOseeServerInfo() {
      String serverAddress = "127.0.0.1";
      try {
         serverAddress = InetAddress.getLocalHost().getCanonicalHostName();
      } catch (UnknownHostException ex) {
      }
      int port = Integer.valueOf(System.getProperty(OSGI_PORT_PROPERTY, "-1"));
      return new OseeServerInfo(serverAddress, port, OseeCodeVersion.getVersion(), GlobalTime.GreenwichMeanTimestamp(),
            false);
   }

   public boolean executeLookupRegistration() {
      this.isRegistered = false;
      ApplicationServerDataStore.deregisterWithDb(getApplicationServerInfo());
      boolean status = ApplicationServerDataStore.registerWithDb(getApplicationServerInfo());
      applicationServerInfo.setAcceptingRequests(status);
      updateServletRequestsAllowed(getApplicationServerInfo().isAcceptingRequests());
      this.isRegistered = status;
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

}
