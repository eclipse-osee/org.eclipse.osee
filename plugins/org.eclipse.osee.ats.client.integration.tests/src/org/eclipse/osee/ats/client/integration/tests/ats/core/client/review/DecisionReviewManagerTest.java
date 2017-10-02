/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.core.client.review;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewOption;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.core.client.review.DecisionReviewManager;
import org.eclipse.osee.ats.core.client.review.DecisionReviewState;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.workdef.SimpleDecisionReviewOption;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Test for {@link DecisionReviewManager}
 *
 * @author Donald G. Dunne
 */
public class DecisionReviewManagerTest extends DecisionReviewManager {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testGetDecisionReviewOptionsStr()  {
      Assert.assertEquals("Yes;Followup;<3333>\nNo;Completed;\n",
         AtsClientService.get().getReviewService().getDecisionReviewOptionsString(
            AtsClientService.get().getReviewService().getDefaultDecisionReviewOptions()));
   }

   @org.junit.Test
   public void testCreateNewDecisionReviewAndTransitionToDecision__Normal()  {
      AtsTestUtil.cleanupAndReset("DecisionReviewManagerTest - Normal");
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      List<IAtsDecisionReviewOption> options = new ArrayList<>();
      options.add(new SimpleDecisionReviewOption(DecisionReviewState.Completed.getName(), false, null));
      options.add(new SimpleDecisionReviewOption(DecisionReviewState.Followup.getName(), true,
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser().getUserId())));

