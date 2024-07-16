/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.internal.users;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.cache.admin.Cache;
import org.eclipse.osee.cache.admin.CacheAdmin;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.UserDataStoreException;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserAdmin;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;

/**
 * @author Roberto E. Escobar
 */
public class UserAdminImpl implements UserAdmin {

   private UserKeysProvider keysProvider;
   private LazyObject<Cache<String, User>> cacheProvider;
   private CurrentUserProvider currentUserProvider;
   private UserArtifactEventListener userArtifactEventListener;

   private CacheAdmin cacheAdmin;

   public void setCacheAdmin(CacheAdmin cacheAdmin) {
      this.cacheAdmin = cacheAdmin;
   }

   public void start() {

      UserService userService = OsgiUtil.getService(getClass(), OseeClient.class).userService();

      keysProvider = new UserKeysProvider(userService);
      cacheProvider = new UserCacheProvider(cacheAdmin, new UserDataLoader(userService), keysProvider);
      currentUserProvider = new CurrentUserProvider(cacheProvider);
      userArtifactEventListener = new UserArtifactEventListener(cacheProvider, keysProvider);

      OseeEventManager.addListener(userArtifactEventListener);
   }

   public void stop() {
      if (userArtifactEventListener != null) {
         OseeEventManager.removeListener(userArtifactEventListener);
         userArtifactEventListener = null;
      }

      if (currentUserProvider != null) {
         currentUserProvider.invalidate();
         currentUserProvider = null;
      }

      if (cacheProvider != null) {
         cacheProvider.invalidate();
         cacheProvider = null;
      }

      if (keysProvider != null) {
         keysProvider.invalidate();
         keysProvider = null;
      }
   }

   @Override
   public void reset() {
      keysProvider.invalidate();
      currentUserProvider.invalidate();
      cacheProvider.invalidate();
   }

   private Cache<String, User> getCache() {
      return cacheProvider.get();
   }

   @Override
   public boolean isDuringCurrentUserCreation() {
      return currentUserProvider.isDuringCurrentUserCreation();
   }

   @Override
   public User getCurrentUser() {
      if (isDuringCurrentUserCreation()) {
         return getUserByUserId(ClientSessionManager.getCurrentUserToken().getUserId());
      }
      return currentUserProvider.get();
   }

   @Override
   public User getCurrentUserOrNull() {
      for (User user : getUsersAll()) {
         if (user.getUserId().equals(ClientSessionManager.getCurrentUserToken().getUserId())) {
            return user;
         }
      }
      return null;
   }

   @Override
   public void releaseCurrentUser() {
      currentUserProvider.invalidate();
   }

   @Override
   public User getUserByUserId(String userId) {
      Conditions.checkNotNullOrEmpty(userId, "userId");
      Cache<String, User> cache = getCache();
      User user = null;
      try {
         user = cache.get(userId);
      } catch (Exception ex) {
         UserDataStoreException userEx = unwrapUserException(ex);
         if (userEx != null) {
            throw userEx;
         } else {
            OseeCoreException.wrapAndThrow(ex);
         }
      }
      return user;
   }

   private UserDataStoreException unwrapUserException(Throwable ex) {
      UserDataStoreException toReturn = null;
      Throwable cause = ex.getCause();
      if (cause instanceof UserDataStoreException) {
         toReturn = (UserDataStoreException) cause;
      } else if (cause != null) {
         toReturn = unwrapUserException(cause);
      }
      return toReturn;
   }

   @Override
   public List<User> getActiveUsers() {
      Cache<String, User> cache = getCache();
      List<User> activeUsers = new ArrayList<>();
      Iterable<User> all = null;
      try {
         all = cache.getAll();

         for (User user : all) {
            if (user.isActive()) {
               activeUsers.add(user);
            }
         }
      } catch (Exception e) {
         OseeCoreException.wrapAndThrow(e);
      }

      return activeUsers;
   }

   @Override
   public List<User> getUsersAll() {
      Cache<String, User> cache = getCache();
      List<User> users = new ArrayList<>();
      Iterable<User> all = null;
      try {
         all = cache.getAll();

         for (User user : all) {
            users.add(user);
         }
      } catch (Exception e) {
         OseeCoreException.wrapAndThrow(e);
      }

      return users;
   }

   @Override
   public List<User> getActiveUsersSortedByName() {
      List<User> users = getActiveUsers();
      Collections.sort(users);
      return users;
   }

   @Override
   public List<User> getUsersAllSortedByName() {
      List<User> users = getUsersAll();
      Collections.sort(users);
      return users;
   }

   @Override
   public String[] getUserNames() {
      List<User> allUsers = getUsersAll();
      String[] userNames = new String[allUsers.size()];
      int index = 0;
      for (User user : allUsers) {
         userNames[index++] = user.getName();
      }
      return userNames;
   }

   /**
    * This is not the preferred way to get a user. Most likely getUserByUserId() or getUserByArtId() should be used
    *
    * @return the first user found with the given name
    */
   @Override
   public User getUserByName(String name) {
      Conditions.checkNotNullOrEmpty(name, "user name");

      // check cached users first
      User toReturn = checkIterableForName(getCache().getAllPresent(), name);
      if (toReturn == null) {
         toReturn = checkIterableForName(getUsersAll(), name);
      }

      if (toReturn == null) {
         throw new UserNotInDatabase("User requested by name [%s] was not found.", name);
      }

      return toReturn;
   }

   private User checkIterableForName(Iterable<User> users, String name) {
      User toReturn = null;
      for (User tempUser : users) {
         if (name.equals(tempUser.getName())) {
            toReturn = tempUser;
            break;
         }
      }
      return toReturn;
   }

   @Override
   public User getUser(UserToken user) {
      Conditions.checkNotNull(user, "user data");
      return getUserByUserId(user.getUserId());
   }

   @Override
   public String getUserNameById(ArtifactId userArtifactId) {
      User user = getUserByArtId(userArtifactId);
      if (user == null) {
         throw new UserNotInDatabase("Unable to find user with artId[%s]", userArtifactId);
      }
      String name = user.getName();
      return name;
   }

   @Override
   public String getSafeUserNameById(ArtifactId userArtifactId) {
      String name = "unknown";
      if (userArtifactId != null) {
         try {
            name = getUserNameById(userArtifactId);
         } catch (Exception ex) {
            name = String.format("Could not resolve user with artId[%s]", userArtifactId);
         }
      }
      return name;
   }

   @Override
   public User getUserByArtId(ArtifactId userArtifactId) {
      User toReturn = null;
      if (userArtifactId.isInvalid()) {
         toReturn = getUser(SystemUser.OseeSystem);
      } else {
         // check cached users first
         toReturn = checkIterableForId(getCache().getAllPresent(), userArtifactId);
         if (toReturn == null) {
            toReturn = checkIterableForId(getUsersAll(), userArtifactId);
         }
      }
      return toReturn;
   }

   private User checkIterableForId(Iterable<User> users, ArtifactId id) {
      User toReturn = null;
      for (User tempUser : users) {
         if (id.equals(tempUser)) {
            toReturn = tempUser;
            break;
         }
      }
      return toReturn;
   }
}