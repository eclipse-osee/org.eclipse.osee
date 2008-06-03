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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Andrew M Finkbeiner
 */
public class UpdateCurrentColumn extends AbstractBlam {

   private static final String SELECT_ATTRIBUTES_TO_UPDATE =
         "SELECT branch_id, maxt, txs2.gamma_id, atid FROM osee_define_attribute att2,  osee_define_txs txs2, (SELECT MAX(txs1.transaction_id) AS  maxt, att1.attr_id AS atid, txd1.branch_id FROM osee_define_attribute att1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE att1.gamma_id = txs1.gamma_id and txs1.transaction_id > ? and txd1.tx_type = 0 AND txs1.transaction_id = txd1.transaction_id GROUP BY att1.attr_id, txd1.branch_id) new_stuff WHERE atid = att2.attr_id AND att2.modification_id <> 3 AND att2.gamma_id = txs2.gamma_id and txs2.transaction_id > ? AND txs2.transaction_id = maxt";
   private static final String SELECT_ARTIFACTS_TO_UPDATE =
         "SELECT branch_id, maxt, txs1.gamma_id, art_id FROM osee_define_artifact_version arv2,  osee_define_txs txs1, (SELECT MAX(txs2.transaction_id) AS maxt, arv1.art_id AS art, txd1.branch_id FROM osee_define_artifact_version arv1, osee_define_txs txs2, osee_define_tx_details txd1 WHERE arv1.gamma_id = txs2.gamma_id and txs2.transaction_id > ? and txd1.tx_type = 0 AND txs2.transaction_id = txd1.transaction_id GROUP BY arv1.art_id, txd1.branch_id) new_stuff WHERE art = arv2.art_id AND arv2.modification_id <> 3 AND arv2.gamma_id = txs1.gamma_id AND txs1.transaction_id = maxt and txs1.transaction_id > ?";
   private static final String SELECT_RELATIONS_TO_UPDATE =
         "SELECT branch_id, maxt, txs1.gamma_id, rel_id FROM osee_define_rel_link rel2, osee_define_txs txs1, (SELECT MAX(txs2.transaction_id) AS maxt, rel1.rel_link_id AS rel_id, txd1.branch_id FROM osee_define_rel_link rel1, osee_define_txs txs2, osee_define_tx_details txd1 WHERE rel1.gamma_id = txs2.gamma_id and txs2.transaction_id > ? and txd1.tx_type = 0 AND txs2.transaction_id = txd1.transaction_id GROUP BY rel1.rel_link_id, txd1.branch_id) new_stuff WHERE rel_id = rel2.rel_link_id AND rel2.modification_id <> 3 AND rel2.gamma_id = txs1.gamma_id AND txs1.transaction_id = maxt and txs1.transaction_id > ?";
   private static final String SELECT_STALE_ATTRIBUTES =
         "SELECT txsouter.gamma_id, txsouter.transaction_id FROM osee_define_txs txsouter, (SELECT txs1.gamma_id, txs1.transaction_id FROM osee_define_txs txs1, osee_define_tx_details txd1, osee_define_attribute attr1 where txd1.branch_id = ? and attr1.attr_id = ? AND txs1.tx_current = 1 AND txd1.transaction_id = txs1.transaction_id AND txs1.gamma_id = attr1.gamma_id) resulttable WHERE txsouter.transaction_id = resulttable.transaction_id AND resulttable.gamma_id = txsouter.gamma_id";
   private static final String SELECT_STALE_ARTIFACTS =
         "SELECT txsouter.gamma_id, txsouter.transaction_id FROM osee_define_txs txsouter, (SELECT txs1.gamma_id, txs1.transaction_id FROM osee_define_txs txs1, osee_define_tx_details txd1, osee_define_artifact_version art1 WHERE txd1.branch_id = ? AND art1.art_id = ? AND txs1.tx_current = 1 AND txd1.transaction_id = txs1.transaction_id AND txs1.gamma_id = art1.gamma_id) resulttable WHERE txsouter.transaction_id = resulttable.transaction_id AND resulttable.gamma_id = txsouter.gamma_id";
   private static final String SELECT_STALE_RELATIONS =
         "SELECT txsouter.gamma_id, txsouter.transaction_id FROM osee_define_txs txsouter, (SELECT txs1.gamma_id, txs1.transaction_id FROM osee_define_txs txs1, osee_define_tx_details txd1, osee_define_rel_link link1 where txd1.branch_id = ? and link1.rel_link_id = ? AND txs1.tx_current = 1 AND txd1.transaction_id = txs1.transaction_id AND txs1.gamma_id = link1.gamma_id) resulttable WHERE txsouter.transaction_id = resulttable.transaction_id AND resulttable.gamma_id = txsouter.gamma_id";
   private static final String UPDATE_TXS_CURRENT_TO_0 =
         "update osee_define_txs set tx_current = 0 where gamma_id = ? and transaction_id = ?";
   private static final String UPDATE_TXS_CURRENT_TO_1 =
         "update osee_define_txs set tx_current = 1 where gamma_id = ? and transaction_id = ?";
   private static final String SELECT_BASELINED_TRANSACTIONS =
         "SELECT txs1.gamma_id, txs1.transaction_id from osee_define_txs txs1, osee_define_tx_details txd1 where txd1.tx_type = 1 and txd1.transaction_id > ? and txd1.transaction_id = txs1.transaction_id";
   private static final String UPDATE_TX_DETAILS_NON_BASELINE_TRANSACTIONS_TO_0 =
         "UPDATE osee_Define_tx_details SET tx_type = 0 WHERE tx_type <> 1";
   private static final String UPDATE_TX_DETAILS_BASELINE_TRANSACTIONS_TO_1 =
         "UPDATE osee_Define_tx_details SET tx_type = 1 WHERE osee_comment LIKE '%New Branch%'";
   private static final String SELECT_ARTIFACT_MOD_TYPE =
         "select artv1.modification_id, txd1.transaction_id, txs1.gamma_id from osee_define_tx_details txd1, osee_define_txs txs1, osee_define_artifact_version artv1 where txd1.transaction_id > ? and txd1.TRANSACTION_ID = txs1.transaction_id and txs1.gamma_id = artv1.gamma_id";
   private static final String UPDATE_TXS_MOD_TYPE =
         "update osee_define_txs set mod_type = ? where transaction_id = ? AND gamma_id = ?";
   private static final String SELECT_ATTRIBUTE_MOD_TYPE =
         "select attr1.modification_id, txd1.transaction_id, txs1.gamma_id from osee_define_tx_details txd1, osee_define_txs txs1, osee_define_attribute attr1 where txd1.transaction_id > ? and txd1.TRANSACTION_ID = txs1.transaction_id and txs1.gamma_id = attr1.gamma_id";
   private static final String SELECT_RELATION_MOD_TYPE =
         "select rel1.modification_id, txd1.transaction_id, txs1.gamma_id from osee_define_tx_details txd1, osee_define_txs txs1, osee_define_rel_link rel1 where txd1.transaction_id > ? and txd1.TRANSACTION_ID = txs1.transaction_id and txs1.gamma_id = rel1.gamma_id";
   private static final String UPDATE_B_ORDER = "update osee_define_rel_link set b_order where gamma_id = ?";
   private static final String SELECT_B_RELATION_ORDER =
         "select rel1.rel_link_type_id,  rel1.a_art_id, txd1.branch_id, rel1.b_order, txs1.gamma_id, rel1.b_art_id, rel1.a_order_value from osee_define_tx_details txd1, osee_define_rel_link rel1, osee_define_txs txs1 where txd1.transaction_id = txs1.transaction_id and txs1.gamma_id = rel1.gamma_id and txs1.tx_current = 1 order by txd1.branch_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.a_order_value";
   private static final String SELECT_A_RELATION_ORDER =
         "select rel1.rel_link_type_id,  rel1.b_art_id, txd1.branch_id, rel1.a_order, txs1.gamma_id, rel1.a_art_id, rel1.b_order_value    from osee_define_tx_details txd1, osee_define_rel_link rel1, osee_define_txs txs1 where txd1.transaction_id = txs1.transaction_id and txs1.gamma_id = rel1.gamma_id and txs1.tx_current = 1 order by txd1.branch_id, rel1.rel_link_type_id, rel1.b_art_id, rel1.b_order_value";
   private static final String UPDATE_A_ORDER = "update osee_define_rel_link set a_order where gamma_id = ?";

