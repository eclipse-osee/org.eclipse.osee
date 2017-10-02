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

import static org.eclipse.osee.framework.core.enums.PresentationType.DEFAULT_OPEN;
import static org.eclipse.osee.framework.core.enums.PresentationType.PREVIEW;
import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
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

   private static final ArtifactFileMonitor monitor = new ArtifactFileMonitor();

   public FileSystemRenderer(Map<RendererOption, Object> rendererOptions) {
      super(rendererOptions);
   }

   public FileSystemRenderer() {
      this(new HashMap<RendererOption, Object>());
   }

   public IFile renderToFile(Artifact artifact, IOseeBranch branch, PresentationType presentationType)  {
      List<Artifact> artifacts;
      if (artifact == null) {
         artifacts = Collections.emptyList();
      } else {
         artifacts = Collections.singletonList(artifact);
      }
      return renderToFile(artifacts, branch, presentationType);
   }

   public IFile renderToFile(List<Artifact> artifacts, PresentationType presentationType)  {
      IOseeBranch initialBranch = null;
      for (Artifact artifact : artifacts) {
         if (initialBranch == null) {
            initialBranch = artifact.getBranchToken();
         } else {
            if (!artifact.isOnBranch(initialBranch)) {
               throw new IllegalArgumentException("All of the artifacts must be on the same branch to be mass edited");
            }
         }
      }

      return renderToFile(artifacts, initialBranch, presentationType);
   }

   public IFile renderToFile(List<Artifact> artifacts, IOseeBranch branch, PresentationType presentationType)  {
      InputStream renderInputStream = getRenderInputStream(presentationType, branch, artifacts);
      IFile workingFile = RenderingUtil.getRenderFile(this, artifacts, branch, presentationType);
      AIFile.writeToFile(workingFile, renderInputStream);

      if (presentationType == SPECIALIZED_EDIT) {
         File file = workingFile.getLocation().toFile();
         monitor.addFile(file, getUpdateOperation(file, artifacts, branch, presentationType));
      } else if (presentationType == PresentationType.PREVIEW) {
         monitor.markAsReadOnly(workingFile);
      }
      return workingFile;
   }

   public abstract InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) ;

   public InputStream getRenderInputStream(PresentationType presentationType, IOseeBranch branch, List<Artifact> artifacts)  {
      return getRenderInputStream(presentationType, artifacts);
   }

   public abstract Program getAssociatedProgram(Artifact artifact) ;

   public abstract String getAssociatedExtension(Artifact artifact) ;

   @Override
   public void open(List<Artifact> artifacts, PresentationType presentationType)  {
      if (presentationType == DEFAULT_OPEN) {
         presentationType = PREVIEW;
      }

      IFile file = renderToFile(artifacts, presentationType);
      if (file != null) {
         if (!artifacts.isEmpty()) {
            String dummyName = file.getName();
            Artifact firstArtifact = artifacts.iterator().next();
            try {
               if (RenderingUtil.arePopupsAllowed()) {
                  RenderingUtil.ensureFilenameLimit(file);
                  Program program = getAssociatedProgram(firstArtifact);
                  program.execute(file.getLocation().toFile().getAbsolutePath());
               } else {
                  OseeLog.logf(Activator.class, Level.INFO,
                     "Test - Opening File - [%s]" + file.getLocation().toFile().getAbsolutePath());
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

   protected abstract IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch, PresentationType presentationType) ;
}
