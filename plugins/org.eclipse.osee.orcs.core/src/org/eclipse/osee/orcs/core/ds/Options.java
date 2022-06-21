/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.core.ds;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Roberto E. Escobar
 */
public class Options implements Cloneable {

   private static final String EXCEPTION_MESSAGE = "No setting found for key: [%s]";
   private final Map<String, Object> data = new HashMap<>();

   protected Options() {
      super();
   }

   public void reset() {
      data.clear();
   }

   public Set<String> getKeys() {
      return Collections.unmodifiableSet(data.keySet());
   }

   public boolean isEmpty(String key) {
      String value = get(key);
      return value == null || "".equals(value);
   }

   @SuppressWarnings("unchecked")
   public <T> T getObject(Class<T> clazz, String key) {
      return (T) getObject(key);
   }

   public Object getObject(String key) {
      return data.get(key);
   }

   public String get(String key) {
      Object value = getObject(key);
      return value != null ? String.valueOf(value) : null;
   }

   public boolean getBoolean(String key) {
      return Boolean.valueOf(get(key)).booleanValue();
   }

   public double getDouble(String key) throws NumberFormatException {
      String setting = get(key);
      if (setting == null) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }

      return Double.valueOf(setting).doubleValue();
   }

   public float getFloat(String key) throws NumberFormatException {
      String setting = get(key);
      if (setting == null) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }

      return Float.valueOf(setting).floatValue();
   }

   public int getInt(String key) throws NumberFormatException {
      String setting = get(key);
      if (setting == null) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }

      return Integer.valueOf(setting).intValue();
   }

   public long getLong(String key) throws NumberFormatException {
      String setting = get(key);
      if (setting == null) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }
      return Long.valueOf(setting).longValue();
   }

   public Date getDate(String key) throws IllegalArgumentException {
      String setting = get(key);
      if (setting == null) {
         throw new IllegalArgumentException(String.format(EXCEPTION_MESSAGE, key));
      }
      return new Date(Long.valueOf(setting).longValue());
   }

   public Date getDateOrNull(String key) throws IllegalArgumentException {
      String setting = get(key);
      Date toReturn = null;
      if (setting != null) {
         toReturn = getDate(key);
      }
      return toReturn;
   }

   public void put(String key, double value) {
      put(key, String.valueOf(value));
   }

   public void put(String key, Object value) {
      data.put(key, value);
   }

   public void put(String key, float value) {
      put(key, String.valueOf(value));
   }

   public void put(String key, int value) {
      put(key, String.valueOf(value));
   }

   public void put(String key, long value) {
      put(key, String.valueOf(value));
   }

   public void put(String key, String value) {
      data.put(key, value);
   }

   public void put(String key, boolean value) {
      put(key, String.valueOf(value));
   }

   public void put(String key, Date date) {
      if (date != null) {
         put(key, date.getTime());
      }
   }

   public void remove(String key) {
      data.remove(key);
   }

   public void setFrom(Options source) {
      this.data.clear();
      data.putAll(source.data);
   }

   @Override
   public Options clone() {
      Options clone = new Options();
      clone.setFrom(this);
      return clone;
   }

   @Override
   public String toString() {
      return "Options [data=" + data + "]";
   }

}
