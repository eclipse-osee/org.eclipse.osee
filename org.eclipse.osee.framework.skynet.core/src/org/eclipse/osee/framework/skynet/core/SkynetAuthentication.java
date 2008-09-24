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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.util.OseeUser;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.event.AccessControlEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.exception.UserInDatabaseMultipleTimes;
import org.eclipse.osee.framework.skynet.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.skynet.core.user.UserEnum;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.security.AuthenticationDialog;
import org.eclipse.osee.framework.ui.plugin.security.OseeAuthentication;
import org.eclipse.osee.framework.ui.plugin.security.UserCredentials.UserCredentialEnum;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.swt.widgets.Display;

/**
 * <b>Skynet Authentication</b><br/> Provides mapping of the current Authenticated User Id to its User Artifact in the
 * Skynet Database.
 * 
 * @author Roberto E. Escobar
 */
public class SkynetAuthentication {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(SkynetAuthentication.class);
   private int noOneArtifactId;
   private final boolean createUserWhenNotInDatabase = true;

   public static enum UserStatusEnum {
      Active, InActive, Both
   }
   private boolean firstTimeThrough;
   private Map<String, User> userIdToUserCache;
   private Map<String, User> nameToUserCache;
   private ArrayList<User> activeUserCache;
   private Map<OseeUser, User> enumeratedUserCache;
   private String[] sortedActiveUserNameCache;
   private User currentUser;
   private boolean duringUserCreation;
   private boolean basicUsersCreated = true;

   private static final SkynetAuthentication instance = new SkynetAuthentication();

   private SkynetAuthentication() {
      firstTimeThrough = true;
   }

   // Needed so an external call to cacheUser() can load the cache first without an infinite loop
   private boolean isLoadingUsersCache = false;
   private boolean userCacheIsLoaded = false;

