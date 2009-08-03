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

   public static void putValue(String key, String value) throws OseeDataStoreException {
      ConnectionHandler.runPreparedUpdate(DELETE_KEY_SQL, key);
      ConnectionHandler.runPreparedUpdate(INSERT_KEY_VALUE_SQL, key, value);
      cache.put(key, value);
   }

   public static String getDatabaseGuid() throws OseeDataStoreException {
      return getCachedValue(DB_ID_KEY);
   }
}