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

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.utility.FileWatcher;
import org.eclipse.osee.framework.ui.plugin.util.AIFile;

/**
 * @author Ryan D. Brooks
 * @author Jeff C. Phillips
 */
public abstract class FileRenderer extends FileSystemRenderer {
   private static final ResourceAttributes readonlyfileAttributes = new ResourceAttributes();
   private static Random generator = new Random();
   private static final FileWatcher watcher = new FileWatcher(3, TimeUnit.SECONDS);
   static {
      readonlyfileAttributes.setReadOnly(true);
      watcher.addListener(new ArtifactEditFileWatcher());
      watcher.start();
   }

   /**
    * @param rendererId
    */
   public FileRenderer(String rendererId) {
      super(rendererId);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer#renderToFileSystem(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.resources.IFolder, org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String, org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer.PresentationType)
    */
   @Override
   public IFile renderToFileSystem(IProgressMonitor monitor, IFolder baseFolder, Artifact artifact, Branch branch, PresentationType presentationType) throws OseeCoreException {
      return renderToFile(baseFolder, getFilenameFromArtifact(artifact, presentationType), branch,
            getRenderInputStream(artifact, presentationType), presentationType);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer#renderToFileSystem(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.resources.IFolder, java.util.List, java.lang.String, org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer.PresentationType)
    */
   @Override
   public IFile renderToFileSystem(IProgressMonitor monitor, IFolder baseFolder, List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
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

      return renderToFile(baseFolder, getFilenameFromArtifact(null, presentationType), initialBranch,
            getRenderInputStream(artifacts, presentationType), presentationType);
   }

   protected IFile renderToFile(IFolder baseFolder, String fileName, Branch branch, InputStream renderInputStream, PresentationType presentationType) throws OseeCoreException {
      try {
         IFile workingFile = baseFolder.getFile(fileName);
         AIFile.writeToFile(workingFile, renderInputStream);

         if (presentationType == PresentationType.EDIT) {
            watcher.addFile(workingFile.getLocation().toFile());
         } else if (presentationType == PresentationType.PREVIEW) {
            workingFile.setResourceAttributes(readonlyfileAttributes);
         }

         return workingFile;
      } catch (CoreException ex) {
         throw new OseeCoreException(ex);
      }
   }

   protected void addFileToWatcher(IFolder baseFolder, String fileName) {
      IFile workingFile = baseFolder.getFile(fileName);
      watcher.addFile(workingFile.getLocation().toFile());
   }

   protected String getFilenameFromArtifact(Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      StringBuilder name = new StringBuilder(100);

      if (artifact != null) {

         name.append(artifact.getSafeName());
         name.append("(");
         name.append(artifact.getGuid());
         name.append(")");

         if (artifact.isHistorical() || presentationType == PresentationType.DIFF) {
            name.append("(");
            name.append(artifact.getTransactionNumber());
            name.append(")");
         }

         name.append(" ");
         name.append((new Date()).toString().replaceAll(":", ";"));
         name.append("-");
         name.append(generator.nextInt(99) + 1);
         name.append(".");
         name.append(getAssociatedExtension(artifact));
      } else {
         name.append(GUID.generateGuidStr());
         name.append(".xml");
      }
      return name.toString();
   }

   public abstract InputStream getRenderInputStream(List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException;

   public abstract InputStream getRenderInputStream(Artifact artifact, PresentationType presentationType) throws OseeCoreException;

   public abstract String getAssociatedExtension(Artifact artifact) throws OseeCoreException;
}