   private static final String VERIFY_TX_CURRENT =
         "SELECT resulttable.branch_id, resulttable.art_id, COUNT(resulttable.branch_id) AS numoccurrences FROM (SELECT txd1.branch_id, txd1.TIME, txd1.tx_type, txs1.*, artv1.art_id, artv1.modification_id, art1.art_type_id FROM osee_define_tx_details txd1, osee_define_txs txs1, osee_define_artifact art1, osee_define_artifact_version artv1 WHERE txd1.transaction_id = txs1.transaction_id AND txs1.gamma_id = artv1.gamma_id AND artv1.art_id = art1.art_id AND txs1.tx_current = 1) resulttable GROUP BY resulttable.branch_id, resulttable.art_id HAVING(COUNT(resulttable.branch_id) > 1)";

   private class UpdateHelper {
      int type;
      int id;
      int branch_id;
      int transaction_id;
      long gamma_id;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      int txNumber = 0;
      int txTypeNumber = 0;
      try {
         txNumber = Integer.parseInt(variableMap.getString("From Transaction Number"));
      } catch (NumberFormatException ex) {
         appendResultLine(String.format("Failed to parse string [%s], specify an integer",
               variableMap.getString("From Transaction Number")));
         return;
      }
      appendResultLine(String.format(
            "Updating attributes, artifacts, and relations current_tx column from transaction id [%d].\n", txNumber));
      Connection connection = null;
      try {
         int rowsUpdated;
         List<UpdateHelper> updates = new ArrayList<UpdateHelper>();
         connection = OseeDbConnection.getConnection();

         //*
         updateBaselineTransactions(connection, txTypeNumber);
         updateBaselinedTransactionsToCurrent(connection, txNumber);
         getUpdates(connection, updates, 1, SELECT_ATTRIBUTES_TO_UPDATE, txNumber);
         getUpdates(connection, updates, 2, SELECT_ARTIFACTS_TO_UPDATE, txNumber);
         getUpdates(connection, updates, 3, SELECT_RELATIONS_TO_UPDATE, txNumber);
         long time = System.currentTimeMillis();
         appendResultLine(String.format("Update [%d] transactions to baseline transactions.\n", txTypeNumber));
         appendResultLine(String.format("Going to update [%d] items to a 0 tx_current value.\n", updates.size()));
         rowsUpdated = updateTxCurrentToZero(connection, updates);
         appendResultLine(String.format("Took [%d]ms to update [%d] rows.\n", (System.currentTimeMillis() - time),
               rowsUpdated));
         time = System.currentTimeMillis();
         appendResultLine(String.format("Going to update [%d] items to a 1 tx_current value.\n", updates.size()));
         rowsUpdated = updateTxCurrentToOne(connection, updates);
         appendResultLine(String.format("Took [%d]ms to update [%d] rows.\n", (System.currentTimeMillis() - time),
               rowsUpdated));
         //*/

         updateArtifactModType(connection, txNumber);
         updateAttributeModType(connection, txNumber);
         updateRelationModType(connection, txNumber);

         updateRelationsSortOrder(connection, SELECT_A_RELATION_ORDER, UPDATE_A_ORDER);
         updateRelationsSortOrder(connection, SELECT_B_RELATION_ORDER, UPDATE_B_ORDER);

         if (variableMap.getBoolean("Run Tx Current Verification")) {
            boolean result = verifyTxCurrent(connection);
            if (!result) {
               throw new Exception("Tx Current Verification [ Failed ]");
            } else {
               appendResultLine("Tx Current Verification [ Passed ]");
            }
         }
      } finally {
         if (connection != null) {
            connection.close();
         }
      }

   }

