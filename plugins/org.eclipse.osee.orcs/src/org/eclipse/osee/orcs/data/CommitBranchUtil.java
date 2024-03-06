/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.orcs.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeMergeData;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.ConflictData;
import org.eclipse.osee.framework.core.data.ConflictUpdateData;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.MergeData;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.ValidateCommitResult;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ConflictType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OseeDb;

public class CommitBranchUtil {
   public static final String GET_CONFLICT_KEYS_GENERIC =
      "select %s, t1.transaction_id workingTx, t1.tx_current workingTxCurrent, t1.mod_type workingModType, t1.gamma_id workingGammaId, t1.app_id workingAppId," + //
         "                  t2.transaction_id currentDestTx, t2.tx_current currentDestTxCurrent, t2.mod_type currentDestModType, t2.gamma_id currentDestGammaId, t2.app_id currentDestAppId," + //
         "                  t3.transaction_id baselineTxTx, t3.tx_current baselineTxTxCurrent, t3.mod_type baselineTxModType, t3.gamma_id baselineTxGammaId, t3.app_id baselineTxAppId " + //
         "from " + //
         " ( " + //--all changes on working branch
         "  select txs.transaction_id, txs.tx_current, txs.mod_type, txs.gamma_id, txs.app_id, %s " + // list of keys
         "  from OSEE_TXS txs, %s item " + //
         "  where txs.BRANCH_ID = ? AND " + //
         "        txs.transaction_ID > ? and txs.TX_CURRENT <> 0 and " + //
         "        txs.gamma_id = item.gamma_id " + //
         "  ) t1 " + "  left outer join " + " ( " + // --values for key on baseline
         "  select txs.transaction_id, txs.tx_current, txs.mod_type, txs.gamma_id, txs.app_id, %s " + //list of keys
         "  from OSEE_TXS txs, %s item " + //
         "  where txs.BRANCH_ID = ? AND " + //
         "      txs.TX_CURRENT <> 0 and " + "      txs.gamma_id = item.gamma_id " + "  ) t2 " + "  on %s " + //keys to line up with t1.x = t2.x
         "  left outer join " + " ( " + //how this key appeared when working branch was baselined...if gamma from baseline != gamma here then there is a merge conflict
         "  select txs.transaction_id, txs.tx_current, txs.mod_type, txs.gamma_id, txs.app_id, %s " + //list of keys
         "  from OSEE_TXS txs, %s item " + //
         "  where txs.BRANCH_ID = ? AND " + //
         "      txs.transaction_ID = ? and " + //
         "      txs.gamma_id = item.gamma_id" + //
         " ) t3 " + //
         " on   %s ";
   public static final String ARTJOIN =
      "osee_txs txs, osee_artifact art where currentDestGammaid is not null and baselineTxGammaid is not null and currentDestGammaid != baselineTxGammaid and txs.branch_id = ? and txs.gamma_id = art.gamma_id and txs.tx_current = 1 and art.art_id = %s.art_id";
   public static final String CONFLICT_CLEANUP =
      "DELETE FROM osee_conflict t1 WHERE merge_branch_id = ? AND NOT EXISTS (SELECT 'X' FROM osee_join_id4 WHERE query_id = ? AND t1.conflict_id = id2 AND (t1.conflict_type = id3 or id3 is NULL))";

   public static final String GET_DESTINATION_BRANCHES =
      "SELECT dest_branch_id FROM osee_merge WHERE source_branch_id = ?";

   public static final String GET_MERGE_BRANCH =
      "SELECT merge_branch_id FROM osee_merge WHERE source_branch_id = ? AND dest_branch_id = ?";

   public static final String GET_COMMIT_TRANSACTION =
      "SELECT commit_transaction_id txId FROM osee_merge, osee_tx_details WHERE source_branch_id = ? AND dest_branch_id = ? and commit_transaction_id > 0  " + //
         " union " + //
         "SELECT transaction_id txId FROM osee_tx_details tx, osee_branch b WHERE b.branch_id = ? and osee_comment = 'Commit Branch '||b.branch_name AND tx.branch_id = ?";
   public static final String COMMIT_COMMENT = "Commit Branch ";
   public static String artQuery =
      "artConflicts( art_id, workingTx, workingTxCurrent, workingModType, workingGammaId, workingAppId, " + //
         "currentDestTx, currentDestTxCurrent, currentDestModType, currentDestGammaId, currentDestAppId, " + //
         "baselineTxTx, baselineTxTxCurrent, baselineTxModType, baselineTxGammaId, baselineTxAppId) as ( " + String.format(
            CommitBranchUtil.GET_CONFLICT_KEYS_GENERIC, "t1.art_id", "item.art_id", "osee_artifact", "item.art_id",
            "osee_artifact", "t1.art_id = t2.art_id", "item.art_id", "osee_artifact", "t1.art_id = t3.art_id)");

