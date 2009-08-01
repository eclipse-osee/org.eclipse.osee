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

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;

/**
 * Check only, no fix.
 * 
 * @author Theron Virgin
 */
public class MultipleParentRelations extends DatabaseHealthOperation {
   private class LocalRelationLink {
      public int relLinkId1;
      public int relLinkId2;
      public int transactionId1;
      public int transactionId2;
      public int gammaId1;
      public int gammaId2;
      public int branchId;
      public String branch;
      public int parentArtId1;
      public int parentArtId2;
      public int childArtId;
      public String parentArt1;
      public String parentArt2;
      public String childArt;
      public Timestamp time1;
      public Timestamp time2;
      public int archived;
      public boolean deleteByTimeStamp;
      public String author1;
      public String author2;

      public LocalRelationLink(int branchId, int childArtId, int gammaId1, int gammaId2, int parentArtId1, int parentArtId2, int relLinkId1, int relLinkId2, Timestamp time1, Timestamp time2, int transactionId1, int transactionId2, int archived) {
         super();
         this.branchId = branchId;
         this.childArtId = childArtId;
         this.gammaId1 = gammaId1;
         this.gammaId2 = gammaId2;
         this.parentArtId1 = parentArtId1;
         this.parentArtId2 = parentArtId2;
         this.relLinkId1 = relLinkId1;
         this.relLinkId2 = relLinkId2;
         this.time1 = time1;
         this.time2 = time2;
         this.transactionId1 = transactionId1;
         this.transactionId2 = transactionId2;
         this.archived = archived;
         this.deleteByTimeStamp = true;
         author1 = "";
         author2 = "";
      }
   }

   private static final String GET_DUPLICATE_DEFAULT_HIER_LINKS =
         "SELECT rel1.rel_link_id as Link_ID_1, rel2.rel_link_id as Link_ID_2, rel1.a_art_id As Parent_Id_1, rel2.a_art_id As Parent_Id_2, rel2.b_art_id As Child_ID, det1.branch_id, det1.time as Time_1 , det2.time as Time_2, bra.archived, txs1.gamma_id as Gamma1, txs2.gamma_id as Gamma2, txs1.transaction_id as transaction_1, txs2.transaction_id as transaction_2 FROM osee_branch bra, osee_relation_link rel1, osee_relation_link rel2, osee_txs txs1, osee_txs txs2, osee_tx_details det1, osee_tx_details det2, osee_relation_link_type typ where typ.type_name = 'Default Hierarchical' AND rel1.rel_link_type_id = typ.rel_link_type_id AND rel2.rel_link_type_id = typ.rel_link_type_id AND rel1.a_art_id < rel2.a_art_id AND rel1.b_art_id = rel2.b_art_id AND rel1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs2.tx_current = 1 AND txs1.transaction_id = det1.transaction_id AND det1.branch_id = det2.branch_id AND det2.transaction_id = txs2.transaction_id AND txs2.gamma_id = rel2.gamma_id AND bra.branch_id = det1.branch_id order by rel1.b_art_id, txs1.transaction_id";

   private static final String GET_AUTHOR =
         "Select attr.Value FROM osee_attribute attr, osee_tx_details det, osee_attribute_type typ, osee_txs txs WHERE det.transaction_id = ? AND det.author = attr.art_id AND attr.attr_type_id = typ.attr_type_id AND typ.name = 'Name' AND attr.gamma_id = txs.gamma_id and txs.tx_current = 1";

   private static final String GET_ARTIFACT_NAME =
         "Select attr.Value FROM osee_attribute attr, osee_attribute_type typ, osee_txs txs WHERE attr.art_id  = ? AND attr.attr_type_id = typ.attr_type_id AND typ.name = 'Name' AND attr.gamma_id = txs.gamma_id and txs.tx_current = 1";

   private static final String GET_BRANCH_NAME = "Select branch_name from osee_branch where branch_id = ?";

   private static final String[] columnHeaders =
         new String[] {"Rel Link ID 1", "Rel Link ID 2", "Parent Art ID 1", "P1 Art Name", "Parent Art ID 2",
               "P2 Art Name", "Child Art ID", "Child Art Name", "Branch Ids", "Branch Name", "Archived", "Author 1",
               "Author 2"};

   private static final String HEADER = "Artifacts that have multiple Parents";

   private List<LocalRelationLink> relations = null;

   public MultipleParentRelations() {
      super("Multiple Parent Relations");
   }

