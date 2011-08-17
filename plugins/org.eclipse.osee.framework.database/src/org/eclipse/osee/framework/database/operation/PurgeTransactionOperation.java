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

package org.eclipse.osee.framework.database.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.operation.NullOperationLogger;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.TransactionJoinQuery;
import org.eclipse.osee.framework.database.internal.Activator;
import org.eclipse.osee.framework.logging.OseeLog;

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
      "SELECT %s as item_id, txs.branch_id from osee_join_transaction ojt, osee_txs txs, %s item where ojt.query_id = ? AND ojt.transaction_id = txs.transaction_id AND txs.gamma_id = item.gamma_id";

   private static final String FIND_NEW_TX_CURRENTS =
      "SELECT oj.id as item_id, txs.mod_type, txs.gamma_id, txs.transaction_id from osee_join_id oj, %s item, osee_txs txs where oj.query_id = ? and oj.id = item.%s and item.gamma_id = txs.gamma_id and txs.branch_id = ? order by oj.id desc, txs.transaction_id desc";

   private static final String UPDATE_TX_CURRENT =
      "update osee_txs set tx_current = ? where branch_id = ? and transaction_id = ? and gamma_id = ?";

   private final int[] txIdsToDelete;
   private boolean success;
   private final TransactionCache transactionCache;
   public static interface PurgeTransactionListener {
      void onPurgeTransactionSuccess(Collection<TransactionRecord> transactions);
   }

   private final Set<PurgeTransactionListener> listeners = new CopyOnWriteArraySet<PurgeTransactionListener>();
   private Collection<TransactionRecord> changedTransactions;

   public PurgeTransactionOperation(IOseeDatabaseService databaseService, OperationLogger logger, int... txIdsToDelete) {
      super(databaseService, String.format("Delete transactions: %s", Arrays.toString(txIdsToDelete)),
         Activator.PLUGIN_ID, logger);
      this.txIdsToDelete = txIdsToDelete;
      this.success = false;
      transactionCache = Activator.getOseeCachingService().getTransactionCache();
   }

   public PurgeTransactionOperation(IOseeDatabaseService databaseService, int... txIdsToDelete) {
      this(databaseService, NullOperationLogger.getSingleton(), txIdsToDelete);
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
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
      log();
      log("Purging Transactions...");
      Conditions.checkNotNull(txIdsToDelete, "transaction ids to delete");
      Conditions.checkExpressionFailOnTrue(txIdsToDelete.length <= 0, "transaction ids to delete cannot be empty");

      Arrays.sort(txIdsToDelete);

      TransactionJoinQuery txsToDeleteQuery = JoinUtility.createTransactionJoinQuery();

      Map<TransactionRecord, TransactionRecord> deleteToPreviousTx =
         findPriorTransactions(monitor, txsToDeleteQuery, 0.20);

      changedTransactions = deleteToPreviousTx.keySet();

      txsToDeleteQuery.store(connection);

      int txQueryId = txsToDeleteQuery.getQueryId();

      try {
         Map<Integer, IdJoinQuery> arts = findAffectedItems(connection, "art_id", "osee_artifact", txQueryId);
         Map<Integer, IdJoinQuery> attrs = findAffectedItems(connection, "attr_id", "osee_attribute", txQueryId);
         Map<Integer, IdJoinQuery> rels = findAffectedItems(connection, "rel_link_id", "osee_relation_link", txQueryId);
         monitor.worked(calculateWork(0.20));

         setChildBranchBaselineTxs(connection, monitor, deleteToPreviousTx, 0.20);

         monitor.subTask("Remove Tx Rows");
         List<Object[]> txsToDelete = new ArrayList<Object[]>();
         for (int txId : txIdsToDelete) {
            log("  Adding tx to list:" + txId);
            txsToDelete.add(new Object[] {transactionCache.getById(txId).getBranch().getId(), txId});
         }
         ConnectionHandler.runBatchUpdate(connection, DELETE_TX_DETAILS, txsToDelete);
         ConnectionHandler.runBatchUpdate(connection, DELETE_TXS, txsToDelete);

         monitor.subTask("Updating Previous Tx to Current");
         List<Object[]> updateData = new ArrayList<Object[]>();
         computeNewTxCurrents(connection, updateData, "art_id", "osee_artifact", arts);
         computeNewTxCurrents(connection, updateData, "attr_id", "osee_attribute", attrs);
         computeNewTxCurrents(connection, updateData, "rel_link_id", "osee_relation_link", rels);

         ConnectionHandler.runBatchUpdate(connection, UPDATE_TX_CURRENT, updateData);
         monitor.worked(calculateWork(0.20));
         success = true;
      } finally {
         clearJoin(connection, txsToDeleteQuery);
      }
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

   private void computeNewTxCurrents(OseeConnection connection, Collection<Object[]> updateData, String itemId, String tableName, Map<Integer, IdJoinQuery> affected) throws OseeCoreException {
      String query = String.format(FIND_NEW_TX_CURRENTS, tableName, itemId);

      for (Entry<Integer, IdJoinQuery> entry : affected.entrySet()) {
         Integer branchId = entry.getKey();
         IdJoinQuery joinQuery = entry.getValue();

         IOseeStatement statement = ConnectionHandler.getStatement(connection);
         try {
            statement.runPreparedQuery(query, joinQuery.getQueryId(), branchId);
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
         joinQuery.delete(connection);
      }
   }

   private void clearJoin(OseeConnection connection, TransactionJoinQuery txsToDeleteQuery) {
      try {
         if (connection != null && !connection.isClosed()) {
            txsToDeleteQuery.delete(connection);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex.toString(), ex);
      }
   }

   private Map<Integer, IdJoinQuery> findAffectedItems(OseeConnection connection, String itemId, String itemTable, int txQueryId) throws OseeCoreException {
      Map<Integer, IdJoinQuery> items = new HashMap<Integer, IdJoinQuery>();

      String query = String.format(SELECT_AFFECTED_ITEMS, itemId, itemTable);
      IOseeStatement statement = ConnectionHandler.getStatement(connection);
      try {
         statement.runPreparedQuery(query, txQueryId);
         while (statement.next()) {
            Integer branchId = statement.getInt("branch_id");
            IdJoinQuery joinId = items.get(branchId);
            if (joinId == null) {
               joinId = JoinUtility.createIdJoinQuery();
               items.put(branchId, joinId);
            }
            Integer id = statement.getInt("item_id");
            joinId.add(id);
         }
      } finally {
         statement.close();
      }

      for (IdJoinQuery join : items.values()) {
         join.store();
      }
      return items;
   }

   private Map<TransactionRecord, TransactionRecord> findPriorTransactions(IProgressMonitor monitor, TransactionJoinQuery txsToDeleteQuery, double workPercentage) throws OseeCoreException {
      Map<TransactionRecord, TransactionRecord> deleteToPreviousTx =
         new HashMap<TransactionRecord, TransactionRecord>();
      double workStep = workPercentage / txIdsToDelete.length;
      for (int index = 0; index < txIdsToDelete.length; index++) {
         monitor.subTask(String.format("Fetching Previous Tx Info: [%d of %d]", index + 1, txIdsToDelete.length));
         int fromTx = txIdsToDelete[index];
         TransactionRecord fromTransaction = transactionCache.getOrLoad(fromTx);
         TransactionRecord previousTransaction;
         try {
            previousTransaction = transactionCache.getPriorTransaction(fromTransaction);
         } catch (TransactionDoesNotExist ex) {
            throw new OseeArgumentException(
               "You are trying to delete Transaction [%d] which is a baseline transaction.  If your intent is to delete the Branch use the delete Branch Operation.  \n\nNO TRANSACTIONS WERE DELETED.",
               fromTx);
         }
         deleteToPreviousTx.put(fromTransaction, previousTransaction);

         // Store transaction id(s) to delete - no need for gammas
         txsToDeleteQuery.add(-1L, fromTx);
         monitor.worked(calculateWork(workStep));
      }
      return deleteToPreviousTx;
   }

   private void setChildBranchBaselineTxs(OseeConnection connection, IProgressMonitor monitor, Map<TransactionRecord, TransactionRecord> deleteToPreviousTx, double workPercentage) throws OseeCoreException {
      List<Object[]> data = new ArrayList<Object[]>();
      monitor.subTask("Update Baseline Txs for Child Branches");
      for (Entry<TransactionRecord, TransactionRecord> entry : deleteToPreviousTx.entrySet()) {
         TransactionRecord previousTransaction = entry.getValue();
         if (previousTransaction != null) {
            int toDeleteTransaction = entry.getKey().getId();

            data.add(new Object[] {
               String.valueOf(toDeleteTransaction),
               String.valueOf(previousTransaction.getId()),
               "%" + toDeleteTransaction});
         }
      }
      if (!data.isEmpty()) {
         ConnectionHandler.runBatchUpdate(connection, UPDATE_TXS_DETAILS_COMMENT, data);
      }
      monitor.worked(calculateWork(workPercentage));
   }
}