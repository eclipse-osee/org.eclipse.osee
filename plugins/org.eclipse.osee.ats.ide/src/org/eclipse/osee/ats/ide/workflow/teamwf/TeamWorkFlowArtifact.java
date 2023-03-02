/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.action.ActionArtifactRollup;
import org.eclipse.osee.ats.ide.workflow.review.ReviewManager;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
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
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButtonsBooleanTriState.BooleanState;

/**
 * @author Donald G. Dunne
 */
public class TeamWorkFlowArtifact extends AbstractWorkflowArtifact implements IAtsTeamWorkflow, IATSStateMachineArtifact {

   private static final Set<ArtifactId> teamArtsWithNoAction = new HashSet<>();
   private IAtsTeamDefinition teamDef;

   public TeamWorkFlowArtifact(Long id, String guid, BranchToken branch, ArtifactTypeToken artifactType) {
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
         for (IAtsTask task : AtsApiService.get().getTaskService().getTasks(this)) {
            artifacts.add(AtsApiService.get().getQueryServiceIde().getArtifact(task));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void save(IAtsChangeSet changes) {
      super.save(changes);
      try {
         IAtsAction parentAction = getParentAction();
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
         if (AtsApiService.get().getVersionService().isTeamUsesVersions(getTeamDefinition())) {
            IAtsVersion version = AtsApiService.get().getVersionService().getTargetedVersion(this);
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
         ArtifactId artId = AtsApiService.get().getAttributeResolver().getSoleArtifactIdReference((IAtsObject) this,
            AtsAttributeTypes.TeamDefinitionReference, ArtifactId.SENTINEL);
         if (artId.isInvalid()) {
            throw new OseeArgumentException("TeamWorkflow [%s] has no Team Definition associated.", getAtsId());
         }
         teamDef = AtsApiService.get().getConfigService().getConfigurations().getIdToTeamDef().get(artId.getId());
         if (teamDef == null) {
            teamDef = AtsApiService.get().getQueryService().getConfigItem(artId);
         }
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
      return (Artifact) getParentAction().getStoreObject();
   }

   @Override
   public IAtsAction getParentAction() {
      if (parentAction != null) {
         return parentAction;
      }
      Collection<Artifact> arts = getRelatedArtifacts(AtsRelationTypes.ActionToWorkflow_Action);
      if (arts.isEmpty()) {
         // Only show exception once in log
         if (!teamArtsWithNoAction.contains(this)) {
            if (!AtsUtil.isInTest()) {
               OseeLog.log(Activator.class, Level.SEVERE,
                  String.format("Team Workflow has no parent Action [%s]", toStringWithId()));
            }
            teamArtsWithNoAction.add(this);
         }
      } else if (arts.size() > 1) {
         throw new OseeStateException("Team [%s] has multiple parent Actions", toStringWithId());
      }
      if (arts.size() > 0) {
         parentAction = (IAtsAction) arts.iterator().next();
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
         Artifact artifact = AtsApiService.get().getQueryServiceIde().getArtifact(getTeamDefinition());
         if (artifact != null) {
            manDayHours = artifact.getSoleAttributeValue(AtsAttributeTypes.HoursPerWorkDay, 0.0);
         }
         if (manDayHours == 0 && AtsApiService.get().getTeamDefinitionService().getParentTeamDef(teamDef) != null) {
            return getHoursPerWorkDayFromItemAndChildren(
               AtsApiService.get().getTeamDefinitionService().getParentTeamDef(teamDef));
         }
         return AtsUtil.DEFAULT_HOURS_PER_WORK_DAY;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return 0.0;
   }

   public BranchToken getWorkingBranchForceCacheUpdate() {
      return AtsApiService.get().getBranchService().getWorkingBranch(this, true);
   }

   public BranchToken getWorkingBranch() {
      return AtsApiService.get().getBranchService().getWorkingBranch(this);
   }

   @Override
   public Set<IAtsActionableItem> getActionableItems() {
      return AtsApiService.get().getActionableItemService().getActionableItems(this);
   }

   @Override
   public BooleanState isParentAtsArtifactLoaded() {
      return parentAction == null ? BooleanState.No : BooleanState.Yes;
   }

}
