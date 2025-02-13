/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.bit.action;

import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactData;
import org.eclipse.osee.ats.ide.actions.AbstractAtsAction;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.editor.tab.bit.WfeBitTab;
import org.eclipse.osee.ats.ide.editor.tab.bit.XBitViewer;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class RemoveBidWorkflowAction extends AbstractAtsAction {

   private final XBitViewer bitXViewer;

   public RemoveBidWorkflowAction(ISelectedAtsArtifacts selectedAtsArtifacts, XBitViewer bitXViewer) {
      super("Remove BID Workflow (admin)", IAction.AS_PUSH_BUTTON);
      this.bitXViewer = bitXViewer;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.DELETE));
   }

   @Override
   public void runWithException() {
      Jobs.startJob(
         new UnRelateWorkflowsToBit(bitXViewer, bitXViewer.getWfeBitTab(), bitXViewer.getSelectedArtifacts()));
   }

   public static class UnRelateWorkflowsToBit extends Job {

      private final XBitViewer bitXViewer;
      private final List<Artifact> selectedAtsArtifacts;
      private final WfeBitTab wfeBitTab;

      public UnRelateWorkflowsToBit(XBitViewer bitXViewer, WfeBitTab wfeBitTab, List<Artifact> selectedAtsArtifacts) {
         super("UnRelate Workflows to BIT");
         this.bitXViewer = bitXViewer;
         this.wfeBitTab = wfeBitTab;
         this.selectedAtsArtifacts = selectedAtsArtifacts;
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         AtsApi atsApi = AtsApiService.get();
         BuildImpactData bid = null;
         IAtsChangeSet changes = atsApi.createChangeSet(getName());
         for (Artifact art : selectedAtsArtifacts) {
            if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               Collection<ArtifactToken> bidArts =
                  atsApi.getRelationResolver().getRelated(art, AtsRelationTypes.BuildImpactDataToTeamWf_Bid);
               if (bidArts.size() == 1) {
                  for (BuildImpactData tableBid : bitXViewer.getBids().getBuildImpacts()) {
                     if (tableBid.getBidArt().getId().equals(bidArts.iterator().next().getId())) {
                        bid = tableBid;
                        break;
                     }
                  }
                  changes.unrelate(bidArts.iterator().next(), AtsRelationTypes.BuildImpactDataToTeamWf_TeamWf, art);
               } else {
                  AWorkbench.popup("Workflow related to multiple BIDs, remove through Relations Tab");
               }
            }
         }
         TransactionToken tx = changes.executeIfNeeded();
         if (tx.isValid()) {
            if (bid != null) {
               BuildImpactData fBid = bid;
               WfeBitTab fWfeBitTab = wfeBitTab;
               Displays.ensureInDisplayThread(new Runnable() {

                  @Override
                  public void run() {
                     fWfeBitTab.refresh();
                     fWfeBitTab.getxViewer().refresh(fBid);
                     fWfeBitTab.getxViewer().expandToLevel(fBid, 2);
                  }
               });
            }
         }
         return Status.OK_STATUS;
      }

   }

}
