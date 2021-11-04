/*********************************************************************
 * Copyright (c) 2010 Boeing
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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.core.workflow.util.ChangeTypeUtil;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.ChangeTypeDialog;
import org.eclipse.osee.ats.ide.workflow.ChangeTypeToSwtImage;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class ChangeTypeColumnUI extends XViewerAtsAttributeValueColumn {

   public static ChangeTypeColumnUI instance = new ChangeTypeColumnUI();

   public static ChangeTypeColumnUI getInstance() {
      return instance;
   }

   private ChangeTypeColumnUI() {
      super(AtsColumnToken.ChangeTypeColumn);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ChangeTypeColumnUI copy() {
      ChangeTypeColumnUI newXCol = new ChangeTypeColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   public static boolean promptChangeType(final Collection<? extends TeamWorkFlowArtifact> teams, boolean persist) {

      try {
         for (TeamWorkFlowArtifact team : teams) {
            if (AtsApiService.get().getVersionService().isReleased(
               team) || AtsApiService.get().getVersionService().isVersionLocked(team)) {
               AWorkbench.popup("ERROR",
                  "Team Workflow\n \"" + team.getName() + "\"\n version is locked or already released.");
               return false;
            }
         }
         final ChangeTypeDialog dialog = new ChangeTypeDialog(Displays.getActiveShell());
         if (teams.size() == 1) {
            ChangeType changeType = ChangeTypeUtil.getChangeType(teams.iterator().next(), AtsApiService.get());
            if (changeType != null) {
               dialog.setSelected(changeType);
            }
         }
         if (dialog.open() == Window.OK) {

            IAtsChangeSet changes = AtsApiService.get().createChangeSet("ATS Prompt Change Type");

            ChangeType newChangeType = dialog.getSelection();
            for (TeamWorkFlowArtifact team : teams) {
               ChangeType currChangeType = ChangeTypeUtil.getChangeType(team, AtsApiService.get());
               if (currChangeType != newChangeType) {
                  ChangeTypeUtil.setChangeType(team, newChangeType, changes);
                  if (persist) {
                     team.save(changes);
                  }
               }
            }
            if (persist) {
               changes.execute();
            }
         }
         return true;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't change priority", ex);
         return false;
      }
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         if (treeItem.getData() instanceof Artifact) {
            Artifact useArt = AtsApiService.get().getQueryServiceIde().getArtifact(treeItem);
            // Only prompt change for sole attribute types
            if (useArt.getArtifactType().getMax(getAttributeType()) != 1) {
               return false;
            }
            if (useArt.isOfType(AtsArtifactTypes.Action)) {
               if (AtsApiService.get().getWorkItemService().getTeams(useArt).size() == 1) {
                  useArt = (AbstractWorkflowArtifact) AtsApiService.get().getWorkItemService().getFirstTeam(
                     useArt).getStoreObject();
               } else {
                  return false;
               }
            }
            if (!useArt.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               return false;
            }
            boolean modified = promptChangeType(Arrays.asList((TeamWorkFlowArtifact) useArt), isPersistViewer());
            XViewer xViewer = (XViewer) ((XViewerColumn) treeColumn.getData()).getXViewer();
            if (modified && isPersistViewer(xViewer)) {
               useArt.persist("persist change type via alt-left-click");
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

   @Override
   public Image getColumnImage(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof IAtsWorkItem) {
            IAtsWorkItem workItem = (IAtsWorkItem) element;
            Artifact useArt = getParentTeamWorkflowOrArtifact(workItem);
            if (useArt != null) {
               ChangeType changeType = ChangeTypeUtil.getChangeType(workItem, AtsApiService.get());
               if (changeType != null) {
                  return ChangeTypeToSwtImage.getImage(changeType);
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      Set<TeamWorkFlowArtifact> awas = new HashSet<>();
      for (TreeItem item : treeItems) {
         if (item.getData() instanceof Artifact) {
            Artifact art = AtsApiService.get().getQueryServiceIde().getArtifact(item);
            if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               awas.add((TeamWorkFlowArtifact) art);
            }
         }
      }
      promptChangeType(awas, true);
      ((XViewer) getXViewer()).update(awas.toArray(), null);
   }

}
