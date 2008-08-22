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
package org.eclipse.osee.framework.branch.management.exchange;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.osee.framework.branch.management.exchange.handler.BaseDbSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.BranchDataSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.ManifestSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.MetaData;
import org.eclipse.osee.framework.branch.management.exchange.handler.MetaDataSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.RelationalSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.RelationalTypeCheckSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.Translator;
import org.eclipse.osee.framework.branch.management.exchange.handler.ManifestSaxHandler.ImportFile;
import org.eclipse.osee.framework.db.connection.core.transaction.DbTransaction;
import org.eclipse.osee.framework.resource.management.Options;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Roberto E. Escobar
 */
final class ImportController extends DbTransaction {

   private File importFile;
   private Options options;
   private int[] branchesToImport;

   ImportController(File importFile, Options options, int... branchesToImport) {
      this.importFile = importFile;
      this.options = options;
      this.branchesToImport = branchesToImport;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.core.transaction.DbTransaction#handleTxWork(java.sql.Connection)
    */
   @Override
   protected void handleTxWork(Connection connection) throws Exception {
      Translator translator = new Translator();
      translator.configure(options);

      ZipFile zipFile = null;
      try {
         zipFile = new ZipFile(importFile);
         ManifestSaxHandler manifestHandler = new ManifestSaxHandler();
         processImportFile(zipFile, "export.manifest.xml", manifestHandler);

         MetaDataSaxHandler metadataHandler = new MetaDataSaxHandler();
         processImportFile(zipFile, manifestHandler.getMetadataFile(), metadataHandler);

         BranchDataSaxHandler branchDataHandler = BranchDataSaxHandler.newCacheAllDataBranchDataSaxHandler();
         processImportFile(zipFile, manifestHandler.getBranchFile(), branchDataHandler);

         if (branchesToImport != null && branchesToImport.length > 0) {
            if (!branchDataHandler.areAvailable(branchesToImport)) {
               throw new IllegalArgumentException(String.format(
                     "Branches not found in import file:\n\t\t- selected to import: [%s]\n\t\t- in import file: [%s]",
                     branchesToImport, branchDataHandler.getBranchData()));
            }
         }

         RelationalTypeCheckSaxHandler typeCheckHandler =
               RelationalTypeCheckSaxHandler.newCacheAllDataRelationalTypeCheckSaxHandler();

         RelationalSaxHandler relationalSaxHandler = RelationalSaxHandler.newCacheAllDataRelationalSaxHandler();
         relationalSaxHandler.setBranchesToImport(branchesToImport);

         processImportFiles(connection, zipFile, metadataHandler, translator, manifestHandler.getTypeFiles(),
               typeCheckHandler);
         processImportFiles(connection, zipFile, metadataHandler, translator, manifestHandler.getImportFiles(),
               relationalSaxHandler);

      } finally {
         translator.cleanUp();
         if (zipFile != null) {
            zipFile.close();
         }
      }
   }

   private void processImportFiles(Connection connection, ZipFile zipFile, MetaDataSaxHandler metaData, Translator translator, Collection<ImportFile> importFiles, BaseDbSaxHandler handler) throws Exception {
      handler.setTranslator(translator);
      handler.setConnection(connection);
      for (ImportFile item : importFiles) {
         MetaData metadata = metaData.getMetadata(item.getSource());
         handler.setMetaData(metadata);
         processImportFile(zipFile, item.getFileName(), handler);
         handler.store();
         handler.reset();
      }
   }

   private void processImportFile(ZipFile zipFile, String fileToProcess, ContentHandler handler) throws Exception {
      InputStream inputStream = null;
      try {
         ZipEntry entry = zipFile.getEntry(fileToProcess);
         inputStream = new BufferedInputStream(zipFile.getInputStream(entry));
         XMLReader reader = XMLReaderFactory.createXMLReader();
         reader.setContentHandler(handler);
         reader.parse(new InputSource(inputStream));
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
   }

}
