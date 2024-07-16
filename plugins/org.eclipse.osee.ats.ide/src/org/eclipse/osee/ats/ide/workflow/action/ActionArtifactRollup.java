/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.workflow.action;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.column.ChangeTypeColumn;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ActionArtifactRollup {

   private final IAtsAction action;
   private final AtsApiIde atsApi;

   public ActionArtifactRollup(IAtsAction action) {
      this.action = action;
      if (!action.isTypeEqual(AtsArtifactTypes.Action)) {
         throw new OseeArgumentException("Artifact must be an Action instead of [%s]", action.getArtifactType());
      }
      this.atsApi = AtsApiService.get();
   }

   public void resetAttributesOffChildren() {
      resetChangeTypeOffChildren(action, atsApi);
      resetPriorityOffChildren();
      resetTitleOffChildren();
      resetValidationOffChildren();
      resetDescriptionOffChildren();
   }

   public static void resetChangeTypeOffChildren(IAtsAction action, AtsApi atsApi) {
      ChangeTypes changeType = null;
      Collection<IAtsTeamWorkflow> teamWfs = atsApi.getWorkItemService().getTeams(action);
      if (teamWfs.size() == 1) {
         changeType = ChangeTypeColumn.getChangeType(teamWfs.iterator().next(), atsApi);
      } else {
         for (IAtsTeamWorkflow team : teamWfs) {
            if (!team.isCancelled()) {
               if (changeType == null) {
                  changeType = ChangeTypeColumn.getChangeType(team, atsApi);
               } else if (changeType != ChangeTypeColumn.getChangeType(team, atsApi)) {
                  return;
               }
            }
         }
      }
      if (changeType != null && ChangeTypeColumn.getChangeType(action, atsApi) != changeType) {
         if (changeType == ChangeTypes.None) {
            ((Artifact) action.getStoreObject()).deleteAttributes(AtsAttributeTypes.ChangeType);
         } else {
            ((Artifact) action.getStoreObject()).setSoleAttributeValue(AtsAttributeTypes.ChangeType, changeType.name());
         }
      }
   }

   /**
    * Reset Action title only if all children are titled the same
    */
   private void resetTitleOffChildren() {
      String title = null;
      for (IAtsTeamWorkflow team : atsApi.getWorkItemServiceIde().getTeams(action)) {
         if (title == null || Strings.isInValid(title)) {
            title = team.getName();
         } else if (!title.equals(team.getName())) {
            return;
         }
      }
      if (title != null && !title.equals(action.getName())) {
         ((Artifact) action.getStoreObject()).setName(title);
      }
   }

   // Set validation to true if any require validation
   private void resetValidationOffChildren() {
      boolean validationRequired = false;
      for (IAtsTeamWorkflow team : atsApi.getWorkItemServiceIde().getTeams(action)) {
         if (atsApi.getAttributeResolver().getSoleAttributeValue(team, AtsAttributeTypes.ValidationRequired, false)) {
            validationRequired = true;
         }
      }
      if (validationRequired != atsApi.getAttributeResolver().getSoleAttributeValue(action,
         AtsAttributeTypes.ValidationRequired, false)) {
         atsApi.getAttributeResolver().setSoleAttributeValue(action, AtsAttributeTypes.ValidationRequired, false);

      }
   }

   /**
    * Reset Action title only if all children are titled the same
    */
   private void resetDescriptionOffChildren() {
      String desc = "";
      for (IAtsTeamWorkflow team : atsApi.getWorkItemServiceIde().getTeams(action)) {
         if (desc.isEmpty()) {
            desc = atsApi.getAttributeResolver().getSoleAttributeValue(team, AtsAttributeTypes.Description, "");
         } else if (!desc.equals(
            atsApi.getAttributeResolver().getSoleAttributeValue(team, AtsAttributeTypes.Description, ""))) {
            return;
         }
      }
      if (!desc.equals(
         atsApi.getAttributeResolver().getSoleAttributeValue(action, AtsAttributeTypes.Description, ""))) {
         atsApi.getAttributeResolver().setSoleAttributeValue(action, AtsAttributeTypes.Description, desc);
      }
      if (desc.isEmpty()) {
         ((Artifact) action).deleteSoleAttribute(AtsAttributeTypes.Description);
      }
   }

   private void resetPriorityOffChildren() {
      AttributeTypeToken attrType = AtsAttributeTypes.Priority;
      String priorityType = null;
      Collection<IAtsTeamWorkflow> teamArts = atsApi.getWorkItemService().getTeams(action);
      if (teamArts.size() == 1) {
         priorityType =
            atsApi.getAttributeResolver().getSoleAttributeValueAsString(teamArts.iterator().next(), attrType, "");
      } else {
         for (IAtsTeamWorkflow team : teamArts) {
            if (!team.isCancelled()) {
               if (priorityType == null) {
                  priorityType = atsApi.getAttributeResolver().getSoleAttributeValueAsString(team, attrType, "");
               } else if (!priorityType.equals(
                  atsApi.getAttributeResolver().getSoleAttributeValueAsString(team, attrType, ""))) {
                  return;
               }
            }
         }
      }
      if (Strings.isValid(priorityType) && atsApi.getStoreService().isAttributeTypeValid(action, attrType)) {
         atsApi.getAttributeResolver().setSoleAttributeValue(action, attrType, priorityType);
      }
   }

}
