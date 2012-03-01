/*
 * Created on Feb 28, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.model.IAtsUser;
import org.eclipse.osee.framework.core.data.IUserToken;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.EmailUtil;

public class AtsUsers {

   public static Map<String, IAtsUser> userIdToUser = new HashMap<String, IAtsUser>();
   public static IAtsUser currentUser;

   public static boolean isOseeSystemUser(IAtsUser user) throws OseeCoreException {
      return getOseeSystemUser().getUserId().equals(user.getUserId());
   }

   public static boolean isGuestUser(IAtsUser user) throws OseeCoreException {
      return getGuestUser().getUserId().equals(user.getUserId());
   }

   public static boolean isUnAssignedUser(IAtsUser user) throws OseeCoreException {
      return getUnAssigned().getUserId().equals(user.getUserId());
   }

   public static Collection<? extends User> toOseeUsers(Collection<? extends IAtsUser> users) throws OseeCoreException {
      List<User> results = new LinkedList<User>();
      for (IAtsUser user : users) {
         results.add(getOseeUser(user));
      }
      return results;
   }

   public static User getOseeUser(IAtsUser user) throws OseeCoreException {
      return UserManager.getUserByUserId(user.getUserId());
   }

   public static IAtsUser getUser(String userId) throws OseeCoreException {
      if (userId == null) {
         return null;
      }
      IAtsUser atsUser = userIdToUser.get(userId);
      if (atsUser == null) {
         atsUser = new AtsUser(UserManager.getUserByUserId(userId));
         userIdToUser.put(userId, atsUser);
      }
      return atsUser;
   }

   public static IAtsUser getUserFromOseeUser(User user) throws OseeCoreException {
      return getUser(user.getUserId());
   }

   public static IAtsUser getUser() throws OseeCoreException {
      if (currentUser == null) {
         currentUser = getUserFromOseeUser(UserManager.getUser());
      }
      return currentUser;
   }

   public static IAtsUser getUserFromToken(IUserToken token) throws OseeCoreException {
      return getUser(token.getUserId());
   }

   public static IAtsUser getUserByUserId(String userId) throws OseeCoreException {
      return getUser(userId);
   }

   public static IAtsUser getGuestUser() throws OseeCoreException {
      return getUser(SystemUser.Guest.getUserId());
   }

   public static Collection<IAtsUser> getUsersByUserIds(Collection<String> userIds) throws OseeCoreException {
      List<IAtsUser> users = new LinkedList<IAtsUser>();
      for (String userId : userIds) {
         users.add(getUser(userId));
      }
      return users;
   }

   public static IAtsUser getOseeSystemUser() throws OseeCoreException {
      return getUser(SystemUser.OseeSystem.getUserId());
   }

   public static Collection<IAtsUser> getUsers(List<? extends Artifact> artifacts) throws OseeCoreException {
      List<IAtsUser> users = new LinkedList<IAtsUser>();
      for (Artifact userId : artifacts) {
         if (userId instanceof User) {
            users.add(getUserFromOseeUser((User) userId));
         }
      }
      return users;
   }

   public static IAtsUser getUnAssigned() throws OseeCoreException {
      return getUser(SystemUser.UnAssigned.getUserId());
   }

   public static Collection<User> getOseeUsers(Collection<? extends IAtsUser> users) throws OseeCoreException {
      List<User> results = new LinkedList<User>();
      for (IAtsUser user : users) {
         results.add(AtsUsers.getOseeUser(user));
      }
      return results;
   }

   public static Collection<IAtsUser> getAtsUsers(Collection<User> users) throws OseeCoreException {
      List<IAtsUser> results = new LinkedList<IAtsUser>();
      for (User user : users) {
         results.add(AtsUsers.getUserByUserId(user.getUserId()));
      }
      return results;
   }

   public static User getOseeUser() throws OseeCoreException {
      return getOseeUser(getUser());
   }

   public static IAtsUser getUserByName(String name) throws OseeCoreException {
      return getUserFromOseeUser(UserManager.getUserByName(name));
   }

   public static Collection<IAtsUser> getValidEmailUsers(Collection<? extends IAtsUser> users) {
      Set<IAtsUser> validUsers = new HashSet<IAtsUser>();
      for (IAtsUser user : users) {
         try {
            if (EmailUtil.isEmailValid(user.getEmail())) {
               validUsers.add(user);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return validUsers;
   }

   public static Collection<IAtsUser> getActiveEmailUsers(Collection<? extends IAtsUser> users) {
      Set<IAtsUser> activeUsers = new HashSet<IAtsUser>();
      for (IAtsUser user : users) {
         try {
            if (user.isActive()) {
               activeUsers.add(user);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return activeUsers;
   }

}
