/*
 * Created on Jun 8, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.review;

import java.util.Date;
import junit.framework.Assert;
import org.eclipse.osee.ats.core.AtsTestUtil;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Test unit for {@link PeerToPeerReviewManager}
 * 
 * @author Donald G. Dunne
 */
public class PeerToPeerReviewManagerTest extends PeerToPeerReviewManager {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testCreateNewPeerToPeerReview__Base() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("PeerToPeerReviewManagerTest - Base");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();

      // create and transition peer review
      SkynetTransaction transaction = new SkynetTransaction(AtsUtilCore.getAtsBranch(), getClass().getSimpleName());
      String reviewTitle = "Test Review - " + teamArt.getName();

      PeerToPeerReviewArtifact peerArt =
         PeerToPeerReviewManager.createNewPeerToPeerReview(teamArt, reviewTitle,
            AtsTestUtil.getAnalyzeStateDef().getPageName(), new Date(), UserManager.getUser(SystemUser.OseeSystem),
            transaction);
      transaction.execute();

      Assert.assertNotNull(peerArt);
      Assert.assertFalse(
         String.format("PeerToPeer Review artifact should not be dirty [%s]", Artifacts.getDirtyReport(peerArt)),
         peerArt.isDirty());
      Assert.assertEquals(PeerToPeerReviewState.Prepare.getPageName(), peerArt.getCurrentStateName());
      Assert.assertEquals("Joe Smith", peerArt.getStateMgr().getAssigneesStr());
      Assert.assertEquals("OSEE System", peerArt.getCreatedBy().getName());
      Assert.assertEquals(AtsTestUtil.getAnalyzeStateDef().getPageName(),
         peerArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState));

   }

   @org.junit.Test
   public void testCreateNewPeerToPeerReview__Simple() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("PeerToPeerReviewManagerTest - Simple");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();

      // create and transition peer review
      SkynetTransaction transaction = new SkynetTransaction(AtsUtilCore.getAtsBranch(), getClass().getSimpleName());
      String reviewTitle = "Test Review - " + teamArt.getName();

      PeerToPeerReviewArtifact peerArt =
         PeerToPeerReviewManager.createNewPeerToPeerReview(teamArt, reviewTitle,
            AtsTestUtil.getAnalyzeStateDef().getPageName(), transaction);
      transaction.execute();

      Assert.assertNotNull(peerArt);
      Assert.assertFalse(
         String.format("PeerToPeer Review artifact should not be dirty [%s]", Artifacts.getDirtyReport(peerArt)),
         peerArt.isDirty());
      Assert.assertEquals(PeerToPeerReviewState.Prepare.getPageName(), peerArt.getCurrentStateName());
      Assert.assertEquals("Joe Smith", peerArt.getStateMgr().getAssigneesStr());
      Assert.assertEquals(AtsTestUtil.getAnalyzeStateDef().getPageName(),
         peerArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState));

   }
}
