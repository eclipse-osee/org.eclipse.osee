package org.eclipse.osee.ote.connection.jini.util;

import java.util.Timer;
import java.util.TimerTask;
import net.jini.core.lease.Lease;
import net.jini.core.lookup.ServiceRegistration;

public class LeaseRenewTask extends TimerTask {
   /**
    * The amount of time before a lease expires to first attempt renewal. This amount of time should be sufficiently
    * large to account for delays in communication (i.e. network delays), and allow for at least a few retries in the
    * event the service is not reachable. This time is specified in milliseconds.
    */
   private static final long RENEWAL_TIME = 2 * 60 * 1000; // 2 minutes

   private final ServiceRegistration registration;

   public LeaseRenewTask(Timer timer, ServiceRegistration registration) {
      this.registration = registration;
      long delay = registration.getLease().getExpiration() - System.currentTimeMillis() - RENEWAL_TIME;
      timer.scheduleAtFixedRate(this, delay, delay);
   }

   public void run() {
      System.out.println("renewing lease");
      try {
         // Renew for the maximum amount of time allowed
         registration.getLease().renew(Lease.FOREVER);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   /* (non-Javadoc)
    * @see java.util.TimerTask#cancel()
    */
   @Override
   public boolean cancel() {
      boolean result = super.cancel();
      try {
         registration.getLease().cancel();
      } catch (Exception ex) {
         throw new RuntimeException("failed to cancel lease", ex);
      }
      return result;
   }

}