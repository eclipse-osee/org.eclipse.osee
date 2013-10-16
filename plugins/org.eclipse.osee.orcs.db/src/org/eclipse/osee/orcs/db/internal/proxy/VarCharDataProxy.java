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
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.core.ds.CharacterDataProxy;
import org.eclipse.osee.orcs.core.ds.ResourceNameResolver;

/**
 * @author Roberto E. Escobar
 */
public class VarCharDataProxy extends AbstractDataProxy implements CharacterDataProxy {

   public static final int MAX_VARCHAR_LENGTH = 4000;
   private String rawStringValue;

   public VarCharDataProxy() {
      super();
      this.rawStringValue = "";
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
   public String getValueAsString() throws OseeCoreException {
      String fromStorage = getFromStorage();
      String toReturn = fromStorage != null ? fromStorage : rawStringValue;
      return toReturn != null ? toReturn : "";
   }

   @Override
   public boolean setValue(String value) throws OseeCoreException {
      boolean response = false;
      String currentValue = getValueAsString();
      if (currentValue == value || currentValue != null && currentValue.equals(value)) {
         response = false;
      } else {
         storeValue(value);
         response = true;
      }
      return response;
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
         OseeExceptions.wrapAndThrow(ex);
      }
      return fromStorage;
   }

   private void storeValue(String value) throws OseeCoreException {
      if (value != null && value.length() > MAX_VARCHAR_LENGTH) {
         ResourceNameResolver resolver = getResolver();
         Conditions.checkNotNull(resolver, "ResourceNameResolver", "Unable to determine internal file name");
         try {
            byte[] compressed =
               Lib.compressStream(new ByteArrayInputStream(value.getBytes("UTF-8")), resolver.getInternalFileName());
            getStorage().setContent(compressed, "zip", "application/zip", "ISO-8859-1");
            this.rawStringValue = "";
         } catch (IOException ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      } else {
         this.rawStringValue = value;
         getStorage().clear();
      }
   }

   @Override
   public Object[] getData() {
      return new Object[] {rawStringValue, getStorage().getLocator()};
   }

   @Override
   public void setData(Object... objects) throws OseeCoreException {
      if (objects != null && objects.length > 1) {
         storeValue((String) objects[0]);
         getStorage().setLocator((String) objects[1]);
      }
   }

   @Override
   public void persist(long storageId) throws OseeCoreException {
      getStorage().persist(storageId);
   }

   @Override
   public void purge() throws OseeCoreException {
      getStorage().purge();
   }
}
