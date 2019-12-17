/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.review.hooks;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.DecisionReviewOptions;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.ats.core.review.DecisionReviewState;

/**
 * Contributed via AtsWorkItemServiceImpl
 *
 * @author Donald G. Dunne
 */
public class AtsDecisionReviewPrepareWorkflowHook implements IAtsTransitionHook {

   public String getName() {
      return AtsDecisionReviewPrepareWorkflowHook.class.getSimpleName();
   }

   @Override
   public String getDescription() {
      return "Add validation of Decision Review options prior to transitioning from Prepare to Decision.";
   }

   @Override
   public void transitioning(TransitionResults results, IAtsWorkItem workItem, IStateToken fromState, IStateToken toState, Collection<? extends IAtsUser> toAssignees) {
      if (workItem instanceof IAtsDecisionReview && fromState.getName().equals(
         DecisionReviewState.Prepare.getName()) && toState.getName().equals(DecisionReviewState.Decision.getName())) {
         DecisionReviewOptions decOptions =
            new DecisionReviewOptions((IAtsDecisionReview) workItem, AtsApiService.get());
         decOptions.validateDecisionOptions(results);
      }
   }

}
