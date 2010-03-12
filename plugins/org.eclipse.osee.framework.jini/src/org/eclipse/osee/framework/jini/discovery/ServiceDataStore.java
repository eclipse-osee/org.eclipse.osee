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
package org.eclipse.osee.framework.jini.discovery;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jini.config.ConfigurationException;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.LookupCache;
import net.jini.lookup.ServiceDiscoveryEvent;
import net.jini.lookup.ServiceDiscoveryListener;
import net.jini.lookup.ServiceDiscoveryManager;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jini.JiniPlugin;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.config.JiniLookupGroupConfig;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;

public class ServiceDataStore implements ServiceDiscoveryListener, DiscoveryListener {

   private static ServiceDataStore theInstance = null;

   private final List<ClassListener> classListeners;
   private final Set<IRegistrarListener> registrarListeners;
   private final Set<IServiceLookupListener> noFilterServiceListeners;
   private final Set<String> locators;

   // private Set allowedGroups;

   private final List<ServiceItem> serviceItemList;
   private final Map<ServiceID, ServiceRegistrar> serviceRegistrars;
   private LookupDiscoveryManager lookupDiscoveryManager;
   private ServiceDiscoveryManager serviceDiscoveryManager;

   // private LookupCache lookupCache;
   // private LookupCache lookupCache;
   private final Map<Class<?>, LookupCache> lookupCaches;
   private final Logger logger = Logger.getLogger("org.eclipse.osee.framework.jini.discovery.ServiceDataStore");
   private LookupCache everythingCache;

   // *************************************************************************
   // Startup
   // *************************************************************************

   private ServiceDataStore(ClassLoader loader) {
      lookupCaches = Collections.synchronizedMap(new HashMap<Class<?>, LookupCache>());
      serviceItemList = Collections.synchronizedList(new ArrayList<ServiceItem>());
      classListeners = new ArrayList<ClassListener>();
      registrarListeners = Collections.synchronizedSet(new HashSet<IRegistrarListener>());
      serviceRegistrars = Collections.synchronizedMap(new HashMap<ServiceID, ServiceRegistrar>());
      noFilterServiceListeners = Collections.synchronizedSet(new HashSet<IServiceLookupListener>());
      locators = Collections.synchronizedSet(new HashSet<String>());

      ClassLoader currentContext = Thread.currentThread().getContextClassLoader();
      try {
         Thread.currentThread().setContextClassLoader(ExportClassLoader.getInstance());
         System.setSecurityManager(new RelaxedSecurity());
         registerWithJINI();
      } finally {
         Thread.currentThread().setContextClassLoader(currentContext);
      }
   }

   private void registerWithJINI() {
      try {
         String[] filterGroups = JiniLookupGroupConfig.getOseeJiniServiceGroups();
         if (filterGroups == null) {
            logger.log(
                  Level.SEVERE,
                  "[-D" + OseeProperties.getOseeJiniServiceGroups() + "] was not set.\n" + "Please enter the Jini Group this service register with.");
            System.exit(1);
         }

         LookupLocator[] locator = null;
         lookupDiscoveryManager = new LookupDiscoveryManager(filterGroups, locator, this, new OseeJiniConfiguration());
         serviceDiscoveryManager =
               new ServiceDiscoveryManager(lookupDiscoveryManager, null, new OseeJiniConfiguration());

         // We will maintain our own cache, so this call just registers
         // ourselves for lookup
         // lookupCache = myManager.createLookupCache(null, null, this);
      } catch (RemoteException anRE) {
         System.err.println("Failed to setup cache - exiting");
         anRE.printStackTrace(System.err);
         System.exit(-1);
      } catch (IOException anIOE) {
         System.err.println("Failed to setup managers - exiting");
         anIOE.printStackTrace(System.err);
         System.exit(-1);
      } catch (ConfigurationException ex) {
         OseeLog.log(JiniPlugin.class, Level.SEVERE, ex);
      } catch (Throwable t) {
         OseeLog.log(JiniPlugin.class, Level.SEVERE, "failed to setup managers", t);
      }
   }

