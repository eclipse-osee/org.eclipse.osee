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

package org.eclipse.osee.framework.skynet.core.access;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.orcs.rest.model.DatastoreEndpoint;

/**
 * @author Donald G. Dunne
 */
public class UserServiceImpl implements UserService {

   private static List<IUserGroupArtifactToken> userGrps;

   private boolean loading = false;

   private boolean beforeUserCreation = false;

   @Override
   public IUserGroup getUserGroupOrNull(IUserGroupArtifactToken userGroup) {
      Artifact userGroupArt = null;
      if (userGroup instanceof Artifact) {
         userGroupArt = (Artifact) userGroup;
      }
      if (userGroupArt == null) {
         userGroupArt = ArtifactQuery.getArtifactFromId(userGroup, CoreBranches.COMMON);
         return new UserGroupImpl(userGroupArt);
      }
      return null;
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
   public IUserGroup getUserGroup(ArtifactToken userGroupArt) {
      return new UserGroupImpl(userGroupArt);
   }

   /**
    * @return User Groups for current user
    */
   public static List<IUserGroupArtifactToken> getUserGrps() {
      if (userGrps == null) {
         userGrps = new ArrayList<>();
         for (Artifact userGrp : UserManager.getUser().getRelatedArtifacts(CoreRelationTypes.Users_Artifact)) {
            userGrps.add(new UserGroupImpl(userGrp));
         }
      }
      return userGrps;
   }

   @Override
   public Collection<IUserGroupArtifactToken> getMyUserGroups() {
      return UserServiceImpl.getUserGrps();
   }

   @Override
   public boolean isInUserGroup(IUserGroupArtifactToken... userGroups) {
      boolean isInGroup = false;
      Collection<IUserGroupArtifactToken> userGrps = getMyUserGroups();
      for (IUserGroupArtifactToken userGroup : userGroups) {
         if (userGrps.contains(userGroup)) {
            isInGroup = true;
            break;
         }
      }
      return isInGroup;
   }

   @Override
   public Collection<UserToken> getUsers(IUserGroupArtifactToken userGroup) {

      List<UserToken> users = new ArrayList<>();
      Artifact userGrpArt = ArtifactQuery.getArtifactFromToken(userGroup);
      if (userGrpArt != null && userGrpArt.isValid()) {
         List<Artifact> list = userGrpArt.getRelatedArtifacts(CoreRelationTypes.Users_User);
         for (Artifact art : list) {
            User user = (User) art;
            users.add(user);
         }
      }

      return users;
   }

   @Override
   public boolean isUserMember(IUserGroupArtifactToken userGroup, Long id) {
      ArtifactToken art = ArtifactQuery.getArtifactTokenFromId(CoreBranches.COMMON, userGroup);
      if (art.isInvalid()) {
         return false;
      }
      return getUserGroup(userGroup).isMember(id);
   }

   @Override
   public boolean isUserMember(IUserGroupArtifactToken userGroup, ArtifactId user) {
      return isUserMember(userGroup, user.getId());
   }

   @Override
   public UserToken getUser() {
      return UserManager.getUser();
   }

   @Override
   public void setUserForCurrentThread(String loginId) {
      throw new UnsupportedOperationException();
   }

   @Override
   public TransactionId createUsers(Iterable<UserToken> users, String comment) {
      DatastoreEndpoint datastoreEndpoint = ServiceUtil.getOseeClient().getDatastoreEndpoint();
      return datastoreEndpoint.createUsers(users);
   }

   @Override
   public void setUserForCurrentThread(UserId accountId) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isBeforeUserCreation() {
      return beforeUserCreation;
   }

   @Override
   public void setBeforeUserCreation(boolean beforeUserCreation) {
      this.beforeUserCreation = beforeUserCreation;
      if (!OseeProperties.isInDbInit()) {
         throw new OseeStateException("No user creation outside of dbinit");
      }
   }

   @Override
   public UserToken getUserIfLoaded() {
      if (loading || isBeforeUserCreation()) {
         return UserToken.SENTINEL;
      }
      return getUser();
   }

   @Override
   public UserToken getUserIfLoaded(Long accountId) {
      if (loading) {
         return UserToken.SENTINEL;
      }
      return getUser(accountId);
   }

   @Override
   public void setUserLoading(boolean loading) {
      this.loading = loading;
   }

   @Override
   public UserToken getUserByUserId(String userId) {
      return UserManager.getUserByUserId(userId);
   }

   @Override
   public UserToken getUser(Long accountId) {
      return UserManager.getUserByArtId(UserId.valueOf(accountId));
   }

   public static void clearCache() {
      userGrps = null;
   }

}