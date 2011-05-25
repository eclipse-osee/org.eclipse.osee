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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.TagQueueJoinQuery;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.search.engine.TagListenerAdapter;
import org.eclipse.osee.framework.server.admin.BaseServerCommand;
import org.eclipse.osee.framework.server.admin.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
class TaggerAllWorker extends BaseServerCommand {
   private static final int BATCH_SIZE = 1000;

   private static final String SELECT_BRANCHES = "SELECT branch_id FROM osee_branch ORDER BY branch_id";

   private static final String FIND_ALL_TAGGABLE_ATTRIBUTES =
      "SELECT DISTINCT attr.gamma_id, type.tagger_id FROM osee_join_id jid, osee_txs txs, osee_attribute_type type, osee_attribute attr WHERE jid.query_id = ? AND jid.id = txs.branch_id AND txs.gamma_id = attr.gamma_id AND attr.attr_type_id = type.attr_type_id AND type.tagger_id IS NOT NULL";

   private static final String COUNT_TAGGABLE_ATTRIBUTES =
      "SELECT count(DISTINCT attr.gamma_id) FROM osee_join_id jid, osee_txs txs, osee_attribute_type type, osee_attribute attr WHERE jid.query_id = ? AND jid.id = txs.branch_id AND txs.gamma_id = attr.gamma_id AND attr.attr_type_id = type.attr_type_id AND type.tagger_id IS NOT NULL";

   private static final String COUNT_MISSING =
      COUNT_TAGGABLE_ATTRIBUTES + " AND attr.gamma_id NOT IN (SELECT ost.gamma_id FROM osee_search_tags ost)";

   private static final String FIND_TAGGABLE_MISSING =
      FIND_ALL_TAGGABLE_ATTRIBUTES + " AND attr.gamma_id NOT IN (SELECT ost.gamma_id FROM osee_search_tags ost)";

   private TagProcessListener processor;

   TaggerAllWorker() {
      super("Tag All Attributes");
      this.processor = null;
   }

   private TagProcessListener process(long startTime, Collection<Integer> branchIds, boolean tagOnlyMissingGammas) throws OseeCoreException {
      StringBuilder builder = new StringBuilder();
      builder.append("Tagging");
      if (tagOnlyMissingGammas) {
         builder.append(" (Only Missing)");
      }
      builder.append(" Attributes For: ");
      if (branchIds.isEmpty()) {
         builder.append("All Branches");
      } else {
         builder.append("Branch(es) ").append(branchIds);
      }
      println(builder.toString());

      if (branchIds.isEmpty()) {
         loadBranchIds(branchIds);
      }

      IdJoinQuery idJoin = JoinUtility.createIdJoinQuery();
      for (Integer id : branchIds) {
         idJoin.add(id);
      }
      idJoin.store();

      Object[] params = new Object[] {idJoin.getQueryId()};

      String countQuery;
      String searchQuery;
      if (tagOnlyMissingGammas) {
         countQuery = COUNT_MISSING;
         searchQuery = FIND_TAGGABLE_MISSING;
      } else {
         countQuery = COUNT_TAGGABLE_ATTRIBUTES;
         searchQuery = FIND_ALL_TAGGABLE_ATTRIBUTES;
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

   private void loadBranchIds(Collection<Integer> branchIds) throws OseeCoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         String query = String.format(SELECT_BRANCHES);
         chStmt.runPreparedQuery(query);
         while (chStmt.next()) {
            branchIds.add(chStmt.getInt("branch_id"));
         }
      } finally {
         chStmt.close();
      }
   }

   private void fetchAndProcessGammas(TagProcessListener processor, String query, Object... params) throws OseeCoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(query, params);
         TagQueueJoinQuery joinQuery = JoinUtility.createTagQueueJoinQuery();
         while (chStmt.next() && isExecutionAllowed()) {
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

   @Override
   protected void doCommandWork(IProgressMonitor monitor) throws Exception {
      long startTime = System.currentTimeMillis();
      try {
         Set<Integer> branchIds = new LinkedHashSet<Integer>();
         boolean tagOnlyMissingGammas = false;
         String arg = getCommandInterpreter().nextArgument();
         while (arg != null) {
            if (Strings.isValid(arg)) {
               if (arg.equals("-missing")) {
                  tagOnlyMissingGammas = true;
               } else {
                  branchIds.add(new Integer(arg));
               }
            }
            arg = getCommandInterpreter().nextArgument();
         }

         TagProcessListener processor = process(startTime, branchIds, tagOnlyMissingGammas);
         if (!processor.isProcessingDone()) {
            synchronized (processor) {
               processor.wait();
            }
         }

         if (!isExecutionAllowed() && !processor.isProcessingDone()) {
            processor.cancelProcessing();
         }
         processor.printStats();
      } finally {
         processor = null;
      }
   }

   @Override
   public void setExecutionAllowed(boolean value) {
      super.setExecutionAllowed(value);
      if (!value && processor != null) {
         synchronized (processor) {
            processor.notify();
         }
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

      public void cancelProcessing() {
         Set<Integer> list = queryIdMap.keySet();
         int[] toStop = new int[list.size()];
         int index = 0;
         for (Integer item : list) {
            toStop[index] = item;
            index++;
         }
         Activator.getSearchTagger().stopTaggingByQueueQueryId(toStop);
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
         if (isVerbose()) {
            println(String.format("QueryIds: [ %d of %d] Attributes: [%d of %d] - Elapsed Time = %s.",
               queriesProcessed, totalQueries(), attributesProcessed, totalAttributes, Lib.getElapseString(startTime)));
         }
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
}
