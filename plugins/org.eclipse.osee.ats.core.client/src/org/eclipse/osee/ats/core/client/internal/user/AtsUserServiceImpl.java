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
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.client.IAtsUserServiceClient;
import org.eclipse.osee.ats.core.client.util.AtsGroup;
import org.eclipse.osee.ats.core.users.AbstractAtsUserService;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * Non-artifact base user service
 *
 * @author Donald G Dunne
 */
public class AtsUserServiceImpl extends AbstractAtsUserService implements IAtsUserServiceClient {

   /************************************************
    ** IAtsUserService implementations
    ************************************************/

   @Override
   public String getCurrentUserId() throws OseeCoreException {
      if (currentUserId == null) {
         currentUserId = ClientSessionManager.getCurrentUserToken().getUserId();
      }
      return currentUserId;
   }

   @Override
   protected IAtsUser loadUserByUserNameFromDb(String name) {
      IAtsUser atsUser = null;
      User user = null;
      try {
         user = (User) ArtifactQuery.getArtifactFromTypeAndAttribute(CoreArtifactTypes.User, CoreAttributeTypes.Name,
            name, AtsUtilCore.getAtsBranch());
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      if (user != null) {
         atsUser = new AtsUser(user);
      }
      return atsUser;
   }

   @Override
   protected IAtsUser loadUserByUserIdFromDb(String userId) {
      IAtsUser atsUser = null;
      User user = (User) ArtifactQuery.getArtifactFromTypeAndAttribute(CoreArtifactTypes.User,
         CoreAttributeTypes.UserId, userId, AtsUtilCore.getAtsBranch());

      if (user != null) {
         atsUser = getUserFromOseeUser(user);
      }
      return atsUser;
   }

   @Override
   public boolean isAtsAdmin(IAtsUser user) {
      ensureLoaded();
      Boolean admin = userIdToAdmin.get(user.getUserId());
      if (admin == null) {
         admin = AtsGroup.AtsAdmin.isMember(user);
         userIdToAdmin.put(user.getUserId(), admin);
      }
      return admin;
   }

   @Override
   public List<IAtsUser> getUsers(Active active) {
      ensureLoaded();
      List<IAtsUser> users = new ArrayList<>();
      for (Artifact user : ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.User, AtsUtilCore.getAtsBranch())) {
         Boolean activeFlag = user.getSoleAttributeValue(CoreAttributeTypes.Active, true);
         if (active == Active.Both || active == Active.Active && activeFlag || active == Active.InActive && !activeFlag) {
            users.add(new AtsUser((User) user));
         }
      }
      return users;
   }

   /************************************************
    ** IAtsUserServiceClient implementations
    ************************************************/

   @Override
   public IAtsUser getUserFromOseeUser(User user) throws OseeCoreException {
      ensureLoaded();
      IAtsUser atsUser = userIdToAtsUser.get(user.getUserId());
      if (atsUser == null) {
         atsUser = new AtsUser(user);
         userIdToAtsUser.put(user.getUserId(), atsUser);
      }
      return atsUser;
   }

   @Override
   public User getOseeUser(IAtsUser atsUser) throws OseeCoreException {
      ensureLoaded();
      User oseeUser = null;
      if (atsUser.getStoreObject() != null) {
         oseeUser = (User) atsUser.getStoreObject();
      } else {
         oseeUser = getOseeUserById(atsUser.getUserId());
      }
      return oseeUser;
   }

   @Override
   public User getCurrentOseeUser() throws OseeCoreException {
      ensureLoaded();
      IAtsUser user = getCurrentUser();
      return getOseeUser(user);
   }

   @Override
   public Collection<? extends User> toOseeUsers(Collection<? extends IAtsUser> users) throws OseeCoreException {
      ensureLoaded();
      List<User> results = new LinkedList<>();
      for (IAtsUser user : users) {
         results.add(getOseeUser(user));
      }
      return results;
   }

   @Override
   public Collection<IAtsUser> getAtsUsers(Collection<? extends Artifact> artifacts) throws OseeCoreException {
      ensureLoaded();
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
   public Collection<User> getOseeUsers(Collection<? extends IAtsUser> users) throws OseeCoreException {
      ensureLoaded();
      List<User> results = new LinkedList<>();
      for (IAtsUser user : users) {
         results.add(getOseeUser(user));
      }
      return results;
   }

   @Override
   public User getOseeUserById(String userId) throws OseeCoreException {
      ensureLoaded();
      return getOseeUser(getUserById(userId));
   }

   @Override
   protected synchronized void ensureLoaded() {
      if (!loaded) {
         for (Artifact art : ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.User,
            AtsUtilCore.getAtsBranch())) {
            User user = (User) art;
            AtsUser atsUser = new AtsUser(user);
            userIdToAtsUser.put(user.getUserId(), atsUser);
            nameToAtsUser.put(user.getName(), atsUser);
         }
         loaded = true;
      }
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
   public List<IAtsUser> getSubscribed(IAtsWorkItem workItem) throws OseeCoreException {
      ArrayList<IAtsUser> arts = new ArrayList<>();
      for (Artifact art : ((Artifact) workItem.getStoreObject()).getRelatedArtifacts(
         AtsRelationTypes.SubscribedUser_User)) {
         arts.add(getUserById((String) art.getSoleAttributeValue(CoreAttributeTypes.UserId)));
      }
      return arts;
   }

}
