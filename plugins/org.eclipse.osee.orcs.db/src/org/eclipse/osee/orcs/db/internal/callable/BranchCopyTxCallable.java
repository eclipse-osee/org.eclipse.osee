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

import static org.eclipse.osee.framework.database.core.IOseeStatement.MAX_FETCH;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.db.internal.accessor.UpdatePreviousTxCurrent;
import org.eclipse.osee.orcs.db.internal.sql.RelationalConstants;

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

   public BranchCopyTxCallable(Log logger, OrcsSession session, IOseeDatabaseService databaseService, CreateBranchData branchData) {
      super(logger, session, databaseService, String.format("Create Branch %s", branchData.getName()));
      this.branchData = branchData;
      //this.systemUserId = -1;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Void handleTxWork(OseeConnection connection) throws OseeCoreException {

      // copy the branch up to the prior transaction - the goal is to have the provided
      // transaction available on the new branch for merging or comparison purposes
      // first set aside the transaction

      Callable<Void> callable =
         new CreateBranchDatabaseTxCallable(getLogger(), getSession(), getDatabaseService(), branchData);

      try {
         callable.call();

         Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
         int nextTransactionId = getDatabaseService().getSequence().getNextTransactionId();

         String creationComment = branchData.getCreationComment();

         getDatabaseService().runPreparedUpdate(connection, INSERT_TX_DETAILS, branchData.getUuid(), nextTransactionId,
            creationComment, timestamp, branchData.getUserArtifactId(), TransactionDetailsType.NonBaselined.getId());

         populateTransaction(0.30, connection, nextTransactionId, branchData.getParentBranchUuid(),
            branchData.getSavedTransaction().getGuid());

         UpdatePreviousTxCurrent updater =
            new UpdatePreviousTxCurrent(getDatabaseService(), connection, branchData.getUuid());
         updater.updateTxNotCurrentsFromTx(nextTransactionId);

      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return null;
   }

   private void populateTransaction(double workAmount, OseeConnection connection, int intoTx, Long parentBranch, int copyTxId) throws OseeCoreException {
      List<Object[]> data = new ArrayList<Object[]>();
      HashSet<Integer> gammas = new HashSet<Integer>(100000);
      long parentBranchId = RelationalConstants.BRANCH_SENTINEL;
      if (parentBranch != null) {
         parentBranchId = parentBranch;
      }

      populateAddressingToCopy(connection, data, intoTx, gammas, SELECT_ADDRESSING, parentBranchId, copyTxId);

      if (!data.isEmpty()) {
         getDatabaseService().runBatchUpdate(connection, INSERT_ADDRESSING, data);
      }

      checkForCancelled();
   }

   private void populateAddressingToCopy(OseeConnection connection, List<Object[]> data, int baseTxId, HashSet<Integer> gammas, String query, Object... parameters) throws OseeCoreException {
      IOseeStatement chStmt = getDatabaseService().getStatement(connection);
      try {
         chStmt.runPreparedQuery(MAX_FETCH, query, parameters);
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
