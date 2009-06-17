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
package org.eclipse.osee.ote.message.instrumentation;

import java.net.InetSocketAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public interface IOInstrumentation extends Remote{

   void register(InetSocketAddress address) throws RemoteException;
   void command(byte[] cmd) throws RemoteException;
   void unregister(InetSocketAddress address) throws RemoteException;
}
