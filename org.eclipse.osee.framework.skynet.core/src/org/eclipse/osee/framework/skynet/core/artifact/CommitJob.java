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
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.messaging.event.skynet.NetworkCommitBranchEvent;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.event.LocalCommitBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.remoteEvent.RemoteEventManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionType;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.AbstractDbTxTemplate;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;

/**
 * Commits gammaIds from a Source branch into a destination branch.
 * 
 * @author Jeff C. Phillips
 */
class CommitJob extends Job {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(CommitJob.class);
//   private static final LocalAliasTable VERSION_ALIAS_1 = ARTIFACT_VERSION_TABLE.aliasAs("art_ver_1");
//   private static final LocalAliasTable VERSION_ALIAS_2 = ARTIFACT_VERSION_TABLE.aliasAs("art_ver_2");
//   private static final LocalAliasTable VERSION_ALIAS_3 = ARTIFACT_VERSION_TABLE.aliasAs("art_ver_3");
//   private static final LocalAliasTable TX_ALIAS_1 = TRANSACTIONS_TABLE.aliasAs("tx_1");
//   private static final LocalAliasTable TX_ALIAS_2 = TRANSACTIONS_TABLE.aliasAs("tx_2");
//   private static final LocalAliasTable TX_ALIAS_3 = TRANSACTIONS_TABLE.aliasAs("tx_3");
//   private static final LocalAliasTable TX_DETAILS_ALIAS_1 = TRANSACTION_DETAIL_TABLE.aliasAs("txd_1");
//   private static final LocalAliasTable TX_DETAILS_ALIAS_2 = TRANSACTION_DETAIL_TABLE.aliasAs("txd_2");
//   private static final LocalAliasTable TX_DETAILS_ALIAS_3 = TRANSACTION_DETAIL_TABLE.aliasAs("txd_3");
//   private static final String BRANCH_COMMIT =
//         "INSERT INTO " + TRANSACTIONS_TABLE + "(transaction_id, gamma_id, tx_type) SELECT ?, " + TX_ALIAS_3.column("gamma_id") + ", ? FROM " + TX_ALIAS_3 + ", " + TX_DETAILS_ALIAS_3 + " WHERE " + TX_ALIAS_3.join(
//               TX_DETAILS_ALIAS_3, "transaction_id") + " AND " + TX_DETAILS_ALIAS_3.column("branch_id") + "=? AND " + TX_DETAILS_ALIAS_3.column("transaction_id") + ">? AND NOT EXISTS(SELECT 'x' FROM " + Collections.toString(
//               ",", VERSION_ALIAS_1, VERSION_ALIAS_2, VERSION_ALIAS_3, TX_ALIAS_1, TX_ALIAS_2, TX_DETAILS_ALIAS_1,
//               TX_DETAILS_ALIAS_2) + " WHERE " + VERSION_ALIAS_3.join(TX_ALIAS_3, "gamma_id") + " AND " + VERSION_ALIAS_1.join(
//               VERSION_ALIAS_3, "art_id") + " AND " + VERSION_ALIAS_1.join(TX_ALIAS_1, "gamma_id") + " AND " + TX_ALIAS_1.join(
//               TX_DETAILS_ALIAS_1, "transaction_id") + " AND " + TX_DETAILS_ALIAS_1.join(TX_DETAILS_ALIAS_3,
//               "branch_id") + " AND " + VERSION_ALIAS_2.join(VERSION_ALIAS_3, "art_id") + " AND " + VERSION_ALIAS_2.join(
//               TX_ALIAS_2, "gamma_id") + " AND " + TX_ALIAS_2.join(TX_DETAILS_ALIAS_2, "transaction_id") + " AND " + TX_DETAILS_ALIAS_2.join(
//               TX_DETAILS_ALIAS_3, "branch_id") + " AND " + VERSION_ALIAS_1.column("modification_id") + "=1 AND " + VERSION_ALIAS_2.column("modification_id") + "=3 AND " + TX_DETAILS_ALIAS_1.column("transaction_id") + ">?) AND NOT EXISTS(SELECT 'x' FROM " + Collections.toString(
//               ",", VERSION_ALIAS_1, VERSION_ALIAS_2, ATTRIBUTE_VERSION_TABLE, TX_ALIAS_1, TX_ALIAS_2,
//               TX_DETAILS_ALIAS_1, TX_DETAILS_ALIAS_2) + " WHERE " + ATTRIBUTE_VERSION_TABLE.join(TX_ALIAS_3,
//               "gamma_id") + " AND " + VERSION_ALIAS_1.join(ATTRIBUTE_VERSION_TABLE, "art_id") + " AND " + VERSION_ALIAS_1.join(
//               TX_ALIAS_1, "gamma_id") + " AND " + TX_ALIAS_1.join(TX_DETAILS_ALIAS_1, "transaction_id") + " AND " + TX_DETAILS_ALIAS_1.join(
//               TX_DETAILS_ALIAS_3, "branch_id") + " AND " + VERSION_ALIAS_2.join(ATTRIBUTE_VERSION_TABLE, "art_id") + " AND " + VERSION_ALIAS_2.join(
//               TX_ALIAS_2, "gamma_id") + " AND " + TX_ALIAS_2.join(TX_DETAILS_ALIAS_2, "transaction_id") + " AND " + TX_DETAILS_ALIAS_2.join(
//               TX_DETAILS_ALIAS_3, "branch_id") + " AND " + VERSION_ALIAS_1.column("modification_id") + "=1 AND " + VERSION_ALIAS_2.column("modification_id") + "=3 AND " + TX_DETAILS_ALIAS_1.column("transaction_id") + ">?) AND NOT EXISTS(SELECT 'x' FROM " + Collections.toString(
//               ",", VERSION_ALIAS_1, VERSION_ALIAS_2, RELATION_LINK_VERSION_TABLE, TX_ALIAS_1, TX_ALIAS_2,
//               TX_DETAILS_ALIAS_1, TX_DETAILS_ALIAS_2) + " WHERE " + RELATION_LINK_VERSION_TABLE.join(TX_ALIAS_3,
//               "gamma_id") + " AND " + VERSION_ALIAS_1.column("art_id") + "=" + RELATION_LINK_VERSION_TABLE.column("a_art_id") + " AND " + VERSION_ALIAS_1.join(
//               TX_ALIAS_1, "gamma_id") + " AND " + TX_ALIAS_1.join(TX_DETAILS_ALIAS_1, "transaction_id") + " AND " + TX_DETAILS_ALIAS_1.join(
//               TX_DETAILS_ALIAS_3, "branch_id") + " AND " + VERSION_ALIAS_2.column("art_id") + "=" + RELATION_LINK_VERSION_TABLE.column("a_art_id") + " AND " + VERSION_ALIAS_2.join(
//               TX_ALIAS_2, "gamma_id") + " AND " + TX_ALIAS_2.join(TX_DETAILS_ALIAS_2, "transaction_id") + " AND " + TX_DETAILS_ALIAS_2.join(
//               TX_DETAILS_ALIAS_3, "branch_id") + " AND " + VERSION_ALIAS_1.column("modification_id") + "=1 AND " + VERSION_ALIAS_2.column("modification_id") + "=3 AND " + TX_DETAILS_ALIAS_1.column("transaction_id") + ">?) AND NOT EXISTS(SELECT 'x' FROM " + Collections.toString(
//               ",", VERSION_ALIAS_1, VERSION_ALIAS_2, RELATION_LINK_VERSION_TABLE, TX_ALIAS_1, TX_ALIAS_2,
//               TX_DETAILS_ALIAS_1, TX_DETAILS_ALIAS_2) + " WHERE " + RELATION_LINK_VERSION_TABLE.join(TX_ALIAS_3,
//               "gamma_id") + " AND " + VERSION_ALIAS_1.column("art_id") + "=" + RELATION_LINK_VERSION_TABLE.column("b_art_id") + " AND " + VERSION_ALIAS_1.join(
//               TX_ALIAS_1, "gamma_id") + " AND " + TX_ALIAS_1.join(TX_DETAILS_ALIAS_1, "transaction_id") + " AND " + TX_DETAILS_ALIAS_1.join(
//               TX_DETAILS_ALIAS_3, "branch_id") + " AND " + VERSION_ALIAS_2.column("art_id") + "=" + RELATION_LINK_VERSION_TABLE.column("b_art_id") + " AND " + VERSION_ALIAS_2.join(
//               TX_ALIAS_2, "gamma_id") + " AND " + TX_ALIAS_2.join(TX_DETAILS_ALIAS_2, "transaction_id") + " AND " + TX_DETAILS_ALIAS_2.join(
//               TX_DETAILS_ALIAS_3, "branch_id") + " AND " + VERSION_ALIAS_1.column("modification_id") + "=1 AND " + VERSION_ALIAS_2.column("modification_id") + "=3 AND " + TX_DETAILS_ALIAS_1.column("transaction_id") + ">?)";
//   private static final String TRANSACTION_COMMIT =
//         "INSERT INTO " + TRANSACTIONS_TABLE + "(transaction_id, gamma_id, tx_type) SELECT ?, " + TX_ALIAS_3.column("gamma_id") + ", ? FROM " + TX_ALIAS_3 + ", " + TX_DETAILS_ALIAS_3 + " WHERE " + TX_ALIAS_3.join(
//               TX_DETAILS_ALIAS_3, "transaction_id") + " AND " + TX_DETAILS_ALIAS_3.column("branch_id") + "=? AND " + TX_DETAILS_ALIAS_3.column("transaction_id") + "=? AND NOT EXISTS(SELECT 'x' FROM " + Collections.toString(
//               ",", VERSION_ALIAS_1, VERSION_ALIAS_2, VERSION_ALIAS_3, TX_ALIAS_1, TX_ALIAS_2, TX_DETAILS_ALIAS_1,
//               TX_DETAILS_ALIAS_2) + " WHERE " + VERSION_ALIAS_3.join(TX_ALIAS_3, "gamma_id") + " AND " + VERSION_ALIAS_1.join(
//               VERSION_ALIAS_3, "art_id") + " AND " + VERSION_ALIAS_1.join(TX_ALIAS_1, "gamma_id") + " AND " + TX_ALIAS_1.join(
//               TX_DETAILS_ALIAS_1, "transaction_id") + " AND " + TX_DETAILS_ALIAS_1.join(TX_DETAILS_ALIAS_3,
//               "branch_id") + " AND " + VERSION_ALIAS_2.join(VERSION_ALIAS_3, "art_id") + " AND " + VERSION_ALIAS_2.join(
//               TX_ALIAS_2, "gamma_id") + " AND " + TX_ALIAS_2.join(TX_DETAILS_ALIAS_2, "transaction_id") + " AND " + TX_DETAILS_ALIAS_2.join(
//               TX_DETAILS_ALIAS_3, "branch_id") + " AND " + VERSION_ALIAS_1.column("modification_id") + "=1 AND " + VERSION_ALIAS_2.column("modification_id") + "=3 AND " + TX_DETAILS_ALIAS_1.column("transaction_id") + "=?) AND NOT EXISTS(SELECT 'x' FROM " + Collections.toString(
//               ",", VERSION_ALIAS_1, VERSION_ALIAS_2, ATTRIBUTE_VERSION_TABLE, TX_ALIAS_1, TX_ALIAS_2,
//               TX_DETAILS_ALIAS_1, TX_DETAILS_ALIAS_2) + " WHERE " + ATTRIBUTE_VERSION_TABLE.join(TX_ALIAS_3,
//               "gamma_id") + " AND " + VERSION_ALIAS_1.join(ATTRIBUTE_VERSION_TABLE, "art_id") + " AND " + VERSION_ALIAS_1.join(
//               TX_ALIAS_1, "gamma_id") + " AND " + TX_ALIAS_1.join(TX_DETAILS_ALIAS_1, "transaction_id") + " AND " + TX_DETAILS_ALIAS_1.join(
//               TX_DETAILS_ALIAS_3, "branch_id") + " AND " + VERSION_ALIAS_2.join(ATTRIBUTE_VERSION_TABLE, "art_id") + " AND " + VERSION_ALIAS_2.join(
//               TX_ALIAS_2, "gamma_id") + " AND " + TX_ALIAS_2.join(TX_DETAILS_ALIAS_2, "transaction_id") + " AND " + TX_DETAILS_ALIAS_2.join(
//               TX_DETAILS_ALIAS_3, "branch_id") + " AND " + VERSION_ALIAS_1.column("modification_id") + "=1 AND " + VERSION_ALIAS_2.column("modification_id") + "=3 AND " + TX_DETAILS_ALIAS_1.column("transaction_id") + "=?) AND NOT EXISTS(SELECT 'x' FROM " + Collections.toString(
//               ",", VERSION_ALIAS_1, VERSION_ALIAS_2, RELATION_LINK_VERSION_TABLE, TX_ALIAS_1, TX_ALIAS_2,
//               TX_DETAILS_ALIAS_1, TX_DETAILS_ALIAS_2) + " WHERE " + RELATION_LINK_VERSION_TABLE.join(TX_ALIAS_3,
//               "gamma_id") + " AND " + VERSION_ALIAS_1.column("art_id") + "=" + RELATION_LINK_VERSION_TABLE.column("a_art_id") + " AND " + VERSION_ALIAS_1.join(
//               TX_ALIAS_1, "gamma_id") + " AND " + TX_ALIAS_1.join(TX_DETAILS_ALIAS_1, "transaction_id") + " AND " + TX_DETAILS_ALIAS_1.join(
//               TX_DETAILS_ALIAS_3, "branch_id") + " AND " + VERSION_ALIAS_2.column("art_id") + "=" + RELATION_LINK_VERSION_TABLE.column("a_art_id") + " AND " + VERSION_ALIAS_2.join(
//               TX_ALIAS_2, "gamma_id") + " AND " + TX_ALIAS_2.join(TX_DETAILS_ALIAS_2, "transaction_id") + " AND " + TX_DETAILS_ALIAS_2.join(
//               TX_DETAILS_ALIAS_3, "branch_id") + " AND " + VERSION_ALIAS_1.column("modification_id") + "=1 AND " + VERSION_ALIAS_2.column("modification_id") + "=3 AND " + TX_DETAILS_ALIAS_1.column("transaction_id") + "=?) AND NOT EXISTS(SELECT 'x' FROM " + Collections.toString(
//               ",", VERSION_ALIAS_1, VERSION_ALIAS_2, RELATION_LINK_VERSION_TABLE, TX_ALIAS_1, TX_ALIAS_2,
//               TX_DETAILS_ALIAS_1, TX_DETAILS_ALIAS_2) + " WHERE " + RELATION_LINK_VERSION_TABLE.join(TX_ALIAS_3,
//               "gamma_id") + " AND " + VERSION_ALIAS_1.column("art_id") + "=" + RELATION_LINK_VERSION_TABLE.column("b_art_id") + " AND " + VERSION_ALIAS_1.join(
//               TX_ALIAS_1, "gamma_id") + " AND " + TX_ALIAS_1.join(TX_DETAILS_ALIAS_1, "transaction_id") + " AND " + TX_DETAILS_ALIAS_1.join(
//               TX_DETAILS_ALIAS_3, "branch_id") + " AND " + VERSION_ALIAS_2.column("art_id") + "=" + RELATION_LINK_VERSION_TABLE.column("b_art_id") + " AND " + VERSION_ALIAS_2.join(
//               TX_ALIAS_2, "gamma_id") + " AND " + TX_ALIAS_2.join(TX_DETAILS_ALIAS_2, "transaction_id") + " AND " + TX_DETAILS_ALIAS_2.join(
//               TX_DETAILS_ALIAS_3, "branch_id") + " AND " + VERSION_ALIAS_1.column("modification_id") + "=1 AND " + VERSION_ALIAS_2.column("modification_id") + "=3 AND " + TX_DETAILS_ALIAS_1.column("transaction_id") + "=?)";

