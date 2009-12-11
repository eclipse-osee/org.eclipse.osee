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

package org.eclipse.osee.framework.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.core.data.IAccessControllable;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.internal.fields.AssociatedArtifactField;
import org.eclipse.osee.framework.core.internal.fields.CollectionField;
import org.eclipse.osee.framework.core.internal.fields.OseeField;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class Branch extends AbstractOseeType implements Comparable<Branch>, IAccessControllable, IAdaptable, IOseeBranch {

   private static final int SHORT_NAME_LIMIT = 25;

   private final Collection<Branch> childBranches = new HashSet<Branch>();

   public Branch(String guid, String name, BranchType branchType, BranchState branchState, boolean isArchived) {
      super(guid, name);
      initializeFields();
      setFieldLogException(BranchField.BRANCH_TYPE_FIELD_KEY, branchType);
      setFieldLogException(BranchField.BRANCH_STATE_FIELD_KEY, branchState);
      setFieldLogException(BranchField.BRANCH_ARCHIVED_STATE_FIELD_KEY, BranchArchivedState.fromBoolean(isArchived));
   }

   protected void initializeFields() {
      addField(BranchField.PARENT_BRANCH, new OseeField<Branch>());
      addField(BranchField.BRANCH_BASE_TRANSACTION, new OseeField<TransactionRecord>());
      addField(BranchField.BRANCH_SOURCE_TRANSACTION, new OseeField<TransactionRecord>());
      addField(BranchField.BRANCH_TYPE_FIELD_KEY, new OseeField<BranchType>());
      addField(BranchField.BRANCH_STATE_FIELD_KEY, new OseeField<BranchState>());
      addField(BranchField.BRANCH_ARCHIVED_STATE_FIELD_KEY, new OseeField<BranchArchivedState>());

      addField(BranchField.BRANCH_ASSOCIATED_ARTIFACT_FIELD_KEY, new AssociatedArtifactField(null));
      addField(BranchField.BRANCH_CHILDREN, new CollectionField<Branch>(childBranches));
   }

   public Branch getParentBranch() throws OseeCoreException {
      return getFieldValue(BranchField.PARENT_BRANCH);
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
      return getFieldValueLogException(null, BranchField.BRANCH_TYPE_FIELD_KEY);
   }

   public BranchState getBranchState() {
      return getFieldValueLogException(null, BranchField.BRANCH_STATE_FIELD_KEY);
   }

   public BranchArchivedState getArchiveState() {
      return getFieldValueLogException(null, BranchField.BRANCH_ARCHIVED_STATE_FIELD_KEY);
   }

   public IBasicArtifact<?> getAssociatedArtifact() throws OseeCoreException {
      return getFieldValue(BranchField.BRANCH_ASSOCIATED_ARTIFACT_FIELD_KEY);
   }

   public void setAssociatedArtifact(IBasicArtifact<?> artifact) throws OseeCoreException {
      setField(BranchField.BRANCH_ASSOCIATED_ARTIFACT_FIELD_KEY, artifact);
   }

   public TransactionRecord getBaseTransaction() throws OseeCoreException {
      return getFieldValue(BranchField.BRANCH_BASE_TRANSACTION);
   }

   public TransactionRecord getSourceTransaction() throws OseeCoreException {
      return getFieldValue(BranchField.BRANCH_SOURCE_TRANSACTION);
   }

   public void setArchived(boolean isArchived) {
      BranchArchivedState newValue = BranchArchivedState.fromBoolean(isArchived);
      setFieldLogException(BranchField.BRANCH_ARCHIVED_STATE_FIELD_KEY, newValue);
   }

   public void setBranchState(BranchState branchState) {
      setFieldLogException(BranchField.BRANCH_STATE_FIELD_KEY, branchState);
   }

   public void setBranchType(BranchType branchType) {
      setFieldLogException(BranchField.BRANCH_TYPE_FIELD_KEY, branchType);
   }

   public void setParentBranch(Branch parentBranch) throws OseeCoreException {
      Branch oldParent = getParentBranch();
      if (oldParent != null) {
         oldParent.childBranches.remove(this);
      }
      setField(BranchField.PARENT_BRANCH, parentBranch);
      if (parentBranch != null) {
         parentBranch.childBranches.add(this);
      }
   }

   public void setBaseTransaction(TransactionRecord baseTx) throws OseeCoreException {
      setField(BranchField.BRANCH_BASE_TRANSACTION, baseTx);
   }

   public void setSourceTransaction(TransactionRecord srcTx) throws OseeCoreException {
      setField(BranchField.BRANCH_SOURCE_TRANSACTION, srcTx);
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

   public int compareTo(Branch other) {
      int result = -1;
      if (other != null && other.getName() != null && getName() != null) {
         result = getName().compareTo(other.getName());
      }
      return result;
   }

   public Collection<Branch> getChildren() throws OseeCoreException {
      return getFieldValue(BranchField.BRANCH_CHILDREN);
   }

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
      for (Branch branch : parentBranch.getChildren()) {
         if (branch.getArchiveState().isUnArchived() && !branch.getBranchType().isMergeBranch()) {
            children.add(branch);
            if (recurse) {
               getChildBranches(branch, children, recurse);
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