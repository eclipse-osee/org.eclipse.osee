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
package org.eclipse.osee.ats.ide.integration.tests.ats.editor.stateItem;

import java.util.Arrays;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.ide.editor.stateItem.AtsDecisionReviewPrepareStateItem;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil.AtsTestUtilState;
import org.eclipse.osee.ats.ide.workflow.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.review.DecisionReviewState;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link AtsDecisionReviewPrepareStateItem}
 *
 * @author Donald G. Dunne
 */
public class AtsDecisionReviewPrepareStateItemTest {

   @Test
   public void testTransitioning() {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      DecisionReviewArtifact decRevArt =
         AtsTestUtil.getOrCreateDecisionReview(ReviewBlockType.None, AtsTestUtilState.Analyze, changes);
      // set valid options
      String decisionOptionStr = AtsClientService.get().getReviewService().getDecisionReviewOptionsString(
         AtsClientService.get().getReviewService().getDefaultDecisionReviewOptions());
      decRevArt.setSoleAttributeValue(AtsAttributeTypes.DecisionReviewOptions, decisionOptionStr);
      changes.execute();

      IStateToken fromState = decRevArt.getWorkDefinition().getStateByName(DecisionReviewState.Prepare.getName());
      IStateToken toState = decRevArt.getWorkDefinition().getStateByName(DecisionReviewState.Decision.getName());

      // make call to state item that should set options based on artifact's attribute value
      AtsDecisionReviewPrepareStateItem stateItem = new AtsDecisionReviewPrepareStateItem();
      TransitionResults results = new TransitionResults();
      stateItem.transitioning(results, decRevArt, fromState, toState,
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()));

      // verify no errors
      Assert.assertTrue(results.toString(), results.isEmpty());

      // set invalid options; NoState is invalid, should only be Completed or FollowUp
      decisionOptionStr = decisionOptionStr.replaceFirst("Completed", "NoState");
      decRevArt.setSoleAttributeValue(AtsAttributeTypes.DecisionReviewOptions, decisionOptionStr);
      decRevArt.persist(getClass().getSimpleName());
      stateItem.transitioning(results, decRevArt, fromState, toState,
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()));
      Assert.assertTrue(results.contains("Invalid Decision Option"));

   }
}
