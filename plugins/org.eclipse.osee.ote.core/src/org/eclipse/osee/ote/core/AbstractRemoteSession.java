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

import java.rmi.RemoteException;

public abstract class AbstractRemoteSession implements IRemoteUserSession {

   private final OSEEPerson1_4 user;

   protected AbstractRemoteSession(OSEEPerson1_4 user) {
      this.user = user;
   }

   @Override
   public OSEEPerson1_4 getUser() {
      return user;
   }

   @Override
   public abstract String getAddress() throws RemoteException;

   @Override
   public abstract byte[] getFile(String workspacePath) throws RemoteException;

   @Override
   public abstract long getFileDate(String workspacePath) throws RemoteException;

   @Override
   public abstract boolean isAlive() throws RemoteException;

}
