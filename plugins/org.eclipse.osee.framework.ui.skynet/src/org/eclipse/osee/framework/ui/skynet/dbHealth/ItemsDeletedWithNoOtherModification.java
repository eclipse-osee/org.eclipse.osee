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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.framework.jdk.core.result.Manipulations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Theron Virgin
 */
public class ItemsDeletedWithNoOtherModification extends DatabaseHealthOperation {
   private class LocalValues {
      public long relLinkId;
      public ArtifactId artId;
      public AttributeId attributeId;
      public GammaId gammaId;
      public long transactionId;
      public BranchId branchUuid;

      public LocalValues(ArtifactId artId, AttributeId attributeId, BranchId branchUuid, GammaId gammaId, long relLinkId, long transactionId) {
         super();
         this.artId = artId;
         this.attributeId = attributeId;
         this.branchUuid = branchUuid;
         this.gammaId = gammaId;
         this.relLinkId = relLinkId;
         this.transactionId = transactionId;
      }
   }
   private static final String COMMITTED_NEW_AND_DELETED_ARTIFACTS =
      "SELECT txs1.gamma_id, txs1.transaction_id, txs1.branch_id, art1.art_id, 0 as attr_id, 0 as rel_link_id FROM osee_txs txs1, osee_artifact art1 WHERE txs1.tx_current = ? AND txs1.mod_type = ? AND txs1.gamma_id = art1.gamma_id AND NOT EXISTS (SELECT ('x') FROM osee_txs txs2, osee_artifact art2 WHERE txs2.mod_type != ? AND txs1.branch_id = txs2.branch_id AND txs2.gamma_id = art2.gamma_id AND art2.art_id = art1.art_id)";
   private static final String COMMITTED_NEW_AND_DELETED_ATTRIBUTES =
      "SELECT txs1.gamma_id, txs1.transaction_id, txs1.branch_id, 0 as art_id, att1.attr_id, 0 as rel_link_id FROM osee_txs txs1, osee_attribute att1, osee_branch brn WHERE txs1.tx_current = ? AND txs1.mod_type = ? AND txs1.gamma_id = att1.gamma_id AND txs1.branch_id = brn.branch_id AND brn.branch_type != " + BranchType.MERGE.getIdString() + " AND NOT EXISTS (SELECT ('x') FROM osee_txs txs2, osee_attribute att2 WHERE txs2.mod_type != ? AND txs1.branch_id = txs2.branch_id AND txs2.gamma_id = att2.gamma_id AND att2.attr_id = att1.attr_id)";
   private static final String COMMITTED_NEW_AND_DELETED_RELATIONS =
      "SELECT txs1.gamma_id, txs1.transaction_id, txs1.branch_id, 0 as art_id, 0 as attr_id, rel1.rel_link_id FROM osee_txs txs1, osee_relation_link rel1 WHERE txs1.tx_current = ? AND txs1.mod_type = ? AND txs1.gamma_id = rel1.gamma_id AND NOT EXISTS (SELECT ('x') FROM osee_txs txs2, osee_relation_link rel2 WHERE txs2.mod_type != ? AND txs1.branch_id = txs2.branch_id AND txs2.gamma_id = rel2.gamma_id AND rel2.rel_link_id = rel1.rel_link_id)";

   private static final String REMOVE_NOT_ADDRESSED_GAMMAS =
      "DELETE FROM osee_txs WHERE gamma_id = ? AND transaction_id = ?";

   private static final String[] COLUMN_HEADER =
      {"Gamma Id", "Transaction Id", "Branch Uuid", "Art id", "Attribute Id", "Rel Link Id"};

   private Set<LocalValues> addressing = null;

   public ItemsDeletedWithNoOtherModification() {
      super("Items marked as deleted or artifact deleted without other entries in txs");
   }