   private boolean verifyTxCurrent(Connection connection) throws SQLException {
      ConnectionHandlerStatement statement = null;
      boolean wasSuccessful = true;
      try {
         statement = ConnectionHandler.runPreparedQuery(connection, 0, VERIFY_TX_CURRENT, new Object[0]);
         while (statement.next()) {
            wasSuccessful = false;
            appendResultLine(String.format("Duplicate tx_current at: branch_id[%s] id[%s]", statement.getRset().getInt(
                  1), statement.getRset().getInt(2)));
         }
      } catch (Exception ex) {
         wasSuccessful = false;
         throw new SQLException(ex);
      } finally {
         if (statement != null) {
            statement.close();
         }
      }
      return wasSuccessful;
   }

   private void updateArtifactModType(Connection connection, int txNumber) throws SQLException {
      List<Object[]> batchArgs = new ArrayList<Object[]>();
      ConnectionHandlerStatement stmt = null;
      try {
         stmt =
               ConnectionHandler.runPreparedQuery(connection, 0, SELECT_ARTIFACT_MOD_TYPE, new Object[] {
                     SQL3DataType.INTEGER, txNumber});
         while (stmt.getRset().next()) {
            batchArgs.add(new Object[] {SQL3DataType.INTEGER, stmt.getRset().getInt(1), SQL3DataType.INTEGER,
                  stmt.getRset().getInt(3), SQL3DataType.BIGINT, stmt.getRset().getLong(3)});
         }
         DbUtil.close(stmt);
         appendResultLine(String.format("Updating %d artifact mod types.\n", batchArgs.size()));
         int count = ConnectionHandler.runPreparedUpdate(connection, UPDATE_TXS_MOD_TYPE, batchArgs);
         appendResultLine(String.format("Updated %d rows.\n", count));
      } finally {
         DbUtil.close(stmt);
      }
   }

