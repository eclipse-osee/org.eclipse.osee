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
package org.eclipse.osee.framework.skynet.core.utility;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.jdbc.JdbcClient;

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

   private static Map<String, Pair<Long, String>> cache = new ConcurrentHashMap<>();

   public static String getValue(String key, String defaultValue) {
      return getValue(ConnectionHandler.getJdbcClient(), key, defaultValue, (long) Integer.MAX_VALUE);
   }

   public static String getValue(String key) {
      return getValue(key, (long) Integer.MAX_VALUE);
   }

   public static String getValue(String key, Long maxStaleness) {
      return getValue(ConnectionHandler.getJdbcClient(), key, "", maxStaleness);
   }

   public static String getValue(JdbcClient jdbcClient, String key) {
      return getValue(jdbcClient, key, "", (long) Integer.MAX_VALUE);
   }

   public static String getValue(JdbcClient jdbcClient, String key, String defaultValue, Long maxStaleness) {
      Pair<Long, String> pair = cache.get(key);
      String value;
      if (pair == null || pair.getFirst() + maxStaleness < System.currentTimeMillis()) {
         value = jdbcClient.fetch(defaultValue, GET_VALUE_SQL, key);
         cacheValue(key, value);
      } else {
         value = pair.getSecond();
      }

      return value;
   }

   public static void setValue(String key, String value) {
      ConnectionHandler.runPreparedUpdate(DELETE_KEY_SQL, key);
      ConnectionHandler.runPreparedUpdate(INSERT_KEY_VALUE_SQL, key, value);
      cacheValue(key, value);
   }

   public static String getDatabaseGuid() {
      return getValue(DB_ID_KEY);
   }

   public static String getCachedValue(String key) {
      Pair<Long, String> cacheValue = cache.get(key);
      String value;
      if (cacheValue == null) {
         value = getValue(ConnectionHandler.getJdbcClient(), key);
      } else {
         value = cacheValue.getSecond();
      }
      return value;
   }

   private static void cacheValue(String key, String value) {
      Long time = System.currentTimeMillis();
      cache.put(key, new Pair<>(time, value));
   }
}