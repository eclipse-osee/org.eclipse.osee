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

package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.access.IAccessControllable;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.BranchCache;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;

/**
 * @author Roberto E. Escobar
 */
public class Branch extends AbstractOseeType implements Comparable<Branch>, IAccessControllable, IAdaptable {
   private static final int SHORT_NAME_LIMIT = 25;

   private BranchType branchType;
   private BranchState branchState;
   private BranchArchivedState archivedState;
   private final DirtyStateDetails dirtyStateDetails;

   public Branch(AbstractOseeCache<Branch> cache, String guid, String name, int parentTxNumber, BranchType branchType, BranchState branchState, boolean isArchived) {
      super(cache, guid, name);
      this.dirtyStateDetails = new DirtyStateDetails();
      this.archivedState = BranchArchivedState.fromBoolean(isArchived);
      this.branchType = branchType;
      this.branchState = branchState;
   }

   @Override
   protected BranchCache getCache() {
      return (BranchCache) super.getCache();
   }

   public Branch getParentBranch() throws OseeCoreException {
      return getCache().getParentBranch(this);
   }

   public boolean hasParentBranch() throws OseeCoreException {
      return getParentBranch() != null;
   }

   public String getShortName() {
      String shortName = "";
      if (Strings.isValid(getName())) {
         shortName = Strings.truncate(getName(), SHORT_NAME_LIMIT);
      }
      return shortName;
   }

   public BranchType getBranchType() {
      return branchType;
   }

   public BranchState getBranchState() {
      return branchState;
   }

   public IArtifact getAssociatedArtifact() throws OseeCoreException {
      return getCache().getAssociatedArtifact(this);
   }

   public void setAssociatedArtifact(IArtifact artifact) throws OseeCoreException {
      IArtifact oldArtifact = getCache().getAssociatedArtifact(this);
      getCache().setAssociatedArtifact(this, artifact);
      IArtifact newArtifact = getCache().getAssociatedArtifact(this);
      getDirtyDetails().isAssociatedArtifactDirty |= isDifferent(oldArtifact, newArtifact);
   }

   public TransactionId getBaseTransaction() throws OseeCoreException {
      return getCache().getBaseTransaction(this);
   }

   public BranchArchivedState getArchiveState() {
      return archivedState;
   }

   public Collection<String> getAliases() throws OseeCoreException {
      return getCache().getAliases(this);
   }

   public void setAliases(String... alias) throws OseeCoreException {
      Collection<String> original = getAliases();
      getCache().setAliases(this, alias);
      Collection<String> other = getAliases();
      getDirtyDetails().areAliasesDirty |= isDifferent(original, other);
   }

   public void setArchived(boolean isArchived) {
      BranchArchivedState newValue = BranchArchivedState.fromBoolean(isArchived);
      getDirtyDetails().isArchivedStateDirty |= isDifferent(this.archivedState, newValue);
      this.archivedState = newValue;
   }

   public void setBranchState(BranchState branchState) {
      getDirtyDetails().isBranchStateDirty |= isDifferent(this.branchState, branchState);
      this.branchState = branchState;
   }

   public void setBranchType(BranchType branchType) {
      getDirtyDetails().isBranchTypeDirty |= isDifferent(this.branchType, branchType);
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

   @Override
   public int compareTo(Branch other) {
      int result = -1;
      if (other != null && other.getName() != null && getName() != null) {
         result = getName().compareTo(other.getName());
      }
      return result;
   }

   public Collection<Branch> getChildren() throws OseeCoreException {
      return getCache().getChildren(this);
   }

   // TODO remove this convenience method
   public int getBranchId() {
      return getId();
   }

   @Override
   public Branch getAccessControlBranch() {
      return this;
   }

   public Collection<Branch> getChildBranches() throws OseeCoreException {
      return getChildBranches(false);
   }

   public Collection<Branch> getChildBranches(boolean recurse) throws OseeCoreException {
      Set<Branch> children = new HashSet<Branch>();
      getChildBranches(this, children, recurse);
      return children;
   }

   private void getChildBranches(Branch parentBranch, Collection<Branch> children, boolean recurse) throws OseeCoreException {
      int parentBranchId = parentBranch.getBranchId();
      for (Branch branch : BranchManager.getNormalBranches()) {
         if (branch.hasParentBranch()) {
            if (parentBranchId == branch.getParentBranch().getBranchId()) {
               children.add(branch);
               if (recurse) {
                  getChildBranches(branch, children, recurse);
               }
            }
         }
      }
   }

   public Collection<Branch> getAncestors() throws OseeCoreException {
      List<Branch> ancestors = new ArrayList<Branch>();
      Branch branchCursor = this;
      ancestors.add(branchCursor);
      while (branchCursor.hasParentBranch()) {
         branchCursor = branchCursor.getParentBranch();
         ancestors.add(branchCursor);
      }
      return ancestors;
   }

   public Collection<Branch> getWorkingBranches() throws OseeCoreException {
      // TODO change this to recurse all Children and then filter by states.

      List<Branch> branches = new ArrayList<Branch>(500);
      for (Branch branch : getCache().getAllTypes()) {
         if (branch.getArchiveState().isUnArchived() && //
         branch.getBranchType().isOfType(BranchType.WORKING) && //
         this.equals(branch.getParentBranch())) {
            branches.add(branch);
         }
      }
      return branches;
   }

   @Override
   public void clearDirty() {
      getDirtyDetails().clearDirty();
   }

   @Override
   public boolean isDirty() {
      return getDirtyDetails().isDirty();
   }

   public DirtyStateDetails getDirtyDetails() {
      return dirtyStateDetails;
   }

   public final class DirtyStateDetails {
      private boolean isBranchTypeDirty;
      private boolean isBranchStateDirty;
      private boolean isArchivedStateDirty;
      private boolean isAssociatedArtifactDirty;
      private boolean areAliasesDirty;

      private DirtyStateDetails() {
         clearDirty();
      }

      public boolean isBranchTypeDirty() {
         return isBranchTypeDirty;
      }

      public boolean isBranchStateDirty() {
         return isBranchStateDirty;
      }

      public boolean isArchivedStateDirty() {
         return isArchivedStateDirty;
      }

      public boolean isAssociatedArtifactDirty() {
         return isAssociatedArtifactDirty;
      }

      public boolean isNameDirty() {
         return Branch.super.isDirty();
      }

      public boolean areAliasesDirty() {
         return areAliasesDirty;
      }

      public boolean isDirty() {
         return isBranchTypeDirty() || //
         isBranchStateDirty() || //
         isArchivedStateDirty() || //
         isAssociatedArtifactDirty() || //
         isNameDirty() || areAliasesDirty();
      }

      public boolean isDataDirty() {
         return isBranchTypeDirty() || //
         isBranchStateDirty() || //
         isArchivedStateDirty() || //
         isAssociatedArtifactDirty() || //
         isNameDirty();
      }

      public void clearDirty() {
         Branch.super.clearDirty();
         isBranchTypeDirty = false;
         isBranchStateDirty = false;
         isArchivedStateDirty = false;
         isAssociatedArtifactDirty = false;
      }
   }

   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      if (adapter == null) {
         throw new IllegalArgumentException("adapter can not be null");
      }

      if (adapter.isInstance(this)) {
         return this;
      }
      return null;
   }

}