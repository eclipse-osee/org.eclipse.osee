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
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;

/**
 * @author Theron Virgin
 */
public class DuplicateAttributes extends DatabaseHealthOperation {

   private static final String GET_DUPLICATE_ATTRIBUTES =
         "SELECT attr1.art_id, aty1.NAME, attr1.attr_id as attr_id_1, attr2.attr_id as attr_id_2, attr1.value as value_1, attr2.value as value_2, attr1.uri as uri_1, attr2.uri as uri_2, attr1.gamma_id as gamma_id_1, attr2.gamma_id as gamma_id_2 FROM osee_attribute attr1, osee_attribute attr2, osee_attribute_type  aty1 WHERE attr1.art_id = attr2.art_id AND attr1.attr_id < attr2.attr_id AND attr1.attr_type_id = attr2.attr_type_id AND attr1.attr_type_id = aty1.attr_type_id AND aty1.max_occurence = 1  AND EXISTS (SELECT 'x' FROM osee_txs txs1 WHERE txs1.gamma_id = attr1.gamma_id AND tx_current = 1) AND EXISTS (SELECT 'x' FROM osee_txs txs2 WHERE txs2.gamma_id = attr2.gamma_id and tx_current = 1) order by aty1.NAME, attr1.art_id";

   private static final String BRANCHES_WITH_ONLY_ATTR =
         "SELECT DISTINCT branch_id FROM osee_tx_details det WHERE EXISTS (SELECT 'x' FROM osee_txs txs, osee_attribute att WHERE det.transaction_id = txs.transaction_id AND txs.gamma_id = att.gamma_id AND att.attr_id = ?) %s (SELECT DISTINCT branch_id FROM osee_tx_details det WHERE EXISTS (SELECT 'x' FROM osee_txs txs, osee_attribute att WHERE det.transaction_id = txs.transaction_id AND txs.gamma_id = att.gamma_id AND att.attr_id = ?))";

   private static final String DELETE_ATTR = "DELETE FROM osee_attribute WHERE attr_id = ?";

   private static final String FILTER_DELTED =
         "SELECT * FROM osee_txs txs, osee_attribute atr WHERE txs.tx_current = 1 AND txs.gamma_id = atr.gamma_id AND atr.attr_id = ?";

   public DuplicateAttributes() {
      super("Duplicate Attribute Errors");
   }

   private DuplicateAttributeData createAttributeData(IOseeStatement chStmt) throws OseeDataStoreException {
      AttributeData attributeData1 =
            new AttributeData(chStmt.getInt("attr_id_1"), chStmt.getInt("gamma_id_1"), chStmt.getString("value_1"),
                  chStmt.getString("uri_1"));
      AttributeData attributeData2 =
            new AttributeData(chStmt.getInt("attr_id_2"), chStmt.getInt("gamma_id_2"), chStmt.getString("value_2"),
                  chStmt.getString("uri_2"));
      return new DuplicateAttributeData(chStmt.getInt("art_id"), chStmt.getString("name"), attributeData1,
            attributeData2);
   }

   private boolean isAttributeIdSetToCurrent(int attrId) throws OseeDataStoreException {
      return ConnectionHandler.runPreparedQueryFetchInt(-1, FILTER_DELTED, attrId) != -1;
   }

   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      List<DuplicateAttributeData> sameValues = new LinkedList<DuplicateAttributeData>();
      List<DuplicateAttributeData> diffValues = new LinkedList<DuplicateAttributeData>();

      monitor.subTask("Querying for Duplicate Attributes");

      //--- Test's for two attributes that are on the same artifact but have different attr_ids, when ---//
      //--- the attribute type has a maximum of 1 allowable attributes. ---------------------------------//

      IOseeStatement chStmt1 = ConnectionHandler.getStatement();
      try {
         chStmt1.runPreparedQuery(GET_DUPLICATE_ATTRIBUTES);
         monitor.worked(6);
         monitor.subTask("Processing Results");
         checkForCancelledStatus(monitor);
         while (chStmt1.next()) {
            DuplicateAttributeData duplicateAttribute = createAttributeData(chStmt1);
            checkForCancelledStatus(monitor);

            boolean isCurrentAtLeastOnceForAttrId1 =
                  isAttributeIdSetToCurrent(duplicateAttribute.getAttributeData1().getAttrId());
            checkForCancelledStatus(monitor);

            boolean isCurrentAtLeastOnceForAttrId2 =
                  isAttributeIdSetToCurrent(duplicateAttribute.getAttributeData2().getAttrId());
            checkForCancelledStatus(monitor);

            if (isCurrentAtLeastOnceForAttrId1 && isCurrentAtLeastOnceForAttrId2) {
               if (duplicateAttribute.areAttributeValuesEqual() && duplicateAttribute.areAttributeURIEqual()) {
                  sameValues.add(duplicateAttribute);
               } else {
                  diffValues.add(duplicateAttribute);
               }
            }
         }
      } finally {
         chStmt1.close();
      }

