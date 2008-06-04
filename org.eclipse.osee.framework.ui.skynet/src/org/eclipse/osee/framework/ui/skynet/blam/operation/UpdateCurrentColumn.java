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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Andrew M Finkbeiner
 */
public class UpdateCurrentColumn extends AbstractBlam {

   private enum Operations {
      Update_Tx_Current, Update_Tx_Mod_Type, Run_Tx_Current_Verification;

      public String asLabel() {
         return this.name().replaceAll("_", " ");
      }
   }

   private enum TypesEnum {
      arts, attrs, rels;
   }

   private static final String SELECT_ATTRIBUTES_TO_UPDATE =
         "SELECT branch_id, maxt, txs2.gamma_id, atid, att2.modification_id FROM osee_define_attribute att2,  osee_define_txs txs2, (SELECT MAX(txs1.transaction_id) AS  maxt, att1.attr_id AS atid, txd1.branch_id FROM osee_define_attribute att1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE att1.gamma_id = txs1.gamma_id and txs1.transaction_id > ? and txd1.tx_type = 0 AND txs1.transaction_id = txd1.transaction_id GROUP BY att1.attr_id, txd1.branch_id) new_stuff WHERE atid = att2.attr_id AND att2.gamma_id = txs2.gamma_id and txs2.transaction_id > ? AND txs2.transaction_id = maxt";
   private static final String SELECT_ARTIFACTS_TO_UPDATE =
         "SELECT branch_id, maxt, txs1.gamma_id, art_id, arv2.modification_id FROM osee_define_artifact_version arv2,  osee_define_txs txs1, (SELECT MAX(txs2.transaction_id) AS maxt, arv1.art_id AS art, txd1.branch_id FROM osee_define_artifact_version arv1, osee_define_txs txs2, osee_define_tx_details txd1 WHERE arv1.gamma_id = txs2.gamma_id and txs2.transaction_id > ? and txd1.tx_type = 0 AND txs2.transaction_id = txd1.transaction_id GROUP BY arv1.art_id, txd1.branch_id) new_stuff WHERE art = arv2.art_id AND arv2.gamma_id = txs1.gamma_id AND txs1.transaction_id = maxt and txs1.transaction_id > ?";
   private static final String SELECT_RELATIONS_TO_UPDATE =
         "SELECT branch_id, maxt, txs1.gamma_id, rel_id, rel2.modification_id FROM osee_define_rel_link rel2, osee_define_txs txs1, (SELECT MAX(txs2.transaction_id) AS maxt, rel1.rel_link_id AS rel_id, txd1.branch_id FROM osee_define_rel_link rel1, osee_define_txs txs2, osee_define_tx_details txd1 WHERE rel1.gamma_id = txs2.gamma_id and txs2.transaction_id > ? and txd1.tx_type = 0 AND txs2.transaction_id = txd1.transaction_id GROUP BY rel1.rel_link_id, txd1.branch_id) new_stuff WHERE rel_id = rel2.rel_link_id AND rel2.gamma_id = txs1.gamma_id AND txs1.transaction_id = maxt and txs1.transaction_id > ?";

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
   private static final String UPDATE_TXS_CURRENT =
         "update osee_define_txs set tx_current = ? where gamma_id = ? and transaction_id = ?";

   private static final String SELECT_BASELINED_TRANSACTIONS =
         "SELECT txs1.gamma_id, txs1.transaction_id from osee_define_txs txs1, osee_define_tx_details txd1 where txd1.tx_type = 1 and txd1.transaction_id > ? and txd1.transaction_id = txs1.transaction_id";

   private static final String UPDATE_TX_DETAILS_NON_BASELINE_TRANSACTIONS_TO_0 =
         "UPDATE osee_Define_tx_details SET tx_type = 0 WHERE tx_type IS NULL"; // Changed tx_type <> 1 to account for null case
   private static final String UPDATE_TX_DETAILS_BASELINE_TRANSACTIONS_TO_1 =
         "UPDATE osee_Define_tx_details SET tx_type = 1 WHERE osee_comment LIKE '%New Branch%'";

   private static final String UPDATE_TXS_MOD_TYPE =
         "update osee_define_txs set mod_type = ? where transaction_id = ? AND gamma_id = ?";

   private static final String SELECT_ARTIFACT_MOD_TYPE =
         "select artv1.modification_id, txd1.transaction_id, txs1.gamma_id from osee_define_tx_details txd1, osee_define_txs txs1, osee_define_artifact_version artv1 where txd1.transaction_id > ? and txd1.TRANSACTION_ID = txs1.transaction_id and txs1.gamma_id = artv1.gamma_id";
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

