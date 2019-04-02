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
package org.eclipse.osee.ats.ide.column;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
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
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeArtifactDialog;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class GroupsColumn extends XViewerAtsColumn implements IXViewerValueColumn, IAltLeftClickProvider, IMultiColumnEditProvider {

   public static GroupsColumn instance = new GroupsColumn();

   public static GroupsColumn getInstance() {
      return instance;
   }

   private GroupsColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".groups", "Groups", 100, XViewerAlign.Left, false,
         SortDataType.String, true, "Groups");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public GroupsColumn copy() {
      GroupsColumn newXCol = new GroupsColumn();
      super.copy(this, newXCol);
      return newXCol;
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
            boolean modified = promptChangeGroups(Arrays.asList((TeamWorkFlowArtifact) useArt), isPersistViewer());
            XViewer xViewer = (XViewer) ((XViewerColumn) treeColumn.getData()).getXViewer();
            if (modified && isPersistViewer(xViewer)) {
               useArt.persist("persist groups via alt-left-click");
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

   public static boolean promptChangeGroups(final Collection<? extends AbstractWorkflowArtifact> awas, boolean persist) {
      Set<Artifact> selected = new HashSet<>();
      for (AbstractWorkflowArtifact awa : awas) {
         selected.addAll(awa.getRelatedArtifacts(CoreRelationTypes.Universal_Grouping__Group));
      }
      Collection<Artifact> allGroups = UniversalGroup.getGroupsNotRoot(AtsClientService.get().getAtsBranch());
      FilteredCheckboxTreeArtifactDialog dialog =
         new FilteredCheckboxTreeArtifactDialog("Select Groups", "Select Groups", allGroups);
      dialog.setInitialSelections(selected);
      if (dialog.open() == 0) {
         for (AbstractWorkflowArtifact awa : awas) {
            Collection<Artifact> checked = dialog.getChecked();
            awa.setRelations(CoreRelationTypes.Universal_Grouping__Group, checked);
         }
         Artifacts.persistInTransaction("Set Groups", awas);
         return true;
      }
      return false;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (Artifacts.isOfType(element, AtsArtifactTypes.Action)) {
            Set<Artifact> groups = new HashSet<>();
            Artifact actionArt = AtsClientService.get().getQueryServiceClient().getArtifact(element);
            groups.addAll(actionArt.getRelatedArtifacts(CoreRelationTypes.Universal_Grouping__Group));
            // Roll up if same for all children
            for (IAtsTeamWorkflow team : AtsClientService.get().getWorkItemService().getTeams(actionArt)) {
               groups.addAll(Collections.castAll(AtsClientService.get().getRelationResolver().getRelatedArtifacts(team,
                  CoreRelationTypes.Universal_Grouping__Group)));
            }
            return Collections.toString("; ", groups);
         }
         if (element instanceof Artifact) {
            return Collections.toString("; ",
               AtsClientService.get().getQueryServiceClient().getArtifact(element).getRelatedArtifacts(
                  CoreRelationTypes.Universal_Grouping__Group));
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
               Artifact art = AtsClientService.get().getQueryServiceClient().getArtifact(item);
               if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
                  awas.add((AbstractWorkflowArtifact) art);
               }
            }
         }
         promptChangeGroups(awas, true);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
