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
import java.util.function.Consumer;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.sql.join.IdJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Ryan D. Brooks
 */
public class PurgeTransactionTxCallable extends AbstractDatastoreTxCallable<Integer> {

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

   private final SqlJoinFactory joinFactory;
   private final Collection<? extends TransactionId> txIdsToDelete;
   private int previousItem;

   public PurgeTransactionTxCallable(Log logger, OrcsSession session, JdbcClient jdbcClient, SqlJoinFactory joinFactory, Collection<? extends TransactionId> txIdsToDelete) {
      super(logger, session, jdbcClient);
      this.joinFactory = joinFactory;
      this.txIdsToDelete = txIdsToDelete;
      previousItem = -1;
   }

   private List<TransactionId> sortTxs(Collection<? extends TransactionId> txIdsToDelete) {
      List<TransactionId> txs = new ArrayList<>(txIdsToDelete);
      if (txs.size() > 1) {
         Collections.sort(txs, new Comparator<TransactionId>() {

            @Override
            public int compare(TransactionId o1, TransactionId o2) {
               return o1.getId().compareTo(o2.getId());
            }
         });
      }
      return txs;
   }

   @Override
   protected Integer handleTxWork(JdbcConnection connection) throws OseeCoreException {
      Conditions.checkNotNull(txIdsToDelete, "transaction ids to delete");
      Conditions.checkExpressionFailOnTrue(txIdsToDelete.isEmpty(), "transaction ids to delete cannot be empty");

      List<TransactionId> txIds = sortTxs(txIdsToDelete);
      int purgeCount = 0;
      for (TransactionId txIdToDelete : txIds) {
         getLogger().info("Purging Transaction: [%s]", txIdToDelete);

         List<Object[]> txsToDelete = new ArrayList<>();

         BranchId txBranchId = getJdbcClient().fetch(BranchId.SENTINEL, SELECT_TRANSACTION_BRANCH_ID, txIdToDelete);
         if (txBranchId.isInvalid()) {
            throw new OseeArgumentException("Cannot find branch for transaction record [%s]", txIdToDelete);
         }

         txsToDelete.add(new Object[] {txBranchId, txIdToDelete});

         TransactionId previousTransactionId =
            getJdbcClient().fetch(TransactionId.SENTINEL, GET_PRIOR_TRANSACTION, txBranchId, txIdToDelete);
         if (previousTransactionId.isInvalid()) {
            throw new OseeArgumentException(
               "You are trying to delete transaction [%d] which is a baseline transaction.  If your intent is to delete the Branch use the delete Branch Operation.  \n\nNO TRANSACTIONS WERE DELETED.",
               txIdToDelete);
         }
         //Find affected items
         Map<BranchId, IdJoinQuery> arts = findAffectedItems(connection, "art_id", "osee_artifact", txsToDelete);
         Map<BranchId, IdJoinQuery> attrs = findAffectedItems(connection, "attr_id", "osee_attribute", txsToDelete);
         Map<BranchId, IdJoinQuery> rels =
            findAffectedItems(connection, "rel_link_id", "osee_relation_link", txsToDelete);

         //Update Baseline txs for Child Branches
         setChildBranchBaselineTxs(connection, txIdToDelete, previousTransactionId);

         //Remove txs Rows
         getJdbcClient().runBatchUpdate(connection, DELETE_TX_DETAILS, txsToDelete);
         getJdbcClient().runBatchUpdate(connection, DELETE_TXS, txsToDelete);

         //Updating Previous txs to Current
         List<Object[]> updateData = new ArrayList<>();
         computeNewTxCurrents(connection, updateData, "art_id", "osee_artifact", arts);
         computeNewTxCurrents(connection, updateData, "attr_id", "osee_attribute", attrs);
         computeNewTxCurrents(connection, updateData, "rel_link_id", "osee_relation_link", rels);
         getJdbcClient().runBatchUpdate(connection, UPDATE_TX_CURRENT, updateData);
         purgeCount++;
         getLogger().info("Transaction: [%s] - purged", txIdToDelete);
      }
      return purgeCount;
   }

   private void computeNewTxCurrents(JdbcConnection connection, Collection<Object[]> updateData, String itemId, String tableName, Map<BranchId, IdJoinQuery> affected) throws OseeCoreException {
      String query = String.format(FIND_NEW_TX_CURRENTS, tableName, itemId);

      for (Entry<BranchId, IdJoinQuery> entry : affected.entrySet()) {
         BranchId branch = entry.getKey();
         try (IdJoinQuery joinQuery = entry.getValue()) {

            Consumer<JdbcStatement> consumer = stmt -> {
               int currentItem = stmt.getInt("item_id");

               if (previousItem != currentItem) {
                  ModificationType modType = ModificationType.getMod(stmt.getInt("mod_type"));
                  TxChange txCurrent = TxChange.getCurrent(modType);
                  updateData.add(new Object[] {
                     txCurrent.getValue(),
                     branch,
                     stmt.getLong("transaction_id"),
                     stmt.getLong("gamma_id")});
                  previousItem = currentItem;
               }
            };

            getJdbcClient().runQuery(connection, consumer, JdbcConstants.JDBC__MAX_FETCH_SIZE, query,
               joinQuery.getQueryId(), branch);
         }
      }
   }

   private Map<BranchId, IdJoinQuery> findAffectedItems(JdbcConnection connection, String itemId, String itemTable, List<Object[]> bindDataList) throws OseeCoreException {
      Map<BranchId, IdJoinQuery> items = new HashMap<>();
      JdbcStatement statement = getJdbcClient().getStatement(connection);

      try {
         for (Object[] bindData : bindDataList) {
            String query = String.format(SELECT_AFFECTED_ITEMS, itemId, itemTable);
            statement.runPreparedQuery(JdbcConstants.JDBC__MAX_FETCH_SIZE, query, bindData);
            IdJoinQuery joinId = joinFactory.createIdJoinQuery();
            items.put((BranchId) bindData[0], joinId);

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

   private void setChildBranchBaselineTxs(JdbcConnection connection, TransactionId toDeleteTransactionId, TransactionId previousTransactionId) throws OseeCoreException {
      List<Object[]> data = new ArrayList<>();
      if (previousTransactionId.isValid()) {
         data.add(new Object[] {
            String.valueOf(toDeleteTransactionId.getId()),
            String.valueOf(previousTransactionId),
            "%" + toDeleteTransactionId.getId()});
      }
      if (!data.isEmpty()) {
         getJdbcClient().runBatchUpdate(connection, UPDATE_TXS_DETAILS_COMMENT, data);
      }
   }

}