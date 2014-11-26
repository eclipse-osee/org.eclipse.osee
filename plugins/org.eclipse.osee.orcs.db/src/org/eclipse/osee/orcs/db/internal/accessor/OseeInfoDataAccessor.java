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
package org.eclipse.osee.orcs.db.internal.accessor;

import java.io.File;
import java.sql.DatabaseMetaData;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcDbType;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataStoreConstants;
import org.eclipse.osee.orcs.core.ds.KeyValueDataAccessor;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.resource.ResourceConstants;

public class OseeInfoDataAccessor implements KeyValueDataAccessor {

   private static final String GET_VALUE_SQL = "SELECT osee_value FROM osee_info WHERE OSEE_KEY = ?";
   private static final String INSERT_KEY_VALUE_SQL = "INSERT INTO osee_info (OSEE_KEY, OSEE_VALUE) VALUES (?, ?)";
   private static final String DELETE_KEY_SQL = "DELETE FROM osee_info WHERE OSEE_KEY = ?";
   private static final String GET_KEYS_SQL = "SELECT osee_key FROM osee_info";

   private static final String ERROR_MESSAGE = "Unsupported modification - attempt to modify [%s].";
   private static final String BINARY_DATA_ERROR_MSG =
      ERROR_MESSAGE + " This can be modified at startup through -D%s=<PATH>.";
   private static final String DB_KEY_ERROR_MSG = ERROR_MESSAGE + " This is an unmodifiable database specific setting.";
   private static final String INDEX_STARTUP_ERROR_MSG = ERROR_MESSAGE + " This is an launch time setting.";

   private Log logger;
   private JdbcClient jdbcClient;
   private boolean wasBinaryDataChecked = false;
   private Boolean areHintsSupported;
   private String recursiveKeyword;
   private String regExpPattern;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcClient = jdbcService.getClient();
   }

   public void start() {
      areHintsSupported = null;
      // Do Nothing
   }

   public void stop() {
      wasBinaryDataChecked = false;
      areHintsSupported = null;
   }

   @Override
   public String getValue(String key) throws OseeCoreException {
      String toReturn = null;
      if (ResourceConstants.BINARY_DATA_PATH.equals(key)) {
         toReturn = getOseeApplicationServerData();
      } else if (SqlProvider.SQL_DATABASE_HINTS_SUPPORTED_KEY.equals(key)) {
         toReturn = String.valueOf(areHintsSupported());
      } else if (SqlProvider.SQL_RECURSIVE_WITH_KEY.equals(key)) {
         toReturn = getSQLRecursiveKeyword();
      } else if (SqlProvider.SQL_REG_EXP_PATTERN_KEY.equals(key)) {
         toReturn = getSQLRegExpPattern();
      } else if (DataStoreConstants.DATASTORE_INDEX_ON_START_UP.equals(key)) {
         toReturn = String.valueOf(isCheckTagQueueOnStartupAllowed());
      } else {
         toReturn = jdbcClient.runPreparedQueryFetchObject("", GET_VALUE_SQL, key);
      }
      return toReturn;
   }

   @Override
   public boolean putValue(String key, String value) throws OseeCoreException {
      boolean wasUpdated = false;
      if (ResourceConstants.BINARY_DATA_PATH.equals(key)) {
         throw new OseeStateException(BINARY_DATA_ERROR_MSG, ResourceConstants.BINARY_DATA_PATH,
            ResourceConstants.BINARY_DATA_PATH);
      } else if (SqlProvider.SQL_DATABASE_HINTS_SUPPORTED_KEY.equals(key)) {
         throw new OseeStateException(DB_KEY_ERROR_MSG, SqlProvider.SQL_DATABASE_HINTS_SUPPORTED_KEY);
      } else if (SqlProvider.SQL_RECURSIVE_WITH_KEY.equals(key)) {
         throw new OseeStateException(DB_KEY_ERROR_MSG, SqlProvider.SQL_RECURSIVE_WITH_KEY);
      } else if (SqlProvider.SQL_REG_EXP_PATTERN_KEY.equals(key)) {
         throw new OseeStateException(DB_KEY_ERROR_MSG, SqlProvider.SQL_REG_EXP_PATTERN_KEY);
      } else if (DataStoreConstants.DATASTORE_INDEX_ON_START_UP.equals(key)) {
         throw new OseeStateException(INDEX_STARTUP_ERROR_MSG, DataStoreConstants.DATASTORE_INDEX_ON_START_UP);
      } else {
         jdbcClient.runPreparedUpdate(DELETE_KEY_SQL, key);
         int updated = jdbcClient.runPreparedUpdate(INSERT_KEY_VALUE_SQL, key, value);
         wasUpdated = updated == 1;
      }
      return wasUpdated;
   }

   /**
    * Check Tag Queue on start up. Entries found in the tag queue are tagged by the server on start up.
    * 
    * @return whether tag queue should be checked upon server start-up.
    */
   public static boolean isCheckTagQueueOnStartupAllowed() {
      return Boolean.valueOf(System.getProperty(DataStoreConstants.DATASTORE_INDEX_ON_START_UP, "false"));
   }

   /**
    * Get location for OSEE application server binary data
    * 
    * @return OSEE application server binary data path
    */
   public String getOseeApplicationServerData() {
      String toReturn = internalGetOseeApplicationServerData();
      if (!wasBinaryDataChecked) {
         File file = new File(toReturn);
         if (file.exists()) {
            logger.info("Application Server Data: [%s]", toReturn);
         } else {
            logger.warn("Application Server Data: [%s] does not exist and will be created", toReturn);
         }
         wasBinaryDataChecked = true;
      }
      return toReturn;
   }

   private String internalGetOseeApplicationServerData() {
      String toReturn = System.getProperty(ResourceConstants.BINARY_DATA_PATH);
      if (!Strings.isValid(toReturn)) {
         String userHome = System.getProperty("user.home");
         if (Strings.isValid(userHome)) {
            toReturn = userHome;
         }
      }
      return toReturn;
   }

   private boolean areHintsSupported() throws OseeCoreException {
      if (areHintsSupported == null) {
         areHintsSupported = false;
         JdbcConnection connection = jdbcClient.getConnection();
         try {
            DatabaseMetaData metaData = connection.getMetaData();
            areHintsSupported = JdbcDbType.areHintsSupported(metaData);
         } finally {
            connection.close();
         }
      }
      return areHintsSupported;
   }

   @Override
   public Set<String> getKeys() throws OseeCoreException {
      Set<String> keys = new HashSet<String>();
      JdbcStatement chStmt = jdbcClient.getStatement();
      try {
         chStmt.runPreparedQuery(GET_KEYS_SQL);
         while (chStmt.next()) {
            keys.add(chStmt.getString("osee_key"));
         }
      } finally {
         chStmt.close();
      }
      return keys;
   }

   private String getSQLRecursiveKeyword() throws OseeCoreException {
      if (recursiveKeyword == null) {
         JdbcConnection connection = jdbcClient.getConnection();
         try {
            DatabaseMetaData metaData = connection.getMetaData();
            recursiveKeyword = JdbcDbType.getRecursiveWithSql(metaData);
         } finally {
            connection.close();
         }
      }
      return recursiveKeyword;
   }

   private String getSQLRegExpPattern() throws OseeCoreException {
      if (regExpPattern == null) {
         JdbcConnection connection = jdbcClient.getConnection();
         try {
            DatabaseMetaData metaData = connection.getMetaData();
            regExpPattern = JdbcDbType.getRegularExpMatchSql(metaData);
         } finally {
            connection.close();
         }
      }
      return regExpPattern;
   }

}
