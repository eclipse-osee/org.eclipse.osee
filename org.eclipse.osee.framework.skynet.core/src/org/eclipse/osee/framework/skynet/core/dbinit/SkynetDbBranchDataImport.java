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
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.database.initialize.tasks.DbInitializationTask;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.plugin.core.util.LogProgressMonitor;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exportImport.BranchImporterSaxHandler;
import org.osgi.framework.Bundle;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Roberto E. Escobar
 */
public class SkynetDbBranchDataImport extends DbInitializationTask {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(SkynetDbBranchDataImport.class);
   private static final String ELEMENT_NAME = "OseeDbImportData";
   private static final String EXTENSION_POINT = SkynetActivator.PLUGIN_ID + "." + ELEMENT_NAME;
   private static final String BRANCH_NAME = "branchName";
   private static final String BRANCH_DATA = "branchData";

   private File getImportFile(ImportData importData) throws Exception {
      if (importData.getBranchData().endsWith("zip") != true) {
         throw new IOException(String.format("Branch data file is invalid [%s] ", importData.getBranchData()));
      }
      Bundle bundle = Platform.getBundle(importData.getBundleName());
      URL url = bundle.getResource(importData.getBranchData());
      url = FileLocator.toFileURL(url);
      File toReturn = new File(url.toURI());
      if (toReturn.exists() != true) {
         throw new FileNotFoundException(String.format("Branch data file cannot be found [%s]",
               importData.getBranchData()));
      }
      return toReturn;
   }

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
      if (OseeProperties.getInstance().getDbOseeSkynetBranchImport()) {
         Collection<ImportData> importDatas = loadDataFromExtensions();
         for (ImportData importData : importDatas) {
            logger.log(Level.INFO, String.format("Import Branch Data: [%s]", importData));
            File importFile = getImportFile(importData);
            importBranchData(importFile, importData.getBranchName());
         }
      }
   }

   private void importBranchData(File importFile, String branchTarget) throws Exception {
      ZipFile zipFile = null;
      try {
         String baseName = Lib.removeExtension(importFile.getName());
         zipFile = new ZipFile(importFile);
         ZipEntry entry = zipFile.getEntry(baseName + ".xml");
         XMLReader reader = XMLReaderFactory.createXMLReader();
         InputStream inputStream = new BufferedInputStream(zipFile.getInputStream(entry));
         try {
            Branch branchToImportInto = getBranch(branchTarget);
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

   private Collection<ImportData> loadDataFromExtensions() throws Exception {
      List<ImportData> toReturn = new ArrayList<ImportData>();
      Map<String, String> branchNames = new HashMap<String, String>();
      List<IConfigurationElement> elements = ExtensionPoints.getExtensionElements(EXTENSION_POINT, ELEMENT_NAME);
      int currentCount = 1;
      for (IConfigurationElement element : elements) {
         String bundleName = element.getContributor().getName();
         String branchName = element.getAttribute(BRANCH_NAME);
         String branchData = element.getAttribute(BRANCH_DATA);

         if (Strings.isValid(branchName) && Strings.isValid(branchData)) {
            if (!branchNames.containsKey(branchName.toLowerCase())) {
               branchNames.put(branchName.toLowerCase(), bundleName);
               toReturn.add(new ImportData(bundleName, branchName, branchData, currentCount++));
            } else {
               throw new Exception(String.format(
                     "Branch import error - cannot import twice into a branch - [%s] was already specified by [%s] ",
                     branchName, branchNames.get(branchName.toLowerCase())));
            }
         } else {
            throw new Exception(String.format("Branch import error: [%s] attributes were empty.",
                  element.getDeclaringExtension().getExtensionPointUniqueIdentifier()));
         }
      }
      Collections.sort(toReturn);
      return toReturn;
   }

   private final class ImportData implements Comparable<ImportData> {
      private String bundleName;
      private String branchName;
      private String branchData;
      private int priority;

      public ImportData(String bundleName, String branchName, String branchData, int currentCount) {
         super();
         this.branchName = branchName;
         this.branchData = branchData;
         this.priority = branchName.toLowerCase().equals("common") ? 0 : currentCount;
      }

      /* (non-Javadoc)
       * @see java.lang.Comparable#compareTo(java.lang.Object)
       */
      @Override
      public int compareTo(ImportData o) {
         return this.priority - o.priority;
      }

      public String toString() {
         return String.format("%s - %s", branchName, branchData);
      }

      public String getBranchName() {
         return branchName;
      }

      public String getBranchData() {
         return branchData;
      }

      public String getBundleName() {
         return bundleName;
      }
   }
}
