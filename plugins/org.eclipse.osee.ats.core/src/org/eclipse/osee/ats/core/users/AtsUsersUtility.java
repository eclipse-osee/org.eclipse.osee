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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.user.IAtsUser;
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

   public static Collection<IAtsUser> getValidEmailUsers(Collection<? extends IAtsUser> users) {
      Set<IAtsUser> validUsers = new HashSet<>();
      for (IAtsUser user : users) {
         if (isEmailValid(user.getEmail())) {
            validUsers.add(user);
         }
      }
      return validUsers;
   }

   public static Collection<IAtsUser> getActiveEmailUsers(Collection<? extends IAtsUser> users) {
      Set<IAtsUser> activeUsers = new HashSet<>();
      for (IAtsUser user : users) {
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

   public static Collection<? extends IAtsUser> getUsers(Collection<String> userIds, IAtsUserService userService) {
      Set<IAtsUser> users = new HashSet<>();
      for (String userId : userIds) {
         users.add(userService.getUserById(userId));
      }
      return users;
   }
}
