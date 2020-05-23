/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class PropertyStore implements IPropertyStore, Serializable {
   private static final String EMPTY_STRING = new String();
   private static final String[] EMPTY_STRING_ARRAY = new String[0];

   private static final long serialVersionUID = 9076969425223251739L;

   private static final String EXCEPTION_MESSAGE = "No setting found for key: [%s]";

   private String storeId;
   private final Map<String, Object> storageData;
   private final Map<String, Object> storageArrays;
   private final Map<String, Object> storageProperties;

   private PropertyStore(String storeId, Map<String, Object> storageData, Map<String, Object> storageArrays, Map<String, Object> storageProperties) {
      super();
      this.storeId = storeId;
      this.storageData = storageData;
      this.storageArrays = storageArrays;
      this.storageProperties = storageProperties;
   }

   public PropertyStore(String storeId) {
      this(storeId, new TreeMap<String, Object>(), new TreeMap<String, Object>(), new TreeMap<String, Object>());
   }

   public PropertyStore() {
      this(EMPTY_STRING);
   }

   public PropertyStore(Map<String, Object> properties) {
      this(Integer.toString(properties.hashCode()), properties, new TreeMap<String, Object>(),
         new TreeMap<String, Object>());
   }

   @Override
   public String get(String key) {
      String result = EMPTY_STRING;
      if (storageData.containsKey(key)) {
         result = (String) storageData.get(key);
      }
      return result;
   }

   @Override
   public String[] getArray(String key) {
      String[] value = (String[]) storageArrays.get(key);
      if (value == null) {
         value = EMPTY_STRING_ARRAY;
      }
      return value;
   }

   @Override
   public boolean getBoolean(String key) {
      return Boolean.valueOf(get(key));
   }

   @Override
   public double getDouble(String key) throws NumberFormatException {
      String setting = get(key);
      if (!Strings.isValid(setting)) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }

      return new Double(setting).doubleValue();
   }

   @Override
   public float getFloat(String key) throws NumberFormatException {
      String setting = get(key);
      if (!Strings.isValid(setting)) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }

      return new Float(setting).floatValue();
   }

   @Override
   public int getInt(String key) throws NumberFormatException {
      String setting = get(key);
      if (!Strings.isValid(setting)) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }

      return new Integer(setting).intValue();
   }

   @Override
   public long getLong(String key) throws NumberFormatException {
      String setting = get(key);
      if (!Strings.isValid(setting)) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }

      return new Long(setting).longValue();
   }

   @Override
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

   @Override
   public void put(String key, String[] value) {
      if (value == null) {
         value = EMPTY_STRING_ARRAY;
      }
      storageArrays.put(key, value);
   }

   @Override
   public void put(String key, double value) {
      put(key, String.valueOf(value));
   }

   @Override
   public void put(String key, float value) {
      put(key, String.valueOf(value));
   }

   @Override
   public void put(String key, int value) {
      put(key, String.valueOf(value));
   }

   @Override
   public void put(String key, long value) {
      put(key, String.valueOf(value));
   }

   @Override
   public void put(String key, String value) {
      if (value == null) {
         value = EMPTY_STRING;
      }
      storageData.put(key, value);
   }

   @Override
   public void put(String key, boolean value) {
      put(key, String.valueOf(value));
   }

   @Override
   public String getId() {
      return storeId;
   }

   void setId(String name) {
      if (name == null) {
         name = EMPTY_STRING;
      }
      this.storeId = name;
   }

   Map<String, Object> getItems() {
      return storageData;
   }

   Map<String, Object> getArrays() {
      return storageArrays;
   }

   Map<String, Object> getPropertyStores() {
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

   @Override
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

   @Override
   public void save(OutputStream outputStream) throws Exception {
      PropertyStoreWriter storeWriter = new PropertyStoreWriter();
      storeWriter.save(this, outputStream);
   }

   @Override
   public Set<String> arrayKeySet() {
      List<String> items = Collections.castAll(this.storageArrays.keySet());
      return Collections.toSet(items);
   }

   @Override
   public Set<String> keySet() {
      List<String> items = Collections.castAll(this.storageData.keySet());
      return Collections.toSet(items);
   }

   @Override
   public Set<String> innerStoresKeySet() {
      List<String> items = Collections.castAll(this.storageProperties.keySet());
      return Collections.toSet(items);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof PropertyStore) {
         PropertyStore castObj = (PropertyStore) obj;
         return castObj.storeId.equals(storeId) && castObj.storageData.equals(storageData) && areStorageArraysEqual(
            castObj);
      }
      return false;
   }

   private boolean areStorageArraysEqual(PropertyStore other) {
      boolean result = other.storageArrays.size() == storageArrays.size();
      if (result) {
         for (Entry<String, Object> expectedEntry : storageArrays.entrySet()) {
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

   public int getPropertiesHashCode(Map<String, Object> properties) {
      int result = 0;
      for (Entry<String, Object> entry : properties.entrySet()) {
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

   public void clear() {
      storageData.clear();
      storageArrays.clear();
   }

}
