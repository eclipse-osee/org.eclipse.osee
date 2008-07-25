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

   public Image getAssigneeImage() throws OseeCoreException, SQLException;

   public String getWorldViewActionableItems() throws OseeCoreException, SQLException;

   public String getWorldViewActivePoc() throws OseeCoreException, SQLException;

   public double getWorldViewAnnualCostAvoidance() throws OseeCoreException, SQLException;

   public String getWorldViewBranchStatus() throws OseeCoreException, SQLException;

   public Date getWorldViewCancelledDate() throws OseeCoreException, SQLException;

   public String getWorldViewCancelledDateStr() throws OseeCoreException, SQLException;

   public String getWorldViewCategory() throws OseeCoreException, SQLException;

   public String getWorldViewCategory2() throws OseeCoreException, SQLException;

   public String getWorldViewCategory3() throws OseeCoreException, SQLException;

   public ChangeType getWorldViewChangeType() throws OseeCoreException, SQLException;

   public String getWorldViewChangeTypeStr() throws OseeCoreException, SQLException;

   public Date getWorldViewCompletedDate() throws OseeCoreException, SQLException;

   public String getWorldViewCompletedDateStr() throws OseeCoreException, SQLException;

   public Date getWorldViewCreatedDate() throws OseeCoreException, SQLException;

   public String getWorldViewCreatedDateStr() throws OseeCoreException, SQLException;

   public Date getWorldViewDeadlineDate() throws OseeCoreException, SQLException;

   public String getWorldViewDeadlineDateStr() throws OseeCoreException, SQLException;

   public String getWorldViewDecision() throws OseeCoreException, SQLException;

   public String getWorldViewDescription() throws OseeCoreException, SQLException;

   public double getWorldViewEstimatedHours() throws OseeCoreException, SQLException;

   public Date getWorldViewEstimatedReleaseDate() throws OseeCoreException, SQLException;

   public String getWorldViewEstimatedReleaseDateStr() throws OseeCoreException, SQLException;

   public double getWorldViewHoursSpentState() throws OseeCoreException, SQLException;

   public double getWorldViewHoursSpentStateReview() throws OseeCoreException, SQLException;

   public double getWorldViewHoursSpentStateTask() throws OseeCoreException, SQLException;

   public double getWorldViewHoursSpentStateTotal() throws OseeCoreException, SQLException;

   public double getWorldViewHoursSpentTotal() throws OseeCoreException, SQLException;

   public String getWorldViewID() throws OseeCoreException, SQLException;

   public String getWorldViewImplementer() throws OseeCoreException, SQLException;

   public String getWorldViewLegacyPCR() throws OseeCoreException, SQLException;

   public String getWorldViewSWEnhancement() throws OseeCoreException, SQLException;

   public double getWorldViewManDaysNeeded() throws OseeCoreException, SQLException;

   public String getWorldViewNotes() throws OseeCoreException, SQLException;

   public String getWorldViewNumberOfTasks() throws OseeCoreException, SQLException;

   public String getWorldViewLastUpdated() throws OseeCoreException, SQLException;

   public String getWorldViewLastStatused() throws OseeCoreException, SQLException;

   public String getWorldViewOriginator() throws OseeCoreException, SQLException;

   public int getWorldViewPercentCompleteState() throws OseeCoreException, SQLException;

   public int getWorldViewPercentCompleteStateReview() throws OseeCoreException, SQLException;

   public int getWorldViewPercentCompleteStateTask() throws OseeCoreException, SQLException;

   public int getWorldViewPercentCompleteTotal() throws OseeCoreException, SQLException;

   public int getWorldViewPercentRework() throws OseeCoreException, SQLException;

   public String getWorldViewPercentReworkStr() throws OseeCoreException, SQLException;

   public String getWorldViewPriority() throws OseeCoreException, SQLException;

   public String getWorldViewRelatedToState() throws OseeCoreException, SQLException;

   public Date getWorldViewReleaseDate() throws OseeCoreException, SQLException;

   public String getWorldViewReleaseDateStr() throws OseeCoreException, SQLException;

   public double getWorldViewRemainHours() throws OseeCoreException, SQLException;

   public String getWorldViewResolution() throws OseeCoreException, SQLException;

   public String getWorldViewReviewAuthor() throws OseeCoreException, SQLException;

   public String getWorldViewReviewDecider() throws OseeCoreException, SQLException;

   public String getWorldViewReviewModerator() throws OseeCoreException, SQLException;

   public String getWorldViewReviewReviewer() throws OseeCoreException, SQLException;

   public String getWorldViewState() throws OseeCoreException, SQLException;

   public String getWorldViewTeam() throws OseeCoreException, SQLException;

   public String getWorldViewTitle() throws OseeCoreException, SQLException;

   public String getWorldViewType() throws OseeCoreException, SQLException;

   public String getWorldViewUserCommunity() throws OseeCoreException, SQLException;

   public String getWorldViewValidationRequiredStr() throws OseeCoreException, SQLException;

   public String getWorldViewVersion() throws OseeCoreException, SQLException;

   public double getWorldViewWeeklyBenefit() throws OseeCoreException, SQLException;

   public String getWorldViewWorkPackage() throws OseeCoreException, SQLException;

   public Result isWorldViewAnnualCostAvoidanceValid() throws OseeCoreException, SQLException;

   public Result isWorldViewDeadlineAlerting() throws OseeCoreException, SQLException;

   public Result isWorldViewManDaysNeededValid() throws OseeCoreException, SQLException;

   public Result isWorldViewRemainHoursValid() throws OseeCoreException, SQLException;

}
