/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.change;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.TupleTypeId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeItemUtil;
import org.eclipse.osee.framework.core.model.change.ChangeVersion;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.jdbc.SqlTable;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.db.internal.sql.join.ExportImportJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.search.ApplicabilityQuery;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 * @author Ryan Schmitt
 * @author Jeff C. Phillips
 */
public class LoadDeltasBetweenBranches {
   // @formatter:off
      private static final String SELECT_ALL_SOURCE_ADDRESSING =
      "with\n" + "txsOuter as (select transaction_id, gamma_id, mod_type, app_id from %s txs where \n" +
         "branch_id = ? and txs.tx_current <> ? and transaction_id <> ? AND \n" +
         "NOT EXISTS (SELECT 1 FROM %s txs1 WHERE txs1.branch_id = ? AND txs1.transaction_id = ? \n" +
         "AND txs1.gamma_id = txs.gamma_id and txs1.mod_type = txs.mod_type and txs1.app_id = txs.app_id)) \n" +
         "SELECT 1 as table_type, attr_type_id as item_type_id, attr_id as item_id, art_id as item_first, 0 as item_second, 0 as item_third, 0 as item_fourth, item.value as item_value, item.uri as item_uri, item.gamma_id, mod_type, app_id, transaction_id, kv.value as app_value \n" +
         "FROM osee_attribute item, txsOuter, osee_key_value kv where txsOuter.gamma_id = item.gamma_id AND txsOuter.app_id = kv.key\n" +
         "UNION ALL\n" +
         "SELECT 2 as table_type, art_type_id as item_type_id, art_id as item_id, 0 as item_first, 0 as item_second, 0 as item_third, 0 as item_fourth, 'na' as item_value, 'na' as item_uri, item.gamma_id, mod_type, app_id, transaction_id, kv.value as app_value \n" +
         "FROM osee_artifact item, txsOuter, osee_key_value kv where txsOuter.gamma_id = item.gamma_id AND txsOuter.app_id = kv.key\n" +
         "UNION ALL\n" +
         "SELECT 3 as table_type, rel_link_type_id as item_type_id, rel_link_id as item_id,  a_art_id as item_first, b_art_id as item_second, 0 as item_third, 0 as item_fourth, rationale as item_value, 'na' as item_uri, item.gamma_id, mod_type, app_id, transaction_id, kv.value as app_value \n" +
         "FROM osee_relation_link item, txsOuter,osee_key_value kv where txsOuter.gamma_id = item.gamma_id AND txsOuter.app_id = kv.key\n" +
         "UNION ALL\n" +
         "SELECT 4 as table_type, tuple_type as item_type_id, 0 as item_id, e1 as item_first, e2 as item_second, 0 as item_third, 0 as item_fourth, 'na' as item_value, 'na' as item_uri, item.gamma_id, mod_type, app_id, transaction_id, 'Base' as app_value \n" +
         "from osee_tuple2 item, txsOuter where txsOuter.gamma_id = item.gamma_id\n" +
         "UNION ALL\n" +
         "SELECT 5 as table_type, tuple_type as item_type_id, 0 as item_id, e1 as item_first, e2 as item_second, e3 as item_third, 0 as item_fourth, 'na' as item_value, 'na' as item_uri, item.gamma_id, mod_type, app_id, transaction_id, 'Base' as app_value \n" +
         "from osee_tuple3 item, txsOuter where txsOuter.gamma_id = item.gamma_id\n" +
         "UNION ALL\n" +
         "SELECT 6 as table_type, tuple_type as item_type_id, 0 as item_id, e1 as item_first, e2 as item_second, e3 as item_third, e4 as item_fourth, 'na' as item_value, 'na' as item_uri, item.gamma_id, mod_type, app_id, transaction_id, 'Base' as app_value \n" +
         "from osee_tuple4 item, txsOuter where txsOuter.gamma_id = item.gamma_id\n" +
         "UNION ALL\n" +
         "SELECT 7 as table_type, rel_type as item_type_id, 0 as item_id,  a_art_id as item_first, b_art_id as item_second, rel_art_id as item_third, rel_order as item_fourth, 'na' as item_value, 'na' as item_uri, item.gamma_id, mod_type, app_id, transaction_id, kv.value as app_value \n" +
         "FROM osee_relation item, txsOuter,osee_key_value kv where txsOuter.gamma_id = item.gamma_id AND txsOuter.app_id = kv.key";
         ;

