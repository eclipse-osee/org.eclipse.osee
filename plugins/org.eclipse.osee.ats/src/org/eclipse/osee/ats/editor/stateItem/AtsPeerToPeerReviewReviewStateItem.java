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
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.core.client.review.role.UserRole;
import org.eclipse.osee.ats.core.client.review.role.UserRoleManager;
import org.eclipse.osee.ats.core.client.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.client.workflow.transition.ITransitionListener;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionResults;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

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
   public void transitioned(AbstractWorkflowArtifact sma, IWorkPage fromState, IWorkPage toState, Collection<? extends IBasicUser> toAssignees, SkynetTransaction transaction) throws OseeCoreException {
      if (sma.isOfType(AtsArtifactTypes.PeerToPeerReview) && toState.getPageName().equals(
         PeerToPeerReviewState.Review.getPageName())) {
         // Set Assignees to all user roles users
         Set<IBasicUser> assignees = new HashSet<IBasicUser>();
         PeerToPeerReviewArtifact peerArt = (PeerToPeerReviewArtifact) sma;
         for (UserRole uRole : UserRoleManager.getUserRoles(peerArt)) {
            if (!uRole.isCompleted()) {
               assignees.add(uRole.getUser());
            }
         }
         assignees.addAll(sma.getStateMgr().getAssignees());

         sma.getStateMgr().setAssignees(assignees);
         sma.persist(transaction);
      }
   }

   @Override
   public void transitioning(TransitionResults results, AbstractWorkflowArtifact sma, IWorkPage fromState, IWorkPage toState, Collection<? extends IBasicUser> toAssignees) {
      // do nothing
   }

}
