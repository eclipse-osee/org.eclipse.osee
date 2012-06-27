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
import java.lang.reflect.Proxy;
import org.eclipse.osee.framework.core.data.Writeable;

public final class ProxyUtil {

   private ProxyUtil() {
      // utility
   }

   public static boolean isProxy(Object object) {
      return object != null && Proxy.isProxyClass(object.getClass());
   }

   @SuppressWarnings("unchecked")
   public static <T> T create(Class<T> type, InvocationHandler handler) {
      Class<?>[] types;
      if (Writeable.class.isAssignableFrom(type)) {
         types = new Class<?>[] {ProxyWriteable.class, type};
      } else {
         types = new Class<?>[] {HasProxiedObject.class, type};
      }
      return (T) Proxy.newProxyInstance(ProxyUtil.class.getClassLoader(), types, handler);
   }
}
