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
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.DbTransaction;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.JoinUtility.TransactionJoinQuery;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Ryan D. Brooks
 */
public class PurgeTransactionJob extends Job {

   private static final String UPDATE_TXS_DETAILS_COMMENT =
         "update osee_tx_details SET osee_comment = replace(osee_comment, ?, ?) WHERE osee_comment like ?";

   private static final String SELECT_GAMMAS_FROM_TRANSACTION =
         "SELECT txs1.gamma_id, txs1.transaction_id FROM osee_txs txs1, osee_join_transaction txj1 WHERE " + "txs1.transaction_id = txj1.transaction_id AND txj1.query_id = ? AND " + "NOT EXISTS (SELECT 'x' FROM osee_txs txs2 WHERE txs1.gamma_id = txs2.gamma_id AND txs1.transaction_id <> txs2.transaction_id)";

   private static final String DELETE_TRANSACTION_FROM_TRANSACTION_DETAILS =
         "DELETE FROM osee_tx_details WHERE transaction_id IN (SELECT txj1.transaction_id FROM osee_join_transaction txj1 WHERE txj1.query_id = ?)";

   private static final String SELECT_ATTRIBUTES_TO_UPDATE =
         "SELECT maxt, txs2.gamma_id FROM osee_attribute att2,  osee_txs txs2, (SELECT MAX(txs1.transaction_id) AS  maxt, att1.attr_id AS atid, txd1.branch_id FROM osee_attribute att1, osee_txs txs1, osee_tx_details txd1 WHERE att1.gamma_id = txs1.gamma_id and txs1.transaction_id >= ? AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = ? GROUP BY att1.attr_id, txd1.branch_id) new_stuff WHERE atid = att2.attr_id AND att2.gamma_id = txs2.gamma_id AND txs2.transaction_id = maxt and txs2.transaction_id >= ?";
   private static final String SELECT_ARTIFACTS_TO_UPDATE =
         "SELECT maxt, txs1.gamma_id FROM osee_artifact_version arv2,  osee_txs txs1, (SELECT MAX(txs2.transaction_id) AS maxt, arv1.art_id AS art, txd1.branch_id FROM osee_artifact_version arv1, osee_txs txs2, osee_tx_details txd1 WHERE arv1.gamma_id = txs2.gamma_id and txs2.transaction_id >= ? AND txs2.transaction_id = txd1.transaction_id AND txd1.branch_id = ? GROUP BY arv1.art_id, txd1.branch_id) new_stuff WHERE art = arv2.art_id AND arv2.gamma_id = txs1.gamma_id AND txs1.transaction_id = maxt and txs1.transaction_id >= ?";
   private static final String SELECT_RELATIONS_TO_UPDATE =
         "SELECT maxt, txs1.gamma_id FROM osee_relation_link rel2, osee_txs txs1, (SELECT MAX(txs2.transaction_id) AS maxt, rel1.rel_link_id AS rel_id, txd1.branch_id FROM osee_relation_link rel1, osee_txs txs2, osee_tx_details txd1 WHERE rel1.gamma_id = txs2.gamma_id and txs2.transaction_id >= ?  AND txs2.transaction_id = txd1.transaction_id AND txd1.branch_id = ? GROUP BY rel1.rel_link_id, txd1.branch_id) new_stuff WHERE rel_id = rel2.rel_link_id AND rel2.gamma_id = txs1.gamma_id AND txs1.transaction_id = maxt and txs1.transaction_id >= ?";

   private static final String UPDATE_TX_CURRENT =
         "UPDATE osee_txs txs1 SET tx_current = 1 where txs1.mod_type <> 3 and txs1.gamma_id IN (SELECT txj1.gamma_id from osee_join_transaction txj1 WHERE txj1.gamma_id = txs1.gamma_id AND txj1.transaction_id = txs1.transaction_id AND txj1.query_id = ?)";

   private static final String UPDATE_TX_CURRENT_DELETED_ITEMS =
         "UPDATE osee_txs txs1 SET tx_current = 2 where txs1.mod_type = 3 and txs1.gamma_id IN (SELECT txj1.gamma_id from osee_join_transaction txj1 WHERE txj1.gamma_id = txs1.gamma_id AND txj1.transaction_id = txs1.transaction_id AND txj1.query_id = ?)";

