/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.internal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class ServiceUtility {

   public static ConsoleDebugSupport getConsoleDebugSupport() {
      return getService(ConsoleDebugSupport.class);
   }

   private static Class<ServiceUtility> getClazz() {
      return ServiceUtility.class;
   }

   private static <T> T getService(Class<T> clazz) {
      BundleContext context = getContext();
      if (context == null) {
         return null;
      }
      ServiceReference serviceReference = context.getServiceReference(clazz.getName());
      if (serviceReference == null) {
         return null;
      }
      return (T) getContext().getService(serviceReference);
   }

   private static <T> T[] getServices(Class<T> clazz) throws InvalidSyntaxException {
      ServiceReference[] serviceReferences = getContext().getServiceReferences(clazz.getName(), null);
      T[] data = (T[]) new Object[serviceReferences.length];
      for (int i = 0; i < serviceReferences.length; i++) {
         data[i] = (T) getContext().getService(serviceReferences[i]);
      }
      return data;
   }

   public static BundleContext getContext() {
      return FrameworkUtil.getBundle(getClazz()).getBundleContext();
   }
}
