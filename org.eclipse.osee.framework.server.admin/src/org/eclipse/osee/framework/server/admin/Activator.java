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
package org.eclipse.osee.framework.server.admin;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.branch.management.IBranchExchange;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.search.engine.ISearchEngine;
import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

   private static Activator instance;

   private enum TrackerId {
      RESOURCE_LOCATOR,
      RESOURCE_MANAGER,
      BRANCH_EXCHANGE,
      SEARCH_ENGINE,
      SEARCH_TAGGER,
      OSEE_CACHING_SERVICE;
   }

   private final Map<TrackerId, ServiceTracker> mappedTrackers;

   public Activator() {
      this.mappedTrackers = new HashMap<TrackerId, ServiceTracker>();
   }

   public void start(BundleContext context) throws Exception {
      instance = this;

      createServiceTracker(context, IResourceManager.class, TrackerId.RESOURCE_MANAGER);
      createServiceTracker(context, IResourceLocatorManager.class, TrackerId.RESOURCE_LOCATOR);
      createServiceTracker(context, ISearchEngineTagger.class, TrackerId.SEARCH_TAGGER);
      createServiceTracker(context, ISearchEngine.class, TrackerId.SEARCH_ENGINE);
      createServiceTracker(context, IBranchExchange.class, TrackerId.BRANCH_EXCHANGE);
      createServiceTracker(context, IOseeCachingService.class, TrackerId.OSEE_CACHING_SERVICE);
   }

   public void stop(BundleContext context) throws Exception {
      for (ServiceTracker tracker : mappedTrackers.values()) {
         tracker.close();
      }
      mappedTrackers.clear();

      instance = null;
   }

   private void createServiceTracker(BundleContext context, Class<?> clazz, TrackerId trackerId) {
      ServiceTracker tracker = new ServiceTracker(context, clazz.getName(), null);
      tracker.open();
      mappedTrackers.put(trackerId, tracker);
   }

   public IBranchExchange getBranchExchange() {
      return getTracker(TrackerId.BRANCH_EXCHANGE, IBranchExchange.class);
   }

   public IResourceManager getResourceManager() {
      return getTracker(TrackerId.RESOURCE_MANAGER, IResourceManager.class);
   }

   public IResourceLocatorManager getResourceLocatorManager() {
      return getTracker(TrackerId.RESOURCE_LOCATOR, IResourceLocatorManager.class);
   }

   public ISearchEngineTagger getSearchTagger() {
      return getTracker(TrackerId.SEARCH_TAGGER, ISearchEngineTagger.class);
   }

   public ISearchEngine getSearchEngine() {
      return getTracker(TrackerId.SEARCH_ENGINE, ISearchEngine.class);
   }

   public IOseeCachingService getOseeCachingService() {
      return getTracker(TrackerId.OSEE_CACHING_SERVICE, IOseeCachingService.class);
   }

   private <T> T getTracker(TrackerId trackerId, Class<T> clazz) {
      ServiceTracker tracker = mappedTrackers.get(trackerId);
      Object service = tracker.getService();
      return clazz.cast(service);
   }

   public static Activator getInstance() {
      return Activator.instance;
   }
}
