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
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.JoinUtility.TransactionJoinQuery;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Ryan D. Brooks
 */
public class PurgeTransactionOperation extends AbstractDbTxOperation {

   private static final String UPDATE_TXS_DETAILS_COMMENT =
         "UPDATE osee_tx_details SET osee_comment = replace(osee_comment, ?, ?) WHERE osee_comment like ?";

   private static final String SELECT_GAMMAS_FROM_TRANSACTION =
         "SELECT txs1.gamma_id, txs1.transaction_id FROM osee_txs txs1, osee_join_transaction txj1 WHERE " + "txs1.transaction_id = txj1.transaction_id AND txj1.query_id = ? AND " + "NOT EXISTS (SELECT 'x' FROM osee_txs txs2 WHERE txs1.gamma_id = txs2.gamma_id AND txs1.transaction_id <> txs2.transaction_id)";

   private static final String DELETE_TRANSACTION_FROM_TRANSACTION_DETAILS =
         "DELETE FROM osee_tx_details WHERE transaction_id IN (SELECT txj1.transaction_id FROM osee_join_transaction txj1 WHERE txj1.query_id = ?)";

   private static final String DELETE_POSTFIX =
         "outerTb where outerTb.gamma_id = (SELECT txj1.gamma_id from osee_join_transaction txj1 WHERE outerTb.gamma_id = txj1.gamma_id AND txj1.query_id = ?)";

   private final static String DELETE_ARTIFACT_VERSIONS = "DELETE FROM osee_arts " + DELETE_POSTFIX;
   private final static String DELETE_ATTRIBUTES = "DELETE FROM osee_attribute " + DELETE_POSTFIX;
   private final static String DELETE_RELATIONS = "DELETE FROM osee_relation_link " + DELETE_POSTFIX;

   private final static String DELETE_TXS = "delete from osee_txs where transaction_id = ?;";

   private final static String TRANSACATION_GAMMA_IN_USE =
         "SELECT txs1.transaction_id FROM osee_txs txs1, osee_txs txs2, osee_join_transaction jn where txs1.transaction_id = jn.transaction_id AND txs1.gamma_id = txs2.gamma_id and txs2.transaction_id != txs1.transaction_id AND jn.query_id = ?";

   private final static String LOAD_ARTIFACTS =
         "INSERT INTO osee_join_artifact (query_id, art_id, branch_id, insert_time) (SELECT ?, art_id, txs.branch_id, CURRENT_TIMESTAMP from osee_txs txs, osee_attribute att, osee_join_transaction tran where tran.query_id = ? AND tran.transaction_id = txs.transaction_id AND txs.gamma_id = att.gamma_id) UNION (SELECT ?, art_id, txs.branch_id, CURRENT_TIMESTAMP FROM osee_txs txs, osee_arts art, osee_join_transaction tran WHERE tran.query_id = ? AND tran.transaction_id = txs.transaction_id AND txs.gamma_id = art.gamma_id) UNION (SELECT ?, a_art_id as art_id, txs.branch_id, CURRENT_TIMESTAMP FROM osee_txs txs, osee_relation_link rel, osee_join_transaction tran WHERE tran.query_id = ? AND tran.transaction_id = txs.transaction_id AND txs.gamma_id = rel.gamma_id) UNION (SELECT ?, b_art_id as art_id, txs.branch_id, CURRENT_TIMESTAMP FROM osee_txs txs, osee_relation_link rel, osee_join_transaction tran WHERE tran.query_id = ? AND tran.transaction_id = txs.transaction_id AND txs.gamma_id = rel.gamma_id)";

   private static final String UPDATE_TXS =
         "UPDATE osee_txs SET tx_current = (CASE WHEN mod_type = 3 THEN 2 ELSE 1 END) WHERE (transaction_id, gamma_id) IN ((SELECT maxt, txs2.gamma_id FROM osee_txs txs2, osee_attribute att2, (SELECT MAX(txs.transaction_id) AS maxt, att.attr_id AS atid FROM osee_txs txs, osee_attribute att, osee_join_artifact jar WHERE txs.branch_id = jar.branch_id AND txs.gamma_id = att.gamma_id and att.art_id = jar.art_id AND jar.query_id = ? GROUP BY att.attr_id, txs.branch_id) new_stuff WHERE txs2.gamma_id = att2.gamma_id AND att2.attr_id = atid AND maxt = txs2.transaction_id) UNION (SELECT maxt, txs2.gamma_id FROM osee_txs txs2, osee_arts art2, (SELECT MAX(txs.transaction_id) AS maxt, ver.art_id AS atid FROM osee_txs txs, osee_arts ver, osee_join_artifact jar WHERE txs.branch_id = jar.branch_id AND txs.gamma_id = ver.gamma_id and ver.art_id = jar.art_id AND jar.query_id = ? GROUP BY ver.art_id, txs.branch_id) new_stuff WHERE txs2.gamma_id = art2.gamma_id AND art2.art_id = atid AND maxt = txs2.transaction_id) UNION (SELECT maxt, txs2.gamma_id FROM osee_txs txs2, osee_relation_link rel2, (SELECT MAX(txs.transaction_id) AS maxt, rel.rel_link_id AS linkid FROM osee_txs txs, osee_relation_link rel, osee_join_artifact jar WHERE txs.branch_id = jar.branch_id AND txs.gamma_id = rel.gamma_id and (rel.a_art_id = jar.art_id or rel.b_art_id = jar.art_id) AND jar.query_id = ? GROUP BY rel.rel_link_id, txs.branch_id) new_stuff WHERE txs2.gamma_id = rel2.gamma_id AND rel2.rel_link_id = linkid AND maxt = txs2.transaction_id))";

