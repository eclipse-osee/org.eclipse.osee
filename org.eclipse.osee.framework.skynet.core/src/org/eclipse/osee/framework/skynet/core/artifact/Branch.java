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
import java.util.Arrays;
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
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeType;
import org.eclipse.osee.framework.skynet.core.types.BranchCache;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.skynet.core.types.field.AliasesField;
import org.eclipse.osee.framework.skynet.core.types.field.AssociatedArtifactField;
import org.eclipse.osee.framework.skynet.core.types.field.OseeField;

/**
 * @author Roberto E. Escobar
 */
public class Branch extends AbstractOseeType implements Comparable<Branch>, IAccessControllable, IAdaptable {
   private static final int SHORT_NAME_LIMIT = 25;

   public static final String BRANCH_TYPE_FIELD_KEY = "osee.branch.type.field";
   public static final String BRANCH_STATE_FIELD_KEY = "osee.branch.state.field";
   public static final String BRANCH_ARCHIVED_STATE_FIELD_KEY = "osee.branch.archived.state.field";
   public static final String BRANCH_ASSOCIATED_ARTIFACT_FIELD_KEY = "osee.branch.associated.artifact.field";
   public static final String BRANCH_ALIASES_FIELD_KEY = "osee.branch.aliases.field";

   public Branch(AbstractOseeCache<Branch> cache, String guid, String name, BranchType branchType, BranchState branchState, boolean isArchived) {
      super(cache, guid, name);
      setFieldLogException(BRANCH_TYPE_FIELD_KEY, branchType);
      setFieldLogException(BRANCH_STATE_FIELD_KEY, branchState);
      setFieldLogException(BRANCH_ARCHIVED_STATE_FIELD_KEY, BranchArchivedState.fromBoolean(isArchived));
   }

   @Override
   protected void initializeFields() {
      addField(BRANCH_TYPE_FIELD_KEY, new OseeField<BranchType>());
      addField(BRANCH_STATE_FIELD_KEY, new OseeField<BranchState>());
      addField(BRANCH_ARCHIVED_STATE_FIELD_KEY, new OseeField<BranchArchivedState>());
      addField(BRANCH_ASSOCIATED_ARTIFACT_FIELD_KEY, new AssociatedArtifactField(getCache(), this));
      addField(BRANCH_ALIASES_FIELD_KEY, new AliasesField(getCache(), this));
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
      return getFieldValueLogException(null, BRANCH_TYPE_FIELD_KEY);
   }

   public BranchState getBranchState() {
      return getFieldValueLogException(null, BRANCH_STATE_FIELD_KEY);
   }

   public BranchArchivedState getArchiveState() {
      return getFieldValueLogException(null, BRANCH_ARCHIVED_STATE_FIELD_KEY);
   }

   public IArtifact getAssociatedArtifact() throws OseeCoreException {
      return getFieldValue(BRANCH_ASSOCIATED_ARTIFACT_FIELD_KEY);
   }

   public void setAssociatedArtifact(IArtifact artifact) throws OseeCoreException {
      setField(BRANCH_ASSOCIATED_ARTIFACT_FIELD_KEY, artifact);
   }

   public TransactionId getBaseTransaction() throws OseeCoreException {
      return getCache().getBaseTransaction(this);
   }

   public TransactionId getSourceTransaction() throws OseeCoreException {
      return getCache().getSourceTransaction(this);
   }

   public Collection<String> getAliases() throws OseeCoreException {
      return getFieldValue(BRANCH_ALIASES_FIELD_KEY);
   }

   public void setAliases(String... alias) throws OseeCoreException {
      setField(BRANCH_ALIASES_FIELD_KEY, Arrays.asList(alias));
   }

   public void setArchived(boolean isArchived) {
      BranchArchivedState newValue = BranchArchivedState.fromBoolean(isArchived);
      setFieldLogException(BRANCH_ARCHIVED_STATE_FIELD_KEY, newValue);
   }

   public void setBranchState(BranchState branchState) {
      setFieldLogException(BRANCH_STATE_FIELD_KEY, branchState);
   }

   public void setBranchType(BranchType branchType) {
      setFieldLogException(BRANCH_TYPE_FIELD_KEY, branchType);
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
      for (Branch branch : getCache().getAll()) {
         if (branch.getArchiveState().isUnArchived() && //
         branch.getBranchType().isOfType(BranchType.WORKING) && //
         this.equals(branch.getParentBranch())) {
            branches.add(branch);
         }
      }
      return branches;
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