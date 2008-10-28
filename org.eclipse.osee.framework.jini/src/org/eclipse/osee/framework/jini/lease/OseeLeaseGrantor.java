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

import java.lang.ref.WeakReference;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lease.UnknownLeaseException;
import org.eclipse.osee.framework.jini.util.OseeJini;

/**
 * This class manages tasks associated with being a lease grantor. It generates leases, handles renewal & cancelation of
 * the leases, and checks the existing leases to determine if any have expired. Provides a callback by way of the
 * ILeasee interface to the leasing service to notify it when a lease is canceled or expired.
 * 
 * @author David Diepenbrock
 */
public class OseeLeaseGrantor implements ILeaseGrantor {

   public static final long maxDuration = 10 * 60 * 1000; /* 10 minutes */
   public static final long minDuration = 2 * 60 * 1000; /* 2 minutes */
   private ILeaseGrantor thisRemoteReference;
   private Map<Object, LeaseData> leaseStore;
   private WeakReference<ILeasee> leasee;
   private LeaseChecker leaseChecker;
   private Timer myTimer;
   private boolean cancelTimer;

   /**
    * Use of this constructor will generate a new timer thread for lease checks
    * 
    * @param leasee The "parent" object to be notified when a lease expires or is canceled.
    */
   public OseeLeaseGrantor(ILeasee leasee) {
      this(leasee, new Timer());
      // debug = new Debug(false, true, this.getClass().getName());
      cancelTimer = true; // We will need to cancel the timer on shutdown.
   }

   /**
    * @param leasee The "parent" object to be notified when a lease expires or is canceled
    * @param timer An existing Timer thread to schedule lease checks
    */
   public OseeLeaseGrantor(ILeasee leasee, Timer timer) {
      this.leasee = new WeakReference<ILeasee>(leasee);
      myTimer = timer;
      cancelTimer = false; // If a timer was provided we don't need to cancel it on shutdown
      leaseStore = Collections.synchronizedMap(new HashMap<Object, LeaseData>());

      try {
         thisRemoteReference = (ILeaseGrantor) OseeJini.getRemoteReference(this);
      } catch (ExportException ex) {
         ex.printStackTrace();
      }

      long leaseCheckFrequency = minDuration / 2;
      leaseChecker = new LeaseChecker();
      timer.schedule(leaseChecker, leaseCheckFrequency, leaseCheckFrequency);
   }

   /**
    * Call this to notify the grantor that we are shutting down. We need to clean up the task for checking the leases.
    */
   public void shutdown() {
      leaseChecker.cancel();
      if (cancelTimer) myTimer.cancel();
   }

   /**
    * Returns a new lease
    * 
    * @param consumer
    * @param duration
    * @return Return lease reference.
    * @throws LeaseDeniedException
    * @throws ExportException
    */
   public OseeLease newLease(Object consumer, long duration) throws LeaseDeniedException, ExportException {
      // debug.report("New Lease:" + consumer + " @" + new Date());
      long actualDuration = checkDuration(duration);
      OseeLease lease = new OseeLease(thisRemoteReference, consumer, actualDuration);
      leaseStore.put(consumer, new LeaseData(actualDuration));
      return lease;
   }

   /**
    * Note that the consumer must reset their start timer on the lease. The Grantor cannot set the time since the system
    * clocks may differ.
    * 
    * @see org.eclipse.osee.framework.jini.lease.ILeaseGrantor#renewRequest
    */
   public void renewRequest(Lease lease, Object consumer, long duration) throws LeaseDeniedException, UnknownLeaseException, RemoteException {

      // debug.report("Lease renewRequest: " + consumer + " @" + new Date());

      synchronized (leaseStore) {
         LeaseData leaseData = leaseStore.get(consumer);
         if (leaseData == null) throw new LeaseDeniedException("Consumer does not currently hold a lease");

         if (lease instanceof OseeLease) {
            long actualDuration = checkDuration(duration);
            leaseData.setDuration(actualDuration);
            leaseData.setStartTime();
            ((OseeLease) lease).setDuration(actualDuration);
         } else
            throw new UnknownLeaseException("Unknown Lease Type");
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jini.lease.ILeaseGrantor#cancelRequest(net.jini.core.lease.Lease,
    *      null)
    */
   public void cancelRequest(Lease lease, Object consumer) throws UnknownLeaseException, RemoteException {
      // debug.report("Lease cancelRequest: " + consumer + " @" + new Date());
      if (leaseStore.remove(consumer) != null) {
         leasee.get().onLeaseCompleted(consumer);
      }
   }

   public boolean isLeaseExpired(Object consumer) {
      LeaseData leaseData = leaseStore.get(consumer);
      if (leaseData != null) return leaseData.isExpired();

      return true;
   }

   private long checkDuration(long duration) throws LeaseDeniedException {
      long actualDuration;
      if (duration > maxDuration)
         actualDuration = maxDuration;
      else if (duration >= minDuration)
         actualDuration = duration;
      else if (duration == Lease.ANY)
         actualDuration = maxDuration;
      else
         throw new LeaseDeniedException("Duration too short - must be at least " + minDuration + " milliseconds.");

      return actualDuration;
   }

   private class LeaseChecker extends TimerTask {

      public void run() {
         synchronized (leaseStore) {
            Set<Entry<Object, LeaseData>> set = leaseStore.entrySet();
            Iterator<Entry<Object, LeaseData>> iter = set.iterator();
            while (iter.hasNext()) {
               Entry<Object, LeaseData> entry = iter.next();
               if (entry.getValue().isExpired()) {
                  leasee.get().onLeaseCompleted(entry.getKey());
                  iter.remove();
               }
            }
         }
      }

   }

}
