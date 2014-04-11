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

import static org.eclipse.osee.framework.database.core.IOseeStatement.MAX_FETCH;
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
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.operation.NullOperationLogger;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.internal.ServiceUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

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
   private final TransactionCache transactionCache;
   public static interface PurgeTransactionListener {
      void onPurgeTransactionSuccess(Collection<TransactionRecord> transactions);
   }

   private final Set<PurgeTransactionListener> listeners = new CopyOnWriteArraySet<PurgeTransactionListener>();
   private Collection<TransactionRecord> changedTransactions = new ArrayList<TransactionRecord>();

   public PurgeTransactionOperation(IOseeDatabaseService databaseService, TransactionCache transactionCache, OperationLogger logger, List<Integer> txIdsToDelete) {
      super(databaseService, "Purge transactions " + txIdsToDelete, ServiceUtil.PLUGIN_ID, logger);
      this.success = false;
      this.transactionCache = transactionCache;
      this.txIdsToDelete = txIdsToDelete;
   }

   public PurgeTransactionOperation(IOseeDatabaseService databaseService, TransactionCache transactionCache, List<Integer> txIdsToDelete) {
      this(databaseService, transactionCache, NullOperationLogger.getSingleton(), txIdsToDelete);
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
         TransactionRecord toDeleteTransaction = transactionCache.getOrLoad(txIdToDelete);
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
         ConnectionHandler.runBatchUpdate(connection, DELETE_TX_DETAILS, txsToDelete);
         ConnectionHandler.runBatchUpdate(connection, DELETE_TXS, txsToDelete);
         monitor.worked(1);

         monitor.subTask("Updating Previous Tx to Current");
         List<Object[]> updateData = new ArrayList<Object[]>();
         computeNewTxCurrents(connection, updateData, "art_id", "osee_artifact", arts);
         computeNewTxCurrents(connection, updateData, "attr_id", "osee_attribute", attrs);
         computeNewTxCurrents(connection, updateData, "rel_link_id", "osee_relation_link", rels);
         ConnectionHandler.runBatchUpdate(connection, UPDATE_TX_CURRENT, updateData);
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

   private void computeNewTxCurrents(OseeConnection connection, Collection<Object[]> updateData, String itemId, String tableName, Map<Long, IdJoinQuery> affected) throws OseeCoreException {
      String query = String.format(FIND_NEW_TX_CURRENTS, tableName, itemId);

      for (Entry<Long, IdJoinQuery> entry : affected.entrySet()) {
         Long branchId = entry.getKey();
         IdJoinQuery joinQuery = entry.getValue();

         IOseeStatement statement = ConnectionHandler.getStatement(connection);
         try {
            statement.runPreparedQuery(MAX_FETCH, query, joinQuery.getQueryId(), branchId);
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

   private Map<Long, IdJoinQuery> findAffectedItems(OseeConnection connection, String itemId, String itemTable, List<Object[]> bindDataList) throws OseeCoreException {
      Map<Long, IdJoinQuery> items = new HashMap<Long, IdJoinQuery>();
      IOseeStatement statement = ConnectionHandler.getStatement(connection);

      try {
         for (Object[] bindData : bindDataList) {
            Long branchId = (Long) bindData[0];
            String query = String.format(SELECT_AFFECTED_ITEMS, itemId, itemTable);
            statement.runPreparedQuery(MAX_FETCH, query, bindData);
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

   private TransactionRecord findPriorTransactions(Integer txIdToDelete) throws OseeCoreException {
      TransactionRecord fromTransaction = transactionCache.getOrLoad(txIdToDelete);
      TransactionRecord previousTransaction;
      try {
         previousTransaction = transactionCache.getPriorTransaction(fromTransaction);
      } catch (TransactionDoesNotExist ex) {
         throw new OseeArgumentException(
            "You are trying to delete Transaction [%d] which is a baseline transaction.  If your intent is to delete the Branch use the delete Branch Operation.  \n\nNO TRANSACTIONS WERE DELETED.",
            txIdToDelete);
      }
      return previousTransaction;
   }

   private void setChildBranchBaselineTxs(OseeConnection connection, TransactionRecord toDeleteTransaction, TransactionRecord previousTransaction) throws OseeCoreException {
      List<Object[]> data = new ArrayList<Object[]>();
      if (previousTransaction != null) {
         int toDeleteTransactionId = toDeleteTransaction.getId();

         data.add(new Object[] {
            String.valueOf(toDeleteTransactionId),
            String.valueOf(previousTransaction.getId()),
            "%" + toDeleteTransactionId});
      }
      if (!data.isEmpty()) {
         ConnectionHandler.runBatchUpdate(connection, UPDATE_TXS_DETAILS_COMMENT, data);
      }
   }
}