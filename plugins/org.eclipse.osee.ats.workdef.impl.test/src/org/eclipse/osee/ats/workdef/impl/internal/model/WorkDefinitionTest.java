/*
 * Created on Mar 21, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.impl.internal.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.ats.workdef.api.IAtsStateDefinition;
import org.eclipse.osee.ats.workdef.api.IAtsWorkDefinition;
import org.eclipse.osee.ats.workdef.api.RuleDefinitionOption;
import org.eclipse.osee.ats.workdef.api.StateType;
import org.eclipse.osee.ats.workdef.impl.internal.AtsWorkDefinitionServiceImpl;
import org.junit.Test;

/**
 * Test case for {@link WorkDefinition}
 * 
 * @author Donald G. Dunne
 */
public class WorkDefinitionTest {

   @Test
   public void testGetStates() {
      IAtsStateDefinition endorse = new StateDefinition("endorse");
      IAtsWorkDefinition def = new WorkDefinition("this");
      def.addState(endorse);
      Assert.assertEquals(1, def.getStates().size());
      Assert.assertEquals(endorse, def.getStates().iterator().next());
   }

   @Test
   public void testGetStatesOrderedByOrdinal() {
      IAtsStateDefinition endorse = new StateDefinition("endorse");
      endorse.setStateType(StateType.Working);
      IAtsStateDefinition analyze = new StateDefinition("analyze");
      analyze.setStateType(StateType.Working);
      IAtsStateDefinition implement = new StateDefinition("implement");
      implement.setStateType(StateType.Working);
      IAtsStateDefinition completed = new StateDefinition("completed");
      completed.setStateType(StateType.Completed);

      IAtsWorkDefinition def = new WorkDefinition("this");
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

   @Test(expected = IllegalArgumentException.class)
   public void testGetStatesOrderedByDefaultToState_exception() {
      IAtsWorkDefinition def = new WorkDefinition("this");
      new AtsWorkDefinitionServiceImpl().getStatesOrderedByDefaultToState(def);
      def.setStartState(null);
   }

   @Test
   public void testGetStatesOrderedByDefaultToState() {
      IAtsStateDefinition endorse = new StateDefinition("endorse");
      endorse.setStateType(StateType.Working);
      IAtsStateDefinition analyze = new StateDefinition("analyze");
      analyze.setStateType(StateType.Working);
      IAtsStateDefinition completed = new StateDefinition("completed");
      completed.setStateType(StateType.Completed);

      IAtsWorkDefinition def = new WorkDefinition("this");
      def.addState(completed);
      def.addState(analyze);
      def.addState(endorse);
      endorse.setOrdinal(1);
      analyze.setOrdinal(2);
      def.setStartState(endorse);

      endorse.setDefaultToState(analyze);
      endorse.getToStates().add(analyze);
      endorse.getToStates().add(completed);

      // handle case where one state is completed
      List<IAtsStateDefinition> states = new AtsWorkDefinitionServiceImpl().getStatesOrderedByDefaultToState(def);
      Assert.assertEquals(endorse, states.get(0));
      Assert.assertEquals(analyze, states.get(1));
      Assert.assertEquals(completed, states.get(2));

      // handle case where all states are working
      completed.setStateType(StateType.Working);
      states = new AtsWorkDefinitionServiceImpl().getStatesOrderedByDefaultToState(def);
      Assert.assertEquals(endorse, states.get(0));
      Assert.assertEquals(analyze, states.get(1));
      Assert.assertEquals(completed, states.get(2));

   }

   @Test
   public void testGetStatesOrderedByDefaultToState_startStateOrderedPages() {
      IAtsStateDefinition endorse = new StateDefinition("endorse");
      endorse.setStateType(StateType.Working);
      IAtsStateDefinition analyze = new StateDefinition("analyze");
      analyze.setStateType(StateType.Working);
      IAtsStateDefinition completed = new StateDefinition("completed");
      completed.setStateType(StateType.Completed);

      IAtsWorkDefinition def = new WorkDefinition("this");
      List<IAtsStateDefinition> states = new LinkedList<IAtsStateDefinition>();
      states.addAll(Arrays.asList(endorse, analyze));
      new AtsWorkDefinitionServiceImpl().getStatesOrderedByDefaultToState(def, endorse, states);
      Assert.assertEquals(2, states.size());
   }

   @Test
   public void testGetStatesOrderedByDefaultToState_defaultPage() {
      IAtsStateDefinition endorse = new StateDefinition("endorse");
      endorse.setStateType(StateType.Working);
      IAtsStateDefinition analyze = new StateDefinition("analyze");
      analyze.setStateType(StateType.Working);
      IAtsStateDefinition completed = new StateDefinition("completed");
      completed.setStateType(StateType.Completed);

      IAtsWorkDefinition def = new WorkDefinition("this");
      endorse.setDefaultToState(endorse);
      List<IAtsStateDefinition> states = new LinkedList<IAtsStateDefinition>();
      states.addAll(Arrays.asList(analyze, completed));
      new AtsWorkDefinitionServiceImpl().getStatesOrderedByDefaultToState(def, endorse, states);
      Assert.assertEquals(3, states.size());
   }

   @Test
   public void testGetDefaultToState() {
      IAtsStateDefinition endorse = new StateDefinition("endorse");
      IAtsStateDefinition analyze = new StateDefinition("analyze");
      Assert.assertNull(endorse.getDefaultToState());
      endorse.setDefaultToState(analyze);
      Assert.assertEquals(analyze, endorse.getDefaultToState());
   }

   @Test
   public void testGetStateNames() {
      IAtsWorkDefinition def = new WorkDefinition("this");
      def.addState(new StateDefinition("endorse"));
      def.addState(new StateDefinition("analyze"));
      Assert.assertEquals(2, new AtsWorkDefinitionServiceImpl().getStateNames(def).size());
      Assert.assertTrue(new AtsWorkDefinitionServiceImpl().getStateNames(def).contains("endorse"));
      Assert.assertTrue(new AtsWorkDefinitionServiceImpl().getStateNames(def).contains("analyze"));
   }

   @Test
   public void testGetStateByName() {
      IAtsWorkDefinition def = new WorkDefinition("this");
      IAtsStateDefinition endorse = new StateDefinition("endorse");
      IAtsStateDefinition analyze = new StateDefinition("analyze");
      def.addState(endorse);
      def.addState(analyze);
      Assert.assertEquals(endorse, def.getStateByName("endorse"));
      Assert.assertNull(def.getStateByName("asdf"));
      Assert.assertNull(def.getStateByName(null));
   }

   @Test
   public void testHasRule() {
      IAtsStateDefinition endorse = new StateDefinition("endorse");
      IAtsWorkDefinition def = new WorkDefinition("this");
      Assert.assertFalse(def.hasRule(RuleDefinitionOption.AddDecisionValidateBlockingReview.name()));
      Assert.assertFalse(def.hasRule("asdf"));

      def.addState(endorse);
      def.getRules().add(RuleDefinitionOption.AddDecisionValidateBlockingReview.name());

      Assert.assertTrue(def.hasRule(RuleDefinitionOption.AddDecisionValidateBlockingReview.name()));
      Assert.assertFalse(def.hasRule("asdf"));
   }

   @Test
   public void testGetStartState() {
      IAtsWorkDefinition def = new WorkDefinition("this");
      Assert.assertNull(def.getStartState());
      IAtsStateDefinition endorse = def.addState(new StateDefinition("endorse"));
      def.setStartState(endorse);
      Assert.assertEquals(endorse, def.getStartState());
   }

   @Test
   public void testGetIds() {
      IAtsWorkDefinition def = new WorkDefinition("this");
      Assert.assertEquals("", def.getId());
      Assert.assertEquals("this", def.getId());
      def.setId("3.4");
      Assert.assertEquals("3.4", def.getId());
      def.setId("3.5");
      Assert.assertEquals("3.5", def.getId());
   }

   @Test
   public void testIsStateWeightingEnabled() {
      IAtsWorkDefinition def = new WorkDefinition("this");
      Assert.assertFalse(new AtsWorkDefinitionServiceImpl().isStateWeightingEnabled(def));
      IAtsStateDefinition endorse = def.addState(new StateDefinition("endorse"));
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
      obj3.setName(null);
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
      obj.setName(null);
      Assert.assertEquals(31, obj.hashCode());
   }

}
