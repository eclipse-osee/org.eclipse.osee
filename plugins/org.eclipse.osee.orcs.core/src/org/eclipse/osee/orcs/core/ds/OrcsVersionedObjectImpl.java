/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.RelationalConstants;
import org.eclipse.osee.framework.core.enums.DirtyState;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Roberto E. Escobar
 */
public abstract class OrcsVersionedObjectImpl<T extends Id> implements OrcsData<T> {
   private Integer localId = Id.SENTINEL.intValue();
   private T type;
   private T baseType;

   private ModificationType baseModType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;
   private ModificationType previousModType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;
   private ModificationType currentModType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;

   private final VersionData version;

   private ApplicabilityId applicId;
   private boolean applicDirty;
   private DirtyState dirtyState = DirtyState.CLEAN;

   protected OrcsVersionedObjectImpl(VersionData version) {
      super();
      this.version = version;
   }

   @Override
   public T getType() {
      return type;
   }

   @Override
   public void setType(T type) {
      this.type = type;
   }

   @Override
   public T getBaseType() {
      return baseType;
   }

   @Override
   public void setBaseType(T baseType) {
      this.baseType = baseType;
   }

   @Override
   public void setBaseModType(ModificationType modType) {
      baseModType = modType;
   }

   @Override
   public ModificationType getModType() {
      return currentModType;
   }

   @Override
   public void setModType(ModificationType modType) {
      previousModType = currentModType;
      this.currentModType = modType;
   }

   @Override
   public ModificationType getPreviousModType() {
      return previousModType;
   }

   @Override
   public ModificationType getBaseModType() {
      return baseModType;
   }

   @Override
   public boolean hasTypeUuidChange() {
      return type.notEqual(baseType);
   }

   @Override
   public boolean hasModTypeChange() {
      return getBaseModType() != getModType();
   }

   @Override
   public VersionData getVersion() {
      return version;
   }

   @Override
   public void setApplicabilityId(ApplicabilityId applicId) {
      if (this.applicId != null && this.applicId.notEqual(applicId)) {
         if (!dirtyState.isDirty()) {
            applicDirty = true;
            dirtyState = DirtyState.APPLICABILITY_ONLY;
         }
      }
      this.applicId = applicId;
   }

   @Override
   public ApplicabilityId getApplicabilityId() {
      return applicId;
   }

   @Override
   public DirtyState getDirtyState() {
      return dirtyState;
   }

   @Override
   public boolean isDirty() {
      return dirtyState.isDirty();
   }

   @Override
   public DirtyState calculateDirtyState(boolean dirty) {
      if (!dirty) {
         if (applicDirty) {
            dirtyState = DirtyState.APPLICABILITY_ONLY;
         } else {
            dirtyState = DirtyState.CLEAN;
         }
      } else if (dirty) {
         return dirtyState = DirtyState.OTHER_CHANGES;
      }

      return dirtyState;
   }

   @Override
   public int hashCode() {
      return getLocalId().hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof OrcsVersionedObjectImpl<?>) {
         OrcsVersionedObjectImpl<?> other = (OrcsVersionedObjectImpl<?>) obj;
         if (!getLocalId().equals(other.getLocalId())) {
            return false;
         }
         if (baseModType != other.baseModType) {
            return false;
         }
         if (baseType.notEqual(other.baseType)) {
            return false;
         }
         if (version == null) {
            if (other.version != null) {
               return false;
            }
            return version.equals(other.version);
         }
         return true;
      } else if (obj instanceof Id) {
         return ((Id) obj).getId().equals(getLocalId().longValue());
      }
      return false;
   }

   @Override
   public String toString() {
      return "OrcsVersionedObject [" + super.toString() + ", type=" + type + ", baseType=" + baseType + ", modType=" + currentModType + ", previousModType=" + previousModType + ", baseModType=" + baseModType + ", version=" + version + "]";
   }

   @Override
   public Integer getLocalId() {
      return localId;
   }

   @Override
   public void setLocalId(Integer localId) {
      this.localId = localId;
   }
}