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
package org.eclipse.osee.framework.branch.management.internal;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.Branch;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.SequenceManager;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class CreateBranchOperation extends AbstractDbTxOperation {
   private static final String INSERT_BRANCH =
         "INSERT INTO osee_branch (branch_id, branch_guid, branch_name, parent_branch_id, parent_transaction_id, archived, associated_art_id, branch_type, branch_state) VALUES (?,?,?,?,?,?,?,?,?)";
   private static final String UPDATE_BRANCH =
         "UPDATE osee_branch SET branch_name = ?, parent_branch_id = ?, parent_transaction_id = ?, archived = ?, associated_art_id = ?, branch_type = ?, branch_state = ? where branch_id = ?";
   private static final String DELETE_BRANCH = "DELETE from osee_branch where branch_id = ?";

   private static final String INSERT_DEFAULT_BRANCH_NAMES =
         "INSERT INTO OSEE_BRANCH_DEFINITIONS (static_branch_name, mapped_branch_id) VALUES (?, ?)";

   private static final String INSERT_TX_DETAILS =
         "INSERT INTO osee_TX_DETAILS ( branch_id, transaction_id, OSEE_COMMENT, time, author, tx_type ) VALUES ( ?, ?, ?, ?, ?, ?)";

   // descending order is used so that the most recent entry will be used if there are multiple rows with the same gamma (an error case)
   private static final String SELECT_ADDRESSING =
         "SELECT gamma_id, mod_type FROM osee_txs txs, osee_tx_details txd WHERE txs.tx_current <> ? AND txs.transaction_id = txd.transaction_id AND txd.branch_id = ? order by txd.transaction_id desc";
   private static final String INSERT_ADDRESSING =
         "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current) VALUES (?,?,?,?)";
   private static final String USER_ID_QUERY =
         "select oa.art_id from osee_attribute_type oat, osee_attribute oa, osee_txs txs where oat.name = 'User Id' and oat.attr_type_id = oa.attr_type_id and oa.gamma_id = txs.gamma_id and txs.tx_current = 1 and oa.value = ?";

   private static final String MERGE_BRANCH_INSERT =
         "INSERT INTO osee_merge (source_branch_id, dest_branch_id, merge_branch_id, commit_transaction_id) VALUES(?,?,?,?)";
   private final static String INSERT_ADDITIONAL_ATTRIBUTE_GAMMAS =
         "INSERT INTO OSEE_TXS (transaction_id, gamma_id, mod_type, tx_current) SELECT ?, atr1.gamma_id, txs1.mod_type, ? FROM osee_attribute atr1, osee_txs txs1, osee_tx_details txd1, osee_join_artifact ald1 WHERE txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current in (1,2) AND txs1.gamma_id = atr1.gamma_id AND atr1.art_id = ald1.art_id and ald1.query_id = ?";
   private final static String INSERT_ADDITIONAL_ARTIFACT_GAMMAS =
         "INSERT INTO OSEE_TXS (transaction_id, gamma_id, mod_type, tx_current) SELECT ?, arv1.gamma_id, txs1.mod_type, ? FROM osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1, osee_join_artifact ald1 WHERE txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current in (1,2) AND txs1.gamma_id = arv1.gamma_id AND arv1.art_id = ald1.art_id and ald1.query_id = ?";

   private final Branch branch;
   private final String creationComment;
   private final int authorId;
   private boolean passedPreConditions;
   private int systemUserId;
   private final int populateBaseTxFromAddressingQueryId;
   private final int destinationBranchId;

   public CreateBranchOperation(Branch branch, int authorId, String creationComment, int populateBaseTxFromAddressingQueryId, int destinationBranchId) {
      super(String.format("Create Branch: [%s from %s]", branch.getName(), branch.getParentBranchId()),
            InternalBranchActivator.PLUGIN_ID);
      this.branch = branch;
      this.authorId = authorId;
      this.creationComment = creationComment;
      this.systemUserId = -1;
      this.populateBaseTxFromAddressingQueryId = populateBaseTxFromAddressingQueryId;
      this.destinationBranchId = destinationBranchId;
   }

   private int getSystemUserId() {
      if (systemUserId == -1) {
         try {
            systemUserId =
                  ConnectionHandler.runPreparedQueryFetchInt(-1, USER_ID_QUERY, SystemUser.OseeSystem.getUserID());
         } catch (OseeDataStoreException ex) {
            OseeLog.log(InternalBranchActivator.class, Level.WARNING, "Unable to retrieve the system user");
         }
      }
      return systemUserId;
   }

   public void checkPreconditions(IProgressMonitor monitor) throws OseeCoreException {
      if (!branch.getBranchType().isMergeBranch() && !branch.getBranchType().isSystemRootBranch()) {
         int associatedArtifactId = branch.getAssociatedArtifactId();
         int systemUserId = getSystemUserId();
         if (associatedArtifactId > -1 && associatedArtifactId != systemUserId) {
            int count =
                  ConnectionHandler.runPreparedQueryFetchInt(0,
                        "select (1) from osee_branch where associated_art_id=? and branch_state <> ?",
                        branch.getAssociatedArtifactId(), BranchState.DELETED.getValue());
            if (count > 0) {
               throw new OseeStateException(String.format("Existing branch creation detected for [%s]", branch));
            }
         }
      }
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
      passedPreConditions = false;

      checkPreconditions(monitor);

      passedPreConditions = true;

      branch.setBranchState(BranchState.CREATION_IN_PROGRESS);

      storeBranch(monitor, 0.10, null, branch, true); // Use different Connection

      populateBaseTransaction(monitor, 0.30, connection, branch, populateBaseTxFromAddressingQueryId);

      addBranchAlias(monitor, 0.20, connection, branch);

      addMergeBranchEntry(monitor, 0.20, connection, branch, destinationBranchId);

      branch.setBranchState(BranchState.CREATED);

      storeBranch(monitor, 0.10, connection, branch, false);
   }

   @Override
   protected void handleTxException(IProgressMonitor monitor, Exception ex) {
      if (passedPreConditions) {
         try {
            ConnectionHandler.runPreparedUpdate(DELETE_BRANCH, branch.getBranchId());
         } catch (OseeDataStoreException ex1) {
            OseeLog.log(InternalBranchActivator.class, Level.SEVERE, ex1);
         }
      }
   }

   @Override
   protected void handleTxFinally(IProgressMonitor monitor) throws OseeCoreException {
      monitor.worked(calculateWork(0.10));
   }

   private void addBranchAlias(IProgressMonitor monitor, double workAmount, OseeConnection connection, Branch branch) throws OseeDataStoreException {
      if (branch.getStaticBranchName() != null) {
         ConnectionHandler.runPreparedUpdate(connection, INSERT_DEFAULT_BRANCH_NAMES, branch.getStaticBranchName(),
               branch.getBranchId());
      }
      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(workAmount));
   }

   private void addMergeBranchEntry(IProgressMonitor monitor, double workAmount, OseeConnection connection, Branch branch, int destinationBranchId) throws OseeDataStoreException {
      if (branch.getBranchType().isMergeBranch()) {
         ConnectionHandler.runPreparedUpdate(connection, MERGE_BRANCH_INSERT, branch.getParentBranchId(),
               destinationBranchId, branch.getBranchId(), -1);
      }
      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(workAmount));
   }

   private void storeBranch(IProgressMonitor monitor, double workAmount, OseeConnection connection, Branch branch, boolean isCreate) throws OseeDataStoreException {
      if (isCreate) {
         Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
         int branchId = SequenceManager.getNextBranchId();
         String guid = branch.getGuid();
         if (!GUID.isValid(guid)) {
            guid = GUID.create();
         }
         ConnectionHandler.runPreparedUpdate(connection, INSERT_BRANCH, branchId, guid, branch.getName(),
               branch.getParentBranchId(), branch.getParentTransactionId(), 0, branch.getAssociatedArtifactId(),
               branch.getBranchType().getValue(), branch.getBranchState().getValue());
         branch.setGuid(guid);
         branch.setBranchId(branchId);

         branch.setBaseTransaction(SequenceManager.getNextTransactionId());
         ConnectionHandler.runPreparedUpdate(connection, INSERT_TX_DETAILS, branch.getBranchId(),
               branch.getBaseTransaction(), creationComment, timestamp, authorId, 1);
      } else {
         ConnectionHandler.runPreparedUpdate(connection, UPDATE_BRANCH, branch.getName(), branch.getParentBranchId(),
               branch.getParentTransactionId(), 0, branch.getAssociatedArtifactId(), branch.getBranchType().getValue(),
               branch.getBranchState().getValue(), branch.getBranchId());
      }
      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(workAmount));
   }

   private void populateBaseTransaction(IProgressMonitor monitor, double workAmount, OseeConnection connection, Branch branch, int populateBaseTxFromAddressingQueryId) throws OseeDataStoreException {
      if (branch.getBranchType() != BranchType.SYSTEM_ROOT) {
         if (populateBaseTxFromAddressingQueryId > 0) {
            int txCurrent = TxChange.CURRENT.getValue();
            int parentBranchId = branch.getParentBranchId();
            int baseTransaction = branch.getBaseTransaction();

            ConnectionHandler.runPreparedUpdate(connection, INSERT_ADDITIONAL_ATTRIBUTE_GAMMAS, baseTransaction,
                  txCurrent, parentBranchId, populateBaseTxFromAddressingQueryId);
            ConnectionHandler.runPreparedUpdate(connection, INSERT_ADDITIONAL_ARTIFACT_GAMMAS, baseTransaction,
                  txCurrent, parentBranchId, populateBaseTxFromAddressingQueryId);
            monitor.setTaskName(String.format("Created base transaction for branch [%s] joining against query id",
                  branch.getName()));
         } else {
            List<Object[]> data = new ArrayList<Object[]>();
            HashSet<Integer> gammas = new HashSet<Integer>(100000);

            ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement(connection);
            try {
               chStmt.runPreparedQuery(10000, SELECT_ADDRESSING, TxChange.NOT_CURRENT.getValue(),
                     branch.getParentBranchId());
               while (chStmt.next()) {
                  checkForCancelledStatus(monitor);
                  Integer gamma = chStmt.getInt("gamma_id");
                  if (!gammas.contains(gamma)) {
                     data.add(new Object[] {branch.getBaseTransaction(), gamma, chStmt.getInt("mod_type"), 1});
                     gammas.add(gamma);
                  }
               }
            } finally {
               chStmt.close();
            }
            ConnectionHandler.runBatchUpdate(connection, INSERT_ADDRESSING, data);
            monitor.setTaskName(String.format("Created branch [%s] with [%d] transactions", branch.getName(),
                  data.size()));
         }
      }
      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(workAmount));
   }
}
