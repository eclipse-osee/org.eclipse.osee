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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.core.JoinUtility;
import org.eclipse.osee.framework.db.connection.core.JoinUtility.TransactionJoinQuery;
import org.eclipse.osee.framework.db.connection.core.transaction.DbTransaction;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.event.LocalTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.event.Event;

/**
 * @author Ryan D. Brooks
 */
public class DeleteTransactionJob extends Job {

   private static final String UPDATE_TXS_DETAILS_COMMENT =
         "update osee_define_tx_details SET osee_comment = replace(osee_comment, ?, ?) WHERE osee_comment like ?";

   private static final String SELECT_GAMMAS_FROM_TRANSACTION =
         "SELECT txs1.gamma_id, txs1.transaction_id FROM osee_define_txs txs1, osee_join_transaction txj1 WHERE " + "txs1.transaction_id = txj1.transaction_id AND txj1.query_id = ? AND " + "NOT EXISTS (SELECT 'x' FROM osee_define_txs txs2 WHERE txs1.gamma_id = txs2.gamma_id AND txs1.transaction_id <> txs2.transaction_id)";

   private static final String DELETE_TRANSACTION_FROM_TRANSACTION_DETAILS =
         "DELETE FROM osee_define_tx_details WHERE transaction_id IN (SELECT txj1.transaction_id FROM osee_join_transaction txj1 WHERE txj1.query_id = ?)";

   private static final String DELETE_POSTFIX =
         " outerTb where outerTb.gamma_id = (SELECT txj1.gamma_id from osee.osee_join_transaction txj1 WHERE outerTb.gamma_id = txj1.gamma_id AND txj1.query_id = ?)";

   private static final String SELECT_ATTRIBUTES_TO_UPDATE =
         "SELECT maxt, txs2.gamma_id FROM osee_define_attribute att2,  osee_define_txs txs2, (SELECT MAX(txs1.transaction_id) AS  maxt, att1.attr_id AS atid, txd1.branch_id FROM osee_define_attribute att1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE att1.gamma_id = txs1.gamma_id and txs1.transaction_id >= ? and txd1.tx_type = 0 AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = ? GROUP BY att1.attr_id, txd1.branch_id) new_stuff WHERE atid = att2.attr_id AND att2.gamma_id = txs2.gamma_id AND txs2.transaction_id = maxt and txs2.transaction_id >= ?";
   private static final String SELECT_ARTIFACTS_TO_UPDATE =
         "SELECT maxt, txs1.gamma_id FROM osee_define_artifact_version arv2,  osee_define_txs txs1, (SELECT MAX(txs2.transaction_id) AS maxt, arv1.art_id AS art, txd1.branch_id FROM osee_define_artifact_version arv1, osee_define_txs txs2, osee_define_tx_details txd1 WHERE arv1.gamma_id = txs2.gamma_id and txs2.transaction_id >= ? and txd1.tx_type = 0 AND txs2.transaction_id = txd1.transaction_id AND txd1.branch_id = ? GROUP BY arv1.art_id, txd1.branch_id) new_stuff WHERE art = arv2.art_id AND arv2.gamma_id = txs1.gamma_id AND txs1.transaction_id = maxt and txs1.transaction_id >= ?";
   private static final String SELECT_RELATIONS_TO_UPDATE =
         "SELECT maxt, txs1.gamma_id FROM osee_define_rel_link rel2, osee_define_txs txs1, (SELECT MAX(txs2.transaction_id) AS maxt, rel1.rel_link_id AS rel_id, txd1.branch_id FROM osee_define_rel_link rel1, osee_define_txs txs2, osee_define_tx_details txd1 WHERE rel1.gamma_id = txs2.gamma_id and txs2.transaction_id >= ? and txd1.tx_type = 0 AND txs2.transaction_id = txd1.transaction_id AND txd1.branch_id = ? GROUP BY rel1.rel_link_id, txd1.branch_id) new_stuff WHERE rel_id = rel2.rel_link_id AND rel2.gamma_id = txs1.gamma_id AND txs1.transaction_id = maxt and txs1.transaction_id >= ?";

   private static final String UPDATE_TX_CURRENT =
         "UPDATE osee.osee_define_txs txs1 SET tx_current = 1 where txs1.mod_type <> 3 and txs1.gamma_id IN (SELECT txj1.gamma_id from osee.osee_join_transaction txj1 WHERE txj1.gamma_id = txs1.gamma_id AND txj1.transaction_id = txs1.transaction_id AND txj1.query_id = ?)";

   private static final String UPDATE_TX_CURRENT_DELETED_ITEMS =
         "UPDATE osee.osee_define_txs txs1 SET tx_current = 2 where txs1.mod_type = 3 and txs1.gamma_id IN (SELECT txj1.gamma_id from osee.osee_join_transaction txj1 WHERE txj1.gamma_id = txs1.gamma_id AND txj1.transaction_id = txs1.transaction_id AND txj1.query_id = ?)";

