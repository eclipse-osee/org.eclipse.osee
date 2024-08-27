/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.disposition.rest.internal;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DispoOseeTypes;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;

/**
 * @author Angel Avila
 */
public class DispoItemArtifact extends BaseIdentity<String> implements DispoItem {

   private final ArtifactReadable artifact;
   private Boolean isIncludeDetails;

   public static DispoItemArtifact SENTINEL = valueOf(ArtifactReadable.SENTINEL);

   public static DispoItemArtifact valueOf(ArtifactReadable artifact) {
      final class DispoItemArtifactImpl extends DispoItemArtifact {

         public DispoItemArtifactImpl(ArtifactReadable artifact) {
            super(artifact);
         }

         @Override
         public String getName() {
            return artifact.getName();
         }
      }
      return new DispoItemArtifactImpl(artifact);
   }

   public DispoItemArtifact(ArtifactReadable artifact) {
      super(artifact.getIdString());
      this.artifact = artifact;
   }

   @Override
   public String getName() {
      return artifact.getName();
   }

   public void setIsIncludeDetails(Boolean isIncludeDetails) {
      this.isIncludeDetails = isIncludeDetails;
   }

   @Override
   public Boolean getIsIncludeDetails() {
      return isIncludeDetails;
   }

   @Override
   public Map<String, Discrepancy> getDiscrepanciesList() {
      String discrepanciesJson = artifact.getSoleAttributeAsString(CoreAttributeTypes.CoverageDiscrepanciesJson, "{}");
      return DispoUtil.jsonStringToDiscrepanciesMap(discrepanciesJson);
   }

   @Override
   public List<DispoAnnotationData> getAnnotationsList() {
      String annotationsList = artifact.getSoleAttributeAsString(CoreAttributeTypes.CoverageAnnotationsJson, "[]");
      return DispoUtil.jsonStringToList(annotationsList, DispoAnnotationData.class);
   }

   @Override
   public String toString() {
      return getName();
   }

   @Override
   public String getAssignee() {
      return artifact.getSoleAttributeAsString(CoreAttributeTypes.CoverageAssignee, "");
   }

   @Override
   public Date getCreationDate() {
      return artifact.getSoleAttributeValue(CoreAttributeTypes.CoverageCreatedDate);

   }

   @Override
   public Date getLastUpdate() {
      return artifact.getSoleAttributeValue(CoreAttributeTypes.CoverageLastUpdated);
   }

   @Override
   public String getStatus() {
      return artifact.getSoleAttributeAsString(CoreAttributeTypes.CoverageStatus);
   }

   @Override
   public String getVersion() {
      return artifact.getSoleAttributeAsString(DispoOseeTypes.DispoItemVersion);
   }

   @Override
   public Boolean getNeedsRerun() {
      return artifact.getSoleAttributeValue(CoreAttributeTypes.CoverageNeedsRerun, false);
   }

   @Override
   public String getTotalPoints() {
      return artifact.getSoleAttributeAsString(CoreAttributeTypes.CoverageTotalPoints, "0");
   }

   @Override
   public String getMachine() {
      return artifact.getSoleAttributeAsString(DispoOseeTypes.DispoItemMachine, "n/a");
   }

   @Override
   public String getCategory() {
      return artifact.getSoleAttributeAsString(DispoOseeTypes.CoverageItemCategory, "");
   }

   @Override
   public String getElapsedTime() {
      return artifact.getSoleAttributeAsString(DispoOseeTypes.DispoItemElapsedTime, "0.0");
   }

   @Override
   public Boolean getAborted() {
      return artifact.getSoleAttributeValue(DispoOseeTypes.DispoItemAborted, false);
   }

   @Override
   public String getItemNotes() {
      return artifact.getSoleAttributeAsString(CoreAttributeTypes.CoverageNotes, "");
   }

   @Override
   public String getMethodNumber() {
      return artifact.getSoleAttributeAsString(CoreAttributeTypes.CoverageMethodNumber, "");
   }

   @Override
   public String getFileNumber() {
      return artifact.getSoleAttributeAsString(CoreAttributeTypes.CoverageFileNumber, "");
   }

   @Override
   public Boolean getNeedsReview() {
      return artifact.getSoleAttributeValue(DispoOseeTypes.DispoItemNeedsReview, false);
   }

   @Override
   public String getTeam() {
      return artifact.getSoleAttributeAsString(CoreAttributeTypes.CoverageTeam, "");
   }

   @Override
   public Boolean isValid() {
      return !this.equals(DispoItemData.SENTINEL);
   }
}
