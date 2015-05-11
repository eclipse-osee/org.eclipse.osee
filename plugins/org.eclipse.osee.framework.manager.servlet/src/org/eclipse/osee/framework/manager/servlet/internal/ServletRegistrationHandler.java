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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.manager.servlet.AdminServlet;
import org.eclipse.osee.framework.manager.servlet.ArtifactFileServlet;
import org.eclipse.osee.framework.manager.servlet.DataServlet;
import org.eclipse.osee.framework.manager.servlet.SessionClientLoopbackServlet;
import org.eclipse.osee.framework.manager.servlet.SessionManagementServlet;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;

/**
 * @author Roberto E. Escobar
 */
public class ServletRegistrationHandler {

   private HttpService httpService;
   private Log logger;
   private ISessionManager sessionManager;
   private IApplicationServerManager appServerManager;
   private IAuthenticationManager authenticationManager;
   private IResourceManager resourceManager;
   private OrcsApi orcsApi;
   private JdbcService jdbcService;
   private ActivityLog activityLog;

   private final Set<String> contexts = new HashSet<String>();

   public void setSessionManager(ISessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }

   public void setAppServerManager(IApplicationServerManager appServerManager) {
      this.appServerManager = appServerManager;
   }

   public void setAuthenticationManager(IAuthenticationManager authenticationManager) {
      this.authenticationManager = authenticationManager;
   }

   public void setResourceManager(IResourceManager resourceManager) {
      this.resourceManager = resourceManager;
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcService = jdbcService;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public Log getLogger() {
      return logger;
   }

   public void setHttpService(HttpService httpService) {
      this.httpService = httpService;
   }

   public synchronized void start(BundleContext context) {
      ServletUtil.unregister(httpService, appServerManager, contexts);
      registerServices(context);
   }

   public synchronized void stop() {
      ServletUtil.unregister(httpService, appServerManager, contexts);
      contexts.clear();
   }

   private void registerServices(BundleContext context) {
      contexts.clear();
      JdbcClient jdbcClient = jdbcService.getClient();
      register(new ArtifactFileServlet(logger, resourceManager, orcsApi, jdbcClient), OseeServerContext.PROCESS_CONTEXT);
      register(new ArtifactFileServlet(logger, resourceManager, orcsApi, jdbcClient),
         OseeServerContext.ARTIFACT_CONTEXT);
      register(new ArtifactFileServlet(logger, resourceManager, orcsApi, jdbcClient), "index");
      register(new SessionManagementServlet(logger, sessionManager, authenticationManager, activityLog),
         OseeServerContext.SESSION_CONTEXT);
      register(new SessionClientLoopbackServlet(logger, sessionManager), OseeServerContext.CLIENT_LOOPBACK_CONTEXT);

      register(new DataServlet(logger, resourceManager, orcsApi, jdbcClient), "osee/data");
      register(new AdminServlet(logger, context), "osee/console");
   }

   private void register(OseeHttpServlet servlet, String contexts) {
      this.contexts.add(contexts);
      ServletUtil.register(httpService, appServerManager, servlet, contexts);
   }

   public void setActivityLog(ActivityLog activityLog) {
      this.activityLog = activityLog;
   }
}
