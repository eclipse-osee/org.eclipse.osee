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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.utils.AttributeResourceProcessor;
import org.eclipse.osee.framework.skynet.core.attribute.utils.BinaryContentUtils;

/**
 * @author Roberto E. Escobar
 */
public class UriAttributeDataProvider extends AbstractAttributeDataProvider implements ICharacterAttributeDataProvider, IBinaryAttributeDataProvider {
   private final DataStore dataStore;
   private String displayable;

   public UriAttributeDataProvider(Attribute<?> attribute) {
      super(attribute);
      this.dataStore = new DataStore(new AttributeResourceProcessor(attribute));
      this.displayable = "";
   }

   @Override
   public String getDisplayableString() {
      return displayable;
   }

   @Override
   public void setDisplayableString(String toDisplay) {
      this.displayable = toDisplay;
   }

   private String getInternalFileName()  {
      return BinaryContentUtils.generateFileName(getAttribute());
   }

   @Override
   public boolean setValue(ByteBuffer data)  {
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
         OseeCoreException.wrapAndThrow(ex);
      }
      return response;
   }

   @Override
   public ByteBuffer getValueAsBytes()  {
      ByteBuffer decompressed = null;
      byte[] rawData = dataStore.getContent();
      if (rawData != null) {
         try {
            decompressed = ByteBuffer.wrap(Lib.decompressBytes(new ByteArrayInputStream(rawData)));
         } catch (IOException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }

      return decompressed;
   }

   @Override
   public String getValueAsString()  {
      String toReturn = null;
      ByteBuffer data = getValueAsBytes();
      if (data != null) {
         try {
            toReturn = new String(data.array(), "UTF-8");
         } catch (UnsupportedEncodingException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      } else {
         toReturn = "";
      }
      return toReturn;
   }

   @Override
   public boolean setValue(Object value)  {
      ByteBuffer toSet = null;
      if (value != null && value instanceof String) {
         try {
            toSet = ByteBuffer.wrap(((String) value).getBytes("UTF-8"));
         } catch (UnsupportedEncodingException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }
      setValue(toSet);
      return true;
   }

   @Override
   public Object[] getData() {
      return new Object[] {"", dataStore.getLocator()};
   }

   @Override
   public void loadData(Object... objects) {
      if (objects != null && objects.length > 1) {
         dataStore.setLocator((String) objects[1]);
      }
   }

   @Override
   public void persist(int storageId)  {
      dataStore.persist(storageId);
   }

   @Override
   public void purge()  {
      dataStore.purge();
   }

   @Override
   public Object getValue() {
      return getValueAsString();
   }
}
