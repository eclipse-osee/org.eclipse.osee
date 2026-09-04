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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.StandardOptions;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.jdbc.DatabaseType;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.IndexerData;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.db.internal.resource.ResourceConstants;
import org.eclipse.osee.orcs.db.internal.search.indexer.callable.DeleteTagSetDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.search.indexer.callable.IndexerDatabaseStatisticsCallable;
import org.eclipse.osee.orcs.db.internal.search.indexer.callable.PurgeAllTagsDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.search.indexer.callable.producer.IndexAllInQueueCallable;
import org.eclipse.osee.orcs.db.internal.search.indexer.callable.producer.IndexBranchesDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.search.indexer.callable.producer.IndexerDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagCollector;
import org.eclipse.osee.orcs.db.internal.search.tagger.Tagger;
import org.eclipse.osee.orcs.db.internal.search.tagger.TaggingEngine;
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
   private final TaggingEngine taggingEngine;
   private final IResourceManager resourceManager;

   private final IndexerCollectorNotifier systemCollector;

   public QueryEngineIndexerImpl(Log logger, JdbcClient jdbcClient, SqlJoinFactory joinFactory, IndexingTaskConsumer indexingConsumer, TaggingEngine taggingEngine, IResourceManager resourceManager) {
      this.logger = logger;
      this.jdbcClient = jdbcClient;
      this.joinFactory = joinFactory;
      this.consumer = indexingConsumer;
      this.taggingEngine = taggingEngine;
      this.resourceManager = resourceManager;
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
   public CancellableCallable<Integer> indexBranches(OrcsSession session, OrcsTokenService tokenService,
      Set<Branch> branches, boolean indexOnlyMissing, IndexerCollector... collector) {
      return new IndexBranchesDatabaseCallable(logger, session, jdbcClient, joinFactory, tokenService, consumer,
         merge(collector), branches, indexOnlyMissing);
   }

   @Override
   public CancellableCallable<Integer> indexAllFromQueue(OrcsSession session, OrcsTokenService tokenService,
      IndexerCollector... collector) {
      return new IndexAllInQueueCallable(logger, session, jdbcClient, joinFactory, tokenService, consumer,
         merge(collector));
   }

   @Override
   public CancellableCallable<List<Future<?>>> indexResources(OrcsSession session, OrcsTokenService tokenService,
      Iterable<Long> datas, IndexerCollector... collector) {
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
         logger.info("Processed %d gammas for type %d", gammaIds.size(), attributeType);
         gammaIds.clear();
      }
   }

   @Override
   public void indexAttrTypeMissingOnly(OrcsTokenService tokenService, Iterable<Long> attrTypeIds) {
      List<Long> typeIdList = new ArrayList<>();
      for (Long id : attrTypeIds) {
         typeIdList.add(id);
      }

      int threadCount = Math.min(typeIdList.size(), 4);
      ExecutorService executor = Executors.newFixedThreadPool(threadCount);
      List<Future<?>> typeFutures = new ArrayList<>();

      for (Long attrTypeId : typeIdList) {
         typeFutures.add(executor.submit(() -> indexSingleAttrTypeMissing(tokenService, attrTypeId)));
      }

      executor.shutdown();
      try {
         executor.awaitTermination(4, TimeUnit.HOURS);
      } catch (InterruptedException ex) {
         Thread.currentThread().interrupt();
         OseeCoreException.wrapAndThrow(ex);
      }

      // Check for exceptions from individual type tasks
      for (Future<?> future : typeFutures) {
         try {
            future.get();
         } catch (Exception ex) {
            logger.error(ex, "Error during attr type indexing");
         }
      }
   }

   private void indexSingleAttrTypeMissing(OrcsTokenService tokenService, Long attrTypeId) {
      String MISSING_GAMMAS_BY_TYPE =
         "SELECT DISTINCT att.gamma_id FROM osee_attribute att, osee_txs txs WHERE att.attr_type_id = ? AND att.gamma_id = txs.gamma_id AND txs.tx_current = 1 AND NOT EXISTS (SELECT 1 FROM osee_search_tags_hash tag WHERE tag.gamma_id = att.gamma_id) AND length(att.value) > 0";
      List<Long> gammaIds = new LinkedList<>();
      try (JdbcStatement chStmt = jdbcClient.getStatement()) {
         chStmt.runPreparedQueryWithMaxFetchSize(MISSING_GAMMAS_BY_TYPE, attrTypeId);
         while (chStmt.next()) {
            gammaIds.add(chStmt.getLong("gamma_id"));
         }
      }
      logger.info("Found %d gammas to tag for attr type %d", gammaIds.size(), attrTypeId);
      if (gammaIds.isEmpty()) {
         return;
      }
      try {
         List<Future<?>> indexFutures =
            new IndexerDatabaseCallable(logger, null, jdbcClient, joinFactory, tokenService, consumer, null,
               IndexerConstants.INDEXER_CACHE_ALL_ITEMS, IndexerConstants.INDEXER_CACHE_LIMIT, gammaIds).call();
         // Wait for all indexing tasks to complete before returning
         for (Future<?> future : indexFutures) {
            future.get(10, TimeUnit.MINUTES);
         }
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      logger.info("Completed tagging for attr type %d", attrTypeId);
   }

   @Override
   public void indexDirectByAttrType(OrcsTokenService tokenService, Long attrTypeId) {
      String QUERY_ATTRS =
         "SELECT DISTINCT att.gamma_id, att.value, att.uri FROM osee_attribute att, osee_txs txs WHERE att.attr_type_id = ? AND att.gamma_id = txs.gamma_id AND txs.tx_current = 1 AND NOT EXISTS (SELECT 1 FROM osee_search_tags_hash tag WHERE tag.gamma_id = att.gamma_id)";

      TaggerTypeToken taggerType = tokenService.getAttributeTypeOrSentinel(attrTypeId).getTaggerType();
      if (!taggerType.isValid()) {
         logger.info("Attr type %d has no valid tagger, skipping", attrTypeId);
         return;
      }

      Tagger tagger = taggingEngine.getTagger(taggerType);
      List<Object[]> batchData = new ArrayList<>();
      long totalGammas = 0;
      long totalTags = 0;

      try (JdbcStatement chStmt = jdbcClient.getStatement()) {
         chStmt.runPreparedQueryWithMaxFetchSize(QUERY_ATTRS, attrTypeId);
         while (chStmt.next()) {
            long gammaId = chStmt.getLong("gamma_id");
            String value = chStmt.getString("value");
            String uri = chStmt.getString("uri");

            Set<Long> tags = new HashSet<>();
            TagCollector collector = (word, codedTag) -> tags.add(codedTag);

            try (InputStream input = getInputStream(value, uri)) {
               if (input != null) {
                  tagger.tagIt(input, collector);
               }
            } catch (Exception ex) {
               logger.error(ex, "Error tagging gamma %d", gammaId);
               continue;
            }

            for (Long tag : tags) {
               batchData.add(new Object[] {tag, gammaId});
            }
            totalTags += tags.size();
            totalGammas++;

            if (batchData.size() >= 10000) {
               jdbcClient.runBatchUpdate(OseeDb.OSEE_SEARCH_TAGS_HASH_TABLE.getInsertSql(), batchData);
               batchData.clear();
            }
         }
      }

      if (!batchData.isEmpty()) {
         jdbcClient.runBatchUpdate(OseeDb.OSEE_SEARCH_TAGS_HASH_TABLE.getInsertSql(), batchData);
         batchData.clear();
      }

      logger.info("Direct index complete for attr type %d: %d gammas, %d tags", attrTypeId, totalGammas, totalTags);
   }

   @Override
   public long indexRecentlyModified(OrcsTokenService tokenService, int hours) {
      List<Long> taggableTypeIds = new ArrayList<>();
      for (var attrType : tokenService.getTaggedAttrs()) {
         taggableTypeIds.add(attrType.getId());
      }
      if (taggableTypeIds.isEmpty()) {
         logger.info("No taggable attribute types found, nothing to index");
         return 0;
      }

      long totalGammas = 0;
      long totalTags = 0;

      try (var typeJoin = joinFactory.createIdJoinQuery()) {
         for (Long typeId : taggableTypeIds) {
            typeJoin.add(typeId);
         }
         typeJoin.store();

         // Find attributes modified in recent transactions, filtered to taggable types via join table,
         // that are missing from osee_search_tags_hash
         String hint = jdbcClient.getDbType().equals(DatabaseType.oracle)
            ? "/*+ LEADING(txd txs att oji) USE_NL(txs att) */" : "";
         String query = "SELECT " + hint + " DISTINCT att.gamma_id, att.value, att.uri, att.attr_type_id" //
            + " FROM osee_tx_details txd, osee_txs txs, osee_attribute att, osee_join_id oji" //
            + " WHERE txd.time > " + jdbcClient.getDbType().getTimestampMinusHours(hours) //
            + " AND txd.transaction_id = txs.transaction_id" //
            + " AND txs.gamma_id = att.gamma_id" //
            + " AND txs.tx_current = 1" //
            + " AND oji.query_id = ?" //
            + " AND att.attr_type_id = oji.id" //
            + " AND NOT EXISTS (SELECT 1 FROM osee_search_tags_hash tag WHERE tag.gamma_id = att.gamma_id)";

         List<Object[]> batchData = new ArrayList<>();

         try (JdbcStatement chStmt = jdbcClient.getStatement()) {
            chStmt.runPreparedQueryWithMaxFetchSize(query, typeJoin.getQueryId());
            while (chStmt.next()) {
               long gammaId = chStmt.getLong("gamma_id");
               String value = chStmt.getString("value");
               String uri = chStmt.getString("uri");
               long attrTypeId = chStmt.getLong("attr_type_id");

               TaggerTypeToken taggerType = tokenService.getAttributeTypeOrSentinel(attrTypeId).getTaggerType();
               if (!taggerType.isValid()) {
                  continue;
               }

               Tagger tagger = taggingEngine.getTagger(taggerType);
               Set<Long> tags = new HashSet<>();
               TagCollector collector = (word, codedTag) -> tags.add(codedTag);

               try (InputStream input = getInputStream(value, uri)) {
                  if (input != null) {
                     tagger.tagIt(input, collector);
                  }
               } catch (Exception ex) {
                  logger.error(ex, "Error tagging gamma %d", gammaId);
                  continue;
               }

               for (Long tag : tags) {
                  batchData.add(new Object[] {tag, gammaId});
               }
               totalTags += tags.size();
               totalGammas++;

               if (batchData.size() >= 10000) {
                  jdbcClient.runBatchUpdate(OseeDb.OSEE_SEARCH_TAGS_HASH_TABLE.getInsertSql(), batchData);
                  batchData.clear();
               }
            }
         }

         if (!batchData.isEmpty()) {
            jdbcClient.runBatchUpdate(OseeDb.OSEE_SEARCH_TAGS_HASH_TABLE.getInsertSql(), batchData);
            batchData.clear();
         }
      }

      logger.info("Recent index complete (%d hour window): %d gammas, %d tags", hours, totalGammas, totalTags);
      return totalGammas;
   }

   private InputStream getInputStream(String value, String uri) throws Exception {
      if (Strings.isValid(uri)) {
         try {
            java.net.URI parsedUri = new java.net.URI(uri);
            if (parsedUri.toASCIIString().startsWith(ResourceConstants.ATTRIBUTE_RESOURCE_PROTOCOL)) {
               org.eclipse.osee.framework.jdk.core.type.PropertyStore options =
                  new org.eclipse.osee.framework.jdk.core.type.PropertyStore();
               options.put(StandardOptions.DecompressOnAquire.name(), true);
               IResourceLocator locator = resourceManager.getResourceLocator(uri);
               IResource resource = resourceManager.acquire(locator, options);
               if (resource != null) {
                  return resource.getContent();
               }
            }
         } catch (Exception ex) {
            // fall through to value-based input
         }
      }
      if (Strings.isValid(value)) {
         return new java.io.ByteArrayInputStream(value.getBytes("UTF-8"));
      }
      return null;
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
