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

import java.util.Arrays;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.workflow.transition.MockTransitionHelper;
import org.eclipse.osee.ats.core.client.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.core.client.review.DecisionReviewDefinitionManager;
import org.eclipse.osee.ats.core.client.review.DecisionReviewState;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Test unit for {@link DecisionReviewDefinitionManager}
 *
 * @author Donald G. Dunne
 */
public class DecisionReviewDefinitionManagerTest extends DecisionReviewDefinitionManager {

   public static String DECISION_WORK_DEF_NAME = "WorkDef_Team_DecisionReviewDefinitionManagerTest_toDecision";
   public static String PREPARE_WORK_DEF_NAME = "WorkDef_Team_DecisionReviewDefinitionManagerTest_Prepare";

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testCreateDecisionReviewDuringTransition_ToDecision() {
      AtsTestUtil.cleanupAndReset("DecisionReviewDefinitionManagerTest - ToDecision");

      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      teamArt.setSoleAttributeValue(AtsAttributeTypes.WorkflowDefinition, DECISION_WORK_DEF_NAME);
      teamArt.persist("DecisionReviewDefinitionManagerTest - ToDecision");

      AtsClientService.get().getWorkDefinitionService().clearCaches();

      Assert.assertEquals("Implement State should have a single decision review definition", 1,
         teamArt.getWorkDefinition().getStateByName(TeamState.Implement.getName()).getDecisionReviews().size());
      Assert.assertEquals("No reviews should be present", 0, ReviewManager.getReviews(teamArt).size());

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      MockTransitionHelper helper = new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
         TeamState.Implement.getName(), Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null,
         changes, TransitionOption.None);
      IAtsTransitionManager transitionMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = transitionMgr.handleAllAndPersist();

      Assert.assertTrue(results.toString(), results.isEmpty());
      Assert.assertFalse(teamArt.isDirty());
      Assert.assertFalse(teamArt.getLog().isDirty());

      Assert.assertEquals("One review should be present", 1, ReviewManager.getReviews(teamArt).size());
      DecisionReviewArtifact decArt = (DecisionReviewArtifact) ReviewManager.getReviews(teamArt).iterator().next();

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

      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      teamArt.setSoleAttributeValue(AtsAttributeTypes.WorkflowDefinition, PREPARE_WORK_DEF_NAME);
      teamArt.persist("DecisionReviewDefinitionManagerTest - Prepare");

      AtsClientService.get().getWorkDefinitionService().clearCaches();

      Assert.assertEquals("No reviews should be present", 0, ReviewManager.getReviews(teamArt).size());

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      MockTransitionHelper helper = new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
         TeamState.Implement.getName(), Arrays.asList(AtsClientService.get().getUserService().getCurrentUser

         ()), null, changes, TransitionOption.None);
      IAtsTransitionManager transitionMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = transitionMgr.handleAllAndPersist();

      Assert.assertTrue(results.toString(), results.isEmpty());

      Assert.assertEquals("One review should be present", 1, ReviewManager.getReviews(teamArt).size());
      DecisionReviewArtifact decArt = (DecisionReviewArtifact) ReviewManager.getReviews(teamArt).iterator().next();

      Assert.assertEquals(DecisionReviewState.Prepare.getName(), decArt.getCurrentStateName());
      // Current user assigned if non specified
      Assert.assertEquals("Joe Smith", decArt.getStateMgr().getAssigneesStr());
      Assert.assertEquals(ReviewBlockType.Commit.name(), decArt.getSoleAttributeValue(AtsAttributeTypes.ReviewBlocks));
      Assert.assertEquals("This is the title", decArt.getName());
      Assert.assertEquals("the description", decArt.getSoleAttributeValue(AtsAttributeTypes.Description));
      Assert.assertEquals(TeamState.Implement.getName(),
         decArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState));

      AtsTestUtil.validateArtifactCache();
   }

}
