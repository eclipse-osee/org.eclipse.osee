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
import java.util.Collection;

import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;
import org.eclipse.osee.ote.core.ConnectionRequestResult;
import org.eclipse.osee.ote.core.IRemoteUserSession;
import org.eclipse.osee.ote.core.environment.BundleDescription;
import org.eclipse.osee.ote.core.environment.TestEnvironmentConfig;
import org.eclipse.osee.ote.core.environment.UserTestSessionKey;

/**
 * @author Andrew M. Finkbeiner
 */
public interface IHostTestEnvironment extends Remote {
   ITestEnvironment[] getRemoteEnvironments() throws RemoteException;

   byte[] getOutfileResultSummary() throws RemoteException;

   ConnectionRequestResult requestEnvironment(IRemoteUserSession session, TestEnvironmentConfig config) throws RemoteException;

   EnhancedProperties getProperties() throws RemoteException;
   
   boolean isBundleAvailable(String symbolicName, String version, byte[] md5Digest) throws RemoteException;

   void sendRuntimeBundle(Collection<BundleDescription> bundles) throws RemoteException; 
   
   void updateRuntimeBundle(Collection<BundleDescription> bundles) throws RemoteException; 
   
   void cleanupRuntimeBundles() throws RemoteException; 

   void disconnect(UserTestSessionKey key) throws RemoteException;
}
