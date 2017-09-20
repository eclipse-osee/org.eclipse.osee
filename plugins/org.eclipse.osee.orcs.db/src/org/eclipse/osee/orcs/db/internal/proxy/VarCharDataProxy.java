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
import java.io.ObjectInputStream;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.core.ds.CharacterDataProxy;
import org.eclipse.osee.orcs.core.ds.ResourceNameResolver;

/**
 * @author Roberto E. Escobar
 */
public class VarCharDataProxy extends AbstractDataProxy implements CharacterDataProxy {
   private Object rawValue;

   public VarCharDataProxy() {
      super();
      this.rawValue = "";
   }

   @Override
   public String getDisplayableString() throws OseeCoreException {
      return getValueAsString();
   }

   @Override
   public void setDisplayableString(String toDisplay) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Object getValue() {
      Object fromStorage = getFromStorageAsObject();
      Object toReturn = null;
      if (fromStorage != null) {
         toReturn = fromStorage;
      } else if (rawValue != null) {
         toReturn = rawValue;
      }
      return toReturn != null ? toReturn : "";
   }

   @Override
   public String getValueAsString() throws OseeCoreException {
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
   public boolean setValue(Object value) throws OseeCoreException {
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

   private Object getFromStorageAsObject() throws OseeCoreException {
      Object fromStorage = null;
      byte[] data = null;
      try {
         data = getStorage().getContent();
         if (data != null) {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            try {
               fromStorage = is.readObject();
            } catch (ClassNotFoundException ex) {
               OseeCoreException.wrapAndThrow(ex);
            }
         }
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return fromStorage;
   }

   private String getFromStorage() throws OseeCoreException {
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

   private void storeValue(Object value) throws OseeCoreException {

      if (value != null && value instanceof String && ((String) value).length() > JDBC__MAX_VARCHAR_LENGTH) {
         ResourceNameResolver resolver = getResolver();
         Conditions.checkNotNull(resolver, "ResourceNameResolver", "Unable to determine internal file name");
         try {
            byte[] compressed = Lib.compressStream(new ByteArrayInputStream(((String) value).getBytes("UTF-8")),
               resolver.getInternalFileName());
            getStorage().setContent(compressed, "zip", "application/zip", "ISO-8859-1");
            this.rawValue = "";
         } catch (IOException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      } else {
         this.rawValue = value;
         getStorage().clear();
      }
   }

   @Override
   public Object getRawValue() {
      return rawValue;
   }

   @Override
   public void setData(Object value, String uri) {
      storeValue(value);
      getStorage().setLocator(uri);
   }
}