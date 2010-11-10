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

   String getAssigneeStr() throws OseeCoreException;

   String getState() throws OseeCoreException;

   String getWorldViewBranchStatus() throws OseeCoreException;

   String getWorldViewOriginatingWorkflowStr() throws OseeCoreException;

   Collection<TeamWorkFlowArtifact> getWorldViewOriginatingWorkflows() throws OseeCoreException;

   Date getWorldViewCancelledDate() throws OseeCoreException;

   String getWorldViewCancelledDateStr() throws OseeCoreException;

   String getWorldViewNumeric1() throws OseeCoreException;

   String getWorldViewNumeric2() throws OseeCoreException;

   Date getWorldViewCompletedDate() throws OseeCoreException;

   String getWorldViewCompletedDateStr() throws OseeCoreException;

   /**
    * @return estimated hours from workflow attribute, tasks and reviews
    */
   double getWorldViewEstimatedHours() throws OseeCoreException;

   double getWorldViewHoursSpentState() throws OseeCoreException;

   double getWorldViewHoursSpentStateReview() throws OseeCoreException;

   double getWorldViewHoursSpentStateTask() throws OseeCoreException;

   double getWorldViewHoursSpentStateTotal() throws OseeCoreException;

   double getWorldViewHoursSpentTotal() throws OseeCoreException;

   String getWorldViewID() throws OseeCoreException;

   String getWorldViewDaysInCurrentState() throws OseeCoreException;

   String getWorldViewImplementer() throws OseeCoreException;

   double getWorldViewManDaysNeeded() throws OseeCoreException;

   String getWorldViewNumberOfTasks() throws OseeCoreException;

   String getWorldViewNumberOfTasksRemaining() throws OseeCoreException;

   String getWorldViewLastStatused() throws OseeCoreException;

   int getWorldViewPercentCompleteState() throws OseeCoreException;

   int getWorldViewPercentCompleteStateReview() throws OseeCoreException;

   int getWorldViewPercentCompleteStateTask() throws OseeCoreException;

   int getWorldViewPercentCompleteTotal() throws OseeCoreException;

   int getWorldViewPercentRework() throws OseeCoreException;

   String getWorldViewPercentReworkStr() throws OseeCoreException;

   String getWorldViewPriority() throws OseeCoreException;

   double getWorldViewRemainHours() throws OseeCoreException;

   String getWorldViewReviewAuthor() throws OseeCoreException;

   String getWorldViewReviewDecider() throws OseeCoreException;

   String getWorldViewReviewModerator() throws OseeCoreException;

   String getWorldViewReviewReviewer() throws OseeCoreException;

   String getWorldViewTitle() throws OseeCoreException;

   String getType() throws OseeCoreException;

   String getWorldViewValidationRequiredStr() throws OseeCoreException;

   double getWorldViewWeeklyBenefit() throws OseeCoreException;

   Result isWorldViewManDaysNeededValid() throws OseeCoreException;

   Result isWorldViewRemainHoursValid() throws OseeCoreException;

   String getWorldViewNumberOfReviewIssueDefects() throws OseeCoreException;

   String getWorldViewNumberOfReviewMajorDefects() throws OseeCoreException;

   String getWorldViewNumberOfReviewMinorDefects() throws OseeCoreException;

   String getWorldViewActionsIntiatingWorkflow() throws OseeCoreException;

}
