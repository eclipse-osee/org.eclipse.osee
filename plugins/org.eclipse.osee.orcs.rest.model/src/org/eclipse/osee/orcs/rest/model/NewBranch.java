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

import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.BranchType;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class NewBranch {

   private String branchName;
   private BranchId parentBranch;
   private int associatedArtifactId;
   private BranchType branchType;

   private int authorId;
   private TransactionId sourceTransaction;
   private String creationComment;
   private int mergeAddressingQueryId;
   private long mergeDestinationBranchId;
   private boolean txCopyBranchType;

   public NewBranch() {
      super();
   }

   public String getBranchName() {
      return branchName;
   }

   public BranchId getParentBranch() {
      return parentBranch;
   }

   public int getAssociatedArtifactId() {
      return associatedArtifactId;
   }

   public BranchType getBranchType() {
      return branchType;
   }

   public int getAuthorId() {
      return authorId;
   }

   public TransactionId getSourceTransaction() {
      return sourceTransaction;
   }

   public String getCreationComment() {
      return creationComment;
   }

   public int getMergeAddressingQueryId() {
      return mergeAddressingQueryId;
   }

   public long getMergeDestinationBranchId() {
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

   public void setAssociatedArtifactId(int associatedArtifactId) {
      this.associatedArtifactId = associatedArtifactId;
   }

   public void setBranchType(BranchType branchType) {
      this.branchType = branchType;
   }

   public void setAuthorId(int authorId) {
      this.authorId = authorId;
   }

   public void setSourceTransactionId(TransactionId sourceTransaction) {
      this.sourceTransaction = sourceTransaction;
   }

   public void setCreationComment(String creationComment) {
      this.creationComment = creationComment;
   }

   public void setMergeAddressingQueryId(int mergeAddressingQueryId) {
      this.mergeAddressingQueryId = mergeAddressingQueryId;
   }

   public void setMergeDestinationBranchId(BranchId mergeDestinationBranch) {
      this.mergeDestinationBranchId = mergeDestinationBranch.getId();
   }

   public void setTxCopyBranchType(boolean txCopyBranchType) {
      this.txCopyBranchType = txCopyBranchType;
   }

   @Override
   public String toString() {
      return "NewBranch [branchName=" + branchName + ", parentBranchId=" + parentBranch + ", associatedArtifactId=" + associatedArtifactId + ", branchType=" + branchType + ", authorId=" + authorId + ", sourceTransactionId=" + sourceTransaction + ", creationComment=" + creationComment + ", mergeAddressingQueryId=" + mergeAddressingQueryId + ", mergeDestinationBranchId=" + mergeDestinationBranchId + ", txCopyBranchType=" + txCopyBranchType + "]";
   }

}