   private void loadData(String sql, TxCurrent txChange, ModificationType modificationType) {
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(sql, txChange, modificationType, modificationType);
         while (chStmt.next()) {
            addressing.add(new LocalValues(ArtifactId.valueOf(chStmt.getLong("art_id")),
               AttributeId.valueOf(chStmt.getLong("attr_id")), BranchId.valueOf(chStmt.getLong("branch_id")),
               GammaId.valueOf(chStmt.getLong("gamma_id")), chStmt.getLong("rel_link_id"),
               chStmt.getLong("transaction_id")));
         }
      } finally {
         chStmt.close();
      }
   }

   private void detectAndCollectErrors(IProgressMonitor monitor, TxCurrent txChange, ModificationType modificationType) {
      monitor.setTaskName("Loading Artifacts that were Introduced as Deleted");
      loadData(COMMITTED_NEW_AND_DELETED_ARTIFACTS, txChange, modificationType);
      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(0.20));

      monitor.setTaskName("Loading Attributes that were Introduced as Deleted");
      loadData(COMMITTED_NEW_AND_DELETED_ATTRIBUTES, txChange, modificationType);
      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(0.20));

      monitor.setTaskName("Loading Relation Links that were Introduced as Deleted");
      loadData(COMMITTED_NEW_AND_DELETED_RELATIONS, txChange, modificationType);
   }

   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      boolean verify = !isFixOperationEnabled();

      if (verify || addressing == null) {
         addressing = new HashSet<>();
         detectAndCollectErrors(monitor, TxCurrent.DELETED, ModificationType.DELETED);
         detectAndCollectErrors(monitor, TxCurrent.ARTIFACT_DELETED, ModificationType.ARTIFACT_DELETED);
      } else {
         monitor.worked(calculateWork(0.40));
      }
      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(0.10));

      StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      sbFull.append(AHTML.addRowMultiColumnTable(COLUMN_HEADER));
      if (addressing != null) {
         displayData(sbFull, getSummary(), verify);
      }

      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(0.10));

      setItemsToFix(addressing != null ? addressing.size() : 0);

      if (isFixOperationEnabled() && getItemsToFixCount() > 0) {
         List<Object[]> insertParameters = new LinkedList<>();
         for (LocalValues value : addressing) {
            insertParameters.add(new Object[] {value.gammaId, value.transactionId});
         }
         if (insertParameters.size() > 0) {
            ConnectionHandler.runBatchUpdate(REMOVE_NOT_ADDRESSED_GAMMAS, insertParameters);
         }
         monitor.worked(calculateWork(0.30));
         addressing = null;
      } else {
         monitor.worked(calculateWork(0.30));
      }

      sbFull.append(AHTML.endMultiColumnTable());
      XResultData rd = new XResultData();
      rd.addRaw(sbFull.toString());
      XResultDataUI.report(rd, getVerifyTaskName(), Manipulations.RAW_HTML);
      monitor.worked(calculateWork(0.10));
   }

   private void displayData(StringBuffer sbFull, Appendable builder, boolean verify) throws IOException {
      int attributeCount = 0, artifactCount = 0, relLinkCount = 0;
      for (LocalValues value : addressing) {
         if (value.artId != ArtifactId.valueOf(0)) {
            artifactCount++;
         }
         if (value.attributeId != AttributeId.valueOf(0)) {
            attributeCount++;
         }
         if (value.relLinkId != 0) {
            relLinkCount++;
         }
         sbFull.append(AHTML.addRowMultiColumnTable(new String[] {
            String.valueOf(value.gammaId),
            String.valueOf(value.transactionId),
            String.valueOf(value.branchUuid),
            String.valueOf(value.artId),
            String.valueOf(value.attributeId),
            String.valueOf(value.relLinkId)}));
      }
      builder.append(verify ? "Found " : "Fixed ");
      builder.append(String.valueOf(artifactCount));
      builder.append(" Artifacts that were Introduced as Deleted\n");
      builder.append(verify ? "Found " : "Fixed ");
      builder.append(String.valueOf(attributeCount));
      builder.append(" Attributes that were Introduced as Deleted\n");
      builder.append(verify ? "Found " : "Fixed ");
      builder.append(String.valueOf(relLinkCount));
      builder.append(" Relation Links that were Introduced as Deleted\n");
   }

   @Override
   public String getCheckDescription() {
      return "Detects items from txs table with tx_current of (deleted or artifact deleted) having no other entries in the txs not equal to mod type (deleted or artifact deleted), respectively.";
   }

   @Override
   public String getFixDescription() {
      return "Remove addressing with delete errors.";
   }

}
