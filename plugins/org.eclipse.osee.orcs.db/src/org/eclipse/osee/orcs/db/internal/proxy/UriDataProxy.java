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
package org.eclipse.osee.orcs.db.internal.proxy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.core.ds.BinaryDataProxy;
import org.eclipse.osee.orcs.core.ds.CharacterDataProxy;

/**
 * @author Roberto E. Escobar
 */
public class UriDataProxy extends AbstractDataSourceProxy implements CharacterDataProxy, BinaryDataProxy {
   private String displayable;

   public UriDataProxy(DataStore dataStore) {
      super(dataStore);
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

   @Override
   public boolean setValue(ByteBuffer data) throws OseeCoreException {
      boolean response = false;
      try {
         if (!Arrays.equals(getDataStore().getContent(), data != null ? data.array() : null)) {
            if (data != null) {
               byte[] compressed;
               compressed = Lib.compressStream(Lib.byteBufferToInputStream(data), getDataStore().getFileName());
               getDataStore().setContent(compressed, "zip", "application/zip", "ISO-8859-1");
               response = true;
            } else {
               String loc = getDataStore().getLocator();
               getDataStore().clear();
               getDataStore().setLocator(loc);
            }
         }
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return response;
   }

   @Override
   public ByteBuffer getValueAsBytes() throws OseeCoreException {
      ByteBuffer decompressed = null;
      byte[] rawData = getDataStore().getContent();
      if (rawData != null) {
         try {
            decompressed = ByteBuffer.wrap(Lib.decompressBytes(new ByteArrayInputStream(rawData)));
         } catch (IOException ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      }

      return decompressed;
   }

   @Override
   public String getValueAsString() throws OseeCoreException {
      String toReturn = null;
      ByteBuffer data = getValueAsBytes();
      if (data != null) {
         try {
            toReturn = new String(data.array(), "UTF-8");
         } catch (UnsupportedEncodingException ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      } else {
         toReturn = "";
      }
      return toReturn;
   }

   @Override
   public boolean setValue(String value) throws OseeCoreException {
      ByteBuffer toSet = null;
      if (value != null) {
         try {
            toSet = ByteBuffer.wrap(value.getBytes("UTF-8"));
         } catch (UnsupportedEncodingException ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      }
      setValue(toSet);
      return true;
   }

   @Override
   public Object[] getData() {
      return new Object[] {"", getDataStore().getLocator()};
   }

   @Override
   public void loadData(Object... objects) {
      if (objects != null && objects.length > 1) {
         getDataStore().setLocator((String) objects[1]);
      }
   }

   @Override
   public void persist(int storageId) throws OseeCoreException {
      getDataStore().persist(storageId);
   }

   @Override
   public void purge() throws OseeCoreException {
      getDataStore().purge();
   }
}
