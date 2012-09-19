/*
 * Created on Aug 30, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal.branch;

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.CreateBranchData;

public class BranchDataFactory {

   private final BranchCache branchCache;
   private final TransactionCache txCache;
   private final int MERGE_DESTINATION_BRANCH_ID = -1; // only used on merge branches
   private final int MERGE_ADDRESSING_QUERY_ID = -1; // only used on merge branches

   public BranchDataFactory(BranchCache branchCache, TransactionCache txCache) {
      super();
      this.branchCache = branchCache;
      this.txCache = txCache;
   }

   public CreateBranchData dataForTopLevelBranch(IOseeBranch branch, ArtifactReadable author) throws OseeCoreException {
      String branchName = branch.getName();
      String creationComment = String.format("Branch Creation for %s", branchName);
      TransactionRecord headTransaction = txCache.getHeadTransaction(branchCache.getSystemRootBranch());
      CreateBranchData createData =
         setupCreateBranchData(branchName, BranchType.BASELINE, creationComment, headTransaction, author, null, false);

      return createData;
   }

   public CreateBranchData dataForBaselineBranch(IOseeBranch branch, ArtifactReadable author, IOseeBranch parent, ArtifactReadable associatedArtifact) throws OseeCoreException {
      String branchName = branch.getName();
      String creationComment = String.format("Branch Creation for %s", branchName);
      TransactionRecord headTransaction = txCache.getHeadTransaction(branchCache.getByGuid(parent.getGuid()));

      CreateBranchData createData =
         setupCreateBranchData(branchName, BranchType.BASELINE, creationComment, headTransaction, author,
            associatedArtifact, false);

      return createData;
   }

   public CreateBranchData dataForWorkingBranch(IOseeBranch branch, ArtifactReadable author, IOseeBranch parent, ArtifactReadable associatedArtifact) throws OseeCoreException {
      String branchName = branch.getName();
      TransactionRecord headTransaction = txCache.getHeadTransaction(branchCache.getByGuid(parent.getGuid()));
      String creationComment = String.format("New Branch from %s (%s)", parent.getName(), headTransaction.getId());

      CreateBranchData createData =
         setupCreateBranchData(branchName, BranchType.WORKING, creationComment, headTransaction, author,
            associatedArtifact, false);

      return createData;
   }

   public CreateBranchData dataForCopyTxBranch(IOseeBranch branch, ArtifactReadable author, int fromTransaction, ArtifactReadable associatedArtifact) throws OseeCoreException {
      String branchName = branch.getName();
      TransactionRecord transaction = txCache.getOrLoad(fromTransaction);
      IOseeBranch parent = transaction.getBranch();
      String creationComment =
         String.format("Transaction %d copied from %s to create Branch %s", fromTransaction, parent.getName(),
            branchName);
      CreateBranchData createData =
         setupCreateBranchData(branchName, BranchType.WORKING, creationComment, transaction, author,
            associatedArtifact, true);

      return createData;
   }

   private CreateBranchData setupCreateBranchData(String name, BranchType branchType, String creationComment, ITransaction fromTransaction, ArtifactReadable author, ArtifactReadable associatedArtifact, boolean bCopyTx) {
      CreateBranchData createData = new CreateBranchData();
      createData.setGuid(GUID.create());
      createData.setName(name);
      createData.setBranchType(branchType);
      createData.setCreationComment(creationComment);
      createData.setFromTransaction(fromTransaction);
      createData.setUserArtifact(author);
      createData.setAssociatedArtifact(associatedArtifact);
      createData.setMergeDestinationBranchId(MERGE_DESTINATION_BRANCH_ID);
      createData.setMergeAddressingQueryId(MERGE_ADDRESSING_QUERY_ID);
      createData.setTxCopyBranchType(bCopyTx);
      return createData;
   }
}
