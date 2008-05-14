package org.eclipse.osee.framework.servlet;

import java.util.logging.Level;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.servlet.log.ILogger;
import org.eclipse.osee.framework.servlet.log.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {
   private static Activator instance;

   private ServiceTracker logServiceTracker;
   private ServiceTracker resourceManagementTracker;
   private ServiceTracker resourceLocatorManagerTracker;
   private ServiceTracker servletTracker;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      Activator.instance = this;
      context.registerService(ILogger.class.getName(), new Logger(), null);

      logServiceTracker = new ServiceTracker(context, ILogger.class.getName(), null);
      logServiceTracker.open();

      resourceManagementTracker = new ServiceTracker(context, IResourceManager.class.getName(), null);
      resourceManagementTracker.open();

      resourceLocatorManagerTracker = new ServiceTracker(context, IResourceLocatorManager.class.getName(), null);
      resourceLocatorManagerTracker.open();

      servletTracker = new HttpServiceTracker(context);
      servletTracker.open();
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      servletTracker.close();
      servletTracker = null;

      resourceManagementTracker.close();
      resourceManagementTracker = null;

      resourceLocatorManagerTracker.close();
      resourceLocatorManagerTracker = null;

      logServiceTracker.close();
      logServiceTracker = null;

      Activator.instance = null;
   }

   public ILogger getLogger() {
      return (ILogger) logServiceTracker.getService();
   }

   public IResourceManager getResourceManager() {
      return (IResourceManager) resourceManagementTracker.getService();
   }

   public IResourceLocatorManager getResourceLocatorManager() {
      return (IResourceLocatorManager) resourceLocatorManagerTracker.getService();
   }

   public static Activator getInstance() {
      return Activator.instance;
   }

   private class HttpServiceTracker extends ServiceTracker {
      public HttpServiceTracker(BundleContext context) {
         super(context, HttpService.class.getName(), null);
      }

      public Object addingService(ServiceReference reference) {
         HttpService httpService = (HttpService) context.getService(reference);
         try {
            httpService.registerServlet("/resource", new ResourceManagerServlet(), null, null);
            getLogger().log(Level.INFO, "Registered servlet '/resource'");
         } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "Error registering servlet", ex);
         }
         return httpService;
      }

      public void removedService(ServiceReference reference, Object service) {
         HttpService httpService = (HttpService) service;
         httpService.unregister("/resource");
         getLogger().log(Level.INFO, "De-registering servlet '/resource'");
         super.removedService(reference, service);
      }
   }
}
