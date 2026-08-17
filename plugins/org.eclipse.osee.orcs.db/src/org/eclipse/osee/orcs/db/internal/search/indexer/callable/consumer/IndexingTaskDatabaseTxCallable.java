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

package org.eclipse.osee.orcs.db.internal.search.indexer.callable.consumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.JoinItem;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.IndexedResource;
import org.eclipse.osee.orcs.core.ds.OrcsDataHandler;
import org.eclipse.osee.orcs.db.internal.callable.AbstractDatastoreTxCallable;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexedResourceLoader;
import org.eclipse.osee.orcs.db.internal.search.tagger.LegacyTagEncoder;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagCollector;
import org.eclipse.osee.orcs.db.internal.search.tagger.Tagger;
import org.eclipse.osee.orcs.db.internal.search.tagger.TaggingEngine;
import org.eclipse.osee.orcs.search.IndexerCollector;

/**
 * @author Roberto E. Escobar
 */
public final class IndexingTaskDatabaseTxCallable extends AbstractDatastoreTxCallable<Long> {

   private static final String DELETE_SEARCH_TAGS = "delete from osee_search_tags where gamma_id = ?";
   private static final String DELETE_SEARCH_TAGS_HASH = "delete from osee_search_tags_hash where gamma_id = ?";

   private final IndexedResourceLoader loader;
   private final TaggingEngine taggingEngine;
   private final IndexerCollector collector;
   private final Long tagQueueQueryId;
   private final boolean isCacheAll;
   private final int cacheLimit;
   private final OrcsTokenService tokenService;

   private final long waitStartTime;
   private long startTime;
   private long waitTime;

   public IndexingTaskDatabaseTxCallable(Log logger, OrcsSession session, JdbcClient jdbcClient, IndexedResourceLoader loader, TaggingEngine taggingEngine, IndexerCollector collector, Long tagQueueQueryId, boolean isCacheAll, int cacheLimit, OrcsTokenService tokenService) {
      super(logger, session, jdbcClient);
      waitStartTime = System.currentTimeMillis();

      this.loader = loader;
      this.taggingEngine = taggingEngine;
      this.collector = collector;
      this.tagQueueQueryId = tagQueueQueryId;
      this.cacheLimit = cacheLimit;
      this.isCacheAll = isCacheAll;
      this.tokenService = tokenService;
   }

   public Long getTagQueueQueryId() {
      return tagQueueQueryId;
   }

   private OrcsDataHandler<IndexedResource> createCollector(final Collection<IndexedResource> sources) {
      return new OrcsDataHandler<IndexedResource>() {

         @Override
         public void onData(IndexedResource data) {
            sources.add(data);
         }
      };
   }

   @Override
   protected Long handleTxWork(JdbcConnection connection) {
      getLogger().debug("Tagging: [%s]", getTagQueueQueryId());
      long totalTags = -1;
      try {
         Collection<IndexedResource> sources = new LinkedHashSet<>();
         OrcsDataHandler<IndexedResource> handler = createCollector(sources);
         loader.loadSource(handler, getTagQueueQueryId(), tokenService);

         if (!sources.isEmpty()) {
            try {
               deleteTags(connection, sources);
               totalTags = createTags(connection, sources);
               removeIndexingTaskFromQueue(connection);
            } catch (Exception ex) {
               throw new OseeCoreException(ex, "Unable to store tags - tagQueueQueryId [%d]", getTagQueueQueryId());
            }
         } else {
            getLogger().warn("Empty gamma query id: %s", getTagQueueQueryId());
         }
      } finally {
         getLogger().debug("End Tagging: [%s] totalTags[%s]", getTagQueueQueryId(), totalTags);
      }
      return totalTags;
   }

   private long createTags(JdbcConnection connection, Collection<IndexedResource> sources) {
      SearchTagCollector tagCollector = new SearchTagCollector();
      LegacyTagEncoder legacyEncoder = new LegacyTagEncoder();

      Set<Long> processed = new HashSet<>();

      Map<Long, Collection<Long>> hashTagsToStore = new HashMap<>();
      Map<Long, Collection<Long>> legacyTagsToStore = new HashMap<>();
      for (IndexedResource source : sources) {
         long startItemTime = System.currentTimeMillis();
         GammaId gamma = source.getGammaId();
         if (processed.add(gamma.getId())) {
            Set<Long> hashTags = new HashSet<>();
            Set<Long> legacyTags = new HashSet<>();
            hashTagsToStore.put(gamma.getId(), hashTags);
            legacyTagsToStore.put(gamma.getId(), legacyTags);
            tagCollector.setCurrentTag(gamma.getId(), hashTags);
            try {
               TaggerTypeToken taggerType =
                  tokenService.getAttributeTypeOrSentinel(source.getAttributeType().getId()).getTaggerType();
               if (taggerType.isValid()) {
                  Tagger tagger = taggingEngine.getTagger(taggerType);
                  // Wrap the collector to also produce legacy bit-packed tags from each word
                  TagCollector dualCollector = (word, codedTag) -> {
                     tagCollector.addTag(word, codedTag);
                     legacyEncoder.encode(word, (w, legacyTag) -> legacyTags.add(legacyTag));
                  };
                  tagger.tagIt(source.getResourceInput(), dualCollector);
                  if (isStorageAllowed(hashTagsToStore)) {
                     getLogger().debug("Stored a - [%s] - connectionId[%s]", getTagQueueQueryId(), connection);
                     storeTags(connection, legacyTagsToStore, hashTagsToStore);
                  }
               } else {
                  getLogger().error("Field has invalid tagger[%s] provider and cannot be tagged - [Gamma: %s]",
                     taggerType, gamma);
               }
            } catch (Exception ex) {
               getLogger().error(ex, "Unable to tag - [%s]", gamma);
            } finally {
               long endItemTime = System.currentTimeMillis() - startItemTime;
               notifyOnIndexItemComplete(gamma, hashTags.size(), endItemTime);
            }
         }
      }

      if (!hashTagsToStore.isEmpty()) {
         getLogger().debug("Stored b - [%s] - connectionId[%s]", getTagQueueQueryId(), connection);
         storeTags(connection, legacyTagsToStore, hashTagsToStore);
      }
      return tagCollector.getTotalTags();
   }

