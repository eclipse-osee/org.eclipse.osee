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

import java.util.Collection;
import java.util.Random;
import org.eclipse.osee.framework.core.enums.JoinItem;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.OseePreparedStatement;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractJoinQuery implements AutoCloseable {
   private static final Long DEFAULT_JOIN_EXPIRATION_SECONDS = 3L * 60L * 60L; // 3 hours
   private static final String INSERT_INTO_JOIN_CLEANUP =
      "INSERT INTO osee_join_cleanup (query_id, table_name, issued_at, expires_in) VALUES (?,?,?,?)";
   private static final String DELETE_FROM_JOIN_CLEANUP = "DELETE FROM osee_join_cleanup WHERE query_id =?";

   private static final Random random = new Random();

   private final JoinItem joinItem;
   private final Long queryId;
   protected final OseePreparedStatement addressing;
   private final JdbcClient jdbcClient;
   private final JdbcConnection connection;

   private boolean wasStored;

   protected AbstractJoinQuery(JoinItem joinItem, JdbcClient jdbcClient, JdbcConnection connection) {
      this.joinItem = joinItem;
      this.queryId = Long.valueOf(random.nextInt());
      this.addressing = jdbcClient.getBatchStatement(connection, joinItem.getInsertSql());
      this.jdbcClient = jdbcClient;
      this.connection = connection;
   }

   public boolean isEmpty() {
      return size() == 0;
   }

   public int size() {
      return addressing.size();
   }

   public int getQueryId() {
      return queryId.intValue();
   }

   public void store() {
      if (wasStored) {
         throw new OseeCoreException("Cannot store query id twice");
      } else {
         jdbcClient.runPreparedUpdate(connection, INSERT_INTO_JOIN_CLEANUP, queryId, joinItem.getJoinTableName(),
            getIssuedAt(), DEFAULT_JOIN_EXPIRATION_SECONDS);
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

   @Override
   public String toString() {
      return String.format("id: [%s] entrySize: [%d]", getQueryId(), size());
   }

   protected void addToBatch(Object obj1) {
      addressing.addToBatch(queryId, obj1);
   }

   protected void addToBatch(Object obj1, Object obj2, Object obj3, Object obj4) {
      addressing.addToBatch(queryId, obj1, obj2, obj3, obj4);
   }

   public void addAndStore(Collection<? extends Id> ids) {
      for (Id id : ids) {
         addressing.addToBatch(queryId, id);
      }
      store();
   }
}