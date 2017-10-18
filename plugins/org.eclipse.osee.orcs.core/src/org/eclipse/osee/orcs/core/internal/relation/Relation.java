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
package org.eclipse.osee.orcs.core.internal.relation;

import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.orcs.core.ds.HasOrcsData;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.internal.util.OrcsWriteable;
import org.eclipse.osee.orcs.data.RelationReadable;
import org.eclipse.osee.orcs.data.RelationTypes;

/**
 * @author Roberto E. Escobar
 */
public class Relation implements RelationReadable, HasOrcsData<RelationData>, OrcsWriteable {

   private final RelationTypes relationTypes;

   private RelationData relationData;

   public Relation(RelationTypes relationTypes, RelationData relationData) {
      super();
      this.relationTypes = relationTypes;
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
   public RelationTypeId getRelationType() {
      return relationTypes.get(getOrcsData().getTypeUuid());
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
   public boolean isOfType(IRelationType oseeType) {
      return getRelationType().equals(oseeType);
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

   public Integer getIdForSide(RelationSide side) {
      return getOrcsData().getArtIdOn(side);
   }

   @Override
   public boolean isDeleteAllowed() {
      return !isDeleted();
   }

   @Override
   public void unDelete() {
      getOrcsData().setModType(ModificationType.UNDELETED);
   }

   @Override
   public String toString() {
      return "Relation [relationData=" + relationData + ", isDirty=" + getOrcsData().isDirty() + "]";
   }

   @Override
   public Long getId() {
      return Long.valueOf(getOrcsData().getLocalId());
   }

   @Override
   public long getGammaId() {
      return getOrcsData().getVersion().getGammaId().getId();
   }

   @Override
   public int getArtIdA() {
      return getOrcsData().getArtIdA();
   }

   @Override
   public int getArtIdB() {
      return getOrcsData().getArtIdB();
   }

}
