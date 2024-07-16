/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.dbHealth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.framework.jdk.core.result.Manipulations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * {@link RelationIntegrityCheckTest }
 *
 * @author Theron Virgin
 */
public class RelationIntegrityCheck extends DatabaseHealthOperation {
   //@formatter:off
   private static final String DELETED_ARTIFACTS_QUERY =
      " SELECT    " +
      "     tx1.gamma_id, " +
      "     tx1.transaction_id, " +
      "     tx1.branch_id, " +
      "     rel1.rel_link_id, " +
      "     rel1.a_art_id, " +
      "     rel1.b_art_id, " +
      "     tx2.transaction_id AS deleted_tran, " +
      "     txd.commit_art_id AS commit_trans_art_id, " +
      "     tx1.mod_type AS creating_trans_mod_type " +
      " FROM " +
      "     osee_txs tx1, " +
      "     osee_txs tx2, " +
      "     osee_relation_link rel1, " +
      "     osee_artifact av1, " +
      "     osee_tx_details txd " +
      " WHERE " +
      "     tx1.branch_id = tx2.branch_id AND " +
      "     tx1.tx_current = " + TxCurrent.CURRENT + " AND " +
      "     tx2.tx_current = " + TxCurrent.DELETED + " AND " +
      "     tx1.gamma_id = rel1.gamma_id AND " +
      "     tx2.gamma_id = av1.gamma_id AND " +
      "     av1.art_id = rel1.%s AND " +
      "     tx1.transaction_id = txd.transaction_id ";
    //@formatter:on

   // relations that reference deleted artifacts on side A
   public static final String DELETED_A_ARTIFACTS = String.format(DELETED_ARTIFACTS_QUERY, "a_art_id");
   public static final String DELETED_B_ARTIFACTS = String.format(DELETED_ARTIFACTS_QUERY, "b_art_id");

   // relations that no artifact references
   //@formatter:off
   private static final String NO_ADDRESSING_QUERY =
      "SELECT " +
      "  tx1.gamma_id, " +
      "  tx1.transaction_id, " +
      "  rel1.rel_link_id, " +
      "  tx1.branch_id, " +
      "  rel1.a_art_id, " +
      "  rel1.b_art_id, " +
      "  0 AS deleted_tran " +
      "FROM " +
      "  osee_txs tx1, " +
      "  osee_relation_link rel1 " +
      "WHERE " +
      "  tx1.gamma_id = rel1.gamma_id AND " +
      "NOT EXISTS " +
      "  (SELECT " +
      "     'x' " +
      "  FROM " +
      "     osee_txs tx2, " +
      "     osee_artifact av1 " +
      "  WHERE " +
      "     tx1.branch_id = tx2.branch_id AND " +
      "     tx2.gamma_id = av1.gamma_id AND " +
      "     av1.art_id = rel1.%s)";
   //@formatter:on

   private static final String NO_ADDRESSING_ARTIFACTS_A = String.format(NO_ADDRESSING_QUERY, "a_art_id");
   private static final String NO_ADDRESSING_ARTIFACTS_B = String.format(NO_ADDRESSING_QUERY, "b_art_id");

   public static final String DEL_FROM_TXS_W_SPEC_BRANCH_ID =
      "DELETE FROM osee_txs where gamma_id = ? AND transaction_id = ? AND branch_id = ?";

   private static final String UPDATE_TXS_PREVIOUS =
      "UPDATE osee_txs SET tx_current = 0 WHERE gamma_id = ? AND transaction_id = ?";
   private static final String UPDATE_TXS_CURRENT =
      "UPDATE osee_txs SET tx_current = " + TxCurrent.ARTIFACT_DELETED + ", mod_type = " + ModificationType.ARTIFACT_DELETED.getIdString() + " WHERE gamma_id = ? AND transaction_id = ?";

   private static final String INSERT_TXS =
      "INSERT INTO osee_txs (gamma_id, transaction_id, tx_current, mod_type, branch_id) VALUES (?, ?, " + TxCurrent.ARTIFACT_DELETED + ", " + ModificationType.ARTIFACT_DELETED.getIdString() + ", ?)";

   private static final String[] COLUMN_HEADERS = {
      "Rel Link ID",
      "Gamma Id",
      "Transaction Id",
      "Branch_id",
      "A Art Id",
      "B Art Id",
      "Transaction ID of Deleted Artifact"};

   private static final String[] DESCRIPTION = {
      "Relation Links with non existent Artifacts on the Branch\n",
      "Relation Links with deleted Artifacts on the Branch\n",
      "Relation Links that have been created new on deleted artifacts \n",
      "Relation Links that are not yet known \n"};

