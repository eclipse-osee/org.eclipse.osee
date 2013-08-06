/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public class Options implements Cloneable {
   private static final String EXCEPTION_MESSAGE = "No setting found for key: [%s]";
   private final Map<String, String> data = new HashMap<String, String>();

   protected Options() {
      super();
   }

   public void reset() {
      data.clear();
   }

   public boolean isEmpty(String key) {
      String value = get(key);
      return value == null || "".equals(value);
   }

   public String get(String key) {
      return data.get(key);
   }

   public boolean getBoolean(String key) {
      return Boolean.valueOf(get(key)).booleanValue();
   }

   public double getDouble(String key) throws NumberFormatException {
      String setting = get(key);
      if (setting == null) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }

      return new Double(setting).doubleValue();
   }

   public float getFloat(String key) throws NumberFormatException {
      String setting = get(key);
      if (setting == null) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }

      return new Float(setting).floatValue();
   }

   public int getInt(String key) throws NumberFormatException {
      String setting = get(key);
      if (setting == null) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }

      return new Integer(setting).intValue();
   }

   public long getLong(String key) throws NumberFormatException {
      String setting = get(key);
      if (setting == null) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }
      return new Long(setting).longValue();
   }

   public Date getDate(String key) throws IllegalArgumentException {
      String setting = get(key);
      if (setting == null) {
         throw new IllegalArgumentException(String.format(EXCEPTION_MESSAGE, key));
      }
      return new Date(new Long(setting).longValue());
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
