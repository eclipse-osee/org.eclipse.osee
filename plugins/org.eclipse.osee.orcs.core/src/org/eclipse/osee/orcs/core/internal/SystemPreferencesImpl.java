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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.core.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.DataStoreConstants;
import org.eclipse.osee.orcs.core.ds.KeyValueDataAccessor;

/**
 * @author Roberto E. Escobar
 */
public class SystemPreferencesImpl implements SystemPreferences {

   private Map<String, String> cache;
   private KeyValueDataAccessor accessor;

   public void setDataAccessor(KeyValueDataAccessor accessor) {
      this.accessor = accessor;
   }

   public void start() {
      cache = new ConcurrentHashMap<String, String>();
   }

   public void stop() {
      cache = null;
   }

   @Override
   public String getSystemUuid() throws OseeCoreException {
      return getValue(DataStoreConstants.DATASTORE_ID_KEY);
   }

   @Override
   public String getValue(String key) throws OseeCoreException {
      String toReturn = accessor.getValue(key);
      cache.put(key, toReturn);
      return toReturn;
   }

   @Override
   public String getCachedValue(String key) throws OseeCoreException {
      String cacheValue = cache.get(key);
      if (cacheValue == null) {
         cacheValue = getValue(key);
         cache.put(key, cacheValue);
      }

      return cacheValue;
   }

   @Override
   public boolean isEnabled(String key) throws OseeCoreException {
      return isBoolean(key);
   }

   @Override
   public boolean isCacheEnabled(String key) throws OseeCoreException {
      String dbProperty = getCachedValue(key);
      if (Strings.isValid(dbProperty)) {
         return dbProperty.equals("true");
      }
      return false;
   }

   @Override
   public void setEnabled(String key, boolean enabled) throws OseeCoreException {
      setBoolean(key, enabled);
   }

   @Override
   public void setBoolean(String key, boolean value) throws OseeCoreException {
      putValue(key, String.valueOf(value));
   }

   @Override
   public boolean isBoolean(String key) throws OseeCoreException {
      String dbProperty = getValue(key);
      if (Strings.isValid(dbProperty)) {
         return dbProperty.equals("true");
      }
      return false;
   }

   @Override
   public boolean isBooleanUsingCache(String key) throws OseeCoreException {
      return isCacheEnabled(key);
   }

   @Override
   public void putValue(String key, String value) throws OseeCoreException {
      accessor.putValue(key, value);
      cache.put(key, value);
   }

   @Override
   public Set<String> getKeys() throws OseeCoreException {
      return accessor.getKeys();
   }

}
