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

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.event.RelationOrderModType;

/**
 * @author Torin Grenda
 */
public class EventTopicRelationReorderTransfer {

   private EventTopicArtifactTransfer parentArt;
   private BranchId branch;
   private Long relTypeUuid;
   private RelationOrderModType modType;

   public EventTopicArtifactTransfer getParentArt() {
      return parentArt;
   }

   public void setParentArt(EventTopicArtifactTransfer parentArt) {
      this.parentArt = parentArt;
   }

   public BranchId getBranch() {
      return branch;
   }

   public void setBranch(BranchId branch) {
      this.branch = branch;
   }

   public Long getRelTypeUuid() {
      return relTypeUuid;
   }

   public void setRelTypeUuid(Long relTypeUuid) {
      this.relTypeUuid = relTypeUuid;
   }

   public RelationOrderModType getModType() {
      return modType;
   }

   public void setModType(RelationOrderModType modType) {
      this.modType = modType;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((parentArt == null) ? 0 : parentArt.hashCode());
      result = prime * result + ((branch == null) ? 0 : branch.hashCode());
      result = prime * result + ((relTypeUuid == null) ? 0 : relTypeUuid.hashCode());
      result = prime * result + ((modType == null) ? 0 : modType.hashCode());
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
      EventTopicRelationReorderTransfer other = (EventTopicRelationReorderTransfer) obj;
      if (parentArt == null) {
         if (other.parentArt != null) {
            return false;
         }
      } else if (!parentArt.equals(other.parentArt)) {
         return false;
      }
      if (branch == null) {
         if (other.branch != null) {
            return false;
         }
      } else if (!branch.getId().equals(other.branch.getId())) {
         return false;
      }
      if (relTypeUuid == null) {
         if (other.relTypeUuid != null) {
            return false;
         }
      } else if (!relTypeUuid.equals(other.relTypeUuid)) {
         return false;
      }
      if (modType != other.modType) {
         return false;
      }
      return true;
   }
}