   public static String attrQuery =
      "attrConflicts(art_id, attr_type_id, attr_id, workingTx, workingTxCurrent, workingModType, workingGammaId, workingAppId," + //
         "                  currentDestTx, currentDestTxCurrent, currentDestModType, currentDestGammaId, currentDestAppId, " + //
         "                  baselineTxTx, baselineTxTxCurrent, baselineTxModType, baselineTxGammaId, baselineTxAppId) as ( " + //
         String.format(CommitBranchUtil.GET_CONFLICT_KEYS_GENERIC, "t1.art_id, t1.attr_type_id, t1.attr_id",
            "item.art_id, item.attr_type_id, item.attr_id", "osee_attribute",
            "item.art_id, item.attr_type_id, item.attr_id", "osee_attribute",
            "t1.art_id = t2.art_id and t1.attr_type_id = t2.attr_type_id and t1.attr_id = t2.attr_id",
            "item.art_id, item.attr_type_id, item.attr_id", "osee_attribute",
            "t1.art_id = t3.art_id and t1.attr_type_id = t3.attr_type_id and t1.attr_id = t3.attr_id)");

   private static final String UPDATE_CONFLICT_STATUS =
      "UPDATE osee_conflict SET status = ? WHERE source_gamma_id = ? AND dest_gamma_id = ? AND merge_branch_id = ?";

   private static final String getMergeData =
      "select c.conflict_type, c.source_gamma_id, c.dest_gamma_id, art.art_id, art.art_type_id, nameAttr.value art_name, src_item.attr_type_id item_type_id, src_item.attr_id item_id, " + //
         "src_item.value src_item_value, dest_item.value dest_item_value, merge_item.value merge_item_value,  " + //
         "src_item.uri src_uri, dest_item.uri dest_uri, merge_item.uri merge_uri, " + //
         "1 src_app_id, 1 dest_app_id, 1 merge_app_id, c.conflict_id, c.status conflict_status " + //
         "from osee_attribute src_item, osee_attribute dest_item, osee_conflict c, osee_txs txs, osee_attribute merge_item, " + //
         "osee_artifact art, osee_txs artTxs, osee_attribute nameAttr, osee_txs attrTxs " + //
         "where c.merge_branch_id = ? and src_item.gamma_id = c.source_gamma_id and dest_item.gamma_id = c.dest_gamma_id " + //
         "and txs.branch_id = ? and txs.gamma_id = merge_item.gamma_id and txs.tx_current = 1 and " + //
         "merge_item.attr_type_id = src_item.attr_type_id and merge_item.art_id = src_item.art_id  " + //
         "and src_item.attr_id = dest_item.attr_id and src_item.attr_id = merge_item.attr_id " + //
         "and artTxs.branch_id = ? and artTxs.gamma_id = art.gamma_id and artTxs.tx_current = 1 and art.art_id = src_item.art_id " + //
         "and attrTxs.branch_id = ? and attrTxs.gamma_id = nameAttr.gamma_id and attrTxs.tx_current = 1 and nameAttr.art_id = src_item.art_id and nameAttr.attr_type_id = ? " + //
         "union " + //
         "select c.conflict_type, c.source_gamma_id, c.dest_gamma_id, src_item.art_id, src_item.art_type_id, nameAttr.value art_name, src_item.art_type_id item_type_id, src_item.art_id item_id, " + //
         "'na' src_item_value, 'na' dest_item_value, 'na' merge_item_value, 'na' src_uri, 'na' dest_uri, 'na' merge_uri,  " + //
         "1 src_app_id, 1 dest_app_id, 1 merge_app_id, c.conflict_id, c.status conflict_status " + //
         "from osee_artifact src_item, osee_artifact dest_item, osee_conflict c, osee_txs txs, osee_artifact merge_item, " + //
         "osee_attribute nameAttr, osee_txs attrTxs " + //
         "where c.merge_branch_id = ? and src_item.gamma_id = c.source_gamma_id and dest_item.gamma_id = c.dest_gamma_id " + //
         "and txs.branch_id = ? and txs.gamma_id = merge_item.gamma_id and txs.tx_current = 1 and merge_item.art_id = src_item.art_id " + //
         "and attrTxs.branch_id = ? and attrTxs.gamma_id = nameAttr.gamma_id and attrTxs.tx_current = 1 and nameAttr.art_id = src_item.art_id and nameAttr.attr_type_id = ? " + //
         "order by 2"; //order by art_id

