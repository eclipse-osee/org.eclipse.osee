/*
 * Created on Mar 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;

/**
 * @author Ryan D. Brooks
 */
public class DeleteTransactionJob extends Job {
   private static final String SELECT_GAMMAS_FROM_TRANSACTION =
         "SELECT txd1.gamma_id FROM osee_define_tx_details txd1 WHERE txd1.transaction_id = ? AND NOT EXISTS (SELECT 'x' FROM osee_define_tx_details txd2 WHERE txd1.gamma_id = txd2.gamma_id AND txd1.transaction_id <> txd2.transaction_id)";
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

         int previousTransaction =
               transactionIdManager.getPriorTransaction(
                     transactionIdManager.getPossiblyEditableTransactionId(transactionIdNumber)).getTransactionNumber();

         chStmt1 =
               ConnectionHandler.runPreparedQuery(
                     "select transaction_id, osee_comment from osee_define_tx_details where osee_comment like ?",
                     SQL3DataType.VARCHAR, "%" + transactionIdNumber + ")");

         ResultSet rSet = chStmt1.getRset();
         while (rSet.next()) {
            String newComment =
                  rSet.getString("osee_comment").replace(String.valueOf(transactionIdNumber),
                        String.valueOf(previousTransaction));
            ConnectionHandler.runPreparedUpdate(
                  "Update osee_define_tx_details SET osee_comment = ? WHERE transaction_id = ?", SQL3DataType.VARCHAR,
                  newComment, SQL3DataType.INTEGER, rSet.getInt("transaction_id"));
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
      } finally {
         DbUtil.close(chStmt1);
         DbUtil.close(chStmt2);
         monitor.done();
      }
      return returnStatus;
   }
}