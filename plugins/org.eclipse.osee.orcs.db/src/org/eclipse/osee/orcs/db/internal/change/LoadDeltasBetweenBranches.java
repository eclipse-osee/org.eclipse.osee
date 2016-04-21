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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.RelationalConstants;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeVersion;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.callable.AbstractDatastoreCallable;
import org.eclipse.osee.orcs.db.internal.change.ChangeItemLoader.ChangeItemFactory;
import org.eclipse.osee.orcs.db.internal.sql.join.IdJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.db.internal.sql.join.TransactionJoinQuery;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 * @author Ryan Schmitt
 * @author Jeff C. Phillips
 */
public class LoadDeltasBetweenBranches extends AbstractDatastoreCallable<List<ChangeItem>> {
   private static final String SELECT_SOURCE_BRANCH_CHANGES =
      "select gamma_id, mod_type, app_id from osee_txs txs where txs.branch_id = ? and txs.tx_current <> ? and txs.transaction_id <> ? AND NOT EXISTS (SELECT 1 FROM osee_txs txs1 WHERE txs1.branch_id = ? AND txs1.transaction_id = ? AND txs1.gamma_id = txs.gamma_id and txs1.mod_type = txs.mod_type and txs1.app_id = txs.app_id)";

   private static final String SELECT_BASE_TRANSACTION =
      "select baseline_transaction_id from osee_branch where branch_id = ?";

   private final HashMap<Long, Pair<ModificationType, ApplicabilityId>> changeByGammaId = new HashMap<>();

   private final Long sourceBranchId, destinationBranchId, mergeBranchId;
   private final Integer destinationHeadTxId, mergeTxId;

   private final ChangeItemLoader changeItemLoader;
   private final SqlJoinFactory joinFactory;

   public LoadDeltasBetweenBranches(Log logger, OrcsSession session, JdbcClient jdbcClient, SqlJoinFactory joinFactory, Long sourceBranchId, Long destinationBranchId, Integer destinationHeadTxId, Long mergeBranchId, Integer mergeTxId) {
      super(logger, session, jdbcClient);
      this.joinFactory = joinFactory;
      this.sourceBranchId = sourceBranchId;
      this.destinationBranchId = destinationBranchId;
      this.destinationHeadTxId = destinationHeadTxId;
      this.mergeBranchId = mergeBranchId;
      this.mergeTxId = mergeTxId;
      this.changeItemLoader = new ChangeItemLoader(jdbcClient, changeByGammaId);
   }

   private boolean hasMergeBranch() {
      return mergeBranchId != null;
   }

   private int getSourceBaselineTxId() {
      return getBaseTxId(sourceBranchId);
   }

   @Override
   public List<ChangeItem> call() throws Exception {
      List<ChangeItem> changeData = new LinkedList<>();

      Conditions.checkExpressionFailOnTrue(sourceBranchId.equals(destinationBranchId),
         "Unable to compute deltas between transactions on the same branch [%s]", sourceBranchId);

      TransactionJoinQuery txJoin = joinFactory.createTransactionJoinQuery();
      int sourceBaselineTxId = getSourceBaselineTxId();

      loadSourceBranchChanges(txJoin, sourceBaselineTxId);

      int txJoinId = txJoin.getQueryId();
      try {
         loadByItemId(changeData, txJoinId, changeItemLoader.createArtifactChangeItemFactory(), sourceBaselineTxId);
         loadByItemId(changeData, txJoinId, changeItemLoader.createAttributeChangeItemFactory(), sourceBaselineTxId);
         loadByItemId(changeData, txJoinId, changeItemLoader.createRelationChangeItemFactory(), sourceBaselineTxId);
      } finally {
         try {
            txJoin.delete();
         } finally {
            changeByGammaId.clear();
         }
      }
      return changeData;
   }

   private void loadSourceBranchChanges(TransactionJoinQuery txJoin, int sourceBaselineTxId) throws OseeCoreException {
      Consumer<JdbcStatement> consumer = stmt -> {
         checkForCancelled();
         Long gammaId = stmt.getLong("gamma_id");
         ModificationType modType = ModificationType.getMod(stmt.getInt("mod_type"));
         ApplicabilityId appId = ApplicabilityId.valueOf(stmt.getLong("app_id"));

         txJoin.add(gammaId, -1);
         changeByGammaId.put(gammaId, new Pair<ModificationType, ApplicabilityId>(modType, appId));
      };
      getJdbcClient().runQuery(consumer, JdbcConstants.JDBC__MAX_FETCH_SIZE, SELECT_SOURCE_BRANCH_CHANGES,
         sourceBranchId, TxChange.NOT_CURRENT.getValue(), sourceBaselineTxId, sourceBranchId, sourceBaselineTxId);
      txJoin.store();
   }

