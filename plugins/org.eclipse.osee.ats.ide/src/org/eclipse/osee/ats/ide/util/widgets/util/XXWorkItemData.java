/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets.util;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.VersionLockedType;
import org.eclipse.osee.ats.api.version.VersionReleaseType;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.XXArtData;

/**
 * Class that, given a beginning artifact, can provide all the data for attrs, rels, relatedArts and etc. This can
 * eventually be sent from server rather than client loading it. It does provide for a single loading of data so each
 * widget in a WorkflowEditor doesn't need to make it's own loading and conversions.
 *
 * @author Donald G. Dunne
 */
public class XXWorkItemData extends XXArtData {

   public static XXWorkItemData SENTINEL = new XXWorkItemData(null);

   private boolean teamDefLoaded = false;
   private IAtsTeamDefinition teamDef;
   private boolean teamWfLoaded = false;
   private IAtsTeamWorkflow teamWf;
   private boolean targetedVersionLoaded = false;
   private IAtsVersion targetedVersion;
   private boolean foundInVersionLoaded = false;
   private IAtsVersion foundInVersion;
   private boolean introducedInVersionLoaded = false;
   private IAtsVersion introducedInVersion;
   private IAtsWorkItem workItem;
   private Collection<IAtsVersion> selectableVersions;
   private IAtsAction parentAction;
   private boolean parentActionLoaded = false;
   private IAtsTeamWorkflow parentTeamWf;
   private boolean parentTeamWfLoaded = false;
   private Collection<AtsUser> teamMembers;
   private final AtsApi atsApi;
   private Collection<ArtifactToken> derivedFrom;
   private Collection<ArtifactToken> derivedTo;
   private Collection<IAtsTeamWorkflow> siblings;
   private Collection<IAtsTask> tasks;
   private Collection<IAtsAbstractReview> reviews;
   private Collection<ArtifactToken> supportingInfo;
   private Collection<ArtifactToken> supportedBy;

   public static XXWorkItemData get(Artifact art) {
      Conditions.assertNotNull(art, "Artifact can not be null");
      XXWorkItemData xxWid = null;
      XXArtData xxArt = XXArtData.get(art);
      if (xxArt instanceof XXWorkItemData) {
         xxWid = (XXWorkItemData) xxArt;
         xxWid.updateArtifact(art);
      } else {
         xxWid = new XXWorkItemData(art);
         XXArtData.put(xxWid);
      }
      return xxWid;
   }

   public static XXWorkItemData get(IAtsWorkItem workItem) {
      return get((Artifact) workItem.getStoreObject());
   }

   public XXWorkItemData(Artifact artifact) {
      super(artifact);
      atsApi = AtsApiService.get();
   }

   @Override
   public void clear() {
      super.clear();
      teamDef = null;
      teamDefLoaded = false;
      teamWf = null;
      teamWfLoaded = false;
      targetedVersionLoaded = false;
      targetedVersion = null;
      foundInVersionLoaded = false;
      foundInVersion = null;
      introducedInVersionLoaded = false;
      introducedInVersion = null;
      workItem = null;
      selectableVersions = null;
      parentAction = null;
      parentActionLoaded = false;
      parentTeamWf = null;
      parentTeamWfLoaded = false;
      teamMembers = null;
      derivedFrom = null;
      derivedTo = null;
      siblings = null;
      tasks = null;
      reviews = null;
      supportingInfo = null;
      supportedBy = null;
   }

   public boolean isTeamWf() {
      return artifact != null && artifact.isOfType(AtsArtifactTypes.TeamWorkflow);
   }

