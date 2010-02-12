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
package org.eclipse.osee.framework.server.admin.internal;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.branch.management.IBranchExchange;
import org.eclipse.osee.framework.core.enums.OseeServiceTrackerId;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.search.engine.ISearchEngine;
import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator, IOseeDatabaseServiceProvider {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.server.admin";
   private static Activator instance;

   private final Map<OseeServiceTrackerId, ServiceTracker> mappedTrackers;

   public Activator() {
      this.mappedTrackers = new HashMap<OseeServiceTrackerId, ServiceTracker>();
   }

   public void start(BundleContext context) throws Exception {
      instance = this;

      createServiceTracker(context, IResourceManager.class, OseeServiceTrackerId.RESOURCE_MANAGER);
      createServiceTracker(context, IResourceLocatorManager.class, OseeServiceTrackerId.RESOURCE_LOCATOR);
      createServiceTracker(context, ISearchEngineTagger.class, OseeServiceTrackerId.SEARCH_TAGGER);
      createServiceTracker(context, ISearchEngine.class, OseeServiceTrackerId.SEARCH_ENGINE);
      createServiceTracker(context, IBranchExchange.class, OseeServiceTrackerId.BRANCH_EXCHANGE);
      createServiceTracker(context, IOseeCachingService.class, OseeServiceTrackerId.OSEE_CACHING_SERVICE);
      createServiceTracker(context, IOseeDatabaseService.class, OseeServiceTrackerId.OSEE_DATABASE_SERVICE);
   }

   public void stop(BundleContext context) throws Exception {
      for (ServiceTracker tracker : mappedTrackers.values()) {
         tracker.close();
      }
      mappedTrackers.clear();

      instance = null;
   }

   @Override
   public IOseeDatabaseService getOseeDatabaseService() throws OseeDataStoreException {
      return getTracker(OseeServiceTrackerId.OSEE_DATABASE_SERVICE, IOseeDatabaseService.class);
   }

   private void createServiceTracker(BundleContext context, Class<?> clazz, OseeServiceTrackerId trackerId) {
      ServiceTracker tracker = new ServiceTracker(context, clazz.getName(), null);
      tracker.open();
      mappedTrackers.put(trackerId, tracker);
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

   public ISearchEngineTagger getSearchTagger() {
      return getTracker(OseeServiceTrackerId.SEARCH_TAGGER, ISearchEngineTagger.class);
   }

   public ISearchEngine getSearchEngine() {
      return getTracker(OseeServiceTrackerId.SEARCH_ENGINE, ISearchEngine.class);
   }

   public IOseeCachingService getOseeCachingService() {
      return getTracker(OseeServiceTrackerId.OSEE_CACHING_SERVICE, IOseeCachingService.class);
   }

   private <T> T getTracker(OseeServiceTrackerId trackerId, Class<T> clazz) {
      ServiceTracker tracker = mappedTrackers.get(trackerId);
      Object service = tracker.getService();
      return clazz.cast(service);
   }

   public static Activator getInstance() {
      return Activator.instance;
   }
}
