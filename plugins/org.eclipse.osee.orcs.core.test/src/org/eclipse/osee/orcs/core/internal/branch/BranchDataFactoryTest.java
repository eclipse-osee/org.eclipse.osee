/*
 * Created on Aug 30, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal.branch;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BranchDataFactoryTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private BranchCache branchCache;
   @Mock private TransactionCache txCache;
   // @formatter:on

   private BranchDataFactory factory;

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);

      factory = new BranchDataFactory(branchCache, txCache);
   }

   @Test
   public void testDataForTopLevelBranch() throws OseeCoreException {
      IOseeBranch branch = mock(IOseeBranch.class);
      String branchName = "testDataForTopLevelBranch";
      when(branch.getName()).thenReturn(branchName);
      ArtifactReadable author = mock(ArtifactReadable.class);
      TransactionRecord txRecord = mock(TransactionRecord.class);
      when(txCache.getHeadTransaction(any(Branch.class))).thenReturn(txRecord);
      CreateBranchData result = factory.dataForTopLevelBranch(branch, author);
      Assert.assertEquals("Branch Creation for " + branchName, result.getCreationComment());
      Assert.assertEquals(author, result.getUserArtifact());
      Assert.assertEquals(txRecord, result.getFromTransaction());
      Assert.assertEquals(BranchType.BASELINE, result.getBranchType());
      Assert.assertEquals(false, result.isTxCopyBranchType());
   }

   @Test
   public void testDataForBaselineBranch() throws OseeCoreException {
      IOseeBranch branch = mock(IOseeBranch.class);
      IOseeBranch parent = mock(IOseeBranch.class);
      String branchName = "testDataForBaselineBranch";
      when(branch.getName()).thenReturn(branchName);
      ArtifactReadable author = mock(ArtifactReadable.class);
      ArtifactReadable associatedArtifact = mock(ArtifactReadable.class);
      TransactionRecord txRecord = mock(TransactionRecord.class);
      when(txCache.getHeadTransaction(any(Branch.class))).thenReturn(txRecord);
      CreateBranchData result = factory.dataForBaselineBranch(branch, author, parent, associatedArtifact);
      Assert.assertEquals("Branch Creation for " + branchName, result.getCreationComment());
      Assert.assertEquals(author, result.getUserArtifact());
      Assert.assertEquals(txRecord, result.getFromTransaction());
      Assert.assertEquals(BranchType.BASELINE, result.getBranchType());
      Assert.assertEquals(false, result.isTxCopyBranchType());
   }

   @Test
   public void testDataForWorkingBranch() throws OseeCoreException {
      IOseeBranch branch = mock(IOseeBranch.class);
      Branch parent = mock(Branch.class);
      String branchName = "testDataForWorkingBranch";
      String parentName = "testParentBranchName";
      when(branch.getName()).thenReturn(branchName);
      when(parent.getName()).thenReturn(parentName);
      ArtifactReadable author = mock(ArtifactReadable.class);
      ArtifactReadable associatedArtifact = mock(ArtifactReadable.class);
      TransactionRecord txRecord = mock(TransactionRecord.class);
      when(txCache.getHeadTransaction(any(Branch.class))).thenReturn(txRecord);
      when(branchCache.getByGuid(any(String.class))).thenReturn(parent);
      CreateBranchData result = factory.dataForWorkingBranch(branch, author, parent, associatedArtifact);
      String creationComment = String.format("New Branch from %s (%s)", parentName, txRecord.getId());
      Assert.assertEquals(creationComment, result.getCreationComment());
      Assert.assertEquals(author, result.getUserArtifact());
      Assert.assertEquals(txRecord, result.getFromTransaction());
      Assert.assertEquals(associatedArtifact, result.getAssociatedArtifact());
      Assert.assertEquals(BranchType.WORKING, result.getBranchType());
      Assert.assertEquals(false, result.isTxCopyBranchType());
   }

   @Test
   public void testDataForCopyTxBranch() throws OseeCoreException {
      IOseeBranch branch = mock(IOseeBranch.class);
      Branch parent = mock(Branch.class);
      String branchName = "testDataForCopyTxBranch";
      String parentName = "testParentBranchName";
      when(branch.getName()).thenReturn(branchName);
      when(parent.getName()).thenReturn(parentName);
      ArtifactReadable author = mock(ArtifactReadable.class);
      TransactionRecord txRecord = mock(TransactionRecord.class);
      when(txRecord.getBranch()).thenReturn(parent);
      when(txCache.getOrLoad(99)).thenReturn(txRecord);
      CreateBranchData result = factory.dataForCopyTxBranch(branch, author, 99, null);
      String creationComment =
         String.format("Transaction %d copied from %s to create Branch %s", 99, parentName, branchName);
      Assert.assertEquals(creationComment, result.getCreationComment());
      Assert.assertEquals(author, result.getUserArtifact());
      Assert.assertEquals(BranchType.WORKING, result.getBranchType());
      Assert.assertEquals(txRecord, result.getFromTransaction());
      Assert.assertEquals(true, result.isTxCopyBranchType());
   }

}
