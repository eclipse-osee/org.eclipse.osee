/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.column.AtsColumnIdValueColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.VersionLockedType;
import org.eclipse.osee.ats.api.version.VersionReleaseType;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.widgets.dialog.VersionListDialog;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumnIdColumn;
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
public abstract class AbstractVersionSelector extends XViewerAtsColumnIdColumn implements IMultiColumnEditProvider, IAltLeftClickProvider {

   public AbstractVersionSelector(AtsColumnIdValueColumn column) {
      super(column);
   }

   public abstract RelationTypeSide getRelation();

   public boolean promptChangeVersion(AbstractWorkflowArtifact sma) {
      return promptChangeVersion(sma, null, null);
   }

   public boolean promptChangeVersion(AbstractWorkflowArtifact sma, VersionReleaseType verRelType, VersionLockedType verLockType) {
      return promptChangeVersionMultiSelect(Arrays.asList((TeamWorkFlowArtifact) sma), verRelType, verLockType);
   }

   private boolean isTargetedVersionRelation() {
      return (getRelation().equals(TargetedVersionColumnUI.getInstance().getRelation()));
   }

   private boolean isFoundInVersionRelation() {
      return (getRelation().equals(FoundInVersionColumnUI.getInstance().getRelation()));
   }

   public boolean promptChangeVersionMultiSelect(List<TeamWorkFlowArtifact> awas, VersionReleaseType versionReleaseType, VersionLockedType versionLockType) {
      if (awas.isEmpty()) {
         return false;
      }

      //validate multi-select
      IAtsTeamDefinition teamDefHoldingVersions = null;
      for (TeamWorkFlowArtifact teamArt : awas) {
         if (!AtsClientService.get().getVersionService().isTeamUsesVersions(teamArt.getTeamDefinition())) {
            AWorkbench.popup("ERROR", "Team \"" + teamArt.getTeamDefinition().getName() + "\" doesn't use versions.");
            return false;
         }

         if (AtsClientService.get().getUserService().isAtsAdmin() && !teamArt.isTeamWorkflow()) {
            AWorkbench.popup("ERROR ", "Cannot set version for: \n\n" + teamArt.getName());
            return false;
         }

         if (AtsClientService.get().getVersionService().isReleased(
            teamArt) || AtsClientService.get().getVersionService().isVersionLocked(teamArt)) {
            String error =
               "Team Workflow\n \"" + teamArt.getName() + "\"\n targeted version is locked or already released.";
            if (AtsClientService.get().getUserService().isAtsAdmin() && !MessageDialog.openConfirm(
               Displays.getActiveShell(), "Change Version", error + "\n\nOverride?")) {
               return false;
            } else if (!AtsClientService.get().getUserService().isAtsAdmin()) {
               AWorkbench.popup("ERROR", error);
               continue;
            }
         }
         if (teamDefHoldingVersions != null && teamDefHoldingVersions.notEqual(
            AtsClientService.get().getTeamDefinitionService().getTeamDefinitionHoldingVersions(
               teamArt.getTeamDefinition()))) {
            AWorkbench.popup("ERROR", "Can't change version on Workflows that have different release version sets.");
            return false;
         }
         if (teamDefHoldingVersions == null) {
            teamDefHoldingVersions = AtsClientService.get().getTeamDefinitionService().getTeamDefinitionHoldingVersions(
               teamArt.getTeamDefinition());
         }
      }

      //call prompt on first version, set rest as same that was selected
      IAtsVersion selectedVersion =
         promptVersionSelectorDialog(awas.get(0), versionReleaseType, versionLockType, teamDefHoldingVersions);
      if (selectedVersion == null) {
         return false;
      }
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("ATS Prompt Change Version");
      for (TeamWorkFlowArtifact sma : awas) {
         // TargetedVerison colUI, use interface method
         if (isTargetedVersionRelation()) {
            AtsClientService.get().getVersionService().setTargetedVersion(sma, selectedVersion, changes);
         } else {
            //If foundInVersion and selected == oldVersion
            if (isFoundInVersionRelation() && selectedVersion == AtsClientService.get().getVersionService().getFoundInVersion(
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

   public IAtsVersion promptVersionSelectorDialog(TeamWorkFlowArtifact teamArt, VersionReleaseType versionReleaseType, VersionLockedType versionLockType, IAtsTeamDefinition teamDefHoldingVersions) {
      if (teamDefHoldingVersions == null) {
         teamDefHoldingVersions = AtsClientService.get().getTeamDefinitionService().getTeamDefinitionHoldingVersions(
            teamArt.getTeamDefinition());
      }
      final VersionListDialog dialog;
      if (versionReleaseType == null || versionLockType == null) {
         if (teamDefHoldingVersions == null) {
            AWorkbench.popup("ERROR", "No versions configured for impacted team(s).");
            return null;
         }
         dialog = new VersionListDialog("Select Version", "Select Version",
            AtsClientService.get().getVersionService().getVersions(teamDefHoldingVersions));
      } else {
         dialog = new VersionListDialog("Select Version", "Select Version",
            AtsClientService.get().getVersionService().getVersions(teamDefHoldingVersions, versionReleaseType,
               versionLockType));
      }
      if (AtsClientService.get().getVersionService().hasTargetedVersion(teamArt)) {
         dialog.setInitialSelections(
            Arrays.asList(AtsClientService.get().getVersionService().getTargetedVersion(teamArt)));
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
         if (AtsClientService.get().getUserService().isAtsAdmin() && !MessageDialog.openConfirm(
            Displays.getActiveShell(), "Change Version", error + "\n\nOverride?")) {
            return null;
         } else if (!AtsClientService.get().getUserService().isAtsAdmin()) {
            AWorkbench.popup("ERROR", error);
         }
      }
      return newVersion;
   }

   public String getCommitFullDisplayName(IAtsVersion version) {
      List<String> strs = new ArrayList<>();
      strs.add(getName());
      String fullName =
         AtsClientService.get().getAttributeResolver().getSoleAttributeValue(version, AtsAttributeTypes.FullName, "");
      if (Strings.isValid(fullName)) {
         strs.add(fullName);
      }
      String description = AtsClientService.get().getAttributeResolver().getSoleAttributeValue(version,
         AtsAttributeTypes.Description, "");
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
            Artifact art = AtsClientService.get().getQueryServiceClient().getArtifact(item);
            if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               awas.add((TeamWorkFlowArtifact) art);
               arts.add(art);
            }
         }
         if (promptChangeVersionMultiSelect(new ArrayList<TeamWorkFlowArtifact>(awas),
            AtsClientService.get().getUserService().isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased,
            AtsClientService.get().getUserService().isAtsAdmin() ? VersionLockedType.Both : VersionLockedType.UnLocked)) {
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
            Artifact useArt = AtsClientService.get().getQueryServiceClient().getArtifact(treeItem);
            if (useArt.isOfType(AtsArtifactTypes.Action)) {
               if (AtsClientService.get().getWorkItemService().getTeams(useArt).size() == 1) {
                  useArt = AtsClientService.get().getQueryServiceClient().getArtifact(
                     AtsClientService.get().getWorkItemService().getFirstTeam(useArt));
               } else {
                  return false;
               }
            }
            if (!useArt.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               return false;
            }
            boolean modified = promptChangeVersion((TeamWorkFlowArtifact) useArt,
               AtsClientService.get().getUserService().isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased,
               AtsClientService.get().getUserService().isAtsAdmin() ? VersionLockedType.Both : VersionLockedType.UnLocked);
            XViewer xViewer = (XViewer) ((XViewerColumn) treeColumn.getData()).getXViewer();
            if (modified && isPersistViewer(xViewer)) {
               useArt.persist("persist goals via alt-left-click");
            }
            if (modified) {
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
