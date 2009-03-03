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

import java.util.Date;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.swt.graphics.Image;

public interface IWorldViewArtifact {

   public Image getAssigneeImage() throws OseeCoreException;

   public String getWorldViewActionableItems() throws OseeCoreException;

   public String getWorldViewActivePoc() throws OseeCoreException;

   public double getWorldViewAnnualCostAvoidance() throws OseeCoreException;

   public String getWorldViewBranchStatus() throws OseeCoreException;

   public Date getWorldViewCancelledDate() throws OseeCoreException;

   public String getWorldViewCancelledDateStr() throws OseeCoreException;

   public String getWorldViewCategory() throws OseeCoreException;

   public String getWorldViewCategory2() throws OseeCoreException;

   public String getWorldViewCategory3() throws OseeCoreException;

   public ChangeType getWorldViewChangeType() throws OseeCoreException;

   public String getWorldViewChangeTypeStr() throws OseeCoreException;

   public Date getWorldViewCompletedDate() throws OseeCoreException;

   public String getWorldViewCompletedDateStr() throws OseeCoreException;

   public Date getWorldViewCreatedDate() throws OseeCoreException;

   public String getWorldViewCreatedDateStr() throws OseeCoreException;

   public Date getWorldViewDeadlineDate() throws OseeCoreException;

   public String getWorldViewDeadlineDateStr() throws OseeCoreException;

   public String getWorldViewDecision() throws OseeCoreException;

   public String getWorldViewDescription() throws OseeCoreException;

   /**
    * @return estimated hours from workflow attribute, tasks and reviews
    * @throws OseeCoreException
    */
   public double getWorldViewEstimatedHours() throws OseeCoreException;

   public Date getWorldViewEstimatedReleaseDate() throws OseeCoreException;

   public String getWorldViewEstimatedReleaseDateStr() throws OseeCoreException;

   public Date getWorldViewEstimatedCompletionDate() throws OseeCoreException;

   public String getWorldViewEstimatedCompletionDateStr() throws OseeCoreException;

   public double getWorldViewHoursSpentState() throws OseeCoreException;

   public double getWorldViewHoursSpentStateReview() throws OseeCoreException;

   public double getWorldViewHoursSpentStateTask() throws OseeCoreException;

   public double getWorldViewHoursSpentStateTotal() throws OseeCoreException;

   public double getWorldViewHoursSpentTotal() throws OseeCoreException;

   public String getWorldViewID() throws OseeCoreException;

   public String getWorldViewParentID() throws OseeCoreException;

   public String getWorldViewImplementer() throws OseeCoreException;

   public String getWorldViewLegacyPCR() throws OseeCoreException;

   public double getWorldViewManDaysNeeded() throws OseeCoreException;

   public String getWorldViewNotes() throws OseeCoreException;

   public String getWorldViewNumberOfTasks() throws OseeCoreException;

   public String getWorldViewLastUpdated() throws OseeCoreException;

   public String getWorldViewLastStatused() throws OseeCoreException;

   public String getWorldViewOriginator() throws OseeCoreException;

   public int getWorldViewPercentCompleteState() throws OseeCoreException;

   public int getWorldViewPercentCompleteStateReview() throws OseeCoreException;

   public int getWorldViewPercentCompleteStateTask() throws OseeCoreException;

   public int getWorldViewPercentCompleteTotal() throws OseeCoreException;

   public int getWorldViewPercentRework() throws OseeCoreException;

   public String getWorldViewPercentReworkStr() throws OseeCoreException;

   public String getWorldViewPriority() throws OseeCoreException;

   public String getWorldViewRelatedToState() throws OseeCoreException;

   public Date getWorldViewReleaseDate() throws OseeCoreException;

   public String getWorldViewReleaseDateStr() throws OseeCoreException;

   public double getWorldViewRemainHours() throws OseeCoreException;

   public String getWorldViewResolution() throws OseeCoreException;

   public String getWorldViewGroups() throws OseeCoreException;

   public String getWorldViewReviewAuthor() throws OseeCoreException;

   public String getWorldViewReviewDecider() throws OseeCoreException;

   public String getWorldViewReviewModerator() throws OseeCoreException;

   public String getWorldViewReviewReviewer() throws OseeCoreException;

   public String getWorldViewState() throws OseeCoreException;

   public String getWorldViewTeam() throws OseeCoreException;

   public String getWorldViewTitle() throws OseeCoreException;

   public String getWorldViewType() throws OseeCoreException;

   public String getWorldViewUserCommunity() throws OseeCoreException;

   public String getWorldViewValidationRequiredStr() throws OseeCoreException;

   public String getWorldViewTargetedVersionStr() throws OseeCoreException;

   public VersionArtifact getWorldViewTargetedVersion() throws OseeCoreException;

   public double getWorldViewWeeklyBenefit() throws OseeCoreException;

   public String getWorldViewWorkPackage() throws OseeCoreException;

   public Result isWorldViewAnnualCostAvoidanceValid() throws OseeCoreException;

   public Result isWorldViewDeadlineAlerting() throws OseeCoreException;

   public Result isWorldViewManDaysNeededValid() throws OseeCoreException;

   public Result isWorldViewRemainHoursValid() throws OseeCoreException;

   public String getWorldViewNumberOfReviewIssueDefects() throws OseeCoreException;

   public String getWorldViewNumberOfReviewMajorDefects() throws OseeCoreException;

   public String getWorldViewNumberOfReviewMinorDefects() throws OseeCoreException;

   public String getWorldViewActionsIntiatingWorkflow() throws OseeCoreException;

}
