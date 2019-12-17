/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.review;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewOption;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsReviewHook;

/**
 * @author Donald G. Dunne
 */
public interface IAtsReviewService {

   boolean isValidationReviewRequired(IAtsWorkItem workItem);

   /**
    * Create a new decision review configured and transitioned to handle action validation
    *
    * @param force will force the creation of the review without checking that a review should be created
    */
   IAtsDecisionReview createValidateReview(IAtsTeamWorkflow teamWf, boolean force, Date transitionDate, IAtsUser transitionUser, IAtsChangeSet changes);

   Collection<IAtsAbstractReview> getReviewsFromCurrentState(IAtsTeamWorkflow teamWf);

   Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf);

   IAtsPeerReviewRoleManager createPeerReviewRoleManager(IAtsPeerToPeerReview peerRev);

   IAtsDecisionReview createNewDecisionReviewAndTransitionToDecision(IAtsTeamWorkflow teamWf, String reviewTitle, String description, String againstState, ReviewBlockType reviewBlockType, Collection<IAtsDecisionReviewOption> options, List<? extends IAtsUser> assignees, Date createdDate, IAtsUser createdBy, IAtsChangeSet changes);

   IAtsDecisionReview createNewDecisionReview(IAtsTeamWorkflow teamWf, ReviewBlockType reviewBlockType, boolean againstCurrentState, Date createdDate, IAtsUser createdBy, IAtsChangeSet changes);

   IAtsDecisionReview createNewDecisionReview(IAtsTeamWorkflow teamWf, ReviewBlockType reviewBlockType, String title, String relatedToState, String description, Collection<IAtsDecisionReviewOption> options, List<? extends IAtsUser> assignees, Date createdDate, IAtsUser createdBy, IAtsChangeSet changes);

   List<IAtsDecisionReviewOption> getDefaultDecisionReviewOptions();

   String getDecisionReviewOptionsString(Collection<IAtsDecisionReviewOption> options);

   boolean hasReviews(IAtsTeamWorkflow teamWf);

   Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf, IStateToken relatedToState);

   boolean isStandAloneReview(IAtsPeerToPeerReview peerRev);

   IAtsPeerToPeerReview createNewPeerToPeerReview(IAtsActionableItem actionableItem, String reviewTitle, String againstState, Date createdDate, IAtsUser createdBy, IAtsChangeSet changes);

   IAtsPeerToPeerReview createNewPeerToPeerReview(IAtsTeamWorkflow teamWf, String reviewTitle, String againstState, Date createdDate, IAtsUser createdBy, IAtsChangeSet changes);

   IAtsPeerToPeerReview createNewPeerToPeerReview(IAtsWorkDefinition workDefinition, IAtsTeamWorkflow teamWf, String reviewTitle, String againstState, IAtsChangeSet changes);

   IAtsPeerToPeerReview createNewPeerToPeerReview(IAtsTeamWorkflow teamWf, String reviewTitle, String againstState, IAtsChangeSet changes);

   String getDefaultPeerReviewTitle(IAtsTeamWorkflow teamWf);

   ReviewBlockType getReviewBlockType(IAtsAbstractReview review);

   Collection<IAtsReviewHook> getReviewHooks();

}
