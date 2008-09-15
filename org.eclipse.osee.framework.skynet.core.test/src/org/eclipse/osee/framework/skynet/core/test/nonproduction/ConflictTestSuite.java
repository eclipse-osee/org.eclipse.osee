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

package org.eclipse.osee.framework.skynet.core.test.nonproduction;

import org.eclipse.osee.framework.skynet.core.test.nonproduction.components.ConflictDetectionTest;
import org.eclipse.osee.framework.skynet.core.test.nonproduction.components.ConflictResolutionTest;
import org.eclipse.osee.framework.skynet.core.test.nonproduction.components.ConflictTestManager;
import org.eclipse.osee.framework.skynet.core.test.nonproduction.components.ConflictedBranchCommitingTest;
import org.eclipse.osee.framework.skynet.core.test.nonproduction.components.MergeBranchManagementTest;
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
      ConflictTestManager.cleanUpConflictTest();
   }

   public static Test suite() {
      TestSuite suite = new TestSuite();
      // Only include short tests
      suite.addTest(new MergeBranchManagementTest("testGetMergeBranchNotCreated"));
      suite.addTest(new ConflictDetectionTest("testGetConflictsPerBranch"));
      suite.addTest(new MergeBranchManagementTest("testGetMergeBranchCreated"));
      suite.addTest(new ConflictedBranchCommitingTest("CheckCommitWithResolutionErrors"));
      //Test conflict resolution
      suite.addTest(new ConflictResolutionTest("testResolveConflicts"));
      suite.addTest(new ConflictedBranchCommitingTest("CheckCommitWithoutResolutionErrors"));

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
