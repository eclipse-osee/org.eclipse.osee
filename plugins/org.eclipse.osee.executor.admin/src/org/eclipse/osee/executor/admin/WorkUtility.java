/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.executor.admin;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author Roberto E. Escobar
 */
public final class WorkUtility {

   private WorkUtility() {
      // Utility Class
   }

   public static interface PartitionFactory<T> {

      Callable<Collection<T>> createWorker(Collection<T> toProcess);

   }

   public static <T> List<Callable<Collection<T>>> partitionWork(Collection<T> work, PartitionFactory<T> factory) throws Exception {
      List<Callable<Collection<T>>> callables = new LinkedList<Callable<Collection<T>>>();

      if (!work.isEmpty()) {
         int numProcessors = Runtime.getRuntime().availableProcessors();
         int partitionSize = work.size() / numProcessors;

         List<T> subList = new LinkedList<T>();

         int index = 0;

         Iterator<T> iterator = work.iterator();
         while (iterator.hasNext()) {
            subList.add(iterator.next());

            if (index != 0 && partitionSize != 0 && (index % partitionSize == 0)) {
               Callable<Collection<T>> worker = factory.createWorker(subList);
               callables.add(worker);
               subList = new LinkedList<T>();
            }
            index++;
         }
         // Anything left over ?
         if (!subList.isEmpty()) {
            Callable<Collection<T>> worker = factory.createWorker(subList);
            callables.add(worker);
         }
      }
      return callables;
   }

   public static <T> List<Future<Collection<T>>> partitionAndScheduleWork(ExecutorAdmin executorAdmin, String executorId, PartitionFactory<T> factory, Collection<T> items) throws Exception {
      return partitionAndScheduleWork(executorAdmin, executorId, factory, items, null);

   }

   public static <T> List<Future<Collection<T>>> partitionAndScheduleWork(ExecutorAdmin executorAdmin, String executorId, PartitionFactory<T> factory, Collection<T> items, ExecutionCallback<Collection<T>> callback) throws Exception {
      List<Future<Collection<T>>> futures = new LinkedList<Future<Collection<T>>>();
      List<Callable<Collection<T>>> callables = partitionWork(items, factory);
      if (!callables.isEmpty()) {
         for (Callable<Collection<T>> callable : callables) {
            Future<Collection<T>> future = executorAdmin.schedule(executorId, callable, callback);
            futures.add(future);
         }
      }
      return futures;
   }
}
