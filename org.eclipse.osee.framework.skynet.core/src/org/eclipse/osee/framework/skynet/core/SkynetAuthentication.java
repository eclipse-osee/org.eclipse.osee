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
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.skynet.core.artifact.search.UserIdSearch;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.user.UserEnum;
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

   private OseeAuthentication oseeAuthentication;
   private ArtifactPersistenceManager artifactManager;
   private BranchPersistenceManager branchManager;
   private int noOneArtifactId;

   public static enum UserStatusEnum {
      Active, InActive, Both
   }
   private boolean firstTimeThrough;
   private final Map<String, User> nameOrIdToUserMap;
   private final Map<Integer, User> artIdToUserMap;
   private final ArrayList<User> activeUsers;
   private String[] activeUserNames;
   private User currentUser;
   private final Map<OseeUser, User> enumeratedUsers;
   private boolean duringUserCreation;

   private static final SkynetAuthentication instance = new SkynetAuthentication();

   private SkynetAuthentication() {
      firstTimeThrough = true;
      enumeratedUsers = new HashMap<OseeUser, User>(30);
      nameOrIdToUserMap = new HashMap<String, User>(800);
      artIdToUserMap = new HashMap<Integer, User>(800);
      activeUsers = new ArrayList<User>(700);
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
      artifactManager = ArtifactPersistenceManager.getInstance();
      oseeAuthentication = OseeAuthentication.getInstance();
      branchManager = BranchPersistenceManager.getInstance();
   }

   public boolean isAuthenticated() {
      return oseeAuthentication.isAuthenticated();
   }

   private void forceAuthenticationRoutine() {
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

   public synchronized User getAuthenticatedUser() throws IllegalStateException {
      if (SkynetDbInit.isPreArtifactCreation()) {
         try {
            return BootStrapUser.getInstance();
         } catch (SQLException ex) {
            // A BootSrapUser is not supposed to touch the database anyway
            logger.log(Level.SEVERE, ex.toString(), ex);
         }
      } else {
         if (firstTimeThrough) {
            forceAuthenticationRoutine();
         }

         if (!oseeAuthentication.isAuthenticated()) {
            try {
               currentUser = getUser(UserEnum.Guest);
            } catch (IllegalArgumentException ex) {
               logger.log(Level.SEVERE, ex.toString(), ex);
            } catch (SQLException ex) {
               logger.log(Level.SEVERE, ex.toString(), ex);
            }
         } else {
            String userId = oseeAuthentication.getCredentials().getField(UserCredentialEnum.Id);
            if (currentUser == null || !currentUser.getUserId().equals(userId)) {
               try {
                  try {
                     currentUser = getUserByIdWithError(userId);
                  } catch (IllegalArgumentException ex) {
                     if (firstTimeThrough) {
                        // try one more time to be sure the user doesn't already exist
                        try {
                           currentUser = getUserByIdWithError(userId);
                        } catch (IllegalArgumentException ex1) {
                           currentUser =
                                 createUser(oseeAuthentication.getCredentials().getField(UserCredentialEnum.Name),
                                       "spawnedBySkynet", userId, true);
                           persistUser(currentUser); // this is done outside of the crateUser call to avoid recursion
                        }
                     }
                  }
               } catch (SQLException ex) {
                  logger.log(Level.SEVERE, ex.toString(), ex);
               }
            }
         }
         firstTimeThrough = false; // firstTimeThrough must be set false after last use of its value
      }

      return currentUser;
   }

   private void persistUser(User user) {
      duringUserCreation = true;
      try {
         user.persistAttributes();
         user.getLinkManager().persistLinks();
      } catch (SQLException ex) {
         duringUserCreation = false;
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
   }

   public User createUser(OseeUser userEnum) {
      User user = createUser(userEnum.getName(), userEnum.getEmail(), userEnum.getUserID(), userEnum.isActive());
      persistUser(user);
      enumeratedUsers.put(userEnum, user);
      return user;
   }

   public User getUser(OseeUser userEnum) throws IllegalArgumentException, SQLException {
      User user = enumeratedUsers.get(userEnum);
      if (user == null) {
         user = getUserByIdWithError(userEnum.getUserID());
         enumeratedUsers.put(userEnum, user);
      }
      return user;
   }

   public User createUser(String name, String email, String userID, boolean active) {
      duringUserCreation = true;
      User user = null;
      try {
         user =
               (User) ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(User.ARTIFACT_NAME,
                     branchManager.getCommonBranch()).makeNewArtifact();
         user.setActive(active);
         user.setUserID(userID);
         user.setName(name);
         user.setEmail(email);
         addUserToMap(user);
         // this is here in case a user is created at an unexpected time
         if (!SkynetDbInit.isDbInit()) logger.log(Level.INFO, "Created user " + user, new Exception(
               "just wanted the stack trace"));
      } catch (SQLException ex) {
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
      if (activeUsers.size() == 0) {
         try {
            Collection<Artifact> dbUsers =
                  artifactManager.getArtifacts(new ArtifactTypeSearch(User.ARTIFACT_NAME, Operator.EQUAL),
                        branchManager.getCommonBranch());
            for (Artifact a : dbUsers) {
               User user = (User) a;
               if (user.isActive()) {
                  activeUsers.add(user);
               }
               addUserToMap(user);
            }
            Collections.sort(activeUsers);
            int i = 0;
            activeUserNames = new String[activeUsers.size()];
            for (User user : activeUsers) {
               activeUserNames[i++] = user.getName();
            }
         } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error Searching for User in DB.\n", ex);
         }
      }

      return (ArrayList<User>) activeUsers.clone();
   }

   public User getUserByIdWithError(String userId) throws SQLException, IllegalArgumentException, IllegalStateException {
      User user = nameOrIdToUserMap.get(userId);

      if (user == null) {
         Collection<Artifact> users =
               artifactManager.getArtifacts(new UserIdSearch(userId, Operator.EQUAL), branchManager.getCommonBranch());
         if (users.size() == 1) {
            user = (User) users.iterator().next();
            addUserToMap(user);
         } else if (users.size() > 1) {
            for (Artifact duplicate : users) {
               logger.log(
                     Level.WARNING,
                     "Duplicate User userId: \"" + userId + "\" with the name: \"" + duplicate.getDescriptiveName() + "\"");
            }
            throw new IllegalStateException(String.format("User \"%s\" (%s) in DB more than once",
                  users.iterator().next().getDescriptiveName(), userId));
         } else {
            // Note this is normal for the creation of this user (i.e. db init)
            throw new IllegalArgumentException("User requested by id " + userId + " was not found.  ");
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
      return activeUserNames;
   }

   /**
    * @param name
    * @param create if true, will create a temp user artifact; should only be used for dev purposes
    * @return user
    */
   public User getUserByName(String name, boolean create) {
      User user = nameOrIdToUserMap.get(name);
      if (user == null) {
         try {
            user =
                  (User) artifactManager.getArtifactFromTypeName(User.ARTIFACT_NAME, name,
                        branchManager.getCommonBranch());
         } catch (IllegalStateException ex) {
            if (create && ex.getLocalizedMessage().contains("There must be exactly one")) {
               user = createUser(name, "", name, true);
               try {
                  user.persistAttributes();
               } catch (SQLException ex2) {
                  logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex2);
               }
               addUserToMap(user);
               return user;
            }
         } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         }
         if (user != null) addUserToMap(user);
      }
      return user;
   }

   public User getUserByArtId(int authorId) {
      User user = null;
      // Anything under 1 will never be acquirable
      if (authorId < 1) {
         return null;
      } else if (artIdToUserMap.containsKey(authorId)) {
         user = artIdToUserMap.get(authorId);
      } else {
         try {
            user = (User) artifactManager.getArtifactFromId(authorId, branchManager.getCommonBranch());
            addUserToMap(user);
         } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            artIdToUserMap.put(authorId, null);
         } catch (IllegalArgumentException ex) {
            artIdToUserMap.put(authorId, null);
         }
      }
      return user;
   }

   private void addUserToMap(User user) {
      nameOrIdToUserMap.put(user.getDescriptiveName(), user);
      nameOrIdToUserMap.put(user.getUserId(), user);
      if (user.isInDb()) {
         artIdToUserMap.put(user.getArtId(), user);
      }
   }

   /**
    * @return whether the Authentification manager is in the middle of creating a user
    */
   public boolean duringUserCreation() {
      return duringUserCreation;
   }

   public int getNoOneArtifactId() {
      if (noOneArtifactId == 0) {
         try {
            noOneArtifactId = getUser(UserEnum.NoOne).getArtId();
         } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
            noOneArtifactId = -1;
         }
      }
      return noOneArtifactId;
   }
}