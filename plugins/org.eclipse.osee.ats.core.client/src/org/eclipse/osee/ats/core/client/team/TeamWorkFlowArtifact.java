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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.action.ActionArtifactRollup;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.util.AtsTaskCache;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IATSStateMachineArtifact;

/**
 * @author Donald G. Dunne
 */
public class TeamWorkFlowArtifact extends AbstractWorkflowArtifact implements IAtsTeamWorkflow, IATSStateMachineArtifact {

   private static final Set<Integer> teamArtsWithNoAction = new HashSet<>();
   private boolean creatingWorkingBranch = false;
   private boolean committingWorkingBranch = false;

   public TeamWorkFlowArtifact(String guid, BranchId branch, ArtifactTypeId artifactType) {
      super(guid, branch, artifactType);
   }

   @Override
   public void getSmaArtifactsOneLevel(AbstractWorkflowArtifact smaArtifact, Set<Artifact> artifacts) throws OseeCoreException {
      super.getSmaArtifactsOneLevel(smaArtifact, artifacts);
      try {
         artifacts.addAll(ReviewManager.getReviews(this));
         for (IAtsTask task : AtsClientService.get().getTaskService().getTask(this)) {
            artifacts.add((Artifact) task.getStoreObject());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public String getArtifactSuperTypeName() {
      return "Team Workflow";
   }

   @Override
   public void save(IAtsChangeSet changes) {
      super.save(changes);
      try {
         ActionArtifact parentAction = getParentActionArtifact();
         ActionArtifactRollup rollup = new ActionArtifactRollup(parentAction);
         rollup.resetAttributesOffChildren();
         changes.add(parentAction);
      } catch (Exception ex) {
         OseeLog.logf(Activator.class, OseeLevel.SEVERE_POPUP, ex, "Can't reset Action parent of children for [%s]",
            toStringWithId());
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
   public String getEditorTitle() throws OseeCoreException {
      try {
         if (getTeamDefinition().isTeamUsesVersions()) {
            IAtsVersion version = AtsClientService.get().getVersionService().getTargetedVersion(this);
            return String.format("%s: [%s] - %s", getType(), version != null ? version : "Un-Targeted", getName());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return super.getEditorTitle();
   }

   public void setTeamDefinition(IAtsTeamDefinition teamDef) throws OseeCoreException {
      this.setSoleAttributeValue(AtsAttributeTypes.TeamDefinition,
         AtsClientService.get().getArtifact(teamDef).getGuid());
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition() throws OseeCoreException {
      String guid = this.getSoleAttributeValue(AtsAttributeTypes.TeamDefinition, "");
      if (!Strings.isValid(guid)) {
         throw new OseeArgumentException("TeamWorkflow [%s] has no Team Definition associated.", getAtsId());
      }
      IAtsTeamDefinition teamDef = AtsClientService.get().getConfigItem(guid);
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
      for (IAtsTask task : AtsClientService.get().getTaskService().getTasks(this)) {
         ((AbstractWorkflowArtifact) task.getStoreObject()).atsDelete(deleteArts, allRelated);
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

   public IOseeBranch getWorkingBranchForceCacheUpdate() throws OseeCoreException {
      return AtsClientService.get().getBranchService().getWorkingBranch(this, true);
   }

   public IOseeBranch getWorkingBranch() throws OseeCoreException {
      return AtsClientService.get().getBranchService().getWorkingBranch(this);
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
      return AtsClientService.get().getWorkItemService().getActionableItemService().getActionableItems(this);
   }

   public Collection<TaskArtifact> getTaskArtifacts() throws OseeCoreException {
      return AtsTaskCache.getTaskArtifacts(this);
   }

   public Collection<TaskArtifact> getTaskArtifacts(IStateToken state) throws OseeCoreException {
      List<TaskArtifact> arts = new ArrayList<>();
      for (TaskArtifact taskArt : getTaskArtifacts()) {
         if (taskArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState, "").equals(state.getName())) {
            arts.add(taskArt);
         }
      }
      return arts;
   }

   public Result areTasksComplete() {
      try {
         for (TaskArtifact taskArt : getTaskArtifacts()) {
            if (taskArt.isInWork()) {
               return new Result(false, "Task " + taskArt.getGuid() + " Not Complete");
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return new Result(false, "Exception " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }

}
