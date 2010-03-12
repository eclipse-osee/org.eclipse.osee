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
package org.eclipse.osee.ote.message.interfaces;

import java.rmi.RemoteException;
import java.util.Collection;
import org.eclipse.osee.ote.core.environment.BundleDescription;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;
import org.eclipse.osee.ote.message.IInstrumentationRegistrationListener;
import org.eclipse.osee.ote.message.instrumentation.IOInstrumentation;



/**
 * @author Andrew M. Finkbeiner
 */
public interface ITestEnvironmentMessageSystem extends ITestEnvironment {
   IRemoteMessageService getMessageToolServiceProxy() throws RemoteException;  
   @Deprecated 
   boolean isMessageJarAvailable(String version) throws RemoteException;
   boolean isBundleAvailable(String symbolicName, String version, byte[] md5Digest) throws RemoteException;
   @Deprecated
   void sendRuntimeJar(byte[] messageJar) throws RemoteException; 
   void sendRuntimeBundle(Collection<BundleDescription> bundles) throws RemoteException; 
   void updateRuntimeBundle(Collection<BundleDescription> bundles) throws RemoteException; 
   void cleanupRuntimeBundles() throws RemoteException; 
   void setupClassLoaderAndJar(String[] jarVersions, String classPath) throws RemoteException;
   void setupClassLoaderAndJar(String[] jarVersion, String[] classPaths) throws RemoteException;
   IOInstrumentation getIOInstrumentation(String name) throws RemoteException;
   void addInstrumentationRegistrationListener(IInstrumentationRegistrationListener listener) throws RemoteException;
   void removeInstrumentationRegistrationListener(IInstrumentationRegistrationListener listener) throws RemoteException;
   
}
