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
package org.eclipse.osee.ats.world;

import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.swt.graphics.Image;

public interface IWorldViewArtifact {

   Image getAssigneeImage() throws OseeCoreException;

   String getWorldViewActionableItems() throws OseeCoreException;

   String getWorldViewActivePoc() throws OseeCoreException;

   double getWorldViewAnnualCostAvoidance() throws OseeCoreException;

   String getWorldViewBranchStatus() throws OseeCoreException;

   String getWorldViewOriginatingWorkflowStr() throws OseeCoreException;

   Collection<TeamWorkFlowArtifact> getWorldViewOriginatingWorkflows() throws OseeCoreException;

   Date getWorldViewCancelledDate() throws OseeCoreException;

   String getWorldViewCancelledDateStr() throws OseeCoreException;

   String getWorldViewPoint() throws OseeCoreException;

   String getWorldViewNumeric1() throws OseeCoreException;

   String getWorldViewNumeric2() throws OseeCoreException;

   String getWorldViewGoalOrderVote() throws OseeCoreException;

   String getWorldViewGoalOrder() throws OseeCoreException;

   Date getWorldViewCompletedDate() throws OseeCoreException;

   String getWorldViewCompletedDateStr() throws OseeCoreException;

   Date getWorldViewCreatedDate() throws OseeCoreException;

   String getWorldViewCreatedDateStr() throws OseeCoreException;

   Date getWorldViewDeadlineDate() throws OseeCoreException;

   String getWorldViewDeadlineDateStr() throws OseeCoreException;

   String getWorldViewDecision() throws OseeCoreException;

   String getWorldViewDescription() throws OseeCoreException;

   /**
    * @return estimated hours from workflow attribute, tasks and reviews
    */
   double getWorldViewEstimatedHours() throws OseeCoreException;

   Date getWorldViewEstimatedReleaseDate() throws OseeCoreException;

   String getWorldViewEstimatedReleaseDateStr() throws OseeCoreException;

   Date getWorldViewEstimatedCompletionDate() throws OseeCoreException;

   String getWorldViewEstimatedCompletionDateStr() throws OseeCoreException;

   double getWorldViewHoursSpentState() throws OseeCoreException;

   double getWorldViewHoursSpentStateReview() throws OseeCoreException;

   double getWorldViewHoursSpentStateTask() throws OseeCoreException;

   double getWorldViewHoursSpentStateTotal() throws OseeCoreException;

   double getWorldViewHoursSpentTotal() throws OseeCoreException;

   String getWorldViewID() throws OseeCoreException;

   String getWorldViewParentID() throws OseeCoreException;

   String getWorldViewParentState() throws OseeCoreException;

   String getWorldViewDaysInCurrentState() throws OseeCoreException;

   String getWorldViewImplementer() throws OseeCoreException;

   String getWorldViewLegacyPCR() throws OseeCoreException;

   double getWorldViewManDaysNeeded() throws OseeCoreException;

   String getWorldViewNotes() throws OseeCoreException;

   String getWorldViewNumberOfTasks() throws OseeCoreException;

   String getWorldViewNumberOfTasksRemaining() throws OseeCoreException;

   String getWorldViewLastStatused() throws OseeCoreException;

   String getWorldViewOriginator() throws OseeCoreException;

   int getWorldViewPercentCompleteState() throws OseeCoreException;

   int getWorldViewPercentCompleteStateReview() throws OseeCoreException;

   int getWorldViewPercentCompleteStateTask() throws OseeCoreException;

   int getWorldViewPercentCompleteTotal() throws OseeCoreException;

   int getWorldViewPercentRework() throws OseeCoreException;

   String getWorldViewPercentReworkStr() throws OseeCoreException;

   String getWorldViewPriority() throws OseeCoreException;

   String getWorldViewRelatedToState() throws OseeCoreException;

   Date getWorldViewReleaseDate() throws OseeCoreException;

   String getWorldViewReleaseDateStr() throws OseeCoreException;

   double getWorldViewRemainHours() throws OseeCoreException;

   String getWorldViewResolution() throws OseeCoreException;

   String getWorldViewReviewAuthor() throws OseeCoreException;

   String getWorldViewReviewDecider() throws OseeCoreException;

   String getWorldViewReviewModerator() throws OseeCoreException;

   String getWorldViewReviewReviewer() throws OseeCoreException;

   String getWorldViewState() throws OseeCoreException;

   String getWorldViewTeam() throws OseeCoreException;

   String getWorldViewTitle() throws OseeCoreException;

   String getWorldViewType() throws OseeCoreException;

   String getWorldViewUserCommunity() throws OseeCoreException;

   String getWorldViewValidationRequiredStr() throws OseeCoreException;

   double getWorldViewWeeklyBenefit() throws OseeCoreException;

   String getWorldViewWorkPackage() throws OseeCoreException;

   Result isWorldViewAnnualCostAvoidanceValid() throws OseeCoreException;

   Result isWorldViewDeadlineAlerting() throws OseeCoreException;

   Result isWorldViewManDaysNeededValid() throws OseeCoreException;

   Result isWorldViewRemainHoursValid() throws OseeCoreException;

   String getWorldViewNumberOfReviewIssueDefects() throws OseeCoreException;

   String getWorldViewNumberOfReviewMajorDefects() throws OseeCoreException;

   String getWorldViewNumberOfReviewMinorDefects() throws OseeCoreException;

   String getWorldViewActionsIntiatingWorkflow() throws OseeCoreException;

}
