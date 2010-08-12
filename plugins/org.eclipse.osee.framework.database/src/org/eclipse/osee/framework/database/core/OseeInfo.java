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
package org.eclipse.osee.framework.database.core;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class OseeInfo {
   private static final String GET_VALUE_SQL = "Select OSEE_VALUE FROM osee_info where OSEE_KEY = ?";
   private static final String INSERT_KEY_VALUE_SQL = "INSERT INTO osee_info (OSEE_KEY, OSEE_VALUE) VALUES (?, ?)";
   private static final String DELETE_KEY_SQL = "DELETE FROM osee_info WHERE OSEE_KEY = ?";
   public static final String SAVE_OUTFILE_IN_DB = "SAVE_OUTFILE_IN_DB";
   public static final String USE_GUID_STORAGE = "osee.framework.skynet.core.guid.storage";
   // This is a unique identifier generated upon database initialization and should never be changed once it has been created.
   public static final String DB_ID_KEY = "osee.db.guid";
   public static final String DB_TYPE_KEY = "osee.db.type";

   private static Map<String, String> cache = new HashMap<String, String>();

   public static String getValue(String key) throws OseeDataStoreException {
      String toReturn = ConnectionHandler.runPreparedQueryFetchString("", GET_VALUE_SQL, key);
      cache.put(key, toReturn);
      return toReturn;
   }

   public static String getCachedValue(String key) throws OseeDataStoreException {
      String cacheValue = cache.get(key);
      if (cacheValue == null) {
         cacheValue = getValue(key);
         cache.put(key, cacheValue);
      }

      return cacheValue;
   }

   /**
    * Return true if key is set in osee_info table and value = "true". Return false if key is either not in osee_info
    * table OR value != "true".<br>
    * <br>
    * Note: This call will hit the database every time, so shouldn't be used for often repeated calls. use
    * isCacheEnabled that will cache the value
    */
   public static boolean isEnabled(String key) throws OseeDataStoreException {
      String dbProperty = OseeInfo.getValue(key);
      if (Strings.isValid(dbProperty)) {
         return dbProperty.equals("true");
      }
      return false;
   }

   /**
    * Return true if key is set in osee_info table and value = "true". Return false if key is either not in osee_info
    * table OR value != "true".<br>
    * <br>
    * Return cached value (value only loaded once per session. Restart will reset value if changed in osee_info
    */
   public static boolean isCacheEnabled(String key) throws OseeDataStoreException {
      String dbProperty = OseeInfo.getCachedValue(key);
      if (Strings.isValid(dbProperty)) {
         return dbProperty.equals("true");
      }
      return false;
   }

   public static void setEnabled(String key, boolean enabled) throws OseeDataStoreException {
      putValue(key, String.valueOf(enabled));
   }

   public static void putValue(String key, String value) throws OseeDataStoreException {
      ConnectionHandler.runPreparedUpdate(DELETE_KEY_SQL, key);
      ConnectionHandler.runPreparedUpdate(INSERT_KEY_VALUE_SQL, key, value);
      cache.put(key, value);
   }

   public static String getDatabaseGuid() throws OseeDataStoreException {
      return getValue(DB_ID_KEY);
   }
}