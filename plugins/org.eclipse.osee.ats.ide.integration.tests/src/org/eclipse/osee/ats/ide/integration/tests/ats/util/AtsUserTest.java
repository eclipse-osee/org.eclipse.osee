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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * @author Donald G. Dunne
 */
public class AtsUserTest {

   private static AtsUser atsUser;
   private static User user;

   @BeforeClass
   public static void setUp() {
      user = UserManager.getUser();
      atsUser = AtsClientService.get().getUserService().getCurrentUser();
   }

   @org.junit.Test
   public void testGetUserId() {
      Assert.assertEquals(user.getUserId(), atsUser.getUserId());
   }

   @org.junit.Test
   public void testGetName() {
      Assert.assertEquals(user.getName(), atsUser.getName());
   }

   @org.junit.Test
   public void testGetEmail() throws Exception {
      Assert.assertEquals(user.getEmail(), atsUser.getEmail());
   }

   @org.junit.Test
   public void testRemove() {
      Collection<AtsUser> assignees = new HashSet<>();
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
