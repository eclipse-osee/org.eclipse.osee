/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.eclipse.osee.connection.service.IConnectionService;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.services.RemoteServiceLookup;
import org.eclipse.osee.framework.messaging.services.ServiceNotification;
import org.eclipse.osee.framework.messaging.services.messages.ServiceHealth;

/**
 * @author Andrew M. Finkbeiner
 */
class OteJmsServiceConnector implements ServiceNotification, OseeMessagingStatusCallback {

   private final ConcurrentHashMap<String, JmsToJiniBridgeConnectorLite> connectors;
   private final ConcurrentHashMap<String, ServiceHealth> serviceHealthMap;
   private final RemoteServiceLookup remoteServiceLookup;
   private final MessageService messageService;
   private final IConnectionService connectionService;

   OteJmsServiceConnector(RemoteServiceLookup remoteServiceLookup, MessageService messageService, IConnectionService connectionService) {
      this.remoteServiceLookup = remoteServiceLookup;
      this.messageService = messageService;
      this.connectionService = connectionService;
      connectors = new ConcurrentHashMap<String, JmsToJiniBridgeConnectorLite>();
      serviceHealthMap = new ConcurrentHashMap<String, ServiceHealth>();
   }

   public void start() {
      remoteServiceLookup.register("osee.ote.server", "1.0", this);
   }

   public void stop() {
      remoteServiceLookup.unregister("osee.ote.server", "1.0", this);
   }

   @Override
   public synchronized void onServiceGone(ServiceHealth serviceHealth) {
      JmsToJiniBridgeConnectorLite connector = removeExistingConnector(serviceHealth);
      if (connector != null) {
         try {
            connectionService.removeConnector(connector);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   @Override
   public boolean isServiceGone(ServiceHealth serviceHealth) {
      return serviceConfirmedGone(serviceHealth);
   }

   private boolean serviceConfirmedGone(ServiceHealth serviceHealth) {
      JmsToJiniBridgeConnectorLite connector = connectors.get(serviceHealth.getServiceUniqueId());
      if (connector == null) {
         return true;
      }
      return !connector.ping();
   }

   private JmsToJiniBridgeConnectorLite removeExistingConnector(ServiceHealth serviceHealth) {
      return connectors.remove(serviceHealth.getServiceUniqueId());
   }

   @Override
   public synchronized void onServiceUpdate(final ServiceHealth serviceHealth) {
      serviceHealthMap.put(serviceHealth.getServiceUniqueId(), serviceHealth);
      JmsToJiniBridgeConnectorLite oldConnector = connectors.get(serviceHealth.getServiceUniqueId());
      if (oldConnector == null){
    	  JmsToJiniBridgeConnectorLite lite = new JmsToJiniBridgeConnectorLite(serviceHealth, messageService);  
    	  connectors.put(serviceHealth.getServiceUniqueId(), lite);
          connectionService.addConnector(lite);
      } else {
    	  oldConnector.setServiceHealth(serviceHealth);    	  
      }
   }

   @Override
   public void fail(Throwable th) {
	   OseeLog.log(Activator.class, Level.SEVERE, th);
   }

   @Override
   public void success() {
   }
}
