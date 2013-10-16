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
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.ChangeType;
import org.eclipse.osee.ats.core.client.workflow.ChangeTypeUtil;
import org.eclipse.osee.ats.core.client.workflow.PriorityUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Donald G. Dunne
 */
public class ActionArtifactRollup {

   private final ActionArtifact action;
   private final SkynetTransaction transaction;

   public ActionArtifactRollup(ActionArtifact action, SkynetTransaction transaction) throws OseeArgumentException {
      this.action = action;
      this.transaction = transaction;
      if (!action.isOfType(AtsArtifactTypes.Action)) {
         throw new OseeArgumentException("Artifact must be an Action instead of [%s]", action.getArtifactTypeName());
      }
   }

   public void resetAttributesOffChildren() throws OseeCoreException {
      resetChangeTypeOffChildren(action);
      resetPriorityOffChildren();
      resetUserCommunityOffChildren();
      resetTitleOffChildren();
      resetValidationOffChildren();
      resetDescriptionOffChildren();
      action.persist(transaction);
   }

   public static void resetChangeTypeOffChildren(Artifact actionArt) throws OseeCoreException {
      if (!actionArt.isOfType(AtsArtifactTypes.Action)) {
         throw new OseeArgumentException("Artifact must be an Action instead of [%s]", actionArt.getArtifactTypeName());
      }
      ChangeType changeType = null;
      Collection<TeamWorkFlowArtifact> teamArts = ActionManager.getTeams(actionArt);
      if (teamArts.size() == 1) {
         changeType = ChangeTypeUtil.getChangeType(teamArts.iterator().next());
      } else {
         for (TeamWorkFlowArtifact team : teamArts) {
            if (!team.isCancelled()) {
               if (changeType == null) {
                  changeType = ChangeTypeUtil.getChangeType(team);
               } else if (changeType != ChangeTypeUtil.getChangeType(team)) {
                  return;
               }
            }
         }
      }
      if (changeType != null && ChangeTypeUtil.getChangeType(actionArt) != changeType) {
         ChangeTypeUtil.setChangeType(actionArt, changeType);
      }
   }

   /**
    * Reset Action title only if all children are titled the same
    */
   private void resetTitleOffChildren() throws OseeCoreException {
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
   private void resetValidationOffChildren() throws OseeCoreException {
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
   private void resetDescriptionOffChildren() throws OseeCoreException {
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

   private void resetPriorityOffChildren() throws OseeCoreException {
      String priorityType = null;
      Collection<TeamWorkFlowArtifact> teamArts = action.getTeams();
      if (teamArts.size() == 1) {
         priorityType = PriorityUtil.getPriorityStr(teamArts.iterator().next());
      } else {
         for (TeamWorkFlowArtifact team : teamArts) {
            if (!team.isCancelled()) {
               if (priorityType == null) {
                  priorityType = PriorityUtil.getPriorityStr(team);
               } else if (!priorityType.equals(PriorityUtil.getPriorityStr(team))) {
                  return;
               }
            }
         }
      }
      if (Strings.isValid(priorityType)) {
         action.setSoleAttributeValue(AtsAttributeTypes.PriorityType, priorityType);
      }
   }

   private void resetUserCommunityOffChildren() throws OseeCoreException {
      Set<String> userComs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : action.getTeams()) {
         if (!team.isCancelled()) {
            userComs.addAll(team.getAttributesToStringList(AtsAttributeTypes.UserCommunity));
         }
      }
      action.setAttributeValues(AtsAttributeTypes.UserCommunity, userComs);
   }

}
