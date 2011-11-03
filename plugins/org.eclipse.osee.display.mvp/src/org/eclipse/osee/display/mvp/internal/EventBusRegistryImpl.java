/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.mvp.internal;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.display.mvp.event.EventBus;
import org.eclipse.osee.display.mvp.event.EventBusRegistry;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class EventBusRegistryImpl implements EventBusRegistry {

   private final Map<Class<? extends EventBus>, EventBus> buses =
      new ConcurrentHashMap<Class<? extends EventBus>, EventBus>();

   private final Log logger;
   private final Subscribers subscribers;

   public EventBusRegistryImpl(Log logger) {
      this.logger = logger;
      this.subscribers = new Subscribers(logger);
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends EventBus> T register(Class<T> type, Object subscriber) {
      if (!buses.containsKey(type)) {
         buses.put(type, create(type));
      }
      addSubscriber(subscriber);
      EventBus eventBus = buses.get(type);
      return (T) eventBus;
   }

   @Override
   public void addSubscriber(Object subscriber) {
      this.subscribers.addSubscriber(subscriber);
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends EventBus> T getEventBus(Class<T> type) {
      if (!buses.containsKey(type)) {
         buses.put(type, create(type));
      }
      return (T) buses.get(type);
   }

   @SuppressWarnings("unchecked")
   protected <T extends EventBus> T create(Class<T> type) {
      EventDispatcher handler = new EventDispatcher(logger, type.getName(), subscribers);
      T bus = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[] {type}, handler);
      return bus;
   }
}
