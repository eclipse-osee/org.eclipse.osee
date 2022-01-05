/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.orcs.data;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Roberto E. Escobar
 */
public class CreateBranchData {
   private final BranchId MERGE_DESTINATION_BRANCH_ID = BranchId.SENTINEL; // only used on merge branches
   private final Long MERGE_ADDRESSING_QUERY_ID = Id.SENTINEL; // only used on merge branches

   private String branchName;
   private final BranchId branch;
   private BranchType branchType;
   private String creationComment;
   private TransactionToken fromTransaction;
   private TransactionId savedTransaction;
   private ArtifactId associatedArtifact;

   // Merge Branch Legacy Support
   private Long mergeAddressingQueryId = MERGE_ADDRESSING_QUERY_ID;
   private BranchId mergeDestinationBranchId = MERGE_DESTINATION_BRANCH_ID;

   private boolean txCopyBranchType = false;
   private BranchId parentBranch;
   private boolean inheritAccess = false;

   public CreateBranchData() {
      this(BranchId.create());
   }

   public CreateBranchData(BranchId branch) {
      this.branch = branch;
   }

   public CreateBranchData(BranchToken branch) {
      this.branch = branch;
      this.branchName = branch.getName();
   }

   public ArtifactId getAssociatedArtifact() {
      return associatedArtifact;
   }

   public String getName() {
      return branchName;
   }

   public void setName(String branchName) {
      this.branchName = branchName;
   }

   public BranchType getBranchType() {
      return branchType;
   }

   public void setBranchType(BranchType branchType) {
      this.branchType = branchType;
   }

   public String getCreationComment() {
      return creationComment;
   }

   public void setCreationComment(String creationComment) {
      this.creationComment = creationComment;
   }

   public TransactionToken getFromTransaction() {
      return fromTransaction;
   }

   public void setFromTransaction(TransactionToken fromTransaction) {
      this.fromTransaction = fromTransaction;
   }

   public TransactionId getSavedTransaction() {
      return savedTransaction;
   }

   public void setSavedTransaction(TransactionId priorTransaction) {
      this.savedTransaction = priorTransaction;
   }

   public void setAssociatedArtifact(ArtifactId associatedArtifact) {
      this.associatedArtifact = associatedArtifact;
   }

   public Long getMergeAddressingQueryId() {
      return mergeAddressingQueryId;
   }

   public void setMergeAddressingQueryId(Long mergeAddressingQueryId) {
      this.mergeAddressingQueryId = mergeAddressingQueryId;
   }

   public BranchId getMergeDestinationBranchId() {
      return mergeDestinationBranchId;
   }

   public void setMergeDestinationBranchId(BranchId destinationBranchId) {
      this.mergeDestinationBranchId = destinationBranchId;
   }

   @Override
   public int hashCode() {
      return branch.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      return branch.equals(obj);
   }

   public boolean isTxCopyBranchType() {
      return txCopyBranchType;
   }

   public void setTxCopyBranchType(boolean value) {
      txCopyBranchType = value;
   }

   public boolean isInheritAccess() {
      return inheritAccess;
   }

   public void setInheritAccess(boolean value) {
      inheritAccess = value;
   }

   @Override
   public String toString() {
      return "CreateBranchData [branchUuid=" + branch + ", branchName=" + branchName + ", branchType=" + branchType + ", creationComment=" + creationComment + ", fromTransaction=" + fromTransaction + ", associatedArtifact=" + associatedArtifact + ", mergeAddressingQueryId=" + mergeAddressingQueryId + ", destinationBranchId=" + mergeDestinationBranchId + "]";
   }

   public BranchId getBranch() {
      return branch;
   }

   public void setParentBranch(BranchId parentBranch) {
      this.parentBranch = parentBranch;
   }

   public BranchId getParentBranch() {
      return parentBranch;
   }

}
