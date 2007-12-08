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
package org.eclipse.osee.framework.ui.skynet.blam;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.activation.ActivationException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.update.core.IFeature;
import org.eclipse.update.core.ISite;
import org.eclipse.update.core.ISiteFeatureReference;
import org.eclipse.update.core.SiteManager;
import org.eclipse.update.operations.IOperationFactory;
import org.eclipse.update.operations.OperationsManager;

/**
 * @author Ryan D. Brooks
 */
public abstract class DynamicBlamOperation {
   private Method mainMethod;
   private BlamParameter[] parameters;

   public void installAssociatedPlugin() throws MalformedURLException, CoreException {
      IOperationFactory operationFactory = OperationsManager.getOperationFactory();
      ISite site = SiteManager.getSite(new URL("http://www.eclipse.org/osee/"), null);
      ISiteFeatureReference[] refs = site.getFeatureReferences();
      IFeature feature = refs[0].getFeature(null);
      operationFactory.createInstallOperation(site.getCurrentConfiguredSite(), feature, null, null, null);
   }

   public void init() throws ActivationException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
      mainMethod = findMainMethod(getMainMethodName());

      String[] parameterNames = getParameterNames();
      Class<?>[] parameterTypes = mainMethod.getParameterTypes();
      if (parameterNames.length != parameterTypes.length) {
         throw new ActivationException(
               "The method " + getMainMethodName() + " has " + parameterTypes.length + " parameters, but " + parameterNames.length + " parameter names.");
      }

      parameters = new BlamParameter[parameterTypes.length];
      for (int i = 0; i < parameterTypes.length; i++) {
         parameters[i] = new BlamParameter(parameterNames[i], parameterTypes[i], null);
      }

   }

   public void executeOperation(Object[] actualParameters) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      mainMethod.invoke(this, actualParameters);
   }

   private Method findMainMethod(String mainMethodName) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
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
