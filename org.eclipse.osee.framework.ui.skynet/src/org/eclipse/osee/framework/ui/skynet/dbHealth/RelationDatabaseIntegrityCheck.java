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
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;

/**
 * @author Jeff C. Phillips
 * @author Theron Virgin
 */
public class RelationDatabaseIntegrityCheck extends DatabaseHealthTask {

   private static final String NO_ADDRESSING_ARTIFACTS_A =
         "SELECT ?, tx1.gamma_id, tx1.transaction_id, Current_Timestamp, 0 FROM osee_define_txs tx1, osee_define_tx_details td1, osee_define_rel_link rel1 WHERE td1.transaction_id = tx1.transaction_id AND tx1.gamma_id = rel1.gamma_id AND not exists (select 'x' from osee_define_txs tx2, osee_define_tx_details td2, osee_define_artifact_version av1 where td1.branch_id = td2.branch_id and td2.transaction_id = tx2.transaction_id and tx2.gamma_id = av1.gamma_id and av1.art_id = rel1.a_art_id)";

   private static final String NO_ADDRESSING_ARTIFACTS_B =
         "SELECT ?, tx1.gamma_id, tx1.transaction_id, Current_Timestamp, 0 from osee_define_txs tx1, osee_define_tx_details td1, osee_define_rel_link rel1 where td1.transaction_id = tx1.transaction_id AND tx1.gamma_id = rel1.gamma_id AND not exists (select 'x' from osee_define_txs tx2, osee_define_tx_details td2, osee_define_artifact_version av1 where td1.branch_id = td2.branch_id and td2.transaction_id = tx2.transaction_id and tx2.gamma_id = av1.gamma_id and av1.art_id = rel1.b_art_id)";

   private static final String DELETED_A_ARTIFACTS =
         "SELECT ?, tx1.gamma_id, tx1.transaction_id, Current_Timestamp, tx2.transaction_id from osee_Define_txs tx1, osee_Define_txs tx2, osee_Define_tx_details td1, osee_Define_tx_details td2, osee_Define_rel_link rl1, osee_define_artifact_version av1 WHERE tx1.transaction_id = td1.transaction_id and tx1.gamma_id = rl1.gamma_id and tx1.tx_current = 1 and td1.branch_id = td2.branch_id and td2.transaction_id = tx2.transaction_id and tx2.gamma_id = av1.gamma_id and tx2.tx_current = 2 and av1.art_id = rl1.a_art_id";

   private static final String DELETED_B_ARTIFACTS =
         "SELECT ?, tx1.gamma_id, tx1.transaction_id, Current_Timestamp, tx2.transaction_id from osee_Define_txs tx1, osee_Define_txs tx2, osee_Define_tx_details td1, osee_Define_tx_details td2, osee_Define_rel_link rl1, osee_define_artifact_version av1 WHERE tx1.transaction_id = td1.transaction_id and tx1.gamma_id = rl1.gamma_id and tx1.tx_current = 1 and td1.branch_id = td2.branch_id and td2.transaction_id = tx2.transaction_id and tx2.gamma_id = av1.gamma_id and tx2.tx_current = 2 and av1.art_id = rl1.b_art_id";

   private static final String SHOW_RESULTS =
         "SELECT rel.rel_link_id, txs.gamma_id, txs.transaction_id, det.branch_id, rel.a_art_id, rel.b_art_id, jc.argument_3 FROM osee_define_tx_details det, osee_define_txs txs, osee_define_rel_link rel, osee_join_cleanup jc WHERE jc.query_id = ? AND jc.transaction_id = txs.transaction_id AND jc.gamma_id = txs.gamma_id AND det.transaction_id = txs.transaction_id AND txs.gamma_id = rel.gamma_id";

   private static final String INSERT_JOIN =
         "INSERT INTO osee_join_cleanup (query_id, gamma_id, transaction_id, insert_time, argument_3) VALUES (?, ?, ?, ?, ?)";

