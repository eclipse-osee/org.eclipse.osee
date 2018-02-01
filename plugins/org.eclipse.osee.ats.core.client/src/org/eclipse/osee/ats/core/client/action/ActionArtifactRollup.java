/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.action;

import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.ChangeTypeUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ActionArtifactRollup {

   private final ActionArtifact action;

   public ActionArtifactRollup(ActionArtifact action) {
      this.action = action;
      if (!action.isTypeEqual(AtsArtifactTypes.Action)) {
         throw new OseeArgumentException("Artifact must be an Action instead of [%s]", action.getArtifactTypeName());
      }
   }

   public void resetAttributesOffChildren() {
      resetChangeTypeOffChildren(action);
      resetPriorityOffChildren();
      resetTitleOffChildren();
      resetValidationOffChildren();
      resetDescriptionOffChildren();
   }

   public static void resetChangeTypeOffChildren(IAtsAction action) {
      Artifact actionArt = (Artifact) AtsClientService.get().getQueryService().getArtifact(action);
      if (!actionArt.isOfType(AtsArtifactTypes.Action)) {
         throw new OseeArgumentException("Artifact must be an Action instead of [%s]", actionArt.getArtifactTypeName());
      }
      ChangeType changeType = null;
      Collection<IAtsTeamWorkflow> teamWfs = AtsClientService.get().getWorkItemService().getTeams(action);
      if (teamWfs.size() == 1) {
         changeType = ChangeTypeUtil.getChangeType(teamWfs.iterator().next());
      } else {
         for (IAtsTeamWorkflow team : teamWfs) {
            if (!team.isCancelled()) {
               if (changeType == null) {
                  changeType = ChangeTypeUtil.getChangeType(team);
               } else if (changeType != ChangeTypeUtil.getChangeType(team)) {
                  return;
               }
            }
         }
      }
      if (changeType != null && ChangeTypeUtil.getChangeType(action) != changeType) {
         ChangeTypeUtil.setChangeType(action, changeType);
      }
   }

   /**
    * Reset Action title only if all children are titled the same
    */
   private void resetTitleOffChildren() {
      String title = "";
      for (TeamWorkFlowArtifact team : action.getTeams()) {
         if (title.isEmpty()) {
            title = team.getName();
         } else if (!title.equals(team.getName())) {
            return;
         }
      }
      if (!title.equals(action.getName())) {
         action.setName(title);
      }
   }

   // Set validation to true if any require validation
   private void resetValidationOffChildren() {
      boolean validationRequired = false;
      for (TeamWorkFlowArtifact team : action.getTeams()) {
         if (team.getSoleAttributeValue(AtsAttributeTypes.ValidationRequired, false)) {
            validationRequired = true;
         }
      }
      if (validationRequired != action.getSoleAttributeValue(AtsAttributeTypes.ValidationRequired, false)) {
         action.setSoleAttributeValue(AtsAttributeTypes.ValidationRequired, validationRequired);
      }
   }

   /**
    * Reset Action title only if all children are titled the same
    */
   private void resetDescriptionOffChildren() {
      String desc = "";
      for (TeamWorkFlowArtifact team : action.getTeams()) {
         if (desc.isEmpty()) {
            desc = team.getSoleAttributeValue(AtsAttributeTypes.Description, "");
         } else if (!desc.equals(team.getSoleAttributeValue(AtsAttributeTypes.Description, ""))) {
            return;
         }
      }
      if (!desc.equals(action.getSoleAttributeValue(AtsAttributeTypes.Description, ""))) {
         action.setSoleAttributeValue(AtsAttributeTypes.Description, desc);
      }
      if (desc.isEmpty()) {
         action.deleteSoleAttribute(AtsAttributeTypes.Description);
      }
   }

   private void resetPriorityOffChildren() {
      String priorityType = null;
      Collection<TeamWorkFlowArtifact> teamArts = action.getTeams();
      if (teamArts.size() == 1) {
         priorityType = teamArts.iterator().next().getSoleAttributeValue(AtsAttributeTypes.PriorityType, "");
      } else {
         for (TeamWorkFlowArtifact team : teamArts) {
            if (!team.isCancelled()) {
               if (priorityType == null) {
                  priorityType = team.getSoleAttributeValue(AtsAttributeTypes.PriorityType, "");
               } else if (!priorityType.equals(team.getSoleAttributeValue(AtsAttributeTypes.PriorityType, ""))) {
                  return;
               }
            }
         }
      }
      if (Strings.isValid(priorityType)) {
         action.setSoleAttributeValue(AtsAttributeTypes.PriorityType, priorityType);
      }
   }

}
