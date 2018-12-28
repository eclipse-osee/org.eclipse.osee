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

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public class RepeatEnumerationAttributeValues extends DatabaseHealthOperation {

   private final static String FIND_REPEAT_ENUMS =
      "select DISTINCT(art1.art_id), att1.art_id, att1.value, att1.attr_type_id from osee_attribute att1, osee_attribute att2, osee_txs txs1, osee_txs txs2, osee_artifact art1 where att1.gamma_id = txs1.gamma_id and txs1.branch_id = ? and att2.gamma_id = txs2.gamma_id and txs2.branch_id = ? and att1.art_id = att2.art_id and att1.attr_id <> att2.attr_id and att1.value = att2.value and txs1.tx_current = " + TxCurrent.CURRENT + " and txs2.tx_current = " + TxCurrent.CURRENT + " and att1.attr_type_id = att2.attr_type_id and art1.art_id = att1.art_id order by att1.art_id, att1.attr_type_id, att1.value";

   public RepeatEnumerationAttributeValues() {
      super("Repeat Enumeration Attribute Values");
   }

   @Override
   public String getFixTaskName() {
      return "Delete Repeat Enumeration Attribute Values";
   }

   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      HashCollectionSet<IOseeBranch, AttrData> attributesWithErrors = new HashCollectionSet<>(HashSet::new);
      List<? extends IOseeBranch> branches = BranchManager.getBaselineBranches();
      if (branches.isEmpty()) {
         throw new OseeStateException("no branches found");
      }
      for (IOseeBranch branch : branches) {
         Set<AttrData> datas = getRepeatEnumeratedAttrs(monitor, branch);
         if (!datas.isEmpty()) {
            attributesWithErrors.put(branch, datas);
         }
      }
      monitor.worked(calculateWork(0.40));

      appendToDetails(AHTML.beginMultiColumnTable(100, 1));
      appendToDetails(AHTML.addHeaderRowMultiColumnTable(new String[] {"GUID", "ATTR TYPE ID", "VALUE"}));
      for (IOseeBranch branch : attributesWithErrors.keySet()) {
         appendToDetails(AHTML.addRowSpanMultiColumnTable(branch.getName(), 3));
         for (AttrData attrData : attributesWithErrors.getValues(branch)) {
            appendToDetails(AHTML.addRowMultiColumnTable(new String[] {
               attrData.getArtifactId().toString(),
               AttributeTypeManager.getTypeById(attrData.getAttributeTypeId()).getName(),
               attrData.getValue()}));
         }
      }
      appendToDetails(AHTML.endMultiColumnTable());

      monitor.worked(calculateWork(0.10));
      checkForCancelledStatus(monitor);

      setItemsToFix(attributesWithErrors.size());
      checkForCancelledStatus(monitor);
      if (isFixOperationEnabled() && hadItemsToFix()) {
         for (IOseeBranch branch : attributesWithErrors.keySet()) {
            Collection<AttrData> attributeData = attributesWithErrors.getValues(branch);
            List<ArtifactId> artifactIds = new ArrayList<>(attributeData.size());
            for (AttrData attrData : attributeData) {
               artifactIds.add(attrData.getArtifactId());
            }

            ArtifactQuery.getArtifactListFrom(artifactIds, branch, EXCLUDE_DELETED); // bulk load for speed
            SkynetTransaction transaction = TransactionManager.createTransaction(branch,
               "Delete Repeat Attribute Values for" + branch.getShortName());
            for (AttrData attrData : attributeData) {
               Artifact artifact = ArtifactQuery.getArtifactFromId(attrData.getArtifactId(), branch);
               AttributeType attributeType = AttributeTypeManager.getTypeById(attrData.getAttributeTypeId());
               if (attributeType.isEnumerated()) {
                  artifact.setAttributeValues(attributeType, artifact.getAttributesToStringList(attributeType));
                  artifact.persist(transaction);
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

   private Set<AttrData> getRepeatEnumeratedAttrs(IProgressMonitor monitor, BranchId branch) {
      Set<AttrData> attrData = new HashSet<>();
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(FIND_REPEAT_ENUMS, branch, branch);
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            attrData.add(new AttrData(ArtifactId.valueOf(chStmt.getLong("art_id")), chStmt.getLong("attr_type_id"),
               chStmt.getString("value")));
         }
      } finally {
         chStmt.close();
      }
      return attrData;
   }

   private final class AttrData {
      private final ArtifactId artifactId;
      private final Long attributeTypeId;
      private final String value;

      public AttrData(ArtifactId artifactId, long attributeTypeId, String value) {
         this.artifactId = artifactId;
         this.attributeTypeId = attributeTypeId;
         this.value = value;
      }

      public ArtifactId getArtifactId() {
         return artifactId;
      }

      public long getAttributeTypeId() {
         return attributeTypeId;
      }

      public String getValue() {
         return value;
      }
   }
}