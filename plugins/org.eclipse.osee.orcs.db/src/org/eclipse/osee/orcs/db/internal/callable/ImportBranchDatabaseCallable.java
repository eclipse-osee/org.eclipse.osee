/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.callable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.ImportOptions;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.SystemProperties;
import org.eclipse.osee.orcs.db.internal.IdentityLocator;
import org.eclipse.osee.orcs.db.internal.exchange.ExchangeUtil;
import org.eclipse.osee.orcs.db.internal.exchange.IOseeExchangeDataProvider;
import org.eclipse.osee.orcs.db.internal.exchange.SavePointManager;
import org.eclipse.osee.orcs.db.internal.exchange.StandardOseeDbExportDataProvider;
import org.eclipse.osee.orcs.db.internal.exchange.TranslationManager;
import org.eclipse.osee.orcs.db.internal.exchange.handler.BaseDbSaxHandler;
import org.eclipse.osee.orcs.db.internal.exchange.handler.BranchDataSaxHandler;
import org.eclipse.osee.orcs.db.internal.exchange.handler.DbTableSaxHandler;
import org.eclipse.osee.orcs.db.internal.exchange.handler.ExportItem;
import org.eclipse.osee.orcs.db.internal.exchange.handler.IExportItem;
import org.eclipse.osee.orcs.db.internal.exchange.handler.ManifestSaxHandler;
import org.eclipse.osee.orcs.db.internal.exchange.handler.MetaData;
import org.eclipse.osee.orcs.db.internal.exchange.handler.MetaDataSaxHandler;
import org.eclipse.osee.orcs.db.internal.exchange.transform.ExchangeDataProcessor;
import org.eclipse.osee.orcs.db.internal.exchange.transform.ExchangeTransformProvider;
import org.eclipse.osee.orcs.db.internal.exchange.transform.ExchangeTransformer;
import org.eclipse.osee.orcs.db.internal.exchange.transform.IExchangeTransformProvider;
import org.eclipse.osee.orcs.db.internal.resource.ResourceConstants;

/**
 * @author Roberto E. Escobar
 */
public class ImportBranchDatabaseCallable extends AbstractDatastoreCallable<URI> {

   private final SystemProperties preferences;

   private final IResourceManager resourceManager;
   private final IdentityLocator identityService;
   private final OrcsTypes orcsTypes;

   private final SavePointManager savePointManager;

   private final URI exchangeFile;
   private final List<? extends BranchId> selectedBranches;
   private final PropertyStore options;

   private ExchangeTransformer exchangeTransformer;
   private ManifestSaxHandler manifestHandler;
   private TranslationManager translator;
   private MetaDataSaxHandler metadataHandler;
   private IOseeExchangeDataProvider exportDataProvider;
   private ExchangeDataProcessor exchangeDataProcessor;

   public ImportBranchDatabaseCallable(Log logger, OrcsSession session, JdbcClient jdbcClient, SystemProperties preferences, IResourceManager resourceManager, IdentityLocator identityService, OrcsTypes orcsTypes, URI exchangeFile, List<? extends BranchId> selectedBranches, PropertyStore options) {
      super(logger, session, jdbcClient);
      this.preferences = preferences;
      this.resourceManager = resourceManager;
      this.identityService = identityService;
      this.orcsTypes = orcsTypes;
      this.savePointManager = new SavePointManager(jdbcClient);
      this.exchangeFile = exchangeFile;
      this.selectedBranches = selectedBranches;
      this.options = options;
   }

   @Override
   public URI call() throws Exception {
      URI importedURI = null;

      checkPreconditions();
      savePointManager.clear();
      try {
         savePointManager.setCurrentSetPointId("start");
         savePointManager.addCurrentSavePointToProcessed();

         setup();

         URI modelUri = exportDataProvider.getFile(manifestHandler.getTypeModel()).toURI();
         loadTypeModel(modelUri);

         ImportBranchesTx importBranchesTx = new ImportBranchesTx(getLogger(), getSession(), getJdbcClient(),
            savePointManager, manifestHandler.getBranchFile());
         callAndCheckForCancel(importBranchesTx);

         savePointManager.setCurrentSetPointId("init_relational_objects");
         savePointManager.addCurrentSavePointToProcessed();

         processImportFiles(selectedBranches, manifestHandler.getImportFiles());

         importBranchesTx.updateBaselineAndParentTransactionId();

         exchangeTransformer.applyFinalTransforms();

         savePointManager.setCurrentSetPointId("stop");
         savePointManager.addCurrentSavePointToProcessed();

         importedURI = exportDataProvider.getExportedDataRoot().toURI();
      } catch (Throwable ex) {
         savePointManager.reportError(ex);
         getLogger().error(ex, "Error importing");
      } finally {
         cleanup();
      }
      return importedURI;
   }

