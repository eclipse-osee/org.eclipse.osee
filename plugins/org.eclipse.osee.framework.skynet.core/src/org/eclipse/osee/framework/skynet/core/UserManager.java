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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.eclipse.osee.cache.admin.CacheAdmin;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.internal.users.UserAdminImpl;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Roberto E. Escobar
 */
public final class UserManager {

   public static String DOUBLE_CLICK_SETTING_KEY_ART_EDIT = "onDoubleClickOpenUsingArtifactEditor";
   public static String DOUBLE_CLICK_SETTING_KEY_EDIT = "onDoubleClickOpenUsingEditMode";
   private static AtomicBoolean showTokenForChangeName;

   private static final LazyObject<UserAdmin> provider = new LazyObject<UserAdmin>() {

      @Override
      protected FutureTask<UserAdmin> createLoaderTask() {
         Callable<UserAdmin> callable = new Callable<UserAdmin>() {

            @Override
            public UserAdmin call() throws Exception {
               UserAdminImpl userAdmin = new UserAdminImpl();

               CacheAdmin cacheAdmin = ServiceUtil.getCacheAdmin();
               userAdmin.setCacheAdmin(cacheAdmin);

               userAdmin.start();

               return userAdmin;
            }

         };
         return new FutureTask<>(callable);
      }

   };

   private UserManager() {
      // Utility class
   }

   private static UserAdmin getUserAdmin() {
      return provider.get();
   }

   /**
    * Returns the currently authenticated user
    */
   public static User getUser() {
      return getUserAdmin().getCurrentUser();
   }

   public static void releaseUser() {
      getUserAdmin().releaseCurrentUser();
   }

   public static void clearCache() {
      getUserAdmin().reset();
   }

   public static List<User> getUsersByUserId(Collection<String> userIds) {
      List<User> users = new ArrayList<>();
      for (String userId : userIds) {
         try {
            User user = getUserAdmin().getUserByUserId(userId);
            if (user != null) {
               users.add(user);
            }
         } catch (UserNotInDatabase ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return users;
   }

   /**
    * @return shallow copy of ArrayList of all active users in the datastore sorted by user name
    */
   public static List<User> getUsers() {
      return getUserAdmin().getActiveUsers();
   }

   public static List<User> getUsersAll() {
      return getUserAdmin().getUsersAll();
   }

   public static List<User> getUsersSortedByName() {
      return getUserAdmin().getActiveUsersSortedByName();
   }

   public static List<User> getUsersAllSortedByName() {
      return getUserAdmin().getUsersAllSortedByName();
   }

   /**
    * Return sorted list of active User.getName() in database
    */
   public static String[] getUserNames() {
      return getUserAdmin().getUserNames();
   }

   public static String getSafeUserNameById(ArtifactId userArtifactId) {
      UserAdmin userAdmin = null;
      try {
         userAdmin = getUserAdmin();
      } catch (OseeCoreException ex) {
         // Do nothing;
      }

      String name;
      if (userAdmin == null) {
         name = String.format("Unable resolve user by artId[%s] since userAdmin was unavailable", userArtifactId);
      } else {
         name = userAdmin.getSafeUserNameById(userArtifactId);
      }
      return name;
   }

   public static User getUserByArtId(ArtifactId userArtifactId) {
      return getUserAdmin().getUserByArtId(userArtifactId);
   }

   public static User getUserByArtId(long userArtifactId) {
      return getUserAdmin().getUserByArtId(ArtifactId.valueOf(userArtifactId));
   }

   /**
    * This is not the preferred way to get a user. Most likely getUserByUserId() or getUserByArtId() should be used
    *
    * @return the first user found with the given name
    */
   public static User getUserByName(String name) {
      return getUserAdmin().getUserByName(name);
   }

   public static User getUser(UserToken user) {
      return getUserAdmin().getUser(user);
   }

   public static User getUserByUserId(String userId) {
      return getUserAdmin().getUserByUserId(userId);
   }

   /**
    * @return whether the Authentication manager is in the middle of creating a user
    */
   public static boolean duringMainUserCreation() {
      return getUserAdmin().isDuringCurrentUserCreation();
   }

   public static User createUser(UserToken userToken, SkynetTransaction transaction) {
      return getUserAdmin().createUser(userToken, transaction);
   }

   public static String getSetting(String key) {
      return getUser().getSetting(key);
   }

   public static String getSetting(Long key) {
      return getUser().getSetting(String.valueOf(key));
   }

   public static boolean getBooleanSetting(String key) {
      return getUser().getBooleanSetting(key);
   }

   public static void setSetting(String key, String value) {
      getUser().setSetting(key, value);
   }

   public static void setSetting(String key, Long value) {
      getUser().setSetting(key, String.valueOf(value));
   }

   public static void setShowTokenForChangeName(boolean showTokenForChangeName) {
      UserManager.showTokenForChangeName.set(showTokenForChangeName);
      getUser().setBooleanSetting(OseeProperties.OSEE_SHOW_TOKEN_FOR_CHANGE_NAME, showTokenForChangeName);
   }

   public synchronized static boolean isShowTokenForChangeName() {
      if (showTokenForChangeName == null) {
         showTokenForChangeName = new AtomicBoolean(false);
         showTokenForChangeName.set(getUser().getBooleanSetting(OseeProperties.OSEE_SHOW_TOKEN_FOR_CHANGE_NAME));
      }
      return showTokenForChangeName.get();
   }

}