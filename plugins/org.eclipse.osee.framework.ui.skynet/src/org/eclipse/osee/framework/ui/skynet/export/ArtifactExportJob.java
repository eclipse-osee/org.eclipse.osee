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
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactExportJob extends Job {
   private final File rootExportPath;
   private final Collection<Artifact> exportArtifacts;

   public ArtifactExportJob(File exportPath, Collection<Artifact> exportArtifacts) {
      super("Artifact Export");
      this.rootExportPath = exportPath;
      this.exportArtifacts = exportArtifacts;
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      IStatus toReturn;
      try {
         monitor.beginTask("Exporting Artifacts", countDescendents());

         for (Artifact artifact : exportArtifacts) {
            if (monitor.isCanceled()) {
               return new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "User Cancled the operation.");
            }
            writeArtifactPreview(rootExportPath, monitor, artifact);
         }

         toReturn = Status.OK_STATUS;
      } catch (Exception ex) {
         toReturn = new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, ex.getLocalizedMessage(), ex);
      } finally {
         monitor.done();
      }
      return toReturn;
   }

   private int countDescendents()  {
      int total = 0;
      for (Artifact artifact : exportArtifacts) {
         total += artifact.getDescendants().size() + 1;
      }
      return total;
   }

   private void writeArtifactPreview(File exportPath, IProgressMonitor monitor, Artifact artifact) throws Exception {
      if (artifact.isOfType(CoreArtifactTypes.Folder)) {
         File folder = new File(exportPath, artifact.getName());
         folder.mkdir();
         for (Artifact child : artifact.getChildren()) {
            writeArtifactPreview(folder, monitor, child);
         }
      } else {
         try {
            RendererManager.open(artifact, PresentationType.PREVIEW);
         } catch (OseeArgumentException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      monitor.worked(1);
   }
}