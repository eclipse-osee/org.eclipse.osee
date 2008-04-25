package org.eclipse.osee.framework.logging;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

   // The shared instance
   private static Activator me;
   private OseeLogger logger;
   private StatusManager statusManager;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      me = this;
      logger = new OseeLogger();
      statusManager = new StatusManager();
      OseeLog.makevalid();
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      me = null;
      logger = null;
      statusManager = null;
   }

   /**
    * @return
    */
   public static Activator getInstance() {
      return me;
   }

   public OseeLogger getLogger() {
      return logger;
   }

   StatusManager getStatusManager() {
      return statusManager;
   }
}
