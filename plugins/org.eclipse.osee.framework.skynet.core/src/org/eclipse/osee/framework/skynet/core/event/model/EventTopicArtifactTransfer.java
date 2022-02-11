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

package org.eclipse.osee.framework.skynet.core.event.model;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.EventTopicTransferType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author David W. Miller
 */
public class EventTopicArtifactTransfer {
   private BranchId branch;
   private ArtifactToken artifactToken;
   private ArtifactTypeId artifactTypeId;
   private EventModType eventModType;
   private ArtifactTypeId fromArtTypeGuid;
   private Collection<AttributeChange> attributeChanges;
   private EventTopicTransferType transferType = EventTopicTransferType.BASE;

   public BranchId getBranch() {
      return branch;
   }

   public void setBranch(BranchId branch) {
      this.branch = branch;
   }

   public ArtifactToken getArtifactToken() {
      return artifactToken;
   }

   public void setArtifactToken(ArtifactToken artifactToken) {
      this.artifactToken = artifactToken;
   }

   public ArtifactTypeId getArtifactTypeId() {
      return artifactTypeId;
   }

   public void setArtifactTypeId(ArtifactTypeId artifactType) {
      this.artifactTypeId = artifactType;
   }

   public EventModType getEventModType() {
      return eventModType;
   }

   public void setEventModType(EventModType eventModType) {
      this.eventModType = eventModType;
   }

   public ArtifactTypeId getFromArtTypeGuid() {
      return fromArtTypeGuid;
   }

   public void setFromArtTypeGuid(ArtifactTypeId fromArtTypeGuid) {
      this.fromArtTypeGuid = fromArtTypeGuid;
   }

   public Collection<AttributeChange> getAttributeChanges() {
      return attributeChanges;
   }

   public void setAttributeChanges(Collection<AttributeChange> attributeChanges) {
      this.attributeChanges = attributeChanges;
   }

   public EventTopicTransferType getTransferType() {
      return transferType;
   }

   public void setTransferType(EventTopicTransferType transferType) {
      this.transferType = transferType;
   }

   @Override
   public String toString() {
      String branchName = this.branch == null ? "null branch" : this.branch.getIdString();
      String artifactTokenName = this.artifactToken == null ? "null Artifact" : this.artifactToken.getIdString();
      String artifactTypeIdName =
         this.artifactTypeId == null ? "null Artifact Type" : this.artifactTypeId.getIdString();
      String eventModTypeName = this.eventModType == null ? "null mod type" : this.eventModType.toString();
      String fromArtTypeGuidName =
         this.fromArtTypeGuid == null ? "null from Artifact Type" : this.fromArtTypeGuid.getIdString();
      String attributeChangesName =
         this.attributeChanges == null ? "null attributes" : this.attributeChanges.toString();

      switch (transferType) {

         case BASE: {
            return String.format("[BASE: %s - B:%s - A:%s]", eventModTypeName, branchName, artifactTypeIdName);
         }
         case CHANGE: {
            return String.format("[CHANGE: %s - %s from type [%s] to [%s]]", eventModTypeName, artifactTokenName,
               fromArtTypeGuidName, artifactTypeIdName);
         }
         case MODIFICATION: {
            return String.format("[MOD: %s - G:%s - B:%s - A:%s - %s]", eventModTypeName, artifactTokenName, branchName,
               artifactTypeIdName, attributeChangesName);
         }
         default: {
            throw new OseeArgumentException("Unknown transfer type in %", this.getClass().toString());
         }
      }
   }

   public boolean is(EventModType... eventModTypes) {
      for (EventModType eventModType : eventModTypes) {
         if (this.eventModType == eventModType) {
            return true;
         }
      }
      return false;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((artifactToken == null) ? 0 : artifactToken.hashCode());
      result = prime * result + ((artifactTypeId == null) ? 0 : artifactTypeId.hashCode());
      result = prime * result + ((attributeChanges == null) ? 0 : attributeChanges.hashCode());
      result = prime * result + ((branch == null) ? 0 : branch.hashCode());
      result = prime * result + ((eventModType == null) ? 0 : eventModType.hashCode());
      result = prime * result + ((fromArtTypeGuid == null) ? 0 : fromArtTypeGuid.hashCode());
      result = prime * result + ((transferType == null) ? 0 : transferType.hashCode());
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
      EventTopicArtifactTransfer other = (EventTopicArtifactTransfer) obj;
      if (artifactToken == null) {
         if (other.artifactToken != null) {
            return false;
         }
      } else if (!artifactToken.equals(other.artifactToken)) {
         return false;
      }
      if (artifactTypeId == null) {
         if (other.artifactTypeId != null) {
            return false;
         }
      } else if (!artifactTypeId.equals(other.artifactTypeId)) {
         return false;
      }
      if (attributeChanges == null) {
         if (other.attributeChanges != null) {
            return false;
         }
      } else if (!attributeChanges.equals(other.attributeChanges)) {
         return false;
      }
      if (branch == null) {
         if (other.branch != null) {
            return false;
         }
      } else if (!branch.equals(other.branch)) {
         return false;
      }
      if (eventModType != other.eventModType) {
         return false;
      }
      if (fromArtTypeGuid == null) {
         if (other.fromArtTypeGuid != null) {
            return false;
         }
      } else if (!fromArtTypeGuid.equals(other.fromArtTypeGuid)) {
         return false;
      }
      if (transferType != other.transferType) {
         return false;
      }
      return true;
   }
}
