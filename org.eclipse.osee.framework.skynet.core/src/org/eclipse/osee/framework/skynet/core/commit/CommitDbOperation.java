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
package org.eclipse.osee.framework.skynet.core.commit;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.SequenceManager;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Ryan D. Brooks
 */
public class CommitDbOperation extends AbstractDbTxOperation {
   private static final String INSERT_COMMIT_TRANSACTION =
         "insert into osee_tx_details(tx_type, branch_id, transaction_id, osee_comment, time, author, commit_art_id) values(?,?,?,?,?,?,?)";

   private static final String ARTIFACT_CHANGES =
         "SELECT av1.art_id, ? as branch_id FROM osee_txs tx1, osee_artifact_version av1 WHERE tx1.transaction_id = ? AND tx1.gamma_id = av1.gamma_id UNION ALL SELECT ar1.art_id, ? as branch_id FROM osee_txs tx1, osee_relation_link rl1, osee_artifact ar1 WHERE (rl1.a_art_id = ar1.art_id OR rl1.b_art_id = ar1.art_id) AND tx1.transaction_id = ? AND tx1.gamma_id = rl1.gamma_id";

   private static final String INSERT_COMMIT_ADDRESSING =
         "insert into osee_txs(transaction_id, gamma_id, mod_type, tx_current) values(?,?,?,?)";

   private static final String UPDATE_CONFLICT_STATUS =
         "UPDATE osee_conflict SET status = ? WHERE status = ? AND merge_branch_id = ?";

   private static final String UPDATE_MERGE_COMMIT_TX =
         "UPDATE osee_merge set commit_transaction_id = ? Where source_branch_id = ? and dest_branch_id = ?";

   private boolean success = true;
   private final Map<Branch, BranchState> savedBranchStates = new HashMap<Branch, BranchState>();
   private final Branch sourceBranch;
   private final Branch destinationBranch;
   private final Branch mergeBranch;
   private final List<ChangeItem> changes;
   private Integer newTransactionNumber;
   private OseeConnection connection;

   public CommitDbOperation(Branch sourceBranch, Branch destinationBranch, Branch mergeBranch, List<ChangeItem> changes) {
      super("Commit Database Operation", Activator.PLUGIN_ID);
      this.sourceBranch = sourceBranch;
      this.destinationBranch = destinationBranch;
      this.mergeBranch = mergeBranch;
      this.changes = changes;

      savedBranchStates.put(sourceBranch, sourceBranch.getBranchState());
      savedBranchStates.put(destinationBranch, destinationBranch.getBranchState());
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
      this.connection = connection;
      if (changes.isEmpty()) {
         throw new OseeStateException(" A branch can not be commited without any changes made.");
      }
      newTransactionNumber = addCommitTransactionToDatabase(UserManager.getUser());

      AccessControlManager.removeAllPermissionsFromBranch(connection, sourceBranch);

      updatePreviousCurrentsOnDestinationBranch();

      insertCommitAddressing();

      updateMergeBranchCommitTx();

      manageBranchStates();
   }

   private void updateMergeBranchCommitTx() throws OseeDataStoreException {
      ConnectionHandler.runPreparedUpdate(connection, UPDATE_MERGE_COMMIT_TX, newTransactionNumber,
            sourceBranch.getBranchId(), destinationBranch.getBranchId());
   }

   private void updatePreviousCurrentsOnDestinationBranch() throws OseeStateException, OseeDataStoreException {
      UpdatePreviousTxCurrent updater = new UpdatePreviousTxCurrent(destinationBranch, connection);
      for (ChangeItem change : changes) {
         if (change instanceof ArtifactChangeItem) {
            updater.addArtifact(change.getItemId());
         } else if (change instanceof AttributeChangeItem) {
            updater.addAttribute(change.getItemId());
         } else if (change instanceof RelationChangeItem) {
            updater.addRelation(change.getItemId());
         } else {
            throw new OseeStateException("Unexpected change type");
         }
      }
      updater.updateTxNotCurrents();
   }

