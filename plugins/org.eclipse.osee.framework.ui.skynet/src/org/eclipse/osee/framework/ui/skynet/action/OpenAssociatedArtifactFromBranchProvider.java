package org.eclipse.osee.framework.ui.skynet.action;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.IBranchProvider;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.change.operations.LoadAssociatedArtifactOperationFromBranch;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;

public class OpenAssociatedArtifactFromBranchProvider extends Action {

   private final IBranchProvider branchProvider;

   public OpenAssociatedArtifactFromBranchProvider(IBranchProvider branchProvider) {
      super("Open Associated Artifact", Action.AS_PUSH_BUTTON);
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
            try {
               Artifact assocArt = BranchManager.getAssociatedArtifact(branchProvider.getBranch(null));
               if (assocArt == null) {
                  AWorkbench.popup("ERROR", "Cannot access associated artifact.");
               } else if (assocArt instanceof User) {
                  AWorkbench.popup(String.format("Associated with user [%s]", assocArt.getName()));
               } else {
                  RendererManager.openInJob(BranchManager.getAssociatedArtifact(branchProvider.getBranch(null)),
                        PresentationType.GENERALIZED_EDIT);
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, "Cannot access associated artifact.", ex);
            }
         }
      });
   }

   public void updateEnablement() {
      Artifact associatedArtifact = null;
      boolean isEnabled;
      try {
         associatedArtifact = BranchManager.getAssociatedArtifact(branchProvider.getBranch(null));
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE, ex);
      }
      ImageDescriptor descriptor;
      if (associatedArtifact != null && !(associatedArtifact instanceof User)) {
         descriptor = ArtifactImageManager.getImageDescriptor(associatedArtifact);
         isEnabled = true;
      } else {
         descriptor = ImageManager.getImageDescriptor(FrameworkImage.EDIT);
         isEnabled = false;
      }
      setImageDescriptor(descriptor);
      setEnabled(isEnabled);
   }
}