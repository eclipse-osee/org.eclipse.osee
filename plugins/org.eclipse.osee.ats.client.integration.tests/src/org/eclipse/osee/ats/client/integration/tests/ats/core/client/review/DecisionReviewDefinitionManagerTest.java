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
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.StateEventType;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.workflow.transition.MockTransitionHelper;
import org.eclipse.osee.ats.core.client.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.core.client.review.DecisionReviewDefinitionManager;
import org.eclipse.osee.ats.core.client.review.DecisionReviewManager;
import org.eclipse.osee.ats.core.client.review.DecisionReviewState;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.mocks.MockDecisionReviewDefinition;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Test unit for {@link DecisionReviewDefinitionManager}
 * 
 * @author Donald G. Dunne
 */
public class DecisionReviewDefinitionManagerTest extends DecisionReviewDefinitionManager {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testCreateDecisionReviewDuringTransition_ToDecision() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("DecisionReviewDefinitionManagerTest - ToDecision");

      // configure WorkDefinition to create a new Review on transition to Implement
      IAtsStateDefinition implement = AtsTestUtil.getImplementStateDef();

      MockDecisionReviewDefinition revDef = new MockDecisionReviewDefinition("Create New on Implement");
      revDef.setAutoTransitionToDecision(true);
      revDef.setBlockingType(ReviewBlockType.Transition);
      revDef.setDescription("the description");
      revDef.setRelatedToState(implement.getName());
      revDef.setStateEventType(StateEventType.TransitionTo);
      revDef.setReviewTitle("This is my review title");
      revDef.getOptions().addAll(DecisionReviewManager.getDefaultDecisionReviewOptions());
      revDef.getAssignees().add(SystemUser.UnAssigned.getUserId());

      implement.getDecisionReviews().add(revDef);

      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      Assert.assertEquals("No reviews should be present", 0, ReviewManager.getReviews(teamArt).size());

      AtsChangeSet changes = new AtsChangeSet(getClass().getSimpleName());
      MockTransitionHelper helper =
         new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt), implement.getName(),
            Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, changes, TransitionOption.None);
      TransitionManager transitionMgr = new TransitionManager(helper);
      TransitionResults results = transitionMgr.handleAll();
      changes.execute();

      Assert.assertFalse(teamArt.isDirty());
      Assert.assertFalse(teamArt.getLog().isDirty());
      Assert.assertTrue(results.toString(), results.isEmpty());

      Assert.assertEquals("One review should be present", 1, ReviewManager.getReviews(teamArt).size());
      DecisionReviewArtifact decArt = (DecisionReviewArtifact) ReviewManager.getReviews(teamArt).iterator().next();

      Assert.assertEquals(DecisionReviewState.Decision.getName(), decArt.getCurrentStateName());
      Assert.assertEquals("UnAssigned", decArt.getStateMgr().getAssigneesStr());
      Assert.assertEquals(ReviewBlockType.Transition.name(),
         decArt.getSoleAttributeValue(AtsAttributeTypes.ReviewBlocks));
      Assert.assertEquals("This is my review title", decArt.getName());
      Assert.assertEquals("the description", decArt.getSoleAttributeValue(AtsAttributeTypes.Description));
      Assert.assertEquals(implement.getName(), decArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState));

      AtsTestUtil.validateArtifactCache();
   }

   @org.junit.Test
   public void testCreateDecisionReviewDuringTransition_Prepare() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("DecisionReviewDefinitionManagerTest - Prepare");

      // configure WorkDefinition to create a new Review on transition to Implement
      IAtsStateDefinition implement = AtsTestUtil.getImplementStateDef();

      MockDecisionReviewDefinition revDef = new MockDecisionReviewDefinition("Create New on Implement");
      revDef.setAutoTransitionToDecision(false);
      revDef.setBlockingType(ReviewBlockType.Commit);
      revDef.setReviewTitle("This is the title");
      revDef.setDescription("the description");
      revDef.setRelatedToState(implement.getName());
      revDef.setStateEventType(StateEventType.TransitionTo);
      revDef.getOptions().addAll(DecisionReviewManager.getDefaultDecisionReviewOptions());

      implement.getDecisionReviews().add(revDef);

      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      Assert.assertEquals("No reviews should be present", 0, ReviewManager.getReviews(teamArt).size());

      AtsChangeSet changes = new AtsChangeSet(getClass().getSimpleName());
      MockTransitionHelper helper =
         new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt), implement.getName(),
            Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, changes, TransitionOption.None);
      TransitionManager transitionMgr = new TransitionManager(helper);
      TransitionResults results = transitionMgr.handleAll();
      changes.execute();

      Assert.assertTrue(results.toString(), results.isEmpty());

      Assert.assertEquals("One review should be present", 1, ReviewManager.getReviews(teamArt).size());
      DecisionReviewArtifact decArt = (DecisionReviewArtifact) ReviewManager.getReviews(teamArt).iterator().next();

      Assert.assertEquals(DecisionReviewState.Prepare.getName(), decArt.getCurrentStateName());
      // Current user assigned if non specified
      Assert.assertEquals("Joe Smith", decArt.getStateMgr().getAssigneesStr());
      Assert.assertEquals(ReviewBlockType.Commit.name(), decArt.getSoleAttributeValue(AtsAttributeTypes.ReviewBlocks));
      Assert.assertEquals("This is the title", decArt.getName());
      Assert.assertEquals("the description", decArt.getSoleAttributeValue(AtsAttributeTypes.Description));
      Assert.assertEquals(implement.getName(), decArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState));

      AtsTestUtil.validateArtifactCache();
   }

}
