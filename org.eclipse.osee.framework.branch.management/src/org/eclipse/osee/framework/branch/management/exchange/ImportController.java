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

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.branch.management.ImportOptions;
import org.eclipse.osee.framework.branch.management.exchange.handler.BaseDbSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.BranchDataSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.BranchDefinitionsSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.ManifestSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.MetaData;
import org.eclipse.osee.framework.branch.management.exchange.handler.MetaDataSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.RelationalSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.RelationalTypeCheckSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.ManifestSaxHandler.ImportFile;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.DbTransaction;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.SequenceManager;
import org.eclipse.osee.framework.database.core.SupportedDatabase;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
final class ImportController {
   private static final String SAVE_POINT_PREFIX = "save.point.";
   private static final String EMPTY_STRING = "";

   private static final String INSERT_INTO_IMPORT_SOURCES =
         "INSERT INTO osee_import_source (import_id, db_source_guid, source_export_date, date_imported) VALUES (?, ?, ?, ?)";

   private static final String INSERT_INTO_IMPORT_SAVE_POINT =
         "INSERT INTO osee_import_save_point (import_id, save_point_name, status, state_error) VALUES (?, ?, ?, ?)";

   private static final String QUERY_SAVE_POINTS_FROM_IMPORT_MAP =
         "SELECT save_point_name from osee_import_save_point oisp, osee_import_source ois WHERE ois.import_id = oisp.import_id AND oisp.status = 1 AND ois.db_source_guid = ? AND ois.source_export_date = ?";

   private final IResourceLocator locator;
   private final Options options;
   private final int[] branchesToImport;
   private final Map<String, SavePoint> savePoints;

   private File importSource;
   private TranslationManager translator;
   private ManifestSaxHandler manifestHandler;
   private MetaDataSaxHandler metadataHandler;
   private boolean wasZipExtractionRequired;
   private String currentSavePoint;

   ImportController(IResourceLocator locator, Options options, int... branchesToImport) {
      this.locator = locator;
      this.options = options;
      this.branchesToImport = branchesToImport;
      if (branchesToImport != null && branchesToImport.length > 0) {
         throw new UnsupportedOperationException("selective branch import is not supported.");
      }
      this.wasZipExtractionRequired = false;
      this.savePoints = new LinkedHashMap<String, SavePoint>();
   }

   private void checkPreconditions() throws OseeCoreException {
      if (SupportedDatabase.isDatabaseType(SupportedDatabase.oracle)) {
         throw new OseeStateException("DO NOT IMPORT ON PRODUCTION");
      }
   }

   private void setup() throws Exception {
      currentSavePoint = "sourceSetup";
      Pair<Boolean, File> result = ExchangeUtil.getTempExchangeFile(locator);
      wasZipExtractionRequired = result.getFirst();
      importSource = result.getSecond();

      currentSavePoint = "setup";
      translator = new TranslationManager();
      translator.configure(options);

      // Process manifest
      currentSavePoint = "manifest";
      manifestHandler = new ManifestSaxHandler();
      ExchangeUtil.readExchange(importSource, "export.manifest.xml", manifestHandler);

      // Process database meta data
      currentSavePoint = manifestHandler.getMetadataFile();
      metadataHandler = new MetaDataSaxHandler();
      ExchangeUtil.readExchange(importSource, manifestHandler.getMetadataFile(), metadataHandler);
      metadataHandler.checkAndLoadTargetDbMetadata();

      // Load Import Indexes
      currentSavePoint = "load.translator";
      translator.loadTranslators(manifestHandler.getSourceDatabaseId());

      loadImportTrace(manifestHandler.getSourceDatabaseId(), manifestHandler.getSourceExportDate());
   }

   private void cleanup() throws Exception {
      try {
         CommitImportSavePointsTx saveImportState = new CommitImportSavePointsTx();
         saveImportState.execute();
      } catch (Exception ex) {
         OseeLog.log(this.getClass(), Level.WARNING,
               "Error during save point save - you will not be able to reimport from last source again.");
         throw ex;
      } finally {
         ExchangeUtil.cleanUpTempExchangeFile(importSource, wasZipExtractionRequired);
         translator = null;
         manifestHandler = null;
         metadataHandler = null;
         wasZipExtractionRequired = false;
         importSource = null;
         savePoints.clear();
      }
   }

