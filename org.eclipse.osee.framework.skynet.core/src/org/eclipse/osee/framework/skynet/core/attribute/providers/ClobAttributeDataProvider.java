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
package org.eclipse.osee.framework.skynet.core.attribute.providers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeResourceProcessor;
import org.eclipse.osee.framework.skynet.core.attribute.utils.BinaryContentUtils;

/**
 * @author Roberto E. Escobar
 */
public class ClobAttributeDataProvider extends AbstractAttributeDataProvider implements ICharacterAttributeDataProvider {
   private static final int MAX_VARCHAR_LENGTH = 4000;
   private String rawStringValue;

   private DataStore dataStore;

   /**
    * @param attributeStateManager
    */
   public ClobAttributeDataProvider(Attribute<?> attribute) {
      super(attribute);
      this.dataStore = new DataStore(new AttributeResourceProcessor(attribute));
      this.rawStringValue = "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.AbstractAttributeDataProvider#getDisplayableString()
    */
   @Override
   public String getDisplayableString() throws OseeDataStoreException {
      return getValueAsString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.AbstractAttributeDataProvider#setDisplayableString(java.lang.String)
    */
   @Override
   public void setDisplayableString(String toDisplay) throws OseeDataStoreException {
      throw new UnsupportedOperationException();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.ICharacterAttributeDataProvider#getValueAsString()
    */
   @Override
   public String getValueAsString() throws OseeDataStoreException {
      String fromStorage = null;
      byte[] data = null;
      try {
         data = dataStore.getContent();
         if (data != null) {
            data = Lib.decompressBytes(new ByteArrayInputStream(data));
            fromStorage = new String(data, "UTF-8");
         }
      } catch (Exception ex) {
         throw new OseeDataStoreException("Error retrieving data.", ex);
      }
      String toReturn = fromStorage != null ? fromStorage : rawStringValue;
      return toReturn != null ? toReturn : "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.ICharacterAttributeDataProvider#setValue(java.lang.String)
    */
   @Override
   public boolean setValue(String value) throws OseeDataStoreException {
      boolean response = false;
      try {
         if (getValueAsString() == value || (getValueAsString() != null && getValueAsString().equals(value))) {
            response = false;
         } else {
            storeValue(value);
            response = true;
         }
      } catch (Exception ex) {
         throw new OseeDataStoreException("Error comitting data.", ex);
      }
      return response;
   }

   public String getInternalFileName() {
      return BinaryContentUtils.generateFileName(getAttribute());
   }

   private void storeValue(String value) throws IOException {
      if (value != null && value.length() > MAX_VARCHAR_LENGTH) {
         byte[] compressed =
               Lib.compressStream(new ByteArrayInputStream(value.getBytes("UTF-8")), getInternalFileName());
         dataStore.setContent(compressed, "zip", "application/zip", "ISO-8859-1");
      } else {
         this.rawStringValue = value;
         dataStore.clear();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.IDataAccessObject#getData()
    */
   @Override
   public Object[] getData() throws OseeDataStoreException {
      return new Object[] {rawStringValue, dataStore.getLocator()};
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.IDataAccessObject#loadData(java.lang.Object[])
    */
   @Override
   public void loadData(Object... objects) throws OseeDataStoreException {
      try {
         if (objects != null && objects.length > 1) {
            storeValue((String) objects[0]);
            dataStore.setLocator((String) objects[1]);
         }
      } catch (IOException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.IDataAccessObject#persist()
    */
   @Override
   public void persist(int storageId) throws OseeDataStoreException, OseeAuthenticationRequiredException {
      dataStore.persist(storageId);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider#purge()
    */
   @Override
   public void purge() throws OseeDataStoreException {
      dataStore.purge();
   }
}
