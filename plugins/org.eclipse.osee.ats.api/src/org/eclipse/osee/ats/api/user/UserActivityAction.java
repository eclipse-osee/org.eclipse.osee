/*********************************************************************
 * Copyright (c) 2025 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.ats.api.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class UserActivityAction extends OseeEnum {

   private static final Long ENUM_ID = 324285298L;

   // @formatter:off

   // All users should fall into one category below, so none should be this
   public static UserActivityAction Not_Set_Fix_This = new UserActivityAction(100L, "(Fix This) - Not_Set", "Every record should be in another group; fix this before persist.");

   // OSEE_TXS_DETAILS author_id isn't a valid user in the db
   public static UserActivityAction Invalid_Txs_Author_Fix_This = new UserActivityAction(200, "(Fix This) - Invalid_Txs_Author", "Invalid in db, fix these.");
   public static UserActivityAction Invalid_Activity_Account_Id_Fix_This = new UserActivityAction(210, "(Fix This) - Invalid_Activity_Account_Id", "Invalid in db, fix these.");

   // Set Inactive cases
   public static UserActivityAction Set_Inactive_Cause_Unused = new UserActivityAction(400, "(On Persist) - Set_Inactive_Cause_Unused", "No transaction or IDE use found within time-frame and user not re-activated; set inactive on persist.");
   public static UserActivityAction Set_Inactive_Cause_Left_Company_No_Record = new UserActivityAction(410, "(On Persist) - Set_Inactive_Cause_Left_Company_No_Record", "Corporate record not found; set inactive on persist.");
   public static UserActivityAction Set_Inactive_Cause_Left_Company_Record_Inactive = new UserActivityAction(420, "(On Persist) - Set_Inactive_Cause_Left_Company_Record_Inactive", "Corporate record shows user left company; set inactive on persist.");

   // Send inactivity notifications and mark in user art
   public static UserActivityAction Send_First_Unused_Notification = new UserActivityAction(500, "(On Persist) - Send_First_Unused_Notification", "First no-usage notification will be sent on persist.");
   public static UserActivityAction Send_Second_Unused_Notification = new UserActivityAction(510, "(On Persist) - Send_Second_Unused_Notification", "Second no-usage notification will be sent on persist.");

   // Ignore cases
   public static UserActivityAction Ignore_System_User = new UserActivityAction(600, "(No Change) - Ignore_System_User", "System User, do nothing");
   public static UserActivityAction Ignore_By_Static_Id = new UserActivityAction(610, "(No Change) - Ignore_By_Static_Id", "User has flag to not sync. eg: shared or service account, do nothing.");
   public static UserActivityAction Ignore_Active_Cause_Recent_Use_Or_Reactivated = new UserActivityAction(620, "(No Change) - Ignore_Active_Cause_Recent_Use_Or_Reactivated", "Active user shows usage or reactivated, do nothing.");
   public static UserActivityAction Ignore_Already_InActive_In_OSEE = new UserActivityAction(630, "(No Change) - Ignore_Already_InActive_In_OSEE", "User already inactive, do nothing.");

   // @formatter:on

   private final String description;

   public UserActivityAction(long id, String name, String description) {
      super(ENUM_ID, id, name);
      this.description = description;
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public OseeEnum getDefault() {
      return Not_Set_Fix_This;
   }

   public String getDescription() {
      return description;
   }

}
