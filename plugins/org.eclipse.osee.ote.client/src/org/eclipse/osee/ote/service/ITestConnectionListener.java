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
package org.eclipse.osee.ote.service;

import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;

/**
 * @author Ken J. Aguilar
 */
public interface ITestConnectionListener {

   void onPostConnect(ConnectionEvent event);

   /**
    * the connection to the service is broken unexpectedly. The service is not considered valid and thus clients should
    * not try to communicate in any way with the OTE server. Clients are free to modify any local state.
    * 
    * @param connector
    * @param testHost
    */
   void onConnectionLost(IServiceConnector connector, IHostTestEnvironment testHost);

   /**
    * this method is called when a disconnect request is initiated by the client. The connector and the service are
    * still valid. This allows clients to remove any listeners on the remote OTE server or set some state prior to the
    * actual disconnect.
    * 
    * @param event
    */
   void onPreDisconnect(ConnectionEvent event);

}
