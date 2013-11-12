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
package org.eclipse.osee.ats.client.integration.tests.ats.core.client.workflow.transition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResult;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.client.demo.DemoUsers;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil.AtsTestUtilState;
import org.eclipse.osee.ats.core.client.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.core.client.review.DecisionReviewManager;
import org.eclipse.osee.ats.core.client.review.DecisionReviewState;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.task.TaskManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.config.AtsVersionService;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.mocks.MockStateDefinition;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Test unit for {@link TransitionManager}
 * 
 * @author Donald G. Dunne
 */
public class TransitionManagerTest {

   private static List<AbstractWorkflowArtifact> EMPTY_AWAS = new ArrayList<AbstractWorkflowArtifact>();

   @BeforeClass
   @AfterClass
   public static void cleanup() throws OseeCoreException {
      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testHandleTransitionValidation__NoAwas() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-A");
      TransitionHelper helper =
         new TransitionHelper(getClass().getSimpleName(), EMPTY_AWAS, AtsTestUtil.getImplementStateDef().getName(),
            Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, new AtsChangeSet(
               getClass().getSimpleName()), TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(TransitionResult.NO_WORKFLOWS_PROVIDED_FOR_TRANSITION));
   }

   @org.junit.Test
   public void testHandleTransitionValidation__ToStateNotNull() throws OseeCoreException {
      TransitionHelper helper =
         new TransitionHelper(getClass().getSimpleName(), Arrays.asList(AtsTestUtil.getTeamWf()), null,
            Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, new AtsChangeSet(
               getClass().getSimpleName()), TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(TransitionResult.TO_STATE_CANT_BE_NULL));
   }

   @org.junit.Test
   public void testHandleTransitionValidation__InvalidToState() throws OseeCoreException {
      TransitionHelper helper =
         new TransitionHelper(getClass().getSimpleName(), Arrays.asList(AtsTestUtil.getTeamWf()), "InvalidStateName",
            Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, new AtsChangeSet(
               getClass().getSimpleName()), TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains("Transition-To State [InvalidStateName] does not exist for Work Definition [" + AtsTestUtil.getTeamWf().getWorkDefinition().getName() + "]"));
   }

   @org.junit.Test
   public void testHandleTransitionValidation__MustBeAssigned() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-B");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      IAtsTeamDefinition teamDef = teamArt.getTeamDefinition();
      Assert.assertNotNull(teamDef);

