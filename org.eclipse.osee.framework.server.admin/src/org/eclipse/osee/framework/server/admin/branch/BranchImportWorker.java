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
package org.eclipse.osee.framework.server.admin.branch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.branch.management.ExportOptions;
import org.eclipse.osee.framework.resource.common.io.Files;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.server.admin.Activator;
import org.eclipse.osee.framework.server.admin.BaseCmdWorker;

/**
 * @author Roberto E. Escobar
 */
public class BranchImportWorker extends BaseCmdWorker {

   private boolean isValidArg(String arg) {
      return arg != null && arg.length() > 0;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.server.admin.BaseCmdWorker#doWork(long)
    */
   @Override
   protected void doWork(long startTime) throws Exception {
      String arg = null;
      boolean excludeBaselineTxs = false;
      List<File> importFiles = new ArrayList<File>();
      do {
         arg = getCommandInterpreter().nextArgument();
         if (isValidArg(arg)) {
            if (arg.equals("-excludeBaselineTxs")) {
               excludeBaselineTxs = true;
            } else if (!arg.startsWith("-")) {
               importFiles.add(new File(arg));
            }
         }
      } while (isValidArg(arg));

      if (importFiles.isEmpty()) {
         throw new IllegalArgumentException("Files to import were not specified");
      }

      for (File file : importFiles) {
         if (file == null || !file.exists() || !file.canRead()) {
            throw new IllegalArgumentException(String.format("File was not accessible: [%s]", file));
         } else if (!Files.getExtension(file.getAbsolutePath()).equals("zip")) {
            throw new IllegalArgumentException(String.format("Invalid File: [%s]", file));
         }
      }

      Options options = new Options();
      options.put(ExportOptions.EXCLUDE_BASELINE_TXS.name(), excludeBaselineTxs);
      for (File fileToImport : importFiles) {
         Activator.getInstance().getBranchImport().importBranch(fileToImport, options);
      }
   }
}
