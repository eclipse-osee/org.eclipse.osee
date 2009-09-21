/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.skynet.core.types.branch;

import java.util.Collection;
import org.eclipse.osee.framework.core.enums.BranchControlled;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.AbstractOseeType;
import org.eclipse.osee.framework.skynet.core.artifact.BranchArchivedState;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;

/**
 * @author Roberto E. Escobar
 */
public class Branch extends AbstractOseeType implements Comparable<Branch> {
   private static final int SHORT_NAME_LIMIT = 25;

   private final int parentTxNumber;
   private final int associatedArtifactId;

   private BranchType branchType;
   private BranchState branchState;
   private BranchArchivedState archivedState;
   private BranchControlled branchControlled;

   public Branch(AbstractOseeCache<Branch> cache, String guid, String name, int parentTxNumber, int associatedArtifactId, BranchType branchType, BranchState branchState, boolean isArchived, boolean isChangeManaged) {
      super(cache, guid, name);
      this.parentTxNumber = parentTxNumber;
      this.archivedState = BranchArchivedState.fromBoolean(isArchived);
      this.branchControlled = BranchControlled.fromBoolean(isChangeManaged);
      this.associatedArtifactId = associatedArtifactId;
      this.branchType = branchType;
      this.branchState = branchState;
   }

   @Override
   protected BranchCache getCache() {
      return (BranchCache) super.getCache();
   }

   public BranchControlled getBranchControl() {
      return branchControlled;
   }

   public BranchType getBranchType() {
      return branchType;
   }

   public BranchState getBranchState() {
      return branchState;
   }

   public int getAssociatedArtifactId() {
      return associatedArtifactId;
   }

   public int getParentTxNumber() {
      return parentTxNumber;
   }

   public BranchArchivedState getArchiveState() {
      return archivedState;
   }

   public void setArchived(boolean isArchived) {
      this.archivedState = BranchArchivedState.fromBoolean(isArchived);
   }

   public void setChangeManaged(boolean isChangeManaged) {
      this.branchControlled = BranchControlled.fromBoolean(isChangeManaged);
   }

   public void setBranchState(BranchState branchState) {
      this.branchState = branchState;
   }

   public void setBranchType(BranchType branchType) {
      this.branchType = branchType;
   }

   public boolean isEditable() {
      BranchState state = getBranchState();
      return !state.isCommitted() && !state.isRebaselined() && // 
      !state.isDeleted() && !state.isCreationInProgress() && //
      !getArchiveState().isArchived();
   }

   @Override
   public String toString() {
      return getName();
   }

   public String getShortName() {
      String shortName = "";
      if (Strings.isValid(getName())) {
         shortName = Strings.truncate(getName(), SHORT_NAME_LIMIT);
      }
      return shortName;
   }

   @Override
   public int compareTo(Branch other) {
      int result = -1;
      if (other != null && other.getName() != null && getName() != null) {
         result = getName().compareTo(other.getName());
      }
      return result;
   }

   public boolean hasTopLevelBranch() throws OseeCoreException {
      return !isTopLevelBranch();
   }

   public boolean isTopLevelBranch() throws OseeCoreException {
      return getParentBranch() != null && getParentBranch().getBranchType().isSystemRootBranch();
   }

   public Branch getParentBranch() throws OseeCoreException {
      return getCache().getParentBranch(this);
   }

   public boolean hasParentBranch() throws OseeCoreException {
      return getParentBranch() != null;
   }

   public Collection<Branch> getChildren() throws OseeCoreException {
      return getCache().getChildren(this);
   }

   //   public boolean hasChanges() throws OseeCoreException {
   //      Pair<TransactionId, TransactionId> transactions = TransactionIdManager.getStartEndPoint(this);
   //      return transactions.getFirst() != transactions.getSecond();
   //   }

   /**
    * @return the top level branch that is an ancestor of this branch (which could be itself)
    * @throws OseeCoreException
    */
   public Branch getTopLevelBranch() throws OseeCoreException {
      Branch branchCursor = this;
      while (branchCursor.hasTopLevelBranch()) {
         branchCursor = branchCursor.getParentBranch();
      }
      return branchCursor;
   }
}