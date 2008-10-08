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
package org.eclipse.osee.framework.database.utility;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.database.data.SchemaData;
import org.eclipse.osee.framework.database.data.TableElement;
import org.eclipse.osee.framework.database.initialize.DbFactory;
import org.eclipse.osee.framework.database.sql.SqlFactory;
import org.eclipse.osee.framework.database.sql.SqlManager;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;
import org.eclipse.osee.framework.db.connection.core.schema.View;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;

public class DbInit {

   private static String oracleCreateViewCmd = "CREATE OR REPLACE FORCE VIEW ";
   private static String derbyCreateViewCmd = "CREATE VIEW ";
   private static String mysqlCreateViewCmd = "CREATE VIEW ";
   private static String postgresqlCreateViewCmd = "CREATE VIEW ";

   /**
    * @param connection
    * @param databaseType
    * @param databaseType2
    * @throws OseeDataStoreException
    */
   public static void addViews(Connection connection, SupportedDatabase databaseType) throws OseeDataStoreException {
      for (View view : SkynetDatabase.getSkynetViews()) {
         String viewCreateCmd = "";
         if (databaseType == SupportedDatabase.derby)
            viewCreateCmd = derbyCreateViewCmd;
         else if (databaseType == SupportedDatabase.oracle)
            viewCreateCmd = oracleCreateViewCmd;
         else if (databaseType == SupportedDatabase.mysql)
            viewCreateCmd = mysqlCreateViewCmd;
         else if (databaseType == SupportedDatabase.postgresql) {
            viewCreateCmd = postgresqlCreateViewCmd;
         } else
            throw new IllegalArgumentException("Unhandled database type.");

         ConnectionHandler.runPreparedUpdate(connection, viewCreateCmd + view.toString() + view.getDefinition());
         ConnectionHandler.runPreparedUpdate(connection,
               "create OR REPLACE public synonym " + view.toString() + " for " + view.toString());
      }
   }

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
   public static void addIndeces(Set<String> schemas, Map<String, SchemaData> userSpecifiedConfig, Connection connection, SupportedDatabase databaseType) throws OseeDataStoreException {
      for (String schemaId : schemas) {
         if (userSpecifiedConfig.containsKey(schemaId)) {
            SchemaData userSpecifiedSchemaData = userSpecifiedConfig.get(schemaId);
            DbFactory userDbFactory = new DbFactory(connection, databaseType, userSpecifiedSchemaData);
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
   public static void addTables(Set<String> schemas, Map<String, SchemaData> userSpecifiedConfig, Connection connection, SupportedDatabase databaseType) throws OseeDataStoreException {
      for (String schemaId : schemas) {
         if (userSpecifiedConfig.containsKey(schemaId)) {
            SchemaData userSpecifiedSchemaData = userSpecifiedConfig.get(schemaId);
            DbFactory userDbFactory = new DbFactory(connection, databaseType, userSpecifiedSchemaData);
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
   public static void dropTables(Set<String> schemas, Map<String, SchemaData> userSpecifiedConfig, Connection connection, SupportedDatabase databaseType, Map<String, SchemaData> currentDatabaseConfig) throws OseeDataStoreException {
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
            DbFactory currentDbFactory = new DbFactory(connection, databaseType, toDrop);
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
   public static void dropIndeces(Set<String> schemas, Map<String, SchemaData> userSpecifiedConfig, Connection connection, SupportedDatabase databaseType, Map<String, SchemaData> currentDatabaseConfig) throws OseeDataStoreException {
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
            DbFactory currentDbFactory = new DbFactory(connection, databaseType, toDrop);
            currentDbFactory.dropIndeces();
         }
      }
   }

   /**
    * @param connection
    */
   public static void dropViews(Connection connection) throws OseeDataStoreException {
      try {
         DatabaseMetaData dbData = connection.getMetaData();
         ResultSet tables = dbData.getTables(null, null, null, new String[] {"VIEW"});
         while (tables.next()) {
            String viewName = tables.getString("TABLE_NAME").toUpperCase();
            for (View viewToDrop : SkynetDatabase.getSkynetViews()) {
               if (viewToDrop.toString().equalsIgnoreCase(viewName)) {
                  Statement statement = connection.createStatement();
                  statement.executeUpdate("DROP VIEW " + viewName);
                  statement.close();
               }
            }
         }
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public static void createSchema(Connection connection, Set<String> schemas) throws OseeDataStoreException {
      SqlManager manager = SqlFactory.getSqlManager(SupportedDatabase.getDatabaseType(connection));
      for (String schemaId : schemas) {
         manager.createSchema(connection, schemaId.toLowerCase());
      }
   }

   public static void dropSchema(Connection connection, Set<String> schemas) throws OseeDataStoreException {
      SqlManager manager = SqlFactory.getSqlManager(SupportedDatabase.getDatabaseType(connection));
      for (String schemaId : schemas) {
         manager.dropSchema(connection, schemaId.toLowerCase());
      }
   }
}
