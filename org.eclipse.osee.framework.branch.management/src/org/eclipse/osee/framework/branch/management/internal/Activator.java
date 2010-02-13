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
package org.eclipse.osee.framework.branch.management.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.branch.management.IBranchExchange;
import org.eclipse.osee.framework.branch.management.exchange.BranchExchange;
import org.eclipse.osee.framework.core.enums.OseeServiceTrackerId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.server.IApplicationServerLookup;
import org.eclipse.osee.framework.core.server.IApplicationServerLookupProvider;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IDataTranslationServiceProvider;
import org.eclipse.osee.framework.core.services.IOseeBranchService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceFactory;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceProvider;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.core.services.IOseeModelingService;
import org.eclipse.osee.framework.core.services.IOseeModelingServiceProvider;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator, IOseeDatabaseServiceProvider, IOseeModelFactoryServiceProvider, IOseeCachingServiceProvider, IOseeModelingServiceProvider, IDataTranslationServiceProvider, IApplicationServerLookupProvider {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.branch.management";

   private static Activator instance;

   private final Map<OseeServiceTrackerId, ServiceTracker> mappedTrackers;
   private final List<ServiceRegistration> services;

   public Activator() {
      this.mappedTrackers = new HashMap<OseeServiceTrackerId, ServiceTracker>();
      this.services = new ArrayList<ServiceRegistration>();
   }

   public void start(BundleContext context) throws Exception {
      instance = this;

      IOseeCachingServiceFactory factory = new ServerOseeCachingServiceFactory(this, this, this, this);
      createService(context, IOseeCachingServiceFactory.class, factory);

      IOseeCachingService cachingService = factory.createCachingService();
      createService(context, IOseeCachingService.class, cachingService);

      createService(context, IBranchExchange.class, new BranchExchange(this));
      createService(context, IOseeBranchService.class, new OseeBranchService(this, this, this));

      createServiceTracker(context, IResourceLocatorManager.class, OseeServiceTrackerId.RESOURCE_LOCATOR);
      createServiceTracker(context, IResourceManager.class, OseeServiceTrackerId.RESOURCE_MANAGER);
      createServiceTracker(context, IBranchExchange.class, OseeServiceTrackerId.BRANCH_EXCHANGE);
      createServiceTracker(context, IOseeDatabaseService.class, OseeServiceTrackerId.OSEE_DATABASE_SERVICE);
      createServiceTracker(context, IOseeCachingService.class, OseeServiceTrackerId.OSEE_CACHING_SERVICE);
      createServiceTracker(context, IOseeModelingService.class, OseeServiceTrackerId.OSEE_MODELING_SERVICE);
      createServiceTracker(context, IDataTranslationService.class, OseeServiceTrackerId.DATA_TRANSLATION_SERVICE);

      createServiceTracker(context, IOseeModelFactoryService.class, OseeServiceTrackerId.OSEE_FACTORY_SERVICE);
      createServiceTracker(context, IApplicationServerManager.class, OseeServiceTrackerId.MASTER_SERVICE);
      createServiceTracker(context, IApplicationServerLookup.class, OseeServiceTrackerId.LOOKUP_SERVICE);
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

   public static Activator getInstance() {
      return instance;
   }

   public IBranchExchange getBranchExchange() {
      return getTracker(OseeServiceTrackerId.BRANCH_EXCHANGE, IBranchExchange.class);
   }

   public IResourceManager getResourceManager() {
      return getTracker(OseeServiceTrackerId.RESOURCE_MANAGER, IResourceManager.class);
   }

   public IResourceLocatorManager getResourceLocatorManager() {
      return getTracker(OseeServiceTrackerId.RESOURCE_LOCATOR, IResourceLocatorManager.class);
   }

   public IApplicationServerManager getApplicationServerManger() {
      return getTracker(OseeServiceTrackerId.MASTER_SERVICE, IApplicationServerManager.class);
   }

   @Override
   public IOseeDatabaseService getOseeDatabaseService() throws OseeDataStoreException {
      return getTracker(OseeServiceTrackerId.OSEE_DATABASE_SERVICE, IOseeDatabaseService.class);
   }

   @Override
   public IOseeModelFactoryService getOseeFactoryService() throws OseeCoreException {
      return getTracker(OseeServiceTrackerId.OSEE_FACTORY_SERVICE, IOseeModelFactoryService.class);
   }

   @Override
   public IOseeCachingService getOseeCachingService() throws OseeCoreException {
      return getTracker(OseeServiceTrackerId.OSEE_CACHING_SERVICE, IOseeCachingService.class);
   }

   @Override
   public IOseeModelingService getOseeModelingService() throws OseeCoreException {
      return getTracker(OseeServiceTrackerId.OSEE_MODELING_SERVICE, IOseeModelingService.class);
   }

   @Override
   public IDataTranslationService getTranslationService() throws OseeCoreException {
      return getTracker(OseeServiceTrackerId.DATA_TRANSLATION_SERVICE, IDataTranslationService.class);
   }

   @Override
   public IApplicationServerLookup getApplicationServerLookupService() throws OseeCoreException {
      return getTracker(OseeServiceTrackerId.LOOKUP_SERVICE, IApplicationServerLookup.class);
   }

   private <T> T getTracker(OseeServiceTrackerId trackerId, Class<T> clazz) {
      ServiceTracker tracker = mappedTrackers.get(trackerId);
      Object service = tracker.getService();
      return clazz.cast(service);
   }

}