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
import org.eclipse.osee.ats.api.review.DecisionReviewState;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.review.DecisionReviewOnTransitionToHook;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workdef.DemoWorkDefinitionTokens;
import org.eclipse.osee.ats.ide.integration.tests.ats.workdef.WorkDefTeamDecisionReviewDefinitionManagerTestPrepare;
import org.eclipse.osee.ats.ide.integration.tests.ats.workdef.WorkDefTeamDecisionReviewDefinitionManagerTesttoDecision;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.transition.TestTransitionData;
import org.eclipse.osee.ats.ide.workflow.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.review.ReviewManager;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Test unit for {@link DecisionReviewOnTransitionToHook}
 *
 * @author Donald G. Dunne
 */
public class DecisionReviewDefinitionManagerTest extends DecisionReviewOnTransitionToHook {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();
      AtsApiService.get().getWorkDefinitionService().addWorkDefinition(
         new WorkDefTeamDecisionReviewDefinitionManagerTesttoDecision());
      AtsApiService.get().getWorkDefinitionService().addWorkDefinition(
         new WorkDefTeamDecisionReviewDefinitionManagerTestPrepare());
   }

   @org.junit.Test
   public void testCreateDecisionReviewDuringTransition_ToDecision() {
      AtsTestUtil.cleanupAndReset("DecisionReviewDefinitionManagerTest - ToDecision");

      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      AtsApiService.get().getWorkDefinitionService().setWorkDefinitionAttrs(teamWf,
         DemoWorkDefinitionTokens.WorkDef_Team_DecisionReviewDefinitionManagerTest_toDecision, changes);
      changes.execute();

      Assert.assertEquals("Implement State should have a single decision review definition", 1,
         teamWf.getWorkDefinition().getStateByName(TeamState.Implement.getName()).getDecisionReviews().size());
      Assert.assertEquals("No reviews should be present", 0, ReviewManager.getReviews(teamWf).size());

      TestTransitionData helper =
         new TestTransitionData(getClass().getSimpleName(), Arrays.asList(teamWf), TeamState.Implement.getName(),
            Arrays.asList(AtsApiService.get().getUserService().getCurrentUser()), null, null, TransitionOption.None);
      TransitionResults results = AtsApiService.get().getWorkItemService().transition(helper);

      Assert.assertTrue(results.toString(), results.isEmpty());
      Assert.assertFalse(teamWf.isDirty());
      Assert.assertFalse(teamWf.getLog().isDirty());

      Assert.assertEquals("One review should be present", 1, ReviewManager.getReviews(teamWf).size());
      DecisionReviewArtifact decArt = (DecisionReviewArtifact) ReviewManager.getReviews(teamWf).iterator().next();

      Assert.assertEquals(DecisionReviewState.Decision.getName(), decArt.getCurrentStateName());
      Assert.assertEquals("UnAssigned", decArt.getStateMgr().getAssigneesStr());
      Assert.assertEquals(ReviewBlockType.Transition.name(),
         decArt.getSoleAttributeValue(AtsAttributeTypes.ReviewBlocks));
      Assert.assertEquals("This is my review title", decArt.getName());
      Assert.assertEquals("the description", decArt.getSoleAttributeValue(AtsAttributeTypes.Description));
      Assert.assertEquals(TeamState.Implement.getName(),
         decArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState));

      AtsTestUtil.validateArtifactCache();
   }

   @org.junit.Test
   public void testCreateDecisionReviewDuringTransition_Prepare() {
      AtsTestUtil.cleanupAndReset("DecisionReviewDefinitionManagerTest - Prepare");

      IAtsTeamWorkflow teamWf = AtsTestUtil.getTeamWf();
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      AtsApiService.get().getWorkDefinitionService().setWorkDefinitionAttrs(teamWf,
         DemoWorkDefinitionTokens.WorkDef_Team_DecisionReviewDefinitionManagerTest_Prepare, changes);
      changes.execute();

      Assert.assertEquals("No reviews should be present", 0, ReviewManager.getReviews(teamWf).size());

      TestTransitionData helper = new TestTransitionData(getClass().getSimpleName(),
         Arrays.asList((TeamWorkFlowArtifact) teamWf.getStoreObject()), TeamState.Implement.getName(),
         Arrays.asList(AtsApiService.get().getUserService().getCurrentUser

         ()), null, null, TransitionOption.None);
      TransitionResults results = AtsApiService.get().getWorkItemService().transition(helper);

      Assert.assertTrue(results.toString(), results.isEmpty());

      Assert.assertEquals("One review should be present", 1, ReviewManager.getReviews(teamWf).size());
      DecisionReviewArtifact decArt = (DecisionReviewArtifact) ReviewManager.getReviews(teamWf).iterator().next();

      Assert.assertEquals(DecisionReviewState.Prepare.getName(), decArt.getCurrentStateName());
      // Current user assigned if non specified
      Assert.assertEquals(DemoUsers.Joe_Smith.getName(), decArt.getStateMgr().getAssigneesStr());
      Assert.assertEquals(ReviewBlockType.Commit.name(), decArt.getSoleAttributeValue(AtsAttributeTypes.ReviewBlocks));
      Assert.assertEquals("This is the title", decArt.getName());
      Assert.assertEquals("the description", decArt.getSoleAttributeValue(AtsAttributeTypes.Description));
      Assert.assertEquals(TeamState.Implement.getName(),
         decArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState));

      AtsTestUtil.validateArtifactCache();
   }

}
