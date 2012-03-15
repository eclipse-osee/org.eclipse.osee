/*
 * Created on Mar 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link AbstractWorkDefItem}
 *
 * @author Donald G. Dunne
 */
public class AbstractWorkDefItemTest {

   @Test
   public void testGetSetDescription() {
      AbstractWorkDefItem item = new AbstractWorkDefItem("name");
      Assert.assertNull(item.getDescription());
      item.setDescription("desc");
      Assert.assertEquals("desc", item.getDescription());
   }

   @Test
   public void testToString() {
      AbstractWorkDefItem item = new AbstractWorkDefItem("name");
      Assert.assertEquals("name", item.toString());
      item.setName("that");
      Assert.assertEquals("that", item.toString());
   }

}
