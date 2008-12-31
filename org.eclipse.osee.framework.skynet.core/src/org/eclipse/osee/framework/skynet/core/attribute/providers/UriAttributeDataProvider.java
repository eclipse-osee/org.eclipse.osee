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
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeResourceProcessor;
import org.eclipse.osee.framework.skynet.core.attribute.utils.AbstractResourceProcessor;
import org.eclipse.osee.framework.skynet.core.attribute.utils.BinaryContentUtils;

/**
 * @author Roberto E. Escobar
 */
public class UriAttributeDataProvider extends AbstractAttributeDataProvider implements ICharacterAttributeDataProvider, IBinaryAttributeDataProvider {
   private DataStore dataStore;
   private String displayable;

   public UriAttributeDataProvider(Attribute<?> attribute) {
      super(attribute);
      AbstractResourceProcessor abstractResourceProcessor = new AttributeResourceProcessor(attribute);
      this.dataStore = new DataStore(abstractResourceProcessor);
      this.displayable = "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.IAttributeDataProvider#getDisplayableString()
    */
   @Override
   public String getDisplayableString() throws OseeDataStoreException {
      return displayable;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.IAttributeDataProvider#setDisplayableString(java.lang.String)
    */
   @Override
   public void setDisplayableString(String toDisplay) throws OseeDataStoreException {
      this.displayable = toDisplay;
   }

   private String getInternalFileName() throws OseeCoreException {
      return BinaryContentUtils.generateFileName(getAttribute());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.IAttributeDataProvider#setValue(ByteBuffer)
    */
   @Override
   public boolean setValue(ByteBuffer data) throws OseeCoreException {
      boolean response = false;
      try {
         if (!Arrays.equals(dataStore.getContent(), data != null ? data.array() : null)) {
            if (data != null) {
               byte[] compressed;
               compressed = Lib.compressStream(Lib.byteBufferToInputStream(data), getInternalFileName());
               dataStore.setContent(compressed, "zip", "application/zip", "ISO-8859-1");
               response = true;
            } else {
               String loc = dataStore.getLocator();
               dataStore.clear();
               dataStore.setLocator(loc);
            }
         }
      } catch (IOException ex) {
         throw new OseeWrappedException("Error committing data. ", ex);
      }
      return response;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.IAttributeDataProvider#getValueAsBytes()
    */
   @Override
   public ByteBuffer getValueAsBytes() throws OseeCoreException {
      ByteBuffer decompressed = null;
      byte[] rawData = dataStore.getContent();
      if (rawData != null) {
         try {
            decompressed = ByteBuffer.wrap(Lib.decompressBytes(new ByteArrayInputStream(rawData)));
         } catch (IOException ex) {
            throw new OseeWrappedException("Error acquiring data. - ", ex);
         }
      }

      return decompressed;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.data.IStringAttributeDataProvider#getValueAsString()
    */
   @Override
   public String getValueAsString() throws OseeCoreException {
      String toReturn = null;
      ByteBuffer data = getValueAsBytes();
      if (data != null) {
         try {
            toReturn = new String(data.array(), "UTF-8");
         } catch (UnsupportedEncodingException ex) {
            throw new OseeWrappedException("Error encoding data.", ex);
         }
      } else {
         toReturn = "";
      }
      return toReturn;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.data.IStringAttributeDataProvider#setValue(java.lang.String)
    */
   @Override
   public boolean setValue(String value) throws OseeCoreException {
      ByteBuffer toSet = null;
      if (value != null) {
         try {
            toSet = ByteBuffer.wrap(value.getBytes("UTF-8"));
         } catch (UnsupportedEncodingException ex) {
            throw new OseeWrappedException("Error encoding data.", ex);
         }
      }
      setValue(toSet);
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.IDataAccessObject#getData()
    */
   @Override
   public Object[] getData() throws OseeDataStoreException {
      return new Object[] {"", dataStore.getLocator()};
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.IDataAccessObject#loadData(java.lang.Object[])
    */
   @Override
   public void loadData(Object... objects) throws OseeCoreException {
      if (objects != null && objects.length > 1) {
         dataStore.setLocator((String) objects[1]);
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
   public void purge() throws OseeCoreException {
      dataStore.purge();
   }
}
