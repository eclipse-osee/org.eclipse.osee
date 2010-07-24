package org.eclipse.osee.framework.access.internal;

import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.access.IAccessProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.lifecycle.ILifecycleService;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.BundleContext;

public final class ObjectAccessProviderRegistrationHandler extends AbstractTrackingHandler {

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