   @SuppressWarnings("unchecked")
   private int addCommitTransactionToDatabase(User userToBlame) throws OseeCoreException {
      int newTransactionNumber = SequenceManager.getNextTransactionId();

      Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
      String comment = BranchManager.COMMIT_COMMENT + sourceBranch.getName();
      int authorId = userToBlame == null ? -1 : userToBlame.getArtId();
      ConnectionHandler.runPreparedUpdate(connection, INSERT_COMMIT_TRANSACTION,
            TransactionDetailsType.NonBaselined.getId(), destinationBranch.getBranchId(), newTransactionNumber,
            comment, timestamp, authorId, sourceBranch.getAssociatedArtifact().getArtId());

      return newTransactionNumber;
   }

   private void insertCommitAddressing() throws OseeDataStoreException {
      List<Object[]> insertData = new ArrayList<Object[]>();
      for (ChangeItem change : changes) {
         ModificationType modType = change.getNet().getModType();
         insertData.add(new Object[] {newTransactionNumber, change.getNet().getGammaId(), modType.getValue(),
               TxChange.getCurrent(modType).getValue()});
      }
      ConnectionHandler.runBatchUpdate(connection, INSERT_COMMIT_ADDRESSING, insertData);
   }

   private void manageBranchStates() throws OseeCoreException {
      destinationBranch.setBranchState(BranchState.MODIFIED);
      BranchState sourceBranchState = sourceBranch.getBranchState();
      if (!sourceBranchState.isCreationInProgress() && !sourceBranchState.isRebaselined() && !sourceBranchState.isRebaselineInProgress() && !sourceBranchState.isCommitted()) {
         sourceBranch.setBranchState(BranchState.COMMITTED);
      }
      if (mergeBranch != null) {
         savedBranchStates.put(mergeBranch, mergeBranch.getBranchState());
         mergeBranch.setBranchState(BranchState.COMMITTED);
         BranchManager.persist(mergeBranch, destinationBranch, sourceBranch);
      } else {
         BranchManager.persist(destinationBranch, sourceBranch);
      }
   }

   @Override
   protected void handleTxException(IProgressMonitor monitor, Exception ex) {
      success = false;
      // Restore Original Branch States
      try {
         for (Entry<Branch, BranchState> entry : savedBranchStates.entrySet()) {
            entry.getKey().setBranchState(entry.getValue());
         }
         BranchManager.persist(savedBranchStates.keySet());
      } catch (OseeCoreException ex1) {
         OseeLog.log(Activator.class, Level.SEVERE, ex1);
      }
   }

   @Override
   protected void handleTxFinally(IProgressMonitor monitor) throws OseeCoreException {
      if (success) {
         // Update commit artifact cache with new information
         if (sourceBranch.getAssociatedArtifact().getArtId() > 0) {
            TransactionIdManager.cacheCommittedArtifactTransaction(sourceBranch.getAssociatedArtifact(),
                  TransactionIdManager.getTransactionId(newTransactionNumber));
         }

         Object[] queryData =
               new Object[] {destinationBranch.getBranchId(), newTransactionNumber, destinationBranch.getBranchId(),
                     newTransactionNumber};
         // reload the committed artifacts since the commit changed them on the destination branch
         ArtifactLoader.getArtifacts(ARTIFACT_CHANGES, queryData, 400, ArtifactLoad.FULL, true, null, true);

         // update conflict status, if necessary
         if (mergeBranch != null) {
            ConnectionHandler.runPreparedUpdate(connection, UPDATE_CONFLICT_STATUS,
                  ConflictStatus.COMMITTED.getValue(), ConflictStatus.RESOLVED.getValue(), mergeBranch.getBranchId());
         }

         OseeEventManager.kickBranchEvent(this, BranchEventType.Committed, sourceBranch.getBranchId());
      }
   }
}