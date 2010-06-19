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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.enums.OseeServiceTrackerId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.IWorkbenchUserService;
import org.eclipse.osee.framework.skynet.core.WorkbenchUserService;
import org.eclipse.osee.framework.skynet.core.attribute.HttpAttributeTagger;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.RemoteEventManager;
import org.eclipse.osee.framework.skynet.core.event.RemoteEventManager2;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Ryan D. Brooks
 */
public class Activator implements BundleActivator, IOseeModelFactoryServiceProvider, IOseeDatabaseServiceProvider {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.skynet.core";

   private static Activator instance;
   private final Map<OseeServiceTrackerId, ServiceTracker> mappedTrackers;
   private final List<ServiceRegistration> services;
   private BundleContext context;

   public Activator() {
      this.mappedTrackers = new HashMap<OseeServiceTrackerId, ServiceTracker>();
      this.services = new ArrayList<ServiceRegistration>();
   }

   public Bundle getBundle() {
      return instance.context.getBundle();
   }

   @Override
   public void start(BundleContext context) throws Exception {
      instance = this;
      this.context = context;
      ClientSessionManager.class.getCanonicalName();
      HttpAttributeTagger.getInstance();

      IOseeCachingService cachingService = new ClientCachingServiceFactory().createService(this);

      createService(context, IOseeCachingService.class, cachingService);
      createService(context, IWorkbenchUserService.class, new WorkbenchUserService());

      createServiceTracker(context, IOseeCachingService.class, OseeServiceTrackerId.OSEE_CACHING_SERVICE);
      createServiceTracker(context, IDataTranslationService.class, OseeServiceTrackerId.TRANSLATION_SERVICE);
      createServiceTracker(context, IOseeModelFactoryService.class, OseeServiceTrackerId.OSEE_FACTORY_SERVICE);
      createServiceTracker(context, IOseeDatabaseService.class, OseeServiceTrackerId.OSEE_DATABASE_SERVICE);

      RemoteEventManager2.getInstance().registerForRemoteEvents();
      if (!OseeEventManager.isNewEvents() && !OseeEventManager.isOldEvents()) {
         OseeLog.log(Activator.class, Level.SEVERE, "Neither Event System Enabled - This is a problem.");
      }
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      HttpAttributeTagger.getInstance().deregisterFromEventManager();
      RemoteEventManager.deregisterFromRemoteEventManager();

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

   public static Activator getInstance() {
      return instance;
   }

   public IOseeCachingService getOseeCacheService() {
      return getTracker(OseeServiceTrackerId.OSEE_CACHING_SERVICE, IOseeCachingService.class);
   }

   public IDataTranslationService getTranslationService() {
      return getTracker(OseeServiceTrackerId.TRANSLATION_SERVICE, IDataTranslationService.class);
   }

   @Override
   public IOseeModelFactoryService getOseeFactoryService() throws OseeCoreException {
      return getTracker(OseeServiceTrackerId.OSEE_FACTORY_SERVICE, IOseeModelFactoryService.class);
   }

   private void createService(BundleContext context, Class<?> serviceInterface, Object serviceImplementation) {
      services.add(context.registerService(serviceInterface.getName(), serviceImplementation, null));
   }

   private void createServiceTracker(BundleContext context, Class<?> clazz, OseeServiceTrackerId trackerId) {
      ServiceTracker tracker = new ServiceTracker(context, clazz.getName(), null);
      tracker.open();
      mappedTrackers.put(trackerId, tracker);
   }

   @Override
   public IOseeDatabaseService getOseeDatabaseService() throws OseeDataStoreException {
      return getTracker(OseeServiceTrackerId.OSEE_DATABASE_SERVICE, IOseeDatabaseService.class);
   }

   private <T> T getTracker(OseeServiceTrackerId trackerId, Class<T> clazz) {
      ServiceTracker tracker = mappedTrackers.get(trackerId);
      Object service = tracker.getService();
      return clazz.cast(service);
   }
}