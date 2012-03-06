/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.executor.admin.internal;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * @author Roberto E. Escobar
 */
public class ExecutorWorkCache {

   private final Map<UUID, Future<?>> waiting = new ConcurrentHashMap<UUID, Future<?>>();
   private final Map<UUID, Future<?>> executing = new ConcurrentHashMap<UUID, Future<?>>();

   public ExecutorWorkCache() {
      //
   }

   public void scheduled(UUID id, Future<?> future) {
      waiting.put(id, future);
   }

   public void executing(UUID id, Future<?> future) {
      waiting.remove(id);
      executing.put(id, future);
   }

   public void completed(UUID id, Future<?> future) {
      waiting.remove(id);
      executing.remove(id);
   }

   public int cancelAll() {
      int itemsCancelled = 0;
      itemsCancelled += cancel(waiting.values());
      itemsCancelled += cancel(executing.values());
      return itemsCancelled;
   }

   private int cancel(Collection<Future<?>> futures) {
      int cancelCount = 0;
      for (Future<?> future : futures) {
         if (!future.isCancelled() && !future.isDone()) {
            future.cancel(true);
            cancelCount++;
         }
      }
      return cancelCount;
   }

   private void cleanUp(Map<UUID, Future<?>> data) {
      List<UUID> toRemove = new LinkedList<UUID>();
      for (Entry<UUID, Future<?>> entry : data.entrySet()) {
         Future<?> future = entry.getValue();
         if (future.isCancelled() || future.isDone()) {
            toRemove.add(entry.getKey());
         }
      }
      for (UUID item : toRemove) {
         data.remove(item);
      }
   }

   public int getWaiting() {
      return waiting.size();
   }

   public int getExecuting() {
      return executing.size();
   }

   public int getInWork() {
      return getWaiting() + getExecuting();
   }

   public void cleanUp() {
      cleanUp(waiting);
      cleanUp(executing);
   }
}
