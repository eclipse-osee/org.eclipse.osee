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
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.OrcsChangeSet;

/**
 * @author Roberto E. Escobar
 */
public class TransactionWriter {

   protected static final String UPDATE_TXS_NOT_CURRENT =
      "UPDATE osee_txs SET tx_current = " + TxChange.NOT_CURRENT.getValue() + " WHERE branch_id = ? AND transaction_id = ? AND gamma_id = ?";

   private static final String INSERT_ARTIFACT =
      "INSERT INTO osee_artifact (art_id, art_type_id, gamma_id, guid) VALUES (?,?,?,?)";

   private static final String INSERT_ATTRIBUTE =
      "INSERT INTO osee_attribute (attr_id, attr_type_id, gamma_id, art_id, value, uri) VALUES (?, ?, ?, ?, ?, ?)";

   private static final String INSERT_RELATION_TABLE =
      "INSERT INTO osee_relation_link (rel_link_id, rel_link_type_id, gamma_id, a_art_id, b_art_id, rationale) VALUES (?,?,?,?,?,?)";

   private static final String INSERT_INTO_TRANSACTION_TABLE =
      "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current, branch_id) VALUES (?, ?, ?, ?, ?)";

   private static final String INSERT_INTO_TRANSACTION_DETAIL =
      "INSERT INTO osee_tx_details (transaction_id, osee_comment, time, author, branch_id, tx_type) VALUES (?, ?, ?, ?, ?, ?)";

   private static final String TX_GET_PREVIOUS_TX_NOT_CURRENT_ARTIFACTS =
      "SELECT txs.transaction_id, txs.gamma_id FROM osee_join_id jid, osee_artifact art, osee_txs txs WHERE jid.query_id = ? AND art.art_id = jid.id AND art.gamma_id = txs.gamma_id AND txs.branch_id = ? AND txs.tx_current <> " + TxChange.NOT_CURRENT.getValue();

   private static final String TX_GET_PREVIOUS_TX_NOT_CURRENT_ATTRIBUTES =
      "SELECT txs.transaction_id, txs.gamma_id FROM osee_join_id jid, osee_attribute attr, osee_txs txs WHERE jid.query_id = ? AND attr.attr_id = jid.id AND attr.gamma_id = txs.gamma_id AND txs.branch_id = ? AND txs.tx_current <> " + TxChange.NOT_CURRENT.getValue();

   private static final String TX_GET_PREVIOUS_TX_NOT_CURRENT_RELATIONS =
      "SELECT txs.transaction_id, txs.gamma_id FROM osee_join_id jid, osee_relation_link rel, osee_txs txs WHERE jid.query_id = ? AND rel.rel_link_id = jid.id AND rel.gamma_id = txs.gamma_id AND txs.branch_id = ? AND txs.tx_current <> " + TxChange.NOT_CURRENT.getValue();

   public static enum SqlOrderEnum {
      ARTIFACTS(INSERT_ARTIFACT, TX_GET_PREVIOUS_TX_NOT_CURRENT_ARTIFACTS),
      ATTRIBUTES(INSERT_ATTRIBUTE, TX_GET_PREVIOUS_TX_NOT_CURRENT_ATTRIBUTES),
      RELATIONS(INSERT_RELATION_TABLE, TX_GET_PREVIOUS_TX_NOT_CURRENT_RELATIONS),
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
   private final IOseeDatabaseService dbService;
   private final TxSqlBuilder sqlBuilder;

   private List<DaoToSql> binaryStores;

   public TransactionWriter(Log logger, IOseeDatabaseService dbService, TxSqlBuilder sqlBuilder) {
      super();
      this.logger = logger;
      this.dbService = dbService;
      this.sqlBuilder = sqlBuilder;
   }

   protected List<DaoToSql> getBinaryStores() {
      return binaryStores;
   }

   public void rollback() {
      for (DaoToSql tx : getBinaryStores()) {
         try {
            tx.rollBack();
         } catch (OseeCoreException ex1) {
            logger.error(ex1, "Error during binary rollback [%s]", tx);
         }
      }
   }

   public void write(OseeConnection connection, TransactionRecord tx, OrcsChangeSet txData) throws OseeCoreException {
      sqlBuilder.accept(tx, txData);
      try {
         binaryStores = sqlBuilder.getBinaryStores();
         for (DaoToSql dao : getBinaryStores()) {
            dao.persist();
         }
         sqlBuilder.updateAfterBinaryStorePersist();

         long branchId = tx.getBranch().getUuid();
         List<Object[]> txNotCurrentData = new ArrayList<Object[]>();
         for (Entry<SqlOrderEnum, ? extends AbstractJoinQuery> entry : sqlBuilder.getTxNotCurrents()) {
            fetchTxNotCurrent(connection, branchId, txNotCurrentData, entry.getKey().getTxsNotCurrentQuery(),
               entry.getValue());
         }

         // Insert into tables
         for (SqlOrderEnum key : SqlOrderEnum.values()) {
            List<Object[]> data = sqlBuilder.getInsertData(key);
            if (data != null && !data.isEmpty()) {
               dbService.runBatchUpdate(connection, key.getInsertSql(), data);
            }
         }
         dbService.runBatchUpdate(connection, UPDATE_TXS_NOT_CURRENT, txNotCurrentData);
         tx.clearDirty();
      } finally {
         sqlBuilder.clear();
      }
   }

   private void fetchTxNotCurrent(OseeConnection connection, long branchId, List<Object[]> results, String query, AbstractJoinQuery join) throws OseeCoreException {
      try {
         join.store();
         IOseeStatement chStmt = dbService.getStatement(connection);
         try {
            chStmt.runPreparedQuery(query, join.getQueryId(), branchId);
            while (chStmt.next()) {
               results.add(new Object[] {branchId, chStmt.getInt("transaction_id"), chStmt.getLong("gamma_id")});
            }
         } finally {
            chStmt.close();
         }
      } finally {
         join.delete();
      }
   }
}
