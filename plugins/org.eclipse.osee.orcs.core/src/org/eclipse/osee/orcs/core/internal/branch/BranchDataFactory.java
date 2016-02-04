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

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TransactionQuery;

/**
 * @author David W. Miller
 */
public class BranchDataFactory {

   private final QueryFactory queryFactory;

   public BranchDataFactory(QueryFactory queryFactory) {
      this.queryFactory = queryFactory;
   }

   public CreateBranchData createTopLevelBranchData(IOseeBranch branch, ArtifactReadable author) throws OseeCoreException {
      TransactionQuery txQuery = queryFactory.transactionQuery();
      TransactionReadable fromTx = txQuery.andIsHead(CoreBranches.SYSTEM_ROOT).getResults().getExactlyOne();
      String creationComment = String.format("Branch Creation for %s", branch.getName());
      return createBranchData(branch, CoreBranches.SYSTEM_ROOT, BranchType.BASELINE, creationComment, fromTx, author,
         null, false);
   }

   public CreateBranchData createBaselineBranchData(IOseeBranch branch, ArtifactReadable author, BranchId parent, ArtifactReadable associatedArtifact) throws OseeCoreException {
      TransactionQuery txQuery = queryFactory.transactionQuery();
      TransactionReadable fromTx = txQuery.andIsHead(parent).getResults().getExactlyOne();
      String creationComment = String.format("Branch Creation for %s", branch.getName());
      return createBranchData(branch, parent, BranchType.BASELINE, creationComment, fromTx, author, associatedArtifact,
         false);
   }

   public CreateBranchData createWorkingBranchData(IOseeBranch branch, ArtifactReadable author, IOseeBranch parent, ArtifactReadable associatedArtifact) throws OseeCoreException {
      TransactionQuery txQuery = queryFactory.transactionQuery();
      TransactionReadable fromTx = txQuery.andIsHead(parent).getResults().getExactlyOne();
      String creationComment = String.format("New Branch from %s (%s)", parent.getName(), fromTx.getGuid());
      return createBranchData(branch, parent, BranchType.WORKING, creationComment, fromTx, author, associatedArtifact,
         false);
   }

   public CreateBranchData createCopyTxBranchData(IOseeBranch branch, ArtifactReadable author, ITransaction fromTransaction, ArtifactReadable associatedArtifact) throws OseeCoreException {
      TransactionQuery txQuery = queryFactory.transactionQuery();
      BranchQuery branchQuery = queryFactory.branchQuery();
      TransactionReadable fromTx = txQuery.andTxId(fromTransaction.getGuid()).getResults().getExactlyOne();
      IOseeBranch parent = branchQuery.andUuids(fromTx.getBranchId()).getResults().getExactlyOne();

      String creationComment = String.format("Transaction %s copied from %s to create Branch %s", fromTransaction,
         parent.getName(), branch.getName());
      return createBranchData(branch, parent, BranchType.WORKING, creationComment, fromTx, author, associatedArtifact,
         true);
   }

   public CreateBranchData createPortBranchData(IOseeBranch branch, ArtifactReadable author, ITransaction fromTransaction, ArtifactReadable associatedArtifact) throws OseeCoreException {
      TransactionQuery txQuery = queryFactory.transactionQuery();
      BranchQuery branchQuery = queryFactory.branchQuery();
      int value = fromTransaction.getGuid();
      TransactionReadable fromTx = txQuery.andTxId(value).getResults().getExactlyOne();
      IOseeBranch parent = branchQuery.andUuids(fromTx.getBranchId()).getResults().getExactlyOne();

      String creationComment =
         String.format("Transaction %d ported from %s to create Branch %s", value, parent.getName(), branch.getName());
      return createBranchData(branch, parent, BranchType.PORT, creationComment, fromTx, author, associatedArtifact,
         true);
   }

   private CreateBranchData createBranchData(IOseeBranch branch, BranchId parent, BranchType branchType, String creationComment, TransactionReadable sysRootHeadTx, ArtifactReadable author, ArtifactReadable associatedArtifact, boolean bCopyTx) {
      CreateBranchData createData = new CreateBranchData();
      createData.setUuid(branch.getUuid());
      createData.setName(branch.getName());
      if (branch.getUuid() > 0) {
         createData.setUuid(branch.getUuid());
      }
      createData.setBranchType(branchType);
      createData.setCreationComment(creationComment);
      createData.setFromTransaction(sysRootHeadTx);
      createData.setUserArtifact(author);
      createData.setAssociatedArtifact(associatedArtifact);
      createData.setTxCopyBranchType(bCopyTx);
      if (parent != null) {
         createData.setParentBranchUuid(parent.getGuid());
      }
      return createData;
   }

}
