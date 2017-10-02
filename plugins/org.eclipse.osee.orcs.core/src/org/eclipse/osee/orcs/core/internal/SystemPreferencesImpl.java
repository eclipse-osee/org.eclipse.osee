/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.DataStoreConstants;
import org.eclipse.osee.orcs.core.ds.KeyValueDataAccessor;

/**
 * @author Roberto E. Escobar
 */
public class SystemPreferencesImpl implements SystemPreferences {

   private static Map<String, Pair<Long, String>> cache;
   private KeyValueDataAccessor accessor;

   public void setDataAccessor(KeyValueDataAccessor accessor) {
      this.accessor = accessor;
   }

   public void start() {
      cache = new ConcurrentHashMap<>();
   }

   public void stop() {
      cache = null;
   }

   @Override
   public String getSystemUuid() {
      return getValue(DataStoreConstants.DATASTORE_ID_KEY);
   }

   @Override
   public String getValue(String key) {
      String toReturn = accessor.getValue(key);
      cacheValue(key, toReturn);
      return toReturn;
   }

   @Override
   public String getCachedValue(String key) {
      return getCachedValue(key, Integer.MAX_VALUE);
   }

   @Override
   public String getCachedValue(String key, long maxStaleness) {
      Pair<Long, String> pair = cache.get(key);
      String value;
      if (pair == null || pair.getFirst() + maxStaleness < System.currentTimeMillis()) {
         value = getValue(key);
      } else {
         value = pair.getSecond();
      }

      return value;
   }

   @Override
   public boolean isEnabled(String key) {
      return isBoolean(key);
   }

   @Override
   public boolean isCacheEnabled(String key) {
      String dbProperty = getCachedValue(key);
      if (Strings.isValid(dbProperty)) {
         return dbProperty.equals("true");
      }
      return false;
   }

   @Override
   public void setEnabled(String key, boolean enabled) {
      setBoolean(key, enabled);
   }

   @Override
   public void setBoolean(String key, boolean value) {
      putValue(key, String.valueOf(value));
   }

   @Override
   public boolean isBoolean(String key) {
      String dbProperty = getValue(key);
      if (Strings.isValid(dbProperty)) {
         return dbProperty.equals("true");
      }
      return false;
   }

   @Override
   public boolean isBooleanUsingCache(String key) {
      return isCacheEnabled(key);
   }

   @Override
   public void putValue(String key, String value) {
      accessor.putValue(key, value);
      cacheValue(key, value);
   }

   @Override
   public Set<String> getKeys() {
      return accessor.getKeys();
   }

   private static void cacheValue(String key, String value) {
      Long time = System.currentTimeMillis();
      cache.put(key, new Pair<Long, String>(time, value));
   }

}
