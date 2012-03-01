/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workflow;

import org.junit.Assert;
import org.junit.Test;

/**
 * @link WorkPageAdapter
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
