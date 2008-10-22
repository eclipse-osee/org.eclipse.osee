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

package org.eclipse.osee.framework.jdk.core.type;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Roberto E. Escobar
 */
public class VariantData implements IVariantData {
   private static final long serialVersionUID = 9076969425223251739L;

   private final String EXCEPTION_MESSAGE = "No setting found for key: [%s]";
   private final String CONVERSION_EXCEPTION_MESSAGE = "Unable to convert [%s] from [%s] to [%s]";

   private final Properties storageData;
   private final Properties storageArrays;
   private final Map<String, byte[]> byteArrayData;

   public VariantData() {
      this.byteArrayData = new HashMap<String, byte[]>();
      this.storageData = new Properties();
      this.storageArrays = new Properties();
   }

   public String get(String key) {
      return (String) storageData.get(key);
   }

   public boolean isEmpty(String key) {
      if (get(key) == null) return true;
      return get(key).equals("");
   }

   public String[] getArray(String key) {
      return (String[]) storageArrays.get(key);
   }

   public boolean getBoolean(String key) {
      return Boolean.valueOf((String) storageData.get(key)).booleanValue();
   }

   public double getDouble(String key) throws NumberFormatException {
      String setting = (String) storageData.get(key);
      if (setting == null) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }

      return new Double(setting).doubleValue();
   }

   public float getFloat(String key) throws NumberFormatException {
      String setting = (String) storageData.get(key);
      if (setting == null) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }

      return new Float(setting).floatValue();
   }

   public int getInt(String key) throws NumberFormatException {
      String setting = (String) storageData.get(key);
      if (setting == null) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }

      return new Integer(setting).intValue();
   }

   public long getLong(String key) throws NumberFormatException {
      String setting = (String) storageData.get(key);
      if (setting == null) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }
      return new Long(setting).longValue();
   }

   public Date getDate(String key) throws IllegalArgumentException {
      String setting = (String) storageData.get(key);
      if (setting == null) {
         throw new IllegalArgumentException(String.format(EXCEPTION_MESSAGE, key));
      }
      return new Date(new Long(setting).longValue());
   }

   public String getStreamAsString(String key) throws Exception {
      String toReturn;
      InputStream inputStream = getStream(key);
      try {
         toReturn = Lib.inputStreamToString(inputStream);
      } catch (IOException ex) {
         throw new IOException(String.format(CONVERSION_EXCEPTION_MESSAGE, key, "byte[]", "String"));
      }
      return toReturn;
   }

   public InputStream getStream(String key) throws IllegalArgumentException {
      byte[] bytes = byteArrayData.get(key);
      if (bytes == null) {
         throw new IllegalArgumentException(String.format(EXCEPTION_MESSAGE, key));
      }
      return new ByteArrayInputStream(bytes);
   }

   public void put(String key, String[] value) {
      if (value == null) {
         value = new String[0];
      }
      storageArrays.put(key, value);
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

   public void put(String key, boolean value) {
      put(key, String.valueOf(value));
   }

   public void put(String key, Date date) {
      if (date != null) {
         put(key, date.getTime());
      }
   }

   public void put(String key, String value) {
      if (value == null) {
         value = "";
      }
      storageData.put(key, value);
   }

   public void put(String key, byte[] bytes) {
      if (bytes == null) {
         bytes = new byte[0];
      }
      byteArrayData.put(key, bytes);
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(storageData.toString().replaceAll(",", ",\n"));
      builder.append(storageArrays.toString().replaceAll(",", ",\n"));
      for (String key : byteArrayData.keySet()) {
         builder.append(key);
         builder.append("=");
         try {
            builder.append(getStreamAsString(key));
         } catch (Exception ex) {
            builder.append("!!Error!!");
         }
         builder.append(",\n");
      }
      return builder.toString();
   }
}
