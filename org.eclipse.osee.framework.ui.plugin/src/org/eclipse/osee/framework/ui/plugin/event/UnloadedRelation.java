/*
 * Created on Sep 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.plugin.event;

/**
 * @author Donald G. Dunne
 */
public class UnloadedRelation {

   private final int artifactAId;
   private final int artifactATypeId;
   private final int artifactBId;
   private final int artifactBTypeId;
   private final int relationTypeId;
   private final int branchId;

   public UnloadedRelation(int branchId, int artifactAId, int artifactATypeId, int artifactBId, int artifactBTypeId, int relationTypeId) {
      this.branchId = branchId;
      this.artifactAId = artifactAId;
      this.artifactATypeId = artifactATypeId;
      this.artifactBId = artifactBId;
      this.artifactBTypeId = artifactBTypeId;
      this.relationTypeId = relationTypeId;
   }

   @Override
   public String toString() {
      return "UnloadedRelation A: " + artifactAId + " AType: " + artifactATypeId + " B:" + artifactBId + " BType: " + artifactBTypeId + " RelType: " + relationTypeId + " Branch: " + branchId;
   }

   /**
    * @return the artifactATypeId
    */
   public int getArtifactATypeId() {
      return artifactATypeId;
   }

   /**
    * @return the artifactBTypeId
    */
   public int getArtifactBTypeId() {
      return artifactBTypeId;
   }

   /**
    * @return the artifactAId
    */
   public int getArtifactAId() {
      return artifactAId;
   }

   /**
    * @return the artifactBId
    */
   public int getArtifactBId() {
      return artifactBId;
   }

   /**
    * @return the relationTypeId
    */
   public int getRelationTypeId() {
      return relationTypeId;
   }

   /**
    * @return the branchId
    */
   public int getBranchId() {
      return branchId;
   }

}