   private static final String DELETE_POSTFIX =
         "outerTb where outerTb.gamma_id = (SELECT txj1.gamma_id from osee_join_transaction txj1 WHERE outerTb.gamma_id = txj1.gamma_id AND txj1.query_id = ?)";

   private final static String DELETE_ARTIFACT_VERSIONS = "DELETE FROM osee_artifact_version " + DELETE_POSTFIX;
   private final static String DELETE_ATTRIBUTES = "DELETE FROM osee_attribute " + DELETE_POSTFIX;
   private final static String DELETE_RELATIONS = "DELETE FROM osee_relation_link " + DELETE_POSTFIX;

   private final static String TRANSACATION_GAMMA_IN_USE =
         "Select txs1.transaction_id from osee_txs txs1, osee_txs txs2, osee_join_transaction jn where txs1.transaction_id = jn.transaction_id AND txs1.gamma_id = txs2.gamma_id and txs2.transaction_id != txs1.transaction_id AND jn.query_id = ?";
   private final static String GET_ARTIFACTS = "Select * from osee_join_artifact where query_id = ?";

   private final static String LOAD_ARTIFACTS =
         "INSERT INTO osee_join_artifact (query_id, art_id, branch_id, insert_time) (Select ?, art_id, branch_id,  CURRENT_TIMESTAMP from osee_txs txs, osee_attribute att, osee_join_transaction tran, osee_tx_details det where tran.query_id = ? AND tran.transaction_id = txs.transaction_id AND txs.gamma_id = att.gamma_id and det.transaction_id = txs.transaction_id) UNION (Select ?, art_id, branch_id,  CURRENT_TIMESTAMP from osee_txs txs, osee_artifact_version art, osee_join_transaction tran, osee_tx_details det where tran.query_id = ? AND tran.transaction_id = txs.transaction_id AND txs.gamma_id = art.gamma_id and det.transaction_id = txs.transaction_id) UNION (Select ?, a_art_id as art_id, branch_id,  CURRENT_TIMESTAMP from osee_txs txs, osee_relation_link rel, osee_join_transaction tran, osee_tx_details det where tran.query_id = ? AND tran.transaction_id = txs.transaction_id AND txs.gamma_id = rel.gamma_id and det.transaction_id = txs.transaction_id) UNION (Select ?, b_art_id as art_id, branch_id,  CURRENT_TIMESTAMP from osee_txs txs, osee_relation_link rel, osee_join_transaction tran, osee_tx_details det where tran.query_id = ? AND tran.transaction_id = txs.transaction_id AND txs.gamma_id = rel.gamma_id and det.transaction_id = txs.transaction_id)";
   private static final String UPDATE_TXS =
         "UPDATE osee_txs  set tx_current = (CASE WHEN mod_type = 3 THEN 2 ELSE 1 END) WHERE (transaction_id, gamma_id) IN ((SELECT maxt, txs2.gamma_id FROM osee_txs txs2, osee_attribute att2, (SELECT MAX(txs.transaction_id) AS maxt, att.attr_id AS atid FROM osee_txs txs, osee_attribute att, osee_tx_details det, osee_join_artifact jar WHERE det.branch_id = jar.branch_id AND det.transaction_id = txs.transaction_id AND txs.gamma_id = att.gamma_id and att.art_id = jar.art_id AND jar.query_id = ? GROUP BY att.attr_id, det.branch_id) new_stuff WHERE txs2.gamma_id = att2.gamma_id AND att2.attr_id = atid AND maxt = txs2.transaction_id) UNION (SELECT maxt, txs2.gamma_id FROM osee_txs txs2, osee_artifact_version ver2, (SELECT MAX(txs.transaction_id) AS maxt, ver.art_id AS atid FROM osee_txs txs, osee_artifact_version ver, osee_tx_details det, osee_join_artifact jar WHERE det.branch_id = jar.branch_id AND det.transaction_id = txs.transaction_id AND txs.gamma_id = ver.gamma_id and ver.art_id = jar.art_id AND jar.query_id = ? GROUP BY ver.art_id, det.branch_id) new_stuff WHERE txs2.gamma_id = ver2.gamma_id AND ver2.art_id = atid AND maxt = txs2.transaction_id)UNION(SELECT maxt, txs2.gamma_id FROM osee_txs txs2, osee_relation_link rel2, (SELECT MAX(txs.transaction_id) AS maxt, rel.rel_link_id AS linkid FROM osee_txs txs, osee_relation_link rel, osee_tx_details det, osee_join_artifact jar WHERE det.branch_id = jar.branch_id AND det.transaction_id = txs.transaction_id AND txs.gamma_id = rel.gamma_id and (rel.a_art_id = jar.art_id or rel.b_art_id = jar.art_id) AND jar.query_id = ? GROUP BY rel.rel_link_id, det.branch_id) new_stuff WHERE txs2.gamma_id = rel2.gamma_id AND rel2.rel_link_id = linkid AND maxt = txs2.transaction_id))";

