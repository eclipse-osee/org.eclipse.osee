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

package org.eclipse.osee.framework.ui.skynet.export;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.render.FileRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactExportJob extends Job {
   private final File rootExportPath;
   private final Collection<Artifact> exportArtifacts;

   /**
    * @param name
    */
   public ArtifactExportJob(File exportPath, Collection<Artifact> exportArtifacts) {
      super("Artifact Export");
      this.rootExportPath = exportPath;
      this.exportArtifacts = exportArtifacts;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected IStatus run(IProgressMonitor monitor) {
      IStatus toReturn;
      try {
         monitor.beginTask("Exporting Artifacts", countDescendents());

         for (Artifact artifact : exportArtifacts) {
            if (monitor.isCanceled()) {
               return new Status(Status.CANCEL, SkynetGuiPlugin.PLUGIN_ID, "User Cancled the operation.");
            }
            writeArtifactPreview(rootExportPath, monitor, artifact, PresentationType.PREVIEW);
         }

         toReturn = Status.OK_STATUS;
      } catch (Exception ex) {
         toReturn = new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, -1, ex.getLocalizedMessage(), ex);
      } finally {
         monitor.done();
      }
      return toReturn;
   }

   private int countDescendents() throws OseeCoreException {
      int total = 0;
      for (Artifact artifact : exportArtifacts) {
         total += artifact.getDescendants().size() + 1;
      }
      return total;
   }

   private void writeArtifactPreview(File exportPath, IProgressMonitor monitor, Artifact artifact, PresentationType presentationType) throws Exception {
      if (artifact.getArtifactTypeName().equals("Folder")) {
         File folder = new File(exportPath, artifact.getDescriptiveName());
         folder.mkdir();
         for (Artifact child : artifact.getChildren()) {
            writeArtifactPreview(folder, monitor, child, presentationType);
         }
      } else {
         try {
            FileRenderer fileRenderer = RendererManager.getBestFileRenderer(presentationType, artifact, null);
            String fileName = artifact.getSafeName() + "." + fileRenderer.getAssociatedExtension(artifact);
            InputStream inputStream = fileRenderer.getRenderInputStream(artifact, presentationType);
            Lib.inputStreamToFile(inputStream, new File(exportPath, fileName));
         } catch (OseeArgumentException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, "Artifact requires a FileRenderer");
         }
      }
      monitor.worked(1);
   }
}