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
import org.eclipse.osee.framework.core.data.IUserToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Artifact-based user service. This brings the User artifact dependency with it.
 * 
 * @author Donald G. Dunne
 */
public class AtsUserAdminImpl implements IAtsUserAdmin {

   @Override
   public Collection<IAtsUser> getUsers() throws OseeCoreException {
      List<User> users = UserManager.getUsers();
      return getAtsUsers(users);
   }

   @Override
   public IAtsUser getUserById(String userId) throws OseeCoreException {
      IAtsUser atsUser = null;
      if (userId != null) {
         atsUser = AtsCoreUsers.getAtsCoreUserByUserId(userId);
         if (atsUser == null) {
            User user = UserManager.getUserByUserId(userId);
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
      return getUserById(getCurrentOseeUser().getUserId());
   }

   @Override
   public IAtsUser getUserByName(String name) throws OseeCoreException {
      return getUserFromOseeUser(UserManager.getUserByName(name));
   }

   @Override
   public IAtsUser getUserFromToken(IUserToken token) throws OseeCoreException {
      return getUserById(token.getUserId());
   }

   @Override
   public User getOseeUser(IAtsUser user) throws OseeCoreException {
      return UserManager.getUserByUserId(user.getUserId());
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
      return UserManager.getUser();
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
      return UserManager.getUserByUserId(userId);
   }

}
