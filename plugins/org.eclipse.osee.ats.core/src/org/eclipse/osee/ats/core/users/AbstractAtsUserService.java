/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.users;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.AtsUserNameComparator;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsUserService implements IAtsUserService {

   protected final Map<Long, IAtsUser> accountIdToAtsUser = new ConcurrentHashMap<>(300);
   protected final Map<String, IAtsUser> userIdToAtsUser = new ConcurrentHashMap<>(300);
   protected final Map<String, IAtsUser> nameToAtsUser = new ConcurrentHashMap<>(300);
   protected IAtsUser currentUser = null;

   @Override
   public IAtsUser getCurrentUser() throws OseeCoreException {
      if (currentUser == null) {
         currentUser = userIdToAtsUser.get(getCurrentUserId());
         if (currentUser == null) {
            for (IAtsUser user : getUsers(Active.Both)) {
               if (user.getUserId().equals(getCurrentUserId())) {
                  currentUser = user;
                  break;
               }
            }
         }
         if (currentUser == null) {
            currentUser = loadUserFromDbByUserId(getCurrentUserId());
         }
      }
      return currentUser;
   }

   @Override
   public Collection<IAtsUser> getUsersByUserIds(Collection<String> userIds) throws OseeCoreException {
      List<IAtsUser> users = new LinkedList<>();
      for (String userId : userIds) {
         IAtsUser user = getUserById(userId);
         if (user != null) {
            users.add(user);
         }
      }
      return users;
   }

   @Override
   public IAtsUser getUserById(String userId) throws OseeCoreException {
      IAtsUser atsUser = null;
      if (Strings.isValid(userId)) {
         atsUser = userIdToAtsUser.get(userId);
         if (atsUser == null && Strings.isValid(userId)) {
            atsUser = AtsCoreUsers.getAtsCoreUserByUserId(userId);
            if (atsUser == null) {
               atsUser = loadUserFromDbByUserId(userId);
               if (atsUser != null) {
                  userIdToAtsUser.put(userId, atsUser);
               }
            }
         }
      }
      return atsUser;
   }

   @Override
   public IAtsUser getUserByAccountId(Long accountId) throws OseeCoreException {
      IAtsUser atsUser = accountIdToAtsUser.get(accountId);
      if (atsUser == null) {
         atsUser = loadUserByAccountId(accountId);
         if (atsUser != null) {
            accountIdToAtsUser.put(accountId, atsUser);
         }
      }
      return atsUser;
   }

   protected abstract IAtsUser loadUserByAccountId(Long accountId);

   protected abstract IAtsUser loadUserFromDbByUserId(String userId);

   @Override
   public IAtsUser getUserByName(String name) throws OseeCoreException {
      IAtsUser atsUser = nameToAtsUser.get(name);
      if (atsUser == null && Strings.isValid(name)) {
         atsUser = loadUserFromDbByUserName(name);
         if (atsUser != null) {
            nameToAtsUser.put(name, atsUser);
         }
      }
      return atsUser;
   }

   protected abstract IAtsUser loadUserFromDbByUserName(String name);

   @Override
   public boolean isUserIdValid(String userId) throws OseeCoreException {
      return getUserById(userId) != null;
   }

   @Override
   public boolean isUserNameValid(String name) throws OseeCoreException {
      return getUserByName(name) != null;
   }

   @Override
   public List<IAtsUser> getUsersSortedByName(Active active) {
      List<IAtsUser> users = getUsers(active);
      Collections.sort(users, new AtsUserNameComparator(false));
      return users;
   }

   public IAtsUser getUserFromToken(UserToken userToken) {
      return getUserById(userToken.getUserId());
   }

   @Override
   public boolean isAtsAdmin() {
      return isAtsAdmin(getCurrentUser());
   }

   @Override
   public List<IAtsUser> getUsers(Active active) {
      List<IAtsUser> users = new ArrayList<>();
      for (IAtsUser user : getUsers()) {
         if (active == Active.Both || active == Active.Active && user.isActive() || active == Active.InActive && !user.isActive()) {
            users.add(user);
         }
      }
      return users;
   }

   @Override
   public void reloadCache() {
      userIdToAtsUser.clear();
      nameToAtsUser.clear();
      currentUser = null;
      for (IAtsUser user : getUsersFromDb()) {
         userIdToAtsUser.put(user.getUserId(), user);
         nameToAtsUser.put(user.getName(), user);
      }
   }

   @Override
   public void releaseUser() {
      currentUser = null;
   }
}