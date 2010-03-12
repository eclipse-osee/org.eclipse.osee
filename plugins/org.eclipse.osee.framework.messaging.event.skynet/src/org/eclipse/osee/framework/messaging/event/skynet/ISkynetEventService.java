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
package org.eclipse.osee.framework.messaging.event.skynet;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.eclipse.osee.framework.messaging.event.skynet.filter.IEventFilter;

public interface ISkynetEventService extends Remote {

   // TODO all calls to the server go here
   /**
    * Register to receive events based on filters.
    * 
    * @param filters
    */
   public void register(ISkynetEventListener listener, IEventFilter... filters) throws RemoteException;

   /**
    * Deregister to receive events based on filters.
    * 
    * @param filters
    */
   public void deregister(ISkynetEventListener listener, IEventFilter... filters) throws RemoteException;

   /**
    * Pass events to the service to be disemminated.
    * 
    * @param events
    */
   public void kick(ISkynetEvent[] events, ISkynetEventListener... except) throws RemoteException;

   public boolean isAlive() throws RemoteException;
}