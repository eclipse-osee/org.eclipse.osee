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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M. Finkbeiner
 * @author Roberto E. Escobar
 */
public final class ServiceDependencyTracker {

   private final Class<?>[] dependencies;
   private final BundleContext context;
   private final AbstractTrackingHandler handler;
   private final Map<Class<?>, Object> services;
   private final List<ServiceTracker> trackers;

   @SuppressWarnings("unchecked")
   public ServiceDependencyTracker(BundleContext context, AbstractTrackingHandler handler, Class... serviceClasses) {
      this.services = new ConcurrentHashMap<Class<?>, Object>();
      this.trackers = new ArrayList<ServiceTracker>();
      this.dependencies = serviceClasses;
      this.context = context;
      this.handler = handler;
   }

   public void open() {
      if (dependencies != null) {
         for (Class<?> clazz : dependencies) {
            services.put(clazz, null);
            ServiceTracker internalTracker = new InternalServiceTracker(getBundleContext(), clazz);
            internalTracker.open(true);
            trackers.add(internalTracker);
         }
      }
   }

   public void close() {
      handler.onDeActivate();
      for (ServiceTracker tracker : trackers) {
         tracker.close();
      }
   }

   private BundleContext getBundleContext() {
      return context;
   }

   private void onAdd(Class<?> classKey, Object service) {
      services.put(classKey, service);
      if (areServicesReady()) {
         handler.onActivate(getBundleContext(), services);
      }
   }

   private boolean areServicesReady() {
      for (Object service : services.values()) {
         if (service == null) {
            return false;
         }
      }
      return true;
   }

   private void onRemove(Class<?> classKey) {
      handler.onDeActivate();
      services.remove(classKey);
   }

   private final class InternalServiceTracker extends ServiceTracker {
      private final Class<?> serviceClass;

      public InternalServiceTracker(BundleContext context, Class<?> serviceClass) {
         super(context, serviceClass.getName(), null);
         this.serviceClass = serviceClass;
      }

      @Override
      public Object addingService(ServiceReference reference) {
         Object object = super.addingService(reference);
         onAdd(serviceClass, object);
         return object;
      }

      @Override
      public void removedService(ServiceReference reference, Object service) {
         onRemove(serviceClass);
         super.removedService(reference, service);
      }
   }

}