   public void execute() throws Exception {
      checkPreconditions();
      savePoints.clear();
      try {
         currentSavePoint = "start";
         addSavePoint(currentSavePoint);
         setup();

         ImportBranchesTx importBranchesTx = new ImportBranchesTx();
         importBranchesTx.execute();

         currentSavePoint = "init.relational.objects";
         RelationalTypeCheckSaxHandler typeCheckHandler = RelationalTypeCheckSaxHandler.createWithLimitedCache(50000);
         RelationalSaxHandler relationalSaxHandler = RelationalSaxHandler.createWithLimitedCache(50000);
         relationalSaxHandler.setSelectedBranchIds(branchesToImport);

         processImportFiles(manifestHandler.getTypeFiles(), typeCheckHandler);
         processImportFiles(manifestHandler.getImportFiles(), relationalSaxHandler);

         importBranchesTx.updateBranchParentTransactionId();

         currentSavePoint = "stop";
         addSavePoint(currentSavePoint);
      } catch (Throwable ex) {
         reportError(currentSavePoint, ex);
         OseeLog.log(this.getClass(), Level.SEVERE, ex);
      } finally {
         cleanup();
      }
   }

   private void initializeHandler(OseeConnection connection, BaseDbSaxHandler handler, MetaData metadata) {
      handler.setConnection(connection);
      handler.setMetaData(metadata);
      handler.setOptions(options);
      handler.setTranslator(translator);
   }

   private void process(BaseDbSaxHandler handler, OseeConnection connection, ImportFile importSourceFile) throws OseeCoreException {
      MetaData metadata = checkMetadata(importSourceFile);
      initializeHandler(connection, handler, metadata);
      if (importSourceFile.getPriority() > 0) {
         boolean cleanDataTable = options.getBoolean(ImportOptions.CLEAN_BEFORE_IMPORT.name());
         cleanDataTable &= !doesSavePointExist(currentSavePoint);
         OseeLog.log(this.getClass(), Level.INFO, String.format("Importing: [%s] %s Meta: %s",
               importSourceFile.getSource(), cleanDataTable ? "clean before import" : "", metadata.getColumnNames()));
         if (cleanDataTable) {
            handler.clearDataTable();
         }
      }
      ExchangeUtil.readExchange(importSource, importSourceFile.getFileName(), handler);
   }

   private MetaData checkMetadata(ImportFile importFile) {
      MetaData metadata = metadataHandler.getMetadata(importFile.getSource());
      if (metadata == null) {
         throw new IllegalStateException(String.format("Invalid metadata for [%s]", importFile.getSource()));
      }
      return metadata;
   }

