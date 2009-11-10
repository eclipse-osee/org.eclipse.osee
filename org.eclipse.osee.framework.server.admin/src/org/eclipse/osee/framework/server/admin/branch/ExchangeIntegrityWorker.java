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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.util.ResourceLocator;
import org.eclipse.osee.framework.server.admin.Activator;
import org.eclipse.osee.framework.server.admin.BaseServerCommand;

/**
 * @author Roberto E. Escobar
 */
public class ExchangeIntegrityWorker extends BaseServerCommand {

   protected ExchangeIntegrityWorker() {
      super("Verify Exchange File");
   }

   private boolean isValidArg(String arg) {
      return arg != null && arg.length() > 0;
   }

   @Override
   protected void doCommandWork(IProgressMonitor monitor) throws Exception {
      String arg = null;
      int count = 0;
      List<File> importFiles = new ArrayList<File>();
      do {
         arg = getCommandInterpreter().nextArgument();
         if (isValidArg(arg)) {
            if (count == 0 && !arg.startsWith("-")) {
               importFiles.add(new File(arg));
            }
            count++;
         }
      } while (isValidArg(arg));

      if (importFiles.isEmpty()) {
         throw new IllegalArgumentException("File to check was not specified");
      }

      for (File file : importFiles) {
         if (file == null || !file.exists() || !file.canRead()) {
            throw new IllegalArgumentException(String.format("File was not accessible: [%s]", file));
         } else if (file.isFile() && !Lib.isCompressed(file)) {
            throw new IllegalArgumentException(String.format("Invalid File: [%s]", file));
         }
      }

      for (File fileToImport : importFiles) {
         URI uri = new URI("exchange://" + fileToImport.toURI().toASCIIString());
         Activator.getInstance().getBranchExchange().checkIntegrity(new ResourceLocator(uri));
      }
   }
}
