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

package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.TransactionJoinQuery;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.PurgeTransactionEventUtil;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Ryan D. Brooks
 */
public class PurgeTransactionOperation extends AbstractDbTxOperation {

   private static final String UPDATE_TXS_DETAILS_COMMENT =
      "UPDATE osee_tx_details SET osee_comment = replace(osee_comment, ?, ?) WHERE osee_comment like ?";

   private static final String SELECT_GAMMAS_FROM_TRANSACTION =
      "SELECT DISTINCT txs1.gamma_id FROM osee_txs txs1, osee_join_transaction txj1 WHERE txs1.transaction_id = txj1.transaction_id AND txj1.query_id = ? AND NOT EXISTS (SELECT 'x' FROM osee_txs txs2 WHERE txs1.gamma_id = txs2.gamma_id AND txs1.transaction_id <> txs2.transaction_id)";

   private final static String DELETE_TXS = "delete from osee_txs where transaction_id = ?";
   private static final String DELETE_TX_DETAILS = "delete from osee_tx_details where transaction_id = ?";

   private final static String DELETE_ARTIFACT_VERSIONS = "delete from osee_artifact items where items.gamma_id = ?";
   private final static String DELETE_ATTRIBUTES = "delete from osee_attribute items where items.gamma_id = ?";
   private final static String DELETE_RELATIONS = "delete from osee_relation_link items where items.gamma_id = ?";

   private final static String TRANSACATION_GAMMA_IN_USE =
      "SELECT txs2.transaction_id FROM osee_txs txs1, osee_txs txs2, osee_join_transaction jn where txs1.transaction_id = jn.transaction_id AND txs1.gamma_id = txs2.gamma_id and txs2.transaction_id != txs1.transaction_id AND jn.query_id = ?";

   private final static String SELECT_AFFECTED_ITEMS =
      "SELECT %s as item_id, txs.branch_id from osee_join_transaction ojt, osee_txs txs, %s item where ojt.query_id = ? AND ojt.transaction_id = txs.transaction_id AND txs.gamma_id = item.gamma_id";

   private final static String FIND_NEW_TX_CURRENTS =
      "SELECT oj.id as item_id, txs.mod_type, txs.gamma_id, txs.transaction_id from osee_join_id oj, %s item, osee_txs txs where oj.query_id = ? and oj.id = item.%s and item.gamma_id = txs.gamma_id and txs.branch_id = ? order by txs.transaction_id desc, oj.id desc";

   private static final String UPDATE_TX_CURRENT =
      "update osee_txs set tx_current = ? where transaction_id = ? and gamma_id = ?";

   private final int[] txIdsToDelete;
   private final boolean force;

