/*
 * Created on Feb 28, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.users;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsUsers {

   public static Map<String, IAtsUser> userIdToUser = new HashMap<String, IAtsUser>();

   public static boolean isSystemUser(IAtsUser user) {
      return SystemUser.instance.equals(user);
   }

   public static boolean isGuestUser(IAtsUser user) {
      return Guest.instance.equals(user);
   }

   public static boolean isUnAssignedUser(IAtsUser user) {
      return UnAssigned.instance.equals(user);
   }

   public static List<IAtsUser> toList(Collection<? extends IAtsUser> users) {
      List<IAtsUser> results = new LinkedList<IAtsUser>();
      for (IAtsUser user : users) {
         results.add(user);
      }
      return results;
   }

   public static IAtsUser getUser(String userId) {
      if (userId == null) {
         return null;
      }
      IAtsUser user = userIdToUser.get(userId);
      if (user == null) {
         if (userId.equals(SystemUser.instance.getUserId())) {
            user = SystemUser.instance;
         } else if (userId.equals(Guest.instance.getUserId())) {
            user = Guest.instance;
         } else if (userId.equals(UnAssigned.instance.getUserId())) {
            user = UnAssigned.instance;
         }
      }
      return user;
   }

   public static IAtsUser getGuestUser() {
      return Guest.instance;
   }

   public static Collection<IAtsUser> getUsersByUserIds(Collection<String> userIds) {
      List<IAtsUser> users = new LinkedList<IAtsUser>();
      for (String userId : userIds) {
         IAtsUser user = getUser(userId);
         if (user != null) {
            users.add(user);
         }
      }
      return users;
   }

   public static IAtsUser getSystemUser() {
      return SystemUser.instance;
   }

   public static IAtsUser getUnAssigned() {
      return UnAssigned.instance;
   }

   public static Collection<IAtsUser> getValidEmailUsers(Collection<? extends IAtsUser> users) throws OseeCoreException {
      Set<IAtsUser> validUsers = new HashSet<IAtsUser>();
      for (IAtsUser user : users) {
         if (isEmailValid(user.getEmail())) {
            validUsers.add(user);
         }
      }
      return validUsers;
   }
   private static Pattern addressPattern = Pattern.compile(".+?@.+?\\.[a-z]+");

   public static boolean isEmailValid(String email) {
      if (Strings.isValid(email)) {
         return addressPattern.matcher(email).matches();
      }
      return false;
   }

   public static Collection<IAtsUser> getActiveEmailUsers(Collection<? extends IAtsUser> users) throws OseeCoreException {
      Set<IAtsUser> activeUsers = new HashSet<IAtsUser>();
      for (IAtsUser user : users) {
         if (user.isActive()) {
            activeUsers.add(user);
         }
      }
      return activeUsers;
   }

   public static void addUser(IAtsUser user) throws OseeCoreException {
      if (user == null) {
         throw new OseeArgumentException("User can't be null");
      }
      userIdToUser.put(user.getUserId(), user);
   }

   public static void clearCache() {
      userIdToUser.clear();
   }

   public static Collection<IAtsUser> getUsers() {
      return userIdToUser.values();
   }
}
