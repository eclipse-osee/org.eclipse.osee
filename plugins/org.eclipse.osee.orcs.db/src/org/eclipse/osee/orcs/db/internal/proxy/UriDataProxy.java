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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.core.ds.BinaryDataProxy;
import org.eclipse.osee.orcs.core.ds.CharacterDataProxy;
import org.eclipse.osee.orcs.core.ds.ResourceNameResolver;

/**
 * @author Roberto E. Escobar
 */
public class UriDataProxy extends AbstractDataProxy implements CharacterDataProxy, BinaryDataProxy {
   private String displayable;

   public UriDataProxy() {
      super();
      this.displayable = "";
   }

   @Override
   public Object getRawValue() {
      return "";
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
   public boolean setValue(ByteBuffer data) {
      boolean response = false;
      try {
         ByteBuffer original = getValueAsBytes();
         if (original != null && data == null || original == null && data != null || //
            original != null && !original.equals(data)) {
            if (data != null) {
               ResourceNameResolver resolver = getResolver();
               Conditions.checkNotNull(resolver, "ResourceNameResolver", "Unable to determine internal file name");

               byte[] compressed =
                  Lib.compressStream(Lib.byteBufferToInputStream(data), resolver.getInternalFileName());
               getStorage().setContent(compressed, "zip", "application/zip", "ISO-8859-1");
            } else {
               getStorage().setContent(null, "txt", "txt/plain", "UTF-8");
            }
            response = true;
         }
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return response;
   }

   @Override
   public ByteBuffer getValueAsBytes() {
      ByteBuffer decompressed = null;
      byte[] rawData = getStorage().getContent();
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
   public String getValueAsString() {
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
   public boolean setValue(Object value) {
      ByteBuffer toSet = null;
      if (value != null && value instanceof String) {
         try {
            toSet = ByteBuffer.wrap(((String) value).getBytes("UTF-8"));
         } catch (UnsupportedEncodingException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      } else if (value != null) {
         OseeCoreException.wrapAndThrow(new UnsupportedEncodingException(
            String.format("Unsupported type [%s]", value.getClass().toGenericString())));
      }
      return setValue(toSet);
   }

   @Override
   public void setData(Object value, String uri) {
      getStorage().setLocator(uri);
   }

   @Override
   public Object getValue() {
      return getValueAsString();
   }

   @Override
   public String getStorageString() {
      return "";
   }
}