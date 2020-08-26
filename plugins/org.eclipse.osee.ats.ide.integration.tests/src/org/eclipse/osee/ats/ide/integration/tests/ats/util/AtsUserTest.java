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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
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
      atsUser = AtsApiService.get().getUserService().getCurrentUser();
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
      assignees.add(AtsApiService.get().getUserService().getUserByToken(DemoUsers.Alex_Kay));
      assignees.add(AtsApiService.get().getUserService().getUserByToken(DemoUsers.Joe_Smith));
      Assert.assertTrue(Collections.isEqual(assignees,
         Arrays.asList(AtsApiService.get().getUserService().getUserByToken(DemoUsers.Alex_Kay),
            AtsApiService.get().getUserService().getUserByToken(DemoUsers.Joe_Smith))));

      assignees.remove(AtsApiService.get().getUserService().getCurrentUser());
      Assert.assertTrue(Collections.isEqual(assignees,
         Arrays.asList(AtsApiService.get().getUserService().getUserByToken(DemoUsers.Alex_Kay))));
   }

}
