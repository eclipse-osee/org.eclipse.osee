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
package org.eclipse.osee.orcs.core.internal.proxy;

import java.lang.reflect.InvocationHandler;
import org.eclipse.osee.framework.core.data.Readable;
import org.eclipse.osee.framework.core.data.Writeable;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public abstract class ProxyFactory<P, R extends Readable, W extends Writeable> {

   private final InvocationHandlerFactory<P> factory;
   private final Class<P> proxyObjectClass;
   private final Class<R> readableClass;
   private final Class<W> writeableClass;

   protected ProxyFactory(InvocationHandlerFactory<P> factory, Class<P> proxyObjectClass, Class<R> readableClass, Class<W> writeableClass) {
      super();
      this.factory = factory;
      this.proxyObjectClass = proxyObjectClass;
      this.readableClass = readableClass;
      this.writeableClass = writeableClass;
   }

   public R createReadable(P toProxy) {
      InvocationHandler handler = factory.createReadHandler(toProxy);
      return ProxyUtil.create(readableClass, handler);
   }

   public W createWriteable(P toProxy) {
      InvocationHandler handler = factory.createWriteHandler(toProxy);
      return ProxyUtil.create(writeableClass, handler);
   }

   @SuppressWarnings("unchecked")
   public P getProxiedObject(Readable object) {
      P toReturn = null;
      if (object != null && proxyObjectClass.isAssignableFrom(object.getClass())) {
         toReturn = proxyObjectClass.cast(object);
      } else {
         if (ProxyUtil.isProxy(object)) {
            if (object instanceof HasProxiedObject) {
               HasProxiedObject<P> proxy = ((HasProxiedObject<P>) object);
               toReturn = proxy.getProxiedObject();
            }
         }
      }
      return toReturn;
   }

   public P getOriginalObject(Writeable object) throws OseeCoreException {
      P toReturn = null;
      if (ProxyUtil.isProxy(object)) {
         ProxyWriteable<P> proxy = asProxyWriteable(object);
         if (proxy != null) {
            toReturn = proxy.getOriginalObject();
         }
      }
      return toReturn;
   }

   @SuppressWarnings("unchecked")
   public ProxyWriteable<P> asProxyWriteable(Writeable object) throws OseeCoreException {
      ProxyWriteable<P> toReturn = null;
      if (ProxyUtil.isProxy(object)) {
         if (object instanceof ProxyWriteable) {
            toReturn = ((ProxyWriteable<P>) object);
         }
      }
      if (toReturn == null) {
         throw new OseeArgumentException("Unable to convert from [%s] to ProxyWriteable",
            object != null ? object.getClass().getSimpleName() : "null");
      }
      return toReturn;
   }
}
