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
package org.eclipse.osee.orcs.core.internal.indexer;

import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.model.ReadableBranch;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.core.internal.indexer.collector.IndexerCollectorNotifier;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.search.IndexerCollector;
import org.eclipse.osee.orcs.search.QueryIndexer;

/**
 * @author Roberto E. Escobar
 */
public class QueryIndexerImpl implements QueryIndexer {

   private final Log logger;
   private final SessionContext sessionContext;
   private final ExecutorAdmin executorAdmin;
   private final IndexerCollector systemCollector;
   private final QueryEngineIndexer engineIndexer;
   private final AttributeTypes attributeTypes;

   public QueryIndexerImpl(Log logger, SessionContext sessionContext, ExecutorAdmin executorAdmin, QueryEngineIndexer engineIndexer, IndexerCollector systemCollector, AttributeTypes attributeTypes) {
      this.logger = logger;
      this.sessionContext = sessionContext;
      this.executorAdmin = executorAdmin;
      this.systemCollector = systemCollector;
      this.engineIndexer = engineIndexer;
      this.attributeTypes = attributeTypes;
   }

   private IndexerCollector merge(IndexerCollector collector) {
      IndexerCollectorNotifier notifier = new IndexerCollectorNotifier(logger);
      notifier.addCollector(systemCollector);
      if (collector != null) {
         notifier.addCollector(collector);
      }
      return notifier;
   }

   @Override
   public CancellableCallable<Integer> indexAllFromQueue() {
      return indexAllFromQueue(null);
   }

   @Override
   public CancellableCallable<Integer> indexAllFromQueue(IndexerCollector collector) {
      return engineIndexer.indexAllFromQueue(sessionContext.getSessionId(), attributeTypes, merge(collector));
   }

   @Override
   public CancellableCallable<?> indexBranches(Set<ReadableBranch> branches, boolean indexOnlyMissing) {
      return indexBranches(null, branches, indexOnlyMissing);
   }

   @Override
   public CancellableCallable<?> indexBranches(final IndexerCollector collector, final Set<ReadableBranch> branches, final boolean indexOnlyMissing) {
      return new CancellableCallable<Void>() {
         @Override
         public Void call() throws Exception {
            engineIndexer.indexBranches(sessionContext.getSessionId(), attributeTypes, merge(collector),
               attributeTypes.getAllTaggable(), branches, indexOnlyMissing);
            return null;
         }
      };
   }

   @Override
   public CancellableCallable<List<Future<?>>> indexXmlStream(InputStream inputStream) {
      return indexXmlStream(null, inputStream);
   }

   @Override
   public CancellableCallable<List<Future<?>>> indexXmlStream(IndexerCollector collector, InputStream inputStream) {
      return engineIndexer.indexXmlStream(sessionContext.getSessionId(), attributeTypes, merge(collector), inputStream);
   }

   @Override
   public void submitXmlStream(InputStream inputStream) throws Exception {
      Callable<?> callable = indexXmlStream(null, inputStream);
      executorAdmin.schedule(callable);
   }

   @Override
   public CancellableCallable<Integer> deleteIndexByQueryId(int queueId) {
      return engineIndexer.deleteIndexByQueryId(sessionContext.getSessionId(), queueId);
   }

   @Override
   public CancellableCallable<Integer> purgeAllIndexes() {
      return engineIndexer.purgeAllIndexes(sessionContext.getSessionId());
   }

}
