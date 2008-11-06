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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.IOseeUser;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.UserInDatabaseMultipleTimes;
import org.eclipse.osee.framework.db.connection.exception.UserNotInDatabase;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * <b>Skynet Authentication</b><br/> Provides mapping of the current Authenticated User Id to its User Artifact in the
 * Skynet Database.
 * 
 * @author Roberto E. Escobar
 */
public class UserCache {
   private int noOneArtifactId;

   public static enum UserStatusEnum {
      Active, InActive, Both
   }

   private static MutableBoolean duringUserCreation = new MutableBoolean(false);
   private static final UserCache instance = new UserCache();

   private Map<String, User> userIdToUserCache;
   private Map<String, User> nameToUserCache;
   private ArrayList<User> activeUserCache;
   private Map<IOseeUser, User> enumeratedUserCache;
   private String[] sortedActiveUserNameCache;

   private UserCache() {
   }

   // Needed so an external call to cacheUser() can load the cache first without an infinite loop
   private boolean isLoadingUsersCache = false;
   private boolean userCacheIsLoaded = false;

   private synchronized void loadUsersCache() throws OseeCoreException {
      try {
         if (!userCacheIsLoaded) {
            isLoadingUsersCache = true;
            enumeratedUserCache = new HashMap<IOseeUser, User>(30);
            nameToUserCache = new HashMap<String, User>(800);
            userIdToUserCache = new HashMap<String, User>(800);
            activeUserCache = new ArrayList<User>(700);
            Collection<Artifact> dbUsers =
                  ArtifactQuery.getArtifactsFromAttributeType(User.userIdAttributeName, BranchManager.getCommonBranch());
            for (Artifact a : dbUsers) {
               User user = (User) a;
               cacheUser(user, null);
            }
            isLoadingUsersCache = false;
            userCacheIsLoaded = true;
         }
      } catch (OseeCoreException ex) {
         // If exception, want to return to the state where cache is not loaded
         userCacheIsLoaded = false;
         userIdToUserCache.clear();
         nameToUserCache.clear();
         enumeratedUserCache.clear();
         throw ex;
      }
   }

   private void cacheUser(User user, IOseeUser userEnum) throws OseeCoreException {
      // System.out.println("caching User " + user.getUserId());
      // If cacheUser is called outside of the main loadUserCache, then load cache first
      if (!isLoadingUsersCache) loadUsersCache();
      // Check to make sure user is not in database more than once
      User currentUser = userIdToUserCache.get(user.getUserId());
      // Allows the same user artifact to be re-cached
      if (currentUser != null && currentUser.getArtId() != user.getArtId()) {
         UserInDatabaseMultipleTimes exception =
               new UserInDatabaseMultipleTimes(
                     "User of userId \"" + user.getUserId() + "\" in datastore more than once");
         if (user.getUserId().equals(ClientSessionManager.getSession().getId())) {
            throw exception;
         } else {
            OseeLog.log(UserCache.class, Level.SEVERE, exception);
         }
      } else {
         userIdToUserCache.put(user.getUserId(), user);
      }
      if (user.isActive()) activeUserCache.add(user);
      nameToUserCache.put(user.getDescriptiveName(), user);

      if (userEnum != null) enumeratedUserCache.put(userEnum, user);
   }

   /**
    * Returns the currently authenticated user
    * 
    * @return User
    */
   public static User getUser() {
      return ClientUser.getInstance().getMainUser();
   }

   static void persistUser(User user, SkynetTransaction transaction) throws OseeCoreException {
      synchronized (duringUserCreation) {
         duringUserCreation.setValue(true);
         try {
            user.persistAttributesAndRelations(transaction);
            instance.cacheUser(user, null);
         } finally {
            duringUserCreation.setValue(false);
         }
      }
   }

   public static User createUser(IOseeUser userEnum, SkynetTransaction transaction) throws OseeCoreException {
      instance.loadUsersCache();
      // Determine if user with id has already been created; boot strap issue with dbInit
      User user = instance.userIdToUserCache.get(userEnum.getUserID());
      if (user != null) {
         // Update user with this enum data
         instance.nameToUserCache.remove(user.getDescriptiveName());
         user.setDescriptiveName(userEnum.getName());
         instance.nameToUserCache.put(userEnum.getName(), user);
         user.setEmail(userEnum.getEmail());
         user.setActive(userEnum.isActive());
         if (!instance.activeUserCache.contains(user) && userEnum.isActive()) {
            instance.activeUserCache.add(user);
         }
         instance.enumeratedUserCache.put(userEnum, user);
         user.persistAttributes(transaction);
      } else {
         user = createUser(userEnum.getName(), userEnum.getEmail(), userEnum.getUserID(), userEnum.isActive());
         persistUser(user, transaction);
      }
      return user;
   }

