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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

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
         "SELECT branch_id, maxt, txs2.gamma_id, data_id, txs2.mod_type FROM osee_attribute att2,  osee_txs txs2, (SELECT MAX(txs1.transaction_id) AS  maxt, att1.attr_id AS data_id, txd1.branch_id FROM osee_attribute att1, osee_txs txs1, osee_tx_details txd1 WHERE att1.gamma_id = txs1.gamma_id and txs1.transaction_id > ? and txd1.tx_type = 0 AND txs1.transaction_id = txd1.transaction_id GROUP BY att1.attr_id, txd1.branch_id) new_stuff WHERE data_id = att2.attr_id AND att2.gamma_id = txs2.gamma_id and txs2.transaction_id > ? AND txs2.transaction_id = maxt";
   private static final String SELECT_ARTIFACTS_TO_UPDATE =
         "SELECT branch_id, maxt, txs1.gamma_id, art_id as data_id, txs1.mod_type FROM osee_artifact_version arv2,  osee_txs txs1, (SELECT MAX(txs2.transaction_id) AS maxt, arv1.art_id AS art, txd1.branch_id FROM osee_artifact_version arv1, osee_txs txs2, osee_tx_details txd1 WHERE arv1.gamma_id = txs2.gamma_id and txs2.transaction_id > ? and txd1.tx_type = 0 AND txs2.transaction_id = txd1.transaction_id GROUP BY arv1.art_id, txd1.branch_id) new_stuff WHERE art = arv2.art_id AND arv2.gamma_id = txs1.gamma_id AND txs1.transaction_id = maxt and txs1.transaction_id > ?";
   private static final String SELECT_RELATIONS_TO_UPDATE =
         "SELECT branch_id, maxt, txs1.gamma_id, data_id, txs1.mod_type FROM osee_relation_link rel2, osee_txs txs1, (SELECT MAX(txs2.transaction_id) AS maxt, rel1.rel_link_id AS data_id, txd1.branch_id FROM osee_relation_link rel1, osee_txs txs2, osee_tx_details txd1 WHERE rel1.gamma_id = txs2.gamma_id and txs2.transaction_id > ? and txd1.tx_type = 0 AND txs2.transaction_id = txd1.transaction_id GROUP BY rel1.rel_link_id, txd1.branch_id) new_stuff WHERE data_id = rel2.rel_link_id AND rel2.gamma_id = txs1.gamma_id AND txs1.transaction_id = maxt and txs1.transaction_id > ?";

   private static final String SELECT_STALE_ATTRIBUTES =
         "SELECT txsouter.gamma_id, txsouter.transaction_id FROM osee_txs txsouter, (SELECT txs1.gamma_id, txs1.transaction_id FROM osee_txs txs1, osee_tx_details txd1, osee_attribute attr1 where txd1.branch_id = ? and attr1.attr_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.gamma_id = attr1.gamma_id) resulttable WHERE txsouter.transaction_id = resulttable.transaction_id AND resulttable.gamma_id = txsouter.gamma_id";
   private static final String SELECT_STALE_ARTIFACTS =
         "SELECT txsouter.gamma_id, txsouter.transaction_id FROM osee_txs txsouter, (SELECT txs1.gamma_id, txs1.transaction_id FROM osee_txs txs1, osee_tx_details txd1, osee_artifact_version art1 WHERE txd1.branch_id = ? AND art1.art_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.gamma_id = art1.gamma_id) resulttable WHERE txsouter.transaction_id = resulttable.transaction_id AND resulttable.gamma_id = txsouter.gamma_id";
   private static final String SELECT_STALE_RELATIONS =
         "SELECT txsouter.gamma_id, txsouter.transaction_id FROM osee_txs txsouter, (SELECT txs1.gamma_id, txs1.transaction_id FROM osee_txs txs1, osee_tx_details txd1, osee_relation_link link1 where txd1.branch_id = ? and link1.rel_link_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.gamma_id = link1.gamma_id) resulttable WHERE txsouter.transaction_id = resulttable.transaction_id AND resulttable.gamma_id = txsouter.gamma_id";

   private static final String UPDATE_TXS_CURRENT_TO_0 =
         "update osee_txs set tx_current = 0 where gamma_id = ? and transaction_id = ?";
   private static final String UPDATE_TXS_CURRENT =
         "update osee_txs set tx_current = ? where gamma_id = ? and transaction_id = ?";
   private static final String UPDATE_TXS_CURRENT_FROM_NULL =
         "UPDATE osee_txs txs1 SET tx_current = 0 WHERE tx_current IS null";

   private static final String SELECT_BASELINED_TRANSACTIONS =
         "SELECT txs1.gamma_id, txs1.transaction_id, txs1.mod_type from osee_txs txs1, osee_tx_details txd1 where txd1.tx_type = 1 and txd1.transaction_id > ? and txd1.transaction_id = txs1.transaction_id";

   private static final String UPDATE_TX_DETAILS_NON_BASELINE_TRANSACTIONS_TO_0 =
         "UPDATE osee_tx_details SET tx_type = 0 WHERE tx_type IS NULL"; // Changed tx_type <> 1 to account for null case
   private static final String UPDATE_TX_DETAILS_BASELINE_TRANSACTIONS_TO_1 =
         "UPDATE osee_tx_details SET tx_type = 1 WHERE osee_comment LIKE '%New Branch%'";

   private static final String VERIFY_ARTIFACT_MOD_TYPE =
         "select count(1) as total from osee_txs txs1, osee_artifact_version artv1 WHERE txs1.gamma_id = artv1.gamma_id AND txs1.mod_type IS NULL";
   private static final String VERIFY_ATTRIBUTE_MOD_TYPE =
         "select count(1) as total from osee_txs txs1, osee_attribute attr1 WHERE txs1.gamma_id = attr1.gamma_id AND txs1.mod_type IS NULL";
   private static final String VERIFY_RELATION_MOD_TYPE =
         "select count(1) as total from osee_txs txs1, osee_relation_link rel1 WHERE txs1.gamma_id = rel1.gamma_id AND txs1.mod_type IS NULL";

   private static final String INNER_SELECT_ARTIFACT_MOD_TYPE =
         "select artv1.modification_id from osee_txs txs1, osee_artifact_version artv1 where txs1.gamma_id = artv1.gamma_id";
   private static final String INNER_SELECT_ATTRIBUTE_MOD_TYPE =
         "select attr1.modification_id from osee_txs txs1, osee_attribute attr1 where txs1.gamma_id = attr1.gamma_id";
   private static final String INNER_SELECT_RELATION_MOD_TYPE =
         "select rel1.modification_id from osee_txs txs1, osee_relation_link rel1 where txs1.gamma_id = rel1.gamma_id";

   private static final String UPDATE_TXS_MOD_TYPE_SINGLE_CALL =
         "update osee_txs txsOuter set mod_type = (%s and txsOuter.transaction_id = txs1.transaction_id and txsOuter.gamma_id = txs1.gamma_id) WHERE txsouter.transaction_id > ? AND txsouter.mod_type IS NULL";

   private static final String VERIFY_TX_CURRENT =
         "SELECT COUNT(resulttable.branch_id) AS numoccurrences FROM (SELECT txd1.branch_id, txd1.TIME, txd1.tx_type, txs1.*, artv1.art_id, txs1.mod_type, art1.art_type_id FROM osee_tx_details txd1, osee_txs txs1, osee_artifact art1, osee_artifact_version artv1 WHERE txd1.transaction_id = txs1.transaction_id AND txs1.gamma_id = artv1.gamma_id AND artv1.art_id = art1.art_id AND txs1.tx_current = 1) resulttable GROUP BY resulttable.branch_id, resulttable.art_id HAVING(COUNT(resulttable.branch_id) > 1)";

   private final class UpdateHelper {
      TypesEnum type;
      int id;
      int branch_id;
      int transaction_id;
      long gamma_id;
      int modification_id;

      UpdateHelper(TypesEnum type, ConnectionHandlerStatement chStmt) throws OseeDataStoreException {
         this.type = type;
         this.branch_id = chStmt.getInt("branch_id");
         this.transaction_id = chStmt.getInt("maxt");
         this.gamma_id = chStmt.getLong("gamma_id");
         this.id = chStmt.getInt("data_id");
         this.modification_id = chStmt.getInt("mod_type");
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getName()
    */
   @Override
   public String getName() {
      return "Update Current Column";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   @Override
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
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      int startAtTxNumber = 0;
      try {
         startAtTxNumber = Integer.parseInt(variableMap.getString("From Transaction Number"));
      } catch (NumberFormatException ex) {
         print(String.format("Failed to parse string [%s], specify an integer",
               variableMap.getString("From Transaction Number")));
         return;
      }
      print(String.format("Processing from transaction id [%d].\n", startAtTxNumber));
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
            op.execute(monitor, startAtTxNumber);
            monitor.setTaskName("");
            if (monitor.isCanceled()) {
               break;
            }
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

      void execute(IProgressMonitor monitor, int startAtTxNumber) throws Exception;
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
      public void execute(IProgressMonitor monitor, int startAtTxNumber) throws Exception {
         totalCount = 0;
         final IProgressMonitor subMonitor = new SubProgressMonitor(monitor, getTotalWork());
         subMonitor.beginTask("Update Tx Current", getTotalWork());

         List<UpdateHelper> updates = new ArrayList<UpdateHelper>();

         int txTypeNumber = updateBaselineTransactions(subMonitor);
         print(String.format("Updated [%d] transactions to baseline transactions.\n", txTypeNumber));

         updateBaselinedTransactionsToCurrent(subMonitor, startAtTxNumber);
         getUpdates(subMonitor, updates, startAtTxNumber);
         print(String.format("Total items identified as latest: [%d] \n", updates.size()));

         long time = System.currentTimeMillis();
         int rowsUpdated = updateTxCurrentToZeroForStaleItems(subMonitor, updates);
         print(String.format("Took [%d]ms to update [%d] rows.\n", (System.currentTimeMillis() - time),
               rowsUpdated));

         time = System.currentTimeMillis();
         print(String.format("Going to update [%d] items to tx_current value of 1 or 2.\n", updates.size()));
         rowsUpdated = updateTxCurrentForLatestItems(subMonitor, updates);
         print(String.format("Took [%d]ms to update [%d] rows.\n", (System.currentTimeMillis() - time),
               rowsUpdated));

         subMonitor.done();
      }

      private int updateBaselineTransactions(IProgressMonitor monitor) throws OseeDataStoreException {
         int txTypeNumber = 0;
         monitor.subTask("Update Baseline Txs - Tx Details Table");
         if (monitor.isCanceled() != true) {
            txTypeNumber += ConnectionHandler.runPreparedUpdate(UPDATE_TX_DETAILS_NON_BASELINE_TRANSACTIONS_TO_0);
            txTypeNumber += ConnectionHandler.runPreparedUpdate(UPDATE_TX_DETAILS_BASELINE_TRANSACTIONS_TO_1);
         }
         monitor.worked(1);
         return txTypeNumber;
      }

      private void updateBaselinedTransactionsToCurrent(final IProgressMonitor monitor, final int txNumber) throws Exception {
         totalCount = 0;
         monitor.subTask("Mark tx current in txs table from data in txd table");
         final int batchSize = 100000;
         final List<Object[]> batchArgs = new ArrayList<Object[]>(batchSize);
         executeQuery(monitor, new IRowProcessor() {
            public void processRow(ConnectionHandlerStatement chStmt) throws OseeDataStoreException {
               int modType = chStmt.getInt("mod_type");
               int tx_current_value = TxChange.CURRENT.getValue();
               if (modType == ModificationType.DELETED.getValue()) {
                  tx_current_value = TxChange.DELETED.getValue();
               }
               batchArgs.add(new Object[] {tx_current_value, chStmt.getLong("gamma_id"),
                     chStmt.getInt("transaction_id")});

               if (monitor.isCanceled() != true && batchArgs.size() >= batchSize) {
                  writeToDb(monitor, UPDATE_TXS_CURRENT, "baselined txs", batchArgs);
                  batchArgs.clear();
               }
            }
         }, 0, SELECT_BASELINED_TRANSACTIONS, txNumber);

         if (monitor.isCanceled() != true && batchArgs.size() > 0) {
            writeToDb(monitor, UPDATE_TXS_CURRENT, "baselined txs", batchArgs);
         }
         monitor.worked(1);
      }

      private void writeToDb(IProgressMonitor monitor, String sql, String name, List<Object[]> data) throws OseeDataStoreException {
         int count = ConnectionHandler.runBatchUpdate(sql, data);
         totalCount += count;
         monitor.subTask(String.format("Updated [%d of %d] %s - overall [%d]\n", count, data.size(), name, totalCount));
      }

      private void getUpdates(IProgressMonitor monitor, final List<UpdateHelper> updates, int txNumber) throws Exception {
         for (final TypesEnum type : typesQueryMap.keySet()) {
            monitor.subTask(String.format("Select Latest For [%s]", type.name()));

            String query = typesQueryMap.get(type).getKey();
            int totalRows = executeQuery(monitor, new IRowProcessor() {
               public void processRow(ConnectionHandlerStatement chStmt) throws OseeDataStoreException {
                  updates.add(new UpdateHelper(type, chStmt));
               }
            }, 0, query, txNumber, txNumber);
            print(String.format("%d updates for [%s]\n", totalRows, type));

            if (monitor.isCanceled()) {
               break;
            }
            monitor.worked(1);
         }
      }

      private int updateTxCurrentToZeroForStaleItems(IProgressMonitor monitor, List<UpdateHelper> updates) throws Exception {
         monitor.subTask("Setting Stale Items to 0");
         final List<Object[]> setToUpdate = new ArrayList<Object[]>();
         IRowProcessor processor = new IRowProcessor() {
            public void processRow(ConnectionHandlerStatement chStmt) throws OseeDataStoreException {
               setToUpdate.add(new Object[] {chStmt.getLong("gamma_id"), chStmt.getInt("transaction_id")});
            }
         };
         // Set Stale Items to 0
         for (UpdateHelper data : updates) {
            String query = typesQueryMap.get(data.type).getValue();
            if (query != null && monitor.isCanceled() != true) {
               executeQuery(monitor, processor, 0, query, data.branch_id, data.id);
            }
         }

         int updated = 0;
         if (monitor.isCanceled() != true) {
            print(String.format("%d updates for [updateTxCurrentToZero]\n", setToUpdate.size()));
            updated = ConnectionHandler.runBatchUpdate(UPDATE_TXS_CURRENT_TO_0, setToUpdate);
         }
         monitor.worked(1);
         return updated;
      }

      private int updateTxCurrentForLatestItems(IProgressMonitor monitor, List<UpdateHelper> updates) throws OseeDataStoreException {
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
            update = ConnectionHandler.runBatchUpdate(UPDATE_TXS_CURRENT, batchArgs);
         }
         ConnectionHandler.runPreparedUpdate(UPDATE_TXS_CURRENT_FROM_NULL);

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

      private final Map<TypesEnum, String> modTypeInnerSelectMap;

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

      @Override
      public void execute(IProgressMonitor monitor, int startAtTxNumber) throws Exception {
         final IProgressMonitor subMonitor = new SubProgressMonitor(monitor, getTotalWork());
         subMonitor.beginTask("Update Mod Type", getTotalWork());

         int totalModified = 0;
         for (TypesEnum type : modTypeInnerSelectMap.keySet()) {
            subMonitor.subTask(String.format("Processing [%s] Mod Types", type.name()));
            String innerSelect = modTypeInnerSelectMap.get(type);
            if (Strings.isValid(innerSelect)) {
               String updateSql = String.format(UPDATE_TXS_MOD_TYPE_SINGLE_CALL, innerSelect);

               long time = System.currentTimeMillis();
               int count = ConnectionHandler.runPreparedUpdate(updateSql, startAtTxNumber);
               print(String.format("Updated [%s] rows for [%s] in [%d]ms\n", count, type.name(),
                     (System.currentTimeMillis() - time)));
               totalModified += count;
            }
            subMonitor.worked(1);
            if (subMonitor.isCanceled()) {
               break;
            }
         }
         print(String.format("Updated [%d]txs mod types\n", totalModified));
         subMonitor.done();
      }
   }

   private final class VerifyTxCurrentOperation implements IOperation {

      @Override
      public void execute(IProgressMonitor monitor, int startAtTxNumber) throws Exception {
         final IProgressMonitor subMonitor = new SubProgressMonitor(monitor, getTotalWork());
         subMonitor.beginTask("Verifying Tx Current", getTotalWork());

         int totalRowCount = ConnectionHandler.runPreparedQueryFetchInt(-1, VERIFY_TX_CURRENT);

         String msg = null;
         boolean result = totalRowCount == 0;
         if (monitor.isCanceled()) {
            msg = "Cancelled";
         } else if (result) {
            msg = "Passed";
         } else {
            msg = "Failed";
         }
         print(String.format("Tx Current Verification [ %s ]\n", msg));
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

      private final Map<TypesEnum, String> modTypeVerificationMap;

      public VerifyTxModTypeOperation() {
         modTypeVerificationMap = new HashMap<TypesEnum, String>();
         modTypeVerificationMap.put(TypesEnum.artifacts, VERIFY_ARTIFACT_MOD_TYPE);
         modTypeVerificationMap.put(TypesEnum.attributes, VERIFY_ATTRIBUTE_MOD_TYPE);
         modTypeVerificationMap.put(TypesEnum.relations, VERIFY_RELATION_MOD_TYPE);
      }

      @Override
      public void execute(IProgressMonitor monitor, int startAtTxNumber) throws Exception {
         final IProgressMonitor subMonitor = new SubProgressMonitor(monitor, getTotalWork());
         subMonitor.beginTask("Verifying Tx Mod Types", getTotalWork());
         final Map<TypesEnum, Integer> results = new HashMap<TypesEnum, Integer>();
         final MutableInteger totalRowCount = new MutableInteger(0);
         for (final TypesEnum type : modTypeVerificationMap.keySet()) {
            monitor.subTask(String.format("Verifying: [%s]", type.name()));
            String sql = modTypeVerificationMap.get(type);
            if (Strings.isValid(sql)) {
               executeQuery(monitor, new IRowProcessor() {
                  @Override
                  public void processRow(ConnectionHandlerStatement chStmt) throws OseeDataStoreException {
                     int total = chStmt.getInt("total");
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
            print(String.format("Tx Mod Type Verification Results [ %s ]\n", results));
         }
         print(String.format("Tx Mod Type Verification [ %s ]\n", msg));
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
      void processRow(ConnectionHandlerStatement chStmt) throws OseeDataStoreException;
   }

   private int executeQuery(IProgressMonitor monitor, IRowProcessor processor, int fetchSize, String sql, Object... data) throws Exception {
      int totalRowCount = 0;
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(fetchSize, sql, data);
         while (chStmt.next()) {
            totalRowCount++;
            processor.processRow(chStmt);
            if (monitor.isCanceled()) {
               break;
            }
         }
      } finally {
         chStmt.close();
      }
      return totalRowCount;
   }

   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }
}