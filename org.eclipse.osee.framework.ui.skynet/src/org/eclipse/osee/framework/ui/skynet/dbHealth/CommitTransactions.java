package org.eclipse.osee.framework.ui.skynet.dbHealth;

import java.sql.ResultSet;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * Updates commit transactions so new and then modified objects will be committed with a mod type of new. This BLAM
 * operation also removes attribute from deleted artifacts from committed transactions.
 * 
 * @author Jeff C. Phillips
 */
public class CommitTransactions extends DatabaseHealthTask {
   private static final String GET_COMMIT_TRANSACTIONS =
         "select transaction_id from osee_Define_tx_details where osee_comment like '%Commit%'";
   private static final String UPDATE_NEW_TRANSACTIONS_TO_CURRENT =
         "update osee_Define_txs set mod_type = 1 where transaction_id = ? AND mod_type <> 1 AND gamma_id in( select tx1.gamma_id from osee_define_tx_details td1, osee_define_txs tx1, osee_define_artifact_version av1 where td1.transaction_id = ? AND td1.transaction_id = tx1.transaction_id AND tx1.gamma_id = av1.gamma_id AND av1.art_id NOT IN (SELECT av2.art_id FROM osee_define_tx_details td2, osee_define_txs tx2, osee_define_artifact_version av2 where td2.osee_comment like '%Commit%' AND td2.transaction_id < td1.transaction_id AND td2.transaction_id = tx2.transaction_id AND tx2.gamma_id = av2.gamma_id) AND av1.art_id NOT IN (SELECT av3.art_id FROM osee_define_txs tx3, osee_define_tx_details td3, osee_define_artifact_version av3 WHERE td3.branch_id = td1.branch_id AND td3.transaction_id < td1.transaction_id AND td3.transaction_id = tx3.transaction_id AND tx3.mod_type = 1 AND tx3.gamma_id = av3.gamma_id))";
   private static final String DELETE_ORPHAN_ATTRIBUTES =
         "delete FROM osee_Define_attribute where gamma_id in (select t3.gamma_id from osee_define_txs t2, osee_define_attribute t3 where t2.transaction_id = ? AND t2.gamma_id = t3.gamma_id AND t3.art_id NOT in(SELECT art_id from osee_Define_txs t4, osee_Define_artifact_version t5 WHERE t4.transaction_id = t2.transaction_id AND t4.gamma_id = t5.gamma_id))";

   @Override
   public String getFixTaskName() {
      return "Fix commit transactionds by deleting orphan attributes and setting new artifacts that have been modified to a mod type of 1";
   }

   @Override
   public String getVerifyTaskName() {
      return null;
   }

   @Override
   public void run(BlamVariableMap variableMap, IProgressMonitor monitor, Operation operation, StringBuilder builder, boolean showDetails) throws Exception {

      if (operation.equals(Operation.Fix)) {
         ConnectionHandlerStatement chStmt = null;
         try {
            chStmt = ConnectionHandler.runPreparedQuery(GET_COMMIT_TRANSACTIONS, new Object[0]);
            ResultSet resultSet = chStmt.getRset();

            while (resultSet.next()) {
               int transactionNumber = resultSet.getInt(1);
               int updateCount =
                     ConnectionHandler.runPreparedUpdate(UPDATE_NEW_TRANSACTIONS_TO_CURRENT, transactionNumber,
                           transactionNumber);
               int deleteAttrCount = ConnectionHandler.runPreparedUpdate(DELETE_ORPHAN_ATTRIBUTES, transactionNumber);

               builder.append("For transaction: " + transactionNumber + " Number of update modTypes to 1:" + updateCount + " Number of deleted attrs: " + deleteAttrCount);
            }
         } finally {
            DbUtil.close(chStmt);
         }
      }
   }
}
