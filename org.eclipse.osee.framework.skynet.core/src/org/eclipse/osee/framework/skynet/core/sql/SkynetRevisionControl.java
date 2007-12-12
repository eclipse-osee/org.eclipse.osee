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
package org.eclipse.osee.framework.skynet.core.sql;

import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase;

public class SkynetRevisionControl {
   public static final String SELECT_REVISION =
         "\n\nSELECT DISTINCT branch_id as branchId, " + "(SELECT MIN(transaction_id) FROM ( " + SkynetDatabase.TRANSACTIONS_TABLE + " txs1)" + " WHERE (txs2.branch_id = txs1.branch_id)) as minTX, " + "(SELECT MAX(transaction_id) FROM ( " + SkynetDatabase.TRANSACTIONS_TABLE + " txs1)" + " WHERE (txs2.branch_id = txs1.branch_id)) as maxTX, " + "(SELECT branch_name FROM ( " + SkynetDatabase.BRANCH_TABLE + " branchTable)" + " WHERE  (txs2.branch_id = branchTable.branch_id)) as branchName" + " FROM " + SkynetDatabase.TRANSACTIONS_TABLE + " txs2 \n\n";

   private static final SkynetRevisionControl instance = new SkynetRevisionControl();

   private SkynetRevisionControl() {
   }

   static SkynetRevisionControl getInstance() {
      return instance;
   }

   public String getValidGammaIds(int branchId, int revision) {
      return "\n\nSELECT gamma_id as valid_gammas FROM " + SkynetDatabase.TRANSACTIONS_TABLE + " transactionTable " + "WHERE (transactionTable.branch_id = " + branchId + " AND transactionTable.transaction_id <= " + revision + ")\n\n";
   }

   public String getBranchAndRevisionData() {
      String validMinTransactionAlias = "minTX";
      String validMaxTransactionAlias = "maxTX";
      String branchNameAlias = "branchName";
      String branchIdAlias = "branchId";
      return "\n\nSELECT DISTINCT branch_id as " + branchIdAlias + ", " + "(SELECT MIN(transaction_id) FROM ( " + SkynetDatabase.TRANSACTIONS_TABLE + " txs1)" + " WHERE (txs2.branch_id = txs1.branch_id)) as " + validMinTransactionAlias + ", " + "(SELECT MAX(transaction_id) FROM ( " + SkynetDatabase.TRANSACTIONS_TABLE + " txs1)" + " WHERE (txs2.branch_id = txs1.branch_id)) as " + validMaxTransactionAlias + ", " + "(SELECT branch_name FROM ( " + SkynetDatabase.BRANCH_TABLE + " branchTable)" + " WHERE  (txs2.branch_id = branchTable.branch_id)) as " + branchNameAlias + " FROM " + SkynetDatabase.TRANSACTIONS_TABLE + " txs2 \n\n";
   }
}