   private static final String DELETE_FROM_TXS =
         "DELETE FROM osee_define_txs where (gamma_id, transaction_id) in (SELECT gamma_id, transaction_id FROM osee_join_cleanup WHERE query_id = ?)";
   private static final String UPDATE_TXS =
         "UPDATE osee_define_txs SET tx_current = 0 WHERE (gamma_id, transaction_id) in (SELECT gamma_id, transaction_id FROM osee_join_cleanup WHERE query_id = ?)";
   private static final String INSERT_TXS =
         "INSERT INTO osee_define_txs (gamma_id, transaction_id, tx_current, mod_type) SELECT join.gamma_id, join.argument_3, " + TxChange.ARTIFACT_DELETED.getValue() + ", " + ModificationType.ARTIFACT_DELETED.getValue() + " FROM osee_join_cleanup WHERE query_id = ?)";

   private static final String CLEAN_UP_JOIN_TABLE = "DELETE FROM osee_join_cleanup WHERE query_id = ?";

   private int joinIdToDelete1 = 0;
   private int joinIdToDelete2 = 0;
   private int joinIdToUpdate1 = 0;
   private int joinIdToUpdate2 = 0;
   private long time;
   private static final long TWO_HOURS = 7200000;

   private static final String[] columnHeaders =
         new String[] {"Rel Link ID", "Gamma Id", "Transaction Id", "Branch_id", "A Art Id", "B Art Id",
               "Transaction ID of Deleted Artifact"};

   private static final String[] DESCRIPTION =
         {" Relation Links with non existant Artifacts on the Branch",
               " Relation Links with deleted Artifacts on the Branch"};

