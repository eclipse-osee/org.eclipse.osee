/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.core.model.event;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

/**
 * @author Donald G. Dunne
 */
public class DefaultBasicIdRelation implements IBasicGuidRelation {

   DefaultBasicGuidArtifact artA, artB;
   BranchId branch;
   Long relTypeUuid;
   GammaId gammaId;
   Long relationId;
   Long relArtId;
   int relOrder;

   public DefaultBasicIdRelation(BranchId branch, Long relTypeUuid, Long relationId, GammaId gammaId, DefaultBasicGuidArtifact artA, DefaultBasicGuidArtifact artB) {
      this.branch = branch;
      this.relTypeUuid = relTypeUuid;
      this.relationId = relationId;
      this.gammaId = gammaId;
      this.artA = artA;
      this.artB = artB;
      this.setRelOrder(0);
      this.setRelArtId(-1L);
   }

   public DefaultBasicIdRelation(BranchId branch, Long relTypeUuid, int relOrder, GammaId gammaId, DefaultBasicGuidArtifact artA, DefaultBasicGuidArtifact artB, Long relArtId) {
      this.branch = branch;
      this.relTypeUuid = relTypeUuid;
      this.relationId = gammaId.getId();
      this.gammaId = gammaId;
      this.artA = artA;
      this.artB = artB;
      this.setRelOrder(relOrder);
      this.setRelArtId(relArtId);
   }

   @Override
   public DefaultBasicGuidArtifact getArtA() {
      return artA;
   }

   @Override
   public DefaultBasicGuidArtifact getArtB() {
      return artB;
   }

   @Override
   public BranchId getBranch() {
      return branch;
   }

   @Override
   public GammaId getGammaId() {
      return gammaId;
   }

   @Override
   public Long getRelTypeGuid() {
      return relTypeUuid;
   }

   public void setArtA(DefaultBasicGuidArtifact artA) {
      this.artA = artA;
   }

   public void setArtB(DefaultBasicGuidArtifact artB) {
      this.artB = artB;
   }

   public void setBranchUuid(BranchId branch) {
      this.branch = branch;
   }

   public void setRelTypeGuid(Long relTypeGuid) {
      this.relTypeUuid = relTypeGuid;
   }

   public void setGammaId(GammaId gammaId) {
      this.gammaId = gammaId;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (artA == null ? 0 : artA.hashCode());
      result = prime * result + (artB == null ? 0 : artB.hashCode());
      result = prime * result + (branch == null ? 0 : branch.hashCode());
      result = prime * result + gammaId.hashCode();
      result = prime * result + (relTypeUuid == null ? 0 : relTypeUuid.hashCode());
      result = prime * result + (relArtId == null ? 0 : relArtId.hashCode());
      result = prime * result + (Integer.toString(relOrder) == null ? 0 : Integer.hashCode(relOrder));
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
      IBasicGuidRelation other = (IBasicGuidRelation) obj;
      if (artA == null) {
         if (other.getArtA() != null) {
            return false;
         }
      } else if (!artA.equals(other.getArtA())) {
         return false;
      }
      if (artB == null) {
         if (other.getArtB() != null) {
            return false;
         }
      } else if (!artB.equals(other.getArtB())) {
         return false;
      }
      if (!isOnSameBranch(other)) {
         return false;
      }
      if (gammaId != other.getGammaId()) {
         return false;
      }
      if (relTypeUuid == null) {
         if (other.getRelTypeGuid() != null) {
            return false;
         }
      } else if (!relTypeUuid.equals(other.getRelTypeGuid())) {
         return false;
      }
      if (relArtId == null) {
         if (other.getRelArtId() != null) {
            return false;
         }
      } else if (!relArtId.equals(other.getRelArtId())) {
         return false;
      }
      return true;
   }

   public Long getRelationId() {
      return relationId;
   }

   public void setRelationId(Long relationId) {
      this.relationId = relationId;
   }

   public boolean is(RelationTypeToken... relationTypes) {
      for (RelationTypeToken relType : relationTypes) {
         if (relType.equals(getRelTypeGuid())) {
            return true;
         }
      }
      return false;
   }

   @Override
   public int getRelOrder() {
      return relOrder;
   }

   public void setRelOrder(int relOrder) {
      this.relOrder = relOrder;
   }

   @Override
   public Long getRelArtId() {
      return relArtId;
   }

   public void setRelArtId(Long relArtId) {
      this.relArtId = relArtId;
   }

}
