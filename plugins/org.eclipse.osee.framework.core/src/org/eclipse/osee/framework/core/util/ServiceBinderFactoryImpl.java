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
package org.eclipse.osee.framework.core.util;

import org.eclipse.osee.framework.core.util.ServiceDependencyTracker.ServiceBinderFactory;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Roberto E. Escobar
 */
public final class ServiceBinderFactoryImpl implements ServiceBinderFactory {
   private final AbstractServiceBinder singleBinder;
   private final AbstractServiceBinder multiBinder;

   public ServiceBinderFactoryImpl(BundleContext context, AbstractTrackingHandler handler) {
      super();
      singleBinder = new SingletonServiceBinder(context, handler);
      multiBinder = new MultiServiceBinder(context, handler);
   }

   @Override
   public ServiceTracker createTracker(ServiceBindType bindType, Class<?> clazz) {
      AbstractServiceBinder binder = null;
      if (ServiceBindType.SINGLETON == bindType) {
         binder = singleBinder;
      } else if (ServiceBindType.MANY == bindType) {
         binder = multiBinder;
      }
      if (binder == null) {
         throw new IllegalStateException(String.format("Unknown bind type: [%s:%s]", clazz.getName(), bindType));
      }
      return binder.createTracker(clazz);
   }
}