         private static final String NON_MATCH_APP="023ef52a-eb74-4ac0-8826-db44093dbe31";
         //note: this is just for giving tuples a value that shouldn't ever be matched by a user. If you are relying on this value, please reconsider the code you are writing.
         //If in the future it makes sense for tuples to have applicability, remove this and replace with a value from the osee_key_value table.


   // @formatter:on
   private static final String SELECT_BASE_TX = "select baseline_transaction_id from osee_branch where branch_id = ?";

   private final JdbcClient jdbcClient;
   private final SqlJoinFactory joinFactory;
   private final OrcsTokenService tokenService;
   private final BranchId sourceBranch, destinationBranch;
   private final TransactionToken sourceTx;
   private final TransactionToken destinationTx;
   private final BranchId mergeBranch;
   private final ApplicabilityQuery applicabilityQuery;
   private final MissingChangeItemFactory missingChangeItemFactory;
   private final OrcsApi orcsApi;
   private final TransactionId mergeTxId;

   public LoadDeltasBetweenBranches(JdbcClient jdbcClient, SqlJoinFactory joinFactory, OrcsTokenService tokenService, BranchId sourceBranch, BranchId destinationBranch, TransactionToken sourceTx, TransactionToken destinationTx, BranchId mergeBranch, OrcsApi orcsApi, MissingChangeItemFactory missingChangeItemFactory) {
      this.jdbcClient = jdbcClient;
      this.joinFactory = joinFactory;
      this.tokenService = tokenService;
      this.sourceBranch = sourceBranch;
      this.destinationBranch = destinationBranch;
      this.sourceTx = sourceTx;
      this.destinationTx = destinationTx;
      this.mergeBranch = mergeBranch;
      this.applicabilityQuery = orcsApi.getQueryFactory().applicabilityQuery();
      this.missingChangeItemFactory = missingChangeItemFactory;
      this.orcsApi = orcsApi;

      if (mergeBranch.isValid()) {
         mergeTxId = orcsApi.getQueryFactory().transactionQuery().andIsHead(mergeBranch).getResults().getExactlyOne();
      } else {
         mergeTxId = TransactionId.SENTINEL;
      }
   }

   private ApplicabilityToken getApplicabilityToken(ApplicabilityId appId) {
      String byKey = orcsApi.getKeyValueOps().getByKey(appId.getId());
      ApplicabilityToken toReturn = ApplicabilityToken.valueOf(appId.getId(), byKey);
      return toReturn;
   }

   private boolean hasMergeBranch() {
      return mergeBranch.isValid();
   }

   public List<ChangeItem> call() {
      Conditions.checkExpressionFailOnTrue(sourceBranch.equals(destinationBranch),
         "Unable to compute deltas between transactions on the same branch [%s]", sourceBranch);

      TransactionId sourceBaselineTxId = jdbcClient.fetch(TransactionId.SENTINEL, SELECT_BASE_TX, sourceBranch);
      DoubleKeyHashMap<Integer, Long, ChangeItem> newChangeData = this.loadSourceBranchChanges(sourceBaselineTxId);
      List<ChangeItem> changes = loadItemsbyId(newChangeData, sourceBaselineTxId);

      changes.addAll(
         missingChangeItemFactory.createMissingChanges(changes, sourceTx, destinationTx, applicabilityQuery));
      return changes;
   }

