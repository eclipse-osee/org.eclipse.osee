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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.util.OseeUser;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.skynet.core.user.UserEnum;
import org.eclipse.osee.framework.skynet.core.user.UserNotInDatabase;
import org.eclipse.osee.framework.ui.plugin.event.AuthenticationEvent;
import org.eclipse.osee.framework.ui.plugin.security.AuthenticationDialog;
import org.eclipse.osee.framework.ui.plugin.security.OseeAuthentication;
import org.eclipse.osee.framework.ui.plugin.security.UserCredentials.UserCredentialEnum;
import org.eclipse.swt.widgets.Display;

/**
 * <b>Skynet Authentication</b><br/> Provides mapping of the current Authenticated User Id to its User Artifact in the
 * Skynet Database.
 * 
 * @author Roberto E. Escobar
 */
public class SkynetAuthentication implements PersistenceManager {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(SkynetAuthentication.class);
   private int noOneArtifactId;
   private boolean createUserWhenNotInDatabase = true;

   public static enum UserStatusEnum {
      Active, InActive, Both
   }
   private boolean firstTimeThrough;
   private final Map<String, User> nameOrIdToUserCache;
   private final Map<Integer, User> artIdToUserCache;
   private final ArrayList<User> activeUserCache;
   private String[] activeUserNameCache;
   private User currentUser;
   private final Map<OseeUser, User> enumeratedUserCache;
   private boolean duringUserCreation;

   private static final SkynetAuthentication instance = new SkynetAuthentication();

   private SkynetAuthentication() {
      firstTimeThrough = true;
      enumeratedUserCache = new HashMap<OseeUser, User>(30);
      nameOrIdToUserCache = new HashMap<String, User>(800);
      artIdToUserCache = new HashMap<Integer, User>(800);
      activeUserCache = new ArrayList<User>(700);
   }

