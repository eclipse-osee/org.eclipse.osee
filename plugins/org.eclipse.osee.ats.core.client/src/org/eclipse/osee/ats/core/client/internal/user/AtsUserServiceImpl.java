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

import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
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

}
