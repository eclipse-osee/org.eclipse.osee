/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.dsl.integration.internal.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.dsl.integration.internal.AtsWorkDefinitionServiceImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link WorkDefinition}
 *
 * @author Donald G. Dunne
 */
public class WorkDefinitionTest {

   @Test
   public void testGetStates() {
      StateDefinition endorse = new StateDefinition("endorse");
      WorkDefinition def = new WorkDefinition("this");
      def.addState(endorse);
      Assert.assertEquals(1, def.getStates().size());
      Assert.assertEquals(endorse, def.getStates().iterator().next());
   }

   @Test
   public void testGetStatesOrderedByOrdinal() {
      StateDefinition endorse = new StateDefinition("endorse");
      endorse.setStateType(StateType.Working);
      StateDefinition analyze = new StateDefinition("analyze");
      analyze.setStateType(StateType.Working);
      StateDefinition implement = new StateDefinition("implement");
      implement.setStateType(StateType.Working);
      StateDefinition completed = new StateDefinition("completed");
      completed.setStateType(StateType.Completed);

      WorkDefinition def = new WorkDefinition("this");
      def.addState(completed);
      def.addState(analyze);
      def.addState(endorse);
      def.addState(implement);
      endorse.setOrdinal(1);
      analyze.setOrdinal(2);
      implement.setOrdinal(3);
      Assert.assertEquals(4, def.getStates().size());
      List<IAtsStateDefinition> states = new AtsWorkDefinitionServiceImpl().getStatesOrderedByOrdinal(def);
      Assert.assertEquals(endorse, states.get(0));
      Assert.assertEquals(analyze, states.get(1));
      Assert.assertEquals(implement, states.get(2));
      Assert.assertEquals(completed, states.get(3));
   }

   @Test
   public void testGetStatesOrderedByDefaultToState_startStateOrderedPages() {
      StateDefinition endorse = new StateDefinition("endorse");
      endorse.setStateType(StateType.Working);
      StateDefinition analyze = new StateDefinition("analyze");
      analyze.setStateType(StateType.Working);
      StateDefinition completed = new StateDefinition("completed");
      completed.setStateType(StateType.Completed);

      IAtsWorkDefinition def = new WorkDefinition("this");
      List<IAtsStateDefinition> states = new LinkedList<>();
      states.addAll(Arrays.asList(endorse, analyze));
      new AtsWorkDefinitionServiceImpl().getStatesOrderedByDefaultToState(def, endorse, states);
      Assert.assertEquals(2, states.size());
   }

   @Test
   public void testGetStatesOrderedByDefaultToState_defaultPage() {
      StateDefinition endorse = new StateDefinition("endorse");
      endorse.setStateType(StateType.Working);
      StateDefinition analyze = new StateDefinition("analyze");
      analyze.setStateType(StateType.Working);
      StateDefinition completed = new StateDefinition("completed");
      completed.setStateType(StateType.Completed);

      IAtsWorkDefinition def = new WorkDefinition("this");
      endorse.setDefaultToState(endorse);
      List<IAtsStateDefinition> states = new LinkedList<>();
      states.addAll(Arrays.asList(analyze, completed));
      new AtsWorkDefinitionServiceImpl().getStatesOrderedByDefaultToState(def, endorse, states);
      Assert.assertEquals(3, states.size());
   }

   @Test
   public void testGetDefaultToState() {
      StateDefinition endorse = new StateDefinition("endorse");
      StateDefinition analyze = new StateDefinition("analyze");
      Assert.assertNull(endorse.getDefaultToState());
      endorse.setDefaultToState(analyze);
      Assert.assertEquals(analyze, endorse.getDefaultToState());
   }

   @Test
   public void testGetStateNames() {
      WorkDefinition def = new WorkDefinition("this");
      def.addState(new StateDefinition("endorse"));
      def.addState(new StateDefinition("analyze"));
      Assert.assertEquals(2, new AtsWorkDefinitionServiceImpl().getStateNames(def).size());
      Assert.assertTrue(new AtsWorkDefinitionServiceImpl().getStateNames(def).contains("endorse"));
      Assert.assertTrue(new AtsWorkDefinitionServiceImpl().getStateNames(def).contains("analyze"));
   }

   @Test
   public void testGetStateByName() {
      WorkDefinition def = new WorkDefinition("this");
      StateDefinition endorse = new StateDefinition("endorse");
      StateDefinition analyze = new StateDefinition("analyze");
      def.addState(endorse);
      def.addState(analyze);
      Assert.assertEquals(endorse, def.getStateByName("endorse"));
      Assert.assertNull(def.getStateByName("asdf"));
      Assert.assertNull(def.getStateByName(null));
   }

   @Test
   public void testGetStartState() {
      WorkDefinition def = new WorkDefinition("this");
      Assert.assertNull(def.getStartState());
      StateDefinition endorse = new StateDefinition("endorse");
      def.addState(endorse);
      def.setStartState(endorse);
      Assert.assertEquals(endorse, def.getStartState());
   }

   @Test
   public void testGetIds() {
      WorkDefinition def = new WorkDefinition("this");
      Assert.assertEquals("this", def.getId());
      def.setId("3.4");
      Assert.assertEquals("3.4", def.getId());
      def.setId("3.5");
      Assert.assertEquals("3.5", def.getId());
   }

   @Test
   public void testIsStateWeightingEnabled() {
      WorkDefinition def = new WorkDefinition("this");
      Assert.assertFalse(new AtsWorkDefinitionServiceImpl().isStateWeightingEnabled(def));
      StateDefinition endorse = new StateDefinition("endorse");
      def.addState(endorse);
      endorse.setStateWeight(34);
      Assert.assertTrue(new AtsWorkDefinitionServiceImpl().isStateWeightingEnabled(def));

      endorse.setStateWeight(0);
      Assert.assertFalse(new AtsWorkDefinitionServiceImpl().isStateWeightingEnabled(def));
   }

   @Test
   public void testEqualsObject() {
      IAtsWorkDefinition obj = new WorkDefinition("hello");
      Assert.assertTrue(obj.equals(obj));

      IAtsWorkDefinition obj2 = new WorkDefinition("hello");

      Assert.assertTrue(obj.equals(obj2));
      Assert.assertFalse(obj.equals(null));
      Assert.assertFalse(obj.equals("str"));

      WorkDefinition obj3 = new WorkDefinition("hello");
      obj3.setId(null);
      Assert.assertFalse(obj.equals(obj3));
      Assert.assertFalse(obj3.equals(obj));

      WorkDefinition obj4 = new WorkDefinition("hello");
      obj4.setName(null);
      Assert.assertFalse(obj3.equals(obj4));
   }

   @Test
   public void testHashCode() {
      WorkDefinition obj = new WorkDefinition("hello");
      Assert.assertEquals(99162353, obj.hashCode());

      obj = new WorkDefinition("hello");
      obj.setId(null);
      Assert.assertEquals(31, obj.hashCode());
   }

}