      // create and transition decision review
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      String reviewTitle = "Test Review - " + teamWf.getName();
      IAtsDecisionReview decRev =
         AtsClientService.get().getReviewService().createNewDecisionReviewAndTransitionToDecision(teamWf, reviewTitle,
            "my description", AtsTestUtil.getAnalyzeStateDef().getName(), ReviewBlockType.Transition, options,
            Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), new Date(),
            AtsClientService.get().getUserService().getCurrentUser(), changes);
      changes.execute();

      Assert.assertNotNull(decRev);
      Assert.assertFalse(
         String.format("Decision Review artifact should not be dirty [%s]",
            Artifacts.getDirtyReport(((Artifact) decRev.getStoreObject()))),
         ((Artifact) decRev.getStoreObject()).isDirty());
      Assert.assertEquals(DecisionReviewState.Decision.getName(), decRev.getStateMgr().getCurrentStateName());
      Assert.assertEquals("Joe Smith", decRev.getStateMgr().getAssigneesStr());

   }

   @org.junit.Test
   public void testCreateNewDecisionReviewAndTransitionToDecision__UnAssigned()  {
      AtsTestUtil.cleanupAndReset("DecisionReviewManagerTest - UnAssigned");
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      List<IAtsDecisionReviewOption> options = new ArrayList<>();
      options.add(new SimpleDecisionReviewOption(DecisionReviewState.Completed.getName(), false, null));
      options.add(new SimpleDecisionReviewOption(DecisionReviewState.Followup.getName(), true,
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser().getUserId())));

      // create and transition decision review
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      String reviewTitle = "Test Review - " + teamWf.getName();
      IAtsDecisionReview decRev =
         AtsClientService.get().getReviewService().createNewDecisionReviewAndTransitionToDecision(teamWf, reviewTitle,
            "my description", AtsTestUtil.getAnalyzeStateDef().getName(), ReviewBlockType.Transition, options,
            Arrays.asList(AtsClientService.get().getUserServiceClient().getUserFromToken(SystemUser.UnAssigned)),
            new Date(), AtsClientService.get().getUserService().getCurrentUser(), changes);
      changes.execute();

      Assert.assertNotNull(decRev);
      Assert.assertEquals(reviewTitle, decRev.getName());
      Assert.assertFalse(
         String.format("Decision Review artifact should not be dirty [%s]",
            Artifacts.getDirtyReport(((Artifact) decRev.getStoreObject()))),
         ((Artifact) decRev.getStoreObject()).isDirty());
      Assert.assertEquals(DecisionReviewState.Decision.getName(), decRev.getStateMgr().getCurrentStateName());
      Assert.assertEquals("UnAssigned", decRev.getStateMgr().getAssigneesStr());

   }

   @org.junit.Test
   public void testCreateNewDecisionReview__Base()  {
      AtsTestUtil.cleanupAndReset("DecisionReviewManagerTest - Base");
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      String reviewTitle = "Test Review - " + teamWf.getName();
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      IAtsDecisionReview decRev = AtsClientService.get().getReviewService().createNewDecisionReview(teamWf,
         ReviewBlockType.Commit, reviewTitle, TeamState.Implement.getName(), "description",
         AtsClientService.get().getReviewService().getDefaultDecisionReviewOptions(),
         Arrays.asList(AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Alex_Kay)), new Date(),
         AtsClientService.get().getUserService().getCurrentUser(), changes);
      changes.execute();

      Assert.assertNotNull(decRev);
      Assert.assertEquals(reviewTitle, decRev.getName());
      Assert.assertEquals(DecisionReviewState.Prepare.getName(), decRev.getStateMgr().getCurrentStateName());
      Assert.assertEquals("Alex Kay", decRev.getStateMgr().getAssigneesStr());
      Assert.assertEquals(TeamState.Implement.getName(),
         AtsClientService.get().getAttributeResolver().getSoleAttributeValue(decRev, AtsAttributeTypes.RelatedToState,
            ""));
      Assert.assertEquals(ReviewBlockType.Commit.name(),
         AtsClientService.get().getAttributeResolver().getSoleAttributeValue(decRev, AtsAttributeTypes.ReviewBlocks,
            ""));
   }

   @org.junit.Test
   public void testCreateNewDecisionReview__BaseUnassigned()  {
      AtsTestUtil.cleanupAndReset("DecisionReviewManagerTest - BaseUnassigned");
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      String reviewTitle = "Test Review - " + teamWf.getName();
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      IAtsDecisionReview decRev = AtsClientService.get().getReviewService().createNewDecisionReview(teamWf,
         ReviewBlockType.Commit, reviewTitle, TeamState.Implement.getName(), "description",
         AtsClientService.get().getReviewService().getDefaultDecisionReviewOptions(),
         Arrays.asList(AtsClientService.get().getUserServiceClient().getUserFromToken(SystemUser.UnAssigned)),
         new Date(), AtsClientService.get().getUserService().getCurrentUser(), changes);
      changes.execute();

      Assert.assertNotNull(decRev);
      Assert.assertEquals(reviewTitle, decRev.getName());
      Assert.assertEquals("UnAssigned", decRev.getStateMgr().getAssigneesStr());
   }

   @org.junit.Test
   public void testCreateNewDecisionReview__Sample()  {
      AtsTestUtil.cleanupAndReset("DecisionReviewManagerTest - Sample");
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      IAtsDecisionReview decRev = AtsClientService.get().getReviewService().createNewDecisionReview(teamWf,
         ReviewBlockType.Commit, true, new Date(), AtsClientService.get().getUserService().getCurrentUser(), changes);
      changes.execute();

      Assert.assertNotNull(decRev);
      Assert.assertEquals("Should we do this?  Yes will require followup, No will not", decRev.getName());
      Assert.assertEquals(DecisionReviewState.Prepare.getName(), decRev.getStateMgr().getCurrentStateName());
      Assert.assertEquals("Joe Smith", decRev.getStateMgr().getAssigneesStr());
      Assert.assertEquals(TeamState.Analyze.getName(),
         AtsClientService.get().getAttributeResolver().getSoleAttributeValue(decRev, AtsAttributeTypes.RelatedToState,
            ""));
      Assert.assertEquals(ReviewBlockType.Commit.name(),
         AtsClientService.get().getAttributeResolver().getSoleAttributeValue(decRev, AtsAttributeTypes.ReviewBlocks,
            ""));
   }

}
