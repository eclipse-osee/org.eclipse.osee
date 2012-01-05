/*
 * Created on Jan 4, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.threading;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;

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

   public List<T> executeWorkersBlocking() throws OseeCoreException {
      ExecutorService executor = Executors.newFixedThreadPool(numberOfWorkers);
      List<T> toReturn = new LinkedList<T>();
      Collection<Callable<T>> workers = createWorkers();

      try {
         for (Future<T> future : executor.invokeAll(workers)) {
            toReturn.add(future.get());
         }
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
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
      Collection<Callable<T>> workers = new LinkedList<Callable<T>>();
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
