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
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Andrew M. Finkbeiner
 * @author Roberto E. Escobar
 */
public class UpdateCurrentColumn extends AbstractBlam {

   private enum Operations {
      Update_Tx_Mod_Type, Update_Tx_Current, Run_Tx_Current_Verification, Run_Tx_Mod_Type_Verification;

      public String asLabel() {
         return this.name().replaceAll("_", " ");
      }
   }

   private enum TypesEnum {
      artifacts, attributes, relations;
   }

   private static final String SELECT_ATTRIBUTES_TO_UPDATE =
         "SELECT branch_id, maxt, txs2.gamma_id, atid, txs2.mod_type FROM osee_define_attribute att2,  osee_define_txs txs2, (SELECT MAX(txs1.transaction_id) AS  maxt, att1.attr_id AS atid, txd1.branch_id FROM osee_define_attribute att1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE att1.gamma_id = txs1.gamma_id and txs1.transaction_id > ? and txd1.tx_type = 0 AND txs1.transaction_id = txd1.transaction_id GROUP BY att1.attr_id, txd1.branch_id) new_stuff WHERE atid = att2.attr_id AND att2.gamma_id = txs2.gamma_id and txs2.transaction_id > ? AND txs2.transaction_id = maxt";
   private static final String SELECT_ARTIFACTS_TO_UPDATE =
         "SELECT branch_id, maxt, txs1.gamma_id, art_id, txs1.mod_type FROM osee_define_artifact_version arv2,  osee_define_txs txs1, (SELECT MAX(txs2.transaction_id) AS maxt, arv1.art_id AS art, txd1.branch_id FROM osee_define_artifact_version arv1, osee_define_txs txs2, osee_define_tx_details txd1 WHERE arv1.gamma_id = txs2.gamma_id and txs2.transaction_id > ? and txd1.tx_type = 0 AND txs2.transaction_id = txd1.transaction_id GROUP BY arv1.art_id, txd1.branch_id) new_stuff WHERE art = arv2.art_id AND arv2.gamma_id = txs1.gamma_id AND txs1.transaction_id = maxt and txs1.transaction_id > ?";
   private static final String SELECT_RELATIONS_TO_UPDATE =
         "SELECT branch_id, maxt, txs1.gamma_id, rel_id, txs1.mod_type FROM osee_define_rel_link rel2, osee_define_txs txs1, (SELECT MAX(txs2.transaction_id) AS maxt, rel1.rel_link_id AS rel_id, txd1.branch_id FROM osee_define_rel_link rel1, osee_define_txs txs2, osee_define_tx_details txd1 WHERE rel1.gamma_id = txs2.gamma_id and txs2.transaction_id > ? and txd1.tx_type = 0 AND txs2.transaction_id = txd1.transaction_id GROUP BY rel1.rel_link_id, txd1.branch_id) new_stuff WHERE rel_id = rel2.rel_link_id AND rel2.gamma_id = txs1.gamma_id AND txs1.transaction_id = maxt and txs1.transaction_id > ?";

   private static final String SELECT_STALE_ATTRIBUTES =
         "SELECT txsouter.gamma_id, txsouter.transaction_id FROM osee_define_txs txsouter, (SELECT txs1.gamma_id, txs1.transaction_id FROM osee_define_txs txs1, osee_define_tx_details txd1, osee_define_attribute attr1 where txd1.branch_id = ? and attr1.attr_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.gamma_id = attr1.gamma_id) resulttable WHERE txsouter.transaction_id = resulttable.transaction_id AND resulttable.gamma_id = txsouter.gamma_id";
   private static final String SELECT_STALE_ARTIFACTS =
         "SELECT txsouter.gamma_id, txsouter.transaction_id FROM osee_define_txs txsouter, (SELECT txs1.gamma_id, txs1.transaction_id FROM osee_define_txs txs1, osee_define_tx_details txd1, osee_define_artifact_version art1 WHERE txd1.branch_id = ? AND art1.art_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.gamma_id = art1.gamma_id) resulttable WHERE txsouter.transaction_id = resulttable.transaction_id AND resulttable.gamma_id = txsouter.gamma_id";
   private static final String SELECT_STALE_RELATIONS =
         "SELECT txsouter.gamma_id, txsouter.transaction_id FROM osee_define_txs txsouter, (SELECT txs1.gamma_id, txs1.transaction_id FROM osee_define_txs txs1, osee_define_tx_details txd1, osee_define_rel_link link1 where txd1.branch_id = ? and link1.rel_link_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.gamma_id = link1.gamma_id) resulttable WHERE txsouter.transaction_id = resulttable.transaction_id AND resulttable.gamma_id = txsouter.gamma_id";

