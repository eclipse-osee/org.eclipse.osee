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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.database.IDbInitializationTask;
import org.eclipse.osee.framework.database.data.SchemaData;
import org.eclipse.osee.framework.database.data.TableElement;
import org.eclipse.osee.framework.database.utility.DatabaseDataExtractor;
import org.eclipse.osee.framework.database.utility.DatabaseSchemaExtractor;
import org.eclipse.osee.framework.database.utility.FileUtility;
import org.eclipse.osee.framework.db.connection.DatabaseInfoManager;
import org.eclipse.osee.framework.db.connection.IDatabaseInfo;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public class ImportDataFromDbService implements IDbInitializationTask {
   private final Map<String, SchemaData> userSpecifiedConfig;
   private static final File backupDirectory = new File("BackupDirectory");

   public ImportDataFromDbService(Map<String, SchemaData> userSpecifiedConfig) {
      this.userSpecifiedConfig = userSpecifiedConfig;
   }

   public void run(OseeConnection connection) throws OseeCoreException {
      Set<String> importConnections = getImportConnections();
      for (String importFromDbService : importConnections) {
         System.out.println("Import Table Data from Db: " + importFromDbService);

         IDatabaseInfo dbInfo = DatabaseInfoManager.getDataStoreById(importFromDbService);
         OseeConnection importConnection = OseeDbConnection.getConnection(dbInfo);
         if (importConnection != null) {
            try {
               System.out.println("Gathering information from ..." + importFromDbService);

               String userName = dbInfo.getDatabaseLoginName();
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
                        tablesToImport = dataToImport.get(OseeClientProperties.getTableImportSource());
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
            } finally {
               importConnection.close();
            }
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
      String importFromDbService = System.getProperty(OseeClientProperties.getTableImportSource());
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
         Map<String, Set<String>> tableNamesToImport = schemaDataInUserConfig.getTablesToImport();
         Set<String> keys = tableNamesToImport.keySet();
         for (String connectionString : keys) {
            if (connectionString.equals(OseeClientProperties.getTableImportSource())) {
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

   private Map<String, SchemaData> getAvailableSchemasFromImportDb(Connection importConnection, Set<String> schemas) throws OseeDataStoreException {
      DatabaseSchemaExtractor schemaExtractor = new DatabaseSchemaExtractor(importConnection, schemas);
      schemaExtractor.extractSchemaData();
      return schemaExtractor.getSchemas();
   }

   private Map<String, Set<String>> getTablesToImport(Connection importConnection, String userName, Set<String> schemasToGet) throws OseeDataStoreException {
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