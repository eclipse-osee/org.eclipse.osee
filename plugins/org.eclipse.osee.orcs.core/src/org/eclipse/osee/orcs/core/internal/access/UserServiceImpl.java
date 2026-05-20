/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.orcs.core.internal.access;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.ApiKeyApi;
import org.eclipse.osee.framework.core.data.ApiKey;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.core.internal.proxy.impl.ArtifactReadOnlyImpl;
import org.eclipse.osee.orcs.data.ArtifactReadableImpl;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * Server Implementation
 *
 * @author Donald G. Dunne
 */

public class UserServiceImpl implements UserService {

   private final ConcurrentHashMap<UserId, UserToken> accountIdToUser = new ConcurrentHashMap<>();
   private final ConcurrentHashMap<String, UserToken> loginIdToUser = new ConcurrentHashMap<>();
   private final String loginKey;
   private final OrcsApi orcsApi;
   private final ConcurrentHashMap<Thread, UserToken> threadToUser = new ConcurrentHashMap<>();
   private final ConcurrentHashMap<String, UserToken> userIdToUser = new ConcurrentHashMap<>();
   private volatile boolean allUsersLoaded = false;
   private final AtomicBoolean backgroundLoadTriggered = new AtomicBoolean(false);

   public UserServiceImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      loginKey = OseeProperties.getJwtLoginKey();
   }

   @Override
   public void clearCaches() {
      synchronized (this) {
         allUsersLoaded = false;
         backgroundLoadTriggered.set(false);
         loginIdToUser.clear();
         accountIdToUser.clear();
         userIdToUser.clear();
      }
   }

   @Override
   public TransactionId createUsers(Iterable<UserToken> users, String comment) {
      ensureLoaded();
      UserToken author = getUser();
      return createUsers(users, author, comment);
   }

   @SuppressWarnings("unlikely-arg-type")
   @Override
   public TransactionId createUsers(Iterable<UserToken> users, UserToken author, String comment) {
      ensureLoaded();

      boolean isBootstrap = loginIdToUser.isEmpty();
      // During bootstrap allow user creation when no users have yet been created
      if (!isBootstrap) {
         requireRole(CoreUserGroups.AccountAdmin);
      }

      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(COMMON, author, comment);

      ArtifactToken userGroupHeader = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
         CoreArtifactTokens.UserGroups).asArtifactToken();

      // Create users and relate to user groups
      Map<ArtifactToken, ArtifactToken> userGroupToArtifact = new HashMap<>();
      List<ArtifactReadable> defaultGroups = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
         CoreArtifactTypes.UserGroup).andAttributeIs(CoreAttributeTypes.DefaultGroup, "true").asArtifacts();

      List<ArtifactReadable> existingUsers =
         orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(CoreArtifactTypes.User).asArtifacts();

      Set<UserToken> bootstrapUsers = BootstrapUsers.getBoostrapUsers();
      for (UserToken userToken : users) {
         if (existingUsers.contains(userToken)) {
            continue;
         }

         ArtifactId user = null;
         if (userToken.isValid()) {
            user = tx.createArtifact(userToken);
         } else {
            user = tx.createArtifact(CoreArtifactTypes.User, userToken.getName());
         }
         tx.setSoleAttributeValue(user, CoreAttributeTypes.Active, userToken.isActive());
         tx.setSoleAttributeValue(user, CoreAttributeTypes.UserId, userToken.getUserId());
         tx.setSoleAttributeValue(user, CoreAttributeTypes.Email, userToken.getEmail());
         for (String loginId : userToken.getLoginIds()) {
            if (bootstrapUsers.contains(userToken) && OseeProperties.isInTest()) {
               tx.createAttributeNoAccess(user, CoreAttributeTypes.LoginId, loginId);
            } else if (author.getRoles().contains(CoreUserGroups.AccountAdmin)) {
               tx.createAttribute(user, CoreAttributeTypes.LoginId, author, loginId);
            }
         }
         for (ArtifactToken userGroup : userToken.getRoles()) {
            ArtifactToken userGroupArt = getOrCreate(userGroup, userGroupToArtifact, tx, userGroupHeader);
            tx.relate(userGroupArt, CoreRelationTypes.Users_User, user);
         }

         for (ArtifactToken userGroup : defaultGroups) {
            ArtifactToken userGroupArt = getOrCreate(userGroup, userGroupToArtifact, tx, userGroupHeader);
            tx.relate(userGroupArt, CoreRelationTypes.Users_User, user);
         }
      }
      return tx.commit();
   }

   /**
    * @implNote Login identifiers are converted to lower case in the {@link AuthenticationRequestFilter} before being
    * assigned as the thread user. All login identifiers read from the user artifact are set to lower case before being
    * set as keys in the {@link #loginIdToUser} map.
    */

   private synchronized void ensureLoaded() {
      if (!allUsersLoaded) {
         for (ArtifactReadable userArtifact : orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andTypeEquals(
            CoreArtifactTypes.User).follow(CoreRelationTypes.Users_Artifact).asArtifacts()) {
            cacheUser(toUser(userArtifact));
         }
         allUsersLoaded = true;
      }
   }

   /**
    * Spawns at most one daemon thread to load all users in the background. Resets the guard on failure to allow retry.
    */
   private void triggerBackgroundLoad() {
      if (!allUsersLoaded && backgroundLoadTriggered.compareAndSet(false, true)) {
         Thread loader = new Thread(() -> {
            try {
               ensureLoaded();
            } finally {
               if (!allUsersLoaded) {
                  backgroundLoadTriggered.set(false);
               }
            }
         }, "UserService-BackgroundLoader");
         loader.setDaemon(true);
         loader.start();
      }
   }

   /**
    * Caches a user token in all lookup maps.
    */
   private void cacheUser(UserToken user) {
      if (user.isValid() && !user.getIdString().isEmpty()) {
         accountIdToUser.put(UserId.valueOf(user.getId()), user);
      }
      String userId = user.getUserId();
      if (user.isValid() && !userId.isEmpty()) {
         userIdToUser.put(userId, user);

         for (String loginId : user.getLoginIds()) {
            if (Strings.isValid(loginId)) {
               loginIdToUser.put(loginId.toLowerCase(), user);
            }
         }
      }
   }

   /**
    * Queries for a single user by login ID.
    */
   private UserToken queryUserByLoginId(String loginId) {
      List<ArtifactReadable> userArtifacts = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andAttributeIs(
         CoreAttributeTypes.LoginId, loginId).follow(CoreRelationTypes.Users_Artifact).asArtifacts();
      if (userArtifacts.size() == 1) {
         UserToken user = toUser(userArtifacts.get(0));
         if (user.isValid()) {
            cacheUser(user);
         }
         return user;
      }
      return UserToken.SENTINEL;
   }

   /**
    * Queries for a single user by checking both LoginId and UserId attributes.
    */
   private UserToken queryUserByLoginIdOrUserId(String identifier) {
      List<ArtifactReadable> userArtifacts = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).and(
         List.of(CoreAttributeTypes.LoginId, CoreAttributeTypes.UserId), identifier).follow(
            CoreRelationTypes.Users_Artifact).asArtifacts();
      if (userArtifacts.size() == 1) {
         UserToken user = toUser(userArtifacts.get(0));
         if (user.isValid()) {
            cacheUser(user);
         }
         return user;
      }
      return UserToken.SENTINEL;
   }

   /**
    * Queries for a single user by artifact ID.
    */
   private UserToken queryUserById(ArtifactId id) {
      List<ArtifactReadable> userArtifacts = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
         id).follow(CoreRelationTypes.Users_Artifact).asArtifacts();
      if (userArtifacts.size() == 1) {
         UserToken user = toUser(userArtifacts.get(0));
         if (user.isValid()) {
            cacheUser(user);
         }
         return user;
      }
      return UserToken.SENTINEL;
   }

   /**
    * Queries for a single user by UserId attribute value.
    */
   private UserToken queryUserByUserId(String userId) {
      List<ArtifactReadable> userArtifacts = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andAttributeIs(
         CoreAttributeTypes.UserId, userId).follow(CoreRelationTypes.Users_Artifact).asArtifacts();
      if (userArtifacts.size() == 1) {
         UserToken user = toUser(userArtifacts.get(0));
         if (user.isValid()) {
            cacheUser(user);
         }
         return user;
      }
      return UserToken.SENTINEL;
   }

   @Override
   public String getLoginKey() {
      return this.loginKey;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Collection<IUserGroupArtifactToken> getMyUserGroups() {
      return this.getUser().getRoles();
   }

   private ArtifactToken getOrCreate(ArtifactToken userGroup, Map<ArtifactToken, ArtifactToken> userGroupToArtifact,
      TransactionBuilder tx, ArtifactToken userGroupHeader) {
      ArtifactToken userGroupArt = userGroupToArtifact.get(userGroup);
      if (userGroupArt == null) {
         userGroupArt = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(userGroup).getArtifactOrNull();
      }
      if (userGroupArt == null) {
         userGroupArt = tx.createArtifact(userGroup);
         tx.addChild(userGroupHeader, userGroupArt);
      }
      userGroupToArtifact.put(userGroup, userGroupArt);
      return userGroupArt;
   }

   @Override
   public UserToken getUser() {
      UserToken user = threadToUser.get(Thread.currentThread());
      if (user == null) {
         user = UserToken.SENTINEL;
      }
      return user;
   }

   @Override
   public UserToken getUser(Long accountId) {
      UserToken user = accountIdToUser.get(UserId.valueOf(accountId));
      if (user == null) {
         user = queryUserById(UserId.valueOf(accountId));
      }
      return user != null ? user : UserToken.SENTINEL;
   }

   @Override
   public UserToken getUserByUserId(String userId) {
      UserToken user = userIdToUser.get(userId);
      if (user == null) {
         user = queryUserByUserId(userId);
      }
      return user != null ? user : UserToken.SENTINEL;
   }

   @Override
   public IUserGroup getUserGroup(ArtifactToken userGroupArt) {
      ArtifactToken art =
         orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(userGroupArt).getArtifactOrSentinal();
      return new UserGroupImpl(art);
   }

   @Override
   public IUserGroup getUserGroup(IUserGroupArtifactToken userGroup) {
      IUserGroup group = getUserGroupOrNull(userGroup);
      if (group != null) {
         return group;
      } else {
         throw new OseeArgumentException("parameter must be artifact");
      }
   }

   @Override
   public IUserGroup getUserGroupOrNull(IUserGroupArtifactToken userGroup) {
      ArtifactReadable userGroupArt = null;
      if (userGroup instanceof ArtifactReadable) {
         userGroupArt = (ArtifactReadable) userGroup;
      }
      if (userGroupArt == null) {
         ArtifactId art =
            orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(userGroup).getArtifactOrSentinal();
         if (art.isValid() && art instanceof ArtifactReadable) {
            userGroupArt = (ArtifactReadable) art;
         }
      }
      if (userGroupArt != null) {
         return new UserGroupImpl(userGroupArt);
      }
      return null;
   }

   @Override
   public UserToken getUserIfLoaded() {
      return UserToken.SENTINEL;
   }

   @Override
   public UserToken getUserIfLoaded(Long accountId) {
      return UserToken.SENTINEL;
   }

   @Override
   public Collection<UserToken> getUsers(IUserGroupArtifactToken userGroup) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isBeforeUserCreation() {
      return false;
   }

   @Override
   public boolean isUserMember(IUserGroupArtifactToken userGroup, Long id) {
      ArtifactToken art =
         orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(userGroup).getArtifactOrSentinal();
      if (art.isInvalid()) {
         return false;
      }
      return getUserGroup(userGroup).isMember(id);
   }

   @Override
   public void setBeforeUserCreation(boolean beforeUserCreation) {
      // do nothing
   }

   @Override
   public void setUserForCurrentThread(String loginId) {
      loginId = loginId.toLowerCase();

      if (Strings.isValid(loginId)) {
         // Try login ID cache first.
         UserToken user = loginIdToUser.get(loginId);
         // If not found as login ID, try as userId attribute.
         if (user == null) {
            user = userIdToUser.get(loginId);
         }
         if (user == null) {
            user = queryUserByLoginIdOrUserId(loginId);
         }
         if (user != null && user.isValid()) {
            threadToUser.put(Thread.currentThread(), user);
         }
         triggerBackgroundLoad();
      }
   }

   @Override
   public void setUserForCurrentThread(UserId accountId) {
      if (accountId.isValid()) {
         UserToken user = accountIdToUser.get(accountId);
         if (user == null) {
            user = queryUserById(accountId);
         }
         if (user != null && user.isValid()) {
            threadToUser.put(Thread.currentThread(), user);
         }
         triggerBackgroundLoad();
      }
   }

   @Override
   public void setUserFromBasic(String credential, ApiKeyApi apiKeyApi) {
      if (!Strings.isValid(credential)) {
         return;
      }

      String lowerCaseCredential = credential.toLowerCase();
      UserToken user = loginIdToUser.get(lowerCaseCredential);

      // Credential might be a userId
      if (user == null) {
         user = userIdToUser.get(lowerCaseCredential);
      }

      // Credential might be an artifact ID
      if (user == null && Strings.isNumeric(credential)) {
         user = accountIdToUser.get(UserId.valueOf(credential));
      }

      if (user == null) {
         ApiKey apiKey = apiKeyApi.getApiKey(credential);

         if (apiKey != null && !apiKey.isExpired()) {
            UserId userArtId = apiKey.getUserArtId();
            user = accountIdToUser.get(userArtId);

            if (user == null) {
               user = queryUserById(userArtId);
            }
         } else {
            user = queryUserByLoginIdOrUserId(lowerCaseCredential);
            // If still not found and it's numeric, try as artifact ID.
            if ((user == null || user.isInvalid()) && Strings.isNumeric(credential)) {
               user = queryUserById(UserId.valueOf(credential));
            }
         }
      }

      if (user != null && user.isValid()) {
         threadToUser.put(Thread.currentThread(), user);
      }
      triggerBackgroundLoad();
   }

   @Override
   public void removeUserFromCurrentThread() {
      threadToUser.remove(Thread.currentThread());
      // Periodically purge entries for threads that are no longer alive
      if (threadToUser.size() > 100) {
         Iterator<Thread> it = threadToUser.keySet().iterator();
         while (it.hasNext()) {
            if (!it.next().isAlive()) {
               it.remove();
            }
         }
      }
   }

   private UserToken toUser(ArtifactReadable userArtifact) {
      try {
         List<ArtifactReadable> groups =
            userArtifact.getRelated(CoreRelationTypes.Users_Artifact, CoreArtifactTypes.UserGroup);

         List<IUserGroupArtifactToken> roles = new ArrayList<>(groups.size());
         for (ArtifactReadable group : groups) {
            roles.add(new UserGroupArtifactToken(group.getId(), group.getName()));
         }
         return UserToken.create(userArtifact.getId(), userArtifact.getName(),
            userArtifact.getSoleAttributeValue(CoreAttributeTypes.Email, ""),
            userArtifact.getSoleAttributeValue(CoreAttributeTypes.UserId, ""),
            userArtifact.getSoleAttributeValue(CoreAttributeTypes.Active, false),
            userArtifact.getAttributeValues(CoreAttributeTypes.LoginId), roles,
            userArtifact.getSoleAttributeValue(CoreAttributeTypes.Phone, ""));
      } catch (Exception ex) {
         OseeLog.log(UserServiceImpl.class, Level.WARNING,
            "Failed to convert user artifact [" + userArtifact.getId() + "]", ex);
         return UserToken.SENTINEL;
      }
   }

   @Override
   public void setUserLoading(boolean loading) {
      // do nothing
   }

   @Override
   public UserToken getUser(UserId userTok) {
      if (userTok == null || userTok.isInvalid()) {
         return UserToken.SENTINEL;
      }
      UserToken user = accountIdToUser.get(userTok);
      if (user == null) {
         user = queryUserById(userTok);
      }
      return user != null ? user : UserToken.SENTINEL;
   }

   @Override
   public Collection<UserToken> getUsers() {
      ensureLoaded();
      return accountIdToUser.values();
   }

   @Override
   public UserToken getCurrentUser() {
      return null;
   }

   @Override
   public UserToken create(UserToken userTok) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getAbridgedEmail(UserToken userTok) {
      UserToken user = getUser(userTok);
      if (user == null || user.isInvalid()) {
         return "";
      }
      if (user instanceof ArtifactReadable) {
         return ((ArtifactReadable) user).getSoleAttributeValue(CoreAttributeTypes.AbridgedEmail, "");
      }
      return "";
   }

   @Override
   public String getAbridgedEmail(ArtifactToken userTok) {
      UserToken user = getUser(userTok);
      if (user == null || user.isInvalid()) {
         return "";
      }
      if (user instanceof ArtifactReadable) {
         return ((ArtifactReadable) user).getSoleAttributeValue(CoreAttributeTypes.AbridgedEmail, "");
      }
      return "";
   }

   @Override
   public String getEmail(ArtifactToken userArt) {
      return null;
   }

   @Override
   public boolean isActive(ArtifactToken userArt) {
      return false;
   }

   @Override
   public boolean isEmailValid(ArtifactToken userArt) {
      return false;
   }

   @Override
   public boolean isSystemUser(ArtifactToken userArt) {
      return false;
   }

   @Override
   public boolean isCurrentUser(ArtifactToken userArt) {
      return false;
   }

   @Override
   public void saveSettings() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setSetting(String key, Long value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setSetting(String key, String value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean getBooleanSetting(String key) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getSetting(String key) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setBooleanSetting(String key, boolean value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setShowTokenForChangeName(boolean set) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isShowTokenForChangeName() {
      throw new UnsupportedOperationException();
   }

   @Override
   public UserToken create(ArtifactToken userArt) {
      ArtifactReadable user = null;
      if (userArt instanceof ArtifactReadOnlyImpl) {
         user = (ArtifactReadOnlyImpl) userArt;
      } else if (userArt instanceof ArtifactReadableImpl) {
         user = (ArtifactReadableImpl) userArt;
      } else {
         user = (ArtifactReadable) orcsApi.getQueryFactory().fromBranch(COMMON).andId(userArt).getArtifactOrNull();
      }
      String name = user.getName();
      String email = user.getSoleAttributeValue(CoreAttributeTypes.Email, "");
      String userId = user.getSoleAttributeValue(CoreAttributeTypes.UserId);
      boolean active = user.getSoleAttributeValue(CoreAttributeTypes.Active);
      List<IUserGroupArtifactToken> roles = new ArrayList<IUserGroupArtifactToken>();
      for (ArtifactReadable userGroupArt : user.getRelated(CoreRelationTypes.Users_Artifact).getList()) {
         IUserGroupArtifactToken userGroup =
            UserGroupArtifactToken.valueOf(userGroupArt.getId(), userGroupArt.getName());
         roles.add(userGroup);
      }
      UserToken userToken = UserToken.create(user.getId(), name, email, userId, active, roles);
      userToken.getLoginIds().addAll(user.getAttributeValues(CoreAttributeTypes.LoginId));
      return userToken;
   }

}