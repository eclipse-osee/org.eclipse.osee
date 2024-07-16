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

package org.eclipse.osee.framework.ui.skynet.action;

import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.IBranchProvider;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.change.operations.LoadAssociatedArtifactOperationFromBranch;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;

public class OpenAssociatedArtifactFromBranchProvider extends Action {

   private final IBranchProvider branchProvider;

   public OpenAssociatedArtifactFromBranchProvider(IBranchProvider branchProvider) {
      super("Open Associated Artifact", IAction.AS_PUSH_BUTTON);
      this.branchProvider = branchProvider;
      setId("open.associated.artifact");
      setToolTipText("Open Associated Artifact");
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.EDIT));
   }

   @Override
   public void run() {
      IOperation operation = new LoadAssociatedArtifactOperationFromBranch(branchProvider);
      Operations.executeAsJob(operation, false, Job.SHORT, new JobChangeAdapter() {

         @Override
         public void done(IJobChangeEvent event) {

            if (branchProvider.getBranch().isValid()) {

               Artifact assocArt = BranchManager.getAssociatedArtifact(branchProvider.getBranch());
               if (assocArt.isInvalid()) {
                  AWorkbench.popup("This branch does not have an associated artifact.");
               }
               RendererManager.openInJob(assocArt, PresentationType.SPECIALIZED_EDIT);

            } else {
               AWorkbench.popup("", "Please select a branch from the Select Branch button");
            }
         }
      });
   }

   public void updateEnablement() {
      boolean isEnabled = false;
      ImageDescriptor descriptor = null;
      try {
         Artifact associatedArtifact = BranchManager.getAssociatedArtifact(branchProvider.getBranch());
         if (associatedArtifact.isValid()) {
            descriptor = ArtifactImageManager.getImageDescriptor(associatedArtifact);
            isEnabled = true;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      if (!isEnabled) {
         descriptor = ImageManager.getImageDescriptor(FrameworkImage.EDIT);
      }
      setImageDescriptor(descriptor);
      setEnabled(isEnabled);

   }
}
