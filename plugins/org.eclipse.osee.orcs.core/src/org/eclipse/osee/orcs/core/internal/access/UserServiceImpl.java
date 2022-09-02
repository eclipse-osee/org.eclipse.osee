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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryBuilder;
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
   private final QueryBuilder query;
   private final ConcurrentHashMap<Thread, UserToken> threadToUser = new ConcurrentHashMap<>();

   private final ConcurrentHashMap<String, UserToken> userIdToUser = new ConcurrentHashMap<>();

   public UserServiceImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      query = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON);
      loginKey = OseeProperties.getJwtLoginKey();
   }

   @Override
   public void clearCaches() {
      loginIdToUser.clear();
   }

   @SuppressWarnings("unlikely-arg-type")
   @Override
   public TransactionId createUsers(Iterable<UserToken> users, String comment) {
      ensureLoaded();
      UserToken currentUser = getUser();

      boolean isBootstrap = loginIdToUser.isEmpty();
      // During bootstrap allow user creation when no users have yet been created
      if (!isBootstrap) {
         requireRole(CoreUserGroups.AccountAdmin);
      }

      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(COMMON, getUser(), comment);

      ArtifactToken userGroupHeader = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
         CoreArtifactTokens.UserGroups).asArtifactToken();

      // Create users and relate to user groups
      Map<ArtifactToken, ArtifactToken> userGroupToArtifact = new HashMap<>();
      List<ArtifactReadable> defaultGroups = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
         CoreArtifactTypes.UserGroup).andAttributeIs(CoreAttributeTypes.DefaultGroup, "true").getResults().getList();

      List<ArtifactReadable> existingUsers = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andTypeEquals(
         CoreArtifactTypes.User).getResults().getList();

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
            } else if (currentUser.getRoles().contains(CoreUserGroups.AccountAdmin)) {
               tx.createAttribute(user, CoreAttributeTypes.LoginId, currentUser, loginId);
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

   private synchronized void ensureLoaded() {
      if (loginIdToUser.isEmpty()) {
         for (ArtifactReadable userArtifact : query.andTypeEquals(CoreArtifactTypes.User).follow(
            CoreRelationTypes.Users_Artifact).asArtifacts()) {
            UserToken user = toUser(userArtifact);

            if (user.isValid() && !user.getIdString().isEmpty()) {
               accountIdToUser.put(UserId.valueOf(user.getId()), user);
            }
            String userId = user.getUserId();
            if (user.isValid() && !userId.isEmpty()) {
               userIdToUser.put(userId, user);

               for (String loginId : user.getLoginIds()) {
                  if (Strings.isValid(loginId)) {
                     loginIdToUser.put(loginId, user);
                  }
               }
            }
         }
      }
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

   private ArtifactToken getOrCreate(ArtifactToken userGroup, Map<ArtifactToken, ArtifactToken> userGroupToArtifact, TransactionBuilder tx, ArtifactToken userGroupHeader) {
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
      ensureLoaded();

      UserToken user = accountIdToUser.get(UserId.valueOf(accountId));
      if (user == null) {
         user = UserToken.SENTINEL;
      }
      return user;
   }

   @Override
   public UserToken getUserByUserId(String userId) {
      ensureLoaded();

      UserToken user = userIdToUser.get(userId);
      if (user == null) {
         user = UserToken.SENTINEL;
      }
      return user;
   }

   @Override
   public IUserGroup getUserGroup(ArtifactToken userGroupArt) {
      return new UserGroupImpl(userGroupArt);
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
   }

   @Override
   public void setUserForCurrentThread(String loginId) {
      ensureLoaded();
      UserToken user = loginIdToUser.get(loginId);
      if (user == null) {
         List<ArtifactReadable> userArtifacts = query.andAttributeIs(CoreAttributeTypes.LoginId, loginId).asArtifacts();
         if (userArtifacts.size() == 1) {
            user = toUser(userArtifacts.get(0));
         }
      }
      if (user != null && user.isValid()) {
         threadToUser.put(Thread.currentThread(), user);
      }
   }

   @Override
   public void setUserForCurrentThread(UserId accountId) {
      if (accountId.isValid()) {
         ensureLoaded();
         UserToken user = accountIdToUser.get(accountId);
         if (user == null) {
            List<ArtifactReadable> userArtifacts = query.andId(accountId).asArtifacts();
            if (userArtifacts.size() == 1) {
               user = toUser(userArtifacts.get(0));
               if (user.isValid()) {
                  accountIdToUser.put(user, user);
               }
            }
         }
         if (user != null && user.isValid()) {
            threadToUser.put(Thread.currentThread(), user);
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
         return UserToken.SENTINEL;
      }
   }

   @Override
   public void setUserLoading(boolean loading) {
      ;
   }
}