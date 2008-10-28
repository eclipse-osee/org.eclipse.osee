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
package org.eclipse.osee.define.meta;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.define.relation.Import.RelationImporter;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Ryan D. Brooks
 */
public class ImportRelationJob extends Job {
   private final File file;
   private final Branch branch;

   public ImportRelationJob(File file, Branch branch) throws IllegalArgumentException, CoreException {
      super("Importing Relations");
      this.file = file;
      this.branch = branch;
   }

   public IStatus run(IProgressMonitor monitor) {
      try {
         RelationImporter importer = new RelationImporter(branch);
         importer.extractRelationsFromSheet(new FileInputStream(file), monitor);
         return Status.OK_STATUS;
      } catch (Exception ex) {
         String message = ex.getMessage();

         if (message == null) message = "";

         OseeLog.log(DefinePlugin.class, Level.SEVERE, ex);
         return new Status(Status.ERROR, DefinePlugin.PLUGIN_ID, -1, ex.toString(), ex);
      }
   }
}