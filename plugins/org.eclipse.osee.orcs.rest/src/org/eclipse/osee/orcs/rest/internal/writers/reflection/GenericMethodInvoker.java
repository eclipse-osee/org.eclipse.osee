/*********************************************************************
 * Copyright (c) 2020 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.rest.internal.writers.reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author David W. Miller
 */
public class GenericMethodInvoker<T> {
   Method method;
   List<Object> arguments = new ArrayList<>();
   private final T realInstance;

   public GenericMethodInvoker(T obj) {
      this.realInstance = obj;
   }

   public Object invoke(T invoker) {
      try {
         Object[] args;
         args = arguments.toArray();
         return method.invoke(invoker, args);
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   public boolean set(String methodName, List<Object> args) {
      for (Method m : realInstance.getClass().getMethods()) {
         if (m.getName().equals(methodName)) {
            Type[] pType = m.getGenericParameterTypes();

            boolean argsMatch = false;
            if (pType.length == args.size()) {
               for (int i = 0; i < pType.length; ++i) {
                  Class<? extends Object> reflectedTypeClass = getClassForType(pType[i]);
                  if (reflectedTypeClass == null) {
                     argsMatch = false;
                     break;
                  }
                  Class<? extends Object> knownArgClass = args.get(i).getClass();
                  if (reflectedTypeClass.isAssignableFrom(knownArgClass)) {
                     argsMatch = true;
                     arguments.add(args.get(i));
                  } else {
                     if (reflectedTypeClass.isArray()) {
                        Object array = Array.newInstance(knownArgClass, 1);
                        if (reflectedTypeClass.isAssignableFrom(array.getClass())) {
                           Array.set(array, 0, args.get(i));
                           arguments.add(array);
                           argsMatch = true;
                        } else {
                           argsMatch = false;
                           break;
                        }

                     } else {
                        argsMatch = false;
                        break;
                     }
                  }
                  // if this arg matches, keep looping and check to make sure they all match
                  argsMatch = true;
               }
            }
            if (argsMatch) {
               method = m;
               return true;
            }
         }
      }
      return false;
   }

   public Class<? extends Object> getClassForType(Type type) {
      Class<? extends Object> clazz = null;
      if (type instanceof ParameterizedType) {
         clazz = getClassFromName(((ParameterizedType) type).getRawType().getTypeName());
      } else {
         clazz = getClassFromName(type.getTypeName());
      }
      return clazz;
   }

   private Class<?> getClassFromName(String name) {
      Class<?> clazz = null;
      try {
         // check for primitive types
         if (name.equals("boolean")) {
            clazz = Boolean.class;
         } else if (name.equals("int")) {
            clazz = Integer.class;
         } else if (name.equals("long")) {
            clazz = Long.class;
         } else if (name.equals("float")) {
            clazz = Float.class;
         } else if (name.equals("double")) {
            clazz = Double.class;
         } else if (name.endsWith("[]")) {
            name = name.substring(0, name.length() - 2);
            clazz = Class.forName("[L" + name + ";");
         } else {
            clazz = Class.forName(name);
         }
      } catch (ClassNotFoundException ex) {
         return clazz;
      }
      return clazz;
   }
}
