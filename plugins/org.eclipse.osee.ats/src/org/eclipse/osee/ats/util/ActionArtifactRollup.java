/*
 * Created on Mar 7, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.column.ChangeTypeColumn;
import org.eclipse.osee.ats.column.PriorityColumn;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

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
      ChangeTypeColumn.resetChangeTypeOffChildren(action);
      resetPriorityOffChildren();
      resetUserCommunityOffChildren();
      resetTitleOffChildren();
      resetValidationOffChildren();
      resetDescriptionOffChildren();
      action.persist(transaction);
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
         priorityType = PriorityColumn.getPriorityStr(teamArts.iterator().next());
      } else {
         for (TeamWorkFlowArtifact team : teamArts) {
            if (!team.isCancelled()) {
               if (priorityType == null) {
                  priorityType = PriorityColumn.getPriorityStr(team);
               } else if (!priorityType.equals(PriorityColumn.getPriorityStr(team))) {
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