   private static final String UPDATE_TXS_CURRENT_TO_0 =
         "update osee_define_txs set tx_current = 0 where gamma_id = ? and transaction_id = ?";
   private static final String UPDATE_TXS_CURRENT =
         "update osee_define_txs set tx_current = ? where gamma_id = ? and transaction_id = ?";
   private static final String UPDATE_TXS_CURRENT_FROM_NULL =
         "UPDATE osee_define_txs txs1 SET tx_current = 0 WHERE tx_current IS null";

   private static final String SELECT_BASELINED_TRANSACTIONS =
         "SELECT txs1.gamma_id, txs1.transaction_id, txs1.mod_type from osee_define_txs txs1, osee_define_tx_details txd1 where txd1.tx_type = 1 and txd1.transaction_id > ? and txd1.transaction_id = txs1.transaction_id";

   private static final String UPDATE_TX_DETAILS_NON_BASELINE_TRANSACTIONS_TO_0 =
         "UPDATE osee_Define_tx_details SET tx_type = 0 WHERE tx_type IS NULL"; // Changed tx_type <> 1 to account for null case
   private static final String UPDATE_TX_DETAILS_BASELINE_TRANSACTIONS_TO_1 =
         "UPDATE osee_Define_tx_details SET tx_type = 1 WHERE osee_comment LIKE '%New Branch%'";

   private static final String VERIFY_ARTIFACT_MOD_TYPE =
         "select count(1) from osee_define_txs txs1, osee_define_artifact_version artv1 WHERE txs1.gamma_id = artv1.gamma_id AND txs1.mod_type IS NULL";
   private static final String VERIFY_ATTRIBUTE_MOD_TYPE =
         "select count(1) from osee_define_txs txs1, osee_define_attribute attr1 WHERE txs1.gamma_id = attr1.gamma_id AND txs1.mod_type IS NULL";
   private static final String VERIFY_RELATION_MOD_TYPE =
         "select count(1) from osee_define_txs txs1, osee_define_rel_link rel1 WHERE txs1.gamma_id = rel1.gamma_id AND txs1.mod_type IS NULL";

   private static final String INNER_SELECT_ARTIFACT_MOD_TYPE =
         "select artv1.modification_id from osee_define_txs txs1, osee_define_artifact_version artv1 where txs1.gamma_id = artv1.gamma_id";
   private static final String INNER_SELECT_ATTRIBUTE_MOD_TYPE =
         "select attr1.modification_id from osee_define_txs txs1, osee_define_attribute attr1 where txs1.gamma_id = attr1.gamma_id";
   private static final String INNER_SELECT_RELATION_MOD_TYPE =
         "select rel1.modification_id from osee_define_txs txs1, osee_define_rel_link rel1 where txs1.gamma_id = rel1.gamma_id";

   private static final String UPDATE_TXS_MOD_TYPE_SINGLE_CALL =
         "update osee_define_txs txsOuter set mod_type = (%s and txsOuter.transaction_id = txs1.transaction_id and txsOuter.gamma_id = txs1.gamma_id) WHERE txsouter.transaction_id > ? AND txsouter.mod_type IS NULL";

