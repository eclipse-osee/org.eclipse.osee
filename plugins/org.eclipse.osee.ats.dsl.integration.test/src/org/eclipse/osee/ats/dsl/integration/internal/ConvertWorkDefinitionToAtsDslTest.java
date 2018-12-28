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
package org.eclipse.osee.ats.dsl.integration.internal;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.workdef.IAtsCompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewOption;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.StateEventType;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.DecisionReviewDefinition;
import org.eclipse.osee.ats.api.workdef.model.DecisionReviewOption;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.api.workdef.model.PeerReviewDefinition;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.dsl.atsDsl.BooleanDef;
import org.eclipse.osee.ats.dsl.atsDsl.Composite;
import org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef;
import org.eclipse.osee.ats.dsl.atsDsl.LayoutDef;
import org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef;
import org.eclipse.osee.ats.dsl.atsDsl.ReviewBlockingType;
import org.eclipse.osee.ats.dsl.atsDsl.StateDef;
import org.eclipse.osee.ats.dsl.atsDsl.WidgetDef;
import org.eclipse.osee.ats.dsl.atsDsl.WorkflowEventType;
import org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslFactoryImpl;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link ConvertWorkDefinitionToAtsDsl}
 *
 * @author Donald G. Dunne
 */
public class ConvertWorkDefinitionToAtsDslTest {

   AtsWorkDefinitionDslServiceImpl service = new AtsWorkDefinitionDslServiceImpl();

   @Test
   public void test_stateDescription() {
      XResultData resultData = new XResultData(false);

      WorkDefinition workDef = new WorkDefinition(15L, "this");
      StateDefinition endorse = new StateDefinition("endorse");
      endorse.setStateType(StateType.Working);
      workDef.addState(endorse);
      workDef.setStartState(endorse);

      ConvertWorkDefinitionToAtsDsl convert = new ConvertWorkDefinitionToAtsDsl(resultData);
      AtsDsl dsl = convert.convert("this", workDef);
      Assert.assertEquals(null, dsl.getWorkDef().iterator().next().getStates().iterator().next().getDescription());
      dsl = convert.convert("this", workDef);
   }

   @Test
   public void test_stateTransitionExceptions() {
      XResultData resultData = new XResultData(false);

      WorkDefinition workDef = new WorkDefinition(15L, "this");
      StateDefinition endorse = new StateDefinition("endorse");
      endorse.setStateType(StateType.Working);
      workDef.addState(endorse);
      StateDefinition analyze = new StateDefinition("analyze");
      analyze.setStateType(StateType.Working);
      workDef.addState(analyze);
      // setup endorse to transition to itself
      endorse.getToStates().add(endorse);
      // setup endorse to transition to analyze twice
      endorse.getToStates().add(analyze);
      endorse.getToStates().add(analyze);
      workDef.setStartState(endorse);

      ConvertWorkDefinitionToAtsDsl convert = new ConvertWorkDefinitionToAtsDsl(resultData);
      AtsDsl dsl = convert.convert("this", workDef);

      StateDef endorseDef = dsl.getWorkDef().iterator().next().getStates().get(0);
      if (endorseDef.getName().equals("analyze")) {
         endorseDef = dsl.getWorkDef().iterator().next().getStates().get(1);
      }
      // only one transition to state should exist; analyze
      Assert.assertEquals(1, endorseDef.getTransitionStates().size());
      Assert.assertEquals("\"analyze\"", endorseDef.getTransitionStates().iterator().next().getState().getName());
   }

   @Test
   public void test_processWorkRulesForState() {
      StateDef dslState = AtsDslFactoryImpl.init().createStateDef();
      List<String> rules = new ArrayList<>();
      XResultData resultData = new XResultData(false);
      ConvertWorkDefinitionToAtsDsl convert = new ConvertWorkDefinitionToAtsDsl(resultData);

      convert.processWorkRulesForState(dslState, rules);
      Assert.assertEquals(0, dslState.getRules().size());

      rules.add(RuleDefinitionOption.AddDecisionValidateBlockingReview.name());
      convert.processWorkRulesForState(dslState, rules);
      Assert.assertEquals(1, dslState.getRules().size());

      rules.clear();
      rules.add("MyRule");
      convert.processWorkRulesForState(dslState, rules);
      Assert.assertEquals(2, dslState.getRules().size());
   }

