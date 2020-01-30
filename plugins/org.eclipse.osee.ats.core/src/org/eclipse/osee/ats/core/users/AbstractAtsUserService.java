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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.IAtsConfigurationsService;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.AtsUserNameComparator;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsUserService implements IAtsUserService {

   protected final Map<Long, IAtsUser> accountIdToAtsUser = new ConcurrentHashMap<>(300);
   protected final Map<String, IAtsUser> userIdToAtsUser = new ConcurrentHashMap<>(300);
   protected final Map<String, IAtsUser> nameToAtsUser = new ConcurrentHashMap<>(300);
   protected IAtsUser currentUser = null;
   protected IAtsConfigurationsService configurationService;

   public void setConfigurationService(IAtsConfigurationsService configurationService) {
      this.configurationService = configurationService;
      Thread thread = new Thread(cacheLoader, "ATS User Loader");
      thread.start();
   }

   /**
    * Pre-load caches from AtsConfigurations
    */
   private final Runnable cacheLoader = new Runnable() {

      @Override
      public void run() {
         for (AtsUser user : configurationService.getConfigurations().getUsers()) {
            accountIdToAtsUser.put(user.getId(), user);
            userIdToAtsUser.put(user.getUserId(), user);
            nameToAtsUser.put(user.getName(), user);
         }
      }
   };

   @Override
   public IAtsUser getCurrentUser() {
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
   public Collection<IAtsUser> getUsersByUserIds(Collection<String> userIds) {
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
   public IAtsUser getUserById(String userId) {
      IAtsUser atsUser = null;
      if (Strings.isValid(userId)) {
         atsUser = userIdToAtsUser.get(userId);
         if (atsUser == null && Strings.isValid(userId)) {
            atsUser = AtsCoreUsers.getAtsCoreUserByUserId(userId);
            if (atsUser == null) {
               try {
                  atsUser = loadUserFromDbByUserId(userId);
               } catch (UserNotInDatabase ex) {
                  // do nothing
               }
               if (atsUser != null) {
                  userIdToAtsUser.put(userId, atsUser);
               }
            }
         }
      }
      return atsUser;
   }

   @Override
   public IAtsUser getUserByAccountId(Long accountId) {
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
   public IAtsUser getUserByName(String name) {
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
   public boolean isUserIdValid(String userId) {
      return getUserById(userId) != null;
   }

   @Override
   public boolean isUserNameValid(String name) {
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

   @Override
   public Collection<IAtsUser> getActiveAndAssignedInActive(Collection<? extends IAtsWorkItem> workItems) {
      Set<IAtsUser> users = new HashSet<>();
      users.addAll(getUsers(Active.Active));
      // Include inactive assigned
      for (IAtsWorkItem workItem : workItems) {
         for (IAtsUser user : workItem.getAssignees()) {
            if (!user.isActive()) {
               users.add(user);
            }
         }
      }
      return users;
   }

   @Override
   public void setCurrentUser(IAtsUser currentUser) {
      this.currentUser = currentUser;
   }

   @Override
   public AtsUser getAtsUser(IAtsUser user) {
      AtsUser atsUser = new AtsUser();
      atsUser.setName(user.getName());
      atsUser.setUserId(user.getUserId());
      atsUser.setEmail(user.getEmail());
      atsUser.setActive(user.isActive());
      atsUser.setId(user.getId());
      return atsUser;
   }

   @Override
   public Collection<IAtsUser> getRelatedUsers(AtsApi atsApi, ArtifactToken artifact, RelationTypeSide relation) {
      Set<IAtsUser> results = new HashSet<>();
      for (Object userArt : atsApi.getRelationResolver().getRelated(artifact, relation)) {
         String userId = (String) atsApi.getAttributeResolver().getSoleAttributeValue((ArtifactId) userArt,
            CoreAttributeTypes.UserId, null);
         IAtsUser lead = atsApi.getUserService().getUserById(userId);
         Conditions.assertNotNull(lead, "Lead can not be null with userArt %s", userArt);
         results.add(lead);
      }
      return results;
   }

}