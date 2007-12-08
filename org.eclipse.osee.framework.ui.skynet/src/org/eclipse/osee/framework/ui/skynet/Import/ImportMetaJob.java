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
package org.eclipse.osee.framework.ui.skynet.Import;

import java.io.File;
import java.io.FileInputStream;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.jdk.core.util.io.ExtensionFilter;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.importing.SkynetTypesImporter;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Robert A. Fisher
 * @author Jeff C. Phillips
 */
public class ImportMetaJob extends Job {
   private final Branch branch;
   private final File file;

   public ImportMetaJob(File file, Branch branch) {
      super("Importing Skynet Types");
      this.branch = branch;
      this.file = file;
   }

   public IStatus run(IProgressMonitor monitor) {
      try {

         SkynetTypesImporter importer = new SkynetTypesImporter(branch);
         if (file.isFile()) {
            monitor.beginTask("Importing " + file.getName(), 2);
            importer.extractTypesFromSheet(new FileInputStream(file));
            monitor.worked(1);
         } else if (file.isDirectory()) {
            File[] children = file.listFiles(new ExtensionFilter(".xml"));
            monitor.beginTask("Importing files", children.length + 1);
            for (File childFile : children) {
               if (monitor.isCanceled()) {
                  monitor.done();
                  return Status.CANCEL_STATUS;
               }

               monitor.subTask(childFile.getName());
               importer.extractTypesFromSheet(new FileInputStream(childFile));
               monitor.worked(1);
            }
         }
         if (monitor.isCanceled()) {
            monitor.done();
            return Status.CANCEL_STATUS;
         }

         monitor.subTask("Finalizing");
         importer.finish();
         monitor.done();
         return Status.OK_STATUS;
      } catch (Exception ex) {
         return new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, -1, ex.getLocalizedMessage(), ex);
      }
   }
}