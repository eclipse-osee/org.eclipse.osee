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
package org.eclipse.osee.framework.jini;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jini.config.ConfigurationException;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
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
import net.jini.lookup.entry.Comment;
import net.jini.lookup.entry.Name;
import net.jini.lookup.entry.ServiceInfo;
import org.eclipse.osee.framework.jini.discovery.OseeJiniConfiguration;
import org.eclipse.osee.framework.jini.discovery.RelaxedSecurity;
import org.eclipse.osee.framework.jini.service.core.VersionEntry;
import org.eclipse.osee.framework.logging.OseeLog;

public class ExperimentalLookup implements ServiceDiscoveryListener, DiscoveryListener {

   private LookupDiscoveryManager lookupDiscoveryManager;
   private ServiceDiscoveryManager serviceDiscoveryManager;
   private LookupCache lookupCache;
   private static final Logger logger = Logger.getLogger("org.eclipse.osee.framework.jini.discovery.ServiceDataStore");

   // *************************************************************************
   // Startup
   // *************************************************************************

   private ExperimentalLookup() {
      System.setSecurityManager(new RelaxedSecurity());
      registerWithJINI();
   }

   private void registerWithJINI() {
      try {
         lookupDiscoveryManager = new LookupDiscoveryManager(new String[] {}, null, this, new OseeJiniConfiguration());
         serviceDiscoveryManager =
               new ServiceDiscoveryManager(lookupDiscoveryManager, null, new OseeJiniConfiguration());

         lookupCache =
               serviceDiscoveryManager.createLookupCache(new ServiceTemplate(null, new Class[] {}, null), null, this);

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

   /*
    * (non-Javadoc)
    * 
    * @see net.jini.lookup.ServiceDiscoveryListener#serviceAdded(net.jini.lookup.ServiceDiscoveryEvent)
    */
   public synchronized void serviceAdded(ServiceDiscoveryEvent event) {
      try {
         ServiceItem item = event.getPostEventServiceItem();
         System.out.println("found service:\n\tid = " + item.serviceID);
         for (Entry entry : item.attributeSets) {
            if (entry instanceof ServiceInfo) {
               ServiceInfo serviceInfo = (ServiceInfo) entry;
               System.out.println("\tname=" + serviceInfo.name);
               System.out.println("\tmanufacturer=" + serviceInfo.manufacturer);
            }
            if (entry instanceof Name) {
               Name name = (Name) entry;
               System.out.println("\tname entry =" + name.name);
            }
            if (entry instanceof Comment) {
               Comment comment = (Comment) entry;
               System.out.println("\tcomment =" + comment.comment);
            }
            if (entry instanceof VersionEntry) {
               VersionEntry version = (VersionEntry) entry;
               System.out.println("\tversion entry =" + version.version);
            }
         }
      } catch (RuntimeException ex) {
         OseeLog.log(JiniPlugin.class, Level.SEVERE, "exception while adding service", ex);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see net.jini.lookup.ServiceDiscoveryListener#serviceRemoved(net.jini.lookup.ServiceDiscoveryEvent)
    */
   public void serviceRemoved(ServiceDiscoveryEvent event) {
      ServiceItem item = event.getPreEventServiceItem();
      System.out.println("service removed:\n\tid = " + item.serviceID);
      for (Entry entry : item.attributeSets) {
         if (entry instanceof ServiceInfo) {
            ServiceInfo serviceInfo = (ServiceInfo) entry;
            System.out.println("\tname=" + serviceInfo.name);
            System.out.println("\tmanufacturer=" + serviceInfo.manufacturer);
         }
         if (entry instanceof Name) {
            Name name = (Name) entry;
            System.out.println("\tname entry =" + name.name);
         }
         if (entry instanceof Comment) {
            Comment comment = (Comment) entry;
            System.out.println("\tcomment =" + comment.comment);
         }
         if (entry instanceof VersionEntry) {
            VersionEntry version = (VersionEntry) entry;
            System.out.println("\tversion entry =" + version.version);
         }
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see net.jini.lookup.ServiceDiscoveryListener#serviceChanged(net.jini.lookup.ServiceDiscoveryEvent)
    */
   public void serviceChanged(ServiceDiscoveryEvent event) {

   }

   public synchronized void discovered(DiscoveryEvent event) {
      for (ServiceRegistrar registrar : event.getRegistrars()) {
         System.out.println("Lookup Discovered: Service ID= " + registrar.getServiceID());
         try {
            for (String group : registrar.getGroups()) {
               System.out.println("\tgroup " + group);
            }
         } catch (RemoteException ex) {
            OseeLog.log(JiniPlugin.class, Level.SEVERE, "failed to get registrar groups", ex);
         }
      }

   }

   public void discarded(DiscoveryEvent arg0) {

   }

   public void addLookupLocators(Collection<String> lookupList) throws MalformedURLException {
      LookupLocator[] locators = new LookupLocator[lookupList.size()];
      int i = 0;
      for (String lookup : lookupList) {
         locators[i++] = new LookupLocator(lookup);
      }
      lookupDiscoveryManager.addLocators(locators);
   }

   public void terminate() {
      lookupDiscoveryManager.terminate();
      serviceDiscoveryManager.terminate();
   }

   public static void main(String[] args) {

      ExperimentalLookup ex = new ExperimentalLookup();
      try {
         ex.addGroup(new String[] {"LBA_DEVELOPMENT", "net.jini_2.1.0.200803130705"});
      } catch (IOException ex2) {
         OseeLog.log(JiniPlugin.class, Level.SEVERE, ex2.toString(), ex2);
      }
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      try {
         String line = reader.readLine();
         while (!line.equals("QUIT")) {
            line = reader.readLine();
         }
      } catch (Exception ex1) {
         OseeLog.log(JiniPlugin.class, Level.SEVERE, ex1.toString(), ex1);
      } finally {
         ex.terminate();

      }
   }
}