   private static final String getMergeBranchId =
      "select merge_branch_id from osee_merge where source_branch_id = ? and dest_branch_id = ?";

   public static List<MergeData> getMergeData(BranchId mergeBranchId, JdbcClient jdbcClient,
      OrcsTokenService tokenService) {
      List<MergeData> mergeData = new ArrayList<>();
      try (JdbcStatement stmt = jdbcClient.getStatement()) {
         stmt.runPreparedQuery(getMergeData, mergeBranchId, mergeBranchId, mergeBranchId, mergeBranchId,
            CoreAttributeTypes.Name.getId(), mergeBranchId, mergeBranchId, mergeBranchId,
            CoreAttributeTypes.Name.getId()); //passing name attr_type_id to retrieve name in this query instead of having to query for the token separately
         while (stmt.next()) {
            ConflictType conflictType = ConflictType.valueOf(stmt.getInt("conflict_type"));
            ConflictStatus conflictStatus = ConflictStatus.valueOf(stmt.getInt("conflict_status"));
            Long conflictId = stmt.getLong("conflict_id");
            ArtifactId artId = ArtifactId.valueOf(stmt.getLong("art_id"));
            Long artTypeId = stmt.getLong("art_type_id");
            String name = stmt.getString("art_name");
            Long itemTypeId = stmt.getLong("item_type_id");
            Long itemId = stmt.getLong("item_id");
            String srcItemValue = stmt.getString("src_item_value");
            String destItemValue = stmt.getString("dest_item_value");
            String mergeItemValue = stmt.getString("merge_item_value");
            String srcItemUri = stmt.getString("src_uri");
            String destItemUri = stmt.getString("dest_uri");
            String mergeItemUri = stmt.getString("merge_uri");
            String srcGammaId = stmt.getString("source_gamma_id");
            String destGammaId = stmt.getString("dest_gamma_id");
            @SuppressWarnings("unused")
            String srcItemAppId = stmt.getString("src_app_id");
            @SuppressWarnings("unused")
            String destItemAppId = stmt.getString("dest_app_id");
            @SuppressWarnings("unused")
            String mergeItemAppId = stmt.getString("merge_app_id");

            AttributeMergeData attrMergeData = null;
            if (conflictType.equals(ConflictType.ATTRIBUTE)) {
               attrMergeData = new AttributeMergeData(tokenService.getAttributeType(itemTypeId),
                  AttributeId.valueOf(itemId), srcItemValue, mergeItemValue, destItemValue, srcItemUri, mergeItemUri,
                  destItemUri, srcGammaId, destGammaId);
            }
            MergeData md = new MergeData(artId, tokenService.getArtifactType(artTypeId), name, conflictType,
               conflictStatus, conflictId, attrMergeData);
            mergeData.add(md);
         }
         stmt.close();
      }
      return mergeData;
   }

   public static BranchId getMergeBranchId(OrcsApi orcsApi, BranchId sourceBranch, BranchId destBranch) {
      BranchId mergeBranchId = BranchId.SENTINEL;
      try (JdbcStatement stmt = orcsApi.getJdbcService().getClient().getStatement()) {
         stmt.runPreparedQuery(getMergeBranchId, sourceBranch, destBranch);
         if (stmt.next()) {
            mergeBranchId = BranchId.valueOf(stmt.getLong("merge_branch_id"));
         }
         stmt.close();
      }
      return mergeBranchId;
   }

