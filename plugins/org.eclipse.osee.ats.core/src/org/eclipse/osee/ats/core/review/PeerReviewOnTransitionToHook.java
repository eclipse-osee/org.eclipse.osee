/*******************************************************************************
 * Copyright (c) 2011 Boeing.
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
import java.util.Date;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsPeerReviewDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateEventType;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsReviewHook;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Create PeerToPeer Review from transition if defined by StateDefinition.</br>
 * </br>
 * Contributed via AtsWorkItemServiceImpl
 *
 * @author Donald G. Dunne
 */
public class PeerReviewOnTransitionToHook implements IAtsTransitionHook {

   /**
    * Creates PeerToPeer review if one of same name doesn't already exist
    */
   public static IAtsPeerToPeerReview createNewPeerToPeerReview(IAtsPeerReviewDefinition peerRevDef, IAtsChangeSet changes, IAtsTeamWorkflow teamWf, Date createdDate, IAtsUser createdBy) {
      String title = peerRevDef.getReviewTitle();
      if (!Strings.isValid(title)) {
         title = String.format("Review [%s]", teamWf.getName());
      }
      if (Lib.getNames(AtsApiService.get().getReviewService().getReviews(teamWf)).contains(title)) {
         // Already created this review
         return null;
      }
      IAtsPeerToPeerReview peerRev = AtsApiService.get().getReviewService().createNewPeerToPeerReview(teamWf, title,
         peerRevDef.getRelatedToState(), createdDate, createdBy, changes);
      if (Strings.isValid(peerRevDef.getDescription())) {
         changes.setSoleAttributeFromString(peerRev, AtsAttributeTypes.Description, peerRevDef.getDescription());
      }
      ReviewBlockType reviewBlockType = peerRevDef.getBlockingType();
      if (reviewBlockType != null) {
         changes.setSoleAttributeFromString(peerRev, AtsAttributeTypes.ReviewBlocks, reviewBlockType.name());
      }
      if (Strings.isValid(peerRevDef.getLocation())) {
         changes.setSoleAttributeFromString(peerRev, AtsAttributeTypes.Location, peerRevDef.getLocation());
      }
      Collection<IAtsUser> assignees =
         AtsApiService.get().getUserService().getUsersByUserIds(peerRevDef.getAssignees());
      if (assignees.size() > 0) {
         peerRev.getStateMgr().setAssignees(assignees);
      }
      peerRev.getLog().addLog(LogType.Note, null, String.format("Review [%s] auto-generated", peerRevDef.getName()),
         AtsApiService.get().getUserService().getCurrentUser().getUserId());
      for (IAtsReviewHook provider : AtsApiService.get().getReviewService().getReviewHooks()) {
         provider.reviewCreated(peerRev);
      }
      changes.add(peerRev);
      return peerRev;
   }

   @Override
   public void transitioned(IAtsWorkItem workItem, IStateToken fromState, IStateToken toState, Collection<? extends IAtsUser> toAssignees, IAtsChangeSet changes) {
      // Create any decision or peerToPeer reviews for transitionTo and transitionFrom
      if (!workItem.isTeamWorkflow()) {
         return;
      }
      Date createdDate = new Date();
      IAtsUser createdBy = AtsCoreUsers.SYSTEM_USER;
      IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;

      for (IAtsPeerReviewDefinition peerRevDef : workItem.getStateDefinition().getPeerReviews()) {
         if (peerRevDef.getStateEventType() != null && peerRevDef.getStateEventType().equals(
            StateEventType.TransitionTo)) {
            IAtsPeerToPeerReview peerRev =
               createNewPeerToPeerReview(peerRevDef, changes, teamWf, createdDate, createdBy);
            if (peerRev != null) {
               changes.add(peerRev);
            }
         }
      }
   }

   @Override
   public String getDescription() {
      return "Create PeerToPeer Review from transition if defined by StateDefinition";
   }

}
