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
package org.eclipse.osee.framework.core.datastore.schema.operations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.datastore.internal.Activator;
import org.eclipse.osee.framework.core.datastore.schema.data.SchemaData;
import org.eclipse.osee.framework.core.datastore.schema.data.TableElement;
import org.eclipse.osee.framework.core.datastore.schema.sql.SqlFactory;
import org.eclipse.osee.framework.core.datastore.schema.sql.SqlManager;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.database.core.ConnectionHandler;

/**
 * @author Roberto E. Escobar
 */
public class RestoreTableDataOperation extends AbstractOperation {
   private static final File backupDirectory = new File("backupDirectory");

   private final Set<String> schemas;
   private final Map<String, SchemaData> userSpecifiedConfig;
   private final String importDatabaseSource;

   public RestoreTableDataOperation(Set<String> schemas, Map<String, SchemaData> userSpecifiedConfig, String importDatabaseSource) {
      super("Restore Table Data", Activator.PLUGIN_ID);
      this.schemas = schemas;
      this.userSpecifiedConfig = userSpecifiedConfig;
      this.importDatabaseSource = importDatabaseSource;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      SqlManager sqlManager = SqlFactory.getSqlManager(ConnectionHandler.getMetaData());

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
      Map<String, Set<String>> importedTables = schemaData.getTablesToImport(importDatabaseSource);
      Set<String> keys = importedTables.keySet();
      for (String key : keys) {
         Set<String> tables = importedTables.get(key);
         for (String tableName : tables) {
            importer.addToTableFilter(tableName);
         }
      }
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

}
