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

import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;

/**
 * @author Theron Virgin
 */
public class RelationDatabaseIntegrityCheck extends DatabaseHealthTask {
   private class LocalRelationLink {
      public int relLinkId;
      public int gammaId;
      public int transactionId;
      public int branchId;
      public int aArtId;
      public int bArtId;
      public int transIdForArtifactDeletion;

      public LocalRelationLink(int relLinkId, int gammaId, int transactionId, int branchId, int aArtId, int bArtId, int transIdForArtifactDeletion) {
         super();
         this.aArtId = aArtId;
         this.bArtId = bArtId;
         this.branchId = branchId;
         this.gammaId = gammaId;
         this.relLinkId = relLinkId;
         this.transactionId = transactionId;
         this.transIdForArtifactDeletion = transIdForArtifactDeletion;
      }
   }

   private static final String NO_ADDRESSING_ARTIFACTS_A =
         "SELECT tx1.gamma_id, tx1.transaction_id, rel1.rel_link_id, td1.branch_id, rel1.a_art_id, rel1.b_art_id, 0 as deleted_tran FROM osee_txs tx1, osee_tx_details td1, osee_relation_link rel1 WHERE td1.transaction_id = tx1.transaction_id AND tx1.gamma_id = rel1.gamma_id AND not exists (select 'x' from osee_txs tx2, osee_tx_details td2, osee_artifact_version av1 where td1.branch_id = td2.branch_id and td2.transaction_id = tx2.transaction_id and tx2.gamma_id = av1.gamma_id and av1.art_id = rel1.a_art_id)";

   private static final String NO_ADDRESSING_ARTIFACTS_B =
         "SELECT tx1.gamma_id, tx1.transaction_id, rel1.rel_link_id, td1.branch_id, rel1.a_art_id, rel1.b_art_id, 0 as deleted_tran from osee_txs tx1, osee_tx_details td1, osee_relation_link rel1 where td1.transaction_id = tx1.transaction_id AND tx1.gamma_id = rel1.gamma_id AND not exists (select 'x' from osee_txs tx2, osee_tx_details td2, osee_artifact_version av1 where td1.branch_id = td2.branch_id and td2.transaction_id = tx2.transaction_id and tx2.gamma_id = av1.gamma_id and av1.art_id = rel1.b_art_id)";

   private static final String DELETED_A_ARTIFACTS =
         "SELECT tx1.gamma_id, tx1.transaction_id, rel1.rel_link_id, td1.branch_id, rel1.a_art_id, rel1.b_art_id, tx2.transaction_id as deleted_tran from osee_txs tx1, osee_txs tx2, osee_tx_details td1, osee_tx_details td2, osee_relation_link rel1, osee_artifact_version av1 WHERE tx1.transaction_id = td1.transaction_id and tx1.gamma_id = rel1.gamma_id and tx1.tx_current = 1 and td1.branch_id = td2.branch_id and td2.transaction_id = tx2.transaction_id and tx2.gamma_id = av1.gamma_id and tx2.tx_current = 2 and av1.art_id = rel1.a_art_id";

   private static final String DELETED_B_ARTIFACTS =
         "SELECT tx1.gamma_id, tx1.transaction_id, rel1.rel_link_id, td1.branch_id, rel1.a_art_id, rel1.b_art_id, tx2.transaction_id as deleted_tran from osee_txs tx1, osee_txs tx2, osee_tx_details td1, osee_tx_details td2, osee_relation_link rel1, osee_artifact_version av1 WHERE tx1.transaction_id = td1.transaction_id and tx1.gamma_id = rel1.gamma_id and tx1.tx_current = 1 and td1.branch_id = td2.branch_id and td2.transaction_id = tx2.transaction_id and tx2.gamma_id = av1.gamma_id and tx2.tx_current = 2 and av1.art_id = rel1.b_art_id";

   private static final String DELETE_FROM_TXS = "DELETE FROM osee_txs where gamma_id = ? AND  transaction_id = ?";

   private static final String UPDATE_TXS =
         "UPDATE osee_txs SET tx_current = 0 WHERE gamma_id = ? AND transaction_id = ?";

   private static final String UPDATE_TXS_SAME =
         "UPDATE osee_txs SET tx_current = " + TxChange.ARTIFACT_DELETED.getValue() + ", mod_type = " + ModificationType.ARTIFACT_DELETED.getValue() + " WHERE gamma_id = ? AND transaction_id = ?";

   private static final String INSERT_TXS =
         "INSERT INTO osee_txs (gamma_id, transaction_id, tx_current, mod_type) VALUES (?, ?, " + TxChange.ARTIFACT_DELETED.getValue() + ", " + ModificationType.ARTIFACT_DELETED.getValue() + ")";

   private static final String[] columnHeaders =
         new String[] {"Rel Link ID", "Gamma Id", "Transaction Id", "Branch_id", "A Art Id", "B Art Id",
               "Transaction ID of Deleted Artifact"};

   private static final String[] DESCRIPTION =
         {" Relation Links with non existant Artifacts on the Branch\n",
               " Relation Links with deleted Artifacts on the Branch\n"};

   private static final String[] HEADER =
         {"Relation Links that have artifacts that don't exist on the branch",
               "Relation Links that have artifacts that are deleted on the branch"};

