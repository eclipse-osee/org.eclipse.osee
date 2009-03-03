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
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
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

   private final HashCollection<IEventFilter, ISkynetEventListener> filteredListeners;
   private final ReadWriteLock filteredListenersLock;
   private final Collection<ISkynetEventListener> nonfilteredListeners;
   private final ReadWriteLock nonfilteredListenersLock;
   private static JiniClassServer jiniClassServer;
   private final ExecutorService executorService;

   protected SkynetEventService(String dbConfig) {
      this.executorService = Executors.newFixedThreadPool(3, new ThreadFactory() {
         @Override
         public Thread newThread(Runnable r) {
            return new Thread(r, "Event Dispatcher");
         }
      });
      this.filteredListeners = new HashCollection<IEventFilter, ISkynetEventListener>();
      this.nonfilteredListeners = new LinkedList<ISkynetEventListener>();
      this.filteredListenersLock = new ReentrantReadWriteLock();
      this.nonfilteredListenersLock = new ReentrantReadWriteLock();
      try {
         registerWithJini(dbConfig);
      } catch (Exception ex) {
         OseeLog.log(SkynetEventPlugin.class, Level.SEVERE, ex);
      }
   }

   final HashCollection<IEventFilter, ISkynetEventListener> getFilteredListeners() {
      return filteredListeners;
   }

   final ReadWriteLock getFilteredListenersLock() {
      return filteredListenersLock;
   }

   final Collection<ISkynetEventListener> getNonFilteredListeners() {
      return nonfilteredListeners;
   }

   final ReadWriteLock getNonFilteredListenersLock() {
      return nonfilteredListenersLock;
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

      OseeActivator plugin = SkynetEventPlugin.getInstance();
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

   public void kick(final ISkynetEvent[] events, final ISkynetEventListener... except) throws RemoteException {
      executorService.submit(new EventDispatchRunnable(this, events, except));
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