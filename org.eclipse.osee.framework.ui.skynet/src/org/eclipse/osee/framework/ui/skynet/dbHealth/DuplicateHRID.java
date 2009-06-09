/*
 * Created on Jun 8, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.dbHealth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;

/**
 * @author Megumi Telles
 */
public class DuplicateHRID extends DatabaseHealthOperation {

   private class DuplicateHRIDs {
      protected String guid;
      protected String hrid;
      protected String artTypeName;
   }

   private class dupHridsSorter implements Comparator<DuplicateHRIDs> {
      public dupHridsSorter() {
         super();
      }

      @Override
      public int compare(DuplicateHRIDs o1, DuplicateHRIDs o2) {
         String str1 = o1.hrid;
         String str2 = o2.hrid;
         return str1.compareTo(str2);
      }
   }

   private static final String GET_DUPLICATE_HRIDS =
         "SELECT t1.guid,  t1.human_readable_id,  t3.name FROM osee_artifact t1, osee_artifact_type t3 WHERE t1.human_readable_id IN  (SELECT t2.human_readable_id    FROM osee_artifact t2   GROUP BY t2.human_readable_id HAVING COUNT(t2.human_readable_id) > 1) AND t3.art_type_id = t1.art_type_id ORDER BY t1.human_readable_id";

   private static final String GET_ATTR = "SELECT * from osee_attribute where value like ?";
   private static final String GET_COMMENT = "SELECT * from osee_tx_details where osee_comment like ?";
   private static final String GET_BRANCH_NAME = "SELECT * from osee_branch where branch_name like ?";
   private static final String FIX_HRID = "UPDATE osee_artifact set human_readable_id=? where guid=?";

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
            duplicateHrid.guid = chStmt1.getString("guid");
            duplicateHrid.hrid = chStmt1.getString("human_readable_id");
            duplicateHrid.artTypeName = chStmt1.getString("name");
            diffValues.add(duplicateHrid);
         }
         Collections.sort(diffValues, new dupHridsSorter());
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
            String[] columnHeaders =
                  new String[] {"GUID", "HRID", "Artifact Type", "Attribute Hits", "Branch Hits", "Comment Hits"};
            rd.addRaw(AHTML.beginMultiColumnTable(100, 1));
            rd.addRaw(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
            for (DuplicateHRIDs dup : diffValues) {
               rd.addRaw(AHTML.addRowMultiColumnTable(new String[] {dup.guid, dup.hrid, dup.artTypeName,
                     String.valueOf(getAdditionalCounts(GET_ATTR, dup.hrid)),
                     String.valueOf(getAdditionalCounts(GET_BRANCH_NAME, dup.hrid)),
                     String.valueOf(getAdditionalCounts(GET_COMMENT, dup.hrid))}));
            }
            rd.addRaw(AHTML.endMultiColumnTable());
         } finally {
            if (isShowDetailsEnabled()) {
               rd.report(getVerifyTaskName());
            }
         }
      }
   }

   private String getAdditionalCounts(String statement, String hrid) {
      ConnectionHandlerStatement chStmt1 = new ConnectionHandlerStatement();
      int count = 0;
      try {
         chStmt1.runPreparedQuery(10, statement, new Object[] {"%" + hrid + "%"});
         while (chStmt1.next()) {
            count++;
         }
         return String.valueOf(count);
      } catch (OseeDataStoreException ex) {
         OseeLog.log(DuplicateHRID.class, Level.SEVERE, ex);
         return ex.getLocalizedMessage();
      } finally {
         chStmt1.close();
      }

   }
}
