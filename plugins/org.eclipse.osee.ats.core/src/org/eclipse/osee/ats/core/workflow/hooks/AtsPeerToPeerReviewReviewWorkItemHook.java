/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.core.review.UserRoleManager;

/**
 * Contributed via AtsWorkItemServiceImpl
 *
 * @author Donald G. Dunne
 */
public class AtsPeerToPeerReviewReviewWorkItemHook implements IAtsTransitionHook {

   public String getName() {
      return AtsPeerToPeerReviewReviewWorkItemHook.class.getSimpleName();
   }

   @Override
   public String getDescription() {
      return "Assign review state to all members of review as per role in prepare state.";
   }

   @Override
   public void transitioned(IAtsWorkItem workItem, IStateToken fromState, IStateToken toState,
      Collection<AtsUser> toAssignees, AtsUser asUser, IAtsChangeSet changes, AtsApi atsApi) {
      if (workItem instanceof IAtsPeerToPeerReview && toState.getName().equals(
         PeerToPeerReviewState.Review.getName())) {
         // Set Assignees to all user roles users
         Set<AtsUser> assignees = new HashSet<>();
         IAtsPeerToPeerReview peerRev = (IAtsPeerToPeerReview) workItem;
         for (UserRole uRole : peerRev.getRoleManager().getUserRoles()) {
            if (!uRole.isCompleted()) {
               assignees.add(UserRoleManager.getUser(uRole, atsApi));
            }
         }
         assignees.addAll(workItem.getAssignees());
         changes.setAssignees(workItem, assignees);
         changes.add(workItem);
      }
   }

}
