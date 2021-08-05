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
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.data.UserTokens;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
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

   private static UserServiceImpl userService;
   private static List<IUserGroupArtifactToken> userGrps;

   public static UserService instance() {
      return getUserService();
   }

   public static IUserGroup getOseeAdmin() {
      return get(CoreUserGroups.OseeAdmin);
   }

   public static IUserGroup getOseeAccessAdmin() {
      return get(CoreUserGroups.OseeAccessAdmin);
   }

   public static IUserGroup get(IUserGroupArtifactToken userGroupArtToken) {
      return getUserService().getUserGroup(userGroupArtToken);
   }

   @Override
   public IUserGroup getUserGroup(IUserGroupArtifactToken userGroup) {
      Artifact userGroupArt = null;
      if (userGroup instanceof Artifact) {
         userGroupArt = (Artifact) userGroup;
      }
      if (userGroupArt == null) {
         userGroupArt = ArtifactQuery.getArtifactFromId(userGroup, CoreBranches.COMMON);
      }
      if (userGroupArt != null) {
         return new UserGroupImpl(userGroupArt);
      } else {
         throw new OseeArgumentException("parameter must be artifact");
      }
   }

   @Override
   public IUserGroup getUserGroup(ArtifactToken userGroupArt) {
      return new UserGroupImpl(userGroupArt);
   }

   private static UserService getUserService() {
      if (userService == null) {
         userService = new UserServiceImpl();
      }
      return userService;
   }

   public static Collection<IUserGroupArtifactToken> getUserGrps() {
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

   public static boolean isInUserGrp(IUserGroupArtifactToken... userGroups) {
      return getUserService().isInUserGroup(userGroups);
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
      UserTokens userToks = new UserTokens();
      userToks.setAccount(SystemUser.OseeSystem);
      users.forEach(userToks::addUser);
      return datastoreEndpoint.createUsers(userToks);
   }
}