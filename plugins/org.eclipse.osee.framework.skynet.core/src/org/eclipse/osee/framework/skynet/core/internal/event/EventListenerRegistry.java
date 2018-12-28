/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.internal.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.skynet.core.event.listener.EventQosType;
import org.eclipse.osee.framework.skynet.core.event.listener.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.FrameworkEvent;

/**
 * @author Roberto E. Escobar
 */
public class EventListenerRegistry {

   private final Map<EventQosType, EventListeners> qosToListeners =
      new ConcurrentHashMap<>();
   private final Set<IEventListener> listenerSet = new HashSet<>();

   public void addListener(EventQosType qos, IEventListener listener) {
      if (listener != null) {
         EventListeners registry = qosToListeners.get(qos);
         if (registry == null) {
            registry = new EventListeners();
            qosToListeners.put(qos, registry);
         }
         registry.addListener(listener);
         listenerSet.add(listener);
      }
   }

   public void removeListener(EventQosType qos, IEventListener listener) {
      if (listener != null) {
         EventListeners registry = qosToListeners.get(qos);
         if (registry != null) {
            registry.removeListener(listener);
            if (registry.isEmpty()) {
               qosToListeners.remove(qos);
            }
         }
         listenerSet.remove(listener);
      }
   }

   public void removeListener(IEventListener listener) {
      if (listener != null) {
         listenerSet.remove(listener);
         for (EventQosType type : EventQosType.values()) {
            removeListener(type, listener);
         }
      }
   }

   public int size(EventQosType type) {
      int count = 0;
      EventListeners registry = qosToListeners.get(type);
      if (registry != null) {
         count += registry.size();
      }
      return count;
   }

   public int size() {
      return listenerSet.size();
   }

   public void clearAll() {
      qosToListeners.clear();
      listenerSet.clear();
   }

   public <D extends IEventListener> Collection<D> getListeners(EventQosType qos, FrameworkEvent event) {
      Collection<D> listener = null;
      EventListeners registry = qosToListeners.get(qos);
      if (registry != null) {
         listener = registry.getListeners(event.getClass());
      }
      if (listener == null) {
         listener = Collections.emptyList();
      }
      return listener;
   }

   @Override
   public String toString() {
      List<String> values = new ArrayList<>();
      for (EventQosType type : EventQosType.values()) {
         EventListeners registry = qosToListeners.get(type);
         for (Class<? extends FrameworkEvent> clazz : registry.keySet()) {
            Collection<IEventListener> data = registry.getListeners(clazz);
            for (IEventListener listener : data) {
               values.add(String.format("type[%s] - event[%s] - [%s]", type, clazz, listener));
            }
         }
      }
      String[] listArr = values.toArray(new String[values.size()]);
      Arrays.sort(listArr);
      return org.eclipse.osee.framework.jdk.core.util.Collections.toString("\n", (Object[]) listArr);
   }
   private final static class EventListeners {
      private final Map<Class<? extends FrameworkEvent>, Set<IEventListener>> eventClassToListeners =
         new ConcurrentHashMap<>();
      private int size = 0;

      public void addListener(IEventListener listener) {
         if (listener != null) {
            Collection<Class<? extends FrameworkEvent>> events = getCompatibleEvents(listener);
            for (Class<? extends FrameworkEvent> clazz : events) {
               add(clazz, listener);
            }
         }
      }

      private void add(Class<? extends FrameworkEvent> clazz, IEventListener listener) {
         Set<IEventListener> items = eventClassToListeners.get(clazz);
         if (items == null) {
            items = new HashSet<>();
            eventClassToListeners.put(clazz, items);
         }
         synchronized (items) {
            if (items.add(listener)) {
               size++;
            }
         }
      }

      public Set<Class<? extends FrameworkEvent>> keySet() {
         return eventClassToListeners.keySet();
      }

      public int size() {
         return size;
      }

      public boolean isEmpty() {
         return eventClassToListeners.isEmpty();
      }

      public void removeListener(IEventListener listener) {
         if (listener != null) {
            Collection<Class<? extends FrameworkEvent>> events = getCompatibleEvents(listener);
            for (Class<? extends FrameworkEvent> clazz : events) {
               remove(clazz, listener);
            }
         }
      }

      private void remove(Class<? extends FrameworkEvent> clazz, IEventListener listener) {
         Set<IEventListener> items = eventClassToListeners.get(clazz);
         if (items != null) {
            synchronized (items) {
               if (items.remove(listener)) {
                  size--;
               }
            }
            if (items.isEmpty()) {
               eventClassToListeners.remove(clazz);
            }
         }
      }

      @SuppressWarnings("unchecked")
      public <D extends IEventListener> Collection<D> getListeners(Class<? extends FrameworkEvent> clazz) {
         Collection<D> items = (Collection<D>) eventClassToListeners.get(clazz);
         if (items == null) {
            items = Collections.emptySet();
         } else {
            Collection<D> copy = new HashSet<>();
            copy.addAll(items);
            items = copy;
         }
         return items;
      }

      @SuppressWarnings("unchecked")
      private Collection<Class<? extends FrameworkEvent>> getCompatibleEvents(IEventListener listener) {
         Collection<Class<? extends FrameworkEvent>> events = new HashSet<>();
         Method[] methods = listener.getClass().getMethods();
         if (methods != null) {
            for (Method method : methods) {
               Class<?>[] parameters = method.getParameterTypes();
               if (parameters != null) {
                  for (Class<?> parameter : parameters) {
                     if (FrameworkEvent.class.isAssignableFrom(parameter)) {
                        events.add((Class<? extends FrameworkEvent>) parameter);
                     }
                  }
               }
            }
         }
         return events;
      }

   }
}
