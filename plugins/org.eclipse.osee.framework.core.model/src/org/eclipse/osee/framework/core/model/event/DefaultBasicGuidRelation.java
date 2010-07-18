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
public class DefaultBasicGuidRelation implements IBasicGuidRelation {

   DefaultBasicGuidArtifact artA, artB;
   String branchGuid;
   String relTypeGuid;
   int gammaId;
   int relationId;

   public DefaultBasicGuidRelation(String branchGuid, String relTypeGuid, int relationId, int gammaId, DefaultBasicGuidArtifact artA, DefaultBasicGuidArtifact artB) {
      this.branchGuid = branchGuid;
      this.relTypeGuid = relTypeGuid;
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
   public String getBranchGuid() {
      return branchGuid;
   }

   @Override
   public int getGammaId() {
      return gammaId;
   }

   @Override
   public String getRelTypeGuid() {
      return relTypeGuid;
   }

   public void setArtA(DefaultBasicGuidArtifact artA) {
      this.artA = artA;
   }

   public void setArtB(DefaultBasicGuidArtifact artB) {
      this.artB = artB;
   }

   public void setBranchGuid(String branchGuid) {
      this.branchGuid = branchGuid;
   }

   public void setRelTypeGuid(String relTypeGuid) {
      this.relTypeGuid = relTypeGuid;
   }

   public void setGammaId(int gammaId) {
      this.gammaId = gammaId;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((artA == null) ? 0 : artA.hashCode());
      result = prime * result + ((artB == null) ? 0 : artB.hashCode());
      result = prime * result + ((branchGuid == null) ? 0 : branchGuid.hashCode());
      result = prime * result + gammaId;
      result = prime * result + ((relTypeGuid == null) ? 0 : relTypeGuid.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      IBasicGuidRelation other = (IBasicGuidRelation) obj;
      if (artA == null) {
         if (other.getArtA() != null) return false;
      } else if (!artA.equals(other.getArtA())) return false;
      if (artB == null) {
         if (other.getArtB() != null) return false;
      } else if (!artB.equals(other.getArtB())) return false;
      if (branchGuid == null) {
         if (other.getBranchGuid() != null) return false;
      } else if (!branchGuid.equals(other.getBranchGuid())) return false;
      if (gammaId != other.getGammaId()) return false;
      if (relTypeGuid == null) {
         if (other.getRelTypeGuid() != null) return false;
      } else if (!relTypeGuid.equals(other.getRelTypeGuid())) return false;
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
         if (relType.getGuid().equals(getRelTypeGuid())) return true;
      }
      return false;
   }

}
