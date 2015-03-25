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
package org.eclipse.osee.ote.core.environment.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;
import org.eclipse.osee.framework.messaging.NodeInfo;
import org.eclipse.osee.ote.core.ConnectionRequestResult;
import org.eclipse.osee.ote.core.IRemoteUserSession;
import org.eclipse.osee.ote.core.environment.TestEnvironmentConfig;

/**
 * @author Andrew M. Finkbeiner
 */
public interface IHostTestEnvironment extends Remote {
   EnhancedProperties getProperties() throws RemoteException;
   
   ConnectionRequestResult requestEnvironment(IRemoteUserSession session, UUID id, TestEnvironmentConfig config) throws RemoteException;
   void disconnect(UUID sessionId) throws RemoteException;
   
   public NodeInfo getBroker() throws RemoteException;
   
   public String getHttpURL() throws RemoteException;
   
}
