/*
 * Created on Oct 21, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.util;

import junit.framework.Assert;
import org.eclipse.osee.ats.core.AtsTestUtil;
import org.eclipse.osee.ats.core.AtsTestUtil.AtsTestUtilState;
import org.eclipse.osee.ats.core.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.core.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.workdef.ReviewBlockType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class CopyActionDetailsTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws OseeCoreException {
      AtsTestUtil.cleanup();
   }

   @Test
   public void testGetDetailsStringForTeamWf() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      String str = new CopyActionDetails(AtsTestUtil.getTeamWf()).getDetailsString();
      Assert.assertEquals(
         "\"Team Workflow\" - " + AtsTestUtil.getTeamWf().getHumanReadableId() + " - \"AtsTestUtil - Team WF [CopyActionDetailsTest]\"",
         str);
   }

   @Test
   public void testGetDetailsStringForTask() throws OseeCoreException {
      String str = new CopyActionDetails(AtsTestUtil.getOrCreateTask()).getDetailsString();
      Assert.assertEquals(
         "\"Task\" - " + AtsTestUtil.getOrCreateTask().getHumanReadableId() + " - \"AtsTestUtil - Task [CopyActionDetailsTest]\"",
         str);
   }

   @Test
   public void testGetDetailsStringForDecisionReview() throws OseeCoreException {
      DecisionReviewArtifact review =
         AtsTestUtil.getOrCreateDecisionReview(ReviewBlockType.Commit, AtsTestUtilState.Analyze);
      String str = new CopyActionDetails(review).getDetailsString();
      Assert.assertEquals(
         "\"Decision Review\" - " + review.getHumanReadableId() + " - \"AtsTestUtil Test Decision Review\"", str);
      review.persist(getClass().getSimpleName());
   }

   @Test
   public void testGetDetailsStringForPeerReview() throws OseeCoreException {
      PeerToPeerReviewArtifact review =
         AtsTestUtil.getOrCreatePeerReview(ReviewBlockType.None, AtsTestUtilState.Analyze, null);
      String str = new CopyActionDetails(review).getDetailsString();
      Assert.assertEquals(
         "\"PeerToPeer Review\" - " + review.getHumanReadableId() + " - \"AtsTestUtil Test Peer Review\"", str);
      review.persist(getClass().getSimpleName());
   }
}
