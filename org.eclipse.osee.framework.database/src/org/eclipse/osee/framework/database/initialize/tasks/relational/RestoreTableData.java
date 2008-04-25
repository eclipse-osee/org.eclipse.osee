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
package org.eclipse.osee.framework.database.initialize.tasks.relational;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.database.data.SchemaData;
import org.eclipse.osee.framework.database.data.TableElement;
import org.eclipse.osee.framework.database.initialize.tasks.IDbInitializationTask;
import org.eclipse.osee.framework.database.sql.SqlFactory;
import org.eclipse.osee.framework.database.sql.SqlManager;
import org.eclipse.osee.framework.database.utility.DatabaseDataImporter;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;

public class RestoreTableData implements IDbInitializationTask {
   private Set<String> schemas;
   private Map<String, SchemaData> userSpecifiedConfig;
   private SupportedDatabase databaseType;
   private static final File backupDirectory = new File("backupDirectory");

   public RestoreTableData(Set<String> schemas, Map<String, SchemaData> userSpecifiedConfig, SupportedDatabase databaseType) {
      this.schemas = schemas;
      this.userSpecifiedConfig = userSpecifiedConfig;
      this.databaseType = databaseType;
   }

   public void run(Connection connection) throws Exception {
      System.out.println("RestoreTables");
      System.out.flush();
      SqlManager sqlManager = SqlFactory.getSqlManager(databaseType);

      for (String schemaKey : schemas) {
         if (userSpecifiedConfig.containsKey(schemaKey)) {
            DatabaseDataImporter importer = new DatabaseDataImporter(connection, backupDirectory, sqlManager);
            SchemaData schemaData = userSpecifiedConfig.get(schemaKey);

            setImportOrder(importer, schemaData);
            setTablesToImport(importer, schemaData);
            importer.setSchemaToImportTo(schemaKey);

            importer.importDataIntoDatabase();
         }
      }

      clearBackupDirectory();
   }

   private void setImportOrder(DatabaseDataImporter importer, SchemaData schemaData) {
      List<String> importOrder = new ArrayList<String>();

      List<TableElement> tables = schemaData.getTablesOrderedByDependency();
      for (TableElement table : tables) {
         importOrder.add(table.getFullyQualifiedTableName());
      }
      importer.setImportOrder(importOrder);
   }

   private void setTablesToImport(DatabaseDataImporter importer, SchemaData schemaData) {
      importer.clearTableFilter();
      Set<String> selectedTables = schemaData.getTablesToBackup();
      for (String tableName : selectedTables) {
         importer.addToTableFilter(tableName);
      }
      Map<String, Set<String>> importedTables = schemaData.getTablesToImport();
      Set<String> keys = importedTables.keySet();
      for (String key : keys) {
         Set<String> tables = importedTables.get(key);
         for (String tableName : tables) {
            importer.addToTableFilter(tableName);
         }
      }
   }

   public boolean canRun() {
      return true;
   }

   private void clearBackupDirectory() {
      if (backupDirectory != null && backupDirectory.exists() && backupDirectory.canWrite()) {
         File[] fileList = backupDirectory.listFiles();
         for (File fileToDelete : fileList) {
            fileToDelete.delete();
         }
         backupDirectory.delete();
      }
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
    * @see org.eclipse.osee.framework.database.initialize.tasks.IDbInitializationTask#setDependancies(java.util.List)
    */
   public void setDependancies(List<String> bundles) {
   }

   /* (non-Javadoc)
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   public int compareTo(Object o) {
      return 0;
   }
}
