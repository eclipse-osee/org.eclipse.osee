/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ats.core.workflow.hooks;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewDefectManager;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResult;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;

/**
 * @author Donald G. Dunne
 */
public class AtsPeerReviewRoleDefectValidator implements IAtsTransitionHook {

   @Override
   public void transitioning(TransitionResults results, IAtsWorkItem workItem, IStateToken fromState, IStateToken toState, Collection<? extends AtsUser> toAssignees) {
      IAtsTransitionHook.super.transitioning(results, workItem, fromState, toState, toAssignees);
      if (workItem.isPeerReview() && toState.getStateType().isCompleted()) {
         IAtsPeerToPeerReview review = (IAtsPeerToPeerReview) workItem;
         IAtsPeerReviewDefectManager defectMgr = review.getDefectManager();

         for (ReviewDefectItem item : defectMgr.getDefectItems()) {
            if (!item.isClosed()) {
               results.addResult(workItem, TransitionResult.REVIEW_DEFECTS_NOT_CLOSED);
               break;
            }
         }

         for (UserRole role : review.getRoleManager().getUserRoles()) {
            if (!role.isCompleted()) {
               results.addResult(workItem, TransitionResult.REVIEW_ROLES_NOT_COMPLETED);
               break;
            }
         }
      }
   }

   @Override
   public String getDescription() {
      return "Verify that Peer to Peer Review Roles and Defects are completed";
   }

}
