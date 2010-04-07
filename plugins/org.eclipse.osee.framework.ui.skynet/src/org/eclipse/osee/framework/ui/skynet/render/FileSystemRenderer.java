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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.FileUiUtil;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Ryan D. Brooks
 */
public abstract class FileSystemRenderer extends DefaultArtifactRenderer {

   @Override
   public void open(List<Artifact> artifacts) throws OseeCoreException {
      internalOpen(artifacts, PresentationType.SPECIALIZED_EDIT);
   }

   @Override
   public void openMergeEdit(List<Artifact> artifacts) throws OseeCoreException {
      internalOpen(artifacts, PresentationType.MERGE_EDIT);
   }

   @Override
   public void preview(List<Artifact> artifacts) throws OseeCoreException {
      internalOpen(artifacts, PresentationType.PREVIEW);
   }

   private void internalOpen(List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      if (presentationType != PresentationType.SPECIALIZED_EDIT || ArtifactGuis.checkOtherEdit(artifacts)) {
         IFile file = getRenderedFile(artifacts, presentationType);
         if (file != null) {
            String dummyName = file.getName();
            if (!artifacts.isEmpty()) {
               Artifact firstArtifact = artifacts.iterator().next();
               try {
                  FileUiUtil.ensureFilenameLimit(file);
                  Program program = getAssociatedProgram(firstArtifact);
                  if (RenderingUtil.arePopupsAllowed()) {
                     program.execute(file.getLocation().toFile().getAbsolutePath());
                  }
               } catch (Exception ex) {
                  IWorkbench workbench = PlatformUI.getWorkbench();
                  IEditorDescriptor editorDescriptor = workbench.getEditorRegistry().getDefaultEditor(dummyName);
                  if (editorDescriptor != null) {
                     try {
                        IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
                        page.openEditor(new FileEditorInput(file), editorDescriptor.getId());
                     } catch (PartInitException ex1) {
                        throw new OseeArgumentException(
                              "No program associated with the extension " + file.getFileExtension() + " found on your local machine.");
                     }
                  }
               }
            }
         }
      }
   }

   public IFile getRenderedFileForOpen(List<Artifact> artifacts) throws OseeCoreException {
      return getRenderedFile(artifacts, PresentationType.SPECIALIZED_EDIT);
   }

   public IFile getRenderedFile(List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      IFile toReturn = null;
      if (!artifacts.isEmpty()) {
         Artifact firstArtifact = artifacts.iterator().next();
         IFolder baseFolder = RenderingUtil.getRenderFolder(firstArtifact.getBranch(), presentationType);
         toReturn = renderToFileSystem(baseFolder, artifacts, presentationType);
      }
      return toReturn;
   }

   public IFile renderForDiff(IProgressMonitor monitor, Branch branch) throws OseeCoreException {
      IFolder baseFolder = RenderingUtil.getRenderFolder(branch, PresentationType.DIFF);
      return renderToFileSystem(baseFolder, null, branch, PresentationType.DIFF);
   }

   public IFile renderForDiff(IProgressMonitor monitor, Artifact artifact) throws OseeCoreException {
      if (artifact == null) {
         throw new OseeArgumentException("Artifact can not be null.");
      }

      IFolder baseFolder = RenderingUtil.getRenderFolder(artifact.getBranch(), PresentationType.DIFF);
      return renderToFileSystem(baseFolder, artifact, artifact.getBranch(), PresentationType.DIFF);
   }

   public IFile renderForMerge(IProgressMonitor monitor, Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      if (artifact == null) {
         throw new IllegalArgumentException("Artifact can not be null.");
      }
      IFolder baseFolder;
      if (presentationType == PresentationType.MERGE_EDIT) {
         baseFolder = RenderingUtil.getRenderFolder(artifact.getBranch(), PresentationType.MERGE_EDIT);
      } else {
         baseFolder = RenderingUtil.getRenderFolder(artifact.getBranch(), PresentationType.DIFF);
      }
      return renderToFileSystem(baseFolder, artifact, artifact.getBranch(), presentationType);
   }

   public abstract IFile renderToFileSystem(IFolder baseFolder, Artifact artifact, Branch branch, PresentationType presentationType) throws OseeCoreException;

   public abstract IFile renderToFileSystem(IFolder baseFolder, List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException;

   public abstract Program getAssociatedProgram(Artifact artifact) throws OseeCoreException;

   public abstract String getAssociatedExtension(Artifact artifact) throws OseeCoreException;

}