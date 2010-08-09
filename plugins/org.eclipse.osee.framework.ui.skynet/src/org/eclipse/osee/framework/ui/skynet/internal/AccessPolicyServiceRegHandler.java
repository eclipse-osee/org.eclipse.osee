/*
 * Created on Aug 9, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.internal;

import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.IAccessPolicyHandlerService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class AccessPolicyServiceRegHandler extends AbstractTrackingHandler {
   private IAccessPolicyHandlerService service;

   //@formatter:off
   private static final Class<?>[] SERVICE_DEPENDENCY = new Class<?>[] {
      IAccessControlService.class,
   };
   //@formatter:on

   private ServiceRegistration serviceRegistration;

   @Override
   public Class<?>[] getDependencies() {
      return SERVICE_DEPENDENCY;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      IAccessControlService accessService = getService(IAccessControlService.class, services);
      try {
         IBasicArtifact<?> workbenchUser = UserManager.getUser();
         service = new AccessPolicyHandlerServiceImpl(workbenchUser, accessService);
         serviceRegistration = context.registerService(IAccessPolicyHandlerService.class.getName(), service, null);
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void onDeActivate() {
      if (serviceRegistration != null) {
         serviceRegistration.unregister();
      }
   }

   public IAccessPolicyHandlerService getService() throws OseeCoreException {
      Conditions.checkNotNull(service, "IAccessPolicyHandlerService",
         "IAccessPolicyHandlerService has not been registered");
      return service;
   }

}