   public boolean isWorkItem() {
      return artifact != null && artifact.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact);
   }

   public IAtsTeamWorkflow getTeamWf() {
      if (teamWf == null && !teamWfLoaded) {
         IAtsWorkItem workItem = getWorkItem();
         if (workItem.isTeamWorkflow()) {
            teamWf = (IAtsTeamWorkflow) workItem;
         }
         teamWfLoaded = true; // So don't attempt to reload if not teamWf
      }
      return teamWf;
   }

   public IAtsWorkItem getWorkItem() {
      if (workItem == null && artifact.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         workItem = atsApi.getWorkItemService().getWorkItem(artifact);
      }
      return workItem;
   }

   public IAtsVersion getVersion() {
      return getTargetedVersion();
   }

   public IAtsVersion getTargetedVersion() {
      if (targetedVersion == null && !targetedVersionLoaded) {
         IAtsTeamWorkflow teamWf = getTeamWf();
         if (teamWf != null) {
            targetedVersion = atsApi.getVersionService().getTargetedVersion(teamWf);
         }
         targetedVersionLoaded = true;
      }
      return targetedVersion;
   }

   public IAtsVersion getFoundInVersion() {
      if (foundInVersion == null && !foundInVersionLoaded) {
         IAtsTeamWorkflow teamWf = getTeamWf();
         if (teamWf != null) {
            foundInVersion = atsApi.getVersionService().getFoundInVersion(teamWf);
         }
         foundInVersionLoaded = true;
      }
      return foundInVersion;
   }

   public IAtsVersion getIntroducedInVersion() {
      if (introducedInVersion == null && !introducedInVersionLoaded) {
         IAtsTeamWorkflow teamWf = getTeamWf();
         if (teamWf != null) {
            introducedInVersion = atsApi.getVersionService().getIntroducedInVersion(teamWf);
         }
         introducedInVersionLoaded = true;
      }
      return introducedInVersion;
   }

   public Collection<IAtsVersion> getSelectableVersions() {
      if (selectableVersions == null) {
         if (isTeamWf()) {
            IAtsTeamWorkflow teamWf = getTeamWf();
            selectableVersions = atsApi.getVersionService().getVersionsFromTeamDefHoldingVersions(teamWf.getTeamDef(),
               VersionReleaseType.UnReleased, VersionLockedType.UnLocked);
         } else {
            selectableVersions = Collections.emptyList();
         }
      }
      return selectableVersions;
   }

   public IAtsAction getAction() {
      if (parentAction == null && !parentActionLoaded) {
         IAtsTeamWorkflow parentTeamWf = getParentTeamWf();
         if (parentTeamWf != null) {
            parentAction = parentTeamWf.getParentAction();
         }
         parentActionLoaded = true;
      }
      return parentAction;
   }

   public IAtsTeamWorkflow getParentTeamWf() {
      if (parentTeamWf == null && !parentTeamWfLoaded) {
         parentTeamWf = getWorkItem().getParentTeamWorkflow();
         parentTeamWfLoaded = true;
      }
      return parentTeamWf;
   }

   public IAtsTeamDefinition getTeamDef() {
      if (!teamDefLoaded) {
         if (teamDef != null) {
            return teamDef;
         } else if (isTeamWf()) {
            teamDef = getTeamWf().getTeamDef();
         }
         teamDefLoaded = true;
      }
      return teamDef;
   }

   public Collection<IAtsActionableItem> getActionableItems() {
      return getParentTeamWf().getActionableItems();
   }

   public void setTeamDef(IAtsTeamDefinition teamDef) {
      this.teamDef = teamDef;
   }

   public Collection<AtsUser> getTeamMembers() {
      if (teamMembers == null) {
         teamMembers = Collections.emptyList();
         if (isWorkItem()) {
            IAtsTeamDefinition teamDef = getTeamDef();
            if (teamDef != null) {
               // Regular team workflow: members/leads come from the ATS team definition
               teamMembers = atsApi.getTeamDefinitionService().getMembersAndLeads(teamDef);
            } else {
               // Agile work items (Backlog/Sprint or their children) have no DIRECT ATS team def, but relate to an
               // Agile Team which in turn relates to an ATS team def. AgileService.getTeamMebers resolves members
               // from users related directly to the Agile Team AND the leads/members of its related ATS team def.
               IAgileTeam agileTeam = getAgileTeamForWorkItem();
               if (agileTeam != null) {
                  teamMembers = atsApi.getAgileService().getTeamMebers(agileTeam);
               }
            }
         }
      }
      return teamMembers;
   }

   /**
    * Resolves the Agile Team for the current work item. A Backlog or Sprint resolves directly via its own relation; any
    * other agile work item (e.g. a child on a sprint/backlog) resolves via the work item's agile-team lookup. Returns
    * null when there is no related agile team.
    */
   private IAgileTeam getAgileTeamForWorkItem() {
      if (artifact.isOfType(AtsArtifactTypes.AgileBacklog)) {
         IAgileBacklog backlog = atsApi.getAgileService().getAgileBacklog(artifact);
         return backlog == null ? null : atsApi.getAgileService().getAgileTeamFromBacklog(backlog);
      }
      if (artifact.isOfType(AtsArtifactTypes.AgileSprint)) {
         IAgileSprint sprint = atsApi.getAgileService().getAgileSprint(artifact);
         return sprint == null ? null : atsApi.getAgileService().getAgileTeamFromSprint(sprint);
      }
      return atsApi.getAgileService().getAgileTeam(getWorkItem());
   }

   public Collection<ArtifactToken> getDerivedFrom() {
      if (derivedFrom == null) {
         derivedFrom = atsApi.getRelationResolver().getRelated(workItem, AtsRelationTypes.Derive_From);
      }
      return derivedFrom;
   }

   public Collection<ArtifactToken> getDerivedTo() {
      if (derivedTo == null) {
         derivedTo = atsApi.getRelationResolver().getRelated(workItem, AtsRelationTypes.Derive_To);
      }
      return derivedTo;
   }

   public Collection<IAtsTeamWorkflow> getSiblings() {
      if (siblings == null) {
         siblings = atsApi.getWorkItemService().getSiblings(getTeamWf());
      }
      return siblings;
   }

   public Collection<IAtsTask> getTasks() {
      if (tasks == null) {
         tasks = atsApi.getTaskService().getTasks(getTeamWf());
      }
      return tasks;
   }

   public Collection<IAtsAbstractReview> getReviews() {
      if (reviews == null) {
         reviews = atsApi.getReviewService().getReviews(getTeamWf());
      }
      return reviews;
   }

   public Collection<ArtifactToken> getSupportingInfo() {
      if (supportingInfo == null) {
         supportingInfo = atsApi.getRelationResolver().getRelated(workItem.getStoreObject(),
            CoreRelationTypes.SupportingInfo_SupportingInfo);
      }
      return supportingInfo;
   }

   public Collection<ArtifactToken> getSupportedBy() {
      if (supportedBy == null) {
         supportedBy = atsApi.getRelationResolver().getRelated(workItem.getStoreObject(),
            CoreRelationTypes.SupportingInfo_IsSupportedBy);
      }
      return supportedBy;
   }

   public boolean isVersionLoaded() {
      return isTargetedVersionLoaded();
   }

   public boolean isTargetedVersionLoaded() {
      return targetedVersionLoaded;
   }

   public boolean isFoundInVersionLoaded() {
      return foundInVersionLoaded;
   }

   public boolean isIntroducedInVersionLoaded() {
      return introducedInVersionLoaded;
   }

}
