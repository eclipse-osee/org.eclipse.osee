/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.database.internal;

import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
public final class ServiceUtil {

   public static final String PLUGIN_ID = "org.eclipse.osee.framework.database";

   private ServiceUtil() {
      // Utility class
   }

   private static BundleContext getBundleContext() throws OseeCoreException {
      Bundle bundle = FrameworkUtil.getBundle(ServiceUtil.class);
      Conditions.checkNotNull(bundle, "bundle");
      return bundle.getBundleContext();
   }

   private static <T> T getService(Class<T> clazz) throws OseeCoreException {
      BundleContext context = getBundleContext();
      Conditions.checkNotNull(context, "bundleContext");
      ServiceReference<T> reference = context.getServiceReference(clazz);
      Conditions.checkNotNull(reference, "serviceReference");
      T service = context.getService(reference);
      Conditions.checkNotNull(service, "service");
      return service;
   }

   public static IOseeDatabaseService getDatabaseService() throws OseeDataStoreException {
      try {
         return getService(IOseeDatabaseService.class);
      } catch (OseeCoreException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

}
