/*
 * Created on Jan 29, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.dbHealth.HealthHelper;

/**
 * @author Theron Virgin
 */
public class ShowRevertTransactions extends AbstractBlam {
   private static final String GET_REVERT_TRANSACTIONS =
         "Select DISTINCT Branch_id, Value, txs.transaction_id, Time FROM osee_removed_txs txs, osee_tx_details det, osee_attribute attr, osee_txs txs1 WHERE txs.transaction_id = det.transaction_id AND det.author = attr.art_id AND attr.attr_type_id = 30 AND attr.gamma_id = txs1.gamma_id AND txs1.tx_current = 1";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));

      sbFull.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Branch ID", "User", "Transaction_ID", "Time"}));

      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      chStmt.runPreparedQuery(GET_REVERT_TRANSACTIONS);
      while (chStmt.next()) {
         sbFull.append(AHTML.addRowMultiColumnTable(new String[] {String.valueOf(chStmt.getInt("branch_id")),
               chStmt.getString("value"), String.valueOf(chStmt.getInt("transaction_id")),
               chStmt.getTime("time").toString()}));
      }
      HealthHelper.endTable(sbFull, "Revert Transactions");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   @Override
   public String getXWidgetsXml() {
      return "<xWidgets></xWidgets>";
   }
}
