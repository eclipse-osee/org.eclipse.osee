/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact;

import static org.junit.Assert.assertFalse;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class BranchManagerTest {

   public static String branchName = "BranchManagerTest";
   public static String branchReNamed = "BranchManagerTest-Renamed";

   private static Branch testBranch;

   @BeforeClass
   public static void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse(TestUtil.isProductionDb());

      testBranch =
         BranchManager.createWorkingBranch(BranchManager.getCommonBranch(), branchName,
            UserManager.getUser(SystemUser.OseeSystem));
   }

   @Test
   public void testBranchRetrieval() throws Exception {
      testBranch.equals(BranchManager.getBranch(branchName));
   }

   @Test
   public void testRenameBranch() throws Exception {
      Assert.assertEquals(branchName, testBranch.getName());

      testBranch.setName(branchReNamed);
      BranchManager.persist(testBranch);

      testBranch = BranchManager.getBranch(branchReNamed);
      Assert.assertEquals(branchReNamed, testBranch.getName());
   }

   @Test(expected = BranchDoesNotExist.class)
   public void testLookForOldBranch() throws OseeCoreException {
      Assert.assertNull("Old branch is found and should be renamed...", BranchManager.getBranch(branchName));
   }

   @AfterClass
   public static void testCleanupPost() throws Exception {
      cleanup();
   }

   private static void cleanup() throws Exception {
      BranchManager.purgeBranch(BranchManager.getBranch(branchReNamed));
   }
}
