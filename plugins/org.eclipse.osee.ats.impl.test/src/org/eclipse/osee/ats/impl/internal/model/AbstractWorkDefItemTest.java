/*
 * Created on Mar 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.impl.internal.model;

import org.eclipse.osee.ats.impl.internal.model.AbstractWorkDefItem;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link AbstractWorkDefItem}
 * 
 * @author Donald G. Dunne
 */
public class AbstractWorkDefItemTest {

   @Test
   public void testToString() {
      AbstractWorkDefItem item = new AbstractWorkDefItem("name");
      Assert.assertEquals("name", item.toString());
      item.setName("that");
      Assert.assertEquals("that", item.toString());
   }

}
