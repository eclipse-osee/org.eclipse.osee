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
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeArtifactDialog;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class GroupsColumnUI extends XViewerAtsColumn implements IXViewerValueColumn {

   public static GroupsColumnUI instance = new GroupsColumnUI();

   public static GroupsColumnUI getInstance() {
      return instance;
   }

   private GroupsColumnUI() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".groups", "Groups", 100, XViewerAlign.Left, false,
         SortDataType.String, true, "Groups");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public GroupsColumnUI copy() {
      GroupsColumnUI newXCol = new GroupsColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         if (treeItem.getData() instanceof Artifact) {
            Artifact useArt = AtsApiService.get().getQueryServiceIde().getArtifact(treeItem);
            if (!useArt.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               return false;
            }
            boolean modified = promptChangeGroups(Arrays.asList((TeamWorkFlowArtifact) useArt));
            XViewer xViewer = (XViewer) ((XViewerColumn) treeColumn.getData()).getXViewer();
            if (modified) {
               useArt.persist("persist groups via alt-left-click");
               xViewer.update(useArt, null);
               return true;
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return false;
   }

   public static boolean promptChangeGroups(final Collection<? extends AbstractWorkflowArtifact> awas) {
      Set<Artifact> selected = new HashSet<>();
      for (AbstractWorkflowArtifact awa : awas) {
         selected.addAll(awa.getRelatedArtifacts(CoreRelationTypes.UniversalGrouping_Group));
      }
      Collection<Artifact> allGroups = UniversalGroup.getGroupsNotRoot(AtsApiService.get().getAtsBranch());
      FilteredCheckboxTreeArtifactDialog dialog =
         new FilteredCheckboxTreeArtifactDialog("Select Groups", "Select Groups", allGroups);
      dialog.setInitialSelections(selected);
      if (dialog.open() == Window.OK) {
         for (AbstractWorkflowArtifact awa : awas) {
            Collection<Artifact> checked = dialog.getChecked();
            awa.setRelations(CoreRelationTypes.UniversalGrouping_Group, checked);
         }
         TransactionManager.persistInTransaction("Set Groups", awas);
         return true;
      }
      return false;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (Artifacts.isOfType(element, AtsArtifactTypes.Action)) {
            Set<Artifact> groups = new HashSet<>();
            Artifact actionArt = AtsApiService.get().getQueryServiceIde().getArtifact(element);
            groups.addAll(actionArt.getRelatedArtifacts(CoreRelationTypes.UniversalGrouping_Group));
            // Roll up if same for all children
            for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(actionArt)) {
               groups.addAll(Collections.castAll(AtsApiService.get().getRelationResolver().getRelatedArtifacts(team,
                  CoreRelationTypes.UniversalGrouping_Group)));
            }
            return Collections.toString("; ", groups);
         }
         if (element instanceof Artifact) {
            return Collections.toString("; ",
               AtsApiService.get().getQueryServiceIde().getArtifact(element).getRelatedArtifacts(
                  CoreRelationTypes.UniversalGrouping_Group));
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         Set<AbstractWorkflowArtifact> awas = new HashSet<>();
         for (TreeItem item : treeItems) {
            if (item.getData() instanceof Artifact) {
               Artifact art = AtsApiService.get().getQueryServiceIde().getArtifact(item);
               if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
                  awas.add((AbstractWorkflowArtifact) art);
               }
            }
         }
         promptChangeGroups(awas);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
