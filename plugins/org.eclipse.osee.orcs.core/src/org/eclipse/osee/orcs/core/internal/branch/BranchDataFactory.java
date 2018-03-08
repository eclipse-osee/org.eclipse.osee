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

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.orcs.data.CreateBranchData;
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

   public CreateBranchData createTopLevelBranchData(IOseeBranch branch, ArtifactId author) {
      return createBranchData(branch, author, CoreBranches.SYSTEM_ROOT, ArtifactId.SENTINEL, BranchType.BASELINE);
   }

   public CreateBranchData createBaselineBranchData(IOseeBranch branch, ArtifactId author, IOseeBranch parent, ArtifactId associatedArtifact) {
      return createBranchData(branch, author, parent, associatedArtifact, BranchType.BASELINE);
   }

   public CreateBranchData createWorkingBranchData(IOseeBranch branch, ArtifactId author, IOseeBranch parent, ArtifactId associatedArtifact) {
      return createBranchData(branch, author, parent, associatedArtifact, BranchType.WORKING);
   }

   private CreateBranchData createBranchData(IOseeBranch branch, ArtifactId author, IOseeBranch parent, ArtifactId associatedArtifact, BranchType branchType) {
      TransactionQuery txQuery = queryFactory.transactionQuery();
      TransactionToken fromTx = txQuery.andIsHead(parent).getTokens().getExactlyOne();
      String creationComment = String.format("New Branch from %s (%s)", parent.getName(), fromTx);
      return createBranchData(branch, parent, branchType, creationComment, fromTx, author, associatedArtifact, false);
   }

   public CreateBranchData createCopyTxBranchData(IOseeBranch branch, ArtifactId author, TransactionId fromTransaction, ArtifactId associatedArtifact) {
      return createBranchData(branch, author, fromTransaction, associatedArtifact, BranchType.WORKING, "copied");
   }

   public CreateBranchData createPortBranchData(IOseeBranch branch, ArtifactId author, TransactionId fromTransaction, ArtifactId associatedArtifact) {
      return createBranchData(branch, author, fromTransaction, associatedArtifact, BranchType.PORT, "ported");
   }

   private CreateBranchData createBranchData(IOseeBranch branch, ArtifactId author, TransactionId fromTransaction, ArtifactId associatedArtifact, BranchType branchType, String verb) {
      TransactionQuery txQuery = queryFactory.transactionQuery();
      BranchQuery branchQuery = queryFactory.branchQuery();
      TransactionToken fromTx = txQuery.andTxId(fromTransaction).getTokens().getExactlyOne();
      IOseeBranch parent = branchQuery.andId(fromTx.getBranch()).getResults().getExactlyOne();

      String creationComment = String.format("Transaction %d %s from %s to create Branch %s", fromTransaction.getId(),
         verb, parent.getName(), branch.getName());
      return createBranchData(branch, parent, branchType, creationComment, fromTx, author, associatedArtifact, true);
   }

   private CreateBranchData createBranchData(IOseeBranch branch, BranchId parent, BranchType branchType, String creationComment, TransactionToken sysRootHeadTx, ArtifactId author, ArtifactId associatedArtifact, boolean bCopyTx) {
      CreateBranchData createData = new CreateBranchData(branch);
      createData.setName(branch.getName());
      createData.setBranchType(branchType);
      createData.setCreationComment(creationComment);
      createData.setFromTransaction(sysRootHeadTx);
      createData.setAuthor(author);
      createData.setAssociatedArtifact(associatedArtifact);
      createData.setTxCopyBranchType(bCopyTx);
      createData.setParentBranch(parent);
      return createData;
   }
}