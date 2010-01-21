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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.OseeServiceTrackerId;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.IApplicationDatabaseManager;
import org.eclipse.osee.framework.database.IOseeConnectionProvider;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.core.IDatabaseInfoProvider;
import org.eclipse.osee.framework.database.internal.core.OseeDatabaseServiceImpl;
import org.eclipse.osee.framework.database.internal.core.OseeSequenceImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Roberto E. Escobar
 */
public class InternalActivator implements BundleActivator, IOseeDatabaseServiceProvider, IOseeConnectionProvider {

   private static final long TIMEOUT = 20000;

   private static InternalActivator instance = null;

   private final Map<OseeServiceTrackerId, ServiceTracker> mappedTrackers;
   private final List<ServiceRegistration> services;

   public InternalActivator() {
      this.mappedTrackers = new HashMap<OseeServiceTrackerId, ServiceTracker>();
      this.services = new ArrayList<ServiceRegistration>();
   }

   public void start(BundleContext context) throws Exception {
      instance = this;

      createService(context, IOseeDatabaseService.class, new OseeDatabaseServiceImpl(new OseeSequenceImpl(this), this));

      createServiceTracker(context, IDbConnectionFactory.class, OseeServiceTrackerId.CONNECTION_PROVIDER);
      createServiceTracker(context, IDbConnectionInformation.class, OseeServiceTrackerId.CONNECTION_INFOS);
      createServiceTracker(context, IApplicationDatabaseManager.class, OseeServiceTrackerId.APPLICATION_MANAGER);
      createServiceTracker(context, IOseeDatabaseService.class, OseeServiceTrackerId.OSEE_DATABASE_SERVICE);
   }

   public void stop(BundleContext context) throws Exception {
      for (ServiceRegistration service : services) {
         service.unregister();
      }

      for (ServiceTracker tracker : mappedTrackers.values()) {
         tracker.close();
      }
      services.clear();
      mappedTrackers.clear();
      instance = null;
   }

   private void createService(BundleContext context, Class<?> serviceInterface, Object serviceImplementation) {
      services.add(context.registerService(serviceInterface.getName(), serviceImplementation, null));
   }

   private void createServiceTracker(BundleContext context, Class<?> clazz, OseeServiceTrackerId trackerId) {
      ServiceTracker tracker = new ServiceTracker(context, clazz.getName(), null);
      tracker.open();
      mappedTrackers.put(trackerId, tracker);
   }

   public static InternalActivator getInstance() {
      return instance;
   }

   public IDbConnectionFactory getConnectionFactory() throws OseeDataStoreException {
      return getTracker(OseeServiceTrackerId.CONNECTION_PROVIDER, IDbConnectionFactory.class, TIMEOUT);
   }

   public IDbConnectionInformation getConnectionInfos() throws OseeDataStoreException {
      return getTracker(OseeServiceTrackerId.CONNECTION_INFOS, IDbConnectionInformation.class, TIMEOUT);
   }

   @Override
   public IOseeDatabaseService getOseeDatabaseService() throws OseeDataStoreException {
      return getTracker(OseeServiceTrackerId.OSEE_DATABASE_SERVICE, IOseeDatabaseService.class, TIMEOUT);
   }

   public IDatabaseInfoProvider getApplicationDatabaseProvider() throws OseeDataStoreException {
      return getApplicationDatabaseManager().getProvider();
   }

   private IApplicationDatabaseManager getApplicationDatabaseManager() throws OseeDataStoreException {
      return getTracker(OseeServiceTrackerId.APPLICATION_MANAGER, IApplicationDatabaseManager.class, TIMEOUT);
   }

   private <T> T getTracker(OseeServiceTrackerId trackerId, Class<T> clazz, Long timeout) throws OseeDataStoreException {
      ServiceTracker tracker = mappedTrackers.get(trackerId);
      Object service;
      if (timeout != null) {
         try {
            service = tracker.waitForService(timeout);
         } catch (InterruptedException ex) {
            throw new OseeDataStoreException(ex);
         }
      } else {
         service = tracker.getService();
      }
      return clazz.cast(service);
   }

}