   private static final Map<TypesEnum, Pair<String, String>> typesQueryMap;
   static {
      typesQueryMap = new HashMap<TypesEnum, Pair<String, String>>();
      typesQueryMap.put(TypesEnum.arts, new Pair<String, String>(SELECT_ARTIFACTS_TO_UPDATE, SELECT_STALE_ARTIFACTS));
      typesQueryMap.put(TypesEnum.attrs, new Pair<String, String>(SELECT_ATTRIBUTES_TO_UPDATE, SELECT_STALE_ATTRIBUTES));
      typesQueryMap.put(TypesEnum.rels, new Pair<String, String>(SELECT_RELATIONS_TO_UPDATE, SELECT_STALE_RELATIONS));
   }
   private static final String[] MOD_TYPE_QUERIES =
         new String[] {SELECT_ARTIFACT_MOD_TYPE, SELECT_ATTRIBUTE_MOD_TYPE, SELECT_RELATION_MOD_TYPE};

   private final class UpdateHelper {
      TypesEnum type;
      int id;
      int branch_id;
      int transaction_id;
      long gamma_id;
      int modification_id;

      UpdateHelper(TypesEnum type, ResultSet resultSet) throws SQLException {
         this.type = type;
         this.branch_id = resultSet.getInt(1);
         this.transaction_id = resultSet.getInt(2);
         this.gamma_id = resultSet.getLong(3);
         this.id = resultSet.getInt(4);
         this.modification_id = resultSet.getInt(5);
      }
   }

   private final class RelationHelper {
      int rel_link_type, art_id, branch_id, order;
      int rel_link_type_old = 0, art_id_old = 0, branch_id_old = 0;
      int new_order;
      int other_side_art_id = 0;
      long gammaId = 0;

      RelationHelper(ResultSet resultSet) throws SQLException {
         rel_link_type = resultSet.getInt(1);
         art_id = resultSet.getInt(2);
         branch_id = resultSet.getInt(3);
         order = resultSet.getInt(4);
         gammaId = resultSet.getLong(5);
         if (!(rel_link_type != rel_link_type_old || art_id != art_id_old || branch_id != branch_id_old)) {//then it's a new start of ordering
            new_order = -1;
         } else {
            new_order = other_side_art_id;
         }

         rel_link_type_old = rel_link_type;
         art_id_old = art_id;
         branch_id_old = branch_id;
         other_side_art_id = resultSet.getInt(6);
      }

      boolean isUpdateRequired() {
         return new_order != order;
      }

      Object[] getUpdateData() {
         return new Object[] {SQL3DataType.INTEGER, new_order, SQL3DataType.BIGINT, gammaId};
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append("<XWidget xwidgetType=\"XText\" displayName=\"From Transaction Number\" />");
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Select Operations to Run:\"/>");
      for (Operations operationType : Operations.values()) {
         builder.append(getOperationsCheckBoxes(operationType));
      }
      builder.append("</xWidgets>");
      return builder.toString();
   }

   private String getOperationsCheckBoxes(Operations ops) {
      StringBuilder builder = new StringBuilder();
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"");
      builder.append(ops.asLabel());
      builder.append("\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      return builder.toString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      int startAtTxNumber = 0;
      try {
         startAtTxNumber = Integer.parseInt(variableMap.getString("From Transaction Number"));
      } catch (NumberFormatException ex) {
         appendResultLine(String.format("Failed to parse string [%s], specify an integer",
               variableMap.getString("From Transaction Number")));
         return;
      }
      appendResultLine(String.format(
            "Updating attributes, artifacts, and relations current_tx column from transaction id [%d].\n",
            startAtTxNumber));
      Connection connection = null;
      try {
         connection = OseeDbConnection.getConnection();

         for (Operations operationType : Operations.values()) {
            if (variableMap.getBoolean(operationType.asLabel())) {
               IOperation op = getOperation(operationType);
               op.execute(connection, startAtTxNumber);
            }
         }
      } finally {
         if (connection != null) {
            connection.close();
         }
      }

   }

   private IOperation getOperation(Operations type) {
      IOperation toReturn = null;
      switch (type) {
         case Update_Tx_Current:
            toReturn = new UpdateTxCurrentOperation();
            break;
         case Update_Tx_Mod_Type:
            toReturn = new UpdateTxModTypeOperation();
            break;
         case Run_Tx_Current_Verification:
            toReturn = new VerifyTxCurrentOperation();
            break;
         default:
            break;
      }
      return toReturn;
   }

