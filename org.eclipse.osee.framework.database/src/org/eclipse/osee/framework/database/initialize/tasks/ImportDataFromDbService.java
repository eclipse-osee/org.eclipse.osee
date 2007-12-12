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
package org.eclipse.osee.framework.database.initialize.tasks;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.osee.framework.database.utility.DatabaseDataExtractor;
import org.eclipse.osee.framework.database.utility.DatabaseSchemaExtractor;
import org.eclipse.osee.framework.database.utility.FileUtility;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.config.data.DbInformation;
import org.eclipse.osee.framework.ui.plugin.util.db.DBConnection;
import org.eclipse.osee.framework.ui.plugin.util.db.data.SchemaData;
import org.eclipse.osee.framework.ui.plugin.util.db.data.TableElement;

public class ImportDataFromDbService implements IDbInitializationTask {
   private Map<String, SchemaData> userSpecifiedConfig;
   private static final File backupDirectory = new File("BackupDirectory");

   public ImportDataFromDbService(Map<String, SchemaData> userSpecifiedConfig) {
      this.userSpecifiedConfig = userSpecifiedConfig;
   }

   public void run(Connection connection) throws Exception {
      Set<String> importConnections = getImportConnections();
      for (String importFromDbService : importConnections) {
         System.out.println("Import Table Data from Db: " + importFromDbService);

         DbInformation databaseService =
               ConfigUtil.getConfigFactory().getOseeConfig().getDatabaseService(importFromDbService);

         Connection importConnection = null;
         try {
            importConnection = DBConnection.getNewConnection(databaseService, false);
         } catch (SQLException ex) {
            System.out.println("Unable to import table data");
         }
         if (importConnection != null) {
            System.out.println("Gathering information from ..." + importFromDbService);

            String userName = importConnection.getMetaData().getUserName();
            if (userName != null && !userName.equals("")) {

               Set<String> schemasToGet = new TreeSet<String>();
               schemasToGet.add(userName.toUpperCase());

               Map<String, Set<String>> dataToImport =
                     getTablesToImport(importConnection, userName.toUpperCase(), schemasToGet);
               if (dataToImport.size() > 0) {
                  System.out.println(dataToImport.toString().replaceAll(", ", "\n"));
                  makeBackupDirectoryIfItDoesntExist();

                  System.out.println("Backing up Files to: " + backupDirectory.getAbsolutePath());
                  DatabaseDataExtractor dbDataExtractor =
                        new DatabaseDataExtractor(importConnection, schemasToGet, backupDirectory);

                  Set<String> tablesToImport;
                  if (importFromDbService.equals(determineDefaultConnection())) {
                     tablesToImport = dataToImport.get(OseeProperties.OSEE_IMPORT_FROM_DB_SERVICE);
                  } else {
                     tablesToImport = dataToImport.get(importFromDbService);
                  }

                  for (String importTable : tablesToImport) {
                     dbDataExtractor.addTableNameToExtract(importTable);
                  }
                  dbDataExtractor.extract();
                  dbDataExtractor.waitForWorkerThreads();

                  prepareFilesForImport();
               }
            }
            importConnection.close();
         }
      }
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
      String importFromDbService = System.getProperty(OseeProperties.OSEE_IMPORT_FROM_DB_SERVICE);
      if (importFromDbService == null || importFromDbService.equals("")) {
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
         Map<String, Set<String>> tableNamesToImport = schemaDataInUserConfig.getTablesToImport();
         Set<String> keys = tableNamesToImport.keySet();
         for (String connectionString : keys) {
            if (connectionString.equals(OseeProperties.OSEE_IMPORT_FROM_DB_SERVICE)) {
               connectionsNeeded.add(defaultConnection);
            } else {
               connectionsNeeded.add(connectionString);
            }
         }
      }
      return connectionsNeeded;
   }

   public boolean canRun() {
      return true;
   }

   private Map<String, SchemaData> getAvailableSchemasFromImportDb(Connection importConnection, Set<String> schemas) throws SQLException {
      DatabaseSchemaExtractor schemaExtractor = new DatabaseSchemaExtractor(importConnection, schemas);
      schemaExtractor.extractSchemaData();
      return schemaExtractor.getSchemas();
   }

   private Map<String, Set<String>> getTablesToImport(Connection importConnection, String userName, Set<String> schemasToGet) throws Exception {
      Map<String, SchemaData> currentDbSchemas = getAvailableSchemasFromImportDb(importConnection, schemasToGet);
      Set<String> userSchemas = userSpecifiedConfig.keySet();

      SchemaData schemaData = currentDbSchemas.get(userName);
      Map<String, TableElement> tableMap = schemaData.getTableMap();

      Map<String, Set<String>> importTables = new HashMap<String, Set<String>>();
      for (String key : userSchemas) {
         SchemaData schemaDataInUserConfig = userSpecifiedConfig.get(key);
         Map<String, Set<String>> tableNamesToImport = schemaDataInUserConfig.getTablesToImport();

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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.database.initialize.tasks.IDbInitializationTask#setDependancies(java.util.List)
    */
   public void setDependancies(List<String> bundles) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.database.initialize.tasks.IDbInitializationTask#getBundle()
    */
   public String getBundle() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.database.initialize.tasks.IDbInitializationTask#getDependancies()
    */
   public List<String> getDependancies() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.database.initialize.tasks.IDbInitializationTask#setBundle(java.lang.String)
    */
   public void setBundle(String bundle) {
   }

   /* (non-Javadoc)
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   public int compareTo(IDbInitializationTask o) {
      return 0;
   }

   /* (non-Javadoc)
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   public int compareTo(Object o) {
      return 0;
   }
}