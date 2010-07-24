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

import java.io.Closeable;
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
public final class ServiceDependencyTracker implements Closeable {
   private static final Object NULL_SERVICE = new Object();

   private final BundleContext context;
   private final AbstractTrackingHandler handler;
   private final Map<Class<?>, Object> services;
   private final List<ServiceTracker> trackers;

   public ServiceDependencyTracker(BundleContext context, AbstractTrackingHandler handler) {
      this.services = new ConcurrentHashMap<Class<?>, Object>();
      this.trackers = new ArrayList<ServiceTracker>();
      this.context = context;
      this.handler = handler;
   }

   public void open() {
      Class<?>[] dependencies = handler.getDependencies();
      if (dependencies != null) {
         for (Class<?> clazz : dependencies) {
            services.put(clazz, NULL_SERVICE);
            trackers.add(new InternalServiceTracker(this, getBundleContext(), clazz));
         }
         for (ServiceTracker tracker : trackers) {
            tracker.open(true);
         }
      }
   }

   @Override
   public void close() {
      handler.onDeActivate();
      for (ServiceTracker tracker : trackers) {
         tracker.close();
      }
   }

   private BundleContext getBundleContext() {
      return context;
   }

   private void onAddingService(Class<?> classKey, Object service) {
      Object previous = services.put(classKey, service);
      if (isValidService(previous) && previous != service) {
         throw new IllegalStateException(String.format("Attempting to overwrite existing service reference: [%s]",
            previous.getClass().getName()));
      }
      if (areServicesReady()) {
         handler.onActivate(getBundleContext(), services);
      }
   }

   private boolean isValidService(Object service) {
      return service != null && !NULL_SERVICE.equals(service);
   }

   private boolean areServicesReady() {
      for (Object service : services.values()) {
         if (!isValidService(service)) {
            return false;
         }
      }
      return true;
   }

   private void onRemovingService(Class<?> classKey) {
      handler.onDeActivate();
      services.remove(classKey);
   }

   private final static class InternalServiceTracker extends ServiceTracker {
      private final ServiceDependencyTracker parentTracker;
      private final Class<?> serviceClass;

      public InternalServiceTracker(ServiceDependencyTracker parentTracker, BundleContext context, Class<?> serviceClass) {
         super(context, serviceClass.getName(), null);
         this.parentTracker = parentTracker;
         this.serviceClass = serviceClass;
      }

      @Override
      public Object addingService(ServiceReference reference) {
         Object object = super.addingService(reference);
         parentTracker.onAddingService(serviceClass, object);
         return object;
      }

      @Override
      public void removedService(ServiceReference reference, Object service) {
         parentTracker.onRemovingService(serviceClass);
         super.removedService(reference, service);
      }
   }

}
