/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.agile;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.AgileSprintData;
import org.eclipse.osee.ats.api.agile.AgileSprintDateData;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class SprintDataBuilder {

   private final AtsApi atsApi;
   private final IAgileSprint sprint;
   private final IAgileTeam agileTeam;
   private final XResultData results;

   public SprintDataBuilder(IAgileTeam agileTeam, IAgileSprint sprint, AtsApi atsApi, XResultData results) {
      this.agileTeam = agileTeam;
      this.sprint = sprint;
      this.atsApi = atsApi;
      this.results = results;
   }

   public AgileSprintData get() {
      AgileSprintData sprintData = new AgileSprintData();
      sprintData.setResults(results);
      try {
         ArtifactToken sprintArt = sprint.getStoreObject();
         sprintData.setSprintName(sprintArt.getName());
         sprintData.setAgileTeamName(agileTeam.getName());

         Date startDate = validateStartDate(sprintData, sprintArt);
         if (startDate == null) {
            return sprintData;
         }

         Date endDate = validateEndDate(sprintData, sprintArt);
         if (endDate == null) {
            return sprintData;
         }

         // retrieve and store holidays
         List<Date> holidays = new LinkedList<>();
         holidays.addAll(atsApi.getAttributeResolver().getAttributeValues(sprintArt, AtsAttributeTypes.Holiday));
         sprintData.setHolidays(holidays);

         // retrieve and store unPlanned points
         Integer unPlannedPoints =
            atsApi.getAttributeResolver().getSoleAttributeValue(sprintArt, AtsAttributeTypes.UnPlannedPoints, 0);
         sprintData.setUnPlannedPoints(unPlannedPoints);

         // retrieve and store planned points
         Integer plannedPoints =
            atsApi.getAttributeResolver().getSoleAttributeValue(sprintArt, AtsAttributeTypes.PlannedPoints, 0);
         sprintData.setPlannedPoints(plannedPoints);

         // store points attribute name
         sprintData.setPointsAttrTypeName(atsApi.getAttributeResolver().getSoleAttributeValue(agileTeam,
            AtsAttributeTypes.PointsAttributeType, AtsAttributeTypes.Points.getName()));

         int totalPoints = unPlannedPoints + plannedPoints;
         long oneDay = 24 * 60 * 60 * 1000;

         computeDateBuckets(sprintData, startDate, endDate, oneDay);

         Collection<IAgileItem> items = atsApi.getAgileService().getItems(sprint);

         computeGoal(sprintData, totalPoints);

         computeTotalUnplannedAndPlannedPoints(sprintData, items);

         computeUnPlannedIncomplete(sprintData);

         computeCompleted(sprintData);

      } catch (Exception ex) {
         sprintData.getResults().error("Error generating burndown data: \n\n" + Lib.exceptionToString(ex));
      }
      return sprintData;
   }

   private void computeTotalUnplannedAndPlannedPoints(AgileSprintData sprintData, Collection<IAgileItem> items) {
      // compute total, unplanned and planned points
      for (IAgileItem item : items) {
         boolean completed = item.isCompletedOrCancelled();
         boolean unPlanned =
            atsApi.getAttributeResolver().getSoleAttributeValue(item, AtsAttributeTypes.UnPlannedWork, false);
         Date completedCancelledDate = item.isCompleted() ? item.getCompletedDate() : item.getCancelledDate();
         // loop through all dates and add points for dates after item was completed/cancelled
         for (AgileSprintDateData dateBucket : sprintData.getDates()) {
            double points = getPoints(sprintData, item);
            if (completed) {
               // only get credit if completed before date bucket
               if (completed && completedCancelledDate.before(dateBucket.getDate())) {
                  if (unPlanned) {
                     if (dateBucket.getCompletedUnPlannedPoints() != null) {
                        dateBucket.setCompletedUnPlannedPoints(dateBucket.getCompletedUnPlannedPoints() + points);
                     } else {
                        dateBucket.setCompletedUnPlannedPoints(points);
                     }
                  } else {
                     if (dateBucket.getCompletedPlannedPoints() != null) {
                        dateBucket.setCompletedPlannedPoints(dateBucket.getCompletedPlannedPoints() + points);
                     } else {
                        dateBucket.setCompletedPlannedPoints(points);
                     }
                  }
               }
            }
            // set points created before this date
            Date createdDate = item.getCreatedDate();
            if (createdDate.before(dateBucket.getDate())) {
               dateBucket.setTotalRealizedPoints(dateBucket.getTotalRealizedPoints() + points);
            }
         }
      }
   }

   private void computeDateBuckets(AgileSprintData sprintData, Date startDate, Date endDate, long oneDay) {
      for (Date date = startDate; date.before(endDate) || date.equals(endDate); date =
         new Date(date.getTime() + oneDay)) {
         // Skip holidays and weekends
         Calendar c = Calendar.getInstance();
         c.setTime(date);
         if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || c.get(
            Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || isHoliday(date, sprintData.getHolidays())) {
            continue;
         }
         AgileSprintDateData sprintDateData = new AgileSprintDateData();
         sprintDateData.setDate(date);
         sprintData.getDates().add(sprintDateData);
      }
   }

   /**
    * @return valid date or null if not set
    */
   private Date validateEndDate(AgileSprintData sprintData, ArtifactToken sprintArt) {
      Date endDate = atsApi.getAttributeResolver().getSoleAttributeValue(sprintArt, AtsAttributeTypes.EndDate, null);
      if (endDate == null) {
         sprintData.getResults().error("End Date must be set on Sprint");
         return null;
      }
      endDate = clearTimeComponent(endDate);
      sprintData.setEndDateAsDate(endDate);
      return endDate;
   }

   /**
    * @return valid date or null if not set
    */
   private Date validateStartDate(AgileSprintData sprintData, ArtifactToken sprintArt) {
      Date startDate =
         atsApi.getAttributeResolver().getSoleAttributeValue(sprintArt, AtsAttributeTypes.StartDate, null);
      if (startDate == null) {
         sprintData.getResults().error("Start Date must be set on Sprint");
         return null;
      }
      startDate = clearTimeComponent(startDate);
      sprintData.setStartDateAsDate(startDate);
      return startDate;
   }

   // Incomplete should just be total unplanned minus total unplanned-completed
   private void computeUnPlannedIncomplete(AgileSprintData burn) {
      for (AgileSprintDateData date : burn.getDates()) {
         Integer unplannedPoints = burn.getUnPlannedPoints();
         Double completedUnPlannedPoints = date.getCompletedUnPlannedPoints();
         if (completedUnPlannedPoints == null) {
            completedUnPlannedPoints = 0.0;
         }
         Double inCompleteUnPlannedPoints = Double.valueOf(unplannedPoints) - completedUnPlannedPoints;
         date.setInCompletedUnPlannedPoints(inCompleteUnPlannedPoints);
      }
   }

   private void computeCompleted(AgileSprintData burn) {
      for (AgileSprintDateData date : burn.getDates()) {
         Double completedUnPlannedPoints = date.getCompletedUnPlannedPoints();
         if (completedUnPlannedPoints == null) {
            completedUnPlannedPoints = 0.0;
         }
         Double copmletedPlannedPoints = date.getCompletedPlannedPoints();
         if (copmletedPlannedPoints == null) {
            copmletedPlannedPoints = 0.0;
         }
         double completed = completedUnPlannedPoints + copmletedPlannedPoints;
         if (completed < 0) {
            completed = 0;
         }
         date.setCompletedPoints(completed);
      }
   }

   private Date clearTimeComponent(Date date) {
      Calendar cal = Calendar.getInstance(); // locale-specific
      cal.setTime(date);
      cal.set(Calendar.HOUR_OF_DAY, 23);
      cal.set(Calendar.MINUTE, 59);
      cal.set(Calendar.SECOND, 59);
      cal.set(Calendar.MILLISECOND, 0);
      cal.set(Calendar.AM_PM, Calendar.PM);
      return cal.getTime();
   }

   private double getPoints(AgileSprintData burn, IAgileItem item) {
      AttributeTypeId pointsType = atsApi.getStoreService().getAttributeType(burn.getPointsAttrTypeName());
      Object value = atsApi.getAttributeResolver().getSoleAttributeValue(item, pointsType, "");
      if (value instanceof Double) {
         return (Double) value;
      } else if (value instanceof String && Strings.isNumeric((String) value)) {
         return Double.valueOf((String) value);
      }
      return 0;
   }

   private void computeGoal(AgileSprintData burn, int totalPoints) {
      int numDates = burn.getDates().size();
      int count = 1;
      int numWorkDays = numDates - 3;
      double pointsPerDay = totalPoints / numWorkDays;
      // set Goal value
      for (AgileSprintDateData date : burn.getDates()) {
         // first day is sprint planning; second gets no work done
         if (count == 1 || count == 2) {
            date.setGoalPoints(Double.valueOf(totalPoints));
         }
         // last day (which counts for 2 buckets) goal is 0
         else if (count == numDates || count == numDates - 1) {
            date.setGoalPoints(Double.valueOf(0L));
         }
         // due to rounding, last day may be more than pointsPerDay
         else {
            double goalPoints = totalPoints - ((count - 2) * pointsPerDay);
            if (goalPoints < 0) {
               goalPoints = 0;
            }
            date.setGoalPoints(goalPoints);
         }
         count++;
      }
   }

   private boolean isHoliday(Date date, List<Date> holidays) {
      Calendar dateCal = Calendar.getInstance();
      dateCal.setTime(date);
      for (Date holiday : holidays) {
         Calendar holCal = Calendar.getInstance();
         holCal.setTime(holiday);
         boolean sameDay = dateCal.get(Calendar.YEAR) == holCal.get(Calendar.YEAR) && dateCal.get(
            Calendar.DAY_OF_YEAR) == holCal.get(Calendar.DAY_OF_YEAR);
         if (sameDay) {
            return true;
         }
      }
      return false;
   }
}
