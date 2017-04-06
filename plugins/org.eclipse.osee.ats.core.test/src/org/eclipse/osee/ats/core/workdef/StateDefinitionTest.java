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
package org.eclipse.osee.ats.core.workdef;

import org.eclipse.osee.ats.api.workdef.IAtsCompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.DecisionReviewDefinition;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.api.workdef.model.PeerReviewDefinition;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.junit.Assert;
import org.junit.Test;

public class StateDefinitionTest {

   @Test
   public void testToString() {
      StateDefinition def = new StateDefinition("endorse");
      Assert.assertEquals("[endorse][null]", def.toString());
      def.setStateType(StateType.Working);
      Assert.assertEquals("[endorse][Working]", def.toString());
   }

   @Test
   public void testGetStateItems() {
      StateDefinition def = new StateDefinition("endorse");
      Assert.assertEquals(0, def.getLayoutItems().size());
      def.getLayoutItems().add(new LayoutItem("item"));
      Assert.assertEquals(1, def.getLayoutItems().size());
   }

   @Test
   public void testAddRemoveRule() {
      StateDefinition def = new StateDefinition("endorse");
      Assert.assertEquals(0, def.getRules().size());
      def.addRule("rule");
      Assert.assertEquals(1, def.getRules().size());
      def.addRule(RuleDefinitionOption.AddDecisionValidateBlockingReview.name());
      Assert.assertEquals(2, def.getRules().size());

      Assert.assertTrue(def.hasRule(RuleDefinitionOption.AddDecisionValidateBlockingReview.name()));
      Assert.assertFalse(def.hasRule(RuleDefinitionOption.AddDecisionValidateNonBlockingReview.name()));

      def.removeRule(RuleDefinitionOption.AddDecisionValidateBlockingReview.name());
      Assert.assertFalse(def.hasRule(RuleDefinitionOption.AddDecisionValidateBlockingReview.name()));
   }

   @Test
   public void testGetSetStateType() {
      StateDefinition def = new StateDefinition("endorse");
      Assert.assertNull(def.getStateType());
      def.setStateType(StateType.Working);
      Assert.assertEquals(StateType.Working, def.getStateType());
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
      Assert.assertEquals("endorse", endorse.getName());
   }

   @Test
   public void testGetSetWorkDefinition() {
      WorkDefinition workDef = new WorkDefinition(15L, "mine");
      StateDefinition state = new StateDefinition("endorse");
      Assert.assertNull(state.getWorkDefinition());
      state.setWorkDefinition(workDef);
      Assert.assertEquals(workDef, state.getWorkDefinition());

      Assert.assertEquals("mine.endorse", state.getFullName());
   }

   @Test
   public void testStateType() {
      StateDefinition state = new StateDefinition("endorse");
      state.setStateType(StateType.Working);

      Assert.assertTrue(state.getStateType().isWorkingState());
      Assert.assertFalse(state.getStateType().isCancelledState());
      Assert.assertFalse(state.getStateType().isCompletedState());
      Assert.assertFalse(state.getStateType().isCompletedOrCancelledState());

      state.setStateType(StateType.Completed);
      Assert.assertTrue(StateType.Completed.isCompletedState());
      Assert.assertTrue(StateType.Completed.isCompletedOrCancelledState());

      state.setStateType(StateType.Cancelled);
      Assert.assertTrue(StateType.Cancelled.isCancelledState());
      Assert.assertTrue(StateType.Cancelled.isCompletedOrCancelledState());
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
      Assert.assertEquals(0,
         new AtsWorkDefinitionServiceImpl(null, null, null, null, null).getWidgetsFromLayoutItems(def).size());

      IAtsWidgetDefinition widget1 = new WidgetDefinition("item 1");
      def.getLayoutItems().add(widget1);

      IAtsCompositeLayoutItem stateItem2 = new CompositeLayoutItem(2);
      def.getLayoutItems().add(stateItem2);
      IAtsWidgetDefinition widget2 = new WidgetDefinition("item 2");
      stateItem2.getaLayoutItems().add(widget2);
      IAtsWidgetDefinition widget3 = new WidgetDefinition("item 3");
      stateItem2.getaLayoutItems().add(widget3);

      CompositeLayoutItem stateItem3 = new CompositeLayoutItem(2);
      stateItem2.getaLayoutItems().add(stateItem3);
      // StateItem is an base class, so it's widgets won't be seen
      LayoutItem widget4 = new LayoutItem("item 4");
      stateItem3.getaLayoutItems().add(widget4);

      Assert.assertEquals(3,
         new AtsWorkDefinitionServiceImpl(null, null, null, null, null).getWidgetsFromLayoutItems(def).size());
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
