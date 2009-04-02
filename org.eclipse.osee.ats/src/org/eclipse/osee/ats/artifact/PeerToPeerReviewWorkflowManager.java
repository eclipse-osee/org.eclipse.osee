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

package org.eclipse.osee.ats.artifact;

import java.util.Collection;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.editor.SMAManager.TransitionOption;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem;
import org.eclipse.osee.ats.util.widgets.role.UserRole;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * Methods in support of programatically transitioning the Peer Review Workflow through it's states. Only to be used for
 * the DefaultReviewWorkflow of Prepare->Review->Complete
 * 
 * @author Donald G. Dunne
 */
public class PeerToPeerReviewWorkflowManager {

   /**
    * Quickly transition to a state with minimal metrics and data entered. Should only be used for automated transition
    * for things such as developmental testing and demos.
    * 
    * @param toState
    * @param user User to transition to OR null if should use user of current state
    * @param popup
    * @param transaction
    * @return Result
    * @throws Exception
    */
   public static Result transitionTo(PeerToPeerReviewArtifact reviewArt, PeerToPeerReviewArtifact.PeerToPeerReviewState toState, Collection<UserRole> roles, Collection<DefectItem> defects, User user, boolean popup, SkynetTransaction transaction) throws OseeCoreException {
      Result result = setPrepareStateData(reviewArt, roles, "DoThis.java", 100, .2, transaction);
      if (result.isFalse()) {
         if (popup) result.popup();
         return result;
      }
      result =
            reviewArt.getSmaMgr().transition(PeerToPeerReviewArtifact.PeerToPeerReviewState.Review.name(),
                  (user != null ? user : reviewArt.getSmaMgr().getStateMgr().getAssignees().iterator().next()),
                  transaction, TransitionOption.None);
      if (result.isFalse()) {
         if (popup) result.popup();
         return result;
      }
      if (toState == PeerToPeerReviewArtifact.PeerToPeerReviewState.Review) return Result.TrueResult;

      result = setReviewStateData(reviewArt, roles, defects, 100, .2, transaction);
      if (result.isFalse()) {
         if (popup) result.popup();
         return result;
      }

      result =
            reviewArt.getSmaMgr().transition(DefaultTeamState.Completed.name(),
                  (user != null ? user : reviewArt.getSmaMgr().getStateMgr().getAssignees().iterator().next()),
                  transaction, TransitionOption.None);
      if (result.isFalse()) {
         if (popup) result.popup();
         return result;
      }
      return Result.TrueResult;
   }

   public static Result setPrepareStateData(PeerToPeerReviewArtifact reviewArt, Collection<UserRole> roles, String reviewMaterials, int statePercentComplete, double stateHoursSpent, SkynetTransaction transaction) throws OseeCoreException {
      if (!reviewArt.getSmaMgr().getStateMgr().getCurrentStateName().equals("Prepare")) return new Result(
            "Action not in Prepare state");
      if (roles != null) for (UserRole role : roles)
         reviewArt.getUserRoleManager().addOrUpdateUserRole(role, false, transaction);
      reviewArt.setSoleAttributeValue(ATSAttributes.LOCATION_ATTRIBUTE.getStoreName(), reviewMaterials);
      reviewArt.getSmaMgr().getStateMgr().updateMetrics(stateHoursSpent, statePercentComplete, true);
      return Result.TrueResult;
   }

   public static Result setReviewStateData(PeerToPeerReviewArtifact reviewArt, Collection<UserRole> roles, Collection<DefectItem> defects, int statePercentComplete, double stateHoursSpent, SkynetTransaction transaction) throws OseeCoreException {
      if (roles != null) for (UserRole role : roles)
         reviewArt.getUserRoleManager().addOrUpdateUserRole(role, false, transaction);
      if (defects != null) for (DefectItem defect : defects)
         reviewArt.getDefectManager().addOrUpdateDefectItem(defect, false, transaction);
      reviewArt.getSmaMgr().getStateMgr().updateMetrics(stateHoursSpent, statePercentComplete, true);
      return Result.TrueResult;
   }

}
