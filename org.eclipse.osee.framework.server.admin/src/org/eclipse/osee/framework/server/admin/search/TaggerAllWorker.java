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
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;
import org.eclipse.osee.framework.search.engine.ITagListener;
import org.eclipse.osee.framework.server.admin.Activator;

/**
 * @author Roberto E. Escobar
 */
class TaggerAllWorker extends BaseCmdWorker implements ITagListener {

   private static final String GET_TAGGABLE_SQL_BODY =
         " FROM osee_define_attribute attr1, osee_define_attribute_type type1,  osee_define_txs txs1, osee_define_tx_details txd1, osee_define_branch br1 WHERE txs1.transaction_id = txd1.transaction_id AND txs1.gamma_id = attr1.gamma_id AND txd1.branch_id = br1.branch_id AND br1.archived <> 1 AND attr1.attr_type_id = type1.attr_type_id AND type1.tagger_id IS NOT NULL";

   private static final String FIND_ALL_TAGGABLE_ATTRIBUTES = "SELECT attr1.gamma_id" + GET_TAGGABLE_SQL_BODY;
   private static final String COUNT_TAGGABLE_ATTRIBUTES = "SELECT count(1)" + GET_TAGGABLE_SQL_BODY;

   private static final String POSTGRESQL_CHECK = " AND type1.tagger_id <> ''";

   private ISearchEngineTagger searchTagger;
   private boolean isTagCompleteDone;

   TaggerAllWorker() {
      super();
      this.isTagCompleteDone = false;
      this.searchTagger = Activator.getInstance().getSearchTagger();
   }

   private int getTotalItems(Connection connection) throws SQLException {
      int total = -1;
      ConnectionHandlerStatement stmt = null;
      try {
         String query = COUNT_TAGGABLE_ATTRIBUTES;
         if (connection.getMetaData().getDatabaseProductName().toLowerCase().contains("gresql")) {
            query += POSTGRESQL_CHECK;
         }
         stmt = ConnectionHandler.runPreparedQuery(connection, query);
         if (stmt.next()) {
            total = stmt.getRset().getInt(1);
         }
      } finally {
         DbUtil.close(stmt);
      }
      return total;
   }

   protected void doWork(long startTime) throws Exception {
      Connection connection = null;
      ConnectionHandlerStatement stmt = null;
      try {
         connection = ConnectionHandler.getConnection();
         int total = getTotalItems(connection);
         String query = FIND_ALL_TAGGABLE_ATTRIBUTES;
         if (connection.getMetaData().getDatabaseProductName().toLowerCase().contains("gresql")) {
            query += POSTGRESQL_CHECK;
         }
         stmt = ConnectionHandler.runPreparedQuery(connection, query);
         int count = 0;
         this.isTagCompleteDone = true;
         while (isExecutionAllowed()) {
            if (this.isTagCompleteDone) {
               if (stmt.next()) {
                  long gammaId = stmt.getRset().getLong("gamma_id");
                  this.isTagCompleteDone = false;
                  searchTagger.tagAttribute(this, gammaId);

                  count++;
                  if (count % 100 == 0) {
                     if (isVerbose()) {
                        println(String.format("[%d of %d ] - Elapsed Time = %s.", count, total,
                              getElapsedTime(startTime)));
                     }
                  }
               } else {
                  break;
               }
            }
         }
         if (isVerbose()) {
            println(String.format("[%d of %d ] - Elapsed Time = %s.", count, total, getElapsedTime(startTime)));
         }
      } finally {
         DbUtil.close(stmt);
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
    * @see org.eclipse.osee.framework.search.engine.ITagListener#onComplete(long)
    */
   @Override
   synchronized public void onComplete(long gammaId) {
      this.isTagCompleteDone = true;
   }
}