   public static SkynetAuthentication getInstance() {
      PersistenceManagerInit.initManagerWeb(instance);
      return instance;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.PersistenceManager#onManagerWebInit()
    */
   public void onManagerWebInit() throws Exception {
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

   private static void notifyListeners() {
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            SkynetEventManager.getInstance().kick(new AuthenticationEvent(this));
         }
      });
   }

   /**
    * Returns the currently authenticated user
    * 
    * @return
    */
   public static User getUser() {
      return instance.getAuthenticatedUser();
   }

   private synchronized User getAuthenticatedUser() {
      try {
         if (SkynetDbInit.isPreArtifactCreation()) {
            return BootStrapUser.getInstance();
         } else {
            if (firstTimeThrough) {
               forceAuthenticationRoutine();
            }

            OseeAuthentication oseeAuthentication = OseeAuthentication.getInstance();
            if (!oseeAuthentication.isAuthenticated()) {
               currentUser = getUser(UserEnum.Guest);
            } else {
               String userId = oseeAuthentication.getCredentials().getField(UserCredentialEnum.Id);
               if (currentUser == null || !currentUser.getUserId().equals(userId)) {
                  try {
                     currentUser = getUserByIdWithError(userId);
                  } catch (UserNotInDatabase ex) {
                     if (createUserWhenNotInDatabase) {
                        currentUser =
                              createUser(oseeAuthentication.getCredentials().getField(UserCredentialEnum.Name),
                                    "spawnedBySkynet", userId, true);
                        persistUser(currentUser); // this is done outside of the crateUser call to avoid recursion
                     } else
                        currentUser = getUser(UserEnum.Guest);
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

   private void persistUser(User user) {
      duringUserCreation = true;
      try {
         user.persistAttributesAndRelations();
      } catch (SQLException ex) {
         duringUserCreation = false;
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
   }

   public User createUser(OseeUser userEnum) {
      User user = createUser(userEnum.getName(), userEnum.getEmail(), userEnum.getUserID(), userEnum.isActive());
      persistUser(user);
      enumeratedUserCache.put(userEnum, user);
      return user;
   }

   public static User getUser(OseeUser userEnum) throws SQLException, MultipleAttributesExist, UserNotInDatabase, MultipleArtifactsExist {
      User user = instance.enumeratedUserCache.get(userEnum);
      if (user == null) {
         user = getUserByIdWithError(userEnum.getUserID());
         instance.enumeratedUserCache.put(userEnum, user);
      }
      return user;
   }

   public User createUser(String name, String email, String userID, boolean active) {
      duringUserCreation = true;
      User user = null;
      try {
         user =
               (User) ArtifactTypeManager.addArtifact(User.ARTIFACT_NAME, BranchPersistenceManager.getCommonBranch(),
                     name);
         user.setActive(active);
         user.setUserID(userID);
         user.setEmail(email);
         addUserToMap(user);
         // this is here in case a user is created at an unexpected time
         if (!SkynetDbInit.isDbInit()) logger.log(Level.INFO, "Created user " + user, new Exception(
               "just wanted the stack trace"));
      } catch (Exception ex) {
         logger.log(Level.WARNING, "Error Creating User.\n", ex);
      } finally {
         duringUserCreation = false;
      }
      return user;
   }

   /**
    * @return shallow copy of ArrayList of all active users in the datastore sorted by user name
    */
   @SuppressWarnings("unchecked")
   public ArrayList<User> getUsers() {
      if (activeUserCache.size() == 0) {
         try {
            Collection<Artifact> dbUsers =
                  ArtifactQuery.getArtifactsFromType(User.ARTIFACT_NAME, BranchPersistenceManager.getCommonBranch());
            for (Artifact a : dbUsers) {
               User user = (User) a;
               if (user.isActive()) {
                  activeUserCache.add(user);
               }
               addUserToMap(user);
            }
            Collections.sort(activeUserCache);
            int i = 0;
            activeUserNameCache = new String[activeUserCache.size()];
            for (User user : activeUserCache) {
               activeUserNameCache[i++] = user.getName();
            }
         } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error Searching for User in DB.\n", ex);
         }
      }

      return (ArrayList<User>) activeUserCache.clone();
   }

   public static User getUserByIdWithError(String userId) throws SQLException, MultipleAttributesExist, UserNotInDatabase, MultipleArtifactsExist {
      if (userId == null || userId.equals("")) {
         throw new IllegalArgumentException("UserId can't be null or \"\"");
      }
      User user = instance.nameOrIdToUserCache.get(userId);

      if (user == null) {
         try {
            user =
                  (User) ArtifactQuery.getArtifactFromTypeAndAttribute(User.ARTIFACT_NAME, User.userIdAttributeName,
                        userId, BranchPersistenceManager.getCommonBranch());
            instance.addUserToMap(user);
         } catch (ArtifactDoesNotExist ex) {
            // Note this is normal for the creation of this user (i.e. db init)
            throw new UserNotInDatabase("User requested by id \"" + userId + "\" was not found.", ex);
         }
      }
      return user;
   }

   /**
    * Return sorted list of active User.getName() in database
    * 
    * @return String[]
    */
   public String[] getUserNames() {
      getUsers(); // ensure users are cached
      return activeUserNameCache;
   }

   /**
    * @param name
    * @param create if true, will create a temp user artifact; should only be used for dev purposes
    * @return user
    */
   public User getUserByName(String name, boolean create) {
      User user = nameOrIdToUserCache.get(name);
      if (user == null) {
         try {
            user =
                  (User) ArtifactQuery.getArtifactFromTypeAndName(User.ARTIFACT_NAME, name,
                        BranchPersistenceManager.getCommonBranch());
         } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         } catch (MultipleArtifactsExist ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         } catch (ArtifactDoesNotExist ex) {
            if (create) {
               user = createUser(name, "", name, true);
               try {
                  user.persistAttributes();
               } catch (SQLException ex2) {
                  logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex2);
               }
            } else
               logger.log(Level.SEVERE, ex.toString(), ex);
         }
         try {
            if (user != null) addUserToMap(user);
         } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         }
      }
      return user;
   }

   public User getUserByArtId(int authorId) {
      User user = null;
      // Anything under 1 will never be acquirable
      if (authorId < 1) {
         return null;
      } else if (artIdToUserCache.containsKey(authorId)) {
         user = artIdToUserCache.get(authorId);
      } else {
         try {
            user = (User) ArtifactQuery.getArtifactFromId(authorId, BranchPersistenceManager.getCommonBranch());
            addUserToMap(user);
         } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            artIdToUserCache.put(authorId, null);
         }
      }
      return user;
   }

   private void addUserToMap(User user) throws SQLException, MultipleAttributesExist {
      nameOrIdToUserCache.put(user.getDescriptiveName(), user);
      nameOrIdToUserCache.put(user.getUserId(), user);
      if (user.isInDb()) {
         artIdToUserCache.put(user.getArtId(), user);
      }
   }

   /**
    * @return whether the Authentication manager is in the middle of creating a user
    */
   public boolean duringUserCreation() {
      return duringUserCreation;
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
}