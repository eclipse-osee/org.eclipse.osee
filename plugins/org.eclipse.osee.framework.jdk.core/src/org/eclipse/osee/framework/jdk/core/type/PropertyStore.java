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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class PropertyStore implements IPropertyStore, Serializable {
   private static final long serialVersionUID = 9076969425223251739L;

   private static final String EXCEPTION_MESSAGE = "No setting found for key: [%s]";

   private String storeId;
   private final Properties storageData;
   private final Properties storageArrays;
   private final Properties storageProperties;

   private PropertyStore(String storeId, Properties storageData, Properties storageArrays, Properties storageProperties) {
      super();
      this.storeId = storeId;
      this.storageData = storageData;
      this.storageArrays = storageArrays;
      this.storageProperties = storageProperties;
   }

   public PropertyStore(String storeId) {
      this(storeId, new Properties(), new Properties(), new Properties());
   }

   public PropertyStore() {
      this(StringUtils.EMPTY);
   }

   public PropertyStore(Properties properties) {
      this(Integer.toString(properties.hashCode()), properties, new Properties(), new Properties());
   }

   public String get(String key) {
      return storageData.getProperty(key, StringUtils.EMPTY);
   }

   public String[] getArray(String key) {
      String[] value = (String[]) storageArrays.get(key);
      if (value == null) {
         value = ArrayUtils.EMPTY_STRING_ARRAY;
      }
      return value;
   }

   public boolean getBoolean(String key) {
      return Boolean.valueOf(get(key));
   }

   public double getDouble(String key) throws NumberFormatException {
      String setting = get(key);
      if (!Strings.isValid(setting)) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }

      return new Double(setting).doubleValue();
   }

   public float getFloat(String key) throws NumberFormatException {
      String setting = get(key);
      if (!Strings.isValid(setting)) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }

      return new Float(setting).floatValue();
   }

   public int getInt(String key) throws NumberFormatException {
      String setting = get(key);
      if (!Strings.isValid(setting)) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }

      return new Integer(setting).intValue();
   }

   public long getLong(String key) throws NumberFormatException {
      String setting = get(key);
      if (!Strings.isValid(setting)) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }

      return new Long(setting).longValue();
   }

   public PropertyStore getPropertyStore(String key) {
      return (PropertyStore) storageProperties.get(key);
   }

   public void put(String key, PropertyStore store) {
      if (store == null) {
         storageProperties.remove(key);
      } else {
         storageProperties.put(key, store);
      }
   }

   public void put(String key, String[] value) {
      if (value == null) {
         value = ArrayUtils.EMPTY_STRING_ARRAY;
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

   public void put(String key, String value) {
      if (value == null) {
         value = StringUtils.EMPTY;
      }
      storageData.setProperty(key, value);
   }

   public void put(String key, boolean value) {
      put(key, String.valueOf(value));
   }

   public String getId() {
      return storeId;
   }

   protected void setId(String name) {
      if (name == null) {
         name = StringUtils.EMPTY;
      }
      this.storeId = name;
   }

   protected Properties getItems() {
      return storageData;
   }

   protected Properties getArrays() {
      return storageArrays;
   }

   protected Properties getPropertyStores() {
      return storageProperties;
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(String.format("Id:[%s] Data:%s Arrays:{", getId(), storageData.toString()));

      int cnt = 0;
      for (Object key : new TreeSet<Object>(storageArrays.keySet())) {
         if (cnt != 0) {
            builder.append(" ");
         }
         builder.append(key);
         builder.append("=");
         builder.append(Arrays.deepToString((String[]) storageArrays.get(key)));
         cnt++;
         if (cnt < storageArrays.size()) {
            builder.append(",");
         }
      }
      builder.append("}");
      return builder.toString();
   }

   public void load(String input) throws Exception {
      PropertyStoreWriter storeWriter = new PropertyStoreWriter();
      storeWriter.load(this, new StringReader(input));
   }

   public void load(InputStream inputStream) throws Exception {
      PropertyStoreWriter storeWriter = new PropertyStoreWriter();
      storeWriter.load(this, inputStream);
   }

   public void load(Reader reader) throws Exception {
      PropertyStoreWriter storeWriter = new PropertyStoreWriter();
      storeWriter.load(this, reader);
   }

   public String save() throws Exception {
      StringWriter writer = new StringWriter();
      save(writer);
      return writer.toString();
   }

   public void save(Writer writer) throws Exception {
      PropertyStoreWriter storeWriter = new PropertyStoreWriter();
      storeWriter.save(this, writer);
   }

   public void save(OutputStream outputStream) throws Exception {
      PropertyStoreWriter storeWriter = new PropertyStoreWriter();
      storeWriter.save(this, outputStream);
   }

   @SuppressWarnings("unchecked")
   public Set<String> arrayKeySet() {
      List<String> items = Collections.castAll(this.storageArrays.keySet());
      return Collections.toSet(items);
   }

   @SuppressWarnings("unchecked")
   public Set<String> keySet() {
      List<String> items = Collections.castAll(this.storageData.keySet());
      return Collections.toSet(items);
   }

   @SuppressWarnings("unchecked")
   public Set<String> innerStoresKeySet() {
      List<String> items = Collections.castAll(this.storageProperties.keySet());
      return Collections.toSet(items);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof PropertyStore) {
         PropertyStore castObj = (PropertyStore) obj;
         return castObj.storeId.equals(storeId) && castObj.storageData.equals(storageData) && areStorageArraysEqual(castObj);
      }
      return false;
   }

   private boolean areStorageArraysEqual(PropertyStore other) {
      boolean result = other.storageArrays.size() == storageArrays.size();
      if (result) {
         for (Entry<Object, Object> expectedEntry : storageArrays.entrySet()) {
            Object expectedValue = expectedEntry.getValue();
            Object actualValue = other.storageArrays.get(expectedEntry.getKey());
            String[] expArray = (String[]) expectedValue;
            String[] actualArray = (String[]) actualValue;
            result &= checkArrays(expArray, actualArray);
            if (!result) {
               break;
            }
         }
      }
      return result;
   }

   private boolean checkArrays(String[] expArray, String[] actualArray) {
      boolean result = expArray.length == actualArray.length;
      if (result) {
         for (int index = 0; index < expArray.length; index++) {
            result &= expArray[index].equals(actualArray[index]);
         }
      }
      return result;
   }

   @Override
   public int hashCode() {
      int result = 17;
      result = 31 * result + storeId.hashCode();
      result = 31 * result + getPropertiesHashCode(storageData);
      result = 31 * result + getPropertiesHashCode(storageArrays);
      return result;
   }

   public int getPropertiesHashCode(Properties properties) {
      int result = 0;
      for (Entry<Object, Object> entry : properties.entrySet()) {
         result += entry.getKey().hashCode();
         Object value = entry.getValue();
         if (value instanceof String[]) {
            result += Arrays.deepHashCode((String[]) value);
         } else {
            result += value.hashCode();
         }
      }
      return result;
   }

   @Override
   public boolean isEmpty() {
      return keySet().isEmpty() && innerStoresKeySet().isEmpty() && arrayKeySet().isEmpty();
   }

}
