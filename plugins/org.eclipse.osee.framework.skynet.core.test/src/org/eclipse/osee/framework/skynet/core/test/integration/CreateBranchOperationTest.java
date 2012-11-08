/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.test.integration;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.DeleteBranchOperation;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * {@link CreateBranchOperation}
 */
public class CreateBranchOperationTest {

   static final String NAME = CreateBranchOperationTest.class.getSimpleName() + " %s";

   @Rule
   public TestName testName = new TestName();

   @Test
   public void test_checkPreconditions_AllowWorkingBranchCreationIfDeleted() throws OseeCoreException {
      Artifact folder = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, CoreBranches.COMMON);
      folder.persist("");

      Branch workingBranch =
         BranchManager.createWorkingBranch(DemoSawBuilds.SAW_Bld_1, String.format(NAME, testName.getMethodName()),
            folder);
      workingBranch.setBranchState(BranchState.DELETED);
      BranchManager.persist(workingBranch);

      Branch workingBranch2 =
         BranchManager.createWorkingBranch(workingBranch, workingBranch.getName() + " child", folder);

      Operations.executeWorkAndCheckStatus(new DeleteBranchOperation(workingBranch));
      Operations.executeWorkAndCheckStatus(new DeleteBranchOperation(workingBranch2));

      folder.deleteAndPersist();
   }

   @Test
   public void test_checkPreconditions_AllowWorkingBranchCreationIfRebaselined() throws OseeCoreException {
      Artifact folder = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, CoreBranches.COMMON);
      folder.persist("");

      Branch workingBranch =
         BranchManager.createWorkingBranch(DemoSawBuilds.SAW_Bld_1, String.format(NAME, testName.getMethodName()),
            folder);
      workingBranch.setBranchState(BranchState.REBASELINED);
      BranchManager.persist(workingBranch);

      Branch workingBranch2 =
         BranchManager.createWorkingBranch(workingBranch, workingBranch.getName() + " child", folder);

      Operations.executeWorkAndCheckStatus(new DeleteBranchOperation(workingBranch));
      Operations.executeWorkAndCheckStatus(new DeleteBranchOperation(workingBranch2));

      folder.deleteAndPersist();

   }

   /**
    * expecting really an OseeStateException from CreateBranchOperation
    */
   @Test
   public void test_checkPreconditions_DisallowWorkingBranchCreation() throws OseeCoreException {
      Set<BranchState> subset = new HashSet<BranchState>(Arrays.asList(BranchState.values()));

      Collection<BranchState> allowedStates = Arrays.asList(BranchState.DELETED, BranchState.REBASELINED);
      subset.removeAll(allowedStates);

      int exceptionsCaught = 0;
      for (BranchState state : subset) {
         Artifact folder = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, CoreBranches.COMMON);
         folder.persist("");

         Branch workingBranch =
            BranchManager.createWorkingBranch(DemoSawBuilds.SAW_Bld_1, String.format(NAME, testName.getMethodName()),
               folder);

         workingBranch.setBranchState(state);
         BranchManager.persist(workingBranch);

         try {
            BranchManager.createWorkingBranch(workingBranch, workingBranch.getName() + " child", folder);
         } catch (OseeCoreException ex) {
            exceptionsCaught++;
         }

         Operations.executeWorkAndCheckStatus(new DeleteBranchOperation(workingBranch));

         folder.deleteAndPersist();
      }

      Assert.assertEquals(
         "CreateBranchOperation.checkPreconditions() should throw " + (subset.size() - allowedStates.size()) + " exceptions",
         exceptionsCaught, subset.size());
   }
}
