/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.callable;

import java.sql.Timestamp;
import java.util.HashSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcDbType;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.jdbc.JdbcTransaction;
import org.eclipse.osee.jdbc.OseePreparedStatement;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.db.internal.IdentityManager;

/**
 * @author Roberto E. Escobar
 */
public class CreateBranchDatabaseTxCallable extends JdbcTransaction {

   private static final String INSERT_TX_DETAILS =
      "INSERT INTO osee_tx_details (branch_id, transaction_id, osee_comment, time, author, tx_type, build_id) VALUES (?,?,?,?,?,?,?)";

   private static final String UPDATE_BASELINE_BRANCH_TX =
      "UPDATE osee_branch SET baseline_transaction_id = ? WHERE branch_id = ? AND baseline_transaction_id = 1";
   // @formatter:off
   private static final String SELECT_ADDRESSING = "with\n"+
"txs as (select transaction_id, gamma_id, mod_type, app_id from osee_txs where branch_id = ? and transaction_id <= ?),\n\n"+

"txsI as (\n"+
"   SELECT transaction_id, item.gamma_id, mod_type, app_id, 1 as item_type, attr_id as group_id FROM osee_attribute item, txs where txs.gamma_id = item.gamma_id\n"+
"UNION ALL\n"+
"   SELECT transaction_id, item.gamma_id, mod_type, app_id, 2 as item_type, art_id as group_id FROM osee_artifact item, txs where txs.gamma_id = item.gamma_id\n"+
"UNION ALL\n"+
"   SELECT transaction_id, item.gamma_id, mod_type, app_id, 4 as item_type, item.gamma_id as group_id FROM osee_tuple2 item, txs where txs.gamma_id = item.gamma_id\n"+
"UNION ALL\n"+
"   SELECT transaction_id, item.gamma_id, mod_type, app_id, 5 as item_type, item.gamma_id as group_id FROM osee_tuple3 item, txs where txs.gamma_id = item.gamma_id\n"+
"UNION ALL\n"+
"   SELECT transaction_id, item.gamma_id, mod_type, app_id, 6 as item_type, item.gamma_id as group_id FROM osee_tuple4 item, txs where txs.gamma_id = item.gamma_id\n"+
"UNION ALL\n"+
"   SELECT transaction_id, item.gamma_id, mod_type, app_id, 3 as item_type, rel_link_id as group_id FROM osee_relation_link item, txs where txs.gamma_id = item.gamma_id),\n\n"+


"txsM as (SELECT MAX(transaction_id) AS transaction_id, item_type, group_id FROM txsI GROUP BY item_type, group_id)\n\n"+

"select gamma_id, mod_type, app_id from txsI, txsM where txsM.item_type = txsI.item_type and txsM.group_id = txsI.group_id and txsM.transaction_id = txsI.transaction_id order by txsM.transaction_id desc";
   // descending order is used so that the most recent entry will be used if there are multiple rows with the same gamma (an error case)
   // @formatter:on

   private static final String INSERT_ADDRESSING =
      "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current, branch_id, app_id) VALUES (?,?,?,?,?,?)";

   private static final String MERGE_BRANCH_INSERT =
      "INSERT INTO osee_merge (source_branch_id, dest_branch_id, merge_branch_id, commit_transaction_id) VALUES (?,?,?,?)";

   private final static String SELECT_ATTRIBUTE_ADDRESSING_FROM_JOIN =
      "SELECT item.gamma_id, txs.mod_type, txs.app_id FROM osee_attribute item, osee_txs txs, osee_join_id4 artjoin WHERE txs.branch_id = ? AND txs.tx_current <> ? AND txs.gamma_id = item.gamma_id AND item.art_id = artjoin.id2 and artjoin.query_id = ? ORDER BY txs.transaction_id DESC";
   private final static String SELECT_ARTIFACT_ADDRESSING_FROM_JOIN =
      "SELECT item.gamma_id, txs.mod_type, txs.app_id FROM osee_artifact item, osee_txs txs, osee_join_id4 artjoin WHERE txs.branch_id = ? AND txs.tx_current <> ? AND txs.gamma_id = item.gamma_id AND item.art_id = artjoin.id2 and artjoin.query_id = ? ORDER BY txs.transaction_id DESC";

