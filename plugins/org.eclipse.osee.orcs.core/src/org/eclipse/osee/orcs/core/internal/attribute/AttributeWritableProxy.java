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
import org.eclipse.osee.framework.core.exception.OseeAccessDeniedException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.internal.transaction.WriteableProxy;
import org.eclipse.osee.orcs.data.AttributeWriteable;

/**
 * @author Roberto E. Escobar
 */
public class AttributeWritableProxy<T> extends AttributeReadableProxy<T> implements AttributeWriteable<T>, WriteableProxy {

   private volatile boolean isWriteAllowed;

   public AttributeWritableProxy(Attribute<T> proxied) {
      super(proxied);
   }

   @Override
   public void setWriteState(boolean isWriteAllowed) {
      this.isWriteAllowed = isWriteAllowed;
   }

   @Override
   public boolean isWriteAllowed() {
      return isWriteAllowed;
   }

   private synchronized Attribute<T> getObjectForWrite() throws OseeAccessDeniedException {
      if (!isWriteAllowed()) {
         throw new OseeAccessDeniedException("The artifact being accessed has been invalidated");
      }
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
   public boolean isDirty() throws OseeCoreException {
      return getObjectForWrite().isDirty();
   }

   @Override
   public void resetToDefaultValue() throws OseeCoreException {
      getObjectForWrite().resetToDefaultValue();
   }

   @Override
   public boolean canDelete() throws OseeCoreException {
      return getObjectForWrite().canDelete();
   }

   @Override
   public void delete() throws OseeCoreException {
      getObjectForWrite().delete();
   }

}
