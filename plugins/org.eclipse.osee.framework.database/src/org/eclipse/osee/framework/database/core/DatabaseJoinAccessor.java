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
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseJoinAccessor implements IJoinAccessor {

   private static final String SELECT_QUERY_IDS = "select DISTINCT query_id from %s";

   private static final String INSERT_INTO_JOIN_ARTIFACT =
      "INSERT INTO osee_join_artifact (query_id, art_id, branch_id, transaction_id) VALUES (?, ?, ?, ?)";
   private static final String INSERT_INTO_JOIN_TRANSACTION =
      "INSERT INTO osee_join_transaction (query_id, gamma_id, transaction_id, branch_id) VALUES (?, ?, ?, ?)";
      
   private static final String INSERT_INTO_JOIN_ID = "INSERT INTO osee_join_id (query_id, id) VALUES (?, ?)";
   private static final String INSERT_INTO_JOIN_CHAR_ID = "INSERT INTO osee_join_char_id (query_id, id) VALUES (?, ?)";

   private static final String DELETE_FROM_JOIN_ID = "DELETE FROM osee_join_id WHERE query_id = ?";
   private static final String DELETE_FROM_JOIN_TRANSACTION = "DELETE FROM osee_join_transaction WHERE query_id = ?";
   private static final String DELETE_FROM_JOIN_ARTIFACT = "DELETE FROM osee_join_artifact WHERE query_id = ?";
   private static final String DELETE_FROM_JOIN_CHAR_ID = "DELETE FROM osee_join_char_id WHERE query_id =?";

   private static final String INSERT_INTO_JOIN_CLEANUP =
      "INSERT INTO osee_join_cleanup (query_id, table_name, issued_at, expires_in) VALUES (?,?,?,?)";

   private static final String DELETE_FROM_JOIN_CLEANUP = "DELETE FROM osee_join_cleanup WHERE query_id =?";

   public enum JoinItem {
      TRANSACTION("osee_join_transaction", INSERT_INTO_JOIN_TRANSACTION, DELETE_FROM_JOIN_TRANSACTION),
      ARTIFACT("osee_join_artifact", INSERT_INTO_JOIN_ARTIFACT, DELETE_FROM_JOIN_ARTIFACT),
      ID("osee_join_id", INSERT_INTO_JOIN_ID, DELETE_FROM_JOIN_ID),
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

   public DatabaseJoinAccessor(IOseeDatabaseService databaseService) {
      super();
      this.databaseService = databaseService;
   }

   @Override
   public int delete(OseeConnection connection, JoinItem joinItem, int queryId) throws OseeCoreException {
      int updated = 0;
      if (queryId != -1) {
         updated = databaseService.runPreparedUpdate(connection, joinItem.getDeleteSql(), queryId);
         databaseService.runPreparedUpdate(connection, DELETE_FROM_JOIN_CLEANUP, queryId);
      }
      return updated;
   }

   @SuppressWarnings("unchecked")
   @Override
   public void store(OseeConnection connection, JoinItem joinItem, int queryId, List<Object[]> dataList, Long issuedAt, Long expiresIn) throws OseeCoreException {
      databaseService.runPreparedUpdate(connection, INSERT_INTO_JOIN_CLEANUP, queryId, joinItem.getJoinTableName(),
         issuedAt, expiresIn);
      databaseService.runBatchUpdate(connection, joinItem.getInsertSql(), dataList);
   }

   @Override
   public Collection<Integer> getAllQueryIds(OseeConnection connection, JoinItem joinItem) throws OseeCoreException {
      Collection<Integer> queryIds = new ArrayList<Integer>();
      IOseeStatement chStmt = databaseService.getStatement(connection);
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
