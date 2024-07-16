/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.query;

import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.ide.world.search.ShowOpenWorkflowsByReviewType;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ShowOpenWorkflowsByArtifactTypeTest {

   @org.junit.Test
   public void testOpenDecisionReviews() throws Exception {
      ShowOpenWorkflowsByReviewType search =
         new ShowOpenWorkflowsByReviewType("Show Open " + WorkItemType.DecisionReview.name() + "s",
            WorkItemType.DecisionReview, false, false, AtsImage.DECISION_REVIEW);
      Collection<Artifact> results = search.performSearchGetResults();
      checkResults(7, AtsArtifactTypes.DecisionReview, results);
   }

   @org.junit.Test
   public void testWorkflowsWaitingDecisionReviews() throws Exception {
      ShowOpenWorkflowsByReviewType search =
         new ShowOpenWorkflowsByReviewType("Show Workflows Waiting " + WorkItemType.DecisionReview.name() + "s",
            WorkItemType.DecisionReview, false, true, AtsImage.DECISION_REVIEW);
      Collection<Artifact> results = search.performSearchGetResults();
      checkResults(7, AtsArtifactTypes.TeamWorkflow, results);
   }

   @org.junit.Test
   public void testOpenPeerReviews() throws Exception {
      ShowOpenWorkflowsByReviewType search =
         new ShowOpenWorkflowsByReviewType("Show Open " + WorkItemType.PeerReview.name() + "s", WorkItemType.PeerReview,
            false, false, AtsImage.PEER_REVIEW);
      Collection<Artifact> results = search.performSearchGetResults();
      checkResults(7, AtsArtifactTypes.PeerToPeerReview, results);
   }

   @org.junit.Test
   public void testWorkflowsWaitingPeerToPeerReviews() throws Exception {
      ShowOpenWorkflowsByReviewType search =
         new ShowOpenWorkflowsByReviewType("Show Workflows Waiting " + WorkItemType.PeerReview.name() + "s",
            WorkItemType.PeerReview, false, true, AtsImage.PEER_REVIEW);
      Collection<Artifact> results = search.performSearchGetResults();
      checkResults(6, AtsArtifactTypes.TeamWorkflow, results);
   }

   private void checkResults(int expectedCount, ArtifactTypeToken ofType, Collection<Artifact> results) {
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