   public void addGroup(String... groups) throws IOException {
      lookupDiscoveryManager.addGroups(groups);
   }

   public String[] getGroups() {
      return lookupDiscoveryManager.getGroups();
   }

   /**
    * If an existing instance exists, returns the instance. Otherwise, creates a new instance and starts the jini lookup
    * service. Note that services are not immediatly available after starting the lookup service, it takes some time for
    * discovery. It is recommended that getInstance() is called at the start of a program's execution in order to start
    * this lookup.
    * 
    * @return Return singleton ServiceDataStore object instance reference.
    */
   public static ServiceDataStore getEclipseInstance(ClassLoader loader) {
      if (theInstance == null) {
         if (loader == null) {
            loader = ServiceDataStore.class.getClassLoader();
         }
         theInstance = new ServiceDataStore(loader);
      }
      return theInstance;
   }

   public static ServiceDataStore getNonEclipseInstance() {
      if (theInstance == null) {
         theInstance = new ServiceDataStore(ServiceDataStore.class.getClassLoader());
      }
      return theInstance;
   }

   public ServiceRegistrar findRegistrar(String host) throws MalformedURLException, ClassNotFoundException, IOException {
      LookupLocator locator = new LookupLocator(host);
      return locator.getRegistrar(5000);
      /*
      if (lookupDiscoveryManager.getFrom(reg) == LookupDiscoveryManager.FROM_GROUP || compareGroups(reg)) {
          if (reg != null) {
              return locator;
          }
      }
      return null;
      */
   }

   private class LookupList extends Thread {

      private final Collection<String> lookupLocations;

      public LookupList(Collection<String> lookupLocations) {
         this.lookupLocations = lookupLocations;
         System.setSecurityManager(new RelaxedSecurity());
      }

      @Override
      public void run() {
         Thread.currentThread().setContextClassLoader(ExportClassLoader.getInstance());
         System.setSecurityManager(new RelaxedSecurity());
         if (lookupLocations != null) {
            List<LookupLocator> locatorList = new ArrayList<LookupLocator>();
            for (String location : lookupLocations) {
               try {
                  LookupLocator locator = new LookupLocator(location);
                  ServiceRegistrar reg = locator.getRegistrar(5000);
                  if (lookupDiscoveryManager.getFrom(reg) == LookupDiscoveryManager.FROM_GROUP || compareGroups(reg)) {
                     if (reg != null) {
                        locatorList.add(locator);
                     }
                  }
               } catch (MalformedURLException ex) {
                  OseeLog.log(JiniPlugin.class, Level.SEVERE, ex);
               } catch (Exception ex) {
                  System.out.println(); // ?
               }
            }
            lookupDiscoveryManager.addLocators(locatorList.toArray(new LookupLocator[locatorList.size()]));
         }
      }
   }

   private boolean compareGroups(ServiceRegistrar reg) {
      String[] groups;
      try {
         groups = reg.getGroups();
         String[] groupsToMatch = lookupDiscoveryManager.getGroups();
         for (int i = 0; i < groups.length; i++) {
            for (int j = 0; j < groupsToMatch.length; j++) {
               if (groups[i].equals(groupsToMatch[j])) {
                  return true;
               }
            }
         }
      } catch (RemoteException ex) {
         OseeLog.log(JiniPlugin.class, Level.SEVERE, ex);
      }
      return false;
   }

   // *************************************************************************
   // Immediate Data Access
   // *************************************************************************

