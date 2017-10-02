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
package org.eclipse.osee.framework.core.threading;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author John R. Misinco
 */
public class ThreadedWorkerExecutor<T> {

   private final int numberOfWorkers;
   private final ThreadedWorkerFactory<T> factory;

   public ThreadedWorkerExecutor(ThreadedWorkerFactory<T> factory, boolean ioBound) {
      this(factory,
         ioBound ? Runtime.getRuntime().availableProcessors() * 2 : Runtime.getRuntime().availableProcessors());
   }

   public ThreadedWorkerExecutor(ThreadedWorkerFactory<T> factory, int numberOfWorkers) {
      this.factory = factory;
      this.numberOfWorkers = numberOfWorkers;
   }

   public List<T> executeWorkersBlocking()  {
      ExecutorService executor = Executors.newFixedThreadPool(numberOfWorkers);
      List<T> toReturn = new LinkedList<>();
      Collection<Callable<T>> workers = createWorkers();

      try {
         for (Future<T> future : executor.invokeAll(workers)) {
            toReturn.add(future.get());
         }
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      } finally {
         executor.shutdown();
      }
      return toReturn;
   }

   private Collection<Callable<T>> createWorkers() {
      int partitionSize = factory.getWorkSize() / numberOfWorkers;
      int remainder = factory.getWorkSize() % numberOfWorkers;
      int startIndex = 0;
      int endIndex = 0;
      Collection<Callable<T>> workers = new LinkedList<>();
      for (int i = 0; i < numberOfWorkers; i++) {
         startIndex = endIndex;
         endIndex = startIndex + partitionSize;
         if (i == 0) {
            endIndex += remainder;
         }
         workers.add(factory.createWorker(startIndex, endIndex));
      }
      return workers;
   }

}
