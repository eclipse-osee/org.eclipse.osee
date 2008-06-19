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
import org.eclipse.osee.framework.db.connection.core.transaction.DbTransaction;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.event.LocalTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.skynet.core.utility.JoinUtility;
import org.eclipse.osee.framework.skynet.core.utility.JoinUtility.TransactionJoinQuery;
import org.eclipse.osee.framework.ui.plugin.event.Event;

/**
 * @author Ryan D. Brooks
 */
public class DeleteTransactionJob extends Job {

   private static final String UPDATE_TXS_DETAILS_COMMENT =
         "update osee_define_tx_details SET osee_comment = replace(osee_comment, ?, ?) WHERE osee_comment like ?";

   private static final String SELECT_GAMMAS_FROM_TRANSACTION =
         "SELECT txs1.gamma_id FROM osee_define_txs txs1, osee_join_transaction txj1 WHERE txs1.transaction_id = txj1.transaction_id AND txj1.query_id = ? AND NOT EXISTS (SELECT 'x' FROM osee_define_txs txs2 WHERE txs1.gamma_id = txs2.gamma_id AND txs1.transaction_id <> txs2.transaction_id)";

   private static final String DELETE_TRANSACTION_FROM_TRANSACTION_DETAILS =
         "DELETE FROM osee_define_tx_details WHERE transaction_id IN (SELECT txj1.transaction_id FROM osee_join_transaction txj1 WHERE txj1.query_id = ?)";

   private static final String DELETE_POSTFIX =
         " outerTb where outerTb.gamma_id = (SELECT txj1.gamma_id from osee.osee_join_transaction txj1 WHERE outerTb.gamma_id = txj1.gamma_id AND txj1.query_id = ?)";

   private static final String UPDATE_TX_CURRENT =
         "UPDATE osee.osee_define_txs txs1 SET tx_current = 1 where txs1.mod_type <> 3 and txs1.gamma_id IN (SELECT txj1.gamma_id from osee.osee_join_transaction txj1 WHERE txj1.gamma_id = txs1.gamma_id AND txj1.query_id = ?)";