   @Test
   public void test_processStateWidgets() {
      StateDef dslState = AtsDslFactoryImpl.init().createStateDef();
      XResultData resultData = new XResultData(false);
      WorkDefinition workDef = new WorkDefinition(15L, "this");
      StateDefinition endorse = new StateDefinition("endorse");
      endorse.setStateType(StateType.Working);
      workDef.addState(endorse);
      workDef.setStartState(endorse);

      ConvertWorkDefinitionToAtsDsl convert = new ConvertWorkDefinitionToAtsDsl(resultData);
      convert.convert("this", workDef);

      convert.processStateWidgets(endorse, dslState);

      IAtsWidgetDefinition widgetDef = new WidgetDefinition("wid1");
      endorse.getLayoutItems().add(widgetDef);

      convert.processStateWidgets(endorse, dslState);
   }

   @Test
   public void test_convertWithDecisionReview() {
      DecisionReviewDefinition revDef = new DecisionReviewDefinition("review");
      revDef.setBlockingType(ReviewBlockType.Transition);
      revDef.setStateEventType(StateEventType.CommitBranch);

      XResultData resultData = new XResultData(false);
      WorkDefinition workDef = new WorkDefinition(15L, "this");
      StateDefinition endorse = new StateDefinition("endorse");
      endorse.setStateType(StateType.Working);
      endorse.getDecisionReviews().add(revDef);
      workDef.addState(endorse);
      workDef.setStartState(endorse);

      ConvertWorkDefinitionToAtsDsl convert = new ConvertWorkDefinitionToAtsDsl(resultData);
      convert.convert("this", workDef);
      Assert.assertEquals(0, resultData.getNumErrors());

      // test cache
      convert.convert("this", workDef);
      Assert.assertEquals(0, resultData.getNumErrors());
   }

   @Test
   public void test_createDecisionReview() {
      DecisionReviewDefinition revDef = new DecisionReviewDefinition("review");
      revDef.setBlockingType(ReviewBlockType.Transition);
      revDef.setStateEventType(StateEventType.CommitBranch);
      revDef.setRelatedToState("endorse");
      revDef.getAssignees().add("12345");
      revDef.setAutoTransitionToDecision(true);
      IAtsDecisionReviewOption option = new DecisionReviewOption("yes");
      option.getUserIds().add("1234");
      option.getUserNames().add("Don");
      option.setFollowupRequired(true);
      revDef.getOptions().add(option);
      IAtsDecisionReviewOption option2 = new DecisionReviewOption("no");
      option2.getUserIds().add("1234");
      option2.getUserNames().add("Don");
      revDef.getOptions().add(option2);

      XResultData resultData = new XResultData(false);
      ConvertWorkDefinitionToAtsDsl convert = new ConvertWorkDefinitionToAtsDsl(resultData);
      DecisionReviewDef dsl = convert.createDslDecisionReviewDef(revDef);
      Assert.assertEquals(0, resultData.getNumErrors());

      Assert.assertEquals("review", dsl.getName());
      Assert.assertEquals(ReviewBlockingType.TRANSITION, dsl.getBlockingType());
      Assert.assertEquals(WorkflowEventType.COMMIT_BRANCH, dsl.getStateEvent());
      Assert.assertEquals(null, dsl.getRelatedToState());
      Assert.assertEquals(1, dsl.getAssigneeRefs().size());
      Assert.assertEquals(BooleanDef.TRUE, dsl.getAutoTransitionToDecision());
      Assert.assertEquals(2, dsl.getOptions().size());
   }

