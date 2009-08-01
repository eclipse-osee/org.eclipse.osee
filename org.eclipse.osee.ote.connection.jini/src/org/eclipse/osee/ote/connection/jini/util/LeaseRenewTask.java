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
package org.eclipse.osee.ote.connection.jini.util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import net.jini.core.lookup.ServiceRegistration;

import org.eclipse.osee.ote.connection.jini.Activator;

public class LeaseRenewTask extends TimerTask {
   /**
    * The amount of time before a lease expires to first attempt renewal. This amount of time should be sufficiently
    * large to account for delays in communication (i.e. network delays), and allow for at least a few retries in the
    * event the service is not reachable. This time is specified in milliseconds.
    */
   private static final long RENEWAL_TIME = 2 * 60 * 1000; // 2 minutes

   private final ServiceRegistration registration;
   private volatile boolean canceled = false;
   
   public LeaseRenewTask(Timer timer, ServiceRegistration registration) {
      this.registration = registration;
      timer.scheduleAtFixedRate(this, 0, RENEWAL_TIME);
   }

   public void run() {
	   if (canceled) {
		   return;
	   }
      try {
         // Renew for the maximum amount of time allowed
	    registration.getLease().renew(RENEWAL_TIME + 10000);
      } catch (Exception ex) {
         Activator.log(Level.SEVERE, "error renewing lease", ex);
      }
   }

   @Override
   public boolean cancel() {
	   canceled = true;
      boolean result = super.cancel();
      try {
         registration.getLease().cancel();
      } catch (Exception ex) {
         throw new RuntimeException("failed to cancel lease", ex);
      }
      return result;
   }

}