   @Override
   protected void handleTxException(Exception ex) {
      super.handleTxException(ex);
      if (collector != null) {
         collector.onIndexTaskError(getTagQueueQueryId(), ex);
      }
   }

   @Override
   protected void onExecutionStart() {
      super.onExecutionStart();
      startTime = System.currentTimeMillis();
      waitTime = startTime - waitStartTime;
   }

   @Override
   protected void onExecutionComplete() {
      super.onExecutionComplete();
      if (collector != null) {
         collector.onIndexTaskComplete(getTagQueueQueryId(), waitTime, System.currentTimeMillis() - startTime);
      }
   }

   private void removeIndexingTaskFromQueue(JdbcConnection connection) {
      getJdbcClient().runPreparedUpdate(connection, JoinItem.TAG_GAMMA_QUEUE.getDeleteSql(), getTagQueueQueryId());
   }

   private boolean isStorageAllowed(Map<Long, Collection<Long>> searchTags) {
      int cummulative = 0;
      boolean needsStorage = false;
      for (Collection<Long> tags : searchTags.values()) {
         cummulative += tags.size();
         if (!isCacheAll && cummulative >= cacheLimit) {
            needsStorage = true;
            break;
         }
      }
      return needsStorage;
   }

   public int deleteTags(JdbcConnection connection, Collection<IndexedResource> sources) {
      int numberDeleted = 0;
      if (!sources.isEmpty()) {
         List<Object[]> datas = new ArrayList<>();
         for (IndexedResource source : sources) {
            datas.add(new Object[] {source.getGammaId()});
         }
         numberDeleted = getJdbcClient().runBatchUpdate(connection, DELETE_SEARCH_TAGS, datas);
         getJdbcClient().runBatchUpdate(connection, DELETE_SEARCH_TAGS_HASH, datas);
      }
      return numberDeleted;
   }

   private int storeTags(JdbcConnection connection, Map<Long, Collection<Long>> legacyTags,
      Map<Long, Collection<Long>> hashTags) {
      int updated = 0;
      if (!legacyTags.isEmpty() || !hashTags.isEmpty()) {
         // Write legacy bit-packed tags to osee_search_tags
         if (!legacyTags.isEmpty()) {
            List<Object[]> legacyData = new ArrayList<>();
            for (Entry<Long, Collection<Long>> entry : legacyTags.entrySet()) {
               Long gammaId = entry.getKey();
               for (Long codedTag : entry.getValue()) {
                  legacyData.add(new Object[] {codedTag, gammaId});
               }
            }
            legacyTags.clear();
            if (!legacyData.isEmpty()) {
               updated +=
                  getJdbcClient().runBatchUpdate(connection, OseeDb.OSEE_SEARCH_TAGS_TABLE.getInsertSql(), legacyData);
            }
         }
         // Write hash tags to osee_search_tags_hash
         if (!hashTags.isEmpty()) {
            List<Object[]> hashData = new ArrayList<>();
            for (Entry<Long, Collection<Long>> entry : hashTags.entrySet()) {
               Long gammaId = entry.getKey();
               for (Long codedTag : entry.getValue()) {
                  hashData.add(new Object[] {codedTag, gammaId});
               }
            }
            hashTags.clear();
            if (!hashData.isEmpty()) {
               getJdbcClient().runBatchUpdate(connection, OseeDb.OSEE_SEARCH_TAGS_HASH_TABLE.getInsertSql(), hashData);
            }
         }
      }
      return updated;
   }

   private void notifyOnIndexItemComplete(GammaId gammaId, int totalTags, long processingTime) {
      if (collector != null) {
         collector.onIndexItemComplete(getTagQueueQueryId(), gammaId.getId(), totalTags, processingTime);
      }
   }

   private void notifyOnIndexItemAdded(long gammaId, String word, long codedTag) {
      if (collector != null) {
         collector.onIndexItemAdded(getTagQueueQueryId(), gammaId, word, codedTag);
      }
   }

   private final class SearchTagCollector implements TagCollector {

      private Long gammaId;
      private Set<Long> currentTag;
      private long totalTags;

      public SearchTagCollector() {
         this.totalTags = 0;
      }

      public void setCurrentTag(Long gammaId, Set<Long> currentTag) {
         this.gammaId = gammaId;
         this.currentTag = currentTag;
      }

      public long getTotalTags() {
         return totalTags;
      }

      @Override
      public void addTag(String word, Long codedTag) {
         if (currentTag != null && gammaId != null) {
            if (currentTag.add(codedTag)) {
               totalTags++;
               notifyOnIndexItemAdded(gammaId, word, codedTag);
            }
         }
      }
   }
}