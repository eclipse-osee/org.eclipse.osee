package org.eclipse.osee.framework.access.internal;

import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.access.IAccessProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.core.util.ServiceDependencyTracker;
import org.eclipse.osee.framework.lifecycle.ILifecycleService;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

   public static final String PLUGIN_ID = "org.eclipse.osee.framework.access";

   private static AccessControlServiceRegHandler handler;

   private ServiceDependencyTracker tracker1;
   private ServiceDependencyTracker tracker2;

   public void start(BundleContext context) throws Exception {
      handler = new AccessControlServiceRegHandler();
      tracker1 = new ServiceDependencyTracker(context, handler);
      tracker1.open();

      tracker2 = new ServiceDependencyTracker(context, new TrackingHandler());
      tracker2.open();
   }

   public void stop(BundleContext context) throws Exception {
      if (tracker1 != null) {
         tracker1.close();
      }
      if (tracker2 != null) {
         tracker2.close();
      }
   }

   public static AccessControlService getAccessControlService() {
      return handler.getService();
   }

   private static final class AccessControlServiceRegHandler extends AbstractTrackingHandler {

      private static final Class<?>[] DEPENDENCIES = new Class[] {IOseeCachingService.class};

      private AccessControlService accessService;
      private ServiceRegistration serviceRegistration;;

      public AccessControlService getService() {
         return accessService;
      }

      @Override
      public Class<?>[] getDependencies() {
         return DEPENDENCIES;
      }

      @Override
      public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
         IOseeCachingService service = (IOseeCachingService) services.get(IOseeCachingService.class);
         accessService = new AccessControlService(service);
         serviceRegistration = context.registerService(IAccessControlService.class.getName(), accessService, null);
      }

      @Override
      public void onDeActivate() {
         if (serviceRegistration != null) {
            serviceRegistration.unregister();
         }
      }
   }

   private static final class TrackingHandler extends AbstractTrackingHandler {

      private static final Class<?>[] DEPENDENCIES = new Class[] {IAccessControlService.class, ILifecycleService.class};

      private IAccessProvider accessProvider;
      private ILifecycleService service;

      @Override
      public Class<?>[] getDependencies() {
         return DEPENDENCIES;
      }

      @Override
      public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
         service = (ILifecycleService) services.get(ILifecycleService.class);
         IAccessControlService accessService = (IAccessControlService) services.get(IAccessControlService.class);
         try {
            // TODO remove specific access control service cast
            accessProvider = new ObjectAccessProvider((AccessControlService) accessService);
            service.addHandler(AccessProviderVisitor.TYPE, accessProvider);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }

      @Override
      public void onDeActivate() {
         if (accessProvider != null) {
            try {
               service.removeHandler(AccessProviderVisitor.TYPE, accessProvider);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }

   }

}