   private static final String TEST_MERGE_BRANCH_EXISTENCE =
      "SELECT COUNT(1) FROM osee_merge WHERE source_branch_id = ? AND dest_branch_id = ?";

   private static final String INSERT_BRANCH =
      "INSERT INTO osee_branch (branch_id, branch_name, parent_branch_id, parent_transaction_id, archived, associated_art_id, branch_type, branch_state, baseline_transaction_id, inherit_access_control) VALUES (?,?,?,?,?,?,?,?,?,?)";
   private static final String SELECT_INHERIT_ACCESS_CONTROL =
      "SELECT inherit_access_control from osee_branch where branch_id = ?";

   private final JdbcClient jdbcClient;
   private final IdentityManager idManager;
   private final CreateBranchData newBranchData;
   private final Long buildVersionId;

   public CreateBranchDatabaseTxCallable(JdbcClient jdbcClient, IdentityManager idManager, CreateBranchData branchData, Long buildVersionId) {
      this.jdbcClient = jdbcClient;
      this.idManager = idManager;
      this.newBranchData = branchData;
      this.buildVersionId = buildVersionId;
   }

   public XResultData checkPreconditions(JdbcConnection connection) {
      XResultData rd = new XResultData();
      BranchId parentBranch = newBranchData.getParentBranch();
      BranchId destinationBranch = newBranchData.getMergeDestinationBranchId();

      if (newBranchData.getBranchType().isMergeBranch()) {
         if (jdbcClient.fetch(connection, 0, TEST_MERGE_BRANCH_EXISTENCE, parentBranch, destinationBranch) > 0) {
            rd.errorf("Existing merge branch detected for [%s] and [%d]", parentBranch, destinationBranch);
         }
      } else if (!newBranchData.getBranchType().isSystemRootBranch()) {
         ArtifactId associatedArtifactId = newBranchData.getAssociatedArtifact();

         // this checks to see if there are any branches that aren't either DELETED or REBASELINED with the same artifact ID
         if (associatedArtifactId.isValid() && ArtifactId.SENTINEL.notEqual(associatedArtifactId)) {
            if (newBranchData.getBranchType().isWorkingBranch()) {
               int count = jdbcClient.fetch(connection, 0,
                  "SELECT (1) FROM osee_branch WHERE associated_art_id = ? AND branch_state NOT IN (?, ?)",
                  newBranchData.getAssociatedArtifact(), BranchState.DELETED, BranchState.REBASELINED);
               if (count > 0) {
                  // the PORT branch type is a special case, a PORT branch can have the same associated artifact
                  // as its related RPCR branch. We need to check to see if there is already a
                  // port branch with the same artifact ID - if the type is port type, then we need an additional check
                  if (newBranchData.getBranchType().equals(BranchType.PORT)) {

                     int portcount = jdbcClient.fetch(connection, 0,
                        "SELECT (1) FROM osee_branch WHERE associated_art_id = ? AND branch_state NOT IN (?, ?) AND branch_type = ?",
                        newBranchData.getAssociatedArtifact(), BranchState.DELETED, BranchState.REBASELINED,
                        BranchType.PORT);
                     if (portcount > 0) {
                        rd.errorf("Existing port branch creation detected for [%s]", newBranchData.getName());
                     }
                  } else {
                     rd.errorf("Existing branch creation detected for [%s]-[%s]", newBranchData.getBranch().getId(),
                        newBranchData.getName());
                  }
               }
            }
         }
      }
      return rd;
   }

