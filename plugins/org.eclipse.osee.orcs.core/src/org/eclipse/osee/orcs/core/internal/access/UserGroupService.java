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
package org.eclipse.osee.orcs.core.internal.access;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.IUserGroupService;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class UserGroupService implements IUserGroupService {

   private static UserGroupService userGroupService;
   private OrcsApi orcsApi;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public static IUserGroup getOseeAdmin() {
      return get(CoreUserGroups.OseeAdmin);
   }

   public static IUserGroup getOseeAccessAdmin() {
      return get(CoreUserGroups.OseeAccessAdmin);
   }

   private static IUserGroup get(IUserGroupArtifactToken userGroupArtToken) {
      return getUserGroupService().getUserGroup(userGroupArtToken);
   }

   private static IUserGroupService getUserGroupService() {
      if (userGroupService == null) {
         userGroupService = new UserGroupService();
      }
      return userGroupService;
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
   public Collection<IUserGroupArtifactToken> getMyUserGroups() {
      return UserGroupService.getUserGroupService().getMyUserGroups();
   }

   @Override
   public boolean isInUserGroup(IUserGroupArtifactToken... userGroups) {
      Collection<IUserGroupArtifactToken> myUserGroups = getUserGroupService().getMyUserGroups();
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

}
