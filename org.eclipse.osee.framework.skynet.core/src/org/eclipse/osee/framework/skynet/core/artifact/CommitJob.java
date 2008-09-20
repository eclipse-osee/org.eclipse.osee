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

import java.sql.ResultSet;
import java.sql.SQLException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.core.transaction.AbstractDbTxTemplate;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.exception.ConflictDetectionException;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionDetailsType;

/**
 * Commits gammaIds from a Source branch into a destination branch.
 * 
 * @author Jeff C. Phillips
 */
class CommitJob extends Job {
   //destination branch id, source branch id
   private static final String UPDATE_CURRENT_COMMIT_ATTRIBUTES =
         "UPDATE osee_define_txs tx2 set tx_current = 0 WHERE (tx2.transaction_id, tx2.gamma_id) in (SELECT tx1.transaction_id, tx1.gamma_id from osee_Define_txs tx1, osee_define_tx_details td2, osee_Define_attribute at3, osee_define_txs tx4, osee_Define_tx_details td5, osee_define_attribute at6 WHERE tx1.transaction_id = td2.transaction_id AND td2.branch_id = ? AND tx1.gamma_id = at3.gamma_id AND tx1.tx_current IN (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND td5.branch_id = ? AND tx4.transaction_id = td5.transaction_id AND td5.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND tx4.tx_current IN (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND tx4.gamma_id = at6.gamma_id AND at6.attr_id = at3.attr_id)";

   private static final String COMMIT_ATTRIBUTES =
         "INSERT INTO osee_define_txs(transaction_id, gamma_id, mod_type, tx_current) SELECT ?, tx1.gamma_id, tx1.mod_type, CASE WHEN tx1.mod_type = 3 THEN " + TxChange.DELETED.getValue() + " ELSE " + TxChange.CURRENT.getValue() + " END FROM osee_define_txs tx1, osee_define_tx_details td2, osee_define_attribute at3 WHERE tx1.tx_current IN (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND tx1.transaction_id = td2.transaction_id AND td2.branch_id = ? AND td2.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND tx1.gamma_id = at3.gamma_id";

   //destination branch id, source branch id
   private static final String UPDATE_CURRENT_COMMIT_RELATIONS =
         "UPDATE osee_define_txs tx2 set tx_current = 0 WHERE (tx2.transaction_id, tx2.gamma_id) in (SELECT tx1.transaction_id, tx1.gamma_id from osee_Define_txs tx1, osee_define_tx_details td2, osee_Define_rel_link rl3, osee_define_txs tx4, osee_Define_tx_details td5, osee_define_rel_link rl6 WHERE tx1.transaction_id = td2.transaction_id AND td2.branch_id = ? AND tx1.gamma_id = rl3.gamma_id AND tx1.tx_current IN (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND td5.branch_id = ? AND tx4.transaction_id = td5.transaction_id AND td5.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND tx4.tx_current IN (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND tx4.gamma_id = rl6.gamma_id AND rl6.rel_link_id = rl3.rel_link_id)";

   private static final String COMMIT_RELATIONS =
         "INSERT INTO osee_define_txs(transaction_id, gamma_id, mod_type, tx_current) SELECT ?, tx1.gamma_id, tx1.mod_type, CASE WHEN tx1.mod_type = 3 THEN " + TxChange.DELETED.getValue() + " ELSE " + TxChange.CURRENT.getValue() + " END FROM osee_define_txs tx1, osee_define_tx_details td2, osee_define_rel_link rl3 WHERE tx1.tx_current IN (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND tx1.transaction_id = td2.transaction_id AND td2.branch_id = ? AND td2.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND tx1.gamma_id = rl3.gamma_id";

   //destination branch id, source branch id
   private static final String UPDATE_CURRENT_COMMIT_ARTIFACTS =
         "UPDATE osee_define_txs tx2 set tx_current = 0 WHERE (tx2.transaction_id, tx2.gamma_id) in (SELECT tx1.transaction_id, tx1.gamma_id from osee_Define_txs tx1, osee_define_tx_details td2, osee_Define_artifact_version av3, osee_define_txs tx4, osee_Define_tx_details td5, osee_define_artifact_version av6 WHERE tx1.transaction_id = td2.transaction_id AND td2.branch_id = ? AND tx1.gamma_id = av3.gamma_id AND tx1.tx_current IN (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND td5.branch_id = ? AND tx4.transaction_id = td5.transaction_id AND td5.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND tx4.tx_current IN (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND tx4.gamma_id = av6.gamma_id AND av6.art_id = av3.art_id)";

