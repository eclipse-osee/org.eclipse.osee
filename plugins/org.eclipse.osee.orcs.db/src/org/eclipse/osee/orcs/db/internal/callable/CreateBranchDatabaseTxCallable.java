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
import java.util.HashSet;
import java.util.List;

import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.BranchFactory;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.db.internal.util.IdUtil;

/**
 * @author Roberto E. Escobar
 */
public class CreateBranchDatabaseTxCallable extends AbstractDatastoreTxCallable<Branch> {

   private static final String INSERT_TX_DETAILS =
      "INSERT INTO osee_tx_details (branch_id, transaction_id, osee_comment, time, author, tx_type) VALUES (?,?,?,?,?,?)";

   // @formatter:off
   private static final String SELECT_ADDRESSING = "with\n"+
"txs as (select transaction_id, gamma_id, mod_type from osee_txs where branch_id = ? and transaction_id <= ?),\n\n"+

"txsI as (\n"+
"   SELECT transaction_id, item.gamma_id, mod_type, 1 as item_type, attr_id as item_id FROM osee_attribute item, txs where txs.gamma_id = item.gamma_id\n"+
"UNION ALL\n"+
"   SELECT transaction_id, item.gamma_id, mod_type, 2 as item_type, art_id as item_id FROM osee_artifact item, txs where txs.gamma_id = item.gamma_id\n"+
"UNION ALL\n"+
"   SELECT transaction_id, item.gamma_id, mod_type, 3 as item_type, rel_link_id as item_id FROM osee_relation_link item, txs where txs.gamma_id = item.gamma_id),\n\n"+

"txsM as (SELECT MAX(transaction_id) AS transaction_id, item_type, item_id FROM txsI GROUP BY item_type, item_id)\n\n"+

"select gamma_id, mod_type from txsI, txsM where txsM.item_type = txsI.item_type and txsM.item_id = txsI.item_id and txsM.transaction_id = txsI.transaction_id order by txsM.transaction_id desc";
   // descending order is used so that the most recent entry will be used if there are multiple rows with the same gamma (an error case)
   // @formatter:on

   private static final String INSERT_ADDRESSING =
      "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current, branch_id) VALUES (?,?,?,?,?)";
   private static final String USER_ID_QUERY = "SELECT art_id FROM osee_artifact WHERE guid = ?";

   private static final String MERGE_BRANCH_INSERT =
      "INSERT INTO osee_merge (source_branch_id, dest_branch_id, merge_branch_id, commit_transaction_id) VALUES (?,?,?,?)";

   private final static String SELECT_ATTRIBUTE_ADDRESSING_FROM_JOIN =
      "SELECT item.gamma_id, txs.mod_type FROM osee_attribute item, osee_txs txs, osee_join_artifact artjoin WHERE txs.branch_id = ? AND txs.tx_current <> ? AND txs.gamma_id = item.gamma_id AND item.art_id = artjoin.art_id and artjoin.query_id = ? ORDER BY txs.transaction_id DESC";
   private final static String SELECT_ARTIFACT_ADDRESSING_FROM_JOIN =
      "SELECT item.gamma_id, txs.mod_type FROM osee_artifact item, osee_txs txs, osee_join_artifact artjoin WHERE txs.branch_id = ? AND txs.tx_current <> ? AND txs.gamma_id = item.gamma_id AND item.art_id = artjoin.art_id and artjoin.query_id = ? ORDER BY txs.transaction_id DESC";

   private static final String TEST_MERGE_BRANCH_EXISTENCE =
      "SELECT COUNT(1) FROM osee_merge WHERE source_branch_id = ? AND dest_branch_id = ?";

   private boolean passedPreConditions;
   private boolean wasSuccessful;
   private int systemUserId;

   private final BranchCache branchCache;
   private final TransactionCache txCache;
   private final BranchFactory branchFactory;
   private final TransactionRecordFactory txFactory;
   private final CreateBranchData newBranchData;
   private Branch branch;

   public CreateBranchDatabaseTxCallable(Log logger, OrcsSession session, IOseeDatabaseService databaseService, BranchCache branchCache, TransactionCache txCache, BranchFactory branchFactory, TransactionRecordFactory txFactory, CreateBranchData branchData) {
      super(logger, session, databaseService, String.format("Create Branch %s", branchData.getName()));
      this.branchCache = branchCache;
      this.txCache = txCache;
      this.branchFactory = branchFactory;
      this.txFactory = txFactory;
      this.newBranchData = branchData;
      this.wasSuccessful = false;
      this.systemUserId = -1;
   }