      monitor.worked(2);
      monitor.subTask("Cleaning Up Attrinbutes");
      checkForCancelledStatus(monitor);
      if (sameValues.isEmpty() && diffValues.isEmpty()) {
         getSummary().append("No Duplicate Attributes Found\n");
      } else {
         StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
         try {
            String[] columnHeaders =
                  new String[] {"Art Id", "Attr id 1", "Attr id 2", "Name", "Value 1", "Value 2", "URI 1", "URI 2",
                        "Gamma ID 1", "Gamma Id 2", "ID to Delete"};
            sbFull.append(AHTML.beginMultiColumnTable(100, 1));
            sbFull.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
            sbFull.append(AHTML.addRowSpanMultiColumnTable("Attributes with the same values", columnHeaders.length));
            int count = showAttributeCleanUpDecisions(sameValues, true, sbFull);
            sbFull.append(AHTML.addRowSpanMultiColumnTable("Attributes with different values", columnHeaders.length));
            count += showAttributeCleanUpDecisions(diffValues, false, sbFull);
            getSummary().append(String.format("Found %d duplicate attributes\n", count));
         } finally {
            sbFull.append(AHTML.endMultiColumnTable());
            XResultData rd = new XResultData();
            rd.addRaw(sbFull.toString());
            rd.report(getVerifyTaskName(), Manipulations.RAW_HTML);

         }
      }

   }

   private int showAttributeCleanUpDecisions(List<DuplicateAttributeData> values, boolean canFixAutomatically, StringBuffer builder) throws OseeDataStoreException {
      int count = 0;
      for (DuplicateAttributeData duplicate : values) {
         String fixMessage;
         if (canFixAutomatically) {
            AttributeData attributeToDelete = null;

            loadBranchesWhereOnlyOneIsUsed(duplicate.getAttributeData1(), duplicate.getAttributeData2().getAttrId());
            loadBranchesWhereOnlyOneIsUsed(duplicate.getAttributeData2(), duplicate.getAttributeData1().getAttrId());

            if (duplicate.getAttributeData1().isEmptyBranches()) {
               attributeToDelete = duplicate.getAttributeData1();
            } else if (duplicate.getAttributeData2().isEmptyBranches()) {
               attributeToDelete = duplicate.getAttributeData2();
            }

            if (attributeToDelete != null) {
               String prefix;
               if (isFixOperationEnabled()) {
                  ConnectionHandler.runPreparedUpdate(DELETE_ATTR, attributeToDelete.getAttrId());
                  prefix = "Fixed";
               } else {
                  prefix = "Needs Fix";
               }
               fixMessage = String.format("[%s] - %s ", attributeToDelete.getAttrId(), prefix);
            } else {
               fixMessage = "Attributes in Use";
            }
         } else {
            fixMessage = "Requires Hand Analysis";
         }

         AttributeData attributeData1 = duplicate.getAttributeData1();
         AttributeData attributeData2 = duplicate.getAttributeData2();
         builder.append(AHTML.addRowMultiColumnTable(new String[] {String.valueOf(duplicate.getArtId()),
               String.valueOf(attributeData1.getAttrId()), String.valueOf(attributeData2.getAttrId()), duplicate.name,
               attributeData1.getValue(), attributeData2.getValue(), attributeData1.getUri(), attributeData2.getUri(),
               String.valueOf(attributeData1.getGamma()), String.valueOf(attributeData2.getGamma()), fixMessage}));
         count++;
      }
      return count;
   }

   //--- Find out if there is an attribute that is on every branch that has either one of the attributes ---//
   private void loadBranchesWhereOnlyOneIsUsed(AttributeData attributeData, int otherAttrId) throws OseeDataStoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(String.format(BRANCHES_WITH_ONLY_ATTR, chStmt.getComplementSql()),
               attributeData.getAttrId(), otherAttrId);
         while (chStmt.next()) {
            attributeData.addBranchId(chStmt.getInt("branch_id"));
         }
      } finally {
         chStmt.close();
      }
   }

   @Override
   public String getCheckDescription() {
      return "Find duplicate attributes which are current and are used in the same branches.";
   }

   @Override
   public String getFixDescription() {
      return "Deletes attributes that have been identified as duplicates by attr id.";
   }

   private final class AttributeData {
      private final int attrId;
      private final String value;
      private final String uri;
      private final int gamma;
      private final List<Integer> branches;

      public AttributeData(int attrId, int gamma, String value, String uri) {
         super();
         this.attrId = attrId;
         this.value = value;
         this.uri = uri;
         this.gamma = gamma;
         this.branches = new LinkedList<Integer>();
      }

      public int getAttrId() {
         return attrId;
      }

      public String getValue() {
         return value;
      }

      public String getUri() {
         return uri;
      }

      public int getGamma() {
         return gamma;
      }

      public void addBranchId(Integer branchId) {
         branches.add(branchId);
      }

      public boolean isEmptyBranches() {
         return branches.isEmpty();
      }
   }

   private final class DuplicateAttributeData {
      private final AttributeData attributeData1;
      private final AttributeData attributeData2;

      private final int artId;
      private final String name;

      public DuplicateAttributeData(int artId, String name, AttributeData attributeData1, AttributeData attributeData2) {
         super();
         this.artId = artId;
         this.name = name;
         this.attributeData1 = attributeData1;
         this.attributeData2 = attributeData2;
      }

      public AttributeData getAttributeData1() {
         return attributeData1;
      }

      public AttributeData getAttributeData2() {
         return attributeData2;
      }

      public int getArtId() {
         return artId;
      }

      public String getName() {
         return name;
      }

      public boolean areAttributeValuesEqual() {
         return areEqual(attributeData1.getValue(), attributeData2.getValue());
      }

      public boolean areAttributeURIEqual() {
         return areEqual(attributeData1.getUri(), attributeData2.getUri());
      }

      private boolean areEqual(Object object1, Object object2) {
         return object1 != null && object2 != null && object1.equals(object2) || object1 == null && object2 == null;
      }
   }
}
