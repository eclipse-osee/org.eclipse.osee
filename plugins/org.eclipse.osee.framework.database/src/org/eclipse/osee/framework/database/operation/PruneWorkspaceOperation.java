/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.database.operation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.internal.Activator;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Ryan D. Brooks
 * @author Shawn F. Cook
 */
public class PruneWorkspaceOperation extends AbstractDbTxOperation {
   private final String preserveFilePattern;
   private final String workspacePathStr;
   private final String purgeFilePattern;

   public PruneWorkspaceOperation(IOseeDatabaseService databaseService, IOseeCachingService cachingService, OperationLogger logger, String preserveFilePattern, String workspacePathStr, String purgeFilePattern) {
      super(databaseService, "Prune Workspace", Activator.PLUGIN_ID, logger);
      this.preserveFilePattern = preserveFilePattern;
      this.workspacePathStr = workspacePathStr;
      this.purgeFilePattern = purgeFilePattern;
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {

      log();
      log("Pruning Workspace:");

      File keeperFile = new File(preserveFilePattern);
      File workspacePath = new File(workspacePathStr);
      String filePathPattern = purgeFilePattern;

      ArrayList<String> preserveList;
      try {
         if (!preserveFilePattern.isEmpty()) {
            preserveList = Lib.readListFromFile(keeperFile, true);
         } else {
            preserveList = new ArrayList<String>();
         }

         HashSet<String> preserveSet = new HashSet<String>(preserveList);

         List<File> files = Lib.recursivelyListFiles(workspacePath, Pattern.compile(filePathPattern));
         for (File file : files) {
            if (monitor.isCanceled()) {
               return;
            }
            if (!preserveSet.contains(file.getName())) {
               file.delete();
            }
         }
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      log("...done.");
   }
}
