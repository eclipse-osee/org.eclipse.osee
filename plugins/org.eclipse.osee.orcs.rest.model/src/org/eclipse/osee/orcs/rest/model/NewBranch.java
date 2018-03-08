/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.model;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchType;

/**
 * @author Roberto E. Escobar
 */
public class NewBranch {
   private String branchName;
   private BranchId parentBranch;
   private ArtifactId associatedArtifact;
   private BranchType branchType;
   private ArtifactId author;
   private TransactionToken sourceTransaction;
   private String creationComment;
   private int mergeAddressingQueryId;
   private BranchId mergeDestinationBranchId;
   private boolean txCopyBranchType;

   public String getBranchName() {
      return branchName;
   }

   public BranchId getParentBranch() {
      return parentBranch;
   }

   public ArtifactId getAssociatedArtifact() {
      return associatedArtifact;
   }

   public BranchType getBranchType() {
      return branchType;
   }

   public ArtifactId getAuthor() {
      return author;
   }

   public TransactionToken getSourceTransaction() {
      return sourceTransaction;
   }

   public String getCreationComment() {
      return creationComment;
   }

   public int getMergeAddressingQueryId() {
      return mergeAddressingQueryId;
   }

   public BranchId getMergeDestinationBranchId() {
      return mergeDestinationBranchId;
   }

   public boolean isTxCopyBranchType() {
      return txCopyBranchType;
   }

   public void setBranchName(String branchName) {
      this.branchName = branchName;
   }

   public void setParentBranchId(BranchId parentBranch) {
      this.parentBranch = parentBranch;
   }

   public void setAssociatedArtifact(ArtifactId associatedArtifact) {
      this.associatedArtifact = associatedArtifact;
   }

   public void setBranchType(BranchType branchType) {
      this.branchType = branchType;
   }

   public void setAuthor(ArtifactId author) {
      this.author = author;
   }

   public void setSourceTransactionId(TransactionToken sourceTransaction) {
      this.sourceTransaction = sourceTransaction;
   }

   public void setCreationComment(String creationComment) {
      this.creationComment = creationComment;
   }

   public void setMergeAddressingQueryId(int mergeAddressingQueryId) {
      this.mergeAddressingQueryId = mergeAddressingQueryId;
   }

   public void setMergeDestinationBranchId(BranchId mergeDestinationBranch) {
      this.mergeDestinationBranchId = mergeDestinationBranch;
   }

   public void setTxCopyBranchType(boolean txCopyBranchType) {
      this.txCopyBranchType = txCopyBranchType;
   }

   @Override
   public String toString() {
      return "NewBranch [branchName=" + branchName + ", parentBranchId=" + parentBranch + ", associatedArtifactId=" + associatedArtifact + ", branchType=" + branchType + ", authorId=" + author + ", sourceTransactionId=" + sourceTransaction + ", creationComment=" + creationComment + ", mergeAddressingQueryId=" + mergeAddressingQueryId + ", mergeDestinationBranchId=" + mergeDestinationBranchId + ", txCopyBranchType=" + txCopyBranchType + "]";
   }
}