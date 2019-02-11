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
package org.eclipse.osee.ats.api.user;

import org.eclipse.osee.framework.core.enums.SystemUser;

/**
 * @author Donald G. Dunne
 */
public final class AtsCoreUsers {

   public static final IAtsUser SYSTEM_USER = new AtsUser(SystemUser.OseeSystem);
   public static final IAtsUser ANONYMOUS_USER = new AtsUser(SystemUser.Anonymous);
   public static final IAtsUser UNASSIGNED_USER = new AtsUser(SystemUser.UnAssigned);
   public static final IAtsUser BOOTSTRAP_USER = new AtsUser(SystemUser.BootStrap);

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

   public static boolean isAtsCoreUser(IAtsUser user) {
      return SYSTEM_USER.equals(user) || ANONYMOUS_USER.equals(user) || UNASSIGNED_USER.equals(
         user) || BOOTSTRAP_USER.equals(user);
   }

   public static IAtsUser getAtsCoreUserByUserId(String userId) {
      IAtsUser toReturn = null;
      if (SystemUser.OseeSystem.getUserId().equals(userId)) {
         toReturn = AtsCoreUsers.SYSTEM_USER;
      } else if (SystemUser.Anonymous.getUserId().equals(userId)) {
         toReturn = AtsCoreUsers.ANONYMOUS_USER;
      } else if (SystemUser.UnAssigned.toString().equals(userId)) {
         toReturn = AtsCoreUsers.UNASSIGNED_USER;
      }
      return toReturn;
   }

   public static boolean isBootstrapUser(IAtsUser user) {
      return BOOTSTRAP_USER.equals(user);
   }

}
