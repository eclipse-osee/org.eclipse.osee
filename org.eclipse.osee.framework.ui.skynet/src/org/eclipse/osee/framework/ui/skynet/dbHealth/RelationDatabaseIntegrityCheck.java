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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;

/**
 * @author Theron Virgin
 */
public class RelationDatabaseIntegrityCheck extends DatabaseHealthOperation {
   private static class LocalRelationLink {
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
         "SELECT tx1.gamma_id, tx1.transaction_id, rel1.rel_link_id, tx1.branch_id, rel1.a_art_id, rel1.b_art_id, 0 AS deleted_tran FROM osee_txs tx1, osee_relation_link rel1 WHERE tx1.gamma_id = rel1.gamma_id AND NOT EXISTS (SELECT 'x' FROM osee_txs tx2, osee_artifact_version av1 WHERE tx1.branch_id = tx2.branch_id AND tx2.gamma_id = av1.gamma_id AND av1.art_id = rel1.a_art_id UNION ALL SELECT 'x' FROM osee_txs_archived ta, osee_artifact_version av1 WHERE tx1.branch_id = ta.branch_id AND ta.gamma_id = av1.gamma_id AND av1.art_id = rel1.a_art_id)";

   private static final String NO_ADDRESSING_ARTIFACTS_B =
         "SELECT tx1.gamma_id, tx1.transaction_id, rel1.rel_link_id, tx1.branch_id, rel1.a_art_id, rel1.b_art_id, 0 AS deleted_tran FROM osee_txs tx1, osee_relation_link rel1 WHERE tx1.gamma_id = rel1.gamma_id AND NOT EXISTS (SELECT 'x' FROM osee_txs tx2, osee_artifact_version av1 WHERE tx1.branch_id = tx2.branch_id AND tx2.gamma_id = av1.gamma_id AND av1.art_id = rel1.b_art_id UNION ALL SELECT 'x' FROM osee_txs_archived ta, osee_artifact_version av1 WHERE tx1.branch_id = ta.branch_id AND ta.gamma_id = av1.gamma_id AND av1.art_id = rel1.b_art_id)";

   private static final String DELETED_A_ARTIFACTS =
         "SELECT tx1.gamma_id, tx1.transaction_id, rel1.rel_link_id, tx1.branch_id, rel1.a_art_id, rel1.b_art_id, tx2.transaction_id AS deleted_tran FROM osee_txs tx1, osee_txs tx2, osee_relation_link rel1, osee_artifact_version av1 WHERE tx1.gamma_id = rel1.gamma_id AND tx1.tx_current = 1 AND tx1.branch_id = tx2.branch_id AND tx2.gamma_id = av1.gamma_id AND tx2.tx_current = 2 AND av1.art_id = rel1.a_art_id";

   private static final String DELETED_B_ARTIFACTS =
         "SELECT tx1.gamma_id, tx1.transaction_id, rel1.rel_link_id, tx1.branch_id, rel1.a_art_id, rel1.b_art_id, tx2.transaction_id AS deleted_tran FROM osee_txs tx1, osee_txs tx2, osee_relation_link rel1, osee_artifact_version av1 WHERE tx1.gamma_id = rel1.gamma_id AND tx1.tx_current = 1 AND tx1.branch_id = tx2.branch_id AND tx2.gamma_id = av1.gamma_id AND tx2.tx_current = 2 AND av1.art_id = rel1.b_art_id";

   private static final String DELETE_FROM_TXS = "DELETE FROM osee_txs where gamma_id = ? AND transaction_id = ?";
   private static final String DELETE_FROM_TX_ARCHIVED =
         "DELETE FROM osee_tx_archived where gamma_id = ? AND transaction_id = ?";

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
         {" Relation Links with non existent Artifacts on the Branch\n",
               " Relation Links with deleted Artifacts on the Branch\n"};

   private static final String[] HEADER =
         {"Relation Links that have artifacts that don't exist on the branch",
               "Relation Links that have artifacts that are deleted on the branch"};

   private DoubleKeyHashMap<Integer, Integer, LocalRelationLink> deleteMap = null;
   private DoubleKeyHashMap<Integer, Integer, LocalRelationLink> updateMap = null;
   private IProgressMonitor monitor;
   private StringBuffer sbFull = null;
   private boolean fix;
   private boolean verify;

   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      init(monitor);
      loadBrokenRelations();
      createReport();
      setItemsToFix(updateMap.size() + deleteMap.size());

      if (fix) {
         fix();
      } else {
         monitor.worked(calculateWork(0.40));
      }

      endReport();
   }

   private void init(IProgressMonitor monitor) {
      this.monitor = monitor;
      sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      fix = isFixOperationEnabled();
      verify = !fix;
   }

   public RelationDatabaseIntegrityCheck() {
      super("Relation Integrity Errors");
   }

