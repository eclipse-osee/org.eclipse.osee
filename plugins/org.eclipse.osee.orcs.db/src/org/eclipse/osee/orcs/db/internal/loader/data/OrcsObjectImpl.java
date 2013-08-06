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
public abstract class OrcsObjectImpl implements OrcsData {

   private int localId = RelationalConstants.DEFAULT_ITEM_ID;
   private long typeUuid = RelationalConstants.DEFAULT_TYPE_UUID;
   private long loadedTypeUuid = RelationalConstants.DEFAULT_TYPE_UUID;
   private ModificationType originalModType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;
   private ModificationType updatedModType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;

   private final VersionData version;

   protected OrcsObjectImpl(VersionData version) {
      super();
      this.version = version;
   }

   @Override
   public int getLocalId() {
      return localId;
   }

   @Override
   public void setLocalId(int localId) {
      this.localId = localId;
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
   public ModificationType getModType() {
      return updatedModType;
   }

   @Override
   public void setModType(ModificationType modType) {
      this.updatedModType = modType;
   }

   @Override
   public VersionData getVersion() {
      return version;
   }

   @Override
   public void setLoadedModType(ModificationType modType) {
      originalModType = modType;
   }

   @Override
   public ModificationType getLoadedModType() {
      return originalModType;
   }

   @Override
   public long getLoadedTypeUuid() {
      return loadedTypeUuid;
   }

   @Override
   public void setLoadedTypeUuid(long loadedTypeUuid) {
      this.loadedTypeUuid = loadedTypeUuid;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + localId;
      result = prime * result + ((originalModType == null) ? 0 : originalModType.hashCode());
      result = prime * result + (int) (typeUuid ^ (typeUuid >>> 32));
      result = prime * result + ((version == null) ? 0 : version.hashCode());
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
      OrcsObjectImpl other = (OrcsObjectImpl) obj;
      if (localId != other.localId) {
         return false;
      }
      if (originalModType != other.originalModType) {
         return false;
      }
      if (typeUuid != other.typeUuid) {
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
      return "OrcsObject [localId=" + localId + ", typeUuid=" + typeUuid + ", modType=" + updatedModType + ", version=" + version + "]";
   }

}
