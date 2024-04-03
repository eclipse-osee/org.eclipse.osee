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

package org.eclipse.osee.ats.core.util;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueService;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G. Dunne
 */
public abstract class AtsAbstractEarnedValueImpl implements IAtsEarnedValueService {

   protected final Log logger;
   protected final AtsApi atsApi;

   public AtsAbstractEarnedValueImpl(Log logger, AtsApi atsApi) {
      this.logger = logger;
      this.atsApi = atsApi;
   }

   @Override
   public double getEstimatedHoursFromArtifact(IAtsWorkItem workItem) {
      return atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.EstimatedHours, 0.0);
   }

   /**
    * Return Estimated Hours for all tasks
    */
   @Override
   public double getEstimatedHoursFromTasks(IAtsWorkItem workItem) {
      if (!(workItem instanceof IAtsTeamWorkflow)) {
         return 0;
      }
      double hours = 0;
      for (IAtsTask task : atsApi.getTaskService().getTask(workItem)) {
         hours += getEstimatedHoursFromArtifact(task);
      }
      return hours;
   }

   @Override
   public double getEstimatedHoursFromReviews(IAtsWorkItem workItem) {
      double hours = 0;
      if (workItem.isTeamWorkflow()) {
         for (IAtsAbstractReview review : atsApi.getReviewService().getReviews((IAtsTeamWorkflow) workItem)) {
            hours += getEstimatedHoursFromArtifact(review);
         }
      }
      return hours;
   }

   @Override
   public double getEstimatedHoursFromReviews(IAtsWorkItem workItem, IStateToken relatedToState) {
      double hours = 0;
      if (workItem.isTeamWorkflow()) {
         IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;
         for (IAtsAbstractReview review : atsApi.getReviewService().getReviews(teamWf)) {
            if (review.getRelatedToState().equals(relatedToState.getName())) {
               hours += getEstimatedHoursFromArtifact(review);
            }
         }
      }
      return hours;
   }

   @Override
   public double getEstimatedHoursTotal(IAtsWorkItem workItem) {
      return getEstimatedHoursFromArtifact(workItem) + getEstimatedHoursFromTasks(
         workItem) + getEstimatedHoursFromReviews(workItem);
   }

   @Override
   public double getRemainHoursFromArtifact(IAtsWorkItem workItem) {
      if (workItem.isCompletedOrCancelled()) {
         return 0;
      }
      double est = atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.EstimatedHours, 0.0);
      if (est == 0) {
         return getEstimatedHoursFromArtifact(workItem);
      }
      return est - est * atsApi.getWorkItemMetricsService().getPercentCompleteTotal(workItem) / 100.0;
   }

   @Override
   public double getRemainHoursTotal(IAtsWorkItem workItem) {
      return getRemainHoursFromArtifact(workItem) + getRemainFromTasks(workItem) + getRemainFromReviews(workItem);
   }

   /**
    * Return Remain Hours for all tasks
    */
   @Override
   public double getRemainFromTasks(IAtsWorkItem workItem) {
      if (!workItem.isTeamWorkflow()) {
         return 0;
      }
      double hours = 0;
      for (IAtsTask task : atsApi.getTaskService().getTasks((IAtsTeamWorkflow) workItem)) {
         hours += getRemainHoursFromArtifact(task);
      }
      return hours;
   }

   @Override
   public double getRemainFromReviews(IAtsWorkItem workItem) {
      if (workItem.isTeamWorkflow()) {
         return atsApi.getEarnedValueService().getEstimatedHoursFromReviews(workItem);
      }
      return 0;
   }

   @Override
   public double getManHrsPerDayPreference() {
      return AtsUtil.DEFAULT_HOURS_PER_WORK_DAY;
   }

   /**
    * Return Total Percent Complete / # Tasks for "Related to State" stateName
    *
    * @param relatedToState state name of parent workflow's state
    */
   @Override
   public int getPercentCompleteFromTasks(IAtsWorkItem workItem, IStateToken relatedToState) {
      int spent = 0, result = 0;
      if (workItem.isTeamWorkflow()) {
         Collection<IAtsTask> tasks = atsApi.getTaskService().getTasks((IAtsTeamWorkflow) workItem, relatedToState);
         for (IAtsTask task : tasks) {
            spent += atsApi.getWorkItemMetricsService().getPercentCompleteTotal(task);
         }
         if (spent > 0) {
            result = spent / tasks.size();
         }
      }
      return result;
   }

   @Override
   public int getPercentCompleteFromTasks(IAtsWorkItem workItem) {
      int spent = 0, result = 0;
      if (workItem.isTeamWorkflow()) {
         Collection<IAtsTask> tasks = atsApi.getTaskService().getTasks((IAtsTeamWorkflow) workItem);
         for (IAtsTask task : tasks) {
            spent += atsApi.getWorkItemMetricsService().getPercentCompleteTotal(task);
         }
         if (spent > 0) {
            result = spent / tasks.size();
         }
      }
      return result;
   }

   /**
    * Return Total Percent Complete / # Reviews for "Related to State" stateName
    *
    * @param relatedToState state name of parent workflow's state
    */
   @Override
   public int getPercentCompleteFromReviews(IAtsWorkItem workItem, IStateToken relatedToState) {
      int spent = 0;
      if (workItem.isTeamWorkflow()) {
         Collection<IAtsAbstractReview> reviews =
            atsApi.getReviewService().getReviews((IAtsTeamWorkflow) workItem, relatedToState);
         for (IAtsAbstractReview review : reviews) {
            spent += atsApi.getWorkItemMetricsService().getPercentCompleteTotal(review);
         }
         if (spent == 0) {
            return 0;
         }
         spent = spent / reviews.size();
      }
      return spent;
   }

}
