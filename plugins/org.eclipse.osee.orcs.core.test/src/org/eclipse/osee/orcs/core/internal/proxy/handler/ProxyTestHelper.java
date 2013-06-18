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

import static org.mockito.Mockito.verify;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.junit.Assert;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.mockito.Mockito;

/**
 * @author Roberto E. Escobar
 */
public final class ProxyTestHelper {

   private final static Map<Class<?>, Object> defaultValues = new HashMap<Class<?>, Object>();
   private final static Collection<String> skipObjectMethods = getObjectMethods();
   static {
      defaultValues.put(String.class, "hello");

      defaultValues.put(Integer.class, 0);
      defaultValues.put(int.class, 0);

      defaultValues.put(Long.class, 0L);
      defaultValues.put(long.class, 0L);

      defaultValues.put(Character.class, '\0');
      defaultValues.put(char.class, '\0');

      defaultValues.put(Boolean.class, true);
      defaultValues.put(boolean.class, true);

      defaultValues.put(Float.class, 12314.02);
      defaultValues.put(float.class, 12314.02);
   }

   private static Collection<String> getObjectMethods() {
      Collection<String> skip = new HashSet<String>();
      for (Method method : Object.class.getMethods()) {
         skip.add(method.getName());
      }
      return skip;
   }

   private static Method getMethod(Object object, String name, Class<?>... paramTypes) {
      Method method = null;
      try {
         method = object.getClass().getMethod(name, paramTypes);
      } catch (Exception ex) {
         // Do Nothing;
      }
      return method;
   }

   public static <T> void checkNoneStaticMethodForwarding(Method method, T proxy, T proxiedObject, Object handler) {
      int modifiers = method.getModifiers();

      if (!Modifier.isStatic(modifiers) && !skipObjectMethods.contains(method.getName())) {
         Mockito.reset(handler);
         try {
            verifyMethodForwarding(method, proxy, proxiedObject, handler);
         } catch (Exception ex) {
            Assert.fail(String.format("Error on [%s]: [%s]\n", method.getName(), Lib.exceptionToString(ex)));
         }
      }
   }

   private static <T> void verifyMethodForwarding(Method method, T proxy, T proxiedObject, Object handler) throws Exception {
      Class<?>[] paramTypes = method.getParameterTypes();
      Object[] params = new Object[paramTypes.length];
      for (int index = 0; index < paramTypes.length; index++) {
         Class<?> type = paramTypes[index];
         params[index] = defaultValues.get(type);
      }

      method.invoke(proxy, params);

      Method methodOnMock = getMethod(proxiedObject, method.getName(), paramTypes);

      if (methodOnMock != null) {
         methodOnMock.invoke(verify(proxiedObject), params);
      } else {
         Method methodOnHandler = getMethod(handler, method.getName(), paramTypes);
         Assert.assertNotNull(methodOnHandler);
         methodOnHandler.invoke(verify(handler), params);
      }
   }
}
