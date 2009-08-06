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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public class RepeatEnumerationAttributeValues extends DatabaseHealthOperation {

   private final static String FIND_REPEAT_ENUMS =
         "select art1.guid, att1.art_id, att1.value, att1.attr_type_id from osee_attribute att1, osee_attribute att2, osee_txs txs1, osee_txs txs2, osee_tx_details txd1, osee_tx_details txd2, osee_artifact art1 where att1.gamma_id = txs1.gamma_id and txs1.transaction_id = txd1.transaction_id and txd1.branch_id = ? and att2.gamma_id = txs2.gamma_id and txs2.transaction_id = txd2.transaction_id and txd2.branch_id = ? and att1.art_id = att2.art_id and att1.attr_id <> att2.attr_id and att1.value = att2.value and txs1.tx_current = " + TxChange.CURRENT.getValue() + " and txs2.tx_current = " + TxChange.CURRENT.getValue() + " and att1.attr_type_id = att2.attr_type_id and art1.art_id = att1.art_id order by att1.art_id,att1.attr_type_id,att1.value";

   public RepeatEnumerationAttributeValues() {
      super("Repeat Enumeration Attribute Values");
   }

   @Override
   public String getFixTaskName() {
      return "Delete Repeat Enumeration Attribute Values";
   }

   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      HashCollection<Branch, AttrData> attributesWithErrors = new HashCollection<Branch, AttrData>();
      List<Branch> branches = BranchManager.getBaselineBranches();
      if (branches.isEmpty()) {
         throw new OseeStateException("no branches found");
      }
      for (Branch branch : branches) {
         Collection<AttrData> datas = getRepeatEnumeratedAttrs(monitor, branch);
         if (!datas.isEmpty()) {
            attributesWithErrors.put(branch, datas);
         }
      }
      monitor.worked(calculateWork(0.40));

      appendToDetails(AHTML.beginMultiColumnTable(100, 1));
      appendToDetails(AHTML.addHeaderRowMultiColumnTable(new String[] {"GUID", "ATTR TYPE ID", "VALUE"}));
      for (Branch branch : attributesWithErrors.keySet()) {
         appendToDetails(AHTML.addRowSpanMultiColumnTable(branch.getName(), 3));
         for (AttrData attrData : attributesWithErrors.getValues(branch)) {
            appendToDetails(AHTML.addRowMultiColumnTable(new String[] {attrData.getArtifactGuid(),
                  AttributeTypeManager.getType(attrData.getAttributeTypeId()).getName(), attrData.getValue()}));
         }
      }
      appendToDetails(AHTML.endMultiColumnTable());

      monitor.worked(calculateWork(0.10));
      checkForCancelledStatus(monitor);

      setItemsToFix(attributesWithErrors.size());
      checkForCancelledStatus(monitor);
      if (isFixOperationEnabled() && hadItemsToFix()) {
         for (Branch branch : attributesWithErrors.keySet()) {
            Collection<AttrData> attributeData = attributesWithErrors.getValues(branch);
            List<String> artifactGuids = new ArrayList<String>(attributeData.size());
            for (AttrData attrData : attributeData) {
               artifactGuids.add(attrData.getArtifactGuid());
            }

            ArtifactQuery.getArtifactListFromIds(artifactGuids, branch, false); // bulk load for speed
            SkynetTransaction transaction =
                  new SkynetTransaction(branch, "Delete Repeat Attribute Values for" + branch.getShortName());
            for (AttrData attrData : attributeData) {
               Artifact artifact = ArtifactQuery.getArtifactFromId(attrData.getArtifactGuid(), branch);
               AttributeType attributeType = AttributeTypeManager.getType(attrData.getAttributeTypeId());
               if (attributeType.isEnumerated()) {
                  artifact.setAttributeValues(attributeType, artifact.getAttributesToStringList(attributeType));
                  transaction.addArtifact(artifact);
               }
            }
            transaction.execute();
         }
      }
      monitor.worked(calculateWork(0.40));

      getSummary().append(String.format("[%s] Repeat Enumeration Attribute Values found\n", getItemsToFixCount()));
      monitor.worked(calculateWork(0.10));
   }

   @Override
   public String getCheckDescription() {
      return "Searches for attributes of the same artifact having the same values in top level branches";
   }

   @Override
   public String getFixDescription() {
      return "Deletes the repeat attribute values using a transaction directly on the branch";
   }

   private Set<AttrData> getRepeatEnumeratedAttrs(IProgressMonitor monitor, Branch branch) throws OseeDataStoreException, OseeTypeDoesNotExist {
      Set<AttrData> attrData = new HashSet<AttrData>();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(FIND_REPEAT_ENUMS, branch.getBranchId(), branch.getBranchId());
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            attrData.add(new AttrData(chStmt.getString("guid"), chStmt.getInt("attr_type_id"),
                  chStmt.getString("value")));
         }
      } finally {
         chStmt.close();
      }
      return attrData;
   }

   private final class AttrData {
      private final String artifactGuid;
      private final int attributeTypeId;
      private final String value;

      public AttrData(String artifactGuid, int attributeTypeId, String value) {
         super();
         this.artifactGuid = artifactGuid;
         this.attributeTypeId = attributeTypeId;
         this.value = value;
      }

      public String getArtifactGuid() {
         return artifactGuid;
      }

      public int getAttributeTypeId() {
         return attributeTypeId;
      }

      public String getValue() {
         return value;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + (artifactGuid == null ? 0 : artifactGuid.hashCode());
         result = prime * result + attributeTypeId;
         result = prime * result + (value == null ? 0 : value.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         }
         AttrData other = (AttrData) obj;
         if (artifactGuid == null) {
            if (other.artifactGuid != null) {
               return false;
            }
         } else if (!artifactGuid.equals(other.artifactGuid)) {
            return false;
         }
         if (attributeTypeId != other.attributeTypeId) {
            return false;
         }
         if (value == null) {
            if (other.value != null) {
               return false;
            }
         } else if (!value.equals(other.value)) {
            return false;
         }
         return true;
      }
   }
}