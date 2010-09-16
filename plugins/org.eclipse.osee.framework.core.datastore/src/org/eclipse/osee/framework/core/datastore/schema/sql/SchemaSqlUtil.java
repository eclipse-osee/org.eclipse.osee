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
package org.eclipse.osee.framework.core.datastore.schema.sql;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.datastore.schema.data.SchemaData;
import org.eclipse.osee.framework.core.datastore.schema.data.TableElement;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public class SchemaSqlUtil {
   private final SqlManager sqlManager;

   public SchemaSqlUtil(SqlManager sqlManager) {
      this.sqlManager = sqlManager;
   }

   public void addIndices(Set<String> schemas, Map<String, SchemaData> userSpecifiedConfig) throws OseeCoreException {
      for (String schemaId : schemas) {
         if (userSpecifiedConfig.containsKey(schemaId)) {
            SchemaData schemaData = userSpecifiedConfig.get(schemaId);

            for (TableElement tableDef : schemaData.getTableMap().values()) {
               sqlManager.createIndex(tableDef);
            }
         }
      }
   }

   public void addTables(Set<String> schemas, Map<String, SchemaData> userSpecifiedConfig) throws OseeCoreException {
      for (String schemaId : schemas) {
         if (userSpecifiedConfig.containsKey(schemaId)) {
            SchemaData schemaData = userSpecifiedConfig.get(schemaId);

            List<TableElement> tableDefs = schemaData.getTablesOrderedByDependency();
            for (TableElement tableDef : tableDefs) {
               sqlManager.createTable(tableDef);
            }
         }
      }
   }

   public void dropTables(Set<String> schemas, Map<String, SchemaData> userSpecifiedConfig, Map<String, SchemaData> currentDatabaseConfig) throws OseeCoreException {
      for (String schemaId : schemas) {
         if (currentDatabaseConfig.containsKey(schemaId)) {
            SchemaData currentDbSchemaData = currentDatabaseConfig.get(schemaId);
            SchemaData userSchema = userSpecifiedConfig.get(schemaId);
            Map<String, TableElement> currentDBmap = currentDbSchemaData.getTableMap();
            Map<String, TableElement> userDbMap = userSchema.getTableMap();
            Set<String> currentDbKeys = currentDBmap.keySet();
            Set<String> userDbKeys = userDbMap.keySet();

            SchemaData toDrop = new SchemaData();
            for (String userKey : userDbKeys) {
               if (currentDbKeys.contains(userKey)) {
                  toDrop.addTableDefinition(currentDBmap.get(userKey));
               }
            }

            List<TableElement> tableDefs = toDrop.getTablesOrderedByDependency();
            for (int index = tableDefs.size() - 1; index >= 0; index--) {
               TableElement tableDef = tableDefs.get(index);
               sqlManager.dropTable(tableDef);
            }
         }
      }
   }

   public void dropIndices(Set<String> schemas, Map<String, SchemaData> userSpecifiedConfig, Map<String, SchemaData> currentDatabaseConfig) throws OseeCoreException {
      for (String schemaId : schemas) {
         if (currentDatabaseConfig.containsKey(schemaId)) {
            SchemaData currentDbSchemaData = currentDatabaseConfig.get(schemaId);
            SchemaData userSchema = userSpecifiedConfig.get(schemaId);
            Map<String, TableElement> currentDBmap = currentDbSchemaData.getTableMap();
            Map<String, TableElement> userDbMap = userSchema.getTableMap();
            Set<String> currentDbKeys = currentDBmap.keySet();
            Set<String> userDbKeys = userDbMap.keySet();

            SchemaData toDrop = new SchemaData();
            for (String userKey : userDbKeys) {
               if (currentDbKeys.contains(userKey)) {
                  toDrop.addTableDefinition(currentDBmap.get(userKey));
               }
            }

            for (TableElement tableDef : toDrop.getTableMap().values()) {
               sqlManager.dropIndex(tableDef);
            }
         }
      }
   }

   public void createSchema(Set<String> schemas) throws OseeCoreException {
      for (String schemaId : schemas) {
         sqlManager.createSchema(schemaId.toLowerCase());
      }
   }

   public void dropSchema(Set<String> schemas) throws OseeCoreException {
      for (String schemaId : schemas) {
         sqlManager.dropSchema(schemaId.toLowerCase());
      }
   }

}
