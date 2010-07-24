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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.ChangeItem;
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
public class LoadDeltasBetweenTxsOnTheSameBranch extends AbstractOperation {

   private static final String SELECT_CHANGES_AT_TRANSACTION =
      "select gamma_id, mod_type from osee_txs where branch_id = ? and transaction_id = ?";

   private final HashMap<Long, ModificationType> changeByGammaId = new HashMap<Long, ModificationType>();

   private final Collection<ChangeItem> changeData;
   private final TransactionDelta txDelta;
   private final IOseeDatabaseServiceProvider oseeDatabaseProvider;
   private final ChangeItemLoader changeItemLoader;

   public LoadDeltasBetweenTxsOnTheSameBranch(IOseeDatabaseServiceProvider oseeDatabaseProvider, TransactionDelta txDelta, Collection<ChangeItem> changeData) {
      super("Load Change Data", Activator.PLUGIN_ID);
      this.oseeDatabaseProvider = oseeDatabaseProvider;
      this.txDelta = txDelta;
      this.changeData = changeData;
      this.changeItemLoader = new ChangeItemLoader(oseeDatabaseProvider, changeByGammaId);
   }

   private int getBranchId() {
      return getEndTx().getBranchId();
   }

   private TransactionRecord getEndTx() {
      return txDelta.getEndTx();
   }

   private TransactionRecord getStartTx() {
      return txDelta.getStartTx();
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Conditions.checkExpressionFailOnTrue(!txDelta.areOnTheSameBranch(),
         "Unable to compute deltas between transactions on different branches [%s]", txDelta);

      TransactionJoinQuery txJoin = JoinUtility.createTransactionJoinQuery();

      loadChangesAtEndTx(monitor, txJoin);

      int txJoinId = txJoin.getQueryId();

      loadByItemId(monitor, txJoinId, changeItemLoader.createArtifactChangeItemFactory());
      loadByItemId(monitor, txJoinId, changeItemLoader.createAttributeChangeItemFactory());
      loadByItemId(monitor, txJoinId, changeItemLoader.createRelationChangeItemFactory());

      txJoin.delete();
   }

   private void loadChangesAtEndTx(IProgressMonitor monitor, TransactionJoinQuery txJoin) throws OseeCoreException {
      IOseeStatement chStmt = oseeDatabaseProvider.getOseeDatabaseService().getStatement();
      try {
         chStmt.runPreparedQuery(10000, SELECT_CHANGES_AT_TRANSACTION, getBranchId(), getEndTx().getId());
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

      loadCurrentData(monitor, factory.getItemTableName(), factory.getItemIdColumnName(), idJoin.getQueryId(),
         changesByItemId, getStartTx());

      idJoin.delete();

      changeData.addAll(changesByItemId.values());
   }

   private void loadCurrentData(IProgressMonitor monitor, String tableName, String columnName, int queryId, HashMap<Integer, ChangeItem> changesByItemId, TransactionRecord transactionLimit) throws OseeCoreException {
      IOseeStatement chStmt = oseeDatabaseProvider.getOseeDatabaseService().getStatement();
      try {
         String query = "select txs.gamma_id, txs.mod_type, item." + columnName + " from osee_join_id idj, " //
            + tableName + " item, osee_txs txs where idj.query_id = ? and idj.id = item." + columnName + //
            " and item.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.transaction_id <= ?";

         chStmt.runPreparedQuery(10000, query, queryId, transactionLimit.getBranchId(), transactionLimit.getId());

         while (chStmt.next()) {
            checkForCancelledStatus(monitor);

            Integer itemId = chStmt.getInt(columnName);
            Long gammaId = chStmt.getLong("gamma_id");
            ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));

            ChangeItem change = changesByItemId.get(itemId);
            change.getDestinationVersion().setModType(modType);
            change.getDestinationVersion().setGammaId(gammaId);
         }
      } finally {
         chStmt.close();
      }
   }
}