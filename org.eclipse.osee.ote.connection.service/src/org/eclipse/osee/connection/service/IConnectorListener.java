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
