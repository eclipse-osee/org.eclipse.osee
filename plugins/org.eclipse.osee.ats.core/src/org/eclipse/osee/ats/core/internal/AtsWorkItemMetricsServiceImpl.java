/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ats.core.internal;

import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemMetricsService;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.core.workflow.state.TeamState;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkItemMetricsServiceImpl implements IAtsWorkItemMetricsService {

   private final AtsApi atsApi;

   public AtsWorkItemMetricsServiceImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public void logMetrics(IAtsWorkItem workItem, IStateToken state, AtsUser user, Date date, IAtsChangeSet changes) {
      String hoursSpent = AtsUtil.doubleToI18nString(getHoursSpentTotal(workItem));
      logMetrics(workItem, getPercentCompleteTotal(workItem) + "", hoursSpent, state, user, date, changes);
   }

   @Override
   public void logMetrics(IAtsWorkItem workItem, String percent, String hours, IStateToken state, AtsUser user, Date date, IAtsChangeSet changes) {
      IAtsLogItem logItem = atsApi.getLogFactory().newLogItem(LogType.Metrics, date, user, state.getName(),
         String.format("Percent %s Hours %s", getPercentCompleteTotal(workItem), Double.parseDouble(hours)));
      workItem.getLog().addLogItem(logItem);
   }

   @Override
   public void updateMetrics(IAtsWorkItem workItem, IStateToken state, double additionalHours, int percentComplete, boolean logMetrics, AtsUser user, IAtsChangeSet changes) {
      double hoursSpent = getHoursSpent(workItem);
      double totalHours = hoursSpent + additionalHours;
      if (totalHours < 0.0) {
         totalHours = 0;
      }
      setHoursSpent(workItem, totalHours, changes);
      setPercentComplete(workItem, percentComplete, changes);
      if (logMetrics) {
         logMetrics(workItem, workItem.getStateMgr().getCurrentState(), user, new Date(), changes);
      }
   }

   @Override
   public double getHoursSpent(IAtsWorkItem workItem) {
      return atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.HoursSpent, 0.0);
   }

   @Override
   public void setMetrics(IAtsWorkItem workItem, double hoursSpent, int percentComplete, boolean logMetrics, AtsUser user, Date date, IAtsChangeSet changes) {
      setHoursSpent(workItem, hoursSpent, changes);
      setPercentComplete(workItem, percentComplete, changes);
      if (logMetrics) {
         logMetrics(workItem, String.valueOf(percentComplete), String.valueOf(hoursSpent),
            TeamState.valueOf(workItem.getCurrentStateName()), user, new Date(), changes);
      }
   }

   @Override
   public void setHoursSpent(IAtsWorkItem workItem, double hoursSpent, IAtsChangeSet changes) {
      changes.setSoleAttributeValue(workItem, AtsAttributeTypes.HoursSpent, hoursSpent);
   }

   @Override
   public void setPercentComplete(IAtsWorkItem workItem, Integer percentComplete, IAtsChangeSet changes) {
      changes.setSoleAttributeValue(workItem, AtsAttributeTypes.PercentComplete, percentComplete);
   }

   @Override
   public Integer getPercentComplete(IAtsWorkItem workItem) {
      return atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.PercentComplete, 0);
   }

   @Override
   public double getHoursSpentTotal(IAtsObject atsObject) {
      double hours = 0.0;
      if (atsObject instanceof IAtsAction) {
         for (IAtsTeamWorkflow team : ((IAtsAction) atsObject).getTeamWorkflows()) {
            if (!team.getCurrentStateType().isCancelled()) {
               hours += getHoursSpentTotal(team);
            }
         }
      } else if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         hours = atsApi.getWorkItemMetricsService().getHoursSpent(workItem) + getHoursSpentFromTasks(
            atsObject) + getHoursSpentReview(atsObject);
      }
      return hours;
   }

   @Override
   public double getHoursSpentReview(IAtsObject atsObject) {
      double hours = 0.0;
      if (atsObject instanceof IAtsTeamWorkflow) {
         for (IAtsAbstractReview review : atsApi.getWorkItemService().getReviews((IAtsTeamWorkflow) atsObject)) {
            hours += getHoursSpentTotal(review);
         }
      }
      return hours;
   }

   @Override
   public double getHoursSpentFromTasks(IAtsObject atsObject) {
      double hours = 0.0;
      if (atsObject instanceof IAtsTeamWorkflow) {
         for (IAtsTask taskArt : atsApi.getTaskService().getTasks((IAtsTeamWorkflow) atsObject)) {
            hours += getHoursSpentTotal(taskArt);
         }
      }
      return hours;
   }

   /**
    * Return Percent Complete on all things (including children SMAs) for this SMA<br>
    * <br>
    * percent = all state's percents / number of states (minus completed/canceled)
    */
   @Override
   public int getPercentCompleteTotal(IAtsObject atsObject) {
      int percent = 0;
      if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         if (workItem.getCurrentStateType().isCompletedOrCancelled()) {
            percent = 100;
         } else {
            percent = getPercentCompleteSMASinglePercent(workItem, atsApi);
         }
      }
      return percent;
   }

   /**
    * Add percent represented by percent attribute, percent for reviews and tasks divided by number of objects.
    */
   private int getPercentCompleteSMASinglePercent(IAtsObject atsObject, AtsApi atsApi) {
      int percent = 0;
      if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         int numObjects = 1;
         percent = atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.PercentComplete, 0);
         if (workItem instanceof IAtsTeamWorkflow) {
            for (IAtsAbstractReview revArt : atsApi.getWorkItemService().getReviews((IAtsTeamWorkflow) workItem)) {
               percent += getPercentCompleteTotal(revArt);
               numObjects++;
            }
         }
         if (workItem instanceof IAtsTeamWorkflow) {
            for (IAtsTask taskArt : atsApi.getTaskService().getTasks((IAtsTeamWorkflow) workItem)) {
               percent += getPercentCompleteTotal(taskArt);
               numObjects++;
            }
         }
         if (percent > 0) {
            if (numObjects > 0) {
               percent = percent / numObjects;
            }
         }
      }
      return percent;
   }

   /**
    * Return Percent Complete on all things (including children SMAs) related to stateName. Total Percent for state,
    * tasks and reviews / 1 + # Tasks + # Reviews
    */
   @Override
   public int getPercentCompleteSMAStateTotal(IAtsObject atsObject, IStateToken state, AtsApi atsApi) {
      if (getStateMetricsData(atsObject, state, atsApi) == null) {
         return 0;
      }

      return getStateMetricsData(atsObject, state, atsApi).getResultingPercent();
   }

   private StateMetricsData getStateMetricsData(IAtsObject atsObject, IStateToken teamState, AtsApi atsApi) {
      if (!(atsObject instanceof IAtsWorkItem)) {
         return null;
      }
      IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
      int percent = 0;
      int numObjects = 0;

      // Add percent for each task and bump objects for each task
      if (workItem instanceof IAtsTeamWorkflow) {
         Collection<IAtsTask> tasks = atsApi.getTaskService().getTasks((IAtsTeamWorkflow) workItem, teamState);
         for (IAtsTask taskArt : tasks) {
            percent += getPercentCompleteTotal(taskArt);
         }
         numObjects += tasks.size();
      }

      // Add percent for each review and bump objects for each review
      if (workItem instanceof IAtsTeamWorkflow) {
         Collection<IAtsAbstractReview> reviews =
            atsApi.getWorkItemService().getReviews((IAtsTeamWorkflow) workItem, teamState);
         for (IAtsAbstractReview reviewArt : reviews) {
            percent += getPercentCompleteTotal(reviewArt);
         }
         numObjects += reviews.size();
      }

      return new StateMetricsData(percent, numObjects);
   }

   private static class StateMetricsData {
      public int numObjects = 0;
      public int percent = 0;

      public StateMetricsData(int percent, int numObjects) {
         this.numObjects = numObjects;
         this.percent = percent;
      }

      public int getResultingPercent() {
         return percent / numObjects;
      }

      @Override
      public String toString() {
         return "Percent: " + getResultingPercent() + "  NumObjs: " + numObjects + "  Total Percent: " + percent;
      }
   }

}