      TransitionHelper helper =
         new TransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getImplementStateDef().getName(),
            Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, new AtsChangeSet(
               getClass().getSimpleName()), TransitionOption.None);
      helper.setExecuteChanges(true);
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // First transition should be valid cause Joe Smith is assigned cause he created
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.toString(), results.isEmpty());

      // Un-Assign Joe Smith
      results.clear();
      Assert.assertFalse(helper.isPrivilegedEditEnabled());
      Assert.assertFalse(helper.isOverrideAssigneeCheck());
      teamArt.getStateMgr().setAssignee(AtsClientService.get().getUserAdmin().getUserFromToken(DemoUsers.Alex_Kay));
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(AtsTestUtil.getTeamWf(), TransitionResult.MUST_BE_ASSIGNED));

      // Set PrivilegedEditEnabled edit enabled; no errors
      results.clear();
      Assert.assertFalse(helper.isOverrideAssigneeCheck());
      helper.addTransitionOption(TransitionOption.PrivilegedEditEnabled);
      Assert.assertTrue(helper.isPrivilegedEditEnabled());
      teamArt.getStateMgr().setAssignee(AtsClientService.get().getUserAdmin().getUserFromToken(DemoUsers.Alex_Kay));
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // Set OverrideAssigneeCheck
      results.clear();
      helper.removeTransitionOption(TransitionOption.PrivilegedEditEnabled);
      helper.addTransitionOption(TransitionOption.OverrideAssigneeCheck);
      Assert.assertFalse(helper.isPrivilegedEditEnabled());
      Assert.assertTrue(helper.isOverrideAssigneeCheck());
      teamArt.getStateMgr().setAssignee(AtsClientService.get().getUserAdmin().getUserFromToken(DemoUsers.Alex_Kay));
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // Set UnAssigned, should be able to transition cause will be assigned as convenience
      results.clear();
      helper.removeTransitionOption(TransitionOption.OverrideAssigneeCheck);
      Assert.assertFalse(helper.isPrivilegedEditEnabled());
      Assert.assertFalse(helper.isOverrideAssigneeCheck());
      teamArt.getStateMgr().setAssignee(AtsClientService.get().getUserAdmin().getUserFromToken(SystemUser.UnAssigned));
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // cleanup test
      teamArt.getStateMgr().setAssignee(AtsClientService.get().getUserAdmin().getUserFromToken(SystemUser.UnAssigned));
   }

   @org.junit.Test
   public void testHandleTransitionValidation__WorkingBranchTransitionable() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-C");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      MockTransitionHelper helper =
         new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getImplementStateDef().getName(),
            Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, new AtsChangeSet(
               getClass().getSimpleName()), TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper);
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
      Assert.assertTrue(results.contains(AtsTestUtil.getTeamWf(), TransitionResult.DELETE_WORKING_BRANCH_BEFORE_CANCEL));

      // attempt to transition workflow with branch being committed
      results.clear();
      helper.setWorkingBranchInWork(true);
      helper.setBranchInCommit(true);
      helper.setToStateName(TeamState.Implement.getName());
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(AtsTestUtil.getTeamWf(), TransitionResult.WORKING_BRANCH_BEING_COMMITTED));
   }

   @org.junit.Test
   public void testHandleTransitionValidation__NoSystemUser() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-D");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      MockTransitionHelper helper =
         new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getImplementStateDef().getName(),
            Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, new AtsChangeSet(
               getClass().getSimpleName()), TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper);
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
   public void testIsStateTransitionable__ValidateXWidgets__RequiredForTransition() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-1");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      MockTransitionHelper helper =
         new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getImplementStateDef().getName(),
            Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, new AtsChangeSet(
               getClass().getSimpleName()), TransitionOption.None);
      IAtsStateDefinition fromStateDef = AtsTestUtil.getAnalyzeStateDef();
      fromStateDef.getLayoutItems().clear();
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // test that normal transition works
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue("Test wasn't reset to allow transition", results.isEmpty());

      IAtsWidgetDefinition estHoursWidget = AtsTestUtil.getEstHoursWidgetDef();
      fromStateDef.getLayoutItems().add(estHoursWidget);
      IAtsWidgetDefinition workPackageWidget = AtsTestUtil.getWorkPackageWidgetDef();
      fromStateDef.getLayoutItems().add(workPackageWidget);

      // test that two widgets validate
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // test that estHours required fails validation
      results.clear();
      estHoursWidget.getOptions().add(WidgetOption.REQUIRED_FOR_TRANSITION);
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains("[Estimated Hours] is required for transition"));

      // test that workPackage required fails both widgets
      results.clear();
      workPackageWidget.getOptions().add(WidgetOption.REQUIRED_FOR_TRANSITION);
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains("[Estimated Hours] is required for transition"));
      Assert.assertTrue(results.contains("[Work Package] is required for transition"));
   }

   @org.junit.Test
   public void testIsStateTransitionable__ValidateXWidgets__RequiredForCompletion() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-2");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      MockTransitionHelper helper =
         new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getImplementStateDef().getName(),
            Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, new AtsChangeSet(
               getClass().getSimpleName()), TransitionOption.None);
      IAtsStateDefinition fromStateDef = AtsTestUtil.getAnalyzeStateDef();
      fromStateDef.getLayoutItems().clear();
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // test that normal transition works
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue("Test wasn't reset to allow transition", results.isEmpty());

      IAtsWidgetDefinition estHoursWidget = AtsTestUtil.getEstHoursWidgetDef();
      fromStateDef.getLayoutItems().add(estHoursWidget);
      IAtsWidgetDefinition workPackageWidget = AtsTestUtil.getWorkPackageWidgetDef();
      fromStateDef.getLayoutItems().add(workPackageWidget);

      // test that two widgets validate
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // test that Work Package only widget required for normal transition
      results.clear();
      helper.setToStateName(AtsTestUtil.getImplementStateDef().getName());
      estHoursWidget.getOptions().add(WidgetOption.REQUIRED_FOR_COMPLETION);
      workPackageWidget.getOptions().add(WidgetOption.REQUIRED_FOR_TRANSITION);
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains("[Work Package] is required for transition"));

      // test that Estimated House and Work Package required for transition to completed
      results.clear();
      helper.setToStateName(AtsTestUtil.getCompletedStateDef().getName());
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains("[Estimated Hours] is required for transition to [Completed]"));
      Assert.assertTrue(results.contains("[Work Package] is required for transition"));

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
   public void testIsStateTransitionable__ValidateTasks() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-3");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      MockTransitionHelper helper =
         new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getImplementStateDef().getName(),
            Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, new AtsChangeSet(
               getClass().getSimpleName()), TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // validate that can transition
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // validate that can't transition with InWork task
      AtsChangeSet changes = new AtsChangeSet(getClass().getSimpleName());
      TaskArtifact taskArt =
         teamArt.createNewTask("New Tasks", new Date(), AtsClientService.get().getUserAdmin().getCurrentUser(),
            teamArt.getCurrentStateName(), changes);
      results.clear();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(teamArt, TransitionResult.TASKS_NOT_COMPLETED));
      changes.execute();

      MockStateDefinition teamCurrentState = (MockStateDefinition) teamArt.getStateDefinition();

      try {
         // test that can transition with AllowTransitionWithoutTaskCompletion rule on state
         teamCurrentState.addRule(RuleDefinitionOption.AllowTransitionWithoutTaskCompletion.name());
         // transition task to completed
         results.clear();

         // should not get transition validation error now
         results.clear();
         transMgr.handleTransitionValidation(results);
         Assert.assertTrue(results.isEmpty());

         // attempt to transition parent to cancelled, should not be able to transition with un-completed/cancelled tasks
         helper =
            new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
               AtsTestUtil.getCancelledStateDef().getName(),
               Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, new AtsChangeSet(
                  getClass().getSimpleName()), TransitionOption.None);
         transMgr = new TransitionManager(helper);
         results.clear();
         transMgr.handleTransitionValidation(results);
         Assert.assertTrue(results.contains(teamArt, TransitionResult.TASKS_NOT_COMPLETED));

         // Cleanup task by completing and validate can transition
         changes = new AtsChangeSet(getClass().getSimpleName());
         TaskManager.transitionToCompleted(taskArt, 0.0, 0.1, changes);
         changes.execute();
         results.clear();
         transMgr.handleTransitionValidation(results);
         Assert.assertTrue(results.isEmpty());
      } finally {
         // just in case test goes bad, make sure we remove this rule
         teamCurrentState.removeRule(RuleDefinitionOption.AllowTransitionWithoutTaskCompletion.name());
      }

   }

   @org.junit.Test
   public void testIsStateTransitionable__RequireTargetedVersion__FromTeamDef() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-4");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      MockTransitionHelper helper =
         new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getImplementStateDef().getName(),
            Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, new AtsChangeSet(
               getClass().getSimpleName()), TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // validate that can transition
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // validate that can't transition without targeted version when team def rule is set
      teamArt.getTeamDefinition().addRule(RuleDefinitionOption.RequireTargetedVersion.name());
      results.clear();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(teamArt, TransitionResult.MUST_BE_TARGETED_FOR_VERSION));

      // set targeted version; transition validation should succeed
      results.clear();
      AtsVersionService.get().setTargetedVersion(teamArt, AtsTestUtil.getVerArt1());
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());
   }

   @org.junit.Test
   public void testIsStateTransitionable__RequireTargetedVersion__FromPageDef() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-5");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      MockTransitionHelper helper =
         new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getImplementStateDef().getName(),
            Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, new AtsChangeSet(
               getClass().getSimpleName()), TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // validate that can transition
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // validate that can't transition without targeted version when team def rule is set
      AtsTestUtil.getAnalyzeStateDef().addRule(RuleDefinitionOption.RequireTargetedVersion.name());
      results.clear();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(teamArt, TransitionResult.MUST_BE_TARGETED_FOR_VERSION));

      // set targeted version; transition validation should succeed
      results.clear();
      AtsVersionService.get().setTargetedVersion(teamArt, AtsTestUtil.getVerArt1());
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());
   }

   @org.junit.Test
   public void testIsStateTransitionable__ReviewsCompleted() throws OseeCoreException {

      AtsTestUtil.cleanupAndReset("TransitionManagerTest-6");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      MockTransitionHelper helper =
         new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getImplementStateDef().getName(),
            Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, new AtsChangeSet(
               getClass().getSimpleName()), TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // validate that can transition
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      AtsChangeSet changes = new AtsChangeSet(getClass().getSimpleName());
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

      // validate that can transition cause review completed
      changes = new AtsChangeSet(getClass().getSimpleName());
      Result result =
         DecisionReviewManager.transitionTo(decArt, DecisionReviewState.Completed,
            AtsClientService.get().getUserAdmin().getCurrentUser(), false, changes);
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
   public void testIsStateTransitionable__ToSameState() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-8");
      TeamWorkFlowArtifact teamArt01 = AtsTestUtil.getTeamWf();
      TeamWorkFlowArtifact teamArt02 = AtsTestUtil.getTeamWf2();

      //1. Initially transition workflows to Implement
      MockTransitionHelper helper =
         new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt01, teamArt02),
            AtsTestUtil.getImplementStateDef().getName(),
            Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, new AtsChangeSet(
               getClass().getSimpleName()), TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      //2. redundant transition workflows to Implement
      MockTransitionHelper helper01 =
         new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt01, teamArt02),
            AtsTestUtil.getImplementStateDef().getName(),
            Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, new AtsChangeSet(
               getClass().getSimpleName()), TransitionOption.None);
      TransitionManager transMgr01 = new TransitionManager(helper01);
      TransitionResults results01 = new TransitionResults();
      transMgr01.handleTransitionValidation(results01);
      Assert.assertTrue(results01.isEmpty());

      //3. Transition one TeamWf to Complete
      MockTransitionHelper helper02 =
         new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt01),
            AtsTestUtil.getCompletedStateDef().getName(),
            Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, new AtsChangeSet(
               getClass().getSimpleName()), TransitionOption.None);
      TransitionManager transMgr02 = new TransitionManager(helper02);
      TransitionResults results02 = new TransitionResults();
      transMgr02.handleTransitionValidation(results02);
      Assert.assertTrue(results02.isEmpty());

      //4. redundant transition workflows to Implement
      MockTransitionHelper helper03 =
         new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt01, teamArt02),
            AtsTestUtil.getCompletedStateDef().getName(),
            Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, new AtsChangeSet(
               getClass().getSimpleName()), TransitionOption.None);
      TransitionManager transMgr03 = new TransitionManager(helper03);
      TransitionResults results03 = new TransitionResults();
      transMgr03.handleTransitionValidation(results03);
      Assert.assertTrue(results03.isEmpty());
   }

   @org.junit.Test
   public void testHandleTransitionValidation__AssigneesUpdate() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-E");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      List<IAtsUser> assigneesBefore = teamArt.getAssignees();
      Assert.assertTrue(assigneesBefore.size() > 0);
      MockTransitionHelper helper =
         new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getImplementStateDef().getName(), teamArt.getAssignees(), null, new AtsChangeSet(
               getClass().getSimpleName()), TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper);
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
   public void testHandleTransitionValidation__AssigneesNull() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-F");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      List<IAtsUser> assigneesBefore = teamArt.getAssignees();
      Assert.assertTrue(assigneesBefore.size() > 0);
      MockTransitionHelper helper =
         new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getImplementStateDef().getName(), null, null, new AtsChangeSet(getClass().getSimpleName()),
            TransitionOption.None);
      helper.setExecuteChanges(true);
      TransitionManager transMgr = new TransitionManager(helper);
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
   public void testHandleTransition__PercentComplete() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-G");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();

      // Setup - Transition to Implement
      AtsChangeSet changes = new AtsChangeSet("create");
      Result result =
         AtsTestUtil.transitionTo(AtsTestUtilState.Implement, AtsClientService.get().getUserAdmin().getCurrentUser(),
            changes, TransitionOption.OverrideAssigneeCheck);
      changes.execute();
      Assert.assertTrue("Transition Error: " + result.getText(), result.isTrue());
      Assert.assertEquals("Implement", teamArt.getCurrentStateName());
      Assert.assertEquals(0, teamArt.getSoleAttributeValue(AtsAttributeTypes.PercentComplete, 0).intValue());

      // Transition to completed should set percent to 100
      changes.clear();
      MockTransitionHelper helper =
         new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getCompletedStateDef().getName(),
            Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, changes, TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();
      transMgr.handleTransition(results);
      changes.execute();
      Assert.assertTrue("Transition Error: " + results.toString(), results.isEmpty());
      Assert.assertEquals("Completed", teamArt.getCurrentStateName());
      Assert.assertEquals(100, teamArt.getSoleAttributeValue(AtsAttributeTypes.PercentComplete, 100).intValue());

      // Transition to Implement should set percent to 0
      changes.clear();
      helper =
         new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getImplementStateDef().getName(),
            Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, changes, TransitionOption.None);
      transMgr = new TransitionManager(helper);
      results = new TransitionResults();
      transMgr.handleTransition(results);
      changes.execute();

      Assert.assertTrue("Transition Error: " + results.toString(), results.isEmpty());
      Assert.assertEquals("Implement", teamArt.getCurrentStateName());
      Assert.assertEquals(0, teamArt.getSoleAttributeValue(AtsAttributeTypes.PercentComplete, 0).intValue());

      // Transition to Cancelled should set percent to 0
      changes.clear();
      helper =
         new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getCancelledStateDef().getName(),
            Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, changes, TransitionOption.None);
      transMgr = new TransitionManager(helper);
      results = new TransitionResults();
      transMgr.handleTransition(results);
      changes.execute();

      Assert.assertTrue("Transition Error: " + results.toString(), results.isEmpty());
      Assert.assertEquals("Cancelled", teamArt.getCurrentStateName());
      Assert.assertEquals(100, teamArt.getSoleAttributeValue(AtsAttributeTypes.PercentComplete, 100).intValue());

   }

}
