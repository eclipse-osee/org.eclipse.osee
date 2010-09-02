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
package org.eclipse.osee.framework.skynet.core.event.systems;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.logging.Level;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceItem;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jini.discovery.EclipseJiniClassloader;
import org.eclipse.osee.framework.jini.discovery.IServiceLookupListener;
import org.eclipse.osee.framework.jini.discovery.ServiceDataStore;
import org.eclipse.osee.framework.jini.service.core.SimpleFormattedEntry;
import org.eclipse.osee.framework.jini.util.OseeJini;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEvent;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEventListener;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEventService;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.event.EventSystemPreferences;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.RemoteEventServiceEventType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public final class JiniSkynetEventServiceLookup implements IServiceLookupListener {

   private final ISkynetEventListener clientEventListener;
   private String acceptableServiceName;
   private ISkynetEventService currentEventService;
   private ISkynetEventListener clientEventListenerRemoteReference;

   public JiniSkynetEventServiceLookup(EventSystemPreferences preferences, ISkynetEventListener clientEventListener) {
      this.clientEventListener = clientEventListener;
      clear();
   }

   private void clear() {
      currentEventService = null;
      clientEventListenerRemoteReference = null;
      acceptableServiceName = null;
   }

   public void start() {
      getClientEventListenerRemoteReference();
   }

   public void stop() {
      if (clientEventListenerRemoteReference != null) {
         ServiceDataStore.getEclipseInstance(EclipseJiniClassloader.getInstance()).removeListener(this);
      }
      reset();
   }

   public ISkynetEventListener getClientEventListenerRemoteReference() {
      checkJiniRegistration();
      return clientEventListenerRemoteReference;
   }

   public void checkJiniRegistration() {
      if (clientEventListenerRemoteReference == null) {
         try {
            // We need to trigger authentication before attempting to get database information from client session manager.
            UserManager.getUser();
            acceptableServiceName =
               ClientSessionManager.getDataStoreName() + ":" + ClientSessionManager.getDataStoreLoginName();
            clientEventListenerRemoteReference =
               (ISkynetEventListener) OseeJini.getRemoteReference(clientEventListener);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            clientEventListenerRemoteReference = null;
         }

         if (clientEventListenerRemoteReference != null) {
            ServiceDataStore.getEclipseInstance(EclipseJiniClassloader.getInstance()).addListener(this,
               ISkynetEventService.class);
         }
      }
   }

   private ISkynetEventService getReference() {
      return currentEventService;
   }

   public boolean isValid() {
      return isValidService(currentEventService);
   }

   public void reset() {
      setEventService(null);
   }

   public void kick(ISkynetEvent[] events, ISkynetEventListener... except) {
      try {
         getReference().kick(events, except);
      } catch (ExportException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      } catch (RemoteException ex) {
         disconnectService(ex);
      }
   }

   private boolean isValidService(ISkynetEventService service) {
      boolean result = false;
      try {
         if (service != null) {
            result = service.isAlive();
         }
      } catch (Exception ex) {
         // Do Nothing
         result = false;
      }
      return result;
   }

   private synchronized void setEventService(ISkynetEventService service) {
      if (isValidService(currentEventService)) {
         try {
            currentEventService.deregister(getClientEventListenerRemoteReference());
         } catch (RemoteException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      currentEventService = service;
   }

   private void disconnectService(Exception e) {
      OseeLog.log(Activator.class, Level.WARNING, "Skynet Event Service connection lost\n" + e.toString(), e);
      setEventService(null);
      try {
         OseeEventManager.kickLocalRemEvent(this, RemoteEventServiceEventType.Rem1_DisConnected);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void connectToService(ISkynetEventService service) {
      try {
         ISkynetEventListener clientListener = getClientEventListenerRemoteReference();
         if (clientListener != null) {
            service.register(clientListener);
            setEventService(service);
            OseeLog.log(Activator.class, Level.INFO,
               "Skynet Event Service connection established " + acceptableServiceName);
            OseeEventManager.kickLocalRemEvent(this, RemoteEventServiceEventType.Rem1_Connected);
         } else {
            OseeLog.log(Activator.class, Level.SEVERE, "Client listener reference was null");
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void serviceAdded(ServiceItem serviceItem) {
      if (serviceItem.service instanceof ISkynetEventService) {
         ISkynetEventService service = (ISkynetEventService) serviceItem.service;
         if (isValidService(service)) {
            // Check if the service is for the database we are using
            for (Entry entry : serviceItem.attributeSets) {
               if (entry instanceof SimpleFormattedEntry) {
                  SimpleFormattedEntry simpleEntry = (SimpleFormattedEntry) entry;
                  if ("db".equals(simpleEntry.name) && acceptableServiceName.equals(simpleEntry.value)) {
                     connectToService(service);
                     break;
                  }
               }
            }
         }
      }
   }

   @Override
   public void serviceChanged(ServiceItem serviceItem) {
      serviceAdded(serviceItem);
   }

   @Override
   public void serviceRemoved(ServiceItem serviceItem) {
      // do nothing
   }
}