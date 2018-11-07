/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.users;

import static org.mockito.Mockito.when;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.user.IAtsUser;
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
      Set<IAtsUser> users = new HashSet<>();
      users.add(joe);
      users.add(steve);
      users.add(alice);
      Assert.assertTrue(AtsUsersUtility.getValidEmailUsers(users).isEmpty());

      when(joe.getEmail()).thenReturn("b@b.com");
      when(steve.getEmail()).thenReturn("asdf");
      when(alice.getEmail()).thenReturn(null);

      Assert.assertEquals(1, AtsUsersUtility.getValidEmailUsers(users).size());
      Assert.assertEquals(joe, AtsUsersUtility.getValidEmailUsers(users).iterator().next());
   }

   @Test
   public void testGetActiveEmailUsers() {
      Set<IAtsUser> users = new HashSet<>();
      users.add(joe);
      users.add(steve);
      users.add(alice);
      when(joe.getEmail()).thenReturn("b@b.com");
      when(joe.isActive()).thenReturn(true);
      when(steve.getEmail()).thenReturn("b@b.com");
      when(steve.isActive()).thenReturn(false);
      when(alice.getEmail()).thenReturn("b@b.com");
      when(alice.isActive()).thenReturn(true);

      Collection<IAtsUser> activeEmailUsers = AtsUsersUtility.getActiveEmailUsers(users);
      Assert.assertEquals(2, activeEmailUsers.size());
      Assert.assertTrue(activeEmailUsers.contains(joe));
      Assert.assertTrue(activeEmailUsers.contains(alice));
   }

}
