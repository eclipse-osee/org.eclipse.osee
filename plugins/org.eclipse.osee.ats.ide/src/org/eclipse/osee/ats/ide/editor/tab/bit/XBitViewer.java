/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.tab.bit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.JaxTeamWorkflow;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactData;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactDatas;
import org.eclipse.osee.ats.ide.editor.tab.bit.action.ChangeBitState;
import org.eclipse.osee.ats.ide.editor.tab.bit.action.RemoveBitWorkflowAction;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskXViewer;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactDoubleClick;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class XBitViewer extends TaskXViewer {

   protected final IAtsTeamWorkflow crTeamWf;
   protected final AtsApi atsApi;
   private BuildImpactDatas bids;
   private RemoveBitWorkflowAction removeBidWorkflow;
   private final WfeBitTab wfeBitTab;

   public XBitViewer(Composite parent, int style, IXViewerFactory xViewerFactory, IDirtiableEditor editor, IAtsTeamWorkflow teamWf, WfeBitTab wfeBitTab) {
      super(parent, style, xViewerFactory, editor, teamWf);
      this.wfeBitTab = wfeBitTab;
      atsApi = AtsApiService.get();
      crTeamWf = teamWf;
   }

   public List<BuildImpactData> getSelectedBuildImpactDatas() {
      List<BuildImpactData> bids = new ArrayList<>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            if (item.getData() instanceof BuildImpactData) {
               BuildImpactData bid = (BuildImpactData) item.getData();
               bids.add(bid);
            }
         }
      }
      return bids;
   }

   @Override
   public List<Artifact> getSelectedArtifacts() {
      List<Artifact> arts = new ArrayList<>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            if (item.getData() instanceof JaxTeamWorkflow) {
               JaxTeamWorkflow jTeamWf = (JaxTeamWorkflow) item.getData();
               Long id = jTeamWf.getId();
               Artifact artifact = AtsApiService.get().getQueryServiceIde().getArtifact(id);
               if (artifact != null) {
                  arts.add(artifact);
               }
            } else if (item.getData() instanceof BuildImpactData) {
               BuildImpactData bid = (BuildImpactData) item.getData();
               Artifact artifact = AtsApiService.get().getQueryServiceIde().getArtifact(bid.getBidArt());
               if (artifact != null) {
                  arts.add(artifact);
               }
            }
         }
      }
      return arts;
   }

   @Override
   public Set<Artifact> getSelectedWorkflowArtifacts() {
      Set<Artifact> smaArts = new HashSet<>();
      for (Artifact art : getSelectedArtifacts()) {
         if (art instanceof AbstractWorkflowArtifact) {
            smaArts.add(art);
         }
      }
      return smaArts;
   }

   public void loadTable() {
      try {
         if (getContentProvider() != null) {
            setInput(bids);
            setLoading(false);
            refresh();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public void handleDoubleClick() {
      List<Artifact> artifacts = getSelectedArtifacts();
      if (!artifacts.isEmpty()) {
         ArtifactDoubleClick.open(artifacts.iterator().next());
      }
   }

   public BuildImpactDatas getBids() {
      return bids;
   }

   public void setBids(BuildImpactDatas bids) {
      this.bids = bids;
   }

   @Override
   protected boolean showTaskMenu() {
      return false;
   }

   @Override
   public boolean isRemoveItemsMenuOptionEnabled() {
      return false;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      if (treeItem.getData() instanceof BuildImpactData) {
         BuildImpactData bid = (BuildImpactData) treeItem.getData();
         if (treeColumn.getText().equals(XBitXViewerFactory.State_Col.getName())) {
            (new ChangeBitState(crTeamWf, this, atsApi)).handleChangeState(bid);
         }
      }
      return false;
   }

   @Override
   protected boolean isAddTaskEnabled() {
      return false;
   }

   @Override
   public boolean isColumnMultiEditable(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      if (!(treeColumn.getData() instanceof XViewerColumn)) {
         return false;
      }
      if (((XViewerColumn) treeColumn.getData()).isMultiColumnEditable()) {
         return true;
      }
      return false;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      if (treeColumn.getText().equals(XBitXViewerFactory.State_Col.getName())) {
         (new ChangeBitState(crTeamWf, this, atsApi)).handleMultiEdit();
      }
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems, boolean persist) {
      super.handleColumnMultiEdit(treeColumn, treeItems);
   }

   @Override
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      if (treeColumn.getText().equals(XBitXViewerFactory.State_Col.getName())) {
         (new ChangeBitState(crTeamWf, this, atsApi)).handleMultiEdit();
      }
      return false;
   }

   @Override
   public void createMenuActions() {
      super.createMenuActions();

      if (AtsApiService.get().getUserService().isAtsAdmin()) {
         removeBidWorkflow = new RemoveBitWorkflowAction(this, this);
      }
   }

   @Override
   public void updateEditMenuActions() {
      MenuManager mm = getMenuManager();

      if (AtsApiService.get().getUserService().isAtsAdmin()) {
         mm.insertBefore(MENU_GROUP_ATS_WORLD_EDIT, removeBidWorkflow);
         boolean enabled = true;
         for (Artifact art : getSelectedArtifacts()) {
            if (!art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               enabled = false;
            }
         }
         removeBidWorkflow.setEnabled(enabled);
      }
   }

   public WfeBitTab getWfeBitTab() {
      return wfeBitTab;
   }

}
