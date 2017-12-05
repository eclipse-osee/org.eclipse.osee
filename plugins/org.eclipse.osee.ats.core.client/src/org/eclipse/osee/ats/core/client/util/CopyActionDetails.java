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
package org.eclipse.osee.ats.core.client.util;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.client.workflow.ChangeTypeUtil;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class CopyActionDetails {

   private final AbstractWorkflowArtifact awa;
   private static final String USE_DEVELOPER_CHANGE_TYPES = "UseDeveloperChangeTypes";

   public CopyActionDetails(AbstractWorkflowArtifact awa) {
      this.awa = awa;
   }

   public String getDetailsString() {
      String detailsStr = "";
      try {
         if (awa.getParentTeamWorkflow() != null) {
            IAtsTeamDefinition teamDef = awa.getParentTeamWorkflow().getTeamDefinition();
            String formatStr = getFormatStr(teamDef);
            if (Strings.isValid(formatStr)) {
               detailsStr = formatStr;
               IAtsAction action = awa.getParentAction();
               if (action != null) {
                  detailsStr = detailsStr.replaceAll("<actionatsid>", action.getAtsId());
               }
               String legacyPcrId = awa.getSoleAttributeValue(AtsAttributeTypes.LegacyPcrId, null);
               if (Strings.isValid(legacyPcrId)) {
                  detailsStr = detailsStr.replaceAll("<legacypcrid>", " - [" + legacyPcrId + "]");
               } else {
                  detailsStr = detailsStr.replaceAll("<legacypcrid>", "");
               }
               detailsStr = detailsStr.replaceAll("<atsid>", awa.getAtsId());
               detailsStr = detailsStr.replaceAll("<name>", awa.getName());
               detailsStr = detailsStr.replaceAll("<artType>", awa.getArtifactTypeName());
               detailsStr = detailsStr.replaceAll("<changeType>", getChangeTypeOrObjectType(awa));
            }
         }
         if (!Strings.isValid(detailsStr)) {
            detailsStr = "\"" + awa.getArtifactTypeName() + "\" - " + awa.getAtsId() + " - \"" + awa.getName() + "\"";
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return detailsStr;
   }

   private String getChangeTypeOrObjectType(AbstractWorkflowArtifact awa) {
      String result = "";
      if (awa instanceof TeamWorkFlowArtifact) {
         TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) awa;
         result = ChangeTypeUtil.getChangeTypeStr(awa);
         if (AtsClientService.get().getAttributeResolver().getAttributesToStringList(teamArt.getTeamDefinition(),
            CoreAttributeTypes.StaticId).contains(USE_DEVELOPER_CHANGE_TYPES)) {
            if (result.equals("Improvement")) {
               result = "feature";
            } else if (result.equals("Problem")) {
               result = "bug";
            } else if (result.equals("Refinement")) {
               result = "refinement";
            }
         }
      } else if (awa instanceof TaskArtifact) {
         result = "Task";
      } else if (awa.isOfType(AtsArtifactTypes.ReviewArtifact)) {
         result = "Review";
      } else if (awa.isTypeEqual(AtsArtifactTypes.Goal)) {
         result = "Goal";
      } else if (awa.isTypeEqual(AtsArtifactTypes.AgileBacklog)) {
         result = "Backlog";
      }
      if (!Strings.isValid(result)) {
         result = "unknown";
      }
      return result;
   }

   private String getFormatStr(IAtsTeamDefinition teamDef) {
      if (teamDef != null) {
         Artifact artifact = AtsClientService.get().getConfigArtifact(teamDef);
         if (artifact != null) {
            String formatStr = artifact.getSoleAttributeValue(AtsAttributeTypes.ActionDetailsFormat, "");
            if (Strings.isValid(formatStr)) {
               return formatStr;
            }
         }
         if (teamDef.getParentTeamDef() != null) {
            return getFormatStr(teamDef.getParentTeamDef());
         }
      }
      return null;
   }
}
