/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.skynet.core;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.Asserts;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeHousekeepingRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.DeleteBranchOperation;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.operation.FinishUpdateBranchOperation;
import org.eclipse.osee.framework.skynet.core.artifact.operation.UpdateBranchOperation;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.update.ConflictResolverOperation;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

/**
 * @author Roberto E. Escobar
 */
public class BranchStateTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public MethodRule oseeHousekeepingRule = new OseeHousekeepingRule();

   private BranchEventListenerAsync branchEventListenerAsync;

   @Before
   public void setUp() throws Exception {
      BranchManager.refreshBranches();
      branchEventListenerAsync = new BranchEventListenerAsync();
   }

   @Test
   public void testRegistration() throws Exception {
      OseeEventManager.removeAllListeners();
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      OseeEventManager.addListener(branchEventListenerAsync);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      OseeEventManager.removeListener(branchEventListenerAsync);
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());
   }

   @Test
   public void testCreateState() {
      String originalBranchName = "Create State Branch";
      BranchId workingBranch = null;
      try {
         workingBranch = BranchManager.createWorkingBranch(SAW_Bld_1, originalBranchName);
         assertEquals(BranchState.MODIFIED, BranchManager.getState(workingBranch));
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
      BranchToken workingBranch = null;
      try {
         workingBranch = BranchManager.createWorkingBranch(SAW_Bld_1, originalBranchName);
         assertEquals(BranchState.MODIFIED, BranchManager.getState(workingBranch));
         assertTrue(BranchManager.isEditable(workingBranch));

         Artifact change = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, workingBranch,
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
      OseeEventManager.addListener(branchEventListenerAsync);

      String originalBranchName = "Deleted State Branch";
      BranchToken workingBranch = null;
      boolean pending = OseeEventManager.getPreferences().isPendRunning();
      try {
         OseeEventManager.getPreferences().setPendRunning(true);

         workingBranch = BranchManager.createWorkingBranch(SAW_Bld_1, originalBranchName);
         assertEquals(BranchState.MODIFIED, BranchManager.getState(workingBranch));
         assertTrue(BranchManager.isEditable(workingBranch));

         branchEventListenerAsync.reset();
         Operations.executeWorkAndCheckStatus(new DeleteBranchOperation(workingBranch));

         verifyReceivedBranchStatesEvent(branchEventListenerAsync.getResults(BranchEventType.ArchiveStateUpdated),
            workingBranch, BranchEventType.ArchiveStateUpdated);

         assertTrue(BranchManager.isArchived(workingBranch));
         assertTrue(!BranchManager.isEditable(workingBranch));
         assertTrue(BranchManager.getState(workingBranch).isDeleted());
         assertEquals(BranchState.DELETED, BranchManager.getState(workingBranch));
      } finally {
         if (workingBranch != null) {
            // needed to allow for archiving to occur
            Thread.sleep(5000);
            BranchManager.purgeBranch(workingBranch);
         }
         OseeEventManager.getPreferences().setPendRunning(pending);
         OseeEventManager.removeListener(branchEventListenerAsync);
      }
   }

   @Test
   public void testPurgeState() throws InterruptedException {
      String originalBranchName = "Purged State Branch";
      BranchToken workingBranch = null;
      boolean branchPurged = false;
      try {
         workingBranch = BranchManager.createWorkingBranch(SAW_Bld_1, originalBranchName);
         assertEquals(BranchState.MODIFIED, BranchManager.getState(workingBranch));
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
      BranchToken workingBranch = null;
      Artifact change = null;
      try {
         workingBranch = BranchManager.createWorkingBranch(SAW_Bld_1, originalBranchName);
         assertEquals(BranchState.MODIFIED, BranchManager.getState(workingBranch));
         assertTrue(BranchManager.isEditable(workingBranch));

         change = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, workingBranch,
            "A commit change");
         change.persist(getClass().getSimpleName());

         Artifact workingBranchRoot = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(workingBranch);
         workingBranchRoot.addChild(change);
         workingBranchRoot.persist(getClass().getSimpleName());

         assertEquals(BranchState.MODIFIED, BranchManager.getState(workingBranch));
         assertTrue(BranchManager.isEditable(workingBranch));

         ConflictManagerExternal conflictManager = new ConflictManagerExternal(SAW_Bld_1, workingBranch);
         TransactionResult transactionResult = BranchManager.commitBranch(null, conflictManager, true, false);
         if (transactionResult.isFailed()) {
            throw new OseeCoreException(transactionResult.toString());
         }

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
      BranchToken workingBranch = null;
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

         Assert.assertEquals(BranchState.REBASELINED, BranchManager.getState(workingBranch));
         Assert.assertEquals(Artifact.SENTINEL, BranchManager.getAssociatedArtifact(workingBranch));

         BranchToken newWorkingBranch = operation.getNewBranch();
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
      BranchToken workingBranch = null;
      Artifact change = null;
      try {
         baseArtifact =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, SAW_Bld_1, "Test Object");
         baseArtifact.setSoleAttributeFromString(CoreAttributeTypes.Annotation, "This is the base annotation");
         baseArtifact.persist(getClass().getSimpleName());

         Artifact rootArtifact = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(SAW_Bld_1);
         rootArtifact.addChild(baseArtifact);
         rootArtifact.persist(getClass().getSimpleName());

         workingBranch = BranchManager.createWorkingBranch(SAW_Bld_1, originalBranchName);

         // Add a new artifact on the working branch
         change = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, workingBranch,
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

         Collection<BranchToken> branches = BranchManager.getBranchesByName(originalBranchName);
         assertEquals("Check only 1 original branch", 1, branches.size());

         BranchToken newWorkingBranch = operation.getNewBranch();
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
      BranchToken workingBranch = null;
      BranchToken mergeBranch = null;
      Artifact sameArtifact = null;
      try {
         baseArtifact =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, SAW_Bld_1, "Test Object");
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
         BranchToken destinationBranch = resolverOperation.getConflictManager().getDestinationBranch();
         assertTrue("Branch name not set correctly",
            destinationBranch.getName().startsWith(String.format("%s - for update -", originalBranchName)));
         assertTrue("Branch was not editable", BranchManager.isEditable(destinationBranch));

         // Check that we have a merge branch
         mergeBranch = BranchManager.getMergeBranch(workingBranch, destinationBranch);
         assertTrue("MergeBranch was not editable", BranchManager.isEditable(mergeBranch));
         assertEquals("Merge Branch should be in Modified State", BranchState.MODIFIED,
            BranchManager.getState(mergeBranch));

         // Run FinishBranchUpdate and check
         FinishUpdateBranchOperation finishUpdateOperation =
            new FinishUpdateBranchOperation(resolverOperation.getConflictManager(), true, true);
         Asserts.assertOperation(finishUpdateOperation, IStatus.OK);

         checkBranchWasRebaselined(originalBranchName, workingBranch);

         Collection<BranchToken> branches = BranchManager.getBranchesByName(originalBranchName);
         assertEquals("Check only 1 original branch", 1, branches.size());

         BranchToken newWorkingBranch = branches.iterator().next();
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
      for (BranchToken child : BranchManager.getChildBranches(branch, true)) {
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

   private boolean containsBranchEventType(List<Pair<Sender, BranchEvent>> eventPairs, BranchEventType eventType) {
      for (Pair<Sender, BranchEvent> eventPair : eventPairs) {
         if (eventPair.getSecond().getEventType().equals(eventType)) {
            return true;
         }
      }
      return false;
   }

   private void verifyReceivedBranchStatesEvent(List<Pair<Sender, BranchEvent>> eventPairs, BranchId expectedBranch, BranchEventType expectedEventType) {
      Sender receivedSender = null;
      BranchEvent receivedBranchEvent = null;

      Assert.assertTrue(containsBranchEventType(eventPairs, expectedEventType));

      for (Pair<Sender, BranchEvent> eventPair : eventPairs) {
         receivedSender = eventPair.getFirst();
         receivedBranchEvent = eventPair.getSecond();

         if (receivedBranchEvent.getEventType().equals(expectedEventType)) {
            if (isRemoteTest()) {
               Assert.assertTrue(receivedSender.isRemote());
            } else {
               Assert.assertTrue(receivedSender.isLocal());
            }
            if (expectedBranch != null) {
               Assert.assertEquals(expectedBranch, receivedBranchEvent.getSourceBranch());
            }
         }
      }
   }

   protected boolean isRemoteTest() {
      return false;
   }
}
