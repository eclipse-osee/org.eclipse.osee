/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.loader.data;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.orcs.core.ds.BranchData;
import org.eclipse.osee.orcs.data.BranchReadable;

/**
 * @author Roberto E. Escobar
 */
public class BranchDataImpl extends NamedIdBase implements BranchData, BranchReadable {
   private ArtifactId associatedArtifact = ArtifactId.SENTINEL;
   private TransactionId baseTransaction = TransactionId.SENTINEL;
   private TransactionId sourceTransaction = TransactionId.SENTINEL;
   private BranchId parentBranch = BranchId.SENTINEL;
   private BranchArchivedState archiveState = BranchArchivedState.UNARCHIVED;
   private BranchState branchState = BranchState.CREATED;
   private BranchType branchType = BranchType.WORKING;
   private boolean inheritAccessControl = false;
   private ArtifactId viewId = ArtifactId.SENTINEL;

   public BranchDataImpl(Long branchId, String name) {
      this(branchId, name, ArtifactId.SENTINEL);
   }

   public BranchDataImpl(Long branchId, String name, ArtifactId viewId) {
      super(branchId, name);
      this.viewId = viewId;
   }

   @Override
   public ArtifactId getAssociatedArtifact() {
      return associatedArtifact;
   }

   @Override
   public void setAssociatedArtifact(ArtifactId associatedArtifact) {
      this.associatedArtifact = associatedArtifact;
   }

   @Override
   public TransactionId getBaseTransaction() {
      return baseTransaction;
   }

   @Override
   public void setBaseTransaction(TransactionId baseTx) {
      baseTransaction = baseTx;
   }

   @Override
   public TransactionId getSourceTransaction() {
      return sourceTransaction;
   }

   @Override
   public void setSourceTransaction(TransactionId sourceTx) {
      sourceTransaction = sourceTx;
   }

   @Override
   public BranchId getParentBranch() {
      return parentBranch;
   }

   @Override
   public void setParentBranch(BranchId parent) {
      parentBranch = parent;
   }

   @Override
   public boolean hasParentBranch() {
      return getParentBranch().isValid();
   }

   @Override
   public BranchArchivedState getArchiveState() {
      return archiveState;
   }

   @Override
   public void setArchiveState(BranchArchivedState state) {
      this.archiveState = state;
   }

   @Override
   public BranchState getBranchState() {
      return branchState;
   }

   @Override
   public void setBranchState(BranchState state) {
      this.branchState = state;
   }

   @Override
   public BranchType getBranchType() {
      return branchType;
   }

   @Override
   public void setBranchType(BranchType type) {
      branchType = type;
   }

   @Override
   public String toString() {
      return "BranchData [uuid=" + getId() + ", " + super.toString() + "]";
   }

   @Override
   public boolean isInheritAccessControl() {
      return inheritAccessControl;
   }

   @Override
   public void setInheritAccessControl(boolean inheritAccessControl) {
      this.inheritAccessControl = inheritAccessControl;
   }

   @Override
   public ArtifactId getViewId() {
      return viewId;
   }
}