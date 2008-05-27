/*
 * Created on May 12, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.test;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Theron Virgin
 */
public class ConflictTestSuite {
   public static void oneTimeSetUp() throws Exception {
      ConflictTestManager.initializeConflictTest();
   }

   public static void oneTimeTearDown() throws Exception {
      //      ConflictTestManager.cleanUpConflictTest();
   }

   public static Test suite() {
      TestSuite suite = new TestSuite();
      // Only include short tests
      suite.addTest(new MergeBranchManagementTest("testGetMergeBranchNotCreated"));
      suite.addTest(new ConflictDetectionTest("testGetConflictsPerBranch"));
      suite.addTest(new MergeBranchManagementTest("testGetMergeBranchCreated"));
      suite.addTest(new ConflictDetectionTest("testBranchHasConflicts"));
      //Test conflict resolution
      suite.addTest(new ConflictResolutionTest("testResolveConflicts"));

      TestSetup wrapper = new TestSetup(suite) {
         protected void setUp() throws Exception {
            oneTimeSetUp();
         }

         protected void tearDown() throws Exception {
            oneTimeTearDown();
         }
      };

      return wrapper;
   }
}
