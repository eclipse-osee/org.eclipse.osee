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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.SupportedDatabase;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataStoreConstants;
import org.eclipse.osee.orcs.core.ds.KeyValueDataAccessor;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.resource.ResourceConstants;

/**
 * @author Roberto E. Escobar
 */
public class OseeInfoDataAccessor implements KeyValueDataAccessor {

   private static final String GET_VALUE_SQL = "SELECT osee_value FROM osee_info WHERE OSEE_KEY = ?";
   private static final String INSERT_KEY_VALUE_SQL = "INSERT INTO osee_info (OSEE_KEY, OSEE_VALUE) VALUES (?, ?)";
   private static final String DELETE_KEY_SQL = "DELETE FROM osee_info WHERE OSEE_KEY = ?";

   private Log logger;
   private IOseeDatabaseService dbService;
   private boolean wasBinaryDataChecked = false;
   private Boolean areHintsSupported;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setDatabaseService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
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
      } else if (DataStoreConstants.DATASTORE_INDEX_ON_START_UP.equals(key)) {
         toReturn = String.valueOf(isCheckTagQueueOnStartupAllowed());
      } else {
         toReturn = dbService.runPreparedQueryFetchObject("", GET_VALUE_SQL, key);
      }
      return toReturn;
   }

   @Override
   public boolean putValue(String key, String value) throws OseeCoreException {
      boolean wasUpdated = false;
      if (ResourceConstants.BINARY_DATA_PATH.equals(key)) {
         throw new OseeStateException(
            "Unsupported modification - attempt to modify binary data path. Sets are startup through -D%s=<PATH>",
            ResourceConstants.BINARY_DATA_PATH);
      } else if (SqlProvider.SQL_DATABASE_HINTS_SUPPORTED_KEY.equals(key)) {
         throw new OseeStateException(
            "Unsupported modification - attempt to modify database hints supported. This is an unmodifiable database specific setting.",
            SqlProvider.SQL_DATABASE_HINTS_SUPPORTED_KEY);
      } else if (DataStoreConstants.DATASTORE_INDEX_ON_START_UP.equals(key)) {
         throw new OseeStateException(
            "Unsupported modification - attempt to modify index start-up flag. This is an launch time setting.",
            DataStoreConstants.DATASTORE_INDEX_ON_START_UP);
      } else {
         ConnectionHandler.runPreparedUpdate(DELETE_KEY_SQL, key);
         int updated = ConnectionHandler.runPreparedUpdate(INSERT_KEY_VALUE_SQL, key, value);
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
         OseeConnection connection = dbService.getConnection();
         try {
            DatabaseMetaData metaData = connection.getMetaData();
            areHintsSupported = SupportedDatabase.areHintsSupported(metaData);
         } finally {
            connection.close();
         }
      }
      return areHintsSupported;
   }
}