   public static User getUser(IOseeUser userEnum) throws OseeCoreException {
      instance.loadUsersCache();
      User user = instance.enumeratedUserCache.get(userEnum);
      if (user == null) {
         user = getUserByUserId(userEnum.getUserID());
         if (user == null) throw new UserNotInDatabase("UserEnum \"" + userEnum + "\" not in database.");
         instance.enumeratedUserCache.put(userEnum, user);
      }
      return instance.enumeratedUserCache.get(userEnum);
   }

   static User createUser(String name, String email, String userID, boolean active) throws OseeCoreException {
      User user = null;
      synchronized (duringUserCreation) {
         duringUserCreation.setValue(true);
         try {
            user = (User) ArtifactTypeManager.addArtifact(User.ARTIFACT_NAME, BranchManager.getCommonBranch(), name);
            user.setActive(active);
            user.setUserID(userID);
            user.setEmail(email);
            // this is here in case a user is created at an unexpected time
            if (!SkynetDbInit.isDbInit()) {
               OseeLog.log(SkynetActivator.class, Level.INFO, "Created user " + user, new Exception(
                     "just wanted the stack trace"));
            }
         } finally {
            duringUserCreation.setValue(false);
         }
      }
      return user;
   }

   public static int getSafeUserId() {
      if (duringUserCreation()) return -1;
      if (getUser() == null) return -1;
      return getUser().getArtId();
   }

   /**
    * @return shallow copy of ArrayList of all active users in the datastore sorted by user name
    */
   @SuppressWarnings("unchecked")
   public static ArrayList<User> getUsers() throws OseeCoreException {
      instance.loadUsersCache();
      return (ArrayList<User>) instance.activeUserCache.clone();
   }

   public static List<User> getUsersSortedByName() throws OseeCoreException {
      List<User> users = new ArrayList<User>();
      for (String user : getUserNames()) {
         users.add(getUserByName(user));
      }
      return users;
   }

   public static User getUserByUserId(String userId) throws OseeCoreException {
      if (userId == null || userId.equals("")) {
         throw new IllegalArgumentException("UserId can't be null or \"\"");
      }

      instance.loadUsersCache();
      User user = instance.userIdToUserCache.get(userId);
      if (user == null) {
         try {
            user = (User) ArtifactQuery.getArtifactFromAttribute("User Id", userId, BranchManager.getCommonBranch());
         } catch (ArtifactDoesNotExist ex) {
            throw new UserNotInDatabase("the user with id " + userId + " was not found.");
         }
      }
      return user;
   }

   /**
    * Return sorted list of active User.getName() in database
    * 
    * @return String[]
    */
   public static String[] getUserNames() throws OseeCoreException {
      instance.loadUsersCache();
      // Sort if null or new names added since last sort
      if (instance.sortedActiveUserNameCache == null || instance.sortedActiveUserNameCache.length != instance.userIdToUserCache.size()) {
         Collections.sort(instance.activeUserCache);
         int i = 0;
         instance.sortedActiveUserNameCache = new String[instance.activeUserCache.size()];
         for (User user : instance.activeUserCache) {
            instance.sortedActiveUserNameCache[i++] = user.getName();
         }
      }
      return instance.sortedActiveUserNameCache;
   }

   public static User getUserByName(String name) throws OseeCoreException {
      return getUserByName(name, false);
   }

   public static User getUserByName(String name, boolean create) throws OseeCoreException {
      return getUserByName(name, create, null);
   }

   /**
    * @param name
    * @param create if true, will create a temp user artifact; should only be used for dev purposes
    * @return user
    */
   public static User getUserByName(String name, boolean create, SkynetTransaction transaction) throws OseeCoreException {
      instance.loadUsersCache();
      User user = instance.nameToUserCache.get(name);
      if (user == null && create) {
         persistUser(createUser(name, "", name, true), transaction);
         user = instance.nameToUserCache.get(name);
         if (user == null) throw new UserNotInDatabase(
               "Error creating and caching user \"" + name + "\" was not found.");
      }
      if (user == null) throw new UserNotInDatabase("User requested by name \"" + name + "\" was not found.");
      return user;
   }

   public static User getUserByArtId(int userArtifactId) throws OseeCoreException {
      instance.loadUsersCache();
      User user = (User) ArtifactCache.getActive(userArtifactId, BranchManager.getCommonBranch());
      if (user == null) throw new UserNotInDatabase("User requested by artId \"" + userArtifactId + "\" was not found.");
      return user;
   }

   /**
    * @return whether the Authentication manager is in the middle of creating a user
    */
   public static boolean duringUserCreation() {
      return duringUserCreation.getValue();
   }

   public static int getNoOneArtifactId() {
      if (instance.noOneArtifactId == 0) {
         try {
            instance.noOneArtifactId = getUser(SystemUser.NoOne).getArtId();
         } catch (Exception ex) {
            OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
            instance.noOneArtifactId = -1;
         }
      }
      return instance.noOneArtifactId;
   }
}