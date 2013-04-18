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
package org.eclipse.osee.framework.core.enums;

/**
 * @author Roberto E. Escobar
 */
public enum BranchState {
   CREATED(0),
   MODIFIED(1),
   COMMITTED(2),
   REBASELINED(3),
   DELETED(4),
   REBASELINE_IN_PROGRESS(5),
   COMMIT_IN_PROGRESS(6),
   CREATION_IN_PROGRESS(7),
   DELETE_IN_PROGRESS(8),
   PURGE_IN_PROGRESS(9),
   PURGED(10);

   private final int value;

   BranchState(int value) {
      this.value = value;
   }

   public final int getValue() {
      return value;
   }

   public boolean isCommitInProgress() {
      return this == BranchState.COMMIT_IN_PROGRESS;
   }

   public boolean isCreated() {
      return this == BranchState.CREATED;
   }

   public boolean isModified() {
      return this == BranchState.MODIFIED;
   }

   public boolean isCommitted() {
      return this == BranchState.COMMITTED;
   }

   public boolean isRebaselined() {
      return this == BranchState.REBASELINED;
   }

   public boolean isRebaselineInProgress() {
      return this == BranchState.REBASELINE_IN_PROGRESS;
   }

   public boolean isCreationInProgress() {
      return this == BranchState.CREATION_IN_PROGRESS;
   }

   public boolean isDeleted() {
      return this == BranchState.DELETED;
   }

   public boolean isPurged() {
      return this == BranchState.PURGED;
   }

   public boolean isDeleteInProgress() {
      return this == BranchState.DELETE_IN_PROGRESS;
   }

   public boolean isPurgeInProgress() {
      return this == BranchState.PURGE_IN_PROGRESS;
   }

   public static BranchState getBranchState(int value) {
      for (BranchState type : values()) {
         if (type.getValue() == value) {
            return type;
         }
      }
      return null;
   }

   public boolean matches(BranchState... branchStates) {
      for (BranchState branchState : branchStates) {
         if (this == branchState) {
            return true;
         }
      }
      return false;
   }

}
