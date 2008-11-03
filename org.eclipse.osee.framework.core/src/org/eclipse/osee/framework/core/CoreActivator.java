package org.eclipse.osee.framework.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class CoreActivator implements BundleActivator {

   private static CoreActivator instance = null;
   private BundleContext bundleContext;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      instance = this;
      instance.bundleContext = context;
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      instance.bundleContext = null;
      instance = null;
   }

   public static CoreActivator getInstance() {
      return instance;
   }

   public BundleContext getBundleContext() {
      return bundleContext;
   }
}
