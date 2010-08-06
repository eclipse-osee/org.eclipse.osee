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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Roberto E. Escobar
 */
public class MultiServiceBinder extends AbstractServiceBinder {
   private final Map<Class<?>, Collection<Object>> multiples = new ConcurrentHashMap<Class<?>, Collection<Object>>();

   private boolean isReady;

   public MultiServiceBinder(BundleContext bundleContext, AbstractTrackingHandler handler) {
      super(bundleContext, handler);
   }

   @Override
   public ServiceTracker createTracker(Class<?> clazz) {
      multiples.put(clazz, new CopyOnWriteArraySet<Object>());
      return super.createTracker(clazz);
   }

   @Override
   public void onAddingService(Class<?> classKey, Object service) {
      Collection<Object> associatedServices = multiples.get(classKey);
      doAdd(associatedServices, service);
      if (!isReady) {
         if (areServicesReady()) {
            isReady = true;
            Map<Class<?>, Object> services = getSingleServiceMap();
            getHandler().onActivate(getBundleContext(), services);
         }
      } else {
         getHandler().onServiceAdded(getBundleContext(), classKey, service);
      }
   }

   protected void doAdd(Collection<Object> associatedServices, Object service) {
      associatedServices.add(service);
   }

   private Map<Class<?>, Object> getSingleServiceMap() {
      Map<Class<?>, Object> items = new HashMap<Class<?>, Object>();
      for (Entry<Class<?>, Collection<Object>> entry : multiples.entrySet()) {
         items.put(entry.getKey(), entry.getValue().iterator().next());
      }
      return items;
   }

   private boolean areServicesReady() {
      for (Collection<Object> services : multiples.values()) {
         if (services.isEmpty()) {
            return false;
         }
      }
      return true;
   }

   @Override
   public void onRemovingService(Class<?> classKey, Object service) {
      Collection<Object> associatedServices = multiples.get(classKey);
      boolean wasRemoved = associatedServices.remove(service);
      if (!wasRemoved) {
         throw new IllegalStateException(String.format("Attempting to remove none managed service reference: [%s]",
            service.getClass().getName()));
      }
      getHandler().onServiceRemoved(getBundleContext(), classKey, service);
      if (associatedServices.isEmpty()) {
         isReady = false;
         getHandler().onDeActivate();
      }
   }
}