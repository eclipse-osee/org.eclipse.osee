/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
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
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.util.ServiceDependencyTracker;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.internal.trackers.OseeDatabaseServiceRegistrationHandler;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Roberto E. Escobar
 */
public class Activator implements BundleActivator, IOseeDatabaseServiceProvider {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.database";

   private static Activator instance = null;

   private ServiceDependencyTracker databaseServiceTracker;
   private ServiceTracker dbTracker;
   private ServiceTracker cacheTracker;
   private DatabaseInfoProvider databaseInfoProvider;

   @Override
   public void start(BundleContext bundleContext) throws Exception {
      instance = this;
      databaseInfoProvider = new DatabaseInfoProvider(bundleContext);

      databaseServiceTracker =
         new ServiceDependencyTracker(bundleContext, new OseeDatabaseServiceRegistrationHandler());
      databaseServiceTracker.open();

      dbTracker = new ServiceTracker(bundleContext, IOseeDatabaseService.class.getName(), null);
      dbTracker.open(true);

      cacheTracker = new ServiceTracker(bundleContext, IOseeCachingService.class.getName(), null);
      cacheTracker.open(true);
   }

   public DatabaseInfoProvider getDatabaseInfoProvider() {
      return databaseInfoProvider;
   }

   @Override
   public IOseeDatabaseService getOseeDatabaseService() throws OseeDataStoreException {
      IOseeDatabaseService databaseService = (IOseeDatabaseService) dbTracker.getService();
      if (databaseService == null) {
         throw new OseeDataStoreException("OseeDatabaseService not found");
      }
      return databaseService;
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      databaseServiceTracker.close();
      dbTracker.close();
      cacheTracker.close();
      databaseInfoProvider = null;
      instance = null;
   }

   public static Activator getInstance() {
      return instance;
   }

   public static IOseeCachingService getOseeCachingService() {
      return (IOseeCachingService) getInstance().cacheTracker.getService();
   }
}
