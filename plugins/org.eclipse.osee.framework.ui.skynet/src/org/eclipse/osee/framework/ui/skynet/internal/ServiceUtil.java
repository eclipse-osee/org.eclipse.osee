/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.internal;

import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.AccessPolicy;
import org.eclipse.osee.framework.ui.skynet.cm.IOseeCmService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

public final class ServiceUtil {

   private ServiceUtil() {
      // Utility class
   }

   private static <T> T getService(Class<T> clazz) throws OseeCoreException {
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

   public static IOseeCachingService getOseeCacheService() throws OseeCoreException {
      return getService(IOseeCachingService.class);
   }

   public static IOseeDatabaseService getOseeDatabaseService() throws OseeCoreException {
      return getService(IOseeDatabaseService.class);
   }

   public static PackageAdmin getPackageAdmin() throws OseeCoreException {
      return getService(PackageAdmin.class);
   }

   public static AccessPolicy getAccessPolicy() throws OseeCoreException {
      try {
         Bundle bundle = Platform.getBundle("org.eclipse.osee.framework.access");
         if (bundle.getState() != Bundle.ACTIVE) {
            bundle.start();
         }
      } catch (BundleException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return getService(AccessPolicy.class);
   }

   public static IOseeCmService getOseeCmService() throws OseeCoreException {
      return getService(IOseeCmService.class);
   }

   public static boolean isOseeCmServiceAvailable() {
      boolean result = false;
      try {
         result = getOseeCmService() != null;
      } catch (OseeCoreException ex) {
         // Do Nothing;
      }
      return result;
   }
}
