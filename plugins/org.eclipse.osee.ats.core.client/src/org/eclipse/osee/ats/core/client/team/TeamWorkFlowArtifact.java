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

package org.eclipse.osee.ats.core.client.team;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.action.ActionArtifactRollup;
import org.eclipse.osee.ats.core.client.branch.AtsBranchManagerCore;
import org.eclipse.osee.ats.core.client.config.ActionableItemManager;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.task.AbstractTaskableArtifact;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.config.AtsVersionService;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IATSStateMachineArtifact;

/**
 * @author Donald G. Dunne
 */
public class TeamWorkFlowArtifact extends AbstractTaskableArtifact implements IAtsTeamWorkflow, IATSStateMachineArtifact {

   private static final Set<Integer> teamArtsWithNoAction = new HashSet<Integer>();
   private final ActionableItemManager actionableItemsDam;
   private boolean creatingWorkingBranch = false;
   private boolean committingWorkingBranch = false;

   public TeamWorkFlowArtifact(String guid, Branch branch, IArtifactType artifactType) throws OseeCoreException {
      super(guid, branch, artifactType);
      actionableItemsDam = new ActionableItemManager(this);
   }

   @Override
   public void getSmaArtifactsOneLevel(AbstractWorkflowArtifact smaArtifact, Set<Artifact> artifacts) throws OseeCoreException {
      super.getSmaArtifactsOneLevel(smaArtifact, artifacts);
      try {
         artifacts.addAll(ReviewManager.getReviews(this));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public String getArtifactSuperTypeName() {
      return "Team Workflow";
   }

   @Override
   public void saveSMA(IAtsChangeSet changes) {
      super.saveSMA(changes);
      try {
         ActionArtifact parentAction = getParentActionArtifact();
         ActionArtifactRollup rollup = new ActionArtifactRollup(parentAction);
         rollup.resetAttributesOffChildren();
         changes.add(parentAction);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't reset Action parent of children", ex);
      }
   }

   @Override
   public String getDescription() {
      try {
         return getSoleAttributeValue(AtsAttributeTypes.Description, "");
      } catch (Exception ex) {
         return "Error: " + ex.getLocalizedMessage();
      }
   }

   @Override
   public boolean isValidationRequired() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.ValidationRequired, false);
   }

