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
package org.eclipse.osee.framework.branch.management.commit;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.internal.InternalBranchActivator;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.cache.TransactionCache;
import org.eclipse.osee.framework.core.data.ArtifactChangeItem;
import org.eclipse.osee.framework.core.data.AttributeChangeItem;
import org.eclipse.osee.framework.core.data.BranchCommitResponse;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.RelationChangeItem;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Ryan D. Brooks
 */
public class CommitDbOperation extends AbstractDbTxOperation {
   private static final String COMMIT_COMMENT = "Commit Branch ";

   private static final String INSERT_COMMIT_TRANSACTION =
         "insert into osee_tx_details(tx_type, branch_id, transaction_id, osee_comment, time, author, commit_art_id) values(?,?,?,?,?,?,?)";

   private static final String INSERT_COMMIT_ADDRESSING =
         "insert into osee_txs(transaction_id, gamma_id, mod_type, tx_current) values(?,?,?,?)";

   private static final String UPDATE_CONFLICT_STATUS =
         "update osee_conflict SET status = ? WHERE status = ? AND merge_branch_id = ?";

   private static final String UPDATE_MERGE_COMMIT_TX =
         "update osee_merge set commit_transaction_id = ? Where source_branch_id = ? and dest_branch_id = ?";

   private static final String SELECT_SOURCE_BRANCH_STATE =
         "select (1) from osee_branch where branch_id=? and branch_state=?";

   private static final String UPDATE_SOURCE_BRANCH_STATE = "update osee_branch set branch_state=? where branch_id=?";

   private final int userArtId;
   private final BranchCache branchCache;
   private final TransactionCache transactionCache;
   private final Map<Branch, BranchState> savedBranchStates;
   private final Branch sourceBranch;
   private final Branch destinationBranch;
   private final Branch mergeBranch;
   private final List<ChangeItem> changes;
   private final BranchCommitResponse txHolder;
   private final IOseeModelFactoryServiceProvider modelFactory;

   private OseeConnection connection;
   private boolean success;

   public CommitDbOperation(IOseeDatabaseServiceProvider databaseProvider, BranchCache branchCache, TransactionCache transactionCache, int userArtId, Branch sourceBranch, Branch destinationBranch, Branch mergeBranch, List<ChangeItem> changes, BranchCommitResponse txHolder, IOseeModelFactoryServiceProvider modelFactory) {
      super(databaseProvider, "Commit Database Operation", InternalBranchActivator.PLUGIN_ID);
      this.savedBranchStates = new HashMap<Branch, BranchState>();
      this.branchCache = branchCache;
      this.transactionCache = transactionCache;
      this.userArtId = userArtId;
      this.sourceBranch = sourceBranch;
      this.destinationBranch = destinationBranch;
      this.mergeBranch = mergeBranch;
      this.changes = changes;
      this.txHolder = txHolder;
      this.modelFactory = modelFactory;

      this.success = true;
      savedBranchStates.put(sourceBranch, sourceBranch.getBranchState());
      savedBranchStates.put(destinationBranch, destinationBranch.getBranchState());
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
      this.connection = connection;
      if (changes.isEmpty()) {
         throw new OseeStateException(" A branch can not be commited without any changes made.");
      }
      checkPreconditions();
      updateBranchState();
      txHolder.setTransaction(addCommitTransactionToDatabase(userArtId));

      //      TODO AccessControlManager.removeAllPermissionsFromBranch(connection, sourceBranch);

      updatePreviousCurrentsOnDestinationBranch();

      insertCommitAddressing();

      updateMergeBranchCommitTx();

      manageBranchStates();
   }

   private void updateMergeBranchCommitTx() throws OseeDataStoreException {
      getDatabaseService().runPreparedUpdate(connection, UPDATE_MERGE_COMMIT_TX, txHolder.getTransaction().getId(),
            sourceBranch.getId(), destinationBranch.getId());
   }

   public void checkPreconditions() throws OseeCoreException {
      int count =
            getDatabaseService().runPreparedQueryFetchObject(0, SELECT_SOURCE_BRANCH_STATE, sourceBranch.getId(),
                  BranchState.COMMIT_IN_PROGRESS.getValue());
      if (count > 0) {
         throw new OseeStateException(String.format("Commit already in progress for [%s]", sourceBranch));
      }
   }

   public void updateBranchState() throws OseeCoreException {
      getDatabaseService().runPreparedUpdate(UPDATE_SOURCE_BRANCH_STATE, BranchState.COMMIT_IN_PROGRESS.getValue(),
            sourceBranch.getId());
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
   private TransactionRecord addCommitTransactionToDatabase(int userArtId) throws OseeCoreException {
      int newTransactionNumber = getDatabaseService().getSequence().getNextTransactionId();

      Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
      String comment = COMMIT_COMMENT + sourceBranch.getName();

      getDatabaseService().runPreparedUpdate(connection, INSERT_COMMIT_TRANSACTION,
            TransactionDetailsType.NonBaselined.getId(), destinationBranch.getId(), newTransactionNumber, comment,
            timestamp, userArtId, sourceBranch.getAssociatedArtifact().getArtId());
      //      transactioCache;
      TransactionRecord record =
            modelFactory.getOseeFactoryService().getTransactionFactory().create(newTransactionNumber,
                  destinationBranch.getId(), comment, new Date(), userArtId,
                  sourceBranch.getAssociatedArtifact().getArtId(), TransactionDetailsType.NonBaselined);
      return record;
   }

   private void insertCommitAddressing() throws OseeDataStoreException {
      List<Object[]> insertData = new ArrayList<Object[]>();
      for (ChangeItem change : changes) {
         ModificationType modType = change.getNetChange().getModType();
         insertData.add(new Object[] {txHolder.getTransaction().getId(), change.getNetChange().getGammaId(),
               modType.getValue(), TxChange.getCurrent(modType).getValue()});
      }
      getDatabaseService().runBatchUpdate(connection, INSERT_COMMIT_ADDRESSING, insertData);
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
         branchCache.storeItems(mergeBranch, destinationBranch, sourceBranch);
      } else {
         branchCache.storeItems(destinationBranch, sourceBranch);
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
         branchCache.storeItems(savedBranchStates.keySet());
      } catch (OseeCoreException ex1) {
         OseeLog.log(InternalBranchActivator.class, Level.SEVERE, ex1);
      }
   }

   @Override
   protected void handleTxFinally(IProgressMonitor monitor) throws OseeCoreException {
      if (success) {
         // update conflict status, if necessary
         if (mergeBranch != null) {
            getDatabaseService().runPreparedUpdate(connection, UPDATE_CONFLICT_STATUS,
                  ConflictStatus.COMMITTED.getValue(), ConflictStatus.RESOLVED.getValue(), mergeBranch.getId());
         }
      }
   }
}