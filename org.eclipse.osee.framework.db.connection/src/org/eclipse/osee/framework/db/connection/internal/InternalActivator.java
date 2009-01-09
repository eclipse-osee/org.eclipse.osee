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
package org.eclipse.osee.framework.db.connection.internal;

import org.eclipse.osee.framework.db.connection.IApplicationDatabaseInfoProvider;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Roberto E. Escobar
 */
public class InternalActivator implements BundleActivator {

   private static final long TIMEOUT = 20000;

   private static InternalActivator instance = null;

   private ServiceTracker applicationDbManagerTracker;
   private ServiceTracker dbConnectionProviderTracker;
   private ServiceTracker dbConnectionInfoTracker;

   public static IDbConnectionFactory getConnectionFactory() throws InterruptedException {
      return (IDbConnectionFactory) instance.dbConnectionProviderTracker.waitForService(TIMEOUT);
   }

   public static IDbConnectionInformation getConnectionInfos() throws InterruptedException {
      return (IDbConnectionInformation) instance.dbConnectionInfoTracker.waitForService(TIMEOUT);
   }

   private static IApplicationDatabaseManager getApplicationDatabaseManager() throws InterruptedException {
      return (IApplicationDatabaseManager) instance.applicationDbManagerTracker.waitForService(TIMEOUT);
   }

   public static IApplicationDatabaseInfoProvider getApplicationDatabaseProvider() throws OseeDataStoreException {
      try {
         return getApplicationDatabaseManager().getProvider();
      } catch (InterruptedException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      instance = this;

      dbConnectionProviderTracker = new ServiceTracker(context, IDbConnectionFactory.class.getName(), null);
      dbConnectionProviderTracker.open();

      dbConnectionInfoTracker = new ServiceTracker(context, IDbConnectionInformation.class.getName(), null);
      dbConnectionInfoTracker.open();

      applicationDbManagerTracker = new ServiceTracker(context, IApplicationDatabaseManager.class.getName(), null);
      applicationDbManagerTracker.open();
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      instance = null;
      dbConnectionProviderTracker.close();
      dbConnectionInfoTracker.close();
      applicationDbManagerTracker.close();
   }
}
