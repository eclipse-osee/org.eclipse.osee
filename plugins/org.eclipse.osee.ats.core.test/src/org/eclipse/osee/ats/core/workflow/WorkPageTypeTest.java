/*
 * Created on Mar 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workflow;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Test case for {@link WorkPageType}
 *
 * @author Donald G. Dunne
 */
public class WorkPageTypeTest {

   @Test
   public void test() {
      Assert.assertTrue(WorkPageType.Working.isWorkingPage());
      Assert.assertFalse(WorkPageType.Completed.isWorkingPage());
      Assert.assertFalse(WorkPageType.Cancelled.isWorkingPage());

      Assert.assertFalse(WorkPageType.Working.isCancelledPage());
      Assert.assertFalse(WorkPageType.Working.isCompletedPage());
      Assert.assertFalse(WorkPageType.Working.isCompletedOrCancelledPage());

      Assert.assertTrue(WorkPageType.Completed.isCompletedPage());
      Assert.assertTrue(WorkPageType.Completed.isCompletedOrCancelledPage());
      Assert.assertTrue(WorkPageType.Cancelled.isCancelledPage());
      Assert.assertTrue(WorkPageType.Cancelled.isCompletedOrCancelledPage());
   }

   @Test
   public void testValues() {
      Assert.assertEquals(3, WorkPageType.values().length);
   }

   @Test
   public void testValueOf() {
      Assert.assertEquals(WorkPageType.Working, WorkPageType.valueOf(WorkPageType.Working.name()));
   }

}
