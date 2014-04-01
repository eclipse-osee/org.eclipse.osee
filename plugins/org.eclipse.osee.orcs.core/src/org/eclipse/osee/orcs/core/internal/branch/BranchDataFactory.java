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
package org.eclipse.osee.orcs.core.internal.branch;

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.CreateBranchData;

/**
 * @author David W. Miller
 */
public class BranchDataFactory {

   private final BranchCache branchCache;
   private final TransactionCache txCache;

   public BranchDataFactory(BranchCache branchCache, TransactionCache txCache) {
      super();
      this.branchCache = branchCache;
      this.txCache = txCache;
   }

   public CreateBranchData createTopLevelBranchData(IOseeBranch branch, ArtifactReadable author) throws OseeCoreException {
      Branch parentBranch = branchCache.getSystemRootBranch();
      TransactionRecord fromTx = txCache.getHeadTransaction(parentBranch);

      String creationComment = String.format("Branch Creation for %s", branch.getName());
      return createBranchData(branch, BranchType.BASELINE, creationComment, fromTx, author, null, false);
   }

   public CreateBranchData createBaselineBranchData(IOseeBranch branch, ArtifactReadable author, IOseeBranch parent, ArtifactReadable associatedArtifact) throws OseeCoreException {
      Branch parentBranch = branchCache.get(parent);
      TransactionRecord fromTx = txCache.getHeadTransaction(parentBranch);

      String creationComment = String.format("Branch Creation for %s", branch.getName());
      return createBranchData(branch, BranchType.BASELINE, creationComment, fromTx, author, associatedArtifact, false);
   }

   public CreateBranchData createWorkingBranchData(IOseeBranch branch, ArtifactReadable author, IOseeBranch parent, ArtifactReadable associatedArtifact) throws OseeCoreException {
      Branch parentBranch = branchCache.get(parent);
      TransactionRecord fromTx = txCache.getHeadTransaction(parentBranch);

      String creationComment = String.format("New Branch from %s (%s)", parentBranch.getName(), fromTx.getId());
      return createBranchData(branch, BranchType.WORKING, creationComment, fromTx, author, associatedArtifact, false);
   }

   public CreateBranchData createCopyTxBranchData(IOseeBranch branch, ArtifactReadable author, ITransaction fromTransaction, ArtifactReadable associatedArtifact) throws OseeCoreException {
      int value = fromTransaction.getGuid();
      TransactionRecord fromTx = txCache.getOrLoad(value);
      IOseeBranch parent = fromTx.getBranch();

      String creationComment =
         String.format("Transaction %s copied from %s to create Branch %s", fromTransaction, parent.getName(),
            branch.getName());
      return createBranchData(branch, BranchType.WORKING, creationComment, fromTx, author, associatedArtifact, true);
   }

   public CreateBranchData createPortBranchData(IOseeBranch branch, ArtifactReadable author, ITransaction fromTransaction, ArtifactReadable associatedArtifact) throws OseeCoreException {
      int value = fromTransaction.getGuid();
      TransactionRecord fromTx = txCache.getOrLoad(value);
      IOseeBranch parent = fromTx.getBranch();

      String creationComment =
         String.format("Transaction %d ported from %s to create Branch %s", value, parent.getName(), branch.getName());
      return createBranchData(branch, BranchType.PORT, creationComment, fromTx, author, associatedArtifact, true);
   }

   private CreateBranchData createBranchData(IOseeBranch branch, BranchType branchType, String creationComment, ITransaction fromTx, ArtifactReadable author, ArtifactReadable associatedArtifact, boolean bCopyTx) {
      CreateBranchData createData = new CreateBranchData();
      createData.setUuid(branch.getUuid());
      createData.setName(branch.getName());
      if (branch.getUuid() > 0) {
         createData.setUuid(branch.getUuid());
      }
      createData.setBranchType(branchType);
      createData.setCreationComment(creationComment);
      createData.setFromTransaction(fromTx);
      createData.setUserArtifact(author);
      createData.setAssociatedArtifact(associatedArtifact);
      createData.setTxCopyBranchType(bCopyTx);
      return createData;
   }

}
