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
   ARCHIVED(1), UNARCHIVED(0), ALL(-1);

   private final int value;

   BranchArchivedState(int value) {
      this.value = value;
   }

   public final int getValue() {
      return value;
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

   public static BranchArchivedState fromBoolean(boolean isArchived) {
      return isArchived ? ARCHIVED : UNARCHIVED;
   }

   public static BranchArchivedState valueOf(int value) {
      return ARCHIVED.getValue() != value ? UNARCHIVED : ARCHIVED;
   }
}
