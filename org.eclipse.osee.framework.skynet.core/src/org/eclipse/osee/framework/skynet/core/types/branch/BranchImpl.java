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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.AbstractOseeCache;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.data.OseeField;
import org.eclipse.osee.framework.core.data.TransactionRecord;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.types.BranchCache;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.skynet.core.types.field.AliasesField;
import org.eclipse.osee.framework.skynet.core.types.field.AssociatedArtifactField;
import org.eclipse.osee.framework.skynet.core.types.impl.BranchField;

/**
 * @author Roberto E. Escobar
 */
public final class BranchImpl extends Branch {
   private static final int SHORT_NAME_LIMIT = 25;

   public BranchImpl(AbstractOseeCache<Branch> cache, String guid, String name, BranchType branchType, BranchState branchState, boolean isArchived) {
      super(cache, guid, name);
      setFieldLogException(BranchField.BRANCH_TYPE_FIELD_KEY, branchType);
      setFieldLogException(BranchField.BRANCH_STATE_FIELD_KEY, branchState);
      setFieldLogException(BranchField.BRANCH_ARCHIVED_STATE_FIELD_KEY, BranchArchivedState.fromBoolean(isArchived));
   }

   @Override
   protected void initializeFields() {
      addField(BranchField.BRANCH_TYPE_FIELD_KEY, new OseeField<BranchType>());
      addField(BranchField.BRANCH_STATE_FIELD_KEY, new OseeField<BranchState>());
      addField(BranchField.BRANCH_ARCHIVED_STATE_FIELD_KEY, new OseeField<BranchArchivedState>());
      addField(BranchField.BRANCH_ASSOCIATED_ARTIFACT_FIELD_KEY, new AssociatedArtifactField(getCache(), this));
      addField(BranchField.BRANCH_ALIASES_FIELD_KEY, new AliasesField(getCache(), this));
   }

   @Override
   protected BranchCache getCache() {
      return (BranchCache) super.getCache();
   }

   @Override
   public Branch getParentBranch() throws OseeCoreException {
      return getCache().getParentBranch(this);
   }

   @Override
   public boolean hasParentBranch() throws OseeCoreException {
      return getParentBranch() != null;
   }

   @Override
   public String getShortName() {
      String shortName = "";
      if (Strings.isValid(getName())) {
         shortName = Strings.truncate(getName(), SHORT_NAME_LIMIT);
      }
      return shortName;
   }

   @Override
   public BranchType getBranchType() {
      return getFieldValueLogException(null, BranchField.BRANCH_TYPE_FIELD_KEY);
   }

   @Override
   public BranchState getBranchState() {
      return getFieldValueLogException(null, BranchField.BRANCH_STATE_FIELD_KEY);
   }

   @Override
   public BranchArchivedState getArchiveState() {
      return getFieldValueLogException(null, BranchField.BRANCH_ARCHIVED_STATE_FIELD_KEY);
   }

   @Override
   public IArtifact getAssociatedArtifact() throws OseeCoreException {
      return getFieldValue(BranchField.BRANCH_ASSOCIATED_ARTIFACT_FIELD_KEY);
   }

   @Override
   public void setAssociatedArtifact(IBasicArtifact<?> artifact) throws OseeCoreException {
      setField(BranchField.BRANCH_ASSOCIATED_ARTIFACT_FIELD_KEY, artifact);
   }

   @Override
   public TransactionRecord getBaseTransaction() throws OseeCoreException {
      return getCache().getBaseTransaction(this);
   }

   @Override
   public TransactionRecord getSourceTransaction() throws OseeCoreException {
      return getCache().getSourceTransaction(this);
   }

   @Override
   public Collection<String> getAliases() throws OseeCoreException {
      return getFieldValue(BranchField.BRANCH_ALIASES_FIELD_KEY);
   }

   @Override
   public void setAliases(String... alias) throws OseeCoreException {
      setField(BranchField.BRANCH_ALIASES_FIELD_KEY, Arrays.asList(alias));
   }

   @Override
   public void setArchived(boolean isArchived) {
      BranchArchivedState newValue = BranchArchivedState.fromBoolean(isArchived);
      setFieldLogException(BranchField.BRANCH_ARCHIVED_STATE_FIELD_KEY, newValue);
   }

   @Override
   public void setBranchState(BranchState branchState) {
      setFieldLogException(BranchField.BRANCH_STATE_FIELD_KEY, branchState);
   }

   @Override
   public void setBranchType(BranchType branchType) {
      setFieldLogException(BranchField.BRANCH_TYPE_FIELD_KEY, branchType);
   }

   @Override
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

   @Override
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

   @Override
   public Collection<Branch> getChildBranches() throws OseeCoreException {
      return getChildBranches(false);
   }

   @Override
   public Collection<Branch> getChildBranches(boolean recurse) throws OseeCoreException {
      Set<Branch> children = new HashSet<Branch>();
      getChildBranches(this, children, recurse);
      return children;
   }

   private void getChildBranches(Branch parentBranch, Collection<Branch> children, boolean recurse) throws OseeCoreException {
      int parentBranchId = parentBranch.getId();
      for (Branch branch : BranchManager.getNormalBranches()) {
         if (branch.hasParentBranch()) {
            if (parentBranchId == branch.getParentBranch().getId()) {
               children.add(branch);
               if (recurse) {
                  getChildBranches(branch, children, recurse);
               }
            }
         }
      }
   }

   @Override
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

   @Override
   public Collection<Branch> getWorkingBranches() throws OseeCoreException {
      // TODO change this to recurse all Children and then filter by states.

      List<Branch> branches = new ArrayList<Branch>(500);
      for (Branch branch : getCache().getAll()) {
         if (branch.getArchiveState().isUnArchived() && //
         branch.getBranchType().isOfType(BranchType.WORKING) && //
         this.equals(branch.getParentBranch())) {
            branches.add(branch);
         }
      }
      return branches;
   }

   @Override
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