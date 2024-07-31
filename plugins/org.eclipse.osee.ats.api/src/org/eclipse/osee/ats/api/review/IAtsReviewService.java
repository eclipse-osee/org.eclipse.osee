/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.api.review;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewOption;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsReviewHook;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.util.Result;

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
   IAtsDecisionReview createValidateReview(IAtsTeamWorkflow teamWf, boolean force, Date transitionDate,
      AtsUser transitionUser, IAtsChangeSet changes);

   Collection<IAtsAbstractReview> getReviewsFromCurrentState(IAtsTeamWorkflow teamWf);

   Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf);

   IAtsPeerReviewRoleManager createPeerReviewRoleManager(IAtsPeerToPeerReview peerRev);

   IAtsDecisionReview createNewDecisionReviewAndTransitionToDecision(IAtsTeamWorkflow teamWf, String reviewTitle,
      String description, String againstState, ReviewBlockType reviewBlockType,
      Collection<IAtsDecisionReviewOption> options, Collection<AtsUser> assignees, Date createdDate, AtsUser createdBy,
      IAtsChangeSet changes);

   IAtsDecisionReview createNewDecisionReview(IAtsTeamWorkflow teamWf, ReviewBlockType reviewBlockType,
      boolean againstCurrentState, Date createdDate, AtsUser createdBy, IAtsChangeSet changes);

   IAtsDecisionReview createNewDecisionReview(IAtsTeamWorkflow teamWf, ReviewBlockType reviewBlockType, String title,
      String relatedToState, String description, Collection<IAtsDecisionReviewOption> options,
      Collection<AtsUser> assignees, Date createdDate, AtsUser createdBy, IAtsChangeSet changes);

   List<IAtsDecisionReviewOption> getDefaultDecisionReviewOptions();

   String getDecisionReviewOptionsString(Collection<IAtsDecisionReviewOption> options);

   boolean hasReviews(IAtsTeamWorkflow teamWf);

   Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf, IStateToken relatedToState);

   IAtsPeerToPeerReview createNewPeerToPeerReview(IAtsActionableItem actionableItem, String reviewTitle,
      String againstState, Date createdDate, AtsUser createdBy, IAtsChangeSet changes);

   IAtsPeerToPeerReview createNewPeerToPeerReview(IAtsTeamWorkflow teamWf, String reviewTitle, String againstState,
      Date createdDate, AtsUser createdBy, IAtsChangeSet changes);

   IAtsPeerToPeerReview createNewPeerToPeerReview(WorkDefinition workDefinition, IAtsTeamWorkflow teamWf,
      String reviewTitle, String againstState, IAtsChangeSet changes);

   IAtsPeerToPeerReview createNewPeerToPeerReview(IAtsTeamWorkflow teamWf, String reviewTitle, String againstState,
      IAtsChangeSet changes);

   String getDefaultPeerReviewTitle(IAtsTeamWorkflow teamWf);

   ReviewBlockType getReviewBlockType(IAtsAbstractReview review);

   Collection<IAtsReviewHook> getReviewHooks();

   Result transitionDecisionTo(IAtsDecisionReview decRev, DecisionReviewState toState, AtsUser user, boolean popup,
      IAtsChangeSet changes);

   String getDefaultReviewTitle(IAtsTeamWorkflow teamWf);

   Result transitionTo(IAtsPeerToPeerReview peerRev, PeerToPeerReviewState toState, Collection<UserRole> roles,
      Collection<ReviewDefectItem> defects, AtsUser user, boolean popup, IAtsChangeSet changes);

   Result setPrepareStateData(boolean popup, IAtsPeerToPeerReview peerRev, Collection<UserRole> roles,
      String reviewMaterials, int statePercentComplete, double stateHoursSpent, IAtsChangeSet changes);

   Result setReviewStateData(IAtsPeerToPeerReview peerRev, Collection<UserRole> roles,
      Collection<ReviewDefectItem> defects, int statePercentComplete, double stateHoursSpent, IAtsChangeSet changes);

   boolean isStandAloneReview(Object object);

   ReviewDefectItem getDefectItem(String xml, IAtsPeerToPeerReview review);

   IAtsAbstractReview getReview(ArtifactToken artifact);

   String getValidateReviewTitle(IAtsTeamWorkflow teamWf);

   boolean isFormalReview(IAtsWorkItem workItem);

   void updateHoursSpentRoles(IAtsPeerToPeerReview peerRev, IAtsChangeSet changes);

}
