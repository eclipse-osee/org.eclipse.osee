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
package org.eclipse.osee.ote.core.model;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.eclipse.osee.ote.core.environment.EnvironmentTask;
import org.eclipse.osee.ote.core.environment.TestEnvironment;


/**
 * @author Andrew M. Finkbeiner
 */
public interface IModel extends Remote{
   void turnModelOn() throws RemoteException;
   void turnModelOff() throws RemoteException;
   @SuppressWarnings("unchecked")
   void init(TestEnvironment testEnvironment, ModelKey key) throws RemoteException;   
   ModelState getState() throws RemoteException;
   @SuppressWarnings("unchecked")
   <CLASSTYPE extends IModel> ModelKey getKey() throws RemoteException;
   void dispose() throws RemoteException;
   EnvironmentTask getEnvironmentTask() throws RemoteException;
}
