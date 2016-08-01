/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.user;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.users.AbstractAtsUserService;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * Non-artifact base user service
 *
 * @author Donald G Dunne
 */
public class AtsUserServiceImpl extends AbstractAtsUserService {

   private OrcsApi orcsApi;
   private Log logger;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void start() throws OseeCoreException {
      Conditions.checkNotNull(orcsApi, "OrcsApi");
      logger.info("AtsUserService started");
   }

   public void stop() {
      //
   }

   @Override
   public String getCurrentUserId() throws OseeCoreException {
      if (currentUserId == null) {
         currentUserId = SystemUser.OseeSystem.getUserId();
      }
      return currentUserId;
   }

   // TODO Replace this once server has user account
   @Override
   public IAtsUser getCurrentUser() throws OseeCoreException {
      if (currentUser == null) {
         currentUser = getUserById(getCurrentUserId());
      }
      return currentUser;
   }

   @Override
   protected IAtsUser loadUserByUserIdFromDb(String userId) {
      IAtsUser atsUser = null;
      ResultSet<ArtifactReadable> results =
         orcsApi.getQueryFactory().fromBranch(AtsUtilCore.getAtsBranch()).andIsOfType(CoreArtifactTypes.User).and(
            CoreAttributeTypes.UserId, userId).getResults();
      if (!results.isEmpty()) {
         ArtifactReadable userArt = results.getExactlyOne();
         atsUser = new AtsUser(userArt);
      }
      return atsUser;
   }

   @Override
   protected IAtsUser loadUserByUserNameFromDb(String name) {
      IAtsUser atsUser = null;
      ArtifactReadable userArt =
         orcsApi.getQueryFactory().fromBranch(AtsUtilCore.getAtsBranch()).andIsOfType(CoreArtifactTypes.User).and(
            CoreAttributeTypes.Name, name).getResults().getExactlyOne();
      if (userArt != null) {
         atsUser = new AtsUser(userArt);
      }
      return atsUser;
   }

   @Override
   public boolean isAtsAdmin(IAtsUser user) {
      ensureLoaded();
      Boolean admin = userIdToAdmin.get(user.getUserId());
      if (admin == null) {
         admin = orcsApi.getQueryFactory().fromBranch(AtsUtilCore.getAtsBranch()).andGuid(
            AtsArtifactToken.AtsAdmin.getGuid()).andRelatedTo(CoreRelationTypes.Users_User,
               getUserArt(user)).getCount() == 1;
         userIdToAdmin.put(user.getUserId(), admin);
      }
      return admin;
   }

   private ArtifactReadable getUserArt(IAtsUser user) {
      ensureLoaded();
      if (user.getStoreObject() instanceof ArtifactReadable) {
         return (ArtifactReadable) user.getStoreObject();
      }
      return orcsApi.getQueryFactory().fromBranch(AtsUtilCore.getAtsBranch()).andUuid(
         user.getId()).getResults().getExactlyOne();
   }

   @Override
   public List<IAtsUser> getUsers(Active active) {
      ensureLoaded();
      List<IAtsUser> users = new ArrayList<>();
      for (ArtifactReadable userArt : orcsApi.getQueryFactory().fromBranch(AtsUtilCore.getAtsBranch()).andIsOfType(
         CoreArtifactTypes.User).getResults()) {
         Boolean activeFlag = userArt.getSoleAttributeValue(CoreAttributeTypes.Active, true);
         if (active == Active.Both || active == Active.Active && activeFlag || active == Active.InActive && !activeFlag) {
            users.add(new AtsUser(userArt));
         }
      }
      return users;
   }

   @Override
   protected synchronized void ensureLoaded() {
      if (!loaded) {
         for (ArtifactReadable art : orcsApi.getQueryFactory().fromBranch(AtsUtilCore.getAtsBranch()).andIsOfType(
            CoreArtifactTypes.User).getResults()) {
            AtsUser atsUser = new AtsUser(art);
            userIdToAtsUser.put(art.getSoleAttributeValue(CoreAttributeTypes.UserId, ""), atsUser);
            nameToAtsUser.put(art.getName(), atsUser);
         }
         loaded = true;
      }
   }

   @Override
   public List<IAtsUser> getSubscribed(IAtsWorkItem workItem) throws OseeCoreException {
      ArrayList<IAtsUser> arts = new ArrayList<>();
      for (ArtifactReadable art : ((ArtifactReadable) workItem.getStoreObject()).getRelated(
         AtsRelationTypes.SubscribedUser_User)) {
         arts.add(getUserById((String) art.getSoleAttributeValue(CoreAttributeTypes.UserId)));
      }
      return arts;
   }

   @Override
   public IAtsUser getUserById(long accountId) {
      ArtifactReadable userArt = orcsApi.getQueryFactory().fromBranch(AtsUtilCore.getAtsBranch()).andUuid(
         accountId).getResults().getAtMostOneOrNull();
      return getUserById(userArt.getSoleAttributeValue(CoreAttributeTypes.UserId, null));
   }

}
