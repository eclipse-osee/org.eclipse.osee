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
package org.eclipse.osee.framework.database.init.internal;

import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Roberto E. Escobar
 */
public class Activator implements BundleActivator {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.database.init";

   private static Activator instance;

   private ServiceTracker<IOseeCachingService, IOseeCachingService> serviceTracker2;
   private ServiceTracker<JdbcService, JdbcService> serviceTracker3;

   @Override
   public void start(BundleContext context) throws Exception {
      Activator.instance = this;

      serviceTracker2 =
         new ServiceTracker<>(context, IOseeCachingService.class, null);
      serviceTracker2.open(true);

      serviceTracker3 = new ServiceTracker<>(context, JdbcService.class, null);
      serviceTracker3.open(true);

   }

   @Override
   public void stop(BundleContext context) throws Exception {
      if (serviceTracker2 != null) {
         serviceTracker2.close();
      }
      if (serviceTracker3 != null) {
         serviceTracker3.close();
      }
   }

   public static Activator getInstance() {
      return instance;
   }

   public IOseeCachingService getCachingService() {
      return serviceTracker2.getService();
   }

   public JdbcClient getJdbcClient() {
      JdbcService jdbcService = serviceTracker3.getService();
      return jdbcService.getClient();
   }

}
