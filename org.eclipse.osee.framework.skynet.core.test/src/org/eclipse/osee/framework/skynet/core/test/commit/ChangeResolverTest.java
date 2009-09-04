package org.eclipse.osee.framework.skynet.core.test.commit;

import org.junit.Test;

public class ChangeResolverTest {

   @Test
   public void testArtifactResolve() {
      /*
       * Commit Responsibility: Compute net changes between source branch to destination branch including any merge
       * changes and add those results to a new transaction on the destination branch. Do not assume any particular
       * branch hierarchical relationship between the source and destination branches.
       * Load the changes from the source branch.
       * Handle case where destination branch is missing an artifact that was modified (not new) on the source branch.
       * Filter out all items that are both new/introduced and deleted on the source branch
       * Filter out all gammas that are already current on the destination branch.
       * Apply changes from merge branch
       * Compute mod type for commit transaction:
       * New and modified -> new
       * Introduced and modified -> introduced
       * Update tx_current for items on destination branch that will no longer be current
       * Update merge table with commit transaction id
       * Manage the branch state of the source, destination, and merge branches
       * If there are no net changes then stop the commit.
       */

   }
}
