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
package org.eclipse.osee.orcs.db.internal.branch;

import org.eclipse.osee.framework.core.enums.BranchType;

/**
 * @author Roberto E. Escobar
 */
public class CreateDatabaseBranch {
   private final String branchName;
   private final int parentBranchId;
   private final int associatedArtifactId;

   private final BranchType branchType;
   private final int sourceTransactionId;
   private final long branchUuid;

   private final int authorId;

   private final String creationComment;
   private final int mergeAddressingQueryId;
   private final int mergeDestinationBranchId;

   public CreateDatabaseBranch(BranchType branchType, int sourceTransactionId, int parentBranchId, long branchUuid, String branchName, int associatedArtifactId, int authorId, String creationComment, int mergeAddressingQueryId, int mergeDestinationBranchId) {
      this.parentBranchId = parentBranchId;
      this.branchName = branchName;
      this.associatedArtifactId = associatedArtifactId;
      this.branchType = branchType;
      this.sourceTransactionId = sourceTransactionId;
      this.branchUuid = branchUuid;
      this.authorId = authorId;
      this.creationComment = creationComment;
      this.mergeAddressingQueryId = mergeAddressingQueryId;
      this.mergeDestinationBranchId = mergeDestinationBranchId;
   }

   public String getBranchName() {
      return branchName;
   }

   public int getParentBranchId() {
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

   public long getBranchUuid() {
      return branchUuid;
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

   public int getMergeDestinationBranchId() {
      return mergeDestinationBranchId;
   }

   @Override
   public String toString() {
      return "Branch [associatedArtifactId=" + associatedArtifactId + ", branchUuid=" + branchUuid + ", branchType=" + branchType + ", name=" + branchName + ", parentBranchId=" + parentBranchId + ", parentTransactionId=" + sourceTransactionId + "]";
   }
}
