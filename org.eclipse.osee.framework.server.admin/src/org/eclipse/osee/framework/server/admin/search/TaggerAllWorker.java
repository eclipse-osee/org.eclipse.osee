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
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.core.JoinUtility;
import org.eclipse.osee.framework.db.connection.core.JoinUtility.TagQueueJoinQuery;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;
import org.eclipse.osee.framework.search.engine.ITagListener;
import org.eclipse.osee.framework.server.admin.Activator;

/**
 * @author Roberto E. Escobar
 */
class TaggerAllWorker extends BaseCmdWorker implements ITagListener {
   private static final int BATCH_SIZE = 1000;
   private static final String GET_TAGGABLE_SQL_BODY =
         " FROM osee_define_attribute attr1, osee_define_attribute_type type1,  osee_define_txs txs1, osee_define_tx_details txd1, osee_define_branch br1 WHERE txs1.transaction_id = txd1.transaction_id AND txs1.gamma_id = attr1.gamma_id AND txd1.branch_id = br1.branch_id AND br1.archived <> 1 AND attr1.attr_type_id = type1.attr_type_id AND type1.tagger_id IS NOT NULL";

   private static final String FIND_ALL_TAGGABLE_ATTRIBUTES = "SELECT attr1.gamma_id" + GET_TAGGABLE_SQL_BODY;
   private static final String COUNT_TAGGABLE_ATTRIBUTES = "SELECT count(1)" + GET_TAGGABLE_SQL_BODY;

   private static final String POSTGRESQL_CHECK = " AND type1.tagger_id <> ''";
   private static final String RESTRICT_BY_BRANCH = " AND txd1.branch_id = ?";

   private ISearchEngineTagger searchTagger;
   private int total = 0;
   private int processed = 0;
   private long startTime = 0;
   private final Set<Integer> queryIds = new CopyOnWriteArraySet<Integer>();

   TaggerAllWorker() {
      super();
      this.searchTagger = Activator.getInstance().getSearchTagger();
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

   private void fetchAndProcessGammas(Connection connection, int branchId, int expectedTotal) throws SQLException {
      ConnectionHandlerStatement stmt = null;
      try {
         queryIds.clear();
         stmt =
               ConnectionHandler.runPreparedQuery(connection, getQuery(connection, branchId, false),
                     branchId > -1 ? new Object[] {SQL3DataType.INTEGER, branchId} : new Object[0]);
         TagQueueJoinQuery joinQuery = null;
         int cacheCount = 0;
         while (stmt.next()) {
            if (isExecutionAllowed() != true) {
               break;
            }
            long gammaId = stmt.getRset().getLong("gamma_id");
            if (joinQuery == null) {
               joinQuery = JoinUtility.createTagQueueJoinQuery();
            }
            joinQuery.add(gammaId);
            cacheCount++;
            if (cacheCount >= BATCH_SIZE) {
               cacheCount = 0;
               joinQuery.store(connection);
               queryIds.add(joinQuery.getQueryId());
               searchTagger.tagByQueueQueryId(this, joinQuery.getQueryId());
               joinQuery = null;
            }
         }
         if (joinQuery != null) {
            joinQuery.store(connection);
            queryIds.add(joinQuery.getQueryId());
            searchTagger.tagByQueueQueryId(this, joinQuery.getQueryId());
            joinQuery = null;
         }
      } finally {
         DbUtil.close(stmt);
      }
   }

   protected void doWork(long startTime) throws Exception {
      this.startTime = startTime;
      Connection connection = null;
      try {
         String arg = getCommandInterpreter().nextArgument();
         int branchId = -1;
         if (arg != null && arg.length() > 0) {
            branchId = Integer.parseInt(arg);
         }
         println(String.format("Tagging Attributes: [%s]", branchId > -1 ? branchId : "All Branches"));
         connection = OseeDbConnection.getConnection();
         processed = 0;
         total = getTotalItems(connection, branchId);
         fetchAndProcessGammas(connection, branchId, total);
         while (queryIds.size() > 0 && isExecutionAllowed()) {
            ;
         }
         if (isVerbose()) {
            println(String.format("[%d of %d ] - Elapsed Time = %s.", processed, total, getElapsedTime(startTime)));
         }
      } finally {
         try {
            if (connection != null) {
               connection.close();
            }
         } catch (SQLException ex) {
            printStackTrace(ex);
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ITagListener#onItemTagged(int, long, int, long)
    */
   @Override
   public void onAttributeTagComplete(int queryId, long gammaId, int totalTags, long processingTime) {
      if (queryIds.contains(queryId)) {
         processed++;
         if (processed % 1000 == 0) {
            if (isVerbose()) {
               println(String.format("[%d of %d ] - Elapsed Time = %s.", processed, total,
                     getElapsedTime(this.startTime)));
            }
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ITagListener#onTagWorkerEnd(int, long)
    */
   @Override
   public void onTagQueryIdTagComplete(int queryId, long processingTime) {
      queryIds.remove(new Integer(queryId));
   }
}
