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
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.accessor.UpdatePreviousTxCurrent;
import org.eclipse.osee.orcs.db.internal.sql.RelationalConstants;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * the behavior of this class - it needs to: have a branch
 * 
 * @author David Miller
 */
public final class BranchCopyTxCallable extends AbstractDatastoreTxCallable<Void> {

   private final CreateBranchData branchData;

   private static final String INSERT_TX_DETAILS =
      "INSERT INTO osee_tx_details (branch_id, transaction_id, osee_comment, time, author, tx_type) VALUES (?,?,?,?,?,?)";

   private static final String INSERT_ADDRESSING =
      "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current, branch_id) VALUES (?,?,?,?,?)";

   private static final String SELECT_ADDRESSING =
      "SELECT gamma_id, mod_type FROM osee_txs txs WHERE txs.branch_id = ? AND txs.transaction_id = ?";

   private final SqlJoinFactory joinFactory;
   private final IdentityManager idManager;

   public BranchCopyTxCallable(Log logger, OrcsSession session, JdbcClient jdbcClient, SqlJoinFactory joinFactory, IdentityManager idManager, CreateBranchData branchData) {
      super(logger, session, jdbcClient);
      this.joinFactory = joinFactory;
      this.branchData = branchData;
      this.idManager = idManager;
   }

   @Override
   public Void handleTxWork(JdbcConnection connection) throws OseeCoreException {

      // copy the branch up to the prior transaction - the goal is to have the provided
      // transaction available on the new branch for merging or comparison purposes
      // first set aside the transaction

      Callable<Void> callable =
         new CreateBranchDatabaseTxCallable(getLogger(), getSession(), getJdbcClient(), idManager, branchData);

      try {
         callable.call();

         Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
         int nextTransactionId = idManager.getNextTransactionId();

         String creationComment = branchData.getCreationComment();

         getJdbcClient().runPreparedUpdate(connection, INSERT_TX_DETAILS, branchData.getUuid(), nextTransactionId,
            creationComment, timestamp, branchData.getUserArtifactId(), TransactionDetailsType.NonBaselined.getId());

         populateTransaction(0.30, connection, nextTransactionId, branchData.getParentBranchUuid(),
            branchData.getSavedTransaction().getGuid());

         UpdatePreviousTxCurrent updater =
            new UpdatePreviousTxCurrent(getJdbcClient(), joinFactory, connection, branchData.getUuid());
         updater.updateTxNotCurrentsFromTx(nextTransactionId);

      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return null;
   }

   private void populateTransaction(double workAmount, JdbcConnection connection, int intoTx, Long parentBranch, int copyTxId) throws OseeCoreException {
      List<Object[]> data = new ArrayList<Object[]>();
      HashSet<Integer> gammas = new HashSet<Integer>(100000);
      long parentBranchId = RelationalConstants.BRANCH_SENTINEL;
      if (parentBranch != null) {
         parentBranchId = parentBranch;
      }

      populateAddressingToCopy(connection, data, intoTx, gammas, SELECT_ADDRESSING, parentBranchId, copyTxId);

      if (!data.isEmpty()) {
         getJdbcClient().runBatchUpdate(connection, INSERT_ADDRESSING, data);
      }

      checkForCancelled();
   }

   private void populateAddressingToCopy(JdbcConnection connection, List<Object[]> data, int baseTxId, HashSet<Integer> gammas, String query, Object... parameters) throws OseeCoreException {
      JdbcStatement chStmt = getJdbcClient().getStatement(connection);
      try {
         chStmt.runPreparedQuery(JdbcConstants.JDBC__MAX_FETCH_SIZE, query, parameters);
         while (chStmt.next()) {
            checkForCancelled();
            Integer gamma = chStmt.getInt("gamma_id");
            if (!gammas.contains(gamma)) {
               ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));
               TxChange txCurrent = TxChange.getCurrent(modType);
               data.add(new Object[] {baseTxId, gamma, modType.getValue(), txCurrent.getValue(), branchData.getUuid()});
               gammas.add(gamma);
            }
         }
      } finally {
         chStmt.close();
      }
   }
}
