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

   /**
    * @param schemas
    * @param schemas
    * @param userSpecifiedConfig
    * @param connection
    * @param databaseType
    * @param userSpecifiedConfig2
    * @param databaseType2
    * @throws Exception
    */
   public static void addIndeces(Set<String> schemas, Map<String, SchemaData> userSpecifiedConfig) throws OseeDataStoreException {
      for (String schemaId : schemas) {
         if (userSpecifiedConfig.containsKey(schemaId)) {
            SchemaData userSpecifiedSchemaData = userSpecifiedConfig.get(schemaId);
            DbFactory userDbFactory = new DbFactory(userSpecifiedSchemaData);
            userDbFactory.createIndeces();
         }
      }
   }

   /**
    * @param schemas
    * @param userSpecifiedConfig2
    * @param databaseType2
    * @throws Exception
    */
   public static void addTables(Set<String> schemas, Map<String, SchemaData> userSpecifiedConfig) throws OseeDataStoreException {
      for (String schemaId : schemas) {
         if (userSpecifiedConfig.containsKey(schemaId)) {
            SchemaData userSpecifiedSchemaData = userSpecifiedConfig.get(schemaId);
            DbFactory userDbFactory = new DbFactory(userSpecifiedSchemaData);
            userDbFactory.createTables();
         }
      }
   }

   /**
    * @param schemas
    * @param currentDatabaseConfig
    * @param userSpecifiedConfig2
    * @param currentDatabaseConfig2
    * @param databaseType2
    * @throws Exception
    */
   public static void dropTables(Set<String> schemas, Map<String, SchemaData> userSpecifiedConfig, Map<String, SchemaData> currentDatabaseConfig) throws OseeDataStoreException {
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
            DbFactory currentDbFactory = new DbFactory(toDrop);
            currentDbFactory.dropTables();
         }
      }
   }

   /**
    * @param schemas
    * @param currentDatabaseConfig
    * @param userSpecifiedConfig2
    * @param currentDatabaseConfig2
    * @param databaseType2
    * @throws Exception
    */
   public static void dropIndeces(Set<String> schemas, Map<String, SchemaData> userSpecifiedConfig, Map<String, SchemaData> currentDatabaseConfig) throws OseeDataStoreException {
      System.out.println("Drop Indeces");
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
            DbFactory currentDbFactory = new DbFactory(toDrop);
            currentDbFactory.dropIndeces();
         }
      }
   }

   public static void createSchema(Set<String> schemas) throws OseeDataStoreException {
      SqlManager manager = SqlFactory.getSqlManager();
      for (String schemaId : schemas) {
         manager.createSchema(schemaId.toLowerCase());
      }
   }

   public static void dropSchema(Set<String> schemas) throws OseeDataStoreException {
      SqlManager manager = SqlFactory.getSqlManager();
      for (String schemaId : schemas) {
         manager.dropSchema(schemaId.toLowerCase());
      }
   }
}