   /**
    * Returns a list of currently available services that match the class types provided. <br />
    * Note: If this is called immediatly after the first call to getInstance(), the Jini lookup will not have had time
    * to complete, and no services will yet be available.
    */
   public List<ServiceItem> getAvailableServices(Class<?>[] classTypes) {
      List<ServiceItem> serviceList = new ArrayList<ServiceItem>();

      for (int i = 0; i < classTypes.length; i++) {
         // Notify the listener of all existing services that match
         synchronized (serviceItemList) {
            for (int j = 0; j < serviceItemList.size(); j++) {
               ServiceItem serviceItem = serviceItemList.get(j);
               if (classTypes[i].isInstance(serviceItem.service)) {
                  serviceList.add(serviceItem);
               }
            }
         }
      }

      return serviceList;
   }

   // *************************************************************************
   // Maintaining Listeners
   // *************************************************************************

   /**
    * Adds the listener. When any service events happen, the listener will be notified. Note that upon calling
    * addListener(), the {@link IServiceLookupListener#serviceAdded(ServiceItem)} method of the listener will be called
    * supplying it all existing services
    * 
    * @param listener The listener to be added
    */
   public void addListener(IServiceLookupListener listener) {
      synchronized (noFilterServiceListeners) {
         noFilterServiceListeners.add(listener);
      }

      try {
         addLookupCache(null);
      } catch (RemoteException e) {

         e.printStackTrace();
      }

      synchronized (serviceItemList) {
         for (int i = 0; i < serviceItemList.size(); i++) {
            ServiceItem serviceItem = serviceItemList.get(i);
            listener.serviceAdded(serviceItem);
         }
      }
   }

   private void addLookupCache(Class<?> classType) throws RemoteException {
      if (classType == null && everythingCache == null) {
         everythingCache = serviceDiscoveryManager.createLookupCache(null, null, this);
      } else {
         if (lookupCaches.get(classType) == null) {
            ClassLoader currentContext = Thread.currentThread().getContextClassLoader();
            try {
               Thread.currentThread().setContextClassLoader(ExportClassLoader.getInstance());
               lookupCaches.put(classType, serviceDiscoveryManager.createLookupCache(new ServiceTemplate(null,
                     new Class[] {classType}, null), null, this));
            } finally {
               Thread.currentThread().setContextClassLoader(currentContext);
            }

         }
      }
   }

   private void resetLookupCaches() throws RemoteException {
      if (everythingCache != null) {
         everythingCache.removeListener(this);
         everythingCache = null;
         addLookupCache(null);
      }
      Iterator<Class<?>> it = lookupCaches.keySet().iterator();
      while (it.hasNext()) {
         lookupCaches.get(it.next()).removeListener(this);
      }
      for (int i = 0; i < classListeners.size(); i++) {
         addLookupCache(classListeners.get(i).classType);
      }
   }

   /**
    * Adds the listener to the classType specified. When any service events which are instances of the specified
    * classType happen, the listener will be notified. If the listener is concerned with multiple types, multiple calls
    * to addListener() can be made. Note that upon calling addListener(), the
    * {@link IServiceLookupListener#serviceAdded(ServiceItem)} method of the listener will be called supplying it all
    * existing services which match the specified classType.
    * 
    * @param listener The listener to be added
    * @param classType The Class of the service events of interest
    */
   public void addListener(IServiceLookupListener listener, Class<?> classType, Entry[] entries) {
      ClassListener classListener;

      try {
         addLookupCache(classType);
      } catch (RemoteException e) {

         e.printStackTrace();
      }

      synchronized (classListeners) {
         classListener = new ClassListener(classType, listener, entries);
         classListeners.add(new ClassListener(classType, listener, entries));
      }

      // Notify the listener of all existing services that match
      synchronized (serviceItemList) {
         for (int i = 0; i < serviceItemList.size(); i++) {
            ServiceItem serviceItem = serviceItemList.get(i);
            if (classListener.matches(serviceItem)) {
               listener.serviceAdded(serviceItem);
            }
         }
      }
   }

   public void addListener(IServiceLookupListener listener, Class<?> classType) {
      addListener(listener, classType, null);
   }

