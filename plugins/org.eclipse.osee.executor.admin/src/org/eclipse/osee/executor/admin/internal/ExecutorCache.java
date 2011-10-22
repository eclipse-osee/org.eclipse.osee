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
package org.eclipse.osee.executor.admin.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author Roberto E. Escobar
 */
public class ExecutorCache implements ExecutorServiceLifecycleListener {

   private final Map<String, ExecutorService> executors = new ConcurrentHashMap<String, ExecutorService>();
   private final Map<String, ExecutorThreadFactory> factories = new ConcurrentHashMap<String, ExecutorThreadFactory>();

   public void put(String id, ExecutorService service) throws IllegalStateException {
      if (executors.containsKey(id)) {
         throw new IllegalStateException(String.format("Error non-unique executor detected [%s]", id));
      }
      executors.put(id, service);
   }

   public void put(String id, ExecutorThreadFactory factory) throws IllegalStateException {
      if (factories.containsKey(id)) {
         throw new IllegalStateException(String.format("Error non-unique thread factory detected [%s]", id));
      }
      factories.put(id, factory);
   }

   public void remove(String id) {
      executors.remove(id);
      factories.remove(id);
   }

   public ExecutorService getById(String id) throws IllegalArgumentException {
      if (id == null || id.length() <= 0) {
         throw new IllegalArgumentException("Error - executorId cannot be null");
      }
      return executors.get(id);
   }

   @Override
   public void onTerminate(String id) {
      remove(id);
   }

   public Map<String, ExecutorThreadFactory> getThreadFactories() {
      return factories;
   }

   public Map<String, ExecutorService> getExecutors() {
      return executors;
   }
}
