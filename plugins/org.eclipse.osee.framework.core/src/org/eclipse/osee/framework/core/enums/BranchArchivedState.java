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
 * @author Ryan D. Brooks
 */
public enum BranchArchivedState {
   ARCHIVED(1),
   UNARCHIVED(0),
   ALL(-1),
   ARCHIVED_IN_PROGRESS(2),
   UNARCHIVED_IN_PROGRESS(3);

   private final int value;

   BranchArchivedState(int value) {
      this.value = value;
   }

   public final int getValue() {
      return value;
   }

   public boolean isBeingArchived() {
      return this == ARCHIVED_IN_PROGRESS;
   }

   public boolean isBeingUnarchived() {
      return this == UNARCHIVED_IN_PROGRESS;
   }

   public boolean isArchived() {
      return this == ARCHIVED;
   }

   public boolean isUnArchived() {
      return this == UNARCHIVED;
   }

   public boolean matches(BranchArchivedState branchState) {
      return branchState == BranchArchivedState.ALL || this == branchState;
   }

   /**
    * @return mapping to BranchArchivedState subset.
    * <p>
    * <code> true == BranchArchivedState.ARCHIVED<br/>
    * false == BranchArchivedState.UNARCHIVED <code>
    * </p>
    */
   public static BranchArchivedState fromBoolean(boolean archived) {
      return archived ? ARCHIVED : UNARCHIVED;
   }

   public static BranchArchivedState valueOf(int value) {
      switch (value) {
         case 1:
            return ARCHIVED;
         case 2:
            return ARCHIVED_IN_PROGRESS;
         case 3:
            return UNARCHIVED_IN_PROGRESS;
         case -1:
         case 0:
         default:
            return UNARCHIVED;
      }
   }
}
