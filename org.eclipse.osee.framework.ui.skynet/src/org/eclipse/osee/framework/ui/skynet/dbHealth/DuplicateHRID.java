/*
 * Created on Jun 8, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.dbHealth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;

/**
 * @author Megumi Telles
 */
public class DuplicateHRID extends DatabaseHealthOperation {
   private class DuplicateHRIDs {
      protected int artId;
      protected int branchId;
      protected String hrid;
      protected Artifact art;
   }

   private static final String GET_DUPLICATE_HRIDS =
         "SELECT distinct t1.art_id, t3.branch_id, t1.human_readable_id from osee_artifact t1, osee_tx_details t3, osee_txs t4, osee_artifact_version t5 where  t3.transaction_id=t4.transaction_id and t4.gamma_id=t5.gamma_id and t3.osee_comment is null and t5.art_id=t1.art_id and t1.human_readable_id in (SELECT human_readable_id from osee_artifact t2 group by t2.human_readable_id having count(t2.human_readable_id)>1) order by t3.branch_id";
   boolean fixErrors = false;
   boolean processTxCurrent = true;

   public DuplicateHRID() {
      super("Duplicate HRID Errors");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation#doHealthCheck(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      LinkedList<DuplicateHRIDs> diffValues = new LinkedList<DuplicateHRIDs>();
      List<Artifact> artifacts = new ArrayList<Artifact>();
      ConnectionHandlerStatement chStmt1 = new ConnectionHandlerStatement();
      this.setShowDetailsEnabled(true);
      fixErrors = isFixOperationEnabled();
      monitor.beginTask("Clean Up Duplicate Human Readable Ids", 1);
      monitor.subTask("Querying for Duplicate Human Readable Ids");
      checkForCancelledStatus(monitor);
      try {
         chStmt1.runPreparedQuery(GET_DUPLICATE_HRIDS);
         monitor.worked(6);
         monitor.subTask("Processing Results");
         checkForCancelledStatus(monitor);
         while (chStmt1.next()) {
            DuplicateHRIDs duplicateHrid;
            duplicateHrid = new DuplicateHRIDs();
            duplicateHrid.artId = chStmt1.getInt("art_id");
            duplicateHrid.branchId = chStmt1.getInt("branch_id");
            duplicateHrid.hrid = chStmt1.getString("human_readable_id");
            artifacts =
                  ArtifactQuery.getArtifactsFromIds(Arrays.asList(duplicateHrid.artId),
                        BranchManager.getBranch(duplicateHrid.branchId), true);
            duplicateHrid.art = artifacts.get(0);
            diffValues.add(duplicateHrid);
         }
      } finally {
         chStmt1.close();
      }
      monitor.worked(2);
      monitor.subTask("Cleaning Up Duplicate HRIDs");
      checkForCancelledStatus(monitor);
      if (diffValues.isEmpty()) {
         getAppendable().append("No Duplicate Attributes Found\n");
      } else {
         XResultData rd = new XResultData();
         try {
            String[] columnHeaders = new String[] {"Artifact Id", "Branch Id", "HRID", "Artifact Type"};
            rd.addRaw(AHTML.beginMultiColumnTable(100, 1));
            rd.addRaw(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
            for (DuplicateHRIDs dup : diffValues) {
               rd.addRaw(AHTML.addRowMultiColumnTable(new String[] {Integer.toString(dup.artId),
                     Integer.toString(dup.branchId), XResultData.getHyperlinkForArtifactEditor(dup.hrid, dup.hrid),
                     dup.art.getArtifactTypeName()}));
            }
            rd.addRaw(AHTML.endMultiColumnTable());
         } finally {
            if (isShowDetailsEnabled()) {
               rd.report(getVerifyTaskName());
            }
         }
      }
   }
}
