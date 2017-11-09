/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.event;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IRelationType;

/**
 * @author Donald G. Dunne
 */
public class DefaultBasicIdRelation implements IBasicGuidRelation {

   DefaultBasicGuidArtifact artA, artB;
   Long branchUuid;
   Long relTypeUuid;
   int gammaId;
   int relationId;

   public DefaultBasicIdRelation(BranchId branch, Long relTypeUuid, int relationId, int gammaId, DefaultBasicGuidArtifact artA, DefaultBasicGuidArtifact artB) {
      this.branchUuid = branch.getId();
      this.relTypeUuid = relTypeUuid;
      this.relationId = relationId;
      this.gammaId = gammaId;
      this.artA = artA;
      this.artB = artB;
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
   public Long getBranchId() {
      return branchUuid;
   }

   @Override
   public int getGammaId() {
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

   public void setBranchUuid(Long branchUuid) {
      this.branchUuid = branchUuid;
   }

   public void setRelTypeGuid(Long relTypeGuid) {
      this.relTypeUuid = relTypeGuid;
   }

   public void setGammaId(int gammaId) {
      this.gammaId = gammaId;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (artA == null ? 0 : artA.hashCode());
      result = prime * result + (artB == null ? 0 : artB.hashCode());
      result = prime * result + (branchUuid == null ? 0 : branchUuid.hashCode());
      result = prime * result + gammaId;
      result = prime * result + (relTypeUuid == null ? 0 : relTypeUuid.hashCode());
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
      if (branchUuid == null) {
         if (other.getBranchId() != null) {
            return false;
         }
      } else if (!isOnSameBranch(other)) {
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
      return true;
   }

   public int getRelationId() {
      return relationId;
   }

   public void setRelationId(int relationId) {
      this.relationId = relationId;
   }

   public boolean is(IRelationType... relationTypes) {
      for (IRelationType relType : relationTypes) {
         if (relType.equals(getRelTypeGuid())) {
            return true;
         }
      }
      return false;
   }

}
