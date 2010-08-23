/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.enums.OseeServiceTrackerId;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.core.util.ServiceDependencyTracker;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.lifecycle.ILifecycleService;
import org.eclipse.osee.framework.lifecycle.ILifecycleServiceProvider;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.systems.EventManagerData;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Ryan D. Brooks
 */
public class Activator implements BundleActivator, IOseeDatabaseServiceProvider, ILifecycleServiceProvider {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.skynet.core";

   private static Activator instance;
   private final Map<OseeServiceTrackerId, ServiceTracker> mappedTrackers;
   private final Collection<ServiceDependencyTracker> trackers = new ArrayList<ServiceDependencyTracker>();

   public Activator() {
      this.mappedTrackers = new HashMap<OseeServiceTrackerId, ServiceTracker>();
   }

   @Override
   public void start(BundleContext context) throws Exception {
      instance = this;

      trackers.add(new ServiceDependencyTracker(context, new SkynetTransactionAccessServiceHandler()));
      trackers.add(new ServiceDependencyTracker(context, new ClientCachingServiceRegHandler()));
      trackers.add(new ServiceDependencyTracker(context, new ClientWorkbenchUserRegHandler()));

      EventManagerData eventManagerData = new EventManagerData();
      OseeEventManager.setEventManagerData(eventManagerData);
      trackers.add(new ServiceDependencyTracker(context, new OseeEventSystemServiceRegHandler(eventManagerData)));

      ClientSessionManager.class.getCanonicalName();

      createServiceTracker(context, IOseeCachingService.class, OseeServiceTrackerId.OSEE_CACHING_SERVICE);
      createServiceTracker(context, IDataTranslationService.class, OseeServiceTrackerId.TRANSLATION_SERVICE);
      createServiceTracker(context, IOseeModelFactoryService.class, OseeServiceTrackerId.OSEE_FACTORY_SERVICE);
      createServiceTracker(context, IOseeDatabaseService.class, OseeServiceTrackerId.OSEE_DATABASE_SERVICE);
      createServiceTracker(context, ILifecycleService.class, OseeServiceTrackerId.LIFECYCLE_SERVER);
      createServiceTracker(context, IAccessControlService.class, OseeServiceTrackerId.OSEE_ACCESS_CONTROL_SERVICE);

      for (ServiceDependencyTracker dependencyTracker : trackers) {
         dependencyTracker.open();
      }
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      for (ServiceDependencyTracker dependencyTracker : trackers) {
         dependencyTracker.close();
      }
      trackers.clear();

      for (ServiceTracker tracker : mappedTrackers.values()) {
         tracker.close();
      }
      mappedTrackers.clear();

      instance = null;
   }

   public static Activator getInstance() {
      return instance;
   }

   public IOseeCachingService getOseeCacheService() {
      return getTracker(OseeServiceTrackerId.OSEE_CACHING_SERVICE, IOseeCachingService.class);
   }

   public IDataTranslationService getTranslationService() {
      return getTracker(OseeServiceTrackerId.TRANSLATION_SERVICE, IDataTranslationService.class);
   }

   public TransactionRecordFactory getTransactionFactory() {
      IOseeModelFactoryService service =
         getTracker(OseeServiceTrackerId.OSEE_FACTORY_SERVICE, IOseeModelFactoryService.class);
      return service != null ? service.getTransactionFactory() : null;
   }

   private void createServiceTracker(BundleContext context, Class<?> clazz, OseeServiceTrackerId trackerId) {
      ServiceTracker tracker = new ServiceTracker(context, clazz.getName(), null);
      tracker.open();
      mappedTrackers.put(trackerId, tracker);
   }

   @Override
   public IOseeDatabaseService getOseeDatabaseService() {
      return getTracker(OseeServiceTrackerId.OSEE_DATABASE_SERVICE, IOseeDatabaseService.class);
   }

   @Override
   public ILifecycleService getLifecycleServices() {
      return getTracker(OseeServiceTrackerId.LIFECYCLE_SERVER, ILifecycleService.class);
   }

   private <T> T getTracker(OseeServiceTrackerId trackerId, Class<T> clazz) {
      ServiceTracker tracker = mappedTrackers.get(trackerId);
      Object service = tracker.getService();
      return clazz.cast(service);
   }

   public IAccessControlService getAccessControlService() {
      try {
         Bundle bundle = Platform.getBundle("org.eclipse.osee.framework.access");
         if (bundle.getState() != Bundle.ACTIVE) {
            bundle.start();
         }
      } catch (BundleException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return getTracker(OseeServiceTrackerId.OSEE_ACCESS_CONTROL_SERVICE, IAccessControlService.class);
   }
}