/*
 * Created on May 12, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.test;

import java.util.Collection;
import junit.framework.TestCase;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Theron Virgin
 */
public class MergeBranchManagementTest extends TestCase {

   /**
    * @param name
    */
   public MergeBranchManagementTest(String name) {
      super(name);
   }

   /* (non-Javadoc)
    * @see junit.framework.TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
   }

   /* (non-Javadoc)
    * @see junit.framework.TestCase#tearDown()
    */
   protected void tearDown() throws Exception {
      super.tearDown();
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#getOrCreateMergeBranch(org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.artifact.Branch, java.util.ArrayList)}.
    */
   public void testGetOrCreateMergeBranch() {
      //      try {
      //         BranchPersistenceManager branchPersistenceManager = BranchPersistenceManager.getInstance();
      //         Branch mergeBranch =
      //               branchPersistenceManager.getOrCreateMergeBranch(ConflictTestManager.getSourceBranch(),
      //                     ConflictTestManager.getDestBranch());
      //
      //         assertTrue(mergeBranch == null);
      //      } catch (Exception ex) {
      //         fail(ex.getMessage());
      //      }
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#getMergeBranch(java.lang.Integer, java.lang.Integer)}.
    */
   public void testGetMergeBranchNotCreated() {

      try {
         BranchPersistenceManager branchPersistenceManager = BranchPersistenceManager.getInstance();
         Branch mergeBranch =
               branchPersistenceManager.getMergeBranch(ConflictTestManager.getSourceBranch().getBranchId(),
                     ConflictTestManager.getDestBranch().getBranchId());

         assertTrue("The merge branch should be null as it hasn't been created yet", mergeBranch == null);
      } catch (Exception ex) {
         fail(ex.getMessage());
      }
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#getMergeBranch(java.lang.Integer, java.lang.Integer)}.
    */
   public void testGetMergeBranchCreated() {
      try {
         BranchPersistenceManager branchPersistenceManager = BranchPersistenceManager.getInstance();
         Branch mergeBranch =
               branchPersistenceManager.getMergeBranch(ConflictTestManager.getSourceBranch().getBranchId(),
                     ConflictTestManager.getDestBranch().getBranchId());
         assertFalse(mergeBranch == null);
         Collection<Artifact> artifacts = ArtifactQuery.getArtifactsFromBranch(mergeBranch, true);
         assertEquals("The merge Branch does not contain the expected number of artifacts",
               ConflictTestManager.numberOfArtifactsOnMergeBranch(), artifacts.toArray().length);
      } catch (Exception ex) {
         fail(ex.getMessage());
      }
   }

}
