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

package org.eclipse.osee.ats.ide.workflow.teamwf;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.action.ActionArtifact;
import org.eclipse.osee.ats.ide.workflow.action.ActionArtifactRollup;
import org.eclipse.osee.ats.ide.workflow.review.ReviewManager;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IATSStateMachineArtifact;

/**
 * @author Donald G. Dunne
 */
public class TeamWorkFlowArtifact extends AbstractWorkflowArtifact implements IAtsTeamWorkflow, IATSStateMachineArtifact {

   private static final Set<Integer> teamArtsWithNoAction = new HashSet<>();
   private IAtsTeamDefinition teamDef;

   public TeamWorkFlowArtifact(Long id, String guid, BranchId branch, ArtifactTypeToken artifactType) {
      super(id, guid, branch, artifactType);
   }

   public TeamWorkFlowArtifact(ArtifactTypeToken artifactType) {
      super(Lib.generateId(), null, CoreBranches.COMMON, artifactType);
   }

   @Override
   public void getSmaArtifactsOneLevel(AbstractWorkflowArtifact smaArtifact, Set<Artifact> artifacts) {
      super.getSmaArtifactsOneLevel(smaArtifact, artifacts);
      try {
         artifacts.addAll(ReviewManager.getReviews(this));
         for (IAtsTask task : AtsClientService.get().getTaskService().getTask(this)) {
            artifacts.add(AtsClientService.get().getQueryServiceClient().getArtifact(task));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
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
   public String getEditorTitle() {
      try {
         if (AtsClientService.get().getVersionService().isTeamUsesVersions(getTeamDefinition())) {
            IAtsVersion version = AtsClientService.get().getVersionService().getTargetedVersion(this);
            return String.format("%s: [%s] - %s", getTeamName(), version != null ? version : "Un-Targeted", getName());

         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return super.getEditorTitle();
   }

   public void setTeamDefinition(IAtsTeamDefinition teamDef) {
      setSoleAttributeValue(AtsAttributeTypes.TeamDefinitionReference, teamDef);
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition() {
      if (teamDef == null) {
         ArtifactId artId = AtsClientService.get().getAttributeResolver().getSoleArtifactIdReference((IAtsObject) this,
            AtsAttributeTypes.TeamDefinitionReference, ArtifactId.SENTINEL);
         if (artId.isInvalid()) {
            throw new OseeArgumentException("TeamWorkflow [%s] has no Team Definition associated.", getAtsId());
         }
         teamDef = AtsClientService.get().getQueryService().getConfigItem(artId);
         Conditions.checkNotNull(teamDef, String.format("TeamDef null for Team WF %s", toStringWithId()));
      }
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
   public TeamWorkFlowArtifact getParentTeamWorkflow() {
      parentTeamArt = this;
      return parentTeamArt;
   }

   @Override
   public Artifact getParentAtsArtifact() {
      return getParentActionArtifact();
   }

   @Override
   public ActionArtifact getParentActionArtifact() {
      if (parentAction != null) {
         return parentAction;
      }
      Collection<Artifact> arts = getRelatedArtifacts(AtsRelationTypes.ActionToWorkflow_Action);
      if (arts.isEmpty()) {
         // Only show exception once in log
         if (!teamArtsWithNoAction.contains(getArtId())) {
            if (!AtsUtil.isInTest()) {
               OseeLog.log(Activator.class, Level.SEVERE,
                  String.format("Team Workflow has no parent Action [%s]", toStringWithId()));
            }
            teamArtsWithNoAction.add(getArtId());
         }
      } else if (arts.size() > 1) {
         throw new OseeStateException("Team [%s] has multiple parent Actions", toStringWithId());
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
   public double getManHrsPerDayPreference() {
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
         Artifact artifact = AtsClientService.get().getQueryServiceClient().getArtifact(getTeamDefinition());
         if (artifact != null) {
            manDayHours = artifact.getSoleAttributeValue(AtsAttributeTypes.HoursPerWorkDay, 0.0);
         }
         if (manDayHours == 0 && AtsClientService.get().getTeamDefinitionService().getParentTeamDef(teamDef) != null) {
            return getHoursPerWorkDayFromItemAndChildren(
               AtsClientService.get().getTeamDefinitionService().getParentTeamDef(teamDef));
         }
         return AtsUtil.DEFAULT_HOURS_PER_WORK_DAY;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return 0.0;
   }

   public IOseeBranch getWorkingBranchForceCacheUpdate() {
      return AtsClientService.get().getBranchService().getWorkingBranch(this, true);
   }

   public IOseeBranch getWorkingBranch() {
      return AtsClientService.get().getBranchService().getWorkingBranch(this);
   }

   @Override
   public Set<IAtsActionableItem> getActionableItems() {
      return AtsClientService.get().getActionableItemService().getActionableItems(this);
   }

}
