/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.mim.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.model.dto.ChangeReportRowDto;

/**
 * @author Ryan T. Baldwin
 */
public class MimChangeSummaryItem {
   private final ArtifactReadable art;
   private boolean added;
   private boolean deleted;
   private boolean addedDueToApplicChange;
   private boolean deletedDueToApplicChange;
   private ApplicabilityToken wasApplic;
   private ApplicabilityToken isApplic;
   private final List<ChangeReportRowDto> attributeChanges;
   private final List<ChangeReportRowDto> relationChanges;
   private final List<MimChangeSummaryItem> children;

   public MimChangeSummaryItem(ArtifactReadable art) {
      this.art = art;
      this.added = false;
      this.deleted = false;
      this.addedDueToApplicChange = false;
      this.deletedDueToApplicChange = false;
      this.wasApplic = ApplicabilityToken.SENTINEL;
      this.isApplic = ApplicabilityToken.SENTINEL;
      this.attributeChanges = new LinkedList<>();
      this.relationChanges = new LinkedList<>();
      this.children = new LinkedList<>();
   }

   public boolean isAdded() {
      return added;
   }

   public void setAdded(boolean added) {
      this.added = added;
   }

   public boolean isDeleted() {
      return deleted;
   }

   public void setDeleted(boolean deleted) {
      this.deleted = deleted;
   }

   public boolean isAddedDueToApplicChange() {
      return addedDueToApplicChange;
   }

   public void setAddedDueToApplicChange(boolean addedDueToApplicChange) {
      this.addedDueToApplicChange = addedDueToApplicChange;
   }

   public boolean isDeletedDueToApplicChange() {
      return deletedDueToApplicChange;
   }

   public void setDeletedDueToApplicChange(boolean deletedDueToApplicChange) {
      this.deletedDueToApplicChange = deletedDueToApplicChange;
   }

   public ApplicabilityToken getWasApplic() {
      return wasApplic;
   }

   public void setWasApplic(ApplicabilityToken wasApplic) {
      this.wasApplic = wasApplic;
   }

   public ApplicabilityToken getIsApplic() {
      return isApplic;
   }

   public void setIsApplic(ApplicabilityToken isApplic) {
      this.isApplic = isApplic;
   }

   public boolean isApplicabilityChanged() {
      return !getIsApplic().equals(getWasApplic());
   }

   public ArtifactId getArtId() {
      return art.getArtifactId();
   }

   @JsonIgnore
   public ArtifactReadable getArtifactReadable() {
      return this.art;
   }

   public String getName() {
      return art.getName();
   }

   public ArtifactTypeToken getArtType() {
      return art.getArtifactType();
   }

   public List<ChangeReportRowDto> getAttributeChanges() {
      return attributeChanges;
   }

   public List<ChangeReportRowDto> getAttributeChanges(Long attributeId) {
      return attributeChanges.stream().filter(attr -> attr.getItemTypeId().equals(attributeId)).collect(
         Collectors.toList());
   }

   public boolean hasAttributeChanges(Long attributeId) {
      return attributeChanges.stream().filter(attr -> attr.getItemTypeId().equals(attributeId)).findAny().isPresent();
   }

   public List<ChangeReportRowDto> getRelationChanges() {
      return relationChanges;
   }

   public List<ChangeReportRowDto> getRelationChanges(Long relationId) {
      return relationChanges.stream().filter(rel -> rel.getItemTypeId().equals(relationId)).collect(
         Collectors.toList());
   }

   public boolean hasRelationChanges(Long relationId) {
      return relationChanges.stream().filter(rel -> rel.getItemTypeId().equals(relationId)).findAny().isPresent();
   }

   public List<MimChangeSummaryItem> getChildren() {
      return children;
   }

   public Optional<MimChangeSummaryItem> getChild(ArtifactId childArtId) {
      return children.stream().filter(child -> child.getArtId().equals(childArtId.getId())).findFirst();
   }

   public boolean hasChild(ArtifactId childArtId) {
      return children.stream().filter(child -> child.getArtId().equals(childArtId.getId())).findAny().isPresent();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof MimChangeSummaryItem) {
         return getArtId().equals(((MimChangeSummaryItem) obj).getArtId());
      }
      return false;
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

}