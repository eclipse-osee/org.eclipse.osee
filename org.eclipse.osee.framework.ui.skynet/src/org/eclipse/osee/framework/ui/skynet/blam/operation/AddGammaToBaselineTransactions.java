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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionType;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Ryan D. Brooks
 */
public class AddGammaToBaselineTransactions extends AbstractBlam {
   public static final Pattern gammaPattern = Pattern.compile("\\d+");

   public static final String INSERT_GAMMA =
         "INSERT INTO osee_define_txs(transaction_id, gamma_id) SELECT txd2.transaction_id, ? FROM osee_define_branch br1, osee_define_tx_details txd2 WHERE br1.archived = 0 AND (br1.parent_branch_id = ? OR br1.branch_id = ?) AND br1.branch_id = txd2.branch_id AND EXISTS (SELECT 'x' FROM osee_define_txs txs0 WHERE txs0.transaction_id = txd2.transaction_id AND txs0.tx_type = ?) AND NOT EXISTS (SELECT 'x' FROM osee_define_txs txs4, osee_define_tx_details txd5 WHERE txs4.transaction_id = txd5.transaction_id AND txd5.branch_id = txd2.branch_id AND txs4.gamma_id = ?)";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      Branch branch = variableMap.getBranch("Branch");

      Matcher gammaIdMatcher = gammaPattern.matcher(variableMap.getString("Gamma List"));
      while (gammaIdMatcher.find()) {
         int gammaId = Integer.parseInt(gammaIdMatcher.group());
         ConnectionHandler.runPreparedUpdate(INSERT_GAMMA, SQL3DataType.INTEGER, gammaId, SQL3DataType.INTEGER,
               branch.getBranchId(), SQL3DataType.INTEGER, branch.getBranchId(), SQL3DataType.INTEGER,
               TransactionType.BRANCHED.getId(), SQL3DataType.INTEGER, gammaId);
      }
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XText\" displayName=\"Gamma List\" /><XWidget xwidgetType=\"XBranchListViewer\" displayName=\"Branch\" /></xWidgets>";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getDescriptionUsage()
    */
   public String getDescriptionUsage() {
      return "Add gamma(s) to the baseline transaction of the specified branch and its child branches that do not already address it (ignores archived branches)";
   }
}