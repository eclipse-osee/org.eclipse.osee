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
package org.eclipse.osee.framework.skynet.core.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseJoinAccessor implements IJoinAccessor {

   private static final String SELECT_QUERY_IDS = "select DISTINCT query_id from %s";

   private static final String INSERT_INTO_JOIN_TRANSACTION =
      "INSERT INTO osee_join_transaction (query_id, gamma_id, transaction_id, branch_id) VALUES (?, ?, ?, ?)";

   private static final String INSERT_INTO_JOIN_ID = "INSERT INTO osee_join_id (query_id, id) VALUES (?, ?)";
   private static final String INSERT_INTO_JOIN_CHAR_ID = "INSERT INTO osee_join_char_id (query_id, id) VALUES (?, ?)";
   private static final String INSERT_INTO_JOIN_ID4 =
      "INSERT INTO osee_join_id4 (query_id, id1, id2, id3, id4) VALUES (?, ?, ?, ?, ?)";

   private static final String DELETE_FROM_JOIN_ID = "DELETE FROM osee_join_id WHERE query_id = ?";
   private static final String DELETE_FROM_JOIN_TRANSACTION = "DELETE FROM osee_join_transaction WHERE query_id = ?";
   private static final String DELETE_FROM_JOIN_CHAR_ID = "DELETE FROM osee_join_char_id WHERE query_id =?";
   private static final String DELETE_FROM_JOIN_ID4 = "DELETE FROM osee_join_id4 WHERE query_id = ?";

   private static final String INSERT_INTO_JOIN_CLEANUP =
      "INSERT INTO osee_join_cleanup (query_id, table_name, issued_at, expires_in) VALUES (?,?,?,?)";

   private static final String DELETE_FROM_JOIN_CLEANUP = "DELETE FROM osee_join_cleanup WHERE query_id =?";

   public enum JoinItem {
      TRANSACTION("osee_join_transaction", INSERT_INTO_JOIN_TRANSACTION, DELETE_FROM_JOIN_TRANSACTION),
      ID("osee_join_id", INSERT_INTO_JOIN_ID, DELETE_FROM_JOIN_ID),
      CHAR_ID("osee_join_char_id", INSERT_INTO_JOIN_CHAR_ID, DELETE_FROM_JOIN_CHAR_ID),
      ID4("osee_join_id4", INSERT_INTO_JOIN_ID4, DELETE_FROM_JOIN_ID4);

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

   private final JdbcClient jdbcClient;

   public DatabaseJoinAccessor(JdbcClient jdbcClient) {
      super();
      this.jdbcClient = jdbcClient;
   }

   @Override
   public int delete(JdbcConnection connection, JoinItem joinItem, int queryId) throws OseeCoreException {
      int updated = 0;
      if (queryId != -1) {
         updated = jdbcClient.runPreparedUpdate(connection, joinItem.getDeleteSql(), queryId);
         jdbcClient.runPreparedUpdate(connection, DELETE_FROM_JOIN_CLEANUP, queryId);
      }
      return updated;
   }

   @Override
   public void store(JdbcConnection connection, JoinItem joinItem, int queryId, List<Object[]> dataList, Long issuedAt, Long expiresIn) throws OseeCoreException {
      jdbcClient.runPreparedUpdate(connection, INSERT_INTO_JOIN_CLEANUP, queryId, joinItem.getJoinTableName(), issuedAt,
         expiresIn);
      jdbcClient.runBatchUpdate(connection, joinItem.getInsertSql(), dataList);
   }

   @Override
   public Collection<Integer> getAllQueryIds(JdbcConnection connection, JoinItem joinItem) throws OseeCoreException {
      Collection<Integer> queryIds = new ArrayList<>();
      JdbcStatement chStmt = jdbcClient.getStatement(connection);
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
