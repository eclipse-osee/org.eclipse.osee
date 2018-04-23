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
package org.eclipse.osee.ats.client.integration.tests.ats.workflow.transition;

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.task.IAtsTaskService;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResult;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.client.integration.tests.ats.workflow.AtsTestUtil.AtsTestUtilState;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.workflow.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.workflow.review.DecisionReviewManager;
import org.eclipse.osee.ats.workflow.review.DecisionReviewState;
import org.eclipse.osee.ats.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test unit for {@link TransitionManager}
 *
 * @author Donald G. Dunne
 */
public class TransitionManagerTest {

   private static List<AbstractWorkflowArtifact> EMPTY_AWAS = new ArrayList<>();

   public static ArtifactToken WorkDefTargetedVersionId = ArtifactToken.valueOf(340096461,
      "WorkDef_Team_TransitionManagerTest_TargetedVersion", AtsArtifactTypes.WorkDefinition);

   public static ArtifactToken WorkDefWidgetRequiredTransitionId = ArtifactToken.valueOf(16919464,
      "WorkDef_Team_TransitionManagerTest_WidgetRequiredTransition", AtsArtifactTypes.WorkDefinition);

   public static ArtifactToken WorkDefWidgetRequiredCompletionId = ArtifactToken.valueOf(7661159,
      "WorkDef_Team_TransitionManagerTest_WidgetRequiredCompletion", AtsArtifactTypes.WorkDefinition);

   public static ArtifactToken WorkDefTeamAtsTestUtil =
      ArtifactToken.valueOf(946555776, "WorkDef_Team_AtsTestUtil", AtsArtifactTypes.WorkDefinition);

