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
package org.eclipse.osee.framework.database.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseJoinAccessor implements IJoinAccessor {

   private static final String SELECT_QUERY_IDS = "select DISTINCT query_id from %s";

   private static final String INSERT_INTO_JOIN_ARTIFACT =
      "INSERT INTO osee_join_artifact (query_id, insert_time, art_id, branch_id) VALUES (?, ?, ?, ?)";

   private static final String INSERT_INTO_JOIN_TRANSACTION =
      "INSERT INTO osee_join_transaction (query_id, insert_time, gamma_id, transaction_id) VALUES (?, ?, ?, ?)";

   private static final String INSERT_INTO_JOIN_SEARCH_TAGS =
      "INSERT INTO osee_join_search_tags (query_id, insert_time, coded_tag_id) VALUES (?, ?, ?)";

   private static final String INSERT_INTO_TAG_GAMMA_QUEUE =
      "INSERT INTO osee_tag_gamma_queue (query_id, insert_time, gamma_id) VALUES (?, ?, ?)";

   private static final String INSERT_INTO_JOIN_EXPORT_IMPORT =
      "INSERT INTO osee_join_export_import (query_id, insert_time, id1, id2) VALUES (?, ?, ?, ?)";

   private static final String INSERT_INTO_JOIN_ID =
      "INSERT INTO osee_join_id (query_id, insert_time, id) VALUES (?, ?, ?)";

   private static final String INSERT_INTO_JOIN_CLEANUP =
      "INSERT INTO osee_join_cleanup (query_id, table_name, session_id) VALUES (?, ?, ?)";

   private static final String INSERT_INTO_JOIN_CHAR_ID = "INSERT INTO osee_join_char_id (query_id, id) VALUES (?, ?)";

   private static final String DELETE_FROM_JOIN_ID = "DELETE FROM osee_join_id WHERE query_id = ?";
   private static final String DELETE_FROM_JOIN_TRANSACTION = "DELETE FROM osee_join_transaction WHERE query_id = ?";
   private static final String DELETE_FROM_JOIN_ARTIFACT = "DELETE FROM osee_join_artifact WHERE query_id = ?";
   private static final String DELETE_FROM_JOIN_SEARCH_TAGS = "DELETE FROM osee_join_search_tags WHERE query_id = ?";
   private static final String DELETE_FROM_TAG_GAMMA_QUEUE = "DELETE FROM osee_tag_gamma_queue WHERE query_id = ?";
   private static final String DELETE_FROM_JOIN_EXPORT_IMPORT = "DELETE FROM osee_join_export_import WHERE query_id =?";
   private static final String DELETE_FROM_JOIN = "DELETE FROM osee_join_cleanup WHERE query_id =?";
   private static final String DELETE_FROM_JOIN_CHAR_ID = "DELETE FROM osee_join_char_id WHERE query_id =?";

   public enum JoinItem {
      TRANSACTION("osee_join_transaction", INSERT_INTO_JOIN_TRANSACTION, DELETE_FROM_JOIN_TRANSACTION),
      ARTIFACT("osee_join_artifact", INSERT_INTO_JOIN_ARTIFACT, DELETE_FROM_JOIN_ARTIFACT),
      SEARCH_TAGS("osee_join_search_tags", INSERT_INTO_JOIN_SEARCH_TAGS, DELETE_FROM_JOIN_SEARCH_TAGS),
      TAG_GAMMA_QUEUE("osee_tag_gamma_queue", INSERT_INTO_TAG_GAMMA_QUEUE, DELETE_FROM_TAG_GAMMA_QUEUE),
      EXPORT_IMPORT("osee_join_export_import", INSERT_INTO_JOIN_EXPORT_IMPORT, DELETE_FROM_JOIN_EXPORT_IMPORT),
      ID("osee_join_id", INSERT_INTO_JOIN_ID, DELETE_FROM_JOIN_ID),
      JOIN("osee_join_cleanup", INSERT_INTO_JOIN_CLEANUP, DELETE_FROM_JOIN),
      CHAR_ID("osee_join_char_id", INSERT_INTO_JOIN_CHAR_ID, DELETE_FROM_JOIN_CHAR_ID);

      private final String tableName;
      private final String deleteSql;
      private final String insertSql;

      JoinItem(String tableName, String insertSql, String deleteSql) {
         this.tableName = tableName;
         this.deleteSql = deleteSql;
         this.insertSql = insertSql;
      }

      public String getDeleteSql() {
         return deleteSql;
      }

      public String getInsertSql() {
         return insertSql;
      }

      public String getJoinTableName() {
         return tableName;
      }
   }

   private final IOseeDatabaseService databaseService;
   private final String sessionId;

   public DatabaseJoinAccessor(IOseeDatabaseService databaseService, String sessionId) {
      super();
      this.databaseService = databaseService;
      this.sessionId = sessionId;
   }

   public DatabaseJoinAccessor(IOseeDatabaseService databaseService) {
      this(databaseService, null);
   }

   @Override
   public int delete(OseeConnection connection, JoinItem joinItem, int queryId) throws OseeCoreException {
      int updated = 0;
      if (queryId != -1) {
         updated = databaseService.runPreparedUpdate(connection, joinItem.getDeleteSql(), queryId);
         if (sessionId != null) {
            databaseService.runPreparedUpdate(connection, DELETE_FROM_JOIN, queryId);
         }
      }
      return updated;
   }

   @SuppressWarnings("unchecked")
   @Override
   public void store(OseeConnection connection, JoinItem joinItem, int queryId, List<Object[]> dataList) throws OseeCoreException {
      databaseService.runBatchUpdate(connection, joinItem.getInsertSql(), dataList);
      if (sessionId != null) {
         databaseService.runPreparedUpdate(connection, INSERT_INTO_JOIN_CLEANUP, queryId, joinItem.getJoinTableName(),
            sessionId);
      }
   }

   @Override
   public Collection<Integer> getAllQueryIds(OseeConnection connection, JoinItem joinItem) throws OseeCoreException {
      Collection<Integer> queryIds = new ArrayList<Integer>();
      IOseeStatement chStmt = databaseService.getStatement();
      try {
         String query = String.format(SELECT_QUERY_IDS, joinItem.getJoinTableName());
         chStmt.runPreparedQuery(query);
         while (chStmt.next()) {
            queryIds.add(chStmt.getInt("query_id"));
         }
      } finally {
         chStmt.close();
      }
      return queryIds;
   }

}
