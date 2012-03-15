/*
 * Created on Mar 19, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.validator;

import junit.framework.Assert;
import org.junit.Test;

public class WidgetStatusTest {

   @Test
   public void test() {
      Assert.assertTrue(WidgetStatus.Valid.isValid());
      Assert.assertFalse(WidgetStatus.Empty.isValid());

      Assert.assertTrue(WidgetStatus.Empty.isEmpty());
      Assert.assertFalse(WidgetStatus.Valid.isEmpty());
   }

   @Test
   public void testValues() {
      Assert.assertEquals(6, WidgetStatus.values().length);
   }

   @Test
   public void testValueOf() {
      Assert.assertEquals(WidgetStatus.Empty, WidgetStatus.valueOf(WidgetStatus.Empty.name()));
   }

}