   private static final String COMMIT_ARTIFACTS =
         "INSERT INTO osee_define_txs(transaction_id, gamma_id, mod_type, tx_current) SELECT ?, tx1.gamma_id, tx1.mod_type, CASE WHEN tx1.mod_type = 3 THEN " + TxChange.DELETED.getValue() + " ELSE " + TxChange.CURRENT.getValue() + " END FROM osee_define_txs tx1, osee_define_tx_details td2, osee_define_artifact_version av3 WHERE tx1.tx_current IN (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND tx1.transaction_id = td2.transaction_id AND td2.branch_id = ? AND td2.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND tx1.gamma_id = av3.gamma_id";

   private static final String UPDATE_MERGE_TRANSACTIONS =
         "UPDATE osee_define_txs set gamma_id = ?, mod_type = " + ModificationType.MERGED.getValue() + " Where transaction_id = ? and gamma_id = ?";

   private static final String UPDATE_MERGE_TRANSACTION_ID =
         "UPDATE osee_define_merge set transaction_id = ? Where source_branch_id = ? and dest_branch_id = ?";

   private static final String ARTIFACT_CHANGES =
         "SELECT av1.art_id, ? as branch_id FROM osee_Define_txs tx1, osee_define_artifact_version av1 WHERE tx1.transaction_id = ? AND tx1.gamma_id = av1.gamma_id UNION ALL SELECT ar1.art_id, ? as branch_id FROM osee_Define_txs tx1, osee_define_rel_link rl1, osee_define_artifact ar1 WHERE (rl1.a_art_id = ar1.art_id OR rl1.b_art_id = ar1.art_id) AND tx1.transaction_id = ? AND tx1.gamma_id = rl1.gamma_id";

   private static final String UPDATE_MODIFICATION_ID =
         "UPDATE osee_define_txs SET mod_type = " + ModificationType.NEW.getValue() + " WHERE mod_type = " + ModificationType.CHANGE.getValue() + " AND (transaction_id, gamma_id) in ((SELECT transaction_id, txs0.gamma_id FROM osee_define_txs txs0, osee_define_artifact_version ver0 where txs0.transaction_id = ? and txs0.gamma_id = ver0.gamma_id and ver0.art_id in (SELECT art_id FROM osee_define_tx_details det1, osee_define_txs txs1, osee_define_artifact_version ver1 WHERE det1.branch_id = ? AND det1.transaction_id = txs1.transaction_id AND det1.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND txs1.mod_type = " + ModificationType.NEW.getValue() + " AND txs1.gamma_id = ver1.gamma_id)) UNION (SELECT transaction_id, txs0.gamma_id FROM osee_define_txs txs0, osee_define_attribute ver0 where txs0.transaction_id = ? and txs0.gamma_id = ver0.gamma_id and ver0.attr_id in (SELECT attr_id FROM osee_define_tx_details det1, osee_define_txs txs1, osee_define_attribute ver1 WHERE det1.branch_id = ? AND det1.transaction_id = txs1.transaction_id AND det1.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND txs1.mod_type = " + ModificationType.NEW.getValue() + " AND txs1.gamma_id = ver1.gamma_id)) UNION (SELECT transaction_id, txs0.gamma_id FROM osee_define_txs txs0, osee_define_rel_link ver0 where txs0.transaction_id = ? and txs0.gamma_id = ver0.gamma_id and (ver0.a_art_id , ver0.b_art_id) in (SELECT a_art_id, b_art_id FROM osee_define_tx_details det1, osee_define_txs txs1, osee_define_rel_link ver1 WHERE det1.branch_id = ? AND det1.transaction_id = txs1.transaction_id AND det1.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND txs1.mod_type = " + ModificationType.NEW.getValue() + " AND txs1.gamma_id = ver1.gamma_id)))";

