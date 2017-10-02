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
package org.eclipse.osee.ats.editor.stateItem;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.core.review.UserRoleManager;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class AtsPeerToPeerReviewReviewStateItem extends AtsStateItem implements ITransitionListener {

   public AtsPeerToPeerReviewReviewStateItem() {
      super(AtsPeerToPeerReviewReviewStateItem.class.getSimpleName());
   }

   @Override
   public String getDescription() {
      return "Assign review state to all members of review as per role in prepare state.";
   }

   @Override
   public void transitioned(IAtsWorkItem workItem, IStateToken fromState, IStateToken toState, Collection<? extends IAtsUser> toAssignees, IAtsChangeSet changes)  {
      if (workItem instanceof IAtsPeerToPeerReview && toState.getName().equals(
         PeerToPeerReviewState.Review.getName())) {
         // Set Assignees to all user roles users
         Set<IAtsUser> assignees = new HashSet<>();
         PeerToPeerReviewArtifact peerArt = (PeerToPeerReviewArtifact) workItem;
         for (UserRole uRole : peerArt.getRoleManager().getUserRoles()) {
            if (!uRole.isCompleted()) {
               assignees.add(UserRoleManager.getUser(uRole, AtsClientService.get()));
            }
         }
         assignees.addAll(workItem.getStateMgr().getAssignees());

         workItem.getStateMgr().setAssignees(assignees);
         changes.add(workItem);
      }
   }

   @Override
   public void transitioning(TransitionResults results, IAtsWorkItem workItem, IStateToken fromState, IStateToken toState, Collection<? extends IAtsUser> toAssignees) {
      // do nothing
   }

}