   public PurgeTransactionOperation(IOseeDatabaseService databaseService, boolean force, int... txIdsToDelete) {
      super(databaseService, String.format("Delete transactions: %s", Arrays.toString(txIdsToDelete)),
         Activator.PLUGIN_ID);
      this.txIdsToDelete = txIdsToDelete;
      this.force = force;
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
      Conditions.checkNotNull(txIdsToDelete, "transaction ids to delete");
      Conditions.checkExpressionFailOnTrue(txIdsToDelete.length <= 0, "transaction ids to delete cannot be empty");

      Arrays.sort(txIdsToDelete);

      TransactionJoinQuery txsToDeleteQuery = JoinUtility.createTransactionJoinQuery();

      Map<TransactionRecord, TransactionRecord> deleteToPreviousTx =
         findPriorTransactions(monitor, txsToDeleteQuery, 0.20);

      TransactionEvent transactionEvent =
         PurgeTransactionEventUtil.createPurgeTransactionEvent(deleteToPreviousTx.keySet());

      txsToDeleteQuery.store(connection);

      int txQueryId = txsToDeleteQuery.getQueryId();

      try {
         checkForModifiedBaselines(connection, force, txQueryId);

         Map<Integer, IdJoinQuery> arts = findAffectedItems(connection, "art_id", "osee_artifact", txQueryId);
         Map<Integer, IdJoinQuery> attrs = findAffectedItems(connection, "attr_id", "osee_attribute", txQueryId);
         Map<Integer, IdJoinQuery> rels = findAffectedItems(connection, "rel_link_id", "osee_relation_link", txQueryId);
         monitor.worked(calculateWork(0.20));

         setChildBranchBaselineTxs(connection, monitor, deleteToPreviousTx, 0.20);
         deleteItemEntriesForTransactions(connection, monitor, txQueryId, 0.20);

         monitor.subTask("Remove Tx Rows");
         List<Object[]> txsToDelete = new ArrayList<Object[]>();
         for (int txId : txIdsToDelete) {
            txsToDelete.add(new Object[] {txId});
         }
         ConnectionHandler.runBatchUpdate(connection, DELETE_TX_DETAILS, txsToDelete);
         ConnectionHandler.runBatchUpdate(connection, DELETE_TXS, txsToDelete);

         monitor.subTask("Updating Previous Tx to Current");
         List<Object[]> txsUpdate = new ArrayList<Object[]>();
         computeNewTxCurrents(connection, txsUpdate, "art_id", "osee_artifact", arts);
         computeNewTxCurrents(connection, txsUpdate, "attr_id", "osee_attribute", attrs);
         computeNewTxCurrents(connection, txsUpdate, "rel_link_id", "osee_relation_link", rels);

         ConnectionHandler.runBatchUpdate(connection, UPDATE_TX_CURRENT, txsUpdate);
         monitor.worked(calculateWork(0.20));

         OseeEventManager.kickTransactionEvent(this, transactionEvent);
      } finally {
         clearJoin(connection, txsToDeleteQuery);
      }
   }

   private void computeNewTxCurrents(OseeConnection connection, Collection<Object[]> txsUpdate, String itemId, String tableName, Map<Integer, IdJoinQuery> affected) throws OseeCoreException {
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
                  txsUpdate.add(new Object[] {
                     txCurrent.getValue(),
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
         TransactionRecord fromTransaction = TransactionManager.getTransactionId(fromTx);
         TransactionRecord previousTransaction;
         try {
            previousTransaction = TransactionManager.getPriorTransaction(fromTransaction);
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

   private void deleteItemEntriesForTransactions(OseeConnection connection, IProgressMonitor monitor, int txsToDeleteQueryId, double workPercentage) throws OseeCoreException {
      monitor.subTask("Deleting Tx Items");
      List<Object[]> data = new ArrayList<Object[]>();
      IOseeStatement chStmt = ConnectionHandler.getStatement(connection);
      try {
         chStmt.runPreparedQuery(SELECT_GAMMAS_FROM_TRANSACTION, txsToDeleteQueryId);
         while (chStmt.next()) {
            data.add(new Object[] {chStmt.getLong("gamma_id")});
         }
      } finally {
         chStmt.close();
      }
      ConnectionHandler.runBatchUpdate(connection, DELETE_ARTIFACT_VERSIONS, data);
      ConnectionHandler.runBatchUpdate(connection, DELETE_ATTRIBUTES, data);
      ConnectionHandler.runBatchUpdate(connection, DELETE_RELATIONS, data);
      monitor.worked(calculateWork(workPercentage));
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

   private void checkForModifiedBaselines(OseeConnection connection, boolean force, int queryId) throws OseeCoreException {
      int transaction_id =
         ConnectionHandler.runPreparedQueryFetchInt(connection, 0, TRANSACATION_GAMMA_IN_USE, queryId);
      if (transaction_id > 0 && !force) {
         throw new OseeCoreException(
            "The Transaction %d holds a Gamma that is in use in other transactions.  In order to delete this Transaction you will need to select the force check box.\n\nNO TRANSACTIONS WERE DELETED.",
            transaction_id);
      }
   }
}