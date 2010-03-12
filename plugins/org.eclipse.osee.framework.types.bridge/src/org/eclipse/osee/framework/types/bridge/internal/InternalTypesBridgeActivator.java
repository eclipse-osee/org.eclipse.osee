/*
 * Created on Mar 10, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.types.bridge.internal;

import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class InternalTypesBridgeActivator extends OseeUiActivator implements IOseeModelFactoryServiceProvider {

   // The plug-in ID
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.types.bridge";

   // The shared instance
   private static InternalTypesBridgeActivator plugin;

   private ServiceTracker cacheServiceTracker;
   private ServiceTracker factoryTracker;

   /**
    * The constructor
    */
   public InternalTypesBridgeActivator() {
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
    */
   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
      plugin = this;

      factoryTracker = new ServiceTracker(context, IOseeModelFactoryService.class.getName(), null);
      factoryTracker.open();

      cacheServiceTracker = new ServiceTracker(context, IOseeCachingService.class.getName(), null);
      cacheServiceTracker.open();

   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
    */
   @Override
   public void stop(BundleContext context) throws Exception {
      plugin = null;
      cacheServiceTracker.close();
      factoryTracker.close();
      super.stop(context);
   }

   /**
    * Returns the shared instance
    * 
    * @return the shared instance
    */
   public static InternalTypesBridgeActivator getDefault() {
      return plugin;
   }

   public IOseeCachingService getOseeCacheService() {
      return (IOseeCachingService) cacheServiceTracker.getService();
   }

   public IOseeModelFactoryService getOseeFactoryService() {
      return (IOseeModelFactoryService) factoryTracker.getService();
   }

}