   private DoubleKeyHashMap<Integer, Integer, LocalRelationLink> deleteMap = null;
   private DoubleKeyHashMap<Integer, Integer, LocalRelationLink> updateMap = null;

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
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthTask#run(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.core.runtime.IProgressMonitor, org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthTask.Operation, java.lang.StringBuilder, boolean)
    */
   @Override
   public void run(VariableMap variableMap, IProgressMonitor monitor, Operation operation, StringBuilder builder, boolean showDetails) throws Exception {
      StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      boolean fix = operation == Operation.Fix;
      boolean verify = !fix;
      monitor.beginTask(fix ? getFixTaskName() : getVerifyTaskName(), 100);

      if (verify || deleteMap == null) {
         deleteMap = new DoubleKeyHashMap<Integer, Integer, LocalRelationLink>();
         monitor.subTask("Loading Relations with non existant artifacts on the A side");
         loadData(NO_ADDRESSING_ARTIFACTS_A, true);
         monitor.worked(15);
         monitor.subTask("Loading Relations with non existant artifacts on the B side");
         loadData(NO_ADDRESSING_ARTIFACTS_B, true);
         monitor.worked(15);
      }
      if (verify || updateMap == null) {
         updateMap = new DoubleKeyHashMap<Integer, Integer, LocalRelationLink>();
         monitor.subTask("Loading Relations with Deleted artifacts on the A side");
         loadData(DELETED_A_ARTIFACTS, false);
         monitor.worked(15);
         monitor.subTask("Loading Relations with Deleted artifacts on the B side");
         loadData(DELETED_B_ARTIFACTS, false);
         monitor.worked(15);
      }

      sbFull.append(AHTML.beginMultiColumnTable(100, 1));
      sbFull.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
      displayData(0, sbFull, builder, verify, deleteMap);
      monitor.worked(10);
      displayData(1, sbFull, builder, verify, updateMap);
      monitor.worked(10);

      if (fix) {
         List<Object[]> insertParameters = new LinkedList<Object[]>();
         for (LocalRelationLink relLink : deleteMap.allValues()) {
            insertParameters.add(new Object[] {relLink.gammaId, relLink.transactionId});
         }
         monitor.subTask("Deleting Relation Addressing with non existant Artifacts");
         if (insertParameters.size() != 0) {
            ConnectionHandler.runBatchUpdate(DELETE_FROM_TXS, insertParameters);
         }
         deleteMap = null;
         monitor.worked(10);

         insertParameters.clear();
         List<Object[]> insertParametersInsert = new LinkedList<Object[]>();
         List<Object[]> insertParametersTransaction = new LinkedList<Object[]>();
         for (LocalRelationLink relLink : updateMap.allValues()) {
            insertParameters.add(new Object[] {relLink.gammaId, relLink.transactionId});
            if (relLink.transactionId == relLink.transIdForArtifactDeletion) {
               insertParametersTransaction.add(new Object[] {relLink.gammaId, relLink.transIdForArtifactDeletion});
            } else if (relLink.transactionId > relLink.transIdForArtifactDeletion) {
               insertParametersTransaction.add(new Object[] {relLink.gammaId, relLink.transactionId});
            } else {
               insertParametersInsert.add(new Object[] {relLink.gammaId, relLink.transIdForArtifactDeletion});
            }
         }

         monitor.subTask("Inserting Addressing for Deleted Artifacts");
         if (insertParametersInsert.size() != 0) {
            ConnectionHandler.runBatchUpdate(INSERT_TXS, insertParametersInsert);
         }
         monitor.worked(5);
         monitor.subTask("Updating Addressing for Deleted Artifacts");
         if (insertParameters.size() != 0) {
            ConnectionHandler.runBatchUpdate(UPDATE_TXS, insertParameters);
         }
         if (insertParametersTransaction.size() != 0) {
            ConnectionHandler.runBatchUpdate(UPDATE_TXS_SAME, insertParametersTransaction);
         }
         monitor.worked(5);
         updateMap = null;
      }

      if (showDetails) {
         sbFull.append(AHTML.endMultiColumnTable());
         XResultData rd = new XResultData();
         rd.addRaw(sbFull.toString());
         rd.report(getVerifyTaskName(), Manipulations.RAW_HTML);
      }
   }

   private void displayData(int x, StringBuffer sbFull, StringBuilder builder, boolean verify, DoubleKeyHashMap<Integer, Integer, LocalRelationLink> map) {
      int count = 0;
      sbFull.append(AHTML.addRowSpanMultiColumnTable(HEADER[x], columnHeaders.length));
      for (LocalRelationLink relLink : map.allValues()) {
         count++;
         sbFull.append(AHTML.addRowMultiColumnTable(new String[] {Integer.toString(relLink.relLinkId),
               Integer.toString(relLink.gammaId), Integer.toString(relLink.transactionId),
               Integer.toString(relLink.branchId), Integer.toString(relLink.aArtId), Integer.toString(relLink.bArtId),
               Integer.toString(relLink.transIdForArtifactDeletion)}));
      }

      builder.append(verify ? "Found " : "Fixed ");
      builder.append(count);
      builder.append(" ");
      builder.append(DESCRIPTION[x]);
   }

   private void loadData(String sql, boolean forDelete) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      DoubleKeyHashMap<Integer, Integer, LocalRelationLink> map = forDelete ? deleteMap : updateMap;
      try {
         chStmt.runPreparedQuery(sql);
         while (chStmt.next()) {
            if (!map.containsKey(chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")) && (forDelete || !deleteMap.containsKey(
                  chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")))) {
               map.put(chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id"), new LocalRelationLink(
                     chStmt.getInt("rel_link_id"), chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id"),
                     chStmt.getInt("branch_id"), chStmt.getInt("a_art_id"), chStmt.getInt("b_art_id"),
                     chStmt.getInt("deleted_tran")));
            }
         }
      } finally {
         chStmt.close();
      }
   }

}