   @Override
   public String getFixTaskName() {
      return Strings.emptyString();
   }

   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      if (relations == null) {
         relations = new LinkedList<LocalRelationLink>();
         monitor.subTask("Finding Artifacts with Multiple Parents");
         loadData();
      }
      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(0.20));

      Map<Integer, List<Integer>> branches = new HashMap<Integer, List<Integer>>();
      createAndDisplayReport(monitor, !isFixOperationEnabled(), branches);
      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(0.20));

      setItemsToFix(relations != null ? relations.size() : 0);

      monitor.worked(calculateWork(0.50));

      getSummary().append(
            String.format("%s %d Artifacts with multiple Parents on %d total branches.\n",
                  isFixOperationEnabled() ? "Fixed" : "Found", branches.size(), relations.size()));
      monitor.worked(calculateWork(0.10));
   }

   private void createAndDisplayReport(IProgressMonitor monitor, boolean isVerify, Map<Integer, List<Integer>> branches) throws OseeCoreException {
      List<Integer> linksfound = new LinkedList<Integer>();
      monitor.subTask("Finding Authors");
      for (LocalRelationLink link : relations) {
         List<Integer> branchs = branches.get(link.relLinkId1);
         if (branchs == null) {
            branchs = new LinkedList<Integer>();
            branches.put(link.relLinkId1, branchs);
         }
         branchs.add(link.branchId);
         if (!link.time1.equals(link.time2)) {
            linksfound.add(link.relLinkId1);
            linksfound.add(link.relLinkId2);
            setAuthors(link);
            setData(link);
         }
      }
      for (LocalRelationLink link : relations) {
         if (link.time1.equals(link.time2)) {
            if (!(linksfound.contains(link.relLinkId1) && linksfound.contains(link.relLinkId2))) {
               link.author1 = "baseline not found";
               link.author2 = "baseline not found";
               setData(link);
               linksfound.add(link.relLinkId1);
               linksfound.add(link.relLinkId2);
            }

         }
      }
      StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      sbFull.append(AHTML.beginMultiColumnTable(100, 1));
      sbFull.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
      displayData(sbFull, getSummary(), isVerify, false, branches);
      sbFull.append(AHTML.endMultiColumnTable());
      XResultData rd = new XResultData();
      rd.addRaw(sbFull.toString());
      rd.report(getVerifyTaskName(), Manipulations.RAW_HTML);

      sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      sbFull.append(AHTML.beginMultiColumnTable(100, 1));
      sbFull.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
      displayData(sbFull, getSummary(), isVerify, true, branches);
      sbFull.append(AHTML.endMultiColumnTable());
      rd = new XResultData();
      rd.addRaw(sbFull.toString());
      rd.report(getVerifyTaskName() + " Verbose", Manipulations.RAW_HTML);
   }

   //{"Rel Link ID 1", "Rel Link ID 2", "Parent Art ID 1", "Parent Art ID 2", "Child Art ID",
   //   "Branch_id", "Archived"};
   private void displayData(StringBuffer sbFull, Appendable builder, boolean verify, boolean displayAll, Map<Integer, List<Integer>> branches) {
      int count = 0;
      sbFull.append(AHTML.addRowSpanMultiColumnTable(HEADER, columnHeaders.length));
      for (LocalRelationLink relLink : relations) {
         if (!relLink.author1.equals("")) {
            count++;
            sbFull.append(AHTML.addRowMultiColumnTable(new String[] {Integer.toString(relLink.relLinkId1),
                  Integer.toString(relLink.relLinkId2), Integer.toString(relLink.parentArtId1), relLink.parentArt1,
                  Integer.toString(relLink.parentArtId2), relLink.parentArt2, Integer.toString(relLink.childArtId),
                  relLink.childArt,
                  displayAll ? branches.get(relLink.relLinkId1).toString() : Integer.toString(relLink.branchId),
                  relLink.branch, Integer.toString(relLink.archived), relLink.author1, relLink.author2}));
         }
      }
   }

   //public LocalRelationLink(int branchId, int childArtId, int gammaId1, int gammaId2, int parentArtId1, int parentArtId2, 
   //int relLinkId1, int relLinkId2, Timestamp time1, Timestamp time2, int transactionId1, int transactionId2) {

   private void loadData() throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(GET_DUPLICATE_DEFAULT_HIER_LINKS);
         while (chStmt.next()) {
            relations.add(new LocalRelationLink(chStmt.getInt("branch_id"), chStmt.getInt("child_id"),
                  chStmt.getInt("gamma1"), chStmt.getInt("gamma2"), chStmt.getInt("parent_id_1"),
                  chStmt.getInt("parent_id_2"), chStmt.getInt("link_id_1"), chStmt.getInt("link_id_2"),
                  chStmt.getTimestamp("time_1"), chStmt.getTimestamp("time_2"), chStmt.getInt("transaction_1"),
                  chStmt.getInt("transaction_2"), chStmt.getInt("archived")));
         }
      } finally {
         chStmt.close();
      }
   }

   private void setAuthors(LocalRelationLink link) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(GET_AUTHOR, link.transactionId1);
         if (chStmt.next()) {
            link.author1 = chStmt.getString("value");
         }
      } finally {
         chStmt.close();
      }
      try {
         chStmt.runPreparedQuery(GET_AUTHOR, link.transactionId2);
         if (chStmt.next()) {
            link.author2 = chStmt.getString("value");
         }
      } finally {
         chStmt.close();
      }
   }

   private void setData(LocalRelationLink link) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(GET_ARTIFACT_NAME, link.parentArtId1);
         if (chStmt.next()) {
            link.parentArt1 = chStmt.getString("value");
         }
      } finally {
         chStmt.close();
      }
      try {
         chStmt.runPreparedQuery(GET_ARTIFACT_NAME, link.parentArtId2);
         if (chStmt.next()) {
            link.parentArt2 = chStmt.getString("value");
         }
      } finally {
         chStmt.close();
      }
      try {
         chStmt.runPreparedQuery(GET_ARTIFACT_NAME, link.childArtId);
         if (chStmt.next()) {
            link.childArt = chStmt.getString("value");
         }
      } finally {
         chStmt.close();
      }
      try {
         chStmt.runPreparedQuery(GET_BRANCH_NAME, link.branchId);
         if (chStmt.next()) {
            link.branch = chStmt.getString("branch_name");
         }

      } finally {
         chStmt.close();
      }
   }

   @Override
   public String getCheckDescription() {
      return "Verfies that artifact has only a single default hierarchical related parent.";
   }

   @Override
   public String getFixDescription() {
      return "";
   }

}
