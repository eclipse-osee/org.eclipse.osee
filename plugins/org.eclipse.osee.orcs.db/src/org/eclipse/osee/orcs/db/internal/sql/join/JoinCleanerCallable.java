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

import java.util.HashMap;
import java.util.function.Consumer;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.jdbc.OseePreparedStatement;
import org.eclipse.osee.logger.Log;

/**
 * If -1 is found in the expires_in column, it means never expire.
 *
 * @author Roberto E. Escobar
 */
public class JoinCleanerCallable extends CancellableCallable<Void> {

   private final static String DELETE_JOIN_CLEANUP = "DELETE FROM osee_join_cleanup WHERE query_id = ?";
   private final static String SELECT_FROM_JOIN_CLEANUP = "SELECT * from osee_join_cleanup";

   private final Log logger;
   private final JdbcClient jdbcClient;

   public JoinCleanerCallable(Log logger, JdbcClient jdbcClient) {
      this.logger = logger;
      this.jdbcClient = jdbcClient;
   }

   private boolean isExpired(Long issuedAt, Long lifetime) {
      return lifetime != -1 && issuedAt + lifetime < System.currentTimeMillis() / 1000;
   }

   @Override
   public Void call() throws Exception {
      try {
         OseePreparedStatement cleanupDelete = jdbcClient.getBatchStatement(DELETE_JOIN_CLEANUP);
         HashMap<String, OseePreparedStatement> joinDeletes = new HashMap<>();

         Consumer<JdbcStatement> consumer = stmt -> {
            Long issuedAt = stmt.getLong("issued_at");
            Long expiresIn = stmt.getLong("expires_in");
            if (isExpired(issuedAt, expiresIn)) {
               String tableName = stmt.getString("table_name");
               Integer queryId = stmt.getInt("query_id");

               OseePreparedStatement joinDelete = joinDeletes.get(tableName);
               if (joinDelete == null) {
                  joinDelete = jdbcClient.getBatchStatement("DELETE FROM " + tableName + " WHERE query_id = ?");
                  joinDeletes.put(tableName, joinDelete);
               }

               joinDelete.addToBatch(queryId);
               cleanupDelete.addToBatch(queryId);
            }
         };
         jdbcClient.runQuery(consumer, SELECT_FROM_JOIN_CLEANUP);

         cleanupDelete.execute();
         for (OseePreparedStatement joinDelete : joinDeletes.values()) {
            joinDelete.execute();
         }
      } catch (Exception ex) {
         logger.error(ex, "Error cleaning join");
      }
      return null;
   }
}