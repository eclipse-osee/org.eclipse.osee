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

import java.io.IOException;
import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.reportdata.ReportDataListener;
import org.eclipse.osee.framework.messaging.Message;
import org.eclipse.osee.ote.core.IUserSession;
import org.eclipse.osee.ote.core.OSEEPerson1_4;
import org.eclipse.osee.ote.core.cmd.Command;
import org.eclipse.osee.ote.core.environment.UserTestSessionKey;
import org.eclipse.osee.ote.core.environment.status.IServiceStatusListener;
import org.eclipse.osee.ote.core.framework.command.ICommandHandle;
import org.eclipse.osee.ote.core.framework.command.ITestServerCommand;
import org.eclipse.osee.ote.core.model.IModelManagerRemote;

/**
 * @author Andrew M. Finkbeiner
 */
public interface ITestEnvironment extends Remote {

   Remote getControlInterface(String controlInterfaceID) throws RemoteException;

   public Collection<OSEEPerson1_4> getUserList() throws RemoteException;

   ICommandHandle addCommand(ITestServerCommand cmd) throws RemoteException;

   void addEnvironmentListener(ITestEnvironmentListener listener) throws RemoteException;

   void addStatusListener(IServiceStatusListener listener) throws RemoteException;

   UserTestSessionKey addUser(IUserSession user) throws RemoteException, Exception;

   void disconnect(UserTestSessionKey user) throws RemoteException;

   boolean equals(ITestEnvironment testEnvironment) throws RemoteException;

   List<String> getQueueLabels() throws RemoteException;

   public IModelManagerRemote getModelManager() throws RemoteException;

   public Remote getRemoteModel(String modelClassName, Class<?>[] methodParameterTypes, Object[] methodParameters) throws RemoteException;

   public Remote getRemoteModel(String modelClassName) throws RemoteException;

   byte[] getScriptOutfile(String outfilePath) throws RemoteException;

   int getUniqueId() throws RemoteException;

   void removeQueueListener(ReportDataListener listener) throws RemoteException;

   void removeStatusListener(IServiceStatusListener listener) throws RemoteException;

   URL setBatchLibJar(byte[] messageJar) throws RemoteException, IOException;

   void onHostKilled() throws RemoteException;

   void startup(String outfileDir) throws RemoteException;

   void setClientClasses(URL[] urls) throws RemoteException;

   IRemoteCommandConsole getCommandConsole() throws RemoteException;

   public void closeCommandConsole(IRemoteCommandConsole console) throws RemoteException;

   public void setBatchMode(boolean isBatched) throws RemoteException;

   public void sendCommand(Command command) throws RemoteException;
   
   public void sendMessage(Message message) throws RemoteException;
   
}