   @Override
   public String getEditorTitle() throws OseeCoreException {
      try {
         if (getTeamDefinition().isTeamUsesVersions()) {
            IAtsVersion version = AtsVersionService.get().getTargetedVersion(this);
            return String.format("%s: [%s] - %s", getType(), version != null ? version : "Un-Targeted", getName());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return super.getEditorTitle();
   }

   public ActionableItemManager getActionableItemsDam() {
      return actionableItemsDam;
   }

   public void setTeamDefinition(IAtsTeamDefinition tda) throws OseeCoreException {
      this.setSoleAttributeValue(AtsAttributeTypes.TeamDefinition, tda.getGuid());
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition() throws OseeCoreException {
      String guid = this.getSoleAttributeValue(AtsAttributeTypes.TeamDefinition, "");
      if (!Strings.isValid(guid)) {
         throw new OseeArgumentException("TeamWorkflow [%s] has no Team Definition associated.", getAtsId());
      }
      IAtsTeamDefinition teamDef = AtsClientService.get().getAtsConfig().getSoleByGuid(guid, IAtsTeamDefinition.class);
      Conditions.checkNotNull(teamDef, String.format("TeamDef null for Team WF %s", toStringWithId()));
      return teamDef;
   }

   public String getTeamName() {
      try {
         if (!isDeleted()) {
            return getTeamDefinition().getName();
         }
         return "(Deleted)";
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return "!Error";
      }
   }

   @Override
   public String getType() {
      return getTeamName() + " Workflow";
   }

   @Override
   public void atsDelete(Set<Artifact> deleteArts, Map<Artifact, Object> allRelated) throws OseeCoreException {
      super.atsDelete(deleteArts, allRelated);
      for (AbstractReviewArtifact reviewArt : ReviewManager.getReviews(this)) {
         reviewArt.atsDelete(deleteArts, allRelated);
      }
   }

   @Override
   public TeamWorkFlowArtifact getParentTeamWorkflow() {
      parentTeamArt = this;
      return parentTeamArt;
   }

   @Override
   public Artifact getParentAtsArtifact() throws OseeCoreException {
      return getParentActionArtifact();
   }

   @Override
   public ActionArtifact getParentActionArtifact() throws OseeCoreException {
      if (parentAction != null) {
         return parentAction;
      }
      Collection<Artifact> arts = getRelatedArtifacts(AtsRelationTypes.ActionToWorkflow_Action);
      if (arts.isEmpty()) {
         // Only show exception once in log
         if (!teamArtsWithNoAction.contains(getArtId())) {
            if (!AtsUtilCore.isInTest()) {
               OseeLog.log(Activator.class, Level.SEVERE,
                  String.format("Team Workflow has no parent Action [%s]", toStringWithId()));
            }
            teamArtsWithNoAction.add(getArtId());
         }
      } else if (arts.size() > 1) {
         throw new OseeStateException("Team [%s] has multiple parent Actions", getGuid());
      }
      if (arts.size() > 0) {
         parentAction = (ActionArtifact) arts.iterator().next();
      }
      return parentAction;
   }

   @Override
   public AbstractWorkflowArtifact getParentAWA() {
      return null;
   }

   @Override
   public double getManHrsPerDayPreference() throws OseeCoreException {
      try {
         return getHoursPerWorkDayFromItemAndChildren(getTeamDefinition());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return super.getManHrsPerDayPreference();
   }

   private double getHoursPerWorkDayFromItemAndChildren(IAtsTeamDefinition teamDef) {
      try {
         double manDayHours = 0;
         Artifact artifact = AtsClientService.get().getConfigArtifact(getTeamDefinition());
         if (artifact != null) {
            manDayHours = artifact.getSoleAttributeValue(AtsAttributeTypes.HoursPerWorkDay, 0.0);
         }
         if (manDayHours == 0 && teamDef.getParentTeamDef() != null) {
            return getHoursPerWorkDayFromItemAndChildren(teamDef.getParentTeamDef());
         }
         return AtsUtilCore.DEFAULT_HOURS_PER_WORK_DAY;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return 0.0;
   }

   @Override
   public double getWorldViewWeeklyBenefit() throws OseeCoreException {
      if (isAttributeTypeValid(AtsAttributeTypes.WeeklyBenefit)) {
         return 0;
      }
      String value = getSoleAttributeValue(AtsAttributeTypes.WeeklyBenefit, "");
      if (!Strings.isValid(value)) {
         return 0;
      }
      return new Float(value).doubleValue();
   }

   public Branch getWorkingBranchForceCacheUpdate() throws OseeCoreException {
      return AtsBranchManagerCore.getWorkingBranch(this, true);
   }

   public Branch getWorkingBranch() throws OseeCoreException {
      return AtsBranchManagerCore.getWorkingBranch(this);
   }

   public String getBranchName() {
      String smaTitle = getName();
      if (smaTitle.length() > 40) {
         smaTitle = smaTitle.substring(0, 39) + "...";
      }
      String typeName = TeamWorkFlowManager.getArtifactTypeShortName(this);
      if (Strings.isValid(typeName)) {
         return String.format("%s - %s - %s", getAtsId(), typeName, smaTitle);
      } else {
         return String.format("%s - %s", getAtsId(), smaTitle);
      }
   }

   public boolean isWorkingBranchCreationInProgress() {
      return creatingWorkingBranch;
   }

   public void setWorkingBranchCreationInProgress(boolean inProgress) {
      this.creatingWorkingBranch = inProgress;
   }

   public boolean isWorkingBranchCommitInProgress() {
      return committingWorkingBranch;
   }

   public void setWorkingBranchCommitInProgress(boolean inProgress) {
      this.committingWorkingBranch = inProgress;
   }

   @Override
   public Set<IAtsActionableItem> getActionableItems() throws OseeCoreException {
      return getActionableItemsDam().getActionableItems();
   }

}
