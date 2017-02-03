/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.agile;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.agile.AgileBurndown;
import org.eclipse.osee.ats.api.agile.AgileBurndownDate;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class SprintBurndownDataBuilder {

   private static final String ART_ID_ADDED_TO_SPRINT_QUERY =
      "select rel.B_ART_ID, rel.GAMMA_ID, txd.TIME from OSEE_RELATION_LINK rel, osee_txs " //
         + "txs, osee_tx_details txd where rel.A_ART_ID = ? and rel.REL_LINK_TYPE_ID = 988214123009313457 and rel.B_ART_ID " //
         + "in (ART_IDS) and txs.BRANCH_ID = ? and txs.TX_CURRENT = 1 and rel.GAMMA_ID = txs.GAMMA_ID and txs.TRANSACTION_ID = txd.TRANSACTION_ID";
   private final IAtsServer atsServer;
   private final IAgileSprint sprint;
   private final IAgileTeam agileTeam;
   private final JdbcService jdbcService;

   public SprintBurndownDataBuilder(IAgileTeam agileTeam, IAgileSprint sprint, IAtsServer atsServer, JdbcService jdbcService) {
      this.agileTeam = agileTeam;
      this.sprint = sprint;
      this.atsServer = atsServer;
      this.jdbcService = jdbcService;
   }

   public AgileBurndown get() {
      AgileBurndown burn = new AgileBurndown();
      try {
         ArtifactReadable sprintArt = (ArtifactReadable) sprint.getStoreObject();
         burn.setSprintName(sprintArt.getName());
         burn.setAgileTeamName(agileTeam.getName());
         Date startDate = sprintArt.getSoleAttributeValue(AtsAttributeTypes.StartDate, null);
         if (startDate == null) {
            burn.setError("Start Date must be set on Sprint");
            return burn;
         }
         startDate = clearTimeComponent(startDate);
         burn.setStartDate(startDate);
         Date endDate = sprintArt.getSoleAttributeValue(AtsAttributeTypes.EndDate, null);
         if (endDate == null) {
            burn.setError("End Date must be set on Sprint");
            return burn;
         }
         endDate = clearTimeComponent(endDate);
         burn.setEndDate(endDate);
         burn.setHolidays(sprintArt.getAttributeValues(AtsAttributeTypes.Holiday));
         Integer unPlannedPoints = sprintArt.getSoleAttributeValue(AtsAttributeTypes.UnPlannedPoints, 0);
         burn.setUnPlannedPoints(unPlannedPoints);
         Integer plannedPoints = sprintArt.getSoleAttributeValue(AtsAttributeTypes.PlannedPoints, 0);
         burn.setPlannedPoints(plannedPoints);
         burn.setPointsAttrTypeName(atsServer.getAttributeResolver().getSoleAttributeValue(agileTeam,
            AtsAttributeTypes.PointsAttributeType, AtsAttributeTypes.Points.getName()));

         int totalPoints = unPlannedPoints + plannedPoints;
         long oneDay = 24 * 60 * 60 * 1000;
         if (startDate != null && endDate != null) {
            for (Date date = startDate; date.before(endDate) || date.equals(endDate); date =
               new Date(date.getTime() + oneDay)) {
               // Skip holidays and weekends
               Calendar c = Calendar.getInstance();
               c.setTime(date);
               if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || c.get(
                  Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || isHoliday(date, burn.getHolidays())) {
                  continue;
               }
               AgileBurndownDate bdDate = new AgileBurndownDate();
               bdDate.setDate(date);
               burn.getDates().add(bdDate);
            }
         }
         Collection<IAgileItem> items = atsServer.getAgileService().getItems(sprint);
         computeGoal(burn, totalPoints);

         // compute total, unplanned and planned points
         Date today = new Date();
         Map<Long, Date> artIdToWalkupDate = retrieveWalkupAddedDate(sprint.getId(), items);
         for (IAgileItem item : items) {
            boolean completed = item.isCompletedOrCancelled();
            boolean unPlanned =
               atsServer.getAttributeResolver().getSoleAttributeValue(item, AtsAttributeTypes.UnPlannedWork, false);
            Date completedCancelledDate = item.isCompleted() ? item.getCompletedDate() : item.getCancelledDate();
            // loop through all dates and add points for dates after item was completed/cancelled
            for (AgileBurndownDate dateBucket : burn.getDates()) {
               if (completed) {
                  // all completed cells after today should be set to 0
                  if (completed && dateBucket.getDate().after(today)) {
                     dateBucket.setCompletedPlannedPoints(null);
                     dateBucket.setCompletedPoints(null);
                     dateBucket.setCompletedUnPlannedPoints(null);
                  }
                  // only get credit if completed after the date
                  else if (completed && dateBucket.getDate().after(completedCancelledDate)) {
                     double points = getPoints(burn, item);
                     if (unPlanned) {
                        // Only count walkup after the date it was added to the sprint
                        Date walkupAdded = artIdToWalkupDate.get(item.getId());
                        if (dateBucket.getDate().after(walkupAdded)) {
                           if (dateBucket.getCompletedUnPlannedPoints() != null) {
                              dateBucket.setCompletedUnPlannedPoints(dateBucket.getCompletedUnPlannedPoints() + points);
                           } else {
                              dateBucket.setCompletedUnPlannedPoints(points);
                           }
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
               if (unPlanned) {
                  Date walkupAdded = artIdToWalkupDate.get(item.getId());
                  boolean walkupAddedBeforeDateBucket = walkupAdded.before(dateBucket.getDate());
                  boolean walkupCompletedAfterDateBucket =
                     completed ? completedCancelledDate.after(dateBucket.getDate()) : false;
                  // walkup was inWork at this date
                  if (walkupAddedBeforeDateBucket && (!completed || walkupCompletedAfterDateBucket)) {
                     double points = getPoints(burn, item);
                     if (dateBucket.getInCompletedUnPlannedPoints() != null) {
                        dateBucket.setInCompletedUnPlannedPoints(dateBucket.getInCompletedUnPlannedPoints() + points);
                     } else {
                        dateBucket.setInCompletedUnPlannedPoints(points);
                     }

                  }

               }
            }
         }
         computeCompleted(burn);
         computeUnrealizedWalkup(burn);
      } catch (Exception ex) {
         burn.setError("Error generating burndown data: \n\n" + Lib.exceptionToString(ex));
      }
      return burn;
   }

   private void computeCompleted(AgileBurndown burn) {
      for (AgileBurndownDate date : burn.getDates()) {
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

   private void computeUnrealizedWalkup(AgileBurndown burn) {
      for (AgileBurndownDate date : burn.getDates()) {
         Double completedUnPlannedPoints = date.getCompletedUnPlannedPoints();
         if (completedUnPlannedPoints == null) {
            completedUnPlannedPoints = 0.0;
         }
         Double inCompleteUnPlannedPoints = date.getInCompletedUnPlannedPoints();
         if (inCompleteUnPlannedPoints == null) {
            inCompleteUnPlannedPoints = 0.0;
         }
         double unRealizedWalkup = burn.getUnPlannedPoints() - completedUnPlannedPoints - inCompleteUnPlannedPoints;
         if (unRealizedWalkup < 0) {
            unRealizedWalkup = 0;
         }
         date.setUnRealizedWalkup(unRealizedWalkup);
      }
   }

   private Map<Long, Date> retrieveWalkupAddedDate(Long sprintId, Collection<IAgileItem> items) {
      List<Long> artIds = new LinkedList<>();
      for (IAgileItem item : items) {
         boolean unPlanned =
            atsServer.getAttributeResolver().getSoleAttributeValue(item, AtsAttributeTypes.UnPlannedWork, false);
         if (unPlanned) {
            artIds.add(item.getId());
         }
      }
      JdbcStatement chStmt = jdbcService.getClient().getStatement();
      Map<Long, Date> artIdToWalkupDate = new HashMap<>();
      String artIdsStr = Collections.toString(",", artIds);
      try {
         String query = ART_ID_ADDED_TO_SPRINT_QUERY.replaceFirst("ART_IDS", artIdsStr);
         chStmt.runPreparedQuery(query, sprintId, atsServer.getAtsBranch().getId());
         while (chStmt.next()) {
            long artId = chStmt.getLong("B_ART_ID");
            Date date = chStmt.getDate("time");
            artIdToWalkupDate.put(artId, date);
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      } finally {
         chStmt.close();
      }
      return artIdToWalkupDate;
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

   private double getPoints(AgileBurndown burn, IAgileItem item) {
      AttributeTypeId pointsType =
         atsServer.getOrcsApi().getOrcsTypes().getAttributeTypes().getByName(burn.getPointsAttrTypeName());
      String value = atsServer.getAttributeResolver().getSoleAttributeValueAsString(item, pointsType, "");
      if (Strings.isNumeric(value)) {
         return Double.valueOf(value);
      }
      return 0;
   }

   private void computeGoal(AgileBurndown burn, int totalPoints) {
      int numDates = burn.getDates().size();
      int count = 1;
      int numWorkDays = numDates - 2;
      double pointsPerDay = totalPoints / numWorkDays;
      // set Goal value
      for (AgileBurndownDate date : burn.getDates()) {
         // first day is sprint planning; second gets no work done
         if (count == 1 || count == 2) {
            date.setGoalPoints(Double.valueOf(totalPoints));
         }
         // last day should be done
         else if (count == numDates) {
            date.setGoalPoints(Double.valueOf(0));
         } else {
            double goalPoints = totalPoints - (count * pointsPerDay);
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