   @Test
   public void test_createDecisionReview2() {
      DecisionReviewDefinition revDef = new DecisionReviewDefinition("review");
      revDef.setBlockingType(ReviewBlockType.Transition);
      revDef.setStateEventType(StateEventType.CommitBranch);
      revDef.setAutoTransitionToDecision(false);

      XResultData resultData = new XResultData(false);
      ConvertWorkDefinitionToAtsDsl convert = new ConvertWorkDefinitionToAtsDsl(resultData);
      DecisionReviewDef dsl = convert.createDslDecisionReviewDef(revDef);
      Assert.assertEquals(0, resultData.getNumErrors());

      Assert.assertEquals(BooleanDef.FALSE, dsl.getAutoTransitionToDecision());
   }

   @Test
   public void test_convertWithPeerReview() {
      PeerReviewDefinition revDef = new PeerReviewDefinition("review");
      revDef.setBlockingType(ReviewBlockType.Transition);
      revDef.setStateEventType(StateEventType.CommitBranch);

      XResultData resultData = new XResultData(false);
      WorkDefinition workDef = new WorkDefinition(15L, "this");
      StateDefinition endorse = new StateDefinition("endorse");
      endorse.setStateType(StateType.Working);
      endorse.getPeerReviews().add(revDef);
      workDef.addState(endorse);
      workDef.setStartState(endorse);

      ConvertWorkDefinitionToAtsDsl convert = new ConvertWorkDefinitionToAtsDsl(resultData);
      convert.convert("this", workDef);
      Assert.assertEquals(0, resultData.getNumErrors());

      // test cache
      convert.convert("this", workDef);
      Assert.assertEquals(0, resultData.getNumErrors());
   }

   @Test
   public void test_createPeerReview() {
      PeerReviewDefinition revDef = new PeerReviewDefinition("review");
      revDef.setBlockingType(ReviewBlockType.Transition);
      revDef.setStateEventType(StateEventType.CommitBranch);
      revDef.setRelatedToState("endorse");
      revDef.getAssignees().add("12345");
      revDef.setLocation("location");
      revDef.setReviewTitle("rev Title");

      XResultData resultData = new XResultData(false);
      ConvertWorkDefinitionToAtsDsl convert = new ConvertWorkDefinitionToAtsDsl(resultData);
      PeerReviewDef dsl = convert.createDslPeerReviewDef(revDef);
      Assert.assertEquals(0, resultData.getNumErrors());

      Assert.assertEquals("review", dsl.getName());
      Assert.assertEquals("location", dsl.getLocation());
      Assert.assertEquals("rev Title", dsl.getTitle());
      Assert.assertEquals(ReviewBlockingType.TRANSITION, dsl.getBlockingType());
      Assert.assertEquals(WorkflowEventType.COMMIT_BRANCH, dsl.getStateEvent());
      Assert.assertEquals(null, dsl.getRelatedToState());
      Assert.assertEquals(1, dsl.getAssigneeRefs().size());
   }

   @Test
   public void test_createPeerReview_nullLocationTitle() {
      PeerReviewDefinition revDef = new PeerReviewDefinition("review");
      revDef.setBlockingType(ReviewBlockType.Transition);
      revDef.setStateEventType(StateEventType.CommitBranch);
      revDef.setRelatedToState("endorse");

      XResultData resultData = new XResultData(false);
      ConvertWorkDefinitionToAtsDsl convert = new ConvertWorkDefinitionToAtsDsl(resultData);
      PeerReviewDef dsl = convert.createDslPeerReviewDef(revDef);
      Assert.assertEquals(0, resultData.getNumErrors());

      Assert.assertEquals(null, dsl.getLocation());
      Assert.assertEquals(null, dsl.getTitle());
   }

   @Test
   public void testProcessStateItem() {
      LayoutDef layout = AtsDslFactoryImpl.init().createLayoutDef();
      IAtsCompositeLayoutItem comp = new CompositeLayoutItem();
      comp.setNumColumns(5);
      comp.setName("name");

      XResultData resultData = new XResultData(false);
      ConvertWorkDefinitionToAtsDsl convert = new ConvertWorkDefinitionToAtsDsl(resultData);
      convert.processStateItem(layout, null, comp);
      Assert.assertEquals(0, resultData.getNumErrors());
      Assert.assertEquals(1, layout.getLayoutItems().size());

      convert.processStateItem(layout, null, new LayoutItem("dummy"));
      Assert.assertEquals(1, resultData.getNumErrors());
   }