   /**
    * Adds a registrar listener. This enables an object to be notified whenever a lookupservice is added removed or has
    * a service added to or removed from it. When a listener is added it immediately gets a callback with all of the
    * currently discovered lookup services.
    * 
    * @param listener The listener to be added
    */
   public void addListener(IRegistrarListener listener) {
      synchronized (registrarListeners) {
         registrarListeners.add(listener);
         // Notify the listener of all existing services that match
         Iterator<IRegistrarListener> it = registrarListeners.iterator();
         while (it.hasNext()) {
            IRegistrarListener reggie = it.next();
            reggie.reggieAdded(new ArrayList<ServiceRegistrar>(serviceRegistrars.values()));
         }
      }
   }

   /**
    * Removes the listener, it will no longer be notified of change events.
    * 
    * @param listener The listener to be removed
    */
   public void removeListener(IRegistrarListener listener) {
      synchronized (registrarListeners) {
         registrarListeners.remove(listener);
      }
   }

   /**
    * Removes the listener, it will no longer be notified of change events.
    * 
    * @param listenerToRemove The listener to be removed
    */
   public void removeListener(IServiceLookupListener listenerToRemove) {
      ClassListener classListener;
      IServiceLookupListener listener;

      synchronized (classListeners) {
         Iterator<ClassListener> classIter = classListeners.iterator();
         while (classIter.hasNext()) {
            classListener = classIter.next();
            Iterator<IServiceLookupListener> iter = classListener.listeners.iterator();
            while (iter.hasNext()) {
               listener = iter.next();
               if (listener.equals(listenerToRemove)) {
                  iter.remove();
               }
            }

            // If this classType has no more listeners, we can remove it
            if (classListener.listeners.isEmpty()) {
               classIter.remove();
            }
         }
      }

      synchronized (noFilterServiceListeners) {
         noFilterServiceListeners.remove(listenerToRemove);
      }
   }

   private class ClassListener {
      public Class<?> classType;

      public ArrayList<IServiceLookupListener> listeners;

      public Entry[] entries;

      // public ClassListener(Class classType, IServiceLookupListener
      // listener) {
      // this.classType = classType;
      // listeners = new ArrayList();
      // listeners.add(listener);
      // }

      public ClassListener(Class<?> classType, IServiceLookupListener listener, Entry[] entries) {
         // this(classType, listener);
         this.entries = entries;
         this.classType = classType;
         listeners = new ArrayList<IServiceLookupListener>();
         listeners.add(listener);
      }

      public boolean matches(ServiceItem serviceItem) {

         if (classType.isInstance(serviceItem.service)) {
            if (entries != null) {
               for (int i = 0; i < entries.length; i++) {
                  boolean foundEntry = false;
                  for (int j = 0; j < serviceItem.attributeSets.length; j++) {
                     if (entries[i].getClass().isInstance(serviceItem.attributeSets[j])) {
                        if (!entries[i].equals(serviceItem.attributeSets[j])) {
                           return false;
                        } else {
                           foundEntry = true;
                        }
                     }
                  }
                  if (!foundEntry) {
                     return false;
                  }
               }
            }
            return true;
         }
         return false;
      }
   }

   // *************************************************************************
   // Notifying Service Changes
   // *************************************************************************

   private void notifyServiceAdded(ServiceItem serviceItem) {
      synchronized (noFilterServiceListeners) {
         Iterator<IServiceLookupListener> iterator = noFilterServiceListeners.iterator();
         while (iterator.hasNext()) {
            iterator.next().serviceAdded(serviceItem);
         }
      }
      synchronized (classListeners) {
         for (int i = 0; i < classListeners.size(); i++) {
            ClassListener classListener = classListeners.get(i);

            if (classListener.matches(serviceItem)) {
               for (int j = 0; j < classListener.listeners.size(); j++) {
                  IServiceLookupListener listener = classListener.listeners.get(j);

                  listener.serviceAdded(serviceItem);
               }
            }
         }
      }
   }

