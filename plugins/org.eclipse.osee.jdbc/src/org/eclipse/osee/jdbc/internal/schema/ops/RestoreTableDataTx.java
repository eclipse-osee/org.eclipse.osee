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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcTransaction;
import org.eclipse.osee.jdbc.internal.schema.data.SchemaData;
import org.eclipse.osee.jdbc.internal.schema.data.TableElement;
import org.eclipse.osee.jdbc.internal.schema.sql.SqlFactory;
import org.eclipse.osee.jdbc.internal.schema.sql.SqlManager;
import org.eclipse.osee.jdbc.internal.schema.util.DatabaseDataImporter;

/**
 * @author Roberto E. Escobar
 */
public class RestoreTableDataTx extends JdbcTransaction {
   private static final File backupDirectory = new File("backupDirectory");

   private final JdbcClient client;
   private final Set<String> schemas;
   private final Map<String, SchemaData> userSpecifiedConfig;
   private final String importDatabaseSource;

   public RestoreTableDataTx(JdbcClient client, Set<String> schemas, Map<String, SchemaData> userSpecifiedConfig, String importDatabaseSource) {
      this.client = client;
      this.schemas = schemas;
      this.userSpecifiedConfig = userSpecifiedConfig;
      this.importDatabaseSource = importDatabaseSource;
   }

   @Override
   public void handleTxWork(JdbcConnection connection) {
      SqlManager sqlManager = SqlFactory.getSqlManager(client, connection, client.getDatabaseType());

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
