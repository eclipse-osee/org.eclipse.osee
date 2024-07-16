/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.util.Import;

import java.io.File;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class TaskImportJob extends Job {
   private final File file;
   private final ExcelAtsTaskArtifactExtractor atsTaskExtractor;
   private XResultData rd;

   public TaskImportJob(File file, ExcelAtsTaskArtifactExtractor atsTaskExtractor) {
      super("Importing Tasks");
      this.file = file;
      this.atsTaskExtractor = atsTaskExtractor;
   }

   public TaskImportJob(File file, ExcelAtsTaskArtifactExtractor atsTaskExtractor, XResultData rd) {
      this(file, atsTaskExtractor);
      this.rd = rd;
   }

   @Override
   public IStatus run(final IProgressMonitor monitor) {
      IStatus toReturn = Status.CANCEL_STATUS;
      try {
         atsTaskExtractor.setMonitor(monitor);
         monitor.beginTask("Importing Tasks", 0);
         if (file != null && file.isFile()) {
            try {
               atsTaskExtractor.process(file.toURI(), rd);
            } catch (Throwable ex) {
               throw new Exception(ex.getMessage(), ex.getCause());
            }
         } else {
            throw new OseeArgumentException("All files passed must be a file");
         }
         toReturn = Status.OK_STATUS;
      } catch (Exception ex) {
         if (ex.getCause().getMessage() == ImportTasksFromSpreadsheet.INVALID_BLAM_CAUSE) {
            // suppress dialog for XReportData
         } else {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            toReturn = new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, ex.getMessage(), ex);
         }
      } finally {
         monitor.done();
      }
      return toReturn;
   }
}
