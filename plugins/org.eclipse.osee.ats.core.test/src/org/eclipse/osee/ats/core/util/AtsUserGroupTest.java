/*
 * Created on Mar 19, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.util;

import java.util.Arrays;
import junit.framework.Assert;
import org.eclipse.osee.ats.core.mock.MockAtsUser;
import org.junit.Test;

public class AtsUserGroupTest {
   private final MockAtsUser joe = new MockAtsUser("joe");
   private final MockAtsUser steve = new MockAtsUser("steve");
   private final MockAtsUser alice = new MockAtsUser("alice");

   @Test
   public void testGetSetAddRemoveUsers() {
      AtsUserGroup group = new AtsUserGroup();
      Assert.assertTrue(group.getUsers().isEmpty());

      group.setUsers(Arrays.asList(joe, steve));
      Assert.assertEquals(2, group.getUsers().size());
      Assert.assertTrue(group.getUsers().contains(joe));
      Assert.assertTrue(group.getUsers().contains(steve));
      Assert.assertFalse(group.getUsers().contains(alice));

      group.removeUser(steve);
      Assert.assertEquals(1, group.getUsers().size());
      Assert.assertTrue(group.getUsers().contains(joe));
      Assert.assertFalse(group.getUsers().contains(steve));
      Assert.assertFalse(group.getUsers().contains(alice));
   }

   @Test
   public void testToString() {
      AtsUserGroup group = new AtsUserGroup();
      group.setUsers(Arrays.asList(joe, steve));
      Assert.assertEquals("[User [joe - joe - null], User [steve - steve - null]]", group.toString());
   }

}
