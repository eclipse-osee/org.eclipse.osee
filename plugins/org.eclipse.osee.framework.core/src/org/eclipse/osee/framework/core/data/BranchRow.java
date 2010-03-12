package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.StorageState;

public final class BranchRow {
   private final int branchId;
   private final String branchGuid;

   private final String branchName;
   private final BranchType branchType;
   private final BranchState branchState;
   private final BranchArchivedState branchArchived;
   private StorageState storageState;

   // TODO remove
   public void setStorageState(StorageState storageState) {
      this.storageState = storageState;
   }

   public BranchRow(int branchId, String branchGuid, String branchName, BranchType branchType, BranchState branchState, BranchArchivedState branchArchived, StorageState storageState) {
      super();
      this.branchId = branchId;
      this.branchGuid = branchGuid;
      this.branchName = branchName;
      this.branchType = branchType;
      this.branchState = branchState;
      this.branchArchived = branchArchived;
      this.storageState = storageState;
   }

   public int getBranchId() {
      return branchId;
   }

   public String getBranchGuid() {
      return branchGuid;
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

   public String[] toArray() {
      return new String[] {getBranchArchived().name(), getBranchGuid(), String.valueOf(getBranchId()), getBranchName(),
            getBranchState().name(), getBranchType().name(), getStorageState().name()};
   }

   public static BranchRow fromArray(String[] data) {
      BranchArchivedState archived = BranchArchivedState.valueOf(data[0]);
      String branchGuid = data[1];
      int branchId = Integer.valueOf(data[2]);
      String branchName = data[3];
      BranchState branchState = BranchState.valueOf(data[4]);
      BranchType branchType = BranchType.valueOf(data[5]);
      StorageState storageState = StorageState.valueOf(data[6]);
      return new BranchRow(branchId, branchGuid, branchName, branchType, branchState, archived, storageState);
   }
}