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
package org.eclipse.osee.framework.jini.lease;

import java.rmi.Remote;
import java.rmi.RemoteException;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lease.UnknownLeaseException;

public interface ILeaseGrantor extends Remote {

   /**
    * @param lease The existing lease
    * @param consumer The "consumer" of the lease (as opposed to the grantor of the lease)
    * @param duration The amount of time requested for the renewed lease
    * @throws LeaseDeniedException If the lease is denied for any reason
    * @throws UnknownLeaseException If the lease type is unknown
    * @throws RemoteException
    */
   public void renewRequest(Lease lease, Object consumer, long duration) throws LeaseDeniedException, UnknownLeaseException, RemoteException;

   /**
    * @param lease The existing lease
    * @param consumer The "consumer" of the lease (as opposed to the grantor of the lease)
    * @throws UnknownLeaseException If the lease type is unknown
    * @throws RemoteException
    */
   public void cancelRequest(Lease lease, Object consumer) throws UnknownLeaseException, RemoteException;

}
