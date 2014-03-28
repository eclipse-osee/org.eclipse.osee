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
package org.eclipse.osee.framework.core.message;

import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Roberto E. Escobar
 */
public class BranchCreationRequest {
   private final String branchName;
   private final long parentBranchId;
   private final int associatedArtifactId;

   private final BranchType branchType;
   private final int sourceTransactionId;

   private final int authorId;

   private final String creationComment;
   private final int mergeAddressingQueryId;
   private final long mergeDestinationBranchId;
   private boolean isTxCopyBranchType;
   private final long branchUuid;

   public BranchCreationRequest(BranchType branchType, int sourceTransactionId, long parentBranchId, String branchName, long branchUuid, int associatedArtifactId, int authorId, String creationComment, int mergeAddressingQueryId, long destinationBranchId) {
      this.parentBranchId = parentBranchId;
      this.branchName = branchName;
      if (branchUuid <= 0) {
         throw new OseeArgumentException("branchUuid [%d] uuid must be > 0", branchUuid);
      }
      this.branchUuid = branchUuid;
      this.associatedArtifactId = associatedArtifactId;
      this.branchType = branchType;
      this.sourceTransactionId = sourceTransactionId;
      this.authorId = authorId;
      this.creationComment = creationComment;
      this.mergeAddressingQueryId = mergeAddressingQueryId;
      this.mergeDestinationBranchId = destinationBranchId;
      this.isTxCopyBranchType = false;
   }

   public String getBranchName() {
      return branchName;
   }

   public long getParentBranchId() {
      return parentBranchId;
   }

   public int getAssociatedArtifactId() {
      return associatedArtifactId;
   }

   public BranchType getBranchType() {
      return branchType;
   }

   public int getSourceTransactionId() {
      return sourceTransactionId;
   }

   public int getAuthorId() {
      return authorId;
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

   public boolean txIsCopied() {
      return isTxCopyBranchType;
   }

   public void setTxIsCopied(boolean value) {
      isTxCopyBranchType = value;
   }

   @Override
   public String toString() {
      return "Branch [associatedArtifactId=" + associatedArtifactId + ", branchUuid=" + branchUuid + ", branchType=" + branchType + ", name=" + branchName + ", parentBranchId=" + parentBranchId + ", parentTransactionId=" + sourceTransactionId + "]";
   }

   public long getBranchUuid() {
      return branchUuid;
   }
}
