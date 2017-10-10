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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractServiceBinder {

   private final Map<Class<?>, Collection<Object>> serviceMap;
   private final BundleContext bundleContext;
   private final AbstractTrackingHandler handler;

   private boolean isReady;

   protected AbstractServiceBinder(Map<Class<?>, Collection<Object>> serviceMap, BundleContext bundleContext, AbstractTrackingHandler handler) {
      super();
      this.serviceMap = serviceMap;
      this.bundleContext = bundleContext;
      this.handler = handler;
   }

   protected AbstractTrackingHandler getHandler() {
      return handler;
   }

   protected BundleContext getBundleContext() {
      return bundleContext;
   }

   @SuppressWarnings("rawtypes")
   public ServiceTracker createTracker(Class<?> clazz) {
      serviceMap.put(clazz, new CopyOnWriteArraySet<Object>());
      return new InternalServiceTracker(this, getBundleContext(), clazz);
   }

   protected abstract void doAdd(Collection<Object> associatedServices, Object service);

   public void onAddingService(Class<?> classKey, Object service) {
      Collection<Object> associatedServices = serviceMap.get(classKey);
      doAdd(associatedServices, service);
      if (!isReady) {
         if (areServicesReady()) {
            isReady = true;

            Set<Object> servicesReported = new HashSet<>();
            Map<Class<?>, Object> services = new HashMap<>();
            for (Entry<Class<?>, Collection<Object>> entry : serviceMap.entrySet()) {
               Object serviceObject = entry.getValue().iterator().next();
               servicesReported.add(serviceObject);
               services.put(entry.getKey(), serviceObject);
            }

            getHandler().onActivate(getBundleContext(), services);

            for (Entry<Class<?>, Collection<Object>> entry : serviceMap.entrySet()) {
               for (Object serviceObject : entry.getValue()) {
                  if (!servicesReported.contains(serviceObject)) {
                     getHandler().onServiceAdded(getBundleContext(), entry.getKey(), serviceObject);
                  }
               }
            }
         }
      } else {
         getHandler().onServiceAdded(getBundleContext(), classKey, service);
      }
   }

   public void onRemovingService(Class<?> classKey, Object service) {
      Collection<Object> associatedServices = serviceMap.get(classKey);
      boolean wasRemoved = associatedServices.remove(service);
      if (!wasRemoved) {
         throw new IllegalStateException(
            String.format("Attempting to remove none managed service reference: [%s]", service.getClass().getName()));
      }
      getHandler().onServiceRemoved(getBundleContext(), classKey, service);
      if (associatedServices.isEmpty()) {
         isReady = false;
         getHandler().onDeActivate();
      }
   }

   protected boolean areServicesReady() {
      for (Collection<Object> services : serviceMap.values()) {
         if (services.isEmpty()) {
            return false;
         }
      }
      return true;
   }

   @SuppressWarnings("unchecked")
   private final static class InternalServiceTracker extends ServiceTracker {
      private final AbstractServiceBinder listener;
      private final Class<?> serviceClass;

      public InternalServiceTracker(AbstractServiceBinder listener, BundleContext context, Class<?> serviceClass) {
         super(context, serviceClass.getName(), null);
         this.listener = listener;
         this.serviceClass = serviceClass;
      }

      @SuppressWarnings("rawtypes")
      @Override
      public Object addingService(ServiceReference reference) {
         Object object = super.addingService(reference);
         listener.onAddingService(serviceClass, object);
         return object;
      }

      @SuppressWarnings("rawtypes")
      @Override
      public void removedService(ServiceReference reference, Object service) {
         listener.onRemovingService(serviceClass, service);
         super.removedService(reference, service);
      }
   }
}