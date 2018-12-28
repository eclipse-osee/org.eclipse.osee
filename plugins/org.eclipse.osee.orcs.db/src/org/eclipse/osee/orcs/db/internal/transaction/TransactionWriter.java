/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.OrcsChangeSet;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class TransactionWriter {

   protected static final String UPDATE_TXS_NOT_CURRENT =
      "UPDATE osee_txs SET tx_current = " + TxCurrent.NOT_CURRENT + " WHERE branch_id = ? AND transaction_id = ? AND gamma_id = ?";

   private static final String INSERT_ARTIFACT =
      "INSERT INTO osee_artifact (art_id, art_type_id, gamma_id, guid) VALUES (?,?,?,?)";

   private static final String INSERT_ATTRIBUTE =
      "INSERT INTO osee_attribute (attr_id, attr_type_id, gamma_id, art_id, value, uri) VALUES (?, ?, ?, ?, ?, ?)";

   private static final String INSERT_RELATION_TABLE =
      "INSERT INTO osee_relation_link (rel_link_id, rel_link_type_id, gamma_id, a_art_id, b_art_id, rationale) VALUES (?,?,?,?,?,?)";

   private static final String INSERT_TUPLES2_TABLE =
      "INSERT INTO osee_tuple2 (tuple_type, e1, e2, gamma_id) VALUES (?,?,?,?)";

   private static final String INSERT_TUPLES3_TABLE =
      "INSERT INTO osee_tuple3 (tuple_type, e1, e2, e3, gamma_id) VALUES (?,?,?,?,?)";

   private static final String INSERT_TUPLES4_TABLE =
      "INSERT INTO osee_tuple4 (tuple_type, e1, e2, e3, e4, gamma_id) VALUES (?,?,?,?,?,?)";

   private static final String INSERT_INTO_TRANSACTION_TABLE =
      "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current, branch_id, app_id) VALUES (?, ?, ?, ?, ?, ?)";

   private static final String INSERT_INTO_TRANSACTION_DETAIL =
      "INSERT INTO osee_tx_details (transaction_id, osee_comment, time, author, branch_id, tx_type, build_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

   private static final String TX_GET_PREVIOUS_TX_NOT_CURRENT_ARTIFACTS =
      "SELECT txs.transaction_id, txs.gamma_id, txs.app_id FROM osee_join_id jid, osee_artifact art, osee_txs txs WHERE jid.query_id = ? AND art.art_id = jid.id AND art.gamma_id = txs.gamma_id AND txs.branch_id = ? AND txs.tx_current <> " + TxCurrent.NOT_CURRENT;

   private static final String TX_GET_PREVIOUS_TX_NOT_CURRENT_ATTRIBUTES =
      "SELECT txs.transaction_id, txs.gamma_id, txs.app_id FROM osee_join_id jid, osee_attribute attr, osee_txs txs WHERE jid.query_id = ? AND attr.attr_id = jid.id AND attr.gamma_id = txs.gamma_id AND txs.branch_id = ? AND txs.tx_current <> " + TxCurrent.NOT_CURRENT;

   private static final String TX_GET_PREVIOUS_TX_NOT_CURRENT_RELATIONS =
      "SELECT txs.transaction_id, txs.gamma_id, txs.app_id FROM osee_join_id jid, osee_relation_link rel, osee_txs txs WHERE jid.query_id = ? AND rel.rel_link_id = jid.id AND rel.gamma_id = txs.gamma_id AND txs.branch_id = ? AND txs.tx_current <> " + TxCurrent.NOT_CURRENT;

   public static enum SqlOrderEnum {
      ARTIFACTS(INSERT_ARTIFACT, TX_GET_PREVIOUS_TX_NOT_CURRENT_ARTIFACTS),
      ATTRIBUTES(INSERT_ATTRIBUTE, TX_GET_PREVIOUS_TX_NOT_CURRENT_ATTRIBUTES),
      RELATIONS(INSERT_RELATION_TABLE, TX_GET_PREVIOUS_TX_NOT_CURRENT_RELATIONS),
      TUPLES2(INSERT_TUPLES2_TABLE),
      TUPLES3(INSERT_TUPLES3_TABLE),
      TUPLES4(INSERT_TUPLES4_TABLE),
      TXS_DETAIL(INSERT_INTO_TRANSACTION_DETAIL),
      TXS(INSERT_INTO_TRANSACTION_TABLE);

      private String notCurrentSearch;
      private String sql;

      private SqlOrderEnum(String sql) {
         this.sql = sql;
         this.notCurrentSearch = null;
      }

      private SqlOrderEnum(String sql, String notCurrentSearch) {
         this.sql = sql;
         this.notCurrentSearch = notCurrentSearch;
      }

      public boolean hasTxNotCurrentQuery() {
         return notCurrentSearch != null;
      }

      public String getTxsNotCurrentQuery() {
         return notCurrentSearch;
      }

      public String getInsertSql() {
         return sql;
      }
   }

   private final Log logger;
   private final JdbcClient jdbcClient;
   private final TxSqlBuilder sqlBuilder;

   private List<DataProxy<?>> binaryStores;

   public TransactionWriter(Log logger, JdbcClient jdbcClient, TxSqlBuilder sqlBuilder) {
      super();
      this.logger = logger;
      this.jdbcClient = jdbcClient;
      this.sqlBuilder = sqlBuilder;
   }

   protected List<DataProxy<?>> getBinaryStores() {
      return binaryStores;
   }

   public void rollback() {
      for (DataProxy<?> proxy : getBinaryStores()) {
         try {
            proxy.rollBack();
         } catch (OseeCoreException ex1) {
            logger.error(ex1, "Error during binary rollback [%s]", proxy);
         }
      }
   }

   public void write(JdbcConnection connection, TransactionReadable tx, OrcsChangeSet txData) {
      sqlBuilder.accept(tx, txData);
      try {
         binaryStores = sqlBuilder.getBinaryStores();
         for (DataProxy<?> proxy : binaryStores) {
            proxy.persist();
         }
         sqlBuilder.updateAfterBinaryStorePersist();

         List<Object[]> txNotCurrentData = new ArrayList<>();
         for (Entry<SqlOrderEnum, ? extends AbstractJoinQuery> entry : sqlBuilder.getTxNotCurrents()) {
            fetchTxNotCurrent(connection, tx.getBranch(), txNotCurrentData, entry.getKey().getTxsNotCurrentQuery(),
               entry.getValue());
         }

         // Insert into tables
         for (SqlOrderEnum key : SqlOrderEnum.values()) {
            List<Object[]> data = sqlBuilder.getInsertData(key);
            if (data != null && !data.isEmpty()) {
               jdbcClient.runBatchUpdate(connection, key.getInsertSql(), data);
            }
         }
         jdbcClient.runBatchUpdate(connection, UPDATE_TXS_NOT_CURRENT, txNotCurrentData);
      } finally {
         sqlBuilder.clear();
      }
   }

   private void fetchTxNotCurrent(JdbcConnection connection, BranchId branch, List<Object[]> results, String query, AbstractJoinQuery join) {
      try {
         join.store();
         JdbcStatement chStmt = jdbcClient.getStatement(connection);
         try {
            chStmt.runPreparedQuery(query, join.getQueryId(), branch);
            while (chStmt.next()) {
               results.add(new Object[] {branch, chStmt.getLong("transaction_id"), chStmt.getLong("gamma_id")});
            }
         } finally {
            chStmt.close();
         }
      } finally {
         join.close();
      }
   }
}
