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
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.JoinItem;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.IndexedResource;
import org.eclipse.osee.orcs.core.ds.OrcsDataHandler;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.db.internal.callable.AbstractDatastoreTxCallable;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexedResourceLoader;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagCollector;
import org.eclipse.osee.orcs.db.internal.search.tagger.Tagger;
import org.eclipse.osee.orcs.db.internal.search.tagger.TaggingEngine;
import org.eclipse.osee.orcs.search.IndexerCollector;

/**
 * @author Roberto E. Escobar
 */
public final class IndexingTaskDatabaseTxCallable extends AbstractDatastoreTxCallable<Long> {

   private static final String INSERT_SEARCH_TAG =
      "insert into osee_search_tags (gamma_id, coded_tag_id) values (?, ?)";

   private static final String DELETE_SEARCH_TAGS = "delete from osee_search_tags where gamma_id = ?";

   private final IndexedResourceLoader loader;
   private final TaggingEngine taggingEngine;
   private final IndexerCollector collector;
   private final int tagQueueQueryId;
   private final boolean isCacheAll;
   private final int cacheLimit;
   private final AttributeTypes attributeTypes;

   private final long waitStartTime;
   private long startTime;
   private long waitTime;

   public IndexingTaskDatabaseTxCallable(Log logger, OrcsSession session, JdbcClient jdbcClient, IndexedResourceLoader loader, TaggingEngine taggingEngine, IndexerCollector collector, int tagQueueQueryId, boolean isCacheAll, int cacheLimit, AttributeTypes attributeTypes) {
      super(logger, session, jdbcClient);
      waitStartTime = System.currentTimeMillis();

      this.loader = loader;
      this.taggingEngine = taggingEngine;
      this.collector = collector;
      this.tagQueueQueryId = tagQueueQueryId;
      this.cacheLimit = cacheLimit;
      this.isCacheAll = isCacheAll;
      this.attributeTypes = attributeTypes;
   }

   public int getTagQueueQueryId() {
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
         loader.loadSource(handler, getTagQueueQueryId(), attributeTypes);

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

      Set<Long> processed = new HashSet<>();

      Map<Long, Collection<Long>> toStore = new HashMap<>();
      for (IndexedResource source : sources) {
         long startItemTime = System.currentTimeMillis();
         GammaId gamma = source.getGammaId();
         if (processed.add(gamma.getId())) {
            Set<Long> tags = new HashSet<>();
            toStore.put(gamma.getId(), tags);
            tagCollector.setCurrentTag(gamma.getId(), tags);
            try {
               String taggerId = attributeTypes.getTaggerId(source.getAttributeType());
               if (taggingEngine.hasTagger(taggerId)) {
                  Tagger tagger = taggingEngine.getTagger(taggerId);
                  tagger.tagIt(source, tagCollector);
                  if (isStorageAllowed(toStore)) {
                     getLogger().debug("Stored a - [%s] - connectionId[%s] - [%s]", getTagQueueQueryId(), connection,
                        toStore);
                     storeTags(connection, toStore);
                  }
               } else {
                  getLogger().error("Field has invalid tagger[%s] provider and cannot be tagged - [Gamma: %s]",
                     taggerId, gamma);
               }
            } catch (Exception ex) {
               getLogger().error(ex, "Unable to tag - [%s]", gamma);
            } finally {
               long endItemTime = System.currentTimeMillis() - startItemTime;
               notifyOnIndexItemComplete(gamma, tags.size(), endItemTime);
            }
         }
      }

      if (!toStore.isEmpty()) {
         getLogger().debug("Stored b - [%s] - connectionId[%s] - [%s]", getTagQueueQueryId(), connection, toStore);
         storeTags(connection, toStore);
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
      }
      return numberDeleted;
   }

   private int storeTags(JdbcConnection connection, Map<Long, Collection<Long>> toStore) {
      int updated = 0;
      if (!toStore.isEmpty()) {
         List<Object[]> data = new ArrayList<>();
         for (Entry<Long, Collection<Long>> entry : toStore.entrySet()) {
            Long gammaId = entry.getKey();
            for (Long codedTag : entry.getValue()) {
               data.add(new Object[] {gammaId, codedTag});
               getLogger().debug("Storing: gamma:[%s] tag:[%s]", gammaId, codedTag);
            }
         }
         toStore.clear();
         if (!data.isEmpty()) {
            updated += getJdbcClient().runBatchUpdate(connection, INSERT_SEARCH_TAG, data);
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