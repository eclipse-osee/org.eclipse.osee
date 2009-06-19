/*
 * Created on Jun 15, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.test.cases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.operation.FinishUpdateBranchOperation;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.update.ConflictResolverOperation;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.junit.Before;

/**
 * @author Roberto E. Escobar
 */
public class BranchStateTest {

   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse(ClientSessionManager.isProductionDataStore());
   }

   @org.junit.Test
   public void testCreateState() throws OseeCoreException {
      Branch mainBranch = BranchManager.getKeyedBranch("SAW_Bld_1");
      String originalBranchName = "Create State Branch";
      Branch workingBranch = null;
      try {
         User user = UserManager.getUser(SystemUser.OseeSystem);
         workingBranch = BranchManager.createWorkingBranch(mainBranch, originalBranchName, user);
         assertEquals(BranchState.CREATED, workingBranch.getBranchState());
         assertTrue(workingBranch.isEditable());
      } finally {
         if (workingBranch != null) {
            BranchManager.purgeBranch(workingBranch);
         }
      }
   }

   @org.junit.Test
   public void testModifiedState() throws OseeCoreException, InterruptedException {
      Branch mainBranch = BranchManager.getKeyedBranch("SAW_Bld_1");
      String originalBranchName = "Modified State Branch";
      Branch workingBranch = null;
      try {
         User user = UserManager.getUser(SystemUser.OseeSystem);
         workingBranch = BranchManager.createWorkingBranch(mainBranch, originalBranchName, user);
         assertEquals(BranchState.CREATED, workingBranch.getBranchState());
         assertTrue(workingBranch.isEditable());

         Artifact change =
               ArtifactTypeManager.addArtifact("Software Requirement", workingBranch, "Test Object on Working Branch");
         change.persistAttributes();

         assertEquals(BranchState.MODIFIED, workingBranch.getBranchState());
         assertTrue(workingBranch.isEditable());
      } finally {
         if (workingBranch != null) {
            BranchManager.purgeBranch(workingBranch);
         }
      }
   }

   @org.junit.Test
   public void testDeleteState() throws OseeCoreException, InterruptedException {
      Branch mainBranch = BranchManager.getKeyedBranch("SAW_Bld_1");
      String originalBranchName = "Deleted State Branch";
      Branch workingBranch = null;
      try {
         User user = UserManager.getUser(SystemUser.OseeSystem);
         workingBranch = BranchManager.createWorkingBranch(mainBranch, originalBranchName, user);
         assertEquals(BranchState.CREATED, workingBranch.getBranchState());
         assertTrue(workingBranch.isEditable());

         Job job = BranchManager.deleteBranch(workingBranch);
         job.join();
         assertEquals(BranchState.DELETED, workingBranch.getBranchState());
         assertTrue(workingBranch.isArchived());
         assertTrue(!workingBranch.isEditable());
         assertTrue(workingBranch.isDeleted());
      } finally {
         if (workingBranch != null) {
            BranchManager.purgeBranch(workingBranch);
         }
      }
   }

   @org.junit.Test
   public void testCommittState() throws OseeCoreException, InterruptedException {
      Branch mainBranch = BranchManager.getKeyedBranch("SAW_Bld_1");
      String originalBranchName = "Commit State Branch";
      Branch workingBranch = null;
      try {
         User user = UserManager.getUser(SystemUser.OseeSystem);
         workingBranch = BranchManager.createWorkingBranch(mainBranch, originalBranchName, user);
         assertEquals(BranchState.CREATED, workingBranch.getBranchState());
         assertTrue(workingBranch.isEditable());

         Artifact change = ArtifactTypeManager.addArtifact("Software Requirement", workingBranch, "A commit change");
         change.persistAttributes();

         assertEquals(BranchState.MODIFIED, workingBranch.getBranchState());
         assertTrue(workingBranch.isEditable());

         ConflictManagerExternal conflictManager = new ConflictManagerExternal(mainBranch, workingBranch);
         BranchManager.commitBranch(conflictManager, true, false);

         assertEquals(BranchState.COMMITTED, workingBranch.getBranchState());
         assertTrue(workingBranch.isArchived());
         assertTrue(!workingBranch.isEditable());
      } finally {
         if (workingBranch != null) {
            BranchManager.purgeBranch(workingBranch);
         }
      }
   }

   @org.junit.Test
   public void testRebaselineWithoutConflicts() throws OseeCoreException, InterruptedException {
      Branch mainBranch = BranchManager.getKeyedBranch("SAW_Bld_1");
      String originalBranchName = "UpdateBranch Test 1";
      Artifact baseArtifact = null;
      Branch workingBranch = null;
      try {
         baseArtifact = ArtifactTypeManager.addArtifact("Software Requirement", mainBranch, "Test Object");
         baseArtifact.setSoleAttributeFromString("Annotation", "This is the base annotation");
         baseArtifact.persistAttributes();

         User user = UserManager.getUser(SystemUser.Guest);
         workingBranch = BranchManager.createWorkingBranch(mainBranch, originalBranchName, user);

         // Add a new artifact on the working branch
         Artifact change =
               ArtifactTypeManager.addArtifact("Software Requirement", workingBranch, "Test Object on Working Branch");
         change.persistAttributes();

         // Make a change on the parent
         baseArtifact.setSoleAttributeFromString("Annotation", "This is the updated annotation");
         baseArtifact.persistAttributes();

         // Update the branch
         ConflictResolverOperation resolverOperation =
               new ConflictResolverOperation("Test 1 Resolver", BranchStateTest.class.getCanonicalName()) {

                  @Override
                  protected void doWork(IProgressMonitor monitor) throws Exception {
                     assertFalse("This code should not be executed since there shouldn't be any conflicts.",
                           wasExecuted());
                  }
               };

         Job job = BranchManager.updateBranch(workingBranch, resolverOperation);
         job.join();
         assertTrue("UpdateBranch was not successful", job.getResult().isOK());
         assertTrue("Resolver was executed", !resolverOperation.wasExecuted());

         checkBranchWasRebaselined(originalBranchName, workingBranch);
         // Check that the associated artifact remained unchanged
         assertEquals(workingBranch.getAssociatedArtifact(), user);

         Collection<Branch> branches = BranchManager.getBranchesByName(originalBranchName);
         assertEquals("Check only 1 original branch", 1, branches.size());

         Branch newWorkingBranch = branches.iterator().next();
         assertTrue(workingBranch.getBranchId() != newWorkingBranch.getBranchId());
         assertEquals(originalBranchName, newWorkingBranch.getBranchName());
         assertTrue("New Working branch is editable", newWorkingBranch.isEditable());
      } finally {
         cleanup(originalBranchName, baseArtifact, workingBranch, null);
      }
   }

   @org.junit.Test
   public void testRebaselineWithConflicts() throws OseeCoreException, InterruptedException {
      Branch mainBranch = BranchManager.getKeyedBranch("SAW_Bld_1");
      String originalBranchName = "UpdateBranch Test 2";
      Artifact baseArtifact = null;
      Branch workingBranch = null;
      Branch mergeBranch = null;
      try {
         baseArtifact = ArtifactTypeManager.addArtifact("Software Requirement", mainBranch, "Test Object");
         baseArtifact.setSoleAttributeFromString("Annotation", "This is the base annotation");
         baseArtifact.persistAttributes();

         User user = UserManager.getUser(SystemUser.OseeSystem);
         workingBranch = BranchManager.createWorkingBranch(mainBranch, originalBranchName, user);

         // Modify same artifact on working branch
         Artifact sameArtifact = ArtifactQuery.getArtifactFromId(baseArtifact.getHumanReadableId(), workingBranch);
         sameArtifact.setSoleAttributeFromString("Annotation", "This is the working branch update annotation");
         sameArtifact.persistAttributes();

         // Make a change on the parent
         baseArtifact.setSoleAttributeFromString("Annotation", "This is the updated annotation");
         baseArtifact.persistAttributes();

         ConflictResolverOperation resolverOperation =
               new ConflictResolverOperation("Test 2 Resolver", BranchStateTest.class.getCanonicalName()) {

                  @Override
                  protected void doWork(IProgressMonitor monitor) throws Exception {
                     assertTrue("This code should have been executed since there shouldn't be any conflicts.",
                           wasExecuted());
                  }
               };

         // Update the branch
         Job job = BranchManager.updateBranch(workingBranch, resolverOperation);
         job.join();

         assertTrue("UpdateBranch was not successful\n" + job.getResult().getMessage(), job.getResult().isOK());
         assertTrue("Resolver not executed", resolverOperation.wasExecuted());

         assertTrue("Branch was archived", !workingBranch.isArchived());
         assertTrue("Branch was not marked as rebaseline in progress", workingBranch.isRebaselineInProgress());
         assertTrue("Branch was not editable", workingBranch.isEditable());
         assertTrue("Branch state was set to rebaselined before complete", !workingBranch.isRebaselined());

         assertEquals("Branch name was changed before update was complete", originalBranchName,
               workingBranch.getBranchName());

         // Check that a new destination branch exists
         Branch destinationBranch = resolverOperation.getConflictManager().getDestinationBranch();
         assertTrue("Branch name not set correctly", destinationBranch.getBranchName().startsWith(
               String.format("%s - for update -", originalBranchName)));
         assertTrue("Branch was not editable", destinationBranch.isEditable());

         // Check that we have a merge branch
         mergeBranch = BranchManager.getMergeBranch(workingBranch, destinationBranch);
         assertTrue("MergeBranch was not editable", mergeBranch.isEditable());
         assertEquals("Merge Branch should be in Created State", BranchState.CREATED, mergeBranch.getBranchState());

         // Run FinishBranchUpdate and check
         FinishUpdateBranchOperation finishUpdateOperation =
               new FinishUpdateBranchOperation("Update Branch Test 2", resolverOperation.getConflictManager(), true,
                     true);
         Operations.executeWork(finishUpdateOperation, new NullProgressMonitor(), -1);
         assertTrue("FinishUpdateBranch was not successful", finishUpdateOperation.getStatus().isOK());

         checkBranchWasRebaselined(originalBranchName, workingBranch);

         Collection<Branch> branches = BranchManager.getBranchesByName(originalBranchName);
         assertEquals("Check only 1 original branch", 1, branches.size());

         Branch newWorkingBranch = branches.iterator().next();
         assertTrue(workingBranch.getBranchId() != newWorkingBranch.getBranchId());
         assertEquals(originalBranchName, newWorkingBranch.getBranchName());
         assertTrue("New Working branch is editable", newWorkingBranch.isEditable());

         // Swapped successfully
         assertEquals(destinationBranch.getBranchId(), newWorkingBranch.getBranchId());
      } finally {
         cleanup(originalBranchName, baseArtifact, workingBranch, mergeBranch);
      }
   }

   private void cleanup(String originalBranchName, Artifact baseArtifact, Branch workingBranch, Branch mergeBranch) throws OseeDataStoreException, OseeCoreException {
      for (Branch branch : BranchManager.getBranchesByName(originalBranchName)) {
         for (Branch child : branch.getChildBranches(true)) {
            BranchManager.purgeBranch(child);
         }
         BranchManager.purgeBranch(branch);
      }
      if (mergeBranch != null) {
         BranchManager.purgeBranch(mergeBranch);
      }
      if (workingBranch != null) {
         BranchManager.purgeBranch(workingBranch);
      }
      if (baseArtifact != null) {
         List<Artifact> itemsToPurge = new ArrayList<Artifact>();
         itemsToPurge.add(baseArtifact);
         ArtifactPersistenceManager.purgeArtifacts(itemsToPurge);
      }
   }

   private void checkBranchWasRebaselined(String originalBranchName, Branch branchToCheck) {
      assertTrue("Branch was not archived", branchToCheck.isArchived());
      assertTrue("Branch was still editable", !branchToCheck.isEditable());
      assertTrue("Branch state was not set as rebaselined", branchToCheck.isRebaselined());
      assertTrue("Branch name not set correctly", branchToCheck.getBranchName().startsWith(
            String.format("%s - moved by update on -", originalBranchName)));
   }

}
