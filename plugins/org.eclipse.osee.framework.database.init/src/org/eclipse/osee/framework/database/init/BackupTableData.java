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
package org.eclipse.osee.framework.database.init;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public class BackupTableData implements IDbInitializationTask {
   private Set<String> schemas;
   private Map<String, SchemaData> userSpecifiedConfig;
   private Map<String, SchemaData> currentDatabaseConfig;
   private static final File backupDirectory = new File("BackupDirectory");

   public BackupTableData(Set<String> schemas, Map<String, SchemaData> userSpecifiedConfig, Map<String, SchemaData> currentDatabaseConfig) {
      this.schemas = schemas;
      this.userSpecifiedConfig = userSpecifiedConfig;
      this.currentDatabaseConfig = currentDatabaseConfig;
   }

   public void run() throws OseeCoreException {
      System.out.println("BackupTableData: ");
      Set<String> dataToBackup = getTablesToBackup();
      if (dataToBackup.size() > 0) {
         System.out.println(dataToBackup.toString().replaceAll(", ", "\n"));
         clearBackupDirectory();
         DatabaseDataExtractor dbDataExtractor = new DatabaseDataExtractor(schemas, backupDirectory);
         Set<String> backupTables = dataToBackup;
         for (String backupTable : backupTables) {
            dbDataExtractor.addTableNameToExtract(backupTable);
         }
         dbDataExtractor.extract();
         dbDataExtractor.waitForWorkerThreads();
      }
   }

   public boolean canRun() {
      return true;
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
                  System.out.println("Table doesn't exist in Db. Unable to backup [" + tableName + "]");
               }
            }
         } else {
            System.out.println("Schema doesn't exist in Db. Unable to backup tables from schema [" + key + "]");
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

   public String getBundle() {
      return null;
   }

   public List<String> getDependancies() {
      return null;
   }

   public void setBundle(String bundle) {
   }

   public void setDependancies(List<String> bundles) {
   }

   public int compareTo(Object o) {
      return 0;
   }
}