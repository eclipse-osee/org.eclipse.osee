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
package org.eclipse.osee.framework.skynet.core.internal;

import java.util.Properties;
import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.cache.admin.CacheAdmin;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.AccessPolicy;
import org.eclipse.osee.framework.skynet.core.event.OseeEventService;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

public final class ServiceUtil {

   private static IOseeCachingService cacheService;

   private ServiceUtil() {
      // Utility class
   }

   private static <T> T getService(Class<T> clazz) {
      return OsgiUtil.getService(ServiceUtil.class, clazz);
   }

   public static CacheAdmin getCacheAdmin() {
      return getService(CacheAdmin.class);
   }

   public static OseeEventService getEventService() {
      return getService(OseeEventService.class);
   }

   public static IOseeCachingService getOseeCacheService() {
      if (cacheService == null) {
         cacheService = getService(IOseeCachingService.class);
      }
      return cacheService;
   }

   public static OseeClient getOseeClient() {
      return getService(OseeClient.class);
   }

   public static AccessPolicy getAccessPolicy() {
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

   public static boolean useOracleHints() {
      return ClientSessionManager.useOracleHints();
   }

   public static String getSql(OseeSql sqlEnum) {
      Properties properties = ClientSessionManager.getSqlProperties();
      String sql = properties.getProperty(sqlEnum.toString());
      if (sql != null) {
         return sql;
      }
      throw new OseeArgumentException("Invalid sql key [%s]", sqlEnum.toString());
   }
}