   private final int[] txIdsToDelete;
   private final boolean force;
   private int artifactJoinId;

   public PurgeTransactionOperation(IOseeDatabaseServiceProvider serviceProvider, boolean force, int... txIdsToDelete) {
      super(serviceProvider, String.format("Delete transactions: %s", Arrays.toString(txIdsToDelete)),
            Activator.PLUGIN_ID);
      this.txIdsToDelete = txIdsToDelete;
      this.force = force;
   }

   public PurgeTransactionOperation(IOseeDatabaseServiceProvider serviceProvider, int... txIdsToDelete) {
      this(serviceProvider, true, txIdsToDelete);
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
      TransactionJoinQuery txsToDeleteQuery = JoinUtility.createTransactionJoinQuery();
      try {
         Arrays.sort(txIdsToDelete);

         HashCollection<Branch, TxDeleteInfo> fromToTxData =
               getTransactionPairs(monitor, txIdsToDelete, txsToDeleteQuery, 0.20);
         txsToDeleteQuery.store(connection);
         checkForModifiedBaselines(connection, force, txsToDeleteQuery.getQueryId());
         getAffectedArtifacts(connection, txsToDeleteQuery.getQueryId());

         setChildBranchBaselineTxs(connection, monitor, fromToTxData, 0.20);
         deleteItemEntriesForTransactions(connection, monitor, txsToDeleteQuery.getQueryId(), 0.20);
         deleteTransactionsFromTxDetails(connection, monitor, txsToDeleteQuery.getQueryId(), 0.20);

         deleteTxCurrent(connection);
         updateTxCurrent(connection, monitor, 0.20);
      } catch (OseeCoreException ex) {
         if (connection != null && connection.isClosed() != true) {
            txsToDeleteQuery.delete(connection);
            ArtifactLoader.clearQuery(connection, artifactJoinId);
         }
         OseeExceptions.wrapAndThrow(ex);
      } finally {
         if (connection != null && connection.isClosed() != true) {
            txsToDeleteQuery.delete(connection);
            ArtifactLoader.clearQuery(connection, artifactJoinId);
         }
      }
      OseeEventManager.kickTransactionsDeletedEvent(this, txIdsToDelete);
   }

   private void getAffectedArtifacts(OseeConnection connection, int transactionQueryId) throws OseeDataStoreException {
      artifactJoinId = ArtifactLoader.getNewQueryId();
      ConnectionHandler.runPreparedUpdate(connection, LOAD_ARTIFACTS, artifactJoinId, transactionQueryId,
            artifactJoinId, transactionQueryId, artifactJoinId, transactionQueryId, artifactJoinId, transactionQueryId);
   }

   private HashCollection<Branch, TxDeleteInfo> getTransactionPairs(IProgressMonitor monitor, int[] txsToDelete, TransactionJoinQuery txsToDeleteQuery, double workPercentage) throws OseeCoreException {
      HashCollection<Branch, TxDeleteInfo> fromToTxData = new HashCollection<Branch, TxDeleteInfo>();
      if (txsToDelete.length > 0) {
         double workStep = workPercentage / txsToDelete.length;
         for (int index = 0; index < txsToDelete.length; index++) {
            monitor.subTask(String.format("Fetching Previous Tx Info: [%d of %d]", index + 1, txsToDelete.length));
            int fromTx = txsToDelete[index];
            TransactionRecord fromTransaction = TransactionManager.getTransactionId(fromTx);
            TransactionRecord previousTransaction;
            try {
               previousTransaction = TransactionManager.getPriorTransaction(fromTransaction);
            } catch (TransactionDoesNotExist ex) {
               throw new OseeCoreException(
                     "You are trying to delete Transaction: " + fromTx + " which is a baseline transaction.  If your intent is to delete the Branch use the delete Branch Operation.  \n\nNO TRANSACTIONS WERE DELETED.");

            }

            fromToTxData.put(fromTransaction.getBranch(), new TxDeleteInfo(fromTransaction, previousTransaction));

            // Store transaction id(s) to delete - no need for gammas
            txsToDeleteQuery.add(-1L, fromTx);
            monitor.worked(calculateWork(workStep));
         }
      } else {
         monitor.worked(calculateWork(workPercentage));
      }
      return fromToTxData;
   }

