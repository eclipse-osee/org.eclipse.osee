/*
 * Created on Mar 19, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import junit.framework.Assert;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.junit.Test;

/**
 * Test case for {@link StateColor}
 *
 * @author Donald G. Dunne
 */
public class StateColorTest {

   @Test
   public void testValues() {
      Assert.assertEquals(16, StateColor.values().length);
   }

   @Test
   public void testValueOf() {
      Assert.assertEquals(StateColor.BLACK, StateColor.valueOf(StateColor.BLACK.name()));
   }

}
