/*
 * Created on May 12, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.UnitTests;

import java.util.Collection;
import junit.framework.TestCase;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictTestManager;

/**
 * @author Theron Virgin
 */
public class BranchPersistenceManagerTest extends TestCase {

   /**
    * @param name
    */
   public BranchPersistenceManagerTest(String name) {
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
    * Test method for {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#getInstance()}.
    */
   public void testGetInstance() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#onManagerWebInit()}.
    */
   public void testOnManagerWebInit() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#getAssociatedArtifactBranches(org.eclipse.osee.framework.skynet.core.artifact.Artifact)}.
    */
   public void testGetAssociatedArtifactBranches() {
      fail("Not yet implemented");
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#getCommonBranch()}.
    */
   public void testGetCommonBranch() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#getKeyedBranch(java.lang.String)}.
    */
   public void testGetKeyedBranch() {
      fail("Not yet implemented");
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#getAtsBranch()}.
    */
   public void testGetAtsBranch() {
      fail("Not yet implemented");
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#getBranches()}.
    */
   public void testGetBranches() {
      fail("Not yet implemented");
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#refreshBranches()}.
    */
   public void testRefreshBranches() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#getBranch(java.lang.String)}.
    */
   public void testGetBranchString() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#getArchivedBranches()}.
    */
   public void testGetArchivedBranches() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#deleteArchivedBranches()}.
    */
   public void testDeleteArchivedBranches() {
      fail("Not yet implemented");
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

         assertTrue(mergeBranch == null);
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
         Collection<Artifact> artifacts = mergeBranch.getArtifacts(true);
         assertTrue(artifacts.toArray().length == ConflictTestManager.numberOfArtifactsOnMergeBranch());
      } catch (Exception ex) {
         fail(ex.getMessage());
      }
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#getBranch(java.lang.Integer)}.
    */
   public void testGetBranchInteger() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#getBranchForTransactionNumber(java.lang.Integer)}.
    */
   public void testGetBranchForTransactionNumber() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#deleteBranch(org.eclipse.osee.framework.skynet.core.artifact.Branch)}.
    */
   public void testDeleteBranch() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#removeBranchFromCache(int)}.
    */
   public void testRemoveBranchFromCache() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#commitBranch(org.eclipse.osee.framework.skynet.core.artifact.Branch, boolean)}.
    */
   public void testCommitBranchBranchBoolean() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#commitBranch(org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.artifact.Branch, boolean)}.
    */
   public void testCommitBranchBranchBranchBoolean() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#createWorkingBranchFromBranchChanges(org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.artifact.Artifact)}.
    */
   public void testCreateWorkingBranchFromBranchChangesBranchBranchArtifact() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#createWorkingBranchFromBranchChanges(org.eclipse.osee.framework.skynet.core.transaction.TransactionId, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.artifact.Artifact)}.
    */
   public void testCreateWorkingBranchFromBranchChangesTransactionIdBranchArtifact() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#hasConflicts(org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.artifact.Branch)}.
    */
   public void testHasConflictsBranchBranch() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#hasConflicts(org.eclipse.osee.framework.skynet.core.transaction.TransactionId, org.eclipse.osee.framework.skynet.core.artifact.Branch)}.
    */
   public void testHasConflictsTransactionIdBranch() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#addCommitTransactionToDatabase(org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.User)}.
    */
   public void testAddCommitTransactionToDatabaseBranchBranchUser() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#addCommitTransactionToDatabase(org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.transaction.TransactionId, org.eclipse.osee.framework.skynet.core.User)}.
    */
   public void testAddCommitTransactionToDatabaseBranchTransactionIdUser() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#getRelationRemoteEvent(org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.artifact.Branch, int, java.util.List)}.
    */
   public void testGetRelationRemoteEvent() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#getArtifactRemoteEvents(org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.User, java.util.List, int)}.
    */
   public void testGetArtifactRemoteEvents() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#archive(org.eclipse.osee.framework.skynet.core.artifact.Branch)}.
    */
   public void testArchive() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#deleteTransaction(int)}.
    */
   public void testDeleteTransaction() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#moveTransaction(org.eclipse.osee.framework.skynet.core.transaction.TransactionId, org.eclipse.osee.framework.skynet.core.artifact.Branch)}.
    */
   public void testMoveTransaction() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#runOnEventInDisplayThread()}.
    */
   public void testRunOnEventInDisplayThread() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#updateAssociatedArtifact(org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.artifact.Artifact)}.
    */
   public void testUpdateAssociatedArtifact() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#createWorkingBranch(org.eclipse.osee.framework.skynet.core.transaction.TransactionId, java.lang.String, java.lang.String, org.eclipse.osee.framework.skynet.core.artifact.Artifact)}.
    */
   public void testCreateWorkingBranch() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#createTestBranch(org.eclipse.osee.framework.skynet.core.transaction.TransactionId, java.lang.String, java.lang.String, org.eclipse.osee.framework.skynet.core.artifact.Artifact)}.
    */
   public void testCreateTestBranch() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#createBranchWithFiltering(org.eclipse.osee.framework.skynet.core.transaction.TransactionId, java.lang.String, java.lang.String, org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String[], java.lang.String[])}.
    */
   public void testCreateBranchWithFiltering() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#createRootBranch(java.lang.String, java.lang.String, java.lang.String, java.util.Collection, boolean)}.
    */
   public void testCreateRootBranch() {
      fail("Not yet implemented");
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#getRootBranches()}.
    */
   public void testGetRootBranches() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#getChangeManagedBranches()}.
    */
   public void testGetChangeManagedBranches() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#cache(org.eclipse.osee.framework.skynet.core.artifact.Branch)}.
    */
   public void testCache() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#setDefaultBranch(org.eclipse.osee.framework.skynet.core.artifact.Branch)}.
    */
   public void testSetDefaultBranch() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#getDefaultBranch()}.
    */
   public void testGetDefaultBranch() {
      fail("Not yet implemented");
   }

}
