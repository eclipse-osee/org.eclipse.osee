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

package org.eclipse.osee.framework.skynet.core.artifact;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.core.transaction.AbstractDbTxTemplate;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.messaging.event.skynet.NetworkCommitBranchEvent;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManager;
import org.eclipse.osee.framework.skynet.core.event.LocalCommitBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.exception.ConflictDetectionException;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionDetailsType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * Commits gammaIds from a Source branch into a destination branch.
 * 
 * @author Jeff C. Phillips
 */
class CommitJob extends Job {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(CommitJob.class);

   private static final String DELETE_TO_BRANCH_TAG_DATA =
         "DELETE FROM osee_tag_art_map tam1 WHERE tam1.branch_id = ? AND EXISTS (SELECT 'x' FROM osee_tag_art_map tam2 WHERE tam1.art_id = tam2.art_id AND tam1.tag_id = tam2.tag_id AND branch_id = ?)";
   private static final String MOVE_TAG_DATA = "UPDATE osee_tag_art_map SET branch_id = ? WHERE branch_id = ?";

   //destination branch id, source branch id
   private static final String UPDATE_CURRENT_COMMIT_ATTRIBUTES =
         "UPDATE osee_Define_txs tx2 set tx_current = 0 WHERE (tx2.transaction_id, tx2.gamma_id) in (SELECT tx1.transaction_id, tx1.gamma_id from osee_Define_txs tx1, osee_define_tx_details td2, osee_Define_attribute at3, osee_define_txs tx4, osee_Define_tx_details td5, osee_define_attribute at6 WHERE tx1.transaction_id = td2.transaction_id AND td2.branch_id = ? AND tx1.gamma_id = at3.gamma_id AND tx1.tx_current IN (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND td5.branch_id = ? AND tx4.transaction_id = td5.transaction_id AND td5.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND tx4.tx_current IN (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND tx4.gamma_id = at6.gamma_id AND at6.attr_id = at3.attr_id)";

   private static final String COMMIT_ATTRIBUTES =
         "INSERT INTO OSEE_DEFINE_TXS(transaction_id, gamma_id, mod_type, tx_current) SELECT ?, tx1.gamma_id, tx1.mod_type, CASE WHEN tx1.mod_type = 3 THEN " + TxChange.DELETED.getValue() + " ELSE " + TxChange.CURRENT.getValue() + " END FROM osee_define_txs tx1, osee_define_tx_details td2, osee_define_attribute at3 WHERE tx1.tx_current IN (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND tx1.transaction_id = td2.transaction_id AND td2.branch_id = ? AND td2.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND tx1.gamma_id = at3.gamma_id";

   //destination branch id, source branch id
   private static final String UPDATE_CURRENT_COMMIT_RELATIONS =
         "UPDATE osee_Define_txs tx2 set tx_current = 0 WHERE (tx2.transaction_id, tx2.gamma_id) in (SELECT tx1.transaction_id, tx1.gamma_id from osee_Define_txs tx1, osee_define_tx_details td2, osee_Define_rel_link rl3, osee_define_txs tx4, osee_Define_tx_details td5, osee_define_rel_link rl6 WHERE tx1.transaction_id = td2.transaction_id AND td2.branch_id = ? AND tx1.gamma_id = rl3.gamma_id AND tx1.tx_current IN (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND td5.branch_id = ? AND tx4.transaction_id = td5.transaction_id AND td5.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND tx4.tx_current IN (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND tx4.gamma_id = rl6.gamma_id AND rl6.rel_link_id = rl3.rel_link_id)";

   private static final String COMMIT_RELATIONS =
         "INSERT INTO OSEE_DEFINE_TXS(transaction_id, gamma_id, mod_type, tx_current) SELECT ?, tx1.gamma_id, tx1.mod_type, CASE WHEN tx1.mod_type = 3 THEN " + TxChange.DELETED.getValue() + " ELSE " + TxChange.CURRENT.getValue() + " END FROM osee_define_txs tx1, osee_define_tx_details td2, osee_define_rel_link rl3 WHERE tx1.tx_current IN (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND tx1.transaction_id = td2.transaction_id AND td2.branch_id = ? AND td2.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND tx1.gamma_id = rl3.gamma_id";

