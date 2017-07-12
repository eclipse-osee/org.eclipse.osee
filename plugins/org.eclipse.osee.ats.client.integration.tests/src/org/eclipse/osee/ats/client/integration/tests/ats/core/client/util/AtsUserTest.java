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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.junit.Assert;
import org.junit.Before;

/**
 * @author Donald G. Dunne
 */
public class AtsUserTest {

   private IAtsUser atsUser;
   private User user;

   @Before
   public void setUp() throws OseeCoreException {
      user = UserManager.getUser();
      atsUser = AtsClientService.get().getUserService().getCurrentUser();
   }

   @org.junit.Test
   public void testGetUserId() throws OseeCoreException {
      Assert.assertEquals(user.getUserId(), atsUser.getUserId());
   }

   @org.junit.Test
   public void testGetName() {
      Assert.assertEquals(user.getName(), atsUser.getName());
   }

   @org.junit.Test
   public void testGetEmail() throws OseeCoreException {
      Assert.assertEquals(user.getEmail(), atsUser.getEmail());
   }

   @org.junit.Test
   public void testRemove() throws OseeCoreException {
      Collection<IAtsUser> assignees = new HashSet<>();
      assignees.add(AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Alex_Kay));
      assignees.add(AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Joe_Smith));
      Assert.assertTrue(Collections.isEqual(assignees,
         Arrays.asList(AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Alex_Kay),
            AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Joe_Smith))));

      assignees.remove(AtsClientService.get().getUserService().getCurrentUser());
      Assert.assertTrue(Collections.isEqual(assignees,
         Arrays.asList(AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Alex_Kay))));
   }

}
