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

package org.eclipse.osee.ats.ide.workflow.priority;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokens;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.Priorities;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.column.PriorityColumn;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumnIdColumn;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class PriorityColumnUI extends XViewerAtsColumnIdColumn implements IAltLeftClickProvider, IMultiColumnEditProvider {

   public static PriorityColumnUI instance = new PriorityColumnUI();

   public static PriorityColumnUI getInstance() {
      return instance;
   }

   private PriorityColumnUI() {
      super(AtsColumnTokens.PriorityColumn);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PriorityColumnUI copy() {
      PriorityColumnUI newXCol = new PriorityColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   public static boolean promptPriority(final Collection<IAtsTeamWorkflow> teamWfs) {

      try {
         final PriorityDialog dialog = new PriorityDialog(teamWfs, null);
         if (teamWfs.size() == 1) {
            Priorities priority = PriorityColumn.getPriority(teamWfs.iterator().next(), AtsApiService.get());
            if (priority != null) {
               dialog.setSelected(priority);
            }
         }
         if (dialog.open() == Window.OK) {

            IAtsChangeSet changes = AtsApiService.get().createChangeSet("ATS Prompt Priority");

            Priorities newChangeType = dialog.getSelected();
            for (IAtsTeamWorkflow team : teamWfs) {
               PriorityColumn.setPriority(team, newChangeType, changes);
            }
            changes.executeIfNeeded();
         }
         return true;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't change Priority", ex);
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
            boolean modified = promptPriority(Arrays.asList((TeamWorkFlowArtifact) useArt));
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
               Priorities priority = PriorityColumn.getPriority(workItem, AtsApiService.get());
               if (priority != null) {
                  return getImage(priority);
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
      promptPriority(awas);
      ((XViewer) getXViewer()).update(awas.toArray(), null);
   }

   public static Image getImage(Priorities type) {
      return null;
   }

}
