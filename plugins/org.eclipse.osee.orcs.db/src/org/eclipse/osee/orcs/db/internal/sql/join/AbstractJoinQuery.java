/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.sql.join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import org.eclipse.osee.framework.core.enums.JoinItem;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.OseePreparedStatement;
import org.eclipse.osee.orcs.OseeDb;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractJoinQuery implements AutoCloseable {
   private static final Long DEFAULT_JOIN_EXPIRATION_SECONDS = 3L * 60L * 60L; // 3 hours
   private static final String SELECT_QUERY_IDS = "select DISTINCT query_id from %s";
   private static final String DELETE_FROM_JOIN_CLEANUP = "DELETE FROM osee_join_cleanup WHERE query_id =?";

   private static final Random random = new Random();

   private final JoinItem joinItem;
   private final Long queryId;
   private final JdbcClient jdbcClient;
   private final JdbcConnection connection;
   private final OseePreparedStatement addressing;
   private boolean wasStored;

   protected AbstractJoinQuery(JoinItem joinItem, JdbcClient jdbcClient, JdbcConnection connection) {
      this.joinItem = joinItem;
      this.queryId = random.nextLong();
      this.jdbcClient = jdbcClient;
      this.connection = connection;
      this.addressing = jdbcClient.getBatchStatement(connection, joinItem.getInsertSql());
   }

   public boolean isEmpty() {
      return size() == 0;
   }

   public int size() {
      return addressing.size();
   }

   public Long getQueryId() {
      return queryId;
   }

   public boolean wasStored() {
      return wasStored;
   }

   public void store() {
      if (wasStored) {
         throw new OseeCoreException("Cannot store query id twice");
      } else {
         jdbcClient.runPreparedUpdate(connection, OseeDb.OSEE_JOIN_CLEANUP_TABLE.getInsertSql(), queryId,
            joinItem.getJoinTableName(), getIssuedAt(), DEFAULT_JOIN_EXPIRATION_SECONDS);
         addressing.execute();
         wasStored = true;
      }
   }

   private Long getIssuedAt() {
      return System.currentTimeMillis() / 1000L;
   }

   @Override
   public void close() {
      if (wasStored) {
         jdbcClient.runPreparedUpdate(connection, joinItem.getDeleteSql(), queryId);
         jdbcClient.runPreparedUpdate(connection, DELETE_FROM_JOIN_CLEANUP, queryId);
      } else {
         addressing.close();
      }
   }

   public Collection<Long> getAllQueryIds() {
      Collection<Long> queryIds = new ArrayList<>();
      String query = String.format(SELECT_QUERY_IDS, joinItem.getJoinTableName());
      jdbcClient.runQuery(connection, stmt -> queryIds.add(stmt.getLong("query_id")), query);
      return queryIds;
   }

   @Override
   public String toString() {
      return String.format("id: [%s] entrySize: [%d]", getQueryId(), size());
   }

   protected void addToBatch(Object obj1) {
      addressing.addToBatch(queryId, obj1);
   }

   protected void addToBatch(Object obj1, Object obj2) {
      addressing.addToBatch(queryId, obj1, obj2);
   }

   protected void addToBatch(Object obj1, Object obj2, Object obj3, Object obj4) {
      addressing.addToBatch(queryId, obj1, obj2, obj3, obj4);
   }

   public void addAll(Iterable<?> values) {
      for (Object value : values) {
         addressing.addToBatch(queryId, value);
      }
   }

   public void addAndStore(Iterable<?> values) {
      addAll(values);
      store();
   }
}