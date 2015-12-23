/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.users;

import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.framework.core.enums.SystemUser;

/**
 * @author Donald G. Dunne
 */
public final class AtsCoreUsers {

   public static final org.eclipse.osee.ats.core.users.SystemUser SYSTEM_USER =
      new org.eclipse.osee.ats.core.users.SystemUser();
   public static final Anonymous ANONYMOUS_USER = new Anonymous();
   public static final UnAssigned UNASSIGNED_USER = new UnAssigned();

   private AtsCoreUsers() {
      // UtilityClass
   }

   public static boolean isSystemUser(IAtsUser user) {
      return SYSTEM_USER.equals(user);
   }

   public static boolean isGuestUser(IAtsUser user) {
      return ANONYMOUS_USER.equals(user);
   }

   public static boolean isUnAssignedUser(IAtsUser user) {
      return UNASSIGNED_USER.equals(user);
   }

   public static boolean isAtsCoreUserId(String userId) {
      return getAtsCoreUserByUserId(userId) != null;
   }

   public static boolean isAtsCoreUser(IAtsUser user) {
      return SYSTEM_USER.equals(user) || ANONYMOUS_USER.equals(user) || UNASSIGNED_USER.equals(user);
   }

   public static IAtsUser getAtsCoreUserByUserId(String userId) {
      IAtsUser toReturn = null;
      if (SystemUser.OseeSystem.getUserId().equals(userId)) {
         toReturn = AtsCoreUsers.SYSTEM_USER;
      } else if (SystemUser.Anonymous.getUserId().equals(userId)) {
         toReturn = AtsCoreUsers.ANONYMOUS_USER;
      } else if (SystemUser.UnAssigned.equals(userId)) {
         toReturn = AtsCoreUsers.UNASSIGNED_USER;
      }
      return toReturn;
   }

}
