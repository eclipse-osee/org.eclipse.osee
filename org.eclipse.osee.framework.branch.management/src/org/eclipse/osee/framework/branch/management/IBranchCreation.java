/*
 * Created on May 8, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management;


/**
 * @author Andrew M Finkbeiner
 */
public interface IBranchCreation {
   public int createRootBranch(int parentBranchId, String childBranchShortName, String childBranchName, String creationComment, int associatedArtifactId, int authorId, String staticBranchName) throws Exception;

   public int createChildBranch(int parentBranchId, String childBranchShortName, String childBranchName, String creationComment, int associatedArtifactId, int authorId) throws Exception;

}
