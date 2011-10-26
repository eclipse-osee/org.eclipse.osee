/**
 * @author Donald G. Dunne
 */
package org.eclipse.osee.ats.core.workflow.transition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.ats.core.AtsTestUtil;
import org.eclipse.osee.ats.core.AtsTestUtil.AtsTestUtilState;
import org.eclipse.osee.ats.core.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.core.review.DecisionReviewManager;
import org.eclipse.osee.ats.core.review.DecisionReviewState;
import org.eclipse.osee.ats.core.task.TaskArtifact;
import org.eclipse.osee.ats.core.task.TaskManager;
import org.eclipse.osee.ats.core.team.TeamState;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.type.AtsRelationTypes;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workdef.ReviewBlockType;
import org.eclipse.osee.ats.core.workdef.RuleDefinition;
import org.eclipse.osee.ats.core.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetOption;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.support.test.util.DemoUsers;
import org.junit.AfterClass;
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
      AtsTestUtil.cleanupAndReset("TransitionManagerTest");
      TransitionHelper helper =
         new TransitionHelper(getClass().getSimpleName(), EMPTY_AWAS, AtsTestUtil.getImplementStateDef().getPageName(),
            Arrays.asList(UserManager.getUser()), null, TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(TransitionResult.NO_WORKFLOWS_PROVIDED_FOR_TRANSITION));
   }

   @org.junit.Test
   public void testHandleTransitionValidation__ToStateNotNull() throws OseeCoreException {
      TransitionHelper helper =
         new TransitionHelper(getClass().getSimpleName(), Arrays.asList(AtsTestUtil.getTeamWf()), null,
            Arrays.asList(UserManager.getUser()), null, TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(TransitionResult.TO_STATE_CANT_BE_NULL));
   }

   @org.junit.Test
   public void testHandleTransitionValidation__InvalidToState() throws OseeCoreException {
      TransitionHelper helper =
         new TransitionHelper(getClass().getSimpleName(), Arrays.asList(AtsTestUtil.getTeamWf()), "InvalidStateName",
            Arrays.asList(UserManager.getUser()), null, TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains("Transition-To State [InvalidStateName] does not exist for Work Definition [" + AtsTestUtil.getTeamWf().getWorkDefinition().getName() + "]"));
   }

   @org.junit.Test
   public void testHandleTransitionValidation__MustBeAssigned() throws OseeCoreException {
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      TransitionHelper helper =
         new TransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getImplementStateDef().getPageName(), Arrays.asList(UserManager.getUser()), null,
            TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // First transition should be valid cause Joe Smith is assigned cause he created
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.toString(), results.isEmpty());

      // Un-Assign Joe Smith
      results.clear();
      Assert.assertFalse(helper.isPriviledgedEditEnabled());
      Assert.assertFalse(helper.isOverrideAssigneeCheck());
      teamArt.getStateMgr().setAssignee(UserManager.getUser(DemoUsers.Alex_Kay));
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(AtsTestUtil.getTeamWf(), TransitionResult.MUST_BE_ASSIGNED));

      // Set PriviledgedEditEnabled edit enabled; no errors
      results.clear();
      Assert.assertFalse(helper.isOverrideAssigneeCheck());
      helper.addTransitionOption(TransitionOption.PriviledgedEditEnabled);
      Assert.assertTrue(helper.isPriviledgedEditEnabled());
      teamArt.getStateMgr().setAssignee(UserManager.getUser(DemoUsers.Alex_Kay));
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // Set OverrideAssigneeCheck
      results.clear();
      helper.removeTransitionOption(TransitionOption.PriviledgedEditEnabled);
      helper.addTransitionOption(TransitionOption.OverrideAssigneeCheck);
      Assert.assertFalse(helper.isPriviledgedEditEnabled());
      Assert.assertTrue(helper.isOverrideAssigneeCheck());
      teamArt.getStateMgr().setAssignee(UserManager.getUser(DemoUsers.Alex_Kay));
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // Set UnAssigned, should be able to transition cause will be assigned as convenience
      results.clear();
      helper.removeTransitionOption(TransitionOption.OverrideAssigneeCheck);
      Assert.assertFalse(helper.isPriviledgedEditEnabled());
      Assert.assertFalse(helper.isOverrideAssigneeCheck());
      teamArt.getStateMgr().setAssignee(UserManager.getUser(SystemUser.UnAssigned));
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // cleanup test
      teamArt.getStateMgr().setAssignee(UserManager.getUser(SystemUser.UnAssigned));
   }

   @org.junit.Test
   public void testHandleTransitionValidation__WorkingBranchTransitionable() throws OseeCoreException {
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      TestTransitionHelper helper =
         new TestTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getImplementStateDef().getPageName(), Arrays.asList(UserManager.getUser()), null,
            TransitionOption.None);
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
      helper.setToStateName(TeamState.Cancelled.getPageName());
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(AtsTestUtil.getTeamWf(), TransitionResult.DELETE_WORKING_BRANCH_BEFORE_CANCEL));

      // attempt to transition workflow with branch being committed
      results.clear();
      helper.setWorkingBranchInWork(true);
      helper.setBranchInCommit(true);
      helper.setToStateName(TeamState.Implement.getPageName());
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(AtsTestUtil.getTeamWf(), TransitionResult.WORKING_BRANCH_BEING_COMMITTED));
   }

   @org.junit.Test
   public void testHandleTransitionValidation__NoSystemUser() throws OseeCoreException {
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      TestTransitionHelper helper =
         new TestTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getImplementStateDef().getPageName(), Arrays.asList(UserManager.getUser()), null,
            TransitionOption.None);
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
      TestTransitionHelper helper =
         new TestTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getImplementStateDef().getPageName(), Arrays.asList(UserManager.getUser()), null,
            TransitionOption.None);
      StateDefinition fromStateDef = AtsTestUtil.getAnalyzeStateDef();
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // test that normal transition works
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue("Test wasn't reset to allow transition", results.isEmpty());

      WidgetDefinition estHoursWidget = AtsTestUtil.getEstHoursWidgetDef();
      fromStateDef.getStateItems().add(estHoursWidget);
      WidgetDefinition workPackageWidget = AtsTestUtil.getWorkPackageWidgetDef();
      fromStateDef.getStateItems().add(workPackageWidget);

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
      TestTransitionHelper helper =
         new TestTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getImplementStateDef().getPageName(), Arrays.asList(UserManager.getUser()), null,
            TransitionOption.None);
      StateDefinition fromStateDef = AtsTestUtil.getAnalyzeStateDef();
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // test that normal transition works
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue("Test wasn't reset to allow transition", results.isEmpty());

      WidgetDefinition estHoursWidget = AtsTestUtil.getEstHoursWidgetDef();
      fromStateDef.getStateItems().add(estHoursWidget);
      WidgetDefinition workPackageWidget = AtsTestUtil.getWorkPackageWidgetDef();
      fromStateDef.getStateItems().add(workPackageWidget);

      // test that two widgets validate
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // test that Work Package only widget required for normal transition
      results.clear();
      helper.setToStateName(AtsTestUtil.getImplementStateDef().getPageName());
      estHoursWidget.getOptions().add(WidgetOption.REQUIRED_FOR_COMPLETION);
      workPackageWidget.getOptions().add(WidgetOption.REQUIRED_FOR_TRANSITION);
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains("[Work Package] is required for transition"));

      // test that Estimated House and Work Package required for transition to completed
      results.clear();
      helper.setToStateName(AtsTestUtil.getCompletedStateDef().getPageName());
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains("[Estimated Hours] is required for transition to [Completed]"));
      Assert.assertTrue(results.contains("[Work Package] is required for transition"));

      // test that neither are required for transition to canceled
      results.clear();
      helper.setToStateName(AtsTestUtil.getCancelledStateDef().getPageName());
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // test that neither are required for transition to completed if overrideValidation is set
      results.clear();
      helper.setToStateName(AtsTestUtil.getCompletedStateDef().getPageName());
      helper.setOverrideTransitionValidityCheck(true);
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());
   }

   @org.junit.Test
   public void testIsStateTransitionable__ValidateTasks() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-3");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      TestTransitionHelper helper =
         new TestTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getImplementStateDef().getPageName(), Arrays.asList(UserManager.getUser()), null,
            TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // validate that can transition
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // validate that can't transition with InWork task
      TaskArtifact taskArt = teamArt.createNewTask("New Tasks", new Date(), UserManager.getUser());
      results.clear();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(teamArt, TransitionResult.TASKS_NOT_COMPLETED));

      StateDefinition teamCurrentState = teamArt.getStateDefinition();
      RuleDefinition allowTransRule = new RuleDefinition(RuleDefinitionOption.AllowTransitionWithoutTaskCompletion);

      try {
         // test that can transition with AllowTransitionWithoutTaskCompletion rule on state
         teamCurrentState.addRule(allowTransRule, "TransitionManagerTest-3");
         // transition task to completed
         results.clear();

         // should not get transition validation error now
         results.clear();
         transMgr.handleTransitionValidation(results);
         Assert.assertTrue(results.isEmpty());

         // attempt to transition parent to cancelled, should not be able to transition with un-completed/cancelled tasks
         helper =
            new TestTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
               AtsTestUtil.getCancelledStateDef().getPageName(), Arrays.asList(UserManager.getUser()), null,
               TransitionOption.None);
         transMgr = new TransitionManager(helper);
         results.clear();
         transMgr.handleTransitionValidation(results);
         Assert.assertTrue(results.contains(teamArt, TransitionResult.TASKS_NOT_COMPLETED));

         // Cleanup task by completing and validate can transition
         SkynetTransaction transaction = new SkynetTransaction(AtsUtilCore.getAtsBranch(), getClass().getSimpleName());
         TaskManager.transitionToCompleted(taskArt, 0.0, 0.1, transaction);
         transaction.execute();
         results.clear();
         transMgr.handleTransitionValidation(results);
         Assert.assertTrue(results.isEmpty());
      } finally {
         // just in case test goes bad, make sure we remove this rule
         teamCurrentState.removeRule(allowTransRule);
      }

   }

   @org.junit.Test
   public void testIsStateTransitionable__RequireTargetedVersion__FromTeamDef() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-4");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      TestTransitionHelper helper =
         new TestTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getImplementStateDef().getPageName(), Arrays.asList(UserManager.getUser()), null,
            TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // validate that can transition
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // validate that can't transition without targeted version when team def rule is set
      teamArt.getTeamDefinition().addAttribute(AtsAttributeTypes.RuleDefinition,
         RuleDefinitionOption.RequireTargetedVersion.name());
      results.clear();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(teamArt, TransitionResult.MUST_BE_TARGETED_FOR_VERSION));

      // set targeted version; transition validation should succeed
      results.clear();
      teamArt.addRelation(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version, AtsTestUtil.getVerArt1());
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());
   }

   @org.junit.Test
   public void testIsStateTransitionable__RequireTargetedVersion__FromPageDef() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("TransitionManagerTest-5");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      TestTransitionHelper helper =
         new TestTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getImplementStateDef().getPageName(), Arrays.asList(UserManager.getUser()), null,
            TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // validate that can transition
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // validate that can't transition without targeted version when team def rule is set
      AtsTestUtil.getAnalyzeStateDef().addRule(new RuleDefinition(RuleDefinitionOption.RequireTargetedVersion),
         "from test");
      results.clear();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.contains(teamArt, TransitionResult.MUST_BE_TARGETED_FOR_VERSION));

      // set targeted version; transition validation should succeed
      results.clear();
      teamArt.addRelation(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version, AtsTestUtil.getVerArt1());
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());
   }

   @org.junit.Test
   public void testIsStateTransitionable__ReviewsCompleted() throws OseeCoreException {

      AtsTestUtil.cleanupAndReset("TransitionManagerTest-6");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      TestTransitionHelper helper =
         new TestTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getImplementStateDef().getPageName(), Arrays.asList(UserManager.getUser()), null,
            TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // validate that can transition
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      DecisionReviewArtifact decArt =
         AtsTestUtil.getOrCreateDecisionReview(ReviewBlockType.None, AtsTestUtilState.Analyze);
      teamArt.addRelation(AtsRelationTypes.TeamWorkflowToReview_Review, decArt);

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
      results.clear();
      SkynetTransaction transaction = new SkynetTransaction(AtsUtilCore.getAtsBranch(), getClass().getSimpleName());
      DecisionReviewManager.transitionTo(decArt, DecisionReviewState.Completed, UserManager.getUser(), false,
         transaction);
      transaction.execute();
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());
   }

   @org.junit.Test
   public void testHandleTransitionValidation__ExtensionPointCheck() throws OseeCoreException {

      AtsTestUtil.cleanupAndReset("TransitionManagerTest-7");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      TestTransitionHelper helper =
         new TestTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
            AtsTestUtil.getImplementStateDef().getPageName(), Arrays.asList(UserManager.getUser()), null,
            TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper);
      TransitionResults results = new TransitionResults();

      // validate that can transition
      transMgr.handleTransitionValidation(results);
      Assert.assertTrue(results.isEmpty());

      // add transition listeners and verify can't transition
      final String reason1 = "Don't want you to transition";
      final String reason2 = "Don't transition yet";
      final String exceptionStr = "This is the exception message";
      ITransitionListener listener1 = new TransitionAdapter() {

         @Override
         public void transitioning(TransitionResults results, AbstractWorkflowArtifact sma, IWorkPage fromState, IWorkPage toState, Collection<? extends IBasicUser> toAssignees) {
            results.addResult(new TransitionResult(reason1));
         }

      };
      ITransitionListener listener2 = new TransitionAdapter() {

         @Override
         public void transitioning(TransitionResults results, AbstractWorkflowArtifact sma, IWorkPage fromState, IWorkPage toState, Collection<? extends IBasicUser> toAssignees) {
            results.addResult(sma, new TransitionResult(reason2));
         }

      };
      ITransitionListener listener3 = new TransitionAdapter() {

         @Override
         public void transitioning(TransitionResults results, AbstractWorkflowArtifact sma, IWorkPage fromState, IWorkPage toState, Collection<? extends IBasicUser> toAssignees) {
            // do nothing
         }

      };
      ITransitionListener listener4 = new TransitionAdapter() {

         @Override
         public void transitioning(TransitionResults results, AbstractWorkflowArtifact sma, IWorkPage fromState, IWorkPage toState, Collection<? extends IBasicUser> toAssignees) throws OseeCoreException {
            throw new OseeCoreException(exceptionStr);
         }

      };
      try {
         TransitionManager.addListener(listener1);
         TransitionManager.addListener(listener2);
         TransitionManager.addListener(listener3);
         TransitionManager.addListener(listener4);

         transMgr.handleTransitionValidation(results);
         Assert.assertTrue(results.contains(reason1));
         Assert.assertTrue(results.contains(reason2));
         Assert.assertTrue(results.contains(exceptionStr));
      } finally {
         TransitionManager.removeListener(listener1);
         TransitionManager.removeListener(listener2);
         TransitionManager.removeListener(listener3);
         TransitionManager.removeListener(listener4);
      }
   }
}