   private void deleteTransactionsFromTxDetails(OseeConnection connection, IProgressMonitor monitor, int queryId, double workPercentage) throws OseeDataStoreException {
      monitor.subTask("Deleting Tx");
      ConnectionHandler.runPreparedUpdate(connection, DELETE_TRANSACTION_FROM_TRANSACTION_DETAILS, queryId);
      monitor.worked(calculateWork(workPercentage));
   }

   private void deleteItemEntriesForTransactions(OseeConnection connection, IProgressMonitor monitor, int txsToDeleteQueryId, double workPercentage) throws OseeDataStoreException {
      monitor.subTask("Deleting Tx Items");
      TransactionJoinQuery txGammasToDelete = JoinUtility.createTransactionJoinQuery();
      try {
         populateJoinQueryFromSql(connection, txGammasToDelete, SELECT_GAMMAS_FROM_TRANSACTION, "transaction_id",
               txsToDeleteQueryId);
         txGammasToDelete.store();
         int deleteQueryId = txGammasToDelete.getQueryId();
         ConnectionHandler.runPreparedUpdate(connection, DELETE_ARTIFACT_VERSIONS, deleteQueryId);
         ConnectionHandler.runPreparedUpdate(connection, DELETE_ATTRIBUTES, deleteQueryId);
         ConnectionHandler.runPreparedUpdate(connection, DELETE_RELATIONS, deleteQueryId);

      } finally {
         if (txGammasToDelete != null && connection != null && connection.isClosed() != true) {
            txGammasToDelete.delete(connection);
         }
      }
      monitor.worked(calculateWork(workPercentage));
   }

   private void populateJoinQueryFromSql(OseeConnection connection, TransactionJoinQuery joinQuery, String sql, String txFieldName, Object... data) throws OseeDataStoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement(connection);
      try {
         chStmt.runPreparedQuery(sql, data);
         while (chStmt.next()) {
            joinQuery.add(chStmt.getLong("gamma_id"), chStmt.getInt(txFieldName));
         }
      } finally {
         chStmt.close();
      }
   }

   private void updateTxCurrent(OseeConnection conn, IProgressMonitor monitor, double workPercentage) throws OseeDataStoreException {
      monitor.setTaskName("Updating Previous Tx to Current");
      ConnectionHandler.runPreparedUpdate(conn, UPDATE_TXS, artifactJoinId, artifactJoinId, artifactJoinId);
      monitor.worked(calculateWork(workPercentage));
   }

   private void deleteTxCurrent(OseeConnection connection) throws OseeCoreException {
      for (int txId : txIdsToDelete) {
         ConnectionHandler.runPreparedUpdate(connection, DELETE_TXS, Integer.valueOf(txId));
      }
   }

   private void setChildBranchBaselineTxs(OseeConnection connection, IProgressMonitor monitor, HashCollection<Branch, TxDeleteInfo> transactions, double workPercentage) throws OseeDataStoreException {
      List<Object[]> data = new ArrayList<Object[]>();
      monitor.setTaskName("Update Baseline Txs for Child Branches");
      for (TxDeleteInfo entry : transactions.getValues()) {
         TransactionRecord previousTransaction = entry.getPreviousTx();
         if (previousTransaction != null) {
            int toDeleteTransaction = entry.getTxToDelete().getId();

            data.add(new Object[] {String.valueOf(toDeleteTransaction), String.valueOf(previousTransaction.getId()),
                  "%" + toDeleteTransaction});
         }
      }
      if (data.size() > 0) {
         ConnectionHandler.runBatchUpdate(connection, UPDATE_TXS_DETAILS_COMMENT, data);
      }
      monitor.worked(calculateWork(workPercentage));
   }

   private void checkForModifiedBaselines(OseeConnection connection, boolean force, int queryId) throws OseeCoreException {
      int transaction_id =
            ConnectionHandler.runPreparedQueryFetchInt(connection, 0, TRANSACATION_GAMMA_IN_USE, queryId);
      if (transaction_id > 0 && !force) {
         throw new OseeCoreException(
               "The Transaction " + transaction_id + " holds a Gamma that is in use on other transactions.  In order to delete this Transaction you will need to select the force check box.\n\nNO TRANSACTIONS WERE DELETED.");
      }
   }

   private final static class TxDeleteInfo {
      private final TransactionRecord txToDelete;
      private final TransactionRecord previousTxFromTxToDelete;

      public TxDeleteInfo(TransactionRecord txToDelete, TransactionRecord previousTxFromTxToDelete) {
         super();
         this.txToDelete = txToDelete;
         this.previousTxFromTxToDelete = previousTxFromTxToDelete;
      }

      public TransactionRecord getTxToDelete() {
         return txToDelete;
      }

      public TransactionRecord getPreviousTx() {
         return previousTxFromTxToDelete;
      }
   }

}