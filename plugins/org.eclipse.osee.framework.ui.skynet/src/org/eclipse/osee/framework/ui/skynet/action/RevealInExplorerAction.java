package org.eclipse.osee.framework.ui.skynet.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ISelectedArtifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

public class RevealInExplorerAction extends Action {
   private final ISelectedArtifact selectedArtifact;

   public RevealInExplorerAction(final Artifact artifact) {
      this(new ISelectedArtifact() {

         @Override
         public Artifact getSelectedArtifact() {
            return artifact;
         }
      });
   }

   public RevealInExplorerAction(ISelectedArtifact selectedArtifact) {
      this.selectedArtifact = selectedArtifact;
      setText("Reveal in Artifact Explorer");
      setToolTipText("Reveal this artifact in the Artifact Explorer");
   }

   @Override
   public void run() {
      ArtifactExplorer.revealArtifact(selectedArtifact.getSelectedArtifact());
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.MAGNIFY);
   }

}
