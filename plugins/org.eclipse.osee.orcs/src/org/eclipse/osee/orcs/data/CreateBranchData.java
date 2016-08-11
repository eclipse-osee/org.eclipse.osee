/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.data;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Roberto E. Escobar
 */
public class CreateBranchData implements Identifiable<Long> {
   private final long MERGE_DESTINATION_BRANCH_ID = -1; // only used on merge branches
   private final int MERGE_ADDRESSING_QUERY_ID = -1; // only used on merge branches

   private String branchName;
   private long branchUuid;
   private BranchType branchType;
   private String creationComment;
   private TransactionId fromTransaction;
   private TransactionId savedTransaction;
   private ArtifactId associatedArtifact;
   private ArtifactId author;

   // Merge Branch Legacy Support
   private int mergeAddressingQueryId = MERGE_ADDRESSING_QUERY_ID;
   private long mergeDestinationBranchId = MERGE_DESTINATION_BRANCH_ID;

   private boolean txCopyBranchType = false;
   private BranchId parentBranch;

   public CreateBranchData() {
      this(Lib.generateUuid());
   }

   public CreateBranchData(long uuid) {
      branchUuid = uuid;
   }

   @Override
   public Long getGuid() {
      return branchUuid;
   }

   public ArtifactId getAssociatedArtifact() {
      return associatedArtifact;
   }

   @Override
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

   public TransactionId getFromTransaction() {
      return fromTransaction;
   }

   public void setFromTransaction(TransactionId fromTransaction) {
      this.fromTransaction = fromTransaction;
   }

   public TransactionId getSavedTransaction() {
      return savedTransaction;
   }

   public void setSavedTransaction(TransactionId priorTransaction) {
      this.savedTransaction = priorTransaction;
   }

   public ArtifactId getAuthor() {
      return author;
   }

   public void setAuthor(ArtifactId author) {
      this.author = author;
   }

   public void setAssociatedArtifact(ArtifactId associatedArtifact) {
      this.associatedArtifact = associatedArtifact;
   }

   public int getMergeAddressingQueryId() {
      return mergeAddressingQueryId;
   }

   public void setMergeAddressingQueryId(int mergeAddressingQueryId) {
      this.mergeAddressingQueryId = mergeAddressingQueryId;
   }

   public long getMergeDestinationBranchId() {
      return mergeDestinationBranchId;
   }

   public void setMergeDestinationBranchId(long destinationBranchId) {
      this.mergeDestinationBranchId = destinationBranchId;
   }

   @Override
   public int hashCode() {
      return getGuid().hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Identity) {
         return getGuid().equals(((Identity<?>) obj).getGuid());
      }
      return false;
   }

   public boolean isTxCopyBranchType() {
      return txCopyBranchType;
   }

   public void setTxCopyBranchType(boolean value) {
      txCopyBranchType = value;
   }

   @Override
   public String toString() {
      return "CreateBranchData [branchUuid=" + branchUuid + ", branchName=" + branchName + ", branchType=" + branchType + ", creationComment=" + creationComment + ", fromTransaction=" + fromTransaction + ", associatedArtifact=" + associatedArtifact + ", userArtifact=" + author + ", mergeAddressingQueryId=" + mergeAddressingQueryId + ", destinationBranchId=" + mergeDestinationBranchId + "]";
   }

   public long getUuid() {
      return branchUuid;
   }

   public void setUuid(long uuid) {
      if (uuid <= 0) {
         throw new OseeStateException("uuid [%d] must be > 0", uuid);
      }
      this.branchUuid = uuid;
   }

   public void setParentBranch(BranchId parentBranch) {
      this.parentBranch = parentBranch;
   }

   public BranchId getParentBranch() {
      return parentBranch;
   }

}
