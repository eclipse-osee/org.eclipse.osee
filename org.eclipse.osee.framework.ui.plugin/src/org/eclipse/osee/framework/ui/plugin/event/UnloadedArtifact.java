/*
 * Created on Sep 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.plugin.event;

/**
 * @author Donald G. Dunne
 */
public class UnloadedArtifact {
   private int artifactId;
   private int branchId;
   private int artifactTypeId;

   public UnloadedArtifact(int branchId, int artifactId, int artifactTypeId) {
      this.branchId = branchId;
      this.artifactId = artifactId;
      this.artifactTypeId = artifactTypeId;
   }

   /**
    * @return the artifactId
    */
   public int getArtifactId() {
      return artifactId;
   }

   /**
    * @param artifactId the artifactId to set
    */
   public void setArtifactId(int artifactId) {
      this.artifactId = artifactId;
   }

   /**
    * @return the branchId
    */
   public int getBranchId() {
      return branchId;
   }

   /**
    * @param branchId the branchId to set
    */
   public void setBranchId(int branchId) {
      this.branchId = branchId;
   }

   /**
    * @return the artifactTypeId
    */
   public int getArtifactTypeId() {
      return artifactTypeId;
   }

   /**
    * @param artifactTypeId the artifactTypeId to set
    */
   public void setArtifactTypeId(int artifactTypeId) {
      this.artifactTypeId = artifactTypeId;
   }

}
