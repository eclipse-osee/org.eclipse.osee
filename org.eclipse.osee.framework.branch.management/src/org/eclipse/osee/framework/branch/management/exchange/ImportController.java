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
import java.sql.SQLException;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.osee.framework.branch.management.exchange.handler.BranchDataSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.ManifestSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.MetaData;
import org.eclipse.osee.framework.branch.management.exchange.handler.MetaDataSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.RelationalSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.RelationalTypeCheckSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.Translator;
import org.eclipse.osee.framework.branch.management.exchange.handler.ManifestSaxHandler.ImportFile;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.core.transaction.DbTransaction;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;
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

      if (SupportedDatabase.getDatabaseType(connection).equals(SupportedDatabase.oracle)) {
         throw new IllegalStateException("DO NOT IMPORT ON PRODUCTION");
      }

      ZipFile zipFile = null;
      try {
         zipFile = new ZipFile(importFile);
         ManifestSaxHandler manifestHandler = new ManifestSaxHandler();
         processImportFile(zipFile, "export.manifest.xml", manifestHandler);

         MetaDataSaxHandler metadataHandler = new MetaDataSaxHandler();
         processImportFile(zipFile, manifestHandler.getMetadataFile(), metadataHandler);

         BranchDataSaxHandler branchDataHandler = BranchDataSaxHandler.newCacheAllDataBranchDataSaxHandler();
         branchDataHandler.setConnection(connection);
         MetaData metadata = metadataHandler.getMetadata(manifestHandler.getBranchFile().getSource());
         if (metadata == null) {
            throw new IllegalStateException("Invalid metadata for branch table");
         }
         branchDataHandler.setTranslator(translator);
         branchDataHandler.setMetaData(metadata);
         processImportFile(zipFile, manifestHandler.getBranchFile().getFileName(), branchDataHandler);
         branchDataHandler.setSelectedBranchIds(branchesToImport);
         branchDataHandler.store();

         RelationalTypeCheckSaxHandler typeCheckHandler =
               RelationalTypeCheckSaxHandler.newLimitedCacheRelationalTypeCheckSaxHandler(1000);
         typeCheckHandler.configure(options);
         typeCheckHandler.setZipFile(zipFile);

         RelationalSaxHandler relationalSaxHandler = RelationalSaxHandler.newLimitedCacheRelationalSaxHandler(1000);
         relationalSaxHandler.setSelectedBranchIds(branchesToImport);
         relationalSaxHandler.configure(options);
         relationalSaxHandler.setZipFile(zipFile);

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

   private void processImportFiles(Connection connection, ZipFile zipFile, MetaDataSaxHandler metaData, Translator translator, Collection<ImportFile> importFiles, RelationalSaxHandler handler) throws Exception {
      for (ImportFile item : importFiles) {
         MetaData metadata = metaData.getMetadata(item.getSource());
         if (metadata == null) {
            throw new IllegalStateException(String.format("Invalid metadata for [%s]", item.getSource()));
         }
         handler.setMetaData(metadata);
         handler.setTranslator(translator);
         handler.setConnection(connection);

         // REMOVE THIS LATER
         if (item.getPriority() > 0) {
            System.out.println(String.format("Importing: [%s] Meta: %s", item.getSource(), metadata.getColumnNames()));
            clearTableData(connection, item.getSource());
         }
         processImportFile(zipFile, item.getFileName(), handler);
         handler.store();
         handler.reset();
      }
   }

   private void clearTableData(Connection connection, String source) throws SQLException {
      ConnectionHandler.runPreparedUpdate(connection, String.format("DELETE FROM %s", source));

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
