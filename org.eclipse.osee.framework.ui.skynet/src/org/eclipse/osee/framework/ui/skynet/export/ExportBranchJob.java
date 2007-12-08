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
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exportImport.BranchExporter;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Robert A. Fisher
 */
public class ExportBranchJob extends Job {
   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(ExportBranchJob.class);
   private final File file;
   private final Branch branch;
   private final boolean descendantsOnly;

   public ExportBranchJob(File file, Branch branch, boolean descendantsOnly) {
      super("Exporting Branch");
      if (branch == null) throw new IllegalArgumentException("branch can not be null");
      if (file == null) throw new IllegalArgumentException("file can not be null");

      this.file = file;
      this.branch = branch;
      this.descendantsOnly = descendantsOnly;
   }

   public IStatus run(IProgressMonitor monitor) {
      try {

         new BranchExporter(monitor, file, branch, new Timestamp(0), GlobalTime.GreenwichMeanTimestamp(),
               descendantsOnly).export();

         return Status.OK_STATUS;
      } catch (Exception ex) {
         monitor.done();
         String message = ex.getLocalizedMessage();

         if (message == null) message = "";

         logger.log(Level.SEVERE, message, ex);
         return new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, -1, ex.toString(), ex);
      }
   }
}