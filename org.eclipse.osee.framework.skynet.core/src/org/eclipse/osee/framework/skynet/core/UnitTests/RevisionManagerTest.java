/*
 * Created on May 7, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.UnitTests;

import java.util.Collection;
import java.util.HashSet;
import junit.framework.TestCase;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictTestManager;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Theron Virgin
 */
public class RevisionManagerTest extends TestCase {

   /**
    * @param name
    */
   public RevisionManagerTest(String name) {
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
    * Test method for {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#getInstance()}.
    */
   public void testGetInstance() {
      RevisionManager revisionManager = RevisionManager.getInstance();
      assertTrue(revisionManager != null);
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#onManagerWebInit()}.
    */
   public void testOnManagerWebInit() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#getTransactionsPerBranch(org.eclipse.osee.framework.skynet.core.artifact.Branch)}.
    */
   public void testGetTransactionsPerBranch() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#getTransactionDataPerCommitArtifact(org.eclipse.osee.framework.skynet.core.artifact.Artifact)}.
    */
   public void testGetTransactionDataPerCommitArtifact() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#cacheTransactionDataPerCommitArtifact(org.eclipse.osee.framework.skynet.core.artifact.Artifact, int)}.
    */
   public void testCacheTransactionDataPerCommitArtifactArtifactInt() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#cacheTransactionDataPerCommitArtifact(int, int)}.
    */
   public void testCacheTransactionDataPerCommitArtifactIntInt() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#getTransactionsPerArtifact(org.eclipse.osee.framework.skynet.core.artifact.Artifact)}.
    */
   public void testGetTransactionsPerArtifactArtifact() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#getTransactionsPerArtifact(org.eclipse.osee.framework.skynet.core.artifact.Artifact, boolean)}.
    */
   public void testGetTransactionsPerArtifactArtifactBoolean() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#getTransactionChanges(org.eclipse.osee.framework.skynet.core.revision.TransactionData)}.
    */
   public void testGetTransactionChangesTransactionData() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#getTransactionChanges(org.eclipse.osee.framework.skynet.core.revision.ArtifactChange, org.eclipse.osee.framework.skynet.core.revision.IArtifactNameDescriptorResolver)}.
    */
   public void testGetTransactionChangesArtifactChangeIArtifactNameDescriptorResolver() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#getAllTransactionChanges(org.eclipse.osee.framework.skynet.core.change.ChangeType, int, int, int, org.eclipse.osee.framework.skynet.core.revision.IArtifactNameDescriptorResolver)}.
    */
   public void testGetAllTransactionChanges() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#getTransactionChanges(org.eclipse.osee.framework.skynet.core.change.ChangeType, org.eclipse.osee.framework.skynet.core.transaction.TransactionId, org.eclipse.osee.framework.skynet.core.transaction.TransactionId, int, org.eclipse.osee.framework.skynet.core.revision.IArtifactNameDescriptorResolver)}.
    */
   public void testGetTransactionChangesChangeTypeTransactionIdTransactionIdIntIArtifactNameDescriptorResolver() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#getChangesPerTransaction(int)}.
    */
   public void testGetChangesPerTransaction() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#getChangesPerBranch(org.eclipse.osee.framework.skynet.core.artifact.Branch)}.
    */
   public void testGetChangesPerBranch() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#getConflictsPerBranch(org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.transaction.TransactionId)}.
    */
   public void testGetConflictsPerBranch() {
      RevisionManager revisionManager = RevisionManager.getInstance();
      Collection<Conflict> conflicts = new HashSet<Conflict>();
      try {
         conflicts =
               revisionManager.getConflictsPerBranch(ConflictTestManager.getSourceBranch(),
                     ConflictTestManager.getDestBranch(), TransactionIdManager.getInstance().getStartEndPoint(
                           ConflictTestManager.getSourceBranch()).getKey());
      } catch (Exception ex) {
         fail(ex.getMessage());
      }
      assertEquals("Number of conflicts found is not equal to the number of conflicts expected",
            ConflictTestManager.numberOfConflicts(), conflicts.toArray().length);
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#getName(int)}.
    */
   public void testGetName() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#getDeletedArtifactChanges(org.eclipse.osee.framework.skynet.core.transaction.TransactionId)}.
    */
   public void testGetDeletedArtifactChangesTransactionId() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#getDeletedArtifactChanges(org.eclipse.osee.framework.skynet.core.transaction.TransactionId, org.eclipse.osee.framework.skynet.core.transaction.TransactionId, org.eclipse.osee.framework.skynet.core.transaction.TransactionId, org.eclipse.osee.framework.skynet.core.transaction.TransactionId, org.eclipse.osee.framework.skynet.core.revision.ArtifactNameDescriptorCache)}.
    */
   public void testGetDeletedArtifactChangesTransactionIdTransactionIdTransactionIdTransactionIdArtifactNameDescriptorCache() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#getNewAndModifiedArtifacts(org.eclipse.osee.framework.skynet.core.artifact.Branch, boolean)}.
    */
   public void testGetNewAndModifiedArtifactsBranchBoolean() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#getNewAndModifiedArtifacts(org.eclipse.osee.framework.skynet.core.transaction.TransactionId, org.eclipse.osee.framework.skynet.core.transaction.TransactionId, boolean)}.
    */
   public void testGetNewAndModifiedArtifactsTransactionIdTransactionIdBoolean() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#getRelationChangedArtifacts(org.eclipse.osee.framework.skynet.core.transaction.TransactionId, org.eclipse.osee.framework.skynet.core.transaction.TransactionId)}.
    */
   public void testGetRelationChangedArtifacts() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#getNewAndModArtifactChanges(org.eclipse.osee.framework.skynet.core.transaction.TransactionId, org.eclipse.osee.framework.skynet.core.transaction.TransactionId, org.eclipse.osee.framework.skynet.core.transaction.TransactionId, org.eclipse.osee.framework.skynet.core.transaction.TransactionId, org.eclipse.osee.framework.skynet.core.revision.ArtifactNameDescriptorCache)}.
    */
   public void testGetNewAndModArtifactChanges() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#branchHasChanges(org.eclipse.osee.framework.skynet.core.artifact.Branch)}.
    */
   public void testBranchHasChanges() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#getOtherEdittedBranches(org.eclipse.osee.framework.skynet.core.artifact.Artifact)}.
    */
   public void testGetOtherEdittedBranches() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.RevisionManager#branchHasConflicts(org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.artifact.Branch)}.
    */
   public void testBranchHasConflicts() {
      RevisionManager revisionManager = RevisionManager.getInstance();
      try {
         assertTrue(revisionManager.branchHasConflicts(ConflictTestManager.getSourceBranch(),
               ConflictTestManager.getDestBranch()));
      } catch (Exception ex) {
         fail(ex.getMessage());
      }
   }

}
