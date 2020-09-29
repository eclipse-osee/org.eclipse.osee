/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.ui.skynet.internal;

import org.eclipse.osee.account.rest.client.AccountClient;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.cm.IOseeCmService;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.osgi.service.packageadmin.PackageAdmin;

@SuppressWarnings("deprecation")
public final class ServiceUtil {

   private ServiceUtil() {
      // Utility class
   }

   private static <T> T getService(Class<T> clazz) {
      return OsgiUtil.getService(ServiceUtil.class, clazz);
   }

   public static AccountClient getAccountClient() {
      return getService(AccountClient.class);
   }

   public static OseeClient getOseeClient() {
      return getService(OseeClient.class);
   }

   public static IOseeCachingService getOseeCacheService() {
      return getService(IOseeCachingService.class);
   }

   public static OrcsTokenService getTokenService() {
      return getOseeCacheService().getTokenService();
   }

   public static PackageAdmin getPackageAdmin() {
      return getService(PackageAdmin.class);
   }

   public static IOseeCmService getOseeCmService() {
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
