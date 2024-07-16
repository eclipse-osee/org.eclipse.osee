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

package org.eclipse.osee.orcs.core.internal.relation;

import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.IRelationLink;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.orcs.core.ds.HasOrcsData;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.internal.util.OrcsWriteable;

/**
 * @author Roberto E. Escobar
 */
public class Relation implements IRelationLink, HasOrcsData<RelationTypeToken, RelationData>, OrcsWriteable {
   private RelationData relationData;

   public Relation(RelationData relationData) {
      this.relationData = relationData;
   }

   @Override
   public RelationData getOrcsData() {
      return relationData;
   }

   @Override
   public void setOrcsData(RelationData data) {
      this.relationData = data;
   }

   @Override
   public RelationTypeToken getRelationType() {
      return getOrcsData().getType();
   }

   @Override
   public ModificationType getModificationType() {
      return getOrcsData().getModType();
   }

   @Override
   public void delete() {
      markAsChanged(ModificationType.DELETED);
   }

   @Override
   public boolean isDirty() {
      return getOrcsData().isDirty();
   }

   public void clearDirty() {
      setDirtyFlag(false);
   }

   public void setDirty() {
      setDirtyFlag(true);
   }

   private void setDirtyFlag(boolean dirty) {
      getOrcsData().calculateDirtyState(dirty);
   }

   public String getRationale() {
      return getOrcsData().getRationale();
   }

   @Override
   public boolean isOfType(RelationTypeToken oseeType) {
      return getRelationType().equals(oseeType);
   }

   public void setRelOrder(int order) {
      getOrcsData().setRelOrder(order);
   }

   public int getRelOrder() {
      return getOrcsData().getRelOrder();
   }

   public void setRationale(String rationale) {
      String toSet = rationale;
      if (toSet == null) {
         toSet = "";
      }
      if (!toSet.equals(getOrcsData().getRationale())) {
         getOrcsData().setRationale(rationale);
         markAsChanged(ModificationType.MODIFIED);
      }
   }

   public void setApplicabilityId(ApplicabilityId applicId) {
      getOrcsData().setApplicabilityId(applicId);
   }

   protected void markAsChanged(ModificationType modificationType) {
      ModificationType modType = computeModType(getOrcsData().getModType(), modificationType);
      getOrcsData().setModType(modType);
      setDirty();
   }

   private ModificationType computeModType(ModificationType original, ModificationType newModType) {
      ModificationType toReturn = original;
      if (original != ModificationType.DELETED || original != ModificationType.ARTIFACT_DELETED) {
         toReturn = newModType;
      }
      return toReturn;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (relationData == null ? 0 : relationData.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      Relation other = (Relation) obj;
      if (relationData == null) {
         if (other.relationData != null) {
            return false;
         }
      } else if (!relationData.equals(other.relationData)) {
         return false;
      }
      return true;
   }

   public ArtifactId getIdForSide(RelationSide side) {
      return getOrcsData().getArtIdOn(side);
   }

   public boolean isDeleteAllowed() {
      return !isDeleted();
   }

   @Override
   public void unDelete() {
      getOrcsData().setModType(ModificationType.UNDELETED);
   }

   @Override
   public String toString() {
      return String.format("Relation id=%s, a=%s, type=%s, typeId=%s, b=%s, dirty=%s", getOrcsData().getId(),
         getOrcsData().getArtifactIdA().getIdString(), getOrcsData().getType().getName(),
         getOrcsData().getType().getId(), getOrcsData().getArtifactIdB().getIdString(), getOrcsData().isDirty());
   }

   @Override
   public Long getId() {
      return Long.valueOf(getOrcsData().getId());
   }

   @Override
   public GammaId getGammaId() {
      return getOrcsData().getVersion().getGammaId();
   }

   @Override
   public boolean isDeleted() {
      return getModificationType().isDeleted();
   }

   @Override
   public ArtifactId getArtifactIdA() {
      return getOrcsData().getArtifactIdA();
   }

   @Override
   public ArtifactId getArtifactIdB() {
      return getOrcsData().getArtifactIdB();
   }
}