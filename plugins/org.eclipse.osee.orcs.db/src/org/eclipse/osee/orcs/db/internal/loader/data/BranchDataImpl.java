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

import org.eclipse.osee.framework.core.data.RelationalConstants;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.orcs.core.ds.BranchData;
import org.eclipse.osee.orcs.data.BranchReadable;

/**
 * @author Roberto E. Escobar
 */
public class BranchDataImpl extends NamedId implements BranchData, BranchReadable {
   private int associatedArtifactId = RelationalConstants.ART_ID_SENTINEL;
   private int baseTransaction = RelationalConstants.TRANSACTION_SENTINEL;
   private int sourceTransaction = RelationalConstants.TRANSACTION_SENTINEL;
   private long parentBranch = RelationalConstants.BRANCH_SENTINEL.getId();
   private BranchArchivedState archiveState = BranchArchivedState.UNARCHIVED;
   private BranchState branchState = BranchState.CREATED;
   private BranchType branchType = BranchType.WORKING;
   private boolean inheritAccessControl = false;

   public BranchDataImpl(Long branchId, String name) {
      super(branchId, name);
   }

   @Override
   public int getAssociatedArtifactId() {
      return associatedArtifactId;
   }

   @Override
   public void setAssociatedArtifactId(int artId) {
      associatedArtifactId = artId;
   }

   @Override
   public int getBaseTransaction() {
      return baseTransaction;
   }

   @Override
   public void setBaseTransaction(int baseTx) {
      baseTransaction = baseTx;
   }

   @Override
   public int getSourceTransaction() {
      return sourceTransaction;
   }

   @Override
   public void setSourceTransaction(int sourceTx) {
      sourceTransaction = sourceTx;
   }

   @Override
   public long getParentBranch() {
      return parentBranch;
   }

   @Override
   public void setParentBranch(long parent) {
      parentBranch = parent;
   }

   @Override
   public boolean hasParentBranch() {
      return !RelationalConstants.BRANCH_SENTINEL.equals(getParentBranch());
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
}