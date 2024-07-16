/*********************************************************************
 * Copyright (c) 2012 Boeing
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
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.TupleTypeId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeItemUtil;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.db.internal.sql.join.ExportImportJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 * @author Ryan Schmitt
 * @author Jeff C. Phillips
 */
public class LoadDeltasBetweenTxsOnTheSameBranch {

  // @formatter:off
   private static final String SELECT_ITEMS_BETWEEN_TRANSACTIONS =
      "with txsOuter as (select gamma_id, mod_type, app_id, transaction_id from %s where branch_id = ? and transaction_id > ? and transaction_id <= ?) \n" +
      "SELECT 1 as table_type, attr_type_id as item_type_id, attr_id as item_id, art_id as item_first, 0 as item_second, 0 as item_third, 0 as item_fourth, value as item_value, item.gamma_id, mod_type, app_id, transaction_id \n" +
      "FROM osee_attribute item, txsOuter where txsOuter.gamma_id = item.gamma_id\n" +
      "UNION ALL\n" +
      "SELECT 2 as table_type, art_type_id as item_type_id, art_id as item_id, 0 as item_first, 0 as item_second, 0 as item_third, 0 as item_fourth, 'na' as item_value, item.gamma_id, mod_type, app_id, transaction_id \n" +
      "FROM osee_artifact item, txsOuter where txsOuter.gamma_id = item.gamma_id\n" +
      "UNION ALL\n" +
      "SELECT 3 as table_type, rel_link_type_id as item_type_id, rel_link_id as item_id,  a_art_id as item_first, b_art_id as item_second, 0 as item_third, 0 as item_fourth, rationale as item_value, item.gamma_id, mod_type, app_id, transaction_id \n" +
      "FROM osee_relation_link item, txsOuter where txsOuter.gamma_id = item.gamma_id\n" +
      "UNION ALL\n" +
      "SELECT 4 as table_type, tuple_type as item_type_id, 0 as item_id, e1 as item_first, e2 as item_second, 0 as item_third, 0 as item_fourth, 'na' as item_value, item.gamma_id, mod_type, app_id, transaction_id \n" +
      "from osee_tuple2 item, txsOuter where txsOuter.gamma_id = item.gamma_id\n" +
      "UNION ALL\n" +
      "SELECT 5 as table_type, tuple_type as item_type_id, 0 as item_id, e1 as item_first, e2 as item_second, e3 as item_third, 0 as item_fourth, 'na' as item_value, item.gamma_id, mod_type, app_id, transaction_id \n" +
      "from osee_tuple3 item, txsOuter where txsOuter.gamma_id = item.gamma_id\n" +
      "UNION ALL\n" +
      "SELECT 6 as table_type, tuple_type as item_type_id, 0 as item_id, e1 as item_first, e2 as item_second, e3 as item_third, e4 as item_fourth, 'na' as item_value, item.gamma_id, mod_type, app_id, transaction_id \n" +
      "from osee_tuple4 item, txsOuter where txsOuter.gamma_id = item.gamma_id\n" +
      "UNION ALL\n" +
      "SELECT 7 as table_type, rel_type as item_type_id, 0 as item_id, a_art_id as item_first, b_art_id as item_second, rel_art_id as item_third, rel_order as item_fourth, 'na' as item_value, item.gamma_id, mod_type, app_id, transaction_id \n" +
      "from osee_relation item, txsOuter where txsOuter.gamma_id = item.gamma_id";
   // @formatter:on

   private final OrcsTokenService tokenService;
   private final TransactionToken sourceTx;
   private final TransactionToken destinationTx;
   private final MissingChangeItemFactory missingChangeItemFactory;
   private final OrcsApi orcsApi;
   private final SqlJoinFactory joinFactory;
   private final JdbcClient jdbcClient;
   private final BranchId mergeBranch;
   private final boolean isArchived;

   public LoadDeltasBetweenTxsOnTheSameBranch(JdbcClient jdbcClient, SqlJoinFactory joinFactory, OrcsTokenService tokenService, TransactionToken sourceTx, TransactionToken destinationTx, BranchId mergeBranch, OrcsApi orcsApi, MissingChangeItemFactory missingChangeItemFactory) {
      this.jdbcClient = jdbcClient;
      this.joinFactory = joinFactory;
      this.tokenService = tokenService;
      this.sourceTx = sourceTx;
      this.destinationTx = destinationTx;
      this.missingChangeItemFactory = missingChangeItemFactory;
      this.mergeBranch = mergeBranch;
      this.orcsApi = orcsApi;
      isArchived = orcsApi.getQueryFactory().branchQuery().isArchived(destinationTx.getBranch());
   }

   private ApplicabilityToken getApplicabilityToken(ApplicabilityId appId) {
      String byKey = orcsApi.getKeyValueOps().getByKey(appId.getId());
      ApplicabilityToken toReturn = ApplicabilityToken.valueOf(appId.getId(), byKey);
      return toReturn;
   }

