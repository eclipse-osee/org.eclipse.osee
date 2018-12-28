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
package org.eclipse.osee.framework.core.executor.internal;

import com.google.common.util.concurrent.ListeningExecutorService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;

/**
 * @author Roberto E. Escobar
 */
public class ExecutorCache {

   private final ConcurrentHashMap<String, ListeningExecutorService> executors =
      new ConcurrentHashMap<>();

   public void put(String id, ListeningExecutorService service) {
      if (executors.putIfAbsent(id, service) != null) {
         throw new OseeStateException("Error non-unique executor detected [%s]", id);
      }
   }

   public ListeningExecutorService getById(String id) {
      if (id == null || id.length() <= 0) {
         throw new OseeArgumentException("Error - executorId cannot be null");
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
