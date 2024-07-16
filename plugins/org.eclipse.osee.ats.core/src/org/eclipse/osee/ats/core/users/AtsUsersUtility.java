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

package org.eclipse.osee.ats.core.users;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public final class AtsUsersUtility {

   private static final Pattern addressPattern = Pattern.compile(".+?@.+?\\.[a-z]+");

   private AtsUsersUtility() {
      // UtilityClass
   }

   public static Collection<AtsUser> getValidEmailUsers(Collection<AtsUser> users) {
      Set<AtsUser> validUsers = new HashSet<>();
      for (AtsUser user : users) {
         if (isEmailValid(user.getEmail())) {
            validUsers.add(user);
         }
      }
      return validUsers;
   }

   public static Collection<AtsUser> getActiveEmailUsers(Collection<AtsUser> users) {
      Set<AtsUser> activeUsers = new HashSet<>();
      for (AtsUser user : users) {
         if (user.isActive()) {
            activeUsers.add(user);
         }
      }
      return activeUsers;
   }

   public static boolean isEmailValid(String email) {
      if (Strings.isValid(email)) {
         return addressPattern.matcher(email).matches();
      }
      return false;
   }

   public static Collection<AtsUser> getUsers(Collection<String> userIds, IAtsUserService userService) {
      Set<AtsUser> users = new HashSet<>();
      for (String userId : userIds) {
         users.add(userService.getUserByUserId(userId));
      }
      return users;
   }
}
