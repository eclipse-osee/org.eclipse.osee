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

package org.eclipse.osee.framework.skynet.core.test2.cases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {MergeBranchManagementTest.class, ConflictDetectionTest.class, MergeBranchManagementTest.class,
      ConflictResolutionTest.class, ConflictedBranchCommitingTest.class, CommitTest.class})
/**
 * @author Theron Virgin
 */
public class ConflictTestSuite {
   //   @BeforeSuite
   // TODO Not valid for JUnit4, need different way to do this
   public static void oneTimeSetUp() throws Exception {
      ConflictTestManager.initializeConflictTest();
   }

   //   @AfterSuite
   public static void oneTimeTearDown() throws Exception {
      ConflictTestManager.cleanUpConflictTest();
   }

}
