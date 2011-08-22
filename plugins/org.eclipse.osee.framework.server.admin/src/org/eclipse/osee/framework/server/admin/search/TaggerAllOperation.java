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
package org.eclipse.osee.framework.server.admin.search;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.TagQueueJoinQuery;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.search.engine.TagListenerAdapter;
import org.eclipse.osee.framework.server.admin.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public final class TaggerAllOperation extends AbstractOperation {
   private static final int BATCH_SIZE = 1000;

   private static final String FIND_ALL_TAGGABLE_ATTRIBUTES =
      "SELECT att.gamma_id FROM osee_attribute_type aty, osee_attribute att WHERE aty.tagger_id IS NOT NULL AND aty.attr_type_id = att.attr_type_id";

   private static final String COUNT_ALL_TAGGABLE_ATTRIBUTES = FIND_ALL_TAGGABLE_ATTRIBUTES.replace("att.gamma_id",
      "count(1)");

   private static final String FIND_MISSING =
      FIND_ALL_TAGGABLE_ATTRIBUTES + " AND att.gamma_id NOT IN (SELECT gamma_id FROM osee_search_tags)";

   private static final String COUNT_MISSING = FIND_MISSING.replaceFirst("att.gamma_id", "count(1)");

   private static final String FIND_TAGGABLE_ATTRIBUTES_BY_BRANCH =
      "SELECT DISTINCT att.gamma_id FROM osee_join_id jid, osee_txs txs, osee_attribute_type type, osee_attribute att WHERE jid.query_id = ? AND jid.id = txs.branch_id AND txs.gamma_id = att.gamma_id AND att.attr_type_id = type.attr_type_id AND type.tagger_id IS NOT NULL";

   private static final String COUNT_TAGGABLE_ATTRIBUTES_BY_BRANCH = FIND_TAGGABLE_ATTRIBUTES_BY_BRANCH.replace(
      "DISTINCT att.gamma_id", "count(DISTINCT att.gamma_id)");

   private static final String FIND_MISSING_BY_BRANCH =
      FIND_TAGGABLE_ATTRIBUTES_BY_BRANCH + " AND att.gamma_id NOT IN (SELECT gamma_id FROM osee_search_tags)";

   private static final String COUNT_MISSING_BY_BRANCH =
      COUNT_TAGGABLE_ATTRIBUTES_BY_BRANCH + " AND att.gamma_id NOT IN (SELECT gamma_id FROM osee_search_tags)";

   private final Set<Integer> branchIds;
   private final boolean tagOnlyMissingGammas;
   private TagProcessListener processor;

   public TaggerAllOperation(OperationLogger logger, Set<Integer> branchIds, boolean tagOnlyMissingGammas) {
      super("Tag Attributes", Activator.PLUGIN_ID, logger);
      this.branchIds = branchIds;
      this.tagOnlyMissingGammas = tagOnlyMissingGammas;
   }

   private TagProcessListener process(long startTime, Collection<Integer> branchIds, boolean tagOnlyMissingGammas) throws OseeCoreException {
      logTaggingStart();

      IdJoinQuery idJoin = JoinUtility.createIdJoinQuery();
      Object[] params;
      String countQuery;
      String searchQuery;

      if (branchIds.isEmpty()) {
         params = new Object[0];
         if (tagOnlyMissingGammas) {
            countQuery = COUNT_MISSING;
            searchQuery = FIND_MISSING;
         } else {
            countQuery = COUNT_ALL_TAGGABLE_ATTRIBUTES;
            searchQuery = FIND_ALL_TAGGABLE_ATTRIBUTES;
         }
      } else {
         for (Integer id : branchIds) {
            idJoin.add(id);
         }
         idJoin.store();
         params = new Object[] {idJoin.getQueryId()};
         if (tagOnlyMissingGammas) {
            countQuery = COUNT_MISSING_BY_BRANCH;
            searchQuery = FIND_MISSING_BY_BRANCH;
         } else {
            countQuery = COUNT_TAGGABLE_ATTRIBUTES_BY_BRANCH;
            searchQuery = FIND_TAGGABLE_ATTRIBUTES_BY_BRANCH;
         }
      }

      int totalAttributes = ConnectionHandler.runPreparedQueryFetchInt(-1, countQuery, params);
      TagProcessListener processor = new TagProcessListener(startTime, totalAttributes);
      try {
         fetchAndProcessGammas(processor, searchQuery, params);
      } finally {
         idJoin.delete();
      }
      return processor;
   }

   private void logTaggingStart() {
      StringBuilder builder = new StringBuilder();
      builder.append("Tagging");
      if (tagOnlyMissingGammas) {
         builder.append(" (Only Missing)");
      }
      builder.append(" Attributes for ");
      if (branchIds.isEmpty()) {
         builder.append("All Branches");
      } else {
         builder.append("Branch(es) ").append(branchIds);
      }
      log(builder.toString());
   }

   private void fetchAndProcessGammas(TagProcessListener processor, String query, Object... params) throws OseeCoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(query, params);
         TagQueueJoinQuery joinQuery = JoinUtility.createTagQueueJoinQuery();
         while (chStmt.next()) {
            long gammaId = chStmt.getLong("gamma_id");
            joinQuery.add(gammaId);
            if (joinQuery.size() >= BATCH_SIZE) {
               processor.storeAndAddQueryId(joinQuery);
               joinQuery = JoinUtility.createTagQueueJoinQuery();
            }
         }
         processor.storeAndAddQueryId(joinQuery);
      } finally {
         chStmt.close();
      }
   }

   private final class TagProcessListener extends TagListenerAdapter {

      private final Map<Integer, TagQueueJoinQuery> queryIdMap;
      private int attributesProcessed;
      private int queriesProcessed;
      private final long startTime;
      private final int totalAttributes;

      public TagProcessListener(long startTime, int totalAttributes) {
         this.queryIdMap = new ConcurrentHashMap<Integer, TagQueueJoinQuery>();
         this.startTime = startTime;
         this.totalAttributes = totalAttributes;
         this.attributesProcessed = 0;
         this.queriesProcessed = 0;
      }

      public void storeAndAddQueryId(TagQueueJoinQuery joinQuery) throws OseeCoreException {
         if (joinQuery.size() > 0) {
            joinQuery.store();
            this.queryIdMap.put(joinQuery.getQueryId(), joinQuery);
            Activator.getSearchTagger().tagByQueueQueryId(this, joinQuery.getQueryId());
         }
      }

      public boolean isProcessingDone() {
         return queriesProcessed == totalQueries();
      }

      public int totalQueries() {
         int remainder = totalAttributes % 1000;
         return totalAttributes / 1000 + (remainder > 0 ? 1 : 0);
      }

      public void printStats() {
         logf("QueryIds: [ %d of %d] Attributes: [%d of %d] - Elapsed Time = %s.", queriesProcessed, totalQueries(),
            attributesProcessed, totalAttributes, Lib.getElapseString(startTime));
      }

      @Override
      public void onAttributeTagComplete(int queryId, long gammaId, int totalTags, long processingTime) {
         if (queryIdMap.containsKey(queryId)) {
            attributesProcessed++;
            if (attributesProcessed % 1000 == 0) {
               printStats();
            }
         }
      }

      @Override
      synchronized public void onTagQueryIdTagComplete(int queryId, long waitTime, long processingTime) {
         TagQueueJoinQuery joinQuery = this.queryIdMap.get(queryId);
         if (joinQuery != null) {
            this.queryIdMap.remove(joinQuery);
            queriesProcessed++;
            if (isProcessingDone()) {
               this.notify();
            }
         }
      }
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      long startTime = System.currentTimeMillis();
      processor = process(startTime, branchIds, tagOnlyMissingGammas);
      processor.printStats();
      log("Tagging Complete");
   }
}