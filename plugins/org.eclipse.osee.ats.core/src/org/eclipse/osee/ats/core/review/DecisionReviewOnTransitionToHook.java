/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.core.review;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateEventType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsReviewHook;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Create DecisionReview from transition if defined by StateDefinition.</br>
 * </br>
 * Contributed via AtsWorkItemServiceImpl
 *
 * @author Donald G. Dunne
 */
public class DecisionReviewOnTransitionToHook implements IAtsTransitionHook {

   /**
    * Creates decision review if one of same name doesn't already exist
    */
   public static IAtsDecisionReview createNewDecisionReview(IAtsDecisionReviewDefinition revDef, IAtsChangeSet changes,
      IAtsTeamWorkflow teamWf, Date createdDate, AtsUser createdBy) {
      AtsApi atsApi = AtsApiService.get();
      if (Named.getNames(atsApi.getReviewService().getReviews(teamWf)).contains(revDef.getReviewTitle())) {
         // Already created this review
         return null;
      }
      // Add current user if no valid users specified
      List<AtsUser> users = new LinkedList<>();
      users.addAll(atsApi.getUserService().getUsersByUserIds(revDef.getAssignees()));
      if (users.isEmpty()) {
         users.add(atsApi.getUserService().getCurrentUser());
      }
      if (!Strings.isValid(revDef.getReviewTitle())) {
         throw new OseeStateException("ReviewDefinition must specify title for Team Workflow [%s] WorkDefinition [%s]",
            teamWf.toStringWithId(), teamWf.getWorkDefinition());
      }
      IAtsDecisionReview decArt = null;
      if (revDef.isAutoTransitionToDecision()) {
         decArt = (IAtsDecisionReview) atsApi.getReviewService().createNewDecisionReviewAndTransitionToDecision(teamWf,
            revDef.getReviewTitle(), revDef.getDescription(), revDef.getRelatedToState(), revDef.getBlockingType(),
            revDef.getOptions(), users, createdDate, createdBy, changes).getStoreObject();
      } else {
         decArt = atsApi.getReviewService().createNewDecisionReview(teamWf, revDef.getBlockingType(),
            revDef.getReviewTitle(), revDef.getRelatedToState(), revDef.getDescription(), revDef.getOptions(), users,
            createdDate, createdBy, changes);
      }
      decArt.getLog().addLog(LogType.Note, null, String.format("Review [%s] auto-generated", revDef.getName()),
         atsApi.getUserService().getCurrentUser().getUserId());
      for (IAtsReviewHook provider : atsApi.getReviewService().getReviewHooks()) {
         provider.reviewCreated(decArt);
      }
      changes.add(decArt);
      return decArt;
   }

   @Override
   public void transitioned(IAtsWorkItem workItem, IStateToken fromState, IStateToken toState,
      Collection<AtsUser> toAssignees, AtsUser asUser, IAtsChangeSet changes, AtsApi atsApi) {
      // Create any decision or peerToPeer reviews for transitionTo and transitionFrom
      if (!workItem.isTeamWorkflow()) {
         return;
      }
      Date createdDate = new Date();
      AtsUser createdBy = AtsCoreUsers.SYSTEM_USER;
      IAtsTeamWorkflow teamArt = (IAtsTeamWorkflow) workItem;

      for (IAtsDecisionReviewDefinition decRevDef : workItem.getStateDefinition().getDecisionReviews()) {
         if (decRevDef.getStateEventType() != null && decRevDef.getStateEventType().equals(
            StateEventType.TransitionTo)) {
            IAtsDecisionReview decArt = DecisionReviewOnTransitionToHook.createNewDecisionReview(decRevDef, changes,
               teamArt, createdDate, createdBy);
            if (decArt != null) {
               changes.add(decArt);
            }
         }
      }
   }

   @Override
   public String getDescription() {
      return "Create DecisionReview from transition if defined by StateDefinition";
   }

}
