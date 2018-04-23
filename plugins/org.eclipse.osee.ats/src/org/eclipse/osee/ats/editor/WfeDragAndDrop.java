/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.editor;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.AccessPolicy;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
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
               AccessPolicy policy = ServiceUtil.getAccessPolicy();
               Artifact[] artifactsBeingDropped = toBeDropped.getArtifacts();
               List<Artifact> artsOnSameBranchAsDestination = new LinkedList<>();
               BranchId destinationBranch = dropTarget.getBranch();
               for (Artifact art : artifactsBeingDropped) {
                  if (art.isOnBranch(destinationBranch)) {
                     artsOnSameBranchAsDestination.add(art);
                  }
               }
               valid = policy.canRelationBeModified(dropTarget, artsOnSameBranchAsDestination,
                  CoreRelationTypes.SupportingInfo_SupportingInfo, Level.FINE).matched();

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
               Jobs.startJob(new WfeEditorAddSupportingFiles(sma, files), false);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

}
