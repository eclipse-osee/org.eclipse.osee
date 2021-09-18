/*********************************************************************
 * Copyright (c) 2012 Boeing
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
import org.eclipse.osee.jdbc.SqlTable;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OseeDb;
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

   private static final String TX_GET_PREVIOUS_TX_NOT_CURRENT_ARTIFACTS =
      "SELECT%s txs.transaction_id, txs.gamma_id FROM osee_join_id jid, osee_artifact art, osee_txs txs WHERE jid.query_id = ? AND art.art_id = jid.id AND art.gamma_id = txs.gamma_id AND txs.branch_id = ? AND txs.tx_current <> " + TxCurrent.NOT_CURRENT;

   private static final String TX_GET_PREVIOUS_TX_NOT_CURRENT_ATTRIBUTES =
      "SELECT%s txs.transaction_id, txs.gamma_id FROM osee_join_id jid, osee_attribute attr, osee_txs txs WHERE jid.query_id = ? AND attr.attr_id = jid.id AND attr.gamma_id = txs.gamma_id AND txs.branch_id = ? AND txs.tx_current <> " + TxCurrent.NOT_CURRENT;

   private static final String TX_GET_PREVIOUS_TX_NOT_CURRENT_RELATIONS =
      "SELECT%s txs.transaction_id, txs.gamma_id FROM osee_join_id jid, osee_relation_link rel, osee_txs txs WHERE jid.query_id = ? AND rel.rel_link_id = jid.id AND rel.gamma_id = txs.gamma_id AND txs.branch_id = ? AND txs.tx_current <> " + TxCurrent.NOT_CURRENT;

   private static final String TX_GET_PREVIOUS_TX_NOT_CURRENT_TUPLE =
      "SELECT%s txs.transaction_id, txs.gamma_id FROM osee_join_id jid, osee_txs txs WHERE jid.query_id = ? AND jid.id = txs.gamma_id AND txs.branch_id = ?   AND txs.tx_current <> " + TxCurrent.NOT_CURRENT;

   public static enum SqlOrderEnum {
      ARTIFACTS(OseeDb.ARTIFACT_TABLE, TX_GET_PREVIOUS_TX_NOT_CURRENT_ARTIFACTS),
      ATTRIBUTES(OseeDb.ATTRIBUTE_TABLE, TX_GET_PREVIOUS_TX_NOT_CURRENT_ATTRIBUTES),
      RELATIONS(OseeDb.RELATION_TABLE, TX_GET_PREVIOUS_TX_NOT_CURRENT_RELATIONS),
      TUPLES2(OseeDb.TUPLE2, TX_GET_PREVIOUS_TX_NOT_CURRENT_TUPLE),
      TUPLES3(OseeDb.TUPLE3, TX_GET_PREVIOUS_TX_NOT_CURRENT_TUPLE),
      TUPLES4(OseeDb.TUPLE4, TX_GET_PREVIOUS_TX_NOT_CURRENT_TUPLE),
      TXS_DETAIL(OseeDb.TX_DETAILS_TABLE),
      TXS(OseeDb.TXS_TABLE);

      private String notCurrentSearch;
      private String sql;

      private SqlOrderEnum(SqlTable table) {
         this(table, null);
      }

      private SqlOrderEnum(SqlTable table, String notCurrentSearch) {
         this.sql = table.getInsertSql();
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
            fetchTxNotCurrent(connection, tx.getBranch(), txNotCurrentData,
               jdbcClient.injectOrderedHint(entry.getKey().getTxsNotCurrentQuery()), entry.getValue());
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

         try (JdbcStatement chStmt = jdbcClient.getStatement(connection)) {
            chStmt.runPreparedQuery(query, join.getQueryId(), branch);
            while (chStmt.next()) {
               results.add(new Object[] {branch, chStmt.getLong("transaction_id"), chStmt.getLong("gamma_id")});
            }
         }
      } finally {
         join.close();
      }
   }
}
