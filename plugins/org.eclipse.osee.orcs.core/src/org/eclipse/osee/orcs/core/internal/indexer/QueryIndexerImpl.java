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
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.search.IndexerCollector;
import org.eclipse.osee.orcs.search.QueryIndexer;

/**
 * @author Roberto E. Escobar
 */
public class QueryIndexerImpl implements QueryIndexer {

   private final OrcsSession session;
   private final ExecutorAdmin executorAdmin;
   private final QueryEngineIndexer engineIndexer;
   private final AttributeTypes attributeTypes;

   public QueryIndexerImpl(OrcsSession session, ExecutorAdmin executorAdmin, QueryEngineIndexer engineIndexer, AttributeTypes attributeTypes) {
      this.session = session;
      this.executorAdmin = executorAdmin;
      this.engineIndexer = engineIndexer;
      this.attributeTypes = attributeTypes;
   }

   @Override
   public CancellableCallable<Integer> indexAllFromQueue(IndexerCollector... collector) {
      return engineIndexer.indexAllFromQueue(session, attributeTypes, collector);
   }

   @Override
   public CancellableCallable<Integer> indexBranches(final Set<ReadableBranch> branches, final boolean indexOnlyMissing, final IndexerCollector... collector) {
      return new CancellableCallable<Integer>() {
         @Override
         public Integer call() throws Exception {
            return engineIndexer.indexBranches(session, attributeTypes, attributeTypes.getAllTaggable(), branches,
               indexOnlyMissing, collector).call();
         }
      };
   }

   @Override
   public CancellableCallable<List<Future<?>>> indexXmlStream(InputStream inputStream, IndexerCollector... collector) {
      return engineIndexer.indexXmlStream(session, attributeTypes, inputStream, collector);
   }

   @Override
   public void submitXmlStream(InputStream inputStream) throws Exception {
      Callable<?> callable = indexXmlStream(inputStream);
      executorAdmin.schedule(callable);
   }

   @Override
   public CancellableCallable<Integer> deleteIndexByQueryId(int queueId) {
      return engineIndexer.deleteIndexByQueryId(session, queueId);
   }

   @Override
   public CancellableCallable<Integer> purgeAllIndexes() {
      return engineIndexer.purgeAllIndexes(session);
   }

}
