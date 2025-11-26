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
public class UserActivityStatus extends OseeEnum {

   private static final Long ENUM_ID = 38853028L;

   public static UserActivityStatus Not_Set = new UserActivityStatus(11L, "Not_Set");
   public static UserActivityStatus No_Use_Detected = new UserActivityStatus(33L, "No_Use_Detected");
   public static UserActivityStatus Only_Days_Since_Ide_Found =
      new UserActivityStatus(44L, "Only_Days_Since_Ide_Found");
   public static UserActivityStatus Only_Days_Since_Txs_Found =
      new UserActivityStatus(55L, "Only_Days_Since_Txs_Found");
   public static UserActivityStatus Both_Days_Found_Ide_Is_Less =
      new UserActivityStatus(66, "Both_Days_Found_Ide_Is_Less");
   public static UserActivityStatus Both_Days_Found_Txs_Is_Less =
      new UserActivityStatus(77, "Both_Days_Found_Txs_Is_Less");

   public UserActivityStatus(long id, String name) {
      super(ENUM_ID, id, name);
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public OseeEnum getDefault() {
      return Not_Set;
   }

}
