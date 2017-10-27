/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.sql.join;

import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.jdbc.OseePreparedStatement;
import org.eclipse.osee.logger.Log;

/**
 * If -1 is found in the expires_in column, it means never expire.
 *
 * @author Roberto E. Escobar
 */
public class JoinCleanerCallable implements Runnable {
   private final static String DELETE_JOIN_CLEANUP = "DELETE FROM osee_join_cleanup WHERE query_id = ?";
   private final static String SELECT_FROM_JOIN_CLEANUP = "SELECT * from osee_join_cleanup order by table_name";

   private final Log logger;
   private final JdbcClient jdbcClient;
   private String previousTableName;
   private long currentTime;
   private OseePreparedStatement joinDelete;
   private OseePreparedStatement cleanupDelete;

   public JoinCleanerCallable(Log logger, JdbcClient jdbcClient) {
      this.logger = logger;
      this.jdbcClient = jdbcClient;
   }

   private boolean isExpired(long issuedAt, long lifetime, long currentTime) {
      return lifetime != -1 && issuedAt + lifetime < currentTime;
   }

   private void resetForNextCall() {
      previousTableName = "";
      currentTime = System.currentTimeMillis() / 1000;
      joinDelete = null;
      cleanupDelete = jdbcClient.getBatchStatement(DELETE_JOIN_CLEANUP);
   }

   @Override
   public void run() {
      try {
         resetForNextCall();
         try {
            jdbcClient.runQuery(this::processRow, SELECT_FROM_JOIN_CLEANUP);
         } finally {
            cleanupDelete.execute();
            if (joinDelete != null) {
               joinDelete.execute();
            }
         }
      } catch (Exception ex) {
         logger.error(ex, "Error cleaning join tables");
      }
   }

   private void processRow(JdbcStatement stmt) {
      long issuedAt = stmt.getLong("issued_at");
      long expiresIn = stmt.getLong("expires_in");

      if (isExpired(issuedAt, expiresIn, currentTime)) {
         String tableName = stmt.getString("table_name");
         if (!tableName.equals(previousTableName)) {
            if (joinDelete != null) {
               joinDelete.execute();
            }
            joinDelete = jdbcClient.getBatchStatement("DELETE FROM " + tableName + " WHERE query_id = ?");
            previousTableName = tableName;
         }

         Integer queryId = stmt.getInt("query_id");
         joinDelete.addToBatch(queryId);
         cleanupDelete.addToBatch(queryId);
      }
   }
}