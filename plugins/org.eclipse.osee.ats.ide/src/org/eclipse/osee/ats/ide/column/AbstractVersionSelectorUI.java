/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.ide.column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.column.AtsCoreCodeColumnToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.VersionLockedType;
import org.eclipse.osee.ats.api.version.VersionReleaseType;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.dialog.VersionListDialog;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsCoreCodeXColumn;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Jeremy A. Midvidy
 */
public abstract class AbstractVersionSelectorUI extends XViewerAtsCoreCodeXColumn {

   public AbstractVersionSelectorUI(AtsCoreCodeColumnToken column) {
      super(column, AtsApiService.get());
   }

   public abstract RelationTypeSide getRelation();

   public boolean promptChangeVersion(AbstractWorkflowArtifact sma) {
      return promptChangeVersion(sma, null, null);
   }

   public boolean promptChangeVersion(AbstractWorkflowArtifact sma, VersionReleaseType verRelType,
      VersionLockedType verLockType) {
      return promptChangeVersionMultiSelect(Arrays.asList((TeamWorkFlowArtifact) sma), verRelType, verLockType);
   }

   private boolean isTargetedVersionRelation() {
      return (getRelation().equals(TargetedVersionColumnUI.getInstance().getRelation()));
   }

   private boolean isFoundInVersionRelation() {
      return (getRelation().equals(FoundInVersionColumnUI.getInstance().getRelation()));
   }

   public boolean promptChangeVersionMultiSelect(List<TeamWorkFlowArtifact> awas, VersionReleaseType versionReleaseType,
      VersionLockedType versionLockType) {
      if (awas.isEmpty()) {
         return false;
      }

      //validate multi-select
      IAtsTeamDefinition teamDefHoldingVersions = null;
      for (TeamWorkFlowArtifact teamArt : awas) {
         if (!AtsApiService.get().getVersionService().isTeamUsesVersions(teamArt.getTeamDefinition())) {
            AWorkbench.popup("ERROR", "Team \"" + teamArt.getTeamDefinition().getName() + "\" doesn't use versions.");
            return false;
         }

         if (AtsApiService.get().getUserService().isAtsAdmin() && !teamArt.isTeamWorkflow()) {
            AWorkbench.popup("ERROR ", "Cannot set version for: \n\n" + teamArt.getName());
            return false;
         }

         if (AtsApiService.get().getVersionService().isReleased(
            teamArt) || AtsApiService.get().getVersionService().isVersionLocked(teamArt)) {
            String error =
               "Team Workflow\n \"" + teamArt.getName() + "\"\n targeted version is locked or already released.";
            if (AtsApiService.get().getUserService().isAtsAdmin() && !MessageDialog.openConfirm(
               Displays.getActiveShell(), "Change Version", error + "\n\nOverride?")) {
               return false;
            } else if (!AtsApiService.get().getUserService().isAtsAdmin()) {
               AWorkbench.popup("ERROR", error);
               continue;
            }
         }
         if (teamDefHoldingVersions != null && teamDefHoldingVersions.notEqual(
            AtsApiService.get().getTeamDefinitionService().getTeamDefinitionHoldingVersions(
               teamArt.getTeamDefinition()))) {
            AWorkbench.popup("ERROR", "Can't change version on Workflows that have different release version sets.");
            return false;
         }
         if (teamDefHoldingVersions == null) {
            teamDefHoldingVersions = AtsApiService.get().getTeamDefinitionService().getTeamDefinitionHoldingVersions(
               teamArt.getTeamDefinition());
         }
      }

      //call prompt on first version, set rest as same that was selected
      IAtsVersion selectedVersion =
         promptVersionSelectorDialog(awas.get(0), versionReleaseType, versionLockType, teamDefHoldingVersions);
      if (selectedVersion == null) {
         return false;
      }
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("ATS Prompt Change Version");
      for (TeamWorkFlowArtifact sma : awas) {
         // TargetedVerison colUI, use interface method
         if (isTargetedVersionRelation()) {
            AtsApiService.get().getVersionService().setTargetedVersion(sma, selectedVersion, changes);
         } else {
            //If foundInVersion and selected == oldVersion
            if (isFoundInVersionRelation() && selectedVersion == AtsApiService.get().getVersionService().getFoundInVersion(
               sma)) {
               // FIV and all are same as prev, changes will be empty --> return false
               continue;
            } else {
               // set generic relation
               changes.setRelation(sma, getRelation(), selectedVersion);
            }
         }
      }
      if (changes.isEmpty()) {
         return false;
      }
      changes.executeIfNeeded();
      ArtifactQuery.reloadArtifacts(awas);
      return true;
   }

