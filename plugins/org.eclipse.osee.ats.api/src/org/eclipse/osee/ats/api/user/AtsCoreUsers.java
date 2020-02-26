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

   public static final AtsUser SYSTEM_USER = new AtsUser(SystemUser.OseeSystem);
   public static final AtsUser ANONYMOUS_USER = new AtsUser(SystemUser.Anonymous);
   public static final AtsUser UNASSIGNED_USER = new AtsUser(SystemUser.UnAssigned);
   public static final AtsUser BOOTSTRAP_USER = new AtsUser(SystemUser.BootStrap);

   private AtsCoreUsers() {
      // UtilityClass
   }

   public static boolean isSystemUser(AtsUser user) {
      return SYSTEM_USER.equals(user);
   }

   public static boolean isAnonymousUser(AtsUser user) {
      return ANONYMOUS_USER.equals(user);
   }

   public static boolean isUnAssignedUser(AtsUser user) {
      return UNASSIGNED_USER.equals(user);
   }

   public static boolean isAtsCoreUser(AtsUser user) {
      return SYSTEM_USER.equals(user) || ANONYMOUS_USER.equals(user) || UNASSIGNED_USER.equals(
         user) || BOOTSTRAP_USER.equals(user);
   }

   public static AtsUser getAtsCoreUserByUserId(String userId) {
      AtsUser toReturn = null;
      if (SystemUser.OseeSystem.getUserId().equals(userId)) {
         toReturn = AtsCoreUsers.SYSTEM_USER;
      } else if (SystemUser.Anonymous.getUserId().equals(userId)) {
         toReturn = AtsCoreUsers.ANONYMOUS_USER;
      } else if (SystemUser.UnAssigned.toString().equals(userId)) {
         toReturn = AtsCoreUsers.UNASSIGNED_USER;
      }
      return toReturn;
   }

   public static boolean isBootstrapUser(AtsUser user) {
      return BOOTSTRAP_USER.equals(user);
   }

}
