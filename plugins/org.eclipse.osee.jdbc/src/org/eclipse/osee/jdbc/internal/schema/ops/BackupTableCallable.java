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
package org.eclipse.osee.jdbc.internal.schema.ops;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcLogger;
import org.eclipse.osee.jdbc.internal.schema.DatabaseCallable;
import org.eclipse.osee.jdbc.internal.schema.data.SchemaData;
import org.eclipse.osee.jdbc.internal.schema.data.TableElement;

/**
 * @author Roberto E. Escobar
 */
public class BackupTableCallable extends DatabaseCallable<Void> {
   private final File backupDirectory;
   private final Set<String> schemas;
   private final Map<String, SchemaData> userSpecifiedConfig;
   private final Map<String, SchemaData> currentDatabaseConfig;

   public BackupTableCallable(JdbcLogger logger, JdbcClient client, String backupDirPath, Set<String> schemas, Map<String, SchemaData> userSpecifiedConfig, Map<String, SchemaData> currentDatabaseConfig) {
      super(logger, client);
      this.schemas = schemas;
      this.userSpecifiedConfig = userSpecifiedConfig;
      this.currentDatabaseConfig = currentDatabaseConfig;
      this.backupDirectory = new File("BackupDirectory");
   }

   @Override
   public Void call() throws Exception {
      Set<String> dataToBackup = getTablesToBackup();
      if (!dataToBackup.isEmpty()) {
         clearBackupDirectory();
         DatabaseDataExtractorCallable dbDataExtractor =
            new DatabaseDataExtractorCallable(getLogger(), getJdbcClient(), schemas, backupDirectory);
         Set<String> backupTables = dataToBackup;
         for (String backupTable : backupTables) {
            dbDataExtractor.addTableNameToExtract(backupTable);
         }
         dbDataExtractor.waitForWorkerThreads();
      }
      return null;
   }

   private Set<String> getTablesToBackup() {
      Set<String> backupTables = new TreeSet<String>();
      Set<String> userSchemas = userSpecifiedConfig.keySet();
      for (String key : userSchemas) {
         // Backup data only if data exists in the current database
         if (currentDatabaseConfig.containsKey(key)) {
            SchemaData schemaDataInDb = currentDatabaseConfig.get(key);
            Map<String, TableElement> currentDbTableMap = schemaDataInDb.getTableMap();
            Set<String> currentDbTableNames = currentDbTableMap.keySet();

            SchemaData schemaData = userSpecifiedConfig.get(key);
            Set<String> tableNamesToBackup = schemaData.getTablesToBackup();
            for (String tableName : tableNamesToBackup) {
               // Check that table we want to backup exists in the database
               // before we add it to the list
               if (currentDbTableNames.contains(tableName)) {
                  backupTables.add(tableName);
               } else {
                  error("Table doesn't exist in Db. Unable to backup [%s]", tableName);
               }
            }
         } else {
            error("Schema doesn't exist in Db. Unable to backup tables from schema [%s]", key);
         }
      }
      return backupTables;
   }

   private void clearBackupDirectory() {
      if (backupDirectory != null && backupDirectory.exists() && backupDirectory.canWrite()) {
         File[] fileList = backupDirectory.listFiles();
         for (File fileToDelete : fileList) {
            fileToDelete.delete();
         }
         backupDirectory.delete();
         backupDirectory.mkdirs();
      }
   }

}