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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.ui.plugin.util.AIFile;

/**
 * @author Ryan D. Brooks
 * @author Jeff C. Phillips
 */
public abstract class FileRenderer extends FileSystemRenderer {
   private ResourceAttributes readonlyfileAttributes;

   public FileRenderer() {
      super();
      readonlyfileAttributes = new ResourceAttributes();
      readonlyfileAttributes.setReadOnly(true);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer#renderToFileSystem(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.resources.IFolder, org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String, org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer.PresentationType)
    */
   @Override
   public IFile renderToFileSystem(IProgressMonitor monitor, IFolder baseFolder, Artifact artifact, Branch branch, String option, PresentationType presentationType) throws Exception {
      return renderToFile(baseFolder, getFilenameFromArtifact(artifact, presentationType), branch,
            getRenderInputStream(monitor, artifact, option, presentationType), presentationType);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer#renderToFileSystem(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.resources.IFolder, java.util.List, java.lang.String, org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer.PresentationType)
    */
   @Override
   public IFile renderToFileSystem(IProgressMonitor monitor, IFolder baseFolder, List<Artifact> artifacts, String option, PresentationType presentationType) throws Exception {
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
            getRenderInputStream(monitor, artifacts, option, presentationType), presentationType);
   }

   protected IFile renderToFile(IFolder baseFolder, String fileName, Branch branch, InputStream renderInputStream, PresentationType presentationType) throws Exception {
      IFile workingFile = baseFolder.getFile(fileName);
      AIFile.writeToFile(workingFile, renderInputStream);

      if (presentationType == PresentationType.PREVIEW) {
         workingFile.setResourceAttributes(readonlyfileAttributes);
      }

      return workingFile;
   }

   protected String getFilenameFromArtifact(Artifact artifact, PresentationType presentationType) {
      StringBuilder name = new StringBuilder(100);

      if (artifact != null) {
         TransactionId transactionId = artifact.getPersistenceMemo().getTransactionId();
         name.append(artifact.getSafeName());
         name.append("(");
         name.append(artifact.getGuid());
         name.append(")");

         if (!transactionId.isEditable() || presentationType == PresentationType.DIFF) {
            name.append("(");
            name.append(transactionId.getTransactionNumber());
            name.append(")");
         }

         name.append(" ");
         name.append((new Date()).toString().replaceAll(":", ";"));
         name.append(".");
         name.append(getAssociatedExtension(artifact));
      } else {
         name.append(GUID.generateGuidStr());
         name.append(".xml");
      }
      return name.toString();
   }

   public abstract InputStream getRenderInputStream(IProgressMonitor monitor, List<Artifact> artifacts, String option, PresentationType presentationType) throws Exception;

   public abstract InputStream getRenderInputStream(IProgressMonitor monitor, Artifact artifact, String option, PresentationType presentationType) throws Exception;

   public abstract String getAssociatedExtension(Artifact artifact);
}
