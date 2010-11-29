/*
 * Created on Nov 23, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.test.util.enumeration;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @Tests AbstractEnumeration
 * @author Donald G. Dunne
 */
public class AbstractEnumerationTest {

   @Test
   public void ordered() {
      Assert.assertEquals(5, OrderedEnum.Five.ordinal());
      Assert.assertEquals(1, OneEnum.Endorse.ordinal());
      Assert.assertEquals(8, OrderedEnum.Completed.ordinal());
      Assert.assertEquals(3, OneEnum.Completed.ordinal());
   }

   @Test
   public void testValueOf() {
      Assert.assertNotNull(OrderedEnum.valueOf("Five"));
      Assert.assertEquals("Five", OrderedEnum.valueOf("Five").name());
   }

   @Test
   public void testValues() {
      Assert.assertEquals(8, OrderedEnum.values().size());
   }

}
