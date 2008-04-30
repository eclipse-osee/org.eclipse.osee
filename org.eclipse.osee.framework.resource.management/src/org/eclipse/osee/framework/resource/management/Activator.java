package org.eclipse.osee.framework.resource.management;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

//   private ServiceTracker simpleLogServiceTracker;
//   private SimpleLogService simpleLogService;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      // register the service
//      context.registerService(SimpleLogService.class.getName(), new SimpleLogServiceImpl(), new Hashtable());
//      context.registerService(IResourceManager.class.getName(), new ResourceManager(),null);

      // create a tracker and track the log service
//      simpleLogServiceTracker = new ServiceTracker(context, SimpleLogService.class.getName(), null);
//      simpleLogServiceTracker.open();

      // grab the service
//      simpleLogService = (SimpleLogService) simpleLogServiceTracker.getService();
//      if (simpleLogService != null) simpleLogService.log("Yee ha, I'm logging!");
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
//      if (simpleLogService != null) simpleLogService.log("Yee ha, I'm logging!");
//
//      // close the service tracker
//      simpleLogServiceTracker.close();
//      simpleLogServiceTracker = null;
//
//      simpleLogService = null;
   }

}
