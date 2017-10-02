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
package org.eclipse.osee.orcs.db.internal.callable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.jdbc.JdbcTransaction;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.accessor.UpdatePreviousTxCurrent;

/**
 * the behavior of this class - it needs to: have a branch
 *
 * @author David Miller
 */
public final class BranchCopyTxCallable extends JdbcTransaction {

   private final CreateBranchData branchData;

   private static final String INSERT_TX_DETAILS =
      "INSERT INTO osee_tx_details (branch_id, transaction_id, osee_comment, time, author, tx_type, build_id) VALUES (?,?,?,?,?,?,?)";

   private static final String INSERT_ADDRESSING =
      "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current, branch_id, app_id) VALUES (?,?,?,?,?,?)";

   private static final String SELECT_ADDRESSING =
      "SELECT gamma_id, mod_type, app_id FROM osee_txs txs WHERE txs.branch_id = ? AND txs.transaction_id = ?";

   private final JdbcClient jdbcClient;
   private final IdentityManager idManager;

   private final Long buildVersionId;

   public BranchCopyTxCallable(JdbcClient jdbcClient, IdentityManager idManager, CreateBranchData branchData, Long buildVersionId) {
      this.jdbcClient = jdbcClient;
      this.branchData = branchData;
      this.idManager = idManager;
      this.buildVersionId = buildVersionId;
   }

   @Override
   public void handleTxWork(JdbcConnection connection) {

      // copy the branch up to the prior transaction - the goal is to have the provided
      // transaction available on the new branch for merging or comparison purposes
      // first set aside the transaction

      new CreateBranchDatabaseTxCallable(jdbcClient, idManager, branchData, buildVersionId).handleTxWork(connection);

      Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
      TransactionId nextTransactionId = idManager.getNextTransactionId();

      String creationComment = branchData.getCreationComment();

      jdbcClient.runPreparedUpdate(connection, INSERT_TX_DETAILS, branchData.getBranch(), nextTransactionId,
         creationComment, timestamp, branchData.getAuthor(), TransactionDetailsType.NonBaselined.getId(),
         OseeCodeVersion.getVersionId());

      populateTransaction(0.30, connection, nextTransactionId, branchData.getParentBranch(),
         branchData.getSavedTransaction());

      UpdatePreviousTxCurrent updater = new UpdatePreviousTxCurrent(jdbcClient, connection, branchData.getBranch());
      updater.updateTxNotCurrentsFromTx(nextTransactionId);
   }

   private void populateTransaction(double workAmount, JdbcConnection connection, TransactionId intoTx, BranchId parentBranch, TransactionId copyTxId) {
      List<Object[]> data = new ArrayList<>();
      HashSet<Long> gammas = new HashSet<>(100000);

      populateAddressingToCopy(connection, data, intoTx, gammas, SELECT_ADDRESSING, parentBranch, copyTxId);

      if (!data.isEmpty()) {
         jdbcClient.runBatchUpdate(connection, INSERT_ADDRESSING, data);
      }
   }

   private void populateAddressingToCopy(JdbcConnection connection, List<Object[]> data, TransactionId baseTxId, HashSet<Long> gammas, String query, Object... parameters) {
      JdbcStatement chStmt = jdbcClient.getStatement(connection);
      try {
         chStmt.runPreparedQuery(JdbcConstants.JDBC__MAX_FETCH_SIZE, query, parameters);
         while (chStmt.next()) {
            Long gamma = chStmt.getLong("gamma_id");
            if (!gammas.contains(gamma)) {
               ModificationType modType = ModificationType.valueOf(chStmt.getInt("mod_type"));
               Long app_id = chStmt.getLong("app_id");
               TxChange txCurrent = TxChange.getCurrent(modType);
               data.add(new Object[] {baseTxId, gamma, modType, txCurrent, branchData.getBranch(), app_id});
               gammas.add(gamma);
            }
         }
      } finally {
         chStmt.close();
      }
   }
}
