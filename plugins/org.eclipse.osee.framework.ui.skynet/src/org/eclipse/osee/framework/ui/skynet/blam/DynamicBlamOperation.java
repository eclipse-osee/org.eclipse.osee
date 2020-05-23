/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.blam;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Ryan D. Brooks
 */
public abstract class DynamicBlamOperation {
   private Method mainMethod;
   private BlamParameter[] parameters;

   public void init() throws IllegalArgumentException, NoSuchMethodException, OseeArgumentException {
      mainMethod = findMainMethod(getMainMethodName());

      String[] parameterNames = getParameterNames();
      Class<?>[] parameterTypes = mainMethod.getParameterTypes();
      if (parameterNames.length != parameterTypes.length) {
         throw new OseeArgumentException("The method [%s] has %d parameters, but %d parameter names.",
            getMainMethodName(), parameterTypes.length, parameterNames.length);
      }

      parameters = new BlamParameter[parameterTypes.length];
      for (int i = 0; i < parameterTypes.length; i++) {
         parameters[i] = new BlamParameter(parameterNames[i], parameterTypes[i], null);
      }

   }

   public void executeOperation(Object[] actualParameters) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      mainMethod.invoke(this, actualParameters);
   }

   private Method findMainMethod(String mainMethodName) throws IllegalArgumentException, NoSuchMethodException {
      Class<? extends DynamicBlamOperation> classRep = getClass();
      Method[] methods = classRep.getDeclaredMethods(); // get only the methods declared directly in the given class

      for (Method method : methods) {
         if (method.getName().equals(mainMethodName)) {
            return method;
         }
      }
      throw new NoSuchMethodException(mainMethodName);
   }

   /**
    * should return user oriented names for input values in the same order that they should be passed to the operation's
    * configured execution method
    * 
    * @return array of parameter names
    */
   public abstract String[] getParameterNames();

   public abstract String getMainMethodName();

   public BlamParameter[] getParameters() {
      return parameters;
   }
}