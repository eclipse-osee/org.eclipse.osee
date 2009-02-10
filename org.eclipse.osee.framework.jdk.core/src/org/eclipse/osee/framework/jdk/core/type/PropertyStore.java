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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.xml.sax.SAXException;

/**
 * @author Roberto E. Escobar
 */
public class PropertyStore implements IPropertyStore, Serializable {
   private static final long serialVersionUID = 9076969425223251739L;

   private final String EXCEPTION_MESSAGE = "No setting found for key: [%s]";

   private String storeId;
   private Properties storageData;
   private Properties storageArrays;

   public PropertyStore(String storeId) {
      this.storeId = storeId;
      this.storageData = new Properties();
      this.storageArrays = new Properties();
   }

   public PropertyStore(Reader properties) throws IOException, SAXException, ParserConfigurationException {
      this((String) null);
      PropertyStoreWriter writer = new PropertyStoreWriter();
      writer.load(this, properties);
   }

   public PropertyStore(Properties properties) {
      this.storageData = properties;
      this.storeId = Integer.toString(properties.hashCode());
   }

   public String get(String key) {
      return (String) storageData.get(key);
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

   public void put(String key, String value) {
      if (value == null) {
         value = "";
      }
      storageData.put(key, value);
   }

   public void put(String key, boolean value) {
      put(key, String.valueOf(value));
   }

   public String getId() {
      return storeId;
   }

   protected void setId(String name) {
      this.storeId = name;
   }

   protected Properties getItems() {
      return storageData;
   }

   protected Properties getArrays() {
      return storageArrays;
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(storageData.toString().replaceAll(",", ",\n"));
      return builder.toString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.framework.data.IPropertyStore#load(java.lang.String)
    */
   public void load(String fileName) throws Exception {
      InputStream inputStream = new FileInputStream(fileName);
      PropertyStoreWriter storeWriter = new PropertyStoreWriter();
      storeWriter.load(this, inputStream);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.framework.data.IPropertyStore#save(java.lang.String)
    */
   public void save(String fileName) throws Exception {
      OutputStream outputStream = new FileOutputStream(fileName);
      PropertyStoreWriter storeWriter = new PropertyStoreWriter();
      storeWriter.save(this, outputStream);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.type.IPropertyStore#arrayKeySet()
    */
   public Set<String> arrayKeySet() {
      List<String> items = Collections.castAll(this.storageArrays.keySet());
      return Collections.toSet(items);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.type.IPropertyStore#keySet()
    */
   public Set<String> keySet() {
      List<String> items = Collections.castAll(this.storageData.keySet());
      return Collections.toSet(items);
   }
}
