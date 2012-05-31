/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.enums.RelationSide;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationData extends OrcsObject {

   private int parentId = -1;

   private int artIdA = -1;
   private int artIdB = -1;
   private String rationale = "";

   public void setRelationId(int relationId) {
      setLocalId(relationId);
   }

   public void setArtIdA(int artIdA) {
      this.artIdA = artIdA;
   }

   public void setArtIdB(int artIdB) {
      this.artIdB = artIdB;
   }

   public void setRelationTypeId(long relationTypeUUId) {
      setTypeUuid(relationTypeUUId);
   }

   public void setRationale(String rationale) {
      this.rationale = rationale;
   }

   public int getRelationId() {
      return getLocalId();
   }

   public int getArtIdA() {
      return artIdA;
   }

   public int getArtIdB() {
      return artIdB;
   }

   public long getRelationTypeUUId() {
      return getTypeUuid();
   }

   public String getRationale() {
      return rationale;
   }

   public void setParentId(int parentId) {
      this.parentId = parentId;
   }

   public int getParentId() {
      return parentId;
   }

   public int getArtIdOn(RelationSide side) {
      return RelationSide.SIDE_A == side ? getArtIdA() : getArtIdB();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + artIdA;
      result = prime * result + artIdB;
      result = prime * result + getBranchId();
      result = prime * result + getGammaId();
      result = prime * result + ((getModType() == null) ? 0 : getModType().hashCode());
      result = prime * result + parentId;
      result = prime * result + ((rationale == null) ? 0 : rationale.hashCode());
      result = prime * result + getLocalId();
      result = (int) (prime * result + getTypeUuid());
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
      RelationData other = (RelationData) obj;
      if (artIdA != other.artIdA) {
         return false;
      }
      if (artIdB != other.artIdB) {
         return false;
      }
      if (parentId != other.parentId) {
         return false;
      }
      if (rationale == null) {
         if (other.rationale != null) {
            return false;
         }
      } else if (!rationale.equals(other.rationale)) {
         return false;
      }
      return super.equals(obj);
   }

   @Override
   public String toString() {
      return String.format(
         "RelationRow: parent[%d] relation[%d] artA[%d] artB[%d] branch[%d] gamma[%d], relationType[%d] rationale[%s]",
         parentId, getLocalId(), artIdA, artIdB, getBranchId(), getGammaId(), getTypeUuid(), rationale,
         getModType().name());
   }

}
