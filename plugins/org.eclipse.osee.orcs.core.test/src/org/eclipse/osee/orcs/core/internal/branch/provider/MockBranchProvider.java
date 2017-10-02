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
package org.eclipse.osee.orcs.core.internal.branch.provider;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author John R. Misinco
 */
public final class MockBranchProvider implements BranchProvider {

   private final static String ROOT_BRANCH_NAME = "ROOT";

   public static Collection<Branch> createTestBranches()  {
      Collection<Branch> branches = new ArrayList<>();
      //create a root branch
      Branch root =
         new Branch(Lib.generateUuid(), ROOT_BRANCH_NAME, BranchType.SYSTEM_ROOT, BranchState.COMMITTED, false, false);

      //add a child to root (parent)
      Branch parent =
         new Branch(Lib.generateUuid(), "parent", BranchType.SYSTEM_ROOT, BranchState.CREATED, false, false);
      parent.setParentBranch(root);

      //add children branches to parent
      Branch child1 =
         new Branch(Lib.generateUuid(), "child1", BranchType.SYSTEM_ROOT, BranchState.MODIFIED, false, false);
      child1.setParentBranch(parent);
      child1.setArchived(true);
      child1.setBranchState(BranchState.DELETED);

      //this branch should get deleted
      Branch child2 = new Branch(Lib.generateUuid(), "child2", BranchType.WORKING, BranchState.DELETED, true, false);
      child2.setParentBranch(parent);

      Branch child3 =
         new Branch(Lib.generateUuid(), "child3", BranchType.SYSTEM_ROOT, BranchState.CREATED, false, false);
      //make one a merge branch
      child3.setBranchType(BranchType.MERGE);
      child3.setParentBranch(parent);

      Branch grandChild1 =
         new Branch(Lib.generateUuid(), "grandChild1", BranchType.MERGE, BranchState.DELETED, false, false);
      grandChild1.setArchived(true);
      grandChild1.setParentBranch(child1);

      Branch grandChild2 =
         new Branch(Lib.generateUuid(), "grandChild2", BranchType.SYSTEM_ROOT, BranchState.MODIFIED, false, false);
      grandChild2.setParentBranch(child1);

      //this branch should get deleted
      Branch grandChild3 =
         new Branch(Lib.generateUuid(), "grandChild3", BranchType.WORKING, BranchState.DELETED, true, false);
      grandChild3.setParentBranch(child2);

      //add branches in a random order
      branches.add(child2);
      branches.add(grandChild2);
      branches.add(grandChild3);
      branches.add(child1);
      branches.add(root);
      branches.add(child3);
      branches.add(grandChild1);
      branches.add(parent);

      return branches;
   }

   public static Branch getRootBranch(Collection<Branch> branches) {
      for (Branch branch : branches) {
         if (ROOT_BRANCH_NAME.equals(branch.getName())) {
            return branch;
         }
      }
      return null;
   }

   @Override
   public Collection<Branch> getBranches()  {
      return MockBranchProvider.createTestBranches();
   }
}