   private static final String DELETE_TO_BRANCH_TAG_DATA =
         "DELETE FROM osee_tag_art_map tam1 WHERE tam1.branch_id = ? AND EXISTS (SELECT 'x' FROM osee_tag_art_map tam2 WHERE tam1.art_id = tam2.art_id AND tam1.tag_id = tam2.tag_id AND branch_id = ?)";
   private static final String MOVE_TAG_DATA = "UPDATE osee_tag_art_map SET branch_id = ? WHERE branch_id = ?";
   private static final String COMMIT_ATTRIBUTE =
         "INSERT INTO OSEE_DEFINE_TXS(transaction_id, gamma_id, tx_type)" + " SELECT ?, txs1.gamma_id, ?" + " FROM osee_define_txs txs1," + " osee_define_tx_details txd2," + " osee_define_attribute attr3" + " WHERE txd2.branch_id = ?" + " AND txd2.transaction_id = txs1.transaction_id" + " AND txs1.gamma_id = attr3.gamma_id" + " AND txs1.transaction_id =" + " (SELECT MAX(t4.transaction_id) AS" + " transaction_id" + " FROM osee_define_txs t4," + " osee_define_tx_details t5," + " osee_define_attribute t6" + " WHERE t4.tx_type <> ? and t4.gamma_id = t6.gamma_id" + " AND t4.transaction_id = t5.transaction_id" + " AND t5.branch_id = txd2.branch_id" + " AND t6.attr_id = attr3.attr_id" + " GROUP BY t6.attr_id)" + " AND(txs1.tx_type <> ? OR EXISTS" + " (SELECT 'x'" + " FROM osee_define_txs txs7, osee_define_attribute attr8" + " WHERE txs7.transaction_id = ?" + " AND attr8.attr_id = attr3.attr_id" + " AND txs7.gamma_id = attr8.gamma_id))";
   private static final String COMMIT_RELATIONS =
         "INSERT INTO OSEE_DEFINE_TXS(transaction_id, gamma_id, tx_type)" + " SELECT ?, txs1.gamma_id, ?" + " FROM osee_define_txs txs1," + " osee_define_tx_details txd2," + " osee_define_rel_link rel3" + " WHERE txd2.branch_id = ?" + " AND txd2.transaction_id = txs1.transaction_id" + " AND txs1.gamma_id = rel3.gamma_id" + " AND txs1.transaction_id =" + " (SELECT MAX(t4.transaction_id) AS" + " transaction_id" + " FROM osee_define_txs t4," + " osee_define_tx_details t5," + " osee_define_rel_link t6" + " WHERE t4.tx_type <> ? and t4.gamma_id = t6.gamma_id" + " AND t4.transaction_id = t5.transaction_id" + " AND t5.branch_id = txd2.branch_id" + " AND t6.rel_link_id = rel3.rel_link_id" + " GROUP BY t6.rel_link_id)" + " AND(txs1.tx_type <> ? OR EXISTS" + " (SELECT 'x'" + " FROM osee_define_txs txs7, osee_define_rel_link rel8" + " WHERE txs7.transaction_id = ?" + " AND rel8.rel_link_id = rel3.rel_link_id" + " AND txs7.gamma_id = rel8.gamma_id))";
   private static final String COMMIT_ARTIFACT =
         "INSERT INTO OSEE_DEFINE_TXS(transaction_id, gamma_id, tx_type)" + " SELECT ?, txs1.gamma_id, ?" + " FROM osee_define_txs txs1," + " osee_define_tx_details txd2," + " osee_define_artifact_version atv3" + " WHERE txd2.branch_id = ?" + " AND txd2.transaction_id = txs1.transaction_id" + " AND txs1.gamma_id = atv3.gamma_id" + " AND txs1.transaction_id =" + " (SELECT MAX(t4.transaction_id) AS" + " transaction_id" + " FROM osee_define_txs t4," + " osee_define_tx_details t5," + " osee_define_artifact_version t6" + " WHERE t4.tx_type <> ? and t4.gamma_id = t6.gamma_id" + " AND t4.transaction_id = t5.transaction_id" + " AND t5.branch_id = txd2.branch_id" + " AND t6.art_id = atv3.art_id" + " GROUP BY t6.art_id)" + " AND(txs1.tx_type <> ? OR EXISTS" + " (SELECT 'x'" + " FROM osee_define_txs txs7, osee_define_artifact_version atv8" + " WHERE txs7.transaction_id = ?" + " AND atv8.art_id = atv3.art_id" + " AND txs7.gamma_id = atv8.gamma_id))";

