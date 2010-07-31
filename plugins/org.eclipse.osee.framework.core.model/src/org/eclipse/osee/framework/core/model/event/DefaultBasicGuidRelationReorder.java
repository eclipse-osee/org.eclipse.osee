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

import org.eclipse.osee.framework.core.data.IRelationType;

/**
 * @author Donald G. Dunne
 */
public class DefaultBasicGuidRelationReorder implements IBasicRelationReorder {

   private DefaultBasicGuidArtifact parentArt;
   private String branchGuid;
   private String relTypeGuid;
   private RelationOrderModType modType;

   public DefaultBasicGuidRelationReorder(RelationOrderModType modType, String branchGuid, String relTypeGuid, DefaultBasicGuidArtifact artA) {
      this.modType = modType;
      this.branchGuid = branchGuid;
      this.relTypeGuid = relTypeGuid;
      this.parentArt = artA;
   }

   @Override
   public DefaultBasicGuidArtifact getParentArt() {
      return parentArt;
   }

   @Override
   public String getBranchGuid() {
      return branchGuid;
   }

   @Override
   public String getRelTypeGuid() {
      return relTypeGuid;
   }

   public void setArtA(DefaultBasicGuidArtifact artA) {
      this.parentArt = artA;
   }

   public void setBranchGuid(String branchGuid) {
      this.branchGuid = branchGuid;
   }

   public void setRelTypeGuid(String relTypeGuid) {
      this.relTypeGuid = relTypeGuid;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (parentArt == null ? 0 : parentArt.hashCode());
      result = prime * result + (branchGuid == null ? 0 : branchGuid.hashCode());
      result = prime * result + (relTypeGuid == null ? 0 : relTypeGuid.hashCode());
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
      IBasicRelationReorder other = (IBasicRelationReorder) obj;
      if (parentArt == null) {
         if (other.getParentArt() != null) {
            return false;
         }
      } else if (!parentArt.equals(other.getParentArt())) {
         return false;
      }
      if (branchGuid == null) {
         if (other.getBranchGuid() != null) {
            return false;
         }
      } else if (!branchGuid.equals(other.getBranchGuid())) {
         return false;
      }
      if (relTypeGuid == null) {
         if (other.getRelTypeGuid() != null) {
            return false;
         }
      } else if (!relTypeGuid.equals(other.getRelTypeGuid())) {
         return false;
      }
      return true;
   }

   public boolean is(IRelationType... relationTypes) {
      for (IRelationType relType : relationTypes) {
         if (relType.getGuid().equals(getRelTypeGuid())) {
            return true;
         }
      }
      return false;
   }

   @Override
   public RelationOrderModType getModType() {
      return modType;
   }

   public void setModType(RelationOrderModType modType) {
      this.modType = modType;
   }

}
