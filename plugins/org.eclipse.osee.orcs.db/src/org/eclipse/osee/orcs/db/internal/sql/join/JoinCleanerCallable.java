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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcProcessor;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import com.google.common.base.Supplier;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;

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
      return lifetime != -1 && ((issuedAt + lifetime) < (System.currentTimeMillis() / 1000));
   }

   @Override
   public Void call() throws Exception {
      try {
         final ListMultimap<String, Object[]> expiredItems = newListMultimap();
         jdbcClient.runQuery(new JdbcProcessor() {

            @Override
            public void processNext(JdbcStatement chStmt) {
               Long issuedAt = chStmt.getLong("issues_at");
               Long expiresIn = chStmt.getLong("expires_in");
               if (isExpired(issuedAt, expiresIn)) {
                  String tableName = chStmt.getString("table_name");
                  Integer queryId = chStmt.getInt("query_id");
                  expiredItems.put(tableName, new Integer[] {queryId});
               }
            }
         }, SELECT_FROM_JOIN_CLEANUP);

         if (!expiredItems.isEmpty()) {
            for (Entry<String, Collection<Object[]>> entry : expiredItems.asMap().entrySet()) {
               String query = String.format("DELETE FROM %s WHERE query_id = ?", entry.getKey());
               List<Object[]> ids = (List<Object[]>) entry.getValue();
               jdbcClient.runBatchUpdate(query, ids);
               jdbcClient.runBatchUpdate(DELETE_JOIN_CLEANUP, ids);
            }
         }
      } catch (Exception ex) {
         logger.error(ex, "Error cleaning join");
         throw ex;
      }
      return null;
   }

   private static <K, V> ListMultimap<K, V> newListMultimap() {
      Map<K, Collection<V>> map = Maps.newLinkedHashMap();
      return Multimaps.newListMultimap(map, new Supplier<List<V>>() {
         @Override
         public List<V> get() {
            return Lists.newArrayList();
         }
      });
   }

}
