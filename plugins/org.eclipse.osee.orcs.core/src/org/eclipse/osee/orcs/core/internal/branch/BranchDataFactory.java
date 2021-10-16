/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.core.internal.branch;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchType;
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

   public CreateBranchData createBaselineBranchData(BranchToken branch, BranchToken parent, ArtifactId associatedArtifact) {
      return createBranchData(branch, parent, associatedArtifact, BranchType.BASELINE);
   }

   public CreateBranchData createWorkingBranchData(BranchToken branch, BranchToken parent, ArtifactId associatedArtifact) {
      return createBranchData(branch, parent, associatedArtifact, BranchType.WORKING);
   }

   private CreateBranchData createBranchData(BranchToken branch, BranchToken parent, ArtifactId associatedArtifact, BranchType branchType) {
      TransactionQuery txQuery = queryFactory.transactionQuery();
      TransactionToken fromTx = txQuery.andIsHead(parent).getTokens().getExactlyOne();
      String creationComment = String.format("New Branch from %s (%s)", parent.getName(), fromTx.getIdString());
      return createBranchData(branch, parent, branchType, creationComment, fromTx, associatedArtifact, false);
   }

   public CreateBranchData createCopyTxBranchData(BranchToken branch, TransactionId fromTransaction, ArtifactId associatedArtifact) {
      return createBranchData(branch, fromTransaction, associatedArtifact, BranchType.WORKING, "copied");
   }

   public CreateBranchData createPortBranchData(BranchToken branch, TransactionId fromTransaction, ArtifactId associatedArtifact) {
      return createBranchData(branch, fromTransaction, associatedArtifact, BranchType.PORT, "ported");
   }

   private CreateBranchData createBranchData(BranchToken branch, TransactionId fromTransaction, ArtifactId associatedArtifact, BranchType branchType, String verb) {
      TransactionQuery txQuery = queryFactory.transactionQuery();
      BranchQuery branchQuery = queryFactory.branchQuery();
      TransactionToken fromTx = txQuery.andTxId(fromTransaction).getTokens().getExactlyOne();
      BranchToken parent = branchQuery.andId(fromTx.getBranch()).getResults().getExactlyOne();

      String creationComment = String.format("Transaction %d %s from %s to create Branch %s", fromTransaction.getId(),
         verb, parent.getName(), branch.getName());
      return createBranchData(branch, parent, branchType, creationComment, fromTx, associatedArtifact, true);
   }

   private CreateBranchData createBranchData(BranchToken branch, BranchId parent, BranchType branchType, String creationComment, TransactionToken sysRootHeadTx, ArtifactId associatedArtifact, boolean bCopyTx) {
      CreateBranchData createData = new CreateBranchData(branch);
      createData.setName(branch.getName());
      createData.setBranchType(branchType);
      createData.setCreationComment(creationComment);
      createData.setFromTransaction(sysRootHeadTx);
      createData.setAssociatedArtifact(associatedArtifact);
      createData.setTxCopyBranchType(bCopyTx);
      createData.setParentBranch(parent);
      return createData;
   }
}