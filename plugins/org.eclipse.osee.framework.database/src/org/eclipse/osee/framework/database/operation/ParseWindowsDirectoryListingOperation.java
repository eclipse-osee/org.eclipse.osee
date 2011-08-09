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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
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
public class ParseWindowsDirectoryListingOperation extends AbstractDbTxOperation {
   private static final String DIRECTORY_PREFIX = " Directory of Y:\\";
   private final String listingFile;

   public ParseWindowsDirectoryListingOperation(IOseeDatabaseService databaseService, IOseeCachingService cachingService, OperationLogger logger, String listingFile) {
      super(databaseService, "Parsing Windows Directory Listing", Activator.PLUGIN_ID, logger);
      this.listingFile = listingFile;
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) {

      log();
      log("Parsing windows directory listing:");

      Matcher matcher = Pattern.compile("(\\d+/\\d+/\\d+).*<DIR>.*?SW\\\\(\\S+)\\s+(.*)").matcher("");
      BufferedWriter writer;
      try {
         writer = new BufferedWriter(new FileWriter(Lib.removeExtension(listingFile) + ".csv"));

         String path = null;
         for (String line : Lib.readListFromFile(listingFile)) {
            if (line.startsWith(DIRECTORY_PREFIX)) {
               path = line.substring(DIRECTORY_PREFIX.length());
            } else {
               matcher.reset(line);
               if (matcher.find()) {
                  String summary = matcher.group(1) + "|" + matcher.group(2) + "|" + path + "\\" + matcher.group(3);
                  if (!summary.endsWith(".")) {
                     writer.write(summary);
                     writer.write(Lib.lineSeparator);
                  }
               }
            }
         }
         writer.close();
      } catch (IOException ex) {
         log("ParseWindowsDirectoryListingOperation::doTxWork: Caught IOException:" + ex.toString());
      } finally {
         log("...done.");
      }
   }
}
