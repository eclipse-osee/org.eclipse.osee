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
public abstract class FileSystemRenderer extends Renderer {
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
    * @see org.eclipse.osee.framework.ui.skynet.render.Renderer#preview(org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void preview(Artifact artifact, IProgressMonitor monitor) throws OseeCoreException {
      open(monitor, artifact, PresentationType.PREVIEW);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.Renderer#edit(org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void edit(Artifact artifact, IProgressMonitor monitor) throws OseeCoreException {
      open(monitor, artifact, PresentationType.EDIT);
   }

   private void open(IProgressMonitor monitor, Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      IFolder baseFolder = getRenderFolder(artifact.getBranch(), presentationType);
      IFile file = renderToFileSystem(monitor, baseFolder, artifact, artifact.getBranch(), presentationType);
      getAssociatedProgram(artifact).execute(file.getLocation().toFile().getAbsolutePath());
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
   public void preview(List<Artifact> artifacts, IProgressMonitor monitor) throws OseeCoreException {
      open(monitor, artifacts, PresentationType.PREVIEW);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.Renderer#edit(java.util.List, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void edit(List<Artifact> artifacts, IProgressMonitor monitor) throws OseeCoreException {
      open(monitor, artifacts, PresentationType.EDIT);
   }

   private void open(IProgressMonitor monitor, List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      if (!artifacts.isEmpty()) {
         Artifact firstArtifact = artifacts.iterator().next();
         IFolder baseFolder = getRenderFolder(firstArtifact.getBranch(), presentationType);
         IFile file = renderToFileSystem(monitor, baseFolder, artifacts, presentationType);
         getAssociatedProgram(firstArtifact).execute(file.getLocation().toFile().getAbsolutePath());
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.Renderer#supportsEdit()
    */
   @Override
   public boolean supportsEdit() {
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.Renderer#supportsPreview()
    */
   @Override
   public boolean supportsPreview() {
      return true;
   }

   public static IFolder ensureRenderFolderExists(PresentationType presentationType) throws OseeCoreException {
      switch (presentationType) {
         case DIFF:
            if (diffFolder == null || !diffFolder.exists()) {
               diffFolder = OseeData.getFolder(".diff");
            }
            return diffFolder;

         case EDIT:
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
      return renderToFileSystem(monitor, baseFolder, null, branch, PresentationType.DIFF);
   }

   public IFile renderForDiff(IProgressMonitor monitor, Artifact artifact) throws OseeCoreException {
      if (artifact == null) {
         throw new OseeArgumentException("Artifact can not be null.");
      }

      IFolder baseFolder = getRenderFolder(artifact.getBranch(), PresentationType.DIFF);
      return renderToFileSystem(monitor, baseFolder, artifact, artifact.getBranch(), PresentationType.DIFF);
   }

   public IFile renderForMerge(IProgressMonitor monitor, Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      if (artifact == null) {
         throw new IllegalArgumentException("Artifact can not be null.");
      }
      IFolder baseFolder;
      if (presentationType == PresentationType.MERGE_EDIT) {
         baseFolder = getRenderFolder(artifact.getBranch(), PresentationType.EDIT);
      } else {
         baseFolder = getRenderFolder(artifact.getBranch(), PresentationType.DIFF);
      }
      return renderToFileSystem(monitor, baseFolder, artifact, artifact.getBranch(), presentationType);
   }

   public abstract IFile renderToFileSystem(IProgressMonitor monitor, IFolder baseFolder, Artifact artifact, Branch branch, PresentationType presentationType) throws OseeCoreException;

   public abstract IFile renderToFileSystem(IProgressMonitor monitor, IFolder baseFolder, List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException;

   public abstract Program getAssociatedProgram(Artifact artifact) throws OseeCoreException;

}