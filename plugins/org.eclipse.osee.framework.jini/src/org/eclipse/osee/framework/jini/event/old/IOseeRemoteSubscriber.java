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
package org.eclipse.osee.framework.jini.event.old;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IOseeRemoteSubscriber extends Remote {
   /**
    * @return false iff the event should no longer be sent to this subscriber
    */
   public boolean receiveEventType(String event) throws RemoteException;

   /**
    * @return false iff the event should no longer be sent to this subscriber
    */
   public boolean receiveEventGuid(String event) throws RemoteException;
}
