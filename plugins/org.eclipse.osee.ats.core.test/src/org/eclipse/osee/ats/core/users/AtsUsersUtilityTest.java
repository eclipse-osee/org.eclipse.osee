/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.core.users;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AtsUsersUtilityTest extends AbstractUserTest {

   @Test
   public void testIsEmailValid() {
      Assert.assertTrue(AtsUsersUtility.isEmailValid("b@b.com"));
      Assert.assertFalse(AtsUsersUtility.isEmailValid("asdf"));
      Assert.assertFalse(AtsUsersUtility.isEmailValid(null));
   }

   @Test
   public void testGetValidEmailUsers() {
      Set<AtsUser> users = new HashSet<>();
      users.add(joe);
      users.add(steve);
      users.add(alice);

      Assert.assertEquals(1, AtsUsersUtility.getValidEmailUsers(users).size());
      Assert.assertEquals(joe, AtsUsersUtility.getValidEmailUsers(users).iterator().next());
   }

   @Test
   public void testGetActiveEmailUsers() {
      Set<AtsUser> users = new HashSet<>();
      users.add(joe);
      users.add(steve);
      users.add(alice);

      Collection<AtsUser> activeEmailUsers = AtsUsersUtility.getActiveEmailUsers(users);
      Assert.assertEquals(2, activeEmailUsers.size());
      Assert.assertTrue(activeEmailUsers.contains(joe));
      Assert.assertTrue(activeEmailUsers.contains(alice));
   }
}