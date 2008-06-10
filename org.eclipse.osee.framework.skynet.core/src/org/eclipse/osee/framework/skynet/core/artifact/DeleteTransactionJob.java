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

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Ryan D. Brooks
 */
public class DeleteTransactionJob extends Job {
   private static final String SELECT_GAMMAS_FROM_TRANSACTION =
         "SELECT txs1.gamma_id FROM osee_define_txs txs1 WHERE txs1.transaction_id = ? AND NOT EXISTS (SELECT 'x' FROM osee_define_txs txs2 WHERE txs1.gamma_id = txs2.gamma_id AND txs1.transaction_id <> txs2.transaction_id)";
   private static final String DELETE_TRANSACTION_FROM_TRANSACTION_DETAILS =
         "DELETE FROM " + TRANSACTION_DETAIL_TABLE + " WHERE transaction_id = ?";
   private static final String DELETE_GAMMA_FROM_ARTIFACT_VERSION =
         "DELETE FROM " + ARTIFACT_VERSION_TABLE + " WHERE gamma_id = ?";
   private static final String DELETE_GAMMA_FROM_RELATION_TABLE =
         "DELETE FROM " + RELATION_LINK_VERSION_TABLE + " WHERE gamma_id = ?";
   private static final String DELETE_GAMMA_FROM_ATTRIBUTE =
         "DELETE FROM " + ATTRIBUTE_VERSION_TABLE + " WHERE gamma_id = ?";
   private static final TransactionIdManager transactionIdManager = TransactionIdManager.getInstance();

   private final int transactionIdNumber;

   /**
    * @param name
    * @param transactionIdNumber
    */
   public DeleteTransactionJob(int transactionIdNumber) {
      super("Delete transaction: " + transactionIdNumber);
      this.transactionIdNumber = transactionIdNumber;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected IStatus run(IProgressMonitor monitor) {
      ConnectionHandlerStatement chStmt1 = null;
      ConnectionHandlerStatement chStmt2 = null;
      IStatus returnStatus = Status.OK_STATUS;

      try {

         TransactionId previousTransactionId =
               transactionIdManager.getPriorTransaction(transactionIdManager.getPossiblyEditableTransactionId(transactionIdNumber));

         if (previousTransactionId != null) {
            chStmt1 =
                  ConnectionHandler.runPreparedQuery(
                        "select transaction_id, osee_comment from osee_define_tx_details where osee_comment like ?",
                        SQL3DataType.VARCHAR, "%" + transactionIdNumber + ")");

            ResultSet rSet = chStmt1.getRset();
            while (rSet.next()) {
               String newComment =
                     rSet.getString("osee_comment").replace(String.valueOf(transactionIdNumber),
                           String.valueOf(previousTransactionId.getTransactionNumber()));
               ConnectionHandler.runPreparedUpdate(
                     "Update osee_define_tx_details SET osee_comment = ? WHERE transaction_id = ?",
                     SQL3DataType.VARCHAR, newComment, SQL3DataType.INTEGER, rSet.getInt("transaction_id"));
            }
         }

         chStmt2 =
               ConnectionHandler.runPreparedQuery(SELECT_GAMMAS_FROM_TRANSACTION, SQL3DataType.INTEGER,
                     transactionIdNumber);

         while (chStmt2.next()) {
            int gammaId = chStmt2.getRset().getInt("gamma_id");

            ConnectionHandler.runPreparedUpdate(DELETE_GAMMA_FROM_RELATION_TABLE, SQL3DataType.INTEGER, gammaId);
            ConnectionHandler.runPreparedUpdate(DELETE_GAMMA_FROM_ARTIFACT_VERSION, SQL3DataType.INTEGER, gammaId);
            ConnectionHandler.runPreparedUpdate(DELETE_GAMMA_FROM_ATTRIBUTE, SQL3DataType.INTEGER, gammaId);
         }
         ConnectionHandler.runPreparedUpdate(DELETE_TRANSACTION_FROM_TRANSACTION_DETAILS, SQL3DataType.INTEGER,
               transactionIdNumber);
      } catch (SQLException ex) {
         returnStatus = new Status(Status.ERROR, SkynetActivator.PLUGIN_ID, -1, ex.getLocalizedMessage(), ex);
      } catch (OseeCoreException ex) {
         returnStatus = new Status(Status.ERROR, SkynetActivator.PLUGIN_ID, -1, ex.getLocalizedMessage(), ex);
      } finally {
         DbUtil.close(chStmt1);
         DbUtil.close(chStmt2);
         monitor.done();
      }
      return returnStatus;
   }
}