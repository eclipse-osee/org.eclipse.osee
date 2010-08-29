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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.eclipse.osee.connection.service.IConnectionService;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.messaging.NodeInfo;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.services.RemoteServiceLookup;
import org.eclipse.osee.framework.messaging.services.ServiceNotification;
import org.eclipse.osee.framework.messaging.services.messages.ServiceDescriptionPair;
import org.eclipse.osee.framework.messaging.services.messages.ServiceHealth;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.eclipse.osee.ote.core.OteBaseMessages;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;

/**
 * @author Andrew M. Finkbeiner
 */
class OteJmsServiceConnector implements ServiceNotification, OseeMessagingStatusCallback {

   private final ConcurrentHashMap<String, JmsToJiniBridgeConnector> connectors;
   private final ConcurrentHashMap<String, ServiceHealth> serviceHealthMap;
   private final RemoteServiceLookup remoteServiceLookup;
   private final MessageService messageService;
   private final IConnectionService connectionService;
   private final ExportClassLoader exportClassLoader;
   private final OseeMessagingListener myOteServiceRequestHandler;

   OteJmsServiceConnector(RemoteServiceLookup remoteServiceLookup, MessageService messageService, IConnectionService connectionService, ExportClassLoader exportClassLoader) {
      this.remoteServiceLookup = remoteServiceLookup;
      this.messageService = messageService;
      this.connectionService = connectionService;
      this.exportClassLoader = exportClassLoader;
      myOteServiceRequestHandler = new OteServiceRequestHandler();
      connectors = new ConcurrentHashMap<String, JmsToJiniBridgeConnector>();
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
      JmsToJiniBridgeConnector connector = removeExistingConnector(serviceHealth);
      if (connector != null) {
         try {
            connectionService.removeConnector(connector);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   private JmsToJiniBridgeConnector removeExistingConnector(ServiceHealth serviceHealth) {
      return connectors.remove(serviceHealth.getServiceUniqueId());
   }

   @Override
   public synchronized void onServiceUpdate(ServiceHealth serviceHealth) {
      serviceHealthMap.put(serviceHealth.getServiceUniqueId(), serviceHealth);
      if (isNewService(serviceHealth)) {
         requestJmsJiniBridgeConnector(serviceHealth);
      } else {
         updateServiceProperties(serviceHealth);
      }
   }

   private void updateServiceProperties(ServiceHealth serviceHealth) {
      JmsToJiniBridgeConnector item = connectors.get(serviceHealth.getServiceUniqueId());
      if (item != null) {
         for (ServiceDescriptionPair pair : serviceHealth.getServiceDescription()) {
            item.setProperty(pair.getName(), pair.getValue());
         }
      }
   }

   private void requestJmsJiniBridgeConnector(ServiceHealth serviceHealth) {
      try {
         //			ConnectionNode connectionNode = messageService.get(new NodeInfo(serviceHealth.getServiceUniqueId(), new URI(serviceHealth.getBrokerURI())));
         ConnectionNode connectionNode = messageService.getDefault();
         connectionNode.subscribeToReply(OteBaseMessages.RequestOteHost, myOteServiceRequestHandler);
         connectionNode.send(OteBaseMessages.RequestOteHost, serviceHealth.getServiceUniqueId(), this);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      //		catch (URISyntaxException ex) {
      //			OseeLog.log(Activator.class, Level.SEVERE, ex);
      //		}
   }

   private boolean isNewService(ServiceHealth serviceHealth) {
      return !connectors.containsKey(serviceHealth.getServiceUniqueId());
   }

   @Override
   public void fail(Throwable th) {
      th.printStackTrace();
   }

   @Override
   public void success() {
   }

   class OteServiceRequestHandler extends OseeMessagingListener {

      @Override
      public void process(Object message, Map<String, Object> headers, ReplyConnection replyConnection) {

         try {
            //            System.out.println("please be a remote reference");
            ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) message);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object msg = ois.readObject();
            msg.toString();
            Object obj = ois.readObject();
            IHostTestEnvironment hostEnv = (IHostTestEnvironment) obj;
            String id = hostEnv.getProperties().getProperty("id").toString();
            if (id != null && connectors.get(id) == null) {
               JmsToJiniBridgeConnector connector = new JmsToJiniBridgeConnector(exportClassLoader, hostEnv, id);
               connector.setProperty("OTEEmbeddedBroker", getNodeInfo(id));
               connectors.put(id, connector);
               connectionService.addConnector(connector);
            }
         } catch (IOException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         } catch (ClassNotFoundException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         } catch (URISyntaxException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   private NodeInfo getNodeInfo(String id) throws URISyntaxException {
      ServiceHealth serviceHealth = this.serviceHealthMap.get(id);
      if (serviceHealth != null) {
         return new NodeInfo("OTEEmbeddedBroker", new URI(serviceHealth.getBrokerURI()));
      }
      return null;
   }

}
