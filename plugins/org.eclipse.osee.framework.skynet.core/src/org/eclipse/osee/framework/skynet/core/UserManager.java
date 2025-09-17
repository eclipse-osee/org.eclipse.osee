/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.skynet.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactNameComparator;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public final class UserManager {

   public static String DOUBLE_CLICK_SETTING_KEY_EDIT = "onDoubleClickOpenUsingEditMode";
   private static AtomicBoolean showTokenForChangeName;
   private static UserService userService;
   private static boolean duringMainUserCreation;

   private UserManager() {
      // Utility class
   }

   private static UserService getUserService() {
      if (userService == null) {
         userService = OsgiUtil.getService(UserManager.class, OseeClient.class).userService();
      }
      return userService;
   }

   /**
    * Returns the currently authenticated user
    */
   public static User getUser() {
      return (User) getUserService().getUser();
   }

   public static User getUserOrNull() {
      UserToken user = getUserService().getUser();
      if (user.isInvalid()) {
         return null;
      }
      return (User) user;
   }

   public static void releaseUser() {
      getUserService().clearCaches();
   }

   public static void clearCache() {
      getUserService().clearCaches();
   }

   public static List<User> getUsersByUserId(Collection<String> userIds) {
      List<User> users = new ArrayList<>();
      for (String userId : userIds) {
         try {
            User user = (User) getUserService().getUserByUserId(userId);
            if (user != null) {
               users.add(user);
            }
         } catch (UserNotInDatabase ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return users;
   }

   public static List<User> getUsers() {
      return Collections.castAll(getUserService().getActiveUsers());
   }

   public static List<User> getUsersAll() {
      return Collections.castAll(getUserService().getUsers());
   }

   public static List<User> getUsersActiveSortedByName() {
      List<User> users = getUsers();
      users.sort(new ArtifactNameComparator(false));
      return users;
   }

   public static List<User> getUsersAllSortedByName() {
      List<User> users = getUsersAll();
      users.sort(new ArtifactNameComparator(false));
      return users;
   }

   public static String getSafeUserNameById(ArtifactId userArtifactId) {
      try {
         User user = getUser();
         return user.getName();
      } catch (OseeCoreException ex) {
         return ex.getLocalizedMessage();
      }
   }

   public static User getUserByArtId(ArtifactId userArtifactId) {
      return (User) getUserService().getUser(userArtifactId.getId());
   }

   public static User getUserByArtId(long userArtifactId) {
      return (User) getUserService().getUser(userArtifactId);
   }

   public static User getUser(UserToken user) {
      return (User) getUserService().getUser(user);
   }

   public static User getUserByUserId(String userId) {
      return (User) getUserService().getUserByUserId(userId);
   }

   /**
    * @return whether the Authentication manager is in the middle of creating a user
    */
   public static boolean duringMainUserCreation() {
      return duringMainUserCreation;
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

   public static void reloadUser() {
      getUser().reloadAttributesAndRelations();
      getUserService().clearCaches();
   }

   public static boolean isDuringMainUserCreation() {
      return duringMainUserCreation;
   }

   public static void setDuringMainUserCreation(boolean duringMainUserCreation) {
      UserManager.duringMainUserCreation = duringMainUserCreation;
   }

}