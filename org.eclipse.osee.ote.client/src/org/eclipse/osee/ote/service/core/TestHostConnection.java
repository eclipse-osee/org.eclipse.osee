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

import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.ote.core.environment.UserTestSessionKey;
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
   private final UserTestSessionKey sessionKey;

   /**
    * @param connectedTestHost
    * @param connectEnvironment
    * @param sessionKey
    */
   TestHostConnection(IServiceConnector connector, ITestEnvironment connectEnvironment, UserTestSessionKey sessionKey) {
      // intentionally package-private
      if (connector == null) {
         throw new NullPointerException("service connector cannot be null");
      }
      if (connectEnvironment == null) {
         throw new NullPointerException("test environment cannot be null");
      }
      if (sessionKey == null) {
         throw new NullPointerException("session key cannot be null");
      }
      this.serviceConnector = connector;
      this.connectEnvironment = connectEnvironment;
      this.sessionKey = sessionKey;
   }

   /**
    * @return the connectedTestHost
    */
   public IHostTestEnvironment getConnectedTestHost() {
      return (IHostTestEnvironment) serviceConnector.getService();
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
   public UserTestSessionKey getSessionKey() {
      return sessionKey;
   }

   /**
    * @return the serviceConnector
    */
   public IServiceConnector getServiceConnector() {
      return serviceConnector;
   }

   /**
    * @throws RemoteException
    */
   void endConnection() throws RemoteException {
      // intentionally package-private

      connectEnvironment.disconnect(sessionKey);
   }
}
