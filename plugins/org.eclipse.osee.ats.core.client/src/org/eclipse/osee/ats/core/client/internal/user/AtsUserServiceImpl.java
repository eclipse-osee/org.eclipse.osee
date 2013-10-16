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
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;

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
   public boolean isAtsAdmin() {
      return AtsUtilCore.isAtsAdmin();
   }

   @Override
   public boolean isAssigneeMe(IAtsWorkItem workItem) throws OseeCoreException {
      return workItem.getStateMgr().getAssignees().contains(AtsClientService.get().getUserAdmin().getCurrentUser());
   }

}
