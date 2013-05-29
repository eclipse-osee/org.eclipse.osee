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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.database.schema.DatabaseTxCallable;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.internal.loader.RelationalConstants;

/**
 * @author Ryan D. Brooks
 */
public class PurgeTransactionTxCallable extends DatabaseTxCallable<Void> {

   private static final String UPDATE_TXS_DETAILS_COMMENT =
      "UPDATE osee_tx_details SET osee_comment = replace(osee_comment, ?, ?) WHERE osee_comment like ?";

   private static final String DELETE_TXS = "delete from osee_txs where branch_id = ? and transaction_id = ?";
   private static final String DELETE_TX_DETAILS =
      "delete from osee_tx_details where branch_id = ? and transaction_id = ?";

   private static final String SELECT_AFFECTED_ITEMS =
      "SELECT %s as item_id from osee_txs txs, %s item where txs.branch_id = ? and txs.transaction_id = ? AND txs.gamma_id = item.gamma_id";

   private static final String FIND_NEW_TX_CURRENTS =
      "SELECT oj.id as item_id, txs.mod_type, txs.gamma_id, txs.transaction_id from osee_join_id oj, %s item, osee_txs txs where oj.query_id = ? and oj.id = item.%s and item.gamma_id = txs.gamma_id and txs.branch_id = ? order by oj.id desc, txs.transaction_id desc";

   private static final String UPDATE_TX_CURRENT =
      "update osee_txs set tx_current = ? where branch_id = ? and transaction_id = ? and gamma_id = ?";

   private static final String GET_PRIOR_TRANSACTION =
      "select max(transaction_id) FROM osee_tx_details where branch_id = ? and transaction_id < ?";

   private static final String SELECT_TRANSACTION_BRANCH_ID =
      "select branch_id from osee_tx_details WHERE transaction_id = ?";

   private final Collection<? extends ITransaction> txIdsToDelete;

   public PurgeTransactionTxCallable(Log logger, IOseeDatabaseService databaseService, Collection<? extends ITransaction> txIdsToDelete) {
      super(logger, databaseService, "Purge transactions");
      this.txIdsToDelete = txIdsToDelete;
   }

   private List<ITransaction> sortTxs(Collection<? extends ITransaction> txIdsToDelete) {
      List<ITransaction> txs = new ArrayList<ITransaction>(txIdsToDelete);
      if (txs.size() > 1) {
         Collections.sort(txs, new Comparator<ITransaction>() {

            @Override
            public int compare(ITransaction o1, ITransaction o2) {
               return o1.getGuid().compareTo(o2.getGuid());
            }
         });
      }
      return txs;
   }

   @Override
   protected Void handleTxWork(OseeConnection connection) throws OseeCoreException {
      Conditions.checkNotNull(txIdsToDelete, "transaction ids to delete");
      Conditions.checkExpressionFailOnTrue(txIdsToDelete.isEmpty(), "transaction ids to delete cannot be empty");

      List<ITransaction> txIds = sortTxs(txIdsToDelete);

      for (ITransaction tx : txIds) {
         Integer txIdToDelete = tx.getGuid();
         getLogger().info("Purging Transaction: [%s]", txIdToDelete);

         List<Object[]> txsToDelete = new ArrayList<Object[]>();

         int txBranchId =
            getDatabaseService().runPreparedQueryFetchObject(RelationalConstants.BRANCH_SENTINEL,
               SELECT_TRANSACTION_BRANCH_ID, txIdToDelete);

         Conditions.checkExpressionFailOnTrue(RelationalConstants.BRANCH_SENTINEL == txBranchId,
            "Cannot find branch for transaction record [%s]", txIdToDelete);
         txsToDelete.add(new Object[] {txBranchId, txIdToDelete});

         int previousTransactionId =
            getDatabaseService().runPreparedQueryFetchObject(RelationalConstants.TRANSACTION_SENTINEL,
               GET_PRIOR_TRANSACTION, txBranchId, txIdToDelete);

         Conditions.checkExpressionFailOnTrue(
            RelationalConstants.TRANSACTION_SENTINEL == previousTransactionId,
            "You are trying to delete transaction [%d] which is a baseline transaction.  If your intent is to delete the Branch use the delete Branch Operation.  \n\nNO TRANSACTIONS WERE DELETED.",
            txIdToDelete);

         //Find affected items
         Map<Integer, IdJoinQuery> arts = findAffectedItems(connection, "art_id", "osee_artifact", txsToDelete);
         Map<Integer, IdJoinQuery> attrs = findAffectedItems(connection, "attr_id", "osee_attribute", txsToDelete);
         Map<Integer, IdJoinQuery> rels =
            findAffectedItems(connection, "rel_link_id", "osee_relation_link", txsToDelete);

         //Update Baseline txs for Child Branches
         setChildBranchBaselineTxs(connection, txIdToDelete, previousTransactionId);

         //Remove txs Rows
         getDatabaseService().runBatchUpdate(connection, DELETE_TX_DETAILS, txsToDelete);
         getDatabaseService().runBatchUpdate(connection, DELETE_TXS, txsToDelete);

         //Updating Previous txs to Current
         List<Object[]> updateData = new ArrayList<Object[]>();
         computeNewTxCurrents(connection, updateData, "art_id", "osee_artifact", arts);
         computeNewTxCurrents(connection, updateData, "attr_id", "osee_attribute", attrs);
         computeNewTxCurrents(connection, updateData, "rel_link_id", "osee_relation_link", rels);
         getDatabaseService().runBatchUpdate(connection, UPDATE_TX_CURRENT, updateData);

         getLogger().info("Transaction: [%s] - purged", txIdToDelete);
      }
      return null;
   }

