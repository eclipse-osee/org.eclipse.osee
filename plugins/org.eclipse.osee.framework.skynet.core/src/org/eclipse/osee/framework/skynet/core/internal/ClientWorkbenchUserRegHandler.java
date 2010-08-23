/*
 * Created on Aug 19, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.internal;

import java.util.Map;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.plugin.core.IWorkbenchUserService;
import org.eclipse.osee.framework.skynet.core.WorkbenchUserService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class ClientWorkbenchUserRegHandler extends AbstractTrackingHandler {

   private static final Class<?>[] DEPENDENCIES = new Class<?>[] {IOseeCachingService.class};

   private ServiceRegistration serviceRegistration;

   @Override
   public Class<?>[] getDependencies() {
      return DEPENDENCIES;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      serviceRegistration =
         context.registerService(IWorkbenchUserService.class.getName(), new WorkbenchUserService(), null);
   }

   @Override
   public void onDeActivate() {
      if (serviceRegistration != null) {
         serviceRegistration.unregister();
      }
   }

}
