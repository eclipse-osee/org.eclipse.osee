/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.render.compare;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
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
      Pair<Artifact, Artifact> renderInput = RenderingUtil.asRenderInput(delta);
      Branch branch = delta.getBranch();
      IFolder renderingFolder = RenderingUtil.getRenderFolder(branch, presentationType);
      IFile baseFile = renderer.renderToFileSystem(renderingFolder, renderInput.getFirst(), branch, presentationType);
      IFile newerFile = renderer.renderToFileSystem(renderingFolder, renderInput.getSecond(), branch, presentationType);
      return new Pair<IFile, IFile>(baseFile, newerFile);
   }
}