   private void computeNewTxCurrents(OseeConnection connection, Collection<Object[]> updateData, String itemId, String tableName, Map<Integer, IdJoinQuery> affected) throws OseeCoreException {
      String query = String.format(FIND_NEW_TX_CURRENTS, tableName, itemId);

      for (Entry<Integer, IdJoinQuery> entry : affected.entrySet()) {
         Integer branchId = entry.getKey();
         IdJoinQuery joinQuery = entry.getValue();
         try {
            IOseeStatement statement = getDatabaseService().getStatement(connection);
            try {
               statement.runPreparedQuery(10000, query, joinQuery.getQueryId(), branchId);
               int previousItem = -1;
               while (statement.next()) {
                  int currentItem = statement.getInt("item_id");

                  if (previousItem != currentItem) {
                     ModificationType modType = ModificationType.getMod(statement.getInt("mod_type"));
                     TxChange txCurrent = TxChange.getCurrent(modType);
                     updateData.add(new Object[] {
                        txCurrent.getValue(),
                        branchId,
                        statement.getInt("transaction_id"),
                        statement.getLong("gamma_id")});
                     previousItem = currentItem;
                  }
               }
            } finally {
               statement.close();
            }
         } finally {
            joinQuery.delete(connection);
         }
      }
   }

   private Map<Integer, IdJoinQuery> findAffectedItems(OseeConnection connection, String itemId, String itemTable, List<Object[]> bindDataList) throws OseeCoreException {
      Map<Integer, IdJoinQuery> items = new HashMap<Integer, IdJoinQuery>();
      IOseeStatement statement = getDatabaseService().getStatement(connection);

      try {
         for (Object[] bindData : bindDataList) {
            Integer branchId = (Integer) bindData[0];
            String query = String.format(SELECT_AFFECTED_ITEMS, itemId, itemTable);
            statement.runPreparedQuery(10000, query, bindData);
            IdJoinQuery joinId = JoinUtility.createIdJoinQuery();
            items.put(branchId, joinId);

            while (statement.next()) {
               Integer id = statement.getInt("item_id");
               joinId.add(id);
            }
            joinId.store();
         }
      } finally {
         statement.close();
      }

      return items;
   }

   private void setChildBranchBaselineTxs(OseeConnection connection, int toDeleteTransactionId, int previousTransactionId) throws OseeCoreException {
      List<Object[]> data = new ArrayList<Object[]>();
      if (RelationalConstants.TRANSACTION_SENTINEL != previousTransactionId) {
         data.add(new Object[] {
            String.valueOf(toDeleteTransactionId),
            String.valueOf(previousTransactionId),
            "%" + toDeleteTransactionId});
      }
      if (!data.isEmpty()) {
         getDatabaseService().runBatchUpdate(connection, UPDATE_TXS_DETAILS_COMMENT, data);
      }
   }

}