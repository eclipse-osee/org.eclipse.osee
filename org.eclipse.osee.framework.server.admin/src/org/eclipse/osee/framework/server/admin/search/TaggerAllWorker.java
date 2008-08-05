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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.core.JoinUtility;
import org.eclipse.osee.framework.db.connection.core.JoinUtility.TagQueueJoinQuery;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.search.engine.TagListenerAdapter;
import org.eclipse.osee.framework.server.admin.Activator;

/**
 * @author Roberto E. Escobar
 */
class TaggerAllWorker extends BaseCmdWorker {
   private static final int BATCH_SIZE = 1000;
   private static final String GET_TAGGABLE_SQL_BODY =
         " FROM osee_define_attribute attr1, osee_define_attribute_type type1,  osee_define_txs txs1, osee_define_tx_details txd1, osee_define_branch br1 WHERE txs1.transaction_id = txd1.transaction_id AND txs1.gamma_id = attr1.gamma_id AND txd1.branch_id = br1.branch_id AND br1.archived <> 1 AND attr1.attr_type_id = type1.attr_type_id AND type1.tagger_id IS NOT NULL";

   private static final String FIND_ALL_TAGGABLE_ATTRIBUTES = "SELECT DISTINCT attr1.gamma_id" + GET_TAGGABLE_SQL_BODY;
   private static final String COUNT_TAGGABLE_ATTRIBUTES =
         "SELECT count(DISTINCT attr1.gamma_id)" + GET_TAGGABLE_SQL_BODY;

   private static final String POSTGRESQL_CHECK = " AND type1.tagger_id <> ''";
   private static final String RESTRICT_BY_BRANCH = " AND txd1.branch_id = ?";

   private TagProcessListener processor;

   TaggerAllWorker() {
      super();
      this.processor = null;
   }

   private int getTotalItems(Connection connection, int branchId) throws SQLException {
      int total = -1;
      ConnectionHandlerStatement stmt = null;
      try {
         stmt =
               ConnectionHandler.runPreparedQuery(connection, getQuery(connection, branchId, true),
                     branchId > -1 ? new Object[] {SQL3DataType.INTEGER, branchId} : new Object[0]);
         if (stmt.next()) {
            total = stmt.getRset().getInt(1);
         }
      } finally {
         DbUtil.close(stmt);
      }
      return total;
   }

   private String getQuery(Connection connection, int branchId, boolean isCountQuery) throws SQLException {
      StringBuilder builder = new StringBuilder();
      builder.append(isCountQuery ? COUNT_TAGGABLE_ATTRIBUTES : FIND_ALL_TAGGABLE_ATTRIBUTES);
      if (connection.getMetaData().getDatabaseProductName().toLowerCase().contains("gresql")) {
         builder.append(POSTGRESQL_CHECK);
      }
      if (branchId > -1) {
         builder.append(RESTRICT_BY_BRANCH);
      }
      return builder.toString();
   }

   private void fetchAndProcessGammas(Connection connection, int branchId, TagProcessListener processor) throws SQLException {
      ConnectionHandlerStatement stmt = null;
      try {
         stmt =
               ConnectionHandler.runPreparedQuery(connection, getQuery(connection, branchId, false),
                     branchId > -1 ? new Object[] {SQL3DataType.INTEGER, branchId} : new Object[0]);
         TagQueueJoinQuery joinQuery = JoinUtility.createTagQueueJoinQuery();
         while (stmt.next() && isExecutionAllowed()) {
            long gammaId = stmt.getRset().getLong("gamma_id");
            joinQuery.add(gammaId);
            if (joinQuery.size() >= BATCH_SIZE) {
               processor.storeAndAddQueryId(connection, joinQuery);
               joinQuery = JoinUtility.createTagQueueJoinQuery();
            }
         }
         processor.storeAndAddQueryId(connection, joinQuery);
      } finally {
         DbUtil.close(stmt);
      }
   }

   protected void doWork(long startTime) throws Exception {
      Connection connection = null;
      try {
         String arg = getCommandInterpreter().nextArgument();
         int branchId = -1;
         if (arg != null && arg.length() > 0) {
            branchId = Integer.parseInt(arg);
         }
         println(String.format("Tagging Attributes For: [%s]", branchId > -1 ? "Branch " + branchId : "All Branches"));
         connection = OseeDbConnection.getConnection();

         int totalAttributes = getTotalItems(connection, branchId);
         processor = new TagProcessListener(startTime, totalAttributes);
         fetchAndProcessGammas(connection, branchId, processor);
         if (!processor.isProcessingDone()) {
            synchronized (processor) {
               processor.wait();
            }
         }

         if (!isExecutionAllowed() && !processor.isProcessingDone()) {
            processor.cancelProcessing(connection);
         }
         processor.printStats();
      } finally {
         processor = null;
         try {
            if (connection != null) {
               connection.close();
            }
         } catch (SQLException ex) {
            printStackTrace(ex);
         }
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.server.admin.search.BaseCmdWorker#setExecutionAllowed(boolean)
    */
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
      private long startTime;
      private int totalAttributes;

      public TagProcessListener(long startTime, int totalAttributes) {
         this.queryIdMap = Collections.synchronizedMap(new HashMap<Integer, TagQueueJoinQuery>());
         this.startTime = startTime;
         this.totalAttributes = totalAttributes;
         this.attributesProcessed = 0;
         this.queriesProcessed = 0;
      }

      /**
       * @param connection
       */
      public void cancelProcessing(Connection connection) {
         Set<Integer> list = queryIdMap.keySet();
         int[] toStop = new int[list.size()];
         int index = 0;
         for (Integer item : list) {
            toStop[index] = item;
            index++;
         }
         Activator.getInstance().getSearchTagger().stopTaggingByQueueQueryId(toStop);
      }

      public void storeAndAddQueryId(Connection connection, TagQueueJoinQuery joinQuery) throws SQLException {
         if (joinQuery.size() > 0) {
            joinQuery.store(connection);
            this.queryIdMap.put(joinQuery.getQueryId(), joinQuery);
            Activator.getInstance().getSearchTagger().tagByQueueQueryId(this, joinQuery.getQueryId());
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
                  queriesProcessed, totalQueries(), attributesProcessed, totalAttributes, getElapsedTime(startTime)));
         }
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.search.engine.ITagListener#onItemTagged(int,
       *      long, int, long)
       */
      @Override
      public void onAttributeTagComplete(int queryId, long gammaId, int totalTags, long processingTime) {
         if (queryIdMap.containsKey(queryId)) {
            attributesProcessed++;
            if (attributesProcessed % 1000 == 0) {
               printStats();
            }
         }
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.search.engine.ITagListener#onTagWorkerEnd(int,
       *      long)
       */
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