   private void loadByItemId(Collection<ChangeItem> changeData, int txJoinId, ChangeItemFactory factory, int sourceBaselineTxId) throws OseeCoreException {
      HashMap<Integer, ChangeItem> changesByItemId = new HashMap<>();

      IdJoinQuery idJoin = joinFactory.createIdJoinQuery();
      try {
         changeItemLoader.loadItemIdsBasedOnGammas(factory, txJoinId, changesByItemId, idJoin);

         idJoin.store();

         if (hasMergeBranch()) {
            loadCurrentData(factory.getItemTableName(), factory.getItemIdColumnName(), idJoin, changesByItemId,
               mergeBranchId, mergeTxId, true);
         }

         loadCurrentData(factory.getItemTableName(), factory.getItemIdColumnName(), idJoin, changesByItemId,
            destinationBranchId, destinationHeadTxId, false);

         loadNonCurrentSourceData(factory.getItemTableName(), factory.getItemIdColumnName(), idJoin, changesByItemId,
            factory.getItemValueColumnName(), sourceBaselineTxId);
         changeData.addAll(changesByItemId.values());

      } finally {
         idJoin.delete();
      }
   }

   private void loadCurrentData(String tableName, String columnName, IdJoinQuery idJoin, HashMap<Integer, ChangeItem> changesByItemId, Long txBranchId, Integer txId, boolean isMergeBranch) throws OseeCoreException {
      try (JdbcStatement chStmt = getJdbcClient().getStatement()) {
         String query = "select txs.gamma_id, txs.mod_type, txs.app_id, item." + columnName + " from osee_join_id idj, " //
         + tableName + " item, osee_txs txs where idj.query_id = ? and idj.id = item." + columnName + //
         " and item.gamma_id = txs.gamma_id and txs.tx_current <> ? and txs.branch_id = ? and txs.transaction_id <= ?";

         chStmt.runPreparedQuery(JdbcConstants.JDBC__MAX_FETCH_SIZE, query, idJoin.getQueryId(),
            TxChange.NOT_CURRENT.getValue(), txBranchId, txId);

         while (chStmt.next()) {
            checkForCancelled();

            Integer itemId = chStmt.getInt(columnName);
            ApplicabilityId appId = ApplicabilityId.valueOf(chStmt.getLong("app_id"));
            Long gammaId = chStmt.getLong("gamma_id");
            ChangeItem change = changesByItemId.get(itemId);

            if (isMergeBranch) {
               change.getNetChange().setGammaId(gammaId);
               change.getNetChange().setModType(ModificationType.MERGED);
               change.getNetChange().setApplicabilityId(appId);
            } else {
               change.getDestinationVersion().setModType(ModificationType.getMod(chStmt.getInt("mod_type")));
               change.getDestinationVersion().setGammaId(gammaId);
               change.getDestinationVersion().setApplicabilityId(appId);
            }
         }
      }
   }

   private void loadNonCurrentSourceData(String tableName, String idColumnName, IdJoinQuery idJoin, HashMap<Integer, ChangeItem> changesByItemId, String columnValueName, int sourceBaselineTxId) throws OseeCoreException {
      try (JdbcStatement chStmt = getJdbcClient().getStatement()) {
         String valueColumnName = columnValueName != null ? "item." + columnValueName + "," : "";
         String query =
            "select " + valueColumnName + "item." + idColumnName + ", txs.gamma_id, txs.mod_type, txs.app_id, txs.transaction_id from osee_join_id idj, " //
            + tableName + " item, osee_txs txs where idj.query_id = ? and idj.id = item." + idColumnName + //
            " and item.gamma_id = txs.gamma_id and txs.tx_current = ? and txs.branch_id = ? order by idj.id, txs.transaction_id asc";

         chStmt.runPreparedQuery(JdbcConstants.JDBC__MAX_FETCH_SIZE, query, idJoin.getQueryId(),
            TxChange.NOT_CURRENT.getValue(), sourceBranchId);

         int previousItemId = -1;
         boolean isFirstSet = false;
         while (chStmt.next()) {
            checkForCancelled();
            int itemId = chStmt.getInt(idColumnName);
            Integer transactionId = chStmt.getInt("transaction_id");
            ApplicabilityId appId = ApplicabilityId.valueOf(chStmt.getLong("app_id"));
            ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));
            Long gammaId = chStmt.getLong("gamma_id");

            String value = null;
            if (columnValueName != null) {
               value = chStmt.getString(columnValueName);
            }

            ChangeItem change = changesByItemId.get(itemId);
            if (previousItemId != itemId) {
               isFirstSet = false;
            }
            if (transactionId.equals(sourceBaselineTxId)) {
               setVersionData(change.getBaselineVersion(), gammaId, modType, value, appId);
            } else if (!isFirstSet) {
               setVersionData(change.getFirstNonCurrentChange(), gammaId, modType, value, appId);
               isFirstSet = true;
            }

            previousItemId = itemId;
         }
      }
   }

   private void setVersionData(ChangeVersion versionedChange, Long gammaId, ModificationType modType, String value, ApplicabilityId appId) {
      // Tolerates the case of having more than one version of an item on a
      // baseline transaction by picking the most recent one
      if (versionedChange.getGammaId() == null || versionedChange.getGammaId().compareTo(gammaId) < 0) {
         versionedChange.setValue(value);
         versionedChange.setModType(modType);
         versionedChange.setGammaId(gammaId);
         versionedChange.setApplicabilityId(appId);
      }
   }

   private int getBaseTxId(long branchId) throws OseeCoreException {
      return getJdbcClient().runPreparedQueryFetchObject(RelationalConstants.TRANSACTION_SENTINEL,
         SELECT_BASE_TRANSACTION, branchId);
   }

}