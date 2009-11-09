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
import org.eclipse.osee.framework.branch.management.IBranchCreation;
import org.eclipse.osee.framework.branch.management.IBranchExchange;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.OseeHttpServiceTracker;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
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
public class MasterServletActivator implements BundleActivator {
   private static MasterServletActivator instance;

   private enum TrackerId {
      SESSION_MANAGER,
      RESOURCE_LOCATOR,
      RESOURCE_MANAGER,
      BRANCH_EXCHANGE,
      BRANCH_CREATION,
      SEARCH_ENGINE,
      SEARCH_ENGINE_TAGGER,
      AUTHENTICATION_SERVICE;
   }

   private final List<ServiceTracker> trackers;
   private final Map<TrackerId, ServiceTracker> mappedTrackers;

   public MasterServletActivator() {
      this.trackers = new ArrayList<ServiceTracker>();
      this.mappedTrackers = new HashMap<TrackerId, ServiceTracker>();
   }

   public void start(BundleContext context) throws Exception {
      instance = this;

      createServiceTracker(context, ISessionManager.class, TrackerId.SESSION_MANAGER);
      createServiceTracker(context, IResourceLocatorManager.class, TrackerId.RESOURCE_LOCATOR);
      createServiceTracker(context, IResourceManager.class, TrackerId.RESOURCE_MANAGER);

      createServiceTracker(context, ISearchEngine.class, TrackerId.SEARCH_ENGINE);
      createServiceTracker(context, ISearchEngineTagger.class, TrackerId.SEARCH_ENGINE_TAGGER);

      createServiceTracker(context, IAuthenticationManager.class, TrackerId.AUTHENTICATION_SERVICE);

      createHttpServiceTracker(context, SystemManagerServlet.class, OseeServerContext.MANAGER_CONTEXT);

      createHttpServiceTracker(context, ArtifactFileServlet.class, OseeServerContext.PROCESS_CONTEXT);
      createHttpServiceTracker(context, ArtifactFileServlet.class, OseeServerContext.ARTIFACT_CONTEXT);
      createHttpServiceTracker(context, ArtifactFileServlet.class, "index");

      createServiceTracker(context, IBranchCreation.class, TrackerId.BRANCH_CREATION);
      createServiceTracker(context, IBranchExchange.class, TrackerId.BRANCH_EXCHANGE);

      createHttpServiceTracker(context, BranchManagerServlet.class, OseeServerContext.BRANCH_CREATION_CONTEXT);
      createHttpServiceTracker(context, BranchExchangeServlet.class, OseeServerContext.BRANCH_EXCHANGE_CONTEXT);

      createHttpServiceTracker(context, SearchEngineServlet.class, OseeServerContext.SEARCH_CONTEXT);
      createHttpServiceTracker(context, SearchEngineTaggerServlet.class, OseeServerContext.SEARCH_TAGGING_CONTEXT);

      createHttpServiceTracker(context, ServerLookupServlet.class, OseeServerContext.LOOKUP_CONTEXT);
      createHttpServiceTracker(context, SessionManagementServlet.class, OseeServerContext.SESSION_CONTEXT);

      createHttpServiceTracker(context, SessionClientLoopbackServlet.class, OseeServerContext.CLIENT_LOOPBACK_CONTEXT);

      createHttpServiceTracker(context, ClientInstallInfoServlet.class, "osee/install/info");
   }

   public void stop(BundleContext context) throws Exception {
      mappedTrackers.clear();
      for (ServiceTracker tracker : trackers) {
         tracker.close();
      }
      trackers.clear();
      instance = null;
   }

   private void createServiceTracker(BundleContext context, Class<?> clazz, TrackerId trackerId) {
      ServiceTracker tracker = new ServiceTracker(context, clazz.getName(), null);
      tracker.open();
      trackers.add(tracker);
      mappedTrackers.put(trackerId, tracker);
   }

   private void createHttpServiceTracker(BundleContext context, Class<? extends OseeHttpServlet> servletClass, String servletAlias) {
      ServiceTracker tracker = new OseeHttpServiceTracker(context, servletAlias, servletClass);
      tracker.open();
      trackers.add(tracker);
   }

   public static MasterServletActivator getInstance() {
      return instance;
   }

   public ISessionManager getSessionManager() {
      return getTracker(TrackerId.SESSION_MANAGER, ISessionManager.class);
   }

   public IBranchCreation getBranchCreation() {
      return getTracker(TrackerId.BRANCH_CREATION, IBranchCreation.class);
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

   public ISearchEngine getSearchEngine() {
      return getTracker(TrackerId.SEARCH_ENGINE, ISearchEngine.class);
   }

   public ISearchEngineTagger getSearchTagger() {
      return getTracker(TrackerId.SEARCH_ENGINE_TAGGER, ISearchEngineTagger.class);
   }

   public IAuthenticationManager getAuthenticationManager() {
      return getTracker(TrackerId.AUTHENTICATION_SERVICE, IAuthenticationManager.class);
   }

   private <T> T getTracker(TrackerId trackerId, Class<T> clazz) {
      ServiceTracker tracker = mappedTrackers.get(trackerId);
      Object service = tracker.getService();
      return clazz.cast(service);
   }
}
