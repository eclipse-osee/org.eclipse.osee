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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.IOseeUser;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.UserInDatabaseMultipleTimes;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.CoreArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;

/**
 * @author Roberto E. Escobar
 */
public final class UserManager {

   private static final String CACHE_PREFIX = "userManager.";
   private static boolean userCacheIsLoaded = false;
   private static boolean duringMainUserCreation = false;

   private UserManager() {

   }

   /**
    * Returns the currently authenticated user
    * 
    * @return User
    * @throws OseeCoreException
    */
   public static User getUser() throws OseeCoreException {
      if (duringMainUserCreation) {
         return BootStrapUser.getInstance();
      }
      return ClientUser.getMainUser();
   }

   /**
    * @return shallow copy of ArrayList of all active users in the datastore sorted by user name
    */
   public static List<User> getUsers() throws OseeCoreException {
      return getUsers(Active.Active);
   }

   public static List<User> getActiveAndInactiveUsers() throws OseeCoreException {
      return getUsers(Active.Both);
   }

   public static List<User> getInactiveUsers() throws OseeCoreException {
      return getUsers(Active.InActive);
   }

   public static List<User> getUsersSortedByName() throws OseeCoreException {
      List<User> users = getUsers();
      Collections.sort(users);
      return users;
   }

   private static List<User> getFromCache() throws OseeCoreException {
      ArtifactType userType = ArtifactTypeManager.getTypeByGuid(CoreArtifacts.User.getGuid());
      return org.eclipse.osee.framework.jdk.core.util.Collections.castAll(ArtifactCache.getArtifactsByType(userType));
   }

   private static List<User> getUsers(Active userStatus) throws OseeCoreException {
      ensurePopulated();
      ArtifactType userType = ArtifactTypeManager.getTypeByGuid(CoreArtifacts.User.getGuid());
      return org.eclipse.osee.framework.jdk.core.util.Collections.castAll(ArtifactCache.getArtifactsByType(userType,
            userStatus));
   }

   /**
    * Return sorted list of active User.getName() in database
    * 
    * @return String[]
    */
   public static String[] getUserNames() throws OseeCoreException {
      ensurePopulated();
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
         if (userArtifactId == 0) {
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
      User user = (User) ArtifactCache.getActive(userArtifactId, BranchManager.getCommonBranch());
      if (user == null) {
         throw new UserNotInDatabase("User requested by artId \"" + userArtifactId + "\" was not found.");
      }
      return user;
   }

   public static boolean userExistsWithName(String name) throws OseeCoreException {
      ensurePopulated();
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
    * @param name
    * @return the first user found with the given name
    * @throws OseeCoreException
    */
   public static User getUserByName(String name) throws OseeCoreException {
      ensurePopulated();
      User user = null;
      for (User tempUser : getFromCache()) {
         if (tempUser.getName().equals(name)) {
            user = tempUser;
            return user;
         }
      }
      throw new UserNotInDatabase("User requested by name \"" + name + "\" was not found.");
   }

   private static User getFromCacheByUserId(String userId) throws OseeCoreException {
      return (User) ArtifactCache.getByTextId(CACHE_PREFIX + userId, BranchManager.getCommonBranch());
   }

   private static User cacheByUserId(User userToCache) throws OseeCoreException {
      return (User) ArtifactCache.cacheByTextId(CACHE_PREFIX + userToCache.getUserId(), userToCache);
   }

   public static User getUserByUserId(String userId) throws OseeCoreException {
      if (userId == null || userId.equals("")) {
         throw new OseeArgumentException("UserId can't be null or \"\"");
      }

      ensurePopulated();
      User user = getFromCacheByUserId(userId);
      if (user == null) {
         try {
            user = (User) ArtifactQuery.getArtifactFromAttribute("User Id", userId, BranchManager.getCommonBranch());
         } catch (ArtifactDoesNotExist ex) {
            throw new UserNotInDatabase("the user with id " + userId + " was not found.");
         }
      }
      return user;
   }

   public static User getUser(IOseeUser userEnum) throws OseeCoreException {
      return getUserByUserId(userEnum.getUserID());
   }

   private static synchronized void ensurePopulated() throws OseeCoreException {
      if (!userCacheIsLoaded) {
         List<Artifact> artifactsFound =
               ArtifactQuery.getArtifactListFromType(CoreArtifacts.User, BranchManager.getCommonBranch(), false);
         for (Artifact artifact : artifactsFound) {
            User user = (User) artifact;
            User cachedUser = cacheByUserId(user);
            if (cachedUser != null) { // if duplicate user id found
               OseeCoreException ex =
                     new UserInDatabaseMultipleTimes(
                           "User of userId \"" + user.getUserId() + "\" in datastore more than once");

               // exception if I am the duplicate user otherwise just log
               if (user.getUserId().equals(ClientSessionManager.getSession().getId())) {
                  throw ex;
               } else {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
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

   public static synchronized User createMainUser(IOseeUser userEnum, SkynetTransaction transaction) throws OseeCoreException {
      duringMainUserCreation = true;
      User user = createUser(userEnum, transaction);
      duringMainUserCreation = false;
      return user;
   }

   public static synchronized User createUser(IOseeUser userEnum, SkynetTransaction transaction) throws OseeCoreException {
      ensurePopulated();
      // Determine if user with id has already been created; boot strap issue with dbInit
      User user = getFromCacheByUserId(userEnum.getUserID());
      if (user != null) {
         // Update user with this enum data
         user.setName(userEnum.getName());
         user.setEmail(userEnum.getEmail());
         user.setActive(userEnum.isActive());
      } else {
         user =
               (User) ArtifactTypeManager.addArtifact(CoreArtifacts.User.getName(), BranchManager.getCommonBranch(),
                     userEnum.getName());
         user.setActive(userEnum.isActive());
         user.setUserID(userEnum.getUserID());
         user.setEmail(userEnum.getEmail());
         cacheByUserId(user);

         // this is here in case a user is created at an unexpected time
         if (!DbUtil.isDbInit()) {
            OseeLog.log(Activator.class, Level.INFO, "Created user " + user, new Exception(
                  "just wanted the stack trace"));
         }
      }

      user.persist(transaction);
      return user;
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
         if (user.isSystemUser()) {
            return true;
         }
      }
      return false;
   }

   public static boolean isUserCurrentUser(Collection<User> users) throws OseeCoreException {
      for (User user : users) {
         if (user.equals(UserManager.getUser())) {
            return true;
         }
      }
      return false;
   }

}