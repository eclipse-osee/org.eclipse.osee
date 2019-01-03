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

package org.eclipse.osee.ats.ide.util.Import;

import java.io.File;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class TaskImportJob extends Job {
   private final File file;
   private final ExcelAtsTaskArtifactExtractor atsTaskExtractor;

   public TaskImportJob(File file, ExcelAtsTaskArtifactExtractor atsTaskExtractor) {
      super("Importing Tasks");
      this.file = file;
      this.atsTaskExtractor = atsTaskExtractor;
   }

   @Override
   public IStatus run(final IProgressMonitor monitor) {
      IStatus toReturn = Status.CANCEL_STATUS;
      try {
         atsTaskExtractor.setMonitor(monitor);
         monitor.beginTask("Importing Tasks", 0);
         if (file != null && file.isFile()) {
            atsTaskExtractor.process(file.toURI());
         } else {
            throw new OseeArgumentException("All files passed must be a file");
         }
         toReturn = Status.OK_STATUS;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         toReturn = new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, ex.getMessage(), ex);
      } finally {
         monitor.done();
      }
      return toReturn;
   }
}
