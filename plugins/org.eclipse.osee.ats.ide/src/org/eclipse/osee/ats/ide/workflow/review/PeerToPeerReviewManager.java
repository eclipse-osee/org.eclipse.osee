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

package org.eclipse.osee.ats.ide.workflow.review;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewRoleManager;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.ats.api.review.ReviewFormalType;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.review.defect.ReviewDefectManager;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.util.Result;

/**
 * Methods in support of programatically transitioning the Peer Review Workflow through it's states. Only to be used for
 * the DefaultReviewWorkflow of Prepare->Review->Complete
 *
 * @author Donald G. Dunne
 */
public class PeerToPeerReviewManager {

   public static String getDefaultReviewTitle(TeamWorkFlowArtifact teamWf) {
      return AtsClientService.get().getReviewService().getDefaultPeerReviewTitle(teamWf);
   }

   protected PeerToPeerReviewManager() {
      // private constructor
   }

   /**
    * Quickly transition to a state with minimal metrics and data entered. Should only be used for automated transition
    * for things such as developmental testing and demos.
    *
    * @param user User to transition to OR null if should use user of current state
    */
   public static Result transitionTo(PeerToPeerReviewArtifact reviewArt, PeerToPeerReviewState toState, Collection<UserRole> roles, Collection<ReviewDefectItem> defects, AtsUser user, boolean popup, IAtsChangeSet changes) {
      Result result = setPrepareStateData(popup, reviewArt, roles, "DoThis.java", 100, .2, changes);
      if (result.isFalse()) {
         return result;
      }
      result = transitionToState(PeerToPeerReviewState.Review.getStateType(), popup, reviewArt,
         PeerToPeerReviewState.Review, changes);
      if (result.isFalse()) {
         return result;
      }
      if (toState == PeerToPeerReviewState.Review) {
         return Result.TrueResult;
      }

      result = setReviewStateData(reviewArt, roles, defects, 100, .2, changes);
      if (result.isFalse()) {
         return result;
      }

      result = transitionToState(PeerToPeerReviewState.Completed.getStateType(), popup, reviewArt,
         PeerToPeerReviewState.Completed, changes);
      if (result.isFalse()) {
         return result;
      }
      return Result.TrueResult;
   }

   private static Result transitionToState(StateType StateType, boolean popup, PeerToPeerReviewArtifact reviewArt, IStateToken toState, IAtsChangeSet changes) {
      TransitionHelper helper = new TransitionHelper("Transition to " + toState.getName(), Arrays.asList(reviewArt),
         toState.getName(), Arrays.asList(reviewArt.getStateMgr().getAssignees().iterator().next()), null, changes,
         AtsClientService.get().getServices(), TransitionOption.OverrideAssigneeCheck);
      IAtsTransitionManager transitionMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = transitionMgr.handleAll();
      if (results.isEmpty()) {
         return Result.TrueResult;
      }
      return new Result("Error transitioning [%s]", results);
   }

   public static Result setPrepareStateData(boolean popup, PeerToPeerReviewArtifact reviewArt, Collection<UserRole> roles, String reviewMaterials, int statePercentComplete, double stateHoursSpent, IAtsChangeSet changes) {
      if (!reviewArt.isInState(PeerToPeerReviewState.Prepare)) {
         Result result = new Result("Action not in Prepare state");
         if (result.isFalse() && popup) {
            return result;
         }

      }
      if (roles != null) {
         IAtsPeerReviewRoleManager roleMgr = ((IAtsPeerToPeerReview) reviewArt).getRoleManager();
         for (UserRole role : roles) {
            roleMgr.addOrUpdateUserRole(role);
         }
         roleMgr.saveToArtifact(changes);
      }
      reviewArt.setSoleAttributeValue(AtsAttributeTypes.Location, reviewMaterials);
      reviewArt.setSoleAttributeValue(AtsAttributeTypes.ReviewFormalType, ReviewFormalType.InFormal.name());
      reviewArt.getStateMgr().updateMetrics(reviewArt.getStateDefinition(), stateHoursSpent, statePercentComplete, true,
         AtsClientService.get().getUserService().getCurrentUser());
      return Result.TrueResult;
   }

   public static Result setReviewStateData(PeerToPeerReviewArtifact reviewArt, Collection<UserRole> roles, Collection<ReviewDefectItem> defects, int statePercentComplete, double stateHoursSpent, IAtsChangeSet changes) {
      if (roles != null) {
         IAtsPeerReviewRoleManager roleMgr = ((IAtsPeerToPeerReview) reviewArt).getRoleManager();
         for (UserRole role : roles) {
            roleMgr.addOrUpdateUserRole(role);
         }
         roleMgr.saveToArtifact(changes);
      }
      if (defects != null) {
         ReviewDefectManager defectManager = new ReviewDefectManager(reviewArt);
         for (ReviewDefectItem defect : defects) {
            defectManager.addOrUpdateDefectItem(defect);
         }
         defectManager.saveToArtifact(reviewArt);
      }
      reviewArt.getStateMgr().updateMetrics(reviewArt.getStateDefinition(), stateHoursSpent, statePercentComplete, true,
         AtsClientService.get().getUserService().getCurrentUser());
      return Result.TrueResult;
   }

   public static PeerToPeerReviewArtifact createNewPeerToPeerReview(TeamWorkFlowArtifact teamArt, String reviewTitle, String againstState, IAtsChangeSet changes) {
      return (PeerToPeerReviewArtifact) AtsClientService.get().getReviewService().createNewPeerToPeerReview(teamArt,
         reviewTitle, againstState, changes);
   }

   public static PeerToPeerReviewArtifact createNewPeerToPeerReview(IAtsWorkDefinition workDefinition, TeamWorkFlowArtifact teamWf, String reviewTitle, String againstState, IAtsChangeSet changes) {
      return (PeerToPeerReviewArtifact) AtsClientService.get().getReviewService().createNewPeerToPeerReview(
         workDefinition, teamWf, reviewTitle, againstState, changes);
   }

   public static PeerToPeerReviewArtifact createNewPeerToPeerReview(TeamWorkFlowArtifact teamWf, String reviewTitle, String againstState, Date createdDate, AtsUser createdBy, IAtsChangeSet changes) {
      return (PeerToPeerReviewArtifact) AtsClientService.get().getReviewService().createNewPeerToPeerReview(teamWf,
         reviewTitle, againstState, createdDate, createdBy, changes);
   }

   public static PeerToPeerReviewArtifact createNewPeerToPeerReview(IAtsActionableItem actionableItem, String reviewTitle, String againstState, Date createdDate, AtsUser createdBy, IAtsChangeSet changes) {
      return (PeerToPeerReviewArtifact) AtsClientService.get().getReviewService().createNewPeerToPeerReview(
         actionableItem, reviewTitle, againstState, createdDate, createdBy, changes);
   }

   public static boolean isStandAlongReview(Object object) {
      if (object instanceof IAtsPeerToPeerReview) {
         return AtsClientService.get().getReviewService().isStandAloneReview((IAtsPeerToPeerReview) object);
      }
      return false;
   }

}
