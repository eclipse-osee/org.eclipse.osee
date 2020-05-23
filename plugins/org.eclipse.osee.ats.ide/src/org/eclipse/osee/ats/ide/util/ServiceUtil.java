/*********************************************************************
 * Copyright (c) 2016 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.util;

import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Donald G. Dunne
 */
public class ServiceUtil {
   @SuppressWarnings({"rawtypes", "unchecked"})
   public static <T> T getService(Class<T> clazz) {
      BundleContext context = getContext();
      if (context == null) {
         return null;
      }
      ServiceReference serviceReference = context.getServiceReference(clazz.getName());
      if (serviceReference == null) {
         return null;
      }
      return (T) getContext().getService(serviceReference);
   }

   public static OseeClient getOseeClient() {
      return getService(OseeClient.class);
   }

   public static BundleContext getContext() {
      return FrameworkUtil.getBundle(ServiceUtil.class).getBundleContext();
   }
}