   private void updateAttributeModType(Connection connection, int txNumber) throws SQLException {
      List<Object[]> batchArgs = new ArrayList<Object[]>();
      ConnectionHandlerStatement stmt = null;
      try {
         stmt =
               ConnectionHandler.runPreparedQuery(connection, 0, SELECT_ATTRIBUTE_MOD_TYPE, new Object[] {
                     SQL3DataType.INTEGER, txNumber});
         while (stmt.getRset().next()) {
            batchArgs.add(new Object[] {SQL3DataType.INTEGER, stmt.getRset().getInt(1), SQL3DataType.INTEGER,
                  stmt.getRset().getInt(3), SQL3DataType.BIGINT, stmt.getRset().getLong(3)});
         }
         DbUtil.close(stmt);
         appendResultLine(String.format("Updating %d attribute mode types.\n", batchArgs.size()));
         int count = ConnectionHandler.runPreparedUpdate(connection, UPDATE_TXS_MOD_TYPE, batchArgs);
         appendResultLine(String.format("Updated %d rows.\n", count));
      } finally {
         DbUtil.close(stmt);
      }
   }

   private void updateRelationModType(Connection connection, int txNumber) throws SQLException {
      List<Object[]> batchArgs = new ArrayList<Object[]>();
      ConnectionHandlerStatement stmt = null;
      try {
         stmt =
               ConnectionHandler.runPreparedQuery(connection, 0, SELECT_RELATION_MOD_TYPE, new Object[] {
                     SQL3DataType.INTEGER, txNumber});
         while (stmt.getRset().next()) {
            batchArgs.add(new Object[] {SQL3DataType.INTEGER, stmt.getRset().getInt(1), SQL3DataType.INTEGER,
                  stmt.getRset().getInt(3), SQL3DataType.BIGINT, stmt.getRset().getLong(3)});
         }
         DbUtil.close(stmt);
         appendResultLine(String.format("Updating %d relation mode types.\n", batchArgs.size()));
         int count = ConnectionHandler.runPreparedUpdate(connection, UPDATE_TXS_MOD_TYPE, batchArgs);
         appendResultLine(String.format("Updated %d rows.\n", count));
      } finally {
         DbUtil.close(stmt);
      }
   }

   private void updateRelationsSortOrder(Connection connection, String select, String update) throws SQLException {
      int rel_link_type, art_id, branch_id, order;
      int rel_link_type_old = 0, art_id_old = 0, branch_id_old = 0;
      int new_order;
      int other_side_art_id = 0;
      List<Object[]> batchArgs = new ArrayList<Object[]>();

      ConnectionHandlerStatement stmt = null;
      try {
         stmt = ConnectionHandler.runPreparedQuery(connection, 5000, select);
         while (stmt.getRset().next()) {

            rel_link_type = stmt.getRset().getInt(1);
            art_id = stmt.getRset().getInt(2);
            branch_id = stmt.getRset().getInt(3);
            order = stmt.getRset().getInt(4);

            if (!(rel_link_type != rel_link_type_old || art_id != art_id_old || branch_id != branch_id_old)) {//then it's a new start of ordering
               new_order = -1;
            } else {
               new_order = other_side_art_id;
            }

            if (new_order != order) {
               batchArgs.add(new Object[] {SQL3DataType.INTEGER, new_order, SQL3DataType.BIGINT,
                     stmt.getRset().getLong(5)});
            }

            rel_link_type_old = rel_link_type;
            art_id_old = art_id;
            branch_id_old = branch_id;

            other_side_art_id = stmt.getRset().getInt(6);
         }
         DbUtil.close(stmt);
         appendResultLine(String.format("Updating %d relation orders.\n", batchArgs.size()));
         int count = ConnectionHandler.runPreparedUpdate(connection, update, batchArgs);
         appendResultLine(String.format("Updated %d rows.\n", count));
      } finally {
         DbUtil.close(stmt);
      }
   }

   /**
    * @param connection
    * @param txTypeNumber
    * @throws SQLException
    */
   private void updateBaselineTransactions(Connection connection, int txTypeNumber) throws SQLException {
      txTypeNumber += ConnectionHandler.runPreparedUpdate(connection, UPDATE_TX_DETAILS_NON_BASELINE_TRANSACTIONS_TO_0);
      txTypeNumber += ConnectionHandler.runPreparedUpdate(connection, UPDATE_TX_DETAILS_BASELINE_TRANSACTIONS_TO_1);
   }