   private static final SkynetEventManager eventManager = SkynetEventManager.getInstance();
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();
   private static final AccessControlManager accessControlManager = AccessControlManager.getInstance();
   private static final TransactionIdManager transactionIdManager = TransactionIdManager.getInstance();
   private IProgressMonitor monitor;
   private CommitDbTx commitDbTx;

   public CommitJob(Branch toBranch, Branch fromBranch, boolean archiveBranch) {
      this(toBranch, fromBranch, null, archiveBranch);
   }

   public CommitJob(Branch toBranch, TransactionId fromTransactionId, boolean archiveBranch) {
      this(toBranch, null, fromTransactionId, archiveBranch);
   }

   private CommitJob(Branch toBranch, Branch fromBranch, TransactionId fromTransactionId, boolean archiveBranch) {
      super("Committing Branch: " + fromBranch.getBranchName());
      commitDbTx = new CommitDbTx(fromBranch, toBranch, fromTransactionId, archiveBranch);
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
      private TransactionId fromTransactionId;
      private int newTransactionNumber;
      private final Branch toBranch;
      private final Branch fromBranch;
      private final boolean archiveBranch;
      private boolean success = true;
      private int fromBranchId = -1;

      private CommitDbTx(Branch fromBranch, Branch toBranch, TransactionId fromTransactionId, boolean archiveBranch) {
         super();
         this.toBranch = toBranch;
         this.fromBranch = fromBranch;
         this.fromTransactionId = fromTransactionId;
         this.newTransactionNumber = -1;
         this.archiveBranch = archiveBranch;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.ui.plugin.util.db.AbstractDbTxTemplate#handleTxWork()
       */
      @Override
      protected void handleTxWork() throws Exception {
         monitor.beginTask("Acquire from branch transactions", 100);

         TransactionId baseTransactionId = fromTransactionId;
         User userToBlame = SkynetAuthentication.getInstance().getAuthenticatedUser();

         if (fromBranch != null) {
            newTransactionNumber = branchManager.addTransactionToDatabase(toBranch, fromBranch, userToBlame);
            fromBranchId = fromBranch.getBranchId();
            Pair<TransactionId, TransactionId> transactions = transactionIdManager.getStartEndPoint(fromBranch);
            baseTransactionId = transactions.getKey(); // minimum transaction on the child branch
            accessControlManager.removeAllPermissionsFromBranch(fromBranch);
         } else {
            //            newTransactionNumber = branchManager.addTransactionToDatabase(toBranch, fromTransactionId, userToBlame);
            //            fromBranchId = fromTransactionId.getBranch().getBranchId();
            //            sql = TRANSACTION_COMMIT;
         }

         monitor.worked(25);
         monitor.setTaskName("Commit transactions");

         int insertCount =
               ConnectionHandler.runPreparedUpdateReturnCount(COMMIT_ARTIFACT, SQL3DataType.INTEGER,
                     newTransactionNumber, SQL3DataType.INTEGER, TransactionType.COMMITTED.getId(),
                     SQL3DataType.INTEGER, fromBranchId, SQL3DataType.INTEGER, TransactionType.BRANCHED.getId(),
                     SQL3DataType.INTEGER, TransactionType.DELETED.getId(), SQL3DataType.INTEGER,
                     baseTransactionId.getTransactionNumber());

         insertCount +=
               ConnectionHandler.runPreparedUpdateReturnCount(COMMIT_ATTRIBUTE, SQL3DataType.INTEGER,
                     newTransactionNumber, SQL3DataType.INTEGER, TransactionType.COMMITTED.getId(),
                     SQL3DataType.INTEGER, fromBranchId, SQL3DataType.INTEGER, TransactionType.BRANCHED.getId(),
                     SQL3DataType.INTEGER, TransactionType.DELETED.getId(), SQL3DataType.INTEGER,
                     baseTransactionId.getTransactionNumber());

         insertCount +=
               ConnectionHandler.runPreparedUpdateReturnCount(COMMIT_RELATIONS, SQL3DataType.INTEGER,
                     newTransactionNumber, SQL3DataType.INTEGER, TransactionType.COMMITTED.getId(),
                     SQL3DataType.INTEGER, fromBranchId, SQL3DataType.INTEGER, TransactionType.BRANCHED.getId(),
                     SQL3DataType.INTEGER, TransactionType.DELETED.getId(), SQL3DataType.INTEGER,
                     baseTransactionId.getTransactionNumber());

         if (insertCount > 0) {
            transactionIdManager.resetEditableTransactionId(newTransactionNumber, toBranch);
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
            eventManager.kick(new LocalCommitBranchEvent(this, fromBranchId));
            RemoteEventManager.getInstance().kick(
                  new NetworkCommitBranchEvent(fromBranchId,
                        SkynetAuthentication.getInstance().getAuthenticatedUser().getArtId()));
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