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

import static org.eclipse.osee.framework.database.core.IOseeStatement.MAX_FETCH;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeVersion;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.TransactionJoinQuery;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.callable.AbstractDatastoreCallable;
import org.eclipse.osee.orcs.db.internal.change.ChangeItemLoader.ChangeItemFactory;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 * @author Ryan Schmitt
 * @author Jeff C. Phillips
 */
public class LoadDeltasBetweenBranches extends AbstractDatastoreCallable<List<ChangeItem>> {
   private static final String SELECT_SOURCE_BRANCH_CHANGES =
      "select gamma_id, mod_type from osee_txs where branch_id = ? and tx_current <> ? and transaction_id <> ?";

   private final HashMap<Long, ModificationType> changeByGammaId = new HashMap<Long, ModificationType>();

   private final TransactionDelta txDelta;
   private final TransactionRecord mergeTransaction;
   private final ChangeItemLoader changeItemLoader;

   public LoadDeltasBetweenBranches(Log logger, OrcsSession session, IOseeDatabaseService dbService, TransactionDelta txDelta, TransactionRecord mergeTransaction) {
      super(logger, session, dbService);
      this.mergeTransaction = mergeTransaction;
      this.txDelta = txDelta;
      this.changeItemLoader = new ChangeItemLoader(dbService, changeByGammaId);
   }

   private int getSourceBranchId() {
      return txDelta.getStartTx().getBranchId();
   }

   private int getSourceBaselineTransactionId() throws OseeCoreException {
      return txDelta.getStartTx().getBranch().getBaseTransaction().getId();
   }

   private TransactionRecord getCompareBranchHeadTx() {
      return txDelta.getEndTx();
   }

   private boolean hasMergeBranch() {
      return mergeTransaction != null;
   }

   @Override
   public List<ChangeItem> call() throws Exception {
      List<ChangeItem> changeData = new LinkedList<ChangeItem>();

      Conditions.checkExpressionFailOnTrue(txDelta.areOnTheSameBranch(),
         "Unable to compute deltas between transactions on the same branch [%s]", txDelta);

      TransactionJoinQuery txJoin = JoinUtility.createTransactionJoinQuery();

      loadSourceBranchChanges(txJoin);

      int txJoinId = txJoin.getQueryId();
      try {
         loadByItemId(changeData, txJoinId, changeItemLoader.createArtifactChangeItemFactory());
         loadByItemId(changeData, txJoinId, changeItemLoader.createAttributeChangeItemFactory());
         loadByItemId(changeData, txJoinId, changeItemLoader.createRelationChangeItemFactory());
      } finally {
         try {
            txJoin.delete();
         } finally {
            changeByGammaId.clear();
         }
      }
      return changeData;
   }

