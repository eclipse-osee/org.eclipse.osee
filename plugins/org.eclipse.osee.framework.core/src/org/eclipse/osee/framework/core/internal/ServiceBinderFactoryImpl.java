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
package org.eclipse.osee.framework.core.internal;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.core.util.ServiceBindType;
import org.eclipse.osee.framework.core.util.ServiceBinderFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
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
      Map<Class<?>, Collection<Object>> serviceMap = new ConcurrentHashMap<>();
      singleBinder = new SingletonServiceBinder(serviceMap, context, handler);
      multiBinder = new MultiServiceBinder(serviceMap, context, handler);
   }

   @Override
   public ServiceTracker<?, ?> createTracker(ServiceBindType bindType, Class<?> clazz)  {
      AbstractServiceBinder binder = null;
      if (ServiceBindType.SINGLETON == bindType) {
         binder = singleBinder;
      } else if (ServiceBindType.MANY == bindType) {
         binder = multiBinder;
      }
      if (binder == null) {
         throw new OseeStateException(String.format("Unknown bind type: [%s:%s]", clazz.getName(), bindType));
      }
      return binder.createTracker(clazz);
   }
}