   private static final String[] HEADER =
         {"Relation Links that have artifacts that don't exist on the branch",
               "Relation Links that have artifacts that are deleted on the branch"};

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthTask#getFixTaskName()
    */
   @Override
   public String getFixTaskName() {
      return "Fix Relation Integrity Errors";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthTask#getVerifyTaskName()
    */
   @Override
   public String getVerifyTaskName() {
      return "Check for Relation Integrity Errors";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthTask#run(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.core.runtime.IProgressMonitor, org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthTask.Operation, java.lang.StringBuilder, boolean)
    */
   @Override
   public void run(BlamVariableMap variableMap, IProgressMonitor monitor, Operation operation, StringBuilder builder, boolean showDetails) throws Exception {
      StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      boolean fix = operation == Operation.Fix;
      boolean verify = !fix;

      try {
         if (verify) {
            if (joinIdToDelete1 != 0 || joinIdToUpdate1 != 0 || joinIdToDelete2 != 0 || joinIdToUpdate2 != 0) {
               cleanupTables();
            }
            joinIdToDelete1 = 0;
            joinIdToDelete2 = 0;
            joinIdToUpdate1 = 0;
            joinIdToUpdate2 = 0;
            time = System.currentTimeMillis();
         }

         if (joinIdToDelete1 == 0 || joinIdToDelete1 == 0 || joinIdToUpdate1 == 0 || (System.currentTimeMillis() - time) > TWO_HOURS) {
            joinIdToDelete1 = ArtifactLoader.getNewQueryId();
            joinIdToDelete2 = ArtifactLoader.getNewQueryId();
            joinIdToUpdate1 = ArtifactLoader.getNewQueryId();
            loadJoinTable(NO_ADDRESSING_ARTIFACTS_A, joinIdToDelete1);
            monitor.worked(10);
            if (monitor.isCanceled()) return;
            loadJoinTable(NO_ADDRESSING_ARTIFACTS_B, joinIdToDelete2);
            monitor.worked(10);
            if (monitor.isCanceled()) return;
            loadJoinTable(DELETED_A_ARTIFACTS, joinIdToUpdate1);
            monitor.worked(10);
         }

         sbFull.append(AHTML.beginMultiColumnTable(100, 1));
         sbFull.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
         displayData(0, sbFull, builder, verify, joinIdToDelete1, " : A side");
         displayData(0, sbFull, builder, verify, joinIdToDelete2, " : B side");
         displayData(1, sbFull, builder, verify, joinIdToUpdate1, " : A side");

         if (fix) {
            //Delete the guys that aren't there and update the currents for the others.
            ConnectionHandler.runPreparedUpdate(DELETE_FROM_TXS, joinIdToDelete1);
            ConnectionHandler.runPreparedUpdate(DELETE_FROM_TXS, joinIdToDelete2);

            ConnectionHandler.runPreparedUpdate(UPDATE_TXS, joinIdToUpdate1);
            ConnectionHandler.runPreparedUpdate(INSERT_TXS, joinIdToUpdate1);
         }

         if (joinIdToUpdate2 == 0 || (System.currentTimeMillis() - time) > TWO_HOURS) {
            joinIdToUpdate2 = ArtifactLoader.getNewQueryId();
            if (monitor.isCanceled()) return;
            loadJoinTable(DELETED_B_ARTIFACTS, joinIdToUpdate2);
            monitor.worked(10);
            displayData(1, sbFull, builder, verify, joinIdToUpdate2, " : B side");
            if (fix) {
               ConnectionHandler.runPreparedUpdate(UPDATE_TXS, joinIdToUpdate2);
               ConnectionHandler.runPreparedUpdate(INSERT_TXS, joinIdToUpdate2);
            }
         }

         if (fix) {
            cleanupTables();
         }

      } finally {
         if (showDetails) {
            sbFull.append(AHTML.endMultiColumnTable());
            XResultData rd = new XResultData(SkynetActivator.getLogger());
            rd.addRaw(sbFull.toString());
            rd.report(getVerifyTaskName(), Manipulations.RAW_HTML);
         }
      }
   }

   private void displayData(int x, StringBuffer sbFull, StringBuilder builder, boolean verify, int joinId, String text) throws SQLException {
      int count = 0;
      ConnectionHandlerStatement chStmt = null;
      ResultSet resultSet = null;
      sbFull.append(AHTML.addRowSpanMultiColumnTable(HEADER[x] + text, columnHeaders.length));
      try {
         chStmt = ConnectionHandler.runPreparedQuery(SHOW_RESULTS, joinId);
         resultSet = chStmt.getRset();
         while (resultSet.next()) {
            count++;
            sbFull.append(AHTML.addRowMultiColumnTable(new String[] {resultSet.getString("rel_link_id"),
                  resultSet.getString("gamma_id"), resultSet.getString("transaction_id"),
                  resultSet.getString("branch_id"), resultSet.getString("a_art_id"), resultSet.getString("b_art_id"),
                  resultSet.getString("argument_3")}));
         }

      } finally {
         DbUtil.close(chStmt);
      }
      builder.append(verify ? "Found " : "Fixed ");
      builder.append(count);
      builder.append(" ");
      builder.append(DESCRIPTION[x]);
      builder.append(text);
      builder.append("\n");
   }

   protected void finalize() throws Throwable {
      try {
         if (joinIdToDelete1 != 0 || joinIdToUpdate1 != 0 || joinIdToDelete2 != 0 || joinIdToUpdate2 != 0) {
            cleanupTables();
         }
      } finally {
         super.finalize();

      }
   }

   private void cleanupTables() throws SQLException {
      ConnectionHandler.runPreparedUpdate(CLEAN_UP_JOIN_TABLE, joinIdToDelete1);
      ConnectionHandler.runPreparedUpdate(CLEAN_UP_JOIN_TABLE, joinIdToDelete2);
      ConnectionHandler.runPreparedUpdate(CLEAN_UP_JOIN_TABLE, joinIdToUpdate1);
      ConnectionHandler.runPreparedUpdate(CLEAN_UP_JOIN_TABLE, joinIdToUpdate2);
   }

   private void loadJoinTable(String sql, int queryId) throws SQLException {
      ConnectionHandlerStatement chStmt = null;
      ResultSet resultSet = null;
      Set<Object[]> insertParameters = new HashSet<Object[]>();
      try {
         chStmt = ConnectionHandler.runPreparedQuery(sql, queryId);
         resultSet = chStmt.getRset();
         while (resultSet.next()) {
            insertParameters.add(new Object[] {resultSet.getInt(1), resultSet.getInt(2), resultSet.getInt(3),
                  resultSet.getTimestamp(4), resultSet.getInt(5)});
         }
         ConnectionHandler.runPreparedUpdateBatch(INSERT_JOIN, insertParameters);
      } finally {
         DbUtil.close(chStmt);
      }
   }

}
