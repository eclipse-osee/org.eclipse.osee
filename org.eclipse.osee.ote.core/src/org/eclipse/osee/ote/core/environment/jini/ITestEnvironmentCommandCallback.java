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
package org.eclipse.osee.ote.core.environment.jini;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.eclipse.osee.ote.core.TestPrompt;
import org.eclipse.osee.ote.core.environment.status.ExceptionEvent;

/**
 * @author Andrew M. Finkbeiner
 */
public interface ITestEnvironmentCommandCallback extends Remote {
	void initiatePrompt(TestPrompt prompt) throws RemoteException;
	void exceptionReceived(ExceptionEvent event) throws RemoteException;
	long getFileDate(String workspacePath) throws RemoteException;
	byte[] getFile(String workspacePath) throws RemoteException;
	Object[] getValues(String key) throws RemoteException;
	boolean isAlive() throws RemoteException;
	String getFileVersion(String workspacePath)throws RemoteException;
	String getAddress() throws RemoteException;
}