   private final int[] txIdsToDelete;
   private final boolean force;
   private int artifactJoinId;

   /**
    * @param name
    * @param transactionIdNumber
    */
   public PurgeTransactionJob(boolean force, int... txIdsToDelete) {
      super(String.format("Delete transactions: %s", Arrays.toString(txIdsToDelete)));
      this.txIdsToDelete = txIdsToDelete;
      this.force = force;
   }

   /**
    * @param name
    * @param transactionIdNumber
    */
   public PurgeTransactionJob(int... txIdsToDelete) {
      this(true, txIdsToDelete);
   }

   @Override
   protected IStatus run(final IProgressMonitor monitor) {
      IStatus returnStatus = Status.CANCEL_STATUS;
      try {
         DeleteTransactionTx deleteTransactionTx = new DeleteTransactionTx(monitor);
         deleteTransactionTx.execute();
         returnStatus = Status.OK_STATUS;

         // Kick Local and Remote Events
         OseeEventManager.kickTransactionsDeletedEvent(this, txIdsToDelete);
      } catch (Exception ex) {
         returnStatus = new Status(Status.ERROR, Activator.PLUGIN_ID, -1, ex.getLocalizedMessage(), ex);
      } finally {
         monitor.done();
      }
      return returnStatus;
   }

   private final class DeleteTransactionTx extends DbTransaction {
      private final IProgressMonitor monitor;

      public DeleteTransactionTx(IProgressMonitor monitor) throws OseeCoreException {
         this.monitor = monitor;
      }

      @Override
      protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
         TransactionJoinQuery txsToDeleteQuery = JoinUtility.createTransactionJoinQuery();
         try {
            monitor.beginTask(getName(), getTotalWork());
            Arrays.sort(txIdsToDelete);

            HashCollection<Branch, TxDeleteInfo> fromToTxData =
                  getTransactionPairs(monitor, txIdsToDelete, txsToDeleteQuery);
            txsToDeleteQuery.store(connection);
            checkForModifiedBaselines(connection, force, txsToDeleteQuery.getQueryId());
            getAffectedArtifacts(connection, monitor, txsToDeleteQuery.getQueryId());

            setChildBranchBaselineTxs(connection, monitor, fromToTxData);
            deleteItemEntriesForTransactions(connection, monitor, txsToDeleteQuery.getQueryId());
            deleteTransactionsFromTxDetails(connection, monitor, txsToDeleteQuery.getQueryId());

            updateTxCurrent(connection, monitor);
         } catch (OseeCoreException ex) {
            if (connection != null && connection.isClosed() != true) {
               txsToDeleteQuery.delete(connection);
               ArtifactLoader.clearQuery(connection, artifactJoinId);
            }
            throw new OseeWrappedException(ex);
         }
         if (connection != null && connection.isClosed() != true) {
            txsToDeleteQuery.delete(connection);
            ArtifactLoader.clearQuery(connection, artifactJoinId);
         }
      }

      /**
       * @throws OseeDataStoreException
       */
      private void getAffectedArtifacts(OseeConnection connection, IProgressMonitor monitor, int transactionQueryId) throws OseeDataStoreException {
         artifactJoinId = ArtifactLoader.getNewQueryId();
         ConnectionHandler.runPreparedUpdate(connection, LOAD_ARTIFACTS, artifactJoinId, transactionQueryId,
               artifactJoinId, transactionQueryId, artifactJoinId, transactionQueryId, artifactJoinId,
               transactionQueryId);
      }

      private int getTotalWork() {
         return txIdsToDelete.length + 5;
      }

