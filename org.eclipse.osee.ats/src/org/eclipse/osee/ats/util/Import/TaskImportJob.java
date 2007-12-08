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

package org.eclipse.osee.ats.util.Import;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;

/**
 * @author Donald G. Dunne
 */
public class TaskImportJob extends Job {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(TaskImportJob.class);
   private final File file;
   private ExcelAtsTaskArtifactExtractor extractor;
   private final Branch branch;

   public TaskImportJob(File file, String hrid, ExcelAtsTaskArtifactExtractor extractor, Branch branch) throws IllegalArgumentException, CoreException, SQLException {
      super("Importing Tasks");
      this.file = file;
      this.extractor = extractor;
      this.branch = branch;
   }

   public IStatus run(final IProgressMonitor monitor) {
      IStatus toReturn = Status.CANCEL_STATUS;
      try {
         extractor.setMonitor(monitor);
         monitor.beginTask("Importing Tasks", 0);
         AbstractSkynetTxTemplate txWrapper = new ExtractArtifactTx(branch, file, monitor);
         txWrapper.execute();
         toReturn = Status.OK_STATUS;
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
         toReturn = new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
      } finally {
         monitor.done();
      }
      return toReturn;
   }
   private final class ExtractArtifactTx extends AbstractSkynetTxTemplate {

      private IProgressMonitor monitor;
      private File file;

      public ExtractArtifactTx(Branch branch, File file, IProgressMonitor monitor) {
         super(branch);
         this.file = file;
         this.monitor = monitor;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.skynet.core.transaction.AbstractTxTemplate#handleTxWork()
       */
      @Override
      protected void handleTxWork() throws Exception {
         if (file != null && file.isFile()) {
            extractor.discoverArtifactAndRelationData(file);
         } else {
            throw new IllegalStateException("All files passed must be a file");
         }
         System.out.println("Committing Transaction");
         monitor.setTaskName("Committing Transaction");
         monitor.subTask(""); // blank out leftover relation subtask
         monitor.worked(1); // cause the status to update
      }

   }
}
