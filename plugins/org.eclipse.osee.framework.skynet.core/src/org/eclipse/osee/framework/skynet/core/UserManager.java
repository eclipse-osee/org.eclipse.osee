/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.IUserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.UserInDatabaseMultipleTimes;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;

/**
 * @author Roberto E. Escobar
 */
public final class UserManager {

   public static String DOUBLE_CLICK_SETTING_KEY = "onDoubleClickOpenUsingArtifactEditor";

   private static final String CACHE_PREFIX = "userManager.";
   private static boolean userCacheIsLoaded = false;
   private static boolean duringMainUserCreation = false;

   private UserManager() {
      // Utility class
   }

   /**
    * Returns the currently authenticated user
    */
   public static User getUser() throws OseeCoreException {
      return ClientUser.getMainUser();
   }

   public static void releaseUser() {
      ClientUser.releaseUser();
   }

   public static void clearCache() throws OseeCoreException {
      for (Artifact art : ArtifactCache.getArtifactsByType(CoreArtifactTypes.User)) {
         ArtifactCache.deCache(art);
      }
      userCacheIsLoaded = false;
   }

   public static List<User> getUsersByUserId(Collection<String> userIds) throws OseeCoreException {
      List<User> users = new ArrayList<User>();
      for (String userId : userIds) {
         try {
            users.add(getUserByUserId(userId));
         } catch (UserNotInDatabase ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return users;
   }

   /**
    * @return shallow copy of ArrayList of all active users in the datastore sorted by user name
    */
   public static List<User> getUsers() throws OseeCoreException {
      List<User> allUsers = getFromCache();
      List<User> activeUsers = new ArrayList<User>(allUsers.size());
      for (User user : allUsers) {
         if (user.isActive()) {
            activeUsers.add(user);
         }
      }
      return activeUsers;
   }

   public static List<User> getUsersAll() throws OseeCoreException {
      return getFromCache();
   }

   public static List<User> getUsersSortedByName() throws OseeCoreException {
      List<User> users = getUsers();
      Collections.sort(users);
      return users;
   }

   public static List<User> getUsersAllSortedByName() throws OseeCoreException {
      List<User> users = getFromCache();
      Collections.sort(users);
      return users;
   }

   private static List<User> getFromCache() throws OseeCoreException {
      ensurePopulated();
      return org.eclipse.osee.framework.jdk.core.util.Collections.castAll(ArtifactCache.getArtifactsByType(CoreArtifactTypes.User));
   }

   /**
    * Return sorted list of active User.getName() in database
    */
   public static String[] getUserNames() throws OseeCoreException {
      List<User> allUsers = getFromCache();
      String[] userNames = new String[allUsers.size()];
      int index = 0;
      for (User user : allUsers) {
         userNames[index++] = user.getName();
      }
      return userNames;
   }

   public static String getUserNameById(int userArtifactId) {
      String name;
      try {
         User user = null;
         if (userArtifactId <= 0) {
            user = UserManager.getUser(SystemUser.OseeSystem);
            userArtifactId = user.getArtId();
         } else {
            user = UserManager.getUserByArtId(userArtifactId);
         }
         name = user.getName();
      } catch (Exception ex) {
         name = "Could not resolve artId: " + userArtifactId;
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return name;
   }

   public static User getUserByArtId(int userArtifactId) throws OseeCoreException {
      ensurePopulated();
      return (User) ArtifactQuery.getArtifactFromId(userArtifactId, CoreBranches.COMMON);
   }

   public static boolean userExistsWithName(String name) throws OseeCoreException {
      for (User tempUser : getFromCache()) {
         if (tempUser.getName().equals(name)) {
            return true;
         }
      }
      return false;
   }

   /**
    * This is not the preferred way to get a user. Most likely getUserByUserId() or getUserByArtId() should be used
    * 
    * @return the first user found with the given name
    */
   public static User getUserByName(String name) throws OseeCoreException {
      for (User tempUser : getFromCache()) {
         if (tempUser.getName().equals(name)) {
            return tempUser;
         }
      }
      throw new UserNotInDatabase("User requested by name [%s] was not found.", name);
   }

   private static User getFromCacheByUserId(String userId) throws OseeCoreException {
      return (User) ArtifactCache.getByTextId(CACHE_PREFIX + userId, BranchManager.getCommonBranch());
   }

   private static User cacheByUserId(User userToCache) throws OseeCoreException {
      return (User) ArtifactCache.cacheByTextId(CACHE_PREFIX + userToCache.getUserId(), userToCache);
   }

   public static User getUser(User user) throws OseeCoreException {
      return getUserByUserId(user.getUserId());
   }

   public static User getUser(IUserToken user) throws OseeCoreException {
      return getUserByUserId(user.getUserId());
   }

   public static String getEmail(User user) throws OseeCoreException {
      return getUser(user).getEmail();
   }

   public static User getUserByUserId(String userId) throws OseeCoreException {
      if (!Strings.isValid(userId)) {
         throw new OseeArgumentException("UserId can't be null or \"\"");
      }

      ensurePopulated();
      User user = getFromCacheByUserId(userId);
      if (user == null) {
         try {
            user =
               (User) ArtifactQuery.getArtifactFromAttribute(CoreAttributeTypes.UserId, userId,
                  BranchManager.getCommonBranch());
         } catch (ArtifactDoesNotExist ex) {
            throw new UserNotInDatabase("The user with id [%s] was not found.", userId);
         }
      }
      return user;
   }

   private static synchronized void ensurePopulated() throws OseeCoreException {
      if (!userCacheIsLoaded) {
         List<Artifact> artifactsFound =
            ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.User, CoreBranches.COMMON, EXCLUDE_DELETED);
         for (Artifact artifact : artifactsFound) {
            User user = (User) artifact;
            User cachedUser = cacheByUserId(user);
            if (cachedUser != null) { // if duplicate user id found
               OseeCoreException ex =
                  new UserInDatabaseMultipleTimes("User of userId \"%s\" in datastore more than once", user.getUserId());

               // exception if I am the duplicate user otherwise just log
               if (user.getUserId().equals(ClientSessionManager.getSession().getId())) {
                  throw ex;
               } else {
                  OseeLog.log(Activator.class, Level.WARNING, ex);
               }
            }
         }
         userCacheIsLoaded = true;
      }
   }

   /**
    * @return whether the Authentication manager is in the middle of creating a user
    */
   public static boolean duringMainUserCreation() {
      return duringMainUserCreation;
   }

   public static synchronized User createMainUser(IUserToken userEnum, SkynetTransaction transaction) throws OseeCoreException {
      duringMainUserCreation = true;
      User user = createUser(userEnum, transaction);
      duringMainUserCreation = false;
      return user;
   }

   public static synchronized User createUser(IUserToken userToken, SkynetTransaction transaction) throws OseeCoreException {
      ensurePopulated();
      // Determine if user with id has already been created; boot strap issue with dbInit
      User user = getFromCacheByUserId(userToken.getUserId());
      if (user != null) {
         // Update user with this enum data
         user.setName(userToken.getName());
         user.setEmail(userToken.getEmail());
         user.setActive(userToken.isActive());
      } else {
         String guid = GUID.isValid(userToken.getGuid()) ? userToken.getGuid() : GUID.create();
         user =
            (User) ArtifactTypeManager.addArtifact(CoreArtifactTypes.User, BranchManager.getCommonBranch(),
               userToken.getName(), guid, HumanReadableId.generate());
         user.setActive(userToken.isActive());
         user.setUserID(userToken.getUserId());
         user.setEmail(userToken.getEmail());
         addUserToUserGroups(user);
         cacheByUserId(user);

         /**
          * Since Users are auto-created, display stack trace as INFO in client's log to help debug any unexpected
          * creation
          */
         if (!DbUtil.isDbInit()) {
            OseeLog.log(Activator.class, Level.INFO, "Created user " + user, new Exception(
               "just wanted the stack trace"));
         }
      }

      user.persist(transaction);
      return user;
   }

   private static void addUserToUserGroups(Artifact user) throws OseeCoreException {
      Collection<Artifact> userGroups =
         ArtifactQuery.getArtifactListFromTypeAndAttribute(CoreArtifactTypes.UserGroup,
            CoreAttributeTypes.DefaultGroup, "yes", CoreBranches.COMMON);
      for (Artifact userGroup : userGroups) {
         userGroup.addRelation(CoreRelationTypes.Users_User, user);
      }
   }

   public static boolean isUserInactive(Collection<User> users) throws OseeCoreException {
      for (User user : users) {
         if (!user.isActive()) {
            return true;
         }
      }
      return false;
   }

   public static boolean isUserSystem(Collection<User> users) throws OseeCoreException {
      for (User user : users) {
         if (isSystemUser(user)) {
            return true;
         }
      }
      return false;
   }

   public static boolean isSystemUser(User user) throws OseeCoreException {
      if (UserManager.getUser(SystemUser.OseeSystem).equals(user) || UserManager.getUser(SystemUser.UnAssigned).equals(
         user) || UserManager.getUser(SystemUser.Guest).equals(user)) {
         return true;
      }

      return false;
   }

   public static boolean isUnAssignedUser(User user) throws OseeCoreException {
      return (SystemUser.UnAssigned.getUserId().equals(user.getUserId()));
   }

   public static boolean isUserCurrentUser(Collection<User> users) throws OseeCoreException {
      for (User user : users) {
         if (UserManager.getUser().equals(user)) {
            return true;
         }
      }
      return false;
   }

   public static String getSetting(String key) throws OseeCoreException {
      return getUser().getSetting(key);
   }

   public static boolean getBooleanSetting(String key) throws OseeCoreException {
      return getUser().getBooleanSetting(key);
   }

   public static void setSetting(String key, String value) throws OseeCoreException {
      getUser().setSetting(key, value);
   }

   public static Collection<User> getUsers(Collection<User> users) throws OseeCoreException {
      List<User> arts = new ArrayList<User>();
      for (User user : users) {
         arts.add(UserManager.getUser(user));
      }
      return arts;
   }
}