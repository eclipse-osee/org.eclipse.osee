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

import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.agile.AgileItem;
import org.eclipse.osee.ats.api.agile.AgileSprintData;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
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

   public static AgileSprintData getAgileSprintData(IAtsServices services, long teamId, long sprintId, XResultData results) {
      if (teamId <= 0) {
         results.errorf("teamId %s is not valid", teamId);
      }
      if (sprintId <= 0) {
         results.errorf("sprintId %s is not valid", sprintId);
      }
      ArtifactToken sprintArt = services.getArtifact(sprintId);
      if (sprintArt == null) {
         results.errorf("Sprint can not be found with id %s", sprintId);
      }
      IAgileTeam agileTeam = services.getAgileService().getAgileTeam(teamId);
      if (agileTeam == null) {
         results.errorf("AgileTeam can not be found with id %s", teamId);
      }
      if (results.isErrors()) {
         return null;
      }
      IAgileSprint sprint = services.getAgileService().getAgileSprint(sprintArt);

      SprintDataBuilder builder = new SprintDataBuilder(agileTeam, sprint, services, results);
      AgileSprintData sprintData = builder.get();
      return sprintData;
   }

   public static AgileItem getAgileItem(IAgileItem aItem, IAtsServices services) {
      AgileItem item = new AgileItem();
      item.setName(aItem.getName());
      item.setFeatureGroups(Collections.toString("; ", services.getAgileService().getFeatureGroups(aItem)));
      item.setUuid(aItem.getId());
      item.setAssignees(Collections.toString("; ", aItem.getStateMgr().getAssigneesStr()));
      item.setAtsId(aItem.getAtsId());
      item.setState(aItem.getStateMgr().getCurrentStateName());
      item.setChangeType(
         services.getAttributeResolver().getSoleAttributeValue(aItem, AtsAttributeTypes.ChangeType, ""));
      IAtsVersion ver = services.getVersionService().getTargetedVersion(aItem);
      item.setVersion(ver == null ? "" : ver.getName());
      Boolean unplanned =
         services.getAttributeResolver().getSoleAttributeValue(aItem, AtsAttributeTypes.UnPlannedWork, false);
      item.setUnPlannedWork((unplanned ? "U" : ""));
      item.setNotes(services.getAttributeResolver().getSoleAttributeValue(aItem, AtsAttributeTypes.SmaNote, ""));
      item.setCreateDate(CreatedDateColumn.getDateStr(item));
      item.setCompCancelDate(CompletedCancelledDateColumn.getCompletedCancelledDateStr(item));
      item.setLink("/ats/ui/action/" + item.getAtsId());
      return item;
   }
}
