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
package org.eclipse.osee.ats.core.client.internal.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.core.HttpHeaders;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.IAtsConfigurationsService;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.client.IAtsUserServiceClient;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.users.AbstractAtsUserService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * Non-artifact base user service
 *
 * @author Donald G Dunne
 */
public class AtsUserServiceClientImpl extends AbstractAtsUserService implements IAtsUserServiceClient {

   private IAtsConfigurationsService configurationService;

   public AtsUserServiceClientImpl() {
      // For OSGI Instantiation
   }

   public void setConfigurationService(IAtsConfigurationsService configurationService) {
      this.configurationService = configurationService;
   }

   @Override
   public void reloadCache() {
      configurationService.getConfigurationsWithPend();
      super.reloadCache();
   }

   @Override
   public IAtsUser getUserFromOseeUser(User user) {
      IAtsUser atsUser = userIdToAtsUser.get(user.getUserId());
      if (atsUser == null) {
         atsUser = createFromArtifact(user);
         userIdToAtsUser.put(user.getUserId(), atsUser);
      }
      return atsUser;
   }

   @Override
   public User getOseeUser(IAtsUser atsUser) {
      User oseeUser = null;
      if (atsUser.getStoreObject() instanceof User) {
         oseeUser = (User) atsUser.getStoreObject();
      } else {
         oseeUser = getOseeUserById(atsUser.getUserId());
      }
      return oseeUser;
   }

   @Override
   public User getCurrentOseeUser() {
      IAtsUser user = getCurrentUser();
      return getOseeUser(user);
   }

   @Override
   public Collection<? extends User> toOseeUsers(Collection<? extends IAtsUser> users) {
      List<User> results = new LinkedList<>();
      for (IAtsUser user : users) {
         results.add(getOseeUser(user));
      }
      return results;
   }

   @Override
   public Collection<IAtsUser> getAtsUsers(Collection<? extends Artifact> artifacts) {
      List<IAtsUser> users = new LinkedList<>();
      for (Artifact artifact : artifacts) {
         if (artifact instanceof User) {
            User user = (User) artifact;
            IAtsUser atsUser = getUserFromOseeUser(user);
            users.add(atsUser);
         }
      }
      return users;
   }

   @Override
   public Collection<User> getOseeUsers(Collection<? extends IAtsUser> users) {
      List<User> results = new LinkedList<>();
      for (IAtsUser user : users) {
         results.add(getOseeUser(user));
      }
      return results;
   }

   @Override
   public User getOseeUserById(String userId) {
      return getOseeUser(getUserById(userId));
   }

   @Override
   public List<User> getOseeUsersSorted(Active active) {
      List<IAtsUser> activeUsers = getUsers(active);
      List<User> oseeUsers = new ArrayList<>();
      oseeUsers.addAll(getOseeUsers(activeUsers));
      Collections.sort(oseeUsers);
      return oseeUsers;
   }

   @Override
   public List<IAtsUser> getSubscribed(IAtsWorkItem workItem) {
      ArrayList<IAtsUser> arts = new ArrayList<>();
      for (Artifact art : ((Artifact) workItem.getStoreObject()).getRelatedArtifacts(
         AtsRelationTypes.SubscribedUser_User)) {
         arts.add(getUserById((String) art.getSoleAttributeValue(CoreAttributeTypes.UserId)));
      }
      return arts;
   }

   @Override
   public IAtsUser getUserById(long accountId) {
      return getUserFromOseeUser(UserManager.getUserByArtId(accountId));
   }

   @Override
   public String getCurrentUserId() {
      return UserManager.getUser().getUserId();
   }

   @Override
   public boolean isAtsAdmin(IAtsUser user) {
      return configurationService.getConfigurations().getAtsAdmins().contains(user.getStoreObject());
   }

   @Override
   public boolean isAtsAdmin(boolean useCache) {
      if (!useCache) {
         Artifact atsAdmin = (Artifact) AtsClientService.get().getQueryService().getArtifact(AtsArtifactToken.AtsAdmin);
         return atsAdmin.isRelated(CoreRelationTypes.Users_User, getCurrentOseeUser());
      }
      return isAtsAdmin();
   }

   @Override
   public boolean isAtsAdmin() {
      return configurationService.getConfigurations().getAtsAdmins().contains(getCurrentUser());
   }

   @Override
   public List<? extends IAtsUser> getUsers() {
      return configurationService.getConfigurations().getUsers();
   }

   @Override
   protected IAtsUser loadUserFromDbByUserId(String userId) {
      Artifact userArt = null;
      try {
         userArt = ArtifactQuery.getArtifactFromTypeAndAttribute(CoreArtifactTypes.User, CoreAttributeTypes.UserId,
            userId, AtsClientService.get().getAtsBranch());
         return createFromArtifact(userArt);
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return null;
   }

   private AtsUser createFromArtifact(Artifact userArt) {
      AtsUser atsUser = new AtsUser();
      atsUser.setName(userArt.getName());
      atsUser.setStoreObject(userArt);
      atsUser.setUserId(userArt.getSoleAttributeValue(CoreAttributeTypes.UserId, ""));
      atsUser.setEmail(userArt.getSoleAttributeValue(CoreAttributeTypes.Email, ""));
      atsUser.setActive(userArt.getSoleAttributeValue(CoreAttributeTypes.Active, true));
      atsUser.setId(userArt.getId());
      return atsUser;
   }

   @Override
   protected IAtsUser loadUserFromDbByUserName(String name) {
      Artifact userArt = null;
      try {
         userArt = ArtifactQuery.getArtifactFromTypeAndAttribute(CoreArtifactTypes.User, CoreAttributeTypes.Name, name,
            AtsClientService.get().getAtsBranch());
         return createFromArtifact(userArt);
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return null;
   }

   @Override
   public IAtsUser getUserByArtifactId(ArtifactId artifact) {
      return getUserFromOseeUser((User) artifact);
   }

   @Override
   protected IAtsUser loadUserByAccountId(Long accountId) {
      IAtsUser user = null;
      ArtifactId userArt = ArtifactQuery.getArtifactFromId(accountId, AtsClientService.get().getAtsBranch());
      if (userArt != null) {
         user = createFromArtifact((Artifact) userArt);
      }
      return user;
   }

   @Override
   public List<IAtsUser> getUsersFromDb() {
      List<IAtsUser> users = new ArrayList<>();
      for (ArtifactId userArt : ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.User, CoreBranches.COMMON)) {
         AtsUser atsUser = createFromArtifact((Artifact) userArt);
         users.add(atsUser);
      }
      return users;
   }

   @Override
   public IAtsUser getUserByAccountId(HttpHeaders httpHeaders) {
      throw new UnsupportedOperationException();
   }

}
