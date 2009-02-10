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
package org.eclipse.osee.connection.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

class ConnectionServiceImpl implements IConnectionService {
	
   private final HashSet<IServiceConnector> connectors = new HashSet<IServiceConnector>();
   private final EventNotifier eventNotifier = new EventNotifier();
   
   private boolean isStopped = false;

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IConnectionService#addConnector(org.eclipse.osee.connection.service.IServiceConnector)
    */
   @Override
   public void addConnector(IServiceConnector connector) {
      addConnectors(Collections.singletonList(connector));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IConnectionService#addConnectors(java.util.Collection)
    */
   @Override
   public synchronized void addConnectors(Collection<IServiceConnector> connectors) {
      checkState();
      this.connectors.addAll(connectors);
      eventNotifier.notifyConnectorsAdded(connectors);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IConnectionService#addListener(org.eclipse.osee.connection.service.IConnectorListener)
    */
   @Override
   public synchronized void addListener(IConnectorListener listener) {
      checkState();
      eventNotifier.addListener(listener);
      listener.onConnectorsAdded(connectors);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IConnectionService#findConnectors(java.lang.String)
    */
   @Override
   public synchronized List<IServiceConnector> findConnectors(IConnectorFilter[] filterChain) {
      checkState();
      ArrayList<IServiceConnector> matchingConnectors = new ArrayList<IServiceConnector>();
      for (IServiceConnector connector : connectors) {
         boolean accepted = true;
         for (IConnectorFilter filter : filterChain) {
            if (!filter.accept(connector)) {
               accepted = false;
               break;
            }
         }
         if (accepted) {
            matchingConnectors.add(connector);
         }
      }
      return matchingConnectors;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IConnectionService#getAllConnectors()
    */
   @Override
   public synchronized Collection<IServiceConnector> getAllConnectors() {
      checkState();
      return connectors;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IConnectionService#removeConnector(org.eclipse.osee.connection.service.IServiceConnector)
    */
   @Override
   public synchronized void removeConnector(IServiceConnector connector) throws Exception {
      checkState();
      if (connectors.remove(connector)) {
         eventNotifier.notifyConnectorRemoved(connector);
         connector.stop();
      }

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IConnectionService#removeListener(org.eclipse.osee.connection.service.IConnectorListener)
    */
   @Override
   public synchronized void removeListener(IConnectorListener listener) {
      checkState();
      eventNotifier.removeListener(listener);
   }

   void stop() {
      isStopped = true;
      eventNotifier.notifyServiceStopped();
      for (IServiceConnector connector : connectors) {
         try {
            connector.stop();
         } catch (Exception ex) {
            Activator.log(Level.SEVERE,
			"Exception notifying listener of service stop", ex);
         }
      }
      connectors.clear();
   }

   private void checkState() throws IllegalStateException {
      if (isStopped) {
         throw new IllegalStateException("service has been stopped");
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IConnectionService#isStopped()
    */
   @Override
   public boolean isStopped() {
      return isStopped;
   }

}
