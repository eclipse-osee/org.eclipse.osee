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
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ActionArtifactRollup {

   private final ActionArtifact action;
   private static AttributeTypeEnum<?> priAttrToken;

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
      Artifact actionArt = AtsClientService.get().getQueryServiceClient().getArtifact(action);
      if (!actionArt.isOfType(AtsArtifactTypes.Action)) {
         throw new OseeArgumentException("Artifact must be an Action instead of [%s]", actionArt.getArtifactTypeName());
      }
      ChangeType changeType = null;
      Collection<IAtsTeamWorkflow> teamWfs = AtsClientService.get().getWorkItemService().getTeams(action);
      if (teamWfs.size() == 1) {
         changeType = ChangeTypeUtil.getChangeType(teamWfs.iterator().next(), AtsClientService.get());
      } else {
         for (IAtsTeamWorkflow team : teamWfs) {
            if (!team.isCancelled()) {
               if (changeType == null) {
                  changeType = ChangeTypeUtil.getChangeType(team, AtsClientService.get());
               } else if (changeType != ChangeTypeUtil.getChangeType(team, AtsClientService.get())) {
                  return;
               }
            }
         }
      }
      if (changeType != null && ChangeTypeUtil.getChangeType(action, AtsClientService.get()) != changeType) {
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
      Collection<TeamWorkFlowArtifact> teamArts = action.getTeams();
      if (teamArts.size() == 1) {
         priorityType = teamArts.iterator().next().getSoleAttributeValue(priToken, "");
      } else {
         for (TeamWorkFlowArtifact team : teamArts) {
            if (!team.isCancelled()) {
               if (priorityType == null) {
                  priorityType = team.getSoleAttributeValue(priToken, "");
               } else if (!priorityType.equals(team.getSoleAttributeValue(priToken, ""))) {
                  return;
               }
            }
         }
      }
      if (Strings.isValid(priorityType)) {
         action.setSoleAttributeValue(priToken, priorityType);
      }
   }

}
