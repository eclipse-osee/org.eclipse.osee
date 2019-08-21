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
package org.eclipse.osee.ats.rest.internal.agile.operations;

import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.sprint.SprintConfigurations;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.agile.AgileUtil;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class SprintConfigOperations {

   private final AtsApi atsApi;

   public SprintConfigOperations(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public SprintConfigurations get(ArtifactId sprintId) {
      IAgileSprint sprint = atsApi.getAgileService().getAgileSprint(sprintId.getId());
      SprintConfigurations configs = new SprintConfigurations();
      configs.setId(sprintId);
      Date startDate = atsApi.getAttributeResolver().getSoleAttributeValue(sprint, AtsAttributeTypes.StartDate, null);
      if (startDate != null) {
         configs.setStartDate(DateUtil.get(startDate, DateUtil.YYYY_MM_DD_WITH_DASHES));
      }
      Date endDate = atsApi.getAttributeResolver().getSoleAttributeValue(sprint, AtsAttributeTypes.EndDate, null);
      if (endDate != null) {
         configs.setEndDate(DateUtil.get(endDate, DateUtil.YYYY_MM_DD_WITH_DASHES));
      }
      configs.setPlannedPoints(
         atsApi.getAttributeResolver().getSoleAttributeValue(sprint, AtsAttributeTypes.PlannedPoints, 0).toString());
      configs.setUnPlannedPoints(
         atsApi.getAttributeResolver().getSoleAttributeValue(sprint, AtsAttributeTypes.UnplannedPoints, 0).toString());
      for (Object holiday : atsApi.getAttributeResolver().getAttributeValues(sprint, AtsAttributeTypes.Holiday)) {
         if (holiday instanceof Date) {
            configs.addHoliday(AgileUtil.getDateStr((Date) holiday));
         }
      }
      return configs;
   }

   public SprintConfigurations update(SprintConfigurations sprintConfig) {
      IAgileSprint sprint = atsApi.getAgileService().getAgileSprint(sprintConfig.getId());
      if (sprint == null) {
         sprintConfig.getResults().errorf("No sprint found with id %s", sprintConfig.getId());
         return sprintConfig;
      }
      IAtsChangeSet changes = atsApi.createChangeSet("Update Sprint Configurations for " + sprint.toStringWithId());
      updateDateIfNecessary(sprintConfig.getStartDate(), sprintConfig, sprint, AtsAttributeTypes.StartDate, changes);
      if (sprintConfig.getResults().isErrors()) {
         return sprintConfig;
      }
      updateDateIfNecessary(sprintConfig.getEndDate(), sprintConfig, sprint, AtsAttributeTypes.EndDate, changes);
      if (sprintConfig.getResults().isErrors()) {
         return sprintConfig;
      }
      updatePointsIfNecessary(sprintConfig.getPlannedPoints(), sprintConfig, sprint, AtsAttributeTypes.PlannedPoints,
         changes);
      if (sprintConfig.getResults().isErrors()) {
         return sprintConfig;
      }
      updatePointsIfNecessary(sprintConfig.getUnPlannedPoints(), sprintConfig, sprint,
         AtsAttributeTypes.UnplannedPoints, changes);
      if (sprintConfig.getResults().isErrors()) {
         return sprintConfig;
      }
      // TTD handle holidays
      TransactionId transaction = changes.executeIfNeeded();
      if (transaction == null) {
         sprintConfig.getResults().error("No changes made");
      }
      return sprintConfig;
   }

   private void updatePointsIfNecessary(String newPointsStr, SprintConfigurations sprintConfig, IAgileSprint sprint, AttributeTypeToken attrType, IAtsChangeSet changes) {
      if (Strings.isValid(newPointsStr)) {
         try {
            if (Strings.isInValid(newPointsStr)) {
               changes.deleteAttributes(sprint, attrType);
            } else {
               if (Strings.isInValid(newPointsStr)) {
                  changes.deleteAttributes(sprint, attrType);
               } else {
                  Integer storedPoints = atsApi.getAttributeResolver().getSoleAttributeValue(sprint, attrType, 0);
                  Integer newPoints = Integer.valueOf(newPointsStr);
                  if (!newPoints.equals(storedPoints)) {
                     changes.setSoleAttributeValue(sprint, attrType, newPoints);
                  }
               }
            }
         } catch (Exception ex) {
            sprintConfig.getResults().errorf("Invalid %s [%s], must be [%s]", attrType.getName(), newPointsStr,
               DateUtil.YYYY_MM_DD_WITH_DASHES);
         }
      }
   }

   private void updateDateIfNecessary(String newDateStr, SprintConfigurations sprintConfig, IAgileSprint sprint, AttributeTypeToken attrType, IAtsChangeSet changes) {
      if (Strings.isValid(newDateStr)) {
         try {
            if (Strings.isInValid(newDateStr)) {
               changes.deleteAttributes(sprint, attrType);
            } else {
               Date storedDate = atsApi.getAttributeResolver().getSoleAttributeValue(sprint, attrType, null);
               Date newStartDate = DateUtil.getDate(DateUtil.YYYY_MM_DD_WITH_DASHES, newDateStr);
               if (!newStartDate.equals(storedDate)) {
                  changes.setSoleAttributeValue(sprint, attrType, newStartDate);
               }
            }
         } catch (Exception ex) {
            sprintConfig.getResults().errorf("Invalid %s [%s], must be [%s]", attrType.getName(), newDateStr,
               DateUtil.YYYY_MM_DD_WITH_DASHES);
         }
      }
   }

}
