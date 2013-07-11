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
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * @author Roberto E. Escobar
 */
public class ExecutorCache {

   private final ConcurrentHashMap<String, ListeningExecutorService> executors =
      new ConcurrentHashMap<String, ListeningExecutorService>();

   public void put(String id, ListeningExecutorService service) throws IllegalStateException {
      if (executors.putIfAbsent(id, service) != null) {
         throw new IllegalStateException(String.format("Error non-unique executor detected [%s]", id));
      }
   }

   public ListeningExecutorService getById(String id) throws IllegalArgumentException {
      if (id == null || id.length() <= 0) {
         throw new IllegalArgumentException("Error - executorId cannot be null");
      }
      ListeningExecutorService executor = executors.get(id);
      if (executor != null && (executor.isShutdown() || executor.isTerminated())) {
         executors.remove(id);
         executor = null;
      }
      return executor;
   }

   public Map<String, ListeningExecutorService> getExecutors() {
      return executors;
   }

   public void remove(String id) {
      executors.remove(id);
   }

}
