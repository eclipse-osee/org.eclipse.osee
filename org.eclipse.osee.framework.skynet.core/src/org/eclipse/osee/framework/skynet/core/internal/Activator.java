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

import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.skynet.core.attribute.HttpAttributeTagger;
import org.eclipse.osee.framework.skynet.core.event.RemoteEventManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Ryan D. Brooks
 */
public class Activator implements BundleActivator, IOseeModelFactoryServiceProvider {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.skynet.core";

   private static Activator instance;
   private final Map<TrackerId, ServiceTracker> mappedTrackers;
   private final List<ServiceRegistration> services;

   private enum TrackerId {
      TRANSLATION_SERVICE,
      OSEE_CACHING_SERVICE,
      OSEE_FACTORY_SERVICE,
      COMMIT_SERVICE;
   }

   public Activator() {
      this.mappedTrackers = new HashMap<TrackerId, ServiceTracker>();
      this.services = new ArrayList<ServiceRegistration>();
   }

   @Override
   public void start(BundleContext context) throws Exception {
      instance = this;
      ClientSessionManager.class.getCanonicalName();
      HttpAttributeTagger.getInstance();

      IOseeCachingService cachingService = new ClientCachingServiceFactory().createService(this);

      createService(context, IOseeCachingService.class, cachingService);

      createServiceTracker(context, IOseeCachingService.class, TrackerId.OSEE_CACHING_SERVICE);
      createServiceTracker(context, IDataTranslationService.class, TrackerId.TRANSLATION_SERVICE);
      createServiceTracker(context, IOseeModelFactoryService.class, TrackerId.OSEE_FACTORY_SERVICE);
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
      return getTracker(TrackerId.OSEE_CACHING_SERVICE, IOseeCachingService.class);
   }

   public IDataTranslationService getTranslationService() {
      return getTracker(TrackerId.TRANSLATION_SERVICE, IDataTranslationService.class);
   }

   @Override
   public IOseeModelFactoryService getOseeFactoryService() throws OseeCoreException {
      return getTracker(TrackerId.OSEE_FACTORY_SERVICE, IOseeModelFactoryService.class);
   }

   private void createService(BundleContext context, Class<?> serviceInterface, Object serviceImplementation) {
      services.add(context.registerService(serviceInterface.getName(), serviceImplementation, null));
   }

   private void createServiceTracker(BundleContext context, Class<?> clazz, TrackerId trackerId) {
      ServiceTracker tracker = new ServiceTracker(context, clazz.getName(), null);
      tracker.open();
      mappedTrackers.put(trackerId, tracker);
   }

   private <T> T getTracker(TrackerId trackerId, Class<T> clazz) {
      ServiceTracker tracker = mappedTrackers.get(trackerId);
      Object service = tracker.getService();
      return clazz.cast(service);
   }
}