   private int getSystemUserId() {
      if (systemUserId == -1) {
         try {
            systemUserId =
               getDatabaseService().runPreparedQueryFetchObject(-1, USER_ID_QUERY, SystemUser.OseeSystem.getGuid());
         } catch (OseeCoreException ex) {
            getLogger().warn(ex, "Unable to retrieve the system user");
         }
      }
      return systemUserId;
   }

   public void checkPreconditions(Branch parentBranch, Branch destinationBranch) throws OseeCoreException {
      if (newBranchData.getBranchType().isMergeBranch()) {
         if (getDatabaseService().runPreparedQueryFetchObject(0, TEST_MERGE_BRANCH_EXISTENCE, parentBranch.getId(),
            destinationBranch.getId()) > 0) {
            throw new OseeStateException("Existing merge branch detected for [%s] and [%s]", parentBranch.getName(),
               destinationBranch.getName());
         }
      } else if (!newBranchData.getBranchType().isSystemRootBranch()) {
         int associatedArtifactId = newBranchData.getAssociatedArtifactId();
         int systemUserId = getSystemUserId();

         // this checks to see if there are any branches that aren't either DELETED or REBASELINED with the same artifact ID
         if (associatedArtifactId > -1 && associatedArtifactId != systemUserId) {
            int count =
               getDatabaseService().runPreparedQueryFetchObject(0,
                  "SELECT (1) FROM osee_branch WHERE associated_art_id = ? AND branch_state NOT IN (?, ?)",
                  newBranchData.getAssociatedArtifactId(), BranchState.DELETED.getValue(),
                  BranchState.REBASELINED.getValue());
            if (count > 0) {
               // the PORT branch type is a special case, a PORT branch can have the same associated artifact
               // as its related RPCR branch. We need to check to see if there is already a 
               // port branch with the same artifact ID - if the type is port type, then we need an additional check
               if (newBranchData.getBranchType().equals(BranchType.PORT)) {

                  int portcount =
                     getDatabaseService().runPreparedQueryFetchObject(
                        0,
                        "SELECT (1) FROM osee_branch WHERE associated_art_id = ? AND branch_state NOT IN (?, ?) AND branch_type = ?",
                        newBranchData.getAssociatedArtifactId(), BranchState.DELETED.getValue(),
                        BranchState.REBASELINED.getValue(), BranchType.PORT.getValue());
                  if (portcount > 0) {
                     throw new OseeStateException("Existing port branch creation detected for [%s]",
                        newBranchData.getName());
                  }
               } else {
                  throw new OseeStateException("Existing branch creation detected for [%s]", newBranchData.getName());
               }
            }
         }
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   protected Branch handleTxWork(OseeConnection connection) throws OseeCoreException {
      Branch parentBranch = branchCache.getById(IdUtil.getParentBranchId(newBranchData, txCache));
      Branch destinationBranch = branchCache.getById(newBranchData.getMergeDestinationBranchId());

      passedPreConditions = false;
      checkPreconditions(parentBranch, destinationBranch);
      passedPreConditions = true;

      String guid = newBranchData.getGuid();
      if (!GUID.isValid(guid)) {
         guid = GUID.create();
      }

      final String truncatedName = Strings.truncate(newBranchData.getName(), 195, true);
      branch =
         branchFactory.create(guid, truncatedName, newBranchData.getBranchType(), BranchState.CREATION_IN_PROGRESS,
            false);

      branch.setParentBranch(parentBranch);
      branch.setAssociatedArtifactId(newBranchData.getAssociatedArtifactId());

      Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
      int nextTransactionId = getDatabaseService().getSequence().getNextTransactionId();

      if (branch.getBranchType().isSystemRootBranch()) {
         TransactionRecord systemTx =
            txFactory.create(nextTransactionId, branch.getId(), newBranchData.getCreationComment(), timestamp,
               newBranchData.getUserArtifactId(), -1, TransactionDetailsType.Baselined, branchCache);
         branch.setSourceTransaction(systemTx);
      } else {
         int srcTx = IdUtil.getSourceTxId(newBranchData, txCache);

         branch.setSourceTransaction(txCache.getOrLoad(srcTx));
      }

      if (branch.getBranchType().isMergeBranch()) {
         ((MergeBranch) branch).setSourceBranch(parentBranch);
         ((MergeBranch) branch).setDestinationBranch(destinationBranch);
      }

      branchCache.cache(branch);
      branchCache.storeItems(branch);

      getDatabaseService().runPreparedUpdate(connection, INSERT_TX_DETAILS, branch.getId(), nextTransactionId,
         newBranchData.getCreationComment(), timestamp, newBranchData.getUserArtifactId(),
         TransactionDetailsType.Baselined.getId());

      TransactionRecord record =
         txFactory.create(nextTransactionId, branch.getId(), newBranchData.getCreationComment(), timestamp,
            newBranchData.getUserArtifactId(), -1, TransactionDetailsType.Baselined, branchCache);

      if (branch.getBranchType().isSystemRootBranch()) {
         branch.setSourceTransaction(record);
      }
      branch.setBaseTransaction(record);
      txCache.cache(record);
      populateBaseTransaction(0.30, connection, branch, newBranchData.getMergeAddressingQueryId());

      addMergeBranchEntry(0.20, connection, branch, newBranchData.getMergeDestinationBranchId());
      wasSuccessful = true;
      return branch;
   }

   @Override
   protected void handleTxException(Exception ex) {
      if (passedPreConditions) {
         try {
            branch.setStorageState(StorageState.PURGED);
            branchCache.storeItems(branch);
         } catch (OseeCoreException ex1) {
            getLogger().error(ex1, "Error during create branch [%s]", branch);
         }
      }
   }

   @Override
   protected void handleTxFinally() throws OseeCoreException {
      if (wasSuccessful) {
         branch.setBranchState(BranchState.CREATED);
         branchCache.storeItems(branch);
      }
   }

   private void addMergeBranchEntry(double workAmount, OseeConnection connection, Branch branch, int destinationBranchId) throws OseeCoreException {
      if (branch.getBranchType().isMergeBranch()) {
         int parentBranchId = branch.hasParentBranch() ? branch.getParentBranch().getId() : -1;
         getDatabaseService().runPreparedUpdate(connection, MERGE_BRANCH_INSERT, parentBranchId,
            newBranchData.getMergeDestinationBranchId(), branch.getId(), 0);
      }
      checkForCancelled();
   }

   private void populateBaseTransaction(double workAmount, OseeConnection connection, Branch branch, int mergeAddressingQueryId) throws OseeCoreException {
      if (branch.getBranchType() != BranchType.SYSTEM_ROOT) {
         List<Object[]> data = new ArrayList<Object[]>();
         HashSet<Integer> gammas = new HashSet<Integer>(100000);
         int parentBranchId = -1;
         if (branch.hasParentBranch()) {
            parentBranchId = branch.getParentBranch().getId();
         }
         int baseTxId = branch.getBaseTransaction().getId();
         if (branch.getBranchType().isMergeBranch()) {
            populateAddressingToCopy(connection, data, baseTxId, gammas, SELECT_ATTRIBUTE_ADDRESSING_FROM_JOIN,
               parentBranchId, TxChange.NOT_CURRENT.getValue(), mergeAddressingQueryId);
            populateAddressingToCopy(connection, data, baseTxId, gammas, SELECT_ARTIFACT_ADDRESSING_FROM_JOIN,
               parentBranchId, TxChange.NOT_CURRENT.getValue(), mergeAddressingQueryId);
         } else {
            populateAddressingToCopy(connection, data, baseTxId, gammas, SELECT_ADDRESSING, parentBranchId,
               branch.getSourceTransaction().getId());
         }
         if (!data.isEmpty()) {
            getDatabaseService().runBatchUpdate(connection, INSERT_ADDRESSING, data);
         }
      }
      checkForCancelled();
   }

   private void populateAddressingToCopy(OseeConnection connection, List<Object[]> data, int baseTxId, HashSet<Integer> gammas, String query, Object... parameters) throws OseeCoreException {
      IOseeStatement chStmt = getDatabaseService().getStatement(connection);
      try {
         chStmt.runPreparedQuery(10000, query, parameters);
         while (chStmt.next()) {
            checkForCancelled();
            Integer gamma = chStmt.getInt("gamma_id");
            if (!gammas.contains(gamma)) {
               ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));
               TxChange txCurrent = TxChange.getCurrent(modType);
               data.add(new Object[] {baseTxId, gamma, modType.getValue(), txCurrent.getValue(), branch.getId()});
               gammas.add(gamma);
            }
         }
      } finally {
         chStmt.close();
      }
   }

}
