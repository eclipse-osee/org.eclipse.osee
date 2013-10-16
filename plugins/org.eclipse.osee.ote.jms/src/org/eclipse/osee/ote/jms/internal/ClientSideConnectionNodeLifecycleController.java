/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.jms.internal;

import java.io.Serializable;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.messaging.NodeInfo;
import org.eclipse.osee.ote.jms.OteServerJmsNodeProvider;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.service.IOteClientService;
import org.eclipse.osee.ote.service.ITestConnectionListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;



/**
 * @author Michael P. Masterson
 */
public final class ClientSideConnectionNodeLifecycleController implements ITestConnectionListener {

   private MessageService messageService;
   private IOteClientService clientService;
   private BundleContext context;
   private ServiceRegistration<?> registration;

   public void start(BundleContext context) {
      this.context = context;
      clientService.addConnectionListener(this);

   }

   public void stop() {
      clientService.removeConnectionListener(this);
      unregisterConnectionNode();
      this.messageService = null;
      this.clientService = null;
      this.context = null;

   }

   public void setMessageService(MessageService messageService) {
      this.messageService = messageService;
   }

   public void setClientService(IOteClientService clientService) {
      this.clientService = clientService;
   }

   @Override
   public void onPostConnect(ConnectionEvent event) {
      if (context == null) {
         OseeLog.log(getClass(), Level.SEVERE, "Received a onPostConnect event with a null context.");
         return;
      }
      IServiceConnector connector = event.getConnector();
      registerConnectionNode(connector);
   }

   /**
    * @param connector
    */
   private void registerConnectionNode(IServiceConnector connector) {
      Object obj = connector.getProperty("OTEEmbeddedBroker", null);
      if (obj != null && obj instanceof NodeInfo) {
         debug("Registering client connection service");
         NodeInfo nodeInfo = (NodeInfo) obj;
         registerConnectionNode(nodeInfo);
      } else {
         debug(String.format("Problem using connector...%s:%s", obj.getClass(), obj));
         EnhancedProperties properties = connector.getProperties();
         for( Entry<String, Serializable> entry : properties.entrySet() ) {
            debug(String.format("\t%s = %s", entry.getKey(), entry.getValue()));
         }
      }
   }

   /**
    * @param nodeInfo
    */
   private void registerConnectionNode(NodeInfo nodeInfo) {
      try {
         ConnectionNode node = messageService.get(nodeInfo);
         registration =
               context.registerService(OteServerJmsNodeProvider.class.getName(), new ClientSideConnectionNodeProviderImpl(node), null);
      } catch (OseeCoreException ex) {
         OseeLog.log(getClass(), Level.SEVERE, ex);
      }
   }

   @Override
   public void onConnectionLost(IServiceConnector connector) {
      unregisterConnectionNode();
   }



   private void unregisterConnectionNode() {
      if (registration != null) {
         registration.unregister();
         registration = null;
      }
   }

   @Override
   public void onPreDisconnect(ConnectionEvent event) {
      unregisterConnectionNode();
   }

   private void debug(String msg ) {
      if( System.getProperty("ote.jms.node.debug") != null )
         System.out.println(msg);
   }
}
