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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.osee.framework.database.initialize.tasks.DbInitializationTask;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exportImport.BranchImporterSaxHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Roberto E. Escobar
 */
public class SkynetDbBranchDataImport extends DbInitializationTask {
   private Logger logger = ConfigUtil.getConfigFactory().getLogger(SkynetDbBranchDataImport.class);

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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.database.initialize.tasks.IDbInitializationTask#run(java.sql.Connection)
    */
   @Override
   public void run(Connection connection) throws Exception {
      Map<String, String> toImport = OseeProperties.getInstance().getDbOseeSkynetBranchImport();
      if (toImport.isEmpty()) {
         logger.log(Level.INFO, "Skynet Branch Data Import skipped [Branch and Data unspecified]");
      } else {
         for (String branchName : toImport.keySet()) {
            Branch branchToImportInto = BranchPersistenceManager.getBranch(branchName);
            String fileToImport = toImport.get(branchName);
            File importFile = getImportFile(fileToImport);
            logger.log(Level.INFO, String.format("Import Skynet Branch Data from [%s]", fileToImport));
            ZipFile zipFile = null;
            try {
               String baseName = Lib.removeExtension(importFile.getName());
               zipFile = new ZipFile(importFile);
               ZipEntry entry = zipFile.getEntry(baseName + ".xml");
               InputStream imputStream = zipFile.getInputStream(entry);
               XMLReader reader = XMLReaderFactory.createXMLReader();
               reader.setContentHandler(new BranchImporterSaxHandler(zipFile, branchToImportInto, true, true, null));
               reader.parse(new InputSource(imputStream));
            } finally {
               if (zipFile != null) {
                  zipFile.close();
               }
            }
         }
      }
   }
}
