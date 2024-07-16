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

import java.util.Arrays;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.review.PeerReviewOnTransitionToHook;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.transition.TestTransitionData;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.review.ReviewManager;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Test unit for {@link PeerReviewOnTransitionToHook}
 *
 * @author Donald G. Dunne
 */
public class PeerReviewDefinitionManagerTest extends PeerReviewOnTransitionToHook {

   public static AtsWorkDefinitionToken PeerWorkDefId =
      AtsWorkDefinitionToken.valueOf(162205335L, "WorkDef_Team_PeerReviewDefinitionManagerTest_Transition");

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testCreatePeerReviewDuringTransition() {
      AtsTestUtil.cleanupAndReset("PeerReviewDefinitionManagerTest");

      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      AtsApiService.get().getWorkDefinitionService().setWorkDefinitionAttrs(teamWf, PeerWorkDefId, changes);
      changes.execute();

      teamWf.persist("PeerReviewDefinitionManagerTest");

      Assert.assertEquals("Implement State should have a single peer review definition", 1,
         teamWf.getWorkDefinition().getStateByName(TeamState.Implement.getName()).getPeerReviews().size());
      Assert.assertEquals("No reviews should be present", 0, ReviewManager.getReviews(teamWf).size());

      TestTransitionData helper =
         new TestTransitionData(getClass().getSimpleName(), Arrays.asList(teamWf), TeamState.Implement.getName(),
            Arrays.asList(AtsApiService.get().getUserService().getCurrentUser()), null, null, TransitionOption.None);
      TransitionResults results = AtsApiService.get().getWorkItemService().transition(helper);

      Assert.assertTrue(results.toString(), results.isEmpty());

      Assert.assertEquals("One review should be present", 1, ReviewManager.getReviews(teamWf).size());
      PeerToPeerReviewArtifact decArt = (PeerToPeerReviewArtifact) ReviewManager.getReviews(teamWf).iterator().next();

      Assert.assertEquals(PeerToPeerReviewState.Prepare.getName(), decArt.getCurrentStateName());
      Assert.assertEquals("UnAssigned", decArt.getStateMgr().getAssigneesStr());
      Assert.assertEquals(ReviewBlockType.Transition.name(),
         decArt.getSoleAttributeValue(AtsAttributeTypes.ReviewBlocks));
      Assert.assertEquals("This is my review title", decArt.getName());
      Assert.assertEquals("the description", decArt.getSoleAttributeValue(AtsAttributeTypes.Description));
      Assert.assertEquals(TeamState.Implement.getName(),
         decArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState));

      AtsTestUtil.validateArtifactCache();
   }

}
