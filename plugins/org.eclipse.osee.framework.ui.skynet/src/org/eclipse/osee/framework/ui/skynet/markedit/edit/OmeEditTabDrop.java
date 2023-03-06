/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.framework.ui.skynet.markedit.edit;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.markedit.model.ArtOmeData;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;

/**
 * @author Donald G. Dunne
 */
public class OmeEditTabDrop {

   private final OmeEditTab omeEditTab;
   private final ArtOmeData omeData;

   public OmeEditTabDrop(OmeEditTab omeEditTab, ArtOmeData omeData) {
      this.omeEditTab = omeEditTab;
      this.omeData = omeData;
   }

   void setDropTarget() {
      new SkynetDragAndDrop(null, omeEditTab.getEditComposite(), omeEditTab.getId()) {

         @Override
         public Artifact[] getArtifacts() throws Exception {
            return null;
         }

         @Override
         public void performDragOver(DropTargetEvent event) {
            event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL | DND.FEEDBACK_EXPAND;
            if (isValidForArtifactDrop(event)) {
               event.detail = DND.DROP_COPY;
            }
         }

         @Override
         public void performArtifactDrop(Artifact[] dropArtifacts) {
            Artifact dropArt = dropArtifacts[0];
            String oseeLink = String.format("<oseelink>[%s]-[%s]</oseelink>", //
               dropArt.getIdString(), dropArt.getName());
            omeEditTab.appendText("\n" + oseeLink);

            // TBD - Shouldn't need loopback, but might for external editor
            //            String loopbackUrl =
            //               // "http://localhost:18001/osee/loopback?branchUuid=3&cmd=open.artifact&guid=AGroS%2B2_9HuWXpkinDQA&id=200154&isDeleted=false";
            //               String.format("http://localhost:18001/osee/loopback?branchUuid=%s" //
            //                  + "&cmd=open.artifact&guid=%s&id=%s&isDeleted=false", //
            //                  dropArt.getBranchIdString(), dropArt.getGuid(), dropArt.getIdString());
            //            omeEditTab.appendText("\n\n[" + dropArt.getName() + "](" + loopbackUrl + ")");
         }
      };

   }

   private boolean isValidForArtifactDrop(DropTargetEvent event) {
      boolean valid = false;
      if (ArtifactTransfer.getInstance().isSupportedType(event.currentDataType)) {

         Artifact dropTarget = omeData.getArtifact();
         ArtifactData toBeDropped = ArtifactTransfer.getInstance().nativeToJava(event.currentDataType);
         if (dropTarget != null) {
            try {
               Artifact[] artifactsBeingDropped = toBeDropped.getArtifacts();
               List<Artifact> artsOnSameBranchAsDestination = new LinkedList<>();
               BranchId destinationBranch = dropTarget.getBranch();
               boolean onSameBranch = true;
               for (Artifact art : artifactsBeingDropped) {
                  if (art.isOnBranch(destinationBranch)) {
                     artsOnSameBranchAsDestination.add(art);
                  } else {
                     onSameBranch = false;
                     break;
                  }
               }
               if (!onSameBranch) {
                  AWorkbench.popup("Related Artifact(s) must be on the same branch.\n\nAborting Drop.");
                  return false;
               }
               if (artsOnSameBranchAsDestination.isEmpty()) {
                  AWorkbench.popup("No Artifact(s) to relate.\n\nAborting Drop.");
                  return false;
               }

               // TBD - Add back in to ensure proper access control
               //               XResultData rd = AtsApiService.get().getAccessControlService().hasRelationTypePermission(dropTarget,
               //                  CoreRelationTypes.SupportingInfo_SupportingInfo, artsOnSameBranchAsDestination, PermissionEnum.WRITE,
               //                  AccessControlArtifactUtil.getXResultAccessHeader("Relating Artifacts to Workflow",
               //                     artsOnSameBranchAsDestination));
               //               if (rd.isErrors()) {
               //                  XResultDataDialog.open(rd, "Relate Artifact(s) to Workflow",
               //                     "Invalid Access for Relation.\n\nAborting Drop.");
               //                  valid = false;
               //               } else {
               //                  valid = true;
               //               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }
      return valid;
   }

}
