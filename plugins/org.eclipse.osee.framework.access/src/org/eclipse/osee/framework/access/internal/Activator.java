package org.eclipse.osee.framework.access.internal;

import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.access.AccessControlService;
import org.eclipse.osee.framework.access.IAccessProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.core.util.ServiceDependencyTracker;
import org.eclipse.osee.framework.lifecycle.ILifecycleService;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransactionCheckPoint;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransactionHandler;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

   public static final String PLUGIN_ID = "org.eclipse.osee.framework.access";

   private ServiceDependencyTracker tracker;

   public void start(BundleContext context) throws Exception {
      tracker = new ServiceDependencyTracker(context, new TrackingHandler());
      tracker.open();
   }

   public void stop(BundleContext context) throws Exception {
      if (tracker != null) {
         tracker.close();
      }
   }

   private static final class TrackingHandler extends AbstractTrackingHandler {

      private static final Class<?>[] DEPENDENCIES = new Class[] {ILifecycleService.class};

      private SkynetTransactionHandler handler;
      private IAccessProvider accessProvider;
      private ILifecycleService service;

      @Override
      public Class<?>[] getDependencies() {
         return DEPENDENCIES;
      }

      @Override
      public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
         service = (ILifecycleService) services.get(ILifecycleService.class);
         try {
            handler = new SkynetTransactionAccessHandler(AccessControlService.getInstance());
            service.addHandler(SkynetTransactionCheckPoint.TYPE, handler);

            accessProvider = new ObjectAccessProvider();
            service.addHandler(AccessProviderVisitor.TYPE, accessProvider);

         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }

      @Override
      public void onDeActivate() {
         if (handler != null) {
            try {
               service.removeHandler(SkynetTransactionCheckPoint.TYPE, handler);
               service.removeHandler(AccessProviderVisitor.TYPE, accessProvider);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }

   }
}