   /**
    * @param connection
    * @throws SQLException
    * @throws
    */
   private void updateBaselinedTransactionsToCurrent(Connection connection, int txNumber) throws SQLException {
      List<Object[]> batchArgs = new ArrayList<Object[]>();
      ConnectionHandlerStatement stmt = null;
      try {
         stmt =
               ConnectionHandler.runPreparedQuery(connection, 0, SELECT_BASELINED_TRANSACTIONS, new Object[] {
                     SQL3DataType.INTEGER, txNumber});
         while (stmt.getRset().next()) {
            batchArgs.add(new Object[] {SQL3DataType.BIGINT, stmt.getRset().getLong(1), SQL3DataType.INTEGER,
                  stmt.getRset().getInt(2)});
         }
         DbUtil.close(stmt);
         appendResultLine(String.format("Updating %d baselined txs.\n", batchArgs.size()));
         int count = ConnectionHandler.runPreparedUpdate(connection, UPDATE_TXS_CURRENT_TO_1, batchArgs);
         appendResultLine(String.format("Updated %d rows.\n", count));
      } finally {
         DbUtil.close(stmt);
      }
   }

   /**
    * @param connection
    * @param updates
    * @throws SQLException
    */
   private int updateTxCurrentToOne(Connection connection, List<UpdateHelper> updates) throws SQLException {
      List<Object[]> batchArgs = new ArrayList<Object[]>();
      for (UpdateHelper data : updates) {
         batchArgs.add(new Object[] {SQL3DataType.BIGINT, data.gamma_id, SQL3DataType.INTEGER, data.transaction_id});
      }
      return ConnectionHandler.runPreparedUpdate(connection, UPDATE_TXS_CURRENT_TO_1, batchArgs);
   }

   /**
    * @param connection
    * @param updates
    * @throws SQLException
    */
   private int updateTxCurrentToZero(Connection connection, List<UpdateHelper> updates) throws SQLException {
      List<Object[]> setToUpdate = new ArrayList<Object[]>();
      List<Object[]> result = null;
      for (UpdateHelper data : updates) {
         result = null;
         if (data.type == 1) {
            result = getStaleTxCurrents(connection, SELECT_STALE_ATTRIBUTES, data);
         } else if (data.type == 2) {
            result = getStaleTxCurrents(connection, SELECT_STALE_ARTIFACTS, data);
         } else if (data.type == 3) {
            result = getStaleTxCurrents(connection, SELECT_STALE_RELATIONS, data);
         }
         if (result != null) {
            setToUpdate.addAll(result);
         }
      }
      return ConnectionHandler.runPreparedUpdate(connection, UPDATE_TXS_CURRENT_TO_0, setToUpdate);
   }

   private List<Object[]> getStaleTxCurrents(Connection connection, String query, UpdateHelper data) throws SQLException {
      List<Object[]> toReturn = new ArrayList<Object[]>();
      ConnectionHandlerStatement stmt = null;
      try {
         stmt =
               ConnectionHandler.runPreparedQuery(query, new Object[] {SQL3DataType.INTEGER, data.branch_id,
                     SQL3DataType.INTEGER, data.id});
         while (stmt.next()) {
            toReturn.add(new Object[] {SQL3DataType.BIGINT, stmt.getRset().getLong(1), SQL3DataType.INTEGER,
                  stmt.getRset().getInt(2)});
         }
      } finally {
         if (stmt != null) {
            stmt.close();
         }
      }
      return toReturn;
   }

   private void getUpdates(Connection connection, List<UpdateHelper> updates, int type, String query, int txNumber) throws SQLException {
      ConnectionHandlerStatement stmt = null;
      int count = 0;
      try {
         stmt =
               ConnectionHandler.runPreparedQuery(connection, 0, query, new Object[] {SQL3DataType.INTEGER, txNumber,
                     SQL3DataType.INTEGER, txNumber});
         while (stmt.getRset().next()) {
            count++;
            UpdateHelper helper = new UpdateHelper();
            helper.type = type;
            helper.branch_id = stmt.getRset().getInt(1);
            helper.transaction_id = stmt.getRset().getInt(2);
            helper.gamma_id = stmt.getRset().getLong(3);
            helper.id = stmt.getRset().getInt(4);
            updates.add(helper);
         }
      } finally {
         DbUtil.close(stmt);
      }
      appendResultLine(String.format("%d updates for [%s]\n", count, query));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XText\" displayName=\"From Transaction Number\" /><XWidget xwidgetType=\"XCheckBox\" displayName=\"Run Tx Current Verification\" labelAfter=\"true\" horizontalLabel=\"true\"/></xWidgets>";
   }
}