   private void loadBrokenRelations() throws OseeCoreException {
      if (isLoadingBrokenRelationsNecessary()) {
         deleteMap = new DoubleKeyHashMap<Integer, Integer, LocalRelationLink>();
         updateMap = new DoubleKeyHashMap<Integer, Integer, LocalRelationLink>();

         loadData("Loading Relations with nonexistent artifacts on the A side", NO_ADDRESSING_ARTIFACTS_A, true);
         loadData("Loading Relations with nonexistent artifacts on the B side", NO_ADDRESSING_ARTIFACTS_B, true);
         loadData("Loading Relations with Deleted artifacts on the A side", DELETED_A_ARTIFACTS, false);
         loadData("Loading Relations with Deleted artifacts on the B side", DELETED_B_ARTIFACTS, false);
      } else {
         checkForCancelledStatus(monitor);
         monitor.worked(calculateWork(0.40));
      }
   }

   private boolean isLoadingBrokenRelationsNecessary() {
      return verify || isFirstRun();
   }

   private boolean isFirstRun() {
      return updateMap == null || deleteMap == null;
   }

   private void createReport() throws IOException {
      sbFull.append(AHTML.beginMultiColumnTable(100, 1));
      sbFull.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
      displayData(0, sbFull, getSummary(), verify, deleteMap);
      displayData(1, sbFull, getSummary(), verify, updateMap);
      monitor.worked(calculateWork(0.10));
      checkForCancelledStatus(monitor);
   }

   private void deleteInvalidRelationAddressing() throws OseeDataStoreException {
      List<Object[]> rowsToDelete = new LinkedList<Object[]>();
      for (LocalRelationLink relLink : deleteMap.allValues()) {
         rowsToDelete.add(new Object[] {relLink.gammaId, relLink.transactionId});
      }

      monitor.subTask("Deleting Relation Addressing with non existent Artifacts");
      if (rowsToDelete.size() != 0) {
         ConnectionHandler.runBatchUpdate(DELETE_FROM_TXS, rowsToDelete);
         ConnectionHandler.runBatchUpdate(DELETE_FROM_TX_ARCHIVED, rowsToDelete);
      }
      deleteMap = null;

      monitor.worked(calculateWork(0.10));
   }

   private void fix() throws OseeCoreException {
      deleteInvalidRelationAddressing();

      List<Object[]> insertArtifactDeleted = new LinkedList<Object[]>();
      List<Object[]> updateCurrentAddressing = new LinkedList<Object[]>();
      List<Object[]> updatePreviousAddressing = new LinkedList<Object[]>();
      for (LocalRelationLink relLink : updateMap.allValues()) {
         if (BranchManager.getBranch(relLink.branchId).getArchiveState().isUnArchived()) {
            updateCurrentAddressing.add(new Object[] {relLink.gammaId, relLink.transactionId});
            if (relLink.transactionId >= relLink.transIdForArtifactDeletion) {
               updatePreviousAddressing.add(new Object[] {relLink.gammaId, relLink.transactionId});
            } else {
               insertArtifactDeleted.add(new Object[] {relLink.gammaId, relLink.transIdForArtifactDeletion});
            }
         }
      }

      runInsert(insertArtifactDeleted, INSERT_TXS, "Inserting Addressing for Deleted Artifacts");
      runInsert(updateCurrentAddressing, UPDATE_TXS, "Updating Addressing for Deleted Artifacts");
      runInsert(updatePreviousAddressing, UPDATE_TXS_SAME, "Updating Addressing for Deleted Artifacts");
      updateMap = null;
   }

   private void runInsert(List<Object[]> insertParameters, String sql, String taskName) throws OseeDataStoreException {
      monitor.subTask(taskName);
      if (insertParameters.size() != 0) {
         ConnectionHandler.runBatchUpdate(sql, insertParameters);
      }
      monitor.worked(calculateWork(0.10));

   }

   private void endReport() throws OseeCoreException {
      sbFull.append(AHTML.endMultiColumnTable());
      XResultData rd = new XResultData();
      rd.addRaw(sbFull.toString());
      rd.report(getVerifyTaskName(), Manipulations.RAW_HTML);
      monitor.worked(calculateWork(0.10));
   }

   private void displayData(int x, StringBuffer sbFull, Appendable builder, boolean verify, DoubleKeyHashMap<Integer, Integer, LocalRelationLink> map) throws IOException {
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
      builder.append(String.valueOf(count));
      builder.append(" ");
      builder.append(DESCRIPTION[x]);
   }

   private void loadData(String description, String sql, boolean forDelete) throws OseeDataStoreException {
      monitor.subTask(description);
      IOseeStatement chStmt = ConnectionHandler.getStatement();
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
            } else {
               System.out.print("");
            }
         }
      } finally {
         chStmt.close();
      }
      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(0.10));
   }

   @Override
   public String getCheckDescription() {
      return "Enter Check Description Here";
   }

   @Override
   public String getFixDescription() {
      return "Enter Fix Description Here";
   }

}
