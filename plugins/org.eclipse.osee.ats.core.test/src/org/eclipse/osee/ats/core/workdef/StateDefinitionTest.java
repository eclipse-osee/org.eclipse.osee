/*
 * Created on Mar 22, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import junit.framework.Assert;
import org.eclipse.osee.ats.core.workflow.WorkPageType;
import org.junit.Test;

public class StateDefinitionTest {

   @Test
   public void testToString() {
      StateDefinition def = new StateDefinition("endorse");
      Assert.assertEquals("[endorse][null]", def.toString());
      def.setWorkPageType(WorkPageType.Working);
      Assert.assertEquals("[endorse][Working]", def.toString());
   }

   @Test
   public void testGetStateItems() {
      StateDefinition def = new StateDefinition("endorse");
      Assert.assertEquals(0, def.getStateItems().size());
      def.getStateItems().add(new StateItem("item"));
      Assert.assertEquals(1, def.getStateItems().size());
   }

   @Test
   public void testAddRemoveRule() {
      StateDefinition def = new StateDefinition("endorse");
      Assert.assertEquals(0, def.getRules().size());
      def.addRule(new RuleDefinition("rule"), "location");
      Assert.assertEquals(1, def.getRules().size());
      RuleDefinition ruleDef = new RuleDefinition(RuleDefinitionOption.AddDecisionValidateBlockingReview);
      def.addRule(ruleDef, "location 2");
      Assert.assertEquals(2, def.getRules().size());

      Assert.assertEquals("location 2", def.getRuleLocations(ruleDef).iterator().next());

      Assert.assertTrue(def.hasRule(RuleDefinitionOption.AddDecisionValidateBlockingReview));
      Assert.assertFalse(def.hasRule(RuleDefinitionOption.AddDecisionValidateNonBlockingReview));

      def.removeRule(ruleDef);
      Assert.assertFalse(def.hasRule(RuleDefinitionOption.AddDecisionValidateBlockingReview));
   }

   @Test
   public void testGetSetWorkPageType() {
      StateDefinition def = new StateDefinition("endorse");
      Assert.assertNull(def.getWorkPageType());
      def.setWorkPageType(WorkPageType.Working);
      Assert.assertEquals(WorkPageType.Working, def.getWorkPageType());
   }

   @Test
   public void testGetSetOrdinal() {
      StateDefinition def = new StateDefinition("endorse");
      Assert.assertEquals(0, def.getOrdinal());
      def.setOrdinal(3);
      Assert.assertEquals(3, def.getOrdinal());
   }

   @Test
   public void testGetToStates() {
      StateDefinition endorse = new StateDefinition("endorse");
      StateDefinition analyze = new StateDefinition("endorse");
      StateDefinition completed = new StateDefinition("endorse");
      endorse.getToStates().add(analyze);
      endorse.getToStates().add(completed);
      Assert.assertTrue(endorse.getToStates().contains(analyze));
      Assert.assertTrue(endorse.getToStates().contains(completed));
   }

   @Test
   public void testGetPageName() {
      StateDefinition endorse = new StateDefinition("endorse");
      Assert.assertEquals("endorse", endorse.getPageName());
   }

   @Test
   public void testGetSetWorkDefinition() {
      WorkDefinition workDef = new WorkDefinition("mine");
      StateDefinition state = new StateDefinition("endorse");
      Assert.assertNull(state.getWorkDefinition());
      state.setWorkDefinition(workDef);
      Assert.assertEquals(workDef, state.getWorkDefinition());

      Assert.assertEquals("mine.endorse", state.getFullName());
   }

   @Test
   public void testWorkPageType() {
      StateDefinition state = new StateDefinition("endorse");
      state.setWorkPageType(WorkPageType.Working);

      Assert.assertTrue(state.isWorkingPage());
      Assert.assertFalse(state.isCancelledPage());
      Assert.assertFalse(state.isCompletedPage());
      Assert.assertFalse(state.isCompletedOrCancelledPage());

      state.setWorkPageType(WorkPageType.Completed);
      Assert.assertTrue(WorkPageType.Completed.isCompletedPage());
      Assert.assertTrue(WorkPageType.Completed.isCompletedOrCancelledPage());

      state.setWorkPageType(WorkPageType.Cancelled);
      Assert.assertTrue(WorkPageType.Cancelled.isCancelledPage());
      Assert.assertTrue(WorkPageType.Cancelled.isCompletedOrCancelledPage());
   }

   @Test
   public void testGetOverrideAttributeValidationStates() {
      StateDefinition endorse = new StateDefinition("endorse");
      StateDefinition analyze = new StateDefinition("endorse");
      StateDefinition completed = new StateDefinition("endorse");
      endorse.getOverrideAttributeValidationStates().add(analyze);
      endorse.getOverrideAttributeValidationStates().add(completed);
      Assert.assertTrue(endorse.getOverrideAttributeValidationStates().contains(analyze));
      Assert.assertTrue(endorse.getOverrideAttributeValidationStates().contains(completed));
   }

   @Test
   public void testGetWidgetsFromStateItems() {
      StateDefinition def = new StateDefinition("endorse");
      Assert.assertEquals(0, def.getWidgetsFromStateItems().size());

      WidgetDefinition widget1 = new WidgetDefinition("item 1");
      def.getStateItems().add(widget1);

      CompositeStateItem stateItem2 = new CompositeStateItem(2);
      def.getStateItems().add(stateItem2);
      WidgetDefinition widget2 = new WidgetDefinition("item 2");
      stateItem2.getStateItems().add(widget2);
      WidgetDefinition widget3 = new WidgetDefinition("item 3");
      stateItem2.getStateItems().add(widget3);

      CompositeStateItem stateItem3 = new CompositeStateItem(2);
      stateItem2.getStateItems().add(stateItem3);
      // StateItem is an base class, so it's widgets won't be seen
      StateItem widget4 = new StateItem("item 4");
      stateItem3.getStateItems().add(widget4);

      Assert.assertEquals(3, def.getWidgetsFromStateItems().size());
   }

   @Test
   public void testGetDecisionReviews() {
      StateDefinition def = new StateDefinition("endorse");
      Assert.assertEquals(0, def.getDecisionReviews().size());
      def.getDecisionReviews().add(new DecisionReviewDefinition("review 1"));
      def.getDecisionReviews().add(new DecisionReviewDefinition("review 2"));
      Assert.assertEquals(2, def.getDecisionReviews().size());
   }

   @Test
   public void testGetPeerReviews() {
      StateDefinition def = new StateDefinition("endorse");
      Assert.assertEquals(0, def.getPeerReviews().size());
      def.getPeerReviews().add(new PeerReviewDefinition("review 1"));
      def.getPeerReviews().add(new PeerReviewDefinition("review 2"));
      Assert.assertEquals(2, def.getPeerReviews().size());
   }

   @Test
   public void testGetSetPercentWeight() {
      StateDefinition def = new StateDefinition("endorse");
      Assert.assertEquals(0, def.getStateWeight());
      def.setStateWeight(34);
      Assert.assertEquals(34, def.getStateWeight());

   }

   @Test
   public void testGetSetRecommendedPercentComplete() {
      StateDefinition endorse = new StateDefinition("endorse");
      Assert.assertEquals((Integer) null, endorse.getRecommendedPercentComplete());
      endorse.setRecommendedPercentComplete(34);
      Assert.assertEquals((Integer) 34, endorse.getRecommendedPercentComplete());
   }

   @Test
   public void testGetSetColor() {
      StateDefinition endorse = new StateDefinition("endorse");
      Assert.assertNull(endorse.getColor());
      endorse.setColor(StateColor.BLUE);
      Assert.assertEquals(StateColor.BLUE, endorse.getColor());
   }

   @Test
   public void testHasWidgetNamed() {
      StateDefinition def = new StateDefinition("endorse");
      Assert.assertFalse(def.hasWidgetNamed("item 2"));

      CompositeStateItem stateItem2 = new CompositeStateItem(2);
      def.getStateItems().add(stateItem2);
      WidgetDefinition widget2 = new WidgetDefinition("item 2");
      stateItem2.getStateItems().add(widget2);
      WidgetDefinition widget3 = new WidgetDefinition("item 3");
      stateItem2.getStateItems().add(widget3);

      Assert.assertFalse(def.hasWidgetNamed("item 45"));
      Assert.assertTrue(def.hasWidgetNamed("item 2"));
   }

   @Test
   public void testEqualsObject() {
      StateDefinition obj = new StateDefinition("hello");
      Assert.assertTrue(obj.equals(obj));

      StateDefinition obj2 = new StateDefinition("hello");

      Assert.assertTrue(obj.equals(obj2));
      Assert.assertFalse(obj.equals(null));
      Assert.assertFalse(obj.equals("str"));

      StateDefinition obj3 = new StateDefinition("hello");
      obj3.setName(null);
      Assert.assertFalse(obj.equals(obj3));
      Assert.assertFalse(obj3.equals(obj));

      StateDefinition obj4 = new StateDefinition("hello");
      obj4.setName(null);
      Assert.assertFalse(obj3.equals(obj4));
   }

   @Test
   public void testHashCode() {
      StateDefinition obj = new StateDefinition("hello");
      Assert.assertEquals(99162353, obj.hashCode());

      obj = new StateDefinition("hello");
      obj.setName(null);
      Assert.assertEquals(31, obj.hashCode());
   }

}
