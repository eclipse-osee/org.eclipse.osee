/*********************************************************************
 * Copyright (c) 2018 Boeing
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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;

/**
 * @author Ryan D. Brooks
 */
@JsonSerialize(as = Branch.class)
@JsonDeserialize(as = Branch.class)
public class Branch extends BranchViewToken implements IOseeBranch {
   public static final Branch SENTINEL = new Branch(Id.SENTINEL, Named.SENTINEL, ArtifactId.SENTINEL,
      TransactionId.SENTINEL, TransactionId.SENTINEL, BranchId.SENTINEL, false, null, null, false, ArtifactId.SENTINEL);

   private ArtifactId associatedArtifact;
   private TransactionId baselineTx;
   private TransactionId parentTx;
   private BranchId parentBranch;
   private boolean isArchived;
   private BranchState branchState;
   private BranchType branchType;
   private boolean inheritAccessControl;
   private ArtifactId viewId;

   public Branch() {
      super(BranchId.SENTINEL, "Sentinal", ArtifactId.SENTINEL);
      // for jax-rs
   }

   public Branch(Long id, String name, ArtifactId associatedArtifact, TransactionId baselineTx, TransactionId parentTx, BranchId parentBranch, boolean isArchived, BranchState branchState, BranchType branchType, boolean inheritAccessControl, ArtifactId viewId) {
      super(id, name, viewId);
      this.associatedArtifact = associatedArtifact;
      this.baselineTx = baselineTx;
      this.parentTx = parentTx;
      this.parentBranch = parentBranch;
      this.isArchived = isArchived;
      this.branchState = branchState;
      this.branchType = branchType;
      this.inheritAccessControl = inheritAccessControl;
      this.viewId = viewId;
   }

   public ArtifactId getAssociatedArtifact() {
      return associatedArtifact;
   }

   public TransactionId getBaselineTx() {
      return baselineTx;
   }

   public TransactionId getParentTx() {
      return parentTx;
   }

   public BranchId getParentBranch() {
      return parentBranch;
   }

   public boolean hasParentBranch() {
      return parentBranch.isValid();
   }

   public boolean isArchived() {
      return isArchived;
   }

   public BranchState getBranchState() {
      return branchState;
   }

   public BranchType getBranchType() {
      return branchType;
   }

   public boolean inheritAccessControl() {
      return inheritAccessControl;
   }

   @Override
   public ArtifactId getViewId() {
      return viewId;
   }

   public boolean isInheritAccessControl() {
      return inheritAccessControl;
   }

   public void setInheritAccessControl(boolean inheritAccessControl) {
      this.inheritAccessControl = inheritAccessControl;
   }

   public static Branch getSentinel() {
      return SENTINEL;
   }

   public void setAssociatedArtifact(ArtifactId associatedArtifact) {
      this.associatedArtifact = associatedArtifact;
   }

   public void setBaselineTx(TransactionId baselineTx) {
      this.baselineTx = baselineTx;
   }

   public void setParentTx(TransactionId parentTx) {
      this.parentTx = parentTx;
   }

   public void setParentBranch(BranchId parentBranch) {
      this.parentBranch = parentBranch;
   }

   public void setArchived(boolean isArchived) {
      this.isArchived = isArchived;
   }

   public void setBranchState(BranchState branchState) {
      this.branchState = branchState;
   }

   public void setBranchType(BranchType branchType) {
      this.branchType = branchType;
   }

   @Override
   public void setViewId(ArtifactId viewId) {
      this.viewId = viewId;
   }
}