   private void checkPreconditions() {
      if (getJdbcClient().getConfig().isProduction()) {
         throw new OseeStateException("DO NOT IMPORT ON PRODUCTION");
      }

   }

   private IResourceLocator findResourceToCheck(URI fileToCheck) {
      IResourceLocator locator = resourceManager.getResourceLocator(fileToCheck.toASCIIString());
      return locator;
   }

   private IOseeExchangeDataProvider createExportDataProvider(IResourceLocator exportDataLocator) {
      String exchangeBasePath = ResourceConstants.getExchangeDataPath(preferences);
      Pair<Boolean, File> result =
         ExchangeUtil.getTempExchangeFile(exchangeBasePath, getLogger(), exportDataLocator, resourceManager);
      return new StandardOseeDbExportDataProvider(exchangeBasePath, getLogger(), result.getSecond(), result.getFirst());
   }

   private void setup() throws Exception {
      IResourceLocator exportDataLocator = findResourceToCheck(exchangeFile);
      exportDataProvider = createExportDataProvider(exportDataLocator);
      exchangeDataProcessor = new ExchangeDataProcessor(exportDataProvider);

      savePointManager.setCurrentSetPointId("sourceSetup");

      IExchangeTransformProvider transformProvider = new ExchangeTransformProvider();
      exchangeTransformer =
         new ExchangeTransformer(getLogger(), getSession(), getJdbcClient(), transformProvider, exchangeDataProcessor);
      exchangeTransformer.applyTransforms();

      savePointManager.setCurrentSetPointId("manifest");
      manifestHandler = new ManifestSaxHandler();
      exchangeDataProcessor.parse(ExportItem.EXPORT_MANIFEST, manifestHandler);

      savePointManager.setCurrentSetPointId("setup");
      translator = new TranslationManager(getJdbcClient());
      translator.configure(options);

      // Process database meta data
      savePointManager.setCurrentSetPointId(manifestHandler.getMetadataFile());
      metadataHandler = new MetaDataSaxHandler(getJdbcClient());
      exchangeDataProcessor.parse(ExportItem.EXPORT_DB_SCHEMA, metadataHandler);
      metadataHandler.checkAndLoadTargetDbMetadata();

      // Load Import Indexes
      savePointManager.setCurrentSetPointId("load.translator");
      translator.loadTranslators(manifestHandler.getSourceDatabaseId());

      savePointManager.loadSavePoints(manifestHandler.getSourceDatabaseId(), manifestHandler.getSourceExportDate());
   }

   private void processImportFiles(Iterable<? extends BranchId> branchesToImport, Collection<IExportItem> importItems) throws Exception {
      final DbTableSaxHandler handler = DbTableSaxHandler.createWithLimitedCache(getLogger(), getJdbcClient(),
         resourceManager, identityService, exportDataProvider, 50000);
      handler.setSelectedBranchIds(branchesToImport);

      for (final IExportItem item : importItems) {
         getLogger().info("starting import for [%s]", item);
         savePointManager.setCurrentSetPointId(item.getSource());
         handler.setExportItem(item);
         if (!savePointManager.isCurrentInProcessed()) {
            process(handler, item);
            handler.store();
            handler.reset();
            savePointManager.addCurrentSavePointToProcessed();
         } else {
            getLogger().info("Save point found for: [%s] - skipping", item.getSource());
         }
      }
   }

