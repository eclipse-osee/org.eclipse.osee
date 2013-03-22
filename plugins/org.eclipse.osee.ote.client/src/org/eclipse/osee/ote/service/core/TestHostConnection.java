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
package org.eclipse.osee.ote.service.core;

import java.rmi.RemoteException;
import java.util.UUID;

import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;

/**
 * Encapsulated all information regarding the current connection between a client and a test server.
 * 
 * @author Ken J. Aguilar
 */
class TestHostConnection {
   // intentionally package-private

   private final IServiceConnector serviceConnector;
   private final ITestEnvironment connectEnvironment;
   private final UUID sessionKey;
   private final IHostTestEnvironment host;
   private String serverId;

   TestHostConnection(IServiceConnector connector, IHostTestEnvironment host, ITestEnvironment connectEnvironment, UUID uuid) {
      // intentionally package-private
      if (connector == null) {
         throw new NullPointerException("service connector cannot be null");
      }
      if (connectEnvironment == null) {
         throw new NullPointerException("test environment cannot be null");
      }
      if (uuid == null) {
         throw new NullPointerException("session key cannot be null");
      }
      this.serviceConnector = connector;
      this.host = host;
      this.connectEnvironment = connectEnvironment;
      this.sessionKey = uuid;
      try {
		this.serverId = (String) host.getProperties().getProperty("id");
      } catch (RemoteException e) {
    	  this.serverId = "";
      }
   }

   /**
    * @return the connectedTestHost
    */
   public IServiceConnector getConnectedTestHost() {
      return serviceConnector;
   }

   /**
    * @return the connectEnvironment
    */
   public ITestEnvironment getConnectEnvironment() {
      return connectEnvironment;
   }

   /**
    * @return the sessionKey
    */
   public UUID getSessionKey() {
      return sessionKey;
   }

   /**
    * @return the serviceConnector
    */
   public IServiceConnector getServiceConnector() {
      return serviceConnector;
   }

   public String getId(){
	   return serverId;
   }
   
   void endConnection() throws RemoteException {
      // intentionally package-private

      host.disconnect(sessionKey);
   }
}
