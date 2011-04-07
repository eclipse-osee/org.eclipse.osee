/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.branch.management.test.purge;

import static org.junit.Assert.assertFalse;
import junit.framework.Assert;
import org.eclipse.osee.framework.branch.management.purge.PurgeBranchRecursive;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Before;
import org.junit.Test;

public class TestPurgeBranchRecursive {

   @Before
   public void checkTestDb() throws OseeCoreException {
      assertFalse(TestUtil.isProductionDb());
   }

   @Test
   public void testPurgeBranchRecursive() throws OseeCoreException {
      //create a root branch
      Branch root = BranchManager.createTopLevelBranch("root");
      //add a child to root (parent)
      Branch parent = BranchManager.createWorkingBranch(root, "parent", UserManager.getUser());

      //add children branches to parent
      Branch child1 = BranchManager.createWorkingBranch(parent, "child1", UserManager.getUser());
      Branch child2 = BranchManager.createWorkingBranch(parent, "child2", UserManager.getUser());
      Branch child3 = BranchManager.createWorkingBranch(parent, "child3", UserManager.getUser());
      //make one a merge branch
      child3.setBranchType(BranchType.MERGE);

      Branch grandChild1 = BranchManager.createWorkingBranch(child1, "grandChild1", UserManager.getUser());
      grandChild1.setBranchType(BranchType.MERGE);
      Branch grandChild2 = BranchManager.createWorkingBranch(child2, "grandChild1", UserManager.getUser());
      Branch grandChild3 = BranchManager.createWorkingBranch(child3, "grandChild1", UserManager.getUser());

      //count how many children of root there are
      Assert.assertEquals(7, root.getAllChildBranches(true).size());
      //call purgeBranch recursive
      OperationLogger ol = new OperationLogger() {
         @Override
         public void log(String... row) {
            //do nothing
         }
      };

      PurgeBranchRecursive purge = new PurgeBranchRecursive(ol, parent, null, null);
      //count how many children branches are in root (should be 0)
      Assert.assertEquals(7, root.getAllChildBranches(true).size());
   }
}
