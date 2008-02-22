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
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.swt.graphics.Image;

public interface IWorldViewArtifact {

   public String getWorldViewNotes();

   public int getWorldViewPercentRework();

   public String getWorldViewNumberOfTasks();

   public String getWorldViewPercentReworkStr();

   public Date getWorldViewEstimatedReleaseDate() throws Exception;

   public String getWorldViewEstimatedReleaseDateStr();

   public String getWorldViewBranchStatus();

   public double getWorldViewRemainHours();

   public double getWorldViewManDaysNeeded();

   public Result isWorldViewAnnualCostAvoidanceValid();

   public double getWorldViewAnnualCostAvoidance();

   public Result isWorldViewRemainHoursValid();

   public Result isWorldViewManDaysNeededValid();

   public Date getWorldViewReleaseDate() throws Exception;

   public String getWorldViewReleaseDateStr();

   public Date getWorldViewDeadlineDate() throws Exception;

   public String getWorldViewDeadlineDateStr();

   /**
    * @return true if the deadline date causes
    */
   public Result isWorldViewDeadlineAlerting();

   public String getWorldViewWorkPackage();

   public String getWorldViewCategory();

   public String getWorldViewCategory2();

   public String getWorldViewCategory3();

   public String getWorldViewType();

   public String getWorldViewTitle();

   public Date getWorldViewCompletedDate() throws Exception;

   public String getWorldViewCompletedDateStr();

   public String getWorldViewDescription();

   public String getWorldViewValidationRequiredStr();

   public boolean isMetricsFromTasks() throws Exception;

   public Date getWorldViewCancelledDate() throws Exception;

   public String getWorldViewCancelledDateStr();

   public ChangeType getWorldViewChangeType();

   public String getWorldViewChangeTypeStr();

   public String getWorldViewState();

   public String getWorldViewTeam();

   public String getWorldViewRelatedToState();

   public String getWorldViewUserCommunity();

   public String getWorldViewResolution();

   public String getWorldViewDecision();

   public String getWorldViewLegacyPCR();

   public String getWorldViewActionableItems();

   public String getWorldViewActivePoc();

   public Image getAssigneeImage();

   public String getWorldViewCreatedDateStr();

   public Date getWorldViewCreatedDate() throws Exception;

   public String getWorldViewOriginator();

   public String getWorldViewImplementer();

   public String getWorldViewReviewAuthor();

   public String getWorldViewReviewModerator();

   public String getWorldViewReviewReviewer();

   public String getWorldViewReviewDecider();

   public String getWorldViewVersion();

   public String getWorldViewID();

   public String getWorldViewPriority();

   public double getWorldViewEstimatedHours();

   public double getWorldViewWeeklyBenefit();

   public int getWorldViewStatePercentComplete();

   public double getWorldViewStateHoursSpent();

   public int getWorldViewTotalPercentComplete();

   public double getWorldViewTotalHoursSpent();

}
