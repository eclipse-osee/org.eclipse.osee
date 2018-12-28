/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.accessor;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.jdbc.OseePreparedStatement;

/**
 * @author Ryan D. Brooks
 */
public class UpdatePreviousTxCurrent {
   private static final String UPDATE_TXS_NOT_CURRENT =
      "update osee_txs SET tx_current = " + TxCurrent.NOT_CURRENT + " where branch_id = ? AND gamma_id = ? and transaction_id = ?";
   private static final String UPDATE_TXS_NOT_CURRENT_NO_TX =
      "update osee_txs SET tx_current = " + TxCurrent.NOT_CURRENT + " where branch_id = ? AND gamma_id = ?";
// @formatter:off
   private static final String SELECT_TXS_AND_GAMMAS_FROM_TXS ="with\n"+
      "txs as (select gamma_id from osee_txs where branch_id = ? and transaction_id = ?),\n"+
      "item as (\n"+
      "   SELECT item2.gamma_id FROM osee_attribute item1, txs, osee_attribute item2 where txs.gamma_id = item1.gamma_id and item1.attr_id = item2.attr_id\n"+
      "UNION ALL\n"+
      "   SELECT item2.gamma_id FROM osee_artifact item1, txs, osee_artifact item2 where txs.gamma_id = item1.gamma_id and item1.art_id = item2.art_id\n"+
      "UNION ALL\n"+
      "   SELECT item2.gamma_id FROM osee_relation_link item1, txs, osee_relation_link item2 where txs.gamma_id = item1.gamma_id and item1.rel_link_id = item2.rel_link_id)\n"+
      "select txsb.transaction_id, txsb.gamma_id FROM item, osee_txs txsb where item.gamma_id = txsb.gamma_id AND txsb.branch_id = ? AND transaction_id <> ? AND txsb.tx_current <> " + TxCurrent.NOT_CURRENT;
// @formatter:on

   private final JdbcClient jdbcClient;
   private final BranchId branch;
   private final JdbcConnection connection;
   private final OseePreparedStatement gammaUpdate;

   public UpdatePreviousTxCurrent(JdbcClient jdbcClient, JdbcConnection connection, BranchId branch) {
      this.jdbcClient = jdbcClient;
      this.branch = branch;
      this.connection = connection;
      gammaUpdate = jdbcClient.getBatchStatement(connection, UPDATE_TXS_NOT_CURRENT_NO_TX);
   }

   public void addGamma(GammaId gammaId) {
      gammaUpdate.addToBatch(branch, gammaId);
   }

   public void updateTxNotCurrents() {
      gammaUpdate.execute();
   }

   public void updateTxNotCurrentsFromTx(TransactionId transaction_id) {
      OseePreparedStatement update = jdbcClient.getBatchStatement(connection, UPDATE_TXS_NOT_CURRENT);
      jdbcClient.runQuery(stmt -> update.addToBatch(branch, stmt.getLong("gamma_id"), stmt.getLong("transaction_id")),
         JdbcConstants.JDBC__MAX_FETCH_SIZE, SELECT_TXS_AND_GAMMAS_FROM_TXS, branch, transaction_id, branch,
         transaction_id);
      update.execute();
   }
}