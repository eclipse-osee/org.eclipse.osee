/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.util;

import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AtsUsersTest {

   @Test
   public void testGetUser() {
      Assert.assertEquals(AtsCoreUsers.SYSTEM_USER,
         AtsApiService.get().getUserService().getUserByUserId(SystemUser.OseeSystem.getUserId()));
      Assert.assertEquals(AtsCoreUsers.ANONYMOUS_USER,
         AtsApiService.get().getUserService().getUserByUserId(SystemUser.Anonymous.getUserId()));
      Assert.assertEquals(AtsCoreUsers.UNASSIGNED_USER,
         AtsApiService.get().getUserService().getUserByUserId(SystemUser.UnAssigned.getUserId()));
   }

   @Test
   public void testGetUserWithNull() {
      Assert.assertNull(AtsApiService.get().getUserService().getUserByUserId(null));
   }

   @Test
   public void testGetCurrentUser() {
      User currentUser = UserManager.getUser();

      Assert.assertEquals(currentUser, AtsApiService.get().getUserService().getCurrentUser());

      AtsUser atsUser = AtsApiService.get().getUserService().getUserByUserId(currentUser.getUserId());

      Assert.assertEquals(currentUser.getUserId(), atsUser.getUserId());
      Assert.assertEquals(currentUser.getEmail(), atsUser.getEmail());
      Assert.assertEquals(currentUser.isActive(), atsUser.isActive());
      Assert.assertEquals(currentUser.getName(), atsUser.getName());
      Assert.assertEquals(atsUser, AtsApiService.get().getUserService().getCurrentUser());
   }

}
