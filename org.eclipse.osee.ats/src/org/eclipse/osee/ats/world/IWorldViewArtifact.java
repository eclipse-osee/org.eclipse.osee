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

import java.sql.SQLException;
import java.util.Date;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.swt.graphics.Image;

public interface IWorldViewArtifact {

   public Image getAssigneeImage() throws Exception;

   public String getWorldViewActionableItems() throws Exception;

   public String getWorldViewActivePoc() throws Exception;

   public double getWorldViewAnnualCostAvoidance() throws Exception;

   public String getWorldViewBranchStatus() throws Exception;

   public Date getWorldViewCancelledDate() throws Exception;

   public String getWorldViewCancelledDateStr() throws Exception;

   public String getWorldViewCategory() throws Exception;

   public String getWorldViewCategory2() throws Exception;

   public String getWorldViewCategory3() throws Exception;

   public ChangeType getWorldViewChangeType() throws Exception;

   public String getWorldViewChangeTypeStr() throws Exception;

   public Date getWorldViewCompletedDate() throws Exception;

   public String getWorldViewCompletedDateStr() throws Exception;

   public Date getWorldViewCreatedDate() throws Exception;

   public String getWorldViewCreatedDateStr() throws Exception;

   public Date getWorldViewDeadlineDate() throws Exception;

   public String getWorldViewDeadlineDateStr() throws Exception;

   public String getWorldViewDecision() throws Exception;

   public String getWorldViewDescription() throws Exception;

   public double getWorldViewEstimatedHours() throws Exception;

   public Date getWorldViewEstimatedReleaseDate() throws Exception;

   public String getWorldViewEstimatedReleaseDateStr() throws Exception;

   public double getWorldViewHoursSpentState() throws Exception;

   public double getWorldViewHoursSpentStateReview() throws Exception;

   public double getWorldViewHoursSpentStateTask() throws Exception;

   public double getWorldViewHoursSpentStateTotal() throws Exception;

   public double getWorldViewHoursSpentTotal() throws Exception;

   public String getWorldViewID() throws Exception;

   public String getWorldViewImplementer() throws Exception;

   public String getWorldViewLegacyPCR() throws Exception;

   public double getWorldViewManDaysNeeded() throws Exception;

   public String getWorldViewNotes() throws Exception;

   public String getWorldViewNumberOfTasks() throws Exception;

   public String getWorldViewLastUpdated() throws OseeCoreException, SQLException;

   public String getWorldViewLastStatused() throws OseeCoreException, SQLException;

   public String getWorldViewOriginator() throws Exception;

   public int getWorldViewPercentCompleteState() throws Exception;

   public int getWorldViewPercentCompleteStateReview() throws Exception;

   public int getWorldViewPercentCompleteStateTask() throws Exception;

   public int getWorldViewPercentCompleteTotal() throws Exception;

   public int getWorldViewPercentRework() throws Exception;

   public String getWorldViewPercentReworkStr() throws Exception;

   public String getWorldViewPriority() throws Exception;

   public String getWorldViewRelatedToState() throws Exception;

   public Date getWorldViewReleaseDate() throws Exception;

   public String getWorldViewReleaseDateStr() throws Exception;

   public double getWorldViewRemainHours() throws Exception;

   public String getWorldViewResolution() throws Exception;

   public String getWorldViewReviewAuthor() throws Exception;

   public String getWorldViewReviewDecider() throws Exception;

   public String getWorldViewReviewModerator() throws Exception;

   public String getWorldViewReviewReviewer() throws Exception;

   public String getWorldViewState() throws Exception;

   public String getWorldViewTeam() throws Exception;

   public String getWorldViewTitle() throws Exception;

   public String getWorldViewType() throws Exception;

   public String getWorldViewUserCommunity() throws Exception;

   public String getWorldViewValidationRequiredStr() throws Exception;

   public String getWorldViewVersion() throws Exception;

   public double getWorldViewWeeklyBenefit() throws Exception;

   public String getWorldViewWorkPackage() throws Exception;

   public Result isWorldViewAnnualCostAvoidanceValid() throws Exception;

   public Result isWorldViewDeadlineAlerting() throws Exception;

   public Result isWorldViewManDaysNeededValid() throws Exception;

   public Result isWorldViewRemainHoursValid() throws Exception;

}
