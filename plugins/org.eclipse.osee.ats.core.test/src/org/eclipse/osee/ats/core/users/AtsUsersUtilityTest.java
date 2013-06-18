/*
 * Created on Apr 9, 2013
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.users;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.mock.MockAtsUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.junit.Test;

public class AtsUsersUtilityTest {

   private final MockAtsUser joe = new MockAtsUser("joe");
   private final MockAtsUser steve = new MockAtsUser("steve");
   private final MockAtsUser alice = new MockAtsUser("alice");

   @Test
   public void testIsEmailValid() {
      Assert.assertTrue(AtsUsersUtility.isEmailValid("b@b.com"));
      Assert.assertFalse(AtsUsersUtility.isEmailValid("asdf"));
      Assert.assertFalse(AtsUsersUtility.isEmailValid(null));
   }

   @Test
   public void testGetValidEmailUsers() throws OseeCoreException {
      Set<IAtsUser> users = new HashSet<IAtsUser>();
      users.add(joe);
      users.add(steve);
      users.add(alice);
      Assert.assertTrue(AtsUsersUtility.getValidEmailUsers(users).isEmpty());

      joe.setEmail("b@b.com");
      steve.setEmail("asdf");
      alice.setEmail(null);

      Assert.assertEquals(1, AtsUsersUtility.getValidEmailUsers(users).size());
      Assert.assertEquals(joe, AtsUsersUtility.getValidEmailUsers(users).iterator().next());
   }

   @Test
   public void testGetActiveEmailUsers() throws OseeCoreException {
      Set<IAtsUser> users = new HashSet<IAtsUser>();
      users.add(joe);
      users.add(steve);
      users.add(alice);
      joe.setEmail("b@b.com");
      joe.setActive(true);
      steve.setEmail("b@b.com");
      steve.setActive(false);
      alice.setEmail("b@b.com");
      alice.setActive(true);

      Collection<IAtsUser> activeEmailUsers = AtsUsersUtility.getActiveEmailUsers(users);
      Assert.assertEquals(2, activeEmailUsers.size());
      Assert.assertTrue(activeEmailUsers.contains(joe));
      Assert.assertTrue(activeEmailUsers.contains(alice));
   }

}
