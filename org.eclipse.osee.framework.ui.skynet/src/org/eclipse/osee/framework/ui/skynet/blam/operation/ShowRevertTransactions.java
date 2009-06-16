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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;

/**
 * @author Theron Virgin
 */
public class ShowRevertTransactions extends AbstractBlam {
   private static final String GET_REVERT_TRANSACTIONS =
         "Select DISTINCT Branch_id, Value, txs.transaction_id, Time FROM osee_removed_txs txs, osee_tx_details det, osee_attribute attr, osee_txs txs1 WHERE txs.transaction_id = det.transaction_id AND det.author = attr.art_id AND attr.attr_type_id = 30 AND attr.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 ORDER BY time";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getName()
    */
   @Override
   public String getName() {
      return "Show Revert Transactions";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));

      sbFull.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Branch ID", "User", "Transaction_ID", "Date"}));

      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      chStmt.runPreparedQuery(GET_REVERT_TRANSACTIONS);
      while (chStmt.next()) {
         sbFull.append(AHTML.addRowMultiColumnTable(new String[] {String.valueOf(chStmt.getInt("branch_id")),
               chStmt.getString("value"), String.valueOf(chStmt.getInt("transaction_id")),
               chStmt.getDate("time").toString()}));
      }
      sbFull.append(AHTML.endMultiColumnTable());
      XResultData rd = new XResultData();
      rd.addRaw(sbFull.toString());
      rd.report("Revert Transactions", Manipulations.RAW_HTML);
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

   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }
}
