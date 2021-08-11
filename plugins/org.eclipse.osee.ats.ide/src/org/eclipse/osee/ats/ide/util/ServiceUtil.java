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

import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Donald G. Dunne
 */
public class ServiceUtil {

   private static <T> T getService(Class<T> serviceClass) {
      return OsgiUtil.getService(ServiceUtil.class, serviceClass);
   }

   public static OseeClient getOseeClient() {
      return getService(OseeClient.class);
   }

   public static BundleContext getContext() {
      return FrameworkUtil.getBundle(ServiceUtil.class).getBundleContext();
   }
}