   private void process(BaseDbSaxHandler handler, IExportItem exportItem) {
      MetaData metadata = checkMetadata(exportItem);
      handler.setMetaData(metadata);
      handler.setOptions(options);
      handler.setTranslator(translator);
      boolean cleanDataTable = options.getBoolean(ImportOptions.CLEAN_BEFORE_IMPORT.name());
      cleanDataTable &= !savePointManager.isCurrentInProcessed();
      getLogger().info("Importing: [%s] %s Meta: %s", exportItem.getSource(),
         cleanDataTable ? "clean before import" : "", metadata.getColumnNames());
      if (cleanDataTable) {
         handler.clearDataTable();
      }
      try {
         exchangeDataProcessor.parse(exportItem, handler);
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private MetaData checkMetadata(IExportItem importFile) {
      MetaData metadata = metadataHandler.getMetadata(importFile.getSource());
      if (metadata == null) {
         throw new OseeArgumentException("Invalid metadata for [%s]", importFile.getSource());
      }
      return metadata;
   }

   private void cleanup() throws Exception {
      try {
         CommitImportSavePointsTx callable = new CommitImportSavePointsTx(getLogger(), getSession(), getJdbcClient());
         callAndCheckForCancel(callable);
      } catch (Exception ex) {
         getLogger().warn(ex,
            "Error during save point save - you will not be able to reimport from last source again.");
         throw ex;
      } finally {
         exchangeDataProcessor.cleanUp();
         translator = null;
         manifestHandler = null;
         metadataHandler = null;
         exchangeTransformer = null;
         savePointManager.clear();
      }
   }

   private void loadTypeModel(final URI modelUri) throws Exception {
      IResource typesResource = new IResource() {

         @Override
         public InputStream getContent() {
            try {
               URL url = modelUri.toURL();
               return new BufferedInputStream(url.openStream());
            } catch (IOException ex) {
               throw OseeCoreException.wrap(ex);
            }
         }

         @Override
         public URI getLocation() {
            return modelUri;
         }

         @Override
         public String getName() {
            String name = modelUri.toASCIIString();
            int index = name.lastIndexOf("/");
            if (index > 0) {
               name = name.substring(index + 1, name.length());
            }
            return name;
         }

         @Override
         public boolean isCompressed() {
            return false;
         }
      };

      getLogger().info("Updating Type Model with [%s]", typesResource.getLocation());
      orcsTypes.loadTypes(typesResource);
      getLogger().info("Type Model Import complete");
   }
   private final class CommitImportSavePointsTx extends AbstractDatastoreTxCallable<Boolean> {

      private static final String IMPORT_ID_SEQ = "SKYNET_IMPORT_ID_SEQ";
      private static final String INSERT_INTO_IMPORT_SOURCES =
         "INSERT INTO osee_import_source (import_id, db_source_guid, source_export_date, date_imported) VALUES (?, ?, ?, ?)";

      public CommitImportSavePointsTx(Log logger, OrcsSession session, JdbcClient jdbcClient) {
         super(logger, session, jdbcClient);
      }

      @Override
      protected Boolean handleTxWork(JdbcConnection connection) {
         if (manifestHandler != null && translator != null) {
            int importIdIndex = (int) getJdbcClient().getNextSequence(IMPORT_ID_SEQ, true);
            String sourceDatabaseId = manifestHandler.getSourceDatabaseId();
            Timestamp importDate = new Timestamp(new Date().getTime());
            Timestamp exportDate = new Timestamp(manifestHandler.getSourceExportDate().getTime());
            getJdbcClient().runPreparedUpdate(connection, INSERT_INTO_IMPORT_SOURCES, importIdIndex, sourceDatabaseId,
               exportDate, importDate);

            translator.store(connection, importIdIndex);

            savePointManager.storeSavePoints(connection, importIdIndex);
         } else {
            throw new OseeStateException("Import didn't make it past initialization");
         }
         return Boolean.TRUE;
      }
   }

   private final class ImportBranchesTx extends AbstractDatastoreTxCallable<Object> {

      private final SavePointManager savePointManager;
      private final BranchDataSaxHandler branchHandler;
      private final IExportItem branchExportItem;
      private Set<BranchId> branchesStored;

      public ImportBranchesTx(Log logger, OrcsSession session, JdbcClient jdbcClient, SavePointManager savePointManager, IExportItem branchExportItem) {
         super(logger, session, jdbcClient);
         this.savePointManager = savePointManager;
         this.branchExportItem = branchExportItem;
         branchHandler = BranchDataSaxHandler.createWithCacheAll(logger, jdbcClient);
         branchesStored = Collections.emptySet();
      }

      public void updateBaselineAndParentTransactionId() {
         savePointManager.setCurrentSetPointId("update_branch_baseline_parent_tx_ids");
         if (!savePointManager.isCurrentInProcessed()) {
            branchHandler.updateBaselineAndParentTransactionId(branchesStored);
            savePointManager.addCurrentSavePointToProcessed();
         } else {
            getLogger().info("Save point found for: [%s] - skipping", savePointManager.getCurrentSetPointId());
         }
      }

      @Override
      protected Object handleTxWork(JdbcConnection connection) {
         // Import Branches
         savePointManager.setCurrentSetPointId(branchExportItem.getSource());
         branchHandler.setConnection(connection);
         process(branchHandler, branchExportItem);

         if (!savePointManager.isCurrentInProcessed()) {
            branchesStored = branchHandler.store(connection, true, selectedBranches);
            savePointManager.addCurrentSavePointToProcessed();
         } else {
            // This step has already been performed - only get branches needed for remaining operations
            getLogger().info("Save point found for: [%s] - skipping", savePointManager.getCurrentSetPointId());
            branchesStored = branchHandler.store(connection, false, selectedBranches);
         }

         return null;
      }

      @Override
      protected void handleTxFinally() {
         super.handleTxFinally();
         branchHandler.setConnection(null);
      }
   }

}
