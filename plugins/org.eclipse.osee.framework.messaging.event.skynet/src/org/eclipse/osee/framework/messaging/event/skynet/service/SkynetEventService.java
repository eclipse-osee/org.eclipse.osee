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

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import net.jini.core.entry.Entry;
import net.jini.lookup.entry.Comment;
import net.jini.lookup.entry.Name;
import net.jini.lookup.entry.ServiceInfo;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jini.JiniClassServer;
import org.eclipse.osee.framework.jini.service.core.JiniService;
import org.eclipse.osee.framework.jini.service.core.SimpleFormattedEntry;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEvent;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEventListener;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEventService;
import org.eclipse.osee.framework.messaging.event.skynet.SkynetEventPlugin;
import org.eclipse.osee.framework.messaging.event.skynet.filter.IEventFilter;
import org.eclipse.osee.framework.plugin.core.OseeActivator;
import org.eclipse.osee.framework.plugin.core.server.BundleResourceFinder;

/**
 * Skynet Event Service handles event distribution to provide network assisted cache consistency for Skynet.
 * 
 * @author Robert A. Fisher
 */
public class SkynetEventService extends JiniService implements ISkynetEventService {
   private static JiniClassServer jiniClassServer;

   private final HashCollection<IEventFilter, ISkynetEventListener> listenerMap;
   private final ExecutorService executorService;
   private final AcceptAllEventFilter defaultEventFilter;

   protected SkynetEventService(String dbConfig) {
      this.defaultEventFilter = new AcceptAllEventFilter();
      this.executorService = Executors.newFixedThreadPool(3, new ThreadFactory() {
         @Override
         public Thread newThread(Runnable r) {
            return new Thread(r, "Event Dispatcher");
         }
      });
      this.listenerMap = new HashCollection<IEventFilter, ISkynetEventListener>();
      try {
         registerWithJini(dbConfig);
      } catch (Exception ex) {
         OseeLog.log(SkynetEventPlugin.class, Level.SEVERE, ex);
      }
   }

   private final IEventFilter getDefaultEventFilter() {
      return defaultEventFilter;
   }

   @SuppressWarnings("unchecked")
   private void registerWithJini(String dbConfig) throws Exception {
      jiniClassServer = JiniClassServer.getInstance();
      jiniClassServer.addResourceFinder(new BundleResourceFinder(new String[] {
            "org.eclipse.osee.framework.messaging.event.skynet", "org.eclipse.osee.framework.jdk.core"}));

      Dictionary dictionary = null;
      String name = "OSEE Event Service";
      String manufacturer = "Eclipse.org";
      String vendor = "Eclipse.org";
      String version = "0.0";
      String model = "SES";
      String serialNumber = "0.0";
      String description = "OSEE Event Service - OSEE Cache Network Manager";

      OseeActivator plugin = SkynetEventPlugin.getInstance();
      if (plugin != null) {
         dictionary = plugin.getBundle().getHeaders();
      }

      this.registerService(new Entry[] {new SimpleFormattedEntry("db", dbConfig),
            new ServiceInfo(name, manufacturer, vendor, version, model, serialNumber), new Name(name),
            new Comment(description)}, dictionary);

      System.out.println("....................OSEE Event Service(" + dbConfig + ") is Alive....................");
      this.stayAlive();
   }

   public void register(ISkynetEventListener listener, IEventFilter... filters) throws RemoteException {
      addListeners(listener, filters);
   }

   public void deregister(ISkynetEventListener listener, IEventFilter... filters) throws RemoteException {
      removeListeners(listener, filters);
   }

   private synchronized void removeListeners(ISkynetEventListener listener, IEventFilter... filters) {
      if (listener != null) {
         for (IEventFilter filter : listenerMap.keySet()) {
            listenerMap.removeValue(filter, listener);
         }
      }
   }

   private synchronized void addListeners(ISkynetEventListener listener, IEventFilter... filters) {
      if (listener != null) {
         if (filters != null && filters.length > 0) {
            for (IEventFilter filter : filters) {
               listenerMap.put(filter, listener);
            }
         } else {
            listenerMap.put(getDefaultEventFilter(), listener);
         }
      }
   }

   private synchronized HashCollection<ISkynetEventListener, ISkynetEvent> getEventAndListenersToSendTo(final ISkynetEvent[] events, Set<ISkynetEventListener> filterOutSet) {
      HashCollection<ISkynetEventListener, ISkynetEvent> eventSets =
            new HashCollection<ISkynetEventListener, ISkynetEvent>(false, HashSet.class);
      // Build sets according to the filters that the event matches
      for (IEventFilter filter : listenerMap.keySet()) {
         for (ISkynetEvent event : events) {
            if (filter.accepts(event)) {
               // When a filter accepts an event, the event to everyone listening for it
               for (ISkynetEventListener listener : listenerMap.getValues(filter)) {
                  if (!filterOutSet.contains(listener)) {
                     eventSets.put(listener, event);
                  }
               }
            }
         }
      }
      return eventSets;
   }

   private Set<ISkynetEventListener> toFilteredSet(final ISkynetEventListener... filterOut) {
      if (filterOut != null && filterOut.length > 0) {
         Set<ISkynetEventListener> filterOutSet =
               new HashSet<ISkynetEventListener>((int) (filterOut.length / .75f) + 1);
         for (ISkynetEventListener listener : filterOut) {
            if (listener != null) {
               filterOutSet.add(listener);
            }
         }
         return filterOutSet;
      }
      return Collections.emptySet();
   }

   public void kick(final ISkynetEvent[] events, final ISkynetEventListener... except) throws RemoteException {
      if (events != null && events.length > 0) {
         executorService.submit(new EventDispatchRunnable(this, getEventAndListenersToSendTo(events,
               toFilteredSet(except))));
      }
   }

   public static void main(String[] args) {
      if (args.length != 2) throw new IllegalArgumentException(
            "Must supply the db name for the service, such as \"dbinstance:schema\"");

      new SkynetEventService(args[1]);
   }

   public void kill() throws RemoteException {
      try {
         this.deregisterService();
         this.commitSuicide();
      } finally {
         this.executorService.shutdown();
      }
   }

   public boolean isAlive() throws RemoteException {
      return true;
   }
}