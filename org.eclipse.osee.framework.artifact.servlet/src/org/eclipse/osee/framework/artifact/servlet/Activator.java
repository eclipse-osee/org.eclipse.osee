package org.eclipse.osee.framework.artifact.servlet;

import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

   private static Activator instance;

   private HttpServiceTracker httpTracker;
   private ServiceTracker resourceManagementTracker;
   private ServiceTracker resourceLocatorManagerTracker;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      instance = this;

      resourceLocatorManagerTracker = new ServiceTracker(context, IResourceLocatorManager.class.getName(), null);
      resourceLocatorManagerTracker.open();

      resourceManagementTracker = new ServiceTracker(context, IResourceManager.class.getName(), null);
      resourceManagementTracker.open();

      httpTracker = new HttpServiceTracker(context);
      httpTracker.open();
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      httpTracker.close();
      httpTracker = null;

      resourceManagementTracker.close();
      resourceManagementTracker = null;

      resourceLocatorManagerTracker.close();
      resourceLocatorManagerTracker = null;

      instance = null;
   }

   public static Activator getInstance() {
      return instance;
   }

   public IResourceManager getResourceManager() {
      return (IResourceManager) resourceManagementTracker.getService();
   }

   public IResourceLocatorManager getResourceLocatorManager() {
      return (IResourceLocatorManager) resourceLocatorManagerTracker.getService();
   }

   private class HttpServiceTracker extends ServiceTracker {
      public HttpServiceTracker(BundleContext context) {
         super(context, HttpService.class.getName(), null);
      }

      public Object addingService(ServiceReference reference) {
         HttpService httpService = (HttpService) context.getService(reference);
         try {
            httpService.registerServlet("/GET.ARTIFACT", new ArtifactFileServlet(), null, null);
            System.out.println("Registered servlet '/GET.ARTIFACT'");
         } catch (Exception ex) {
         }
         return httpService;
      }

      public void removedService(ServiceReference reference, Object service) {
         HttpService httpService = (HttpService) service;
         httpService.unregister("/GET.ARTIFACT");
         System.out.println("De-registering servlet '/GET.ARTIFACT'");
         super.removedService(reference, service);
      }
   }
}
