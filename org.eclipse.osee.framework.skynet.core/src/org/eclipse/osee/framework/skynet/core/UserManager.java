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
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.IOseeUser;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.UserInDatabaseMultipleTimes;
import org.eclipse.osee.framework.db.connection.exception.UserNotInDatabase;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.ITransactionsDeletedEventListener;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;

/**
 * @author Roberto E. Escobar
 */
public class UserManager implements IFrameworkTransactionEventListener, ITransactionsDeletedEventListener, IArtifactsPurgedEventListener {
   public static enum UserStatusEnum {
      Active, InActive, Both
   }

   private static final UserManager instance = new UserManager();
   private final Map<String, User> userIdToUserCache = Collections.synchronizedMap(new HashMap<String, User>());
   private boolean userCacheIsLoaded = false;
   private boolean duringMainUserCreation = false;

   private UserManager() {
   }

   /**
    * Returns the currently authenticated user
    * 
    * @return User
    * @throws OseeCoreException
    */
   public static User getUser() throws OseeCoreException {
      if (instance.duringMainUserCreation) {
         return BootStrapUser.getInstance();
      }
      return ClientUser.getMainUser();
   }

   /**
    * @return shallow copy of ArrayList of all active users in the datastore sorted by user name
    */
   public static ArrayList<User> getUsers() throws OseeCoreException {
      return getUsers(UserStatusEnum.Active);
   }

   public static ArrayList<User> getUsersSortedByName() throws OseeCoreException {
      ArrayList<User> users = getUsers();
      Collections.sort(users);
      return users;
   }

   public static ArrayList<User> getUsers(UserStatusEnum userStatus) throws OseeCoreException {
      instance.ensurePopulated();
      if (userStatus == UserStatusEnum.Both) {
         return new ArrayList<User>(instance.userIdToUserCache.values());
      }

      ArrayList<User> users = new ArrayList<User>(instance.userIdToUserCache.size());
      for (User user : instance.userIdToUserCache.values()) {
         if (userStatus == UserStatusEnum.Active && user.isActive()) {
            users.add(user);
         } else if (userStatus == UserStatusEnum.InActive && !user.isActive()) {
            users.add(user);
         }
      }
      return users;
   }

   /**
    * Return sorted list of active User.getName() in database
    * 
    * @return String[]
    */
   public static String[] getUserNames() throws OseeCoreException {
      instance.ensurePopulated();
      String[] userNames = new String[instance.userIdToUserCache.size()];
      int index = 0;
      for (User user : instance.userIdToUserCache.values()) {
         userNames[index++] = user.getDescriptiveName();
      }
      return userNames;
   }
   
   public static String getUserNameById(int userArtifactId){
      String name;
      try {
         User user = null;
         if (userArtifactId == 0) {
            user = UserManager.getUser(SystemUser.OseeSystem);
            userArtifactId = user.getArtId();
         } else {
            user = UserManager.getUserByArtId(userArtifactId);
         }
         name = user.getDescriptiveName();
      } catch (Exception ex) {
         name = "Could not resolve artId: " + userArtifactId;
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      }
      return name;
   }

   public static User getUserByArtId(int userArtifactId) throws OseeCoreException {
      instance.ensurePopulated();
      User user = (User) ArtifactCache.getActive(userArtifactId, BranchManager.getCommonBranch());
      if (user == null) {
         throw new UserNotInDatabase("User requested by artId \"" + userArtifactId + "\" was not found.");
      }
      return user;
   }

   /**
    * This is not the preferred way to get a user. Most likely getUserByUserId() or getUserByArtId() should be used
    * 
    * @param name
    * @return the first user found with the given name
    * @throws OseeCoreException
    */
   public static User getUserByName(String name) throws OseeCoreException {
      instance.ensurePopulated();
      User user = null;
      for (User tempUser : instance.userIdToUserCache.values()) {
         if (tempUser.getDescriptiveName().equals(name)) {
            user = tempUser;
            return user;
         }
      }
      throw new UserNotInDatabase("User requested by name \"" + name + "\" was not found.");
   }

