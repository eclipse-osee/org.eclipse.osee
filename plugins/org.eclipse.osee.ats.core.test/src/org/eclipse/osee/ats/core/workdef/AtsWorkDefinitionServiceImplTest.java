/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workdef;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.IAtsCompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionProviderService;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test case for {@link AtsWorkDefinitionServiceImpl}
 *
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionServiceImplTest {

   private static final String MyPeerToPeerWorkDefName = "myPeerToPeerWorkDef";
   private static final ArtifactId MyPeerToPeerWorkDefArt = ArtifactId.valueOf(266434L);
   private static final String MyTaskWorkDefId = "WorkDef_Task_Test_Review";
   private static final ArtifactId MyTaskWorkDefArt = ArtifactId.valueOf(34345L);

   // @formatter:off
   @Mock IAtsTeamDefinition topTeamDef;
   @Mock IAtsTeamDefinition projTeamDef;
   @Mock IAtsTeamDefinition featureTeamDef;
   @Mock IAtsWorkItemService workItemService;
   @Mock IAtsWorkDefinitionService workDefinitionService;
   @Mock IAtsWorkDefinitionProviderService workDefinitionProviderService;
   @Mock IAtsActionableItem actionableItem;
   @Mock IAtsPeerToPeerReview peerReview;
   @Mock XResultData resultData;
   @Mock IAtsTeamWorkflow teamWf;
   @Mock IAtsWorkDefinition defaultPeerToPeerWorkDef;
   @Mock IAtsWorkDefinition myPeerToPeerWorkDef;
   @Mock IAtsWorkDefinition myTaskWorkDef;
   @Mock ITeamWorkflowProvidersLazy teamWorkflowProviders;
   @Mock IAttributeResolver attributeResolver;
   @Mock IAtsTask task;
   @Mock AtsApi atsApi;
   @Mock IAtsWorkDefinitionService workDefService;
   @Mock IAtsTeamDefinitionService teamDefinitionService;
   @Mock IAtsActionableItemService actionableItemService;

   // @formatter:on

   @Before
   public void setup() throws Exception {
      MockitoAnnotations.initMocks(this);
      when(atsApi.isWorkDefAsName()).thenReturn(true);
      when(atsApi.getTeamDefinitionService()).thenReturn(teamDefinitionService);
      when(atsApi.getTeamDefinitionService().getParentTeamDef(topTeamDef)).thenReturn(null);
      when(atsApi.getTeamDefinitionService().getParentTeamDef(projTeamDef)).thenReturn(topTeamDef);
      when(atsApi.getTeamDefinitionService().getParentTeamDef(featureTeamDef)).thenReturn(projTeamDef);
      when(actionableItem.getAtsApi()).thenReturn(atsApi);
      when(atsApi.getActionableItemService()).thenReturn(actionableItemService);
      // always return default when requested
      when(workDefinitionService.getWorkDefinitionByName(
         eq(AtsWorkDefinitionTokens.WorkDef_Review_PeerToPeer.getName()))).thenReturn(defaultPeerToPeerWorkDef);

      workDefService = new AtsWorkDefinitionServiceImpl(atsApi, teamWorkflowProviders);
      when(atsApi.getAttributeResolver()).thenReturn(attributeResolver);
      when(atsApi.getWorkDefinitionProviderService()).thenReturn(workDefinitionProviderService);

      // always return myPeerToPeerWorkDef when requested
      workDefService = Mockito.spy(workDefService);
      Mockito.doReturn(defaultPeerToPeerWorkDef).when(workDefService).getWorkDefinitionByName(
         eq(AtsWorkDefinitionTokens.WorkDef_Review_PeerToPeer.getName()));
      Mockito.doReturn(defaultPeerToPeerWorkDef).when(workDefService).getWorkDefinition(
         eq(AtsWorkDefinitionTokens.WorkDef_Review_PeerToPeer.getId()));
   }

   @Test
   public void testGetDefaultPeerToPeerWorkflowDefinitionMatch() {
      assertEquals(defaultPeerToPeerWorkDef, workDefService.getDefaultPeerToPeerWorkflowDefinition());
   }

   /**
    * When peerReview has no WorkDefinition attribute set, then default peer review WorkDefinition is returned
    */
   @Test
   public void testGetWorkDefinitionForPeerToPeerReview_deafault() throws Exception {
      when(attributeResolver.getAttributeValues(peerReview, AtsAttributeTypes.WorkflowDefinition)).thenReturn(
         Collections.emptyList());
      when(
         attributeResolver.getAttributeValues(peerReview, AtsAttributeTypes.RelatedPeerWorkflowDefinition)).thenReturn(
            Collections.emptyList());

      IAtsWorkDefinition workDef = workDefService.getWorkDefinitionForPeerToPeerReview(peerReview);
      assertEquals(defaultPeerToPeerWorkDef, workDef);
   }

   /**
    * Test that peer review WorkDefinition id comes from teamDefinition hierarchy
    */
   @Test
   public void testGetWorkDefinitionForPeerToPeerReviewNotYetCreated() throws Exception {
      when(teamWf.getTeamDefinition()).thenReturn(topTeamDef);
      when(attributeResolver.getSoleArtifactIdReference(topTeamDef,
         AtsAttributeTypes.RelatedPeerWorkflowDefinitionReference, ArtifactId.SENTINEL)).thenReturn(
            ArtifactId.SENTINEL);

      IAtsWorkDefinition workDef = workDefService.getWorkDefinitionForPeerToPeerReviewNotYetCreated(teamWf);
      assertEquals(defaultPeerToPeerWorkDef, workDef);
   }

   /**
    * When peerReview WorkDefinition attribute is set, then that WorkDefinition is returned instead of default
    */
   @Test
   public void testGetWorkDefinitionForPeerToPeerReviewIAtsTeamWorkflowIAtsPeerToPeerReview__fromReview() throws Exception {
      List<Object> attrValues = new ArrayList<>();
      attrValues.add(MyPeerToPeerWorkDefName);
      when(attributeResolver.getAttributeValues(peerReview, AtsAttributeTypes.WorkflowDefinition)).thenReturn(
         attrValues);
      List<Object> attrValues2 = new ArrayList<>();
      attrValues2.add(MyPeerToPeerWorkDefArt);
      when(
         attributeResolver.getAttributeValues(peerReview, AtsAttributeTypes.RelatedPeerWorkflowDefinition)).thenReturn(
            attrValues2);

      Mockito.doReturn(myPeerToPeerWorkDef).when(workDefService).getWorkDefinitionByName(MyPeerToPeerWorkDefName);

      IAtsWorkDefinition workDef = workDefService.getWorkDefinitionForPeerToPeerReview(peerReview);
      assertEquals(myPeerToPeerWorkDef, workDef);
   }

   /**
    * Test that stand alone peer WorkDefinition comes from actionableItem's Team Definition hierarchy
    */
   @Test
   public void testGetWorkDefinitionForPeerToPeerReviewNotYetCreatedAndStandalone() throws Exception {
      when(teamWf.getTeamDefinition()).thenReturn(featureTeamDef);
      when(attributeResolver.getSoleAttributeValueAsString(topTeamDef,
         AtsAttributeTypes.RelatedPeerWorkflowDefinitionReference, "")).thenReturn(
            MyPeerToPeerWorkDefArt.getIdString());

      when(actionableItemService.getTeamDefinitionInherited(actionableItem)).thenReturn(topTeamDef);
      when(attributeResolver.getSoleAttributeValueAsString(topTeamDef,
         AtsAttributeTypes.RelatedPeerWorkflowDefinitionReference, "")).thenReturn(
            MyPeerToPeerWorkDefArt.getIdString());

      when(workDefinitionService.getWorkDefinition(MyPeerToPeerWorkDefArt.getId())).thenReturn(myPeerToPeerWorkDef);
      when(workDefinitionProviderService.getWorkDefinition(MyPeerToPeerWorkDefArt.getId())).thenReturn(
         myPeerToPeerWorkDef);

      IAtsWorkDefinition workDef =
         workDefService.getWorkDefinitionForPeerToPeerReviewNotYetCreatedAndStandalone(actionableItem);

      assertEquals(myPeerToPeerWorkDef, workDef);

   }

   /**
    * When no team definition in team definition hierarchy has a Peer WorkDefinition defined, return No-Match<br>
    * When top team definition has a Peer WorkDefinition defined, return it's value.
    */
   @Test
   public void testGetPeerToPeerWorkDefinitionFromTeamDefinitionAttributeValueRecurse() throws Exception {
      // Setup all teamDefinitions to not have values defined
      when(attributeResolver.getSoleArtifactIdReference(topTeamDef,
         AtsAttributeTypes.RelatedPeerWorkflowDefinitionReference, ArtifactId.SENTINEL)).thenReturn(
            ArtifactId.SENTINEL);
      when(attributeResolver.getSoleArtifactIdReference(projTeamDef,
         AtsAttributeTypes.RelatedPeerWorkflowDefinitionReference, ArtifactId.SENTINEL)).thenReturn(
            ArtifactId.SENTINEL);

      when(attributeResolver.getSoleArtifactIdReference(featureTeamDef,
         AtsAttributeTypes.RelatedPeerWorkflowDefinitionReference, ArtifactId.SENTINEL)).thenReturn(
            ArtifactId.SENTINEL);
      when(attributeResolver.getSoleArtifactIdReference(featureTeamDef,
         AtsAttributeTypes.RelatedPeerWorkflowDefinitionReference, ArtifactId.SENTINEL)).thenReturn(
            ArtifactId.SENTINEL);

      // Test that no-match is returned
      Mockito.doReturn(myPeerToPeerWorkDef).when(workDefService).getWorkDefinitionByName(eq(MyPeerToPeerWorkDefName));

      // Setup that top team definition has WorkDefinition defined
      when(attributeResolver.getSoleAttributeValue(topTeamDef, AtsAttributeTypes.RelatedPeerWorkflowDefinition,
         "")).thenReturn(MyPeerToPeerWorkDefName);

      when(attributeResolver.getSoleArtifactIdReference(peerReview, AtsAttributeTypes.RelatedPeerWorkflowDefinition,
         ArtifactId.SENTINEL)).thenReturn(MyPeerToPeerWorkDefArt);

      when(attributeResolver.getSoleAttributeValue(topTeamDef, AtsAttributeTypes.WorkflowDefinition, "")).thenReturn(
         MyPeerToPeerWorkDefName);
      when(attributeResolver.getSoleArtifactIdReference(topTeamDef, AtsAttributeTypes.WorkflowDefinitionReference,
         ArtifactId.SENTINEL)).thenReturn(MyPeerToPeerWorkDefArt);
      Mockito.doReturn(myPeerToPeerWorkDef).when(workDefService).getWorkDefinitionByName(MyPeerToPeerWorkDefName);

   }

   @Test
   public void testGetWorkDefinitionIAtsWorkItem() throws Exception {
      when(peerReview.getParentTeamWorkflow()).thenReturn(teamWf);
      when(teamWf.getTeamDefinition()).thenReturn(topTeamDef);
      when(
         attributeResolver.getAttributeValues(topTeamDef, AtsAttributeTypes.RelatedPeerWorkflowDefinition)).thenReturn(
            Collections.emptyList());
      when(
         attributeResolver.getAttributeValues(peerReview, AtsAttributeTypes.RelatedPeerWorkflowDefinition)).thenReturn(
            Collections.emptyList());

      Mockito.doReturn(defaultPeerToPeerWorkDef).when(workDefService).getWorkDefinition(peerReview);

      IAtsWorkDefinition workDef = workDefService.getWorkDefinition(peerReview);
      assertEquals(defaultPeerToPeerWorkDef, workDef);
   }

   @Test
   public void testGetWorkDefinitionForTaskWithSpecifiedId() throws Exception {
      List<Object> attrValues = new ArrayList<>();
      attrValues.add(MyTaskWorkDefId);
      when(attributeResolver.getAttributeValues(task, AtsAttributeTypes.WorkflowDefinition)).thenReturn(attrValues);

      List<Object> attrValues2 = new ArrayList<>();
      attrValues2.add(MyTaskWorkDefArt);
      when(attributeResolver.getAttributeValues(task, AtsAttributeTypes.WorkflowDefinitionReference)).thenReturn(
         attrValues2);

      when(task.getParentTeamWorkflow()).thenReturn(teamWf);

      Mockito.doReturn(myTaskWorkDef).when(workDefService).getWorkDefinitionByName(eq(MyTaskWorkDefId));
      Mockito.doReturn(myTaskWorkDef).when(workDefService).getWorkDefinition(task);

      IAtsWorkDefinition workDef = workDefService.getWorkDefinition(task);
      assertEquals(workDef, myTaskWorkDef);
   }

   @Test
   public void testHasWidgetNamed() {
      StateDefinition def = new StateDefinition("endorse");
      Assert.assertFalse(new AtsWorkDefinitionServiceImpl(atsApi, null).hasWidgetNamed(def, "item 2"));

      IAtsCompositeLayoutItem stateItem2 = new CompositeLayoutItem(2);
      def.getLayoutItems().add(stateItem2);
      IAtsWidgetDefinition widget2 = new WidgetDefinition("item 2");
      stateItem2.getaLayoutItems().add(widget2);
      IAtsWidgetDefinition widget3 = new WidgetDefinition("item 3");
      stateItem2.getaLayoutItems().add(widget3);

      Assert.assertFalse(new AtsWorkDefinitionServiceImpl(atsApi, null).hasWidgetNamed(def, "item 45"));
      Assert.assertTrue(new AtsWorkDefinitionServiceImpl(atsApi, null).hasWidgetNamed(def, "item 2"));
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

      WorkDefinition def = new WorkDefinition(15L, "this");
      def.addState(completed);
      def.addState(analyze);
      def.addState(endorse);
      def.addState(implement);
      endorse.setOrdinal(1);
      analyze.setOrdinal(2);
      implement.setOrdinal(3);
      Assert.assertEquals(4, def.getStates().size());
      List<IAtsStateDefinition> states = new AtsWorkDefinitionServiceImpl(atsApi, null).getStatesOrderedByOrdinal(def);
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

      IAtsWorkDefinition def = new WorkDefinition(15L, "this");
      List<IAtsStateDefinition> states = new LinkedList<>();
      states.addAll(Arrays.asList(endorse, analyze));
      new AtsWorkDefinitionServiceImpl(atsApi, null).getStatesOrderedByDefaultToState(def, endorse, states);
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

      IAtsWorkDefinition def = new WorkDefinition(15L, "this");
      endorse.setDefaultToState(endorse);
      List<IAtsStateDefinition> states = new LinkedList<>();
      states.addAll(Arrays.asList(analyze, completed));
      new AtsWorkDefinitionServiceImpl(atsApi, null).getStatesOrderedByDefaultToState(def, endorse, states);
      Assert.assertEquals(3, states.size());
   }

   @Test
   public void testGetStateNames() {
      WorkDefinition def = new WorkDefinition(15L, "this");
      def.addState(new StateDefinition("endorse"));
      def.addState(new StateDefinition("analyze"));
      Assert.assertEquals(2, new AtsWorkDefinitionServiceImpl(atsApi, null).getStateNames(def).size());
      Assert.assertTrue(new AtsWorkDefinitionServiceImpl(atsApi, null).getStateNames(def).contains("endorse"));
      Assert.assertTrue(new AtsWorkDefinitionServiceImpl(atsApi, null).getStateNames(def).contains("analyze"));
   }

   @Test
   public void testGetStates() {
      StateDefinition endorse = new StateDefinition("endorse");
      WorkDefinition def = new WorkDefinition(15L, "this");
      def.addState(endorse);
      Assert.assertEquals(1, def.getStates().size());
      Assert.assertEquals(endorse, def.getStates().iterator().next());
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
   public void testGetStateByName() {
      WorkDefinition def = new WorkDefinition(15L, "this");
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
      WorkDefinition def = new WorkDefinition(15L, "this");
      Assert.assertNull(def.getStartState());
      StateDefinition endorse = new StateDefinition("endorse");
      def.addState(endorse);
      def.setStartState(endorse);
      Assert.assertEquals(endorse, def.getStartState());
   }

   @Test
   public void testGetIds() {
      WorkDefinition def = new WorkDefinition(15L, "this");
      Assert.assertEquals((Long) 15L, def.getId());
   }

   @Test
   public void testIsStateWeightingEnabled() {
      WorkDefinition def = new WorkDefinition(15L, "this");
      Assert.assertFalse(new AtsWorkDefinitionServiceImpl(atsApi, null).isStateWeightingEnabled(def));
      StateDefinition endorse = new StateDefinition("endorse");
      def.addState(endorse);
      endorse.setStateWeight(34);
      Assert.assertTrue(new AtsWorkDefinitionServiceImpl(atsApi, null).isStateWeightingEnabled(def));

      endorse.setStateWeight(0);
      Assert.assertFalse(new AtsWorkDefinitionServiceImpl(atsApi, null).isStateWeightingEnabled(def));
   }

   @Test
   public void testEqualsObject() {
      IAtsWorkDefinition obj = new WorkDefinition(15L, "hello");
      Assert.assertTrue(obj.equals(obj));

      IAtsWorkDefinition obj2 = new WorkDefinition(15L, "hello");

      Assert.assertTrue(obj.equals(obj2));
      Assert.assertFalse(obj.equals(null));
      Assert.assertFalse(obj.equals("str"));

      WorkDefinition obj3 = new WorkDefinition(16L, "hello");
      Assert.assertFalse(obj.equals(obj3));
      Assert.assertFalse(obj3.equals(obj));

   }

   @Test
   public void testHashCode() {
      WorkDefinition obj = new WorkDefinition(15L, "hello");
      Assert.assertEquals(15L, obj.hashCode());
   }

}
