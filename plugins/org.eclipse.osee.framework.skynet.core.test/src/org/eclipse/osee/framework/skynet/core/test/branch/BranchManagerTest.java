/*
 * Created on Dec 15, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.test.branch;

import static org.junit.Assert.assertFalse;
import java.util.Arrays;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.After;
import org.junit.Before;

/**
 * @author Donald G. Dunne
 */
public class BranchManagerTest {

   public static String branchName = "BranchManagerTest";
   public static String branchReNamed = "BranchManagerTest-Renamed";

   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse(TestUtil.isProductionDb());
      cleanup();
   }

   @org.junit.Test
   public void testRenameBranch() throws Exception {

      Assert.assertEquals("Branch should not exist", 0, BranchManager.getBranchesByName(branchName).size());
      Assert.assertEquals("Branch should not exist", 0, BranchManager.getBranchesByName(branchReNamed).size());

      // create a new working branch
      Branch branch =
            BranchManager.createWorkingBranch(BranchManager.getCommonBranch(), branchName,
                  UserManager.getUser(SystemUser.OseeSystem));

      Assert.assertNotNull(BranchManager.getBranch(branchName));
      Assert.assertEquals("Branch should not exist", 0, BranchManager.getBranchesByName(branchReNamed).size());

      branch.setName(branchReNamed);
      BranchManager.persist(branch);

      Assert.assertEquals("Branch should not exist", 0, BranchManager.getBranchesByName(branchName).size());
      Assert.assertNotNull(BranchManager.getBranch(branchReNamed));

   }

   @After
   public void testCleanupPost() throws Exception {
      cleanup();
   }

   private static void cleanup() throws Exception {
      if (BranchManager.getBranchesByName(branchName).size() > 0) {
         FrameworkTestUtil.purgeWorkingBranches(Arrays.asList(branchName));
      }
      if (BranchManager.getBranchesByName(branchReNamed).size() > 0) {
         FrameworkTestUtil.purgeWorkingBranches(Arrays.asList(branchReNamed));
      }
   }

}
