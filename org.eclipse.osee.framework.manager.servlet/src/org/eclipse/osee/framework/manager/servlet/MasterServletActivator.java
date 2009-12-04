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

package org.eclipse.osee.framework.manager.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.osee.framework.branch.management.IBranchExchange;
import org.eclipse.osee.framework.core.data.OseeServerContext;
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
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.search.engine.ISearchEngine;
import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;
import org.eclipse.osee.framework.services.IOseeModelingService;
import org.eclipse.osee.framework.services.IOseeModelingServiceProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Donald G. Dunne
 */
public class MasterServletActivator implements BundleActivator, IOseeModelFactoryServiceProvider,
      IOseeModelingServiceProvider, IOseeBranchServiceProvider, IOseeDataTranslationProvider {
   private static MasterServletActivator instance;

   private enum OseeServiceId {
      SESSION_MANAGER, RESOURCE_LOCATOR, RESOURCE_MANAGER, BRANCH_EXCHANGE, BRANCH_CREATION, OSEE_DATA_TRANSLATOR_SERVICE, OSEE_BRANCH_SERVICE, PURGE_BRANCH, CHANGE_REPORT, BRANCH_COMMIT, SEARCH_ENGINE, SEARCH_ENGINE_TAGGER, CACHING_SERVICE, AUTHENTICATION_SERVICE, OSEE_MODEL_FACTORY, OSEE_MODELING_SERVICE;
   }

   private final List<ServiceTracker> trackers;
   private final Map<OseeServiceId, ServiceTracker> mappedTrackers;

   public MasterServletActivator() {
      this.trackers = new ArrayList<ServiceTracker>();
      this.mappedTrackers = new HashMap<OseeServiceId, ServiceTracker>();
   }

   public void start(BundleContext context) throws Exception {
      instance = this;

      createServiceTracker(context, ISessionManager.class, OseeServiceId.SESSION_MANAGER);
      createServiceTracker(context, IResourceLocatorManager.class, OseeServiceId.RESOURCE_LOCATOR);
      createServiceTracker(context, IResourceManager.class, OseeServiceId.RESOURCE_MANAGER);
      createServiceTracker(context, ISearchEngine.class, OseeServiceId.SEARCH_ENGINE);
      createServiceTracker(context, ISearchEngineTagger.class, OseeServiceId.SEARCH_ENGINE_TAGGER);
      createServiceTracker(context, IAuthenticationManager.class, OseeServiceId.AUTHENTICATION_SERVICE);
      createServiceTracker(context, IBranchExchange.class, OseeServiceId.BRANCH_EXCHANGE);
      createServiceTracker(context, IOseeCachingService.class, OseeServiceId.CACHING_SERVICE);
      createServiceTracker(context, IOseeModelFactoryService.class, OseeServiceId.OSEE_MODEL_FACTORY);
      createServiceTracker(context, IOseeModelingService.class, OseeServiceId.OSEE_MODELING_SERVICE);
      createServiceTracker(context, IOseeBranchService.class, OseeServiceId.OSEE_BRANCH_SERVICE);
      createServiceTracker(context, IDataTranslationService.class, OseeServiceId.OSEE_DATA_TRANSLATOR_SERVICE);

      createHttpServiceTracker(context, new SystemManagerServlet(),
                               OseeServerContext.MANAGER_CONTEXT);
      createHttpServiceTracker(context, new ResourceManagerServlet(),
                               OseeServerContext.RESOURCE_CONTEXT);
      createHttpServiceTracker(context, new ArtifactFileServlet(),
                               OseeServerContext.PROCESS_CONTEXT);
      createHttpServiceTracker(context, new ArtifactFileServlet(),
                               OseeServerContext.ARTIFACT_CONTEXT);
      createHttpServiceTracker(context, new ArtifactFileServlet(), "index");
      createHttpServiceTracker(context, new BranchExchangeServlet(),
                               OseeServerContext.BRANCH_EXCHANGE_CONTEXT);
      createHttpServiceTracker(context, new BranchManagerServlet(this, this),
                               OseeServerContext.BRANCH_CONTEXT);
      createHttpServiceTracker(context, new SearchEngineServlet(), OseeServerContext.SEARCH_CONTEXT);
      createHttpServiceTracker(context, new SearchEngineTaggerServlet(),
                               OseeServerContext.SEARCH_TAGGING_CONTEXT);
      createHttpServiceTracker(context, new ServerLookupServlet(), OseeServerContext.LOOKUP_CONTEXT);
      createHttpServiceTracker(context, new SessionManagementServlet(),
                               OseeServerContext.SESSION_CONTEXT);
      createHttpServiceTracker(context, new SessionClientLoopbackServlet(),
                               OseeServerContext.CLIENT_LOOPBACK_CONTEXT);
      createHttpServiceTracker(context, new ClientInstallInfoServlet(), "osee/install/info");
      createHttpServiceTracker(context, new OseeCacheServlet(this), OseeServerContext.CACHE_CONTEXT);
      createHttpServiceTracker(context, new OseeModelServlet(this),
                               OseeServerContext.OSEE_MODEL_CONTEXT);
   }

   public void stop(BundleContext context) throws Exception {
      mappedTrackers.clear();
      for (ServiceTracker tracker : trackers) {
         tracker.close();
      }
      trackers.clear();
      instance = null;
   }

   private void createServiceTracker(BundleContext context, Class<?> clazz, OseeServiceId trackerId) {
      ServiceTracker tracker = new ServiceTracker(context, clazz.getName(), null);
      tracker.open();
      trackers.add(tracker);
      mappedTrackers.put(trackerId, tracker);
   }

   private void createHttpServiceTracker(BundleContext context, OseeHttpServlet servletClass,
         String servletAlias) {
      ServiceTracker tracker = new OseeHttpServiceTracker(context, servletAlias, servletClass);
      tracker.open();
      trackers.add(tracker);
   }

   private <T> T getTracker(OseeServiceId trackerId, Class<T> clazz) {
      ServiceTracker tracker = mappedTrackers.get(trackerId);
      Object service = tracker.getService();
      return clazz.cast(service);
   }

   public static MasterServletActivator getInstance() {
      return instance;
   }

   public ISessionManager getSessionManager() {
      return getTracker(OseeServiceId.SESSION_MANAGER, ISessionManager.class);
   }

   public IBranchExchange getBranchExchange() {
      return getTracker(OseeServiceId.BRANCH_EXCHANGE, IBranchExchange.class);
   }

   public IResourceManager getResourceManager() {
      return getTracker(OseeServiceId.RESOURCE_MANAGER, IResourceManager.class);
   }

   public IResourceLocatorManager getResourceLocatorManager() {
      return getTracker(OseeServiceId.RESOURCE_LOCATOR, IResourceLocatorManager.class);
   }

   public ISearchEngine getSearchEngine() {
      return getTracker(OseeServiceId.SEARCH_ENGINE, ISearchEngine.class);
   }

   public ISearchEngineTagger getSearchTagger() {
      return getTracker(OseeServiceId.SEARCH_ENGINE_TAGGER, ISearchEngineTagger.class);
   }

   public IAuthenticationManager getAuthenticationManager() {
      return getTracker(OseeServiceId.AUTHENTICATION_SERVICE, IAuthenticationManager.class);
   }

   @Override
   public IDataTranslationService getTranslatorService() throws OseeCoreException {
      return getTracker(OseeServiceId.OSEE_DATA_TRANSLATOR_SERVICE, IDataTranslationService.class);
   }

   public IOseeCachingService getOseeCache() {
      return getTracker(OseeServiceId.CACHING_SERVICE, IOseeCachingService.class);
   }

   @Override
   public IOseeModelFactoryService getOseeFactoryService() throws OseeCoreException {
      return getTracker(OseeServiceId.OSEE_MODEL_FACTORY, IOseeModelFactoryService.class);
   }

   @Override
   public IOseeModelingService getOseeModelingService() throws OseeCoreException {
      return getTracker(OseeServiceId.OSEE_MODELING_SERVICE, IOseeModelingService.class);
   }

   @Override
   public IOseeBranchService getBranchService() throws OseeCoreException {
      return getTracker(OseeServiceId.OSEE_BRANCH_SERVICE, IOseeBranchService.class);
   }
}