   public List<ChangeItem> loadDeltasBetweenTxsOnTheSameBranch() {
      Conditions.checkExpressionFailOnTrue(!sourceTx.isOnSameBranch(destinationTx),
         "Unable to compute deltas between transactions on different branches");

      DoubleKeyHashMap<Integer, Long, ChangeItem> hashChangeData = loadChangesAtEndTx();
      return loadItemsByItemId(hashChangeData);
   }

   private DoubleKeyHashMap<Integer, Long, ChangeItem> loadChangesAtEndTx() {
      DoubleKeyHashMap<Integer, Long, ChangeItem> hashChangeData = new DoubleKeyHashMap<>();
      Consumer<JdbcStatement> consumer = stmt -> {
         GammaId gammaId = GammaId.valueOf(stmt.getLong("gamma_id"));
         ModificationType modType = ModificationType.valueOf(stmt.getInt("mod_type"));
         ApplicabilityId appId = ApplicabilityId.valueOf(stmt.getLong("app_id"));
         TransactionToken txToken = TransactionToken.valueOf(stmt.getLong("transaction_id"), destinationTx.getBranch());
         int tableType = stmt.getInt("table_type");
         Long itemId = stmt.getLong("item_id");
         Long itemTypeId = stmt.getLong("item_type_id");
         switch (tableType) {
            case 1: {
               ArtifactId artId = ArtifactId.valueOf(stmt.getLong("item_first"));
               String value = stmt.getString("item_value");
               hashChangeData.put(1, itemId,
                  ChangeItemUtil.newAttributeChange(AttributeId.valueOf(itemId),
                     tokenService.getAttributeType(itemTypeId), artId, gammaId, modType, value,
                     getApplicabilityToken(appId), txToken));
               break;
            }
            case 2: {
               hashChangeData.put(2, itemId, ChangeItemUtil.newArtifactChange(ArtifactId.valueOf(itemId),
                  tokenService.getArtifactType(itemTypeId), gammaId, modType, getApplicabilityToken(appId), txToken));
               break;
            }
            case 3: {
               ArtifactId aArtId = ArtifactId.valueOf(stmt.getLong("item_first"));
               ArtifactId bArtId = ArtifactId.valueOf(stmt.getLong("item_second"));
               String rationale = stmt.getString("item_value");
               hashChangeData.put(3, itemId,
                  ChangeItemUtil.newRelationChange(RelationId.valueOf(itemId), tokenService.getRelationType(itemTypeId),
                     gammaId, modType, aArtId, bArtId, rationale, getApplicabilityToken(appId), txToken));
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
               ArtifactId relType = ArtifactId.valueOf(stmt.getLong("item_type_id"));
               ArtifactId a_art_id = ArtifactId.valueOf(stmt.getLong("item_first"));
               ArtifactId b_art_id = ArtifactId.valueOf(stmt.getLong("item_second"));
               ArtifactId rel_art_id = ArtifactId.valueOf(stmt.getLong("item_third"));
               int rel_order = stmt.getInt("item_fourth");
               hashChangeData.put(7, gammaId.getId(),
                  ChangeItemUtil.newRelationChange2(tokenService.getRelationType(relType.getId()), gammaId, modType,
                     a_art_id, b_art_id, rel_art_id, rel_order, getApplicabilityToken(appId), txToken));
               break;
            }
         }
      };
      String query = String.format(SELECT_ITEMS_BETWEEN_TRANSACTIONS, OseeDb.getTxsTable(isArchived));
      jdbcClient.runQueryWithMaxFetchSize(consumer, query, destinationTx.getBranch(), sourceTx, destinationTx);

      return hashChangeData;
   }

   private List<ChangeItem> loadItemsByItemId(DoubleKeyHashMap<Integer, Long, ChangeItem> changeData) {
      try (ExportImportJoinQuery idJoin = joinFactory.createExportImportJoinQuery()) {
         for (Integer i : changeData.getKeySetOne()) {
            if (changeData.get(i) != null) {
               for (ChangeItem item : changeData.get(i)) {
                  idJoin.add(i, item.getItemId());
               }
            }
         }
         idJoin.store();
         loadCurrentVersionData(idJoin.getQueryId(), changeData, sourceTx);
      }
      List<ChangeItem> list = new LinkedList<>(changeData.allValues());
      return list;
   }

