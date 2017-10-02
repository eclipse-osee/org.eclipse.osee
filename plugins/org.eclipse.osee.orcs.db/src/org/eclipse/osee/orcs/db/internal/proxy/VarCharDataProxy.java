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

import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__MAX_VARCHAR_LENGTH;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.core.ds.CharacterDataProxy;
import org.eclipse.osee.orcs.core.ds.ResourceNameResolver;

/**
 * @author Roberto E. Escobar
 */
public class VarCharDataProxy<T> extends AbstractDataProxy<T> implements CharacterDataProxy<T> {
   private T rawValue;

   @Override
   public String getDisplayableString()  {
      return getValueAsString();
   }

   @Override
   public void setDisplayableString(String toDisplay) {
      throw new UnsupportedOperationException();
   }

   @Override
   public T getValue() {
      String fromStorage = getFromStorage();
      T toReturn = null;
      if (fromStorage != null) {
         return getAttribute().convertStringToValue(fromStorage);
      } else if (rawValue != null) {
         return rawValue;
      }
      return toReturn;
   }

   @Override
   public String getValueAsString()  {
      String fromStorage = getFromStorage();
      String toReturn = null;
      if (fromStorage != null) {
         toReturn = fromStorage;
      } else if (rawValue != null) {
         toReturn = rawValue.toString();
      }
      return toReturn != null ? toReturn : "";
   }

   @Override
   public boolean setValue(T value)  {
      boolean response = false;
      Object currentValue;
      if (value instanceof String) {
         currentValue = getValueAsString();
      } else {
         currentValue = getValue();
      }

      if (currentValue == value || currentValue != null && currentValue.equals(value)) {
         response = false;
      } else {
         storeValue(value);
         response = true;
      }
      return response;
   }

   private String getFromStorage()  {
      String fromStorage = null;
      byte[] data = null;
      try {
         data = getStorage().getContent();
         if (data != null) {
            data = Lib.decompressBytes(new ByteArrayInputStream(data));
            fromStorage = new String(data, "UTF-8");
         }
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return fromStorage;
   }

   private void storeValue(T value)  {

      if (value != null && value instanceof String && ((String) value).length() > JDBC__MAX_VARCHAR_LENGTH) {
         ResourceNameResolver resolver = getResolver();
         Conditions.checkNotNull(resolver, "ResourceNameResolver", "Unable to determine internal file name");
         try {
            byte[] compressed = Lib.compressStream(new ByteArrayInputStream(((String) value).getBytes("UTF-8")),
               resolver.getInternalFileName());
            getStorage().setContent(compressed, "zip", "application/zip", "ISO-8859-1");
            this.rawValue = null;
         } catch (IOException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      } else {
         this.rawValue = value;
         getStorage().clear();
      }
   }

   @Override
   public T getRawValue() {
      return rawValue;
   }

   @Override
   public String getStorageString() {
      if (rawValue == null) {
         return "";
      } else {
         return getAttribute().convertToStorageString(rawValue);
      }
   }

   @Override
   public void setData(T value, String uri) {
      storeValue(value);
      getStorage().setLocator(uri);
   }
}