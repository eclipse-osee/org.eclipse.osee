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
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.jdbc.OseePreparedStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.sql.RelationalConstants;

/**
 * @author Roberto E. Escobar
 */
public class CreateBranchDatabaseTxCallable extends AbstractDatastoreTxCallable<Void> {

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

   private static final String MERGE_BRANCH_INSERT =
      "INSERT INTO osee_merge (source_branch_id, dest_branch_id, merge_branch_id, commit_transaction_id) VALUES (?,?,?,?)";

   private final static String SELECT_ATTRIBUTE_ADDRESSING_FROM_JOIN =
      "SELECT item.gamma_id, txs.mod_type FROM osee_attribute item, osee_txs txs, osee_join_artifact artjoin WHERE txs.branch_id = ? AND txs.tx_current <> ? AND txs.gamma_id = item.gamma_id AND item.art_id = artjoin.art_id and artjoin.query_id = ? ORDER BY txs.transaction_id DESC";
   private final static String SELECT_ARTIFACT_ADDRESSING_FROM_JOIN =
      "SELECT item.gamma_id, txs.mod_type FROM osee_artifact item, osee_txs txs, osee_join_artifact artjoin WHERE txs.branch_id = ? AND txs.tx_current <> ? AND txs.gamma_id = item.gamma_id AND item.art_id = artjoin.art_id and artjoin.query_id = ? ORDER BY txs.transaction_id DESC";

   private static final String TEST_MERGE_BRANCH_EXISTENCE =
      "SELECT COUNT(1) FROM osee_merge WHERE source_branch_id = ? AND dest_branch_id = ?";

   private static final String INSERT_INTO_BRANCH_ACL =
      "INSERT INTO osee_branch_acl (permission_id, privilege_entity_id, branch_id) VALUES (?, ?, ?)";

   private final String GET_BRANCH_ACCESS_CONTROL_LIST =
      "SELECT permission_id, privilege_entity_id FROM osee_branch_acl WHERE branch_id= ?";

   private static final String INSERT_BRANCH_WITH_GUID =
      "INSERT INTO osee_branch (branch_id, branch_guid, branch_name, parent_branch_id, parent_transaction_id, archived, associated_art_id, branch_type, branch_state, baseline_transaction_id, inherit_access_control) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
   private static final String INSERT_BRANCH =
      "INSERT INTO osee_branch (branch_id, branch_name, parent_branch_id, parent_transaction_id, archived, associated_art_id, branch_type, branch_state, baseline_transaction_id, inherit_access_control) VALUES (?,?,?,?,?,?,?,?,?,?)";
   protected static final int NULL_PARENT_BRANCH_ID = -1;
   private static final String SELECT_INHERIT_ACCESS_CONTROL =
      "SELECT inherit_access_control from osee_branch where branch_id = ?";

   private final IdentityManager idManager;

   private final CreateBranchData newBranchData;

   public CreateBranchDatabaseTxCallable(Log logger, OrcsSession session, JdbcClient jdbcClient, IdentityManager idManager, CreateBranchData branchData) {
      super(logger, session, jdbcClient);
      this.idManager = idManager;
      this.newBranchData = branchData;
   }

