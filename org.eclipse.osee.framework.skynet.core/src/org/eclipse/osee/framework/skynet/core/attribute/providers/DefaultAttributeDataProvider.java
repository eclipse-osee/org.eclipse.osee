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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeResourceProcessor;
import org.eclipse.osee.framework.skynet.core.attribute.utils.BinaryContentUtils;

/**
 * @author Roberto E. Escobar
 */
public class DefaultAttributeDataProvider extends AbstractAttributeDataProvider implements ICharacterAttributeDataProvider {

   private static final int MAX_VARCHAR_LENGTH = 4000;
   private String rawStringValue;

   private final DataStore dataStore;

   /**
    * @param attributeStateManager
    */
   public DefaultAttributeDataProvider(Attribute<?> attribute) {
      super(attribute);
      this.dataStore = new DataStore(new AttributeResourceProcessor(attribute));
      this.rawStringValue = "";
   }

   @Override
   public String getDisplayableString() throws OseeCoreException {
      return getValueAsString();
   }

   @Override
   public void setDisplayableString(String toDisplay) throws OseeDataStoreException {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getValueAsString() throws OseeCoreException {
      String fromStorage = null;
      byte[] data = null;
      try {
         data = dataStore.getContent();
         if (data != null) {
            data = Lib.decompressBytes(new ByteArrayInputStream(data));
            fromStorage = new String(data, "UTF-8");
         }
      } catch (IOException ex) {
         throw new OseeWrappedException("Error retrieving data.", ex);
      }
      String toReturn = fromStorage != null ? fromStorage : rawStringValue;
      return toReturn != null ? toReturn : "";
   }

   @Override
   public boolean setValue(String value) throws OseeCoreException {
      boolean response = false;
      if (getValueAsString() == value || (getValueAsString() != null && getValueAsString().equals(value))) {
         response = false;
      } else {
         storeValue(value);
         response = true;
      }
      return response;
   }

   private String getInternalFileName() throws OseeCoreException {
      return BinaryContentUtils.generateFileName(getAttribute());
   }

   private void storeValue(String value) throws OseeCoreException {
      if (value != null && value.length() > MAX_VARCHAR_LENGTH) {
         try {
            byte[] compressed =
                  Lib.compressStream(new ByteArrayInputStream(value.getBytes("UTF-8")), getInternalFileName());
            dataStore.setContent(compressed, "zip", "application/zip", "ISO-8859-1");
         } catch (IOException ex) {
            throw new OseeWrappedException(ex);
         }
      } else {
         this.rawStringValue = value;
         dataStore.clear();
      }
   }

   @Override
   public Object[] getData() throws OseeDataStoreException {
      return new Object[] {rawStringValue, dataStore.getLocator()};
   }

   @Override
   public void loadData(Object... objects) throws OseeCoreException {
      if (objects != null && objects.length > 1) {
         storeValue((String) objects[0]);
         dataStore.setLocator((String) objects[1]);
      }
   }

   @Override
   public void persist(int storageId) throws OseeDataStoreException, OseeAuthenticationRequiredException {
      dataStore.persist(storageId);
   }

   @Override
   public void purge() throws OseeCoreException {
      dataStore.purge();
   }
}
