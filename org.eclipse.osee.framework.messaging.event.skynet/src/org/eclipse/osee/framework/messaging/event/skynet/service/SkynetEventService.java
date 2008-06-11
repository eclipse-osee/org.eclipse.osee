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
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jini.core.entry.Entry;
import net.jini.lookup.entry.Comment;
import net.jini.lookup.entry.Name;
import net.jini.lookup.entry.ServiceInfo;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jini.JiniClassServer;
import org.eclipse.osee.framework.jini.service.core.JiniService;
import org.eclipse.osee.framework.jini.service.core.SimpleFormattedEntry;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEvent;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEventListener;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEventService;
import org.eclipse.osee.framework.messaging.event.skynet.SkynetEventPlugin;
import org.eclipse.osee.framework.messaging.event.skynet.filter.IEventFilter;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.server.BundleResourceFinder;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;

/**
 * Skynet Event Service handles event distribution to provide network assisted cache consistency for Skynet.
 * 
 * @author Robert A. Fisher
 */
public class SkynetEventService extends JiniService implements ISkynetEventService {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(SkynetEventService.class);

   private final HashCollection<IEventFilter, ISkynetEventListener> filteredListeners;
   private final ReadWriteLock filteredListenersLock;
   private final Collection<ISkynetEventListener> nonfilteredListeners;
   private final ReadWriteLock nonfilteredListenersLock;
   private static JiniClassServer jiniClassServer;

   protected SkynetEventService(String dbConfig) {
      this.filteredListeners = new HashCollection<IEventFilter, ISkynetEventListener>();
      this.nonfilteredListeners = new LinkedList<ISkynetEventListener>();
      this.filteredListenersLock = new ReentrantReadWriteLock();
      this.nonfilteredListenersLock = new ReentrantReadWriteLock();
      try {
         registerWithJini(dbConfig);
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
   }

   @SuppressWarnings("unchecked")
   private void registerWithJini(String dbConfig) throws Exception {
      jiniClassServer = JiniClassServer.getInstance();
      jiniClassServer.addResourceFinder(new BundleResourceFinder(new String[] {
            "org.eclipse.osee.framework.messaging.event.skynet", "org.eclipse.osee.framework.jdk.core"}));

      Dictionary dictionary = null;
      String name = "Skynet Event Service";
      String manufacturer = "Eclipse.org";
      String vendor = "Eclipse.org";
      String version = "0.0";
      String model = "SES";
      String serialNumber = "0.0";
      String description = "Skynet Event Service - Skynet Cache Network Manager";

      OseeUiActivator plugin = SkynetEventPlugin.getInstance();
      if (plugin != null) {
         dictionary = plugin.getBundle().getHeaders();
      }

      this.registerService(new Entry[] {new SimpleFormattedEntry("db", dbConfig),
            new ServiceInfo(name, manufacturer, vendor, version, model, serialNumber), new Name(name),
            new Comment(description)}, dictionary);

      System.out.println("....................Skynet Event Service(" + dbConfig + ") is Alive....................");
      this.stayAlive();
   }

   public void register(ISkynetEventListener listener, IEventFilter... filters) throws RemoteException {

      if (listener == null) {
         System.out.println("the listener is null");
         return;
      }

      if (filters.length == 0) {
         nonfilteredListenersLock.writeLock().lock();
         try {
            nonfilteredListeners.add(listener);
         } finally {
            nonfilteredListenersLock.writeLock().unlock();
         }
      } else {
         filteredListenersLock.writeLock().lock();
         try {
            for (IEventFilter filter : filters) {
               filteredListeners.put(filter, listener);
            }
         } finally {
            filteredListenersLock.writeLock().unlock();
         }
      }
   }

   public void deregister(ISkynetEventListener listener, IEventFilter... filters) throws RemoteException {
      if (filters.length == 0) {
         nonfilteredListenersLock.writeLock().lock();
         try {
            nonfilteredListeners.remove(listener);
         } finally {
            nonfilteredListenersLock.writeLock().unlock();
         }
      } else {
         filteredListenersLock.writeLock().lock();
         try {
            for (IEventFilter filter : filters) {
               filteredListeners.removeValue(filter, listener);
            }
         } finally {
            filteredListenersLock.writeLock().unlock();
         }
      }
   }

   // TODO thread pool this guy
   public void kick(final ISkynetEvent[] events, final ISkynetEventListener... except) throws RemoteException {
      Thread thread = new Thread("kicker") {

         @Override
         public void run() {
            HashCollection<ISkynetEventListener, ISkynetEvent> eventSets =
                  new HashCollection<ISkynetEventListener, ISkynetEvent>(false, HashSet.class);
            HashSet<ISkynetEventListener> exceptList =
                  new HashSet<ISkynetEventListener>((int) (except.length / .75f) + 1, .75f);
            for (ISkynetEventListener listener : except)
               exceptList.add(listener);

            nonfilteredListenersLock.readLock().lock();
            try {
               // The non-filtered listeners will receive all events by definition
               for (ISkynetEventListener listener : nonfilteredListeners)
                  for (ISkynetEvent event : events)
                     if (!exceptList.contains(listener)) eventSets.put(listener, event);
            } finally {
               nonfilteredListenersLock.readLock().unlock();
            }

            // Build sets according to the filters that the event matches

            filteredListenersLock.readLock().lock();
            try {
               // Iterate all of the filters that have mappings to listeners
               for (IEventFilter filter : filteredListeners.keySet())
                  // Check each event against a particular filter
                  for (ISkynetEvent event : events)
                     if (filter.accepts(event))
                     // When a filter accepts an event, the event to everyone listening for it
                     for (ISkynetEventListener listener : filteredListeners.getValues(filter))
                        if (!exceptList.contains(listener)) eventSets.put(listener, event);
            } finally {
               filteredListenersLock.readLock().unlock();
            }

            // Kick all of the listeners with their set of events
            for (ISkynetEventListener listener : eventSets.keySet())
               try {

                  if (listener != null) listener.onEvent(eventSets.getValues(listener).toArray(ISkynetEvent.EMPTY_ARRAY));

               } catch (Exception ex) {

                  // TODO Remove from filteredListeners

                  nonfilteredListeners.remove(listener);
                  logger.log(Level.SEVERE, ex.toString(), ex);
               }
         }

      };

      thread.setPriority(Thread.MIN_PRIORITY);
      thread.start();
   }

   public static void main(String[] args) {
      if (args.length != 2) throw new IllegalArgumentException(
            "Must supply the db name for the service, such as \"dbinstance:schema\"");

      new SkynetEventService(args[1]);
   }

   public void kill() throws RemoteException {
      this.deregisterService();
      this.commitSuicide();
   }

   public boolean isAlive() throws RemoteException {
      return true;
   }
}