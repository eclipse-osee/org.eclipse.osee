/*
 * Created on Nov 24, 2015
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.client.integration.tests.ats.world.search;

import java.util.Collection;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.world.search.ShowOpenWorkflowsByArtifactType;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class ShowOpenWorkflowsByArtifactTypeTest {

   @org.junit.Test
   public void testOpenDecisionReviews() throws Exception {
      ShowOpenWorkflowsByArtifactType search =
         new ShowOpenWorkflowsByArtifactType("Show Open " + AtsArtifactTypes.DecisionReview.getName() + "s",
            AtsArtifactTypes.DecisionReview, false, false, AtsImage.DECISION_REVIEW);
      Collection<Artifact> results = search.performSearchGetResults();
      checkResults(5, AtsArtifactTypes.DecisionReview, results);
   }

   @org.junit.Test
   public void testWorkflowsWaitingDecisionReviews() throws Exception {
      ShowOpenWorkflowsByArtifactType search = new ShowOpenWorkflowsByArtifactType(
         "Show Workflows Waiting " + AtsArtifactTypes.DecisionReview.getName() + "s", AtsArtifactTypes.DecisionReview,
         false, true, AtsImage.DECISION_REVIEW);
      Collection<Artifact> results = search.performSearchGetResults();
      checkResults(5, AtsArtifactTypes.TeamWorkflow, results);
   }

   @org.junit.Test
   public void testOpenPeerReviews() throws Exception {
      ShowOpenWorkflowsByArtifactType search =
         new ShowOpenWorkflowsByArtifactType("Show Open " + AtsArtifactTypes.PeerToPeerReview.getName() + "s",
            AtsArtifactTypes.PeerToPeerReview, false, false, AtsImage.PEER_REVIEW);
      Collection<Artifact> results = search.performSearchGetResults();
      checkResults(5, AtsArtifactTypes.PeerToPeerReview, results);
   }

   @org.junit.Test
   public void testWorkflowsWaitingPeerToPeerReviews() throws Exception {
      ShowOpenWorkflowsByArtifactType search = new ShowOpenWorkflowsByArtifactType(
         "Show Workflows Waiting " + AtsArtifactTypes.PeerToPeerReview.getName() + "s",
         AtsArtifactTypes.PeerToPeerReview, false, true, AtsImage.PEER_REVIEW);
      Collection<Artifact> results = search.performSearchGetResults();
      checkResults(4, AtsArtifactTypes.TeamWorkflow, results);
   }

   private void checkResults(int expectedCount, IArtifactType ofType, Collection<Artifact> results) {
      org.junit.Assert.assertEquals(expectedCount, results.size());
      int count = 0;
      for (Artifact art : results) {
         if (art.isOfType(ofType)) {
            count++;
         }
      }
      org.junit.Assert.assertEquals("Not all results are of correct type", expectedCount, count);
   }

}
