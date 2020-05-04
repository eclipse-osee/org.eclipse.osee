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
package org.eclipse.osee.ats.ide.integration.tests.ats.util;

import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
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
         AtsClientService.get().getUserService().getUserByUserId(SystemUser.OseeSystem.getUserId()));
      Assert.assertEquals(AtsCoreUsers.ANONYMOUS_USER,
         AtsClientService.get().getUserService().getUserByUserId(SystemUser.Anonymous.getUserId()));
      Assert.assertEquals(AtsCoreUsers.UNASSIGNED_USER,
         AtsClientService.get().getUserService().getUserByUserId(SystemUser.UnAssigned.getUserId()));
   }

   @Test
   public void testGetUserWithNull() {
      Assert.assertNull(AtsClientService.get().getUserService().getUserByUserId(null));
   }

   @Test
   public void testGetCurrentUser() {
      User currentUser = UserManager.getUser();

      Assert.assertEquals(currentUser, AtsClientService.get().getUserService().getCurrentUser());

      AtsUser atsUser = AtsClientService.get().getUserService().getUserByUserId(currentUser.getUserId());

      Assert.assertEquals(currentUser.getUserId(), atsUser.getUserId());
      Assert.assertEquals(currentUser.getEmail(), atsUser.getEmail());
      Assert.assertEquals(currentUser.isActive(), atsUser.isActive());
      Assert.assertEquals(currentUser.getName(), atsUser.getName());
      Assert.assertEquals(atsUser, AtsClientService.get().getUserService().getCurrentUser());
   }

}
