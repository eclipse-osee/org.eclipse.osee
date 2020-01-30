/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workflow.util;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class CopyActionDetails {

   private final IAtsWorkItem workItem;
   private static final String USE_DEVELOPER_CHANGE_TYPES = "UseDeveloperChangeTypes";
   private final AtsApi atsApi;

   public CopyActionDetails(IAtsWorkItem workItem, AtsApi atsApi) {
      this.workItem = workItem;
      this.atsApi = atsApi;
   }

   public String getDetailsString() {
      String detailsStr = "";
      try {
         if (workItem.getParentTeamWorkflow() != null) {
            IAtsTeamDefinition teamDef = workItem.getParentTeamWorkflow().getTeamDefinition();
            String formatStr = getFormatStr(teamDef);
            if (Strings.isValid(formatStr)) {
               detailsStr = formatStr;
               IAtsAction action = workItem.getParentAction();
               if (action != null) {
                  detailsStr = detailsStr.replaceAll("<actionatsid>", action.getAtsId());
               }
               String legacyPcrId =
                  atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.LegacyPcrId, null);
               if (Strings.isValid(legacyPcrId)) {
                  detailsStr = detailsStr.replaceAll("<legacypcrid>", " - [" + legacyPcrId + "]");
               } else {
                  detailsStr = detailsStr.replaceAll("<legacypcrid>", "");
               }
               detailsStr = detailsStr.replaceAll("<atsid>", workItem.getAtsId());
               detailsStr = detailsStr.replaceAll("<name>", workItem.getName());
               detailsStr = detailsStr.replaceAll("<artType>", workItem.getArtifactTypeName());
               detailsStr = detailsStr.replaceAll("<changeType>", getChangeTypeOrObjectType(workItem));
            }
         }
         if (!Strings.isValid(detailsStr)) {
            detailsStr =
               "\"" + workItem.getArtifactTypeName() + "\" - " + workItem.getAtsId() + " - \"" + workItem.getName() + "\"";
         }
      } catch (Exception ex) {
         OseeLog.log(CopyActionDetails.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return detailsStr;
   }

   private String getChangeTypeOrObjectType(IAtsWorkItem workItem) {
      String result = "";
      if (workItem instanceof IAtsTeamWorkflow) {
         IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;
         result = ChangeTypeUtil.getChangeTypeStr(workItem, atsApi);
         if (atsApi.getAttributeResolver().getAttributesToStringList(teamWf.getTeamDefinition(),
            CoreAttributeTypes.StaticId).contains(USE_DEVELOPER_CHANGE_TYPES)) {
            if (result.equals("Improvement")) {
               result = "feature";
            } else if (result.equals("Problem")) {
               result = "bug";
            } else if (result.equals("Refinement")) {
               result = "refinement";
            }
         }
      } else if (workItem instanceof IAtsTask) {
         result = "Task";
      } else if (workItem.isOfType(AtsArtifactTypes.AbstractReview)) {
         result = "Review";
      } else if (workItem.isTypeEqual(AtsArtifactTypes.Goal)) {
         result = "Goal";
      } else if (workItem.isTypeEqual(AtsArtifactTypes.AgileBacklog)) {
         result = "Backlog";
      }
      if (!Strings.isValid(result)) {
         result = "unknown";
      }
      return result;
   }

   private String getFormatStr(IAtsTeamDefinition teamDef) {
      if (teamDef != null) {
         ArtifactId artifact = atsApi.getQueryService().getArtifact(teamDef);
         if (artifact != null) {
            String formatStr =
               atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.ActionDetailsFormat, "");
            if (Strings.isValid(formatStr)) {
               return formatStr;
            }
         }
         if (atsApi.getTeamDefinitionService().getParentTeamDef(teamDef) != null) {
            return getFormatStr(atsApi.getTeamDefinitionService().getParentTeamDef(teamDef));
         }
      }
      return null;
   }
}
