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
package org.eclipse.osee.framework.ui.skynet.dbHealth;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;

/**
 * @author Jeff C. Phillips
 */
public class RemoveAttributesWithoutArtifacts extends DatabaseHealthTask {

   private static final String SELECT_ATTRIBUTES_WITH_NO_ARTIFACTS =
         "select tx1.transaction_id, tx1.gamma_id, td1.branch_id, att1.art_id, att1.attr_id from osee_txs tx1, osee_tx_Details td1, osee_attribute att1 where td1.transaction_id = tx1.transaction_id AND tx1.gamma_id = att1.gamma_id AND not exists (select 'x' from osee_txs tx2, osee_tx_details td2, osee_artifact_version av1 where td1.branch_id = td2.branch_id and td2.transaction_id = tx2.transaction_id and tx2.gamma_id = av1.gamma_id AND av1.art_id = att1.art_id) ";
   private static final String DELETE_ATTRIBUTES = "delete from osee_txs where transaction_id = ? and gamma_id = ?";
   private static final String[] columnHeaders =
         new String[] {"Transaction id", "Gamma Id", "Branch_id", "Art Id", "Attribute Id"};
   private static ArrayList<Integer[]> datas = new ArrayList<Integer[]>();
   private static final String DESCRIPTION = "Attributes the do not have artifacts on the branch where they exist.";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthTask#getFixTaskName()
    */
   @Override
   public String getFixTaskName() {
      return "Fix Attributes with no Artifacts";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthTask#getVerifyTaskName()
    */
   @Override
   public String getVerifyTaskName() {
      return "Check for Attributes with no Artifacts";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthTask#run(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.core.runtime.IProgressMonitor, org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthTask.Operation, java.lang.StringBuilder, boolean)
    */
   @Override
   public void run(VariableMap variableMap, IProgressMonitor monitor, Operation operation, StringBuilder builder, boolean showDetails) throws Exception {
      StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      sbFull.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
      boolean fix = operation == Operation.Fix;
      boolean verify = !fix;

      try {
         if (verify) {
            loadData();
            displayData(sbFull, builder, verify);
         }

         if (fix) {
            if (datas.isEmpty()) {
               loadData();
            }
            fixAttributes();
            displayData(sbFull, builder, verify);
         }
      } finally {
         if (showDetails) {
            sbFull.append(AHTML.endMultiColumnTable());
            XResultData rd = new XResultData();
            rd.addRaw(sbFull.toString());
            rd.report(getVerifyTaskName(), Manipulations.RAW_HTML);
         }
      }
   }

   private void loadData() throws OseeDataStoreException {
      datas.clear();

      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(SELECT_ATTRIBUTES_WITH_NO_ARTIFACTS);
         int transactionNumber;
         int gammaIdNumber;

         while (chStmt.next()) {
            transactionNumber = chStmt.getInt("transaction_id");
            gammaIdNumber = chStmt.getInt("gamma_id");
            datas.add(new Integer[] {transactionNumber, gammaIdNumber, chStmt.getInt("branch_id"),
                  chStmt.getInt("art_id"), chStmt.getInt("attr_id")});
         }
      } finally {
         chStmt.close();
      }

   }

   private void displayData(StringBuffer sbFull, StringBuilder builder, boolean verify) {
      sbFull.append(AHTML.addRowSpanMultiColumnTable(DESCRIPTION, columnHeaders.length));
      for (Integer[] data : datas) {
         sbFull.append(AHTML.addRowMultiColumnTable(new String[] {String.valueOf(data[0]), String.valueOf(data[1]),
               String.valueOf(data[2]), String.valueOf(data[3]), String.valueOf(data[4])}));
      }
      builder.append(verify ? "Found " : "Fixed ");
      builder.append(datas.size());
      builder.append(" Attributes that have no Artifacts\n");
   }

   private void fixAttributes() throws OseeDataStoreException {
      List<Object[]> insertParameters = new LinkedList<Object[]>();
      for (Integer[] data : datas) {
         insertParameters.add(new Object[] {data[0], data[1]});
      }
      ConnectionHandler.runBatchUpdate(DELETE_ATTRIBUTES, insertParameters);
   }
}