   private synchronized void loadUsersCache() throws OseeCoreException, SQLException {
      try {
         if (!userCacheIsLoaded) {
            isLoadingUsersCache = true;
            enumeratedUserCache = new HashMap<OseeUser, User>(30);
            nameToUserCache = new HashMap<String, User>(800);
            userIdToUserCache = new HashMap<String, User>(800);
            activeUserCache = new ArrayList<User>(700);
            Collection<Artifact> dbUsers =
                  ArtifactQuery.getArtifactsFromAttributeType(User.userIdAttributeName,
                        BranchPersistenceManager.getCommonBranch());
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
      } catch (SQLException ex) {
         // If exception, want to return to the state where cache is not loaded
         userCacheIsLoaded = false;
         userIdToUserCache.clear();
         nameToUserCache.clear();
         enumeratedUserCache.clear();
      }
   }

   private void cacheUser(User user, OseeUser userEnum) throws OseeCoreException, SQLException {
      // System.out.println("caching User " + user.getUserId());
      // If cacheUser is called outside of the main loadUserCache, then load cache first
      if (!isLoadingUsersCache) loadUsersCache();
      // Check to make sure user is not in databaes more than once
      User currentUser = userIdToUserCache.get(user.getUserId());
      // Allows the same user artifact to be re-cached
      if (currentUser != null && currentUser.getArtId() != user.getArtId()) {
         UserInDatabaseMultipleTimes exception =
               new UserInDatabaseMultipleTimes(
                     "User of userId \"" + user.getUserId() + "\" in datastore more than once");
         if (user.getUserId().equals(OseeAuthentication.getInstance().getCredentials().getField(UserCredentialEnum.Id))) {
            throw exception;
         } else {
            OseeLog.log(SkynetAuthentication.class, Level.SEVERE, exception);
         }
      } else {
         userIdToUserCache.put(user.getUserId(), user);
      }
      if (user.isActive()) activeUserCache.add(user);
      nameToUserCache.put(user.getDescriptiveName(), user);

      if (userEnum != null) enumeratedUserCache.put(userEnum, user);
   }

   public boolean isAuthenticated() {
      return OseeAuthentication.getInstance().isAuthenticated();
   }

   private void forceAuthenticationRoutine() {
      OseeAuthentication oseeAuthentication = OseeAuthentication.getInstance();
      if (!oseeAuthentication.isAuthenticated()) {
         if (oseeAuthentication.isLoginAllowed()) {
            AuthenticationDialog.openDialog();
         } else {
            oseeAuthentication.authenticate("", "", "", false);
         }
         notifyListeners();
      }
   }

   public static void notifyListeners() {
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            try {
               OseeEventManager.kickAccessControlArtifactsEvent(this, AccessControlEventType.UserAuthenticated,
                     LoadedArtifacts.EmptyLoadedArtifacts());
            } catch (Exception ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }
      });
   }

   /**
    * Returns the currently authenticated user
    * 
    * @return User
    */
   public static User getUser() {
      return instance.getAuthenticatedUser();
   }

   private synchronized User getAuthenticatedUser() {
      try {
         if (!isBasicUsersCreated()) {
            return BootStrapUser.getInstance();
         } else {
            if (firstTimeThrough) {
               forceAuthenticationRoutine();
            }
            if (!OseeAuthentication.getInstance().isAuthenticated()) {
               currentUser = getUser(UserEnum.Guest);
            } else {
               String userId = OseeAuthentication.getInstance().getCredentials().getField(UserCredentialEnum.Id);
               if (currentUser == null) {
                  try {
                     currentUser = getUserByUserId(userId);
                  } catch (UserNotInDatabase ex) {
                     if (createUserWhenNotInDatabase) {
                        currentUser =
                              createUser(OseeAuthentication.getInstance().getCredentials().getField(
                                    UserCredentialEnum.Name), "spawnedBySkynet", userId, true);
                        persistUser(currentUser); // this is done outside of the crateUser call to avoid recursion
                     } else {
                        AWorkbench.popup("Logged in as Guest",
                              "If you do not expect to be logged in as Guest, please report this immediately.");
                        currentUser = getUser(UserEnum.Guest);
                     }
                  }
               }
            }
            firstTimeThrough = false; // firstTimeThrough must be set false after last use of its value
         }
      } catch (OseeCoreException ex) {
         logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }

      return currentUser;
   }

   private static void persistUser(User user) throws OseeCoreException, SQLException {
      instance.duringUserCreation = true;
      try {
         user.persistAttributesAndRelations();
         instance.cacheUser(user, null);
      } catch (SQLException ex) {
         instance.duringUserCreation = false;
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
   }

   public static User createUser(OseeUser userEnum) throws OseeCoreException, SQLException {
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
         user.persistAttributes();
      } else {
         user = instance.createUser(userEnum.getName(), userEnum.getEmail(), userEnum.getUserID(), userEnum.isActive());
         persistUser(user);
      }
      return user;
   }

   public static User getUser(OseeUser userEnum) throws OseeCoreException, SQLException {
      instance.loadUsersCache();
      User user = instance.enumeratedUserCache.get(userEnum);
      if (user == null) {
         user = getUserByUserId(userEnum.getUserID());
         if (user == null) throw new UserNotInDatabase("UserEnum \"" + userEnum + "\" not in database.");
         instance.enumeratedUserCache.put(userEnum, user);
      }
      return instance.enumeratedUserCache.get(userEnum);
   }

   private User createUser(String name, String email, String userID, boolean active) throws OseeCoreException, SQLException {
      duringUserCreation = true;
      User user = null;
      try {
         user =
               (User) ArtifactTypeManager.addArtifact(User.ARTIFACT_NAME, BranchPersistenceManager.getCommonBranch(),
                     name);
         user.setActive(active);
         user.setUserID(userID);
         user.setEmail(email);
         // this is here in case a user is created at an unexpected time
         if (!SkynetDbInit.isDbInit()) logger.log(Level.INFO, "Created user " + user, new Exception(
               "just wanted the stack trace"));
      } finally {
         duringUserCreation = false;
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
   public static ArrayList<User> getUsers() throws OseeCoreException, SQLException {
      instance.loadUsersCache();
      return (ArrayList<User>) instance.activeUserCache.clone();
   }

   public static List<User> getUsersSortedByName() throws OseeCoreException, SQLException {
      List<User> users = new ArrayList<User>();
      for (String user : getUserNames()) {
         users.add(getUserByName(user, false));
      }
      return users;
   }

   public static User getUserByUserId(String userId) throws OseeCoreException, SQLException {
      if (userId == null || userId.equals("")) {
         throw new IllegalArgumentException("UserId can't be null or \"\"");
      }

      instance.loadUsersCache();
      User user = instance.userIdToUserCache.get(userId);
      if (user == null) {
         try {
            user =
                  (User) ArtifactQuery.getArtifactFromAttribute("User Id", userId,
                        BranchPersistenceManager.getCommonBranch());
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
   public static String[] getUserNames() throws OseeCoreException, SQLException {
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

   /**
    * @param name
    * @param create if true, will create a temp user artifact; should only be used for dev purposes
    * @return user
    */
   public static User getUserByName(String name, boolean create) throws OseeCoreException, SQLException {
      instance.loadUsersCache();
      User user = instance.nameToUserCache.get(name);
      if (user == null && create) {
         instance.persistUser(instance.createUser(name, "", name, true));
         user = instance.nameToUserCache.get(name);
         if (user == null) throw new UserNotInDatabase(
               "Error creating and caching user \"" + name + "\" was not found.");
      }
      if (user == null) throw new UserNotInDatabase("User requested by name \"" + name + "\" was not found.");
      return user;
   }

   public static User getUserByArtId(int userArtifactId) throws OseeCoreException, SQLException {
      instance.loadUsersCache();
      User user = (User) ArtifactCache.getActive(userArtifactId, BranchPersistenceManager.getCommonBranch());
      if (user == null) throw new UserNotInDatabase("User requested by artId \"" + userArtifactId + "\" was not found.");
      return user;
   }

   /**
    * @return whether the Authentication manager is in the middle of creating a user
    */
   public static boolean duringUserCreation() {
      return instance.duringUserCreation;
   }

   public static int getNoOneArtifactId() {
      if (instance.noOneArtifactId == 0) {
         try {
            instance.noOneArtifactId = getUser(UserEnum.NoOne).getArtId();
         } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
            instance.noOneArtifactId = -1;
         }
      }
      return instance.noOneArtifactId;
   }

   public static boolean isBasicUsersCreated() {
      return instance.basicUsersCreated;
   }

   public static void setBasicUsersCreated(boolean basicUsersCreated) {
      instance.basicUsersCreated = basicUsersCreated;
   }

}