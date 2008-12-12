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
package org.eclipse.osee.framework.ui.skynet.render;

import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 */
public abstract class FileSystemRenderer extends DefaultArtifactRenderer {
   /**
    * @param rendererId
    */
   public FileSystemRenderer(String rendererId) {
      super(rendererId);
   }

   private static IFolder workingFolder;
   private static IFolder diffFolder;
   private static IFolder previewFolder;

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.Renderer#open(java.util.List)
    */
   @Override
   public void open(List<Artifact> artifacts) throws OseeCoreException {
      internalOpen(artifacts, PresentationType.SPECIALIZED_EDIT);
   }

   public IFolder getRenderFolder(Branch branch, PresentationType presentationType) throws OseeCoreException {
      try {
         IFolder baseFolder = ensureRenderFolderExists(presentationType);
         IFolder renderFolder = baseFolder.getFolder(branch.asFolderName());
         if (!renderFolder.exists()) {
            renderFolder.create(true, true, null);
         }
         return renderFolder;
      } catch (CoreException ex) {
         throw new OseeCoreException(ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.Renderer#preview(java.util.List, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void preview(List<Artifact> artifacts) throws OseeCoreException {
      internalOpen(artifacts, PresentationType.PREVIEW);
   }

   private void internalOpen(List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      if (!artifacts.isEmpty()) {
         Artifact firstArtifact = artifacts.iterator().next();
         IFolder baseFolder = getRenderFolder(firstArtifact.getBranch(), presentationType);
         IFile file = renderToFileSystem(baseFolder, artifacts, presentationType);
         getAssociatedProgram(firstArtifact).execute(file.getLocation().toFile().getAbsolutePath());
      }
   }

   public static IFolder ensureRenderFolderExists(PresentationType presentationType) throws OseeCoreException {
      switch (presentationType) {
         case DIFF:
            if (diffFolder == null || !diffFolder.exists()) {
               diffFolder = OseeData.getFolder(".diff");
            }
            return diffFolder;

         case SPECIALIZED_EDIT:
            if (workingFolder == null || !workingFolder.exists()) {
               workingFolder = OseeData.getFolder(".working");
            }
            return workingFolder;

         case PREVIEW:
            if (previewFolder == null || !previewFolder.exists()) {
               previewFolder = OseeData.getFolder(".preview");
            }
            return previewFolder;

         default:
            throw new OseeArgumentException("Unexpected presentation type");
      }
   }

   public IFile renderForDiff(IProgressMonitor monitor, Branch branch) throws OseeCoreException {
      IFolder baseFolder = getRenderFolder(branch, PresentationType.DIFF);
      return renderToFileSystem(baseFolder, null, branch, PresentationType.DIFF);
   }

   public IFile renderForDiff(IProgressMonitor monitor, Artifact artifact) throws OseeCoreException {
      if (artifact == null) {
         throw new OseeArgumentException("Artifact can not be null.");
      }

      IFolder baseFolder = getRenderFolder(artifact.getBranch(), PresentationType.DIFF);
      return renderToFileSystem(baseFolder, artifact, artifact.getBranch(), PresentationType.DIFF);
   }

   public IFile renderForMerge(IProgressMonitor monitor, Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      if (artifact == null) {
         throw new IllegalArgumentException("Artifact can not be null.");
      }
      IFolder baseFolder;
      if (presentationType == PresentationType.MERGE_EDIT) {
         baseFolder = getRenderFolder(artifact.getBranch(), PresentationType.GENERALIZED_EDIT);
      } else {
         baseFolder = getRenderFolder(artifact.getBranch(), PresentationType.DIFF);
      }
      return renderToFileSystem(baseFolder, artifact, artifact.getBranch(), presentationType);
   }

   public abstract IFile renderToFileSystem(IFolder baseFolder, Artifact artifact, Branch branch, PresentationType presentationType) throws OseeCoreException;

   public abstract IFile renderToFileSystem(IFolder baseFolder, List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException;

   public abstract Program getAssociatedProgram(Artifact artifact) throws OseeCoreException;

}