   private List<ChangeItem> loadItemsbyId(DoubleKeyHashMap<Integer, Long, ChangeItem> changeData,
      TransactionId sourceBaselineTxId) {
      try (ExportImportJoinQuery idJoin = joinFactory.createExportImportJoinQuery()) {
         for (Integer i : changeData.getKeySetOne()) {
            for (ChangeItem item : changeData.allValues(i)) {
               idJoin.add(i, item.getItemId());
            }
         }
         idJoin.store();

         if (hasMergeBranch()) {
            loadCurrentVersionData(idJoin, changeData, mergeBranch, mergeTxId, true);
         }
         loadCurrentVersionData(idJoin, changeData, destinationBranch, destinationTx, false);

         loadNonCurrentSourceVersionData(idJoin, changeData, sourceBaselineTxId);
      }
      List<ChangeItem> list = new LinkedList<>(changeData.allValues());
      return list;
   }

   private DoubleKeyHashMap<Integer, Long, ChangeItem> loadSourceBranchChanges(TransactionId sourceBaselineTxId) {
      DoubleKeyHashMap<Integer, Long, ChangeItem> hashChangeData = new DoubleKeyHashMap<>();

      Consumer<JdbcStatement> consumer = stmt -> {
         GammaId gammaId = GammaId.valueOf(stmt.getLong("gamma_id"));
         ModificationType modType = ModificationType.valueOf(stmt.getInt("mod_type"));
         ApplicabilityId appId = ApplicabilityId.valueOf(stmt.getLong("app_id"));
         TransactionToken txToken = TransactionToken.valueOf(stmt.getLong("transaction_id"), sourceBranch);
         int tableType = stmt.getInt("table_type");
         Long itemId = stmt.getLong("item_id");
         Long itemTypeId = stmt.getLong("item_type_id");
         switch (tableType) {
            case 1:
               ArtifactId artId = ArtifactId.valueOf(stmt.getLong("item_first"));
               String value = stmt.getString("item_value");
               String uri = stmt.getString("item_uri");
               hashChangeData.put(1, itemId,
                  ChangeItemUtil.newAttributeChange(AttributeId.valueOf(itemId),
                     tokenService.getAttributeTypeOrCreate(itemTypeId), artId, gammaId, modType, value, uri,
                     ApplicabilityToken.valueOf(stmt.getLong("app_id"), stmt.getString("app_value")), txToken));
               break;

            case 2: {
               hashChangeData.put(2, itemId,
                  ChangeItemUtil.newArtifactChange(ArtifactId.valueOf(itemId),
                     tokenService.getArtifactTypeOrCreate(itemTypeId), gammaId, modType,
                     ApplicabilityToken.valueOf(stmt.getLong("app_id"), stmt.getString("app_value")), txToken));
               break;
            }
            case 3: {
               ArtifactId aArtId = ArtifactId.valueOf(stmt.getLong("item_first"));
               ArtifactId bArtId = ArtifactId.valueOf(stmt.getLong("item_second"));
               String rationale = stmt.getString("item_value");
               hashChangeData.put(3, itemId,
                  ChangeItemUtil.newRelationChange(RelationId.valueOf(itemId),
                     tokenService.getRelationTypeOrCreate(itemTypeId), gammaId, modType, aArtId, bArtId, rationale,
                     ApplicabilityToken.valueOf(stmt.getLong("app_id"), stmt.getString("app_value")), txToken));
               break;
            }
            case 4: {
               long e1 = stmt.getLong("item_first");
               long e2 = stmt.getLong("item_second");
               hashChangeData.put(4, gammaId.getId(), ChangeItemUtil.newTupleChange(TupleTypeId.valueOf(itemTypeId),
                  gammaId, getApplicabilityToken(appId), modType, txToken, e1, e2));
               break;
            }
            case 5: {
               long e1 = stmt.getLong("item_first");
               long e2 = stmt.getLong("item_second");
               long e3 = stmt.getLong("item_third");
               hashChangeData.put(5, gammaId.getId(), ChangeItemUtil.newTupleChange(TupleTypeId.valueOf(itemTypeId),
                  gammaId, getApplicabilityToken(appId), modType, txToken, e1, e2, e3));
               break;
            }
            case 6: {
               long e1 = stmt.getLong("item_first");
               long e2 = stmt.getLong("item_second");
               long e3 = stmt.getLong("item_third");
               long e4 = stmt.getLong("item_fourth");
               hashChangeData.put(6, gammaId.getId(), ChangeItemUtil.newTupleChange(TupleTypeId.valueOf(itemTypeId),
                  gammaId, getApplicabilityToken(appId), modType, txToken, e1, e2, e3, e4));
               break;
            }
            case 7: {
               ArtifactId aArtId = ArtifactId.valueOf(stmt.getLong("item_first"));
               ArtifactId bArtId = ArtifactId.valueOf(stmt.getLong("item_second"));
               ArtifactId relArtId = ArtifactId.valueOf(stmt.getLong("item_third"));
               int relOrder = stmt.getInt("item_fourth");
               hashChangeData.put(7, gammaId.getId(),
                  ChangeItemUtil.newRelationChange2(tokenService.getRelationTypeOrCreate(itemTypeId), gammaId, modType,
                     aArtId, bArtId, relArtId, relOrder,
                     ApplicabilityToken.valueOf(stmt.getLong("app_id"), stmt.getString("app_value")), txToken));
               break;
            }

         }
      };
      SqlTable txsTable = OseeDb.getTxsTable(orcsApi.getQueryFactory().branchQuery().isArchived(sourceBranch));
      String sql = String.format(SELECT_ALL_SOURCE_ADDRESSING, txsTable, txsTable);

      jdbcClient.runQueryWithMaxFetchSize(consumer, sql, sourceBranch, TxCurrent.NOT_CURRENT, sourceBaselineTxId,
         sourceBranch, sourceBaselineTxId);

      return hashChangeData;
   }

