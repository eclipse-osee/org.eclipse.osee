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
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.INewActionPageAttributeFactory;
import org.eclipse.osee.ats.api.workflow.INewActionPageAttributeFactoryProvider;
import org.eclipse.osee.ats.core.util.ActionFactory;
import org.eclipse.osee.ats.core.workflow.util.ChangeTypeUtil;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ActionArtifactRollup {

   private final IAtsAction action;
   private static AttributeTypeEnum<?> priAttrToken;

   public ActionArtifactRollup(IAtsAction action) {
      this.action = action;
      if (!action.isTypeEqual(AtsArtifactTypes.Action)) {
         throw new OseeArgumentException("Artifact must be an Action instead of [%s]", action.getArtifactType());
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
      Artifact actionArt = AtsApiService.get().getQueryServiceIde().getArtifact(action);
      if (!actionArt.isOfType(AtsArtifactTypes.Action)) {
         throw new OseeArgumentException("Artifact must be an Action instead of [%s]", actionArt.getArtifactTypeName());
      }
      ChangeType changeType = null;
      Collection<IAtsTeamWorkflow> teamWfs = AtsApiService.get().getWorkItemService().getTeams(action);
      if (teamWfs.size() == 1) {
         changeType = ChangeTypeUtil.getChangeType(teamWfs.iterator().next(), AtsApiService.get());
      } else {
         for (IAtsTeamWorkflow team : teamWfs) {
            if (!team.isCancelled()) {
               if (changeType == null) {
                  changeType = ChangeTypeUtil.getChangeType(team, AtsApiService.get());
               } else if (changeType != ChangeTypeUtil.getChangeType(team, AtsApiService.get())) {
                  return;
               }
            }
         }
      }
      if (changeType != null && ChangeTypeUtil.getChangeType(action, AtsApiService.get()) != changeType) {
         if (changeType == ChangeType.None) {
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
      String title = "";
      for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemServiceIde().getTeams(action)) {

         if (title.isEmpty()) {
            title = team.getName();
         } else if (!title.equals(team.getName())) {
            return;
         }
      }
      if (!title.equals(action.getName())) {
         ((Artifact) action.getStoreObject()).setName(title);
      }
   }

   // Set validation to true if any require validation
   private void resetValidationOffChildren() {
      boolean validationRequired = false;
      for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemServiceIde().getTeams(action)) {
         if (AtsApiService.get().getAttributeResolver().getSoleAttributeValue(team,
            AtsAttributeTypes.ValidationRequired, false)) {
            validationRequired = true;
         }
      }
      if (validationRequired != AtsApiService.get().getAttributeResolver().getSoleAttributeValue(action,
         AtsAttributeTypes.ValidationRequired, false)) {
         AtsApiService.get().getAttributeResolver().setSoleAttributeValue(action, AtsAttributeTypes.ValidationRequired,
            false);

      }
   }

   /**
    * Reset Action title only if all children are titled the same
    */
   private void resetDescriptionOffChildren() {
      String desc = "";
      for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemServiceIde().getTeams(action)) {
         if (desc.isEmpty()) {
            desc = AtsApiService.get().getAttributeResolver().getSoleAttributeValue(team, AtsAttributeTypes.Description,
               "");
         } else if (!desc.equals(AtsApiService.get().getAttributeResolver().getSoleAttributeValue(team,
            AtsAttributeTypes.Description, ""))) {
            return;
         }
      }
      if (!desc.equals(
         AtsApiService.get().getAttributeResolver().getSoleAttributeValue(action, AtsAttributeTypes.Description, ""))) {
         AtsApiService.get().getAttributeResolver().setSoleAttributeValue(action, AtsAttributeTypes.Description, desc);
      }
      if (desc.isEmpty()) {
         ((Artifact) action).deleteSoleAttribute(AtsAttributeTypes.Description);
      }
   }

   private AttributeTypeEnum<?> getPrioirtyAttrToken() {
      if (priAttrToken == null) {
         for (INewActionPageAttributeFactoryProvider provider : ActionFactory.getProviders()) {
            for (INewActionPageAttributeFactory factory : provider.getNewActionAttributeFactory()) {
               if (factory.useFactory() && factory.getPrioirtyColumnToken() != null) {
                  priAttrToken = factory.getPrioirtyAttrToken();
                  return priAttrToken;
               }
            }
         }
         priAttrToken = AtsAttributeTypes.Priority;
      }
      return priAttrToken;
   }

   private void resetPriorityOffChildren() {
      AttributeTypeEnum<?> priToken = getPrioirtyAttrToken();
      String priorityType = null;
      Collection<IAtsTeamWorkflow> teamArts = AtsApiService.get().getWorkItemServiceIde().getTeams(action);
      if (teamArts.size() == 1) {
         priorityType = AtsApiService.get().getAttributeResolver().getSoleAttributeValueAsString(
            teamArts.iterator().next(), priToken, "");
      } else {
         for (IAtsTeamWorkflow team : teamArts) {
            if (!team.isCancelled()) {
               if (priorityType == null) {
                  priorityType =
                     AtsApiService.get().getAttributeResolver().getSoleAttributeValueAsString(team, priToken, "");
               } else if (!priorityType.equals(
                  AtsApiService.get().getAttributeResolver().getSoleAttributeValueAsString(team, priToken, ""))) {
                  return;
               }
            }
         }
      }
      if (Strings.isValid(priorityType)) {
         AtsApiService.get().getAttributeResolver().setSoleAttributeValue(action, priToken, priorityType);
      }
   }

}
