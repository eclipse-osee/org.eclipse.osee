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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.message.internal.DatabaseService;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public final class BranchRow {
   private final long branchUuid;

   private final String branchName;
   private final BranchType branchType;
   private final BranchState branchState;
   private final BranchArchivedState branchArchived;
   private StorageState storageState;
   private final boolean inheritAccessControl;

   // TODO remove
   public void setStorageState(StorageState storageState) {
      this.storageState = storageState;
   }

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
      if (isOseeUsingGuidsForAppServerMessaging()) {
         return new String[] {
            getBranchArchived().name(),
            getBranchGuidLegacy(getBranchId()),
            String.valueOf(getBranchId()),
            getBranchName(),
            getBranchState().name(),
            getBranchType().name(),
            getStorageState().name(),
            Boolean.toString(isInheritAccessControl())};
      } else {
         return new String[] {
            getBranchArchived().name(),
            String.valueOf(getBranchId()),
            getBranchName(),
            getBranchState().name(),
            getBranchType().name(),
            getStorageState().name(),
            Boolean.toString(isInheritAccessControl())};
      }
   }

   public static BranchRow fromArray(String[] data) {
      BranchArchivedState archived = BranchArchivedState.valueOf(data[0]);
      long branchUuid = 0;
      boolean inheritAccessControl = false;
      String branchName = null;
      BranchState branchState = null;
      BranchType branchType = null;
      StorageState storageState = null;
      if (GUID.isValid(data[1])) {
         // skip guid
         branchUuid = Long.valueOf(data[2]);
         branchName = data[3];
         branchState = BranchState.valueOf(data[4]);
         branchType = BranchType.valueOf(data[5]);
         storageState = StorageState.valueOf(data[6]);
      } else {
         branchUuid = Long.valueOf(data[1]);
         branchName = data[2];
         branchState = BranchState.valueOf(data[3]);
         branchType = BranchType.valueOf(data[4]);
         storageState = StorageState.valueOf(data[5]);
         inheritAccessControl = Boolean.parseBoolean(data[6]);
      }
      return new BranchRow(branchUuid, branchName, branchType, branchState, archived, storageState,
         inheritAccessControl);
   }

   // Temporary cache till all code uses branch uuid. Remove after 0.17.0
   private static final String SELECT_OSEE_INFO =
      "select osee_value from osee_info where osee_key = 'osee.using.guids.for.app.server.messaging'";
   private static Boolean oseeUsingGuidsForAppServerMessaging = null;

   public static boolean isOseeUsingGuidsForAppServerMessaging() {
      if (oseeUsingGuidsForAppServerMessaging == null) {
         oseeUsingGuidsForAppServerMessaging =
            DatabaseService.getDatabaseService().runPreparedQueryFetchObject("", SELECT_OSEE_INFO).equals("true");
      }
      return oseeUsingGuidsForAppServerMessaging;
   }

   private static final String SELECT_BRANCH_GUID_BY_ID = "select branch_guid from osee_branch where branch_id = ?";
   // Temporary cache till all code uses branch uuid. Remove after 0.17.0
   private static final Map<Long, String> longToGuidCache = new HashMap<Long, String>(50);

   /**
    * Temporary method till all code uses branch uuid. Remove after 0.17.0
    */
   public static String getBranchGuidLegacy(long branchUuid) {
      String guid = longToGuidCache.get(branchUuid);
      if (!Strings.isValid(guid)) {
         guid =
            DatabaseService.getDatabaseService().runPreparedQueryFetchObject("", SELECT_BRANCH_GUID_BY_ID, branchUuid);
         Conditions.checkExpressionFailOnTrue(!Strings.isValid(guid), "Error getting branch_guid for branch: [%d]",
            branchUuid);
         longToGuidCache.put(branchUuid, guid);
      }
      return guid;
   }

   // Temporary cache till all code uses branch uuid. Remove after 0.17.0
   private static final String SELECT_BRANCH_ID_BY_GUID = "select branch_id from osee_branch where branch_guid = ?";
   // Temporary cache till all code uses branch uuid. Remove after 0.17.0
   private static final Map<String, Long> guidToLongCache = new HashMap<String, Long>(50);

   /**
    * Temporary method till all code uses branch uuid. Remove after 0.17.0
    */
   public static long getBranchIdLegacy(String branchGuid) {
      Long longId = guidToLongCache.get(branchGuid);
      if (longId == null) {
         longId =
            DatabaseService.getDatabaseService().runPreparedQueryFetchObject(0L, SELECT_BRANCH_ID_BY_GUID, branchGuid);
         Conditions.checkExpressionFailOnTrue(longId <= 0, "Error getting branch_id for branch: [%s]", branchGuid);
         guidToLongCache.put(branchGuid, longId);
      }
      return longId;
   }

}
