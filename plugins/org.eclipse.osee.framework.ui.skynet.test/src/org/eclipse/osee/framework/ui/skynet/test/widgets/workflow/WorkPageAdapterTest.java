/*
 * Created on Nov 23, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.test.widgets.workflow;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @Tests WorkPageAdapter
 * @author Donald G. Dunne
 */
public class WorkPageAdapterTest {

   @Test
   public void ordered() {
      Assert.assertEquals(5, OrderedStates.Five.ordinal());
      Assert.assertEquals(1, OneStates.Endorse.ordinal());
      Assert.assertEquals(8, OrderedStates.Completed.ordinal());
      Assert.assertEquals(3, OneStates.Completed.ordinal());
   }

   @Test
   public void testValueOf() {
      Assert.assertNotNull(OrderedStates.valueOf("Five"));
      Assert.assertEquals("Five", OrderedStates.valueOf("Five").getPageName());
   }

   @Test
   public void testValues() {
      Assert.assertEquals(8, OrderedStates.values().size());

      // values should return in ordinal order 
      Assert.assertEquals(OrderedStates.One.getPageName(), OrderedStates.values().get(0).getPageName());
      Assert.assertEquals(OrderedStates.Five.getPageName(), OrderedStates.values().get(4).getPageName());
      Assert.assertEquals(OrderedStates.Completed.getPageName(), OrderedStates.values().get(7).getPageName());
   }

}
