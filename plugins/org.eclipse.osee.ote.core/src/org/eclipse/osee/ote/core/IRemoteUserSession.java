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
package org.eclipse.osee.ote.core;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.eclipse.osee.framework.messaging.Message;
import org.eclipse.osee.ote.core.framework.prompt.IPassFailPromptResponse;
import org.eclipse.osee.ote.core.framework.prompt.IResumeResponse;
import org.eclipse.osee.ote.core.framework.prompt.IUserInputPromptResponse;

/**
 * @author Ken J. Aguilar
 */
public interface IRemoteUserSession extends Remote, IUserSession {
   public String getAddress() throws RemoteException;

   public OSEEPerson1_4 getUser() throws RemoteException;

   public byte[] getFile(String workspacePath) throws RemoteException;

   public long getFileDate(String workspacePath) throws RemoteException;

   public String getFileVersion(String workspacePath) throws RemoteException;

   public void initiatePassFailPrompt(IPassFailPromptResponse prompt) throws RemoteException;

   public void initiateUserInputPrompt(IUserInputPromptResponse prompt) throws RemoteException;

   public void initiateResumePrompt(IResumeResponse prompt) throws RemoteException;

   public void initiateInformationalPrompt(String message) throws RemoteException;

   public boolean isAlive() throws RemoteException;
   
   public void sendMessageToClient(Message message)  throws RemoteException;

}
