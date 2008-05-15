package org.eclipse.osee.framework.branch.management;

import org.eclipse.osee.framework.branch.management.impl.BranchCreation;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

   private ServiceRegistration serviceRegistration;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      serviceRegistration = context.registerService(IBranchCreation.class.getName(), new BranchCreation(), null);
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      serviceRegistration.unregister();
   }

}
