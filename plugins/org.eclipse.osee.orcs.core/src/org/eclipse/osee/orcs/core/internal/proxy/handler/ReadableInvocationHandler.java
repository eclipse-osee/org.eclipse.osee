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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.eclipse.osee.orcs.core.internal.proxy.HasProxiedObject;

/**
 * @author Roberto E. Escobar
 */
public class ReadableInvocationHandler<T> implements InvocationHandler, HasProxiedObject<T> {

   protected T proxied;

   public ReadableInvocationHandler(T proxied) {
      this.proxied = proxied;
   }

   @Override
   public T getProxiedObject() {
      return proxied;
   }

   @Override
   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      Object toReturn = null;
      Method localMethod = getMethodFor(this.getClass(), method);
      if (localMethod != null) {
         toReturn = localMethod.invoke(this, args);
      } else {
         toReturn = invokeOnDelegate(proxied, method, args);
      }
      return toReturn;
   }

   protected Object invokeOnDelegate(T target, Method method, Object[] args) throws Throwable {
      return method.invoke(target, args);
   }

   private Method getMethodFor(Class<?> clazz, Method method) {
      Method toReturn = null;
      try {
         toReturn = clazz.getMethod(method.getName(), method.getParameterTypes());
      } catch (Exception ex) {
         // Do Nothing;
      }
      return toReturn;
   }
}