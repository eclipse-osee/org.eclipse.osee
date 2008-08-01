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
package org.eclipse.osee.framework.skynet.core.dbinit;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.osee.framework.database.initialize.tasks.DbInitializationTask;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.util.LogProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exportImport.BranchImporterSaxHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Roberto E. Escobar
 */
public class SkynetDbBranchDataImport extends DbInitializationTask {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(SkynetDbBranchDataImport.class);

   private File getImportFile(String fileToImport) throws Exception {
      File toReturn = null;
      if (fileToImport.startsWith("http")) {
         throw new UnsupportedOperationException("Remote Skynet branch data file import is unsupported.");
      } else {
         toReturn = new File(fileToImport);
         if (toReturn == null) {
            throw new FileNotFoundException("Skynet branch data file cannot be null");
         } else if (toReturn.exists() != true) {
            throw new FileNotFoundException(String.format("Skynet branch data file cannot be found [%s]", fileToImport));
         } else if (fileToImport.endsWith("zip") != true) {
            throw new IOException(String.format("Skynet branch data file is invalid [%s] ", fileToImport));
         }
      }
      return toReturn;
   }

//   private Branch getBranchToImportIntoFromXml(InputStream inputStream) throws Exception {
//      Branch toReturn = null;
//      String branchName = null;
//      try {
//         byte[] buffer = new byte[1000];
//         inputStream.mark(1000);
//         inputStream.read(buffer);
//         Pattern pattern = Pattern.compile("<Name>(.*)?</Name>");
//         Matcher matcher = pattern.matcher(new String(buffer));
//         if (matcher.find()) {
//            branchName = matcher.group(1);
//            toReturn = getBranch(branchName);
//         } else {
//            throw new Exception("Unable to get Branch Name from Import file.");
//         }
//      } finally {
//         inputStream.reset();
//      }
//      return toReturn;
//   }

   private Branch getBranch(String branchName) throws Exception {
      Branch toReturn = null;
      try {
         toReturn = BranchPersistenceManager.getBranch(branchName);
      } catch (BranchDoesNotExist ex) {
         toReturn = BranchPersistenceManager.createRootBranch(null, branchName, branchName, null, false);
         logger.log(Level.INFO, String.format("Created [%s] Branch", branchName));
      }
      return toReturn;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.database.initialize.tasks.IDbInitializationTask#run(java.sql.Connection)
    */
   @Override
   public void run(Connection connection) throws Exception {
      List<String> toImport = OseeProperties.getInstance().getDbOseeSkynetBranchImport();
      if (toImport.isEmpty()) {
         logger.log(Level.INFO, "Skynet Branch Data Import skipped [Branch Data unspecified]");
      } else {
         for (String fileToImport : toImport) {
            File importFile = getImportFile(fileToImport);
            logger.log(Level.INFO, String.format("Import Skynet Branch Data from [%s]", fileToImport));
            ZipFile zipFile = null;
            try {
               String baseName = Lib.removeExtension(importFile.getName());
               zipFile = new ZipFile(importFile);
               ZipEntry entry = zipFile.getEntry(baseName + ".xml");
               XMLReader reader = XMLReaderFactory.createXMLReader();
               InputStream inputStream = new BufferedInputStream(zipFile.getInputStream(entry));
               try {
                  Branch branchToImportInto = getBranch(baseName);
                  reader.setContentHandler(new BranchImporterSaxHandler(zipFile, branchToImportInto, true, true,
                        new LogProgressMonitor()));
                  reader.parse(new InputSource(inputStream));
               } finally {
                  if (inputStream != null) {
                     inputStream.close();
                  }
               }
            } finally {
               if (zipFile != null) {
                  zipFile.close();
               }
            }
         }
      }
   }
}
