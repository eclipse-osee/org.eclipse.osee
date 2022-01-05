/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.search.indexer;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.executor.ExecutionCallback;
import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.search.IndexerCollector;

/**
 * @author Roberto E. Escobar
 */
public class IndexingTaskConsumerImpl implements IndexingTaskConsumer {

   private final Map<Long, Future<?>> futureTasks = new ConcurrentHashMap<>();

   private final IndexerCallableFactory factory;
   private final ExecutorAdmin executorAdmin;

   public IndexingTaskConsumerImpl(ExecutorAdmin executorAdmin, IndexerCallableFactory factory) {
      this.executorAdmin = executorAdmin;
      this.factory = factory;
   }

   @Override
   public Long cancelTaskId(Collection<Long> taskIds) {
      Long toReturn = 0L;
      for (Long item : taskIds) {
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
   public Future<?> submitTaskId(OrcsSession session, OrcsTokenService tokenService, IndexerCollector collector, final Long queryId) throws Exception {
      Callable<?> callable = factory.createIndexerTaskCallable(session, tokenService, collector, queryId);
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
