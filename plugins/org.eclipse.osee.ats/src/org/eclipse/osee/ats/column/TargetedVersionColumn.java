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
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.type.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.client.version.TargetedVersionUtil;
import org.eclipse.osee.ats.core.client.version.VersionArtifact;
import org.eclipse.osee.ats.core.client.version.VersionLockedType;
import org.eclipse.osee.ats.core.client.version.VersionReleaseType;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.widgets.dialog.VersionListDialog;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class TargetedVersionColumn extends XViewerAtsColumn implements IXViewerValueColumn, IAltLeftClickProvider, IMultiColumnEditProvider {

   public static TargetedVersionColumn instance = new TargetedVersionColumn();

   public static TargetedVersionColumn getInstance() {
      return instance;
   }

   private TargetedVersionColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".versionTarget", "Targeted Version", 40, SWT.LEFT, true,
         SortDataType.String, true, "Date this workflow transitioned to the Completed state.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public TargetedVersionColumn copy() {
      TargetedVersionColumn newXCol = new TargetedVersionColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         if (treeItem.getData() instanceof Artifact) {
            Artifact useArt = (Artifact) treeItem.getData();
            if (useArt.isOfType(AtsArtifactTypes.Action)) {
               if (ActionManager.getTeams(useArt).size() == 1) {
                  useArt = ActionManager.getFirstTeam(useArt);
               } else {
                  return false;
               }
            }
            if (!(useArt.isOfType(AtsArtifactTypes.TeamWorkflow))) {
               return false;
            }
            boolean modified =
               promptChangeVersion(Arrays.asList((TeamWorkFlowArtifact) useArt),
                  AtsUtilCore.isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased,
                  AtsUtilCore.isAtsAdmin() ? VersionLockedType.Both : VersionLockedType.UnLocked);
            XViewer xViewer = ((XViewerColumn) treeColumn.getData()).getTreeViewer();
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
      if (AtsUtilCore.isAtsAdmin() && !sma.isTeamWorkflow()) {
         AWorkbench.popup("ERROR ", "Cannot set version for: \n\n" + sma.getName());
         return false;
      }
      return promptChangeVersion(Arrays.asList((TeamWorkFlowArtifact) sma), versionReleaseType, versionLockType);
   }

   public static boolean promptChangeVersion(final Collection<? extends TeamWorkFlowArtifact> awas, VersionReleaseType versionReleaseType, VersionLockedType versionLockType) throws OseeCoreException {
      TeamDefinitionArtifact teamDefHoldingVersions = null;
      for (TeamWorkFlowArtifact teamArt : awas) {
         if (!teamArt.getTeamDefinition().isTeamUsesVersions()) {
            AWorkbench.popup("ERROR", "Team \"" + teamArt.getTeamDefinition().getName() + "\" doesn't use versions.");
            return false;
         }
         if (teamArt.isReleased() || teamArt.isVersionLocked()) {
            String error =
               "Team Workflow\n \"" + teamArt.getName() + "\"\n targeted version is locked or already released.";
            if (AtsUtilCore.isAtsAdmin() && !MessageDialog.openConfirm(Displays.getActiveShell(), "Change Version",
               error + "\n\nOverride?")) {
               return false;
            } else if (!AtsUtilCore.isAtsAdmin()) {
               AWorkbench.popup("ERROR", error);
               continue;
            }
         }
         if (teamDefHoldingVersions != null && teamDefHoldingVersions != teamArt.getTeamDefinition().getTeamDefinitionHoldingVersions()) {
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
      final VersionListDialog vld =
         new VersionListDialog("Select Version", "Select Version", teamDefHoldingVersions.getVersionsArtifacts(
            versionReleaseType, versionLockType));
      if (awas.size() == 1 && awas.iterator().next().getTargetedVersion() != null) {
         Object[] objs = new Object[1];
         objs[0] = awas.iterator().next().getTargetedVersion();
         vld.setInitialSelections(objs);
      }
      int result = vld.open();
      if (result != 0) {
         return false;
      }
      Object obj = vld.getResult()[0];
      VersionArtifact newVersion = (VersionArtifact) obj;
      //now check selected version
      if (newVersion.isVersionLocked()) {
         String error = "Version \"" + newVersion.getFullDisplayName() + "\" is locked or already released.";
         if (AtsUtilCore.isAtsAdmin() && !MessageDialog.openConfirm(Displays.getActiveShell(), "Change Version",
            error + "\n\nOverride?")) {
            return false;
         } else if (!AtsUtilCore.isAtsAdmin()) {
            AWorkbench.popup("ERROR", error);
         }
      }

      for (TeamWorkFlowArtifact teamArt : awas) {
         teamArt.setRelations(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version,
            java.util.Collections.singleton(newVersion));
      }
      Artifacts.persistInTransaction("ATS Prompt Change Version", awas);
      return true;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (Artifacts.isOfType(element, AtsArtifactTypes.Action)) {
            Set<String> strs = new HashSet<String>();
            for (TeamWorkFlowArtifact team : ActionManager.getTeams(element)) {
               String str = team.getTargetedVersionStr();
               if (Strings.isValid(str)) {
                  strs.add(str);
               }
            }
            return Collections.toString(";", strs);

         } else {
            return TargetedVersionUtil.getTargetedVersionStr(element);
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         Set<TeamWorkFlowArtifact> awas = new HashSet<TeamWorkFlowArtifact>();
         List<Artifact> arts = new ArrayList<Artifact>();
         for (TreeItem item : treeItems) {
            Artifact art = (Artifact) item.getData();
            if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               awas.add((TeamWorkFlowArtifact) art);
               arts.add(art);
            }
         }

         promptChangeVersion(awas, AtsUtilCore.isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased,
            AtsUtilCore.isAtsAdmin() ? VersionLockedType.Both : VersionLockedType.UnLocked);
         getXViewer().update(awas.toArray(), null);
         return;
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
