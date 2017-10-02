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
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.DeleteBranchOperation;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test Case for {@link CreateBranchOperation}
 */
public class CreateBranchOperationTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo method = new TestInfo();

   @Test
   public void test_checkPreconditions_AllowWorkingBranchCreationIfDeleted() {
      Artifact folder = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, CoreBranches.COMMON);
      folder.persist("");

      IOseeBranch workingBranch = BranchManager.createWorkingBranch(SAW_Bld_1, method.getQualifiedTestName(), folder);
      BranchManager.setState(workingBranch, BranchState.DELETED);

      IOseeBranch workingBranch2 =
         BranchManager.createWorkingBranch(workingBranch, getName(workingBranch, "child"), folder);

      Operations.executeWorkAndCheckStatus(new DeleteBranchOperation(workingBranch));
      Operations.executeWorkAndCheckStatus(new DeleteBranchOperation(workingBranch2));

      folder.deleteAndPersist();
   }

   @Test
   public void test_checkPreconditions_AllowWorkingBranchCreationIfRebaselined() {
      Artifact folder = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, CoreBranches.COMMON);
      folder.persist("");

      IOseeBranch workingBranch = BranchManager.createWorkingBranch(SAW_Bld_1, method.getQualifiedTestName(), folder);
      BranchManager.setState(workingBranch, BranchState.REBASELINED);

      IOseeBranch workingBranch2 =
         BranchManager.createWorkingBranch(workingBranch, getName(workingBranch, "child"), folder);

      Operations.executeWorkAndCheckStatus(new DeleteBranchOperation(workingBranch));
      Operations.executeWorkAndCheckStatus(new DeleteBranchOperation(workingBranch2));

      folder.deleteAndPersist();
   }

   /**
    * expecting really an OseeStateException from CreateBranchOperation
    */
   @Test
   public void test_checkPreconditions_DisallowWorkingBranchCreation() {
      Set<BranchState> subset = new HashSet<>(Arrays.asList(BranchState.values()));

      Collection<BranchState> allowedStates = Arrays.asList(BranchState.DELETED, BranchState.REBASELINED);
      subset.removeAll(allowedStates);

      int exceptionsCaught = 0;
      for (BranchState state : subset) {
         Artifact folder = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, CoreBranches.COMMON);
         folder.persist("");

         IOseeBranch workingBranch =
            BranchManager.createWorkingBranch(SAW_Bld_1, method.getQualifiedTestName(), folder);
         BranchManager.setState(workingBranch, state);

         try {
            BranchManager.createWorkingBranch(workingBranch, getName(workingBranch, "child"), folder);
         } catch (OseeCoreException ex) {
            exceptionsCaught++;
         }

         Operations.executeWorkAndCheckStatus(new DeleteBranchOperation(workingBranch));

         folder.deleteAndPersist();
      }

      String errorMessage = String.format("CreateBranchOperation.checkPreconditions() should throw [%s] exceptions",
         subset.size() - allowedStates.size());
      assertEquals(errorMessage, exceptionsCaught, subset.size());
   }

   private String getName(IOseeBranch branch, String value) {
      return String.format("%s_%s", branch.getName(), value);
   }

}