   private void loadSourceBranchChanges(TransactionJoinQuery txJoin) throws OseeCoreException {
      IOseeStatement chStmt = getDatabaseService().getStatement();
      try {
         chStmt.runPreparedQuery(MAX_FETCH, SELECT_SOURCE_BRANCH_CHANGES, getSourceBranchId(),
            TxChange.NOT_CURRENT.getValue(), getSourceBaselineTransactionId());
         while (chStmt.next()) {
            checkForCancelled();
            Long gammaId = chStmt.getLong("gamma_id");
            ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));

            txJoin.add(gammaId, -1);
            changeByGammaId.put(gammaId, modType);
         }
         txJoin.store();
      } finally {
         chStmt.close();
      }
   }

   private void loadByItemId(Collection<ChangeItem> changeData, int txJoinId, ChangeItemFactory factory) throws OseeCoreException {
      HashMap<Integer, ChangeItem> changesByItemId = new HashMap<Integer, ChangeItem>();

      IdJoinQuery idJoin = JoinUtility.createIdJoinQuery();
      try {
         changeItemLoader.loadItemIdsBasedOnGammas(factory, txJoinId, changesByItemId, idJoin);

         idJoin.store();

         if (hasMergeBranch()) {
            loadCurrentData(factory.getItemTableName(), factory.getItemIdColumnName(), idJoin, changesByItemId,
               mergeTransaction);
         }

         loadCurrentData(factory.getItemTableName(), factory.getItemIdColumnName(), idJoin, changesByItemId,
            getCompareBranchHeadTx());

         loadNonCurrentSourceData(factory.getItemTableName(), factory.getItemIdColumnName(), idJoin, changesByItemId,
            factory.getItemValueColumnName());
         changeData.addAll(changesByItemId.values());

      } finally {
         idJoin.delete();
      }
   }

   private void loadCurrentData(String tableName, String columnName, IdJoinQuery idJoin, HashMap<Integer, ChangeItem> changesByItemId, TransactionRecord transactionLimit) throws OseeCoreException {
      IOseeStatement chStmt = getDatabaseService().getStatement();
      try {
         String query = "select txs.gamma_id, txs.mod_type, item." + columnName + " from osee_join_id idj, " //
            + tableName + " item, osee_txs txs where idj.query_id = ? and idj.id = item." + columnName + //
            " and item.gamma_id = txs.gamma_id and txs.tx_current <> ? and txs.branch_id = ? and txs.transaction_id <= ?";

         chStmt.runPreparedQuery(MAX_FETCH, query, idJoin.getQueryId(), TxChange.NOT_CURRENT.getValue(),
            transactionLimit.getBranchId(), transactionLimit.getId());

         while (chStmt.next()) {
            checkForCancelled();

            Integer itemId = chStmt.getInt(columnName);
            Long gammaId = chStmt.getLong("gamma_id");
            ChangeItem change = changesByItemId.get(itemId);

            if (transactionLimit.getBranch().getBranchType().isMergeBranch()) {
               change.getNetChange().setGammaId(gammaId);
               change.getNetChange().setModType(ModificationType.MERGED);
            } else {
               change.getDestinationVersion().setModType(ModificationType.getMod(chStmt.getInt("mod_type")));
               change.getDestinationVersion().setGammaId(gammaId);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadNonCurrentSourceData(String tableName, String idColumnName, IdJoinQuery idJoin, HashMap<Integer, ChangeItem> changesByItemId, String columnValueName) throws OseeCoreException {
      IOseeStatement chStmt = getDatabaseService().getStatement();
      String query;

      try {
         String valueColumnName = columnValueName != null ? "item." + columnValueName + "," : "";
         query =
            "select " + valueColumnName + "item." + idColumnName + ", txs.gamma_id, txs.mod_type, txs.transaction_id from osee_join_id idj, " //
               + tableName + " item, osee_txs txs where idj.query_id = ? and idj.id = item." + idColumnName + //
               " and item.gamma_id = txs.gamma_id and txs.tx_current = ? and txs.branch_id = ? order by idj.id, txs.transaction_id asc";

         chStmt.runPreparedQuery(MAX_FETCH, query, idJoin.getQueryId(), TxChange.NOT_CURRENT.getValue(),
            getSourceBranchId());

         int baselineTransactionId = getSourceBaselineTransactionId();
         int previousItemId = -1;
         boolean isFirstSet = false;
         while (chStmt.next()) {
            checkForCancelled();
            int itemId = chStmt.getInt(idColumnName);
            Integer transactionId = chStmt.getInt("transaction_id");
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
            if (baselineTransactionId == transactionId) {
               setVersionData(change.getBaselineVersion(), gammaId, modType, value);
            } else if (!isFirstSet) {
               setVersionData(change.getFirstNonCurrentChange(), gammaId, modType, value);
               isFirstSet = true;
            }

            previousItemId = itemId;
         }
      } finally {
         chStmt.close();
      }
   }

   private void setVersionData(ChangeVersion versionedChange, Long gammaId, ModificationType modType, String value) {
      // Tolerates the case of having more than one version of an item on a
      // baseline transaction by picking the most recent one
      if (versionedChange.getGammaId() == null || versionedChange.getGammaId().compareTo(gammaId) < 0) {
         versionedChange.setValue(value);
         versionedChange.setModType(modType);
         versionedChange.setGammaId(gammaId);
      }
   }

}