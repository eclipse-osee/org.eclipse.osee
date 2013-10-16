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
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.TransactionJoinQuery;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
public class LoadDeltasBetweenTxsOnTheSameBranch extends AbstractDatastoreCallable<List<ChangeItem>> {

   private static final String SELECT_CHANGES_BETWEEN_TRANSACTIONS =
      "select gamma_id, mod_type from osee_txs where branch_id = ? and transaction_id > ? and transaction_id <= ?";

   private final HashMap<Long, ModificationType> changeByGammaId = new HashMap<Long, ModificationType>();

   private final TransactionDelta txDelta;
   private final ChangeItemLoader changeItemLoader;

   public LoadDeltasBetweenTxsOnTheSameBranch(Log logger, OrcsSession session, IOseeDatabaseService dbService, TransactionDelta txDelta) {
      super(logger, session, dbService);
      this.txDelta = txDelta;
      this.changeItemLoader = new ChangeItemLoader(dbService, changeByGammaId);
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
   public List<ChangeItem> call() throws Exception {
      List<ChangeItem> changeData = new LinkedList<ChangeItem>();

      Conditions.checkExpressionFailOnTrue(!txDelta.areOnTheSameBranch(),
         "Unable to compute deltas between transactions on different branches [%s]", txDelta);

      TransactionJoinQuery txJoin = JoinUtility.createTransactionJoinQuery();

      loadChangesAtEndTx(txJoin);

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

   private void loadChangesAtEndTx(TransactionJoinQuery txJoin) throws OseeCoreException {
      IOseeStatement chStmt = getDatabaseService().getStatement();
      try {
         chStmt.runPreparedQuery(MAX_FETCH, SELECT_CHANGES_BETWEEN_TRANSACTIONS, getBranchId(), getStartTx().getId(),
            getEndTx().getId());
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

      IdJoinQuery idJoin = JoinUtility.createIdJoinQuery();

      HashMap<Integer, ChangeItem> changesByItemId = new HashMap<Integer, ChangeItem>();
      changeItemLoader.loadItemIdsBasedOnGammas(factory, txJoinId, changesByItemId, idJoin);

      idJoin.store();

      loadCurrentData(factory.getItemTableName(), factory.getItemIdColumnName(), idJoin.getQueryId(), changesByItemId,
         getStartTx());

      idJoin.delete();

      changeData.addAll(changesByItemId.values());
   }

   private void loadCurrentData(String tableName, String columnName, int queryId, HashMap<Integer, ChangeItem> changesByItemId, TransactionRecord transactionLimit) throws OseeCoreException {
      IOseeStatement chStmt = getDatabaseService().getStatement();
      try {
         String query = "select txs.gamma_id, txs.mod_type, item." + columnName + " from osee_join_id idj, " //
            + tableName + " item, osee_txs txs where idj.query_id = ? and idj.id = item." + columnName + //
            " and item.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.transaction_id <= ?";

         chStmt.runPreparedQuery(MAX_FETCH, query, queryId, transactionLimit.getBranchId(), transactionLimit.getId());

         while (chStmt.next()) {
            checkForCancelled();

            Integer itemId = chStmt.getInt(columnName);
            Long gammaId = chStmt.getLong("gamma_id");
            ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));

            ChangeItem change = changesByItemId.get(itemId);
            change.getDestinationVersion().setModType(modType);
            change.getDestinationVersion().setGammaId(gammaId);
            change.getBaselineVersion().copy(change.getDestinationVersion());
         }
      } finally {
         chStmt.close();
      }
   }
}