   public IAtsVersion promptVersionSelectorDialog(TeamWorkFlowArtifact teamArt, VersionReleaseType versionReleaseType,
      VersionLockedType versionLockType, IAtsTeamDefinition teamDefHoldingVersions) {
      if (teamDefHoldingVersions == null) {
         teamDefHoldingVersions = AtsApiService.get().getTeamDefinitionService().getTeamDefinitionHoldingVersions(
            teamArt.getTeamDefinition());
      }
      final VersionListDialog dialog;
      if (versionReleaseType == null || versionLockType == null) {
         if (teamDefHoldingVersions == null) {
            AWorkbench.popup("ERROR", "No versions configured for impacted team(s).");
            return null;
         }
         dialog = new VersionListDialog("Select Version", "Select Version",
            AtsApiService.get().getVersionService().getVersions(teamDefHoldingVersions));
      } else {
         dialog = new VersionListDialog("Select Version", "Select Version",
            AtsApiService.get().getVersionService().getVersions(teamDefHoldingVersions, versionReleaseType,
               versionLockType));
      }
      if (AtsApiService.get().getVersionService().hasTargetedVersion(teamArt)) {
         dialog.setInitialSelections(
            Arrays.asList(AtsApiService.get().getVersionService().getTargetedVersion(teamArt)));
      }
      int result = dialog.open();
      if (result != 0) {
         return null;
      }
      Object obj = dialog.getSelectedFirst();
      IAtsVersion newVersion = (IAtsVersion) obj;
      //now check selected version
      if (newVersion != null && newVersion.isLocked()) {
         String error = "Version \"" + getCommitFullDisplayName(newVersion) + "\" is locked or already released.";
         if (AtsApiService.get().getUserService().isAtsAdmin() && !MessageDialog.openConfirm(Displays.getActiveShell(),
            "Change Version", error + "\n\nOverride?")) {
            return null;
         } else if (!AtsApiService.get().getUserService().isAtsAdmin()) {
            AWorkbench.popup("ERROR", error);
         }
      }
      return newVersion;
   }

   public String getCommitFullDisplayName(IAtsVersion version) {
      List<String> strs = new ArrayList<>();
      strs.add(getName());
      String fullName =
         AtsApiService.get().getAttributeResolver().getSoleAttributeValue(version, AtsAttributeTypes.FullName, "");
      if (Strings.isValid(fullName)) {
         strs.add(fullName);
      }
      String description =
         AtsApiService.get().getAttributeResolver().getSoleAttributeValue(version, AtsAttributeTypes.Description, "");
      if (Strings.isValid(description)) {
         strs.add(description);
      }
      return Collections.toString(" - ", strs);
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         Set<TeamWorkFlowArtifact> awas = new HashSet<>();
         List<Artifact> arts = new ArrayList<>();
         for (TreeItem item : treeItems) {
            Artifact art = AtsApiService.get().getQueryServiceIde().getArtifact(item);
            if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               awas.add((TeamWorkFlowArtifact) art);
               arts.add(art);
            }
         }
         if (promptChangeVersionMultiSelect(new ArrayList<TeamWorkFlowArtifact>(awas),
            AtsApiService.get().getUserService().isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased,
            AtsApiService.get().getUserService().isAtsAdmin() ? VersionLockedType.Both : VersionLockedType.UnLocked)) {
            ((XViewer) getXViewer()).update(awas.toArray(), null);
         }
         return;
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         if (treeItem.getData() instanceof Artifact) {
            Artifact useArt = AtsApiService.get().getQueryServiceIde().getArtifact(treeItem);
            if (useArt.isOfType(AtsArtifactTypes.Action)) {
               if (AtsApiService.get().getWorkItemService().getTeams(useArt).size() == 1) {
                  useArt = AtsApiService.get().getQueryServiceIde().getArtifact(
                     AtsApiService.get().getWorkItemService().getFirstTeam(useArt));
               } else {
                  return false;
               }
            }
            if (!useArt.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               return false;
            }
            boolean modified = promptChangeVersion((TeamWorkFlowArtifact) useArt,
               AtsApiService.get().getUserService().isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased,
               AtsApiService.get().getUserService().isAtsAdmin() ? VersionLockedType.Both : VersionLockedType.UnLocked);
            XViewer xViewer = (XViewer) ((XViewerColumn) treeColumn.getData()).getXViewer();
            if (modified) {
               useArt.persist("persist goals via alt-left-click");
               xViewer.update(useArt, null);
               return true;
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return false;
   }

}