   private interface IOperation {
      void execute(Connection connection, int startAtTxNumber) throws Exception;
   }

   private final class UpdateTxCurrentOperation implements IOperation {

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.blam.operation.UpdateCurrentColumn.IOperation#execute()
       */
      @Override
      public void execute(Connection connection, int startAtTxNumber) throws Exception {
         int rowsUpdated = 0;
         int txTypeNumber = 0;

         List<UpdateHelper> updates = new ArrayList<UpdateHelper>();

         updateBaselineTransactions(connection, txTypeNumber);
         updateBaselinedTransactionsToCurrent(connection, startAtTxNumber);
         getUpdates(connection, updates, startAtTxNumber);
         long time = System.currentTimeMillis();
         appendResultLine(String.format("Update [%d] transactions to baseline transactions.\n", txTypeNumber));
         appendResultLine(String.format("Going to update [%d] items to a 0 tx_current value.\n", updates.size()));
         rowsUpdated = updateTxCurrentToZeroForStaleItems(connection, updates);
         appendResultLine(String.format("Took [%d]ms to update [%d] rows.\n", (System.currentTimeMillis() - time),
               rowsUpdated));
         time = System.currentTimeMillis();
         appendResultLine(String.format("Going to update [%d] items to a 1 tx_current value.\n", updates.size()));
         rowsUpdated = updateTxCurrentForLatestItems(connection, updates);
         appendResultLine(String.format("Took [%d]ms to update [%d] rows.\n", (System.currentTimeMillis() - time),
               rowsUpdated));
      }

      private void updateBaselineTransactions(Connection connection, int txTypeNumber) throws SQLException {
         txTypeNumber +=
               ConnectionHandler.runPreparedUpdate(connection, UPDATE_TX_DETAILS_NON_BASELINE_TRANSACTIONS_TO_0);
         txTypeNumber += ConnectionHandler.runPreparedUpdate(connection, UPDATE_TX_DETAILS_BASELINE_TRANSACTIONS_TO_1);
      }

      private void updateBaselinedTransactionsToCurrent(Connection connection, int txNumber) throws SQLException {
         final List<Object[]> batchArgs = new ArrayList<Object[]>();
         executeQuery(connection, new IRowProcessor() {
            public void processRow(ResultSet resultSet) throws SQLException {
               batchArgs.add(new Object[] {SQL3DataType.BIGINT, resultSet.getLong(1), SQL3DataType.INTEGER,
                     resultSet.getInt(2)});
            }
         }, 0, SELECT_BASELINED_TRANSACTIONS, SQL3DataType.INTEGER, txNumber);

         appendResultLine(String.format("Updating %d baselined txs.\n", batchArgs.size()));
         int count = ConnectionHandler.runPreparedUpdate(connection, UPDATE_TXS_CURRENT_TO_1, batchArgs);
         appendResultLine(String.format("Updated %d rows.\n", count));
      }

      private void getUpdates(Connection connection, final List<UpdateHelper> updates, int txNumber) throws SQLException {
         for (final TypesEnum type : typesQueryMap.keySet()) {
            String query = typesQueryMap.get(type).getKey();
            int totalRows = executeQuery(connection, new IRowProcessor() {
               public void processRow(ResultSet resultSet) throws SQLException {
                  updates.add(new UpdateHelper(type, resultSet));
               }
            }, 0, query, SQL3DataType.INTEGER, txNumber, SQL3DataType.INTEGER, txNumber);
            appendResultLine(String.format("%d updates for [%s]\n", totalRows, type));
         }
      }

      private int updateTxCurrentToZeroForStaleItems(Connection connection, List<UpdateHelper> updates) throws SQLException {
         final List<Object[]> setToUpdate = new ArrayList<Object[]>();
         IRowProcessor processor = new IRowProcessor() {
            public void processRow(ResultSet resultSet) throws SQLException {
               setToUpdate.add(new Object[] {SQL3DataType.BIGINT, resultSet.getLong(1), SQL3DataType.INTEGER,
                     resultSet.getInt(2)});
            }
         };
         // Set Stale Items to 0
         for (UpdateHelper data : updates) {
            String query = typesQueryMap.get(data.type).getValue();
            if (query != null) {
               executeQuery(connection, processor, 0, query, SQL3DataType.INTEGER, data.branch_id,
                     SQL3DataType.INTEGER, data.id);
            }
         }
         appendResultLine(String.format("%d updates for [updateTxCurrentToZero]\n", setToUpdate.size()));
         return ConnectionHandler.runPreparedUpdate(connection, UPDATE_TXS_CURRENT_TO_0, setToUpdate);
      }

