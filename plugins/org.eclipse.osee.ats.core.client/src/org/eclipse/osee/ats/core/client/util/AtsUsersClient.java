/*
 * Created on Feb 28, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.core.model.IAtsUser;
import org.eclipse.osee.ats.core.users.AtsUsers;
import org.eclipse.osee.framework.core.data.IUserToken;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class AtsUsersClient {

   public static IAtsUser currentUser;

   public static void start() throws OseeCoreException {
      for (User user : UserManager.getUsersAll()) {
         AtsUsers.addUser(getUserFromOseeUser(user));
      }
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
      IAtsUser atsUser = AtsUsers.getUser(userId);
      if (atsUser == null) {
         atsUser = new AtsUser(UserManager.getUserByUserId(userId));
         AtsUsers.addUser(atsUser);
      }
      return atsUser;
   }

   public static IAtsUser getUserFromOseeUser(User user) throws OseeCoreException {
      if (user != null) {
         return getUser(user.getUserId());
      }
      return null;
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

   public static Collection<IAtsUser> getUsers(List<? extends Artifact> artifacts) throws OseeCoreException {
      List<IAtsUser> users = new LinkedList<IAtsUser>();
      for (Artifact userId : artifacts) {
         if (userId instanceof User) {
            users.add(getUserFromOseeUser((User) userId));
         }
      }
      return users;
   }

   public static Collection<User> getOseeUsers(Collection<? extends IAtsUser> users) throws OseeCoreException {
      List<User> results = new LinkedList<User>();
      for (IAtsUser user : users) {
         results.add(AtsUsersClient.getOseeUser(user));
      }
      return results;
   }

   public static Collection<IAtsUser> getAtsUsers(Collection<User> users) throws OseeCoreException {
      List<IAtsUser> results = new LinkedList<IAtsUser>();
      for (User user : users) {
         IAtsUser userByUserId = AtsUsers.getUser(user.getUserId());
         if (userByUserId == null) {
            AtsUsers.addUser(getUserFromOseeUser(user));
            userByUserId = AtsUsers.getUser(user.getUserId());
            if (userByUserId == null) {
               throw new OseeCoreException("Can not access IAtsUser from User [%s]", user.toStringWithId());
            }
         }
         results.add(userByUserId);
      }
      return results;
   }

   public static User getOseeUser() throws OseeCoreException {
      return getOseeUser(getUser());
   }

   public static IAtsUser getUserByName(String name) throws OseeCoreException {
      return getUserFromOseeUser(UserManager.getUserByName(name));
   }

}
