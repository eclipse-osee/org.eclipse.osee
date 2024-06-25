/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.enums.BranchState;

/**
 * @author Ryan T. Baldwin
 */
public class UpdateFromParentData {
   private final BranchId sourceBranchId;
   private final String sourceBranchName;
   private final BranchState sourceBranchState;
   private final TransactionId sourceBranchBaselineTx;
   private final TransactionId sourceBranchHeadTxNoCategory;
   private final ArtifactId sourceBranchAssociatedArtifact;
   private final BranchId parentBranchId;
   private final String parentBranchName;
   private final TransactionId parentBranchTxId;
   private final TransactionId parentBranchHeadTx;

   public UpdateFromParentData(BranchId sourceBranchId, String sourceBranchName, BranchState sourceBranchState, TransactionId sourceBranchBaselineTx, TransactionId sourceBranchHeadTxNoCategory, ArtifactId sourceBranchAssociatedArtifact, BranchId parentBranchId, String parentBranchName, TransactionId parentBranchTxId, TransactionId parentBranchHeadTx) {
      this.sourceBranchId = sourceBranchId;
      this.sourceBranchName = sourceBranchName;
      this.sourceBranchState = sourceBranchState;
      this.sourceBranchBaselineTx = sourceBranchBaselineTx;
      this.sourceBranchHeadTxNoCategory = sourceBranchHeadTxNoCategory;
      this.sourceBranchAssociatedArtifact = sourceBranchAssociatedArtifact;
      this.parentBranchId = parentBranchId;
      this.parentBranchName = parentBranchName;
      this.parentBranchTxId = parentBranchTxId;
      this.parentBranchHeadTx = parentBranchHeadTx;
   }

   public UpdateFromParentData() {
      this.sourceBranchId = BranchId.SENTINEL;
      this.sourceBranchName = "";
      this.sourceBranchState = BranchState.CREATED;
      this.sourceBranchBaselineTx = TransactionId.SENTINEL;
      this.sourceBranchHeadTxNoCategory = TransactionId.SENTINEL;
      this.sourceBranchAssociatedArtifact = ArtifactId.SENTINEL;
      this.parentBranchId = BranchId.SENTINEL;
      this.parentBranchName = "";
      this.parentBranchTxId = TransactionId.SENTINEL;
      this.parentBranchHeadTx = TransactionId.SENTINEL;
   }

   public BranchId getSourceBranchId() {
      return sourceBranchId;
   }

   public String getSourceBranchName() {
      return sourceBranchName;
   }

   public BranchState getSourceBranchState() {
      return sourceBranchState;
   }

   public TransactionId getSourceBranchBaselineTx() {
      return sourceBranchBaselineTx;
   }

   public TransactionId getSourceBranchHeadTxNoCategory() {
      return sourceBranchHeadTxNoCategory;
   }

   public ArtifactId getSourceBranchAssociatedArtifact() {
      return sourceBranchAssociatedArtifact;
   }

   public BranchId getParentBranchId() {
      return parentBranchId;
   }

   public String getParentBranchName() {
      return parentBranchName;
   }

   public TransactionId getParentBranchTxId() {
      return parentBranchTxId;
   }

   public TransactionId getParentBranchHeadTx() {
      return parentBranchHeadTx;
   }

}
