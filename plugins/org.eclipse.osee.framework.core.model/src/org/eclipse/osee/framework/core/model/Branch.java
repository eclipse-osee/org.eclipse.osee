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
import java.util.function.Predicate;
import org.eclipse.osee.framework.core.data.Adaptable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class Branch extends NamedIdBase implements IOseeBranch, Adaptable {

   private final Set<Branch> childBranches = new HashSet<>();
   private BranchType branchType;
   private BranchState branchState;
   private boolean isArchived;
   private boolean inheritsAccessControl;
   private TransactionRecord parentTx;
   private TransactionRecord baselineTx;
   private Branch parent;
   private ArtifactId associatedArtifact;
   private ArtifactId branchView;

   public Branch(Long uuid, String name, BranchType branchType, BranchState branchState, boolean isArchived, boolean inheritsAccessControl) {
      this(uuid, name, branchType, branchState, isArchived, inheritsAccessControl, ArtifactId.SENTINEL);
   }

   public Branch(Long uuid, String name, BranchType branchType, BranchState branchState, boolean isArchived, boolean inheritsAccessControl, ArtifactId viewId) {
      super(uuid, name);
      this.branchType = branchType;
      this.branchState = branchState;
      this.isArchived = isArchived;
      this.inheritsAccessControl = inheritsAccessControl;
      this.branchView = viewId;
   }

   public static Branch createBranchView(Branch branch, ArtifactId viewId, String name) {
      Branch viewBranch = new Branch(branch.getId(), branch.getName(), branch.getBranchType(), branch.getBranchState(),
         branch.isArchived, true);
      viewBranch.setAssociatedArtifact(branch.getAssociatedArtifactId());
      viewBranch.setBaseTransaction(branch.getBaseTransaction());
      viewBranch.setBranchView(viewId);
      viewBranch.setParentBranch(branch.getParentBranch());
      viewBranch.setName(name);

      return viewBranch;
   }

   public Branch getParentBranch()  {
      return parent;
   }

   public BranchType getBranchType() {
      return branchType;
   }

   public BranchState getBranchState() {
      return branchState;
   }

   public boolean isArchived() {
      return isArchived;
   }

   public ArtifactId getAssociatedArtifactId() {
      return associatedArtifact;
   }

   public void setAssociatedArtifact(ArtifactId artifact) {
      this.associatedArtifact = artifact;
   }

   public TransactionRecord getBaseTransaction()  {
      return baselineTx;
   }

   public TransactionRecord getSourceTransaction()  {
      return parentTx;
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

   public void setParentBranch(Branch parentBranch)  {
      if (parent != null) {
         parent.childBranches.remove(this);
      }
      parent = parentBranch;
      parentBranch.childBranches.add(this);
   }

   public void setBaseTransaction(TransactionRecord baselineTx)  {
      this.baselineTx = baselineTx;
   }

   public void setSourceTransaction(TransactionRecord parentTx)  {
      this.parentTx = parentTx;
   }

   public boolean isInheritAccessControl() {
      return inheritsAccessControl;
   }

   public void setInheritAccessControl(boolean inheritsAccessControl) {
      this.inheritsAccessControl = inheritsAccessControl;
   }

   public Set<Branch> getChildren()  {
      return childBranches;
   }

   public ArtifactId getBranchView() {
      return branchView;
   }

   public void setBranchView(ArtifactId branchView) {
      this.branchView = branchView;
   }

   /**
    * @return all child branches. It is equivalent to calling getChildBranches with new BranchFilter() (.i.e no child
    * branches are excluded)
    * 
    */
   public Collection<Branch> getAllChildBranches(boolean recurse)  {
      Set<Branch> children = new HashSet<>();
      getChildBranches(children, recurse, b -> true);
      return children;
   }

   public void getChildBranches(Collection<Branch> children, boolean recurse, Predicate<Branch> filter) {
      for (Branch branch : getChildren()) {
         if (filter.test(branch)) {
            children.add(branch);
            if (recurse) {
               branch.getChildBranches(children, recurse, filter);
            }
         }
      }
   }

   public Collection<BranchId> getAncestors()  {
      List<BranchId> ancestors = new ArrayList<>();
      Branch branchCursor = this;
      ancestors.add(branchCursor);
      while ((branchCursor = branchCursor.parent) != null) {
         ancestors.add(branchCursor);
      }
      return ancestors;
   }

   public boolean isAncestorOf(BranchId branch)  {
      return getAllChildBranches(true).contains(branch);
   }

   public boolean hasAncestor(BranchId ancestor) {
      Branch branchCursor = this;
      while ((branchCursor = branchCursor.parent) != null) {
         if (branchCursor.equals(ancestor)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof BranchId) {
         return super.equals(obj) && branchView.equals(((BranchId) obj).getViewId());
      }

      return false;
   }

   @Override
   public ArtifactId getViewId() {
      return branchView;
   }

   /*
    * Provide easy way to display/report [guid][name]
    */
   public final String toStringWithId() {
      return String.format("[%s][%s]", getName(), getId());
   }

}