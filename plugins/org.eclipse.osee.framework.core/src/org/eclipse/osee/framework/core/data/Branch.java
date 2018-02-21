/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Ryan D. Brooks
 */
public class Branch extends NamedIdBase implements IOseeBranch {
   private final ArtifactId associatedArtifact;
   private final TransactionId baselineTx;
   private final TransactionId parentTx;
   private final BranchId parentBranch;
   private final boolean isArchived;
   private final BranchState branchState;
   private final BranchType branchType;
   private final boolean inheritAccessControl;
   private final ArtifactId viewId;

   public Branch(Long id, String name, ArtifactId associatedArtifact, TransactionId baselineTx, TransactionId parentTx, BranchId parentBranch, boolean isArchived, BranchState branchState, BranchType branchType, boolean inheritAccessControl, ArtifactId viewId) {
      super(id, name);
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
}