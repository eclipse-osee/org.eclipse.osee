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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionType;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Ryan D. Brooks
 */
public class CopyTransactionDataToBaseline extends AbstractBlam {
   private static final String COPY_TX_DATA =
         "INSERT INTO osee_define_txs (transaction_id, gamma_id, tx_type) SELECT ?, gamma_id, ? FROM osee_define_txs WHERE transaction_id = ?";
   private static final TransactionIdManager transactionIdManager = TransactionIdManager.getInstance();

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      Branch branch = variableMap.getBranch("Branch");
      int txNumber = Integer.parseInt(variableMap.getString("From Transaction Number"));
      TransactionId fromTransactionId = transactionIdManager.getPossiblyEditableTransactionId(txNumber);
      TransactionId baseLineTransaction = transactionIdManager.getStartEndPoint(branch).getValue();

      ConnectionHandler.runPreparedUpdate(COPY_TX_DATA, SQL3DataType.INTEGER, baseLineTransaction,
            SQL3DataType.INTEGER, TransactionType.BRANCHED, SQL3DataType.INTEGER, fromTransactionId);

   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XText\" displayName=\"From Transaction Number\" /><XWidget xwidgetType=\"XBranchListViewer\" displayName=\"Branch\" /></xWidgets>";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getDescriptionUsage()
    */
   public String getDescriptionUsage() {
      return "Copy tx data from the From Transaction to the baseline transaction of the branch";
   }
}