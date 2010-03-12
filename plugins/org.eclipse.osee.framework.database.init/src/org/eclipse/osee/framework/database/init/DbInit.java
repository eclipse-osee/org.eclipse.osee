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

import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;

public class DbInit {
   private final SqlManager manager;

   public DbInit(SqlManager manager) {
      this.manager = manager;
   }

   public void addIndices(Set<String> schemas, Map<String, SchemaData> userSpecifiedConfig) throws OseeDataStoreException {
      for (String schemaId : schemas) {
         if (userSpecifiedConfig.containsKey(schemaId)) {
            SchemaData userSpecifiedSchemaData = userSpecifiedConfig.get(schemaId);
            DbFactory userDbFactory = new DbFactory(manager, userSpecifiedSchemaData);
            userDbFactory.createIndices();
         }
      }
   }

   public void addTables(Set<String> schemas, Map<String, SchemaData> userSpecifiedConfig) throws OseeDataStoreException {
      for (String schemaId : schemas) {
         if (userSpecifiedConfig.containsKey(schemaId)) {
            SchemaData userSpecifiedSchemaData = userSpecifiedConfig.get(schemaId);
            DbFactory userDbFactory = new DbFactory(manager, userSpecifiedSchemaData);
            userDbFactory.createTables();
         }
      }
   }

   public void dropTables(Set<String> schemas, Map<String, SchemaData> userSpecifiedConfig, Map<String, SchemaData> currentDatabaseConfig) throws OseeDataStoreException {
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
            DbFactory currentDbFactory = new DbFactory(manager, toDrop);
            currentDbFactory.dropTables();
         }
      }
   }

   public void dropIndices(Set<String> schemas, Map<String, SchemaData> userSpecifiedConfig, Map<String, SchemaData> currentDatabaseConfig) throws OseeDataStoreException {
      System.out.println("Drop Indices");
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
            DbFactory currentDbFactory = new DbFactory(manager, toDrop);
            currentDbFactory.dropIndices();
         }
      }
   }

   public void createSchema(Set<String> schemas) throws OseeDataStoreException {
      for (String schemaId : schemas) {
         manager.createSchema(schemaId.toLowerCase());
      }
   }

   public void dropSchema(Set<String> schemas) throws OseeDataStoreException {
      for (String schemaId : schemas) {
         manager.dropSchema(schemaId.toLowerCase());
      }
   }
}