   public static TransactionToken getCommitTransaction(OrcsApi orcsApi, BranchId sourceBranch, BranchId destBranch) {
      TransactionToken transactionId = TransactionToken.SENTINEL;
      try (JdbcStatement stmt = orcsApi.getJdbcService().getClient().getStatement()) {
         stmt.runPreparedQuery(GET_COMMIT_TRANSACTION, sourceBranch, destBranch, sourceBranch, destBranch);
         if (stmt.next()) {
            transactionId = TransactionToken.valueOf(stmt.getLong("tx_id"), destBranch);
         }
         stmt.close();
      }
      return transactionId;
   }

   public static int updateConflictStatus(List<ConflictUpdateData> conflictStatusUpdates, JdbcClient jdbcClient) {
      List<Object[]> updateData = new ArrayList<>();
      for (ConflictUpdateData conflictData : conflictStatusUpdates) {
         updateData.add(new Object[] {
            conflictData.getStatus().getValue(),
            conflictData.getSourceGammaId(),
            conflictData.getDestGammaId(),
            conflictData.getMergeBranchId()});
      }
      if (!updateData.isEmpty()) {
         return jdbcClient.runBatchUpdate(jdbcClient.getConnection(), UPDATE_CONFLICT_STATUS, updateData);
      }
      return 0;
   }

   public static void loadConflicts(BranchId sourceBranchId, BranchId destinationBranchId, List<ConflictData> conflicts,
      JdbcClient jdbcClient) {
      BranchId mergeBranchId =
         BranchId.valueOf(jdbcClient.fetch(-1L, GET_MERGE_BRANCH, sourceBranchId, destinationBranchId));
      if (mergeBranchId.isValid()) {
         List<Object[]> insertData = new ArrayList<>();
         for (ConflictData conflictData : conflicts) {
            Long conflictId =
               (conflictData.getAttrTypeId().isValid()) ? conflictData.getAttrId().getId() : conflictData.getArtId().getId();
            ConflictType conflictType =
               (conflictData.getAttrTypeId().isValid()) ? ConflictType.ATTRIBUTE : ConflictType.ARTIFACT;
            insertData.add(new Object[] {
               mergeBranchId,
               conflictData.getWorkingGammaId(),
               conflictId,
               conflictData.getCurrentDestGammaId(),
               conflictType.getValue(),
               ConflictStatus.UNTOUCHED.getValue()});
         }
         if (!insertData.isEmpty()) {
            jdbcClient.runBatchUpdate(jdbcClient.getConnection(), OseeDb.OSEE_CONFLICT_TABLE.getInsertSql(),
               insertData);
         }

      }
   }

   public static String getAddressingConflictQuery(BranchId branch, BranchId destinationBranch,
      TransactionId baselineTx, OrcsTokenService tokenService) {

      String singleAttrTypes = "1";

      String allQuery = "with " + artQuery + " , " + attrQuery + ", conflictArts(art_id) as (" + //
         "select art.art_id " + //
         "from attrConflicts t4, osee_txs txs, osee_artifact art where currentDestGammaId is null and baselineTxGammaId is null " + //
         "and exists (select null from osee_txs txs, osee_attribute attr where txs.branch_id = ? and txs.gamma_id = attr.gamma_id and attr.attr_type_id in (" + //
         singleAttrTypes + ") and attr.art_id = t4.art_id " + //
         "and tx_current = 1) and txs.branch_id = ? and txs.tx_current = 1 and txs.gamma_id = art.gamma_id and art.art_id = t4.art_id union " + //
         "select art.art_id from attrConflicts t5, " + String.format(ARTJOIN, "t5") + " union " + //
         "select art.art_id from artConflicts t7, " + String.format(ARTJOIN, "t7") + ") " + //
         "select txs.gamma_id, mod_type, app_id from conflictArts ca, osee_txs txs, osee_artifact art " + //
         "where ca.art_id = art.art_id and txs.branch_id = ? and txs.gamma_id = art.gamma_id and txs.tx_current <> 0 " + //
         "union " + //
         "select txs.gamma_id, mod_type, app_id from conflictArts ca, osee_txs txs, osee_attribute attr " + //
         "where ca.art_id = attr.art_id and txs.branch_id = ? and txs.gamma_id = attr.gamma_id and txs.tx_current <> 0";
      return allQuery;

   }

