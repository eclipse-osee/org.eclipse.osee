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
package org.eclipse.osee.orcs.db.internal.loader.data;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.db.internal.sql.RelationalConstants;

/**
 * @author Roberto E. Escobar
 */
public abstract class OrcsVersionedObjectImpl extends OrcsObjectImpl<Integer>implements OrcsData {

   private long typeUuid = RelationalConstants.DEFAULT_TYPE_UUID;
   private long baseTypeUuid = RelationalConstants.DEFAULT_TYPE_UUID;

   private ModificationType baseModType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;
   private ModificationType previousModType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;
   private ModificationType currentModType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;

   private final VersionData version;

   protected OrcsVersionedObjectImpl(VersionData version) {
      super();
      this.version = version;
   }

   @Override
   public long getTypeUuid() {
      return typeUuid;
   }

   @Override
   public void setTypeUuid(long typeUuid) {
      this.typeUuid = typeUuid;
   }

   @Override
   public long getBaseTypeUuid() {
      return baseTypeUuid;
   }

   @Override
   public void setBaseTypeUuid(long baseTypeUuid) {
      this.baseTypeUuid = baseTypeUuid;
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
      return getBaseTypeUuid() != getTypeUuid();
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
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + (baseModType == null ? 0 : baseModType.hashCode());
      result = prime * result + (int) (baseTypeUuid ^ baseTypeUuid >>> 32);
      result = prime * result + (version == null ? 0 : version.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!super.equals(obj)) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      OrcsVersionedObjectImpl other = (OrcsVersionedObjectImpl) obj;
      if (baseModType != other.baseModType) {
         return false;
      }
      if (baseTypeUuid != other.baseTypeUuid) {
         return false;
      }
      if (version == null) {
         if (other.version != null) {
            return false;
         }
      } else if (!version.equals(other.version)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "OrcsVersionedObject [" + super.toString() + ", typeUuid=" + typeUuid + ", baseTypeUuid=" + baseTypeUuid + ", modType=" + currentModType + ", previousModType=" + previousModType + ", baseModType=" + baseModType + ", version=" + version + "]";
   }

}
