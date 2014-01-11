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

import org.eclipse.osee.framework.messaging.Message;
import org.eclipse.osee.ote.core.cmd.Command;
import org.eclipse.osee.ote.core.model.IModelManagerRemote;

/**
 * @author Andrew M. Finkbeiner
 */
public interface ITestEnvironment extends Remote {

   Remote getControlInterface(String controlInterfaceID) throws RemoteException;

   IRemoteCommandConsole getCommandConsole() throws RemoteException;
   public void closeCommandConsole(IRemoteCommandConsole console) throws RemoteException;

   public IModelManagerRemote getModelManager() throws RemoteException;

   byte[] getScriptOutfile(String outfilePath) throws RemoteException;

   int getUniqueId() throws RemoteException;

   public void setBatchMode(boolean isBatched) throws RemoteException;

   public void sendCommand(Command command) throws RemoteException;

   public void sendMessage(Message message) throws RemoteException;

}