   private static final String UPDATE_TX_CURRENT_DELETED_ITEMS =
         "UPDATE osee.osee_define_txs txs1 SET tx_current = 2 where txs1.mod_type = 3 and txs1.gamma_id IN (SELECT txj1.gamma_id from osee.osee_join_transaction txj1 WHERE txj1.gamma_id = txs1.gamma_id AND txj1.query_id = ?)";

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
         TransactionJoinQuery previousTxsQuery = JoinUtility.createTransactionJoinQuery();
         try {
            monitor.beginTask(getName(), getTotalWork());
            List<ObjectPair<Integer, TransactionId>> fromToTxData =
                  getTransactionPairs(monitor, txIdsToDelete, txsToDeleteQuery, previousTxsQuery);
            txsToDeleteQuery.store(connection);
            previousTxsQuery.store(connection);

            setChildBranchBaselineTxs(connection, monitor, fromToTxData);

            deleteItemEntriesForTransactions(connection, monitor, txsToDeleteQuery.getQueryId());

            setPreviousTxToCurrent(connection, monitor, previousTxsQuery.getQueryId());

            deleteTransactionsFromTxDetails(connection, monitor, txsToDeleteQuery.getQueryId());
         } finally {
            if (connection != null && connection.isClosed() != true) {
               txsToDeleteQuery.delete(connection);
               previousTxsQuery.delete(connection);
            }
         }
      }

      private int getTotalWork() {
         return txIdsToDelete.length + 4;
      }

      private List<ObjectPair<Integer, TransactionId>> getTransactionPairs(IProgressMonitor monitor, int[] txsToDelete, TransactionJoinQuery txsToDeleteQuery, TransactionJoinQuery previousTxsQuery) throws BranchDoesNotExist, TransactionDoesNotExist, SQLException {
         List<ObjectPair<Integer, TransactionId>> fromToTxData = new ArrayList<ObjectPair<Integer, TransactionId>>();
         for (int index = 0; index < txsToDelete.length; index++) {
            monitor.subTask(String.format("Fetching Previous Tx Info: [%d of %d]", index + 1, txsToDelete.length));
            int fromTx = txsToDelete[index];
            TransactionId previous =
                  transactionIdManager.getPriorTransaction(transactionIdManager.getPossiblyEditableTransactionId(fromTx));

            fromToTxData.add(new ObjectPair<Integer, TransactionId>(fromTx, previous));
            // Store transaction id(s) to delete - no need for gammas
            txsToDeleteQuery.add(-1, fromTx);
            if (previous != null) {
               previousTxsQuery.add(-1, previous.getTransactionNumber());
            }
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

      private void deleteItemEntriesForTransactions(Connection connection, IProgressMonitor monitor, int queryId) throws SQLException {
         monitor.subTask("Deleting Tx Items");
         TransactionJoinQuery joinQuery = null;
         try {
            joinQuery = findAllGammasForTransaction(connection, queryId);
            int deleteQueryId = joinQuery.getQueryId();
            ConnectionHandler.runPreparedUpdate(connection, DELETE_ARTIFACT_VERSIONS, SQL3DataType.INTEGER,
                  deleteQueryId);
            ConnectionHandler.runPreparedUpdate(connection, DELETE_ATTRIBUTES, SQL3DataType.INTEGER, deleteQueryId);
            ConnectionHandler.runPreparedUpdate(connection, DELETE_RELATIONS, SQL3DataType.INTEGER, deleteQueryId);
         } finally {
            if (connection != null && connection.isClosed() != true) {
               joinQuery.delete(connection);
            }
         }
         monitor.worked(1);
      }

      private TransactionJoinQuery findAllGammasForTransaction(Connection connection, int queryId) throws SQLException {
         ConnectionHandlerStatement chStmt = null;
         TransactionJoinQuery joinQuery = JoinUtility.createTransactionJoinQuery();
         try {
            chStmt =
                  ConnectionHandler.runPreparedQuery(connection, SELECT_GAMMAS_FROM_TRANSACTION, SQL3DataType.INTEGER,
                        queryId);
            while (chStmt.next()) {
               joinQuery.add(chStmt.getRset().getInt("gamma_id"), -1);
            }
            joinQuery.store();
         } finally {
            DbUtil.close(chStmt);
         }
         return joinQuery;
      }

      private void setPreviousTxToCurrent(Connection connection, IProgressMonitor monitor, int queryId) throws SQLException {
         monitor.subTask("Updating Previous Tx to Current");
         TransactionJoinQuery joinQuery = null;
         try {
            joinQuery = findAllGammasForTransaction(connection, queryId);
            joinQuery.store(connection);

            ConnectionHandler.runPreparedUpdate(connection, UPDATE_TX_CURRENT, SQL3DataType.INTEGER,
                  joinQuery.getQueryId());
            ConnectionHandler.runPreparedUpdate(connection, UPDATE_TX_CURRENT_DELETED_ITEMS, SQL3DataType.INTEGER,
                  joinQuery.getQueryId());
         } finally {
            if (connection != null && connection.isClosed() != true) {
               joinQuery.delete(connection);
            }
         }
         monitor.worked(1);
      }

      private void setChildBranchBaselineTxs(Connection connection, IProgressMonitor monitor, List<ObjectPair<Integer, TransactionId>> transactions) throws SQLException {
         List<Object[]> data = new ArrayList<Object[]>();
         monitor.subTask("Update Baseline Txs for Child Branches");
         for (ObjectPair<Integer, TransactionId> entry : transactions) {
            int fromTransaction = entry.object1;
            TransactionId toTransaction = entry.object2;
            if (toTransaction != null) {
               data.add(new Object[] {SQL3DataType.VARCHAR, String.valueOf(fromTransaction), SQL3DataType.VARCHAR,
                     String.valueOf(toTransaction.getTransactionNumber()), SQL3DataType.VARCHAR, "%" + fromTransaction});
            }
         }
         if (data.size() > 0) {
            ConnectionHandler.runPreparedUpdate(connection, UPDATE_TXS_DETAILS_COMMENT, data);
         }
         monitor.worked(1);
      }
   }
}