   private void processImportFiles(Collection<ImportFile> importFiles, final RelationalSaxHandler handler) throws Exception {
      handler.setDecompressedFolder(importSource);
      for (final ImportFile item : importFiles) {
         currentSavePoint = item.getSource();
         if (!doesSavePointExist(currentSavePoint)) {
            DbTransaction importTx = new DbTransaction() {
               @Override
               protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
                  process(handler, connection, item);
                  handler.store();
                  handler.reset();
                  addSavePoint(currentSavePoint);
               }
            };
            importTx.execute();
         } else {
            OseeLog.log(this.getClass(), Level.INFO, String.format("Save point found for: [%s] - skipping",
                  item.getSource()));
         }
      }
   }

   private void loadImportTrace(String sourceDatabaseId, Date sourceExportDate) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         currentSavePoint = "load.save.points";
         chStmt.runPreparedQuery(QUERY_SAVE_POINTS_FROM_IMPORT_MAP, sourceDatabaseId, new Timestamp(
               sourceExportDate.getTime()));
         while (chStmt.next()) {
            String key = chStmt.getString("save_point_name");
            savePoints.put(key, new SavePoint(key));
         }
         addSavePoint(currentSavePoint);
      } finally {
         chStmt.close();
      }
   }

   private String asSavePointName(String sourceName) {
      return SAVE_POINT_PREFIX + sourceName;
   }

   private boolean doesSavePointExist(String sourceName) {
      return savePoints.containsKey(asSavePointName(sourceName));
   }

   private void addSavePoint(String sourceName) {
      String key = asSavePointName(sourceName);
      SavePoint point = savePoints.get(key);
      if (point == null) {
         point = new SavePoint(key);
         savePoints.put(key, point);
      }
   }

   private void reportError(String sourceName, Throwable ex) {
      String key = asSavePointName(sourceName);
      SavePoint point = savePoints.get(key);
      if (point == null) {
         point = new SavePoint(key);
         savePoints.put(key, point);
      }
      point.addError(ex);
   }

   private final class ImportBranchesTx extends DbTransaction {

      private final BranchDataSaxHandler branchHandler;
      private int[] branchesStored;

      /**
       * @throws OseeStateException
       */
      public ImportBranchesTx() throws OseeCoreException {
         super();
         branchHandler = BranchDataSaxHandler.createWithCacheAll();
         branchesStored = new int[0];
      }

      public void updateBranchParentTransactionId() throws OseeDataStoreException {
         currentSavePoint = "update branch parent transaction id";
         if (!doesSavePointExist(currentSavePoint)) {
            if (branchesStored.length > 0) {
               branchHandler.updateParentTransactionId(branchesStored);
            }
         } else {
            OseeLog.log(this.getClass(), Level.INFO, String.format("Save point found for: [%s] - skipping",
                  currentSavePoint));
         }
      }

      @Override
      protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
         // Import Branches
         currentSavePoint = manifestHandler.getBranchFile().getSource();
         process(branchHandler, connection, manifestHandler.getBranchFile());

         if (!doesSavePointExist(currentSavePoint)) {
            branchesStored = branchHandler.store(true, branchesToImport);
            addSavePoint(currentSavePoint);
         } else {
            // This step has already been performed - only get branches needed for remaining operations
            OseeLog.log(this.getClass(), Level.INFO, String.format("Save point found for: [%s] - skipping",
                  currentSavePoint));
            branchesStored = branchHandler.store(false, branchesToImport);
         }

         // Import Branch Definitions
         currentSavePoint = manifestHandler.getBranchDefinitionsFile().getSource();
         if (!doesSavePointExist(currentSavePoint)) {
            BranchDefinitionsSaxHandler definitionsHandler = BranchDefinitionsSaxHandler.createWithCacheAll();
            definitionsHandler.setStoredBranches(branchesStored);
            process(definitionsHandler, connection, manifestHandler.getBranchDefinitionsFile());
            definitionsHandler.store();
            addSavePoint(currentSavePoint);
         } else {
            OseeLog.log(this.getClass(), Level.INFO, String.format("Save point found for: [%s] - skipping",
                  currentSavePoint));
         }
      }

   }

   private final class CommitImportSavePointsTx extends DbTransaction {

      /**
       * @throws OseeStateException
       */
      public CommitImportSavePointsTx() throws OseeCoreException {
         super();
      }

      @Override
      protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
         if (manifestHandler != null && translator != null) {
            int importIdIndex = SequenceManager.getNextImportId();
            String sourceDatabaseId = manifestHandler.getSourceDatabaseId();
            Timestamp importDate = new Timestamp(new Date().getTime());
            Timestamp exportDate = new Timestamp(manifestHandler.getSourceExportDate().getTime());
            ConnectionHandler.runPreparedUpdate(connection, INSERT_INTO_IMPORT_SOURCES, importIdIndex,
                  sourceDatabaseId, exportDate, importDate);

            ConnectionHandler.deferConstraintChecking(connection);
            translator.store(connection, importIdIndex);

            List<Object[]> data = new ArrayList<Object[]>();
            for (SavePoint savePoint : savePoints.values()) {
               int status = 1;
               String comment = EMPTY_STRING;
               if (savePoint.hasErrors()) {
                  status = -1;
                  StringBuilder builder = new StringBuilder();
                  for (Throwable ex : savePoint.getErrors()) {
                     builder.append(Lib.exceptionToString(ex).replaceAll("\n", " "));
                  }
                  if (builder.length() < 4000) {
                     comment = builder.toString();
                  } else {
                     comment = builder.substring(0, 3999);
                  }
               }
               data.add(new Object[] {importIdIndex, savePoint.getName(), status, comment});
            }
            ConnectionHandler.runBatchUpdate(connection, INSERT_INTO_IMPORT_SAVE_POINT, data);
         } else {
            throw new OseeStateException("Import didn't make it past initialization");
         }
      }
   }

   private final class SavePoint {
      private final String savePointName;
      private List<Throwable> errors;

      public SavePoint(String name) {
         this.savePointName = name;
         this.errors = null;
      }

      public String getName() {
         return savePointName;
      }

      public void addError(Throwable ex) {
         if (errors == null) {
            errors = new ArrayList<Throwable>();
         }
         if (!errors.contains(ex)) {
            errors.add(ex);
         }
      }

      public List<Throwable> getErrors() {
         if (errors == null) {
            return Collections.emptyList();
         } else {
            return this.errors;
         }
      }

      public boolean hasErrors() {
         return errors != null;
      }
   }
}
