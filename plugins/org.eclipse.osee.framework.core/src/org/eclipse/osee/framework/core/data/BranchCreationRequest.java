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
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.enums.BranchType;

/**
 * @author Roberto E. Escobar
 */
public class BranchCreationRequest {
   private final String branchName;
   private final int parentBranchId;
   private final int associatedArtifactId;

   private final BranchType branchType;
   private final int sourceTransactionId;
   private final String branchGuid;

   private final int authorId;

   private final String creationComment;
   private final int populateBaseTxFromAddressingQueryId;
   private final int destinationBranchId;

   public BranchCreationRequest(BranchType branchType, int sourceTransactionId, int parentBranchId, String branchGuid, String branchName, int associatedArtifactId, int authorId, String creationComment, int populateBaseTxFromAddressingQueryId, int destinationBranchId) {
      this.parentBranchId = parentBranchId;
      this.branchName = branchName;
      this.associatedArtifactId = associatedArtifactId;
      this.branchType = branchType;
      this.sourceTransactionId = sourceTransactionId;
      this.branchGuid = branchGuid;
      this.authorId = authorId;
      this.creationComment = creationComment;
      this.populateBaseTxFromAddressingQueryId = populateBaseTxFromAddressingQueryId;
      this.destinationBranchId = destinationBranchId;
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

   public String getBranchGuid() {
      return branchGuid;
   }

   public int getAuthorId() {
      return authorId;
   }

   public String getCreationComment() {
      return creationComment;
   }

   public int getPopulateBaseTxFromAddressingQueryId() {
      return populateBaseTxFromAddressingQueryId;
   }

   public int getDestinationBranchId() {
      return destinationBranchId;
   }

   @Override
   public String toString() {
      return "Branch [associatedArtifactId=" + associatedArtifactId + ", branchGuid=" + branchGuid + ", branchType=" + branchType + ", name=" + branchName + ", parentBranchId=" + parentBranchId + ", parentTransactionId=" + sourceTransactionId + "]";
   }
}
