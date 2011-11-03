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

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class Subscribers {

   private final Log logger;
   private final Map<Class<?>, WeakReference<?>> subscribers = new ConcurrentHashMap<Class<?>, WeakReference<?>>();

   public Subscribers(Log logger) {
      this.logger = logger;
   }

   public void addSubscriber(Object subscriber) {
      manageReferences();
      logger.debug("Adding subscriber: [%s]", subscriber);
      subscribers.put(subscriber.getClass(), new WeakReference<Object>(subscriber));
   }

   private void manageReferences() {
      Set<Class<?>> toRemove = new HashSet<Class<?>>();
      for (Entry<Class<?>, WeakReference<?>> entry : subscribers.entrySet()) {
         WeakReference<?> reference = entry.getValue();
         Object object = reference.get();
         if (object == null) {
            toRemove.add(entry.getKey());
         }
      }
      for (Class<?> item : toRemove) {
         subscribers.remove(item);
         logger.debug("Removing subscriber: [%s]", item);
      }
   }

   @SuppressWarnings("unchecked")
   public <T> T findSubscriber(Class<T> subscriberType) {
      T subscriber = null;
      for (Entry<Class<?>, WeakReference<?>> entry : subscribers.entrySet()) {
         if (subscriberType.isAssignableFrom(entry.getKey())) {
            WeakReference<T> reference = (WeakReference<T>) entry.getValue();
            subscriber = reference.get();
            if (subscriber == null) {
               logger.debug("Removing subscriber: [%s]", subscriberType);
               subscribers.remove(subscriberType);
            } else {
               break;
            }
         }
      }
      return subscriber;
   }
}
