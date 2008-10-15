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
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Collection;
import java.util.logging.Level;
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
import org.eclipse.osee.framework.branch.management.exchange.resource.ExchangeProvider;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.core.transaction.DbTransaction;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;
import org.eclipse.osee.framework.jdk.core.util.Lib;
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
   private static final String TEMP_NAME_PREFIX = "branch.imp.xchng.";
   private final IResourceLocator locator;
   private final Options options;
   private final int[] branchesToImport;

   ImportController(IResourceLocator locator, Options options, int... branchesToImport) {
      this.locator = locator;
      this.options = options;
      this.branchesToImport = branchesToImport;
   }

   private File createTempFolder() {
      String basePath = ExchangeProvider.getExchangeFilePath();
      String fileName = TEMP_NAME_PREFIX + Lib.getDateTimeString();
      File rootDirectory = new File(basePath, fileName + File.separator);
      rootDirectory.mkdirs();
      return rootDirectory;
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
      boolean wasExtracted = false;
      File tempZipFolder = null;
      try {
         IResource resource = Activator.getInstance().getResourceManager().acquire(locator, new Options());
         File source = new File(resource.getLocation());
         if (source.isFile()) {
            tempZipFolder = createTempFolder();
            OseeLog.log(this.getClass(), Level.INFO, String.format("Extracting Branch Import File: [%s] to [%s]",
                  source.getName(), tempZipFolder));
            Lib.decompressStream(new FileInputStream(source), tempZipFolder);
            wasExtracted = true;
         } else {
            tempZipFolder = source;
         }
         // Process manifest
         ManifestSaxHandler manifestHandler = new ManifestSaxHandler();
         processImportFile(tempZipFolder, "export.manifest.xml", manifestHandler);

         // Process database meta data
         MetaDataSaxHandler metadataHandler = new MetaDataSaxHandler();
         processImportFile(tempZipFolder, manifestHandler.getMetadataFile(), metadataHandler);
         metadataHandler.checkAndLoadTargetDbMetadata(connection);

         // Load Import Indexes
         translator.loadTranslators(connection, manifestHandler.getSourceDatabaseId());

         // Import Branches
         BranchDataSaxHandler branchHandler = BranchDataSaxHandler.createWithCacheAll();
         process(branchHandler, connection, tempZipFolder, manifestHandler.getBranchFile(), metadataHandler, translator);
         int[] branchesStored = branchHandler.store(branchesToImport);

         // Import Branch Definitions
         BranchDefinitionsSaxHandler definitionsHandler = BranchDefinitionsSaxHandler.createWithCacheAll();
         definitionsHandler.setStoredBranches(branchesStored);
         process(definitionsHandler, connection, tempZipFolder, manifestHandler.getBranchDefinitionsFile(),
               metadataHandler, translator);
         definitionsHandler.store();

         // Type Checks
         RelationalTypeCheckSaxHandler typeCheckHandler = RelationalTypeCheckSaxHandler.createWithLimitedCache(1000);
         processImportFiles(connection, tempZipFolder, metadataHandler, translator, manifestHandler.getTypeFiles(),
               typeCheckHandler);

         // Data Table Imports
         RelationalSaxHandler relationalSaxHandler = RelationalSaxHandler.createWithLimitedCache(1000);
         relationalSaxHandler.setSelectedBranchIds(branchesToImport);
         processImportFiles(connection, tempZipFolder, metadataHandler, translator, manifestHandler.getImportFiles(),
               relationalSaxHandler);

         // Store Import Index Translations
         translator.storeImport(connection, manifestHandler.getSourceDatabaseId(),
               manifestHandler.getSourceExportDate());
      } finally {
         if (wasExtracted && tempZipFolder != null && tempZipFolder.exists() && tempZipFolder.getAbsolutePath() != ExchangeProvider.getExchangeFilePath()) {
            OseeLog.log(this.getClass(), Level.INFO, String.format("Deleting Branch Import Temp Folder - [%s]",
                  tempZipFolder));
            Lib.deleteDir(tempZipFolder);
         }
      }
   }

   private void initializeHandler(Connection connection, BaseDbSaxHandler handler, MetaData metadata, Translator translator) {
      handler.setConnection(connection);
      handler.setMetaData(metadata);
      handler.setOptions(options);
      handler.setTranslator(translator);
   }

   private void process(BaseDbSaxHandler handler, Connection connection, File decompressedFolder, ImportFile importSourceFile, MetaDataSaxHandler metadataHandler, Translator translator) throws Exception {
      MetaData metadata = checkMetadata(metadataHandler, importSourceFile);
      initializeHandler(connection, handler, metadata, translator);
      processImportFile(decompressedFolder, importSourceFile.getFileName(), handler);
   }

   private MetaData checkMetadata(MetaDataSaxHandler metadataHandler, ImportFile importFile) {
      MetaData metadata = metadataHandler.getMetadata(importFile.getSource());
      if (metadata == null) {
         throw new IllegalStateException(String.format("Invalid metadata for [%s]", importFile.getSource()));
      }
      return metadata;
   }

   private void processImportFiles(Connection connection, File decompressedFolder, MetaDataSaxHandler metaHandler, Translator translator, Collection<ImportFile> importFiles, RelationalSaxHandler handler) throws Exception {
      handler.setDecompressedFolder(decompressedFolder);
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
         processImportFile(decompressedFolder, item.getFileName(), handler);
         handler.store();
         handler.reset();
      }
   }

   private void processImportFile(File zipFile, String fileToProcess, ContentHandler handler) throws Exception {
      InputStream inputStream = null;
      try {
         File entry = new File(zipFile, fileToProcess);
         inputStream = new BufferedInputStream(new FileInputStream(entry));
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
