/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review.hooks;

import static org.junit.Assert.assertFalse;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.DecisionReviewState;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXDecisionRevWidget;
import org.eclipse.osee.ats.ide.workflow.hooks.AtsDecisionReviewDecisionWorkItemHookIde;
import org.eclipse.osee.ats.ide.workflow.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.util.BooleanState;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for the Decision Review decision workflow: creates a real Decision Review with default options, sets the
 * Decision, transitions, and verifies it lands in the correct state (Yes -> Followup, No -> Completed) with correct
 * assignees. Also covers the {@link AtsDecisionReviewDecisionWorkItemHookIde} routing logic and that
 * {@link XXDecisionRevWidget} reads its options from the review.
 *
 * @author Donald G. Dunne
 */
public class AtsDecisionReviewDecisionStateHookTest {

   @Before
   public void setUp() {
      assertFalse("Test should not be run in production db", AtsApiService.get().getStoreService().isProductionDb());
   }

   @BeforeClass
   @AfterClass
   public static void testCleanup() throws Exception {
      AtsTestUtil.cleanup();
   }

   @Test
   public void testDecisionYesTransitionsToFollowup() {
      IAtsDecisionReview decRev = createDecisionReviewInDecisionState("Yes to Followup");
      DecisionReviewArtifact decArt = (DecisionReviewArtifact) AtsApiService.get().getQueryServiceIde().getArtifact(
         decRev);

      // The widget's selectable options are read straight from the review's DecisionReviewOptions attribute
      XXDecisionRevWidget decisionWidget = new XXDecisionRevWidget();
      decisionWidget.setArtifact(decArt);
      Assert.assertEquals(BooleanState.Yes.name(), decisionWidget.getSelectable().toArray()[0]);
      Assert.assertEquals(BooleanState.No.name(), decisionWidget.getSelectable().toArray()[1]);

      // "Yes" -> followup required -> Followup state, assigned to current user
      transitionDecision(decRev, BooleanState.Yes.name());

      Assert.assertEquals(DecisionReviewState.Followup.getName(), decRev.getCurrentStateName());
      Assert.assertEquals(AtsApiService.get().getUserService().getCurrentUser().getName(), decRev.getAssigneesStr());
   }

   @Test
   public void testDecisionNoTransitionsToCompleted() {
      IAtsDecisionReview decRev = createDecisionReviewInDecisionState("No to Completed");

      // "No" -> no followup -> Completed state
      transitionDecision(decRev, BooleanState.No.name());

      Assert.assertEquals(DecisionReviewState.Completed.getName(), decRev.getCurrentStateName());
   }

   /**
    * Creates a Decision Review with the default options (Yes -> Followup, No -> Completed) and transitions it to the
    * Decision state.
    */
   private IAtsDecisionReview createDecisionReviewInDecisionState(String testName) {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName() + " - " + testName);
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      AtsUser currentUser = AtsApiService.get().getUserService().getCurrentUser();
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      IAtsDecisionReview decRev = AtsApiService.get().getReviewService().createNewDecisionReviewAndTransitionToDecision(
         teamWf, "Test Decision Review - " + testName, "description", AtsTestUtil.getAnalyzeStateDef().getName(),
         ReviewBlockType.Transition, AtsApiService.get().getReviewService().getDefaultDecisionReviewOptions(),
         Arrays.asList(currentUser), new Date(), currentUser, changes);
      changes.execute();

      Assert.assertEquals(DecisionReviewState.Decision.getName(), decRev.getCurrentStateName());
      return decRev;
   }

   /**
    * Sets the Decision attribute and transitions to the state the decision routes to (computed via the hook's headless
    * routing logic), with the decision option's assignees.
    */
   private void transitionDecision(IAtsDecisionReview decRev, String decision) {
      DecisionReviewArtifact decArt = (DecisionReviewArtifact) AtsApiService.get().getQueryServiceIde().getArtifact(
         decRev);

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      changes.setSoleAttributeValue(decRev, AtsAttributeTypes.Decision, decision);
      changes.execute();

      AtsDecisionReviewDecisionWorkItemHookIde hook = new AtsDecisionReviewDecisionWorkItemHookIde();
      XXDecisionRevWidget decisionWidget = new XXDecisionRevWidget();
      decisionWidget.setArtifact(decArt);
      decisionWidget.setSelected(decision);
      String toState = hook.getOverrideTransitionToStateName(decArt, decisionWidget);
      Collection<AtsUser> toAssignees = hook.getOverrideTransitionToAssignees(decArt, decision);

      IAtsChangeSet transChanges = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      TransitionData transData = new TransitionData("Transition Decision", Arrays.asList(decRev), toState, toAssignees,
         null, transChanges, TransitionOption.OverrideAssigneeCheck);
      TransitionResults results = new TransitionManager(transData).handleAllAndPersist();
      Assert.assertTrue(results.toString(), results.isEmpty());
   }

}
