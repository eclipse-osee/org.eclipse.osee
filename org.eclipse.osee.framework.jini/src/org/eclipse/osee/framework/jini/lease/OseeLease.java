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

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Date;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lease.LeaseMap;
import net.jini.core.lease.UnknownLeaseException;

public class OseeLease implements Lease, Serializable {

   private static final long serialVersionUID = -2821773288662499183L;

   private ILeaseGrantor leaseManager;
   private Object myConsumer;

   private int serialFormat = Lease.DURATION;
   private long duration;
   private long startTime;

   public OseeLease(ILeaseGrantor manager, Object consumer, long duration) {
      super();
      this.myConsumer = consumer;
      this.duration = duration;
      leaseManager = manager;
   }

   public long getExpiration() {
      return duration + startTime;
   }

   public void cancel() throws UnknownLeaseException, RemoteException {
      leaseManager.cancelRequest(this, myConsumer);
   }

   /**
    * Requests that the lease be renewed.
    */
   public void renew(long durationFromNow) throws LeaseDeniedException, UnknownLeaseException, RemoteException {
      System.out.println("Requesting Lease Renewal: @" + new Date());
      leaseManager.renewRequest(this, myConsumer, duration);
      resetStartTime();
   }

   /* package */void setDuration(long duration) {
      this.duration = duration;
   }

   /* package */void resetStartTime() {
      startTime = System.currentTimeMillis();
   }

   public void setSerialFormat(int leaseFormat) {
      if (leaseFormat == Lease.DURATION)
         serialFormat = Lease.DURATION;
      else if (leaseFormat == Lease.ABSOLUTE)
         serialFormat = Lease.ABSOLUTE;
      else
         assert false : leaseFormat;
   }

   public int getSerialFormat() {
      return serialFormat;
   }

   public LeaseMap createLeaseMap(long duration) {
      return null;
   }

   public boolean canBatch(Lease lease) {
      return false;
   }

   public long getDuration() {
      return duration;
   }

}
