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
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class CopyTransactionDataToBaseline extends AbstractBlam {
   private static final String COPY_TX_DATA =
         "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current) SELECT ?, gamma_id, mod_type, tx_current FROM osee_txs WHERE transaction_id = ?";

   @Override
   public String getName() {
      return "Copy TransactionData To Baseline";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      Branch branch = variableMap.getBranch("Branch");
      int txNumber = Integer.parseInt(variableMap.getString("From Transaction Number"));
      TransactionRecord fromTransactionId = TransactionManager.getTransactionId(txNumber);
      TransactionRecord baseLineTransaction = TransactionManager.getStartEndPoint(branch).getSecond();

      ConnectionHandler.runPreparedUpdate(COPY_TX_DATA, baseLineTransaction, fromTransactionId);

   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XText\" displayName=\"From Transaction Number\" /><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" /></xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return "Copy tx data from the From Transaction to the baseline transaction of the branch";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }
}