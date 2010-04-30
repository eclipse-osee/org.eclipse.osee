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

package org.eclipse.osee.framework.manager.servlet.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.osee.framework.branch.management.IBranchExchange;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.OseeServiceTrackerId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.OseeHttpServiceTracker;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeBranchService;
import org.eclipse.osee.framework.core.services.IOseeBranchServiceProvider;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeDataTranslationProvider;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.core.services.IOseeModelingService;
import org.eclipse.osee.framework.core.services.IOseeModelingServiceProvider;
import org.eclipse.osee.framework.manager.servlet.ArtifactFileServlet;
import org.eclipse.osee.framework.manager.servlet.AtsServlet;
import org.eclipse.osee.framework.manager.servlet.BranchExchangeServlet;
import org.eclipse.osee.framework.manager.servlet.BranchManagerServlet;
import org.eclipse.osee.framework.manager.servlet.ClientInstallInfoServlet;
import org.eclipse.osee.framework.manager.servlet.OseeCacheServlet;
import org.eclipse.osee.framework.manager.servlet.OseeModelServlet;
import org.eclipse.osee.framework.manager.servlet.ResourceManagerServlet;
import org.eclipse.osee.framework.manager.servlet.SearchEngineServlet;
import org.eclipse.osee.framework.manager.servlet.SearchEngineTaggerServlet;
import org.eclipse.osee.framework.manager.servlet.ServerLookupServlet;
import org.eclipse.osee.framework.manager.servlet.SessionClientLoopbackServlet;
import org.eclipse.osee.framework.manager.servlet.SessionManagementServlet;
import org.eclipse.osee.framework.manager.servlet.SystemManagerServlet;
import org.eclipse.osee.framework.manager.servlet.UnsubscribeServlet;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.search.engine.ISearchEngine;
import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Donald G. Dunne
 */
public class Activator implements BundleActivator, IOseeModelFactoryServiceProvider, IOseeModelingServiceProvider, IOseeBranchServiceProvider, IOseeDataTranslationProvider {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.manager.servlet";

   private static Activator instance;

   private final List<ServiceTracker> trackers;
   private final Map<OseeServiceTrackerId, ServiceTracker> mappedTrackers;

   public Activator() {
      this.trackers = new ArrayList<ServiceTracker>();
      this.mappedTrackers = new HashMap<OseeServiceTrackerId, ServiceTracker>();
   }

   public void start(BundleContext context) throws Exception {
      instance = this;

      createServiceTracker(context, ISessionManager.class, OseeServiceTrackerId.SESSION_MANAGER);
      createServiceTracker(context, IResourceLocatorManager.class, OseeServiceTrackerId.RESOURCE_LOCATOR);
      createServiceTracker(context, IResourceManager.class, OseeServiceTrackerId.RESOURCE_MANAGER);
      createServiceTracker(context, ISearchEngine.class, OseeServiceTrackerId.SEARCH_ENGINE);
      createServiceTracker(context, ISearchEngineTagger.class, OseeServiceTrackerId.SEARCH_TAGGER);
      createServiceTracker(context, IAuthenticationManager.class, OseeServiceTrackerId.AUTHENTICATION_SERVICE);
      createServiceTracker(context, IBranchExchange.class, OseeServiceTrackerId.BRANCH_EXCHANGE);
      createServiceTracker(context, IOseeCachingService.class, OseeServiceTrackerId.OSEE_CACHING_SERVICE);
      createServiceTracker(context, IOseeModelFactoryService.class, OseeServiceTrackerId.OSEE_MODEL_FACTORY);
      createServiceTracker(context, IOseeModelingService.class, OseeServiceTrackerId.OSEE_MODELING_SERVICE);
      createServiceTracker(context, IOseeBranchService.class, OseeServiceTrackerId.OSEE_BRANCH_SERVICE);
      createServiceTracker(context, IDataTranslationService.class, OseeServiceTrackerId.TRANSLATION_SERVICE);

      createHttpServiceTracker(context, new SystemManagerServlet(), OseeServerContext.MANAGER_CONTEXT);
      createHttpServiceTracker(context, new ResourceManagerServlet(), OseeServerContext.RESOURCE_CONTEXT);
      createHttpServiceTracker(context, new ArtifactFileServlet(), OseeServerContext.PROCESS_CONTEXT);
      createHttpServiceTracker(context, new ArtifactFileServlet(), OseeServerContext.ARTIFACT_CONTEXT);
      createHttpServiceTracker(context, new ArtifactFileServlet(), "index");
      createHttpServiceTracker(context, new BranchExchangeServlet(), OseeServerContext.BRANCH_EXCHANGE_CONTEXT);
      createHttpServiceTracker(context, new BranchManagerServlet(this, this), OseeServerContext.BRANCH_CONTEXT);
      createHttpServiceTracker(context, new SearchEngineServlet(), OseeServerContext.SEARCH_CONTEXT);
      createHttpServiceTracker(context, new SearchEngineTaggerServlet(), OseeServerContext.SEARCH_TAGGING_CONTEXT);
      createHttpServiceTracker(context, new ServerLookupServlet(), OseeServerContext.LOOKUP_CONTEXT);
      createHttpServiceTracker(context, new SessionManagementServlet(), OseeServerContext.SESSION_CONTEXT);
      createHttpServiceTracker(context, new SessionClientLoopbackServlet(), OseeServerContext.CLIENT_LOOPBACK_CONTEXT);
      createHttpServiceTracker(context, new ClientInstallInfoServlet(), "osee/install/info");
      createHttpServiceTracker(context, new OseeCacheServlet(this), OseeServerContext.CACHE_CONTEXT);
      createHttpServiceTracker(context, new OseeModelServlet(this), OseeServerContext.OSEE_MODEL_CONTEXT);
      createHttpServiceTracker(context, new UnsubscribeServlet(this), "osee/unsubscribe");

      createHttpServiceTracker(context, new AtsServlet(), "osee/ats");
   }