   private void loadCurrentVersionData(Long queryId, DoubleKeyHashMap<Integer, Long, ChangeItem> changesByItemId, TransactionToken transactionLimit) {

      Consumer<JdbcStatement> consumer = stmt -> {
         Long itemId = stmt.getLong("item_id");
         Integer tableType = stmt.getInt("table_type");
         GammaId gammaId = GammaId.valueOf(stmt.getLong("gamma_id"));
         ApplicabilityId appId = ApplicabilityId.valueOf(stmt.getLong("app_id"));
         ModificationType modType = ModificationType.valueOf(stmt.getInt("mod_type"));
         TransactionToken txToken =
            TransactionToken.valueOf(stmt.getLong("transaction_id"), transactionLimit.getBranch());
         ChangeItem change = changesByItemId.get(tableType, itemId);

         change.getDestinationVersion().setModType(modType);
         change.getDestinationVersion().setGammaId(gammaId);
         change.getDestinationVersion().setApplicabilityToken(getApplicabilityToken(appId));
         change.getDestinationVersion().setTransactionToken(txToken);
         change.getBaselineVersion().copy(change.getDestinationVersion());
      };

      String archiveTable = isArchived ? "osee_txs_archived" : "osee_txs";
      String query = String.format(
         "select txs.gamma_id, txs.mod_type, txs.app_id, item.art_id as item_id, 2 as table_type, transaction_id from osee_join_export_import idj," + //
            " osee_artifact item, %s txs where idj.query_id = ? and idj.id2 = item.art_id and idj.id1 = 2" + //
            " and item.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.transaction_id <= ?" + //
            " union all select txs.gamma_id, txs.mod_type, txs.app_id, item.attr_id as item_id, 1 as table_type, transaction_id from osee_join_export_import idj," + //
            " osee_attribute item, %s txs where idj.query_id = ? and idj.id2 = item.attr_id and idj.id1 = 1" + //
            " and item.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.transaction_id <= ?" + //
            " union all select txs.gamma_id, txs.mod_type, txs.app_id, item.rel_link_id as item_id, 3 as table_type, transaction_id from osee_join_export_import idj," + //
            " osee_relation_link item, %s txs where idj.query_id = ? and idj.id2 = item.rel_link_id and idj.id1 = 3" + //
            " and item.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.transaction_id <= ?" + //
            " union all select txs.gamma_id, txs.mod_type, txs.app_id, item.gamma_id as item_id, 4 as table_type, transaction_id from osee_join_export_import idj," + //
            " osee_tuple2 item, %s txs where idj.query_id = ? and idj.id2 = item.gamma_id and idj.id1 = 4" + //
            " and item.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.transaction_id <= ?" + //
            " union all select txs.gamma_id, txs.mod_type, txs.app_id, item.gamma_id as item_id, 5 as table_type, transaction_id from osee_join_export_import idj," + //
            " osee_tuple3 item, %s txs where idj.query_id = ? and idj.id2 = item.gamma_id and idj.id1 = 5" + //
            " and item.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.transaction_id <= ?" + //
            " union all select txs.gamma_id, txs.mod_type, txs.app_id, item.gamma_id as item_id, 6 as table_type, transaction_id from osee_join_export_import idj," + //
            " osee_tuple4 item, %s txs where idj.query_id = ? and idj.id2 = item.gamma_id and idj.id1 = 6" + //
            " and item.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.transaction_id <= ?" + //
            " union all select txs.gamma_id, txs.mod_type, txs.app_id, item.gamma_id as item_id, 7 as table_type, transaction_id from osee_join_export_import idj," + //
            " osee_relation item, %s txs where idj.query_id = ? and idj.id2 = item.gamma_id and idj.id1 = 7" + //
            " and item.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.transaction_id <= ?" + //
            " ORDER BY transaction_id",
         archiveTable, archiveTable, archiveTable, archiveTable, archiveTable, archiveTable, archiveTable);

      jdbcClient.runQueryWithMaxFetchSize(consumer, query, queryId, transactionLimit.getBranch(), transactionLimit,
         queryId, transactionLimit.getBranch(), transactionLimit, queryId, transactionLimit.getBranch(),
         transactionLimit, queryId, transactionLimit.getBranch(), transactionLimit, queryId,
         transactionLimit.getBranch(), transactionLimit, queryId, transactionLimit.getBranch(), transactionLimit,
         queryId, transactionLimit.getBranch(), transactionLimit);
   }

   public List<ChangeItem> compareTransactions() {

      // Get initial change items from database
      List<ChangeItem> changes;
      if (sourceTx.isOnSameBranch(destinationTx)) {
         changes = loadDeltasBetweenTxsOnTheSameBranch();
         changes.addAll(missingChangeItemFactory.createMissingChanges(changes, sourceTx, destinationTx,
            orcsApi.getQueryFactory().applicabilityQuery()));
      } else {
         changes = new LoadDeltasBetweenBranches(jdbcClient, joinFactory, tokenService, sourceTx.getBranch(),
            destinationTx.getBranch(), sourceTx, destinationTx, mergeBranch, orcsApi, missingChangeItemFactory).call();
      }

      // Calculate and set the mod types
      ChangeItemUtil.computeNetChanges(changes);

      // Add a synthetic artifact change for any set of attr/rel changes
      AddSyntheticArtifactChangeData addArtifactData =
         new AddSyntheticArtifactChangeData(changes, jdbcClient, sourceTx.getBranch(), tokenService);
      changes = addArtifactData.doWork();
      return changes;

   }
}