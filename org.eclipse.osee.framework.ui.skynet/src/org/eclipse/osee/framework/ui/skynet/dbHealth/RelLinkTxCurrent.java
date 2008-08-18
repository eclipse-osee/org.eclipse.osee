/*
 * Created on Aug 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.dbHealth;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Theron Virgin
 */
public class RelLinkTxCurrent extends DatabaseHealthTask {
   private static final String NO_TX_CURRENT_SET =
         "SELECT distinct rel.rel_link_id, det.branch_id FROM osee_define_tx_details det, osee_define_txs txs, osee_define_rel_link rel WHERE det.transaction_id = txs.transaction_id AND txs.gamma_id = rel.gamma_id AND txs.tx_current = 0 MINUS SELECT distinct rel.rel_link_id, det.branch_id FROM osee_define_tx_details det, osee_define_txs txs, osee_define_rel_link rel WHERE det.transaction_id = txs.transaction_id AND txs.gamma_id = rel.gamma_id AND txs.tx_current in (1,2)";

   private static final String QUERY_TX_CURRENT_SET =
         "SELECT txs.transaction_id, txs.gamma_id from osee_define_txs txs, osee_define_tx_details det, osee_define_rel_link rel WHERE det.branch_id = ? And det.transaction_id = txs.transaction_id And txs.tx_current in (1,2) And txs.gamma_id = rel.gamma_id and rel.rel_link_id = ?";

   private static final String NO_TX_CURRENT_CLEANUP =
         "UPDATE osee_define_txs SET tx_current = CASE WHEN mod_type = 3 THEN 2 ELSE 1 END WHERE (transaction_id, gamma_id) = (SELECT txs2.transaction_id, txs2.gamma_id FROM osee_define_txs txs2, osee_define_rel_link rel2 WHERE rel2.rel_link_id = ? And rel2.gamma_id = txs2.gamma_id  And txs2.transaction_id = (SELECT MAX(txs.transaction_id) from osee_define_txs txs, osee_define_tx_details det, osee_define_rel_link rel WHERE det.branch_id = ? And det.transaction_id = txs.transaction_id And txs.gamma_id = rel.gamma_id and rel.rel_link_id = ?))";

   private static final String MULTIPLE_TX_CURRENT_SET =
         "SELECT resulttable.branch_id, resulttable.rel_link_id, COUNT(resulttable.branch_id) AS numoccurrences FROM (SELECT txd1.branch_id, rel1.rel_link_id FROM osee_define_tx_details txd1, osee_define_txs txs1, osee_define_rel_link rel1 WHERE txd1.transaction_id = txs1.transaction_id AND txs1.gamma_id = rel1.gamma_id AND txs1.tx_current in (1,2)) resulttable GROUP BY resulttable.branch_id, resulttable.rel_link_id HAVING(COUNT(resulttable.branch_id) > 1) order by branch_id";

   private static final String DUPLICATE_RELATIONS_TX_CURRENT =
         "Select rel1.rel_link_id, rel1.gamma_id as gamma_id_1, rel2.gamma_id as gamma_id_2, txs1.tx_current as tx_current_1, txs2.tx_current as tx_current_2, txs1.transaction_id as tran_id_1, txs2.transaction_id as tran_id_2, det1.branch_id From osee_define_rel_link rel1, osee_define_rel_link rel2, osee_define_txs txs1, osee_define_txs txs2, osee_define_tx_details det1, osee_define_tx_details det2 WHERE rel1.rel_link_id = rel2.rel_link_id AND rel1.gamma_id < rel2.gamma_id AND rel1.gamma_id = txs1.gamma_id AND rel2.gamma_id = txs2.gamma_id AND txs1.tx_current in (1,2) and txs2.tx_current in (1,2) AND txs1.transaction_id = det1.transaction_id AND txs2.transaction_id = det2.transaction_id AND det1.branch_id = det2.branch_id";

