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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.client.IAtsUserAdmin;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.IUserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * Artifact-based user service. This brings the User artifact dependency with it.
 * 
 * @author Donald G. Dunne
 */
public class AtsUserAdminImpl implements IAtsUserAdmin {

   @Override
   public Collection<IAtsUser> getUsers() throws OseeCoreException {
      List<User> users = UserManager.getUsers();
      if (!AtsUtilCore.getAtsBranch().equals(CoreBranches.COMMON)) {
         users =
            Collections.castAll(ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.User,
               AtsUtilCore.getAtsBranch()));
      }
      return getAtsUsers(users);
   }

   @Override
   public IAtsUser getUserById(String userId) throws OseeCoreException {
      IAtsUser atsUser = null;
      if (userId != null) {
         atsUser = AtsCoreUsers.getAtsCoreUserByUserId(userId);
         if (atsUser == null) {
            User user = UserManager.getUserByUserId(userId);
            if (!AtsUtilCore.getAtsBranch().equals(CoreBranches.COMMON)) {
               user =
                  (User) ArtifactQuery.getArtifactFromTypeAndAttribute(CoreArtifactTypes.User,
                     CoreAttributeTypes.UserId, userId, AtsUtilCore.getAtsBranch());
            }
            atsUser = new AtsUser(user);
         }
      }
      return atsUser;
   }

   @Override
   public Collection<IAtsUser> getUsersByUserIds(Collection<String> userIds) throws OseeCoreException {
      List<IAtsUser> users = new LinkedList<IAtsUser>();
      for (String userId : userIds) {
         IAtsUser user = getUserById(userId);
         if (user != null) {
            users.add(user);
         }
      }
      return users;
   }

   @Override
   public IAtsUser getCurrentUser() throws OseeCoreException {
      return getUserById(ClientSessionManager.getCurrentUserToken().getUserId());
   }

   @Override
   public IAtsUser getUserByName(String name) throws OseeCoreException {
      User userByName = UserManager.getUserByName(name);
      if (!AtsUtilCore.getAtsBranch().equals(CoreBranches.COMMON)) {
         userByName =
            (User) ArtifactQuery.getArtifactFromTypeAndAttribute(CoreArtifactTypes.User, CoreAttributeTypes.Name, name,
               AtsUtilCore.getAtsBranch());
      }
      return getUserFromOseeUser(userByName);
   }

   @Override
   public IAtsUser getUserFromToken(IUserToken token) throws OseeCoreException {
      return getUserById(token.getUserId());
   }

   @Override
   public User getOseeUser(IAtsUser user) throws OseeCoreException {
      User oseeUser = UserManager.getUserByUserId(user.getUserId());
      if (!AtsUtilCore.getAtsBranch().equals(CoreBranches.COMMON)) {
         oseeUser =
            (User) ArtifactQuery.getArtifactFromTypeAndAttribute(CoreArtifactTypes.User, CoreAttributeTypes.UserId,
               user.getUserId(), AtsUtilCore.getAtsBranch());
      }
      return oseeUser;
   }

   @Override
   public IAtsUser getUserFromOseeUser(User user) throws OseeCoreException {
      IAtsUser atsUser = null;
      if (user != null) {
         atsUser = getUserById(user.getUserId());
      }
      return atsUser;
   }

   @Override
   public User getCurrentOseeUser() throws OseeCoreException {
      return getOseeUserById(ClientSessionManager.getCurrentUserToken().getUserId());
   }

   @Override
   public Collection<? extends User> toOseeUsers(Collection<? extends IAtsUser> users) throws OseeCoreException {
      List<User> results = new LinkedList<User>();
      for (IAtsUser user : users) {
         results.add(getOseeUser(user));
      }
      return results;
   }

   @Override
   public Collection<IAtsUser> getUsers(List<? extends Artifact> artifacts) throws OseeCoreException {
      List<IAtsUser> users = new LinkedList<IAtsUser>();
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
      List<User> results = new LinkedList<User>();
      for (IAtsUser user : users) {
         results.add(getOseeUser(user));
      }
      return results;
   }

   @Override
   public Collection<IAtsUser> getAtsUsers(Collection<User> users) throws OseeCoreException {
      List<IAtsUser> results = new LinkedList<IAtsUser>();
      for (User user : users) {
         IAtsUser userByUserId = getUserById(user.getUserId());
         if (userByUserId != null) {
            results.add(userByUserId);
         }
      }
      return results;
   }

   @Override
   public User getOseeUserById(String userId) throws OseeCoreException {
      return (User) getUserById(userId).getStoreObject();
   }

}
