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
package org.eclipse.osee.framework.messaging.event.skynet.service;

import java.util.HashSet;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEvent;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEventListener;
import org.eclipse.osee.framework.messaging.event.skynet.SkynetEventPlugin;
import org.eclipse.osee.framework.messaging.event.skynet.filter.IEventFilter;

/**
 * @author Roberto E. Escobar
 */
public class EventDispatchRunnable implements Runnable {
   private final ISkynetEvent[] events;
   private final ISkynetEventListener[] except;
   private final SkynetEventService service;

   public EventDispatchRunnable(final SkynetEventService service, final ISkynetEvent[] events, final ISkynetEventListener... except) {
      this.service = service;
      this.events = events;
      this.except = except;
   }

   /* (non-Javadoc)
    * @see java.lang.Runnable#run()
    */
   @Override
   public void run() {
      try {
         HashCollection<ISkynetEventListener, ISkynetEvent> eventSets =
               new HashCollection<ISkynetEventListener, ISkynetEvent>(false, HashSet.class);
         HashSet<ISkynetEventListener> exceptList =
               new HashSet<ISkynetEventListener>((int) (except.length / .75f) + 1, .75f);
         for (ISkynetEventListener listener : except) {
            exceptList.add(listener);
         }

         service.getNonFilteredListenersLock().readLock().lock();
         try {
            // The non-filtered listeners will receive all events by definition
            for (ISkynetEventListener listener : service.getNonFilteredListeners()) {
               for (ISkynetEvent event : events) {
                  if (!exceptList.contains(listener)) {
                     eventSets.put(listener, event);
                  }
               }
            }
         } finally {
            service.getNonFilteredListenersLock().readLock().unlock();
         }

         // Build sets according to the filters that the event matches

         service.getFilteredListenersLock().readLock().lock();
         try {
            // Iterate all of the filters that have mappings to listeners
            for (IEventFilter filter : service.getFilteredListeners().keySet()) {
               // Check each event against a particular filter
               for (ISkynetEvent event : events) {
                  if (filter.accepts(event)) {
                     // When a filter accepts an event, the event to everyone listening for it
                     for (ISkynetEventListener listener : service.getFilteredListeners().getValues(filter)) {
                        if (!exceptList.contains(listener)) {
                           eventSets.put(listener, event);
                        }
                     }
                  }
               }
            }
         } finally {
            service.getFilteredListenersLock().readLock().unlock();
         }

         // Kick all of the listeners with their set of events
         for (ISkynetEventListener listener : eventSets.keySet()) {
            try {
               if (listener != null) {
                  ISkynetEvent[] events = eventSets.getValues(listener).toArray(ISkynetEvent.EMPTY_ARRAY);
                  if (events.length > 0) {
                     listener.onEvent(events);
                  }
               }
            } catch (Exception ex) {
               // TODO Remove from filteredListeners
               if (listener != null) {
                  service.getNonFilteredListeners().remove(listener);
               }
               OseeLog.log(SkynetEventPlugin.class, Level.SEVERE, ex);
            }
         }
      } catch (Throwable ex) {
         OseeLog.log(SkynetEventPlugin.class, Level.SEVERE, ex);
      }
   }
}
