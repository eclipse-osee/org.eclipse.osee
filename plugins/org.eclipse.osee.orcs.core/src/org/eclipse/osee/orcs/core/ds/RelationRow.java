/*
 * Created on Sep 28, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.enums.ModificationType;

public class RelationRow {

   private int relationId = -1;
   private int artIdA = -1;
   private int artIdB = -1;
   private int branchId = -1;
   private int gammaId = -1;
   private int relationTypeId = -1;
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

   public void setRelationTypeId(int relationTypeId) {
      this.relationTypeId = relationTypeId;
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

   public int getRelationTypeId() {
      return relationTypeId;
   }

   public String getRationale() {
      return rationale;
   }

   public ModificationType getModType() {
      return modType;
   }

}