   private void notifyServiceRemoved(ServiceItem serviceItem) {
      synchronized (noFilterServiceListeners) {
         Iterator<IServiceLookupListener> iterator = noFilterServiceListeners.iterator();
         while (iterator.hasNext()) {
            iterator.next().serviceRemoved(serviceItem);
         }
      }

      synchronized (classListeners) {
         for (int i = 0; i < classListeners.size(); i++) {
            ClassListener classListener = classListeners.get(i);
            if (classListener.matches(serviceItem)) {
               for (int j = 0; j < classListener.listeners.size(); j++) {
                  IServiceLookupListener listener = classListener.listeners.get(j);
                  listener.serviceRemoved(serviceItem);
               }
            }
         }
      }
   }

   private void notifyServiceChanged(ServiceItem serviceItem) {
      synchronized (noFilterServiceListeners) {
         Iterator<IServiceLookupListener> iterator = noFilterServiceListeners.iterator();
         while (iterator.hasNext()) {
            iterator.next().serviceChanged(serviceItem);
         }
      }

      synchronized (classListeners) {
         for (int i = 0; i < classListeners.size(); i++) {
            ClassListener classListener = classListeners.get(i);
            if (classListener.matches(serviceItem)) {
               for (int j = 0; j < classListener.listeners.size(); j++) {
                  IServiceLookupListener listener = classListener.listeners.get(j);
                  listener.serviceChanged(serviceItem);
               }
            }
         }
      }
   }

   // *************************************************************************
   // Handling Service Changes
   // *************************************************************************

   // private static boolean findServiceId(Collection<ServiceItem> list,
   // ServiceID sid, boolean
   // remove) {
   private static boolean findServiceId(Collection<ServiceItem> list, ServiceID sid, boolean remove) {
      synchronized (list) {
         Iterator<ServiceItem> iter = list.iterator();
         while (iter.hasNext()) {
            ServiceItem si = iter.next();
            if (si.serviceID.equals(sid)) {
               if (remove) {
                  iter.remove();
               }
               return true;
            }
         }
      }
      return false;
   }

