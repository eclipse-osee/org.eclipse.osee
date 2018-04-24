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

import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.AgileItem;
import org.eclipse.osee.ats.api.agile.AgileSprintData;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.column.CompletedCancelledDateColumn;
import org.eclipse.osee.ats.core.column.CreatedDateColumn;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class SprintUtil {

   public static String RGB_GREEN = "3, 127, 24";
   public static String RGB_BLACK = "0, 0, 0";
   public static String RGB_BLUE = "0, 4, 140";
   public static String RGB_YELLOW = "252, 252, 2";
   public static final String POINTS = "Points";
   public static final String DATES = "Dates";
   public static final String COMPLETED_UN_PLANNED = "Completed Un-Planned";
   public static final String COMPLETED_PLANNED = "Completed Planned";
   public static final String TOTAL_COMPLETED = "Total Completed";
   public static final String TOTAL_POINTS = "Total Points";
   public static final String TOTAL_REALIZED_POINTS = "Total Realized Points";
   public static final String REMAINING_WORK = "Remaining Work";
   public static final String TOTAL_WORK = "Total Work";

   private SprintUtil() {
      // utility class
   }

   public static AgileSprintData updateAgileSprintData(AtsApi atsApi, long teamId, long sprintId, AgileSprintData sprintData, XResultData results) {
      IAgileSprint sprint = atsApi.getAgileService().getAgileSprint(sprintId);
      if (sprint == null) {
         sprintData.getResults().errorf("Sprint can not be found with id %s", sprintId);
         return sprintData;
      }
      IAtsChangeSet changes = atsApi.createChangeSet("Update Agile Sprint Data");
      if (sprintData.getStartDate() != null) {
         Date startDate = null;
         try {
            startDate = sprintData.getStartDateAsDate();
            if (startDate != null) {
               changes.setSoleAttributeValue(sprint, AtsAttributeTypes.StartDate, startDate);
            }
         } catch (Exception ex) {
            // do nothing
         }
      }
      if (sprintData.getEndDate() != null) {
         Date endDate = null;
         try {
            endDate = sprintData.getEndDateAsDate();
            if (endDate != null) {
               changes.setSoleAttributeValue(sprint, AtsAttributeTypes.EndDate, endDate);
            }
         } catch (Exception ex) {
            // do nothing
         }
      }
      if (sprintData.getUnPlannedPoints() != null) {
         changes.setSoleAttributeValue(sprint, AtsAttributeTypes.UnPlannedPoints, sprintData.getUnPlannedPoints());
      }
      if (sprintData.getPlannedPoints() != null) {
         changes.setSoleAttributeValue(sprint, AtsAttributeTypes.PlannedPoints, sprintData.getPlannedPoints());
      }
      changes.executeIfNeeded();
      return SprintUtil.getAgileSprintData(atsApi, teamId, sprintId, results);
   }

   public static AgileSprintData getAgileSprintData(AtsApi atsApi, long teamId, long sprintId, XResultData results) {
      if (teamId <= 0) {
         results.errorf("teamId %s is not valid", teamId);
      }
      if (sprintId <= 0) {
         results.errorf("sprintId %s is not valid", sprintId);
      }
      ArtifactToken sprintArt = atsApi.getQueryService().getArtifact(sprintId);
      if (sprintArt == null) {
         results.errorf("Sprint can not be found with id %s", sprintId);
      }
      IAgileTeam agileTeam = atsApi.getAgileService().getAgileTeam(teamId);
      if (agileTeam == null) {
         results.errorf("AgileTeam can not be found with id %s", teamId);
      }
      if (results.isErrors()) {
         return null;
      }
      IAgileSprint sprint = atsApi.getAgileService().getAgileSprint(sprintArt);

      SprintDataBuilder builder = new SprintDataBuilder(agileTeam, sprint, atsApi, results);
      AgileSprintData sprintData = builder.get();
      return sprintData;
   }

   public static AgileItem getAgileItem(IAgileItem aItem, AtsApi atsApi) {
      AgileItem item = new AgileItem();
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(aItem.getStoreObject());
      item.setName(aItem.getName());
      item.setFeatureGroups(Collections.toString("; ", atsApi.getAgileService().getFeatureGroups(aItem)));
      item.setId(aItem.getId());
      if (aItem.isCompletedOrCancelled()) {
         String implementers = atsApi.getImplementerService().getImplementersStr(aItem);
         item.setImplementers(implementers);
         item.setAssigneesOrImplementers("(" + implementers + ")");
      } else {
         String assignees = Collections.toString("; ", aItem.getStateMgr().getAssigneesStr());
         item.setAssignees(assignees);
         item.setAssigneesOrImplementers(assignees);
      }
      item.setAtsId(workItem.getAtsId());
      item.setState(aItem.getStateMgr().getCurrentStateName());
      item.setChangeType(atsApi.getAttributeResolver().getSoleAttributeValue(aItem, AtsAttributeTypes.ChangeType, ""));
      item.setAgilePoints(atsApi.getAgileService().getAgileTeamPointsStr(workItem));
      IAtsVersion ver = atsApi.getVersionService().getTargetedVersion(workItem);
      item.setVersion(ver == null ? "" : ver.getName());
      Boolean unplanned =
         atsApi.getAttributeResolver().getSoleAttributeValue(aItem, AtsAttributeTypes.UnPlannedWork, false);
      item.setUnPlannedWork((unplanned ? "U" : ""));
      item.setNotes(atsApi.getAttributeResolver().getSoleAttributeValue(aItem, AtsAttributeTypes.SmaNote, ""));
      item.setCreateDate(CreatedDateColumn.getDateStr(workItem));
      item.setCompCancelDate(CompletedCancelledDateColumn.getCompletedCancelledDateStr(workItem));
      item.setLink("/ats/ui/action/" + item.getAtsId());
      return item;
   }

}