   private static final String VERIFY_TX_CURRENT =
         "SELECT resulttable.branch_id, resulttable.art_id, COUNT(resulttable.branch_id) AS numoccurrences FROM (SELECT txd1.branch_id, txd1.TIME, txd1.tx_type, txs1.*, artv1.art_id, txs1.mod_type, art1.art_type_id FROM osee_define_tx_details txd1, osee_define_txs txs1, osee_define_artifact art1, osee_define_artifact_version artv1 WHERE txd1.transaction_id = txs1.transaction_id AND txs1.gamma_id = artv1.gamma_id AND artv1.art_id = art1.art_id AND txs1.tx_current = 1) resulttable GROUP BY resulttable.branch_id, resulttable.art_id HAVING(COUNT(resulttable.branch_id) > 1)";

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
      appendResultLine(String.format("Processing from transaction id [%d].\n", startAtTxNumber));
      Connection connection = null;
      try {
         connection = OseeDbConnection.getConnection();

         int totalWork = 0;
         for (Operations operationType : Operations.values()) {
            if (variableMap.getBoolean(operationType.asLabel())) {
               IOperation op = getOperation(operationType);
               totalWork += op.getTotalWork();
            }
         }

         monitor.beginTask(getName(), totalWork);
         for (Operations operationType : Operations.values()) {
            if (variableMap.getBoolean(operationType.asLabel())) {
               IOperation op = getOperation(operationType);
               monitor.setTaskName(String.format("Executing: [%s]", operationType.asLabel()));
               op.execute(monitor, connection, startAtTxNumber);
               monitor.setTaskName("");
               if (monitor.isCanceled()) {
                  break;
               }
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
            toReturn = new UpdateTxModTypeSingleCallOperation();
            break;
         case Run_Tx_Current_Verification:
            toReturn = new VerifyTxCurrentOperation();
            break;
         case Run_Tx_Mod_Type_Verification:
            toReturn = new VerifyTxModTypeOperation();
            break;
         default:
            break;
      }
      return toReturn;
   }
   private interface IOperation {
      int getTotalWork();

      void execute(IProgressMonitor monitor, Connection connection, int startAtTxNumber) throws Exception;
   }

   private final class UpdateTxCurrentOperation implements IOperation {

      private final Map<TypesEnum, Pair<String, String>> typesQueryMap;
      private int totalCount;

      public UpdateTxCurrentOperation() {
         totalCount = 0;
         typesQueryMap = new HashMap<TypesEnum, Pair<String, String>>();
         typesQueryMap.put(TypesEnum.artifacts, new Pair<String, String>(SELECT_ARTIFACTS_TO_UPDATE,
               SELECT_STALE_ARTIFACTS));
         typesQueryMap.put(TypesEnum.attributes, new Pair<String, String>(SELECT_ATTRIBUTES_TO_UPDATE,
               SELECT_STALE_ATTRIBUTES));
         typesQueryMap.put(TypesEnum.relations, new Pair<String, String>(SELECT_RELATIONS_TO_UPDATE,
               SELECT_STALE_RELATIONS));
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.blam.operation.UpdateCurrentColumn.IOperation#execute()
       */
      @Override
      public void execute(IProgressMonitor monitor, Connection connection, int startAtTxNumber) throws Exception {
         totalCount = 0;
         final IProgressMonitor subMonitor = new SubProgressMonitor(monitor, getTotalWork());
         subMonitor.beginTask("Update Tx Current", getTotalWork());

         List<UpdateHelper> updates = new ArrayList<UpdateHelper>();

         int txTypeNumber = updateBaselineTransactions(subMonitor, connection);
         appendResultLine(String.format("Updated [%d] transactions to baseline transactions.\n", txTypeNumber));

         updateBaselinedTransactionsToCurrent(subMonitor, connection, startAtTxNumber);
         getUpdates(subMonitor, connection, updates, startAtTxNumber);
         appendResultLine(String.format("Total items identified as latest: [%d] \n", updates.size()));

         long time = System.currentTimeMillis();
         int rowsUpdated = updateTxCurrentToZeroForStaleItems(subMonitor, connection, updates);
         appendResultLine(String.format("Took [%d]ms to update [%d] rows.\n", (System.currentTimeMillis() - time),
               rowsUpdated));

         time = System.currentTimeMillis();
         appendResultLine(String.format("Going to update [%d] items to tx_current value of 1 or 2.\n", updates.size()));
         rowsUpdated = updateTxCurrentForLatestItems(subMonitor, connection, updates);
         appendResultLine(String.format("Took [%d]ms to update [%d] rows.\n", (System.currentTimeMillis() - time),
               rowsUpdated));

         subMonitor.done();
      }

      private int updateBaselineTransactions(IProgressMonitor monitor, Connection connection) throws SQLException {
         int txTypeNumber = 0;
         monitor.subTask("Update Baseline Txs - Tx Details Table");
         if (monitor.isCanceled() != true) {
            txTypeNumber +=
                  ConnectionHandler.runPreparedUpdate(connection, UPDATE_TX_DETAILS_NON_BASELINE_TRANSACTIONS_TO_0);
            txTypeNumber +=
                  ConnectionHandler.runPreparedUpdate(connection, UPDATE_TX_DETAILS_BASELINE_TRANSACTIONS_TO_1);
         }
         monitor.worked(1);
         return txTypeNumber;
      }

      private void updateBaselinedTransactionsToCurrent(final IProgressMonitor monitor, final Connection connection, final int txNumber) throws Exception {
         totalCount = 0;
         monitor.subTask("Mark tx current in txs table from data in txd table");
         final int batchSize = 100000;
         final List<Object[]> batchArgs = new ArrayList<Object[]>(batchSize);
         executeQuery(monitor, connection, new IRowProcessor() {
            public void processRow(ResultSet resultSet) throws Exception {
               int modType = resultSet.getInt(3);
               int tx_current_value = TxChange.CURRENT.getValue();
               if (modType == ModificationType.DELETED.getValue()) {
                  tx_current_value = TxChange.DELETED.getValue();
               }
               batchArgs.add(new Object[] {tx_current_value, resultSet.getLong(1), resultSet.getInt(2)});

               if (monitor.isCanceled() != true && batchArgs.size() >= batchSize) {
                  writeToDb(monitor, connection, UPDATE_TXS_CURRENT, "baselined txs", batchArgs);
                  batchArgs.clear();
               }
            }
         }, 0, SELECT_BASELINED_TRANSACTIONS, txNumber);

         if (monitor.isCanceled() != true && batchArgs.size() > 0) {
            writeToDb(monitor, connection, UPDATE_TXS_CURRENT, "baselined txs", batchArgs);
         }
         monitor.worked(1);
      }

      private void writeToDb(IProgressMonitor monitor, Connection connection, String sql, String name, List<Object[]> data) throws SQLException {
         int count = ConnectionHandler.runPreparedUpdate(connection, sql, data);
         totalCount += count;
         monitor.subTask(String.format("Updated [%d of %d] %s - overall [%d]\n", count, data.size(), name, totalCount));
      }

      private void getUpdates(IProgressMonitor monitor, Connection connection, final List<UpdateHelper> updates, int txNumber) throws Exception {
         for (final TypesEnum type : typesQueryMap.keySet()) {
            monitor.subTask(String.format("Select Latest For [%s]", type.name()));

            String query = typesQueryMap.get(type).getKey();
            int totalRows = executeQuery(monitor, connection, new IRowProcessor() {
               public void processRow(ResultSet resultSet) throws Exception {
                  updates.add(new UpdateHelper(type, resultSet));
               }
            }, 0, query, txNumber, txNumber);
            appendResultLine(String.format("%d updates for [%s]\n", totalRows, type));

            if (monitor.isCanceled()) {
               break;
            }
            monitor.worked(1);
         }
      }

      private int updateTxCurrentToZeroForStaleItems(IProgressMonitor monitor, Connection connection, List<UpdateHelper> updates) throws Exception {
         monitor.subTask("Setting Stale Items to 0");
         final List<Object[]> setToUpdate = new ArrayList<Object[]>();
         IRowProcessor processor = new IRowProcessor() {
            public void processRow(ResultSet resultSet) throws Exception {
               setToUpdate.add(new Object[] {resultSet.getLong(1), resultSet.getInt(2)});
            }
         };
         // Set Stale Items to 0
         for (UpdateHelper data : updates) {
            String query = typesQueryMap.get(data.type).getValue();
            if (query != null && monitor.isCanceled() != true) {
               executeQuery(monitor, connection, processor, 0, query, data.branch_id, data.id);
            }
         }

         int updated = 0;
         if (monitor.isCanceled() != true) {
            appendResultLine(String.format("%d updates for [updateTxCurrentToZero]\n", setToUpdate.size()));
            updated = ConnectionHandler.runPreparedUpdate(connection, UPDATE_TXS_CURRENT_TO_0, setToUpdate);
         }
         monitor.worked(1);
         return updated;
      }

      private int updateTxCurrentForLatestItems(IProgressMonitor monitor, Connection connection, List<UpdateHelper> updates) throws SQLException {
         monitor.subTask("Setting Tx Current for current items");
         List<Object[]> batchArgs = new ArrayList<Object[]>();
         for (UpdateHelper data : updates) {
            TxChange txCurrentValue =
                  data.modification_id == ModificationType.DELETED.getValue() ? TxChange.DELETED : TxChange.CURRENT; // Set to Current or Current was Deleted
            batchArgs.add(new Object[] {txCurrentValue.getValue(), data.gamma_id, data.transaction_id});
            if (monitor.isCanceled()) {
               break;
            }
         }

         int update = 0;
         if (monitor.isCanceled() != true) {
            update = ConnectionHandler.runPreparedUpdate(connection, UPDATE_TXS_CURRENT, batchArgs);
         }
         ConnectionHandler.runPreparedUpdate(connection, UPDATE_TXS_CURRENT_FROM_NULL);

         monitor.worked(1);
         return update;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.blam.operation.UpdateCurrentColumn.IOperation#getTotalWork()
       */
      @Override
      public int getTotalWork() {
         return 4 + typesQueryMap.size();
      }
   }

   private final class UpdateTxModTypeSingleCallOperation implements IOperation {

      private Map<TypesEnum, String> modTypeInnerSelectMap;

      public UpdateTxModTypeSingleCallOperation() {
         modTypeInnerSelectMap = new HashMap<TypesEnum, String>();
         modTypeInnerSelectMap.put(TypesEnum.artifacts, INNER_SELECT_ARTIFACT_MOD_TYPE);
         modTypeInnerSelectMap.put(TypesEnum.attributes, INNER_SELECT_ATTRIBUTE_MOD_TYPE);
         modTypeInnerSelectMap.put(TypesEnum.relations, INNER_SELECT_RELATION_MOD_TYPE);
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.blam.operation.UpdateCurrentColumn.IOperation#getTotalWork()
       */
      @Override
      public int getTotalWork() {
         return modTypeInnerSelectMap.size();
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.blam.operation.UpdateCurrentColumn.IOperation#execute(org.eclipse.core.runtime.IProgressMonitor, java.sql.Connection, int)
       */
      @Override
      public void execute(IProgressMonitor monitor, Connection connection, int startAtTxNumber) throws Exception {
         final IProgressMonitor subMonitor = new SubProgressMonitor(monitor, getTotalWork());
         subMonitor.beginTask("Update Mod Type", getTotalWork());

         int totalModified = 0;
         for (TypesEnum type : modTypeInnerSelectMap.keySet()) {
            subMonitor.subTask(String.format("Processing [%s] Mod Types", type.name()));
            String innerSelect = modTypeInnerSelectMap.get(type);
            if (Strings.isValid(innerSelect)) {
               String updateSql = String.format(UPDATE_TXS_MOD_TYPE_SINGLE_CALL, innerSelect);

               long time = System.currentTimeMillis();
               int count = ConnectionHandler.runPreparedUpdate(connection, updateSql, startAtTxNumber);
               appendResultLine(String.format("Updated [%s] rows for [%s] in [%d]ms\n", count, type.name(),
                     (System.currentTimeMillis() - time)));
               totalModified += count;
            }
            subMonitor.worked(1);
            if (subMonitor.isCanceled()) {
               break;
            }
         }
         appendResultLine(String.format("Updated [%d]txs mod types\n", totalModified));
         subMonitor.done();
      }
   }

   private final class VerifyTxCurrentOperation implements IOperation {

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.blam.operation.UpdateCurrentColumn.IOperation#execute(java.sql.Connection, int)
       */
      @Override
      public void execute(IProgressMonitor monitor, Connection connection, int startAtTxNumber) throws Exception {
         final IProgressMonitor subMonitor = new SubProgressMonitor(monitor, getTotalWork());
         subMonitor.beginTask("Verifying Tx Current", getTotalWork());

         int totalRowCount = executeQuery(monitor, connection, new IRowProcessor() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
               // Do Nothing
            }
         }, 0, VERIFY_TX_CURRENT);

         String msg = null;
         boolean result = totalRowCount == 0;
         if (monitor.isCanceled()) {
            msg = "Cancelled";
         } else if (result) {
            msg = "Passed";
         } else {
            msg = "Failed";
         }
         appendResultLine(String.format("Tx Current Verification [ %s ]\n", msg));
         subMonitor.worked(1);
         subMonitor.done();
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.blam.operation.UpdateCurrentColumn.IOperation#getTotalWork()
       */
      @Override
      public int getTotalWork() {
         return 1;
      }
   }

   private final class VerifyTxModTypeOperation implements IOperation {

      private Map<TypesEnum, String> modTypeVerificationMap;

      public VerifyTxModTypeOperation() {
         modTypeVerificationMap = new HashMap<TypesEnum, String>();
         modTypeVerificationMap.put(TypesEnum.artifacts, VERIFY_ARTIFACT_MOD_TYPE);
         modTypeVerificationMap.put(TypesEnum.attributes, VERIFY_ATTRIBUTE_MOD_TYPE);
         modTypeVerificationMap.put(TypesEnum.relations, VERIFY_RELATION_MOD_TYPE);
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.blam.operation.UpdateCurrentColumn.IOperation#execute(org.eclipse.core.runtime.IProgressMonitor, java.sql.Connection, int)
       */
      @Override
      public void execute(IProgressMonitor monitor, Connection connection, int startAtTxNumber) throws Exception {
         final IProgressMonitor subMonitor = new SubProgressMonitor(monitor, getTotalWork());
         subMonitor.beginTask("Verifying Tx Mod Types", getTotalWork());
         final Map<TypesEnum, Integer> results = new HashMap<TypesEnum, Integer>();
         final MutableInteger totalRowCount = new MutableInteger(0);
         for (final TypesEnum type : modTypeVerificationMap.keySet()) {
            monitor.subTask(String.format("Verifying: [%s]", type.name()));
            String sql = modTypeVerificationMap.get(type);
            if (Strings.isValid(sql)) {
               executeQuery(monitor, connection, new IRowProcessor() {
                  @Override
                  public void processRow(ResultSet resultSet) throws SQLException {
                     int total = resultSet.getInt(1);
                     totalRowCount.getValueAndInc(total);
                     results.put(type, total);
                  }
               }, 0, sql);
            }
            monitor.worked(1);
         }
         String msg = null;
         boolean result = totalRowCount.getValue() == 0;
         if (monitor.isCanceled()) {
            msg = "Cancelled";
         } else if (result) {
            msg = "Passed";
         } else {
            msg = "Failed";
            appendResultLine(String.format("Tx Mod Type Verification Results [ %s ]\n", results));
         }
         appendResultLine(String.format("Tx Mod Type Verification [ %s ]\n", msg));
         subMonitor.done();
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.blam.operation.UpdateCurrentColumn.IOperation#getTotalWork()
       */
      @Override
      public int getTotalWork() {
         return modTypeVerificationMap.size();
      }
   }

   private interface IRowProcessor {
      void processRow(ResultSet resultSet) throws Exception;
   }

   private int executeQuery(IProgressMonitor monitor, Connection connection, IRowProcessor processor, int fetchSize, String sql, Object... data) throws Exception {
      int totalRowCount = 0;
      ConnectionHandlerStatement statement = null;
      try {
         statement = ConnectionHandler.runPreparedQuery(connection, fetchSize, sql, data);
         ResultSet resultSet = statement.getRset();
         while (statement.next()) {
            totalRowCount++;
            processor.processRow(resultSet);
            if (monitor.isCanceled()) {
               break;
            }
         }
      } finally {
         if (statement != null) {
            statement.close();
         }
      }
      return totalRowCount;
   }
}