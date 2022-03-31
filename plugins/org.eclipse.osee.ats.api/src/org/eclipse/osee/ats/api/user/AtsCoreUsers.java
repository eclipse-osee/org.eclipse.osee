/*********************************************************************
 * Copyright (c) 2013 Boeing
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

import org.eclipse.osee.framework.core.enums.SystemUser;

/**
 * @author Donald G. Dunne
 */
public final class AtsCoreUsers {

   public static final AtsUser SYSTEM_USER = new AtsUser(SystemUser.OseeSystem);
   public static final AtsUser UNASSIGNED_USER = new AtsUser(SystemUser.UnAssigned);

   private AtsCoreUsers() {
      // UtilityClass
   }

   public static boolean isSystemUser(AtsUser user) {
      return SYSTEM_USER.equals(user);
   }

   public static boolean isUnAssignedUser(AtsUser user) {
      return UNASSIGNED_USER.equals(user);
   }

   public static boolean isAtsCoreUser(AtsUser user) {
      return SYSTEM_USER.equals(user) || UNASSIGNED_USER.equals(user);
   }

   public static AtsUser getAtsCoreUserByUserId(String userId) {
      AtsUser toReturn = null;
      if (SystemUser.OseeSystem.getUserId().equals(userId)) {
         toReturn = AtsCoreUsers.SYSTEM_USER;
      } else if (SystemUser.UnAssigned.toString().equals(userId)) {
         toReturn = AtsCoreUsers.UNASSIGNED_USER;
      }
      return toReturn;
   }

}