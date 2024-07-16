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

import java.util.Date;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.review.AtsReviewServiceImpl;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Test unit for {@link AtsReviewServiceImpl}
 *
 * @author Donald G. Dunne
 */
public class PeerToPeerReviewManagerTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();
      SkynetTransaction transaction = TransactionManager.createTransaction(AtsApiService.get().getAtsBranch(),
         PeerToPeerReviewManagerTest.class.getSimpleName());
      for (Artifact art : ArtifactQuery.getArtifactListFromTypeAndName(AtsArtifactTypes.PeerToPeerReview,
         "PeerToPeerReviewManagerTest", AtsApiService.get().getAtsBranch(), QueryOption.CONTAINS_MATCH_OPTIONS)) {
         if (art.getName().contains("StandAlone")) {
            art.deleteAndPersist(transaction);
         }
      }
      transaction.execute();
   }

   @org.junit.Test
   public void testCreateNewPeerToPeerReview__Base() {
      AtsTestUtil.cleanupAndReset("PeerToPeerReviewManagerTest - Base");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();

      // create and transition peer review
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      String reviewTitle = "Test Review - " + teamArt.getName();

      PeerToPeerReviewArtifact peerArt =
         (PeerToPeerReviewArtifact) AtsApiService.get().getReviewService().createNewPeerToPeerReview(teamArt,
            reviewTitle, AtsTestUtil.getAnalyzeStateDef().getName(), new Date(),
            AtsApiService.get().getUserService().getCurrentUser(), changes);
      changes.execute();

      Assert.assertNotNull(peerArt);
      Assert.assertFalse(
         String.format("PeerToPeer Review artifact should not be dirty [%s]", Artifacts.getDirtyReport(peerArt)),
         peerArt.isDirty());
      Assert.assertEquals(PeerToPeerReviewState.Prepare.getName(), peerArt.getCurrentStateName());
      Assert.assertEquals(AtsCoreUsers.UNASSIGNED_USER.getName(), peerArt.getAssigneesStr());
      Assert.assertEquals(DemoUsers.Joe_Smith.getName(), peerArt.getCreatedBy().getName());
      Assert.assertEquals(AtsTestUtil.getAnalyzeStateDef().getName(),
         peerArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState));

   }

   @org.junit.Test
   public void testCreateNewPeerToPeerReview__Simple() {
      AtsTestUtil.cleanupAndReset("PeerToPeerReviewManagerTest - Simple");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();

      // create and transition peer review
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      String reviewTitle = "Test Review - " + teamArt.getName();

      PeerToPeerReviewArtifact peerArt =
         (PeerToPeerReviewArtifact) AtsApiService.get().getReviewService().createNewPeerToPeerReview(teamArt,
            reviewTitle, AtsTestUtil.getAnalyzeStateDef().getName(), changes);
      changes.execute();

      Assert.assertNotNull(peerArt);
      Assert.assertFalse(
         String.format("PeerToPeer Review artifact should not be dirty [%s]", Artifacts.getDirtyReport(peerArt)),
         peerArt.isDirty());
      Assert.assertEquals(PeerToPeerReviewState.Prepare.getName(), peerArt.getCurrentStateName());
      Assert.assertEquals(AtsCoreUsers.UNASSIGNED_USER.getName(), peerArt.getAssigneesStr());
      Assert.assertEquals(AtsTestUtil.getAnalyzeStateDef().getName(),
         peerArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState));

   }

   @org.junit.Test
   public void testCreateNewPeerToPeerReview__StandAlone() {
      AtsTestUtil.cleanupAndReset("PeerToPeerReviewManagerTest - StandAlone");
      IAtsActionableItem testAi = AtsTestUtil.getTestAi();

      // create and transition peer review
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      String reviewTitle = "Test Review - " + testAi;

      PeerToPeerReviewArtifact peerArt =
         (PeerToPeerReviewArtifact) AtsApiService.get().getReviewService().createNewPeerToPeerReview(testAi,
            reviewTitle, null, new Date(), AtsApiService.get().getUserService().getCurrentUser(), changes);
      changes.execute();

      Assert.assertNotNull(peerArt);
      Assert.assertFalse(
         String.format("PeerToPeer Review artifact should not be dirty [%s]", Artifacts.getDirtyReport(peerArt)),
         peerArt.isDirty());
      Assert.assertEquals(PeerToPeerReviewState.Prepare.getName(), peerArt.getCurrentStateName());
      Assert.assertEquals(AtsCoreUsers.UNASSIGNED_USER.getName(), peerArt.getAssigneesStr());
      Assert.assertEquals(AtsTestUtil.getTestAi(),
         peerArt.getSoleAttributeValue(AtsAttributeTypes.ActionableItemReference));
   }
}
