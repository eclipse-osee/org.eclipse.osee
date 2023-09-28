/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.framework.core.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.change.ChangeType;

/**
 * @author Ryan T. Baldwin
 */
public class ChangeReportRowDto {

   // Serialized
   private String names = "";
   private String itemType = "";
   private String changeType = "";
   private String isValue = "";
   private String wasValue = "";

   // JsonIgnored
   private ArtifactReadable artA;
   private ArtifactReadable artB;
   private ChangeType itemKindType;
   private Long itemTypeId;
   private ModificationType modType;
   private ApplicabilityToken isApplic;
   private ApplicabilityToken wasApplic;

   public ChangeReportRowDto() {
   }

   public ChangeReportRowDto(ArtifactReadable artA, ArtifactReadable artB, String names, String itemType, Long itemTypeId, String changeType, String isValue, String wasValue, ChangeType itemKindType, ModificationType modType, ApplicabilityToken isApplic, ApplicabilityToken wasApplic) {
      this.artA = artA;
      this.artB = artB;
      this.names = names;
      this.itemType = itemType;
      this.changeType = changeType;
      this.isValue = isValue;
      this.wasValue = wasValue;
      this.itemKindType = itemKindType;
      this.itemTypeId = itemTypeId;
      this.modType = modType;
      this.isApplic = isApplic;
      this.wasApplic = wasApplic;
   }

   public String getIds() {
      String id = artA.getIdString();
      if (artB.isValid()) {
         id += " - " + artB.getIdString();
      }
      return id;
   }

   public String getNames() {
      return names;
   }

   public void setNames(String names) {
      this.names = names;
   }

   public String getItemType() {
      return itemType;
   }

   public void setItemType(String itemType) {
      this.itemType = itemType;
   }

   public String getItemKind() {
      return getItemKindType().getName().replace("Change", "");
   }

   public String getChangeType() {
      return changeType;
   }

   public void setChangeType(String changeType) {
      this.changeType = changeType;
   }

   public String getIsValue() {
      return isValue;
   }

   public void setIsValue(String isValue) {
      this.isValue = isValue;
   }

   public String getWasValue() {
      return wasValue;
   }

   public void setWasValue(String wasValue) {
      this.wasValue = wasValue;
   }

   @JsonIgnore
   public ArtifactReadable getArtA() {
      return artA;
   }

   @JsonIgnore
   public ArtifactReadable getArtB() {
      return artB;
   }

   @JsonIgnore
   public ChangeType getItemKindType() {
      return itemKindType;
   }

   public void setItemKindType(ChangeType itemKindType) {
      this.itemKindType = itemKindType;
   }

   @JsonIgnore
   public Long getItemTypeId() {
      return itemTypeId;
   }

   public void setItemTypeId(Long itemTypeId) {
      this.itemTypeId = itemTypeId;
   }

   @JsonIgnore
   public ModificationType getModType() {
      return modType;
   }

   public void setModType(ModificationType modType) {
      this.modType = modType;
   }

   @JsonIgnore
   public ArtifactTypeToken getArtType() {
      return this.artA.getArtifactType();
   }

   @JsonIgnore
   public ApplicabilityToken getIsApplic() {
      return isApplic;
   }

   @JsonIgnore
   public ApplicabilityToken getWasApplic() {
      return wasApplic;
   }

}