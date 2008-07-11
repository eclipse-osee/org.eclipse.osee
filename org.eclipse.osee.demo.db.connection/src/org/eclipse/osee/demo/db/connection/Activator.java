package org.eclipse.osee.demo.db.connection;

import org.eclipse.osee.framework.db.connection.IDbConnectionInformationContributer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

   private static Activator me;
   private BundleContext context;
   private ServiceRegistration registration;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      me = this;
      this.context = context;
      registration =
            this.context.registerService(IDbConnectionInformationContributer.class.getName(), new DbConnectionInfo(),
                  null);
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      registration.unregister();
   }

   public static Activator getInstance() {
      return me;
   }

   public BundleContext getBundleContext() {
      return this.context;
   }

}
