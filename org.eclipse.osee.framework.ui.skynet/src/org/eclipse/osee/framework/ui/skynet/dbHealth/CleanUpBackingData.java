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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;

/**
 * Identifies and removes addressing from the transaction table that no longer addresses other tables.
 * 
 * @author Theron Virgin
 */
public class CleanUpBackingData extends DatabaseHealthTask {

   private static final String NOT_ADDRESSESED_GAMMAS =
         "(SELECT gamma_id FROM osee_Define_artifact_version UNION SELECT gamma_id FROM osee_Define_attribute UNION SELECT gamma_id FROM osee_Define_rel_link) MINUS SELECT gamma_id FROM osee_define_txs";
   private static final String NOT_ADDRESSESED_TRANSACTIONS =
         "SELECT DISTINCT transaction_id FROM osee_Define_tx_details MINUS SELECT transaction_id FROM osee_Define_txs";
   private static final String REMOVE_GAMMAS_ARTIFACT = "DELETE FROM osee_define_artifact_version WHERE gamma_id = ?";
   private static final String REMOVE_GAMMAS_ATTRIBUTE = "DELETE FROM osee_define_attribute WHERE gamma_id = ?";
   private static final String REMOVE_GAMMAS_RELATIONS = "DELETE FROM osee_define_rel_link WHERE gamma_id = ?";
   private static final String REMOVE_NOT_ADDRESSED_TRANSACTIONS =
         "DELETE FROM osee_define_tx_details WHERE transaction_id = ?";

   private static final String[] COLUMN_HEADER = {"Gamma Id", "Transaction Id"};
   private static final int GAMMA = 0;
   private static final int TRANSACTION = 1;

   private Set<Integer> gammas = null;
   private Set<Integer> transactions = null;

   @Override
   public String getFixTaskName() {
      return "Fix Data with no TXS Addressing";
   }

   @Override
   public String getVerifyTaskName() {
      return "Check for Data with no TXS Addressing";
   }

   @Override
   public void run(BlamVariableMap variableMap, IProgressMonitor monitor, Operation operation, StringBuilder builder, boolean showDetails) throws Exception {
      boolean fix = operation == Operation.Fix;
      boolean verify = !fix;
      monitor.beginTask(fix ? "Deleting Data with no TXS addressing" : "Checking For Data with no TXS addressing", 100);
      monitor.worked(5);

      if (verify || gammas == null || transactions == null) {
         gammas = new HashSet<Integer>();
         transactions = new HashSet<Integer>();
         ConnectionHandlerStatement chStmt = null;
         ResultSet resultSet = null;
         try {
            chStmt = ConnectionHandler.runPreparedQuery(NOT_ADDRESSESED_GAMMAS);
            resultSet = chStmt.getRset();
            while (resultSet.next()) {
               gammas.add(resultSet.getInt("gamma_id"));
            }
         } finally {
            DbUtil.close(chStmt);
         }
         monitor.worked(25);
         if (monitor.isCanceled()) return;
         try {
            chStmt = ConnectionHandler.runPreparedQuery(NOT_ADDRESSESED_TRANSACTIONS);
            resultSet = chStmt.getRset();
            while (resultSet.next()) {
               transactions.add(resultSet.getInt("transaction_id"));
            }
         } finally {
            DbUtil.close(chStmt);
         }
         monitor.worked(25);
      }
      if (monitor.isCanceled()) return;

      StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      displayData(GAMMA, sbFull, builder, verify, gammas);
      monitor.worked(20);
      displayData(TRANSACTION, sbFull, builder, verify, transactions);
      monitor.worked(20);

      if (monitor.isCanceled()) return;

      if (fix) {
         HashSet<Object[]> insertParameters = new HashSet<Object[]>();
         for (Integer value : gammas) {
            insertParameters.add(new Object[] {value.intValue()});
         }
         ConnectionHandler.runPreparedUpdateBatch(REMOVE_GAMMAS_ARTIFACT, insertParameters);
         monitor.worked(5);
         ConnectionHandler.runPreparedUpdateBatch(REMOVE_GAMMAS_ATTRIBUTE, insertParameters);
         monitor.worked(5);
         ConnectionHandler.runPreparedUpdateBatch(REMOVE_GAMMAS_RELATIONS, insertParameters);
         monitor.worked(5);
         insertParameters.clear();
         for (Integer value : transactions) {
            insertParameters.add(new Object[] {value.intValue()});
         }
         ConnectionHandler.runPreparedUpdateBatch(REMOVE_NOT_ADDRESSED_TRANSACTIONS, insertParameters);
         monitor.worked(5);
         gammas = null;
         transactions = null;
      }

      if (showDetails) {
         sbFull.append(AHTML.endMultiColumnTable());
         XResultData rd = new XResultData(SkynetActivator.getLogger());
         rd.addRaw(sbFull.toString());
         rd.report(getVerifyTaskName(), Manipulations.RAW_HTML);
      }
   }

   private void displayData(int x, StringBuffer sbFull, StringBuilder builder, boolean verify, Set<Integer> set) throws SQLException {
      int count = 0;
      sbFull.append(AHTML.addHeaderRowMultiColumnTable(new String[] {COLUMN_HEADER[x]}));
      sbFull.append(AHTML.addRowSpanMultiColumnTable(COLUMN_HEADER[x] + "'s with no TXS addressing", 1));
      for (Integer value : set) {
         count++;
         sbFull.append(AHTML.addRowMultiColumnTable(new String[] {value.toString()}));
      }
      builder.append(verify ? "Found " : "Fixed ");
      builder.append(count);
      builder.append(" ");
      builder.append(COLUMN_HEADER[x]);
      builder.append("'s with no TXS addressing\n");
   }
}
