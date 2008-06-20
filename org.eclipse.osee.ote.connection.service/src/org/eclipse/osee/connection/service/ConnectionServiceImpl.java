package org.eclipse.osee.connection.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

class ConnectionServiceImpl implements IConnectionService {

   private final HashSet<IServiceConnector> connectors = new HashSet<IServiceConnector>();
   private final HashSet<IConnectorListener> connectorListener = new HashSet<IConnectorListener>();

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
      for (IConnectorListener listener : connectorListener) {
         try {
            listener.onConnectorsAdded(connectors);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IConnectionService#addListener(org.eclipse.osee.connection.service.IConnectorListener)
    */
   @Override
   public synchronized void addListener(IConnectorListener listener) {
      checkState();
      connectorListener.add(listener);
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
         for (IConnectorListener listener : connectorListener) {
            try {
               listener.onConnectorRemoved(connector);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
         connector.stop();
      }

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IConnectionService#removeListener(org.eclipse.osee.connection.service.IConnectorListener)
    */
   @Override
   public synchronized void removeListener(IConnectorListener listener) {
      checkState();
      connectorListener.remove(listener);
   }

   void stop() {
      isStopped = true;
      for (IConnectorListener listener : connectorListener) {
         listener.onConnectionServiceStopped();
      }
      connectorListener.clear();
      for (IServiceConnector connector : connectors) {
         try {
            connector.stop();
         } catch (Exception ex) {
            ex.printStackTrace();
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
