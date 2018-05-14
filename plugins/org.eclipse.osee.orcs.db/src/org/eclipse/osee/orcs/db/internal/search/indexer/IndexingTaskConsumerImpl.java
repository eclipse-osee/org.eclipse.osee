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
package org.eclipse.osee.orcs.db.internal.search.indexer;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import org.eclipse.osee.framework.core.executor.ExecutionCallback;
import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.search.IndexerCollector;

/**
 * @author Roberto E. Escobar
 */
public class IndexingTaskConsumerImpl implements IndexingTaskConsumer {

   private final Map<Integer, Future<?>> futureTasks = new ConcurrentHashMap<>();

   private final IndexerCallableFactory factory;
   private final ExecutorAdmin executorAdmin;

   public IndexingTaskConsumerImpl(ExecutorAdmin executorAdmin, IndexerCallableFactory factory) {
      this.executorAdmin = executorAdmin;
      this.factory = factory;
   }

   @Override
   public int cancelTaskId(Collection<Integer> taskIds) {
      int toReturn = 0;
      for (int item : taskIds) {
         Future<?> task = futureTasks.get(item);
         if (task != null && !task.isDone() && !task.isCancelled()) {
            if (task.cancel(true)) {
               toReturn++;
            }
         }
      }
      return toReturn;
   }

   @Override
   public int getWorkersInQueue() {
      return futureTasks.size();
   }

   @Override
   @SuppressWarnings({"unchecked", "rawtypes"})
   public Future<?> submitTaskId(OrcsSession session, AttributeTypes types, IndexerCollector collector, final int queryId) throws Exception {
      Callable<?> callable = factory.createIndexerTaskCallable(session, types, collector, queryId);
      if (collector != null) {
         collector.onIndexTaskSubmit(queryId);
      }
      Future<?> future =
         executorAdmin.schedule(IndexerConstants.INDEXING_CONSUMER_EXECUTOR_ID, callable, new ExecutionCallback() {

            @Override
            public void onCancelled() {
               removeFromQueue();
            }

            @Override
            public void onSuccess(Object result) {
               removeFromQueue();
            }

            @Override
            public void onFailure(Throwable throwable) {
               removeFromQueue();
            }

            private void removeFromQueue() {
               futureTasks.remove(queryId);
            }
         });
      futureTasks.put(queryId, future);
      return future;
   }

}
