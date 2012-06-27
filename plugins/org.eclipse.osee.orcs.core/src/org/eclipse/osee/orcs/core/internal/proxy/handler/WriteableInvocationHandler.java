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
package org.eclipse.osee.orcs.core.internal.proxy.handler;

import java.lang.reflect.Method;
import org.eclipse.osee.framework.core.exception.OseeAccessDeniedException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.internal.proxy.ProxyWriteable;

/**
 * @author Roberto E. Escobar
 */
public class WriteableInvocationHandler<T> extends ReadableInvocationHandler<T> implements ProxyWriteable<T> {

   private static final String[] WRITE_PREFIXES = new String[] {"set", "create", "delete", "getwriteable", "reset"};

   private T original;
   private volatile boolean isWriteAllowed;
   private volatile boolean isCopyRequired;

   public WriteableInvocationHandler(T proxied) {
      super(proxied);
      isWriteAllowed = true;
      setProxiedObject(proxied);
   }

   @Override
   public void setProxiedObject(T proxied) {
      this.proxied = proxied;
      this.original = proxied;
      this.isCopyRequired = true;
   }

   @Override
   public T getOriginalObject() {
      return original;
   }

   @Override
   public void setWritesAllowed(boolean isWriteAllowed) {
      this.isWriteAllowed = isWriteAllowed;
   }

   @Override
   public boolean isWriteAllowed() {
      return isWriteAllowed;
   }

   @Override
   protected Object invokeOnDelegate(T target, Method method, Object[] args) throws Throwable {
      T proxied;
      if (isWriteMethod(method, args)) {
         proxied = getObjectForWrite();
      } else {
         proxied = getProxiedObject();
      }
      return super.invokeOnDelegate(proxied, method, args);
   }

   private boolean isWriteMethod(Method method, Object[] args) {
      boolean result = false;
      String name = method.getName().toLowerCase();
      for (String prefix : WRITE_PREFIXES) {
         result = name.startsWith(prefix);
         if (result) {
            break;
         }
      }
      return result;
   }

   protected boolean isCopyRequired() {
      return isCopyRequired;
   }

   private synchronized T getObjectForWrite() throws OseeCoreException {
      if (!isWriteAllowed()) {
         throw new OseeAccessDeniedException("Write violation - The object being accessed has been invalidated");
      }
      if (isCopyRequired()) {
         boolean successful = false;
         try {
            T copy = createCopyForWrite(getOriginalObject());
            proxied = copy;
            isCopyRequired = false;
            successful = true;
         } finally {
            setWritesAllowed(successful);
         }
      }
      return getProxiedObject();
   }

   @SuppressWarnings("unused")
   protected T createCopyForWrite(T original) throws OseeCoreException {
      return getProxiedObject();
   }

}