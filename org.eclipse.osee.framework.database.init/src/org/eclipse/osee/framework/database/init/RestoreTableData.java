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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public class RestoreTableData implements IDbInitializationTask {
   private Set<String> schemas;
   private Map<String, SchemaData> userSpecifiedConfig;
   private static final File backupDirectory = new File("backupDirectory");

   public RestoreTableData(Set<String> schemas, Map<String, SchemaData> userSpecifiedConfig) {
      this.schemas = schemas;
      this.userSpecifiedConfig = userSpecifiedConfig;
   }

   public void run() throws OseeCoreException {
      System.out.println("RestoreTables");
      System.out.flush();
      SqlManager sqlManager = SqlFactory.getSqlManager();

      for (String schemaKey : schemas) {
         if (userSpecifiedConfig.containsKey(schemaKey)) {
            DatabaseDataImporter importer = new DatabaseDataImporter(backupDirectory, sqlManager);
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
