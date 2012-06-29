/*
 * Created on Mar 19, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.impl.internal.model;

import junit.framework.Assert;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.junit.Test;

/**
 * Test case for {@link WidgetOption}
 *
 * @author Donald G. Dunne
 */
public class WidgetOptionTest {

   @Test
   public void testValues() {
      Assert.assertEquals(31, WidgetOption.values().length);
   }

   @Test
   public void testValueOf() {
      Assert.assertEquals(WidgetOption.ADD_DEFAULT_VALUE, WidgetOption.valueOf(WidgetOption.ADD_DEFAULT_VALUE.name()));
   }

}