   @Override
   public void handleTxWork(JdbcConnection connection) {
      BranchId parentBranch = newBranchData.getParentBranch();

      XResultData rd = checkPreconditions(connection);
      if (rd.isErrors()) {
         throw new OseeStateException(rd.toString());
      }

      BranchId branch = newBranchData.getBranch();

      final String truncatedName = Strings.truncate(newBranchData.getName(), 195, true);

      Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
      TransactionId nextTransactionId = idManager.getNextTransactionId();
      TransactionId tobeTransactionId = nextTransactionId;
      boolean needsUpdate = jdbcClient.getDbType().matches(JdbcDbType.hsql, JdbcDbType.mysql);
      if (needsUpdate) {
         nextTransactionId = TransactionId.valueOf(1);
      }

      TransactionId sourceTx;
      if (newBranchData.getBranchType().isSystemRootBranch()) {
         sourceTx = tobeTransactionId;
      } else {
         sourceTx = TransactionId.SENTINEL;

         if (BranchType.SYSTEM_ROOT != newBranchData.getBranchType()) {
            sourceTx = newBranchData.getFromTransaction();
         }
      }

      int inheritAccessControl = jdbcClient.fetch(connection, 0, SELECT_INHERIT_ACCESS_CONTROL, parentBranch);
      if (inheritAccessControl == 1) {
         newBranchData.setInheritAccess(true);
         // used later to copy the access control values
      }

      //write to branch table
      Object[] toInsert = new Object[] {
         branch,
         truncatedName,
         parentBranch,
         sourceTx,
         BranchArchivedState.UNARCHIVED,
         newBranchData.getAssociatedArtifact(),
         newBranchData.getBranchType(),
         BranchState.CREATED,
         nextTransactionId,
         inheritAccessControl};

      jdbcClient.runPreparedUpdate(connection, INSERT_BRANCH, toInsert);

      nextTransactionId = tobeTransactionId;
      jdbcClient.runPreparedUpdate(connection, INSERT_TX_DETAILS, branch, nextTransactionId,
         newBranchData.getCreationComment(), timestamp, newBranchData.getAuthor(), TransactionDetailsType.Baselined,
         buildVersionId);

      if (needsUpdate) {
         jdbcClient.runPreparedUpdate(connection, UPDATE_BASELINE_BRANCH_TX, nextTransactionId, branch);
      }

      populateBaseTransaction(0.30, connection, nextTransactionId, sourceTx);

      addMergeBranchEntry(0.20, connection);
   }

   private void addMergeBranchEntry(double workAmount, JdbcConnection connection) {
      if (newBranchData.getBranchType().isMergeBranch()) {
         jdbcClient.runPreparedUpdate(connection, MERGE_BRANCH_INSERT, newBranchData.getParentBranch(),
            newBranchData.getMergeDestinationBranchId(), newBranchData.getBranch(), 0);
      }
   }

   private void populateBaseTransaction(double workAmount, JdbcConnection connection, TransactionId baseTxId, TransactionId sourceTxId) {
      if (newBranchData.getBranchType() != BranchType.SYSTEM_ROOT) {
         HashSet<Long> gammas = new HashSet<>(100000);
         BranchId parentBranch = newBranchData.getParentBranch();

         OseePreparedStatement addressing = jdbcClient.getBatchStatement(connection, INSERT_ADDRESSING);
         if (newBranchData.getBranchType().isMergeBranch()) {
            populateAddressingToCopy(connection, addressing, baseTxId, gammas, SELECT_ATTRIBUTE_ADDRESSING_FROM_JOIN,
               parentBranch, TxCurrent.NOT_CURRENT, newBranchData.getMergeAddressingQueryId());
            populateAddressingToCopy(connection, addressing, baseTxId, gammas, SELECT_ARTIFACT_ADDRESSING_FROM_JOIN,
               parentBranch, TxCurrent.NOT_CURRENT, newBranchData.getMergeAddressingQueryId());
         } else {
            populateAddressingToCopy(connection, addressing, baseTxId, gammas, SELECT_ADDRESSING, parentBranch,
               sourceTxId);
         }

         addressing.execute();
      }
   }

   private void populateAddressingToCopy(JdbcConnection connection, OseePreparedStatement addressing, TransactionId baseTxId, HashSet<Long> gammas, String query, Object... parameters) {
      JdbcStatement chStmt = jdbcClient.getStatement(connection);
      try {
         chStmt.runPreparedQueryWithMaxFetchSize(query, parameters);
         BranchId branchId = newBranchData.getBranch();
         while (chStmt.next()) {
            Long gamma = chStmt.getLong("gamma_id");
            if (!gammas.contains(gamma)) {
               ModificationType modType = ModificationType.valueOf(chStmt.getInt("mod_type"));
               Long appId = chStmt.getLong("app_id");
               TxCurrent txCurrent = TxCurrent.getCurrent(modType);
               addressing.addToBatch(baseTxId, gamma, modType, txCurrent, branchId, appId);
               gammas.add(gamma);
            }
         }
      } finally {
         chStmt.close();
      }
   }
}
