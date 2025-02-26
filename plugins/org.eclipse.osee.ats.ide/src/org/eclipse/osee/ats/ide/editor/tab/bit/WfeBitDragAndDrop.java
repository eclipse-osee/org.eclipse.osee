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

package org.eclipse.osee.ats.ide.editor.tab.bit;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactData;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class WfeBitDragAndDrop extends SkynetDragAndDrop {

   private final AbstractWorkflowArtifact sma;
   private final WfeBitTab wfeBitTab;

   public WfeBitDragAndDrop(WfeBitTab wfeBitTab, Control control, AbstractWorkflowArtifact sma, String viewId) {
      super(control, viewId);
      this.wfeBitTab = wfeBitTab;
      this.sma = sma;
   }

   @Override
   public Artifact[] getArtifacts() {
      return new Artifact[] {sma};
   }

   @Override
   public void performDragOver(DropTargetEvent event) {
      event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL | DND.FEEDBACK_EXPAND;
      boolean validForArtifactDrop = isValidForArtifactDrop(event);
      if (validForArtifactDrop) {
         event.detail = DND.DROP_COPY;
      }
   }

   private BuildImpactData getSelectedBid(DropTargetEvent event) {
      if (event.item != null && event.item.getData() instanceof BuildImpactData) {
         return (BuildImpactData) event.item.getData();
      }
      return null;
   }

   private boolean isValidForArtifactDrop(DropTargetEvent event) {
      if (ArtifactTransfer.getInstance().isSupportedType(event.currentDataType)) {

         BuildImpactData dropBid = getSelectedBid(event);
         if (dropBid != null) {
            ArtifactData toBeDropped = ArtifactTransfer.getInstance().nativeToJava(event.currentDataType);
            try {
               Artifact[] artifactsBeingDropped = toBeDropped.getArtifacts();
               for (Artifact art : artifactsBeingDropped) {
                  if (!wfeBitTab.isValidBidWorkflow(art)) {
                     return false;
                  }
               }
               return true;
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }
      return false;
   }

   @Override
   public void performDrop(final DropTargetEvent event) {

      if (ArtifactTransfer.getInstance().isSupportedType(event.currentDataType)) {
         BuildImpactData dropBid = getSelectedBid(event);
         if (dropBid != null) {

            ArtifactData artData = ArtifactTransfer.getInstance().nativeToJava(event.currentDataType);
            List<Artifact> artifactsToBeRelated = new LinkedList<>();
            for (Artifact artifact : artData.getArtifacts()) {
               if (wfeBitTab.isValidBidWorkflow(artifact)) {
                  artifactsToBeRelated.add(artifact);
               } else {
                  AWorkbench.popup("Invalid Workflow",
                     String.format("Invalid workflow type to add to BID\n\nBID: %s\n\nWorkflow: %s",
                        dropBid.getBidArt().getName(), artifact.toStringWithId()));
                  return;
               }
            }
            Jobs.startJob(new RelateWorkflowsToBit(wfeBitTab, dropBid, artifactsToBeRelated));
         }
      }
   }

   public static class RelateWorkflowsToBit extends Job {

      private final List<Artifact> artifactsToBeRelated;
      private final BuildImpactData bid;
      private final WfeBitTab wfeBitTab;

      public RelateWorkflowsToBit(WfeBitTab wfeBitTab, BuildImpactData bid, List<Artifact> artifactsToBeRelated) {
         super("Relate Workflows to BIT");
         this.wfeBitTab = wfeBitTab;
         this.bid = bid;
         this.artifactsToBeRelated = artifactsToBeRelated;
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         AtsApi atsApi = AtsApiService.get();
         IAtsChangeSet changes = atsApi.createChangeSet(getName());
         for (Artifact art : artifactsToBeRelated) {
            changes.relate(bid.getBidArt(), AtsRelationTypes.BuildImpactDataToTeamWf_TeamWf, art);
         }
         TransactionToken tx = changes.executeIfNeeded();
         if (tx.isValid()) {
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  wfeBitTab.refresh();
                  wfeBitTab.getxViewer().refresh(bid);
                  wfeBitTab.getxViewer().expandToLevel(bid, 2);
               }
            });
         }
         return Status.OK_STATUS;
      }

   }

}
