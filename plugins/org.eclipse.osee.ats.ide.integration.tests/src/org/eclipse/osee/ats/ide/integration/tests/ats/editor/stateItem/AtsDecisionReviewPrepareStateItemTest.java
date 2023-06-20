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

package org.eclipse.osee.ats.ide.integration.tests.ats.editor.stateItem;

import java.util.Arrays;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.DecisionReviewState;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.review.hooks.AtsDecisionReviewPrepareWorkItemHook;
import org.eclipse.osee.ats.core.test.AtsTestUtilCore.AtsTestUtilState;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.workflow.review.DecisionReviewArtifact;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link AtsDecisionReviewPrepareWorkItemHook}
 *
 * @author Donald G. Dunne
 */
public class AtsDecisionReviewPrepareStateItemTest {

   @Test
   public void testTransitioning() {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      DecisionReviewArtifact decRevArt =
         AtsTestUtil.getOrCreateDecisionReview(ReviewBlockType.None, AtsTestUtilState.Analyze, changes);
      // set valid options
      String decisionOptionStr = AtsApiService.get().getReviewService().getDecisionReviewOptionsString(
         AtsApiService.get().getReviewService().getDefaultDecisionReviewOptions());
      decRevArt.setSoleAttributeValue(AtsAttributeTypes.DecisionReviewOptions, decisionOptionStr);
      changes.execute();

      IStateToken fromState = decRevArt.getWorkDefinition().getStateByName(DecisionReviewState.Prepare.getName());
      IStateToken toState = decRevArt.getWorkDefinition().getStateByName(DecisionReviewState.Decision.getName());

      // make call to state item that should set options based on artifact's attribute value
      AtsDecisionReviewPrepareWorkItemHook stateItem = new AtsDecisionReviewPrepareWorkItemHook();
      TransitionResults results = new TransitionResults();
      stateItem.transitioning(results, decRevArt, fromState, toState,
         Arrays.asList(AtsApiService.get().getUserService().getCurrentUser()),
         AtsApiService.get().getUserService().getCurrentUser(), AtsApiService.get());

      // verify no errors
      Assert.assertTrue(results.toString(), results.isEmpty());

      // set invalid options; NoState is invalid, should only be Completed or FollowUp
      decisionOptionStr = decisionOptionStr.replaceFirst("Completed", "NoState");
      decRevArt.setSoleAttributeValue(AtsAttributeTypes.DecisionReviewOptions, decisionOptionStr);
      decRevArt.persist(getClass().getSimpleName());
      stateItem.transitioning(results, decRevArt, fromState, toState,
         Arrays.asList(AtsApiService.get().getUserService().getCurrentUser()),
         AtsApiService.get().getUserService().getCurrentUser(), AtsApiService.get());
      Assert.assertTrue(results.contains("Invalid Decision Option"));

   }
}