   public static ValidateCommitResult validateCommitBranch(OrcsApi orcsApi, BranchId branch,
      BranchId destinationBranch) {
      String singleAttrTypes =
         orcsApi.tokenService().getSingletonAttributeTypes().stream().map(a -> a.getIdString()).collect(
            Collectors.joining(","));

      ValidateCommitResult validateCommit = new ValidateCommitResult();
      Branch sourceBranch =
         orcsApi.getQueryFactory().branchQuery().andId(branch).getResults().getAtMostOneOrDefault(Branch.SENTINEL);
      Branch destBranch =
         orcsApi.getQueryFactory().branchQuery().andId(destinationBranch).getResults().getAtMostOneOrDefault(
            Branch.SENTINEL);
      validateCommit.setSourceBranch(sourceBranch);
      validateCommit.setDestinationBranch(destBranch);
      if (sourceBranch.isValid()) {
         TransactionId baselineTx = sourceBranch.getBaselineTx();
         String allQuery = "with savedConflicts(conflict_count, resolved_count) as (" + //
            "select distinct count(conflict_id) over () conflict_count, sum(case when c.status = " + ConflictStatus.RESOLVED.getValue() + " then 1 else 0 end) over () resolved_count " + //
            "from osee_merge m, osee_conflict c where m.source_branch_id = ? and m.dest_branch_id = ? and commit_transaction_id = 0 and m.merge_branch_id = c.merge_branch_id), " + //
            artQuery + " , " + attrQuery + //
            "select count('x') value, 'conflicts' value_type from (select 'x' from attrConflicts t4 where currentDestGammaId is null and baselineTxGammaId is null " + //
            "and exists (select null from osee_txs txs, osee_attribute attr where txs.branch_id = ? and txs.gamma_id = attr.gamma_id and attr.attr_type_id in (" + //
            singleAttrTypes + ") and attr.art_id = t4.art_id " + //
            "and tx_current = 1) union all " + //
            "select 'x' from attrConflicts t5, " + String.format(ARTJOIN, "t5") + " union all " + //
            "select 'x' from artConflicts t7, " + String.format(ARTJOIN, "t7") + ") t8 union all " + //
            "select txId value, 'commit_transaction_id' value_type from (" + GET_COMMIT_TRANSACTION + ") t9 " + //
            "union all select resolved_count value, 'resolved_count' value_type from savedConflicts";

         try (JdbcStatement stmt = orcsApi.getJdbcService().getClient().getStatement()) {
            stmt.runPreparedQuery(allQuery, branch.getId(), destinationBranch.getId(), branch.getId(),
               baselineTx.getId(), destinationBranch.getId(), branch.getId(), baselineTx.getId(), branch.getId(),
               baselineTx.getId(), destinationBranch.getId(), branch.getId(), baselineTx.getId(),
               destinationBranch.getId(), branch.getId(), branch.getId(), branch.getId(), destinationBranch.getId(),
               branch.getId(), destinationBranch.getId());
            while (stmt.next()) {
               String value = stmt.getString("value");
               String value_type = stmt.getString("value_type");

               switch (value_type) {
                  case "conflicts": {
                     validateCommit.setConflictCount(Integer.parseInt(value));
                     break;
                  }
                  case "commit_transaction_id": {
                     validateCommit.setTx(TransactionToken.valueOf(Long.parseLong(value), destinationBranch));
                     break;
                  }
                  case "resolved_count": {
                     validateCommit.setConflictsResolved(Integer.parseInt(value));
                  }
               }
            }
            stmt.close();
         }
      }
      if (validateCommit.getSourceBranch().isValid() && validateCommit.getDestinationBranch().isValid() && !validateCommit.getTx().isValid() && (validateCommit.getConflictCount() == 0 || validateCommit.getConflictCount() == validateCommit.getConflictsResolved())) {
         validateCommit.setCommitable(true);
      }
      return validateCommit;
   }

