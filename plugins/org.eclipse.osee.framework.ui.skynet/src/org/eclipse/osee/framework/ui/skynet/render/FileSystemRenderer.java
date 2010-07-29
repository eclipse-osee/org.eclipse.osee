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

import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.DEFAULT_OPEN;
import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.PREVIEW;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
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
 * @author Jeff C. Phillips
 */
public abstract class FileSystemRenderer extends DefaultArtifactRenderer {

   private static final ArtifactFileMonitor FILE_MONITOR = new ArtifactFileMonitor();

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

   public IFile renderToFileSystem(IFolder baseFolder, Artifact artifact, Branch branch, PresentationType presentationType) throws OseeCoreException {
      String fileName = RenderingUtil.getFilenameFromArtifact(this, artifact, presentationType);
      List<Artifact> artifacts;
      if (artifact != null) {
         artifacts = Collections.singletonList(artifact);
      } else {
         artifacts = Collections.emptyList();
      }
      InputStream inputStream = getRenderInputStream(presentationType, artifacts);
      return renderToFile(baseFolder, fileName, branch, inputStream, presentationType);
   }

   public IFile renderToFileSystem(IFolder baseFolder, List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      Branch initialBranch = null;
      for (Artifact artifact : artifacts) {
         if (initialBranch == null) {
            initialBranch = artifact.getBranch();
         } else {
            if (artifact.getBranch() != initialBranch) {
               throw new IllegalArgumentException("All of the artifacts must be on the same branch to be mass edited");
            }
         }
      }

      Artifact artifact = null;
      if (artifacts.size() == 1) {
         artifact = artifacts.iterator().next();
      }
      String fileName = RenderingUtil.getFilenameFromArtifact(this, artifact, presentationType);
      InputStream inputStream = getRenderInputStream(presentationType, artifacts);
      return renderToFile(baseFolder, fileName, initialBranch, inputStream, presentationType);
   }

   public IFile renderToFile(IFolder baseFolder, String fileName, Branch branch, InputStream renderInputStream, PresentationType presentationType) throws OseeCoreException {
      IFile workingFile = baseFolder.getFile(fileName);
      AIFile.writeToFile(workingFile, renderInputStream);

      if (presentationType == PresentationType.SPECIALIZED_EDIT || presentationType == PresentationType.MERGE_EDIT) {
         FILE_MONITOR.addFile(workingFile);
      } else if (presentationType == PresentationType.PREVIEW) {
         FILE_MONITOR.markAsReadOnly(workingFile);
      }
      return workingFile;
   }

   public void addFileToWatcher(IFolder baseFolder, String fileName) {
      IFile workingFile = baseFolder.getFile(fileName);
      FILE_MONITOR.addFile(workingFile);
   }

   public abstract InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) throws OseeCoreException;

   public abstract Program getAssociatedProgram(Artifact artifact) throws OseeCoreException;

   public abstract String getAssociatedExtension(Artifact artifact) throws OseeCoreException;

   /**
    * @return the workbenchSavePopUpDisabled
    */
   public static boolean isWorkbenchSavePopUpDisabled() {
      return FILE_MONITOR.isWorkbenchSavePopUpDisabled();
   }

   /**
    * @param workbenchSavePopUpDisabled the workbenchSavePopUpDisabled to set
    */
   public static void setWorkbenchSavePopUpDisabled(boolean workbenchSavePopUpDisabled) {
      FILE_MONITOR.setWorkbenchSavePopUpDisabled(workbenchSavePopUpDisabled);
   }

   @Override
   public void open(List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      if (presentationType == DEFAULT_OPEN) {
         presentationType = PREVIEW;
      }

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
