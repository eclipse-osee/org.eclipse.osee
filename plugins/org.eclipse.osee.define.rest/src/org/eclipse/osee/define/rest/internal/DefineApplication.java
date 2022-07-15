/*********************************************************************
 * Copyright (c) 2013, 2022 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *     Boeing - add Synchronization Endpoint
 **********************************************************************/

package org.eclipse.osee.define.rest.internal;

import java.util.Map;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.app.OseeAppResourceTokens;
import org.eclipse.osee.define.api.DefineApi;
import org.eclipse.osee.define.rest.DataRightsEndpointImpl;
import org.eclipse.osee.define.rest.GitEndpointImpl;
import org.eclipse.osee.define.rest.ImportEndpointImpl;
import org.eclipse.osee.define.rest.RenderEndpointImpl;
import org.eclipse.osee.define.rest.TraceabilityEndpointImpl;
import org.eclipse.osee.define.rest.synchronization.SynchronizationEndpointImpl;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceRegistry;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Ryan D. Brooks
 */

@ApplicationPath("define")
public final class DefineApplication extends Application {

   private Set<Object> singletons;
   private OrcsApi orcsApi;
   private DefineApi defineApi;
   private ActivityLog activityLog;
   private JdbcService jdbcService;

   public DefineApplication() {
      this.singletons = null;
      this.orcsApi = null;
      this.defineApi = null;
      this.activityLog = null;
      this.jdbcService = null;
   }

   public void setDefineApi(DefineApi defineApi) {
      this.defineApi = defineApi;
   }

   public void setActivityLog(ActivityLog activityLog) {
      this.activityLog = activityLog;
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcService = jdbcService;
   }

   public void start(Map<String, Object> properties) {

      IResourceRegistry resourceRegistry = new ResourceRegistry();
      OseeAppResourceTokens.register(resourceRegistry);

      JdbcClient jdbcClient = jdbcService.getClient();

      //@formatter:off
      this.singletons =
         Set.of
            (
              new SystemSafetyResource(activityLog, resourceRegistry, orcsApi),
              new TraceabilityEndpointImpl(activityLog, resourceRegistry, orcsApi, defineApi),
              new GitEndpointImpl(activityLog, orcsApi, defineApi),
              new DataRightsSwReqAndCodeResource(activityLog, resourceRegistry, orcsApi),
              new DataRightsEndpointImpl(defineApi),
              new RenderEndpointImpl(defineApi),
              new DefineBranchEndpointImpl(jdbcClient, orcsApi),
              new ImportEndpointImpl(defineApi),
              SynchronizationEndpointImpl.create(this.orcsApi)
            );
      //@formatter:on

      this.activityLog.getDebugLogger().warn("Define Application Started - %s",
         System.getProperty("OseeApplicationServer"));
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }

}

/* EOF */