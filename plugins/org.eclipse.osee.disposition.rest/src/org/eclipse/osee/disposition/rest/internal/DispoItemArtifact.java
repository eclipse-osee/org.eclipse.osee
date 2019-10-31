/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.rest.DispoOseeTypes;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Angel Avila
 */
public class DispoItemArtifact extends BaseIdentity<String> implements DispoItem {

   private final ArtifactReadable artifact;
   private Boolean isIncludeDetails;

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
      String discrepanciesJson = artifact.getSoleAttributeAsString(DispoOseeTypes.DispoDiscrepanciesJson, "{}");
      return DispoUtil.jsonStringToDiscrepanciesMap(discrepanciesJson);
   }

   @Override
   public List<DispoAnnotationData> getAnnotationsList() {
      String annotationsList = artifact.getSoleAttributeAsString(DispoOseeTypes.DispoAnnotationsJson, "[]");
      return DispoUtil.jsonStringToList(annotationsList, DispoAnnotationData.class);
   }

   @Override
   public String toString() {
      return getName();
   }

   @Override
   public String getAssignee() {
      return artifact.getSoleAttributeAsString(DispoOseeTypes.DispoItemAssignee, "");
   }

   @Override
   public Date getCreationDate() {
      return artifact.getSoleAttributeValue(DispoOseeTypes.DispoDateCreated);

   }

   @Override
   public Date getLastUpdate() {
      return artifact.getSoleAttributeValue(DispoOseeTypes.DispoItemLastUpdated);
   }

   @Override
   public String getStatus() {
      return artifact.getSoleAttributeAsString(DispoOseeTypes.DispoItemStatus);
   }

   @Override
   public String getVersion() {
      return artifact.getSoleAttributeAsString(DispoOseeTypes.DispoItemVersion);
   }

   @Override
   public Boolean getNeedsRerun() {
      return artifact.getSoleAttributeValue(DispoOseeTypes.DispoItemNeedsRerun, false);
   }

   @Override
   public String getTotalPoints() {
      return artifact.getSoleAttributeAsString(DispoOseeTypes.DispoItemTotalPoints, "0");
   }

   @Override
   public String getMachine() {
      return artifact.getSoleAttributeAsString(DispoOseeTypes.DispoItemMachine, "n/a");
   }

   @Override
   public String getCategory() {
      return artifact.getSoleAttributeAsString(DispoOseeTypes.DispoItemCategory, "");
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
      return artifact.getSoleAttributeAsString(DispoOseeTypes.DispoItemNotes, "");
   }

   @Override
   public String getMethodNumber() {
      return artifact.getSoleAttributeAsString(DispoOseeTypes.DispoItemMethodNumber, "");
   }

   @Override
   public String getFileNumber() {
      return artifact.getSoleAttributeAsString(DispoOseeTypes.DispoItemFileNumber, "");
   }

   @Override
   public Boolean getNeedsReview() {
      return artifact.getSoleAttributeValue(DispoOseeTypes.DispoItemNeedsReview, false);
   }

   @Override
   public String getTeam() {
      return artifact.getSoleAttributeAsString(DispoOseeTypes.DispoItemTeam, "");
   }
}
