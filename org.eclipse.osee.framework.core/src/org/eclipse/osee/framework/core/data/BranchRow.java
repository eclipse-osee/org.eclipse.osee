package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.ModificationType;

public final class BranchRow {
   private final int branchId;
   private final String branchGuid;

   private final String branchName;
   private final BranchType branchType;
   private final BranchState branchState;
   private final BranchArchivedState branchArchived;
   private final ModificationType modType;

   public BranchRow(int branchId, String branchGuid, String branchName, BranchType branchType, BranchState branchState, BranchArchivedState branchArchived, ModificationType modType) {
      super();
      this.branchId = branchId;
      this.branchGuid = branchGuid;
      this.branchName = branchName;
      this.branchType = branchType;
      this.branchState = branchState;
      this.branchArchived = branchArchived;
      this.modType = modType;
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

   public ModificationType getModType() {
      return modType;
   }

   public String[] toArray() {
      return new String[] {getBranchArchived().name(), getBranchGuid(), String.valueOf(getBranchId()), getBranchName(),
            getBranchState().name(), getBranchType().name(), getModType().name()};
   }

   public static BranchRow fromArray(String[] data) {
      BranchArchivedState archived = BranchArchivedState.valueOf(data[0]);
      String branchGuid = data[1];
      int branchId = Integer.valueOf(data[2]);
      String branchName = data[3];
      BranchState branchState = BranchState.valueOf(data[4]);
      BranchType branchType = BranchType.valueOf(data[5]);
      ModificationType modType = ModificationType.valueOf(data[6]);

      return new BranchRow(branchId, branchGuid, branchName, branchType, branchState, archived, modType);
   }
}