   //destination branch id, source branch id
   private static final String UPDATE_CURRENT_COMMIT_ARTIFACTS =
         "UPDATE osee_Define_txs tx2 set tx_current = 0 WHERE (tx2.transaction_id, tx2.gamma_id) in (SELECT tx1.transaction_id, tx1.gamma_id from osee_Define_txs tx1, osee_define_tx_details td2, osee_Define_artifact_version av3, osee_define_txs tx4, osee_Define_tx_details td5, osee_define_artifact_version av6 WHERE tx1.transaction_id = td2.transaction_id AND td2.branch_id = ? AND tx1.gamma_id = av3.gamma_id AND tx1.tx_current IN (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND td5.branch_id = ? AND tx4.transaction_id = td5.transaction_id AND td5.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND tx4.tx_current IN (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND tx4.gamma_id = av6.gamma_id AND av6.art_id = av3.art_id)";

   private static final String COMMIT_ARTIFACTS =
         "INSERT INTO OSEE_DEFINE_TXS(transaction_id, gamma_id, mod_type, tx_current) SELECT ?, tx1.gamma_id, tx1.mod_type, CASE WHEN tx1.mod_type = 3 THEN " + TxChange.DELETED.getValue() + " ELSE " + TxChange.CURRENT.getValue() + " END FROM osee_define_txs tx1, osee_define_tx_details td2, osee_define_artifact_version av3 WHERE tx1.tx_current IN (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND tx1.transaction_id = td2.transaction_id AND td2.branch_id = ? AND td2.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND tx1.gamma_id = av3.gamma_id";

   private static final String UPDATE_MERGE_TRANSACTIONS =
         "UPDATE osee_Define_txs set gamma_id = ?, mod_type = " + ModificationType.MERGED.getValue() + " Where transaction_id = ? and gamma_id = ?";

   private static final String ARTIFACT_CHANGES =
         "SELECT av1.art_id, ? as branch_id FROM osee_Define_txs tx1, osee_define_artifact_version av1 WHERE tx1.transaction_id = ? AND tx1.gamma_id = av1.gamma_id UNION ALL SELECT ar1.art_id, ? as branch_id FROM osee_Define_txs tx1, osee_define_rel_link rl1, osee_define_artifact ar1 WHERE (rl1.a_art_id = ar1.art_id OR rl1.b_art_id = ar1.art_id) AND tx1.transaction_id = ? AND tx1.gamma_id = rl1.gamma_id";

   private IProgressMonitor monitor;
   private CommitDbTx commitDbTx;
   private ConflictManager conflictManager;