   private final static String DELETE_ARTIFACT_VERSIONS = "DELETE FROM osee_define_artifact_version " + DELETE_POSTFIX;
   private final static String DELETE_ATTRIBUTES = "DELETE FROM osee_define_attribute " + DELETE_POSTFIX;
   private final static String DELETE_RELATIONS = "DELETE FROM osee_define_rel_link " + DELETE_POSTFIX;

   private static final TransactionIdManager transactionIdManager = TransactionIdManager.getInstance();

   private final int[] txIdsToDelete;

   /**
    * @param name
    * @param transactionIdNumber
    */
   public DeleteTransactionJob(int... txIdsToDelete) {
      super(String.format("Delete transactions: %s", Arrays.toString(txIdsToDelete)));
      this.txIdsToDelete = txIdsToDelete;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected IStatus run(final IProgressMonitor monitor) {
      IStatus returnStatus = Status.CANCEL_STATUS;
      try {
         DeleteTransactionTx deleteTransactionTx = new DeleteTransactionTx(monitor);
         deleteTransactionTx.execute();
         returnStatus = Status.OK_STATUS;

         Collection<Event> events = new ArrayList<Event>();
         SkynetEventManager.getInstance().kick(new LocalTransactionEvent(events, this));
      } catch (Exception ex) {
         returnStatus = new Status(Status.ERROR, SkynetActivator.PLUGIN_ID, -1, ex.getLocalizedMessage(), ex);
      } finally {
         monitor.done();
      }
      return returnStatus;
   }

   private final class DeleteTransactionTx extends DbTransaction {
      private IProgressMonitor monitor;

      public DeleteTransactionTx(IProgressMonitor monitor) {
         this.monitor = monitor;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.db.connection.core.transaction.AbstractDbTxTemplate#handleTxWork()
       */
      @Override
      protected void handleTxWork(Connection connection) throws Exception {
         TransactionJoinQuery txsToDeleteQuery = JoinUtility.createTransactionJoinQuery();
         try {
            monitor.beginTask(getName(), getTotalWork());
            Arrays.sort(txIdsToDelete);

            HashCollection<Branch, TxDeleteInfo> fromToTxData =
                  getTransactionPairs(monitor, txIdsToDelete, txsToDeleteQuery);
            txsToDeleteQuery.store(connection);

            setChildBranchBaselineTxs(connection, monitor, fromToTxData);
            deleteItemEntriesForTransactions(connection, monitor, txsToDeleteQuery.getQueryId());
            deleteTransactionsFromTxDetails(connection, monitor, txsToDeleteQuery.getQueryId());

            updateTxCurrent(connection, monitor, fromToTxData);
         } finally {
            if (connection != null && connection.isClosed() != true) {
               txsToDeleteQuery.delete(connection);
            }
         }
      }

      private int getTotalWork() {
         return txIdsToDelete.length + 5;
      }

      private HashCollection<Branch, TxDeleteInfo> getTransactionPairs(IProgressMonitor monitor, int[] txsToDelete, TransactionJoinQuery txsToDeleteQuery) throws BranchDoesNotExist, TransactionDoesNotExist, SQLException {
         HashCollection<Branch, TxDeleteInfo> fromToTxData = new HashCollection<Branch, TxDeleteInfo>();
         for (int index = 0; index < txsToDelete.length; index++) {
            monitor.subTask(String.format("Fetching Previous Tx Info: [%d of %d]", index + 1, txsToDelete.length));
            int fromTx = txsToDelete[index];
            TransactionId fromTransaction = transactionIdManager.getPossiblyEditableTransactionId(fromTx);
            TransactionId previousTransaction = transactionIdManager.getPriorTransaction(fromTransaction);

            fromToTxData.put(fromTransaction.getBranch(), new TxDeleteInfo(fromTransaction, previousTransaction));

            // Store transaction id(s) to delete - no need for gammas
            txsToDeleteQuery.add(-1, fromTx);
            monitor.worked(1);
         }
         return fromToTxData;
      }

      private void deleteTransactionsFromTxDetails(Connection connection, IProgressMonitor monitor, int queryId) throws SQLException {
         monitor.subTask("Deleting Tx");
         ConnectionHandler.runPreparedUpdate(connection, DELETE_TRANSACTION_FROM_TRANSACTION_DETAILS,
               SQL3DataType.INTEGER, queryId);
         monitor.worked(1);
      }

      private void deleteItemEntriesForTransactions(Connection connection, IProgressMonitor monitor, int txsToDeleteQueryId) throws SQLException {
         monitor.subTask("Deleting Tx Items");
         TransactionJoinQuery txGammasToDelete = JoinUtility.createTransactionJoinQuery();
         try {
            populateJoinQueryFromSql(connection, txGammasToDelete, SELECT_GAMMAS_FROM_TRANSACTION, "transaction_id",
                  SQL3DataType.INTEGER, txsToDeleteQueryId);
            txGammasToDelete.store();
            int deleteQueryId = txGammasToDelete.getQueryId();
            ConnectionHandler.runPreparedUpdate(connection, DELETE_ARTIFACT_VERSIONS, SQL3DataType.INTEGER,
                  deleteQueryId);
            ConnectionHandler.runPreparedUpdate(connection, DELETE_ATTRIBUTES, SQL3DataType.INTEGER, deleteQueryId);
            ConnectionHandler.runPreparedUpdate(connection, DELETE_RELATIONS, SQL3DataType.INTEGER, deleteQueryId);

         } finally {
            if (txGammasToDelete != null && connection != null && connection.isClosed() != true) {
               txGammasToDelete.delete(connection);
            }
         }
         monitor.worked(1);
      }

      private void populateJoinQueryFromSql(Connection connection, TransactionJoinQuery joinQuery, String sql, String txFieldName, Object... data) throws SQLException {
         ConnectionHandlerStatement chStmt = null;
         try {
            chStmt = ConnectionHandler.runPreparedQuery(connection, sql, data);
            while (chStmt.next()) {
               joinQuery.add(chStmt.getRset().getInt("gamma_id"), chStmt.getRset().getInt(txFieldName));
            }
         } finally {
            DbUtil.close(chStmt);
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

      private void updateTxCurrent(Connection conn, IProgressMonitor monitor, HashCollection<Branch, TxDeleteInfo> txToDelete) throws SQLException {
         monitor.subTask("Updating Previous Tx to Current");
         List<TransactionJoinQuery> joins = new ArrayList<TransactionJoinQuery>();
         try {
            monitor.subTask("Recalculating Tx Current");
            for (Branch branch : txToDelete.keySet()) {
               int branchId = branch.getBranchId();
               int startTx = getMinTransaction(txToDelete.getValues(branch));

               Object[] searchData =
                     new Object[] {SQL3DataType.INTEGER, startTx, SQL3DataType.INTEGER, branchId, SQL3DataType.INTEGER,
                           startTx};

               TransactionJoinQuery joinQuery = JoinUtility.createTransactionJoinQuery();
               joins.add(joinQuery);
               populateJoinQueryFromSql(conn, joinQuery, SELECT_ATTRIBUTES_TO_UPDATE, "maxt", searchData);
               populateJoinQueryFromSql(conn, joinQuery, SELECT_ARTIFACTS_TO_UPDATE, "maxt", searchData);
               populateJoinQueryFromSql(conn, joinQuery, SELECT_RELATIONS_TO_UPDATE, "maxt", searchData);
               joinQuery.store();
            }
            monitor.worked(1);

            monitor.subTask("Updating Tx Current");
            List<Object[]> data = new ArrayList<Object[]>();
            for (TransactionJoinQuery join : joins) {
               data.add(new Object[] {SQL3DataType.INTEGER, join.getQueryId()});
            }
            ConnectionHandler.runPreparedUpdate(conn, UPDATE_TX_CURRENT, data);
            ConnectionHandler.runPreparedUpdate(conn, UPDATE_TX_CURRENT_DELETED_ITEMS, data);
            monitor.worked(1);
         } finally {
            if (conn != null && conn.isClosed() != true) {
               for (TransactionJoinQuery join : joins) {
                  try {
                     join.delete(conn);
                  } catch (Exception ex) {
                  }
               }
            }
         }
      }

      private void setChildBranchBaselineTxs(Connection connection, IProgressMonitor monitor, HashCollection<Branch, TxDeleteInfo> transactions) throws SQLException {
         List<Object[]> data = new ArrayList<Object[]>();
         monitor.subTask("Update Baseline Txs for Child Branches");
         for (TxDeleteInfo entry : transactions.getValues()) {
            TransactionId previousTransaction = entry.getPreviousTx();
            if (previousTransaction != null) {
               int toDeleteTransaction = entry.getTxToDelete().getTransactionNumber();

               data.add(new Object[] {SQL3DataType.VARCHAR, String.valueOf(toDeleteTransaction), SQL3DataType.VARCHAR,
                     String.valueOf(previousTransaction.getTransactionNumber()), SQL3DataType.VARCHAR,
                     "%" + toDeleteTransaction});
            }
         }
         if (data.size() > 0) {
            ConnectionHandler.runPreparedUpdate(connection, UPDATE_TXS_DETAILS_COMMENT, data);
         }
         monitor.worked(1);
      }
   }

   private final class TxDeleteInfo {
      private TransactionId txToDelete;
      private TransactionId previousTxFromTxToDelete;

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