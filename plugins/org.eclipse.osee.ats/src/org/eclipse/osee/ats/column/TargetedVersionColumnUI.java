/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.column;

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
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.VersionLockedType;
import org.eclipse.osee.ats.api.version.VersionReleaseType;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.widgets.dialog.VersionListDialog;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumnIdColumn;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class TargetedVersionColumnUI extends XViewerAtsColumnIdColumn implements IAltLeftClickProvider, IMultiColumnEditProvider {

   public static TargetedVersionColumnUI instance = new TargetedVersionColumnUI();

   public static TargetedVersionColumnUI getInstance() {
      return instance;
   }

   private TargetedVersionColumnUI() {
      super(AtsColumnToken.TargtedVersionColumn);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public TargetedVersionColumnUI copy() {
      TargetedVersionColumnUI newXCol = new TargetedVersionColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         if (treeItem.getData() instanceof Artifact) {
            Artifact useArt = (Artifact) treeItem.getData();
            if (useArt.isOfType(AtsArtifactTypes.Action)) {
               if (AtsClientService.get().getWorkItemService().getTeams(useArt).size() == 1) {
                  useArt = (Artifact) AtsClientService.get().getWorkItemService().getFirstTeam(useArt).getStoreObject();
               } else {
                  return false;
               }
            }
            if (!useArt.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               return false;
            }
            boolean modified = promptChangeVersion(Arrays.asList((TeamWorkFlowArtifact) useArt),
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

   public static boolean promptChangeVersion(AbstractWorkflowArtifact sma, VersionReleaseType versionReleaseType, VersionLockedType versionLockType) throws OseeCoreException {
      if (AtsClientService.get().getUserService().isAtsAdmin() && !sma.isTeamWorkflow()) {
         AWorkbench.popup("ERROR ", "Cannot set version for: \n\n" + sma.getName());
         return false;
      }
      return promptChangeVersion(Arrays.asList((TeamWorkFlowArtifact) sma), versionReleaseType, versionLockType);
   }

   public static boolean promptChangeVersion(final Collection<? extends TeamWorkFlowArtifact> awas, VersionReleaseType versionReleaseType, VersionLockedType versionLockType) throws OseeCoreException {
      IAtsTeamDefinition teamDefHoldingVersions = null;
      for (TeamWorkFlowArtifact teamArt : awas) {
         if (!teamArt.getTeamDefinition().isTeamUsesVersions()) {
            AWorkbench.popup("ERROR", "Team \"" + teamArt.getTeamDefinition().getName() + "\" doesn't use versions.");
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
            teamArt.getTeamDefinition().getTeamDefinitionHoldingVersions())) {
            AWorkbench.popup("ERROR", "Can't change version on Workflows that have different release version sets.");
            return false;
         }
         if (teamDefHoldingVersions == null) {
            teamDefHoldingVersions = teamArt.getTeamDefinition().getTeamDefinitionHoldingVersions();
         }
      }
      if (teamDefHoldingVersions == null) {
         AWorkbench.popup("ERROR", "No versions configured for impacted team(s).");
         return false;
      }
      TeamWorkFlowArtifact teamArt = awas.iterator().next();
      final VersionListDialog dialog = new VersionListDialog("Select Version", "Select Version",
         teamDefHoldingVersions.getVersions(versionReleaseType, versionLockType));
      if (awas.size() == 1 && AtsClientService.get().getVersionService().hasTargetedVersion(teamArt)) {
         dialog.setInitialSelections(
            Arrays.asList(AtsClientService.get().getVersionService().getTargetedVersion(teamArt)));
      }
      int result = dialog.open();
      if (result != 0) {
         return false;
      }
      Object obj = dialog.getSelectedFirst();
      IAtsVersion newVersion = (IAtsVersion) obj;
      //now check selected version
      if (newVersion != null && newVersion.isVersionLocked()) {
         String error = "Version \"" + newVersion.getCommitFullDisplayName() + "\" is locked or already released.";
         if (AtsClientService.get().getUserService().isAtsAdmin() && !MessageDialog.openConfirm(
            Displays.getActiveShell(), "Change Version", error + "\n\nOverride?")) {
            return false;
         } else if (!AtsClientService.get().getUserService().isAtsAdmin()) {
            AWorkbench.popup("ERROR", error);
         }
      }

      IAtsChangeSet changes = AtsClientService.get().createChangeSet("ATS Prompt Change Version");

      for (TeamWorkFlowArtifact teamArt1 : awas) {
         AtsClientService.get().getVersionService().setTargetedVersion(teamArt1, newVersion, changes);
      }
      changes.execute();

      ArtifactQuery.reloadArtifacts(awas);

      return true;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         Set<TeamWorkFlowArtifact> awas = new HashSet<>();
         List<Artifact> arts = new ArrayList<>();
         for (TreeItem item : treeItems) {
            Artifact art = (Artifact) item.getData();
            if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               awas.add((TeamWorkFlowArtifact) art);
               arts.add(art);
            }
         }

         promptChangeVersion(awas,
            AtsClientService.get().getUserService().isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased,
            AtsClientService.get().getUserService().isAtsAdmin() ? VersionLockedType.Both : VersionLockedType.UnLocked);
         ((XViewer) getXViewer()).update(awas.toArray(), null);
         return;
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
