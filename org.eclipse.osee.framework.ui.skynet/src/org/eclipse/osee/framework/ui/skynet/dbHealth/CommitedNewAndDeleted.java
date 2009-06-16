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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;

/**
 * Identifies and removes addressing from the transaction table that no longer addresses other tables.
 * 
 * @author Theron Virgin
 */
public class CommitedNewAndDeleted extends DatabaseHealthOperation {
   private class LocalValues {
      public int relLinkId;
      public int artId;
      public int attributeId;
      public int gammaId;
      public int transactionId;
      public int branchId;

      public LocalValues(int artId, int attributeId, int branchId, int gammaId, int relLinkId, int transactionId) {
         super();
         this.artId = artId;
         this.attributeId = attributeId;
         this.branchId = branchId;
         this.gammaId = gammaId;
         this.relLinkId = relLinkId;
         this.transactionId = transactionId;
      }
   }
   private static final String COMMITTED_NEW_AND_DELETED_ARTIFACTS =
         "SELECT txs1.gamma_id, txs1.transaction_id, det1.branch_id, art1.art_id, 0 as attr_id, 0 as rel_link_id FROM osee_tx_details det1, osee_txs txs1, osee_artifact_version art1 WHERE txs1.tx_current = " + TxChange.DELETED.getValue() + " AND det1.transaction_id = txs1.transaction_id AND txs1.gamma_id = art1.gamma_id  AND  NOT EXISTS (SELECT ('x') FROM osee_tx_details det2, osee_txs txs2, osee_artifact_version art2 WHERE txs2.mod_type != " + ModificationType.DELETED.getValue() + " AND det1.branch_id = det2.branch_id AND det2.transaction_id = txs2.transaction_id AND txs2.gamma_id = art2.gamma_id AND art2.art_id = art1.art_id)";
   private static final String COMMITTED_NEW_AND_DELETED_ATTRIBUTES =
         "SELECT txs1.gamma_id, txs1.transaction_id, det1.branch_id, 0 as art_id, att1.attr_id, 0 as rel_link_id FROM osee_tx_details det1, osee_txs txs1, osee_attribute att1, osee_branch brn WHERE txs1.tx_current = " + TxChange.DELETED.getValue() + " AND det1.transaction_id = txs1.transaction_id AND txs1.gamma_id = att1.gamma_id  AND  det1.branch_id = brn.branch_id AND brn.branch_type != " + BranchType.MERGE.getValue() + " AND NOT EXISTS (SELECT ('x') FROM osee_tx_details det2, osee_txs txs2, osee_attribute att2 WHERE txs2.mod_type != " + ModificationType.DELETED.getValue() + " AND det1.branch_id = det2.branch_id AND det2.transaction_id = txs2.transaction_id AND txs2.gamma_id = att2.gamma_id AND att2.attr_id = att1.attr_id)";
   private static final String COMMITTED_NEW_AND_DELETED_RELATIONS =
         "SELECT txs1.gamma_id, txs1.transaction_id, det1.branch_id, 0 as art_id, 0 as attr_id, rel1.rel_link_id FROM osee_tx_details det1, osee_txs txs1, osee_relation_link rel1 WHERE txs1.tx_current = " + TxChange.DELETED.getValue() + " AND det1.transaction_id = txs1.transaction_id AND txs1.gamma_id = rel1.gamma_id  AND  NOT EXISTS (SELECT ('x') FROM osee_tx_details det2, osee_txs txs2, osee_relation_link rel2 WHERE txs2.mod_type != " + ModificationType.DELETED.getValue() + " AND det1.branch_id = det2.branch_id AND det2.transaction_id = txs2.transaction_id AND txs2.gamma_id = rel2.gamma_id AND rel2.rel_link_id = rel1.rel_link_id)";
   private static final String REMOVE_NOT_ADDRESSED_GAMMAS =
         "DELETE FROM osee_txs WHERE gamma_id = ? AND transaction_id = ?";

   private static final String[] COLUMN_HEADER =
         {"Gamma Id", "Transaction Id", "Branch Id", "Art id", "Attribute Id", "Rel Link Id"};

   private Set<LocalValues> addressing = null;

   public CommitedNewAndDeleted() {
      super("Artifacts, Relation, Attributes that were Introduced on a Branch as Deleted");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation#doHealthCheck(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      boolean fix = isFixOperationEnabled();
      boolean verify = !fix;

      if (verify || addressing == null) {
         addressing = new HashSet<LocalValues>();
         monitor.subTask("Loading Artifacts that were Introduced as Deleted");
         loadData(COMMITTED_NEW_AND_DELETED_ARTIFACTS);
         checkForCancelledStatus(monitor);
         monitor.worked(calculateWork(0.20));

         monitor.subTask("Loading Attributes that were Introduced as Deleted");
         loadData(COMMITTED_NEW_AND_DELETED_ATTRIBUTES);
         checkForCancelledStatus(monitor);
         monitor.worked(calculateWork(0.20));

         monitor.subTask("Loading Relation Links that were Introduced as Deleted");
         loadData(COMMITTED_NEW_AND_DELETED_RELATIONS);
      } else {
         monitor.worked(calculateWork(0.40));
      }
      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(0.10));

      StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      sbFull.append(AHTML.addRowMultiColumnTable(COLUMN_HEADER));
      displayData(sbFull, getSummary(), verify);

      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(0.10));

      setItemsToFix(addressing != null ? addressing.size() : 0);

      if (fix) {
         List<Object[]> insertParameters = new LinkedList<Object[]>();
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

      if (isShowDetailsEnabled()) {
         sbFull.append(AHTML.endMultiColumnTable());
         XResultData rd = new XResultData();
         rd.addRaw(sbFull.toString());
         rd.report(getVerifyTaskName(), Manipulations.RAW_HTML);
      }
      monitor.worked(calculateWork(0.10));
   }

   private void displayData(StringBuffer sbFull, Appendable builder, boolean verify) throws IOException {
      int attributeCount = 0, artifactCount = 0, relLinkCount = 0;
      for (LocalValues value : addressing) {
         if (value.artId != 0) {
            artifactCount++;
         }
         if (value.attributeId != 0) {
            attributeCount++;
         }
         if (value.relLinkId != 0) {
            relLinkCount++;
         }
         sbFull.append(AHTML.addRowMultiColumnTable(new String[] {String.valueOf(value.gammaId),
               String.valueOf(value.transactionId), String.valueOf(value.branchId), String.valueOf(value.artId),
               String.valueOf(value.attributeId), String.valueOf(value.relLinkId)}));
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

   //LocalValues(int artId, int attributeId, int branchId, int gammaId, int relLinkId, int transactionId)
   private void loadData(String sql) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(sql);
         while (chStmt.next()) {
            addressing.add(new LocalValues(chStmt.getInt("art_id"), chStmt.getInt("attr_id"),
                  chStmt.getInt("branch_id"), chStmt.getInt("gamma_id"), chStmt.getInt("rel_link_id"),
                  chStmt.getInt("transaction_id")));
         }
      } finally {
         chStmt.close();
      }
   }
}
