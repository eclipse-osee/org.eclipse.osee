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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.DecisionReviewState;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewOption;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.core.review.AtsReviewServiceImpl;
import org.eclipse.osee.ats.core.workdef.SimpleDecisionReviewOption;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Test for {@link AtsReviewServiceImpl}
 *
 * @author Donald G. Dunne
 */
public class DecisionReviewManagerTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testGetDecisionReviewOptionsStr() {
      Assert.assertEquals("Yes;Followup;<3333>\nNo;Completed;\n",
         AtsApiService.get().getReviewService().getDecisionReviewOptionsString(
            AtsApiService.get().getReviewService().getDefaultDecisionReviewOptions()));
   }

   @org.junit.Test
   public void testCreateNewDecisionReviewAndTransitionToDecision__Normal() {
      AtsTestUtil.cleanupAndReset("DecisionReviewManagerTest - Normal");
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      List<IAtsDecisionReviewOption> options = new ArrayList<>();
      options.add(new SimpleDecisionReviewOption(DecisionReviewState.Completed.getName(), false, null));
      options.add(new SimpleDecisionReviewOption(DecisionReviewState.Followup.getName(), true,
         Arrays.asList(AtsApiService.get().getUserService().getCurrentUser().getUserId())));

      // create and transition decision review
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      String reviewTitle = "Test Review - " + teamWf.getName();
      IAtsDecisionReview decRev =
         AtsApiService.get().getReviewService().createNewDecisionReviewAndTransitionToDecision(teamWf, reviewTitle,
            "my description", AtsTestUtil.getAnalyzeStateDef().getName(), ReviewBlockType.Transition, options,
            Arrays.asList(AtsApiService.get().getUserService().getCurrentUser()), new Date(),
            AtsApiService.get().getUserService().getCurrentUser(), changes);
      changes.execute();

      Assert.assertNotNull(decRev);
      Assert.assertFalse(
         String.format("Decision Review artifact should not be dirty [%s]",
            Artifacts.getDirtyReport(AtsApiService.get().getQueryServiceIde().getArtifact(decRev))),
         AtsApiService.get().getQueryServiceIde().getArtifact(decRev).isDirty());
      Assert.assertEquals(DecisionReviewState.Decision.getName(), decRev.getCurrentStateName());
      Assert.assertEquals(DemoUsers.Joe_Smith.getName(), decRev.getStateMgr().getAssigneesStr());

   }

   @org.junit.Test
   public void testCreateNewDecisionReviewAndTransitionToDecision__UnAssigned() {
      AtsTestUtil.cleanupAndReset("DecisionReviewManagerTest - UnAssigned");
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      List<IAtsDecisionReviewOption> options = new ArrayList<>();
      options.add(new SimpleDecisionReviewOption(DecisionReviewState.Completed.getName(), false, null));
      options.add(new SimpleDecisionReviewOption(DecisionReviewState.Followup.getName(), true,
         Arrays.asList(AtsApiService.get().getUserService().getCurrentUser().getUserId())));

      // create and transition decision review
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      String reviewTitle = "Test Review - " + teamWf.getName();
      IAtsDecisionReview decRev =
         AtsApiService.get().getReviewService().createNewDecisionReviewAndTransitionToDecision(teamWf, reviewTitle,
            "my description", AtsTestUtil.getAnalyzeStateDef().getName(), ReviewBlockType.Transition, options,
            Arrays.asList(AtsApiService.get().getUserService().getUserByToken(SystemUser.UnAssigned)), new Date(),
            AtsApiService.get().getUserService().getCurrentUser(), changes);
      changes.execute();

      Assert.assertNotNull(decRev);
      Assert.assertEquals(reviewTitle, decRev.getName());
      Assert.assertFalse(
         String.format("Decision Review artifact should not be dirty [%s]",
            Artifacts.getDirtyReport(AtsApiService.get().getQueryServiceIde().getArtifact(decRev))),
         AtsApiService.get().getQueryServiceIde().getArtifact(decRev).isDirty());
      Assert.assertEquals(DecisionReviewState.Decision.getName(), decRev.getCurrentStateName());
      Assert.assertEquals("UnAssigned", decRev.getStateMgr().getAssigneesStr());

   }

   @org.junit.Test
   public void testCreateNewDecisionReview__Base() {
      AtsTestUtil.cleanupAndReset("DecisionReviewManagerTest - Base");
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      String reviewTitle = "Test Review - " + teamWf.getName();
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      IAtsDecisionReview decRev = AtsApiService.get().getReviewService().createNewDecisionReview(teamWf,
         ReviewBlockType.Commit, reviewTitle, TeamState.Implement.getName(), "description",
         AtsApiService.get().getReviewService().getDefaultDecisionReviewOptions(),
         Arrays.asList(AtsApiService.get().getUserService().getUserByToken(DemoUsers.Alex_Kay)), new Date(),
         AtsApiService.get().getUserService().getCurrentUser(), changes);
      changes.execute();

      Assert.assertNotNull(decRev);
      Assert.assertEquals(reviewTitle, decRev.getName());
      Assert.assertEquals(DecisionReviewState.Prepare.getName(), decRev.getCurrentStateName());
      Assert.assertEquals("Alex Kay", decRev.getStateMgr().getAssigneesStr());
      Assert.assertEquals(TeamState.Implement.getName(),
         AtsApiService.get().getAttributeResolver().getSoleAttributeValue(decRev, AtsAttributeTypes.RelatedToState,
            ""));
      Assert.assertEquals(ReviewBlockType.Commit.name(),
         AtsApiService.get().getAttributeResolver().getSoleAttributeValue(decRev, AtsAttributeTypes.ReviewBlocks,
            ""));
   }

   @org.junit.Test
   public void testCreateNewDecisionReview__BaseUnassigned() {
      AtsTestUtil.cleanupAndReset("DecisionReviewManagerTest - BaseUnassigned");
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      String reviewTitle = "Test Review - " + teamWf.getName();
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      IAtsDecisionReview decRev = AtsApiService.get().getReviewService().createNewDecisionReview(teamWf,
         ReviewBlockType.Commit, reviewTitle, TeamState.Implement.getName(), "description",
         AtsApiService.get().getReviewService().getDefaultDecisionReviewOptions(),
         Arrays.asList(AtsApiService.get().getUserService().getUserByToken(SystemUser.UnAssigned)),
         new Date(), AtsApiService.get().getUserService().getCurrentUser(), changes);
      changes.execute();

      Assert.assertNotNull(decRev);
      Assert.assertEquals(reviewTitle, decRev.getName());
      Assert.assertEquals("UnAssigned", decRev.getStateMgr().getAssigneesStr());
   }

   @org.junit.Test
   public void testCreateNewDecisionReview__Sample() {
      AtsTestUtil.cleanupAndReset("DecisionReviewManagerTest - Sample");
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      IAtsDecisionReview decRev = AtsApiService.get().getReviewService().createNewDecisionReview(teamWf,
         ReviewBlockType.Commit, true, new Date(), AtsApiService.get().getUserService().getCurrentUser(), changes);
      changes.execute();

      Assert.assertNotNull(decRev);
      Assert.assertEquals("Should we do this?  Yes will require followup, No will not", decRev.getName());
      Assert.assertEquals(DecisionReviewState.Prepare.getName(), decRev.getCurrentStateName());
      Assert.assertEquals(DemoUsers.Joe_Smith.getName(), decRev.getStateMgr().getAssigneesStr());
      Assert.assertEquals(TeamState.Analyze.getName(),
         AtsApiService.get().getAttributeResolver().getSoleAttributeValue(decRev, AtsAttributeTypes.RelatedToState,
            ""));
      Assert.assertEquals(ReviewBlockType.Commit.name(),
         AtsApiService.get().getAttributeResolver().getSoleAttributeValue(decRev, AtsAttributeTypes.ReviewBlocks,
            ""));
   }

}
