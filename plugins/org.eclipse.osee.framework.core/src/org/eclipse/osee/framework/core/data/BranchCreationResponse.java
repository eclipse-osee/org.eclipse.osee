/*
 * Created on Dec 1, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.data;

/**
 * @author Roberto E. Escobar
 */
public class BranchCreationResponse {

   private int branchId;

   public BranchCreationResponse(int branchId) {
      this.branchId = branchId;
   }

   public int getBranchId() {
      return branchId;
   }

   public void setBranchId(int branchId) {
      this.branchId = branchId;
   }

   @Override
   public String toString() {
      return "BranchCreationResponse [branchId=" + branchId + "]";
   }
}
