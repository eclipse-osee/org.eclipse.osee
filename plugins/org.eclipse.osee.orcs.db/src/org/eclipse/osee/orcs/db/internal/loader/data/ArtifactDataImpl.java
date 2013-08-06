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

import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.db.internal.sql.RelationalConstants;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactDataImpl extends OrcsObjectImpl implements ArtifactData {

   private String guid = RelationalConstants.DEFAULT_GUID;
   private String humanReadableId = RelationalConstants.HUMAN_READABLE_ID;

   public ArtifactDataImpl(VersionData version) {
      super(version);
   }

   @Override
   public String getGuid() {
      return guid;
   }

   @Override
   public void setGuid(String guid) {
      this.guid = guid;
   }

   @Override
   public String getHumanReadableId() {
      return humanReadableId;
   }

   @Override
   public void setHumanReadableId(String humanReadableId) {
      this.humanReadableId = humanReadableId;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((guid == null) ? 0 : guid.hashCode());
      result = prime * result + ((humanReadableId == null) ? 0 : humanReadableId.hashCode());
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
      ArtifactDataImpl other = (ArtifactDataImpl) obj;
      if (guid == null) {
         if (other.guid != null) {
            return false;
         }
      } else if (!guid.equals(other.guid)) {
         return false;
      }
      if (humanReadableId == null) {
         if (other.humanReadableId != null) {
            return false;
         }
      } else if (!humanReadableId.equals(other.humanReadableId)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "ArtifactData [guid=" + guid + ", humanReadableId=" + humanReadableId + " " + super.toString() + "]";
   }

}
