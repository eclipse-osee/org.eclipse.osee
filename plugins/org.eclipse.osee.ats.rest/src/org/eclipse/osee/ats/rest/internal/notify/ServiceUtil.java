package org.eclipse.osee.ats.rest.internal.notify;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * This util class is for getting service
 *
 * @author Donald G. Dunne
 */
public final class ServiceUtil {

   private ServiceUtil() {
      // Utility class
   }

   private static <T> T getService(final Class<T> clazz) throws OseeCoreException {
      Bundle bundle = FrameworkUtil.getBundle(ServiceUtil.class);
      Conditions.checkNotNull(bundle, "bundle");
      BundleContext context = bundle.getBundleContext();
      Conditions.checkNotNull(context, "bundleContext");
      ServiceReference<T> reference = context.getServiceReference(clazz);
      Conditions.checkNotNull(reference, "serviceReference");
      T service = context.getService(reference);
      Conditions.checkNotNull(service, "service");
      return service;
   }

   /**
    * get package admin
    *
    * @return
    * @throws OseeCoreException
    */
   public static PackageAdmin getPackageAdmin() throws OseeCoreException {
      return getService(PackageAdmin.class);
   }

}
