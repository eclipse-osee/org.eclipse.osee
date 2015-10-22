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

import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.orcs.core.ds.BranchData;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.db.internal.sql.RelationalConstants;

/**
 * @author Roberto E. Escobar
 */
public class BranchDataImpl extends OrcsObjectImpl<Long> implements BranchData, BranchReadable {

   private Long uuid = RelationalConstants.DEFAULT_UUID;
   private String name = "";
   private int associatedArtifactId = RelationalConstants.ART_ID_SENTINEL;
   private int baseTransaction = RelationalConstants.TRANSACTION_SENTINEL;
   private int sourceTransaction = RelationalConstants.TRANSACTION_SENTINEL;
   private long parentBranch = RelationalConstants.BRANCH_SENTINEL;
   private BranchArchivedState archiveState = BranchArchivedState.UNARCHIVED;
   private BranchState branchState = BranchState.CREATED;
   private BranchType branchType = BranchType.WORKING;
   private boolean inheritAccessControl = false;

   public BranchDataImpl() {
      super();
   }

   @Override
   public Long getGuid() {
      return getUuid();
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public void setName(String name) {
      this.name = name;
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
      return getParentBranch() != RelationalConstants.BRANCH_SENTINEL;
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
   public int hashCode() {
      return getGuid().hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Identity) {
         Identity<?> id = (Identity<?>) obj;
         return id.getGuid().equals(this.getGuid());
      }
      return false;

   }

   @Override
   public String toString() {
      return "BranchData [uuid=" + uuid + ", " + super.toString() + "]";
   }

   @Override
   public Long getUuid() {
      return uuid;
   }

   @Override
   public void setUuid(Long uuid) {
      this.uuid = uuid;
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