   public CommitJob(Branch toBranch, Branch fromBranch, boolean archiveBranch, boolean forceCommit) throws OseeCoreException, SQLException {
      super("Committing Branch: " + fromBranch.getBranchName());
      conflictManager = new ConflictManager(toBranch, fromBranch);

      if (conflictManager.remainingConflictsExist()) {
         throw new ConflictDetectionException(
               "Trying to commit " + fromBranch.getBranchName() + " into " + toBranch.getBranchName() + " when " + conflictManager.getRemainingConflicts().size() + " conflicts still exist");
      }
      commitDbTx = new CommitDbTx(fromBranch, toBranch, archiveBranch, conflictManager);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected IStatus run(IProgressMonitor monitor) {
      this.monitor = monitor;

      IStatus toReturn = Status.CANCEL_STATUS;
      try {
         commitDbTx.execute();
         toReturn = Status.OK_STATUS;
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         toReturn = new Status(Status.ERROR, SkynetActivator.PLUGIN_ID, Status.OK, ex.getLocalizedMessage(), ex);
      }
      return toReturn;
   }

   private final class CommitDbTx extends AbstractDbTxTemplate {
      private int newTransactionNumber;
      private final Branch toBranch;
      private final Branch fromBranch;
      private final boolean archiveBranch;
      private boolean success = true;
      private int fromBranchId = -1;

      private CommitDbTx(Branch fromBranch, Branch toBranch, boolean archiveBranch, ConflictManager conflictManager) {
         super();
         this.toBranch = toBranch;
         this.fromBranch = fromBranch;
         this.newTransactionNumber = -1;
         this.archiveBranch = archiveBranch;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.ui.plugin.util.db.AbstractDbTxTemplate#handleTxWork()
       */
      @Override
      protected void handleTxWork() throws OseeCoreException, SQLException {
         monitor.beginTask("Acquire from branch transactions", 100);

         User userToBlame = SkynetAuthentication.getUser();

         if (fromBranch != null) {
            newTransactionNumber =
                  BranchPersistenceManager.getInstance().addCommitTransactionToDatabase(toBranch, fromBranch,
                        userToBlame);
            fromBranchId = fromBranch.getBranchId();
            AccessControlManager.getInstance().removeAllPermissionsFromBranch(fromBranch);
         } else {
            //Commit transaction instead of a branch
         }

         monitor.worked(25);
         monitor.setTaskName("Commit transactions");

         int insertCount =
               ConnectionHandler.runPreparedUpdateReturnCount(UPDATE_CURRENT_COMMIT_ATTRIBUTES, SQL3DataType.INTEGER,
                     toBranch.getBranchId(), SQL3DataType.INTEGER, fromBranchId);

         insertCount +=
               ConnectionHandler.runPreparedUpdateReturnCount(COMMIT_ATTRIBUTES, SQL3DataType.INTEGER,
                     newTransactionNumber, SQL3DataType.INTEGER, fromBranchId);

         insertCount +=
               ConnectionHandler.runPreparedUpdateReturnCount(UPDATE_CURRENT_COMMIT_ARTIFACTS, SQL3DataType.INTEGER,
                     toBranch.getBranchId(), SQL3DataType.INTEGER, fromBranchId);

         insertCount +=
               ConnectionHandler.runPreparedUpdateReturnCount(COMMIT_ARTIFACTS, SQL3DataType.INTEGER,
                     newTransactionNumber, SQL3DataType.INTEGER, fromBranchId);

         insertCount +=
               ConnectionHandler.runPreparedUpdateReturnCount(UPDATE_CURRENT_COMMIT_RELATIONS, SQL3DataType.INTEGER,
                     toBranch.getBranchId(), SQL3DataType.INTEGER, fromBranchId);

         insertCount +=
               ConnectionHandler.runPreparedUpdateReturnCount(COMMIT_RELATIONS, SQL3DataType.INTEGER,
                     newTransactionNumber, SQL3DataType.INTEGER, fromBranchId);

         //add in all merge branch changes over any other source branch changes.
         if (conflictManager.originalConflictsExist()) {
            for (Conflict conflict : conflictManager.getOriginalConflicts()) {
               ConnectionHandler.runPreparedUpdateReturnCount(UPDATE_MERGE_TRANSACTIONS, SQL3DataType.INTEGER,
                     conflict.getMergeGammaId(), SQL3DataType.INTEGER, newTransactionNumber, SQL3DataType.INTEGER,
                     conflict.getSourceGamma());
            }

         }

         if (insertCount > 0) {
            Object[] dataList =
                  new Object[] {SQL3DataType.INTEGER, toBranch.getBranchId(), SQL3DataType.INTEGER,
                        newTransactionNumber, SQL3DataType.INTEGER, toBranch.getBranchId(), SQL3DataType.INTEGER,
                        newTransactionNumber};
            ArtifactLoader.getArtifacts(ARTIFACT_CHANGES, dataList, 400, ArtifactLoad.FULL, true);

            TransactionIdManager.getInstance().resetEditableTransactionId(newTransactionNumber, toBranch);
            tagArtifacts(toBranch, fromBranchId, monitor);

            if (archiveBranch) {
               fromBranch.archive();
            }

         } else {
            throw new IllegalStateException(" A branch can not be commited without any changes made.");
         }
         success = true;
         monitor.done();
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.plugin.util.db.AbstractDbTxTemplate#handleTxFinally()
       */
      @Override
      protected void handleTxFinally() throws Exception {
         super.handleTxFinally();
         if (success) {
            SkynetEventManager.getInstance().kick(new LocalCommitBranchEvent(this, fromBranchId));
            RemoteEventManager.kick(new NetworkCommitBranchEvent(fromBranchId,
                  SkynetAuthentication.getUser().getArtId()));
         }
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.ui.plugin.util.db.AbstractDbTxTemplate#handleTxException(java.lang.Exception)
       */
      @Override
      protected void handleTxException(Exception ex) throws Exception {
         super.handleTxException(ex);
         success = false;
      }

      private void tagArtifacts(Branch toBranch, int fromBranchId, IProgressMonitor progressMonitor) throws SQLException {
         progressMonitor.worked(15);
         progressMonitor.setTaskName("Tagging artifacts");

         //Delete toBranch artifact tags
         ConnectionHandler.runPreparedUpdate(DELETE_TO_BRANCH_TAG_DATA, SQL3DataType.INTEGER, toBranch.getBranchId(),
               SQL3DataType.INTEGER, fromBranchId);
         //move artifact tags from fromBranch to toBranch
         ConnectionHandler.runPreparedUpdate(MOVE_TAG_DATA, SQL3DataType.INTEGER, toBranch.getBranchId(),
               SQL3DataType.INTEGER, fromBranchId);
      }
   }
}