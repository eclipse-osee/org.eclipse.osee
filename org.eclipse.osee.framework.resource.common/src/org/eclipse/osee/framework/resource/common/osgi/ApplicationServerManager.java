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
package org.eclipse.osee.framework.resource.common.osgi;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import org.eclipse.osee.framework.resource.common.IApplicationServerManager;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationServerManager implements IApplicationServerManager {
   private Map<String, OseeServerThreadFactory> threadFactories;
   private final Map<String, OseeHttpServlet> oseeHttpServlets;

   public ApplicationServerManager() {
      this.oseeHttpServlets = Collections.synchronizedMap(new HashMap<String, OseeHttpServlet>());
      this.threadFactories = Collections.synchronizedMap(new HashMap<String, OseeServerThreadFactory>());
   }

   void register(String context, OseeHttpServlet servlets) {
      this.oseeHttpServlets.put(context, servlets);
   }

   void unregister(String key) {
      this.oseeHttpServlets.remove(key);
      this.threadFactories.remove(key);
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
         OseeHttpServlet servlets = oseeHttpServlets.get(contexts);
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

   public void setServletRequestsAllowed(boolean value) {
      for (String contexts : oseeHttpServlets.keySet()) {
         OseeHttpServlet servlets = oseeHttpServlets.get(contexts);
         servlets.setRequestsAllowed(value);
      }
   }

   public void shutdown() {
      setServletRequestsAllowed(false);

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
         OseeHttpServlet servlets = oseeHttpServlets.get(contexts);
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
         OseeHttpServlet servlets = oseeHttpServlets.get(contexts);
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
