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
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.internal.ServiceBinderFactoryImpl;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M. Finkbeiner
 * @author Roberto E. Escobar
 * @deprecated Use Declarative Services instead
 */
@Deprecated
public final class ServiceDependencyTracker implements Closeable {

   private final AbstractTrackingHandler handler;
   private final List<ServiceTracker> trackers;
   private final ServiceBinderFactory serviceBindFactory;

   public ServiceDependencyTracker(BundleContext context, AbstractTrackingHandler handler) {
      this(new ServiceBinderFactoryImpl(context, handler), context, handler);
   }

   public ServiceDependencyTracker(ServiceBinderFactory factory, BundleContext context, AbstractTrackingHandler handler) {
      this.trackers = new ArrayList<>();
      this.handler = handler;
      this.serviceBindFactory = factory;
   }

   public void open() {
      Map<Class<?>, ServiceBindType> configuration = handler.getConfiguredDependencies();
      if (configuration != null) {
         for (Entry<Class<?>, ServiceBindType> entry : configuration.entrySet()) {
            Class<?> clazz = entry.getKey();
            ServiceBindType bindType = entry.getValue();
            ServiceTracker tracker = serviceBindFactory.createTracker(bindType, clazz);
            trackers.add(tracker);
         }
         for (ServiceTracker tracker : trackers) {
            tracker.open(true);
         }
      }
   }

   public AbstractTrackingHandler getHandler() {
      return handler;
   }

   @Override
   public void close() {
      for (ServiceTracker tracker : trackers) {
         tracker.close();
      }
      try {
         handler.onDeActivate();
      } catch (Exception ex) {
         // Do Nothing
      }
   }
}
