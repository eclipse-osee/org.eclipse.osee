/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.rest.internal.config;

import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.core.users.AbstractAtsUserService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G. Dunne
 */
public class AtsUserServiceServerImpl extends AbstractAtsUserService {

   private OrcsApi orcsApi;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public String getCurrentUserId() {
      return SystemUser.OseeSystem.getUserId();
   }

   @Override
   public AtsUser getCurrentUser() {
      UserToken user = orcsApi.userService().getUser();
      return getUserById(user);
   }

   @Override
   public AtsUser getCurrentUserNoCache() {
      return getCurrentUser();
   }

   private ArtifactReadable getArtifactOrSentinel(ArtifactId artifactId) {
      return getQuery().andId(artifactId).getResults().getAtMostOneOrDefault(ArtifactReadable.SENTINEL);
   }

   private QueryBuilder getQuery() {
      return orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON);
   }

   public static AtsUser valueOf(ArtifactReadable userArt) {
      AtsUser atsUser = new AtsUser();
      atsUser.setName(userArt.getName());
      atsUser.setStoreObject(userArt);
      atsUser.setUserId(userArt.getSoleAttributeAsString(CoreAttributeTypes.UserId, ""));
      atsUser.setEmail(userArt.getSoleAttributeAsString(CoreAttributeTypes.Email, ""));
      atsUser.setActive(userArt.getSoleAttributeValue(CoreAttributeTypes.Active, true));
      atsUser.setId(userArt.getId());
      atsUser.getLoginIds().addAll(userArt.getAttributeValues(CoreAttributeTypes.LoginId));
      return atsUser;
   }

   @Override
   public Collection<AtsUser> getUsers() {
      return configurationService.getConfigurations().getUsers();
   }

   @Override
   public boolean isLoadValid() {
      return true;
   }

   @Override
   protected AtsUser loadUserByUserId(String userId) {
      ArtifactReadable userArt =
         getQuery().andTypeEquals(CoreArtifactTypes.User).andAttributeIs(CoreAttributeTypes.UserId,
            userId).getResults().getAtMostOneOrDefault(ArtifactReadable.SENTINEL);
      if (userArt.isValid()) {
         return valueOf(userArt);
      }
      return null;
   }

   @Override
   protected AtsUser loadUserByUserName(String name) {
      ArtifactReadable userArt =
         getQuery().andTypeEquals(CoreArtifactTypes.User).andNameEquals(name).getResults().getAtMostOneOrDefault(
            ArtifactReadable.SENTINEL);
      if (userArt.isValid()) {
         return valueOf(userArt);
      }
      return null;
   }

   @Override
   protected AtsUser loadUserByUserId(Long accountId) {
      AtsUser user = null;
      ArtifactId userArt = getArtifactOrSentinel(ArtifactId.valueOf(accountId));
      if (userArt.isValid()) {
         user = valueOf((ArtifactReadable) userArt);
      }
      return user;
   }

   @Override
   public AtsUser getUserById(ArtifactId id) {
      ArtifactReadable userArt = null;
      if (id instanceof ArtifactReadable) {
         userArt = (ArtifactReadable) id;
      } else {
         userArt = getQuery().andId(id).getResults().getExactlyOne();
      }
      return valueOf(userArt);
   }

   @Override
   public void setCurrentUser(AtsUser user) {
      // do nothing
   }

   @Override
   public void clearCaches() {
      // do nothing
   }

   @Override
   public boolean isAtsAdmin() {
      return orcsApi.userService().isInUserGroup(AtsUserGroups.AtsAdmin);
   }

   @Override
   public boolean isAtsDeleteWorkflowAdmin() {
      return orcsApi.userService().isUserMember(AtsUserGroups.AtsDeleteWorkflowAdmin, getCurrentUser());
   }
}