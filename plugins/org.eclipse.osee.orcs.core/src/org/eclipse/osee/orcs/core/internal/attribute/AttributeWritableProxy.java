/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.attribute;

import java.io.InputStream;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.orcs.data.AttributeWriteable;

/**
 * @author Roberto E. Escobar
 */
public class AttributeWritableProxy<T> extends AttributeReadableProxy<T> implements AttributeWriteable<T> {

   public AttributeWritableProxy(Attribute<T> proxied) {
      super(proxied);
   }

   private synchronized Attribute<T> getObjectForWrite() {
      //      if (isCopyRequired) {
      //         try {
      //            Attribute<T> copy = getOriginal().clone();
      //            super.setProxiedObject(copy);
      //            isCopyRequired = false;
      //         } catch (CloneNotSupportedException ex) {
      //            OseeExceptions.wrapAndThrow(ex);
      //         }
      //      }
      return getProxiedObject();
   }

   @Override
   public void setValue(T value) throws OseeCoreException {
      getObjectForWrite().setValue(value);
   }

   @Override
   public boolean setFromString(String value) throws OseeCoreException {
      return getObjectForWrite().setFromString(value);
   }

   @Override
   public boolean setValueFromInputStream(InputStream value) throws OseeCoreException {
      return getObjectForWrite().setValueFromInputStream(value);
   }

   @Override
   public boolean isDirty() {
      return getObjectForWrite().isDirty();
   }

   @Override
   public void resetToDefaultValue() throws OseeCoreException {
      getObjectForWrite().resetToDefaultValue();
   }

   @Override
   public boolean canDelete() {
      return getObjectForWrite().canDelete();
   }

   @Override
   public void delete() throws OseeStateException {
      getObjectForWrite().delete();
   }

}
