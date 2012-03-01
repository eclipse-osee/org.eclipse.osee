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
package org.eclipse.osee.ats.core.client.review;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.core.client.team.TeamState;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.util.AtsUsers;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workdef.DecisionReviewOption;
import org.eclipse.osee.ats.core.workdef.ReviewBlockType;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.support.test.util.DemoUsers;
import org.junit.AfterClass;
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
   public void testGetDecisionReviewOptionsStr() throws OseeCoreException {
      Assert.assertEquals("Yes;Followup;<Joe Smith>\nNo;Completed;\n",
         DecisionReviewManager.getDecisionReviewOptionsString(DecisionReviewManager.getDefaultDecisionReviewOptions()));
   }

   @org.junit.Test
   public void testCreateNewDecisionReviewAndTransitionToDecision__Normal() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("DecisionReviewManagerTest - Normal");
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      List<DecisionReviewOption> options = new ArrayList<DecisionReviewOption>();
      options.add(new DecisionReviewOption(DecisionReviewState.Completed.getPageName(), false, null));
      options.add(new DecisionReviewOption(DecisionReviewState.Followup.getPageName(), true,
         Arrays.asList(AtsUsers.getUser().getUserId())));

      // create and transition decision review
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), getClass().getSimpleName());
      String reviewTitle = "Test Review - " + teamWf.getName();
      DecisionReviewArtifact decRev =
         DecisionReviewManager.createNewDecisionReviewAndTransitionToDecision(teamWf, reviewTitle, "my description",
            AtsTestUtil.getAnalyzeStateDef().getPageName(), ReviewBlockType.Transition, options,
            Arrays.asList(AtsUsers.getUser()), new Date(), AtsUsers.getUser(), transaction);
      transaction.execute();

      Assert.assertNotNull(decRev);
      Assert.assertFalse(
         String.format("Decision Review artifact should not be dirty [%s]", Artifacts.getDirtyReport(decRev)),
         decRev.isDirty());
      Assert.assertEquals(DecisionReviewState.Decision.getPageName(), decRev.getCurrentStateName());
      Assert.assertEquals("Joe Smith", decRev.getStateMgr().getAssigneesStr());

   }

   @org.junit.Test
   public void testCreateNewDecisionReviewAndTransitionToDecision__UnAssigned() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("DecisionReviewManagerTest - UnAssigned");
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      List<DecisionReviewOption> options = new ArrayList<DecisionReviewOption>();
      options.add(new DecisionReviewOption(DecisionReviewState.Completed.getPageName(), false, null));
      options.add(new DecisionReviewOption(DecisionReviewState.Followup.getPageName(), true,
         Arrays.asList(AtsUsers.getUser().getUserId())));

      // create and transition decision review
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), getClass().getSimpleName());
      String reviewTitle = "Test Review - " + teamWf.getName();
      DecisionReviewArtifact decRev =
         DecisionReviewManager.createNewDecisionReviewAndTransitionToDecision(teamWf, reviewTitle, "my description",
            AtsTestUtil.getAnalyzeStateDef().getPageName(), ReviewBlockType.Transition, options,
            Arrays.asList(AtsUsers.getUserFromToken(SystemUser.UnAssigned)), new Date(), AtsUsers.getUser(), transaction);
      transaction.execute();

      Assert.assertNotNull(decRev);
      Assert.assertEquals(reviewTitle, decRev.getName());
      Assert.assertFalse(
         String.format("Decision Review artifact should not be dirty [%s]", Artifacts.getDirtyReport(decRev)),
         decRev.isDirty());
      Assert.assertEquals(DecisionReviewState.Decision.getPageName(), decRev.getCurrentStateName());
      Assert.assertEquals("UnAssigned", decRev.getStateMgr().getAssigneesStr());

   }

   @org.junit.Test
   public void testCreateNewDecisionReview__Base() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("DecisionReviewManagerTest - Base");
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      String reviewTitle = "Test Review - " + teamWf.getName();
      DecisionReviewArtifact decRev =
         DecisionReviewManager.createNewDecisionReview(teamWf, ReviewBlockType.Commit, reviewTitle,
            TeamState.Implement.getPageName(), "description", DecisionReviewManager.getDefaultDecisionReviewOptions(),
            Arrays.asList(AtsUsers.getUserFromToken(DemoUsers.Alex_Kay)), new Date(), AtsUsers.getUser());

      Assert.assertNotNull(decRev);
      Assert.assertEquals(reviewTitle, decRev.getName());
      Assert.assertEquals(DecisionReviewState.Prepare.getPageName(), decRev.getCurrentStateName());
      Assert.assertEquals("Alex Kay", decRev.getStateMgr().getAssigneesStr());
      Assert.assertEquals(TeamState.Implement.getPageName(),
         decRev.getSoleAttributeValue(AtsAttributeTypes.RelatedToState));
      Assert.assertEquals(ReviewBlockType.Commit.name(), decRev.getSoleAttributeValue(AtsAttributeTypes.ReviewBlocks));
   }

   @org.junit.Test
   public void testCreateNewDecisionReview__BaseUnassigned() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("DecisionReviewManagerTest - BaseUnassigned");
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      String reviewTitle = "Test Review - " + teamWf.getName();
      DecisionReviewArtifact decRev =
         DecisionReviewManager.createNewDecisionReview(teamWf, ReviewBlockType.Commit, reviewTitle,
            TeamState.Implement.getPageName(), "description", DecisionReviewManager.getDefaultDecisionReviewOptions(),
            Arrays.asList(AtsUsers.getUserFromToken(SystemUser.UnAssigned)), new Date(), AtsUsers.getUser());

      Assert.assertNotNull(decRev);
      Assert.assertEquals(reviewTitle, decRev.getName());
      Assert.assertEquals("UnAssigned", decRev.getStateMgr().getAssigneesStr());
   }

   @org.junit.Test
   public void testCreateNewDecisionReview__Sample() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("DecisionReviewManagerTest - Sample");
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      DecisionReviewArtifact decRev =
         DecisionReviewManager.createNewDecisionReview(teamWf, ReviewBlockType.Commit, true, new Date(),
            AtsUsers.getUser());

      Assert.assertNotNull(decRev);
      Assert.assertEquals("Should we do this?  Yes will require followup, No will not", decRev.getName());
      Assert.assertEquals(DecisionReviewState.Prepare.getPageName(), decRev.getCurrentStateName());
      Assert.assertEquals("Joe Smith", decRev.getStateMgr().getAssigneesStr());
      Assert.assertEquals(TeamState.Analyze.getPageName(),
         decRev.getSoleAttributeValue(AtsAttributeTypes.RelatedToState));
      Assert.assertEquals(ReviewBlockType.Commit.name(),
         decRev.getSoleAttributeValue(AtsAttributeTypes.ReviewBlocks, ""));
   }

}
