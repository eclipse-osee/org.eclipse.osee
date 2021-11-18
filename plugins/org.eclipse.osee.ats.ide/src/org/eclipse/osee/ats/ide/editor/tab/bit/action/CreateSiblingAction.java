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
package org.eclipse.osee.ats.ide.editor.tab.bit.action;

import java.util.Collection;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.JaxTeamWorkflow;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactData;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactDatas;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.bit.WfeBitTab;
import org.eclipse.osee.ats.ide.editor.tab.bit.XBitViewer;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.dialog.ActionableItemTreeWithChildrenDialog;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class CreateSiblingAction extends Action {

   private final IAtsTeamWorkflow teamWf;
   private final AtsApi atsApi;
   private final WorkflowEditor editor;

   public CreateSiblingAction(IAtsTeamWorkflow teamWf, WorkflowEditor editor) {
      this.teamWf = teamWf;
      this.editor = editor;
      atsApi = AtsApiService.get();
   }

   @Override
   public void run() {
      WfeBitTab bitTab = editor.getBitTab();
      XBitViewer viewer = bitTab.getxViewer();
      TreeItem[] items = viewer.getTree().getSelection();
      if (items.length != 1) {
         AWorkbench.popup("Must Select a Single Build Impact");
         return;
      }
      TreeItem item = items[0];
      Object obj = item.getData();
      if (obj instanceof BuildImpactData) {
         BuildImpactData bid = (BuildImpactData) obj;
         ArtifactToken progArt = bid.getProgram();
         IAtsProgram program = atsApi.getProgramService().getProgramById(progArt);
         Collection<IAtsActionableItem> ais = atsApi.getProgramService().getAis(program);
         ActionableItemTreeWithChildrenDialog dialog = new ActionableItemTreeWithChildrenDialog(Active.Active, ais);
         dialog.setAddIncludeAllCheckbox(false);
         if (dialog.open() == Window.OK) {
            handleSelection(bid, dialog.getChecked());
         }
      } else {
         AWorkbench.popup("Must Select a Single Build Impact");
         return;
      }
   }

   private void handleSelection(BuildImpactData selBid, Collection<IAtsActionableItem> aias) {
      BuildImpactDatas bids = new BuildImpactDatas();
      bids.setTeamWf(teamWf.getStoreObject());
      for (IAtsActionableItem ai : aias) {
         BuildImpactData bid = new BuildImpactData();
         bid.setBids(bids);
         bid.setBidArt(selBid.getBidArt());
         bid.setBuild(selBid.getBuild());
         bid.setProgram(selBid.getProgram());
         bids.addBuildImpactData(bid);

         JaxTeamWorkflow teamWf = new JaxTeamWorkflow();
         teamWf.setName(teamWf.getTitle());
         teamWf.setNewAi(ai.getArtifactToken());
         bid.addTeamWorkflow(teamWf);
      }
      bids = atsApi.getServerEndpoints().getActionEndpoint().updateBids(teamWf.getAtsId(), bids);
      if (bids.getResults().isErrors()) {
         XResultDataUI.report(bids.getResults(), "Error Creating BID Workflows");
      } else {
         ((Artifact) teamWf).reloadAttributesAndRelations();
      }

   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.WORKFLOW);
   }

   @Override
   public String getText() {
      return "Create New Sibling Team Workflow(s)";
   }

   @Override
   public boolean isEnabled() {
      return teamWf.isInWork();
   }

}
