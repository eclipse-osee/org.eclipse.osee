package org.eclipse.osee.connection.service;

import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

   private ConnectionServiceImpl service;
   private ServiceTracker connectionServiceTracker;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      service = new ConnectionServiceImpl();

      // register the service
      context.registerService(IConnectionService.class.getName(), service, null);

      // create a tracker and track the service
      connectionServiceTracker = new ServiceTracker(context, IConnectionService.class.getName(), null);
      connectionServiceTracker.open();

   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      // close the service tracker
      connectionServiceTracker.close();
      connectionServiceTracker = null;
      service.stop();
      service = null;
   }
   
   public static void log(Level level, String message, Throwable t) {
		OseeLog.log("org.eclipse.osee.connection.service", level, message, t);
	}

	public static void log(Level level, String message) {
		log(level, message, null);
	}

}
