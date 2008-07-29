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
package org.eclipse.osee.framework.ui.skynet.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exportImport.FullPortableExport;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.search.report.RelationMatrixExportJob;
import org.eclipse.search.ui.text.Match;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactExportJob extends Job {
   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(RelationMatrixExportJob.class);
   private IStructuredSelection selection;
   private Collection<Artifact> artifacts;
   private String exportFilename;

   public ArtifactExportJob() {
      super("Exporting Artifacts and Relations to " + OseeData.getPath());
   }

   public ArtifactExportJob(TableViewer viewer) {
      this();
      selection = (IStructuredSelection) viewer.getSelection();
   }

   public ArtifactExportJob(Collection<Artifact> artifacts) {
      this();
      this.artifacts = artifacts;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected IStatus run(IProgressMonitor monitor) {
      try {
         FullPortableExport exporter = new FullPortableExport();
         if (artifacts == null && selection == null) {
            exporter.createArtifactSheets(BranchPersistenceManager.getDefaultBranch());
            exporter.createRelationsSheet();
         } else {
            if (artifacts == null) {
               artifacts = new ArrayList<Artifact>(selection.size());
               Iterator<?> matchIter = selection.iterator();
               while (matchIter.hasNext()) {
                  artifacts.add((Artifact) ((Match) matchIter.next()).getElement());
               }
            }

            exporter.createArtifactSheets(artifacts);
            exporter.createRelationsSheet(artifacts);
         }

         exporter.finish(exportFilename);
         System.out.println("Export Complete");
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
         return new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, -1, ex.toString(), ex);
      }
      return Status.OK_STATUS;
   }

   public String getExportFilename() {
      return exportFilename;
   }

   public void setExportFilename(String exportFilename) {
      this.exportFilename = exportFilename;
   }
}