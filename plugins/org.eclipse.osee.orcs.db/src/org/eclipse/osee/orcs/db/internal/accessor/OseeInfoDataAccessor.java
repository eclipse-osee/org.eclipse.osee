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
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataStoreConstants;
import org.eclipse.osee.orcs.core.ds.KeyValueDataAccessor;

public class OseeInfoDataAccessor implements KeyValueDataAccessor {

   private static final String GET_VALUE_SQL = "SELECT osee_value FROM osee_info WHERE OSEE_KEY = ?";
   private static final String INSERT_KEY_VALUE_SQL = "INSERT INTO osee_info (OSEE_KEY, OSEE_VALUE) VALUES (?, ?)";
   private static final String DELETE_KEY_SQL = "DELETE FROM osee_info WHERE OSEE_KEY = ?";
   private static final String GET_KEYS_SQL = "SELECT osee_key FROM osee_info";

   private static final String ERROR_MESSAGE = "Unsupported modification - attempt to modify [%s].";
   private static final String BINARY_DATA_ERROR_MSG =
      ERROR_MESSAGE + " This can be modified at startup through -D%s=<PATH>.";
   private static final String INDEX_STARTUP_ERROR_MSG = ERROR_MESSAGE + " This is an launch time setting.";

   private Log logger;
   private JdbcClient jdbcClient;
   private boolean wasBinaryDataChecked = false;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcClient = jdbcService.getClient();
   }

   public void start() {
      // Do Nothing
   }

   public void stop() {
      wasBinaryDataChecked = false;
   }

   @Override
   public String getValue(String key)  {
      String toReturn = null;
      if (OseeClient.OSEE_APPLICATION_SERVER_DATA.equals(key)) {
         toReturn = getOseeApplicationServerData();
      } else if (DataStoreConstants.DATASTORE_INDEX_ON_START_UP.equals(key)) {
         toReturn = String.valueOf(isCheckTagQueueOnStartupAllowed());
      } else {
         toReturn = jdbcClient.fetch("", GET_VALUE_SQL, key);
      }
      return toReturn;
   }

   @Override
   public boolean putValue(String key, String value)  {
      boolean wasUpdated = false;
      if (OseeClient.OSEE_APPLICATION_SERVER_DATA.equals(key)) {
         throw new OseeStateException(BINARY_DATA_ERROR_MSG, OseeClient.OSEE_APPLICATION_SERVER_DATA,
            OseeClient.OSEE_APPLICATION_SERVER_DATA);
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
      String toReturn = System.getProperty(OseeClient.OSEE_APPLICATION_SERVER_DATA);
      if (!Strings.isValid(toReturn)) {
         String userHome = System.getProperty("user.home");
         if (Strings.isValid(userHome)) {
            toReturn = userHome;
         }
      }
      return toReturn;
   }

   @Override
   public Set<String> getKeys()  {
      Set<String> keys = new HashSet<>();
      jdbcClient.runQuery(stmt -> keys.add(stmt.getString("osee_key")), GET_KEYS_SQL);
      return keys;
   }
}