   public static List<ConflictData> populateMergeConflictData(BranchId sourceBranch, BranchId destinationBranch,
      TransactionId baselineTx, JdbcClient jdbcClient, OrcsTokenService tokenService) {
      String singleAttrTypes = "1";
      List<ConflictData> conflicts = new ArrayList<>();

      String allQuery = "with " + artQuery + " , " + attrQuery + //
         "select art.art_id, art.art_type_id, attr_type_id, attr_id, workingTx, workingTxCurrent, workingModType, workingGammaId, workingAppId, " + //
         "currentDestTx, currentDestTxCurrent, currentDestModType, currentDestGammaId, currentDestAppId, " + //
         "baselineTxTx, baselineTxTxCurrent, baselineTxModType, baselineTxGammaId, baselineTxAppId " + //
         "from attrConflicts t4, osee_txs txs, osee_artifact art where currentDestGammaId is null and baselineTxGammaId is null " + //
         "and exists (select null from osee_txs txs, osee_attribute attr where txs.branch_id = ? and txs.gamma_id = attr.gamma_id and attr.attr_type_id in (" + //
         singleAttrTypes + ") and attr.art_id = t4.art_id " + //
         "and tx_current = 1) and txs.branch_id = ? and txs.tx_current = 1 and txs.gamma_id = art.gamma_id and art.art_id = t4.art_id union all " + //
         "select art.art_id, art.art_type_id, attr_type_id, attr_id, workingTx, workingTxCurrent, workingModType, workingGammaId, workingAppId, " + //
         "currentDestTx, currentDestTxCurrent, currentDestModType, currentDestGammaId, currentDestAppId, " + //
         "baselineTxTx, baselineTxTxCurrent, baselineTxModType, baselineTxGammaId, baselineTxAppId " + //
         "from attrConflicts t5, " + String.format(ARTJOIN, "t5") + " union all " + //
         "select art.art_id, art.art_type_id, -1 attr_type_id, -1 attr_id, workingTx, workingTxCurrent, workingModType, workingGammaId, workingAppId, " + //
         "currentDestTx, currentDestTxCurrent, currentDestModType, currentDestGammaId, currentDestAppId, " + //
         "baselineTxTx, baselineTxTxCurrent, baselineTxModType, baselineTxGammaId, baselineTxAppId " + //
         "from artConflicts t7, " + String.format(ARTJOIN, "t7");

      try (JdbcStatement stmt = jdbcClient.getStatement()) {
         stmt.runPreparedQuery(allQuery, sourceBranch, baselineTx, destinationBranch, sourceBranch, baselineTx,
            sourceBranch, baselineTx, destinationBranch, sourceBranch, baselineTx, destinationBranch, sourceBranch,
            sourceBranch, sourceBranch);
         while (stmt.next()) {
            ArtifactId artId = ArtifactId.valueOf(stmt.getLong("art_id"));
            ArtifactTypeToken artType = ArtifactTypeToken.valueOf(stmt.getString("art_type_id"));
            AttributeTypeId attrTypeId = AttributeTypeId.valueOf(stmt.getLong("attr_type_id"));
            AttributeId attrId = AttributeId.valueOf(stmt.getLong("attr_id"));
            //working values
            TransactionId workingTx = TransactionId.valueOf(stmt.getLong("workingTx"));
            TxCurrent workingTxCurrent = TxCurrent.valueOf(stmt.getInt("workingTxCurrent"));
            ModificationType workingModType = ModificationType.valueOf(stmt.getLong("workingModType"));
            GammaId workingGammaId = GammaId.valueOf(stmt.getLong("workingGammaId"));
            //destination current values
            TransactionId currentDestTx = TransactionId.valueOf(stmt.getLong("currentDestTx"));
            TxCurrent currentDestTxCurrent = TxCurrent.valueOf(stmt.getInt("currentDestTxCurrent"));
            ModificationType currentDestModType = ModificationType.valueOf(stmt.getLong("currentDestModType"));
            GammaId currentDestGammaId = GammaId.valueOf(stmt.getLong("currentDestGammaId"));
            //baselineTx values
            TransactionId baselineTxTx = TransactionId.valueOf(stmt.getLong("baselineTxTx"));
            TxCurrent baselineTxTxCurrent = TxCurrent.valueOf(stmt.getInt("baselineTxTxCurrent"));
            ModificationType baselineTxModType = ModificationType.valueOf(stmt.getLong("workingModType"));
            GammaId baselineTxGammaId = GammaId.valueOf(stmt.getLong("workingGammaId"));
            conflicts.add(new ConflictData(artId, artType, attrTypeId, attrId, workingTx, workingTxCurrent,
               workingModType, workingGammaId, currentDestTx, currentDestTxCurrent, currentDestModType,
               currentDestGammaId, baselineTxTx, baselineTxTxCurrent, baselineTxModType, baselineTxGammaId));
         }
         stmt.close();
      }
      return conflicts;
   }

}
