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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.IndexerData;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.db.internal.search.indexer.callable.DeleteTagSetDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.search.indexer.callable.IndexerDatabaseStatisticsCallable;
import org.eclipse.osee.orcs.db.internal.search.indexer.callable.PurgeAllTagsDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.search.indexer.callable.producer.IndexAllInQueueCallable;
import org.eclipse.osee.orcs.db.internal.search.indexer.callable.producer.IndexBranchesDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.search.indexer.callable.producer.IndexerDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.search.IndexerCollector;

/**
 * @author Roberto E. Escobar
 */
public class QueryEngineIndexerImpl implements QueryEngineIndexer {

   private final Log logger;
   private final JdbcClient jdbcClient;
   private final SqlJoinFactory joinFactory;
   private final IndexingTaskConsumer consumer;

   private final IndexerCollectorNotifier systemCollector;

   public QueryEngineIndexerImpl(Log logger, JdbcClient jdbcClient, SqlJoinFactory joinFactory, IndexingTaskConsumer indexingConsumer) {
      this.logger = logger;
      this.jdbcClient = jdbcClient;
      this.joinFactory = joinFactory;
      this.consumer = indexingConsumer;
      this.systemCollector = new IndexerCollectorNotifier(logger);
   }

   @Override
   public CancellableCallable<Integer> deleteIndexByQueryId(OrcsSession session, int queueId) {
      return new DeleteTagSetDatabaseTxCallable(logger, session, jdbcClient, queueId);
   }

   @Override
   public CancellableCallable<Integer> purgeAllIndexes(OrcsSession session) {
      return new PurgeAllTagsDatabaseCallable(logger, session, jdbcClient);
   }

   @Override
   public CancellableCallable<IndexerData> getIndexerData(OrcsSession session) {
      return new IndexerDatabaseStatisticsCallable(logger, session, jdbcClient);
   }

   @Override
   public CancellableCallable<Integer> indexBranches(OrcsSession session, OrcsTokenService tokenService, Set<Branch> branches, boolean indexOnlyMissing, IndexerCollector... collector) {
      return new IndexBranchesDatabaseCallable(logger, session, jdbcClient, joinFactory, tokenService, consumer,
         merge(collector), branches, indexOnlyMissing);
   }

   @Override
   public CancellableCallable<Integer> indexAllFromQueue(OrcsSession session, OrcsTokenService tokenService, IndexerCollector... collector) {
      return new IndexAllInQueueCallable(logger, session, jdbcClient, joinFactory, tokenService, consumer,
         merge(collector));
   }

   @Override
   public CancellableCallable<List<Future<?>>> indexResources(OrcsSession session, OrcsTokenService tokenService, Iterable<Long> datas, IndexerCollector... collector) {
      return new IndexerDatabaseCallable(logger, session, jdbcClient, joinFactory, tokenService, consumer,
         merge(collector), IndexerConstants.INDEXER_CACHE_ALL_ITEMS, IndexerConstants.INDEXER_CACHE_LIMIT, datas);
   }

   @Override
   public void indexAttrTypeIds(OrcsSession session, OrcsTokenService tokenService, Iterable<Long> attrTypeIds) {
      String GAMMAS_BY_TYPE = "select gamma_id from osee_attribute where attr_type_id = ?";
      List<Long> gammaIds = new LinkedList<>();
      for (Long attributeType : attrTypeIds) {
         try (JdbcStatement chStmt = jdbcClient.getStatement()) {
            chStmt.runPreparedQueryWithMaxFetchSize(GAMMAS_BY_TYPE, attributeType);
            while (chStmt.next()) {
               gammaIds.add(chStmt.getLong("gamma_id"));
            }
         }
         try {
            new IndexerDatabaseCallable(logger, session, jdbcClient, joinFactory, tokenService, consumer, null,
               IndexerConstants.INDEXER_CACHE_ALL_ITEMS, IndexerConstants.INDEXER_CACHE_LIMIT, gammaIds).call();
         } catch (Exception ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
         System.out.println(String.format("Processed %d gammas for type %d", gammaIds.size(), attributeType));
         gammaIds.clear();
      }
   }

   @Override
   public void indexAttrTypeMissingOnly(OrcsTokenService tokenService, Iterable<Long> attrTypeIds) {
      String MISSING_GAMMAS_BY_TYPE =
         "SELECT DISTINCT att.gamma_id FROM OSEE_ATTRIBUTE att, osee_txs txs WHERE attr_type_id IN (" + Collections.toString(
            ",",
            attrTypeIds) + ") AND att.GAMMA_ID = txs.gamma_id AND txs.tx_current = 1 AND NOT EXISTS (SELECT 1 FROM osee_search_tags tag WHERE tag.gamma_id = att.gamma_id) AND length(value) > 1";
      List<Long> gammaIds = new LinkedList<>();
      try (JdbcStatement chStmt = jdbcClient.getStatement()) {
         chStmt.runPreparedQueryWithMaxFetchSize(MISSING_GAMMAS_BY_TYPE);
         while (chStmt.next()) {
            gammaIds.add(chStmt.getLong("gamma_id"));
         }
      }
      System.out.println("Found gammas to tag: " + gammaIds.size());
      try {
         new IndexerDatabaseCallable(logger, null, jdbcClient, joinFactory, tokenService, consumer, null,
            IndexerConstants.INDEXER_CACHE_ALL_ITEMS, IndexerConstants.INDEXER_CACHE_LIMIT, gammaIds).call();
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   private IndexerCollector merge(IndexerCollector... collectors) {
      IndexerCollector toReturn = systemCollector;
      if (collectors != null && collectors.length > 0) {
         IndexerCollectorNotifier notifier = new IndexerCollectorNotifier(logger);
         notifier.addCollector(systemCollector);
         for (IndexerCollector collector : collectors) {
            notifier.addCollector(collector);
         }
      }
      return toReturn;
   }

   @Override
   public void addCollector(IndexerCollector collector) {
      systemCollector.addCollector(collector);
   }

   @Override
   public void removeCollector(IndexerCollector collector) {
      systemCollector.removeCollector(collector);
   }

}
