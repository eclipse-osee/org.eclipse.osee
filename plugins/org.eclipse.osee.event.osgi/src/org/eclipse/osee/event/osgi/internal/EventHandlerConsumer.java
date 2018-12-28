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
package org.eclipse.osee.event.osgi.internal;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.event.EventHandler;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public class EventHandlerConsumer {

   private final Map<EventHandler, ServiceRegistration<?>> registered =
      new ConcurrentHashMap<>();

   private final Map<EventHandler, Dictionary<String, ?>> pending =
      new ConcurrentHashMap<>();

   private BundleContext context;
   private Thread thread;

   public void start(BundleContext context) {
      this.context = context;
      this.thread = new Thread("Register Pending Event Handlers") {
         @Override
         public void run() {
            for (Entry<EventHandler, Dictionary<String, ?>> entry : pending.entrySet()) {
               registerHelper(entry.getKey(), entry.getValue());
            }
            pending.clear();
         }
      };
      thread.start();
   }

   public void stop() {
      if (thread != null && thread.isAlive()) {
         thread.interrupt();
      }
      this.context = null;
   }

   private boolean isReady() {
      return context != null;
   }

   public void registerHandler(EventHandler handler, Map<String, ?> properties) {
      Hashtable<String, Object> serviceProps = new Hashtable<>();
      serviceProps.putAll(properties);
      if (isReady()) {
         registerHelper(handler, serviceProps);
      } else {
         pending.put(handler, serviceProps);
      }
   }

   public void unregisterHandler(EventHandler handler, Map<String, ?> properties) {
      if (isReady()) {
         ServiceRegistration<?> registration = registered.remove(handler);
         if (registration != null) {
            registration.unregister();
         }
      } else {
         pending.remove(handler);
      }
   }

   private void registerHelper(EventHandler handler, Dictionary<String, ?> serviceProps) {
      try {
         EventHandlerProxy proxy = new EventHandlerProxy(handler);
         ServiceRegistration<?> registration =
            context.registerService(org.osgi.service.event.EventHandler.class, proxy, serviceProps);
         registered.put(handler, registration);
      } catch (Exception ex) {
         throw new RuntimeException(ex);
      }
   }
}
