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

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.Branch;
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
   private final QueryEngineIndexer engineIndexer;
   private final AttributeTypes attributeTypes;

   public QueryIndexerImpl(OrcsSession session, QueryEngineIndexer engineIndexer, AttributeTypes attributeTypes) {
      this.session = session;
      this.engineIndexer = engineIndexer;
      this.attributeTypes = attributeTypes;
   }

   @Override
   public CancellableCallable<Integer> indexAllFromQueue(IndexerCollector... collector) {
      return engineIndexer.indexAllFromQueue(session, attributeTypes, collector);
   }

   @Override
   public CancellableCallable<Integer> indexBranches(final Set<Branch> branches, final boolean indexOnlyMissing, final IndexerCollector... collector) {
      return new CancellableCallable<Integer>() {
         @Override
         public Integer call() throws Exception {
            return engineIndexer.indexBranches(session, attributeTypes, attributeTypes.getAllTaggable(), branches,
               indexOnlyMissing, collector).call();
         }
      };
   }

   @Override
   public CancellableCallable<List<Future<?>>> indexResources(Iterable<Long> gammaIds, IndexerCollector... collector) {
      return engineIndexer.indexResources(session, attributeTypes, gammaIds, collector);
   }

   @Override
   public void indexAttrTypeIds(Iterable<Long> attrTypeIds) {
      engineIndexer.indexAttrTypeIds(session, attributeTypes, attrTypeIds);
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
