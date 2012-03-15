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
 * Test case for {@link WorkPageAdapter}
 *
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

   @Test
   public void testSetGetDescription() {
      TestState state = new TestState("Endorse", WorkPageType.Working);
      Assert.assertNull(state.getDescription());
      state.setDescription("desc");
      Assert.assertEquals("desc", state.getDescription());
   }

   @Test
   public void testCompletedCancelledWorking() {
      TestState state = new TestState("Endorse", WorkPageType.Working);
      Assert.assertTrue(state.isWorkingPage());
      Assert.assertFalse(state.isCancelledPage());
      Assert.assertFalse(state.isCompletedPage());
      Assert.assertFalse(state.isCompletedOrCancelledPage());

      state = new TestState("Endorse", WorkPageType.Cancelled);
      Assert.assertFalse(state.isWorkingPage());
      Assert.assertTrue(state.isCancelledPage());
      Assert.assertTrue(state.isCompletedOrCancelledPage());

      state = new TestState("Endorse", WorkPageType.Completed);
      Assert.assertFalse(state.isWorkingPage());
      Assert.assertTrue(state.isCompletedPage());
      Assert.assertTrue(state.isCompletedOrCancelledPage());
   }

   @Test
   public void testToString() {
      TestState state = new TestState("Endorse", WorkPageType.Working);
      Assert.assertEquals("[Endorse][Working]", state.toString());
   }

   private class TestState extends WorkPageAdapter {

      public TestState(String pageName, WorkPageType workPageType) {
         super(TestState.class, pageName, workPageType);
      }

   }
}
