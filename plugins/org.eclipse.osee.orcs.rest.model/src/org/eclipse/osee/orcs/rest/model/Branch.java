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
package org.eclipse.osee.orcs.rest.model;

import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class Branch {
   private long branchUuid;
   private String name;
   private BranchId parentBranch;

   private ArtifactId associatedArtifact = ArtifactId.SENTINEL;
   private TransactionId baseTransaction = TransactionId.SENTINEL;
   private TransactionId sourceTransaction = TransactionId.SENTINEL;

   private BranchArchivedState archiveState = BranchArchivedState.UNARCHIVED;
   private BranchState branchState = BranchState.CREATED;
   private BranchType branchType = BranchType.WORKING;
   private boolean inheritAccessControl = false;

   public Branch() {
      super();
   }

   public long getBranchUuid() {
      return branchUuid;
   }

   public void setBranchUuid(long branchUuid) {
      this.branchUuid = branchUuid;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public ArtifactId getAssociatedArtifact() {
      return associatedArtifact;
   }

   public void setAssociatedArtifact(ArtifactId associatedArtifact) {
      this.associatedArtifact = associatedArtifact;
   }

   public TransactionId getBaseTransactionId() {
      return baseTransaction;
   }

   public void setBaseTransactionId(TransactionId baseTx) {
      baseTransaction = baseTx;
   }

   public TransactionId getSourceTransactionId() {
      return sourceTransaction;
   }

   public void setSourceTransactionId(TransactionId sourceTx) {
      sourceTransaction = sourceTx;
   }

   public BranchId getParentBranch() {
      return parentBranch;
   }

   public void setParentBranch(BranchId parentBranch) {
      this.parentBranch = parentBranch;
   }

   public BranchArchivedState getArchiveState() {
      return archiveState;
   }

   public void setArchiveState(BranchArchivedState state) {
      this.archiveState = state;
   }

   public BranchState getBranchState() {
      return branchState;
   }

   public void setBranchState(BranchState state) {
      this.branchState = state;
   }

   public BranchType getBranchType() {
      return branchType;
   }

   public void setBranchType(BranchType type) {
      branchType = type;
   }

   public boolean isInheritAccessControl() {
      return inheritAccessControl;
   }

   public void setInheritAccessControl(boolean inheritAccessControl) {
      this.inheritAccessControl = inheritAccessControl;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (branchUuid ^ branchUuid >>> 32);
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Branch) {
         return branchUuid == ((Branch) obj).branchUuid;
      }
      return false;
   }

   @Override
   public String toString() {
      return "Branch [branchUuid=" + getBranchUuid() + ", name=" + name + ", parentBranchUuid=" + getParentBranch() + ", associatedArtifactId=" + associatedArtifact + ", baseTransaction=" + baseTransaction + ", sourceTransaction=" + sourceTransaction + ", archiveState=" + archiveState + ", branchState=" + branchState + ", branchType=" + branchType + ", inheritAccessControl=" + inheritAccessControl + "]";
   }
}