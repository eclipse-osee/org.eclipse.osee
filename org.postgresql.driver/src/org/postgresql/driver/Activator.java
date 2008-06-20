package org.postgresql.driver;

import org.eclipse.osee.framework.db.connection.IConnection;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/*
 * Created on Apr 23, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */

public class Activator implements BundleActivator {

   private ServiceRegistration registration;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      registration = context.registerService(IConnection.class.getName(), new PostgresqlConnection(), null);
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      registration.unregister();
   }

}
