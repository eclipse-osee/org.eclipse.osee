/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.core.workflow.state;

import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.StateTypeAdapter;
import org.eclipse.osee.ats.core.workflow.OneStates;
import org.eclipse.osee.ats.core.workflow.OrderedStates;
import org.eclipse.osee.ats.core.workflow.TestState;
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
      Assert.assertTrue(state.isWorking());
      Assert.assertFalse(state.isCancelled());
      Assert.assertFalse(state.isCompleted());
      Assert.assertFalse(state.isCompletedOrCancelled());

      state = new TestState("Endorse", StateType.Cancelled);
      Assert.assertFalse(state.isWorking());
      Assert.assertTrue(state.isCancelled());
      Assert.assertTrue(state.isCompletedOrCancelled());

      state = new TestState("Endorse", StateType.Completed);
      Assert.assertFalse(state.isWorking());
      Assert.assertTrue(state.isCompleted());
      Assert.assertTrue(state.isCompletedOrCancelled());
   }

   @Test
   public void testToString() {
      TestState state = new TestState("Endorse", StateType.Working);
      Assert.assertEquals("[Endorse][Working]", state.toString());
   }

   @Test
   public void testDescription() {
      TestState state = new TestState("Endorse", StateType.Working);
      Assert.assertNull(state.getDescription());
      state.setDescription("description");
      Assert.assertEquals("description", state.getDescription());
   }

}
