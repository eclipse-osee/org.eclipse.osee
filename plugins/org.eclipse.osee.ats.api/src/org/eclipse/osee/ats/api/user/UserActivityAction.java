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

   // All users should fall into one category below, so none should be this
   public static UserActivityAction Not_Set_Fix_This = new UserActivityAction(11L, "(Fix This) - Not_Set");

   // OSEE_TXS_DETAILS author_id isn't a valid user in the db
   public static UserActivityAction Invalid_Txs_Author_Fix_This =
      new UserActivityAction(22L, "(Fix This) - Invalid_Txs_Author");
   public static UserActivityAction Invalid_Activity_Account_Id_Fix_This =
      new UserActivityAction(1000L, "(Fix This) - Invalid_ActivityTxs_Account_Id");

   // Set inactive cases
   public static UserActivityAction Set_Inactive_Cause_Unused =
      new UserActivityAction(55L, "(On Persist) - Set_Inactive_Cause_Unused");
   public static UserActivityAction Set_Inactive_Cause_Left =
      new UserActivityAction(66L, "(On Persist) - Set_Inactive_Cause_Left");

   // Send inactivity notifications and mark in user art
   public static UserActivityAction Send_First_Unused_Notification =
      new UserActivityAction(77L, "(On Persist) - Send_First_Unused_Notification");
   public static UserActivityAction Send_Second_Unused_Notification =
      new UserActivityAction(88L, "(On Persist) - Send_Second_Unused_Notification");

   // Ignore cases
   public static UserActivityAction Ignore_Active_Cause_Recent_Use =
      new UserActivityAction(33L, "(No Change) - Ignore_Active_Cause_Recent_Use");
   public static UserActivityAction Ignore_Already_InActive_In_OSEE =
      new UserActivityAction(44L, "(No Change) - Ignore_Already_InActive_In_OSEE");
   public static UserActivityAction Ignore_System_User = new UserActivityAction(99L, "Ignore_System_User");
   // Static Id attribute of User includes ignore tag
   public static UserActivityAction Ignore_By_Static_Id = new UserActivityAction(1111L, "Ignore_By_Static_Id");
   // Don't update name based on corporate records
   public static UserActivityAction Ignore_Name_Update = new UserActivityAction(2222L, "Ignore_Name_Update");
   /**
    * Where additional User artifact was created to handle different login-ids before login_id was 0..n. This could be
    * resolved, removed by User data cleanup
    */
   public static UserActivityAction Ignore_Duplicate_Names = new UserActivityAction(3333L, "Ignore_Duplicate_Names");

   public UserActivityAction(long id, String name) {
      super(ENUM_ID, id, name);
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

}