   public void stop(BundleContext context) throws Exception {
      mappedTrackers.clear();
      for (ServiceTracker tracker : trackers) {
         tracker.close();
      }
      trackers.clear();
      instance = null;
   }

   private void createServiceTracker(BundleContext context, Class<?> clazz, OseeServiceTrackerId trackerId) {
      ServiceTracker tracker = new ServiceTracker(context, clazz.getName(), null);
      tracker.open();
      trackers.add(tracker);
      mappedTrackers.put(trackerId, tracker);
   }

   private void createHttpServiceTracker(BundleContext context, OseeHttpServlet servletClass, String servletAlias) {
      ServiceTracker tracker = new OseeHttpServiceTracker(context, servletAlias, servletClass);
      tracker.open();
      trackers.add(tracker);
   }

   private <T> T getTracker(OseeServiceTrackerId trackerId, Class<T> clazz) {
      ServiceTracker tracker = mappedTrackers.get(trackerId);
      Object service = tracker.getService();
      return clazz.cast(service);
   }

   public static Activator getInstance() {
      return instance;
   }

   public ISessionManager getSessionManager() {
      return getTracker(OseeServiceTrackerId.SESSION_MANAGER, ISessionManager.class);
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

   public ISearchEngine getSearchEngine() {
      return getTracker(OseeServiceTrackerId.SEARCH_ENGINE, ISearchEngine.class);
   }

   public ISearchEngineTagger getSearchTagger() {
      return getTracker(OseeServiceTrackerId.SEARCH_TAGGER, ISearchEngineTagger.class);
   }

   public IAuthenticationManager getAuthenticationManager() {
      return getTracker(OseeServiceTrackerId.AUTHENTICATION_SERVICE, IAuthenticationManager.class);
   }

   @Override
   public IDataTranslationService getTranslatorService() throws OseeCoreException {
      return getTracker(OseeServiceTrackerId.TRANSLATION_SERVICE, IDataTranslationService.class);
   }

   public IOseeCachingService getOseeCache() {
      return getTracker(OseeServiceTrackerId.OSEE_CACHING_SERVICE, IOseeCachingService.class);
   }

   @Override
   public IOseeModelFactoryService getOseeFactoryService() throws OseeCoreException {
      return getTracker(OseeServiceTrackerId.OSEE_MODEL_FACTORY, IOseeModelFactoryService.class);
   }

   @Override
   public IOseeModelingService getOseeModelingService() throws OseeCoreException {
      return getTracker(OseeServiceTrackerId.OSEE_MODELING_SERVICE, IOseeModelingService.class);
   }

   @Override
   public IOseeBranchService getBranchService() throws OseeCoreException {
      return getTracker(OseeServiceTrackerId.OSEE_BRANCH_SERVICE, IOseeBranchService.class);
   }
}