   public static User getUserByUserId(String userId) throws OseeCoreException {
      if (userId == null || userId.equals("")) {
         throw new OseeArgumentException("UserId can't be null or \"\"");
      }

      instance.ensurePopulated();
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

   public static User getUser(IOseeUser userEnum) throws OseeCoreException {
      return getUserByUserId(userEnum.getUserID());
   }

   private synchronized void ensurePopulated() throws OseeCoreException {
      if (!userCacheIsLoaded) {
         Collection<User> dbUsers =
               org.eclipse.osee.framework.jdk.core.util.Collections.castAll(ArtifactQuery.getArtifactsFromType(
                     User.ARTIFACT_NAME, BranchManager.getCommonBranch()));
         for (User user : dbUsers) {
            User previousUser = userIdToUserCache.put(user.getUserId(), user);
            if (previousUser != null) { // if duplicate user id found
               OseeCoreException ex =
                     new UserInDatabaseMultipleTimes(
                           "User of userId \"" + user.getUserId() + "\" in datastore more than once");

               // exception if I am the duplicate user otherwise just log
               if (user.getUserId().equals(ClientSessionManager.getSession().getId())) {
                  throw ex;
               } else {
                  OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
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
      return instance.duringMainUserCreation;
   }

   public static synchronized User createMainUser(IOseeUser userEnum, SkynetTransaction transaction) throws OseeCoreException {
      instance.duringMainUserCreation = true;
      User user = createUser(userEnum, transaction);
      instance.duringMainUserCreation = false;
      return user;
   }

   public static synchronized User createUser(IOseeUser userEnum, SkynetTransaction transaction) throws OseeCoreException {
      instance.ensurePopulated();
      // Determine if user with id has already been created; boot strap issue with dbInit
      User user = instance.userIdToUserCache.get(userEnum.getUserID());
      if (user != null) {
         // Update user with this enum data
         user.setDescriptiveName(userEnum.getName());
         user.setEmail(userEnum.getEmail());
         user.setActive(userEnum.isActive());
      } else {
         user =
               (User) ArtifactTypeManager.addArtifact(User.ARTIFACT_NAME, BranchManager.getCommonBranch(),
                     userEnum.getName());
         user.setActive(userEnum.isActive());
         user.setUserID(userEnum.getUserID());
         user.setEmail(userEnum.getEmail());
         instance.userIdToUserCache.put(user.getUserId(), user);

         // this is here in case a user is created at an unexpected time
         if (!SkynetDbInit.isDbInit()) {
            OseeLog.log(SkynetActivator.class, Level.INFO, "Created user " + user, new Exception(
                  "just wanted the stack trace"));
         }
      }

      user.persistAttributesAndRelations(transaction);
      return user;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException {
      if (transData.branchId != BranchManager.getCommonBranch().getBranchId()) return;
      ArtifactType userType = ArtifactTypeManager.getType(User.ARTIFACT_NAME);

      Collection<Integer> deletedUserArtifactIds =
            transData.getArtifactIdsOfArtifactType(userType, ArtifactModType.Deleted);
      ArrayList<User> usersToRemove = new ArrayList<User>();
      for (User tempUser : userIdToUserCache.values()) {
         if (deletedUserArtifactIds.contains(tempUser.getArtId())) {
            usersToRemove.add(tempUser);
         }
      }
      for (User user : usersToRemove) {
         userIdToUserCache.remove(user.getUserId());
      }

      Collection<Integer> newUserArtifactIds = transData.getArtifactIdsOfArtifactType(userType, ArtifactModType.Added);
      Collection<User> newUsers =
            org.eclipse.osee.framework.jdk.core.util.Collections.castAll(ArtifactQuery.getArtifactsFromIds(
                  newUserArtifactIds, BranchManager.getCommonBranch(), false));
      for (User newUser : newUsers) {
         userIdToUserCache.put(newUser.getUserId(), newUser);
      }

      Collection<Integer> modUserArtifacts = transData.getArtifactIdsOfArtifactType(userType, ArtifactModType.Changed);

      Collection<User> modUsers =
            org.eclipse.osee.framework.jdk.core.util.Collections.castAll(ArtifactQuery.getArtifactsFromIds(
                  modUserArtifacts, BranchManager.getCommonBranch(), false));
      for (User modUser : modUsers) {
         User previousUser = (User) ArtifactCache.getActive(modUser.getArtId(), BranchManager.getCommonBranch());
         if (previousUser != null) {
            userIdToUserCache.remove(previousUser);
         }
         userIdToUserCache.put(modUser.getUserId(), modUser);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.ITransactionsDeletedEventListener#handleTransactionsDeletedEvent(org.eclipse.osee.framework.skynet.core.event.Sender, int[])
    */
   @Override
   public void handleTransactionsDeletedEvent(Sender sender, int[] transactionIds) {
      // TODO Need to handle this case when event sends more data about the contents of the deleted transactions
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener#handleArtifactsPurgedEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts)
    */
   @Override
   public void handleArtifactsPurgedEvent(Sender sender, LoadedArtifacts loadedArtifacts) throws OseeCoreException {
      for (Artifact artifact : loadedArtifacts.getLoadedArtifacts()) {
         if (artifact instanceof User) {
            userIdToUserCache.remove(((User) artifact).getUserId());
         }
      }
   }
}