   @Test
   public void testProcessCompositeStateItem() {
      LayoutDef layout = AtsDslFactoryImpl.init().createLayoutDef();
      IAtsCompositeLayoutItem comp = new CompositeLayoutItem();
      comp.setNumColumns(5);
      comp.setName("name");

      XResultData resultData = new XResultData(false);
      ConvertWorkDefinitionToAtsDsl convert = new ConvertWorkDefinitionToAtsDsl(resultData);
      Composite composite = AtsDslFactoryImpl.init().createComposite();
      convert.processCompositeStateItem(layout, composite, comp);
      Assert.assertEquals(0, resultData.getNumErrors());
      Assert.assertEquals(1, composite.getLayoutItems().size());
   }

   @Test
   public void testProcessWidgetDefinitionStateItem_attrWidget() {
      LayoutDef layout = AtsDslFactoryImpl.init().createLayoutDef();
      IAtsWidgetDefinition widgetDef = new WidgetDefinition("Change Type");

      XResultData resultData = new XResultData(false);
      ConvertWorkDefinitionToAtsDsl convert = new ConvertWorkDefinitionToAtsDsl(resultData);
      convert.processWidgetDefinition(layout, null, widgetDef);
      Assert.assertEquals(0, resultData.getNumErrors());
      Assert.assertEquals(1, layout.getLayoutItems().size());

      Composite composite = AtsDslFactoryImpl.init().createComposite();
      convert = new ConvertWorkDefinitionToAtsDsl(resultData);
      convert.processWidgetDefinition(layout, composite, widgetDef);
      Assert.assertEquals(0, resultData.getNumErrors());
      Assert.assertEquals(1, composite.getLayoutItems().size());

   }

   @Test
   public void testGetOrCreateWidget_cache() {
      XResultData resultData = new XResultData(false);
      ConvertWorkDefinitionToAtsDsl convert = new ConvertWorkDefinitionToAtsDsl(resultData);
      IAtsWidgetDefinition widgetDef = new WidgetDefinition("Change Type");
      WidgetDef widgetDef2 = convert.getOrCreateWidget(widgetDef);
      WidgetDef widgetDef3 = convert.getOrCreateWidget(widgetDef);
      Assert.assertEquals(widgetDef2, widgetDef3);
   }

   @Test
   public void testGetOrCreateWidget_options() {
      XResultData resultData = new XResultData(false);
      ConvertWorkDefinitionToAtsDsl convert = new ConvertWorkDefinitionToAtsDsl(resultData);

      IAtsWidgetDefinition widgetDef = new WidgetDefinition("Change Type");
      widgetDef.getOptions().getXOptions().add(WidgetOption.ALIGN_CENTER);
      WidgetDef widgetDef4 = convert.getOrCreateWidget(widgetDef);
      Assert.assertTrue(widgetDef4.getOption().contains(WidgetOption.ALIGN_CENTER.name()));
   }

   @Test
   public void testProcessWidgetDefinitionStateItem_nonAttrWidget() {
      LayoutDef layout = AtsDslFactoryImpl.init().createLayoutDef();
      IAtsWidgetDefinition widgetDef = new WidgetDefinition("wid");

      XResultData resultData = new XResultData(false);
      ConvertWorkDefinitionToAtsDsl convert = new ConvertWorkDefinitionToAtsDsl(resultData);
      convert.processWidgetDefinition(layout, null, widgetDef);
      Assert.assertEquals(0, resultData.getNumErrors());
      Assert.assertEquals(1, layout.getLayoutItems().size());

      Composite composite = AtsDslFactoryImpl.init().createComposite();
      convert = new ConvertWorkDefinitionToAtsDsl(resultData);
      convert.processWidgetDefinition(layout, composite, widgetDef);
      Assert.assertEquals(0, resultData.getNumErrors());
      Assert.assertEquals(1, composite.getLayoutItems().size());
   }
}
