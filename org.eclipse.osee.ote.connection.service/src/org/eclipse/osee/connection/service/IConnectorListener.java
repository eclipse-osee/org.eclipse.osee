/*
 * Created on May 1, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.connection.service;

import java.util.Collection;

/**
 * @author b1529404
 */
public interface IConnectorListener {

   void onConnectorsAdded(Collection<IServiceConnector> connectors);

   void onConnectorRemoved(IServiceConnector connector);

   /**
    * this method will be called when the connect service has been commanded to stop but before it is actually stopped.
    * this gives clients a chance to cleanup however, clients should not call any methods on the service.
    */
   void onConnectionServiceStopped();
}
