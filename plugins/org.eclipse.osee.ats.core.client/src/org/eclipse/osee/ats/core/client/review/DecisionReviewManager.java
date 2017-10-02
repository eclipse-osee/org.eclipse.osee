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

package org.eclipse.osee.ats.core.client.review;

import java.util.Arrays;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.framework.core.util.Result;

/**
 * Methods in support of Decision Reviews
 *
 * @author Donald G. Dunne
 */
public class DecisionReviewManager {

   protected DecisionReviewManager() {
      // private constructor
   }

   /**
    * Quickly transition to a state with minimal metrics and data entered. Should only be used for automated
    * transitioning for things such as developmental testing and demos.
    *
    * @param user User to transition to OR null if should use user of current state
    */
   public static Result transitionTo(DecisionReviewArtifact reviewArt, DecisionReviewState toState, IAtsUser user, boolean popup, IAtsChangeSet changes)  {
      Result result = Result.TrueResult;
      // If in Prepare state, set data and transition to Decision
      if (reviewArt.isInState(DecisionReviewState.Prepare)) {
         result = setPrepareStateData(popup, reviewArt, 100, 3, .2);
         if (result.isFalse()) {
            return result;
         }
         result =
            transitionToState(toState.getStateType(), popup, DecisionReviewState.Decision, reviewArt, user, changes);
         if (result.isFalse()) {
            return result;
         }
      }
      if (toState == DecisionReviewState.Decision) {
         return Result.TrueResult;
      }

      // If desired to transition to follow-up, then decision is false
      boolean decision = toState != DecisionReviewState.Followup;

      result = setDecisionStateData(popup, reviewArt, decision, 100, .2);
      if (result.isFalse()) {
         return result;
      }

      result = transitionToState(toState.getStateType(), popup, toState, reviewArt, user, changes);
      if (result.isFalse()) {
         return result;
      }
      return Result.TrueResult;
   }

   public static Result setPrepareStateData(boolean popup, DecisionReviewArtifact reviewArt, int statePercentComplete, double estimateHours, double stateHoursSpent)  {
      if (!reviewArt.isInState(DecisionReviewState.Prepare)) {
         Result result = new Result("Action not in Prepare state");
         if (result.isFalse() && popup) {
            return result;
         }
      }
      reviewArt.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, estimateHours);
      reviewArt.getStateMgr().updateMetrics(reviewArt.getStateDefinition(), stateHoursSpent, statePercentComplete, true,
         AtsClientService.get().getUserService().getCurrentUser());
      return Result.TrueResult;
   }

   public static Result transitionToState(StateType StateType, boolean popup, IStateToken toState, DecisionReviewArtifact reviewArt, IAtsUser user, IAtsChangeSet changes)  {
      TransitionHelper helper =
         new TransitionHelper("Transition to " + toState.getName(), Arrays.asList(reviewArt), toState.getName(),
            Arrays.asList(user == null ? reviewArt.getStateMgr().getAssignees().iterator().next() : user), null,
            changes, AtsClientService.get().getServices(), TransitionOption.OverrideAssigneeCheck);
      IAtsTransitionManager transitionMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = transitionMgr.handleAll();
      if (results.isEmpty()) {
         return Result.TrueResult;
      }
      return new Result("Transition Error %s", results.toString());
   }

   public static Result setDecisionStateData(boolean popup, DecisionReviewArtifact reviewArt, boolean decision, int statePercentComplete, double stateHoursSpent)  {
      if (!reviewArt.isInState(DecisionReviewState.Decision)) {
         Result result = new Result("Action not in Decision state");
         if (result.isFalse() && popup) {
            return result;
         }
      }
      reviewArt.setSoleAttributeValue(AtsAttributeTypes.Decision, decision ? "Yes" : "No");

      reviewArt.getStateMgr().updateMetrics(reviewArt.getStateDefinition(), stateHoursSpent, statePercentComplete, true,
         AtsClientService.get().getUserService().getCurrentUser());
      return Result.TrueResult;
   }

}
