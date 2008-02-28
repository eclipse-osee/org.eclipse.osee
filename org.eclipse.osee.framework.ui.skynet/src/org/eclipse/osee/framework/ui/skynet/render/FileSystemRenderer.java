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
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 */
public abstract class FileSystemRenderer extends Renderer {
   private static IFolder workingFolder;
   private static IFolder diffFolder;
   private static IFolder previewFolder;

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.Renderer#preview(org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void preview(Artifact artifact, String option, IProgressMonitor monitor) throws Exception {
      open(monitor, artifact, option, PresentationType.PREVIEW);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.Renderer#edit(org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void edit(Artifact artifact, String option, IProgressMonitor monitor) throws Exception {
      open(monitor, artifact, option, PresentationType.EDIT);
   }

   private void open(IProgressMonitor monitor, Artifact artifact, String option, PresentationType presentationType) throws Exception {
      IFolder baseFolder = getRenderFolder(artifact.getBranch(), presentationType);
      IFile file = renderToFileSystem(monitor, baseFolder, artifact, artifact.getBranch(), option, presentationType);
      getAssociatedProgram(artifact).execute(file.getLocation().toFile().getAbsolutePath());
   }

   public IFolder getRenderFolder(Branch branch, PresentationType presentationType) throws CoreException {
      IFolder baseFolder = ensureRenderFolderExists(presentationType);
      IFolder renderFolder = baseFolder.getFolder(branch.asFolderName());
      if (!renderFolder.exists()) {
         renderFolder.create(true, true, null);
      }
      return renderFolder;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.Renderer#preview(java.util.List, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void preview(List<Artifact> artifacts, String option, IProgressMonitor monitor) throws Exception {
      open(monitor, artifacts, option, PresentationType.PREVIEW);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.Renderer#edit(java.util.List, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void edit(List<Artifact> artifacts, String option, IProgressMonitor monitor) throws Exception {
      open(monitor, artifacts, option, PresentationType.EDIT);
   }

   private void open(IProgressMonitor monitor, List<Artifact> artifacts, String option, PresentationType presentationType) throws Exception {
      if (!artifacts.isEmpty()) {
         Artifact firstArtifact = artifacts.iterator().next();
         IFolder baseFolder = getRenderFolder(firstArtifact.getBranch(), presentationType);
         IFile file = renderToFileSystem(monitor, baseFolder, artifacts, option, presentationType);
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

   public static IFolder ensureRenderFolderExists(PresentationType presentationType) throws CoreException {
      switch (presentationType) {
         case DIFF:
            if (diffFolder == null || !diffFolder.exists()) {
               diffFolder = OseeData.getFolder(".diff");
            }
            return diffFolder;

         case EDIT:
            if (workingFolder == null || !workingFolder.exists()) {
               OSEELog.logInfo(SkynetGuiPlugin.class, "Created edit change listener", false);

               workingFolder = OseeData.getFolder(".working");
               // Set the visitor to this directory
               IResourceChangeListener listener = new EditChangeListener(workingFolder);
               ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
            }
            return workingFolder;

         case PREVIEW:
            if (previewFolder == null || !previewFolder.exists()) {
               previewFolder = OseeData.getFolder(".preview");
            }
            return previewFolder;

         default:
            throw new IllegalStateException("Unexpected presentation type");
      }
   }

   public IFile renderForDiff(IProgressMonitor monitor, Branch branch, String option) throws Exception {
      IFolder baseFolder = getRenderFolder(branch, PresentationType.DIFF);
      return renderToFileSystem(monitor, baseFolder, null, branch, option, PresentationType.DIFF);
   }

   public IFile renderForDiff(IProgressMonitor monitor, Artifact artifact, String option) throws Exception {
      if (artifact == null) {
         throw new IllegalArgumentException("Artifact can not be null.");
      }

      IFolder baseFolder = getRenderFolder(artifact.getBranch(), PresentationType.DIFF);
      return renderToFileSystem(monitor, baseFolder, artifact, artifact.getBranch(), option, PresentationType.DIFF);
   }

   public abstract IFile renderToFileSystem(IProgressMonitor monitor, IFolder baseFolder, Artifact artifact, Branch branch, String option, PresentationType presentationType) throws Exception;

   public abstract IFile renderToFileSystem(IProgressMonitor monitor, IFolder baseFolder, List<Artifact> artifacts, String option, PresentationType presentationType) throws Exception;

   public abstract Program getAssociatedProgram(Artifact artifact);

}