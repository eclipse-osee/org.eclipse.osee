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

package org.eclipse.osee.framework.ui.service.control.managers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceMatches;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.ServiceDiscoveryManager;
import org.eclipse.osee.framework.jini.discovery.IRegistrarListener;
import org.eclipse.osee.framework.jini.discovery.RelaxedSecurity;

public class ReggieCache implements DiscoveryListener {

   private static ReggieCache theInstance = null;
   private static Logger logger = Logger.getLogger(ReggieCache.class.getName());

   private Set<IRegistrarListener> registrarListeners;
   private Set<String> locators;
   private Map<ServiceID, ServiceRegistrar> serviceRegistrars;
   private LookupDiscoveryManager lookupDiscoveryManager;
   private ServiceDiscoveryManager serviceDiscoveryManager;

   private ClassLoader loader;

   private ReggieCache(ClassLoader loader) {
      this.loader = loader;
      registrarListeners = Collections.synchronizedSet(new HashSet<IRegistrarListener>());
      serviceRegistrars = Collections.synchronizedMap(new HashMap<ServiceID, ServiceRegistrar>());
      locators = Collections.synchronizedSet(new HashSet<String>());

      Thread.currentThread().setContextClassLoader(this.loader);
      System.setSecurityManager(new RelaxedSecurity());
      registerWithJINI();
   }

   private void registerWithJINI() {
      try {
         LookupLocator[] locator = null;
         lookupDiscoveryManager = new LookupDiscoveryManager(null, locator, this);
         serviceDiscoveryManager = new ServiceDiscoveryManager(lookupDiscoveryManager, null);
      } catch (RemoteException anRE) {
         System.err.println("Failed to setup cache - exiting");
         anRE.printStackTrace(System.err);
         System.exit(-1);
      } catch (IOException anIOE) {
         System.err.println("Failed to setup managers - exiting");
         anIOE.printStackTrace(System.err);
         System.exit(-1);
      }
   }

   public static ReggieCache getEclipseInstance(ClassLoader loader) {
      if (theInstance == null) {
         if (loader == null) {
            loader = ReggieCache.class.getClassLoader();
         }
         theInstance = new ReggieCache(loader);
      }
      return theInstance;
   }

   private class LookupList extends Thread {

      private String[] lookupLocations;

      public LookupList(String[] lookupLocations) {
         this.lookupLocations = lookupLocations;
         System.setSecurityManager(new RelaxedSecurity());
      }

      public void run() {
         Thread.currentThread().setContextClassLoader(ReggieCache.this.loader);
         System.setSecurityManager(new RelaxedSecurity());
         try {
            if (lookupLocations != null) {
               LookupLocator[] locators = new LookupLocator[lookupLocations.length];
               for (int i = 0; i < locators.length; i++) {
                  locators[i] = new LookupLocator(lookupLocations[i]);
               }
               List<LookupLocator> locatorList = new ArrayList<LookupLocator>();
               for (int i = 0; i < locators.length; i++) {
                  try {
                     ServiceRegistrar reg = locators[i].getRegistrar(5000);
                     if (reg != null) {
                        locatorList.add(locators[i]);
                     }
                  } catch (Exception ex) {
                     System.out.println();
                  }
               }
               lookupDiscoveryManager.addLocators(locatorList.toArray(new LookupLocator[locatorList.size()]));
            }
         } catch (MalformedURLException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
         }
      }
   }

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

   public void removeListener(IRegistrarListener listener) {
      synchronized (registrarListeners) {
         registrarListeners.remove(listener);
      }
   }

   public void discovered(DiscoveryEvent arg0) {
      synchronized (serviceRegistrars) {
         ServiceRegistrar[] reggies = arg0.getRegistrars();
         for (int i = 0; i < reggies.length; i++) {

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
      }

   }

   public void discarded(DiscoveryEvent arg0) {
      synchronized (serviceRegistrars) {
         ServiceRegistrar[] reggies = arg0.getRegistrars();
         for (int i = 0; i < reggies.length; i++) {

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

   public Map<ServiceID, ServiceRegistrar> getServiceRegistrars() {
      return serviceRegistrars;
   }

   public Set<String> getAvailableJiniGroups() {
      Set<String> toReturn = new TreeSet<String>();
      synchronized (serviceRegistrars) {
         for (ServiceRegistrar reggie : getServiceRegistrars().values()) {
            try {
               String[] groups = reggie.getGroups();
               for (String group : groups) {
                  toReturn.add(group);
               }
            } catch (RemoteException ex) {
            }
         }
      }
      return toReturn;
   }

   public ServiceMatches lookupAllServices(ServiceRegistrar reggie) throws RemoteException {
      Thread.currentThread().setContextClassLoader(this.loader);
      ServiceTemplate st = new ServiceTemplate(null, null, null);
      return reggie.lookup(st, Integer.MAX_VALUE);
   }

   public void addLookupLocators(String[] lookups) {
      Thread.currentThread().setContextClassLoader(this.loader);
      for (int i = 0; i < lookups.length; i++) {
         locators.add(lookups[i]);
      }

      Thread thread = new LookupList(lookups);
      thread.setContextClassLoader(this.loader);
      thread.start();
   }

   public void terminate() {
      lookupDiscoveryManager.terminate();
      serviceDiscoveryManager.terminate();
   }

   public void refresh() {
      lookupDiscoveryManager.removeDiscoveryListener(this);
      serviceRegistrars.clear();
      Iterator<IRegistrarListener> it = registrarListeners.iterator();
      while (it.hasNext()) {
         IRegistrarListener reggie = it.next();
         reggie.reggieRemoved(new ArrayList<ServiceRegistrar>(serviceRegistrars.values()));
      }

      registerWithJINI();

      String[] locatorsArray = new String[locators.size()];
      int index = 0;
      Iterator<String> iterator = locators.iterator();
      while (iterator.hasNext()) {
         locatorsArray[index++] = iterator.next();
      }
      addLookupLocators(locatorsArray);
   }
}
