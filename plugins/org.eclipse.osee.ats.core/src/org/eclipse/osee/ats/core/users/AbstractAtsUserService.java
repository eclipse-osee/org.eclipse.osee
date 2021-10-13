/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.core.users;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.IAtsConfigurationsService;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
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

   protected IAtsConfigurationsService configurationService;

   @Override
   public void setConfigurationService(IAtsConfigurationsService configurationService) {
      this.configurationService = configurationService;
   }

   @Override
   public Collection<AtsUser> getUsersByUserIds(Collection<String> userIds) {
      List<AtsUser> users = new LinkedList<>();
      for (String userId : userIds) {
         AtsUser user = getUserByUserId(userId);
         if (user != null) {
            users.add(user);
         }
      }
      return users;
   }

   public boolean isLoadValid() {
      return false;
   }

   @Override
   public AtsUser getUserByUserId(String userId) {
      AtsUser atsUser = null;
      if (Strings.isValid(userId)) {
         atsUser = configurationService.getUserByUserId(userId);
         if (atsUser == null && Strings.isValid(userId)) {
            atsUser = AtsCoreUsers.getAtsCoreUserByUserId(userId);
            if (atsUser == null && isLoadValid()) {
               try {
                  atsUser = loadUserByUserId(userId);
               } catch (UserNotInDatabase ex) {
                  // do nothing
               }
               if (atsUser != null) {
                  configurationService.getConfigurations().addUser(atsUser);
               }
            }
         }
      }
      return atsUser;
   }

   @Override
   public AtsUser getUserById(ArtifactId user) {
      AtsUser atsUser = configurationService.getConfigurations().getIdToUser().get(user.getId());
      if (atsUser == null && isLoadValid()) {
         atsUser = loadUserByUserId(user.getId());
         if (atsUser != null) {
            configurationService.getConfigurations().addUser(atsUser);
         }
      }
      return atsUser;
   }

   protected AtsUser loadUserByUserId(Long accountId) {
      throw new UnsupportedOperationException();
   }

   protected AtsUser loadUserByUserId(String userId) {
      throw new UnsupportedOperationException();
   }

   protected AtsUser loadUserByUserName(String name) {
      throw new UnsupportedOperationException();
   }

   @Override
   public AtsUser getUserByName(String name) {
      AtsUser atsUser = configurationService.getUserByName(name);
      if (atsUser == null && Strings.isValid(name) && isLoadValid()) {
         atsUser = loadUserByUserName(name);
         if (atsUser != null) {
            configurationService.getConfigurations().addUser(atsUser);
         }
      }
      return atsUser;
   }

   @Override
   public AtsUser getUserByToken(UserToken userToken) {
      return getUserByUserId(userToken.getUserId());
   }

   @Override
   public Collection<AtsUser> getUsers(Active active) {
      List<AtsUser> users = new ArrayList<>();
      for (AtsUser user : getUsers()) {
         if (active == Active.Both || active == Active.Active && user.isActive() || active == Active.InActive && !user.isActive()) {
            users.add(user);
         }
      }
      return users;
   }

   @Override
   public void reloadCache() {
      configurationService.getConfigurationsWithPend();
      setCurrentUser(null);
   }

   @Override
   public Collection<AtsUser> getActiveAndAssignedInActive(Collection<? extends IAtsWorkItem> workItems) {
      Set<AtsUser> users = new HashSet<>();
      users.addAll(getUsers(Active.Active));
      // Include inactive assigned
      for (IAtsWorkItem workItem : workItems) {
         for (AtsUser user : workItem.getAssignees()) {
            if (!user.isActive()) {
               users.add(user);
            }
         }
      }
      return users;
   }

   @Override
   public Collection<AtsUser> getRelatedUsers(AtsApi atsApi, ArtifactToken artifact, RelationTypeSide relation) {
      Set<AtsUser> results = new HashSet<>();
      for (Object userArt : atsApi.getRelationResolver().getRelated(artifact, relation)) {
         String userId = (String) atsApi.getAttributeResolver().getSoleAttributeValue((ArtifactId) userArt,
            CoreAttributeTypes.UserId, null);
         AtsUser lead = atsApi.getUserService().getUserByUserId(userId);
         Conditions.assertNotNull(lead, "Lead can not be null with userArt %s", userArt);
         results.add(lead);
      }
      return results;
   }
}