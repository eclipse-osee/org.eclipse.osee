/*
 * Created on Mar 21, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.ats.core.workflow.WorkPageType;
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
      def.getStates().add(endorse);
      Assert.assertEquals(1, def.getStates().size());
      Assert.assertEquals(endorse, def.getStates().iterator().next());
   }

   @Test
   public void testGetStatesOrderedByOrdinal() {
      StateDefinition endorse = new StateDefinition("endorse");
      endorse.setWorkPageType(WorkPageType.Working);
      StateDefinition analyze = new StateDefinition("analyze");
      analyze.setWorkPageType(WorkPageType.Working);
      StateDefinition implement = new StateDefinition("implement");
      implement.setWorkPageType(WorkPageType.Working);
      StateDefinition completed = new StateDefinition("completed");
      completed.setWorkPageType(WorkPageType.Completed);

      WorkDefinition def = new WorkDefinition("this");
      def.getStates().add(completed);
      def.getStates().add(analyze);
      def.getStates().add(endorse);
      def.getStates().add(implement);
      endorse.setOrdinal(1);
      analyze.setOrdinal(2);
      implement.setOrdinal(3);
      Assert.assertEquals(4, def.getStates().size());
      List<StateDefinition> states = def.getStatesOrderedByOrdinal();
      Assert.assertEquals(endorse, states.get(0));
      Assert.assertEquals(analyze, states.get(1));
      Assert.assertEquals(implement, states.get(2));
      Assert.assertEquals(completed, states.get(3));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testGetStatesOrderedByDefaultToState_exception() {
      WorkDefinition def = new WorkDefinition("this");
      def.getStatesOrderedByDefaultToState();
      def.setStartState(null);
   }

   @Test
   public void testGetStatesOrderedByDefaultToState() {
      StateDefinition endorse = new StateDefinition("endorse");
      endorse.setWorkPageType(WorkPageType.Working);
      StateDefinition analyze = new StateDefinition("analyze");
      analyze.setWorkPageType(WorkPageType.Working);
      StateDefinition completed = new StateDefinition("completed");
      completed.setWorkPageType(WorkPageType.Completed);

      WorkDefinition def = new WorkDefinition("this");
      def.getStates().add(completed);
      def.getStates().add(analyze);
      def.getStates().add(endorse);
      endorse.setOrdinal(1);
      analyze.setOrdinal(2);
      def.setStartState(endorse);

      endorse.setDefaultToState(analyze);
      endorse.getToStates().add(analyze);
      endorse.getToStates().add(completed);

      // handle case where one state is completed
      List<StateDefinition> states = def.getStatesOrderedByDefaultToState();
      Assert.assertEquals(endorse, states.get(0));
      Assert.assertEquals(analyze, states.get(1));
      Assert.assertEquals(completed, states.get(2));

      // handle case where all states are working
      completed.setWorkPageType(WorkPageType.Working);
      states = def.getStatesOrderedByDefaultToState();
      Assert.assertEquals(endorse, states.get(0));
      Assert.assertEquals(analyze, states.get(1));
      Assert.assertEquals(completed, states.get(2));

   }

   @Test
   public void testGetStatesOrderedByDefaultToState_startStateOrderedPages() {
      StateDefinition endorse = new StateDefinition("endorse");
      endorse.setWorkPageType(WorkPageType.Working);
      StateDefinition analyze = new StateDefinition("analyze");
      analyze.setWorkPageType(WorkPageType.Working);
      StateDefinition completed = new StateDefinition("completed");
      completed.setWorkPageType(WorkPageType.Completed);

      WorkDefinition def = new WorkDefinition("this");
      List<StateDefinition> states = new LinkedList<StateDefinition>();
      states.addAll(Arrays.asList(endorse, analyze));
      def.getStatesOrderedByDefaultToState(endorse, states);
      Assert.assertEquals(2, states.size());
   }

   @Test
   public void testGetStatesOrderedByDefaultToState_defaultPage() {
      StateDefinition endorse = new StateDefinition("endorse");
      endorse.setWorkPageType(WorkPageType.Working);
      StateDefinition analyze = new StateDefinition("analyze");
      analyze.setWorkPageType(WorkPageType.Working);
      StateDefinition completed = new StateDefinition("completed");
      completed.setWorkPageType(WorkPageType.Completed);

      WorkDefinition def = new WorkDefinition("this");
      endorse.setDefaultToState(endorse);
      List<StateDefinition> states = new LinkedList<StateDefinition>();
      states.addAll(Arrays.asList(analyze, completed));
      def.getStatesOrderedByDefaultToState(endorse, states);
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
      def.getStates().add(new StateDefinition("endorse"));
      def.getStates().add(new StateDefinition("analyze"));
      Assert.assertEquals(2, def.getStateNames().size());
      Assert.assertTrue(def.getStateNames().contains("endorse"));
      Assert.assertTrue(def.getStateNames().contains("analyze"));
   }

   @Test
   public void testGetStateByName() {
      WorkDefinition def = new WorkDefinition("this");
      StateDefinition endorse = new StateDefinition("endorse");
      StateDefinition analyze = new StateDefinition("analyze");
      def.getStates().add(endorse);
      def.getStates().add(analyze);
      Assert.assertEquals(endorse, def.getStateByName("endorse"));
      Assert.assertNull(def.getStateByName("asdf"));
      Assert.assertNull(def.getStateByName(null));
   }

   @Test
   public void testHasRule() {
      StateDefinition endorse = new StateDefinition("endorse");
      WorkDefinition def = new WorkDefinition("this");
      Assert.assertFalse(def.hasRule(RuleDefinitionOption.AddDecisionValidateBlockingReview.name()));
      Assert.assertFalse(def.hasRule("asdf"));

      def.getStates().add(endorse);
      RuleDefinition ruleDef = new RuleDefinition(RuleDefinitionOption.AddDecisionValidateBlockingReview);
      def.getRules().add(ruleDef);

      Assert.assertTrue(def.hasRule(RuleDefinitionOption.AddDecisionValidateBlockingReview.name()));
      Assert.assertFalse(def.hasRule("asdf"));
   }

   @Test
   public void testGetOrCreateState() {
      WorkDefinition def = new WorkDefinition("this");
      Assert.assertNull(def.getStateByName("endorse"));
      StateDefinition endorse = def.getOrCreateState("endorse");
      Assert.assertEquals(endorse, def.getStateByName("endorse"));

      endorse = def.getOrCreateState("endorse");
      Assert.assertNotNull(endorse);
   }

   @Test
   public void testGetSetVersion() {
      WorkDefinition def = new WorkDefinition("this");
      Assert.assertNull(def.getVersion());
      def.setVersion("3.4");
      Assert.assertEquals("3.4", def.getVersion());
   }

   @Test
   public void testGetStartState() {
      WorkDefinition def = new WorkDefinition("this");
      Assert.assertNull(def.getStartState());
      StateDefinition endorse = def.getOrCreateState("endorse");
      def.setStartState(endorse);
      Assert.assertEquals(endorse, def.getStartState());
   }

   @Test
   public void testGetIds() {
      WorkDefinition def = new WorkDefinition("this");
      Assert.assertFalse(def.getIds().isEmpty());
      Assert.assertEquals("this", def.getIds().iterator().next());
      def.getIds().add("3.4");
      Assert.assertEquals("3.4", def.getIds().iterator().next());
      def.getIds().add("3.5");
      Assert.assertTrue(def.getIds().contains("3.4"));
      Assert.assertTrue(def.getIds().contains("3.5"));
   }

   @Test
   public void testIsStateWeightingEnabled() {
      WorkDefinition def = new WorkDefinition("this");
      Assert.assertFalse(def.isStateWeightingEnabled());
      StateDefinition endorse = def.getOrCreateState("endorse");
      endorse.setStateWeight(34);
      Assert.assertTrue(def.isStateWeightingEnabled());

      endorse.setStateWeight(0);
      Assert.assertFalse(def.isStateWeightingEnabled());
   }

   @Test
   public void testValidateStateWeighting() {
      WorkDefinition def = new WorkDefinition("this");
      Assert.assertTrue(def.validateStateWeighting().isOK());

      StateDefinition endorse = def.getOrCreateState("endorse");
      endorse.setStateWeight(34);
      Assert.assertFalse(def.validateStateWeighting().isOK());

      StateDefinition analyze = def.getOrCreateState("analyze");
      analyze.setStateWeight(66);
      Assert.assertTrue(def.validateStateWeighting().isOK());
   }

   @Test
   public void testEqualsObject() {
      WorkDefinition obj = new WorkDefinition("hello");
      Assert.assertTrue(obj.equals(obj));

      WorkDefinition obj2 = new WorkDefinition("hello");

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
