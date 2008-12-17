
package org.eclipse.osee.ote.connection.jini;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceRegistration;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.LookupCache;
import net.jini.lookup.ServiceDiscoveryEvent;
import net.jini.lookup.ServiceDiscoveryListener;
import net.jini.lookup.ServiceDiscoveryManager;

import org.eclipse.osee.connection.service.IConnectionService;
import org.eclipse.osee.connection.service.IConnectorListener;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.jini.discovery.OseeJiniConfiguration;
import org.eclipse.osee.framework.plugin.core.config.JiniLookupGroupConfig;

/**
 * @author b1529404
 */
public class JiniConnectorRegistrar implements IJiniConnectorRegistrar, IConnectorListener,
      ServiceDiscoveryListener, DiscoveryListener {

   private final HashMap<JiniServiceSideConnector, HashSet<ServiceRegistrar>> serverSideConnectors = new HashMap<JiniServiceSideConnector, HashSet<ServiceRegistrar>>();
   private final HashSet<ServiceRegistrar> serviceRegistrars = new HashSet<ServiceRegistrar>();
   private final IConnectionService connectionService;
   private final HashMap<ServiceID, JiniClientSideConnector> clientSideConnectors = new HashMap<ServiceID, JiniClientSideConnector>();

   private LookupDiscoveryManager lookupDiscoveryManager;
   private ServiceDiscoveryManager serviceDiscoveryManager;
   private LookupCache lookupCache;
   private boolean isShutdown = false;

   public JiniConnectorRegistrar(ClassLoader loader, IConnectionService connectionService)
         throws Exception, ConfigurationException {
      this.connectionService = connectionService;
      connectionService.addListener(this);
      String[] groups = JiniLookupGroupConfig.getOseeJiniServiceGroups();
      if (groups == null) {
         groups = new String[] {};
      }
      Configuration config = new OseeJiniConfiguration();
      ClassLoader previousLoader = Thread.currentThread().getContextClassLoader();
      try {
         Thread.currentThread().setContextClassLoader(loader);
         lookupDiscoveryManager = new LookupDiscoveryManager(groups, null, this, config);
         serviceDiscoveryManager = new ServiceDiscoveryManager(lookupDiscoveryManager, null, config);

         lookupCache = serviceDiscoveryManager.createLookupCache(new ServiceTemplate(null,new Class[] {},null),
                                                                 null,
                                                                 this);
      }
      finally {
         Thread.currentThread().setContextClassLoader(previousLoader);
      }
   }

   /*
    * (non-Javadoc)
    * @seeorg.eclipse.osee.connection.service.IConnectorListener# onConnectionServiceStopped()
    */
   @Override
   public void onConnectionServiceStopped() {
      shutdown();
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IConnectorListener#onConnectorAdded
    * (org.eclipse.osee.connection.service.IServiceConnector)
    */
   @Override
   public synchronized void onConnectorsAdded(
	    Collection<IServiceConnector> connectors) {
      for (IServiceConnector connector : connectors) {
         if (connector.getConnectorType().equals(JiniServiceSideConnector.TYPE)) {
            System.out.println("found jini server side connector");
            JiniServiceSideConnector jiniConnector = (JiniServiceSideConnector) connector;
            HashSet<ServiceRegistrar> list = new HashSet<ServiceRegistrar>(serviceRegistrars);
            serverSideConnectors.put(jiniConnector, list);
            for (ServiceRegistrar registrar : list) {
               try {
                  final ServiceRegistration registration = registrar.register(
                                                                              jiniConnector.getServiceItem(),
                                                                              Long.MAX_VALUE);
                  jiniConnector.addRegistration(registration);
               }
               catch (RemoteException ex) {
                  Activator.log(Level.WARNING, "Error registering service", ex);
               }
            }

         }
      }
   }

   public synchronized void shutdown() {
      if (!isShutdown) {
         System.out.println("shuting down JiniRegistrar");
         isShutdown = true;
         lookupDiscoveryManager.terminate();
         serviceDiscoveryManager.terminate();
         serviceRegistrars.clear();
         for (JiniServiceSideConnector connector : serverSideConnectors.keySet()) {
            try {
               connector.removeAllRegistrations();
            }
            catch (Exception ex) {
               Activator.log(Level.WARNING, "Error removing registrations on shutdown", ex);
            }
         }
         if (!connectionService.isStopped()) {
            for (JiniServiceSideConnector connector : serverSideConnectors.keySet()) {
               try {
                  connectionService.removeConnector(connector);
               }
               catch (Exception ex) {
                  Activator.log(Level.WARNING, "Error removing connectors on shutdown", ex);
               }
            }
            for (JiniClientSideConnector connector : new ArrayList<JiniClientSideConnector>(
                                                                                            clientSideConnectors.values())) {
               try {
                  connectionService.removeConnector(connector);
               }
               catch (Exception ex) {
                  Activator.log(Level.WARNING, "Error removing connectors on shutdown", ex);
               }
            }
         }
         serverSideConnectors.clear();
         clientSideConnectors.clear();
      }
   }

   @Override
   public void addGroup(String... groups) throws IOException {
      lookupDiscoveryManager.addGroups(groups);
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IConnectorListener#onConnectorRemoved
    * (org.eclipse.osee.connection.service.IServiceConnector)
    */
   @Override
   public synchronized void onConnectorRemoved(IServiceConnector connector) {
      if (connector.getConnectorType().equals(JiniServiceSideConnector.TYPE)) {
         JiniServiceSideConnector jiniConnector = (JiniServiceSideConnector) connector;
         serverSideConnectors.remove(jiniConnector);
      }
      else if (connector.getConnectorType().equals(JiniClientSideConnector.TYPE)) {
         Iterator<Entry<ServiceID, JiniClientSideConnector>> iter = clientSideConnectors.entrySet().iterator();
         while (iter.hasNext()) {
            final Entry<ServiceID, JiniClientSideConnector> entry = iter.next();
            if (entry.getValue().equals(connector)) {
               iter.remove();
            }
         }
      }
   }

   @Override
   public synchronized void discovered(DiscoveryEvent event) {
      try {
         for (ServiceRegistrar registrar : event.getRegistrars()) {
            System.out.println("Lookup Discovered: Service ID= " + registrar.getServiceID());
            try {
               for (String group : registrar.getGroups()) {
                  Activator.log(Level.INFO, "found JINI registrar group: " + group);
               }
            }
            catch (RemoteException ex) {
               ex.printStackTrace();
            }
            serviceRegistrars.add(registrar);
            for (Map.Entry<JiniServiceSideConnector, HashSet<ServiceRegistrar>> entry : serverSideConnectors.entrySet()) {
               final ServiceRegistration registration = registrar.register(
                                                                           entry.getKey().getServiceItem(),
                                                                           Long.MAX_VALUE);
               entry.getKey().addRegistration(registration);
               entry.getValue().addAll(serviceRegistrars);
            }
         }
      }
      catch (Exception ex) {
         Activator.log(Level.SEVERE, "Error processing service discovery", ex);
      }
   }

   @Override
   public synchronized void discarded(DiscoveryEvent event) {
      this.serviceRegistrars.removeAll(Arrays.asList(event.getRegistrars()));

   }

   /*
    * (non-Javadoc)
    * @see net.jini.lookup.ServiceDiscoveryListener#serviceAdded(net.jini.lookup
    * .ServiceDiscoveryEvent)
    */
   public synchronized void serviceAdded(ServiceDiscoveryEvent event) {
      ServiceItem serviceItem = event.getPostEventServiceItem();
      JiniClientSideConnector connector = new JiniClientSideConnector(serviceItem);
      clientSideConnectors.put(serviceItem.serviceID, connector);
      connectionService.addConnector(connector);
   }

   @Override
   public synchronized void serviceRemoved(ServiceDiscoveryEvent event) {
      JiniClientSideConnector connector = clientSideConnectors.remove(event.getPreEventServiceItem().serviceID);

      if (connector != null) {
         try {
            connectionService.removeConnector(connector);
         }
         catch (Exception e) {
            Activator.log(Level.SEVERE, "error processing removed service event", e);
         }
      }
   }

   /*
    * (non-Javadoc)
    * @see net.jini.lookup.ServiceDiscoveryListener#serviceChanged(net.jini.lookup
    * .ServiceDiscoveryEvent)
    */
   public synchronized void serviceChanged(ServiceDiscoveryEvent event) {
      ServiceItem item = event.getPostEventServiceItem();
      JiniClientSideConnector connector = clientSideConnectors.get(item.serviceID);
      if (connector != null) {
         connector.entriesChanged(item.attributeSets);
      }
   }

   @Override
   public String[] getGroups() {
      return lookupDiscoveryManager.getGroups();
   }

   @Override
   public void addLocators(String... hosts) throws MalformedURLException, ClassNotFoundException, IOException {
      LookupLocator[] locators = new LookupLocator[hosts.length];
      int i = 0;
      for (String host : hosts) {
         locators[i++] = new LookupLocator(host);
      }
      lookupDiscoveryManager.addLocators(locators);
   }

   @Override
   public LookupLocator[] getLocators() {
      return lookupDiscoveryManager.getLocators();
   }
}
