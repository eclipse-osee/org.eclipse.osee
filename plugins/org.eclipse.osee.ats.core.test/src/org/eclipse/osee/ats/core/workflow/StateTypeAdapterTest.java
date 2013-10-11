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

import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.core.workflow.state.StateTypeAdapter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link StateTypeAdapter}
 * 
 * @author Donald G. Dunne
 */
public class StateTypeAdapterTest {

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
      Assert.assertEquals("Five", OrderedStates.valueOf("Five").getName());
   }

   @Test
   public void testValues() {
      Assert.assertEquals(8, OrderedStates.values().size());

      // values should return in ordinal order
      Assert.assertEquals(OrderedStates.One.getName(), OrderedStates.values().get(0).getName());
      Assert.assertEquals(OrderedStates.Five.getName(), OrderedStates.values().get(4).getName());
      Assert.assertEquals(OrderedStates.Completed.getName(), OrderedStates.values().get(7).getName());
   }

   @Test
   public void testSetGetDescription() {
      TestState state = new TestState("Endorse", StateType.Working);
      Assert.assertNull(state.getDescription());
      state.setDescription("desc");
      Assert.assertEquals("desc", state.getDescription());
   }

   @Test
   public void testCompletedCancelledWorking() {
      TestState state = new TestState("Endorse", StateType.Working);
      Assert.assertTrue(state.getStateType().isWorkingState());
      Assert.assertFalse(state.getStateType().isCancelledState());
      Assert.assertFalse(state.getStateType().isCompletedState());
      Assert.assertFalse(state.getStateType().isCompletedOrCancelledState());

      state = new TestState("Endorse", StateType.Cancelled);
      Assert.assertFalse(state.getStateType().isWorkingState());
      Assert.assertTrue(state.getStateType().isCancelledState());
      Assert.assertTrue(state.getStateType().isCompletedOrCancelledState());

      state = new TestState("Endorse", StateType.Completed);
      Assert.assertFalse(state.getStateType().isWorkingState());
      Assert.assertTrue(state.getStateType().isCompletedState());
      Assert.assertTrue(state.getStateType().isCompletedOrCancelledState());
   }

   @Test
   public void testToString() {
      TestState state = new TestState("Endorse", StateType.Working);
      Assert.assertEquals("[Endorse][Working]", state.toString());
   }

   private class TestState extends StateTypeAdapter {

      public TestState(String pageName, StateType StateType) {
         super(TestState.class, pageName, StateType);
      }

   }
}
