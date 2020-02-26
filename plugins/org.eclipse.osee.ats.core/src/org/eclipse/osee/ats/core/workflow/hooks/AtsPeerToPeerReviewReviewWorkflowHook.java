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
package org.eclipse.osee.ats.core.workflow.hooks;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.ats.core.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.core.review.UserRoleManager;

/**
 * Contributed via AtsWorkItemServiceImpl
 *
 * @author Donald G. Dunne
 */
public class AtsPeerToPeerReviewReviewWorkflowHook implements IAtsTransitionHook {

   public String getName() {
      return AtsPeerToPeerReviewReviewWorkflowHook.class.getSimpleName();
   }

   @Override
   public String getDescription() {
      return "Assign review state to all members of review as per role in prepare state.";
   }

   @Override
   public void transitioned(IAtsWorkItem workItem, IStateToken fromState, IStateToken toState, Collection<? extends AtsUser> toAssignees, IAtsChangeSet changes) {
      if (workItem instanceof IAtsPeerToPeerReview && toState.getName().equals(
         PeerToPeerReviewState.Review.getName())) {
         // Set Assignees to all user roles users
         Set<AtsUser> assignees = new HashSet<>();
         IAtsPeerToPeerReview peerRev = (IAtsPeerToPeerReview) workItem;
         for (UserRole uRole : peerRev.getRoleManager().getUserRoles()) {
            if (!uRole.isCompleted()) {
               assignees.add(UserRoleManager.getUser(uRole, AtsApiService.get()));
            }
         }
         assignees.addAll(workItem.getStateMgr().getAssignees());

         workItem.getStateMgr().setAssignees(assignees);
         changes.add(workItem);
      }
   }

}
