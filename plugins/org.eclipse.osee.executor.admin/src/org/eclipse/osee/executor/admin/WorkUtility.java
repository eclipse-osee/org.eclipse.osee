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

import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.eclipse.osee.executor.admin.internal.ExecutorAdminImpl;

/**
 * @author Roberto E. Escobar
 */
public final class WorkUtility {

   private static final int NUM_PARTITIONS = Math.min(4, Runtime.getRuntime().availableProcessors());

   private WorkUtility() {
      // Utility Class
   }

   public static interface PartitionFactory<INPUT, OUTPUT> {

      Callable<Collection<OUTPUT>> createWorker(Collection<INPUT> toProcess);

   }

   public static <INPUT, OUTPUT> List<Callable<Collection<OUTPUT>>> partitionWork(Iterable<INPUT> work, PartitionFactory<INPUT, OUTPUT> factory) throws Exception {
      List<Callable<Collection<OUTPUT>>> callables = new LinkedList<>();
      int size = Iterables.size(work);
      if (size > 0) {
         int partitionSize = Math.max(1, size / NUM_PARTITIONS);

         List<INPUT> subList = new LinkedList<>();

         int index = 0;

         Iterator<INPUT> iterator = work.iterator();
         while (iterator.hasNext()) {
            subList.add(iterator.next());

            if (index != 0 && partitionSize != 0 && index % partitionSize == 0) {
               Callable<Collection<OUTPUT>> worker = factory.createWorker(subList);
               callables.add(worker);
               subList = new LinkedList<>();
            }
            index++;
         }
         // Anything left over ?
         if (!subList.isEmpty()) {
            Callable<Collection<OUTPUT>> worker = factory.createWorker(subList);
            callables.add(worker);
         }
      }
      return callables;
   }

   public static <INPUT, OUTPUT> List<Future<Collection<OUTPUT>>> partitionAndScheduleWork(ExecutorAdmin executorAdmin, PartitionFactory<INPUT, OUTPUT> factory, Iterable<INPUT> items) throws Exception {
      return partitionAndScheduleWork(executorAdmin, ExecutorAdminImpl.DEFAULT_EXECUTOR, factory, items, null);
   }

   public static <INPUT, OUTPUT> List<Future<Collection<OUTPUT>>> partitionAndScheduleWork(ExecutorAdmin executorAdmin, PartitionFactory<INPUT, OUTPUT> factory, Iterable<INPUT> items, ExecutionCallback<Collection<OUTPUT>> callback) throws Exception {
      return partitionAndScheduleWork(executorAdmin, ExecutorAdminImpl.DEFAULT_EXECUTOR, factory, items, callback);
   }

   public static <INPUT, OUTPUT> List<Future<Collection<OUTPUT>>> partitionAndScheduleWork(ExecutorAdmin executorAdmin, String executorId, PartitionFactory<INPUT, OUTPUT> factory, Iterable<INPUT> items) throws Exception {
      return partitionAndScheduleWork(executorAdmin, executorId, factory, items, null);
   }

   public static <INPUT, OUTPUT> List<Future<Collection<OUTPUT>>> partitionAndScheduleWork(ExecutorAdmin executorAdmin, String executorId, PartitionFactory<INPUT, OUTPUT> factory, Iterable<INPUT> items, ExecutionCallback<Collection<OUTPUT>> callback) throws Exception {
      List<Future<Collection<OUTPUT>>> futures = new LinkedList<>();
      List<Callable<Collection<OUTPUT>>> callables = partitionWork(items, factory);
      if (!callables.isEmpty()) {
         for (Callable<Collection<OUTPUT>> callable : callables) {
            Future<Collection<OUTPUT>> future = executorAdmin.schedule(executorId, callable, callback);
            futures.add(future);
         }
      }
      return futures;
   }
}
