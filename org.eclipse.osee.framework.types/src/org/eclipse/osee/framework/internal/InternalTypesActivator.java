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
package org.eclipse.osee.framework.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceFactory;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceFactoryProvider;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceProvider;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.services.IOseeModelingService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Roberto E. Escobar
 */
public class InternalTypesActivator implements BundleActivator, IOseeCachingServiceProvider, IOseeModelFactoryServiceProvider, IOseeCachingServiceFactoryProvider {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.types";

   private static InternalTypesActivator instance;

   private enum TrackerId {
      OSEE_MODEL_FACTORY,
      OSEE_CACHING_SERVICE,
      OSEE_CACHING_SERVICE_FACTORY;
   }

   private final List<ServiceTracker> trackers;
   private final Map<TrackerId, ServiceTracker> mappedTrackers;
   private ServiceRegistration registration;

   public InternalTypesActivator() {
      this.trackers = new ArrayList<ServiceTracker>();
      this.mappedTrackers = new HashMap<TrackerId, ServiceTracker>();
   }

   public void start(BundleContext context) throws Exception {
      instance = this;

      registration =
            context.registerService(IOseeModelingService.class.getName(),
                  new OseeModelingServiceImpl(this, this, this), null);

      createServiceTracker(context, IOseeCachingService.class, TrackerId.OSEE_CACHING_SERVICE);
      createServiceTracker(context, IOseeModelFactoryService.class, TrackerId.OSEE_MODEL_FACTORY);
      createServiceTracker(context, IOseeCachingServiceFactory.class, TrackerId.OSEE_CACHING_SERVICE_FACTORY);
   }

   public void stop(BundleContext context) throws Exception {
      mappedTrackers.clear();
      for (ServiceTracker tracker : trackers) {
         tracker.close();
      }
      trackers.clear();
      registration.unregister();

      instance = null;
   }

   private void createServiceTracker(BundleContext context, Class<?> clazz, TrackerId trackerId) {
      ServiceTracker tracker = new ServiceTracker(context, clazz.getName(), null);
      tracker.open();
      trackers.add(tracker);
      mappedTrackers.put(trackerId, tracker);
   }

   public static InternalTypesActivator getInstance() {
      return instance;
   }

   @Override
   public IOseeCachingService getOseeCachingService() throws OseeCoreException {
      return getTracker(TrackerId.OSEE_CACHING_SERVICE, IOseeCachingService.class);
   }

   @Override
   public IOseeModelFactoryService getOseeFactoryService() throws OseeCoreException {
      return getTracker(TrackerId.OSEE_MODEL_FACTORY, IOseeModelFactoryService.class);
   }

   @Override
   public IOseeCachingServiceFactory getFactory() {
      return getTracker(TrackerId.OSEE_CACHING_SERVICE_FACTORY, IOseeCachingServiceFactory.class);
   }

   private <T> T getTracker(TrackerId trackerId, Class<T> clazz) {
      ServiceTracker tracker = mappedTrackers.get(trackerId);
      Object service = tracker.getService();
      return clazz.cast(service);
   }

}