   private void loadCurrentVersionData(ExportImportJoinQuery idJoin,
      DoubleKeyHashMap<Integer, Long, ChangeItem> changesByItemId, BranchId txBranchId, TransactionId txId,
      boolean isMergeBranch) {
      Consumer<JdbcStatement> consumer = stmt -> {
         Long itemId = stmt.getLong("item_id");
         Integer tableType = stmt.getInt("table_type");
         ApplicabilityId appId = ApplicabilityId.valueOf(stmt.getLong("app_id"));
         String appValue = stmt.getString("app_value");
         GammaId gammaId = GammaId.valueOf(stmt.getLong("gamma_id"));
         ChangeItem change = changesByItemId.get(tableType, itemId);
         TransactionToken txToken = TransactionToken.valueOf(stmt.getLong("transaction_id"), txBranchId);
         if (isMergeBranch) {
            change.getNetChange().setGammaId(gammaId);
            change.getNetChange().setModType(ModificationType.MERGED);
            if (appValue.equals(NON_MATCH_APP)) {

               change.getNetChange().setApplicabilityToken(getApplicabilityToken(appId));
            } else {
               change.getNetChange().setApplicabilityToken(ApplicabilityToken.valueOf(appId.getId(), appValue));
            }
            change.getNetChange().setTransactionToken(txToken);
         } else {
            change.getDestinationVersion().setModType(ModificationType.valueOf(stmt.getInt("mod_type")));
            change.getDestinationVersion().setGammaId(gammaId);
            if (appValue.equals(NON_MATCH_APP)) {

               change.getDestinationVersion().setApplicabilityToken(getApplicabilityToken(appId));
            } else {
               change.getDestinationVersion().setApplicabilityToken(
                  ApplicabilityToken.valueOf(appId.getId(), appValue));
            }
            change.getDestinationVersion().setTransactionToken(txToken);
         }
      };

      String query =
         "select txs.gamma_id, txs.mod_type, txs.app_id, item.art_id as item_id, 2 as table_type, txs.transaction_id, kv.value as app_value from osee_join_export_import idj," + //
            " osee_artifact item, osee_txs txs,osee_key_value kv where idj.query_id = ? and idj.id2 = item.art_id and idj.id1 = 2" + //
            " and item.gamma_id = txs.gamma_id and txs.tx_current <> ? and txs.branch_id = ? and txs.transaction_id <= ? AND txs.app_id = kv.key" + //
            " union all select txs.gamma_id, txs.mod_type, txs.app_id, item.attr_id as item_id, 1 as table_type, txs.transaction_id, kv.value as app_value from osee_join_export_import idj," + //
            " osee_attribute item, osee_txs txs,osee_key_value kv where idj.query_id = ? and idj.id2 = item.attr_id and idj.id1 = 1" + //
            " and item.gamma_id = txs.gamma_id and txs.tx_current <> ? and txs.branch_id = ? and txs.transaction_id <= ? AND txs.app_id = kv.key" + //
            " union all select txs.gamma_id, txs.mod_type, txs.app_id, item.rel_link_id as item_id, 3 as table_type, txs.transaction_id, kv.value as app_value from osee_join_export_import idj," + //
            " osee_relation_link item, osee_txs txs,osee_key_value kv where idj.query_id = ? and idj.id2 = item.rel_link_id and idj.id1 = 3 AND txs.app_id = kv.key " + //
            " and item.gamma_id = txs.gamma_id and txs.tx_current <> ? and txs.branch_id = ? and txs.transaction_id <= ?" + //
            " union all select txs.gamma_id, txs.mod_type, txs.app_id, item.gamma_id as item_id, 7 as table_type, txs.transaction_id, kv.value as app_value from osee_join_export_import idj," + //
            " osee_relation item, osee_txs txs, osee_key_value kv where idj.query_id = ? and idj.id2 = item.gamma_id and idj.id1 = 7 AND txs.app_id = kv.key " + //
            " and item.gamma_id = txs.gamma_id and txs.tx_current <> ? and txs.branch_id = ? and txs.transaction_id <= ?" + //
            " union all select txs.gamma_id, txs.mod_type, txs.app_id, item.gamma_id as item_id, 4 as table_type, txs.transaction_id, '" + NON_MATCH_APP + "' as app_value from osee_join_export_import idj," + //
            " osee_tuple2 item, osee_txs txs where idj.query_id = ? and idj.id2 = item.gamma_id and item.gamma_id = txs.gamma_id" + //
            " and txs.tx_current <> ? and txs.branch_id = ? and txs.transaction_id <= ?" + //
            " union all select txs.gamma_id, txs.mod_type, txs.app_id, item.gamma_id as item_id, 5 as table_type, txs.transaction_id, '" + NON_MATCH_APP + "' as app_value from osee_join_export_import idj," + //
            " osee_tuple3 item, osee_txs txs where idj.query_id = ? and idj.id2 = item.gamma_id and item.gamma_id = txs.gamma_id" + //
            " and txs.tx_current <> ? and txs.branch_id = ? and txs.transaction_id <= ?" + //
            " union all select txs.gamma_id, txs.mod_type, txs.app_id, item.gamma_id as item_id, 6 as table_type, txs.transaction_id, '" + NON_MATCH_APP + "' as app_value from osee_join_export_import idj," + //
            " osee_tuple4 item, osee_txs txs where idj.query_id = ? and idj.id2 = item.gamma_id and item.gamma_id = txs.gamma_id" + //
            " and txs.tx_current <> ? and txs.branch_id = ? and txs.transaction_id <= ?";

      jdbcClient.runQueryWithMaxFetchSize(consumer, query, idJoin.getQueryId(), TxCurrent.NOT_CURRENT, txBranchId, txId,
         idJoin.getQueryId(), TxCurrent.NOT_CURRENT, txBranchId, txId, idJoin.getQueryId(), TxCurrent.NOT_CURRENT,
         txBranchId, txId, idJoin.getQueryId(), TxCurrent.NOT_CURRENT, txBranchId, txId, idJoin.getQueryId(),
         TxCurrent.NOT_CURRENT, txBranchId, txId, idJoin.getQueryId(), TxCurrent.NOT_CURRENT, txBranchId, txId,
         idJoin.getQueryId(), TxCurrent.NOT_CURRENT, txBranchId, txId);

   }

