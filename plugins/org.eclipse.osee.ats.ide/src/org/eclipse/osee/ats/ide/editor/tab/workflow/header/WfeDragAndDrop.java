/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.access.AccessControlArtifactUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.XResultDataDialog;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class WfeDragAndDrop extends SkynetDragAndDrop {

   private final AbstractWorkflowArtifact sma;

   public WfeDragAndDrop(Control control, AbstractWorkflowArtifact sma, String viewId) {
      super(control, viewId);
      this.sma = sma;
   }

   @Override
   public Artifact[] getArtifacts() {
      return new Artifact[] {sma};
   }

   @Override
   public void performDragOver(DropTargetEvent event) {
      event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL | DND.FEEDBACK_EXPAND;

      if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
         event.detail = DND.DROP_COPY;
      } else if (isValidForArtifactDrop(event)) {
         event.detail = DND.DROP_COPY;
      }
   }

   private boolean isValidForArtifactDrop(DropTargetEvent event) {
      boolean valid = false;
      if (ArtifactTransfer.getInstance().isSupportedType(event.currentDataType)) {

         Artifact dropTarget = sma;
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

               XResultData rd = AtsApiService.get().getAccessControlService().hasRelationTypePermission(dropTarget,
                  CoreRelationTypes.SupportingInfo_SupportingInfo, artsOnSameBranchAsDestination, PermissionEnum.WRITE,
                  AccessControlArtifactUtil.getXResultAccessHeader("Relating Artifacts to Workflow",
                     artsOnSameBranchAsDestination));
               if (rd.isErrors()) {
                  XResultDataDialog.open(rd, "Relate Artifact(s) to Workflow",
                     "Invalid Access for Relation.\n\nAborting Drop.");
                  valid = false;
               } else {
                  valid = true;
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }
      return valid;
   }

   @Override
   public void performDrop(final DropTargetEvent event) {

      if (ArtifactTransfer.getInstance().isSupportedType(event.currentDataType)) {
         ArtifactData artData = ArtifactTransfer.getInstance().nativeToJava(event.currentDataType);
         List<Artifact> artifactsToBeRelated = new LinkedList<>();
         for (Artifact artifact : artData.getArtifacts()) {
            artifactsToBeRelated.add(artifact);
         }
         Jobs.startJob(new WfeEditorAddSupportingArtifacts(sma, artifactsToBeRelated), false);
      } else if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
         try {
            Object object = FileTransfer.getInstance().nativeToJava(event.currentDataType);
            if (object instanceof String[]) {
               String[] items = (String[]) object;
               List<File> files = new LinkedList<>();
               for (String item : items) {
                  files.add(new File(item));
               }
               Jobs.startJob(new WfeEditorAddSupportingFiles(sma, files, null), false);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

}
