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
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.utils.Asserts;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.operation.FinishUpdateBranchOperation;
import org.eclipse.osee.framework.skynet.core.artifact.operation.UpdateBranchOperation;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.update.ConflictResolverOperation;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class BranchStateTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Before
   public void setUp() throws Exception {
      BranchManager.refreshBranches();
   }

   @Test
   public void testCreateState() {
      String originalBranchName = "Create State Branch";
      BranchId workingBranch = null;
      try {
         workingBranch = BranchManager.createWorkingBranch(SAW_Bld_1, originalBranchName);
         assertEquals(BranchState.CREATED, BranchManager.getState(workingBranch));
         assertTrue(BranchManager.isEditable(workingBranch));
      } finally {
         if (workingBranch != null) {
            BranchManager.purgeBranch(workingBranch);
         }
      }
   }

   @Test
   public void testModifiedState() {
      String originalBranchName = "Modified State Branch";
      IOseeBranch workingBranch = null;
      try {
         workingBranch = BranchManager.createWorkingBranch(SAW_Bld_1, originalBranchName);
         assertEquals(BranchState.CREATED, BranchManager.getState(workingBranch));
         assertTrue(BranchManager.isEditable(workingBranch));

         Artifact change = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, workingBranch,
            "Test Object on Working Branch");
         change.persist(getClass().getSimpleName());

         assertEquals(BranchState.MODIFIED, BranchManager.getState(workingBranch));
         assertTrue(BranchManager.isEditable(workingBranch));
      } finally {
         if (workingBranch != null) {
            BranchManager.purgeBranch(workingBranch);
         }
      }
   }

   @Test
   public void testDeleteState() throws InterruptedException {
      String originalBranchName = "Deleted State Branch";
      IOseeBranch workingBranch = null;
      try {
         workingBranch = BranchManager.createWorkingBranch(SAW_Bld_1, originalBranchName);
         assertEquals(BranchState.CREATED, BranchManager.getState(workingBranch));
         assertTrue(BranchManager.isEditable(workingBranch));

         Job job = BranchManager.deleteBranch(workingBranch);
         job.join();
         assertEquals(BranchState.DELETED, BranchManager.getState(workingBranch));
         assertTrue(BranchManager.isArchived(workingBranch));
         assertTrue(!BranchManager.isEditable(workingBranch));
         assertTrue(BranchManager.getState(workingBranch).isDeleted());
      } finally {
         if (workingBranch != null) {
            // needed to allow for archiving to occur
            Thread.sleep(5000);
            BranchManager.purgeBranch(workingBranch);
         }
      }
   }

   @Test
   public void testPurgeState() throws InterruptedException {
      String originalBranchName = "Purged State Branch";
      IOseeBranch workingBranch = null;
      boolean branchPurged = false;
      try {
         workingBranch = BranchManager.createWorkingBranch(SAW_Bld_1, originalBranchName);
         assertEquals(BranchState.CREATED, BranchManager.getState(workingBranch));
         assertTrue(BranchManager.isEditable(workingBranch));

         Branch fullBranch = BranchManager.getBranch(workingBranch);
         BranchManager.purgeBranch(workingBranch);
         branchPurged = true;

         assertEquals(BranchState.PURGED, fullBranch.getBranchState());
         assertTrue(fullBranch.isArchived());
      } finally {
         if (workingBranch != null && !branchPurged) {
            // needed to allow for archiving to occur
            Thread.sleep(5000);
            BranchManager.purgeBranch(workingBranch);
         }
      }
   }

   @Test
   public void testCommitState() throws InterruptedException {
      String originalBranchName = "Commit State Branch";
      IOseeBranch workingBranch = null;
      Artifact change = null;
      try {
         workingBranch = BranchManager.createWorkingBranch(SAW_Bld_1, originalBranchName);
         assertEquals(BranchState.CREATED, BranchManager.getState(workingBranch));
         assertTrue(BranchManager.isEditable(workingBranch));

         change =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, workingBranch, "A commit change");
         change.persist(getClass().getSimpleName());

         Artifact workingBranchRoot = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(workingBranch);
         workingBranchRoot.addChild(change);
         workingBranchRoot.persist(getClass().getSimpleName());

         assertEquals(BranchState.MODIFIED, BranchManager.getState(workingBranch));
         assertTrue(BranchManager.isEditable(workingBranch));

         ConflictManagerExternal conflictManager = new ConflictManagerExternal(SAW_Bld_1, workingBranch);
         BranchManager.commitBranch(null, conflictManager, true, false);

         assertEquals(BranchState.COMMITTED, BranchManager.getState(workingBranch));
         assertTrue(BranchManager.isArchived(workingBranch));
         assertTrue(!BranchManager.isEditable(workingBranch));
      } finally {
         if (workingBranch != null) {
            // needed to allow for archiving to occur
            Thread.sleep(5000);
            BranchManager.purgeBranch(workingBranch);
         }
      }
   }

   @Test
   public void testRebaselineBranchNoChanges() throws Exception {
      String originalBranchName = "UpdateBranch No Changes Test";
      IOseeBranch workingBranch = null;
      try {
         workingBranch = BranchManager.createWorkingBranch(SAW_Bld_1, originalBranchName);

         // Update the branch
         ConflictResolverOperation resolverOperation =
            new ConflictResolverOperation("Test 1 Resolver", BranchStateTest.class.getCanonicalName()) {

               @Override
               protected void doWork(IProgressMonitor monitor) throws Exception {
                  assertFalse("This code should not be executed since there shouldn't be any conflicts.",
                     wasExecuted());
               }
            };

         UpdateBranchOperation operation = new UpdateBranchOperation(workingBranch, resolverOperation);
         Asserts.assertOperation(operation, IStatus.OK);

         Assert.assertEquals(BranchState.DELETED, BranchManager.getState(workingBranch));
         Assert.assertEquals(Artifact.SENTINEL, BranchManager.getAssociatedArtifact(workingBranch));

         IOseeBranch newWorkingBranch = operation.getNewBranch();
         assertFalse(workingBranch.equals(newWorkingBranch));
         assertEquals(originalBranchName, newWorkingBranch.getName());
         assertTrue("New Working branch was not editable", BranchManager.isEditable(newWorkingBranch));
         assertFalse("New Working branch was editable", BranchManager.isEditable(workingBranch));
      } finally {
         cleanup(workingBranch, null);
      }
   }

   @Test
   public void testRebaselineWithoutConflicts() throws Exception {
      String originalBranchName = "UpdateBranch Test 1";
      Artifact baseArtifact = null;
      IOseeBranch workingBranch = null;
      Artifact change = null;
      try {
         baseArtifact =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, SAW_Bld_1, "Test Object");
         baseArtifact.setSoleAttributeFromString(CoreAttributeTypes.Annotation, "This is the base annotation");
         baseArtifact.persist(getClass().getSimpleName());

         Artifact rootArtifact = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(SAW_Bld_1);
         rootArtifact.addChild(baseArtifact);
         rootArtifact.persist(getClass().getSimpleName());

         workingBranch = BranchManager.createWorkingBranch(SAW_Bld_1, originalBranchName);

         // Add a new artifact on the working branch
         change = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, workingBranch,
            "Test Object on Working Branch");
         change.persist(getClass().getSimpleName());

         Artifact workingBranchRoot = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(workingBranch);
         workingBranchRoot.addChild(change);
         workingBranchRoot.persist(getClass().getSimpleName());

         // Make a change on the parent
         baseArtifact.setSoleAttributeFromString(CoreAttributeTypes.Annotation, "This is the updated annotation");
         baseArtifact.persist(getClass().getSimpleName());

         // Update the branch
         ConflictResolverOperation resolverOperation =
            new ConflictResolverOperation("Test 1 Resolver", BranchStateTest.class.getCanonicalName()) {

               @Override
               protected void doWork(IProgressMonitor monitor) throws Exception {
                  assertFalse("This code should not be executed since there shouldn't be any conflicts.",
                     wasExecuted());
               }
            };

         UpdateBranchOperation operation = new UpdateBranchOperation(workingBranch, resolverOperation);
         Asserts.assertOperation(operation, IStatus.OK);
         assertFalse("Resolver was executed", resolverOperation.wasExecuted());

         checkBranchWasRebaselined(originalBranchName, workingBranch);
         // Check that the associated artifact remained unchanged
         assertEquals(BranchManager.getAssociatedArtifactId(workingBranch), ArtifactId.SENTINEL);

         Collection<IOseeBranch> branches = BranchManager.getBranchesByName(originalBranchName);
         assertEquals("Check only 1 original branch", 1, branches.size());

         IOseeBranch newWorkingBranch = operation.getNewBranch();
         assertFalse(workingBranch.equals(newWorkingBranch));
         assertEquals(originalBranchName, newWorkingBranch.getName());
         assertTrue("New Working branch is editable", BranchManager.isEditable(newWorkingBranch));
      } finally {
         cleanup(workingBranch, null, change, baseArtifact);
      }
   }

   @Test
   public void testRebaselineWithConflicts() throws Exception {
      String originalBranchName = "UpdateBranch Test 2";
      Artifact baseArtifact = null;
      IOseeBranch workingBranch = null;
      IOseeBranch mergeBranch = null;
      Artifact sameArtifact = null;
      try {
         baseArtifact =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, SAW_Bld_1, "Test Object");
         baseArtifact.setSoleAttributeFromString(CoreAttributeTypes.Annotation, "This is the base annotation");
         baseArtifact.persist(getClass().getSimpleName());

         Artifact rootArtifact = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(SAW_Bld_1);
         rootArtifact.addChild(baseArtifact);
         rootArtifact.persist(getClass().getSimpleName());

         workingBranch = BranchManager.createWorkingBranch(SAW_Bld_1, originalBranchName);

         // Modify same artifact on working branch
         sameArtifact = ArtifactQuery.getArtifactFromId(baseArtifact, workingBranch);
         sameArtifact.setSoleAttributeFromString(CoreAttributeTypes.Annotation,
            "This is the working branch update annotation");
         sameArtifact.persist(getClass().getSimpleName());

         // Make a change on the parent
         baseArtifact.setSoleAttributeFromString(CoreAttributeTypes.Annotation, "This is the updated annotation");
         baseArtifact.persist(getClass().getSimpleName());

         ConflictResolverOperation resolverOperation =
            new ConflictResolverOperation("Test 2 Resolver", BranchStateTest.class.getCanonicalName()) {

               @Override
               protected void doWork(IProgressMonitor monitor) throws Exception {
                  assertTrue("This code should have been executed since there should be conflicts.", wasExecuted());
               }
            };

         IOperation operation = new UpdateBranchOperation(workingBranch, resolverOperation);
         Asserts.assertOperation(operation, IStatus.OK);

         assertTrue("Resolver not executed", resolverOperation.wasExecuted());

         assertTrue("Branch was archived", !BranchManager.isArchived(workingBranch));
         assertTrue("Branch was not marked as rebaseline in progress",
            BranchManager.getState(workingBranch).isRebaselineInProgress());
         assertTrue("Branch was not editable", BranchManager.isEditable(workingBranch));
         assertTrue("Branch state was set to rebaselined before complete",
            !BranchManager.getState(workingBranch).isRebaselined());

         assertEquals("Branch name was changed before update was complete", originalBranchName,
            workingBranch.getName());

         // Check that a new destination branch exists
         IOseeBranch destinationBranch = resolverOperation.getConflictManager().getDestinationBranch();
         assertTrue("Branch name not set correctly",
            destinationBranch.getName().startsWith(String.format("%s - for update -", originalBranchName)));
         assertTrue("Branch was not editable", BranchManager.isEditable(destinationBranch));

         // Check that we have a merge branch
         mergeBranch = BranchManager.getMergeBranch(workingBranch, destinationBranch);
         assertTrue("MergeBranch was not editable", BranchManager.isEditable(mergeBranch));
         assertEquals("Merge Branch should be in Created State", BranchState.CREATED,
            BranchManager.getState(mergeBranch));

         // Run FinishBranchUpdate and check
         FinishUpdateBranchOperation finishUpdateOperation =
            new FinishUpdateBranchOperation(resolverOperation.getConflictManager(), true, true);
         Asserts.assertOperation(finishUpdateOperation, IStatus.OK);

         checkBranchWasRebaselined(originalBranchName, workingBranch);

         Collection<IOseeBranch> branches = BranchManager.getBranchesByName(originalBranchName);
         assertEquals("Check only 1 original branch", 1, branches.size());

         IOseeBranch newWorkingBranch = branches.iterator().next();
         assertFalse(workingBranch.equals(newWorkingBranch));
         assertEquals(originalBranchName, newWorkingBranch.getName());
         assertTrue("New Working branch is editable", BranchManager.isEditable(newWorkingBranch));

         // Swapped successfully
         assertEquals(destinationBranch.getId(), newWorkingBranch.getId());
      } catch (Exception ex) {
         throw ex;
      } finally {
         cleanup(workingBranch, mergeBranch, sameArtifact, baseArtifact);

      }
   }

   private void cleanup(BranchId workingBranch, BranchId mergeBranch, Artifact... toDelete) {
      try {
         if (mergeBranch != null) {
            BranchManager.purgeBranch(mergeBranch);
         }
         if (workingBranch != null) {
            purgeBranchAndChildren(workingBranch);
         }
         if (toDelete != null && toDelete.length > 0) {
            Operations.executeWorkAndCheckStatus(new PurgeArtifacts(Arrays.asList(toDelete)));
         }
      } catch (Exception ex) {
         // Do Nothing;
      }
   }

   private void purgeBranchAndChildren(BranchId branch) {
      for (IOseeBranch child : BranchManager.getChildBranches(branch, true)) {
         BranchManager.purgeBranch(child);
      }
      BranchManager.purgeBranch(branch);
   }

   private void checkBranchWasRebaselined(String originalBranchName, BranchId branchToCheck) {
      assertTrue("Branch was not archived", BranchManager.isArchived(branchToCheck));
      assertTrue("Branch was still editable", !BranchManager.isEditable(branchToCheck));
      assertTrue("Branch state was not set as rebaselined", BranchManager.getState(branchToCheck).isRebaselined());
      assertTrue("Branch name not set correctly", BranchManager.getBranchName(branchToCheck).startsWith(
         String.format("%s - moved by update on -", originalBranchName)));
   }

}
