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
import java.sql.Timestamp;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;

/**
 * Identifies and removes addressing from the transaction table that no longer addresses other tables.
 * 
 * @author Jeff C. Phillips
 * @author Theron Virgin
 */
public class CleanUpBackingData extends DatabaseHealthTask {

   private static final String CLEAN_UP_JOIN_TABLE = "DELETE FROM osee_join_cleanup WHERE query_id = ?";
   private static final String NOT_ADDRESSESED_GAMMAS =
         "INSERT INTO osee_join_cleanup (query_id, gamma_id, transaction_id, insert_time) ((SELECT ?, gamma_id, 0, ? FROM osee_Define_artifact_version UNION SELECT ?, gamma_id, 0, ? FROM osee_Define_attribute UNION SELECT ?, gamma_id, 0, ? FROM osee_Define_rel_link) MINUS SELECT DISTINCT ?, gamma_id, 0, ? FROM osee_define_txs)";
   private static final String NOT_ADDRESSESED_TRANSACTIONS =
         "INSERT INTO osee_join_cleanup (query_id, gamma_id, transaction_id, insert_time) (SELECT DISTINCT ?, 0, transaction_id, ? FROM osee_Define_tx_details MINUS SELECT ?, 0, transaction_id, ? FROM osee_Define_txs)";
   private static final String REMOVE_GAMMAS_ARTIFACT =
         "DELETE FROM osee_define_artifact_version WHERE gamma_id IN (SELECT gamma_id FROM osee_join_cleanup WHERE query_id = ?)";
   private static final String REMOVE_GAMMAS_ATTRIBUTE =
         "DELETE FROM osee_define_attribute WHERE gamma_id IN (SELECT gamma_id FROM osee_join_cleanup WHERE query_id = ?)";
   private static final String REMOVE_GAMMAS_RELATIONS =
         "DELETE FROM osee_define_rel_link WHERE gamma_id IN (SELECT gamma_id FROM osee_join_cleanup WHERE query_id = ?)";
   private static final String REMOVE_NOT_ADDRESSED_TRANSACTIONS =
         "DELETE FROM osee_define_tx_details WHERE transaction_id IN (SELECT transaction_id FROM osee_join_cleanup WHERE query_id = ?)";
   private static final String[] GET_NOT_ADDRESSED_DATA =
         {"SELECT gamma_id FROM osee_join_cleanup WHERE query_id = ? AND gamma_id > 0",
               "SELECT transaction_id FROM osee_join_cleanup WHERE query_id = ? AND transaction_id > 0"};

   private static final String[] COLUMN_HEADER = {"Gamma Id", "Transaction Id"};
   private static final String[] QUERY_RESULT = {"gamma_id", "transaction_id"};
   private static final int GAMMA = 0;
   private static final int TRANSACTION = 1;

   private int joinId = 0;
   private long time;
   private static final long TWO_HOURS = 7200000;

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
      if (verify) {
         if (joinId != 0) {
            ConnectionHandler.runPreparedUpdate(CLEAN_UP_JOIN_TABLE, joinId);
         }
         joinId = 0;
         time = System.currentTimeMillis();
      }
      monitor.worked(5);

      if (joinId == 0 || (System.currentTimeMillis() - time) > TWO_HOURS) {
         joinId = ArtifactLoader.getNewQueryId();
         Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();
         ConnectionHandler.runPreparedUpdate(NOT_ADDRESSESED_GAMMAS, joinId, insertTime, joinId, insertTime, joinId,
               insertTime, joinId, insertTime);
         monitor.worked(25);
         if (monitor.isCanceled()) return;
         ConnectionHandler.runPreparedUpdate(NOT_ADDRESSESED_TRANSACTIONS, joinId, insertTime, joinId, insertTime);
         monitor.worked(25);
      }
      if (monitor.isCanceled()) return;

      StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      displayData(GAMMA, sbFull, builder, verify);
      monitor.worked(20);
      displayData(TRANSACTION, sbFull, builder, verify);
      monitor.worked(20);

      if (monitor.isCanceled()) return;

      if (fix) {
         ConnectionHandler.runPreparedUpdate(REMOVE_GAMMAS_ARTIFACT, joinId);
         monitor.worked(5);
         ConnectionHandler.runPreparedUpdate(REMOVE_GAMMAS_ATTRIBUTE, joinId);
         monitor.worked(5);
         ConnectionHandler.runPreparedUpdate(REMOVE_GAMMAS_RELATIONS, joinId);
         monitor.worked(5);
         ConnectionHandler.runPreparedUpdate(REMOVE_NOT_ADDRESSED_TRANSACTIONS, joinId);
         monitor.worked(5);
         ConnectionHandler.runPreparedUpdate(CLEAN_UP_JOIN_TABLE, joinId);
         joinId = 0;
      }

      if (showDetails) {
         sbFull.append(AHTML.endMultiColumnTable());
         XResultData rd = new XResultData(SkynetActivator.getLogger());
         rd.addRaw(sbFull.toString());
         rd.report(getVerifyTaskName(), Manipulations.RAW_HTML);
      }
   }

   private void displayData(int x, StringBuffer sbFull, StringBuilder builder, boolean verify) throws SQLException {
      int count = 0;
      ConnectionHandlerStatement chStmt = null;
      ResultSet resultSet = null;
      sbFull.append(AHTML.addHeaderRowMultiColumnTable(new String[] {COLUMN_HEADER[x]}));
      sbFull.append(AHTML.addRowSpanMultiColumnTable(COLUMN_HEADER[x] + "'s with no TXS addressing", 1));
      try {
         chStmt = ConnectionHandler.runPreparedQuery(GET_NOT_ADDRESSED_DATA[x], joinId);
         resultSet = chStmt.getRset();
         while (resultSet.next()) {
            count++;
            sbFull.append(AHTML.addRowMultiColumnTable(new String[] {resultSet.getString(QUERY_RESULT[x])}));
         }
      } finally {
         DbUtil.close(chStmt);
      }
      builder.append(verify ? "Found " : "Fixed ");
      builder.append(count);
      builder.append(" ");
      builder.append(COLUMN_HEADER[x]);
      builder.append("'s with no TXS addressing\n");
   }

   protected void finalize() throws Throwable {
      try {
         if (joinId != 0) {
            ConnectionHandler.runPreparedUpdate(CLEAN_UP_JOIN_TABLE, joinId);
         }
      } finally {
         super.finalize();

      }
   }
}
