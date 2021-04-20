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

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G. Dunne
 */
public class UserServiceImpl implements UserService {
   private final OrcsApi orcsApi;
   private final ConcurrentHashMap<Thread, UserToken> threadToUser = new ConcurrentHashMap<>();
   private final ConcurrentHashMap<String, UserToken> emailToUser = new ConcurrentHashMap<>();
   private final QueryBuilder query;

   public UserServiceImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      query = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON);
   }

   public IUserGroup getOseeAdmin() {
      return getUserGroup(CoreUserGroups.OseeAdmin);
   }

   public IUserGroup getOseeAccessAdmin() {
      return getUserGroup(CoreUserGroups.OseeAccessAdmin);
   }

   @Override
   public IUserGroup getUserGroup(IUserGroupArtifactToken userGroup) {
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
      } else {
         throw new OseeArgumentException("parameter must be artifact");
      }
   }

   @Override
   public IUserGroup getUserGroup(ArtifactToken userGroupArt) {
      return new UserGroupImpl(userGroupArt);
   }

   @Override
   public Collection<IUserGroupArtifactToken> getMyUserGroups() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isInUserGroup(IUserGroupArtifactToken... userGroups) {
      Collection<IUserGroupArtifactToken> myUserGroups = getMyUserGroups();
      for (IUserGroupArtifactToken userGrp : userGroups) {
         if (myUserGroups.contains(userGrp)) {
            return true;
         }
      }
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
   public Collection<UserToken> getUsers(IUserGroupArtifactToken userGroup) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isUserMember(IUserGroupArtifactToken userGroup, ArtifactId user) {
      return isUserMember(userGroup, user.getId());
   }

   @Override
   public UserToken getUser() {
      UserToken user = threadToUser.get(Thread.currentThread());
      if (user == null) {
         user = UserToken.SENTINEL;
      }
      return user;
   }

   private synchronized void ensureLoaded() {
      if (emailToUser.isEmpty()) {
         for (ArtifactReadable userArtifact : query.andTypeEquals(CoreArtifactTypes.User).asArtifacts()) {
            UserToken user = toUser(userArtifact);
            if (Strings.isValid(user.getEmail())) {
               emailToUser.put(user.getEmail(), user);
            }
         }
      }
   }

   private UserToken toUser(ArtifactReadable userArtifact) {
      return UserToken.create(userArtifact.getId(), userArtifact.getName(),
         userArtifact.getSoleAttributeValue(CoreAttributeTypes.Email),
         userArtifact.getSoleAttributeValue(CoreAttributeTypes.UserId),
         userArtifact.getSoleAttributeValue(CoreAttributeTypes.Active),
         userArtifact.getAttributeValues(CoreAttributeTypes.LoginId));
   }

   @Override
   public void setUserForCurrentThread(String userEmail) {
      ensureLoaded();
      UserToken user = emailToUser.get(userEmail);
      if (user == null) {
         ArtifactReadable userArtifact =
            query.andAttributeIs(CoreAttributeTypes.Email, userEmail).asArtifactOrSentinel();
         if (userArtifact.isValid()) {
            user = toUser(userArtifact);
         }
      }
      if (user != null) {
         threadToUser.put(Thread.currentThread(), user);
      }
   }
}