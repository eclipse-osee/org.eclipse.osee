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
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.column.ChangeTypeColumn;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.AtsColumnUtilIde;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsCoreCodeXColumn;
import org.eclipse.osee.ats.ide.workflow.chgtype.ChangeTypeDialog;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class ChangeTypeColumnUI extends XViewerAtsCoreCodeXColumn {

   public static ChangeTypeColumnUI instance = new ChangeTypeColumnUI();

   public static ChangeTypeColumnUI getInstance() {
      return instance;
   }

   private ChangeTypeColumnUI() {
      super(AtsColumnTokensDefault.ChangeTypeColumn, AtsApiService.get());
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

   public static boolean promptChangeType(final Collection<IAtsTeamWorkflow> teamWfs) {

      try {
         for (IAtsTeamWorkflow teamWf : teamWfs) {
            if (AtsApiService.get().getVersionService().isReleased(
               teamWf) || AtsApiService.get().getVersionService().isVersionLocked(teamWf)) {
               AWorkbench.popup("ERROR",
                  "Team Workflow\n \"" + teamWf.getName() + "\"\n version is locked or already released.");
               return false;
            }
         }
         final ChangeTypeDialog dialog = new ChangeTypeDialog(teamWfs, null);
         if (teamWfs.size() == 1) {
            ChangeTypes changeType = ChangeTypeColumn.getChangeType(teamWfs.iterator().next(), AtsApiService.get());
            if (changeType != null) {
               dialog.setSelected(changeType);
            }
         }
         if (dialog.open() == Window.OK) {

            IAtsChangeSet changes = AtsApiService.get().createChangeSet("ATS Prompt Change Type");

            ChangeTypes newChangeType = dialog.getSelected();
            for (IAtsTeamWorkflow team : teamWfs) {
               ChangeTypeColumn.setChangeType(team, newChangeType, changes);
            }
            changes.executeIfNeeded();
         }
         return true;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't change Change Type", ex);
         return false;
      }
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         if (treeItem.getData() instanceof Artifact) {
            Artifact useArt = AtsApiService.get().getQueryServiceIde().getArtifact(treeItem);
            // Only prompt change for sole attribute types
            if (useArt.getArtifactType().getMax(AtsAttributeTypes.ChangeType) != 1) {
               return false;
            }
            if (!useArt.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               AWorkbench.popup(AtsColumnUtilIde.INVALID_SELECTION, AtsColumnUtilIde.INVALID_COLUMN_FOR_SELECTED,
                  treeColumn.getText());
               return false;
            }
            boolean modified = promptChangeType(Arrays.asList((TeamWorkFlowArtifact) useArt));
            XViewer xViewer = (XViewer) ((XViewerColumn) treeColumn.getData()).getXViewer();
            if (modified) {
               useArt.persist("persist change type via alt-left-click");
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
            IAtsTeamWorkflow useArt = workItem.getParentTeamWorkflow();
            if (useArt != null) {
               ChangeTypes changeType = ChangeTypeColumn.getChangeType(workItem, AtsApiService.get());
               if (changeType != null) {
                  return getImage(changeType);
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
      Set<IAtsTeamWorkflow> awas = new HashSet<>();
      for (TreeItem item : treeItems) {
         if (item.getData() instanceof Artifact) {
            Artifact art = AtsApiService.get().getQueryServiceIde().getArtifact(item);
            if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               awas.add((IAtsTeamWorkflow) art);
            }
         }
      }
      if (awas.isEmpty()) {
         AWorkbench.popup(AtsColumnUtilIde.INVALID_SELECTION, AtsColumnUtilIde.INVALID_COLUMN_FOR_SELECTED,
            treeColumn.getText());
         return;
      }
      promptChangeType(awas);
      ((XViewer) getXViewer()).update(awas.toArray(), null);
   }

   public static Image getImage(ChangeTypes type) {
      if (type == ChangeTypes.Problem || type == ChangeTypes.Fix) {
         return ImageManager.getImage(FrameworkImage.PROBLEM);
      } else if (type == ChangeTypes.Improvement || type == ChangeTypes.InitialDev) {
         return ImageManager.getImage(FrameworkImage.GREEN_PLUS);
      } else if (type == ChangeTypes.Support) {
         return ImageManager.getImage(FrameworkImage.SUPPORT);
      } else if (type == ChangeTypes.Refinement) {
         return ImageManager.getImage(FrameworkImage.REFINEMENT);
      }
      return null;
   }

}
