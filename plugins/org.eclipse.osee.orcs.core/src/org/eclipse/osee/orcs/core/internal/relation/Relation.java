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

import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.HasOrcsData;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.internal.util.OrcsWriteable;
import org.eclipse.osee.orcs.core.internal.util.ValueProvider;
import org.eclipse.osee.orcs.data.RelationTypes;

/**
 * @author Roberto E. Escobar
 */
public class Relation implements HasOrcsData<RelationData>, OrcsWriteable {

   private final RelationTypes relationTypes;
   private final ValueProvider<Branch, OrcsData> branchProvider;

   private RelationData relationData;
   private boolean isDirty;

   public Relation(RelationTypes relationTypes, RelationData relationData, ValueProvider<Branch, OrcsData> branchProvider) {
      super();
      this.relationTypes = relationTypes;
      this.relationData = relationData;
      this.branchProvider = branchProvider;
   }

   @Override
   public RelationData getOrcsData() {
      return relationData;
   }

   @Override
   public void setOrcsData(RelationData data) {
      this.relationData = data;
      branchProvider.setOrcsData(data);
   }

   public IRelationType getRelationType() throws OseeCoreException {
      return relationTypes.getByUuid(getOrcsData().getTypeUuid());
   }

   public ModificationType getModificationType() {
      return getOrcsData().getModType();
   }

   @Override
   public boolean isDeleted() {
      return getModificationType().isDeleted();
   }

   @Override
   public void delete() {
      markAsChanged(ModificationType.DELETED);
   }

   public Branch getBranch() throws OseeCoreException {
      return branchProvider.get();
   }

   @Override
   public boolean isDirty() {
      return isDirty;
   }

   public void clearDirty() {
      setDirtyFlag(false);
   }

   public void setDirty() {
      setDirtyFlag(true);
   }

   private void setDirtyFlag(boolean dirty) {
      this.isDirty = dirty;
   }

   public String getRationale() {
      return getOrcsData().getRationale();
   }

   public boolean isOfType(IRelationType oseeType) throws OseeCoreException {
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
      result = prime * result + ((relationData == null) ? 0 : relationData.hashCode());
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

   public int getLocalIdForSide(RelationSide side) {
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
      return "Relation [relationData=" + relationData + ", isDirty=" + isDirty + "]";
   }

}
