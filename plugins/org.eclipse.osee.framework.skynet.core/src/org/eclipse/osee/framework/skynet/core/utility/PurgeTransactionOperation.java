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

package org.eclipse.osee.framework.skynet.core.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.operation.NullOperationLogger;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Ryan D. Brooks
 */
public class PurgeTransactionOperation extends AbstractDbTxOperation {

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

   private final List<Integer> txIdsToDelete;
   private boolean success;
   private final BranchCache branchCache;
   public static interface PurgeTransactionListener {
      void onPurgeTransactionSuccess(Collection<TransactionRecord> transactions);
   }

   private final Set<PurgeTransactionListener> listeners = new CopyOnWriteArraySet<PurgeTransactionListener>();
   private Collection<TransactionRecord> changedTransactions = new ArrayList<TransactionRecord>();

   public PurgeTransactionOperation(JdbcClient jdbcClient, BranchCache branchCache, OperationLogger logger, List<Integer> txIdsToDelete) {
      super(jdbcClient, "Purge transactions " + txIdsToDelete, Activator.PLUGIN_ID, logger);
      this.success = false;
      this.branchCache = branchCache;
      this.txIdsToDelete = txIdsToDelete;
   }

   public PurgeTransactionOperation(JdbcClient jdbcClient, BranchCache branchCache, List<Integer> txIdsToDelete) {
      this(jdbcClient, branchCache, NullOperationLogger.getSingleton(), txIdsToDelete);
   }

   public void addListener(PurgeTransactionListener listener) {
      if (listener != null) {
         listeners.add(listener);
      }
   }

   public void removeListener(PurgeTransactionListener listener) {
      if (listener != null) {
         listeners.remove(listener);
      }
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, JdbcConnection connection) throws OseeCoreException {
      log();

      monitor.beginTask("Purge Transaction(s)", txIdsToDelete.size() * 5);
      Conditions.checkNotNull(txIdsToDelete, "transaction ids to delete");
      Conditions.checkExpressionFailOnTrue(txIdsToDelete.isEmpty(), "transaction ids to delete cannot be empty");

      if (txIdsToDelete.size() > 1) {
         java.util.Collections.sort(txIdsToDelete);
      }

      for (Integer txIdToDelete : txIdsToDelete) {
         log("Purging Transaction: " + txIdToDelete);

         monitor.subTask("Find Prior Transaction");
         TransactionRecord previousTransaction = findPriorTransactions(txIdToDelete);
         monitor.worked(1);

         List<Object[]> txsToDelete = new ArrayList<Object[]>();
         logf("Adding tx to list: %d", txIdToDelete);
         TransactionRecord toDeleteTransaction = branchCache.getOrLoad(txIdToDelete);
         changedTransactions.add(toDeleteTransaction);
         Conditions.checkNotNull(toDeleteTransaction, "transaction", " record [%s]", txIdToDelete);
         txsToDelete.add(new Object[] {toDeleteTransaction.getBranch().getUuid(), txIdToDelete});

         monitor.subTask("Find affected items");
         Map<Long, IdJoinQuery> arts = findAffectedItems(connection, "art_id", "osee_artifact", txsToDelete);
         Map<Long, IdJoinQuery> attrs = findAffectedItems(connection, "attr_id", "osee_attribute", txsToDelete);
         Map<Long, IdJoinQuery> rels = findAffectedItems(connection, "rel_link_id", "osee_relation_link", txsToDelete);
         monitor.worked(1);

         monitor.subTask("Update Baseline Txs for Child Branches");
         setChildBranchBaselineTxs(connection, toDeleteTransaction, previousTransaction);
         monitor.worked(1);

         monitor.subTask("Remove Tx Rows");
         getJdbcClient().runBatchUpdate(connection, DELETE_TX_DETAILS, txsToDelete);
         getJdbcClient().runBatchUpdate(connection, DELETE_TXS, txsToDelete);
         monitor.worked(1);

         monitor.subTask("Updating Previous Tx to Current");
         List<Object[]> updateData = new ArrayList<Object[]>();
         computeNewTxCurrents(connection, updateData, "art_id", "osee_artifact", arts);
         computeNewTxCurrents(connection, updateData, "attr_id", "osee_attribute", attrs);
         computeNewTxCurrents(connection, updateData, "rel_link_id", "osee_relation_link", rels);
         getJdbcClient().runBatchUpdate(connection, UPDATE_TX_CURRENT, updateData);
         monitor.worked(1);
      }
      monitor.done();

      success = true;
      log("...done.");
   }

