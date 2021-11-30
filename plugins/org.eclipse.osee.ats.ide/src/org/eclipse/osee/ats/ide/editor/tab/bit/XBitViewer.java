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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.JaxTeamWorkflow;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactData;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactDatas;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactState;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskXViewer;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.ListSelectionDialogNoSave;
import org.eclipse.osee.framework.ui.skynet.ArtifactDoubleClick;
import org.eclipse.osee.framework.ui.swt.Displays;
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

   public XBitViewer(Composite parent, int style, IXViewerFactory xViewerFactory, IDirtiableEditor editor, IAtsTeamWorkflow teamWf) {
      super(parent, style, xViewerFactory, editor, teamWf);
      atsApi = AtsApiService.get();
      crTeamWf = teamWf;
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
            return handleChangeState(bid);
         }
      }
      return false;
   }

   private boolean handleChangeState(BuildImpactData bid) {
      List<String> states = new ArrayList<String>();
      for (String state : Arrays.asList(BuildImpactState.Open.name(), BuildImpactState.Analyzed.name(),
         BuildImpactState.InWork.name(), BuildImpactState.Promoted.name(), BuildImpactState.Closed.name(),
         BuildImpactState.Deferred.name(), BuildImpactState.Cancelled.name())) {
         if (!bid.getState().equals(state)) {
            states.add(state);
         }
      }
      states.remove(bid.getState());

      ListSelectionDialogNoSave dialog =
         new ListSelectionDialogNoSave(Collections.castAll(states), Displays.getActiveShell().getShell(),
            "Select Transition-To State", null, "Select the state to transition to.\n\n" //
               + "Transition will happen upon selection and Transition button.\n\n" //
               + "Double-click will select, close and transition.",
            2, new String[] {"Transition", "Cancel"}, 0);

      if (dialog.open() == Window.OK) {
         Artifact bidArt = (Artifact) atsApi.getQueryService().getArtifact(bid.getBidArt());
         bidArt.setSoleAttributeValue(AtsAttributeTypes.BitState, dialog.getSelected());
         bidArt.persist("Update BID State");
         return true;
      }

      return false;
   }

   @Override
   protected boolean isAddTaskEnabled() {
      return false;
   }

}
