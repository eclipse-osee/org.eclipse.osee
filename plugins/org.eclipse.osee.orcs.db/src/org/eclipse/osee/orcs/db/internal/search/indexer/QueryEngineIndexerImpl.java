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

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.model.ReadableBranch;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.IndexerData;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.db.internal.search.indexer.callable.DeleteTagSetDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.search.indexer.callable.IndexerDatabaseStatisticsCallable;
import org.eclipse.osee.orcs.db.internal.search.indexer.callable.PurgeAllTagsDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.search.indexer.callable.producer.IndexAllInQueueCallable;
import org.eclipse.osee.orcs.db.internal.search.indexer.callable.producer.IndexBranchesDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.search.indexer.callable.producer.XmlStreamIndexerDatabaseCallable;
import org.eclipse.osee.orcs.search.IndexerCollector;

/**
 * @author Roberto E. Escobar
 */
public class QueryEngineIndexerImpl implements QueryEngineIndexer {

   private final Log logger;
   private final IOseeDatabaseService dbService;
   private final IdentityService identityService;
   private final IndexingTaskConsumer consumer;

   public QueryEngineIndexerImpl(Log logger, IOseeDatabaseService dbService, IdentityService identityService, IndexingTaskConsumer indexingConsumer) {
      this.logger = logger;
      this.dbService = dbService;
      this.identityService = identityService;
      this.consumer = indexingConsumer;
   }

   @Override
   public CancellableCallable<Integer> deleteIndexByQueryId(OrcsSession session, int queueId) {
      return new DeleteTagSetDatabaseTxCallable(logger, session, dbService, queueId);
   }

   @Override
   public CancellableCallable<Integer> purgeAllIndexes(OrcsSession session) {
      return new PurgeAllTagsDatabaseCallable(logger, session, dbService);
   }

   @Override
   public CancellableCallable<IndexerData> getIndexerData(OrcsSession session) {
      return new IndexerDatabaseStatisticsCallable(logger, session, dbService);
   }

   @Override
   public CancellableCallable<?> indexBranches(OrcsSession session, AttributeTypes types, IndexerCollector collector, Collection<? extends IAttributeType> typeToTag, Set<ReadableBranch> branches, boolean indexOnlyMissing) {
      return new IndexBranchesDatabaseCallable(logger, session, dbService, identityService, types, consumer, collector,
         typeToTag, branches, indexOnlyMissing);
   }

   @Override
   public CancellableCallable<Integer> indexAllFromQueue(OrcsSession session, AttributeTypes types, IndexerCollector collector) {
      return new IndexAllInQueueCallable(logger, session, dbService, types, consumer, collector);
   }

   @Override
   public CancellableCallable<List<Future<?>>> indexXmlStream(OrcsSession session, AttributeTypes types, IndexerCollector collector, InputStream inputStream) {
      return new XmlStreamIndexerDatabaseCallable(logger, session, dbService, types, consumer, collector,
         IndexerConstants.INDEXER_CACHE_ALL_ITEMS, IndexerConstants.INDEXER_CACHE_LIMIT, inputStream);
   }

}
