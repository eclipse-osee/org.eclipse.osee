/*
 * Created on Jun 13, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.review.role;

import junit.framework.Assert;

/**
 * Test unit for {@link UserRole}
 * 
 * @author Donald G. Dunne
 */
public class UserRoleTest {

   @org.junit.Test
   public void testToXmlFromXml() {
      UserRole item = new UserRole(Role.Author, "1233", 23.3, true);

      UserRole fromItem = new UserRole(item.toXml());
      Assert.assertEquals("1233", fromItem.getUserId());
      Assert.assertEquals(item.getRole(), fromItem.getRole());
      Assert.assertEquals(item.getHoursSpent(), fromItem.getHoursSpent());
      Assert.assertEquals(item.isCompleted(), fromItem.isCompleted());
   }
}
