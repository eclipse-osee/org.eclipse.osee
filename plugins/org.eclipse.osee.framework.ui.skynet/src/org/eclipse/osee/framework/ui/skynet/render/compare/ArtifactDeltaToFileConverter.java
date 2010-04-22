/*
 * Created on Apr 22, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.render.compare;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;

public class ArtifactDeltaToFileConverter {
   private final FileSystemRenderer renderer;

   public ArtifactDeltaToFileConverter(FileSystemRenderer renderer) {
      this.renderer = renderer;
   }

   public FileSystemRenderer getRenderer() {
      return renderer;
   }

   public Pair<IFile, IFile> convertToFile(PresentationType presentationType, ArtifactDelta delta) throws OseeCoreException {
      Pair<IFile, IFile> toReturn;
      if (presentationType == PresentationType.MERGE || presentationType == PresentationType.MERGE_EDIT) {
         Branch branch = delta.getBranch();
         IFile baseFile = renderForMerge(renderer, delta.getStartArtifact(), branch, presentationType);
         IFile newerFile = renderForMerge(renderer, delta.getEndArtifact(), branch, presentationType);
         toReturn = new Pair<IFile, IFile>(baseFile, newerFile);
      } else {
         toReturn = asFiles(renderer, presentationType, delta);
      }
      return toReturn;
   }

   private IFile renderForMerge(FileSystemRenderer renderer, Artifact artifact, Branch branch, PresentationType presentationType) throws OseeCoreException {
      if (artifact == null) {
         throw new IllegalArgumentException("Artifact can not be null.");
      }
      IFolder baseFolder;
      if (presentationType == PresentationType.MERGE_EDIT) {
         baseFolder = RenderingUtil.getRenderFolder(artifact.getBranch(), PresentationType.MERGE_EDIT);
      } else {
         baseFolder = RenderingUtil.getRenderFolder(artifact.getBranch(), PresentationType.DIFF);
      }
      return renderer.renderToFileSystem(baseFolder, artifact, artifact.getBranch(), presentationType);
   }

   private Pair<IFile, IFile> asFiles(FileSystemRenderer renderer, PresentationType presentationType, ArtifactDelta delta) throws OseeCoreException {
      Artifact artFile1;
      Artifact artFile2;

      ModificationType startModType = delta.getStartArtifact().getModType();
      if (startModType.isDeleted()) {
         artFile1 = delta.getStartArtifact();
         artFile2 = null;
      } else if ((startModType == ModificationType.INTRODUCED || startModType == ModificationType.NEW) && delta.getEndArtifact() == null) {
         artFile1 = null;
         artFile2 = delta.getStartArtifact();
      } else {
         artFile1 = delta.getStartArtifact();
         artFile2 = delta.getEndArtifact();
      }
      Branch branch = delta.getBranch();
      IFolder renderingFolder = RenderingUtil.getRenderFolder(branch, presentationType);

      IFile baseFile = renderer.renderToFileSystem(renderingFolder, artFile1, branch, presentationType);
      IFile newerFile = renderer.renderToFileSystem(renderingFolder, artFile2, branch, presentationType);
      return new Pair<IFile, IFile>(baseFile, newerFile);
   }
}
