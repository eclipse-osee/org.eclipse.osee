/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.access.internal;

import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public final class AccessControlHelper {

   public static final String PLUGIN_ID = "org.eclipse.osee.framework.access";

   private AccessControlHelper() {
      // Utility class
   }

   private static BundleContext getBundleContext() throws OseeCoreException {
      Bundle bundle = FrameworkUtil.getBundle(AccessControlHelper.class);
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

   @Deprecated
   public static AccessControlService getAccessControlService() throws OseeCoreException {
      IAccessControlService service = getService(IAccessControlService.class);
      AccessControlService toReturn = null;
      if (service instanceof AccessControlServiceProxy) {
         toReturn = ((AccessControlServiceProxy) service).getProxiedObject();
      }
      return toReturn;
   }

}
