/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.artifact;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.column.ChangeTypeColumn;
import org.eclipse.osee.ats.column.PriorityColumn;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class ActionArtifact extends AbstractAtsArtifact implements IWorldViewArtifact {

   public static enum CreateTeamOption {
      Duplicate_If_Exists; // If option exists, then duplication of workflow of same team definition is allowed
   };

   public ActionArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, IArtifactType artifactType) throws OseeCoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   public void resetAttributesOffChildren(SkynetTransaction transaction) throws OseeCoreException {
      ChangeTypeColumn.resetChangeTypeOffChildren(this);
      resetPriorityOffChildren();
      resetUserCommunityOffChildren();
      resetTitleOffChildren();
      resetValidationOffChildren();
      resetDescriptionOffChildren();
      persist(transaction);
   }

   public boolean hasAtsWorldChildren() {
      return true;
   }

   public Set<ActionableItemArtifact> getActionableItems() throws OseeCoreException {
      Set<ActionableItemArtifact> aias = new HashSet<ActionableItemArtifact>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         aias.addAll(team.getActionableItemsDam().getActionableItems());
      }
      return aias;
   }

   /**
    * Reset Action title only if all children are titled the same
    */
   private void resetTitleOffChildren() throws OseeCoreException {
      String title = "";
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (title.isEmpty()) {
            title = team.getName();
         } else if (!title.equals(team.getName())) {
            return;
         }
      }
      if (!title.equals(getName())) {
         setName(title);
      }
   }

   // Set validation to true if any require validation
   private void resetValidationOffChildren() throws OseeCoreException {
      boolean validationRequired = false;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (team.getSoleAttributeValue(AtsAttributeTypes.ValidationRequired, false)) {
            validationRequired = true;
         }
      }
      if (validationRequired != getSoleAttributeValue(AtsAttributeTypes.ValidationRequired, false)) {
         setSoleAttributeValue(AtsAttributeTypes.ValidationRequired, validationRequired);
      }
   }

   /**
    * Reset Action title only if all children are titled the same
    */
   private void resetDescriptionOffChildren() throws OseeCoreException {
      String desc = "";
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (desc.isEmpty()) {
            desc = team.getSoleAttributeValue(AtsAttributeTypes.Description, "");
         } else if (!desc.equals(team.getSoleAttributeValue(AtsAttributeTypes.Description, ""))) {
            return;
         }
      }
      if (!desc.equals(getSoleAttributeValue(AtsAttributeTypes.Description, ""))) {
         setSoleAttributeValue(AtsAttributeTypes.Description, desc);
      }
      if (desc.isEmpty()) {
         deleteSoleAttribute(AtsAttributeTypes.Description);
      }
   }

   private void resetPriorityOffChildren() throws OseeCoreException {
      String priorityType = null;
      Collection<TeamWorkFlowArtifact> teamArts = getTeamWorkFlowArtifacts();
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
         setSoleAttributeValue(AtsAttributeTypes.PriorityType, priorityType);
      }
   }

   private void resetUserCommunityOffChildren() throws OseeCoreException {
      Set<String> userComs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.isCancelled()) {
            userComs.addAll(team.getAttributesToStringList(AtsAttributeTypes.UserCommunity));
         }
      }
      setAttributeValues(AtsAttributeTypes.UserCommunity, userComs);
   }

   public Collection<TeamWorkFlowArtifact> getTeamWorkFlowArtifacts() throws OseeCoreException {
      return getRelatedArtifactsUnSorted(AtsRelationTypes.ActionToWorkflow_WorkFlow, TeamWorkFlowArtifact.class);
   }

   @Override
   public String getType() {
      return AtsArtifactTypes.Action.getName();
   }

   @Override
   public String getWorldViewTitle() {
      return getName();
   }

   @Override
   public String getState() throws OseeCoreException {
      Set<String> strs = new HashSet<String>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         strs.add(team.getState());
      }
      return Collections.toString(";", strs);
   }

   @Override
   public String getAssigneeStr() throws OseeCoreException {
      Set<User> pocs = new HashSet<User>();
      Set<User> implementers = new HashSet<User>();
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (team.isCancelledOrCompleted()) {
            implementers.addAll(team.getImplementers());
         } else {
            pocs.addAll(team.getStateMgr().getAssignees());
         }
      }
      return Artifacts.toString("; ", pocs) + (implementers.isEmpty() ? "" : "(" + Artifacts.toString("; ",
         implementers) + ")");
   }

   @Override
   public String getWorldViewID() {
      return getHumanReadableId();
   }

   @Override
   public Image getAssigneeImage() throws OseeCoreException {
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         Image image = team.getAssigneeImage();
         if (image != null) {
            return image;
         }
      }
      return null;
   }

   @Override
   public void atsDelete(Set<Artifact> deleteArts, Map<Artifact, Object> allRelated) throws OseeCoreException {
      super.atsDelete(deleteArts, allRelated);
      // Delete all products
      for (TeamWorkFlowArtifact art : getRelatedArtifacts(AtsRelationTypes.ActionToWorkflow_WorkFlow,
         TeamWorkFlowArtifact.class)) {
         art.atsDelete(deleteArts, allRelated);
      }
   }

   public int getWorldViewStatePercentComplete() throws OseeCoreException {
      if (getTeamWorkFlowArtifacts().size() == 1) {
         return getTeamWorkFlowArtifacts().iterator().next().getWorldViewStatePercentComplete();
      } else {
         double percent = 0;
         int items = 0;
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (!team.isCancelled()) {
               percent += team.getWorldViewStatePercentComplete();
               items++;
            }
         }
         if (items > 0) {
            Double rollPercent = percent / items;
            return rollPercent.intValue();
         }
      }
      return 0;
   }

   @Override
   public Artifact getParentAtsArtifact() {
      return null;
   }

   @Override
   public double getWorldViewWeeklyBenefit() throws OseeCoreException {
      double hours = 0;
      // Add up hours for all children
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.isCompleted() && !team.isCancelled()) {
            hours += team.getWorldViewWeeklyBenefit();
         }
      }
      return hours;
   }

   @Override
   public double getWorldViewHoursSpentState() throws OseeCoreException {
      double hours = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.isCancelled()) {
            hours += team.getWorldViewHoursSpentState();
         }
      }
      return hours;
   }

   @Override
   public double getWorldViewHoursSpentStateReview() throws OseeCoreException {
      double hours = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.isCancelled()) {
            hours += team.getWorldViewHoursSpentStateReview();
         }
      }
      return hours;
   }

   @Override
   public double getWorldViewHoursSpentStateTask() throws OseeCoreException {
      double hours = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.isCancelled()) {
            hours += team.getWorldViewHoursSpentStateTask();
         }
      }
      return hours;
   }

   @Override
   public double getWorldViewHoursSpentStateTotal() throws OseeCoreException {
      double hours = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.isCancelled()) {
            hours += team.getWorldViewHoursSpentStateTotal();
         }
      }
      return hours;
   }

   @Override
   public double getWorldViewHoursSpentTotal() throws OseeCoreException {
      double hours = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.isCancelled()) {
            hours += team.getWorldViewHoursSpentTotal();
         }
      }
      return hours;
   }

   @Override
   public int getWorldViewPercentCompleteState() throws OseeCoreException {
      double percent = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.isCancelled()) {
            percent += team.getWorldViewPercentCompleteState();
         }
      }
      if (percent == 0) {
         return 0;
      }
      Double rollPercent = percent / getTeamWorkFlowArtifacts().size();
      return rollPercent.intValue();
   }

   @Override
   public int getWorldViewPercentCompleteStateReview() throws OseeCoreException {
      double percent = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.isCancelled()) {
            percent += team.getWorldViewPercentCompleteStateReview();
         }
      }
      if (percent == 0) {
         return 0;
      }
      Double rollPercent = percent / getTeamWorkFlowArtifacts().size();
      return rollPercent.intValue();
   }

   @Override
   public int getWorldViewPercentCompleteStateTask() throws OseeCoreException {
      double percent = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.isCancelled()) {
            percent += team.getWorldViewPercentCompleteStateTask();
         }
      }
      if (percent == 0) {
         return 0;
      }
      Double rollPercent = percent / getTeamWorkFlowArtifacts().size();
      return rollPercent.intValue();
   }

   @Override
   public int getWorldViewPercentCompleteTotal() throws OseeCoreException {
      double percent = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.isCancelled()) {
            percent += team.getWorldViewPercentCompleteTotal();
         }
      }
      if (percent == 0) {
         return 0;
      }
      Double rollPercent = percent / getTeamWorkFlowArtifacts().size();
      return rollPercent.intValue();
   }

   public String getWorldViewLastUpdated() throws OseeCoreException {
      return DateUtil.getMMDDYYHHMM(getLastModified());
   }

}
