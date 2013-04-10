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

/**
 * @author Donald G. Dunne
 */
public final class AtsCoreUsers {

   public static final SystemUser SYSTEM_USER = new SystemUser();
   public static final Guest GUEST_USER = new Guest();
   public static final UnAssigned UNASSIGNED_USER = new UnAssigned();

   private AtsCoreUsers() {
      // UtilityClass
   }

   public static boolean isSystemUser(IAtsUser user) {
      return SYSTEM_USER.equals(user);
   }

   public static boolean isGuestUser(IAtsUser user) {
      return GUEST_USER.equals(user);
   }

   public static boolean isUnAssignedUser(IAtsUser user) {
      return UNASSIGNED_USER.equals(user);
   }

   public static boolean isAtsCoreUserId(String userId) {
      return getAtsCoreUserByUserId(userId) != null;
   }

   public static boolean isAtsCoreUser(IAtsUser user) {
      return SYSTEM_USER.equals(user) || GUEST_USER.equals(user) || UNASSIGNED_USER.equals(user);
   }

   public static IAtsUser getAtsCoreUserByUserId(String userId) {
      IAtsUser toReturn = null;
      if (SYSTEM_USER.getUserId().equals(userId)) {
         toReturn = AtsCoreUsers.SYSTEM_USER;
      } else if (GUEST_USER.getUserId().equals(userId)) {
         toReturn = AtsCoreUsers.GUEST_USER;
      } else if (UNASSIGNED_USER.getUserId().equals(userId)) {
         toReturn = AtsCoreUsers.UNASSIGNED_USER;
      }
      return toReturn;
   }

}
