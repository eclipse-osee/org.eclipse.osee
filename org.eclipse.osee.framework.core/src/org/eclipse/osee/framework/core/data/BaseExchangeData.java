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
package org.eclipse.osee.framework.core.data;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Properties;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.type.PropertyStoreWriter;

public class BaseExchangeData implements Serializable {
   private static final long serialVersionUID = -3844333805269321833L;
   protected final PropertyStore backingData;

   public BaseExchangeData() {
      super();
      this.backingData = new PropertyStore(Integer.toString(this.hashCode()));
   }

   protected String getString(String key) {
      return backingData.get(key);
   }

   /**
    * Set data from XML input stream
    * 
    * @param xml inputStream
    * @throws OseeWrappedException
    */
   protected void loadfromXml(InputStream inputStream) throws OseeWrappedException {
      try {
         PropertyStoreWriter writer = new PropertyStoreWriter();
         writer.load(backingData, inputStream);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   /**
    * Write to output stream
    * 
    * @param outputStream
    * @throws OseeWrappedException
    */
   public void write(OutputStream outputStream) throws OseeWrappedException {
      try {
         PropertyStoreWriter writer = new PropertyStoreWriter();
         writer.save(backingData, outputStream);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   @Override
   public String toString() {
      return this.backingData.toString();
   }

   protected void putProperties(String fieldName, Properties properties) {
      for (Object theKey : properties.keySet()) {
         String keyStr = String.format("%s.%s", fieldName, theKey);
         backingData.put(keyStr, properties.getProperty((String) theKey));
      }
   }

   protected Properties getPropertyString(String fieldName) {
      String prefix = fieldName + ".";
      Properties toReturn = new Properties();
      for (String key : backingData.keySet()) {
         if (key.startsWith(prefix)) {
            String normalizedKey = key.substring(prefix.length(), key.length());
            toReturn.put(normalizedKey, backingData.get(key));
         }
      }
      return toReturn;
   }
}
