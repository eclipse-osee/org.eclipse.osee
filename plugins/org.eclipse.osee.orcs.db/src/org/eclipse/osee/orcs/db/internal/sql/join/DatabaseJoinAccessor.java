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
package org.eclipse.osee.orcs.db.internal.sql.join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.JoinItem;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseJoinAccessor implements IJoinAccessor {

   private static final String SELECT_QUERY_IDS = "select DISTINCT query_id from %s";

   private static final String INSERT_INTO_JOIN_CLEANUP =
      "INSERT INTO osee_join_cleanup (query_id, table_name, issued_at, expires_in) VALUES (?,?,?,?)";

   private static final String DELETE_FROM_JOIN_CLEANUP = "DELETE FROM osee_join_cleanup WHERE query_id =?";

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
