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
package org.eclipse.osee.framework.core.server;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.jdbc.JdbcClient;

/**
 * @author Donald G. Dunne
 */
public class OseeInfo {
   private static final String GET_VALUE_SQL = "Select OSEE_VALUE FROM osee_info where OSEE_KEY = ?";
   private static final String INSERT_KEY_VALUE_SQL = "INSERT INTO osee_info (OSEE_KEY, OSEE_VALUE) VALUES (?, ?)";
   private static final String DELETE_KEY_SQL = "DELETE FROM osee_info WHERE OSEE_KEY = ?";
   public static final String DB_ID_KEY = "osee.db.guid";
   private static Map<String, String> cache = new HashMap<>();

   public static String getValue(JdbcClient jdbcClient, String key) {
      String toReturn = jdbcClient.fetch("", GET_VALUE_SQL, key);
      cache.put(key, toReturn);
      return toReturn;
   }

   public static String getDatabaseGuid(JdbcClient jdbcClient) {
      return getValue(jdbcClient, DB_ID_KEY);
   }

   public static String getCachedValue(JdbcClient jdbcClient, String key) {
      String cacheValue = cache.get(key);
      if (cacheValue == null) {
         cacheValue = getValue(jdbcClient, key);
         cache.put(key, cacheValue);
      }
      return cacheValue;
   }

   public static void setValue(JdbcClient jdbcClient, String key, String value) {
      jdbcClient.runPreparedUpdate(DELETE_KEY_SQL, key);
      jdbcClient.runPreparedUpdate(INSERT_KEY_VALUE_SQL, key, value);
      cache.put(key, value);
   }

}