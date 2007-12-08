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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.jini.core.lookup.ServiceItem;
import org.eclipse.osee.framework.jini.service.interfaces.IService;
import org.eclipse.osee.framework.ui.service.control.data.ServiceNode;
import org.eclipse.osee.framework.ui.service.control.managers.interfaces.IConnectionListener;
import org.eclipse.osee.framework.ui.service.control.renderer.IServiceRenderer;

/**
 * @author Roberto E. Escobar
 */
public class ConnectionManager {

   private static ConnectionManager instance = null;
   private boolean connected;
   private ServiceNode connectedTo;
   private Class<?> connectionType;
   private Collection<IConnectionListener> listeners;
   private Map<Class<?>, IServiceRenderer> allowedConnectionTypes;

   private ConnectionManager() {
      super();
      this.connected = false;
      this.connectedTo = null;
      this.connectionType = null;
      this.listeners = Collections.synchronizedList(new ArrayList<IConnectionListener>());
      this.allowedConnectionTypes = new HashMap<Class<?>, IServiceRenderer>();
   }

   public static ConnectionManager getInstance() {
      if (instance == null) {
         instance = new ConnectionManager();
      }
      return instance;
   }

   public void addConnectionListener(IConnectionListener listener) {
      synchronized (listeners) {
         listeners.add(listener);
      }
      listener.onConnectionChanged(connectedTo, connected);
   }

   public void removeConnectionListener(IConnectionListener listener) {
      synchronized (listeners) {
         listeners.remove(listener);
      }
   }

   private boolean isAbleToConnect(Object service) {
      boolean returnVal = true;
      if (service instanceof IService) {
         try {
            ((IService) service).getServiceID();
         } catch (RemoteException e) {
            returnVal = false;
         }
      }
      return returnVal;
   }

   private void notifyConnected(ServiceNode serviceNode, boolean connected) {
      synchronized (listeners) {
         for (IConnectionListener listener : listeners) {
            listener.onConnectionChanged(serviceNode, connected);
         }
      }
   }

   public void forceDisconnect() {
      if (connectedTo != null) {
         if (connected) {
            handleDisconnect();
         }
      }
   }

   public void attemptConnection(ServiceNode serviceNode) throws ServiceConnectionException {
      if (connectedTo != null) {
         ServiceNode temp = connectedTo;
         if (connected) {
            handleDisconnect();
         }

         if (!temp.getServiceID().equals(serviceNode.getServiceID())) {
            handleConnect(serviceNode);
         }
      } else {
         handleConnect(serviceNode);
      }
   }

   private void handleConnect(ServiceNode node) throws ServiceConnectionException {
      if (isAbleToConnect(node.getServiceItem().service)) {
         connected = true;
         connectedTo = node;
         this.connectionType = getConnectionType(node.getServiceItem());
         connectedTo.setConnected(connected);
         notifyConnected(connectedTo, connected);
      } else {
         throw new ServiceConnectionException();
      }
   }

   private void handleDisconnect() {
      connected = false;
      allowedConnectionTypes.get(connectionType).disconnect();
      connectedTo.setConnected(connected);
      notifyConnected(connectedTo, connected);
      connectionType = null;
      connectedTo = null;
   }

   public boolean isConnected() {
      return connected;
   }

   public Class<?> getConnectionType() {
      return connectionType;
   }

   public Class<?> getConnectionType(ServiceItem serviceItem) {
      for (Class<?> connectionType : allowedConnectionTypes.keySet()) {
         if (connectionType.isInstance(serviceItem.service)) {
            return connectionType;
         }
      }
      return null;
   }

   public IServiceRenderer getRenderer() {
      return allowedConnectionTypes.get(connectionType);
   }

   public boolean isAllowedConnectionType(ServiceItem serviceItem) {
      for (Class<?> connectionType : allowedConnectionTypes.keySet()) {
         if (connectionType.isInstance(serviceItem.service)) {
            return true;
         }
      }
      return false;
   }

   public void registerForConnection(Class<?> serviceType, IServiceRenderer renderer) {
      allowedConnectionTypes.put(serviceType, renderer);
   }

}