   private static final String[] HEADER = {
      "%S Relation Links that have artifacts that don't exist on the branch",
      "%s Relation Links that have artifacts that are deleted on the branch",
      "%s Relation Links that have been created new on deleted artifacts (subset)",
      "%s Relation Links that are unknown (subset)"};

   private DoubleKeyHashMap<Long, Long, LocalRelationLink> deleteMap;
   private DoubleKeyHashMap<Long, Long, LocalRelationLink> updateMap;
   private IProgressMonitor monitor;
   private StringBuffer sbFull;
   private boolean fix;
   private boolean verify;

   //Containers for different cases
   private final Collection<LocalRelationLink> unExpectedCases = new ArrayList<>();
   private final Collection<LocalRelationLink> newRelationOnDeletedArtifact = new ArrayList<>();

   private List<Object[]> insertArtifactDeleted;
   private List<Object[]> updatePreviousAddressing;
   private List<Object[]> updateCurrentAddressing;
   private List<Object[]> commitOfNewRelationOnDeletedArtifactCases;

   public RelationIntegrityCheck() {
      super("Relation Integrity Errors");
   }

   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      init(monitor);
      loadBrokenRelations();
      classifyResults();
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

      unExpectedCases.clear();
      newRelationOnDeletedArtifact.clear();
   }

   private void loadBrokenRelations() {
      if (isLoadingBrokenRelationsNecessary()) {
         deleteMap = new DoubleKeyHashMap<>();
         updateMap = new DoubleKeyHashMap<>();

         loadData("Loading Relations with nonexistent artifacts on the A side", NO_ADDRESSING_ARTIFACTS_A, true);
         loadData("Loading Relations with nonexistent artifacts on the B side", NO_ADDRESSING_ARTIFACTS_B, true);
         loadData("Loading Relations with Deleted artifacts on the A side", DELETED_A_ARTIFACTS, false);
         loadData("Loading Relations with Deleted artifacts on the B side", DELETED_B_ARTIFACTS, false);
      } else {
         checkForCancelledStatus(monitor);
         monitor.worked(calculateWork(0.40));
      }
   }

   private void classifyResults() {

      insertArtifactDeleted = new LinkedList<>();
      updatePreviousAddressing = new LinkedList<>();
      updateCurrentAddressing = new LinkedList<>();
      commitOfNewRelationOnDeletedArtifactCases = new ArrayList<>();

      for (LocalRelationLink relLink : updateMap.allValues()) {
         if (relLink.relTransId.isGreaterThan(relLink.transIdForArtifactDeletion)) {

            //typically during a merge of a branch, see RelationIntegrityCheckTest.java
            if (relLink.commitTrans.isGreaterThan(TransactionId.valueOf(0)) && relLink.modType == 1) {
               commitOfNewRelationOnDeletedArtifactCases.add(
                  new Object[] {relLink.gammaId, relLink.relTransId, relLink.branch});
               newRelationOnDeletedArtifact.add(relLink);
            } else {
               unExpectedCases.add(relLink);
            }

         } else if (relLink.relTransId == relLink.transIdForArtifactDeletion) {
            updateCurrentAddressing.add(new Object[] {relLink.gammaId, relLink.relTransId});
         } else {
            updatePreviousAddressing.add(new Object[] {relLink.gammaId, relLink.relTransId});
            insertArtifactDeleted.add(
               new Object[] {relLink.gammaId, relLink.transIdForArtifactDeletion, relLink.branch});
         }
      }

   }

   private void fix() {

      //fix for NO_ADDRESSING_ARTIFACTS_A or NO_ADDRESSING_ARTIFACTS_B
      deleteInvalidRelationAddressing();

      runInsert(commitOfNewRelationOnDeletedArtifactCases, DEL_FROM_TXS_W_SPEC_BRANCH_ID,
         "Deleting TXs with relation links made with deleted artifacts.");
      runInsert(insertArtifactDeleted, INSERT_TXS, "Inserting Addressing for Deleted Artifacts");
      runInsert(updatePreviousAddressing, UPDATE_TXS_PREVIOUS, "Updating Addressing for Deleted Artifacts");
      runInsert(updateCurrentAddressing, UPDATE_TXS_CURRENT, "Updating Addressing for Deleted Artifacts");
      updateMap = null;

   }

   private void deleteInvalidRelationAddressing() {
      List<Object[]> rowsToDelete = new LinkedList<>();
      for (LocalRelationLink relLink : deleteMap.allValues()) {
         rowsToDelete.add(new Object[] {relLink.gammaId, relLink.relTransId, relLink.branch});
      }

      monitor.subTask("Deleting Relation Addressing with " + TxCurrent.DELETED + " Artifact");
      if (rowsToDelete.size() != 0) {
         ConnectionHandler.runBatchUpdate(DEL_FROM_TXS_W_SPEC_BRANCH_ID, rowsToDelete);
      }
      deleteMap = null;

      monitor.worked(calculateWork(0.10));
   }

   private void createReport() throws IOException {
      sbFull.append(AHTML.beginMultiColumnTable(100, 1));
      sbFull.append(AHTML.addHeaderRowMultiColumnTable(COLUMN_HEADERS));
      displayData(0, sbFull, getSummary(), verify, deleteMap);
      displayData(1, sbFull, getSummary(), verify, updateMap);
      displayData(2, sbFull, getSummary(), true, unExpectedCases);
      displayData(3, sbFull, getSummary(), true, newRelationOnDeletedArtifact);
      monitor.worked(calculateWork(0.10));
      checkForCancelledStatus(monitor);
   }

   private void runInsert(List<Object[]> insertParameters, String sql, String taskName) {
      monitor.subTask(taskName);
      if (insertParameters.size() != 0) {
         ConnectionHandler.runBatchUpdate(sql, insertParameters);
      }
      monitor.worked(calculateWork(0.10));
   }

   private void endReport() {
      sbFull.append(AHTML.endMultiColumnTable());
      XResultData rd = new XResultData();
      rd.addRaw(sbFull.toString());
      XResultDataUI.report(rd, getVerifyTaskName(), Manipulations.RAW_HTML);
      monitor.worked(calculateWork(0.10));
   }

   private void displayData(int x, StringBuffer sbFull, Appendable builder, boolean verify, DoubleKeyHashMap<Long, Long, LocalRelationLink> map) throws IOException {
      displayData(x, sbFull, builder, verify, map.allValues());
   }

   private void displayData(int x, StringBuffer sbFull, Appendable builder, boolean verify, Collection<LocalRelationLink> map) throws IOException {
      Integer count = 0;
      String header = String.format(HEADER[x], map.size());
      sbFull.append(AHTML.addRowSpanMultiColumnTable(header, COLUMN_HEADERS.length));
      for (LocalRelationLink relLink : map) {
         count++;
         sbFull.append(AHTML.addRowMultiColumnTable(new String[] {
            relLink.relLinkId.getIdString(),
            relLink.gammaId.getIdString(),
            relLink.relTransId.getIdString(),
            relLink.branch.getIdString(),
            relLink.aArtId.getIdString(),
            relLink.bArtId.getIdString(),
            relLink.transIdForArtifactDeletion.getIdString()}));
      }

      builder.append(verify ? "Found " : "Fixed ");
      builder.append(count.toString());
      builder.append(" ");
      builder.append(DESCRIPTION[x]);
   }

   private void loadData(String description, String sql, boolean forDelete) {
      monitor.subTask(description);
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      DoubleKeyHashMap<Long, Long, LocalRelationLink> map = forDelete ? deleteMap : updateMap;
      try {
         chStmt.runPreparedQuery(sql);
         while (chStmt.next()) {
            GammaId gamma_id = GammaId.valueOf(chStmt.getLong("gamma_id"));
            TransactionId transactionId = TransactionId.valueOf(chStmt.getLong("transaction_id"));
            RelationId relationId = RelationId.valueOf(chStmt.getLong("rel_link_id"));
            BranchId branch = BranchId.valueOf(chStmt.getLong("branch_id"));
            ArtifactId a_sideArtifactId = ArtifactId.valueOf(chStmt.getLong("a_art_id"));
            ArtifactId b_sideArtifactId = ArtifactId.valueOf(chStmt.getLong("b_art_id"));
            TransactionId deletedTransaction = TransactionId.valueOf(chStmt.getLong("deleted_tran"));

            TransactionId commitTransId =
               forDelete ? TransactionId.valueOf(0) : TransactionId.valueOf(chStmt.getLong("commit_trans_art_id"));
            int modType = forDelete ? -1 : chStmt.getInt("creating_trans_mod_type");

            if (!map.containsKey(gamma_id.getId(), transactionId.getId()) && (forDelete || !deleteMap.containsKey(
               gamma_id.getId(), transactionId.getId()))) {
               map.put(gamma_id.getId(), transactionId.getId(),
                  new LocalRelationLink(relationId, gamma_id, transactionId, branch, a_sideArtifactId, b_sideArtifactId,
                     deletedTransaction, commitTransId, modType));
            }
         }
      } finally {
         chStmt.close();
      }
      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(0.10));
   }

   private boolean isLoadingBrokenRelationsNecessary() {
      return verify || isFirstRun();
   }

   private boolean isFirstRun() {
      return updateMap == null || deleteMap == null;
   }

   @Override
   public String getCheckDescription() {
      return "Checks for relational link inconsistencies in the db.";
   }

   @Override
   public String getFixDescription() {
      return "Fixes inconsistencies between relational links and artifacts.";
   }
}
