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

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class PopulateTxsBranchIdColumn extends AbstractBlam {

   @Override
   public String getName() {
      return "Populate Txs Branch Id Column";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      String tableName = variableMap.getBoolean("Archived Table") ? "osee_txs_archived" : "osee_txs";
      if (variableMap.getBoolean("Incremental Update")) {
         incrementallySetBranchId(tableName);
      } else {
         fullySetBranchId(tableName);
      }
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Incremental Update\" /><XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Archived Table\" /></xWidgets>";
   }

   private void fullySetBranchId(String tableName) throws OseeCoreException {
      int blockSize = 200000;
      String sql =
            String.format(
                  "update %s txs set branch_id = (select branch_id from osee_tx_details txd where txs.transaction_id = txd.transaction_id) where transaction_id > ? and transaction_id < ?",
                  tableName);

      for (int i = 0; i < 1000000; i += blockSize) {
         println("> " + i + " and < " + (i + blockSize + 1));
         ConnectionHandler.runPreparedUpdate(sql, i, i + blockSize + 1);
      }
   }

   private void incrementallySetBranchId(String tableName) throws OseeCoreException {
      String sql = "select branch_id, transaction_id from osee_tx_details";
      HashMap<Integer, Integer> branchMap = new HashMap<Integer, Integer>(600000);

      IOseeStatement chStmt = databaseService.getStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
      try {
         chStmt.runPreparedQuery(10000, sql);
         while (chStmt.next()) {
            branchMap.put(chStmt.getInt("transaction_id"), chStmt.getInt("branch_id"));
         }
      } finally {
         chStmt.close();
      }
      println("# of transactions: " + branchMap.size());

      sql = String.format("select transaction_id, branch_id from %s where branch_id is null", tableName);
      int counter = 0;
      try {
         chStmt.runPreparedQuery(10000, sql);
         while (chStmt.next()) {
            Integer branchId = branchMap.get(chStmt.getInt("transaction_id"));
            if (branchId == null) {
               println("map not not have branch id for transaction id: " + chStmt.getInt("transaction_id"));
            } else {
               chStmt.updateObject("branch_id", branchId);
               chStmt.updateRow();
               counter++;
            }
         }
      } finally {
         chStmt.close();
      }

      println("Updated " + counter + " rows");
   }
}