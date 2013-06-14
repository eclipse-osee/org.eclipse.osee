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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.database.schema.DatabaseTxCallable;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.DatabaseJoinAccessor.JoinItem;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.db.internal.search.indexer.QueueToAttributeLoader;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagCollector;
import org.eclipse.osee.orcs.db.internal.search.tagger.Tagger;
import org.eclipse.osee.orcs.db.internal.search.tagger.TaggingEngine;
import org.eclipse.osee.orcs.search.IndexerCollector;

/**
 * @author Roberto E. Escobar
 */
public final class IndexingTaskDatabaseTxCallable extends DatabaseTxCallable<Long> {

   private static final String INSERT_SEARCH_TAG =
      "insert into osee_search_tags (gamma_id, coded_tag_id) values (?, ?)";

   private static final String DELETE_SEARCH_TAGS = "delete from osee_search_tags where gamma_id = ?";

   private final QueueToAttributeLoader loader;
   private final TaggingEngine taggingEngine;
   private final IndexerCollector collector;
   private final int tagQueueQueryId;
   private final boolean isCacheAll;
   private final int cacheLimit;
   private final AttributeTypes attributeTypes;

   private final long waitStartTime;
   private long startTime;
   private long waitTime;

   public IndexingTaskDatabaseTxCallable(Log logger, IOseeDatabaseService dbService, QueueToAttributeLoader loader, TaggingEngine taggingEngine, IndexerCollector collector, int tagQueueQueryId, boolean isCacheAll, int cacheLimit, AttributeTypes attributeTypes) {
      super(logger, dbService, "Attribute to Tag Database Transaction");
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

   @Override
   protected Long handleTxWork(OseeConnection connection) throws OseeCoreException {
      getLogger().debug("Tagging: [%s]", getTagQueueQueryId());
      long totalTags = -1;
      try {
         Collection<AttributeReadable<?>> attributes = new HashSet<AttributeReadable<?>>();
         loader.loadAttributes(connection, getTagQueueQueryId(), attributes);

         if (!attributes.isEmpty()) {
            try {
               deleteTags(connection, attributes);
               totalTags = createTags(connection, attributes);
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

   private long createTags(OseeConnection connection, Collection<AttributeReadable<?>> attributes) throws OseeCoreException {
      SearchTagCollector tagCollector = new SearchTagCollector();

      Set<Long> processed = new HashSet<Long>();

      Map<Long, Collection<Long>> toStore = new HashMap<Long, Collection<Long>>();
      for (AttributeReadable<?> attributeData : attributes) {
         long startItemTime = System.currentTimeMillis();
         Long gamma = attributeData.getGammaId();
         if (processed.add(gamma)) {
            Set<Long> tags = new HashSet<Long>();
            toStore.put(gamma, tags);
            tagCollector.setCurrentTag(gamma, tags);
            try {
               String taggerId = attributeTypes.getTaggerId(attributeData.getAttributeType());
               Tagger tagger = taggingEngine.getTagger(taggerId);
               tagger.tagIt(attributeData, tagCollector);
               if (isStorageAllowed(toStore)) {
                  if (getLogger().isDebugEnabled()) {
                     getLogger().debug("Stored a - [%s] - connectionId[%s] - [%s]", getTagQueueQueryId(), connection,
                        toStore);
                  }
                  storeTags(connection, toStore);
               }
            } catch (Throwable ex) {
               getLogger().error(ex, "Unable to tag - [%s]", gamma);
            } finally {
               long endItemTime = System.currentTimeMillis() - startItemTime;
               notifyOnIndexItemComplete(gamma, tags.size(), endItemTime);
            }
         }
      }

      if (!toStore.isEmpty()) {
         if (getLogger().isDebugEnabled()) {
            getLogger().debug("Stored b - [%s] - connectionId[%s] - [%s]", getTagQueueQueryId(), connection, toStore);
         }
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

   private void removeIndexingTaskFromQueue(OseeConnection connection) throws OseeCoreException {
      getDatabaseService().runPreparedUpdate(connection, JoinItem.TAG_GAMMA_QUEUE.getDeleteSql(), getTagQueueQueryId());
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

   public int deleteTags(OseeConnection connection, Collection<AttributeReadable<?>> attributes) throws OseeCoreException {
      int numberDeleted = 0;
      if (!attributes.isEmpty()) {
         List<Object[]> datas = new ArrayList<Object[]>();
         for (AttributeReadable<?> attribute : attributes) {
            datas.add(new Object[] {attribute.getGammaId()});
         }
         numberDeleted = getDatabaseService().runBatchUpdate(connection, DELETE_SEARCH_TAGS, datas);
      }
      return numberDeleted;
   }

   private int storeTags(OseeConnection connection, Map<Long, Collection<Long>> toStore) throws OseeCoreException {
      int updated = 0;
      if (!toStore.isEmpty()) {
         List<Object[]> data = new ArrayList<Object[]>();
         for (Entry<Long, Collection<Long>> entry : toStore.entrySet()) {
            Long gammaId = entry.getKey();
            for (Long codedTag : entry.getValue()) {
               data.add(new Object[] {gammaId, codedTag});
               getLogger().debug("Storing: gamma:[%s] tag:[%s]", gammaId, codedTag);
            }
         }
         toStore.clear();
         if (!data.isEmpty()) {
            updated += getDatabaseService().runBatchUpdate(connection, INSERT_SEARCH_TAG, data);
         }
      }
      return updated;
   }

   private void notifyOnIndexItemComplete(long gammaId, int totalTags, long processingTime) {
      if (collector != null) {
         collector.onIndexItemComplete(getTagQueueQueryId(), gammaId, totalTags, processingTime);
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