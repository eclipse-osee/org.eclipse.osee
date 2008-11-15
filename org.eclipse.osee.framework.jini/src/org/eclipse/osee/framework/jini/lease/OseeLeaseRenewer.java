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

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lease.UnknownLeaseException;
import org.eclipse.osee.framework.jini.JiniPlugin;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * This class handles the renewal of leases. It is designed such that a lease will be renewed a set amount of time prior
 * to expiration, and handles retrying in cases where the renewal fails. Leases are renewed for the maximum amount of
 * time the lease will allow. Retries are be scheduled for half of the remaining lease time. For example, if the first
 * attempt to renew occurs with 2 minutes left, the first retry will occur with 1 minute, then 30 seconds, until
 * STOP_RETRY_TIME is reached.
 * 
 * @author David Diepenbrock
 */
public class OseeLeaseRenewer {

   private Timer timer;
   private Map<OseeLease, Renewer> map;
   private final Logger logger = Logger.getLogger(OseeLeaseRenewer.class.getName());

   /**
    * The amount of time before a lease expires to first attempt renewal. This amount of time should be sufficiently
    * large to account for delays in communication (i.e. network delays), and allow for at least a few retries in the
    * event the service is not reachable. This time is specified in milliseconds.
    */
   private static final long RENEWAL_TIME = 2 * 60 * 1000; // 2 minutes
   /**
    * When less than this amount of time is remaining in a lease, failed renewal attempts will not be retried. This time
    * is specified in milliseconds.
    */
   private static final long STOP_RETRY_TIME = 250; // 250 ms

   public OseeLeaseRenewer() {
      // debug = new Debug(false, true, this.getClass().getName());
      map = new HashMap<OseeLease, Renewer>(4);
   }

   /**
    * Attempts to maintain a lease until cancelRenewal is called
    * 
    * @param lease
    */
   public synchronized void startRenewal(OseeLease lease) {
      if (timer == null) timer = new Timer(true);

      lease.resetStartTime();
      Renewer renewer = new Renewer(lease);
      map.put(lease, renewer);

      // Pick the larger duration - RENEWAL_TIME before the expiration, or half of the total lease
      // time.
      long duration = Math.max(lease.getDuration() / 2, lease.getDuration() - RENEWAL_TIME);
      timer.schedule(renewer, duration, duration);
   }

   /**
    * Prevents the lease from being renewed. This does not cancel the lease, only the renewal attempts.
    * 
    * @param lease
    */
   public synchronized void cancelRenewal(OseeLease lease) {
      Renewer renewer = map.remove(lease);
      if (renewer != null)
         renewer.cancel();
      else
         OseeLog.log(JiniPlugin.class, Level.WARNING,
               this.getClass().getName() + ": Lease Cancel Attempt: Lease Not Found!");

      if (map.isEmpty()) {
         timer.cancel();
         timer = null;
      }
   }

   private class Renewer extends TimerTask {

      private OseeLease lease;

      public Renewer(OseeLease lease) {
         super();
         this.lease = lease;
      }

      public void run() {
         try {
            // Obtain the longest lease allowed
            lease.renew(Lease.FOREVER);
         } catch (LeaseDeniedException ex) {
            OseeLog.log(JiniPlugin.class, Level.SEVERE, ex.getMessage(), ex);
         } catch (UnknownLeaseException ex) {
            OseeLog.log(JiniPlugin.class, Level.SEVERE, ex.getMessage(), ex);
         }
         /*
          * If there was a problem with the lease renewal, retry up until there is less than 250 ms
          * remaining in the lease, at which point we can give up hope. Assuming leases are renewed
          * with 2 minutes remaining & 250 ms for STOP_RETRY_TIME, this will provide 8 attempts.
          */
         catch (RemoteException ex) {
            long remainingTime = lease.getExpiration() - System.currentTimeMillis();
            if (remainingTime > STOP_RETRY_TIME)
               timer.schedule(new Renewer(lease), remainingTime / 2);
            else {
               // debug.report("Canceling Renewals Retries");
               cancelRenewal(lease);
               OseeLog.log(JiniPlugin.class, Level.SEVERE, "Unable to renew lease.", ex);
            }
         }
      }
   }
}
