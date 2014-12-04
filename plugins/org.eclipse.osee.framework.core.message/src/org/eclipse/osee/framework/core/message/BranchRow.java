/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.message;

import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.StorageState;

public final class BranchRow {
   private final long branchUuid;

   private final String branchName;
   private final BranchType branchType;
   private final BranchState branchState;
   private final BranchArchivedState branchArchived;
   private final StorageState storageState;
   private final boolean inheritAccessControl;

   public BranchRow(long branchUuid, String branchName, BranchType branchType, BranchState branchState, BranchArchivedState branchArchived, StorageState storageState, boolean inheritAccessControl) {
      this.branchUuid = branchUuid;
      this.branchName = branchName;
      this.branchType = branchType;
      this.branchState = branchState;
      this.branchArchived = branchArchived;
      this.storageState = storageState;
      this.inheritAccessControl = inheritAccessControl;
   }

   public long getBranchId() {
      return branchUuid;
   }

   public String getBranchName() {
      return branchName;
   }

   public BranchType getBranchType() {
      return branchType;
   }

   public BranchState getBranchState() {
      return branchState;
   }

   public BranchArchivedState getBranchArchived() {
      return branchArchived;
   }

   public StorageState getStorageState() {
      return storageState;
   }

   public boolean isInheritAccessControl() {
      return inheritAccessControl;
   }

   public String[] toArray() {
      return new String[] {
         getBranchArchived().name(),
         String.valueOf(getBranchId()),
         getBranchName(),
         getBranchState().name(),
         getBranchType().name(),
         getStorageState().name(),
         Boolean.toString(isInheritAccessControl())};
   }

   public static BranchRow fromArray(String[] data) {
      BranchArchivedState archived = BranchArchivedState.valueOf(data[0]);
      long branchUuid = Long.valueOf(data[1]);
      String branchName = data[2];
      BranchState branchState = BranchState.valueOf(data[3]);
      BranchType branchType = BranchType.valueOf(data[4]);
      StorageState storageState = StorageState.valueOf(data[5]);
      boolean inheritAccessControl = Boolean.parseBoolean(data[6]);
      return new BranchRow(branchUuid, branchName, branchType, branchState, archived, storageState,
         inheritAccessControl);
   }

}
