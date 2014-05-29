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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.AtsUserNameComparator;
import org.eclipse.osee.framework.core.data.IUserToken;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsUserService implements IAtsUserService {

   protected final Map<String, IAtsUser> userIdToAtsUser = new HashMap<String, IAtsUser>(300);
   protected final Map<String, Boolean> userIdToAdmin = new HashMap<String, Boolean>(300);
   protected final Map<String, IAtsUser> nameToAtsUser = new HashMap<String, IAtsUser>(300);
   protected IAtsUser currentUser = null;
   protected String currentUserId = null;
   protected boolean loaded = false;

   protected abstract void ensureLoaded();

   @Override
   public IAtsUser getCurrentUser() throws OseeCoreException {
      ensureLoaded();
      if (currentUser == null) {
         currentUser = getUserById(getCurrentUserId());
      }
      return currentUser;
   }

   @Override
   public Collection<IAtsUser> getUsersByUserIds(Collection<String> userIds) throws OseeCoreException {
      ensureLoaded();
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
   public IAtsUser getUserById(String userId) throws OseeCoreException {
      ensureLoaded();
      IAtsUser atsUser = userIdToAtsUser.get(userId);
      if (atsUser == null && Strings.isValid(userId)) {
         atsUser = AtsCoreUsers.getAtsCoreUserByUserId(userId);
         if (atsUser == null) {
            atsUser = loadUserByUserIdFromDb(userId);
         }
      }
      return atsUser;
   }

   protected abstract IAtsUser loadUserByUserIdFromDb(String userId);

   @Override
   public IAtsUser getUserByName(String name) throws OseeCoreException {
      ensureLoaded();
      IAtsUser atsUser = nameToAtsUser.get(name);
      if (atsUser == null && Strings.isValid(name)) {
         atsUser = loadUserByUserNameFromDb(name);
      }
      return atsUser;
   }

   protected abstract IAtsUser loadUserByUserNameFromDb(String name);

   @Override
   public boolean isUserIdValid(String userId) throws OseeCoreException {
      ensureLoaded();
      return getUserById(userId) != null;
   }

   @Override
   public boolean isUserNameValid(String name) throws OseeCoreException {
      ensureLoaded();
      return getUserByName(name) != null;
   }

   @Override
   public List<IAtsUser> getUsersSortedByName(Active active) {
      ensureLoaded();
      List<IAtsUser> users = getUsers(active);
      Collections.sort(users, new AtsUserNameComparator(false));
      return users;
   }

   public IAtsUser getUserFromToken(IUserToken userToken) {
      ensureLoaded();
      return getUserById(userToken.getUserId());
   }

   @Override
   public void clearCache() {
      userIdToAdmin.clear();
      userIdToAtsUser.clear();
      nameToAtsUser.clear();
      loaded = false;
   }

}
