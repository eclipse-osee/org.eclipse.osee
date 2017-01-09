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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewOption;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workdef.SimpleDecisionReviewOption;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;

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
   public static Result transitionTo(DecisionReviewArtifact reviewArt, DecisionReviewState toState, IAtsUser user, boolean popup, IAtsChangeSet changes) throws OseeCoreException {
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

   public static Result setPrepareStateData(boolean popup, DecisionReviewArtifact reviewArt, int statePercentComplete, double estimateHours, double stateHoursSpent) throws OseeCoreException {
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

   public static Result transitionToState(StateType StateType, boolean popup, IStateToken toState, DecisionReviewArtifact reviewArt, IAtsUser user, IAtsChangeSet changes) throws OseeCoreException {
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

   public static Result setDecisionStateData(boolean popup, DecisionReviewArtifact reviewArt, boolean decision, int statePercentComplete, double stateHoursSpent) throws OseeCoreException {
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

   public static DecisionReviewArtifact createNewDecisionReviewAndTransitionToDecision(TeamWorkFlowArtifact teamArt, String reviewTitle, String description, String againstState, ReviewBlockType reviewBlockType, Collection<IAtsDecisionReviewOption> options, List<? extends IAtsUser> assignees, Date createdDate, IAtsUser createdBy, IAtsChangeSet changes) throws OseeCoreException {
      DecisionReviewArtifact decRev = createNewDecisionReview(teamArt, reviewBlockType, reviewTitle, againstState,
         description, options, assignees, createdDate, createdBy, changes);
      changes.add(decRev);

      // transition to decision
      TransitionHelper helper =
         new TransitionHelper("Transition to Decision", Arrays.asList(decRev), DecisionReviewState.Decision.getName(),
            assignees, null, changes, AtsClientService.get().getServices(), TransitionOption.OverrideAssigneeCheck);
      IAtsTransitionManager transitionMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = transitionMgr.handleAll();

      if (!results.isEmpty()) {
         throw new OseeStateException("Error auto-transitioning review %s to Decision state. Results [%s]",
            decRev.toStringWithId(), results.toString());
      }
      // ensure assignees are as requested
      decRev.getStateMgr().setAssignees(assignees);
      changes.add(decRev);
      return decRev;
   }

   public static DecisionReviewArtifact createNewDecisionReview(TeamWorkFlowArtifact teamArt, ReviewBlockType reviewBlockType, boolean againstCurrentState, Date createdDate, IAtsUser createdBy, IAtsChangeSet changes) throws OseeCoreException {
      return createNewDecisionReview(teamArt, reviewBlockType,
         "Should we do this?  Yes will require followup, No will not",
         againstCurrentState ? teamArt.getStateMgr().getCurrentStateName() : null,
         "Enter description of the decision, if any", getDefaultDecisionReviewOptions(), null, createdDate, createdBy,
         changes);
   }

   public static List<IAtsDecisionReviewOption> getDefaultDecisionReviewOptions() throws OseeCoreException {
      List<IAtsDecisionReviewOption> options = new ArrayList<>();
      options.add(new SimpleDecisionReviewOption("Yes", true,
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser().getUserId())));
      options.add(new SimpleDecisionReviewOption("No", false, null));
      return options;
   }

   public static String getDecisionReviewOptionsString(Collection<IAtsDecisionReviewOption> options) {
      StringBuffer sb = new StringBuffer();
      for (IAtsDecisionReviewOption opt : options) {
         sb.append(opt.getName());
         sb.append(";");
         sb.append(opt.isFollowupRequired() ? "Followup" : "Completed");
         sb.append(";");
         for (String userId : opt.getUserIds()) {
            sb.append("<" + userId + ">");
         }
         sb.append("\n");
      }
      return sb.toString();
   }

   public static DecisionReviewArtifact createNewDecisionReview(TeamWorkFlowArtifact teamArt, ReviewBlockType reviewBlockType, String title, String relatedToState, String description, Collection<IAtsDecisionReviewOption> options, List<? extends IAtsUser> assignees, Date createdDate, IAtsUser createdBy, IAtsChangeSet changes) throws OseeCoreException {
      DecisionReviewArtifact decRev =
         (DecisionReviewArtifact) ArtifactTypeManager.addArtifact(AtsArtifactTypes.DecisionReview,
            AtsClientService.get().getAtsBranch(), title);

      teamArt.addRelation(AtsRelationTypes.TeamWorkflowToReview_Review, decRev);
      AtsClientService.get().getUtilService().setAtsId(AtsClientService.get().getSequenceProvider(), decRev,
         decRev.getParentTeamWorkflow().getTeamDefinition(), changes);

      // Initialize state machine
      decRev.initializeNewStateMachine(assignees, createdDate, createdBy, changes);

      if (Strings.isValid(relatedToState)) {
         decRev.setSoleAttributeValue(AtsAttributeTypes.RelatedToState, relatedToState);
      }
      if (Strings.isValid(description)) {
         decRev.setSoleAttributeValue(AtsAttributeTypes.Description, description);
      }
      decRev.setSoleAttributeValue(AtsAttributeTypes.DecisionReviewOptions, getDecisionReviewOptionsString(options));
      if (reviewBlockType != null) {
         decRev.setSoleAttributeFromString(AtsAttributeTypes.ReviewBlocks, reviewBlockType.name());
      }
      AtsReviewCache.decache(teamArt);
      changes.add(decRev);
      return decRev;
   }

}