   private void checkPreconditions(Long parentBranch, Long destinationBranch) throws OseeCoreException {
      if (newBranchData.getBranchType().isMergeBranch()) {
         if (getJdbcClient().runPreparedQueryFetchObject(0, TEST_MERGE_BRANCH_EXISTENCE, parentBranch,
            destinationBranch) > 0) {
            throw new OseeStateException("Existing merge branch detected for [%d] and [%d]", parentBranch,
               destinationBranch);
         }
      } else if (!newBranchData.getBranchType().isSystemRootBranch()) {
         int associatedArtifactId = newBranchData.getAssociatedArtifactId();

         // this checks to see if there are any branches that aren't either DELETED or REBASELINED with the same artifact ID
         if (associatedArtifactId > -1 && associatedArtifactId != SystemUser.OseeSystem.getUuid()) {
            int count = getJdbcClient().runPreparedQueryFetchObject(0,
               "SELECT (1) FROM osee_branch WHERE associated_art_id = ? AND branch_state NOT IN (?, ?)",
               newBranchData.getAssociatedArtifactId(), BranchState.DELETED.getValue(),
               BranchState.REBASELINED.getValue());
            if (count > 0) {
               // the PORT branch type is a special case, a PORT branch can have the same associated artifact
               // as its related RPCR branch. We need to check to see if there is already a
               // port branch with the same artifact ID - if the type is port type, then we need an additional check
               if (newBranchData.getBranchType().equals(BranchType.PORT)) {

                  int portcount = getJdbcClient().runPreparedQueryFetchObject(0,
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

   @Override
   protected Void handleTxWork(JdbcConnection connection) throws OseeCoreException {
      Long parentBranchUuid = newBranchData.getParentBranchUuid();
      Long destinationBranchUuid = newBranchData.getMergeDestinationBranchId();

      checkPreconditions(parentBranchUuid, destinationBranchUuid);

      long uuid = newBranchData.getUuid();

      final String truncatedName = Strings.truncate(newBranchData.getName(), 195, true);

      Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
      int nextTransactionId = idManager.getNextTransactionId();

      int sourceTx;
      if (newBranchData.getBranchType().isSystemRootBranch()) {
         sourceTx = nextTransactionId;
      } else {
         sourceTx = RelationalConstants.TRANSACTION_SENTINEL;

         if (BranchType.SYSTEM_ROOT != newBranchData.getBranchType()) {
            sourceTx = newBranchData.getFromTransaction().getGuid();
         }
      }

      int inheritAccessControl = 0;
      if (parentBranchUuid != null) {
         inheritAccessControl =
            getJdbcClient().runPreparedQueryFetchObject(connection, 0, SELECT_INHERIT_ACCESS_CONTROL, parentBranchUuid);
      }

      //write to branch table
      boolean insertBranchGuid = isBranchGuidNeeded(connection);
      long parentBranchId = parentBranchUuid != null ? parentBranchUuid : NULL_PARENT_BRANCH_ID;
      Object[] toInsert;
      if (insertBranchGuid) {
         toInsert = new Object[] {
            uuid,
            GUID.create(),
            truncatedName,
            parentBranchId,
            sourceTx,
            BranchArchivedState.UNARCHIVED.getValue(),
            newBranchData.getAssociatedArtifactId(),
            newBranchData.getBranchType().getValue(),
            BranchState.CREATED.getValue(),
            nextTransactionId,
            inheritAccessControl};
      } else {
         toInsert = new Object[] {
            uuid,
            truncatedName,
            parentBranchId,
            sourceTx,
            BranchArchivedState.UNARCHIVED.getValue(),
            newBranchData.getAssociatedArtifactId(),
            newBranchData.getBranchType().getValue(),
            BranchState.CREATED.getValue(),
            nextTransactionId,
            inheritAccessControl};
      }

      String insertBranch = insertBranchGuid ? INSERT_BRANCH_WITH_GUID : INSERT_BRANCH;
      getJdbcClient().runPreparedUpdate(connection, insertBranch, toInsert);

      if (inheritAccessControl != 0) {
         copyAccessRules(connection, newBranchData.getUserArtifactId(), parentBranchUuid, uuid);
      }

      getJdbcClient().runPreparedUpdate(connection, INSERT_TX_DETAILS, uuid, nextTransactionId,
         newBranchData.getCreationComment(), timestamp, newBranchData.getUserArtifactId(),
         TransactionDetailsType.Baselined.getId());

      populateBaseTransaction(0.30, connection, nextTransactionId, sourceTx);

      addMergeBranchEntry(0.20, connection);
      return null;
   }

   private boolean isBranchGuidNeeded(JdbcConnection connection) {
      return getJdbcClient().runPreparedQueryFetchObject(connection, false,
         "select osee_value from osee_info where osee_key = ?", "osee.insert.branch.guid.on.create");
   }

   private void addMergeBranchEntry(double workAmount, JdbcConnection connection) {
      if (newBranchData.getBranchType().isMergeBranch()) {
         long parentBranchId = newBranchData.getParentBranchUuid() != null ? newBranchData.getParentBranchUuid() : -1;
         getJdbcClient().runPreparedUpdate(connection, MERGE_BRANCH_INSERT, parentBranchId,
            newBranchData.getMergeDestinationBranchId(), newBranchData.getUuid(), 0);
      }
      checkForCancelled();
   }

   private void populateBaseTransaction(double workAmount, JdbcConnection connection, int baseTxId, int sourceTxId) throws OseeCoreException {
      if (newBranchData.getBranchType() != BranchType.SYSTEM_ROOT) {
         HashSet<Integer> gammas = new HashSet<Integer>(100000);
         long parentBranchId = -1;

         OseePreparedStatement addressing = getJdbcClient().getBatchStatement(connection, INSERT_ADDRESSING);
         if (newBranchData.getParentBranchUuid() != null) {
            parentBranchId = newBranchData.getParentBranchUuid();
         }
         if (newBranchData.getBranchType().isMergeBranch()) {
            populateAddressingToCopy(connection, addressing, baseTxId, gammas, SELECT_ATTRIBUTE_ADDRESSING_FROM_JOIN,
               parentBranchId, TxChange.NOT_CURRENT.getValue(), newBranchData.getMergeAddressingQueryId());
            populateAddressingToCopy(connection, addressing, baseTxId, gammas, SELECT_ARTIFACT_ADDRESSING_FROM_JOIN,
               parentBranchId, TxChange.NOT_CURRENT.getValue(), newBranchData.getMergeAddressingQueryId());
         } else {
            populateAddressingToCopy(connection, addressing, baseTxId, gammas, SELECT_ADDRESSING, parentBranchId,
               sourceTxId);
         }

         addressing.execute();
      }
      checkForCancelled();
   }

   private void populateAddressingToCopy(JdbcConnection connection, OseePreparedStatement addressing, int baseTxId, HashSet<Integer> gammas, String query, Object... parameters) throws OseeCoreException {
      JdbcStatement chStmt = getJdbcClient().getStatement(connection);
      try {
         chStmt.runPreparedQuery(JdbcConstants.JDBC__MAX_FETCH_SIZE, query, parameters);
         while (chStmt.next()) {
            checkForCancelled();
            Integer gamma = chStmt.getInt("gamma_id");
            if (!gammas.contains(gamma)) {
               ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));
               TxChange txCurrent = TxChange.getCurrent(modType);
               addressing.addToBatch(baseTxId, gamma, modType.getValue(), txCurrent.getValue(),
                  newBranchData.getUuid());
               gammas.add(gamma);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   private void copyAccessRules(JdbcConnection connection, int userArtId, Long parentBranch, Long branchUuid) {
      int owner = PermissionEnum.OWNER.getPermId();
      int deny = PermissionEnum.DENY.getPermId();

      List<Object[]> data = new ArrayList<Object[]>();
      JdbcStatement chStmt = getJdbcClient().getStatement(connection);
      try {
         chStmt.runPreparedQuery(JdbcConstants.JDBC__MAX_FETCH_SIZE, GET_BRANCH_ACCESS_CONTROL_LIST, parentBranch);
         while (chStmt.next()) {
            int permissionId = chStmt.getInt("permission_id");
            int priviledgeId = chStmt.getInt("privilege_entity_id");
            if (priviledgeId == userArtId && permissionId < owner && permissionId != deny) {
               permissionId = owner;
            }
            data.add(new Object[] {permissionId, priviledgeId, branchUuid});
         }
      } finally {
         Lib.close(chStmt);
      }
      if (!data.isEmpty()) {
         getJdbcClient().runBatchUpdate(INSERT_INTO_BRANCH_ACL, data);
      }
   }
}
