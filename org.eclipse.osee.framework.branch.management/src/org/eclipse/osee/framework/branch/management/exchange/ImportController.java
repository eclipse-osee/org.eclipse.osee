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
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.osee.framework.branch.management.Activator;
import org.eclipse.osee.framework.branch.management.ImportOptions;
import org.eclipse.osee.framework.branch.management.exchange.handler.BaseDbSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.BranchDataSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.BranchDefinitionsSaxHandler;
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
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.Options;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Roberto E. Escobar
 */
final class ImportController extends DbTransaction {

   private final IResourceLocator locator;
   private final Options options;
   private final int[] branchesToImport;

   ImportController(IResourceLocator locator, Options options, int... branchesToImport) {
      this.locator = locator;
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
         IResource resource = Activator.getInstance().getResourceManager().acquire(locator, new Options());
         zipFile = new ZipFile(new File(resource.getLocation()));

         // Process manifest
         ManifestSaxHandler manifestHandler = new ManifestSaxHandler();
         processImportFile(zipFile, "export.manifest.xml", manifestHandler);

         // Process database meta data
         MetaDataSaxHandler metadataHandler = new MetaDataSaxHandler();
         processImportFile(zipFile, manifestHandler.getMetadataFile(), metadataHandler);
         metadataHandler.checkAndLoadTargetDbMetadata(connection);

         // Load Import Indexes
         translator.loadTranslators(connection, manifestHandler.getSourceDatabaseId());

         // Import Branches
         BranchDataSaxHandler branchHandler = BranchDataSaxHandler.createWithCacheAll();
         process(branchHandler, connection, zipFile, manifestHandler.getBranchFile(), metadataHandler, translator);
         int[] branchesStored = branchHandler.store(branchesToImport);

         // Import Branch Definitions
         BranchDefinitionsSaxHandler definitionsHandler = BranchDefinitionsSaxHandler.createWithCacheAll();
         definitionsHandler.setStoredBranches(branchesStored);
         process(definitionsHandler, connection, zipFile, manifestHandler.getBranchDefinitionsFile(), metadataHandler,
               translator);
         definitionsHandler.store();

         // Type Checks
         RelationalTypeCheckSaxHandler typeCheckHandler = RelationalTypeCheckSaxHandler.createWithLimitedCache(1000);
         processImportFiles(connection, zipFile, metadataHandler, translator, manifestHandler.getTypeFiles(),
               typeCheckHandler);

         // Data Table Imports
         RelationalSaxHandler relationalSaxHandler = RelationalSaxHandler.createWithLimitedCache(1000);
         relationalSaxHandler.setSelectedBranchIds(branchesToImport);
         processImportFiles(connection, zipFile, metadataHandler, translator, manifestHandler.getImportFiles(),
               relationalSaxHandler);

         // Store Import Index Translations
         translator.storeImport(connection, manifestHandler.getSourceDatabaseId(),
               manifestHandler.getSourceExportDate());
      } finally {
         if (zipFile != null) {
            zipFile.close();
         }
      }
   }

   private void initializeHandler(Connection connection, BaseDbSaxHandler handler, MetaData metadata, Translator translator) {
      handler.setConnection(connection);
      handler.setMetaData(metadata);
      handler.setOptions(options);
      handler.setTranslator(translator);
   }

   private void process(BaseDbSaxHandler handler, Connection connection, ZipFile zipFile, ImportFile importSourceFile, MetaDataSaxHandler metadataHandler, Translator translator) throws Exception {
      MetaData metadata = checkMetadata(metadataHandler, importSourceFile);
      initializeHandler(connection, handler, metadata, translator);
      processImportFile(zipFile, importSourceFile.getFileName(), handler);
   }

   private MetaData checkMetadata(MetaDataSaxHandler metadataHandler, ImportFile importFile) {
      MetaData metadata = metadataHandler.getMetadata(importFile.getSource());
      if (metadata == null) {
         throw new IllegalStateException(String.format("Invalid metadata for [%s]", importFile.getSource()));
      }
      return metadata;
   }

   private void processImportFiles(Connection connection, ZipFile zipFile, MetaDataSaxHandler metaHandler, Translator translator, Collection<ImportFile> importFiles, RelationalSaxHandler handler) throws Exception {
      handler.setZipFile(zipFile);
      for (ImportFile item : importFiles) {
         MetaData metadata = checkMetadata(metaHandler, item);
         initializeHandler(connection, handler, metadata, translator);
         if (item.getPriority() > 0) {
            boolean cleanDataTable = options.getBoolean(ImportOptions.CLEAN_BEFORE_IMPORT.name());
            OseeLog.log(this.getClass(), Level.INFO, String.format("Importing: [%s] %s Meta: %s", item.getSource(),
                  cleanDataTable ? "clean before import" : "", metadata.getColumnNames()));
            if (cleanDataTable) {
               ConnectionHandler.runPreparedUpdate(connection, String.format("DELETE FROM %s", item.getSource()));
            }
         }
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