      private int updateTxCurrentForLatestItems(Connection connection, List<UpdateHelper> updates) throws SQLException {
         List<Object[]> batchArgs = new ArrayList<Object[]>();
         int txCurrentValue = -1;
         for (UpdateHelper data : updates) {
            txCurrentValue = data.modification_id != 3 ? 1 : 2; // Set to Current or Current was Deleted
            batchArgs.add(new Object[] {SQL3DataType.INTEGER, txCurrentValue, SQL3DataType.BIGINT, data.gamma_id,
                  SQL3DataType.INTEGER, data.transaction_id});
         }
         return ConnectionHandler.runPreparedUpdate(connection, UPDATE_TXS_CURRENT, batchArgs);
      }
   }

   private final class UpdateTxModTypeOperation implements IOperation {

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.blam.operation.UpdateCurrentColumn.IOperation#execute(java.sql.Connection, int)
       */
      @Override
      public void execute(Connection connection, int startAtTxNumber) throws Exception {
         final List<Object[]> batchArgs = new ArrayList<Object[]>();
         IRowProcessor processor = new IRowProcessor() {
            public void processRow(ResultSet resultSet) throws SQLException {
               batchArgs.add(new Object[] {SQL3DataType.INTEGER, resultSet.getInt(1), SQL3DataType.INTEGER,
                     resultSet.getInt(3), SQL3DataType.BIGINT, resultSet.getLong(3)});
            }
         };
         for (String query : MOD_TYPE_QUERIES) {
            executeQuery(connection, processor, 0, query, SQL3DataType.INTEGER, startAtTxNumber);
         }
         appendResultLine(String.format("Updating %d mod types.\n", batchArgs.size()));
         int count = ConnectionHandler.runPreparedUpdate(connection, UPDATE_TXS_MOD_TYPE, batchArgs);
         appendResultLine(String.format("Updated %d rows.\n", count));
      }

   }

   private final class UpdateRelationsSortOrder implements IOperation {

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.blam.operation.UpdateCurrentColumn.IOperation#execute(java.sql.Connection, int)
       */
      @Override
      public void execute(Connection connection, int startAtTxNumber) throws Exception {
         updateRelationsSortOrder(connection, SELECT_A_RELATION_ORDER, UPDATE_A_ORDER);
         updateRelationsSortOrder(connection, SELECT_B_RELATION_ORDER, UPDATE_B_ORDER);
      }

      private void updateRelationsSortOrder(Connection connection, String query, String update) throws SQLException {
         final List<Object[]> batchArgs = new ArrayList<Object[]>();
         executeQuery(connection, new IRowProcessor() {
            public void processRow(ResultSet resultSet) throws SQLException {
               RelationHelper helper = new RelationHelper(resultSet);
               if (helper.isUpdateRequired()) {
                  batchArgs.add(helper.getUpdateData());
               }
            }
         }, 5000, query);
         appendResultLine(String.format("Updating %d relation orders.\n", batchArgs.size()));
         int count = ConnectionHandler.runPreparedUpdate(connection, update, batchArgs);
         appendResultLine(String.format("Updated %d rows.\n", count));
      }
   }

   private final class VerifyTxCurrentOperation implements IOperation {

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.blam.operation.UpdateCurrentColumn.IOperation#execute(java.sql.Connection, int)
       */
      @Override
      public void execute(Connection connection, int startAtTxNumber) throws Exception {
         int totalRowCount = executeQuery(connection, new IRowProcessor() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
               // Do Nothing
            }
         }, 0, VERIFY_TX_CURRENT);

         boolean result = totalRowCount == 0;
         if (!result) {
            throw new Exception("Tx Current Verification [ Failed ]");
         } else {
            appendResultLine("Tx Current Verification [ Passed ]");
         }
      }
   }

   private interface IRowProcessor {
      void processRow(ResultSet resultSet) throws SQLException;
   }

   private int executeQuery(Connection connection, IRowProcessor processor, int fetchSize, String sql, Object... data) throws SQLException {
      int totalRowCount = 0;
      ConnectionHandlerStatement statement = null;
      try {
         statement = ConnectionHandler.runPreparedQuery(connection, fetchSize, sql, data);
         ResultSet resultSet = statement.getRset();
         while (statement.next()) {
            totalRowCount++;
            processor.processRow(resultSet);
         }
      } finally {
         if (statement != null) {
            statement.close();
         }
      }
      return totalRowCount;
   }
}