      private HashCollection<Branch, TxDeleteInfo> getTransactionPairs(IProgressMonitor monitor, int[] txsToDelete, TransactionJoinQuery txsToDeleteQuery) throws OseeCoreException {
         HashCollection<Branch, TxDeleteInfo> fromToTxData = new HashCollection<Branch, TxDeleteInfo>();
         for (int index = 0; index < txsToDelete.length; index++) {
            monitor.subTask(String.format("Fetching Previous Tx Info: [%d of %d]", index + 1, txsToDelete.length));
            int fromTx = txsToDelete[index];
            TransactionId fromTransaction = TransactionIdManager.getTransactionId(fromTx);
            TransactionId previousTransaction;
            try {
               previousTransaction = TransactionIdManager.getPriorTransaction(fromTransaction);
            } catch (TransactionDoesNotExist ex) {
               throw new OseeCoreException(
                     "You are trying to delete Transaction: " + fromTx + " which is a baseline transaction.  If your intent is to delete the Branch use the delete Branch Operation.  \n\nNO TRANSACTIONS WERE DELETED.");

            }

            fromToTxData.put(fromTransaction.getBranch(), new TxDeleteInfo(fromTransaction, previousTransaction));

            // Store transaction id(s) to delete - no need for gammas
            txsToDeleteQuery.add(-1L, fromTx);
            monitor.worked(1);
         }
         return fromToTxData;
      }

      private void deleteTransactionsFromTxDetails(OseeConnection connection, IProgressMonitor monitor, int queryId) throws OseeDataStoreException {
         monitor.subTask("Deleting Tx");
         ConnectionHandler.runPreparedUpdate(connection, DELETE_TRANSACTION_FROM_TRANSACTION_DETAILS, queryId);
         monitor.worked(1);
      }

      private void deleteItemEntriesForTransactions(OseeConnection connection, IProgressMonitor monitor, int txsToDeleteQueryId) throws OseeDataStoreException {
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
         monitor.worked(1);
      }

      private void populateJoinQueryFromSql(OseeConnection connection, TransactionJoinQuery joinQuery, String sql, String txFieldName, Object... data) throws OseeDataStoreException {
         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement(connection);
         try {
            chStmt.runPreparedQuery(sql, data);
            while (chStmt.next()) {
               joinQuery.add(chStmt.getLong("gamma_id"), chStmt.getInt(txFieldName));
            }
         } finally {
            chStmt.close();
         }
      }

      private int getMinTransaction(Collection<TxDeleteInfo> infos) {
         int toReturn = Integer.MAX_VALUE;
         for (TxDeleteInfo info : infos) {
            TransactionId previous = info.getPreviousTx();
            if (previous != null) {
               int toCheck = previous.getTransactionNumber();
               toReturn = Math.min(toReturn, toCheck);
            }
         }
         return toReturn != Integer.MAX_VALUE ? toReturn : -1;
      }

      private void updateTxCurrent(OseeConnection conn, IProgressMonitor monitor) throws OseeDataStoreException {
         monitor.subTask("Updating Previous Tx to Current");
         ConnectionHandler.runPreparedUpdate(conn, UPDATE_TXS, artifactJoinId, artifactJoinId, artifactJoinId);
         monitor.worked(1);
      }

      private void setChildBranchBaselineTxs(OseeConnection connection, IProgressMonitor monitor, HashCollection<Branch, TxDeleteInfo> transactions) throws OseeDataStoreException {
         List<Object[]> data = new ArrayList<Object[]>();
         monitor.subTask("Update Baseline Txs for Child Branches");
         for (TxDeleteInfo entry : transactions.getValues()) {
            TransactionId previousTransaction = entry.getPreviousTx();
            if (previousTransaction != null) {
               int toDeleteTransaction = entry.getTxToDelete().getTransactionNumber();

               data.add(new Object[] {String.valueOf(toDeleteTransaction),
                     String.valueOf(previousTransaction.getTransactionNumber()), "%" + toDeleteTransaction});
            }
         }
         if (data.size() > 0) {
            ConnectionHandler.runBatchUpdate(connection, UPDATE_TXS_DETAILS_COMMENT, data);
         }
         monitor.worked(1);
      }
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
      private final TransactionId txToDelete;
      private final TransactionId previousTxFromTxToDelete;

      public TxDeleteInfo(TransactionId txToDelete, TransactionId previousTxFromTxToDelete) {
         super();
         this.txToDelete = txToDelete;
         this.previousTxFromTxToDelete = previousTxFromTxToDelete;
      }

      public TransactionId getTxToDelete() {
         return txToDelete;
      }

      public TransactionId getPreviousTx() {
         return previousTxFromTxToDelete;
      }
   }
}