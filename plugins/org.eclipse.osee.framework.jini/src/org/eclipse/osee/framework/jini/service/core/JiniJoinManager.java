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
package org.eclipse.osee.framework.jini.service.core;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lease.UnknownLeaseException;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceRegistration;
import org.eclipse.osee.framework.jini.discovery.IRegistrarListener;
import org.eclipse.osee.framework.jini.discovery.ServiceDataStore;
import org.eclipse.osee.framework.jini.service.interfaces.IService;
import org.eclipse.osee.framework.jini.util.OseeJini;

/**
 * @author Andrew M. Finkbeiner
 */
public class JiniJoinManager implements IRegistrarListener {

   /**
    * The amount of time before a lease expires to first attempt renewal. This amount of time should be sufficiently
    * large to account for delays in communication (i.e. network delays), and allow for at least a few retries in the
    * event the service is not reachable. This time is specified in milliseconds.
    */
   private static final long RENEWAL_TIME = 2 * 60 * 1000; // 2 minutes

   //  private HashMap<ServiceID, ServiceRegistrar> idToReggie;
   private HashMap<ServiceID, ServiceRegistrar> idToReggie;
   //  private ArrayList<ServiceRegistration> registrations;
   private ArrayList<ServiceRegistration> registrations;
   private Timer renewTimer;
   private final Remote proxy;
   private Entry[] entry;
   private ServiceID serviceID;
   private ServiceDataStore serviceDataStore;

   public JiniJoinManager(ServiceID serviceID, JiniService js, Entry[] entry) throws IOException {
      proxy = OseeJini.getRemoteReference(js);
      this.entry = entry;
      this.serviceID = serviceID;
      registrations = new ArrayList<ServiceRegistration>();
      idToReggie = new HashMap<ServiceID, ServiceRegistrar>();
      renewTimer = new Timer();
      serviceDataStore = ServiceDataStore.getNonEclipseInstance();
      serviceDataStore.addListener(this);
   }

   public JiniJoinManager(ServiceID serviceID, IService service, Entry[] entry) throws IOException {
      proxy = service;
      this.entry = entry;
      this.serviceID = serviceID;
      registrations = new ArrayList<ServiceRegistration>();
      idToReggie = new HashMap<ServiceID, ServiceRegistrar>();
      renewTimer = new Timer();
      serviceDataStore = ServiceDataStore.getNonEclipseInstance();
      serviceDataStore.addListener(this);
   }

   public void terminate() throws UnknownLeaseException, RemoteException {
      renewTimer.cancel();
      for (int i = 0; i < registrations.size(); i++) {
         ServiceRegistration registration = registrations.get(i);
         try {
            registration.getLease().cancel();
         } catch (UnknownLeaseException ex) {

         }
      }
   }

   public void setAttributes(Entry[] entry) {
      for (int i = 0; i < registrations.size(); i++) {
         ServiceRegistration registration = registrations.get(i);
         try {
            registration.setAttributes(entry);
         } catch (UnknownLeaseException ex) {
            registrations.remove(i);
            i--;
            //		ex.printStackTrace();
         } catch (RemoteException ex) {
            registrations.remove(i);
            i--;
            //		ex.printStackTrace();
         }
      }
   }

   private class RenewLease extends TimerTask {
      ServiceRegistration registration;

      public RenewLease(ServiceRegistration registration) {
         this.registration = registration;
      }

      public void run() {
         try {
            // Renew for the maximum amount of time allowed
            registration.getLease().renew(Lease.FOREVER);
            renewTimer.schedule(new RenewLease(registration),
                  registration.getLease().getExpiration() - System.currentTimeMillis() - RENEWAL_TIME);
         } catch (LeaseDeniedException ex) {
            //		ex.printStackTrace();
         } catch (UnknownLeaseException ex) {
            //		ex.printStackTrace();
         } catch (RemoteException ex) {
            //		ex.printStackTrace();
         }
         //	    finally{
         //	    renewTimer.schedule(new RenewLease(registration), registration.getLease().getExpiration() - System.currentTimeMillis() - RENEWAL_TIME);
         //	    }
      }

   }

   public void addGroup(String... groups) throws IOException {
      serviceDataStore.addGroup(groups);
   }

   public String[] getGroups() {
      return serviceDataStore.getGroups();
   }

   public void reggieAdded(List<ServiceRegistrar> serviceRegistrars) {
      ServiceRegistrar[] reggies =
            (ServiceRegistrar[]) serviceRegistrars.toArray(new ServiceRegistrar[serviceRegistrars.size()]);
      try {
         for (int i = 0; i < reggies.length; i++) {
            ServiceRegistration registration;
            registration = reggies[i].register(new ServiceItem(serviceID, proxy, entry), Long.MAX_VALUE);
            idToReggie.put(reggies[i].getServiceID(), reggies[i]);
            registrations.add(registration);
            renewTimer.schedule(new RenewLease(registration),
                  registration.getLease().getExpiration() - System.currentTimeMillis() - RENEWAL_TIME);
         }
      } catch (ExportException ex) {
         ex.printStackTrace();
      } catch (RemoteException ex) {
         ex.printStackTrace();
      } catch (Throwable t) {
         t.printStackTrace();
      }
   }

   public void reggieRemoved(List<ServiceRegistrar> serviceRegistrars) {
      ServiceRegistrar[] reggies =
            (ServiceRegistrar[]) serviceRegistrars.toArray(new ServiceRegistrar[serviceRegistrars.size()]);
      for (int i = 0; i < reggies.length; i++) {
         idToReggie.remove(reggies[i].getServiceID());
      }
   }

   public void reggieChanged(List<ServiceRegistrar> serviceRegistrars) {
   }

   public Entry[] getEntry() {
      return entry;
   }

   public Collection<ServiceRegistrar> getRegistrations() {
      return Collections.unmodifiableCollection(idToReggie.values());
   }

   public Remote getProxy() {
      return proxy;
   }

   public ServiceRegistrar findRegistrar(String host) throws MalformedURLException, ClassNotFoundException, IOException {
      return serviceDataStore.findRegistrar(host);
   }
}
