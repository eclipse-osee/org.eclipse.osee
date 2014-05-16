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
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.AtsUserNameComparator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.util.AtsGroup;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * Non-artifact base user service
 * 
 * @author Donald G Dunne
 */
public class AtsUserServiceImpl implements IAtsUserService {

   @Override
   public IAtsUser getCurrentUser() throws OseeCoreException {
      return getUserById(UserManager.getUser().getUserId());
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

   private IAtsUser getUserFromOseeUser(User user) throws OseeCoreException {
      IAtsUser atsUser = null;
      if (user != null) {
         atsUser = getUserById(user.getUserId());
      }
      return atsUser;
   }

   @Override
   public IAtsUser getUserByName(String name) throws OseeCoreException {
      return getUserFromOseeUser(UserManager.getUserByName(name));
   }

   @Override
   public boolean isUserIdValid(String userId) throws OseeCoreException {
      return getUserById(userId) != null;
   }

   @Override
   public boolean isUserNameValid(String name) throws OseeCoreException {
      return getUserByName(name) != null;
   }

   @Override
   public String getUserIdByName(String name) throws OseeCoreException {
      String userId = null;
      IAtsUser userByName = getUserByName(name);
      if (userByName != null) {
         userId = userByName.getUserId();
      }
      return userId;
   }

   @Override
   public boolean isAtsAdmin(IAtsUser user) {
      return AtsGroup.AtsAdmin.isMember(user);
   }

   @Override
   public boolean isAssigneeMe(IAtsWorkItem workItem) throws OseeCoreException {
      return workItem.getStateMgr().getAssignees().contains(AtsClientService.get().getUserAdmin().getCurrentUser());
   }

   @Override
   public List<IAtsUser> getUsers(Active active) {
      List<IAtsUser> users = new ArrayList<IAtsUser>();
      for (Artifact user : ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.User, AtsUtilCore.getAtsBranch())) {
         Boolean activeFlag = user.getSoleAttributeValue(AtsAttributeTypes.Active, true);
         if (active == Active.Both || ((active == Active.Active) && activeFlag) || ((active == Active.InActive) && !activeFlag)) {
            users.add(new AtsUser((User) user));
         }
      }
      return users;
   }

   @Override
   public List<IAtsUser> getUsersSortedByName(Active active) {
      List<IAtsUser> users = getUsers(active);
      Collections.sort(users, new AtsUserNameComparator());
      return users;
   }

}
