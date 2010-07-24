/*******************************************************************************
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.branch.management.change;

import java.util.Collection;
import java.util.HashMap;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.change.ChangeItemLoader.ChangeItemFactory;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.ChangeItem;
import org.eclipse.osee.framework.core.message.ChangeVersion;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.JoinUtility.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility.TransactionJoinQuery;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 * @author Ryan Schmitt
 * @author Jeff C. Phillips
 */
public class LoadDeltasBetweenBranches extends AbstractOperation {
   private static final String SELECT_SOURCE_BRANCH_CHANGES =
      "select gamma_id, mod_type from osee_txs where branch_id = ? and tx_current <> ? and transaction_id <> ?";

   private final HashMap<Long, ModificationType> changeByGammaId = new HashMap<Long, ModificationType>();

   private final Collection<ChangeItem> changeData;
   private final TransactionDelta txDelta;
   private final TransactionRecord mergeTransaction;
   private final IOseeDatabaseServiceProvider oseeDatabaseProvider;
   private final ChangeItemLoader changeItemLoader;

   public LoadDeltasBetweenBranches(IOseeDatabaseServiceProvider oseeDatabaseProvider, TransactionDelta txDelta, TransactionRecord mergeTransaction, Collection<ChangeItem> changeData) {
      super("Load Change Data", Activator.PLUGIN_ID);
      this.oseeDatabaseProvider = oseeDatabaseProvider;
      this.mergeTransaction = mergeTransaction;
      this.txDelta = txDelta;
      this.changeData = changeData;
      this.changeItemLoader = new ChangeItemLoader(oseeDatabaseProvider, changeByGammaId);
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
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Conditions.checkExpressionFailOnTrue(txDelta.areOnTheSameBranch(),
         "Unable to compute deltas between transactions on the same branch [%s]", txDelta);

      TransactionJoinQuery txJoin = JoinUtility.createTransactionJoinQuery();

      loadSourceBranchChanges(monitor, txJoin);

      int txJoinId = txJoin.getQueryId();

      loadByItemId(monitor, txJoinId, changeItemLoader.createArtifactChangeItemFactory());
      loadByItemId(monitor, txJoinId, changeItemLoader.createAttributeChangeItemFactory());
      loadByItemId(monitor, txJoinId, changeItemLoader.createRelationChangeItemFactory());

      txJoin.delete();
   }

   private void loadSourceBranchChanges(IProgressMonitor monitor, TransactionJoinQuery txJoin) throws OseeCoreException {
      IOseeStatement chStmt = oseeDatabaseProvider.getOseeDatabaseService().getStatement();
      try {
         chStmt.runPreparedQuery(10000, SELECT_SOURCE_BRANCH_CHANGES, getSourceBranchId(),
            TxChange.NOT_CURRENT.getValue(), getSourceBaselineTransactionId());
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
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

   private void loadByItemId(IProgressMonitor monitor, int txJoinId, ChangeItemFactory factory) throws OseeCoreException {
      HashMap<Integer, ChangeItem> changesByItemId = new HashMap<Integer, ChangeItem>();

      IdJoinQuery idJoin = JoinUtility.createIdJoinQuery();

      changeItemLoader.loadItemIdsBasedOnGammas(monitor, factory, txJoinId, changesByItemId, idJoin);

      idJoin.store();

      if (hasMergeBranch()) {
         loadCurrentData(monitor, factory.getItemTableName(), factory.getItemIdColumnName(), idJoin, changesByItemId,
            mergeTransaction);
      }

      loadCurrentData(monitor, factory.getItemTableName(), factory.getItemIdColumnName(), idJoin, changesByItemId,
         getCompareBranchHeadTx());

      loadNonCurrentSourceData(monitor, factory.getItemTableName(), factory.getItemIdColumnName(), idJoin,
         changesByItemId, factory.getItemValueColumnName());

      idJoin.delete();

      changeData.addAll(changesByItemId.values());
   }

   private void loadCurrentData(IProgressMonitor monitor, String tableName, String columnName, IdJoinQuery idJoin, HashMap<Integer, ChangeItem> changesByItemId, TransactionRecord transactionLimit) throws OseeCoreException {
      IOseeStatement chStmt = oseeDatabaseProvider.getOseeDatabaseService().getStatement();
      try {
         String query = "select txs.gamma_id, txs.mod_type, item." + columnName + " from osee_join_id idj, " //
            + tableName + " item, osee_txs txs where idj.query_id = ? and idj.id = item." + columnName + //
            " and item.gamma_id = txs.gamma_id and txs.tx_current <> ? and txs.branch_id = ? and txs.transaction_id <= ?";

         chStmt.runPreparedQuery(10000, query, idJoin.getQueryId(), TxChange.NOT_CURRENT.getValue(),
            transactionLimit.getBranchId(), transactionLimit.getId());

         while (chStmt.next()) {
            checkForCancelledStatus(monitor);

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

   private void loadNonCurrentSourceData(IProgressMonitor monitor, String tableName, String idColumnName, IdJoinQuery idJoin, HashMap<Integer, ChangeItem> changesByItemId, String columnValueName) throws OseeCoreException {
      IOseeStatement chStmt = oseeDatabaseProvider.getOseeDatabaseService().getStatement();
      String query;

      try {
         String valueColumnName = columnValueName != null ? "item." + columnValueName + "," : "";
         query =
            "select " + valueColumnName + "item." + idColumnName + ", txs.gamma_id, txs.mod_type, txs.transaction_id from osee_join_id idj, " //
               + tableName + " item, osee_txs txs where idj.query_id = ? and idj.id = item." + idColumnName + //
               " and item.gamma_id = txs.gamma_id and txs.tx_current = ? and txs.branch_id = ? order by idj.id, txs.transaction_id asc";

         chStmt.runPreparedQuery(10000, query, idJoin.getQueryId(), TxChange.NOT_CURRENT.getValue(),
            getSourceBranchId());

         int baselineTransactionId = getSourceBaselineTransactionId();
         int previousItemId = -1;
         boolean isFirstSet = false;
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
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
               loadVersionData(change.getBaselineVersion(), gammaId, modType, value);
            } else if (!isFirstSet) {
               loadVersionData(change.getFirstNonCurrentChange(), gammaId, modType, value);
               isFirstSet = true;
            }

            previousItemId = itemId;
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadVersionData(ChangeVersion versionedChange, Long gammaId, ModificationType modType, String value) {
      // Tolerates the case of having more than one version of an item on a
      // baseline transaction by picking the most recent one
      if (versionedChange.getGammaId() == null || versionedChange.getGammaId().compareTo(gammaId) < 0) {
         versionedChange.setValue(value);
         versionedChange.setModType(modType);
         versionedChange.setGammaId(gammaId);
      }
   }
}