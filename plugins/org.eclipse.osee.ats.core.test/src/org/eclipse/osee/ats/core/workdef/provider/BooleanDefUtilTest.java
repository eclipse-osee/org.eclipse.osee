/*
 * Created on Mar 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef.provider;

import junit.framework.Assert;
import org.eclipse.osee.ats.dsl.atsDsl.BooleanDef;
import org.junit.Test;

/**
 * Test case for {@link BooleanDefUtil}
 *
 * @author Donald G. Dunne
 */
public class BooleanDefUtilTest {

   @Test
   public void testConstructor() {
      new BooleanDefUtil();
   }

   @Test
   public void testGet() {
      Assert.assertFalse(BooleanDefUtil.get(BooleanDef.NONE, false));
      Assert.assertTrue(BooleanDefUtil.get(BooleanDef.NONE, true));

      Assert.assertFalse(BooleanDefUtil.get(BooleanDef.FALSE, false));
      Assert.assertFalse(BooleanDefUtil.get(BooleanDef.FALSE, true));

      Assert.assertTrue(BooleanDefUtil.get(BooleanDef.TRUE, false));
      Assert.assertTrue(BooleanDefUtil.get(BooleanDef.TRUE, true));

      Assert.assertTrue(BooleanDefUtil.get(null, true));

   }

}