   private static final String DUPLICATE_TX_CURRENT_CLEANUP =
         "UPDATE osee_define_txs SET tx_current = 0 WHERE gamma_id = ? AND transaction_id = ?";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthTask#getFixTaskName()
    */
   @Override
   public String getFixTaskName() {
      return "Fix TX_Current Relation Link Errors";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthTask#getVerifyTaskName()
    */
   @Override
   public String getVerifyTaskName() {
      return "Check for TX_Current Relation Link Errors";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthTask#run(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.core.runtime.IProgressMonitor, org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthTask.Operation, java.lang.StringBuilder)
    */
   @Override
   public void run(BlamVariableMap variableMap, IProgressMonitor monitor, Operation operation, StringBuilder builder, boolean showDetails) throws Exception {
      monitor.beginTask("Verify TX_Current Relation Link Errors", 100);
      ConnectionHandlerStatement connection = null;
      ConnectionHandlerStatement connection2 = null;
      ResultSet resultSet;
      int count = 0;
      if (operation.equals(Operation.Verify)) {
         try {
            connection = ConnectionHandler.runPreparedQuery(NO_TX_CURRENT_SET);
            monitor.worked(35);
            if (monitor.isCanceled()) return;
            resultSet = connection.getRset();
            while (resultSet.next()) {
               if (count == 0 && showDetails) {
                  builder.append("FAILED: Found the following TX_Current Relation Link Errors with no tx_current set:\n");
               }
               count++;
               if (showDetails) {
                  builder.append(String.format("%-4d Rel_Link_ID = %-8d Branch_ID = %-8d No tx_current set.\n", count,
                        resultSet.getInt("rel_link_id"), resultSet.getInt("branch_id")));
               }
            }
         } finally {
            DbUtil.close(connection);
         }
         monitor.worked(15);
         if (monitor.isCanceled()) return;
         if (count > 0) {
            builder.append("FAILED: Found ");
            builder.append(count);
            builder.append(" Relation Links that have no tx_current value set to either 1 or 2\n");
         } else {
            builder.append("PASSED: Found no Relation Links with no tx_current values set.\n");
         }
         try {
            count = 0;
            monitor.worked(5);
            connection = ConnectionHandler.runPreparedQuery(MULTIPLE_TX_CURRENT_SET);
            monitor.worked(30);
            if (monitor.isCanceled()) return;
            resultSet = connection.getRset();
            while (resultSet.next()) {
               if (count == 0 && showDetails) {
                  builder.append("Found the following TX_Current Relation Link Errors with multiple tx_currents set:\n");
               }
               count++;
               if (showDetails) {
                  builder.append(String.format(
                        "%-4d Rel_Link_ID = %-8d , Branch_ID = %-8d With %-2d tx_currents set.\n", count,
                        resultSet.getInt("rel_link_id"), resultSet.getInt("branch_id"),
                        resultSet.getInt("numoccurrences")));
               }
            }
         } finally {
            DbUtil.close(connection);
         }
         monitor.worked(15);
         if (monitor.isCanceled()) return;
         if (count > 0) {
            builder.append("FAILED: Found ");
            builder.append(count);
            builder.append(" Relation Links that have multiple tx_current values set to either 1 or 2\n");
         } else {
            builder.append("PASSED: Found no Relation Links with multiple tx_current values set.\n");
         }

      } else {
         /** Duplicate TX_current Cleanup **/
         monitor.worked(1);
         monitor.subTask("Querying for multiple Tx_currents");
         try {
            connection = ConnectionHandler.runPreparedQuery(DUPLICATE_RELATIONS_TX_CURRENT);
            resultSet = connection.getRset();
            monitor.worked(9);
            monitor.subTask("Processing Results");
            if (monitor.isCanceled()) return;

            int total = 0;
            while (resultSet.next()) {
               if (total == 0 && showDetails) {
                  builder.append("Cleaning up the following tx_current duplications\n");
               }
               total++;
               if (showDetails) {
                  showTxCurrentText(resultSet, total, builder);
               }
               int transaction_id =
                     resultSet.getInt("tran_id_1") < resultSet.getInt("tran_id_2") ? resultSet.getInt("tran_id_1") : resultSet.getInt("tran_id_2");
               int gamma_id =
                     resultSet.getInt("tran_id_1") < resultSet.getInt("tran_id_2") ? resultSet.getInt("gamma_id_1") : resultSet.getInt("gamma_id_2");
               ConnectionHandler.runPreparedUpdateReturnCount(DUPLICATE_TX_CURRENT_CLEANUP, SQL3DataType.INTEGER,
                     gamma_id, SQL3DataType.INTEGER, transaction_id);
               if (showDetails) {
                  builder.append("Set Transaction ");
                  builder.append(transaction_id);
                  builder.append(" to 0\n");
               }
               if (monitor.isCanceled()) {
                  builder.append("Cleaned up " + total + "Tx_Current duplication errors");
                  return;
               }
            }
            builder.append("Cleaned up " + total + "Tx_Current duplication errors");

         } finally {
            DbUtil.close(connection);
         }
         try {
            connection = ConnectionHandler.runPreparedQuery(NO_TX_CURRENT_SET);
            monitor.worked(35);
            if (monitor.isCanceled()) return;
            resultSet = connection.getRset();
            while (resultSet.next()) {
               if (count == 0 && showDetails) {
                  builder.append("Cleaning up the following TX_Current Relation Link Errors with no tx_current set:\n");
               }
               count++;
               ConnectionHandler.runPreparedUpdate(NO_TX_CURRENT_CLEANUP, SQL3DataType.INTEGER,
                     resultSet.getInt("rel_link_id"), SQL3DataType.INTEGER, resultSet.getInt("branch_id"),
                     SQL3DataType.INTEGER, resultSet.getInt("rel_link_id"));
               if (showDetails) {
                  connection2 =
                        ConnectionHandler.runPreparedQuery(QUERY_TX_CURRENT_SET, SQL3DataType.INTEGER,
                              resultSet.getInt("branch_id"), SQL3DataType.INTEGER, resultSet.getInt("rel_link_id"));
                  ResultSet resultSet2 = connection2.getRset();
                  int trans_id = 0, gamma_id = 0;
                  if (resultSet2.next()) {
                     trans_id = resultSet2.getInt("transaction_id");
                     gamma_id = resultSet2.getInt("gamma_id");
                  }
                  builder.append(String.format(
                        "%-4d Rel_Link_ID = %-8d Branch_ID = %-8d Set Transaction: %-8d Gamma: %-8d current.\n", count,
                        resultSet.getInt("rel_link_id"), resultSet.getInt("branch_id"), trans_id, gamma_id));
                  DbUtil.close(connection2);
               }
            }
            builder.append("Cleaned up " + count + " no Tx_Current set errors");
         } finally {
            DbUtil.close(connection2);
            DbUtil.close(connection);
         }
      }

   }

   protected void showTxCurrentText(ResultSet resultSet, int x, StringBuilder builder) throws SQLException {
      builder.append(String.format(
            "%-4d Rel_Link_id = %-8d GAMMA_1 = %-8d  GAMMA_2 = %-8d Tx_Current_1 = %-2d Tx_Current_2 = %-2d Transaction_ID_1 = %-8d Transaction_ID_2 = %-8d\n Branch_Id = %-5d\n",
            x, resultSet.getInt("rel_link_id"), resultSet.getInt("gamma_id_1"),
            resultSet.getInt("gamma_id_2"), resultSet.getInt("tx_current_1"), resultSet.getInt("tx_current_2"),
            resultSet.getInt("tran_id_1"), resultSet.getInt("tran_id_2"), resultSet.getInt("branch_id")));
   }
}