   private static final String REVERT_DELETED_NEW =
         "SELECT av1.art_id, td1.branch_id from osee_define_txs tx1, osee_define_txs tx2, osee_Define_tx_details td1, osee_Define_tx_details td2, osee_Define_artifact_version av1, osee_Define_artifact_version av2 WHERE td1.branch_id = ? AND td1.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND td1.transaction_id = tx1.transaction_id AND tx1.mod_type = " + ModificationType.NEW.getValue() + " AND tx1.gamma_id = av1.gamma_id AND td2.branch_id = ? AND td2.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND td2.transaction_id = tx2.transaction_id AND tx2.tx_current = " + TxChange.DELETED.getValue() + " AND tx2.gamma_id = av2.gamma_id AND av1.art_id = av2.art_id";

   private IProgressMonitor monitor;
   private final CommitDbTx commitDbTx;
   private final ConflictManagerExternal conflictManager;

   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core/debug/Commit"));
   private static final boolean MERGE_DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core/debug/Merge"));

   public CommitJob(Branch toBranch, Branch fromBranch, boolean archiveBranch, boolean forceCommit) throws OseeCoreException, SQLException {
      super("\nCommitting Branch: " + fromBranch.getBranchName());
      conflictManager = new ConflictManagerExternal(toBranch, fromBranch);

      if (DEBUG) {
         System.out.println(String.format("Commiting Branch %s into Branch %s", fromBranch.getBranchId(),
               toBranch.getBranchId()));
      }

      if (conflictManager.remainingConflictsExist() && !forceCommit) {
         if (DEBUG) {
            System.out.println(String.format("  FAILED: Found %d unresolved conflicts",
                  conflictManager.getRemainingConflicts().size()));
         }
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

      private CommitDbTx(Branch fromBranch, Branch toBranch, boolean archiveBranch, ConflictManagerExternal conflictManager) {
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

         long time = System.currentTimeMillis();
         long totalTime = time;
         int count = 0;
         //Load new and deleted artifact so that they can be compressed out of the commit transaction
         ResultSet resultSet =
               ConnectionHandler.runPreparedQuery(REVERT_DELETED_NEW, fromBranch.getBranchId(),
                     fromBranch.getBranchId()).getRset();
         while (resultSet.next()) {
            ArtifactPersistenceManager.getInstance().revertArtifact(resultSet.getInt("branch_id"),
                  resultSet.getInt("art_id"));
         }
         if (DEBUG) {
            System.out.println(String.format(
                  "   Reverted %d Artifacts in %s to avoid commiting new and deleted artifacts", count,
                  Lib.getElapseString(time)));
         }

         time = System.currentTimeMillis();
         if (fromBranch != null) {
            newTransactionNumber =
                  BranchPersistenceManager.addCommitTransactionToDatabase(toBranch, fromBranch, userToBlame);
            fromBranchId = fromBranch.getBranchId();
            AccessControlManager.getInstance().removeAllPermissionsFromBranch(fromBranch);
         } else {
            //Commit transaction instead of a branch
         }
         if (DEBUG) {
            System.out.println(String.format("   Added commit transaction [%d] into the DB in %s",
                  newTransactionNumber, Lib.getElapseString(time)));
         }

         monitor.worked(25);
         monitor.setTaskName("Commit transactions");

         time = System.currentTimeMillis();
         int insertCount =
               ConnectionHandler.runPreparedUpdateReturnCount(UPDATE_CURRENT_COMMIT_ATTRIBUTES, toBranch.getBranchId(),
                     fromBranchId);
         if (DEBUG) {
            count = insertCount;
            System.out.println(String.format(
                  "   Updated %d TX_Current values on Destination Branch for Attributes in %s", count,
                  Lib.getElapseString(time)));
         }

         time = System.currentTimeMillis();
         insertCount +=
               ConnectionHandler.runPreparedUpdateReturnCount(COMMIT_ATTRIBUTES, newTransactionNumber, fromBranchId);
         if (DEBUG) {
            System.out.println(String.format("   Commited %d Attributes in %s", insertCount - count,
                  Lib.getElapseString(time)));
            count = insertCount;
         }

         time = System.currentTimeMillis();
         insertCount +=
               ConnectionHandler.runPreparedUpdateReturnCount(UPDATE_CURRENT_COMMIT_ARTIFACTS, toBranch.getBranchId(),
                     fromBranchId);
         if (DEBUG) {
            System.out.println(String.format(
                  "   Updated %d TX_Current values on Destination Branch for Artifacts in %s", insertCount - count,
                  Lib.getElapseString(time)));
            count = insertCount;
         }

         time = System.currentTimeMillis();
         insertCount +=
               ConnectionHandler.runPreparedUpdateReturnCount(COMMIT_ARTIFACTS, newTransactionNumber, fromBranchId);
         if (DEBUG) {
            System.out.println(String.format("   Commited %d Artifacts in %s", insertCount - count,
                  Lib.getElapseString(time)));
            count = insertCount;
         }

         time = System.currentTimeMillis();
         insertCount +=
               ConnectionHandler.runPreparedUpdateReturnCount(UPDATE_CURRENT_COMMIT_RELATIONS, toBranch.getBranchId(),
                     fromBranchId);
         if (DEBUG) {
            System.out.println(String.format(
                  "   Updated %d TX_Current values on Destination Branch for Relations in %s", insertCount - count,
                  Lib.getElapseString(time)));
            count = insertCount;
         }

         time = System.currentTimeMillis();
         insertCount +=
               ConnectionHandler.runPreparedUpdateReturnCount(COMMIT_RELATIONS, newTransactionNumber, fromBranchId);
         if (DEBUG) {
            System.out.println(String.format("   Commited %d Relations in %s", insertCount - count,
                  Lib.getElapseString(time)));
            count = insertCount;
         }

         //Change all modifications on artifacts/relation/attributes that are modified but should be new, because both new'd 
         //and modified on the same branch.
         time = System.currentTimeMillis();
         ConnectionHandler.runPreparedUpdate(UPDATE_MODIFICATION_ID, newTransactionNumber, fromBranchId,
               newTransactionNumber, fromBranchId, newTransactionNumber, fromBranchId);
         if (DEBUG) {
            System.out.println(String.format("   Updated modification types for new and modified to modified in %s",
                  Lib.getElapseString(time)));
         }

         //add in all merge branch changes over any other source branch changes.
         time = System.currentTimeMillis();
         if (conflictManager.originalConflictsExist()) {
            count = 0;
            for (Conflict conflict : conflictManager.getOriginalConflicts()) {
               if (conflict.statusResolved()) {
                  count++;
                  if (MERGE_DEBUG) {
                     System.out.println(String.format(
                           "     Using Merge value for Artifact %d item %s, setting gamma id to %d where it was %d",
                           conflict.getArtifact().getArtId(), conflict.getChangeItem(), conflict.getMergeGammaId(),
                           conflict.getSourceGamma()));
                  }
                  ConnectionHandler.runPreparedUpdateReturnCount(UPDATE_MERGE_TRANSACTIONS, conflict.getMergeGammaId(),
                        newTransactionNumber, conflict.getSourceGamma());
                  conflict.setStatus(Conflict.Status.COMMITTED);
               }
            }
            if (DEBUG) {
               System.out.println(String.format("    Added %d Merge Values in %s", count, Lib.getElapseString(time)));
            }

            time = System.currentTimeMillis();
            //insert transaction id into the branch table
            ConnectionHandler.runPreparedUpdateReturnCount(UPDATE_MERGE_TRANSACTION_ID, newTransactionNumber,
                  fromBranch.getBranchId(), toBranch.getBranchId());
            if (DEBUG) {
               System.out.println(String.format("   Updated the Merge Transaction Id in the conflict table in %s",
                     Lib.getElapseString(time)));
            }
         }

         time = System.currentTimeMillis();
         if (insertCount > 0) {
            Object[] dataList =
                  new Object[] {toBranch.getBranchId(), newTransactionNumber, toBranch.getBranchId(),
                        newTransactionNumber};
            // reload the committed artifacts since the commit changed them on the destination branch
            ArtifactLoader.getArtifacts(ARTIFACT_CHANGES, dataList, 400, ArtifactLoad.FULL, true, null, null, true);
            if (DEBUG) {
               System.out.println(String.format("   Reloaded the Artifacts after the commit in %s",
                     Lib.getElapseString(time)));
            }

            if (archiveBranch) {
               fromBranch.archive();
            }

         } else {
            throw new IllegalStateException(" A branch can not be commited without any changes made.");
         }

         if (DEBUG) {
            System.out.println(String.format("Commit Completed in %s", Lib.getElapseString(totalTime)));
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
            OseeEventManager.kickBranchEvent(this, BranchEventType.Committed, fromBranchId);
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
   }
}