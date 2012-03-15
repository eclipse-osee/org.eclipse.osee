/*
 * Created on Mar 19, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Test case for {@link StateEventType}
 *
 * @author Donald G. Dunne
 */
public class StateEventTypeTest {

   @Test
   public void testValues() {
      Assert.assertEquals(4, StateEventType.values().length);
   }

   @Test
   public void testValueOf() {
      Assert.assertEquals(StateEventType.CommitBranch, StateEventType.valueOf(StateEventType.CommitBranch.name()));
   }

}
