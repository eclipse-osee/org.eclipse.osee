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
import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.IOperation;
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

   private static final Map<Class<? extends IArtifactUpdateOperationFactory>, ArtifactFileMonitor> FILE_MONITOR_MAP =
      new ConcurrentHashMap<Class<? extends IArtifactUpdateOperationFactory>, ArtifactFileMonitor>();

   private static final IArtifactUpdateOperationFactory DEFAULT_ARTIFACT_OP_FACTORY = new UpdateArtifactJobFactory();

   private static final class UpdateArtifactJobFactory implements IArtifactUpdateOperationFactory {

      @SuppressWarnings("unused")
      @Override
      public IOperation createUpdateOp(File file) throws OseeCoreException {
         return new UpdateArtifactOperation(file);
      }
   }

   private final Class<? extends IArtifactUpdateOperationFactory> monitorKey;

   protected FileSystemRenderer(IArtifactUpdateOperationFactory jobFactory) {
      super();
      this.monitorKey = jobFactory.getClass();

      ArtifactFileMonitor monitor = getFileMonitor();
      if (monitor == null) {
         monitor = new ArtifactFileMonitor(jobFactory);
         monitor.setWorkbenchSavePopUpDisabled(isWorkbenchSavePopUpDisabled());
         FILE_MONITOR_MAP.put(monitorKey, monitor);
      }
   }

   public FileSystemRenderer() {
      this(DEFAULT_ARTIFACT_OP_FACTORY);
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
         getFileMonitor().addFile(workingFile);
      } else if (presentationType == PresentationType.PREVIEW) {
         getFileMonitor().markAsReadOnly(workingFile);
      }
      return workingFile;
   }

   public void addFileToWatcher(IFolder baseFolder, String fileName) {
      IFile workingFile = baseFolder.getFile(fileName);
      getFileMonitor().addFile(workingFile);
   }

   public abstract InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) throws OseeCoreException;

   public abstract Program getAssociatedProgram(Artifact artifact) throws OseeCoreException;

   public abstract String getAssociatedExtension(Artifact artifact) throws OseeCoreException;

   /**
    * @return the workbenchSavePopUpDisabled
    */
   public static boolean isWorkbenchSavePopUpDisabled() {
      boolean result = false;
      for (ArtifactFileMonitor monitor : FILE_MONITOR_MAP.values()) {
         result = monitor.isWorkbenchSavePopUpDisabled();
         if (result) {
            break;
         }
      }
      return result;
   }

   /**
    * @param workbenchSavePopUpDisabled the workbenchSavePopUpDisabled to set
    */
   public static void setWorkbenchSavePopUpDisabled(boolean workbenchSavePopUpDisabled) {
      for (ArtifactFileMonitor monitor : FILE_MONITOR_MAP.values()) {
         monitor.setWorkbenchSavePopUpDisabled(workbenchSavePopUpDisabled);
      }
   }

   private ArtifactFileMonitor getFileMonitor() {
      return FILE_MONITOR_MAP.get(monitorKey);
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
               if (RenderingUtil.arePopupsAllowed()) {
                  FileUiUtil.ensureFilenameLimit(file);
                  Program program = getAssociatedProgram(firstArtifact);
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
                        "No program associated with the extension [%s] found on your local machine.",
                        file.getFileExtension());
                  }
               }
            }
         }
      }
   }
}