   private void loadNonCurrentSourceVersionData(ExportImportJoinQuery idJoin,
      DoubleKeyHashMap<Integer, Long, ChangeItem> changesByItemId, TransactionId sourceBaselineTxId) {
      try (JdbcStatement chStmt = jdbcClient.getStatement()) {

         String query =
            "select * from (select null as value, item.art_id as item_id, txs.gamma_id, txs.mod_type, txs.app_id, txs.transaction_id, idj.id2, 2 as table_type, kv.value as app_value from osee_join_export_import idj, " + //
               "osee_artifact item, osee_txs txs, osee_key_value kv where idj.query_id = ? and idj.id2 = item.art_id and idj.id1 = 2 " + //
               "and item.gamma_id = txs.gamma_id and txs.tx_current = ? and txs.branch_id = ? AND txs.app_id = kv.key " + //
               "union all select item.value as value, item.attr_id as item_id, txs.gamma_id, txs.mod_type, txs.app_id, txs.transaction_id, idj.id2, 1 as table_type, kv.value as app_value from osee_join_export_import idj, " + //
               "osee_attribute item, osee_txs txs, osee_key_value kv where idj.query_id = ? and idj.id2 = item.attr_id and idj.id1 = 1 " + //
               "and item.gamma_id = txs.gamma_id and txs.tx_current = ? and txs.branch_id = ? AND txs.app_id = kv.key " + //
               "union all select null as value, item.rel_link_id as item_id, txs.gamma_id, txs.mod_type, txs.app_id, txs.transaction_id, idj.id2, 3 as table_type, kv.value as app_value from osee_join_export_import idj, " + //
               "osee_relation_link item, osee_txs txs,osee_key_value kv where idj.query_id = ? and idj.id2 = item.rel_link_id and idj.id1 = 3 AND txs.app_id = kv.key " + //
               "and item.gamma_id = txs.gamma_id and txs.tx_current = ? and txs.branch_id = ? " + //
               "union all select null as value, item.gamma_id as item_id, txs.gamma_id, txs.mod_type, txs.app_id, txs.transaction_id, idj.id2, 7 as table_type, kv.value as app_value from osee_join_export_import idj, " + //
               "osee_relation item, osee_txs txs,osee_key_value kv where idj.query_id = ? and idj.id2 = item.gamma_id and idj.id1 = 7 AND txs.app_id = kv.key " + //
               "and item.gamma_id = txs.gamma_id and txs.tx_current = ? and txs.branch_id = ? " + //
               "union all select null as value, item.gamma_id as item_id, txs.gamma_id, txs.mod_type, txs.app_id, txs.transaction_id, idj.id2, 4 as table_type, '" + NON_MATCH_APP + "' as app_value from osee_join_export_import idj, " + //
               "osee_tuple2 item, osee_txs txs where idj.query_id = ? and idj.id2 = item.gamma_id and idj.id1 = 4 " + //
               "and item.gamma_id = txs.gamma_id and txs.tx_current = ? and txs.branch_id = ? " + //
               "union all select null as value, item.gamma_id as item_id, txs.gamma_id, txs.mod_type, txs.app_id, txs.transaction_id, idj.id2, 5 as table_type, '" + NON_MATCH_APP + "' as app_value from osee_join_export_import idj, " + //
               "osee_tuple3 item, osee_txs txs where idj.query_id = ? and idj.id2 = item.gamma_id and idj.id1 = 5 " + //
               "and item.gamma_id = txs.gamma_id and txs.tx_current = ? and txs.branch_id = ? " + //
               "union all select null as value, item.gamma_id as item_id, txs.gamma_id, txs.mod_type, txs.app_id, txs.transaction_id, idj.id2, 6 as table_type, '" + NON_MATCH_APP + "' as app_value from osee_join_export_import idj, " + //
               "osee_tuple4 item, osee_txs txs where idj.query_id = ? and idj.id2 = item.gamma_id and idj.id1 = 6 " + //
               "and item.gamma_id = txs.gamma_id and txs.tx_current = ? and txs.branch_id = ? " + //
               ") t order by t.id2, t.transaction_id asc";

         chStmt.runPreparedQueryWithMaxFetchSize(query, idJoin.getQueryId(), TxCurrent.NOT_CURRENT, sourceBranch,
            idJoin.getQueryId(), TxCurrent.NOT_CURRENT, sourceBranch, idJoin.getQueryId(), TxCurrent.NOT_CURRENT,
            sourceBranch, idJoin.getQueryId(), TxCurrent.NOT_CURRENT, sourceBranch, idJoin.getQueryId(),
            TxCurrent.NOT_CURRENT, sourceBranch, idJoin.getQueryId(), TxCurrent.NOT_CURRENT, sourceBranch,
            idJoin.getQueryId(), TxCurrent.NOT_CURRENT, sourceBranch);

         Long previousItemId = -1L;
         boolean isFirstSet = false;
         while (chStmt.next()) {
            Long itemId = chStmt.getLong("item_id");
            Integer tableType = chStmt.getInt("table_type");
            Long transactionId = chStmt.getLong("transaction_id");
            ApplicabilityId appId = ApplicabilityId.valueOf(chStmt.getLong("app_id"));
            ModificationType modType = ModificationType.valueOf(chStmt.getInt("mod_type"));
            GammaId gammaId = GammaId.valueOf(chStmt.getLong("gamma_id"));
            String appValue = chStmt.getString("app_value");

            String value = chStmt.getString("value");

            ChangeItem change = changesByItemId.get(tableType, itemId);
            if (!previousItemId.equals(itemId)) {
               isFirstSet = false;
            }
            if (sourceBaselineTxId.equals(transactionId)) {
               if (appValue.equals(NON_MATCH_APP)) {

                  setVersionData(change.getBaselineVersion(), gammaId, modType, value, appId);
               } else {
                  setVersionData(change.getBaselineVersion(), gammaId, modType, value,
                     ApplicabilityToken.valueOf(appId.getId(), appValue));
               }
            } else if (!isFirstSet) {
               if (appValue.equals(NON_MATCH_APP)) {

                  setVersionData(change.getFirstNonCurrentChange(), gammaId, modType, value, appId);
               } else {
                  setVersionData(change.getFirstNonCurrentChange(), gammaId, modType, value,
                     ApplicabilityToken.valueOf(appId.getId(), appValue));

               }
               isFirstSet = true;
            }
            previousItemId = itemId;
         }
      }
   }

   private void setVersionData(ChangeVersion versionedChange, GammaId gammaId, ModificationType modType, String value,
      ApplicabilityId appId) {
      this.setVersionData(versionedChange, gammaId, modType, value, getApplicabilityToken(appId));
   }

   private void setVersionData(ChangeVersion versionedChange, GammaId gammaId, ModificationType modType, String value,
      ApplicabilityToken app) {
      // Tolerates the case of having more than one version of an item on a
      // baseline transaction by picking the most recent one
      if (versionedChange.getGammaId() == null || versionedChange.getGammaId().getId().compareTo(gammaId.getId()) < 0) {
         versionedChange.setValue(value);
         versionedChange.setModType(modType);
         versionedChange.setGammaId(gammaId);
         versionedChange.setApplicabilityToken(app);
      }
   }
}