   // @formatter:off
   @Mock private IAtsTask task;
   @Mock private IAtsStateDefinition toStateDef;
   @Mock private IAtsWorkItemService workItemService;
   @Mock private IAtsTaskService taskService;
   @Mock private IAtsStateManager teamWfStateMgr, taskStateMgr;
   // @formatter:on

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);
   }

   @BeforeClass
   @AfterClass
   public static void cleanup() {
      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testHandleTransitionValidation__NoAwas() {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-A");
      TransitionHelper helper =
         new TransitionHelper(getClass().getSimpleName(), EMPTY_AWAS, AtsTestUtil.getImplementStateDef().getName(),
            Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null,
            AtsClientService.get().createChangeSet(getClass().getSimpleName()), AtsClientService.get().getServices(),
            TransitionOption.None);
      IAtsTransitionManager transMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = new TransitionResults();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(TransitionResult.NO_WORKFLOWS_PROVIDED_FOR_TRANSITION));
   }

   @org.junit.Test
   public void testHandleTransitionValidation__ToStateNotNull() {
      TransitionHelper helper = new TransitionHelper(getClass().getSimpleName(), Arrays.asList(AtsTestUtil.getTeamWf()),
         null, Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null,
         AtsClientService.get().createChangeSet(getClass().getSimpleName()), AtsClientService.get().getServices(),
         TransitionOption.None);
      IAtsTransitionManager transMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = new TransitionResults();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(TransitionResult.TO_STATE_CANT_BE_NULL));
   }

   @org.junit.Test
   public void testHandleTransitionValidation__InvalidToState() {
      TransitionHelper helper = new TransitionHelper(getClass().getSimpleName(), Arrays.asList(AtsTestUtil.getTeamWf()),
         "InvalidStateName", Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null,
         AtsClientService.get().createChangeSet(getClass().getSimpleName()), AtsClientService.get().getServices(),
         TransitionOption.None);
      IAtsTransitionManager transMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = new TransitionResults();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(
         "Transition-To State [InvalidStateName] does not exist for Work Definition [" + AtsTestUtil.getTeamWf().getWorkDefinition().getName() + "]"));
   }

   @org.junit.Test
   public void testHandleTransitionValidation__MustBeAssigned() {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-B");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      IAtsTeamDefinition teamDef = teamArt.getTeamDefinition();
      Assert.assertNotNull(teamDef);

      TransitionHelper helper = new TransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
         AtsTestUtil.getImplementStateDef().getName(),
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null,
         AtsClientService.get().createChangeSet(getClass().getSimpleName()), AtsClientService.get().getServices(),
         TransitionOption.None);
      helper.setExecuteChanges(true);
      IAtsTransitionManager transMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // First transition should be valid cause Joe Smith is assigned cause he created
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.toString(), results.isEmpty());

      // Un-Assign Joe Smith
      results.clear();
      Assert.assertFalse(helper.isPrivilegedEditEnabled());
      Assert.assertFalse(helper.isOverrideAssigneeCheck());
      teamArt.getStateMgr().setAssignee(
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Alex_Kay));
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(AtsTestUtil.getTeamWf(), TransitionResult.MUST_BE_ASSIGNED));

      // Set PrivilegedEditEnabled edit enabled; no errors
      results.clear();
      Assert.assertFalse(helper.isOverrideAssigneeCheck());
      helper.addTransitionOption(TransitionOption.PrivilegedEditEnabled);
      Assert.assertTrue(helper.isPrivilegedEditEnabled());
      teamArt.getStateMgr().setAssignee(
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Alex_Kay));
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // Set OverrideAssigneeCheck
      results.clear();
      helper.removeTransitionOption(TransitionOption.PrivilegedEditEnabled);
      helper.addTransitionOption(TransitionOption.OverrideAssigneeCheck);
      Assert.assertFalse(helper.isPrivilegedEditEnabled());
      Assert.assertTrue(helper.isOverrideAssigneeCheck());
      teamArt.getStateMgr().setAssignee(
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Alex_Kay));
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // Set UnAssigned, should be able to transition cause will be assigned as convenience
      results.clear();
      helper.removeTransitionOption(TransitionOption.OverrideAssigneeCheck);
      Assert.assertFalse(helper.isPrivilegedEditEnabled());
      Assert.assertFalse(helper.isOverrideAssigneeCheck());
      teamArt.getStateMgr().setAssignee(
         AtsClientService.get().getUserServiceClient().getUserFromToken(SystemUser.UnAssigned));
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(getResultsAndDebug(results, teamArt, helper), results.isEmpty());

      // cleanup test
      teamArt.getStateMgr().setAssignee(
         AtsClientService.get().getUserServiceClient().getUserFromToken(SystemUser.UnAssigned));
   }

   private String getResultsAndDebug(TransitionResults results, TeamWorkFlowArtifact teamArt, TransitionHelper helper) {
      StringBuilder sb = new StringBuilder(results.toString());
      sb.append("\n\n");
      sb.append("assignees: ");
      sb.append(teamArt.getStateMgr().getAssigneesStr());
      sb.append("\n\n");
      sb.append("transitionOptions: ");
      sb.append(helper.getTransitionOptions());
      return sb.toString();
   }

   @org.junit.Test
   public void testHandleTransitionValidation__WorkingBranchTransitionable() {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-C");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      MockTransitionHelper helper = new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
         AtsTestUtil.getImplementStateDef().getName(),
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null,
         AtsClientService.get().createChangeSet(getClass().getSimpleName()), TransitionOption.None);
      IAtsTransitionManager transMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // this should pass
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue("Test wasn't reset to allow transition", results.isEmpty());

      // attempt to transition to Implement with working branch
      helper.setWorkingBranchInWork(true);
      helper.setBranchInCommit(false);
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(AtsTestUtil.getTeamWf(), TransitionResult.WORKING_BRANCH_EXISTS));

      // attempt to cancel workflow with working branch
      results.clear();
      helper.setWorkingBranchInWork(true);
      helper.setBranchInCommit(false);
      helper.setToStateName(TeamState.Cancelled.getName());
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(
         results.contains(AtsTestUtil.getTeamWf(), TransitionResult.DELETE_WORKING_BRANCH_BEFORE_CANCEL));

      // attempt to transition workflow with branch being committed
      results.clear();
      helper.setWorkingBranchInWork(true);
      helper.setBranchInCommit(true);
      helper.setToStateName(TeamState.Implement.getName());
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(AtsTestUtil.getTeamWf(), TransitionResult.WORKING_BRANCH_BEING_COMMITTED));
   }

   @org.junit.Test
   public void testHandleTransitionValidation__NoSystemUser() {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-D");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      MockTransitionHelper helper = new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
         AtsTestUtil.getImplementStateDef().getName(),
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null,
         AtsClientService.get().createChangeSet(getClass().getSimpleName()), TransitionOption.None);
      IAtsTransitionManager transMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // First transition should be valid cause Joe Smith is assigned cause he created
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      helper.setSystemUser(true);
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(TransitionResult.CAN_NOT_TRANSITION_AS_SYSTEM_USER));

      results.clear();
      helper.setSystemUser(false);
      helper.setSystemUserAssigned(true);
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(teamArt, TransitionResult.CAN_NOT_TRANSITION_WITH_SYSTEM_USER_ASSIGNED));
   }

   @org.junit.Test
   public void testIsStateTransitionable__ValidateXWidgets__RequiredForTransition() {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-1");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      MockTransitionHelper helper = new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
         AtsTestUtil.getImplementStateDef().getName(),
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null,
         AtsClientService.get().createChangeSet(getClass().getSimpleName()), TransitionOption.None);
      IAtsStateDefinition fromStateDef = AtsTestUtil.getAnalyzeStateDef();
      fromStateDef.getLayoutItems().clear();
      IAtsTransitionManager transMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // test that normal transition works
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue("Test wasn't reset to allow transition", results.isEmpty());

      // test that two widgets validate
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // test that estHours required fails validation
      results.clear();

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      AtsClientService.get().getWorkDefinitionService().setWorkDefinitionAttrs(teamArt,
         WorkDefWidgetRequiredTransitionId, changes);
      changes.execute();

      // clear the team workflow to work definition cache, since we just changed the configured work definition for this workflow
      AtsClientService.get().getWorkDefinitionService().clearCaches();

      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.toString(), results.contains("[Estimated Hours] is required for transition"));
      Assert.assertTrue(results.toString(), results.contains("[Work Package] is required for transition"));
   }

   @org.junit.Test
   public void testIsStateTransitionable__ValidateXWidgets__RequiredForCompletion() {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-2");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();

      MockTransitionHelper helper = new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
         AtsTestUtil.getImplementStateDef().getName(),
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null,
         AtsClientService.get().createChangeSet(getClass().getSimpleName()), TransitionOption.None);
      IAtsStateDefinition fromStateDef = AtsTestUtil.getAnalyzeStateDef();
      fromStateDef.getLayoutItems().clear();
      IAtsTransitionManager transMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // test that normal transition works
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue("Test wasn't reset to allow transition", results.isEmpty());

      // test that two widgets validate
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // test that neither are required for transition to implement
      results.clear();
      helper.setToStateName(AtsTestUtil.getImplementStateDef().getName());
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // test that Work Package only widget required for normal transition
      results.clear();
      helper.setToStateName(AtsTestUtil.getCompletedStateDef().getName());

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      AtsClientService.get().getWorkDefinitionService().setWorkDefinitionAttrs(teamArt,
         WorkDefWidgetRequiredCompletionId, changes);
      changes.execute();

      // clear the team workflow to work definition cache, since we just changed the configured work definition for this workflow
      AtsClientService.get().getWorkDefinitionService().clearCaches();

      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.toString(),
         results.contains("[Estimated Hours] is required for transition to [Completed]"));
      Assert.assertTrue(results.toString(), results.contains("[Work Package] is required for transition"));

      // test that neither are required for transition to canceled
      results.clear();
      helper.setToStateName(AtsTestUtil.getCancelledStateDef().getName());
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // test that neither are required for transition to completed if overrideValidation is set
      results.clear();
      helper.setToStateName(AtsTestUtil.getCompletedStateDef().getName());
      helper.setOverrideTransitionValidityCheck(true);
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());
   }

   @org.junit.Test
   public void testIsStateTransitionable__ValidateTasks() {
      IAtsTeamWorkflow teamWf = org.mockito.Mockito.mock(IAtsTeamWorkflow.class);

      TransitionResults results = new TransitionResults();
      when(teamWf.isTeamWorkflow()).thenReturn(true);

      // validate that if rule exists and is working, then transition with tasks is ok
      when(teamWf.getStateDefinition()).thenReturn(toStateDef);
      when(toStateDef.getStateType()).thenReturn(StateType.Working);
      when(toStateDef.hasRule(RuleDefinitionOption.AllowTransitionWithoutTaskCompletion.name())).thenReturn(true);
      TransitionManager.validateTaskCompletion(results, teamWf, toStateDef, taskService);
      Assert.assertTrue(results.isEmpty());

      // test only check if Team Workflow
      when(toStateDef.hasRule(RuleDefinitionOption.AllowTransitionWithoutTaskCompletion.name())).thenReturn(false);
      TransitionManager.validateTaskCompletion(results, task, toStateDef, taskService);
      Assert.assertTrue(results.isEmpty());

      // test not check if working state
      when(teamWf.getStateMgr()).thenReturn(teamWfStateMgr);
      when(teamWfStateMgr.getStateType()).thenReturn(StateType.Working);
      TransitionManager.validateTaskCompletion(results, teamWf, toStateDef, taskService);
      Assert.assertTrue(results.isEmpty());

      // test transition to completed; all tasks are completed
      when(toStateDef.getStateType()).thenReturn(StateType.Completed);
      when(taskService.getTask(teamWf)).thenReturn(Collections.singleton(task));
      when(task.getStateMgr()).thenReturn(taskStateMgr);
      when(taskStateMgr.getStateType()).thenReturn(StateType.Completed);
      TransitionManager.validateTaskCompletion(results, teamWf, toStateDef, taskService);
      Assert.assertTrue(results.isEmpty());

      // test transtion to completed; task is not completed
      when(taskStateMgr.getStateType()).thenReturn(StateType.Working);
      TransitionManager.validateTaskCompletion(results, teamWf, toStateDef, taskService);
      Assert.assertTrue(results.contains(TransitionResult.TASKS_NOT_COMPLETED.getDetails()));

   }

   @org.junit.Test
   public void testIsStateTransitionable__RequireTargetedVersion__FromTeamDef() {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-4");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());

      MockTransitionHelper helper = new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
         AtsTestUtil.getImplementStateDef().getName(),
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null, changes, TransitionOption.None);

      IAtsTransitionManager transMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // validate that can transition
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // validate that can't transition without targeted version when team def rule is set
      ((Artifact) AtsClientService.get().getQueryService().getArtifact(
         teamArt.getTeamDefinition())).addAttributeFromString(AtsAttributeTypes.RuleDefinition,
            RuleDefinitionOption.RequireTargetedVersion.name());
      ((Artifact) AtsClientService.get().getQueryService().getArtifact(teamArt.getTeamDefinition())).persist(
         getClass().getSimpleName());
      results.clear();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(teamArt, TransitionResult.MUST_BE_TARGETED_FOR_VERSION));

      // set targeted version; transition validation should succeed
      results.clear();
      AtsClientService.get().getVersionService().setTargetedVersion(teamArt, AtsTestUtil.getVerArt1(), changes);
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());
   }

   @org.junit.Test
   public void testIsStateTransitionable__RequireTargetedVersion__FromPageDef() {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-5");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      MockTransitionHelper helper = new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
         AtsTestUtil.getImplementStateDef().getName(),
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null, changes, TransitionOption.None);

      IAtsTransitionManager transMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // validate that can transition
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.toString(), results.isEmpty());
      changes.execute();

      changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      AtsClientService.get().getWorkDefinitionService().setWorkDefinitionAttrs(teamArt, WorkDefTargetedVersionId,
         changes);
      changes.execute();

      // clear the team workflow to work definition cache, since we just changed the configured work definition for this workflow
      AtsClientService.get().getWorkDefinitionService().clearCaches();

      results.clear();

      // validate that can't transition without targeted version when team def rule is set
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(teamArt, TransitionResult.MUST_BE_TARGETED_FOR_VERSION));

      // set targeted version; transition validation should succeed
      results.clear();
      AtsClientService.get().getVersionService().setTargetedVersion(teamArt, AtsTestUtil.getVerArt1(), changes);
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());
   }

   @org.junit.Test
   public void testIsStateTransitionable__ReviewsCompleted() {

      AtsTestUtil.cleanupAndReset("TransitionManagerTest-6");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      MockTransitionHelper helper = new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
         AtsTestUtil.getImplementStateDef().getName(),
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null,
         AtsClientService.get().createChangeSet(getClass().getSimpleName()), TransitionOption.None);
      IAtsTransitionManager transMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // validate that can transition
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      DecisionReviewArtifact decArt =
         AtsTestUtil.getOrCreateDecisionReview(ReviewBlockType.None, AtsTestUtilState.Analyze, changes);
      teamArt.addRelation(AtsRelationTypes.TeamWorkflowToReview_Review, decArt);
      changes.execute();

      // validate that can transition cause non-blocking review
      results.clear();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // validate that can transition cause no transition blocking review
      results.clear();
      decArt.setSoleAttributeValue(AtsAttributeTypes.ReviewBlocks, ReviewBlockType.Commit.name());
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // validate that can  NOT transition cause blocking review
      results.clear();
      decArt.setSoleAttributeValue(AtsAttributeTypes.ReviewBlocks, ReviewBlockType.Transition.name());
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(teamArt, TransitionResult.COMPLETE_BLOCKING_REVIEWS));

      decArt.setSoleAttributeValue(AtsAttributeTypes.Decision, "yes");
      decArt.persist(getClass().getSimpleName());

      // validate that can transition cause review completed
      changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      Result result = DecisionReviewManager.transitionTo(decArt, DecisionReviewState.Completed,
         AtsClientService.get().getUserService().getCurrentUser(), false, changes);
      Assert.assertTrue(result.getText(), result.isTrue());
      changes.execute();
      results.clear();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());
   }

   /**
    * Test that artifacts can be transitioned to the same state without error. (i.e.: An action in Implement can be
    * transition to Implement a second time and the TransitionManager will just ignore this second, redundant
    * transition)
    */
   @org.junit.Test
   public void testIsStateTransitionable__ToSameState() {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-8");
      TeamWorkFlowArtifact teamArt01 = AtsTestUtil.getTeamWf();
      TeamWorkFlowArtifact teamArt02 = AtsTestUtil.getTeamWf2();

      //1. Initially transition workflows to Implement
      MockTransitionHelper helper = new MockTransitionHelper(getClass().getSimpleName(),
         Arrays.asList(teamArt01, teamArt02), AtsTestUtil.getImplementStateDef().getName(),
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null,
         AtsClientService.get().createChangeSet(getClass().getSimpleName()), TransitionOption.None);
      IAtsTransitionManager transMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = new TransitionResults();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      //2. redundant transition workflows to Implement
      MockTransitionHelper helper01 = new MockTransitionHelper(getClass().getSimpleName(),
         Arrays.asList(teamArt01, teamArt02), AtsTestUtil.getImplementStateDef().getName(),
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null,
         AtsClientService.get().createChangeSet(getClass().getSimpleName()), TransitionOption.None);
      IAtsTransitionManager transMgr01 = TransitionFactory.getTransitionManager(helper01);
      TransitionResults results01 = new TransitionResults();
      transMgr01.handleTransitionValidation(results01);
      Assert.assertTrue(results01.isEmpty());

      //3. Transition one TeamWf to Complete
      MockTransitionHelper helper02 = new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt01),
         AtsTestUtil.getCompletedStateDef().getName(),
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null,
         AtsClientService.get().createChangeSet(getClass().getSimpleName()), TransitionOption.None);
      IAtsTransitionManager transMgr02 = TransitionFactory.getTransitionManager(helper02);
      TransitionResults results02 = new TransitionResults();
      transMgr02.handleTransitionValidation(results02);
      Assert.assertTrue(results02.isEmpty());

      //4. redundant transition workflows to Implement
      MockTransitionHelper helper03 = new MockTransitionHelper(getClass().getSimpleName(),
         Arrays.asList(teamArt01, teamArt02), AtsTestUtil.getCompletedStateDef().getName(),
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null,
         AtsClientService.get().createChangeSet(getClass().getSimpleName()), TransitionOption.None);
      IAtsTransitionManager transMgr03 = TransitionFactory.getTransitionManager(helper03);
      TransitionResults results03 = new TransitionResults();
      transMgr03.handleTransitionValidation(results03);
      Assert.assertTrue(results03.isEmpty());
   }

   @org.junit.Test
   public void testHandleTransitionValidation__AssigneesUpdate() {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-E");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      List<IAtsUser> assigneesBefore = teamArt.getAssignees();
      Assert.assertTrue(assigneesBefore.size() > 0);
      MockTransitionHelper helper = new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
         AtsTestUtil.getImplementStateDef().getName(), teamArt.getAssignees(), null,
         AtsClientService.get().createChangeSet(getClass().getSimpleName()), TransitionOption.None);
      IAtsTransitionManager transMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = new TransitionResults();
      TransitionResults results01 = new TransitionResults();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());
      results01 = transMgr.handleAll();
      Assert.assertTrue(results01.isEmpty());
      List<IAtsUser> assigneesAfter = teamArt.getAssignees();
      Assert.assertTrue(assigneesAfter.containsAll(assigneesBefore));
      Assert.assertTrue(assigneesBefore.containsAll(assigneesAfter));
   }

   @org.junit.Test
   public void testHandleTransitionValidation__AssigneesNull() {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-F");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      List<IAtsUser> assigneesBefore = teamArt.getAssignees();
      Assert.assertTrue(assigneesBefore.size() > 0);
      MockTransitionHelper helper = new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
         AtsTestUtil.getImplementStateDef().getName(), null, null,
         AtsClientService.get().createChangeSet(getClass().getSimpleName()), TransitionOption.None);
      helper.setExecuteChanges(true);
      IAtsTransitionManager transMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = new TransitionResults();
      TransitionResults results01 = new TransitionResults();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());
      results01 = transMgr.handleAll();
      Assert.assertTrue(results01.isEmpty());
      List<IAtsUser> assigneesAfter = teamArt.getAssignees();
      Assert.assertTrue(assigneesAfter.containsAll(assigneesBefore));
      Assert.assertTrue(assigneesBefore.containsAll(assigneesAfter));
   }

   @org.junit.Test
   public void testHandleTransition__PercentComplete() {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-G");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();

      // Setup - Transition to Implement
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("create");
      Result result = AtsTestUtil.transitionTo(AtsTestUtilState.Implement,
         AtsClientService.get().getUserService().getCurrentUser(), changes, TransitionOption.OverrideAssigneeCheck);
      changes.execute();
      Assert.assertTrue("Transition Error: " + result.getText(), result.isTrue());
      Assert.assertEquals("Implement", teamArt.getCurrentStateName());
      Assert.assertEquals(0, teamArt.getSoleAttributeValue(AtsAttributeTypes.PercentComplete, 0).intValue());

      // Transition to completed should set percent to 100
      changes.clear();
      MockTransitionHelper helper = new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
         AtsTestUtil.getCompletedStateDef().getName(),
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null, changes, TransitionOption.None);
      IAtsTransitionManager transMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = new TransitionResults();
      transMgr.handleTransition(results);
      changes.execute();
      Assert.assertTrue("Transition Error: " + results.toString(), results.isEmpty());
      Assert.assertEquals("Completed", teamArt.getCurrentStateName());
      Assert.assertEquals(100, teamArt.getSoleAttributeValue(AtsAttributeTypes.PercentComplete, 100).intValue());

      // Transition to Implement should set percent to 0
      changes.clear();
      helper = new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
         AtsTestUtil.getImplementStateDef().getName(),
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null, changes, TransitionOption.None);
      transMgr = TransitionFactory.getTransitionManager(helper);
      results = new TransitionResults();
      transMgr.handleTransition(results);
      changes.execute();

      Assert.assertTrue("Transition Error: " + results.toString(), results.isEmpty());
      Assert.assertEquals("Implement", teamArt.getCurrentStateName());
      Assert.assertEquals(0, teamArt.getSoleAttributeValue(AtsAttributeTypes.PercentComplete, 0).intValue());

      // Transition to Cancelled should set percent to 0
      changes.clear();
      helper = new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
         AtsTestUtil.getCancelledStateDef().getName(),
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null, changes, TransitionOption.None);
      transMgr = TransitionFactory.getTransitionManager(helper);
      results = new TransitionResults();
      transMgr.handleTransition(results);
      changes.execute();

      Assert.assertTrue("Transition Error: " + results.toString(), results.isEmpty());
      Assert.assertEquals("Cancelled", teamArt.getCurrentStateName());
      Assert.assertEquals(100, teamArt.getSoleAttributeValue(AtsAttributeTypes.PercentComplete, 100).intValue());
   }

   @org.junit.Test
   public void testIsStateTransitionable__ReviewsCancelled() {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-Cancel");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      TransitionResults results = new TransitionResults();

      // create a peer to peer review
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      PeerToPeerReviewArtifact peerReview =
         AtsTestUtil.getOrCreatePeerReview(ReviewBlockType.Transition, AtsTestUtilState.Analyze, changes);
      changes.relate(teamArt, AtsRelationTypes.TeamWorkflowToReview_Review, peerReview);
      changes.execute();

      // transition workflow to cancelled - peer review not cancelled
      changes.clear();
      TransitionHelper transHelper = new TransitionHelper("Transition Team Workflow Review", Arrays.asList(teamArt),
         "Cancelled", new ArrayList<IAtsUser>(), "", changes, AtsClientService.get().getServices(),
         TransitionOption.OverrideAssigneeCheck);
      transHelper.setTransitionUser(AtsClientService.get().getUserService().getCurrentUser());
      TransitionManager mgr = new TransitionManager(transHelper);
      results = mgr.handleAll();
      changes.execute();
      Assert.assertTrue(results.contains(teamArt, TransitionResult.CANCEL_REVIEWS_BEFORE_CANCEL));

      // transition workflow again - peer review cancelled
      results.clear();
      changes.clear();
      transHelper = new TransitionHelper("Transition Team Workflow Review", Arrays.asList(peerReview), "Cancelled",
         new ArrayList<IAtsUser>(), "", changes, AtsClientService.get().getServices(),
         TransitionOption.OverrideAssigneeCheck);
      transHelper.setTransitionUser(AtsClientService.get().getUserService().getCurrentUser());
      mgr = new TransitionManager(transHelper);
      results = mgr.handleAll();
      changes.execute();
      Assert.assertTrue(results.isEmpty());

   }

}
