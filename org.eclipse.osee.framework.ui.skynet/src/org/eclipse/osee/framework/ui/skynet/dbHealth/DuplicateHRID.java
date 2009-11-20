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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;

/**
 * @author Megumi Telles
 */
public class DuplicateHRID extends DatabaseHealthOperation {

   private class ArtifactData {
      protected String guid;
      protected String hrid;
      protected String artTypeName;
   }

   private class DuplicateHridSorter implements Comparator<ArtifactData> {
      public DuplicateHridSorter() {
         super();
      }

      @Override
      public int compare(ArtifactData o1, ArtifactData o2) {
         if (o1 == null || o2 == null) {
            return 0;
         }
         String str1 = o1.hrid;
         String str2 = o2.hrid;
         if (str1 == null || str2 == null) {
            return 0;
         }
         return str1.compareTo(str2);
      }
   }

   private static final String GET_DUPLICATE_HRIDS =
         "SELECT t1.guid,  t1.human_readable_id,  t3.name FROM osee_artifact t1, osee_artifact_type t3 WHERE t1.human_readable_id IN (SELECT t2.human_readable_id FROM osee_artifact t2 GROUP BY t2.human_readable_id HAVING COUNT(t2.human_readable_id) > 1) AND t3.art_type_id = t1.art_type_id ORDER BY t1.human_readable_id";

   private static final String COUNT_ATTRIBUTE_VALUES_CONTAINING =
         "SELECT count(1) from osee_attribute where value like ?"; // TODO value not necessarily in database
   private static final String COUNT_COMMENTS_CONTAINING =
         "SELECT count(1) from osee_tx_details where osee_comment like ?";
   private static final String COUNT_BRANCH_NAMES_CONTAINING =
         "SELECT count(1) from osee_branch where branch_name like ?";

   //   private static final String FIX_HRID = "UPDATE osee_artifact set human_readable_id=? where guid=?";

   public DuplicateHRID() {
      super("Duplicate HRID Errors");
   }

   @Override
   public String getFixTaskName() {
      return Strings.emptyString();
   }

   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      monitor.subTask("Querying for Duplicate Human Readable Ids");
      List<ArtifactData> diffValues = getDuplicateHRIDArtifacts(monitor);
      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(0.20));

      displayReport(monitor, diffValues, 0.20);

      checkForCancelledStatus(monitor);
      if (isFixOperationEnabled()) {
         //       monitor.worked(calculateWork(0.50));
      } else {
         monitor.worked(calculateWork(0.50));
      }

      getSummary().append(String.format("[%s] Duplicate Human Readable Ids found\n", diffValues.size()));
      monitor.worked(calculateWork(0.10));
   }

   private void displayReport(IProgressMonitor monitor, List<ArtifactData> duplicateHrids, double percentage) throws OseeCoreException {
      XResultData rd = new XResultData();
      Map<String, String[]> knownValues = new HashMap<String, String[]>();

      try {
         String[] columnHeaders =
               new String[] {"GUID", "HRID", "Artifact Type", "Attribute Hits", "Branch Hits", "Comment Hits"};
         rd.addRaw(AHTML.beginMultiColumnTable(100, 1));
         rd.addRaw(AHTML.addHeaderRowMultiColumnTable(columnHeaders));

         int totalAmount = calculateWork(percentage);
         if (!duplicateHrids.isEmpty()) {
            int stepAmount = totalAmount / duplicateHrids.size();
            for (ArtifactData dup : duplicateHrids) {
               checkForCancelledStatus(monitor);

               String[] results = knownValues.get(dup.hrid);

               if (results == null) {
                  results =
                        new String[] {String.valueOf(getAdditionalCounts(COUNT_ATTRIBUTE_VALUES_CONTAINING, dup.hrid)),
                              String.valueOf(getAdditionalCounts(COUNT_COMMENTS_CONTAINING, dup.hrid)),
                              String.valueOf(getAdditionalCounts(COUNT_BRANCH_NAMES_CONTAINING, dup.hrid))};
                  knownValues.put(dup.hrid, results);
               }
               rd.addRaw(AHTML.addRowMultiColumnTable(new String[] {dup.guid, dup.hrid, dup.artTypeName, results[0],
                     results[1], results[2]}));
               monitor.worked(stepAmount);
            }
         } else {
            monitor.worked(totalAmount);
         }
         rd.addRaw(AHTML.endMultiColumnTable());

      } finally {
         rd.report(getName());
      }
   }

   private List<ArtifactData> getDuplicateHRIDArtifacts(IProgressMonitor monitor) throws OseeDataStoreException {
      List<ArtifactData> duplicateItems = new LinkedList<ArtifactData>();
      IOseeStatement chStmt1 = ConnectionHandler.getStatement();
      try {
         chStmt1.runPreparedQuery(GET_DUPLICATE_HRIDS);
         while (chStmt1.next()) {
            checkForCancelledStatus(monitor);
            ArtifactData duplicateHrid = new ArtifactData();
            duplicateHrid.guid = chStmt1.getString("guid");
            duplicateHrid.hrid = chStmt1.getString("human_readable_id");
            duplicateHrid.artTypeName = chStmt1.getString("name");
            duplicateItems.add(duplicateHrid);
         }
         Collections.sort(duplicateItems, new DuplicateHridSorter());
      } finally {
         chStmt1.close();
      }
      return duplicateItems;
   }

   private String getAdditionalCounts(String query, String hrid) throws OseeDataStoreException {
      return String.valueOf(ConnectionHandler.runPreparedQueryFetchInt(-1, query, new Object[] {"%" + hrid + "%"}));
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