   @Override
   protected void handleTxFinally(IProgressMonitor monitor) {
      if (success && changedTransactions != null) {
         for (PurgeTransactionListener listener : listeners) {
            listener.onPurgeTransactionSuccess(changedTransactions);
         }
      }
      changedTransactions = null;
   }

   private void computeNewTxCurrents(JdbcConnection connection, Collection<Object[]> updateData, String itemId, String tableName, Map<Long, IdJoinQuery> affected) throws OseeCoreException {
      String query = String.format(FIND_NEW_TX_CURRENTS, tableName, itemId);

      for (Entry<Long, IdJoinQuery> entry : affected.entrySet()) {
         Long branchUuid = entry.getKey();
         IdJoinQuery joinQuery = entry.getValue();

         JdbcStatement statement = getJdbcClient().getStatement(connection);
         try {
            statement.runPreparedQuery(JdbcConstants.JDBC__MAX_FETCH_SIZE, query, joinQuery.getQueryId(), branchUuid);
            int previousItem = -1;
            while (statement.next()) {
               int currentItem = statement.getInt("item_id");

               if (previousItem != currentItem) {
                  ModificationType modType = ModificationType.getMod(statement.getInt("mod_type"));
                  TxChange txCurrent = TxChange.getCurrent(modType);
                  updateData.add(new Object[] {
                     txCurrent.getValue(),
                     branchUuid,
                     statement.getInt("transaction_id"),
                     statement.getLong("gamma_id")});
                  previousItem = currentItem;
               }
            }
         } finally {
            statement.close();
         }
         joinQuery.delete(connection);
      }
   }

   private Map<Long, IdJoinQuery> findAffectedItems(JdbcConnection connection, String itemId, String itemTable, List<Object[]> bindDataList) throws OseeCoreException {
      Map<Long, IdJoinQuery> items = new HashMap<Long, IdJoinQuery>();
      JdbcStatement statement = getJdbcClient().getStatement(connection);

      try {
         for (Object[] bindData : bindDataList) {
            Long branchUuid = (Long) bindData[0];
            String query = String.format(SELECT_AFFECTED_ITEMS, itemId, itemTable);
            statement.runPreparedQuery(JdbcConstants.JDBC__MAX_FETCH_SIZE, query, bindData);
            IdJoinQuery joinId = JoinUtility.createIdJoinQuery(getJdbcClient());
            items.put(branchUuid, joinId);

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

   private TransactionRecord findPriorTransactions(Integer txIdToDelete) throws OseeCoreException {
      TransactionRecord fromTransaction = branchCache.getOrLoad(txIdToDelete);
      TransactionRecord previousTransaction;
      try {
         previousTransaction = branchCache.getPriorTransaction(fromTransaction);
      } catch (TransactionDoesNotExist ex) {
         throw new OseeArgumentException(
            "You are trying to delete Transaction [%d] which is a baseline transaction.  If your intent is to delete the Branch use the delete Branch Operation.  \n\nNO TRANSACTIONS WERE DELETED.",
            txIdToDelete);
      }
      return previousTransaction;
   }

   private void setChildBranchBaselineTxs(JdbcConnection connection, TransactionRecord toDeleteTransaction, TransactionRecord previousTransaction) throws OseeCoreException {
      List<Object[]> data = new ArrayList<Object[]>();
      if (previousTransaction != null) {
         int toDeleteTransactionId = toDeleteTransaction.getId();

         data.add(new Object[] {
            String.valueOf(toDeleteTransactionId),
            String.valueOf(previousTransaction.getId()),
            "%" + toDeleteTransactionId});
      }
      if (!data.isEmpty()) {
         getJdbcClient().runBatchUpdate(connection, UPDATE_TXS_DETAILS_COMMENT, data);
      }
   }
}