package org.eclipse.osee.framework.ui.skynet.dbHealth;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * Identifies and removes addressing from the transaction table that no longer addresses other tables.
 * 
 * @author Jeff C. Phillips
 */
public class CleanUpAddressingData extends DatabaseHealthTask {

   private static final String NOT_ADDRESSESED_GAMMAS =
         "select distinct gamma_id from osee_Define_txs minus (select gamma_id from osee_Define_artifact_version union select gamma_id from osee_Define_attribute union select gamma_id from osee_Define_rel_link)";
   private static final String NOT_ADDRESSESED_TRANSACTIONS =
         "select distinct transaction_id from osee_Define_tx_details minus select transaction_id from osee_Define_txs";
   private static final String REMOVE_NOT_ADDRESSED_GAMMAS = "Delete From osee_define_txs Where gamma_id = ?";
   private static final String REMOVE_NOT_ADDRESSED_TRANSACTIONS =
         "Delete From osee_define_txs Where transaction_id = ?";

   @Override
   public String getFixTaskName() {
      return "Fix not addressed txs data";
   }

   @Override
   public String getVerifyTaskName() {
      return "Check for not addressed txs data";
   }

   @Override
   public void run(BlamVariableMap variableMap, IProgressMonitor monitor, Operation operation, StringBuilder builder, boolean showDetails) throws Exception {
      ConnectionHandlerStatement chStmt = null;
      boolean fix = operation == Operation.Fix;

      if (showDetails) {
         if (fix) {
            builder.append("Fixing Not Addressed Gamma IDs \n");
         } else {
            builder.append("Checking For Not Addressed Gamma IDs \n");
         }
      }

      try {
         chStmt = ConnectionHandler.runPreparedQuery(NOT_ADDRESSESED_GAMMAS);
         int count = 0;
         while (chStmt.next()) {
            count++;
            int gammaId = chStmt.getRset().getInt(1);
            if (showDetails) {
               builder.append("Gamma ID: " + gammaId + "\n");
            }
            if (fix) {
               ConnectionHandler.runPreparedUpdate(REMOVE_NOT_ADDRESSED_GAMMAS, gammaId);
            }
         }
         if (!fix) {
            builder.append(count != 0 ? "Failed: found - " + count + " not addressed Gammas" : "Passed: Not Addressed Gamma IDs \n");
         } else {
            builder.append(count != 0 ? "Fixed - " + count + " not addressed Gammas" : "Found 0 Not Addressed Gamma IDs to fix\n");
         }
      } finally {
         DbUtil.close(chStmt);
      }

      if (showDetails) {
         if (fix) {
            builder.append("Fixing Not Addressed Transaction IDs \n");
         } else {
            builder.append("Checking For Not Addressed Transaction IDs \n");
         }
      }
      try {
         chStmt = ConnectionHandler.runPreparedQuery(NOT_ADDRESSESED_TRANSACTIONS);
         int count = 0;
         while (chStmt.next()) {
            count++;
            int transactionId = chStmt.getRset().getInt(1);
            if (showDetails) {
               builder.append("Transaction ID: " + transactionId + "\n");
            }
            if (fix) {
               ConnectionHandler.runPreparedUpdate(REMOVE_NOT_ADDRESSED_TRANSACTIONS, transactionId);
            }
         }
         if (!fix) {
            builder.append(count != 0 ? "Failed: found - " + count + "not addressed transactions\n" : "Passed: Not Addressed Transaction IDs \n");
         } else {
            builder.append(count != 0 ? "Fixed - " + count + "not addressed transactions\n" : "Found 0 Not Addressed Transaction IDs to fix \n");
         }
      } finally {
         DbUtil.close(chStmt);
      }
   }

}