   public synchronized void serviceAdded(ServiceDiscoveryEvent event) {
      try {
         if (!findServiceId(serviceItemList, event.getPostEventServiceItem().serviceID, false)) {
            synchronized (serviceItemList) {
               serviceItemList.add(event.getPostEventServiceItem());
            }
            notifyServiceAdded(event.getPostEventServiceItem());

            synchronized (registrarListeners) {
               Iterator<IRegistrarListener> it = registrarListeners.iterator();
               while (it.hasNext()) {
                  IRegistrarListener reggie = it.next();
                  reggie.reggieChanged(new ArrayList<ServiceRegistrar>(serviceRegistrars.values()));
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(JiniPlugin.class, Level.SEVERE, ex);
      }
   }

   public void serviceRemoved(ServiceDiscoveryEvent event) {
      if (findServiceId(serviceItemList, event.getPreEventServiceItem().serviceID, true)) {
         notifyServiceRemoved(event.getPreEventServiceItem());

         synchronized (registrarListeners) {
            Iterator<IRegistrarListener> it = registrarListeners.iterator();
            while (it.hasNext()) {
               IRegistrarListener reggie = it.next();
               reggie.reggieChanged(new ArrayList<ServiceRegistrar>(serviceRegistrars.values()));
            }
         }
      }
   }

   public void serviceChanged(ServiceDiscoveryEvent event) {
      if (findServiceId(serviceItemList, event.getPostEventServiceItem().serviceID, false)) {
         notifyServiceChanged(event.getPostEventServiceItem());
      }
   }

   public void discovered(DiscoveryEvent arg0) {
      synchronized (serviceRegistrars) {
         ServiceRegistrar[] reggies = arg0.getRegistrars();
         for (int i = 0; i < reggies.length; i++) {

            if (lookupDiscoveryManager.getFrom(reggies[i]) == LookupDiscoveryManager.FROM_GROUP || compareGroups(reggies[i])) {
               Object last = serviceRegistrars.put(reggies[i].getServiceID(), reggies[i]);

               synchronized (registrarListeners) {
                  if (last == null) {
                     Iterator<IRegistrarListener> it = registrarListeners.iterator();
                     while (it.hasNext()) {
                        IRegistrarListener reggie = it.next();
                        reggie.reggieAdded(new ArrayList<ServiceRegistrar>(serviceRegistrars.values()));
                     }
                  } else {
                     Iterator<IRegistrarListener> it = registrarListeners.iterator();
                     while (it.hasNext()) {
                        IRegistrarListener reggie = it.next();
                        reggie.reggieChanged(new ArrayList<ServiceRegistrar>(serviceRegistrars.values()));
                     }
                  }
               }
            }
            // else {
            // try {
            // System.out.println("Reject: "
            // + reggies[i].getLocator().getHost());
            // } catch (RemoteException ex) {
            // ex.printStackTrace();
            // }
            // }
         }
      }

   }

   public void discarded(DiscoveryEvent arg0) {
      synchronized (serviceRegistrars) {
         ServiceRegistrar[] reggies = arg0.getRegistrars();
         for (int i = 0; i < reggies.length; i++) {
            if (lookupDiscoveryManager.getFrom(reggies[i]) == LookupDiscoveryManager.FROM_GROUP || compareGroups(reggies[i])) {

               Object removedObject = serviceRegistrars.remove(reggies[i].getServiceID());
               if (removedObject == null) {
                  synchronized (registrarListeners) {
                     Iterator<IRegistrarListener> it = registrarListeners.iterator();
                     while (it.hasNext()) {
                        IRegistrarListener reggie = it.next();
                        reggie.reggieRemoved(new ArrayList<ServiceRegistrar>(serviceRegistrars.values()));
                     }
                  }
               }
            }
         }
      }
   }

   /**
    * @return Returns the serviceRegistrars.
    */
   public Map<ServiceID, ServiceRegistrar> getServiceRegistrars() {
      return serviceRegistrars;
   }

   /**
    * @param lookupList
    * @param addToLocators - If true, adds the lookupList to the 'global' lookup list such that a refresh will try to
    *           locate the service again. This is provided primarily as an optimization for when this routine is called
    *           from refresh().
    */
   private void addLookupLocators(Collection<String> lookupList, boolean addToLocators) {
      boolean isEnabled = OseeProperties.isOseeJiniForcedReggieSearchEnabled();
      if (isEnabled) {

         ClassLoader currentContext = Thread.currentThread().getContextClassLoader();
         try {
            Thread.currentThread().setContextClassLoader(ExportClassLoader.getInstance());
            if (addToLocators) {
               locators.addAll(lookupList);
            }

            Thread thread = new LookupList(lookupList);
            thread.setContextClassLoader(ExportClassLoader.getInstance());
            thread.start();
         } finally {
            Thread.currentThread().setContextClassLoader(currentContext);
         }
      }
   }

   public void addLookupLocators(Collection<String> lookupList) {
      addLookupLocators(lookupList, true);
   }

   public void addLookupLocators(String[] lookups) {
      if (lookups == null) {
         throw new IllegalArgumentException("lookups should not be null");
      }
      addLookupLocators(Arrays.asList(lookups), true);
   }

   public void terminate() {
      lookupDiscoveryManager.terminate();
      serviceDiscoveryManager.terminate();
   }

   public void refresh() {
      lookupDiscoveryManager.removeDiscoveryListener(this);
      // lookupCache.removeListener(this);
      serviceItemList.clear();
      serviceRegistrars.clear();
      Iterator<IRegistrarListener> it = registrarListeners.iterator();
      while (it.hasNext()) {
         IRegistrarListener reggie = it.next();
         reggie.reggieRemoved(new ArrayList<ServiceRegistrar>(serviceRegistrars.values()));
      }

      registerWithJINI();
      try {
         resetLookupCaches();
      } catch (RemoteException e) {
         e.printStackTrace();
      }

      addLookupLocators(locators, false);
   }
}
