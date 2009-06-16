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
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.update.ConflictResolverOperation;
import org.junit.Before;

/**
 * @author Roberto E. Escobar
 */
public class UpdateBranchTest {

   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse(ClientSessionManager.isProductionDataStore());
   }

   @org.junit.Test
   public void testUpdateWithoutConflicts() throws OseeCoreException, InterruptedException {
      Branch mainBranch = BranchManager.getKeyedBranch("SAW_Bld_1");
      String originalBranchName = "UpdateBranch Test 1";
      Artifact baseArtifact = null;
      Branch workingBranch = null;
      try {
         baseArtifact = ArtifactTypeManager.addArtifact("Software Requirement", mainBranch, "Test Object");
         baseArtifact.setSoleAttributeFromString("Annotation", "This is the base annotation");
         baseArtifact.persistAttributes();

         User user = UserManager.getUser(SystemUser.OseeSystem);
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
               new ConflictResolverOperation("Test 1 Resolver", UpdateBranchTest.class.getCanonicalName()) {

                  @Override
                  protected void doWork(IProgressMonitor monitor) throws Exception {
                     assertFalse("This code should not be executed since there shouldn't be any conflicts.",
                           wasExecuted());
                  }
               };

         Job job = BranchManager.updateBranch(workingBranch, resolverOperation);
         job.join();
         assertTrue("Resolver was executed", !resolverOperation.wasExecuted());
         assertTrue("UpdateBranch was not successful", job.getResult().isOK());

         checkBranchWasRebaselined(originalBranchName, workingBranch);

         Collection<Branch> branches = BranchManager.getBranchesByName(originalBranchName);
         assertEquals("Check only 1 original branch", 1, branches.size());

         Branch newWorkingBranch = branches.iterator().next();
         assertTrue(workingBranch.getBranchId() != newWorkingBranch.getBranchId());
         assertEquals(originalBranchName, newWorkingBranch.getBranchName());
         assertTrue("New Working branch is editable", newWorkingBranch.isEditable());
      } finally {
         if (workingBranch != null) {
            BranchManager.purgeBranch(workingBranch);
         }
         for (Branch branch : BranchManager.getBranchesByName(originalBranchName)) {
            BranchManager.purgeBranch(branch);
         }
         if (baseArtifact != null) {
            List<Artifact> itemsToPurge = new ArrayList<Artifact>();
            itemsToPurge.add(baseArtifact);
            ArtifactPersistenceManager.purgeArtifacts(itemsToPurge);
         }
      }
   }

   @org.junit.Test
   public void testUpdateWithConflicts() throws OseeCoreException, InterruptedException {
      Branch mainBranch = BranchManager.getKeyedBranch("SAW_Bld_1");
      String originalBranchName = "UpdateBranch Test 2";
      Artifact baseArtifact = null;
      Branch workingBranch = null;
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
               new ConflictResolverOperation("Test 2 Resolver", UpdateBranchTest.class.getCanonicalName()) {

                  @Override
                  protected void doWork(IProgressMonitor monitor) throws Exception {
                     assertTrue("This code should have been executed since there shouldn't be any conflicts.",
                           wasExecuted());
                  }
               };

         // Update the branch
         Job job = BranchManager.updateBranch(workingBranch, resolverOperation);
         job.join();
         assertTrue("Resolver not executed", resolverOperation.wasExecuted());
         assertTrue("UpdateBranch was not successful", job.getResult().isOK());

         checkBranchWasRebaselinedForConflicts(originalBranchName, workingBranch);

         // Resolve Conflicts

         // Run FinishBranchUpdate and check

         Collection<Branch> branches = BranchManager.getBranchesByName(originalBranchName);
         assertEquals("Check only 1 original branch", 1, branches.size());

         Branch newWorkingBranch = branches.iterator().next();
         assertTrue(workingBranch.getBranchId() != newWorkingBranch.getBranchId());
         assertEquals(originalBranchName, newWorkingBranch.getBranchName());
         assertTrue("New Working branch is editable", newWorkingBranch.isEditable());
      } finally {
         if (workingBranch != null) {
            BranchManager.purgeBranch(workingBranch);
         }
         for (Branch branch : BranchManager.getBranchesByName(originalBranchName)) {
            BranchManager.purgeBranch(branch);
         }
         if (baseArtifact != null) {
            List<Artifact> itemsToPurge = new ArrayList<Artifact>();
            itemsToPurge.add(baseArtifact);
            ArtifactPersistenceManager.purgeArtifacts(itemsToPurge);
         }
      }
   }

   private void checkBranchWasRebaselinedForConflicts(String originalBranchName, Branch branchToCheck) {
      assertTrue("Branch was archived", !branchToCheck.isArchived());
      assertTrue("Branch was not editable", branchToCheck.isEditable());
      assertTrue("Branch state was not set as rebaselined", branchToCheck.isRebaselined());
      assertTrue("Branch name not set correctly", branchToCheck.getBranchName().startsWith(
            String.format("%s - for update -", originalBranchName)));
   }

   private void checkBranchWasRebaselined(String originalBranchName, Branch branchToCheck) {
      assertTrue("Branch was not archived", branchToCheck.isArchived());
      assertTrue("Branch was still editable", !branchToCheck.isEditable());
      assertTrue("Branch state was not set as rebaselined", branchToCheck.isRebaselined());
      assertTrue("Branch name not set correctly", branchToCheck.getBranchName().startsWith(
            String.format("%s - moved by update on -", originalBranchName)));
   }
}
