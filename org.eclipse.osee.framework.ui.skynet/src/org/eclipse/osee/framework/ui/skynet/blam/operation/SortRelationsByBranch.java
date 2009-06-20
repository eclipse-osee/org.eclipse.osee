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
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Andrew M. Finkbeiner
 * @author Roberto E. Escobar
 */
public class SortRelationsByBranch extends AbstractBlam {
   private static final String UPDATE_B_ORDER = "update osee_relation_link set b_order = ? where gamma_id = ?";
   private static final String SELECT_B_RELATION_ORDER =
         "select rel1.rel_link_type_id,  rel1.a_art_id as art_id1, txd1.branch_id, rel1.b_order as order1, txs1.gamma_id, rel1.b_art_id as art_id2, rel1.a_order_value as order2 from osee_tx_details txd1, osee_relation_link rel1, osee_txs txs1 where txd1.branch_id = ? and txd1.transaction_id = txs1.transaction_id and txs1.gamma_id = rel1.gamma_id and txs1.tx_current = 1 order by txd1.branch_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.a_order_value";
   private static final String SELECT_A_RELATION_ORDER =
         "select rel1.rel_link_type_id,  rel1.b_art_id as art_id1, txd1.branch_id, rel1.a_order as order1, txs1.gamma_id, rel1.a_art_id as art_id2, rel1.b_order_value as order2 from osee_tx_details txd1, osee_relation_link rel1, osee_txs txs1 where txd1.branch_id = ? and txd1.transaction_id = txs1.transaction_id and txs1.gamma_id = rel1.gamma_id and txs1.tx_current = 1 order by txd1.branch_id, rel1.rel_link_type_id, rel1.b_art_id, rel1.b_order_value";
   private static final String UPDATE_A_ORDER = "update osee_relation_link set a_order = ? where gamma_id = ?";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getName()
    */
   @Override
   public String getName() {
      return "Sort Relations By Branch";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      int totalWork = 0;
      monitor.beginTask(getName(), totalWork);

      List<Branch> branchesToSort = BranchManager.getTopLevelBranches();

      for (Branch branch : branchesToSort) {
         IOperation op = new UpdateRelationsSortOrder(branch);
         monitor.setTaskName("Executing: [UpdateRelationsSortOrder] " + branch.getBranchName());
         op.execute(monitor, 0);
         monitor.setTaskName("");
      }
   }
   private interface IOperation {
      int getTotalWork();

      void execute(IProgressMonitor monitor, int startAtTxNumber) throws Exception;
   }

   private final class UpdateRelationsSortOrder implements IOperation {
      int totalModCount = 0;

      Branch branchToSort;

      /**
       * @param branch
       */
      public UpdateRelationsSortOrder(Branch branch) {
         branchToSort = branch;
      }

      @Override
      public void execute(IProgressMonitor monitor, int startAtTxNumber) throws Exception {
         IProgressMonitor subMonitor = new SubProgressMonitor(monitor, getTotalWork());
         totalModCount = 0;
         subMonitor.beginTask("Update Relation Sort Order", getTotalWork());
         updateRelationsSortOrder(subMonitor, "B side", SELECT_B_RELATION_ORDER, UPDATE_B_ORDER);
         updateRelationsSortOrder(subMonitor, "A side", SELECT_A_RELATION_ORDER, UPDATE_A_ORDER);
         subMonitor.done();
      }

      private void updateRelationsSortOrder(final IProgressMonitor monitor, String name, String query, final String update) throws OseeDataStoreException {
         final List<Object[]> batchArgs = new ArrayList<Object[]>();
         final RelationOrderTracker relationOrderTracker = new RelationOrderTracker();
         monitor.subTask(String.format("Updating [%s] sort order", name));

         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
         try {
            chStmt.runPreparedQuery(5000, query, branchToSort.getBranchId());
            while (chStmt.next()) {
               relationOrderTracker.processRow(chStmt);
               if (relationOrderTracker.isUpdateRequired()) {
                  if (monitor.isCanceled() != true && batchArgs.size() >= 100000) {
                     writeToDb(monitor, update, batchArgs);
                     batchArgs.clear();
                  }
                  batchArgs.add(relationOrderTracker.getUpdateData());
               }

               if (monitor.isCanceled()) {
                  break;
               }
            }
         } finally {
            chStmt.close();
         }

         if (monitor.isCanceled() != true) {
            writeToDb(monitor, update, batchArgs);
         }
         print(String.format("Updated [%d] relation [%s] orders.\n", totalModCount, name));
         monitor.worked(1);
      }

      private void writeToDb(IProgressMonitor monitor, String update, List<Object[]> data) throws OseeDataStoreException {
         int count = ConnectionHandler.runBatchUpdate(update, data);
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

      void processRow(ConnectionHandlerStatement chStmt) throws OseeDataStoreException {
         rel_link_type = chStmt.getInt("rel_link_type_id");
         art_id = chStmt.getInt("art_id1");
         branch_id = chStmt.getInt("branch_id");
         order = chStmt.getInt("order1");
         gammaId = chStmt.getLong("gamma_id");
         if ((rel_link_type != rel_link_type_old || art_id != art_id_old || branch_id != branch_id_old)) {//then it's a new start of ordering
            new_order = -1;
         } else {
            new_order = other_side_art_id;
         }

         rel_link_type_old = rel_link_type;
         art_id_old = art_id;
         branch_id_old = branch_id;
         other_side_art_id = chStmt.getInt("art_id2");
      }

      boolean isUpdateRequired() {
         return order == 0;
      }

      Object[] getUpdateData() {
         return new Object[] {new_order, gammaId};
      }
   }

   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }
}