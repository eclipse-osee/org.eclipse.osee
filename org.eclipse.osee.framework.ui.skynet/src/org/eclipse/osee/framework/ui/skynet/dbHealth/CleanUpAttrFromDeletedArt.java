package org.eclipse.osee.framework.ui.skynet.dbHealth;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

public class CleanUpAttrFromDeletedArt extends DatabaseHealthTask {
   private static final String INSERT_ATTRS_TO_ART_COMMIT_TRANSACTION =
         "insert into osee_txs (tx_current, mod_type, transaction_id, gamma_id)  select 3, 5, tx1.transaction_id, att1.gamma_id from osee_txs tx1, osee_txs tx2, osee_tx_details td1, osee_tx_details td2, osee_artifact_version av1, osee_attribute att1 where td1.transaction_id = tx1.transaction_id AND tx1.gamma_id = av1.gamma_id and tx1.tx_current = 2 and tx1.mod_type = 3 and td1.branch_id = td2.branch_id and td2.transaction_id = tx2.transaction_id and tx2.gamma_id = att1.gamma_id and av1.art_id = att1.art_id and tx2.tx_current = 1 and td1.transaction_id <> td2.transaction_id";
   private static final String UPDATE_OLD_ATTRS_NOT_SAME_TRANSACTION =
         "update osee_txs set tx_current = 0 where (transaction_id, gamma_id) in (select tx2.transaction_id, tx2.gamma_id from osee_txs tx1, osee_txs tx2, osee_tx_details td1, osee_tx_details td2, osee_artifact_version av1, osee_attribute att1 where td1.transaction_id = tx1.transaction_id AND tx1.gamma_id = av1.gamma_id and tx1.tx_current = 2 and tx1.mod_type = 3 and td1.branch_id = td2.branch_id and td2.transaction_id = tx2.transaction_id and tx2.gamma_id = att1.gamma_id and av1.art_id = att1.art_id and tx2.tx_current = 1 and td1.transaction_id <> td2.transaction_id)";
   private static final String UPDATE_OLD_ATTRS_SAME_TRANSACTION =
         "update osee_txs set tx_current = 3, mod_type = 5 where (transaction_id, gamma_id) in (select tx2.transaction_id, tx2.gamma_id from osee_txs tx1, osee_txs tx2, osee_tx_details td1, osee_tx_details td2, osee_artifact_version av1, osee_attribute att1 where td1.transaction_id = tx1.transaction_id AND tx1.gamma_id = av1.gamma_id and tx1.tx_current = 2 and tx1.mod_type = 3 and td1.branch_id = td2.branch_id and td2.transaction_id = tx2.transaction_id and tx2.gamma_id = att1.gamma_id and av1.art_id = att1.art_id and tx2.tx_current = 1 and td1.transaction_id = td2.transaction_id)";

   @Override
   public String getFixTaskName() {
      return "Fix attributes from deleted artifacts";
   }

   @Override
   public String getVerifyTaskName() {
      return null;
   }

   @Override
   public void run(VariableMap variableMap, IProgressMonitor monitor, Operation operation, StringBuilder builder, boolean showDetails) throws Exception {

      boolean fix = operation == Operation.Fix;

      if (fix) {
         monitor.beginTask("Clean up attributes from deleted artifacts", 3);
         monitor.setTaskName("INSERT_ATTRS_TO_ART_COMMIT_TRANSACTION");
         ConnectionHandler.runPreparedUpdate(INSERT_ATTRS_TO_ART_COMMIT_TRANSACTION);
         monitor.worked(1);
         monitor.setTaskName("UPDATE_OLD_ATTRS_NOT_SAME_TRANSACTION");
         ConnectionHandler.runPreparedUpdate(UPDATE_OLD_ATTRS_NOT_SAME_TRANSACTION);
         monitor.worked(1);
         monitor.setTaskName("UPDATE_OLD_ATTRS_SAME_TRANSACTION");
         ConnectionHandler.runPreparedUpdate(UPDATE_OLD_ATTRS_SAME_TRANSACTION);
         monitor.done();
      }
   }

}
