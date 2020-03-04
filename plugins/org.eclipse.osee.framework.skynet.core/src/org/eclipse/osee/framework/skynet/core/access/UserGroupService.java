/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.access;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.IUserGroupService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class UserGroupService implements IUserGroupService {

   private static UserGroupService userGroupService;

   public static IUserGroup getOseeAdmin() {
      return get(CoreUserGroups.OseeAdmin);
   }

   public static IUserGroup getOseeAccessAdmin() {
      return get(CoreUserGroups.OseeAccessAdmin);
   }

   public static IUserGroup get(IUserGroupArtifactToken userGroupArtToken) {
      return getUserGroupService().getUserGroup(userGroupArtToken);
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

   private static IUserGroupService getUserGroupService() {
      if (userGroupService == null) {
         userGroupService = new UserGroupService();
      }
      return userGroupService;
   }

   public static Collection<IUserGroupArtifactToken> getUserGrps() {
      List<IUserGroupArtifactToken> userGrps = new ArrayList<>();
      for (Artifact userGrp : UserManager.getUser().getRelatedArtifacts(CoreRelationTypes.Users_Artifact)) {
         userGrps.add(new UserGroupImpl(userGrp));
      }
      return userGrps;
   }

   @Override
   public Collection<IUserGroupArtifactToken> getMyUserGroups() {
      return UserGroupService.getUserGrps();
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
      return getUserGroupService().isInUserGroup(userGroups);
   }

   @Override
   public boolean isUserMember(IUserGroupArtifactToken userGroup, Long id) {
      ArtifactToken art = ArtifactQuery.getArtifactTokenFromId(CoreBranches.COMMON, userGroup);
      if (art.isInvalid()) {
         return false;
      }
      return getUserGroup(userGroup).isMember(id);
   }
}
