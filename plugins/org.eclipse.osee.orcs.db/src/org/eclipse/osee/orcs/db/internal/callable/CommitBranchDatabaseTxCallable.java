/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
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
import java.util.List;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeItemUtil;
import org.eclipse.osee.framework.core.model.change.ChangeVersion;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.accessor.UpdatePreviousTxCurrent;
import org.eclipse.osee.orcs.db.internal.change.LoadDeltasBetweenBranches;
import org.eclipse.osee.orcs.db.internal.change.MissingChangeItemFactory;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Ryan D. Brooks
 */
public class CommitBranchDatabaseTxCallable extends AbstractDatastoreTxCallable<TransactionId> {
   private static final String COMMIT_COMMENT = "Commit Branch ";

   private static final String INSERT_COMMIT_TRANSACTION =
      "insert into osee_tx_details(tx_type, branch_id, transaction_id, osee_comment, time, author, commit_art_id, build_id) values(?,?,?,?,?,?,?,?)";

   private static final String INSERT_COMMIT_ADDRESSING =
      "insert into osee_txs(transaction_id, branch_id, gamma_id, mod_type, tx_current, app_id) values(?,?,?,?,?,?)";

   private static final String UPDATE_CONFLICT_STATUS =
      "update osee_conflict SET status = ? WHERE status = ? AND merge_branch_id = ?";

   private static final String UPDATE_MERGE_COMMIT_TX =
      "update osee_merge set commit_transaction_id = ? Where source_branch_id = ? and dest_branch_id = ?";

   private static final String SELECT_SOURCE_BRANCH_STATE =
      "select (1) from osee_branch where branch_id=? and branch_state=?";

   private static final String UPDATE_SOURCE_BRANCH_STATE = "update osee_branch set branch_state=? where branch_id=?";

   private final IdentityManager idManager;
   private final ArtifactId committer;
   private final Branch sourceBranch;
   private final BranchId destinationBranch;
   private final BranchId mergeBranch;
   private final SqlJoinFactory joinFactory;
   private final TransactionToken sourceTx;
   private final TransactionToken destinationTx;
   private final QueryFactory queryFactory;
   private final MissingChangeItemFactory missingChangeItemFactory;

   public CommitBranchDatabaseTxCallable(IdentityManager idManager, ArtifactId committer, JdbcClient jdbcClient, SqlJoinFactory joinFactory, Branch sourceBranch, BranchId destinationBranch, TransactionToken sourceTx, TransactionToken destinationTx, BranchId mergeBranch, QueryFactory queryFactory, MissingChangeItemFactory missingChangeItemFactory) {
      super(null, null, jdbcClient);
      this.joinFactory = joinFactory;
      this.idManager = idManager;
      this.committer = committer;
      this.sourceBranch = sourceBranch;
      this.destinationBranch = destinationBranch;
      this.sourceTx = sourceTx;
      this.destinationTx = destinationTx;
      this.mergeBranch = mergeBranch;
      this.queryFactory = queryFactory;
      this.missingChangeItemFactory = missingChangeItemFactory;
   }

   @Override
   protected TransactionId handleTxWork(JdbcConnection connection) {
      List<ChangeItem> changes = new LoadDeltasBetweenBranches(getJdbcClient(), joinFactory, sourceBranch,
         destinationBranch, sourceTx, destinationTx, mergeBranch, queryFactory, missingChangeItemFactory).call();

      changes = ChangeItemUtil.computeNetChangesAndFilter(changes);

      BranchState storedBranchState;
      if (changes.isEmpty()) {
         throw new OseeStateException("A branch can not be committed without any changes made.");
      }
      storedBranchState = sourceBranch.getBranchState();
      checkPreconditions();

      TransactionId newTx = null;
      try {
         newTx = addCommitTransactionToDatabase(committer, connection);
         updatePreviousCurrentsOnDestinationBranch(connection, changes);
         insertCommitAddressing(newTx, connection, changes);

         getJdbcClient().runPreparedUpdate(connection, UPDATE_MERGE_COMMIT_TX, newTx, sourceBranch, destinationBranch);

         manageBranchStates();
         if (mergeBranch.isValid()) {
            getJdbcClient().runPreparedUpdate(UPDATE_CONFLICT_STATUS, ConflictStatus.COMMITTED.getValue(),
               ConflictStatus.RESOLVED.getValue(), mergeBranch);
         }
      } catch (OseeCoreException ex) {
         updateBranchState(storedBranchState, sourceBranch);
         throw ex;
      }
      return newTx;
   }

   public synchronized void checkPreconditions() {
      int count = getJdbcClient().fetch(0, SELECT_SOURCE_BRANCH_STATE, sourceBranch, BranchState.COMMIT_IN_PROGRESS);
      if (sourceBranch.getBranchState().isCommitInProgress() || sourceBranch.isArchived() || count > 0) {
         throw new OseeStateException("Commit completed or in progress for [%s]", sourceBranch);
      }

      if (!sourceBranch.getBranchState().equals(BranchState.COMMITTED)) {
         updateBranchState(BranchState.COMMIT_IN_PROGRESS, sourceBranch);
      }
   }

   private void updateBranchState(BranchState state, BranchId branchId) {
      getJdbcClient().runPreparedUpdate(UPDATE_SOURCE_BRANCH_STATE, state, branchId);
   }

   private void updatePreviousCurrentsOnDestinationBranch(JdbcConnection connection, List<ChangeItem> changes) {
      UpdatePreviousTxCurrent updater = new UpdatePreviousTxCurrent(getJdbcClient(), connection, destinationBranch);
      for (ChangeItem change : changes) {
         ChangeVersion destVersion = change.getDestinationVersion();
         if (destVersion.isValid()) {
            updater.addGamma(destVersion.getGammaId());
         }
      }
      updater.updateTxNotCurrents();
   }

   private TransactionId addCommitTransactionToDatabase(ArtifactId committer, JdbcConnection connection) {
      TransactionId newTransactionNumber = idManager.getNextTransactionId();

      Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
      String comment = COMMIT_COMMENT + sourceBranch.getName();

      getJdbcClient().runPreparedUpdate(connection, INSERT_COMMIT_TRANSACTION,
         TransactionDetailsType.NonBaselined.getId(), destinationBranch, newTransactionNumber, comment, timestamp,
         committer, sourceBranch.getAssociatedArtifact(), OseeCodeVersion.getVersionId());
      return newTransactionNumber;
   }

   private void insertCommitAddressing(TransactionId newTx, JdbcConnection connection, List<ChangeItem> changes) {
      List<Object[]> insertData = new ArrayList<>();
      for (ChangeItem change : changes) {
         ModificationType modType = change.getNetChange().getModType();
         ApplicabilityToken appToken = change.getNetChange().getApplicabilityToken();
         insertData.add(new Object[] {
            newTx,
            destinationBranch,
            change.getNetChange().getGammaId(),
            modType,
            TxChange.getCurrent(modType),
            appToken});
      }
      getJdbcClient().runBatchUpdate(connection, INSERT_COMMIT_ADDRESSING, insertData);
   }

   private void manageBranchStates() {
      updateBranchState(BranchState.MODIFIED, destinationBranch);

      BranchState sourceBranchState = sourceBranch.getBranchState();
      if (!sourceBranchState.isCreationInProgress() && !sourceBranchState.isRebaselined() && !sourceBranchState.isRebaselineInProgress() && !sourceBranchState.isCommitted()) {
         updateBranchState(BranchState.COMMITTED, sourceBranch);
      }
      if (mergeBranch.isValid()) {
         updateBranchState(BranchState.COMMITTED, mergeBranch);
      }
   }

}