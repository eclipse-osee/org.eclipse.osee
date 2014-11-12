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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcLogger;
import org.eclipse.osee.jdbc.internal.schema.DatabaseCallable;
import org.eclipse.osee.jdbc.internal.schema.data.SchemaData;
import org.eclipse.osee.jdbc.internal.schema.data.TableElement;
import org.eclipse.osee.jdbc.internal.schema.util.FileUtility;

/**
 * @author Roberto E. Escobar
 */
public class ImportDataFromDbCallable extends DatabaseCallable<Void> {

   private static final File backupDirectory = new File("BackupDirectory");

   private final Map<String, SchemaData> userSpecifiedConfig;
   private final String tableImportSource;

   public ImportDataFromDbCallable(JdbcLogger logger, JdbcClient client, Map<String, SchemaData> userSpecifiedConfig, String tableImportSource) {
      super(logger, client);
      this.userSpecifiedConfig = userSpecifiedConfig;
      this.tableImportSource = tableImportSource;
   }

   @Override
   public Void call() throws Exception {
      Set<String> importConnections = getImportConnections();
      for (String importFromDbService : importConnections) {
         getLogger().info("Import Table Data from Db: [%s]", importFromDbService);
         getLogger().info("Gathering information from ... [%s]", importFromDbService);

         String userName = getJdbcClient().getConfig().getDbUsername();
         if (Strings.isValid(userName)) {

            Set<String> schemasToGet = new TreeSet<String>();
            schemasToGet.add(userName.toUpperCase());

            Map<String, Set<String>> dataToImport = getTablesToImport(userName.toUpperCase(), schemasToGet);
            if (dataToImport.size() > 0) {
               getLogger().info(dataToImport.toString().replaceAll(", ", "\n"));
               makeBackupDirectoryIfItDoesntExist();

               getLogger().info("Backing up Files to: [%s]", backupDirectory.getAbsolutePath());
               DatabaseDataExtractorCallable dbDataExtractor =
                  new DatabaseDataExtractorCallable(getLogger(), getJdbcClient(), schemasToGet, backupDirectory);

               Set<String> tablesToImport;
               if (importFromDbService.equals(determineDefaultConnection())) {
                  tablesToImport = dataToImport.get(tableImportSource);
               } else {
                  tablesToImport = dataToImport.get(importFromDbService);
               }

               for (String importTable : tablesToImport) {
                  dbDataExtractor.addTableNameToExtract(importTable);
               }
               dbDataExtractor.call();
               dbDataExtractor.waitForWorkerThreads();

               prepareFilesForImport();
            }
         }
      }
      return null;
   }

   private void prepareFilesForImport() {
      Set<String> keys = userSpecifiedConfig.keySet();
      if (keys.size() == 1) {
         String userName = "";
         for (String temp : keys) {
            userName = temp;
         }
         List<File> files = FileUtility.getDBDataFileList(backupDirectory);
         for (File fileName : files) {
            String filename = fileName.getAbsolutePath().toString();
            filename = filename.substring(filename.lastIndexOf(File.separator) + 1, filename.length());
            filename = filename.substring(filename.indexOf(".") + 1, filename.length());
            fileName.renameTo(new File(backupDirectory + File.separator + userName + "." + filename));
         }
      }
   }

   private String determineDefaultConnection() {
      String importFromDbService = System.getProperty(tableImportSource);
      if (!Strings.isValid(importFromDbService)) {
         importFromDbService = "oracle";
      }
      return importFromDbService;
   }

   private Set<String> getImportConnections() {
      String defaultConnection = determineDefaultConnection();
      Set<String> userSchemas = userSpecifiedConfig.keySet();
      Set<String> connectionsNeeded = new TreeSet<String>();
      for (String key : userSchemas) {
         SchemaData schemaDataInUserConfig = userSpecifiedConfig.get(key);
         Map<String, Set<String>> tableNamesToImport = schemaDataInUserConfig.getTablesToImport(tableImportSource);
         Set<String> keys = tableNamesToImport.keySet();
         for (String connectionString : keys) {
            if (connectionString.equals(tableImportSource)) {
               connectionsNeeded.add(defaultConnection);
            } else {
               connectionsNeeded.add(connectionString);
            }
         }
      }
      return connectionsNeeded;
   }

   private Map<String, SchemaData> getAvailableSchemasFromImportDb(Set<String> schemas) throws Exception {
      Map<String, SchemaData> schemaMap = new HashMap<String, SchemaData>();
      ExtractSchemaCallable schemaExtractor = new ExtractSchemaCallable(getJdbcClient(), schemas, schemaMap);
      schemaExtractor.call();
      return schemaMap;
   }

   private Map<String, Set<String>> getTablesToImport(String userName, Set<String> schemasToGet) throws Exception {
      Map<String, SchemaData> currentDbSchemas = getAvailableSchemasFromImportDb(schemasToGet);
      Set<String> userSchemas = userSpecifiedConfig.keySet();

      SchemaData schemaData = currentDbSchemas.get(userName);
      Map<String, TableElement> tableMap = schemaData.getTableMap();

      Map<String, Set<String>> importTables = new HashMap<String, Set<String>>();
      for (String key : userSchemas) {
         SchemaData schemaDataInUserConfig = userSpecifiedConfig.get(key);
         Map<String, Set<String>> tableNamesToImport = schemaDataInUserConfig.getTablesToImport(tableImportSource);

         Set<String> keys = tableNamesToImport.keySet();
         for (String importKey : keys) {
            Set<String> namesToImport = tableNamesToImport.get(importKey);

            for (String tableName : namesToImport) {
               tableName = tableName.replaceAll(key + "\\.", userName + ".");

               if (tableMap.containsKey(tableName)) {
                  Set<String> tableSet;
                  if (importTables.containsKey(importKey)) {
                     tableSet = importTables.get(importKey);
                  } else {
                     tableSet = new TreeSet<String>();
                  }
                  tableSet.add(tableName);
                  importTables.put(importKey, tableSet);
               }
            }
         }
      }
      return importTables;
   }

   private void makeBackupDirectoryIfItDoesntExist() {
      if (backupDirectory != null && backupDirectory.exists() && backupDirectory.canWrite()) {
         return;
      } else {
         backupDirectory.mkdirs();
      }
   }

}