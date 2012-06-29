/*
 * Created on Mar 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workflow;

import junit.framework.Assert;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.junit.Test;

/**
 * Test case for {@link StateType}
 * 
 * @author Donald G. Dunne
 */
public class StateTypeTest {

   @Test
   public void test() {
      Assert.assertTrue(StateType.Working.isWorkingState());
      Assert.assertFalse(StateType.Completed.isWorkingState());
      Assert.assertFalse(StateType.Cancelled.isWorkingState());

      Assert.assertFalse(StateType.Working.isCancelledState());
      Assert.assertFalse(StateType.Working.isCompletedState());
      Assert.assertFalse(StateType.Working.isCompletedOrCancelledState());

      Assert.assertTrue(StateType.Completed.isCompletedState());
      Assert.assertTrue(StateType.Completed.isCompletedOrCancelledState());
      Assert.assertTrue(StateType.Cancelled.isCancelledState());
      Assert.assertTrue(StateType.Cancelled.isCompletedOrCancelledState());
   }

   @Test
   public void testValues() {
      Assert.assertEquals(3, StateType.values().length);
   }

   @Test
   public void testValueOf() {
      Assert.assertEquals(StateType.Working, StateType.valueOf(StateType.Working.name()));
   }

}
