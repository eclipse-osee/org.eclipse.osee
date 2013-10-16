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
package org.eclipse.osee.ats.client.integration.tests.ats.core.client.util;

import org.junit.Assert;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AtsUsersTest {

   @Test
   public void testGetUser() throws OseeCoreException {
      Assert.assertEquals(AtsCoreUsers.SYSTEM_USER,
         AtsClientService.get().getUserAdmin().getUserById(AtsCoreUsers.SYSTEM_USER.getUserId()));
      Assert.assertEquals(AtsCoreUsers.GUEST_USER,
         AtsClientService.get().getUserAdmin().getUserById(AtsCoreUsers.GUEST_USER.getUserId()));
      Assert.assertEquals(AtsCoreUsers.UNASSIGNED_USER,
         AtsClientService.get().getUserAdmin().getUserById(AtsCoreUsers.UNASSIGNED_USER.getUserId()));
   }

   @Test(expected = UserNotInDatabase.class)
   public void testGetUserException() throws OseeCoreException {
      Assert.assertNull(AtsClientService.get().getUserAdmin().getUserById("2345"));
   }

   @Test
   public void testGetUserWithNull() throws OseeCoreException {
      Assert.assertNull(AtsClientService.get().getUserAdmin().getUserById(null));
   }

   @Test
   public void testGetGuestUser() {
      Assert.assertEquals(AtsCoreUsers.GUEST_USER, AtsCoreUsers.GUEST_USER);
   }

   @Test
   public void testGetSystemUser() {
      Assert.assertEquals(AtsCoreUsers.SYSTEM_USER, AtsCoreUsers.SYSTEM_USER);
   }

   @Test
   public void testGetUnAssigned() {
      Assert.assertEquals(AtsCoreUsers.UNASSIGNED_USER, AtsCoreUsers.UNASSIGNED_USER);
   }

   @Test
   public void testGetCurrentUser() throws OseeCoreException {
      User currentUser = UserManager.getUser();

      Assert.assertEquals(currentUser, AtsClientService.get().getUserAdmin().getCurrentOseeUser());

      IAtsUser atsUser = AtsClientService.get().getUserAdmin().getUserById(currentUser.getUserId());

      Assert.assertEquals(currentUser.getUserId(), atsUser.getUserId());
      Assert.assertEquals(currentUser.getEmail(), atsUser.getEmail());
      Assert.assertEquals(currentUser.isActive(), atsUser.isActive());
      Assert.assertEquals(currentUser.getName(), atsUser.getName());
      Assert.assertEquals(atsUser, AtsClientService.get().getUserAdmin().getCurrentUser());
   }

}
