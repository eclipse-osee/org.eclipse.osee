/*
 * Created on Sep 28, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationSide;

public class RelationRow {

   private int parentId = -1;
   private int relationId = -1;
   private int artIdA = -1;
   private int artIdB = -1;
   private int branchId = -1;
   private int gammaId = -1;
   private long relationTypeUUId = -1l;
   private String rationale = "";
   private ModificationType modType = null;

   public void setRelationId(int relationId) {
      this.relationId = relationId;
   }

   public void setArtIdA(int artIdA) {
      this.artIdA = artIdA;
   }

   public void setArtIdB(int artIdB) {
      this.artIdB = artIdB;
   }

   public void setBranchId(int branchId) {
      this.branchId = branchId;
   }

   public void setGammaId(int gammaId) {
      this.gammaId = gammaId;
   }

   public void setRelationTypeId(long relationTypeUUId) {
      this.relationTypeUUId = relationTypeUUId;
   }

   public void setRationale(String rationale) {
      this.rationale = rationale;
   }

   public void setModType(ModificationType modType) {
      this.modType = modType;
   }

   public int getRelationId() {
      return relationId;
   }

   public int getArtIdA() {
      return artIdA;
   }

   public int getArtIdB() {
      return artIdB;
   }

   public int getBranchId() {
      return branchId;
   }

   public int getGammaId() {
      return gammaId;
   }

   public long getRelationTypeUUId() {
      return relationTypeUUId;
   }

   public String getRationale() {
      return rationale;
   }

   public ModificationType getModType() {
      return modType;
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
      result = prime * result + branchId;
      result = prime * result + gammaId;
      result = prime * result + ((modType == null) ? 0 : modType.hashCode());
      result = prime * result + parentId;
      result = prime * result + ((rationale == null) ? 0 : rationale.hashCode());
      result = prime * result + relationId;
      result = (int) (prime * result + relationTypeUUId);
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
      RelationRow other = (RelationRow) obj;
      if (artIdA != other.artIdA) {
         return false;
      }
      if (artIdB != other.artIdB) {
         return false;
      }
      if (branchId != other.branchId) {
         return false;
      }
      if (gammaId != other.gammaId) {
         return false;
      }
      if (modType != other.modType) {
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
      if (relationId != other.relationId) {
         return false;
      }
      if (relationTypeUUId != other.relationTypeUUId) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return String.format(
         "RelationRow: parent[%d] relation[%d] artA[%d] artB[%d] branch[%d] gamma[%d], relationType[%d] rationale[%s]",
         parentId, relationId, artIdA, artIdB, branchId, gammaId, relationTypeUUId, rationale, modType.name());
   }

}
