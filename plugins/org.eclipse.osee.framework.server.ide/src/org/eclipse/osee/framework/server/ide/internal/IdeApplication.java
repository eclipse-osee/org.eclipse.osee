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
package org.eclipse.osee.framework.server.ide.internal;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * Get application.wadl at this context to get rest documentation
 *
 * @author Roberto E. Escobar
 */
@ApplicationPath("ide")
public class IdeApplication extends Application {

   private final Set<Object> resources = new HashSet<>();
   private final Set<Class<?>> classes = new HashSet<>();
   private static OrcsApi orcsApi;

   private IResourceManager resourceManager;
   private ActivityLog activityLog;
   private JdbcService jdbcService;
   private IAuthenticationManager authenticationManager;
   private ISessionManager sessionManager;

   public void setOrcsApi(OrcsApi orcsApi) {
      IdeApplication.orcsApi = orcsApi;
   }

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcService = jdbcService;
   }

   public void setActivityLog(ActivityLog activityLog) {
      this.activityLog = activityLog;
   }

   public void setResourceManager(IResourceManager resourceManager) {
      this.resourceManager = resourceManager;
   }

   public void setAuthenticationManager(IAuthenticationManager authenticationManager) {
      this.authenticationManager = authenticationManager;
   }

   public void setSessionManager(ISessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }

   public static OrcsApi getOrcsApi() {
      return orcsApi;
   }

   public void start() {
      resources.add(new ClientEndpointImpl(jdbcService, orcsApi));
      resources.add(new SessionEndpointImpl(jdbcService, orcsApi, authenticationManager, sessionManager, activityLog));
   }

   public void stop() {
      resources.clear();
      classes.clear();
   }

   @Override
   public Set<Class<?>> getClasses() {
      return classes;
   }

   @Override
   public Set<Object> getSingletons() {
      return resources;
   }

}
