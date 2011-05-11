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

package org.eclipse.osee.ats.core.review;

import java.util.Collection;
import org.eclipse.osee.ats.core.review.defect.DefectItem;
import org.eclipse.osee.ats.core.review.role.UserRole;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.core.workflow.transition.TransitionOption;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.core.util.WorkPageType;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * Methods in support of programatically transitioning the Peer Review Workflow through it's states. Only to be used for
 * the DefaultReviewWorkflow of Prepare->Review->Complete
 * 
 * @author Donald G. Dunne
 */
public final class PeerToPeerReviewManager {

   public static String getDefaultReviewTitle(TeamWorkFlowArtifact teamArt) {
      return "Review \"" + teamArt.getArtifactTypeName() + "\" titled \"" + teamArt.getName() + "\"";
   }

   private PeerToPeerReviewManager() {
      // private constructor
   }

   /**
    * Quickly transition to a state with minimal metrics and data entered. Should only be used for automated transition
    * for things such as developmental testing and demos.
    * 
    * @param user User to transition to OR null if should use user of current state
    */
   public static Result transitionTo(PeerToPeerReviewArtifact reviewArt, PeerToPeerReviewState toState, Collection<UserRole> roles, Collection<DefectItem> defects, User user, boolean popup, SkynetTransaction transaction) throws OseeCoreException {
      Result result = setPrepareStateData(popup, reviewArt, roles, "DoThis.java", 100, .2, transaction);
      if (result.isFalse()) {
         return result;
      }
      result =
         transitionToState(PeerToPeerReviewState.Review.getWorkPageType(), popup, reviewArt,
            PeerToPeerReviewState.Review, transaction);
      if (result.isFalse()) {
         return result;
      }
      if (toState == PeerToPeerReviewState.Review) {
         return Result.TrueResult;
      }

      result = setReviewStateData(popup, reviewArt, roles, defects, 100, .2, transaction);
      if (result.isFalse()) {
         return result;
      }

      result =
         transitionToState(PeerToPeerReviewState.Completed.getWorkPageType(), popup, reviewArt,
            PeerToPeerReviewState.Completed, transaction);
      if (result.isFalse()) {
         return result;
      }
      return Result.TrueResult;
   }

   private static Result transitionToState(WorkPageType workPageType, boolean popup, PeerToPeerReviewArtifact reviewArt, IWorkPage toState, SkynetTransaction transaction) throws OseeCoreException {
      TransitionManager transitionMgr = new TransitionManager(reviewArt);
      Result result =
         transitionMgr.transition(toState, reviewArt.getStateMgr().getAssignees().iterator().next(), transaction,
            TransitionOption.None);
      return result;
   }

   public static Result setPrepareStateData(boolean popup, PeerToPeerReviewArtifact reviewArt, Collection<UserRole> roles, String reviewMaterials, int statePercentComplete, double stateHoursSpent, SkynetTransaction transaction) throws OseeCoreException {
      if (!reviewArt.isInState(PeerToPeerReviewState.Prepare)) {
         Result result = new Result("Action not in Prepare state");
         if (result.isFalse() && popup) {
            return result;
         }

      }
      if (roles != null) {
         for (UserRole role : roles) {
            reviewArt.getUserRoleManager().addOrUpdateUserRole(role, false, transaction);
         }
      }
      reviewArt.setSoleAttributeValue(AtsAttributeTypes.Location, reviewMaterials);
      reviewArt.getStateMgr().updateMetrics(stateHoursSpent, statePercentComplete, true);
      return Result.TrueResult;
   }

   public static Result setReviewStateData(boolean popup, PeerToPeerReviewArtifact reviewArt, Collection<UserRole> roles, Collection<DefectItem> defects, int statePercentComplete, double stateHoursSpent, SkynetTransaction transaction) throws OseeCoreException {
      if (roles != null) {
         for (UserRole role : roles) {
            reviewArt.getUserRoleManager().addOrUpdateUserRole(role, false, transaction);
         }
      }
      if (defects != null) {
         for (DefectItem defect : defects) {
            reviewArt.getDefectManager().addOrUpdateDefectItem(defect, false, transaction);
         }
      }
      reviewArt.getStateMgr().updateMetrics(stateHoursSpent, statePercentComplete, true);
      return Result.TrueResult;
   }

}
