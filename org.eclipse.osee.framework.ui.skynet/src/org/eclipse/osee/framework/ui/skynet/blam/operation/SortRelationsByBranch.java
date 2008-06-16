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
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Andrew M. Finkbeiner
 * @author Roberto E. Escobar
 */
public class SortRelationsByBranch extends AbstractBlam {
   private static final String UPDATE_B_ORDER = "update osee_define_rel_link set b_order = ? where gamma_id = ?";
   private static final String SELECT_B_RELATION_ORDER =
         "select rel1.rel_link_type_id,  rel1.a_art_id, txd1.branch_id, rel1.b_order, txs1.gamma_id, rel1.b_art_id, rel1.a_order_value from osee_define_tx_details txd1, osee_define_rel_link rel1, osee_define_txs txs1 where txd1.branch_id = ? and txd1.transaction_id = txs1.transaction_id and txs1.gamma_id = rel1.gamma_id and txs1.tx_current = 1 order by txd1.branch_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.a_order_value";
   private static final String SELECT_A_RELATION_ORDER =
         "select rel1.rel_link_type_id,  rel1.b_art_id, txd1.branch_id, rel1.a_order, txs1.gamma_id, rel1.a_art_id, rel1.b_order_value    from osee_define_tx_details txd1, osee_define_rel_link rel1, osee_define_txs txs1 where txd1.branch_id = ? and txd1.transaction_id = txs1.transaction_id and txs1.gamma_id = rel1.gamma_id and txs1.tx_current = 1 order by txd1.branch_id, rel1.rel_link_type_id, rel1.b_art_id, rel1.b_order_value";
   private static final String UPDATE_A_ORDER = "update osee_define_rel_link set a_order = ? where gamma_id = ?";
   private Branch branchToSort;

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#wrapOperationForBranch(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap)
    */
   @Override
   public Branch wrapOperationForBranch(BlamVariableMap variableMap) {
      return variableMap.getBranch("Parent Branch");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {

      this.branchToSort = variableMap.getBranch("Parent Branch");
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

         monitor.beginTask(getName(), totalWork);

         IOperation op = new UpdateRelationsSortOrder();
         monitor.setTaskName("Executing: [UpdateRelationsSortOrder]");
         op.execute(monitor, connection, startAtTxNumber);
         monitor.setTaskName("");

      } finally {
         if (connection != null) {
            connection.close();
         }
      }

   }
   private interface IOperation {
      int getTotalWork();

      void execute(IProgressMonitor monitor, Connection connection, int startAtTxNumber) throws Exception;
   }

   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XText\" displayName=\"Branch List\" /><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Parent Branch\" /></xWidgets>";
   }
   private final class UpdateRelationsSortOrder implements IOperation {
      int totalModCount = 0;

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.blam.operation.UpdateCurrentColumn.IOperation#execute(java.sql.Connection, int)
       */
      @Override
      public void execute(IProgressMonitor monitor, Connection connection, int startAtTxNumber) throws Exception {
         IProgressMonitor subMonitor = new SubProgressMonitor(monitor, getTotalWork());
         totalModCount = 0;
         subMonitor.beginTask("Update Relation Sort Order", getTotalWork());
         updateRelationsSortOrder(subMonitor, connection, "B side", SELECT_B_RELATION_ORDER, UPDATE_B_ORDER);
         updateRelationsSortOrder(subMonitor, connection, "A side", SELECT_A_RELATION_ORDER, UPDATE_A_ORDER);
         subMonitor.done();
      }

      private void updateRelationsSortOrder(final IProgressMonitor monitor, final Connection connection, String name, String query, final String update) throws Exception {
         final List<Object[]> batchArgs = new ArrayList<Object[]>();
         final RelationOrderTracker relationOrderTracker = new RelationOrderTracker();
         monitor.subTask(String.format("Updating [%s] sort order", name));
         executeQuery(monitor, connection, new IRowProcessor() {
            public void processRow(ResultSet resultSet) throws Exception {
               relationOrderTracker.processRow(resultSet);
               if (relationOrderTracker.isUpdateRequired()) {
                  if (monitor.isCanceled() != true && batchArgs.size() >= 100000) {
                     writeToDb(monitor, connection, update, batchArgs);
                     batchArgs.clear();
                  }
                  batchArgs.add(relationOrderTracker.getUpdateData());
               }
            }
         }, 5000, query, SQL3DataType.INTEGER, branchToSort.getBranchId());
         if (monitor.isCanceled() != true) {
            writeToDb(monitor, connection, update, batchArgs);
         }
         appendResultLine(String.format("Updated [%d] relation [%s] orders.\n", totalModCount, name));
         monitor.worked(1);
      }

      private void writeToDb(IProgressMonitor monitor, Connection connection, String update, List<Object[]> data) throws SQLException {
         int count = ConnectionHandler.runPreparedUpdate(connection, update, data);
         totalModCount += count;
         monitor.subTask(String.format("Updated [%d of %d] relation orders - overall [%d]", count, data.size(),
               totalModCount));
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.blam.operation.UpdateCurrentColumn.IOperation#getTotalWork()
       */
      @Override
      public int getTotalWork() {
         return 2;
      }
   }

   private final class RelationOrderTracker {
      int rel_link_type, art_id, branch_id, order;
      int rel_link_type_old = -1, art_id_old = -1, branch_id_old = -1;
      int new_order;
      int other_side_art_id = -1;
      long gammaId = -1;

      void processRow(ResultSet resultSet) throws SQLException {
         rel_link_type = resultSet.getInt(1);
         art_id = resultSet.getInt(2);
         branch_id = resultSet.getInt(3);
         order = resultSet.getInt(4);
         gammaId = resultSet.getLong(5);
         if ((rel_link_type != rel_link_type_old || art_id != art_id_old || branch_id != branch_id_old)) {//then it's a new start of ordering
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